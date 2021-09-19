package hippos.lang.stats;

import java.sql.Connection;
import java.util.Date;

public interface Fetchable {
    Form fetchForm(Connection conn, String horseName, Date date);
}
