package lambda;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import saaf.Inspector;
import saaf.Response;

public class GrayScale implements RequestHandler<Request, HashMap<String, Object>> {
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
        inspector.inspectAll();
        
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
            image = applyGrayScale(image);

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
}