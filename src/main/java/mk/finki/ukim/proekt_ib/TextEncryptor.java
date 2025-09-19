package mk.finki.ukim.proekt_ib;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;


public class TextEncryptor {

    public SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        return keyGen.generateKey();
    }

    public Cipher getCipher() throws Exception {
        return Cipher.getInstance("AES");
    }

    public String encrypt(String input, SecretKey secretKey, Cipher cipher) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedBytes = cipher.doFinal(inputBytes);

        return Base64.getEncoder().encodeToString(encryptedBytes);
    }
}
