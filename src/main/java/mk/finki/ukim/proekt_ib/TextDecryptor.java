package mk.finki.ukim.proekt_ib;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;


public class TextDecryptor {
    public String decrypt(String base64CipherText, SecretKey secretKey, Cipher cipher) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        try {
            byte[] cipherBytes = Base64.getDecoder().decode(base64CipherText);
            byte[] decryptedBytes = cipher.doFinal(cipherBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.out.println("No hidden message");
        }


        return "";
    }
}
