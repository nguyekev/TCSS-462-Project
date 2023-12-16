package lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import saaf.Inspector;
import saaf.Response;

public class UploadImage implements RequestHandler<Request, HashMap<String, Object>> {

    public HashMap<String, Object> handleRequest(Request request, Context context) {
        Inspector inspector = new Inspector();
        inspector.inspectAll();

        String bucketname = request.getBucketname();
        String filename = request.getFilename();

        // Load the image using the class loader
        String imagePath = request.getImagePath();
        InputStream imageStream = getClass().getResourceAsStream(imagePath);

        if (imageStream != null) {
            try {
                context.getLogger().log("Reading image from: " + imagePath);

                BufferedImage image = ImageIO.read(imageStream);

                if (image != null) {
                    context.getLogger().log("Image read successfully.");

                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    ImageIO.write(image, "png", os);
                    InputStream is = new ByteArrayInputStream(os.toByteArray());

                    // Specify metadata for the image
                    ObjectMetadata meta = new ObjectMetadata();
                    meta.setContentType("image/png");

                    // Upload image to the S3 bucket
                    AmazonS3 s3Client = AmazonS3ClientBuilder.standard().build();
                    s3Client.putObject(new PutObjectRequest(bucketname, filename, is, meta));

                    context.getLogger().log("Image uploaded to S3. Bucket: " + bucketname + ", Filename: " + filename);
                } else {
                    context.getLogger().log("Failed to read the image. Image is null.");
                }

            } catch (IOException e) {
                e.printStackTrace();
                context.getLogger().log("Error reading the image: " + e.getMessage());
            }
        } else {
            context.getLogger().log("Image stream is null. Image file not found in the JAR.");
        }
        inspector.addAttribute("message", "bucketname=" + request.getBucketname() + " filename=" + request.getFilename()
                + " image path=" + request.getImagePath());
        
        Response response = new Response();
        response.setValue("Bucket:" + bucketname + ", Filename:" + filename + ", ImagePath:" + imagePath);

        inspector.consumeResponse(response);
        inspector.inspectAllDeltas();
        return inspector.finish();
    }
}