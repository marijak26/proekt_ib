package mk.finki.ukim.proekt_ib;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.util.Arrays;
import java.util.Scanner;

@SpringBootApplication
public class ProektIbApplication {

    private static EmailService emailService;

    public static void main(String[] args) throws Exception {
        ApplicationContext context = SpringApplication.run(ProektIbApplication.class, args);
        emailService = context.getBean(EmailService.class);

        Scanner myObj = new Scanner(System.in);
        System.out.println("What would you like to do?(A/B)\nA:Write a hidden message to an image.\nB:Read a hidden message from an image");

        String action = myObj.nextLine();

        if (action.equalsIgnoreCase("A")) {
            System.out.println("What message would you like to hide?");
            String message = myObj.nextLine();

            System.out.println("Where is your image located?");
            String input_path = myObj.nextLine();

            System.out.println("Where would you like to save the output image?");
            String output_path = myObj.nextLine();

            generateImage(message, input_path, output_path);
        } else if (action.equalsIgnoreCase("B")) {
            System.out.println("Enter the secret keys with []:");
            String input = myObj.nextLine();
            String[] byteStrings = input.substring(1, input.length() - 1).split(", ");
            byte[] secretKeyBytes = new byte[byteStrings.length];
            for (int i = 0; i < byteStrings.length; i++) {
                secretKeyBytes[i] = Byte.parseByte(byteStrings[i]);
            }

            System.out.println("Enter the location of the image:");
            String output_path = myObj.nextLine();

            System.out.println("Enter the cipher algorithm. (e.g., AES)");
            String cipher_algorithm = myObj.nextLine();

            extractText(secretKeyBytes, output_path, cipher_algorithm);
        } else {
            System.out.println("Invalid input!");
        }
    }

    public static void generateImage(String message, String input_path, String output_path) throws Exception {
        File imageFile = new File(input_path);
        TextEncryptor textEncryptor = new TextEncryptor();
        SecretKey key = textEncryptor.generateAESKey();
        Cipher cipher = textEncryptor.getCipher();
        byte[] secretKeyBytes = key.getEncoded();

        String encryptedText = textEncryptor.encrypt(message, key, cipher);
        if (imageFile != null) {
            EmbedLSB.Embed(imageFile, encryptedText, output_path);
            System.out.println("Message hidden successfully!");
        }

        System.out.println("SecretKey (byte array): " + Arrays.toString(secretKeyBytes));
        System.out.println("Cipher: AES");

        Scanner sc = new Scanner(System.in);
        System.out.println("Send results by email? (Y/N)");
        String choice = sc.nextLine();

        if (choice.equalsIgnoreCase("Y")) {
            System.out.println("Recipient email for IMAGE + KEY INFO:");
            String recipient = sc.nextLine();

            String keyInfo = "Cipher algorithm: AES\n" +
                    "Secret key bytes: " + Arrays.toString(secretKeyBytes) + "\n\n" +
                    "Use this info to extract the message from the image.";

            emailService.sendMessageWithAttachment(
                    recipient,
                    "Image with hidden message + Key Info",
                    "Hi,\n\nHere’s the image with the hidden message.\n\n" + keyInfo + "\n\n—App",
                    new File(output_path)
            );

            System.out.println("Email sent successfully!");
        }
    }

    public static void extractText(byte[] secretKeyBytes, String output_path, String cipher_algorithm) throws Exception {
        SecretKey receivedSecretKey = new SecretKeySpec(secretKeyBytes, cipher_algorithm);
        Cipher receivedCipher = Cipher.getInstance(cipher_algorithm);
        receivedCipher.init(Cipher.DECRYPT_MODE, receivedSecretKey);
        ExtractLSB.Extract(output_path, receivedSecretKey, receivedCipher);
    }
}
