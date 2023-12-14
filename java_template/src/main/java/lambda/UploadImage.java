package lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
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
        String imagePath = request.getImagePath();

        AmazonS3 s3Client = AmazonS3ClientBuilder.standard().build();


        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                context.getLogger().log("Reading image from: " + imagePath);

                BufferedImage image = ImageIO.read(new File(imagePath));

                if (image != null) {
                    context.getLogger().log("Image read successfully.");

                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    ImageIO.write(image, "png", os);
                    InputStream is = new ByteArrayInputStream(os.toByteArray());

                    // Specify metadata for the image
                    ObjectMetadata meta = new ObjectMetadata();
                    meta.setContentType("image/png");

                    // Upload image to the S3 bucket
                    s3Client.putObject(new PutObjectRequest(bucketname, filename, is, meta)
                            .withCannedAcl(CannedAccessControlList.PublicRead));

                    context.getLogger().log("Image uploaded to S3. Bucket: " + bucketname + ", Filename: " + filename);
                } else {
                    context.getLogger().log("Failed to read the image.");
                }

            } catch (IOException e) {
                e.printStackTrace();
                context.getLogger().log("Error reading the image: " + e.getMessage());
            }
        } else {
            context.getLogger().log("Image path is null or empty.");
        }

        Response response = new Response();
        response.setValue("Bucket:" + bucketname + ", Filename:" + filename + ", ImagePath:" + imagePath);

        inspector.consumeResponse(response);
        return inspector.finish();
    }
}