package lambda;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

/**
 * Testing without updating lambda every single time.
 */
public class TestMain {
    /** Your Credentials. */
    private static final AWSCredentials credentials = new BasicAWSCredentials(
        "<Access Key>", 
        "<Private Key>"
    );

    /** S3 Bucket Name. */
    private static final String bucketname = "tcss.bucket.462.f23.image.bl";
    /** Image name that is already in the bucket to get. */
    private static final String filename = "Dog.png";
    /** Output file name to upload to the bucket after finsihed processing. */
    private static final String outfilename = "GrayDog.png";

    /**
     * Main.
     * @param args none
     */
    public static void main(String[] args) {
        AmazonS3 s3Client = AmazonS3ClientBuilder
            .standard()
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .withRegion(Regions.US_EAST_2)
            .build();

        // get s3 object given bucket name and the file name
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
            image = applyGrayScale(image);
            

            // uploading
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
    }

    // transformation methods
    
    /**
     * Apply gray scale transformation to a buffered image.
     * @param image the buffered image
     * @return grayscaled buffered image
     */
    private static BufferedImage applyGrayScale(final BufferedImage image) {
        BufferedImage grayImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = grayImage.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return grayImage;
    }

    

    // unused methods

    /**
     * Read a image from a file path.
     * @param path the file path as a string
     * @return a buffered image
     */
    public static BufferedImage readFromFile(final String path) {
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

    /**
     * Save a buffered image as a file in system.
     * @param image the buffered image
     * @param outPath the path of the output
     */
    public static void writeToFile(final BufferedImage image, final String outPath) {
        try {
            // Output file path
            File output_file = new File(outPath);

            // Writing to file taking type and path
            ImageIO.write(image, "png", output_file);

            System.out.println("Writing complete.");
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }
}
