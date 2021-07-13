package hippos;

import hippos.lang.stats.Form;
import hippos.utils.DateUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class RaceResultDriver extends Driver {

    public RaceResultDriver(String value) {

        super(value);
    }

    public RaceResultDriver(ResultSet raceSet) throws SQLException {
        super(raceSet);
    }

    /*
    public Form fetchFullYearForm(Connection conn, Date date) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        //fullYearForm = new Form("1 year");
        try {
            Date startDate = DateUtils.rollYears(date, -1);
            java.sql.Date sqlStartDate = DateUtils.toSQLDate(startDate);
            java.sql.Date sqlEndDate = DateUtils.toSQLDate(date);

            stmt = conn.prepareStatement("select count(*), sum(S_1), sum(S_2), sum(S_3), sum(palkinto), sum(KCODE), count(XCODE) from SUBRESULT where ohjelmakuljettaja like ? and pvm > ? and pvm < ?");

            StringBuffer sb = new StringBuffer();
            sb.append(firstname1 + "%");
            if(firstname2 != null) {
                sb.append("-" + firstname2 + "%");
            }
            if(secondName != null) {
                sb.append(" " + secondName + "%");
            }

            sb.append(" " + lastname);
            String name = sb.toString();

            stmt.setString(1, name);
            stmt.setDate(2, sqlStartDate);
            stmt.setDate(3, sqlEndDate);
            rs = stmt.executeQuery();

            fullYearForm.init(rs);

            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                stmt.close();
            } catch (Exception e) {
            }
            try {
                rs.close();
            } catch (Exception e) {
            }
        }
        return fullYearForm;
    }*/

}
