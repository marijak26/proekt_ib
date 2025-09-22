package mk.finki.ukim.proekt_ib;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Scanner;


@SpringBootApplication
public class ProektIbApplication {


    private static EmailService emailService;


    public static void main(String[] args) throws Exception {
        ApplicationContext context = SpringApplication.run(ProektIbApplication.class, args);
        emailService = context.getBean(EmailService.class);


        Scanner sc = new Scanner(System.in);
        System.out.println("Choose action (A/B):\nA: Hide a message in an image\nB: Extract a hidden message");
        String action = sc.nextLine();


        if (action.equalsIgnoreCase("A")) {
            System.out.println("Enter the message:");
            String msg = sc.nextLine();
            System.out.println("Enter image path:");
            String path = sc.nextLine();
            generateImage(msg, path);
        } else if (action.equalsIgnoreCase("B")) {
            System.out.println("Enter secret key bytes (format: [1,2,3,...]):");
            String input = sc.nextLine();
            String[] parts = input.substring(1, input.length() - 1).split(", ");
            byte[] keyBytes = new byte[parts.length];
            for (int i = 0; i < parts.length; i++) {
                keyBytes[i] = Byte.parseByte(parts[i].trim());
            }



            System.out.println("Enter image path:");
            String imgPath = sc.nextLine();
            extractText(keyBytes, imgPath, "AES");
        } else {
            System.out.println("Invalid input!");
        }
    }


    private static void generateImage(String message, String inputPath) throws Exception {
        File imageFile = new File(inputPath);
        SecretKey key = AESCryptoUtil.generateKey(256);
        Cipher cipher = AESCryptoUtil.getCipher("AES");


        String encrypted = AESCryptoUtil.encrypt(message, key, cipher);
        BufferedImage stego = LSBEncoder.embedToImage(imageFile, encrypted);

        File outputFile = new File(imageFile.getParent(),"hidden.png");
        javax.imageio.ImageIO.write(stego, "png", outputFile);

        System.out.println("Message hidden successfully!");
        System.out.println("Key bytes: " + Arrays.toString(key.getEncoded()));


        Scanner sc = new Scanner(System.in);
        System.out.println("Send results by email? (Y/N)");
        if (sc.nextLine().equalsIgnoreCase("Y")) {
            System.out.println("Recipient email:");
            String recipient = sc.nextLine();
            String info = "Cipher: AES\nKey bytes: " + Arrays.toString(key.getEncoded());


            emailService.sendImage(recipient, "Stego Image + Key", info, stego, "hidden.png");
            System.out.println("Email sent!");
        }
    }


    private static void extractText(byte[] keyBytes, String path, String algo) throws Exception {
        SecretKey key = AESCryptoUtil.fromBytes(keyBytes, algo);
        Cipher cipher = AESCryptoUtil.getCipher(algo);
        LSBDecoder.extract(path, key, cipher);
    }
}