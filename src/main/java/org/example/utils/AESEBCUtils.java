package org.example.utils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import org.example.dto.CredentialsDTO;
import javax.crypto.*;
import java.security.Security;
import java.util.Random;

public class AESEBCUtils {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static void generate() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES", "BC");

        SecretKey key = keyGen.generateKey();
        SecretKey iv = keyGen.generateKey();

        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding", "BC");

        byte[] array = new byte[16];
        byte[] arrayIv = new byte[16];
        new Random().nextBytes(array);
        String generatedStringKey = bytesToHex(array);

        new Random().nextBytes(arrayIv);
        String generatedStringIv = bytesToHex(arrayIv);

        byte[] input = Hex.decode(generatedStringKey);
        byte[] inputIv = Hex.decode(generatedStringIv);

        cipher.init(Cipher.ENCRYPT_MODE, key);
        cipher.init(Cipher.ENCRYPT_MODE, iv);

        String keyGenerated = Hex.toHexString(cipher.doFinal(input));
        String ivGenerated = Hex.toHexString(cipher.doFinal(inputIv));

        FileUtils fileUtils = new FileUtils();
        fileUtils.gravaCredenciais(keyGenerated + "#" + ivGenerated);
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    public static CredentialsDTO getCredentials(String linhaRetorno) throws Exception {
        String keyFromFile = linhaRetorno.split("#")[0];
        String ivFromFile = linhaRetorno.split("#")[1];

        CredentialsDTO credentialsDTO = new CredentialsDTO();
        credentialsDTO.setKey(keyFromFile);
        credentialsDTO.setIv(ivFromFile);
        return credentialsDTO;
    }
}
