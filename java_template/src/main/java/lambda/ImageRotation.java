package lambda;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ImageRotation {

    public static void main(String[] args) {
        // Specify the file paths you want to check
        String filePath1 = "image/Electron Configuration Table.png";
        String filePath2 = "image/5xgkhmr1.png";

        // Rotate the first image
        rotateAndSaveImage(filePath1, "image/rotated1.png");

        // Rotate the second image
        rotateAndSaveImage(filePath2, "image/rotated2.png");
    }

    private static void rotateAndSaveImage(String inputFilePath, String outputFilePath) {
        // Convert the file path to a Path object
        Path path = Path.of(inputFilePath);

        // Check if the file exists
        if (Files.exists(path)) {
            // Check read permissions
            if (hasReadPermissions(path)) {
                System.out.println("Read permission is granted for " + inputFilePath);

                // Check write permissions
                if (hasWritePermissions(path)) {
                    System.out.println("Write permission is granted for " + inputFilePath);

                    // Rotate the image
                    try {
                        System.out.println("Image at: " + new File(inputFilePath).getAbsolutePath());

                        // Load the original image
                        BufferedImage originalImage = ImageIO.read(new File(inputFilePath));

                        // Specify the rotation angle in radians (degrees in this example)
                        double rotationAngle = Math.toRadians(45);

                        // Create a transformation matrix for rotation
                        AffineTransform rotationTransform = new AffineTransform();
                        rotationTransform.rotate(rotationAngle, originalImage.getWidth() / 2, originalImage.getHeight() / 2);

                        // Apply the transformation to the image
                        BufferedImage rotatedImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), originalImage.getType());
                        Graphics2D g2d = rotatedImage.createGraphics();
                        g2d.setTransform(rotationTransform);
                        g2d.drawImage(originalImage, 0, 0, null);
                        g2d.dispose();

                        // Save the rotated image to a new file
                        ImageIO.write(rotatedImage, "png", new File(outputFilePath));
                        System.out.println("Image saved to: " + new File(outputFilePath).getAbsolutePath());

                        System.out.println("Image rotation complete for " + inputFilePath);
                        // Display the rotated image in a frame
                        JFrame frame = new JFrame();
                        frame.setTitle("Rotated Image");

                        // Add the rotated image to the frame
                        JLabel imageLabel = new JLabel(new ImageIcon(outputFilePath));
                        frame.add(imageLabel);

                        // Pack the frame and make it visible
                        frame.pack();
                        frame.setVisible(true);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Write permission is denied for " + inputFilePath);
                }
            } else {
                System.out.println("Read permission is denied for " + inputFilePath);
            }
        } else {
            System.out.println("File does not exist: " + inputFilePath);
        }
    }

    private static boolean hasReadPermissions(Path path) {
        try {
            // Check read permissions
            return Files.isReadable(path);
        } catch (SecurityException e) {
            System.out.println("SecurityException: " + e.getMessage());
            return false;
        }
    }

    private static boolean hasWritePermissions(Path path) {
        try {
            // Check write permissions
            return Files.isWritable(path);
        } catch (SecurityException e) {
            System.out.println("SecurityException: " + e.getMessage());
            return false;
        }
    }
}
