package org.example.utils;

import com.google.gson.Gson;
import org.example.dto.CredentialsDTO;
import org.example.dto.Usuario;

import java.io.*;

public class FileUtils {
    public static final String DIRETORIO = "src/main/resources/";
    public static final String NOME_ARQUIVO_DADOS_USUARIO = "dadosUsuario.txt";
    public static final String IV_AND_KEY_FILE = "iv_and_key.txt";

    public void grava(String conteudo) {
        File arquivo = new File(DIRETORIO + NOME_ARQUIVO_DADOS_USUARIO);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo))) {
            writer.write(conteudo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void gravaCredenciais(String conteudo) {
        File arquivo = new File(DIRETORIO + IV_AND_KEY_FILE);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo))) {
            writer.write(conteudo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Usuario getUsuarioFromFile() {
        String linhaRetorno = getContentFromFile(NOME_ARQUIVO_DADOS_USUARIO);
        Gson gson = new Gson();
        return gson.fromJson(linhaRetorno, Usuario.class);
    }

    private static String getContentFromFile(String nomeArquivo) {
        ClassLoader classLoader = FileUtils.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(nomeArquivo);
        String linhaRetorno = "";

        if (inputStream != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String linha;
                while ((linha = reader.readLine()) != null) {
                    linhaRetorno = linha;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("O arquivo não foi encontrado.");
        }
        return linhaRetorno;
    }

    public CredentialsDTO getCredentialsFromFile(){
        String linhaRetorno = getContentFromFile(IV_AND_KEY_FILE);
        try {
            return AESEBCUtils.getCredentials(linhaRetorno);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}