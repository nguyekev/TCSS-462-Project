package lambda;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;

import javax.imageio.ImageIO;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;

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
        //inspector.inspectAll();
        
        //****************START FUNCTION IMPLEMENTATION*************************
        // int row = request.getRow();
        // int col = request.getCol();
        String bucketname = request.getBucketname();
        String filename = request.getFilename();
        String input_file = request.getFilePath();

        BufferedImage image = readFromFile(input_file);
        DataBufferByte data = (DataBufferByte) image.getRaster().getDataBuffer();
        
        InputStream is = new ByteArrayInputStream(data.getData());
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentType("image/png");
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard().build();
        s3Client.putObject(bucketname, filename, is, meta);
        
        //Create and populate a separate response object for function output. (OPTIONAL)
        Response response = new Response();
        response.setValue("Bucket:" + bucketname + " filename:" + filename);
        
        inspector.consumeResponse(response);
        
        //****************END FUNCTION IMPLEMENTATION***************************
        
        //Collect final information such as total runtime and cpu deltas.
        inspector.inspectAllDeltas();
        return inspector.finish();
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