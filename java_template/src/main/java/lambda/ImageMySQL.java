package lambda;


import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context; 
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.Properties;
import saaf.Inspector;
import java.util.HashMap;

/**
 * uwt.lambda_test::handleRequest
 *
 * @author Wes Lloyd
 * @author Robert Cordingly
 */
public class ImageMySQL implements RequestHandler<Request, HashMap<String, Object>> {

    /**
     * Lambda Function Handler
     * 
     * @param request Request POJO with defined variables from Request.java
     * @param context 
     * @return HashMap that Lambda will automatically convert into JSON.
     */
    public HashMap<String, Object> handleRequest(Request request, Context context) {

        // Create logger
        LambdaLogger logger = context.getLogger();        

        //Collect inital data.
        Inspector inspector = new Inspector();
        inspector.inspectAll();
        
        //****************START FUNCTION IMPLEMENTATION*************************
        //Add custom key/value attribute to SAAF's output. (OPTIONAL)
        
        //Create and populate a separate response object for function output. (OPTIONAL)
        Response r = new Response();

        try 
        {
            Properties properties = new Properties();
            properties.load(new FileInputStream("db.properties"));
            
            context.getLogger().log("Properties init");
            String url = properties.getProperty("url");
            String username = properties.getProperty("username");
            String password = properties.getProperty("password");
            String driver = properties.getProperty("driver");
            context.getLogger().log("Properties init done");
            
            r.setValue(request.getUrl());
            // Manually loading the JDBC Driver is commented out
            // No longer required since JDBC 4
            //Class.forName(driver);
            Connection con = DriverManager.getConnection(url,username,password);
            context.getLogger().log("Connection done");
            
            PreparedStatement ps = con.prepareStatement("insert into urltable values('" + request.getUrl() + "');");
            ps.execute();
            ps = con.prepareStatement("select * from urltable;");
            
            ResultSet rs = ps.executeQuery();
            LinkedList<String> ll = new LinkedList<String>();
            while (rs.next())
            {
                logger.log("url=" + rs.getString("url"));
                ll.add(rs.getString("url"));
            }
            ResultSet rs2 = con.prepareStatement("select version() as version;").executeQuery();
            while (rs2.next())
            {
                logger.log("MySqlVersion=" + rs2.getString("version"));
                r.setSqlVersion(rs2.getString("version"));
            }
            rs.close();
            rs2.close();
            con.close();
            r.setNames(ll);
        } 
        catch (Exception e) 
        {
            logger.log("Got an exception working with MySQL! ");
            logger.log(e.getMessage());
        }

        //Print log information to the Lambda log as needed
        //logger.log("log message...");
        
        inspector.consumeResponse(r);
        
        //****************END FUNCTION IMPLEMENTATION***************************
        
        //Collect final information such as total runtime and cpu deltas.
        inspector.inspectAllDeltas();
        return inspector.finish();
    }


    // int main enables testing function from cmd line
    public static void main (String[] args)
    {
        Context c = new Context() {
            @Override
            public String getAwsRequestId() {
                return "";
            }

            @Override
            public String getLogGroupName() {
                return "";
            }

            @Override
            public String getLogStreamName() {
                return "";
            }

            @Override
            public String getFunctionName() {
                return "";
            }

            @Override
            public String getFunctionVersion() {
                return "";
            }

            @Override
            public String getInvokedFunctionArn() {
                return "";
            }

            @Override
            public CognitoIdentity getIdentity() {
                return null;
            }

            @Override
            public ClientContext getClientContext() {
                return null;
            }

            @Override
            public int getRemainingTimeInMillis() {
                return 0;
            }

            @Override
            public int getMemoryLimitInMB() {
                return 0;
            }

            @Override
            public LambdaLogger getLogger() {
                return new LambdaLogger() {
                    @Override
                    public void log(String string) {
                        System.out.println("LOG:" + string);
                    }
                };
            }
        };

        // Create an instance of the class
        ImageMySQL lt = new ImageMySQL();

        // Create a request object
        Request req = new Request();

        // Grab the name from the cmdline from arg 0
        String name = (args.length > 0 ? args[0] : "");

        // Load the name into the request object
        req.setName(name);

        // Report name to stdout
        System.out.println("cmd-line param name=" + req.getName());

        // Test properties file creation
        Properties properties = new Properties();
        properties.setProperty("driver", "com.mysql.cj.jdbc.Driver");
        properties.setProperty("url","");
        properties.setProperty("username","");
        properties.setProperty("password","");
        try
        {
          properties.store(new FileOutputStream("test.properties"),"");
        }
        catch (IOException ioe)
        {
          System.out.println("error creating properties file.")   ;
        }


        // Run the function
        //Response resp = lt.handleRequest(req, c);
        System.out.println("The MySQL Serverless can't be called directly without running on the same VPC as the RDS cluster.");
        Response resp = new Response();

        // Print out function result
        System.out.println("function result:" + resp.toString());
    }

}
