package utils.html;

import utils.Log;

import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Jul 21, 2004
 * AlphaNumber: 10:33:53 AM
 * To change this template use Options | File Templates.
 */
public class Tag extends Properties {
    private String name;

    public String getName() { return name; }

    /**
     * Creates a tag with parameters from the line
     *
     * @param line f.e. a href='http://hippos.ip-finland.com/hippos/ohjelmatiedot/h20040721.php'style='text-decoration:none'
     */
    public Tag(String line) {
        try {
            StringTokenizer st = new StringTokenizer(line, " ");
            name = st.nextToken();
            String param = null, value = null;
            while(st.hasMoreTokens()) {
                String token = st.nextToken(" ='\"");
                if(!token.equals(" ")) {
                    if(param == null) {
                        param = token;
                        continue;
                    } else {
                        value = token;
                    }
                    super.setProperty(param, value);
                    param = null;
                    value = null;
                }
            }
        } catch(Exception e) {
            Log.write(e, line);
        }
    }

    public boolean isNewLine() {
        if(name.equals("tr")) return true;
        //if(name.equals("/tr")) return true;
        if(name.equals("p")) return true;
        if(name.equals("/p")) return true;
        if(name.equals("br")) return true;
        if(name.equals("hr")) return true;
        return false;
    }

    public boolean isTitle() {
        return name.equals("title");
    }

    public String toString() {
        StringBuffer str = new StringBuffer();
        str.append(name);
        str.append(" ");
        str.append(super.toString());

        return str.toString();
    }
}
