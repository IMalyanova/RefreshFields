
public class Loader {

   public static void main(String[] args)  {

      RefreshFields refreshFields = RefreshFields.getInstance();
   }

}


//=======================



import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Properties;

public class RefreshFields {

    @Property(nameInterface = "old", defaultValue = "0")
    protected String old;

    @Property(nameInterface = "name", defaultValue = "Company")
    protected String name;

    @Property(nameInterface = "address")
    protected Address address;


    private static RefreshFields instance;
    protected Properties prop;


    private RefreshFields() {

        address = new Address();
        fieldsDefault();
        doRefresh();
    }

    public static synchronized RefreshFields getInstance() {
        if (instance == null)
            instance = new RefreshFields();
        return instance;
    }


    private void fieldsDefault(){

        Properties defaultProp = new Properties();
        defaultProp.setProperty("name", "Company");
        defaultProp.setProperty("old", "0");
        prop = new Properties(defaultProp);
    }


    public synchronized void doRefresh() {

//        address.refreshAddress();

        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream( "refresh.properties" );
            prop.load(fileInputStream);
//            refreshName();
//            refreshOld();

            try {
                this.serchAnatation(this, this.getClass());
                this.serchAnatation(address,address.getClass());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("in log ");
            e.printStackTrace();
        }finally {
            try {
                fileInputStream.close();
            } catch (Exception e) {
                System.out.println("in log ");
                e.printStackTrace();
            }
        }





    }



    void serchAnatation (Object obj, Class clasS) throws IllegalAccessException {

        Field fields[] = clasS.getDeclaredFields();

        for (Field field : fields){

            Annotation annotations[] = field.getDeclaredAnnotations();
            for (Annotation annotation : annotations){

                if (annotation.annotationType().equals(Property.class)){

                    switch(field.getName()) {
                        case "name" : refreshName();
                            break;
                        case "old" : refreshOld();
                            break;
                        case "address" :
                        case "home" :
                        case "street" : address.refreshAddress();
                            break;
                    }

                    System.out.println();
                    System.out.println("anat : " + field.getName());
                    System.out.println( field.get(obj));

                }
            }
        }
    }



    public String getOld() {return old;}
    protected void setOld(String old) { this.old = old;}
    public void refreshOld(){ setOld(prop.getProperty("old"));   }

    public String getName() {  return name; }
    protected void setName(String name) { this.name = name; }
    public void refreshName(){ setName(prop.getProperty("name"));   }

    public Address getAddress() { return address;}
    protected void setAddress(Address address) { this.address = address;}


}




//==================



import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class Address {

    @Property(nameInterface = "street", defaultValue = "street")
    protected String street;

    @Property(nameInterface = "home", defaultValue = "0")
    protected String home;

    protected JSONObject object;
    protected String strAddress;
    private String path = "address.json";
    Properties prop;


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

       FileInputStream fileInputStream = null;

       try {
           fileInputStream = new FileInputStream( "refresh.properties" );
           prop.load(fileInputStream);
           strAddress = prop.getProperty("address");
       } catch (Exception e) {
           System.out.println("in log ");
           e.printStackTrace();
       }finally {
           try {
               fileInputStream.close();
           } catch (Exception e) {
               System.out.println("in log ");
               e.printStackTrace();
           }
       }
       return strAddress;
   }


    private void updateVarInFile(){

        PrintWriter pw = null;
        try {
            pw = new PrintWriter(path);
            JSONObject object = new JSONObject();
            String array[] = getStrAddress().replaceAll("[^a-zA-ZА-Яа-яЁё\\d\\:\\,]","").split(",");
            for (String element : array) {
                String elements[] = element.split(":");
                object.put(elements[0],elements[1]);
            }
            pw.write(String.valueOf(object));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            pw.flush();
            pw.close();
        }
    }


    public synchronized void refreshAddress() {

        refreshStrAdr();
        updateVarInFile();

        try {
            JSONParser parser = new JSONParser();
            object = (JSONObject) parser.parse(new String(Files.readAllBytes(Paths.get(path))));

            setStreet((String) object.get("street"));
            setHome((String) object.get("home"));
            this.street = getStreet();
            this.home = getHome();
        } catch (Exception e) {
            System.out.println("in log");
            e.printStackTrace();
        }
    }


    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public void setHome(String home) { this.home = home;}
    public String getHome() { return home;}

    public JSONObject getObject() { return object; }
    public void setObject(JSONObject object) { this.object = object; }

    public String getStrAddress() { return strAddress;  }
    public void setStrAddress(String strAddress) { this.strAddress = strAddress;  }


}




//================


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
