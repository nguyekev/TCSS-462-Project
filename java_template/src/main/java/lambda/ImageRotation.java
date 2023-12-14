package lambda;

import javax.imageio.ImageIO;
import javax.swing.*;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import saaf.Inspector;
import saaf.Response;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class ImageRotation implements RequestHandler<Request, HashMap<String, Object>> {

    /**
     * Lambda Function Handler
     * 
     * @param request Request POJO with defined variables from Request.java
     * @param context 
     * @return HashMap that Lambda will automatically convert into JSON.
     */
    public HashMap<String, Object> handleRequest(Request request, Context context) {
        
        //Collect inital data.
        Inspector inspector = new Inspector();
        //inspector.inspectAll();
        
        //****************START FUNCTION IMPLEMENTATION*************************
        String bucketname = request.getBucketname();
        String filename = request.getFilename();
        String outfilename = request.getOutfilename();

        AmazonS3 s3Client = AmazonS3ClientBuilder.standard().build();

        // get image from s3 bucket
        S3Object s3Object = s3Client.getObject(new GetObjectRequest(bucketname, filename));
        InputStream objectData = s3Object.getObjectContent();
        
        // convert the s3 object to a buffered image for processing
        BufferedImage image = null;
        try {
            image = ImageIO.read(objectData);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (image != null) { // error handling just in case
            // process image here
            image = rotateImage(image);

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                // get input stream
                ImageIO.write(image,"png", os);
                InputStream is = new ByteArrayInputStream(os.toByteArray());

                // specify meta type
                ObjectMetadata meta = new ObjectMetadata();
                meta.setContentType("image/png");

                // upload image to the s3 bucket
                s3Client.putObject(new PutObjectRequest(bucketname, outfilename, is, meta)
                                    .withCannedAcl(CannedAccessControlList.PublicRead));
            } catch (IOException e) {
                e.printStackTrace();
            } 
        }
        
        //Create and populate a separate response object for function output. (OPTIONAL)
        Response response = new Response();
        response.setValue("Bucket:" + bucketname + ", Filename:" + filename + ", Outfile: " + outfilename);
        
        inspector.consumeResponse(response);
        
        //****************END FUNCTION IMPLEMENTATION***************************
        
        //Collect final information such as total runtime and cpu deltas.
        inspector.inspectAllDeltas();
        return inspector.finish();
    }

    // public static void main(String[] args) {
    //     // Specify the file paths you want to check
    //     String filePath1 = "image/Electron Configuration Table.png";
    //     String filePath2 = "image/5xgkhmr1.png";

    //     // Rotate the first image
    //     rotateAndSaveImage(filePath1, "image/rotated1.png");

    //     // Rotate the second image
    //     rotateAndSaveImage(filePath2, "image/rotated2.png");
    // }
    private BufferedImage rotateImage(final BufferedImage image) {
        double rotationAngle = Math.toRadians(90);

        // Create a transformation matrix for rotation
        AffineTransform rotationTransform = new AffineTransform();
        rotationTransform.rotate(rotationAngle, image.getWidth() / 2, image.getHeight() / 2);
        double offset = (image.getWidth() - image.getHeight()) / 2;
        rotationTransform.translate(offset, offset);

        // Apply the transformation to the image
        BufferedImage rotatedImage = new BufferedImage(image.getHeight(), image.getWidth(), image.getType());
        Graphics2D g2d = rotatedImage.createGraphics();
        g2d.setTransform(rotationTransform);
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        return rotatedImage;
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

                        // Specify the rotation angle in radians 
                        double rotationAngle = Math.toRadians(90);

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
