package hippos;

import hippos.database.Database;
import hippos.lang.stats.Form;
import hippos.utils.DateUtils;
import utils.Log;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Mar 3, 2006
 * Time: 8:09:01 PM
 * To change this template use Options | File Templates.
 */
public class Driver extends Person {

    protected String jockeyClass;
    //protected Form fullYearForm = new Form("1 year");
    protected Form raceTypeForm = new Form("Jockey");

    /**
     * Alustaa kuljettajan
     *
     * @param value
     *  R Tupamäki
     *  P J Korhonen
     *  Mario De la Cruz
     */
    public Driver(String name) {
        super(name);
    }

    public Form fetchRaceTypeForm(Connection conn, Date date, String raceType) {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            java.sql.Date sqlEndDate = DateUtils.toSQLDate(date);
            StringBuffer sb = new StringBuffer();
            sb.append(raceTypeForm.getSelect() + " ");
            sb.append("from SUBRESULT ");
            sb.append("where kuljettaja like ? ");
            sb.append("and pvm < ? ");
            sb.append("and lahtotyyppi = ? ");
            sb.append("and KERROIN is not null");

            stmt = conn.prepareStatement(sb.toString());

            String sqlSearchName = initSQLSearchStr();

            stmt.setString(1, sqlSearchName);
            stmt.setDate(2, sqlEndDate);
            stmt.setString(3, raceType);
            rs = stmt.executeQuery();

            raceTypeForm.init(rs);

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
        return raceTypeForm;
    }


    public Driver(ResultSet raceSet) throws SQLException {
        this(raceSet.getString("KULJETTAJA"));
        raceTypeForm.setStarts(raceSet.getBigDecimal("K_S"));
        raceTypeForm.setFirsts(raceSet.getBigDecimal("K_1"));
        raceTypeForm.setSeconds(raceSet.getBigDecimal("K_2"));
        raceTypeForm.setThirds(raceSet.getBigDecimal("K_3"));
        raceTypeForm.setAwards(raceSet.getBigDecimal("K_R"));
        raceTypeForm.setKcode(raceSet.getBigDecimal("K_PAALU"));
        raceTypeForm.setXcode(raceSet.getBigDecimal("K_X"));
    }

    public String getJockeyClass() {
        return jockeyClass;
    }

    public void setJockeyClass(String jockeyClass) {
        this.jockeyClass = jockeyClass;
    }

    public Form getForm() {

        return raceTypeForm;
    }

    public void setForm(Form form) {

        this.raceTypeForm = form;
    }

    public static void main(String args []) {
        RaceProgramDriver driver = new RaceProgramDriver("Orlando");
        System.out.println(driver);
    }
}
