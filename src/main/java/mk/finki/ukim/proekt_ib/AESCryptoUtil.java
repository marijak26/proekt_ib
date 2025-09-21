package mk.finki.ukim.proekt_ib;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;


public class AESCryptoUtil {


    public static SecretKey generateKey(int keySize) throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(keySize);
        return keyGen.generateKey();
    }


    public static SecretKey fromBytes(byte[] keyBytes, String algorithm) {
        return new SecretKeySpec(keyBytes, algorithm);
    }


    public static Cipher getCipher(String algorithm) throws Exception {
        return Cipher.getInstance(algorithm);
    }


    public static String encrypt(String plainText, SecretKey secretKey, Cipher cipher) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }


    public static String decrypt(String base64CipherText, SecretKey secretKey, Cipher cipher) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decoded = Base64.getDecoder().decode(base64CipherText);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted, StandardCharsets.UTF_8);
    }
}
