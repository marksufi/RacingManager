package hippos.lang.stats;

import hippos.Coach;
import hippos.utils.DateUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class FormCoach extends Coach implements Fetchable {
    private Form horseForm = new Form("Coach");

    public FormCoach(ResultSet raceSet) throws SQLException {
        super(raceSet.getString("VALMENTAJA"));
    }


    /**
     * Hakee valmentajan hevoskohtaiset tilastot hevosen historia-kannasta
     *
     * Ei käytössä
     *
     * @param conn      Tietokantayhteys
     * @param horseName Hevosen nimi
     * @param date      Ravien päivämäärä
     *
     * @return          Valmentajan tilastot
     */
    public Form fetchForm(Connection conn, String horseName, Date date) {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            java.sql.Date sqlEndDate = DateUtils.toSQLDate(date);

            StringBuilder sb = new StringBuilder();
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
