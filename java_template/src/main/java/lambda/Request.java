package lambda;

/**
 *
 * @author Wes Lloyd
 */
public class Request {

    String name;
    String filename;
    String bucketname;
    String filepath;

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
    public String getFilePath() {
        return filepath;
    }
    public String getBucketname() {
        return bucketname;
    }

}
