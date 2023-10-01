package org.example.utils;

import org.bouncycastle.util.encoders.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class TesteBaboseira {

    public static void main(String[] args) {
        try {
            byte[] retorno = deriveAESKey( "LuanaBesta");
            System.out.println(Hex.toHexString(retorno));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] deriveAESKey(String keyString) throws NoSuchAlgorithmException {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = keyString.getBytes();
        byte[] hashBytes = sha256.digest(keyBytes);
        return Arrays.copyOf(hashBytes, 16);
    }

}
