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

    public String getName() {
        return name;
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

    public void setFilename(String theFilename) {
        filename = theFilename;
    }
    public void setOutfilename(String theOutfilename) {
        outfilename = theOutfilename;
    }
    public void setBucketname(String theBucketname) {
        bucketname = theBucketname;
    }

}
