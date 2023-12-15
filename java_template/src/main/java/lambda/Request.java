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
    String url;

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
