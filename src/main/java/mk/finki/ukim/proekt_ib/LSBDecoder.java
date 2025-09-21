package mk.finki.ukim.proekt_ib;


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class LSBDecoder {


    public static void extract(String imagePath, SecretKey secretKey, Cipher cipher) throws Exception {
        BufferedImage image = ImageIO.read(new File(imagePath));
        Pixel[] pixels = toPixelArray(image);
        String hidden = extractMessage(pixels);
        try {
            String decrypted = AESCryptoUtil.decrypt(hidden, secretKey, cipher);
            System.out.println("Message: " + decrypted);
        } catch (Exception e) {
            System.out.println("No hidden message or wrong key.");
        }
    }


    private static Pixel[] toPixelArray(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        Pixel[] pixels = new Pixel[width * height];
        int index = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                pixels[index++] = new Pixel(x, y, new Color(image.getRGB(x, y)));
            }
        }
        return pixels;
    }


    private static String extractMessage(Pixel[] pixels) {
        StringBuilder sb = new StringBuilder();
        int index = 0;
        boolean done = false;
        while (!done) {
            Pixel[] group = {pixels[index++], pixels[index++], pixels[index++]};
            sb.append(toChar(group));
            done = isEnd(group[2]);
        }
        return sb.toString();
    }


    private static char toChar(Pixel[] group) {
        ArrayList<Character> bits = new ArrayList<>();
        for (Pixel p : group) {
            bits.add(lastBit(p.getColor().getRed()));
            bits.add(lastBit(p.getColor().getGreen()));
            bits.add(lastBit(p.getColor().getBlue()));
        }
        String bitString = bits.subList(0, bits.size() - 1).stream()
                .map(String::valueOf)
                .reduce("", String::concat);
        int ascii = Integer.parseInt(bitString, 2);
        return (char) ascii;
    }


    private static char lastBit(int value) {
        return Integer.toBinaryString(value).charAt(Integer.toBinaryString(value).length() - 1);
    }


    private static boolean isEnd(Pixel pixel) {
        return lastBit(pixel.getColor().getBlue()) == '0';
    }
}