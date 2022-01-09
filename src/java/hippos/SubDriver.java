package hippos;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SubDriver extends DriverForm {

    public SubDriver(String name) {
        super(name);
    }

    public SubDriver(String name, ResultSet raceSet) throws SQLException {
        super(name, raceSet);
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

            stmt = conn.prepareStatement("select count(*), sum(S_1), sum(S_2), sum(S_3), sum(palkinto), sum(KCODE), count(XCODE) from SUBRESULT where ohjelmakuljettaja = ? and pvm > ? and pvm < ?");

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
