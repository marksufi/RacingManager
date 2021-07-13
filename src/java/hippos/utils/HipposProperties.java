package hippos.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * This is Grazyhorse program specific Properties class
 * @author marktolo
 * @version 1.0
 */


public class HipposProperties extends Properties {
    private static HipposProperties instance;


    public static HipposProperties getInstance() {
        if(instance == null)
            instance = new HipposProperties();
        return instance;
    }

    private HipposProperties() {
        try {
            String propFile = "hippos.properties";
            load(new FileInputStream(propFile));
        } catch (FileNotFoundException fe) {
            System.out.println(fe.getMessage());
            fe.printStackTrace();
        } catch (IOException ie) {
            System.out.println(ie.getMessage());
            ie.printStackTrace();
        }
    }

    public static String get(String name) {
        return HipposProperties.getInstance().getProperty(name);
    }

    public static void main(String [] args) {
        System.out.println(HipposProperties.get("PROGRAM_FILE_PATH"));
    }
}
