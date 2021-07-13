package hippos.lang.stats;

import hippos.exception.Missing;
import hippos.math.AlphaNumber;

import java.math.BigDecimal;
import java.sql.*;
import java.util.SortedSet;
import java.util.TreeSet;

public class TimeForm extends Form {
    private String raceMode;
    //private AlphaNumber recordTime;
    private SortedSet<AlphaNumber> recordTimes = new TreeSet<>();

    public TimeForm(String label) {
        super(label);
    }

    public TimeForm(String label, FormValidation formValidation) {
        super(label, formValidation);
    }

    public TimeForm(ResultSet set) throws SQLException {
        super(set);
        setLabel(set.getString("TYYPPI"));
        this.raceMode = set.getString("LAHTOTYYPPI");
        setRecordTime(set.getBigDecimal("AIKA"));
    }

    public void setRecordTime(BigDecimal recordTime) {
        if(recordTime != null) {
            recordTimes.add(new AlphaNumber(recordTime, this.raceMode));
        }
    }

    public static PreparedStatement getStatement(Connection conn, String name, String race, Date endDate) {
        return getStatement(conn, name, race, null, endDate);
    }

    public static PreparedStatement getStatement(Connection conn, String name, String race, Date startDate, Date endDate) {
        PreparedStatement statement = null;

        try {
            StringBuffer stmt = new StringBuffer();
            stmt.append("select count(*), sum(S_1), sum(S_2), sum(S_3), sum(palkinto), sum(KCODE), count(XCODE), min(AIKA) Aika, TYYPPI, LAHTOTYYPPI ");
            stmt.append("from SUBRESULT ");
            stmt.append("where NIMI=? and laji=? and PVM < ? ");
            if(startDate != null) {
                stmt.append("and PVM > ? ");
            }
            //stmt.append("and KERROIN is not null");
            stmt.append("group by TYYPPI, LAHTOTYYPPI");

            statement = conn.prepareStatement(stmt.toString());

            statement.setString(1, name);
            statement.setString(2, race);
            statement.setDate(3, endDate);

            if(startDate != null)
                statement.setDate(4, startDate);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return statement;
    }

    public void add(TimeForm form) {
        super.add(form);

        try { recordTimes.addAll(form.recordTimes);  } catch (NullPointerException e) {}
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(super.toString());

        while (sb.length() < 50)
            sb.append(" ");

        sb.append(recordTimes);

        return sb.toString();
    }

}
