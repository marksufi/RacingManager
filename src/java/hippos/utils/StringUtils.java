package hippos.utils;

import hippos.HarnessApp;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Nov 2, 2005
 * AlphaNumber: 10:42:02 PM
 * To change this template use Options | File Templates.
 */
public class StringUtils {
    public static final int ALIGN_RIGHT = 1;
    public static final int ALIGN_LEFT = 2;

    private String str;

    public static String toColumn(String str, int capacity) {
        StringBuffer sb = new StringBuffer();
        sb.append(str != null ? str : "");

        while (sb.length() < capacity)
            sb.append(" ");

        /*
        for(int i = 0, len = str != null ? str.length() : 0; i < capacity; i++) {
            sb.append( i < len ? str.charAt(i) : " " );
        }*/

        return sb.toString().substring(0, capacity);

    }


    public static String toColumn(Object object, int capacity) {
        return toColumn(object != null ? object.toString() : null, capacity);
    }

    /*
    public static String toColumn(BigDecimal bigDecimal, int capacity) {
        return toColumn(bigDecimal != null ? bigDecimal.toString() : null, capacity);
    }
    */


    public static String parse(String str, char c, int length, int align) {
        if(str == null)
            str = "";
        if(str.length() >= length) {
            str = str.substring(length - 1);
        }
        StringBuffer sb = new StringBuffer();
        switch(align) {
            case StringUtils.ALIGN_LEFT : sb.append(str);
                                     fill(sb, c, length);
                                     break;
            case StringUtils.ALIGN_RIGHT :fill(sb, c, length - str.length());
                                     sb.append(str);
                                     break;
        }
        return sb.toString();
    }

    private static void fill(StringBuffer sb, char c, int length) {
        while(sb.length() < length) sb.append(c);
    }

    public String toString() {
        return str;
    }


    /**
     * Palauttaa true jos merkkijono name sisältää kaikki merkkijono anotherName merkit
     *
     * @param name
     * @param anotherName
     * @return
     */
    public static boolean contains(String name, String anotherName) {
        String [] splitted1 = name.split("[ -]");
        String [] splitted2 = anotherName.split("[ -]");

        if(splitted1.length != splitted2.length)
            return false;

        for(int i = 0; i < splitted1.length; i++) {
            if(splitted1[i].indexOf(splitted2[i]) != 0)
                return false;
        }
        return true;
    }

    public static void main(String args[]) {
        String k1 = new String("Mika Niemistö");
        String k2 = new String("M Niemistö");

        System.out.println(StringUtils.contains(k1, k2));

        System.out.println(StringUtils.toColumn("Markku Nieminen", 8));

        k1 = "Veli-Erkki Paavola";
        k2 = "V-E Paavola";

        System.out.println(StringUtils.contains(k1, k2));
    }

    public static String getRaceMode(String raceMode, BigDecimal raceRanking, String rankingString) {
        StringBuffer sb = new StringBuffer();

        if(HarnessApp.useKp == false) {
            raceMode.replaceAll("kp", "ke");
        }

        sb.append(raceMode);

        if(raceRanking == null && rankingString != null && rankingString.indexOf('h') < 0) {
            if(!HarnessApp.useNr) {
                rankingString = rankingString.replaceAll("nr", "kl");
            }
            sb.append(rankingString);
        }

        return sb.toString();
    }

}
