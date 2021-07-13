package hippos;

import hippos.lang.stats.Form;
import hippos.utils.DateUtils;
import utils.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.StringTokenizer;

public class Coach extends Person {
    private Form horseForm = new Form("Coach");;

    public Coach(String coach) {
        super(coach);
    }

    public Coach(ResultSet raceSet) throws SQLException {
        super(raceSet.getString("VALMENTAJA"));
        try {
            horseForm.setStarts(raceSet.getBigDecimal("V_S"));
            horseForm.setFirsts(raceSet.getBigDecimal("V_1"));
            horseForm.setSeconds(raceSet.getBigDecimal("V_2"));
            horseForm.setThirds(raceSet.getBigDecimal("V_3"));
            horseForm.setAwards(raceSet.getBigDecimal("V_R"));
        } catch (Exception e) {
            e.printStackTrace();
            Log.write(e);
        }
    }

    public Form fetchForm(Connection conn, String horseName, Date date) {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            java.sql.Date sqlEndDate = DateUtils.toSQLDate(date);

            StringBuffer sb = new StringBuffer();
            sb.append("select count(*), sum(S_1), sum(S_2), sum(S_3), sum(palkinto), sum(KCODE), count(XCODE) ");
            sb.append("from SUBRESULT ");
            sb.append("where VALMENTAJA like ? and NIMI=? and pvm < ? ");
            sb.append("and KERROIN is not null");

            stmt = conn.prepareStatement(sb.toString());

            String name = super.initSQLSearchStr();

            stmt.setString(1, name);
            stmt.setString(2, horseName);
            stmt.setDate(3, sqlEndDate);

            rs = stmt.executeQuery();

            horseForm.init(rs);

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
        return horseForm;
    }

    Form getForm() {
        return horseForm;
    }
}
