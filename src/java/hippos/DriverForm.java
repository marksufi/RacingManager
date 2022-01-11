package hippos;

import hippos.lang.stats.Form;
import hippos.utils.DateUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Mar 3, 2006
 * Time: 8:09:01 PM
 * To change this template use Options | File Templates.
 */
public class DriverForm extends Person implements Comparable {

    protected String jockeyClass;
    //protected Form fullYearForm = new Form("1 year");
    protected Form raceTypeForm = new Form("Jockey");

    /**
     * Alustaa kuljettajan
     *
     * @param name
     *  R Tupamäki
     *  P J Korhonen
     *  Mario De la Cruz
     */
    public DriverForm(String name) {
        super(name);
    }

    public DriverForm(String name, ResultSet raceSet) throws SQLException {
        this(name);
        raceTypeForm.setStarts(raceSet.getBigDecimal("K_S"));
        raceTypeForm.setFirsts(raceSet.getBigDecimal("K_1"));
        raceTypeForm.setSeconds(raceSet.getBigDecimal("K_2"));
        raceTypeForm.setThirds(raceSet.getBigDecimal("K_3"));
        raceTypeForm.setAwards(raceSet.getBigDecimal("K_R"));
        raceTypeForm.setKcode(raceSet.getBigDecimal("K_PAALU"));
        raceTypeForm.setXcode(raceSet.getBigDecimal("K_X"));
    }

    public Form fetchRaceTypeForm(Connection conn, Date date, String raceType) {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            java.sql.Date sqlEndDate = DateUtils.toSQLDate(date);
            StringBuilder sb = new StringBuilder();

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
                e.printStackTrace();
            }
            try {
                rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return raceTypeForm;
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

    @Override
    public int compareTo(Object o) {

        try {
            DriverForm aDriver = (DriverForm) o;

            if(this.hashCode() == o.hashCode())
                return 0;

            if(raceTypeForm.firstRate().equals(aDriver.getForm().firstRate())) {
                // Voittoprosentti yhtä suuri, palauttaa sijoitusten vertailun

                if (raceTypeForm.sijaRate().compareTo(aDriver.raceTypeForm.sijaRate()) == 0) {
                    // Myös sijaprosentti yhtä suuri, kokeilee startteja

                    if (raceTypeForm.getStarts().compareTo(aDriver.raceTypeForm.getStarts()) == 0) {
                        // ja myös startteja yhtä paljon, palauttaa vain nimivertailun
                        return getName().compareTo(aDriver.getName());
                    }

                    return aDriver.raceTypeForm.getStarts().compareTo(raceTypeForm.getStarts());
                }

                aDriver.raceTypeForm.sijaRate().compareTo(raceTypeForm.sijaRate());
            }

            return aDriver.raceTypeForm.firstRate().compareTo(raceTypeForm.firstRate());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        try {
            //sb.append(getName());
            //sb.append("(");
            sb.append(raceTypeForm.firstRateProcents(2) + "%");
            //sb.append(")");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();

    }
}