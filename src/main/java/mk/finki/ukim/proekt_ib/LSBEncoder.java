package mk.finki.ukim.proekt_ib;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

public class LSBEncoder {


    public static BufferedImage embedToImage(File imageFile, String message) throws IOException {
        BufferedImage image = ImageIO.read(imageFile);
        BufferedImage copy = deepCopy(image);
        Pixel[] pixels = toPixelArray(copy);


        String[] binaryMessage = message.chars()
                .mapToObj(c -> String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0'))
                .toArray(String[]::new);


        encode(binaryMessage, pixels);
        applyPixels(pixels, copy);
        return copy;
    }


    private static BufferedImage deepCopy(BufferedImage image) {
        ColorModel cm = image.getColorModel();
        boolean alphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = image.copyData(null);
        return new BufferedImage(cm, raster, alphaPremultiplied, null);
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


    private static void encode(String[] binaryMessage, Pixel[] pixels) {
        int pixelIndex = 0;
        for (int i = 0; i < binaryMessage.length; i++) {
            boolean lastChar = (i == binaryMessage.length - 1);
            Pixel[] group = {pixels[pixelIndex], pixels[pixelIndex + 1], pixels[pixelIndex + 2]};
            writeBits(binaryMessage[i], group, lastChar);
            pixelIndex += 3;
        }
    }


    private static void writeBits(String bits, Pixel[] group, boolean lastChar) {
        int bitIndex = 0;
        for (int i = 0; i < group.length - 1; i++) {
            char[] chunk = {bits.charAt(bitIndex++), bits.charAt(bitIndex++), bits.charAt(bitIndex++)};
            group[i].setColor(new Color(
                    setLSB(group[i].getColor().getRed(), chunk[0]),
                    setLSB(group[i].getColor().getGreen(), chunk[1]),
                    setLSB(group[i].getColor().getBlue(), chunk[2])
            ));
        }
// last pixel marks end of message
        char[] lastChunk = {bits.charAt(bitIndex++), bits.charAt(bitIndex++), lastChar ? '0' : '1'};
        group[2].setColor(new Color(
                setLSB(group[2].getColor().getRed(), lastChunk[0]),
                setLSB(group[2].getColor().getGreen(), lastChunk[1]),
                setLSB(group[2].getColor().getBlue(), lastChunk[2])
        ));
    }


    private static int setLSB(int value, char bit) {
        String bin = Integer.toBinaryString(value);
        String newBin = bin.substring(0, bin.length() - 1) + bit;
        return Integer.parseInt(newBin, 2);
    }


    private static void applyPixels(Pixel[] pixels, BufferedImage image) {
        for (Pixel p : pixels) {
            image.setRGB(p.getX(), p.getY(), p.getColor().getRGB());
        }
    }
}