import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;


public class Address {

    private static final Logger LOGGER = Logger.getLogger(Address.class);


    protected String path = "address.json";

    private String pathProperties = "refresh.properties";
    private String street;
    private String home;
    private JSONObject objectJSON;
    private Properties prop;
    private String strAddress;

    public Address() {

        fieldsAddressDefault();
        refreshAddress();
    }

    private void fieldsAddressDefault(){

        Properties defaultProp = new Properties();
        defaultProp.setProperty("address", "{\"street\": \"street\", \"home\": \"0\"}");
        defaultProp.setProperty("street", "street");
        defaultProp.setProperty("home", "0");
        prop = new Properties(defaultProp);
    }


   protected String refreshStrAdr(){

       try {
           BufferedReader reader = new BufferedReader(new FileReader(pathProperties));
           prop.load(reader);
           setStrAddress( prop.getProperty("address"));

       } catch (Exception e) {

           LOGGER.error(e.getMessage(), e);
       }
       return strAddress;
   }



    private void updateVarInFile(){

        PrintWriter pw = null;

        try {
            pw = new PrintWriter(path);
            pw.write(getStrAddress());
            pw.flush();

        } catch (Exception e) {

            LOGGER.error(e.getMessage(), e);

        } finally {
            pw.close();
        }
    }


    public synchronized void refreshAddress() {

        refreshStrAdr();
        updateVarInFile();

        try {
            JSONParser parser = new JSONParser();
            objectJSON = (JSONObject) parser.parse(new String(Files.readAllBytes(Paths.get(path))));
            setStreet((String) objectJSON.get("street"));
            setHome((String) objectJSON.get("home"));
            this.street = getStreet();
            this.home = getHome();

        } catch (Exception e) {

            LOGGER.error(e.getMessage(), e);
        }
    }


    public String getStreet() { return street; }
    private void setStreet(String street) { this.street = street; }

    public String getHome() { return home;}
    private void setHome(String home) { this.home = home;}

    public JSONObject getObjectJSON() { return objectJSON; }
    private void setObjectJSON(JSONObject object) { this.objectJSON = object; }

    public String getStrAddress() { return strAddress;  }
    private void setStrAddress(String strAddress) { this.strAddress = strAddress;  }


}
