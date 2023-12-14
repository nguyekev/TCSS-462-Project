package lambda;

import java.io.File;
import java.io.IOException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.AmazonServiceException;

public class UploadImage {
    
public static void main(String[] args) throws IOException {
        // Provide your bucket name and file name
        String bucketName = "YOUR_S3_BUCKET_NAME";
        String fileName = "example.jpg";

        // Create an instance of UploadObject
        UploadImage uploader = new UploadImage(bucketName, fileName);
    }

    String bucket;

    /**
     * Constructor
     */
    public UploadImage(String theBucket, String fileName) {
        bucket = theBucket;
        UploadImageToBucket(bucket, fileName);
    }

    /**
     * The method to upload the image to the bucket
     */
    public static void UploadImageToBucket(String bucketName, String fileName) {
        String filePath = "/tmp/" + fileName;
        // String filePath = System.getProperty("user.dir") + "/" + fileName;
        System.out.println(filePath);
        String key = fileName;

        try {
            AmazonS3 client = AmazonS3ClientBuilder.standard().build();
            client.putObject(bucketName, key, new File(filePath));
            System.out.println("File " + fileName + " was uploaded.");
        } catch (AmazonServiceException e) {
            System.err.println("Amazon service exception: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
        }
    }
}

