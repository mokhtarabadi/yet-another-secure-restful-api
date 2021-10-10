package com.mokhtarabadi.yasra.encryption;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class Encryption {

    private static final char[] hexArray = "0123456789abcdef".toCharArray();

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0, v; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private static byte[] hMac(String algorithm, byte[] key, byte[] message) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(algorithm);
        mac.init(new SecretKeySpec(key, algorithm));
        return mac.doFinal(message);
    }

    private static SecretKey generateKey(int n) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(n);
        return keyGenerator.generateKey();
    }

    private static SecretKey getKeyFromPassword(String password, String salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
        return new SecretKeySpec(factory.generateSecret(spec)
                .getEncoded(), "AES");
    }

    private static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    private static String encrypt(String algorithm, String input, SecretKey key,
                                  IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] cipherText = cipher.doFinal(input.getBytes());
        return Base64.getEncoder()
                .encodeToString(cipherText);
    }

    private static String decrypt(String algorithm, String cipherText, SecretKey key,
                                  IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] plainText = cipher.doFinal(Base64.getDecoder()
                .decode(cipherText));
        return new String(plainText);
    }

    public static String generateSignature(String message, String key) {
        try {
            byte[] bytes = hMac("HmacSHA256", key.getBytes(), message.getBytes());
            return bytesToHex(bytes);
        } catch (Exception ignore) {
            return null;
        }
    }

    public static boolean checkSignature(String signature, String message, String key) {
        String newSignature = generateSignature(message, key);
        if (newSignature == null) {
            return false;
        }
        return signature.equals(newSignature);
    }

    public static String encrypt(String input, String password, String salt) {
        try {
            SecretKey key = getKeyFromPassword(password, salt);
            IvParameterSpec ivParameterSpec = generateIv();
            String algorithm = "AES/CBC/PKCS5Padding";
            return encrypt(algorithm, input, key, ivParameterSpec);
        } catch (Exception ignore) {
            return null;
        }
    }

    public static String decrypt(String encrypted, String password, String salt) {
        try {
            SecretKey key = getKeyFromPassword(password, salt);
            IvParameterSpec ivParameterSpec = generateIv();
            String algorithm = "AES/CBC/PKCS5Padding";
            return decrypt(algorithm, encrypted, key, ivParameterSpec);
        } catch (Exception ignore) {
            return null;
        }
    }
}
