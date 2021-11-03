package hippos.lang.stats;

import hippos.RaceProgramHorse;
import hippos.database.Database;
import hippos.database.Statements;
import hippos.utils.DateUtils;
import utils.Log;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class YearStatistics extends Statistics {

    private Date startDate;

    public YearStatistics(String label, RaceProgramHorse raceProgramHorse) {
        super(label, raceProgramHorse);
        this.startDate = DateUtils.rollYears(this.endDate, -1);
    }

    /*
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
    }*/

    protected PreparedStatement getStatement(Connection conn) {
        return Statements.getTimeFormStatement(conn, name, race, DateUtils.toSQLDate(startDate), DateUtils.toSQLDate(endDate));
    }


}
