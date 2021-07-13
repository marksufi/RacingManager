package hippos.io;

import java.util.Comparator;
import java.io.File;
import java.math.BigDecimal;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Mar 15, 2006
 * Time: 12:02:13 AM
 * To change this template use Options | File Templates.
 */
public class FileComparator implements Comparator {
    public int compare(Object a, Object b) {
        String filenameA = (String)a;
        String filenameB = (String)b;

        if(filenameA.indexOf("_CC") > 0 && filenameA.substring(filenameA.indexOf("_CC")).length() < 9) {
            // CF1325368800000_CEO_CC1.php
            filenameA = filenameA.replace("_CC", "_CC0");
        }

        if(filenameB.indexOf("_CC") > 0 && filenameB.substring(filenameB.indexOf("_CC")).length() < 9) {
            // CF1325368800000_CEO_CC1.php
            filenameB = filenameB.replace("_CC", "_CC0");
        }

        if(filenameA.indexOf("_") == filenameA.lastIndexOf("_")) {
            filenameA = filenameA.replace(".", "____.");
        }

        if(filenameB.indexOf("_") == filenameB.lastIndexOf("_")) {
            filenameB = filenameB.replace(".", "_CC20.");
        }
        return filenameA.compareTo(filenameB);
    }

    public static void main(String args[]) {
        FileComparator fc = new FileComparator();

        System.out.println("FileComparator.main: " + fc.compare("CF1325368800000_CEO_CC1.php", "CF1325368800000_CEO_CC10.php"));
        System.out.println("FileComparator.main: " + fc.compare("CF1325368800000_CEO_CC1.php", "CF1325368800000_CEO.php"));
        System.out.println("FileComparator.main: " + fc.compare("CF1325368800000_CEO.php", "CF1325368800000_CEO_CC1.php"));
        System.out.println("FileComparator.main: " + fc.compare("CF1326837600000_CEH.php", "CF1326837600000_CEO.php"));
        System.out.println("FileComparator.main: " + fc.compare("CF1326837600000_CEH_CC1.php", "CF1326837600000_CEO.php"));
        System.out.println("FileComparator.main: " + fc.compare("CF1326837600000_CEH.php", "CF1326837600000_CEO_CC1.php"));
        System.out.println("FileComparator.main: " + fc.compare("CF1326837600000_CEH_CC1.php", "CF1326837600000_CEO_CC1.php"));


    }
}
