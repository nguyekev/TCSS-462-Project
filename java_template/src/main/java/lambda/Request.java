package lambda;

/**
 *
 * @author Wes Lloyd
 */
public class Request {

    private String filename;
    private String bucket;


    public String getFilename() {
        return filename;
    }

    public String getBucket() {
        return bucket;
    }

    public void setFilename(String filename) {
        this.filename = filename;

    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }


    public Request() {

    }
}
