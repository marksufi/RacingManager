package utils;


import org.apache.commons.text.StringEscapeUtils;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Nov 6, 2003
 * AlphaNumber: 12:03:04 AM
 * To change this template use Options | File Templates.
 */
public class Scand {

    public static String parse(String line) {
        //System.out.println(StringEscapeUtils.unescapeHtml3(line));

        if (line != null && line.length() > 0 && (line.indexOf("Ã") >= 0 || line.indexOf("&") >= 0)) {

            if(line.contains("Ã¤") || line.contains("Ã„") || line.contains("Ã¶"))
                System.out.println(StringEscapeUtils.unescapeHtml4(line));

            line = line.replaceAll("&nbsp;", "");
            //line = line.replaceAll("[\\u00A0\\u2007\\u202F]+", " ");
            line = StringEscapeUtils.unescapeHtml4(line);

            /*
            line = line.replaceAll("Ã¤", "ä");
            line = line.replaceAll("Ã„", "Ä");
            line = line.replaceAll("Ã¶", "ö");
            line = line.replaceAll("Ã–", "Ö");
            line = line.replaceAll("Ã¥", "å");
            line = line.replaceAll("Ã…", "Å");
            line = line.replaceAll("Ã©", "é");
            line = line.replaceAll("Ã¼", "ü");
            */

            /*
            line = line.replaceAll("&nbsp;", "\t");

            if(line.indexOf("Ã") >= 0)
                Log.write("Unknown mark!: " + line);

            int i = -1;
            while ((i = line.indexOf("&auml;")) >= 0)
                line = line.substring(0, i) + "�" + line.substring(i + 6);

            while ((i = line.indexOf("&Auml;")) >= 0)
                line = line.substring(0, i) + "�" + line.substring(i + 6);

            while ((i = line.indexOf("&ouml;")) >= 0)
                line = line.substring(0, i) + "�" + line.substring(i + 6);

            while ((i = line.indexOf("&Ouml;")) >= 0)
                line = line.substring(0, i) + "�" + line.substring(i + 6);

            while ((i = line.indexOf("&nbsp;")) >= 0)
                line = line.substring(0, i) + "\t" + line.substring(i + 6);

            while ((i = line.indexOf("&aring;")) >= 0)
                line = line.substring(0, i) + "�" + line.substring(i + 7);

            while ((i = line.indexOf("&Aring;")) >= 0)
                line = line.substring(0, i) + "�" + line.substring(i + 7);

            while ((i = line.indexOf("&nbsp")) >= 0)
                line = line.substring(0, i) + "" + line.substring(i + 5);

            while ((i = line.indexOf("&amp;")) >= 0)
                line = line.substring(0, i) + "&" + line.substring(i + 5);

            while ((i = line.indexOf("&#x023C;")) >= 0)
                line = line.substring(0, i) + "C" + line.substring(i + 5);

            if ((i = line.indexOf("&")) >= 0 && i < line.indexOf(";")) {
                System.out.println(StringEscapeUtils.unescapeHtml3(line));
                //if(line.indexOf("&copy;")  < 0) {
                //    Log.write("Unknown scand: " + line);
                //}
            }*/

        }
        return line;
    }
}
