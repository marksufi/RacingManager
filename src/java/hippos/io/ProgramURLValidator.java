package hippos.io;

import java.net.URL;
import java.util.Calendar;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Mar 24, 2006
 * Time: 1:15:53 PM
 * To change this template use Options | File Templates.
 */
public class ProgramURLValidator implements Validator {
    private static final SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

    public boolean validate(Object o) {
        URL url = (URL)o;
        Calendar horsesDate = null;
        if((horsesDate = parseDate(new File(url.getFile()).getName())) != null) {
            Calendar systemDate = Calendar.getInstance();
            systemDate.add(Calendar.DATE, 1);
            return(horsesDate.before(systemDate));
        }
        return false;
    }

    private Calendar parseDate(String f) {
        try {
            int i = 0;
            if((i = f.indexOf('.')) > 9) {
                f = f.substring(2, i);
            } else if( i > 8) {
                f = f.substring(1, i);
            }
            df.parse(f);
            return df.getCalendar();
        } catch (ParseException e) {
            return null;
        }
    }
}
