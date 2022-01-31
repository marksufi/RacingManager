package hippos;

import hippos.utils.DateUtils;
import utils.Log;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SubDriver extends DriverForm {
    BigDecimal winRate = BigDecimal.ZERO;

    public SubDriver(String name) {
        super(name);
    }

    public SubDriver(String name, ResultSet raceSet) throws SQLException {
        super(name, raceSet);
    }

    public BigDecimal fetchWinRate(Connection conn, SubStart subStart) {
        PreparedStatement stmt  = null;
        ResultSet rs = null;

        BigDecimal winRate = BigDecimal.ZERO;

        try {
            java.sql.Date sqlEndDate = DateUtils.toSQLDate(subStart.date);

            stmt = conn.prepareStatement("select avg(S_1) from SUBRESULT where kuljettaja = ? and pvm < ? and KERROIN is not null");

            stmt.setString(1, subStart.getSubDriver().getName());
            stmt.setDate(2, sqlEndDate);
            rs = stmt.executeQuery();

            if(rs.next()) {
                winRate = rs.getBigDecimal(1);
            }

        } catch (Exception e) {
            Log.write(e);
            e.printStackTrace();
        } finally {
            try { stmt.close(); } catch (Exception e) { }
            try { rs.close(); } catch (Exception e) { }
        }

        return winRate != null ? winRate : BigDecimal.ZERO;
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

    public BigDecimal getWinRate() {
        return winRate;
    }

    public void setWinRate(BigDecimal winRate) {
        this.winRate = winRate;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(getName());
        sb.append(" (" + winRate + "%)");

        return sb.toString();
    }
}
