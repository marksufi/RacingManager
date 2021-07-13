package hippos.lang.stats;

import hippos.RaceProgramHorse;
import hippos.util.Mapper;
import hippos.utils.DateUtils;
import utils.Log;

import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FullStatistics extends TimeForm {
    private RaceProgramHorse raceProgramHorse;
    private TimeStatistics timeStatistics;
    private java.util.Date endDate;
    private String name;
    private String race;

    private Mapper<TimeForm> forms = new Mapper<>();

    public FullStatistics(String label, RaceProgramHorse raceProgramHorse) {
        super(label);
        this.raceProgramHorse = raceProgramHorse;
        this.endDate = new Date(raceProgramHorse.getRaceProgramStart().getDate().getTime());
        this.name = raceProgramHorse.getRaceHorseName();
        this.race= raceProgramHorse.getRaceMode().substring(0, 1);
        this.timeStatistics = new TimeStatistics(raceProgramHorse);
    }

    public FullStatistics(String label, ResultSet raceSet, RaceProgramHorse raceProgramHorse) {
        super(label);

        this.raceProgramHorse = raceProgramHorse;
        this.endDate = new Date(raceProgramHorse.getRaceProgramStart().getDate().getTime());
        this.name = raceProgramHorse.getRaceHorseName();
        this.race= raceProgramHorse.getRaceMode().substring(0, 1);
        this.timeStatistics = new TimeStatistics(raceProgramHorse);

        try {
            setStarts(raceSet.getBigDecimal("YHT_S"));
            setFirsts(raceSet.getBigDecimal("YHT_1"));
            setSeconds(raceSet.getBigDecimal("YHT_2"));
            setThirds(raceSet.getBigDecimal("YHT_3"));
            setAwards(raceSet.getBigDecimal("YHT_R"));
            setKcode(raceSet.getBigDecimal("KCODE"));
            setXcode(raceSet.getBigDecimal("XCODE"));

        } catch (SQLException e) {
            Log.write(e);
        }

    }

    public void fetchForm(Connection conn) {
        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = TimeForm.getStatement(conn, name, race, DateUtils.toSQLDate(endDate));

            set = statement.executeQuery();
            while(set.next()) {
                TimeForm form = new TimeForm(set);
                forms.getOrCreate(Collections.singletonList(form.getLabel()), new TimeForm(form.getLabel())).add(form);
                super.add(form);
            }

        } catch (Exception e) {
            Log.write(e);
        } finally {
            try { set.close(); } catch (Exception e) { }
            try { statement.close();} catch (Exception e) { }
        }
    }

    public String toString() {
        StringBuffer str = new StringBuffer();
        str.append(super.toString());
        //str.append(getLabel() + ": " + getStarts() + " " + getFirsts() + "-" + getSeconds() + "-" + getThirds());
        //str.append("->" + getKcode());
        //str.append("(" + getKcodeProcents(BigDecimal.ZERO) + "%)");
        //str.append("-" + getXcode() + "x");

        //str.append("\t" + getAwardRate() + "€/s");

        for(TimeForm timeForm : forms.getValues()) {
            str.append("\n\t" + timeForm);
        }

        return str.toString();
    }


    public TimeStatistics getTimeStatistics() {
        return timeStatistics;
    }

}


