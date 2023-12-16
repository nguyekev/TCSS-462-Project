package lambda;

/**
 *
 * @author Wes Lloyd
 */
public class Request {

    String name;
    String filename;
    String bucketname;
    String outfilename;
    String imagePath;
    String url1, url2, url3;

    public String getName() {
        return name;
    }

    public String getUrl1() {
        return url1;
    }

    public void setUrl1(String url1) {
        this.url1 = url1;
    }
    public String getUrl2() {
        return url2;
    }

    public void setUrl2(String url2) {
        this.url2 = url2;
    }
    public String getUrl3() {
        return url3;
    }

    public void setUrl3(String url3) {
        this.url3 = url3;
    }
    
    public String getNameALLCAPS() {
        return name.toUpperCase();
    }

    public void setName(String name) {
        this.name = name;
    }

    public Request(String name) {
        this.name = name;
    }

    public Request() {

    }

    public String getFilename() {
        return filename;
    }
    public String getBucketname() {
        return bucketname;
    }
    public String getOutfilename() {
        return outfilename;
    }
    public String getImagePath() {
        return imagePath;
    }

    public void setFilename(String theFilename) {
        filename = theFilename;
    }
    public void setOutfilename(String theOutfilename) {
        outfilename = theOutfilename;
    }
    public void setBucketname(String theBucketname) {
        bucketname = theBucketname;
    }

    public void setImagePath(String theImagePath) {
        imagePath = theImagePath;
    }

}
