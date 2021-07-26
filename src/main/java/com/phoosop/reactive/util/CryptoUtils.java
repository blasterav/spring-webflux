package com.phoosop.reactive.util;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class CryptoUtils {

    public static String signText(byte[] data, String privateKey) throws Exception {
        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        PrivateKey key = generatePrivateKey(privateKey);
        privateSignature.initSign(key);
        privateSignature.update(data);
        byte[] s = privateSignature.sign();
        return Base64.getEncoder().encodeToString(s);
    }

    public static PrivateKey generatePrivateKey(String privateKeyPEM) throws Exception {
        // strip of header, footer, newlines, whitespaces
        privateKeyPEM = privateKeyPEM
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        // decode to get the binary DER representation
        byte[] privateKeyDER = Base64.getDecoder().decode(privateKeyPEM);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyDER));
    }

    public static PublicKey generatePublicKey(String publicKeyPEM) throws Exception {
        // strip of header, footer, newlines, whitespaces
        publicKeyPEM = publicKeyPEM
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        // decode to get the binary DER representation
        byte[] publicKeyDER = Base64.getDecoder().decode(publicKeyPEM);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyDER));
    }

    public static String encrypt(byte[] data, String publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/NOPADDING");
        PublicKey key = generatePublicKey(publicKey);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return Base64.getEncoder().encodeToString(cipher.doFinal(data));
    }

    public static String encryptWithPrivateKey(byte[] data, String privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/NOPADDING");
        PrivateKey key = generatePrivateKey(privateKey);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return Base64.getEncoder().encodeToString(cipher.doFinal(data));
    }

    public static String decrypt(byte[] messageBytes, String privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        PrivateKey key = generatePrivateKey(privateKey);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return Base64.getEncoder().encodeToString(cipher.doFinal(messageBytes));
    }

    public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        return kpg.generateKeyPair();
    }
}
