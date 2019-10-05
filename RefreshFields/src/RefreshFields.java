import org.apache.log4j.Logger;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Properties;

class RefreshFields {

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
