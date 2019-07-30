import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class Loader {

   public static void main(String[] args)  {

       Loader loader = new Loader();
       RefreshFields refreshFields = RefreshFields.getInstance();
       Address addressInLoader = new Address();
       try {
           loader.serchAnatation(refreshFields, refreshFields.getClass());
           loader.serchAnatation(addressInLoader,addressInLoader.getClass());
       } catch (IllegalAccessException e) {
           e.printStackTrace();
       }
   }

    void serchAnatation (Object obj, Class clasS) throws IllegalAccessException {

        Field fields[] = clasS.getDeclaredFields();

        for (Field field : fields){

            Annotation annotations[] = field.getDeclaredAnnotations();
            for (Annotation annotation : annotations){

                if (annotation.annotationType().equals(Property.class)){
                    System.out.println("anat : " + field.get(obj));
                }
            }
        }
    }


}
//=========================================================================================================================

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Property {

    String nameInterface();


}

//=========================================================================================================================

import java.io.*;
import java.util.Properties;

public class RefreshFields {

    @Property(nameInterface = "old")
    protected String old;

    @Property(nameInterface = "name")
    protected String name;

    @Property(nameInterface = "address")
    protected Address address;

    private static RefreshFields instance;


    private RefreshFields() {
        address = new Address();
        this.address = address;
        doRefresh();
        this.old = old;
        this.name = name;
    }

    public static synchronized RefreshFields getInstance() {
        if (instance == null)
            instance = new RefreshFields();
        return instance;
    }


    public synchronized void doRefresh() {

        address.refreshAddress();
        System.out.println(address.getStreet());
        System.out.println(address.getHome());

        Properties defaultPropName = new Properties();
        defaultPropName.setProperty("name", "Company");
        Properties propName = new Properties(defaultPropName);

        Properties defaultPropOld = new Properties();
        defaultPropOld.setProperty("old", "0");
        Properties propOld = new Properties(defaultPropOld);

        Properties propA = new Properties();
        propA.setProperty("address", String.valueOf(address.getObject()));

        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream( "refresh.properties" );

            propName.load(fileInputStream);
            System.out.println("name " + propName.getProperty("name"));

            propOld.load(fileInputStream);
            System.out.println("old " + propOld.getProperty("old"));

            propA.load(fileInputStream);
            System.out.println("Address " + propA.getProperty("address"));

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


    public String getOld() {return old;}
    public void setOld(String old) { this.old = old;}

    public String getName() {  return name; }
    public void setName(String name) { this.name = name; }

    public Address getAddress() { return address;}
    public void setAddress(Address address) { this.address = address;}

    public static void setInstance(RefreshFields instance) {
        RefreshFields.instance = instance;
    }

}




//=========================================================================================================================

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Address {

    @Property(nameInterface = "street")
    protected String street;

    @Property(nameInterface = "home")
    protected String home;

    @Property(nameInterface = "object")
    protected JSONObject object;

    public Address() {
        refreshAddress();
    }

    public void refreshAddress() {
        try {
            JSONParser parser = new JSONParser();
            String path = "address.json";
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



    public String getStreet() {
        return street;
    }
    public void setStreet(String street) {
        this.street = street;
    }

    public void setHome(String home) {
        this.home = home;
    }
    public String getHome() {
        return home;
    }


    public JSONObject getObject() {
        return object;
    }

    public void setObject(JSONObject object) {
        this.object = object;
    }


}

