package org.example;

import com.google.gson.GsonBuilder;
import org.bouncycastle.util.encoders.Hex;
import org.example.dto.CredentialsDTO;
import org.example.dto.Usuario;
import org.example.utils.AESCBCWithSHA256;
import org.example.utils.AESEBCUtils;
import org.example.utils.FileUtils;
import org.example.utils.PBKDF2Util;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import static org.example.utils.PBKDF2Util.generateDerivedKey;

public class Main {

    public static void main(String[] args)  {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Escolha uma opção");
        System.out.println("1. Cadastro");
        System.out.println("2. Login");
        System.out.println("3. Gerar Chave e IV");

        int escolha = scanner.nextInt();

        switch (escolha){
            case 1:
                cadastro();
                break;
            case 2:
                login();
                break;
            case 3:
                try {
                    AESEBCUtils.generate();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
        }
    }

    public static void cadastro() {
        PBKDF2Util obj = new PBKDF2Util();
        Usuario usuario = nomeSenhaUsuario();
        String saltoAleatorio;
        try {
            saltoAleatorio = obj.getSalt();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        String chaveDerivada = chaveDerivada(usuario.getSenha(), saltoAleatorio);
        FileUtils fileUtils = new FileUtils();
        usuario.setSalt(saltoAleatorio);
        usuario.setSenha(Hex.toHexString(criptografaSenha(chaveDerivada)));
        usuario.setHash256(geraHash256(usuario));
        fileUtils.grava(new GsonBuilder().create().toJson(usuario));
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    public static void login() {
        Usuario usuario = nomeSenhaUsuario();
        Usuario usuarioFromFile = getUsuarioFromFile();
        String chaveDerivada = chaveDerivada(usuario.getSenha(), usuarioFromFile.getSalt());
        usuario.setSenha(Hex.toHexString(criptografaSenha(chaveDerivada)));

        String hashUsuarioLogin = geraHash256(usuario);

        if (usuarioFromFile.getHash256().equals(hashUsuarioLogin)){
            System.out.println("Login realizado com sucesso :)");
        } else {
            System.out.println("Não foi possível realizar o login :(");
        }
    }

    public static String geraHash256(Usuario usuario) {
        byte[] resultado = criptografaUsuario(usuario);
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = messageDigest.digest(resultado);
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static Usuario nomeSenhaUsuario() {
        Usuario usuario = new Usuario();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Digite o seu nome: ");
        usuario.setNome(scanner.next());
        System.out.println("Digite a sua senha: ");
        usuario.setSenha(scanner.next());

        return usuario;
    }

    public static String chaveDerivada(String senha,String saltoAleatorio) {
        return generateDerivedKey(senha, saltoAleatorio);
    }

    public static Usuario getUsuarioFromFile() {
        FileUtils fileUtils = new FileUtils();
        return fileUtils.getUsuarioFromFile();
    }

    public static byte[] criptografaUsuario(Usuario usuario){
        FileUtils fileUtils = new FileUtils();
        CredentialsDTO credentials = fileUtils.getCredentialsFromFile();
        String usuarioSenha = usuario.getNome() + usuario.getSenha();
        try {
            return AESCBCWithSHA256.encryptAES_CBC(usuarioSenha.getBytes(),
                    AESCBCWithSHA256.deriveAESKey(credentials.getKey()),
                    AESCBCWithSHA256.deriveAESKey(credentials.getIv())
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] criptografaSenha(String senha){
        FileUtils fileUtils = new FileUtils();
        CredentialsDTO credentials = fileUtils.getCredentialsFromFile();
        try {
            return AESCBCWithSHA256.encryptAES_CBC(senha.getBytes(),
                    AESCBCWithSHA256.deriveAESKey(credentials.getKey()),
                    AESCBCWithSHA256.deriveAESKey(credentials.getIv())
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}