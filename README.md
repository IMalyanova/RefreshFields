public class Loader {

    public static void main(String[] args)   {

        RefreshFields refreshFields = RefreshFields.getInstance();
   }

}



//====================



import org.apache.log4j.Logger;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Properties;

public class RefreshFields {

    private static final Logger LOGGER = Logger.getLogger(RefreshFields.class);

    @Property(nameInterface = "old", defaultValue = "0")
    private String old;

    @Property(nameInterface = "name", defaultValue = "Company")
    private String name;

    @Property(nameInterface = "address")
    private Address address;

    private static RefreshFields instance;
    private Properties prop;
    private String path = "refresh.properties";



    private RefreshFields() {

        fieldsDefault();
        doRefresh();
        address = new Address();
    }

    public static synchronized RefreshFields getInstance() {

        if (instance == null) {

            instance = new RefreshFields();
        }
        return instance;
    }


    private void fieldsDefault() {

        Properties defaultProp = new Properties();
        defaultProp.setProperty("name", "Company");
        defaultProp.setProperty("old", "0");
        prop = new Properties(defaultProp);
    }


    public synchronized void doRefresh() {

        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            prop.load(reader);

            this.serchAnatation();

        } catch (Exception e) {

            LOGGER.error(e.getMessage(), e);
        }
    }


    private void serchAnatation() {

        Field fields[] = this.getClass().getDeclaredFields();

        for (Field field : fields) {

            Annotation annotations[] = field.getDeclaredAnnotations();

            for (Annotation annotation : annotations) {

                if (annotation.annotationType().equals(Property.class)) {

                    switch (field.getName()) {

                        case "name":
                            refreshName();
                            break;
                        case "old":
                            refreshOld();
                            break;
                        case "address":
                            if (address == null){
                                break;
                            }else {
                                address.refreshAddress();
                                break;
                            }
                    }
                }
            }
        }



    }


    public String getOld() {return old;}
    private void setOld(String old) { this.old = old;}
    public void refreshOld(){ setOld(prop.getProperty("old"));   }

    public String getName() {  return name; }
    private void setName(String name) { this.name = name; }
    public void refreshName(){ setName(prop.getProperty("name"));   }

    public Address getAddress() { return address;}
    private void setAddress(Address address) { this.address = address;}

}



//========================


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




//=====================

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Property {

    String nameInterface();
    String defaultValue() default "-";

}
