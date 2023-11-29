package lambda;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class GrayScale {
    public static void main(String[] args) {

        String imageFile = "lol.png";
        String outFile = "output.png";

        BufferedImage image = readFromFile(imageFile);
        image = applyGrayScale(image);

        writeToFile(image, outFile);
    }

    private static BufferedImage readFromFile(final String path) {
        BufferedImage image = null;
        try {
            File input_file = new File(path);

            // Reading input file
            image = ImageIO.read(input_file);

            System.out.println("Reading complete.");
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        return image;
    }

    private static void writeToFile(final BufferedImage image, final String outPath) {
        try {
            // Output file path
            File output_file = new File(outPath);

            // Writing to file taking type and path as
            ImageIO.write(image, "png", output_file);

            System.out.println("Writing complete.");
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }

    private static BufferedImage applyGrayScale(final BufferedImage image) {
        System.out.println("Starting Grayscale Conversion...");
        BufferedImage grayImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = grayImage.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        System.out.println("...Finish Grayscale Conversion");
        return grayImage;
    }
}