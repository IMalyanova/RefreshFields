import java.io.*;
import java.util.Properties;

public class  RefreshFields {

    //    @Property
    private int old;
    //    @Property
    private String name;
    //    @Property
    private Address address;

    private static RefreshFields instance;
    public static void setInstance(RefreshFields instance) {
        RefreshFields.instance = instance;
    }

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

        Properties propName = getDefaultfProperties("name", "Company");
        Properties propOld = getDefaultfProperties("old", "0");
        Properties propA = new Properties();


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
        }
    }

    private Properties getDefaultfProperties(String key, String value) {
        Properties defaultProp = new Properties();
        defaultProp.setProperty(key, value);
        return new Properties(defaultProp);
    }


    public int getOld() {return old;}
    public void setOld(int old) {
        this.old = old;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() { return address;}
    public void setAddress(Address address) { this.address = address;}

}

//========================================================================================

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Address {

    private String street;
    private String home;

    public Address() {
        refreshAddress();
    }

    public void refreshAddress() {
        try {
//            HashMap<String,String> hashMap = new HashMap<>();
            JSONParser parser = new JSONParser();
            String path = "address.json";
            JSONObject object = (JSONObject) parser.parse(new String(Files.readAllBytes(Paths.get(path))));

            setStreet((String) object.get("street"));
            setHome((String) object.get("home"));
//            System.out.println(getHome());
//            System.out.println(getStreet());
            this.street = getStreet();
            this.home = getHome();
//            for (Object key : object.keySet()){
//                String value = String.valueOf(object.get(key));
//                System.out.println(value);
////                hashMap.put((String) key,value);
//            }
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
//    public void setStreet() {
//
//        Properties defoltPropStreet = new Properties();
//        defoltPropStreet.setProperty("street", "Lenina");
//        Properties propStreet = new Properties(defoltPropStreet);
//        FileInputStream fileInputStream = null;
//        try {
//            fileInputStream = new FileInputStream( "refresh.properties" );
//            propStreet.load(fileInputStream);
//            System.out.println(propStreet.getProperty("name"));
//        } catch (Exception e) {
//            System.out.println("in log");
//        }
//        street = String.valueOf(propStreet.getProperty("street"));//
//    }

//    public void setHome()  {
//        Properties defoltPropHome = new Properties();
//        defoltPropHome.setProperty("home", "0");
//        Properties propHome = new Properties(defoltPropHome);
//        Properties address = new Properties();//
//        try {
//            FileInputStream fileInputStream = new FileInputStream( "refresh.properties" );
//            address.load(fileInputStream);
//            home = String.valueOf(address.getProperty("home"));
//            System.out.println(address.getProperty("home"));
//        }catch (Exception e){
//            System.out.println("yyyyyf");
//        }////
//    }



}
