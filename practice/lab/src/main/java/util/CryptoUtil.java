package org.vaje8_playground;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

public class CryptoUtil {
    public static PublicKey pub;
    public static PrivateKey pvt;

    public static void generatePubPvtKey() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();
            pub = keyPair.getPublic();
            pvt = keyPair.getPrivate();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static String publicKeyToString(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    public static PublicKey stringToPublicKey(String publicKeyString) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(publicKeyString);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            return keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean verifyMessage(Message message) {
        boolean valid = false;
        try {
            Signature rsaVerify = Signature.getInstance("SHA256withRSA");
            EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(message.senderPublicKey));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
            rsaVerify.initVerify(publicKey);
            rsaVerify.update(message.body.getBytes("UTF-8"));
            valid = rsaVerify.verify(Base64.getDecoder().decode(message.signature));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | UnsupportedEncodingException | SignatureException e) {
            e.printStackTrace();
        }
        return valid;
    }


    public static Message signMessage(Message message) {
        try {
            Signature rsaSign = Signature.getInstance("SHA256withRSA");
            rsaSign.initSign(pvt);
            rsaSign.update(message.body.getBytes("UTF-8"));
            byte[] signature = rsaSign.sign();

            String pub_key = Base64.getEncoder().encodeToString(pub.getEncoded());
            String sig = Base64.getEncoder().encodeToString(signature);

            message.signature = sig;
            message.senderPublicKey = pub_key;
        } catch (NoSuchAlgorithmException | InvalidKeyException | UnsupportedEncodingException | SignatureException e) {
            e.printStackTrace();
        }
        return message;
    }

    public static String encryptBody(String body, PublicKey recipientPublicKey) throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        SecretKey aesKey = keyGen.generateKey();

        Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] iv = new byte[16];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        aesCipher.init(Cipher.ENCRYPT_MODE, aesKey, ivSpec);
        byte[] encryptedBody = aesCipher.doFinal(body.getBytes("UTF-8"));
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        rsaCipher.init(Cipher.ENCRYPT_MODE, recipientPublicKey);
        byte[] encryptedAESKey = rsaCipher.doFinal(aesKey.getEncoded());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(iv);

        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        dataOutputStream.writeInt(encryptedAESKey.length);

        outputStream.write(encryptedAESKey);
        outputStream.write(encryptedBody);
        byte[] combined = outputStream.toByteArray();
        return Base64.getEncoder().encodeToString(combined);
    }

    public static String decryptBody(String encryptedData, PrivateKey privateKey) throws Exception {
        byte[] combined = Base64.getDecoder().decode(encryptedData);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(combined);

        byte[] iv = new byte[16];
        inputStream.read(iv);
        DataInputStream dataInputStream = new DataInputStream(inputStream);

        int encryptedAESKeyLength = dataInputStream.readInt();
        byte[] encryptedAESKey = new byte[encryptedAESKeyLength];
        dataInputStream.readFully(encryptedAESKey);

        byte[] encryptedBody = inputStream.readAllBytes();
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);

        byte[] aesKeyBytes = rsaCipher.doFinal(encryptedAESKey);
        SecretKeySpec aesKey = new SecretKeySpec(aesKeyBytes, "AES");
        Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        aesCipher.init(Cipher.DECRYPT_MODE, aesKey, ivSpec);

        byte[] decryptedBodyBytes = aesCipher.doFinal(encryptedBody);
        return new String(decryptedBodyBytes, "UTF-8");
    }

}