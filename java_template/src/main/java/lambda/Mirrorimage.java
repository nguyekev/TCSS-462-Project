package lambda;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

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

public class Mirrorimage implements RequestHandler<Request, HashMap<String, Object>> {
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
            image = mirrorImage(image);

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
     * Apply mirror image transformation to a buffered image.
     * @param image the buffered image
     * @return mirrored buffered image
     */
    private static BufferedImage mirrorImage(final BufferedImage image) {
        AffineTransform tx = AffineTransform.getScaleInstance(-1.0,1.0); 
        tx.translate(-image.getWidth(), 0); 
        AffineTransformOp tr = new AffineTransformOp(tx,null);
        
        return tr.filter(image, null);
    }

    // public static void main(String[] args) {
    //     BufferedImage img = TestMain.readFromFile("C:/Users/zebol/Pictures/Bird.png");
    //     img = GrayScale.applyGrayScale(img);
    //     TestMain.writeToFile(img, "C:/Users/zebol/Pictures/BirdG.png");
    //     img = mirrorImage(img);
    //     TestMain.writeToFile(img, "C:/Users/zebol/Pictures/BirdM.png");
    // }

    // public static void main(String args[])throws IOException{
    //     //BufferedImage for source image
    //     BufferedImage simg = null;
        
    //     File f = null;
      

    //     try{
    //         f = new File("path.png");
    //         simg = ImageIO.read(f);
    //     }catch(IOException e){
    //         System.out.println("Error: " + e);
    //     }

    //     int width = simg.getWidth();
    //     int height = simg.getHeight();

    //     BufferedImage mimg = new BufferedImage(width * 2, height, BufferedImage.TYPE_INT_ARGB);
        

    //     for(int y = 0; y < height; y++){
    //         for(int lx = 0, rx = width*2 - 1; lx < width; lx++, rx--) {

                
    //             int p = simg.getRGB(lx, y);
    //             mimg.setRGB(lx, y, p);
    //             mimg.setRGB(rx, y, p);
    //         }
    //     }

    //     // crop just the right half of the image (the mirrored part)
    //     BufferedImage mimghalf = mimg.getSubimage(width, 0, width, height);
        
    //     //save mirror image
    //     try{
    //         f = new File("out.png");
    //         ImageIO.write(mimghalf, "png", f);
    //     }catch(IOException e){
    //         System.out.println("Error: " + e);
    //     }
    // }
}
