package hippos.lang.stats;

import hippos.RaceProgramHorse;
import hippos.database.Database;
import hippos.utils.DateUtils;
import utils.Log;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class YearStatistics extends Form {
    private RaceProgramHorse raceProgramHorse;
    private TimeStatistics timeStatistics;
    private Date startDate;
    private Date endDate;
    private String name;
    private String race;

    public YearStatistics(String label, RaceProgramHorse raceProgramHorse) {
        super(label);
        this.raceProgramHorse = raceProgramHorse;
        this.endDate = raceProgramHorse.getRaceProgramStart().getDate();
        this.startDate = DateUtils.rollYears(this.endDate, -1);
        this.name = raceProgramHorse.getRaceHorseName();
        this.race= raceProgramHorse.getRaceMode().substring(0, 1);
        this.timeStatistics = new TimeStatistics(raceProgramHorse);

    }

    public YearStatistics(String label, ResultSet raceSet, RaceProgramHorse raceProgramHorse) {
        super(label);
        this.raceProgramHorse = raceProgramHorse;
        this.timeStatistics = new TimeStatistics(raceProgramHorse);

        try {
            this.endDate = raceProgramHorse.getRaceProgramStart().getDate();
            this.startDate = DateUtils.rollYears(this.endDate, -1);
            this.name = raceProgramHorse.getRaceHorseName();
            this.race= raceProgramHorse.getRaceMode().substring(0, 1);

            setStarts(raceSet.getBigDecimal("V_S"));
            setFirsts(raceSet.getBigDecimal("V_1"));
            setSeconds(raceSet.getBigDecimal("V_2"));
            setThirds(raceSet.getBigDecimal("V_3"));
            setAwards(raceSet.getBigDecimal("V_R"));
            //setKcode(raceSet.getBigDecimal("V_KCODE"));
            //setXcode(raceSet.getBigDecimal("V_XCODE"));
        } catch (SQLException e) {
            Log.write(e);
        }
    }

    public TimeStatistics getTimeStatistics() {
        return timeStatistics;
    }

    public void fetchForm(Connection conn) {
        PreparedStatement statement = null;
        ResultSet set = null;

        try {

            statement = super.getStatement(conn, name, race, DateUtils.toSQLDate(startDate), DateUtils.toSQLDate(endDate));

            set = statement.executeQuery();

            super.init(set);
        } catch (Exception e) {
            Log.write(e, "GeneralRaceProgramHorseDatabaseForm.select");
        } finally {
            try { set.close(); } catch (Exception e) { }
            try { statement.close();} catch (Exception e) { }
        }
    }
}
