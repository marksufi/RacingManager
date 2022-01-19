package hippos.io;


import java.io.IOException;
import java.sql.Connection;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Feb 26, 2005
 * AlphaNumber: 6:57:53 PM
 * To change this template use Options | File Templates.
 */

/**
 * An interface representing method factory
 */
public interface FileParser {
    public Object parse() throws Exception;
    public Object parse(Connection conn) throws Exception;
}
