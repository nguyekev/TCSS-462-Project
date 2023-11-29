package lambda;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Mirrorimage{
    public static void main(String args[])throws IOException{
        //BufferedImage for source image
        BufferedImage simg = null;
        
        File f = null;
      

        try{
            f = new File("FILE PATH HERE");
            simg = ImageIO.read(f);
        }catch(IOException e){
            System.out.println("Error: " + e);
        }

        int width = simg.getWidth();
        int height = simg.getHeight();

        BufferedImage mimg = new BufferedImage(width*2, height, BufferedImage.TYPE_INT_ARGB);
        

        for(int y = 0; y < height; y++){
            for(int lx = 0, rx = width*2 - 1; lx < width; lx++, rx--){

                
                int p = simg.getRGB(lx, y);
                mimg.setRGB(lx, y, p);
                mimg.setRGB(rx, y, p);
            }
        }
        
        //save mirror image
        try{
            f = new File("D:\\Image\\Output.png");
            ImageIO.write(mimg, "png", f);
        }catch(IOException e){
            System.out.println("Error: " + e);
        }
    }
}
