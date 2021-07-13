package hippos.math.betting;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Mar 29, 2006
 * Time: 2:05:26 AM
 * To change this template use Options | File Templates.
 */
public interface Betting {
    public void check(List ranking, List myRanking);
    public void update(Connection conn) throws SQLException;
    public void insert(Connection conn) throws SQLException;
}
