package hippos.lang.stats;

import hippos.*;
import hippos.math.*;
import hippos.math.racing.FinalTime;
import hippos.math.racing.QuarterTime;
import hippos.math.regression.RegressionModelException;
import hippos.util.Mapper;
import hippos.util.QuarterTimes;
import hippos.utils.DateUtils;
import hippos.utils.HorsesHelper;
import utils.Log;

import java.math.BigDecimal;
import java.sql.Connection;
//import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class TimeStatistics {
    private RaceProgramHorse raceProgramHorse;
    private Date raceDate;

    private SortedSet <SubTime> aRecordSet = new TreeSet();
    private SortedSet <SubTime> tRecordSet = new TreeSet();

    private SortedSet<SubTime> raceTimeSet = new TreeSet();
    private List<SubTime> raceTimeList = new ArrayList();

    private SortedSet <SubRank> raceRankingSet = new TreeSet();
    private List <SubRank> raceRankingList = new ArrayList();

    private SortedSet <SubValue> raceTimeValueSet = new TreeSet();
    private SortedSet <SubValue> raceRankValueSet = new TreeSet();

    private QuarterTimes firstQuarter;
    private QuarterTimes secondQuarter;
    private QuarterTimes lastQuarter;
    private QuarterTimes finalTimes;

    public TimeStatistics(RaceProgramHorse raceProgramHorse) {
        this.raceProgramHorse = raceProgramHorse;
        this.raceDate = raceProgramHorse.getRaceProgramStart().getDate();
        this.firstQuarter = new QuarterTimes("500m  ", raceProgramHorse, 500);
        this.secondQuarter = new QuarterTimes("1000m ", raceProgramHorse, 1000);
        this.lastQuarter = new QuarterTimes("V500  ", raceProgramHorse, 500);
        this.finalTimes = new QuarterTimes("final ", raceProgramHorse, raceProgramHorse.getRaceLength().intValue());
    }

    public void init() {
        aRecordSet = new TreeSet();
        tRecordSet = new TreeSet();

        raceTimeSet = new TreeSet();
        raceTimeList = new ArrayList();

        raceRankingSet = new TreeSet();
        raceRankingList = new ArrayList();

    }

    public SubTime getaRecord() {
        if (!aRecordSet.isEmpty()) {
            return (SubTime) aRecordSet.first();
        }
        //return new SubTime("    ");
        return null;
    }

    public SubTime gettRecord() {
        if (!tRecordSet.isEmpty()) {
            return (SubTime) tRecordSet.first();
        }
        //return new SubTime("    ");
        return null;
    }

    public SubTime getRecord() {
        if (!raceTimeSet.isEmpty()) {
            return (SubTime) raceTimeSet.first();
        }
        //return new SubTime("    ");
        return null;
    }

    /**
     * Lisää hevosen ajan setteihin ja listoihin.
     *
     * @param raceTime  Lisättävä aika
     */
    public void add(SubTime raceTime) {
        try {
            if (raceTime.getNumber() != null) {
                if (raceTime.getAlpha() != null && raceTime.getAlpha().contains("a")) {
                    aRecordSet.add(raceTime);
                } else {
                    tRecordSet.add(raceTime);
                }

                raceTimeSet.add(raceTime);
                raceTimeList.add(0, raceTime);
                raceTimeValueSet.add(raceTime.getSubValue());
            }
        } catch (Exception e) {
            Log.write(e);
        }
    }

    /**
     * Lisää hevosen sijoituksen settiin ja listaan
     *
     * @param raceRanking   Lisättävä sijoitus
     */
    public void add(SubRank raceRank) {
        try {
            if (raceRank.getBigDecimal() != null) {
                raceRankingSet.add(raceRank);
                raceRankingList.add(0, raceRank);
                raceRankValueSet.add(raceRank.getSubValue());
            }
        } catch (Exception e) {
            Log.write(e);
        }
    }

    public void fetchQuarterTimesStatistics(Connection conn) {
        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            StringBuffer sb = new StringBuffer();
            sb.append("select lahtotyyppi, tasoitus, min(VA_1), count(va_1), min(VA_2), count(VA_2), min(V500), count(V500), min(aika), count(*), avg(PALKINTO) ");
            sb.append("from RESULTHORSE ");
            sb.append("where NIMI=? and PVM<? ");
            sb.append("group by lahtotyyppi, tasoitus");

            statement = conn.prepareStatement(sb.toString());
            statement.setString(1, raceProgramHorse.getRaceHorseName());
            statement.setDate(2, raceProgramHorse.getRaceProgramStart().getSQLDate());
            set = statement.executeQuery();
            while(set.next()) {
                int i = 1;
                String raceType = set.getString(i++);
                BigDecimal tasoitus = set.getBigDecimal(i++);
                BigDecimal minVA_1 = set.getBigDecimal(i++);
                BigDecimal countVA_1 = set.getBigDecimal(i++);
                BigDecimal minVA_2 = set.getBigDecimal(i++);
                BigDecimal countVA_2 = set.getBigDecimal(i++);
                BigDecimal minV500 = set.getBigDecimal(i++);
                BigDecimal countV500 = set.getBigDecimal(i++);
                BigDecimal minAika = set.getBigDecimal(i++);
                BigDecimal count = set.getBigDecimal(i++);
                BigDecimal palkinto = set.getBigDecimal(i++);

                if(minVA_1 != null)
                    System.out.print("");

                tasoitus = raceProgramHorse.getTasoitus().subtract(tasoitus);
                QuarterTime firstQuarter = new QuarterTime(raceProgramHorse, minVA_1, raceType, tasoitus, countVA_1, count, palkinto);
                this.firstQuarter.add(firstQuarter);

                QuarterTime secondQuarter = new QuarterTime(raceProgramHorse, minVA_2, raceType, tasoitus, countVA_2, count, palkinto);
                this.secondQuarter.add(secondQuarter);

                QuarterTime lastQuarter = new QuarterTime(raceProgramHorse, minV500, raceType, BigDecimal.ZERO, countV500, count, palkinto);
                this.lastQuarter.add(lastQuarter);

                tasoitus = raceProgramHorse.getTasoitus();
                QuarterTime finalTime = new QuarterTime(raceProgramHorse, minAika, raceType, tasoitus, countV500, count, palkinto);
                if(minAika != null) {
                    System.out.print("");
                }
                finalTimes.add(finalTime);
            }
        } catch (Exception e) {
            Log.write(e);
        } finally {
            try { set.close(); } catch (Exception e) { }
            try { statement.close();} catch (Exception e) { }
        }

    }

    /**
     * @return Palauttaa ajolähtöjen ajat paremmuusjärjestyksessä
     */
    public SortedSet getaRecordSet() {
        return aRecordSet;
    }

    /**
     * @return Palauttaa tasoitusajojen ajat paremmuusjärjestyksessä
     */
    public SortedSet gettRecordSet() {
        return tRecordSet;
    }

    /**
     * @return  Palauttaa ajat paremmuusjärjestyksessä
     */
    public SortedSet <SubTime>getRaceTimeSet() {

        return raceTimeSet;
    }

    /**
     * @return  Palauttaa ajat tallennusaikajärjestyksessä
     */
    public List <SubTime> getRaceTimeList() {
        return raceTimeList;
    }

    /**
     * @return  Palauttaa sijoitukset paremmuusjärjestyksessä
     */
    public SortedSet <SubRank> getRaceRankingSet() {

        return raceRankingSet;
    }

    /**
     * @return  Palauttaa sijoitukset tallennusaikajärjestyksessä
     */
    public List <SubRank> getRaceRankingList() {

        return raceRankingList;
    }

    /**
     * Tulostaa kaksi parasta aikaa
     * @return
     *
    public String toString() {
        StringBuffer str = new StringBuffer();

        str.append(firstQuarter != null ? "500m :" + firstQuarter.toString() : "");
        str.append(secondQuarter != null ? "\t1000m:" + secondQuarter.toString() : "");
        str.append(lastQuarter != null ? "\tV500m:" + lastQuarter.toString() : "");
        str.append(finalTimes != null ? "\tfinal:" + finalTimes.toString() : "");
        //str.append(finalTime.toString());

        if (!raceTimeSet.isEmpty()) {
            Iterator itr = raceTimeSet.iterator();

            for (int i = 0; itr.hasNext() && i < 2; i++) {
                str.append((SubTime) itr.next() + "  ");
            }
        }

        return str.toString();
    }*/

    public SortedSet getRecordSet(int count) {
        Iterator si = raceTimeSet.iterator();
        SortedSet subset = new TreeSet();

        for(int i = 0; si.hasNext() && i < count; i++) {
            subset.add(si.next());
        };

        return subset;
    }



    public SortedSet<SubValue> getRaceTimeValueSet() {
        return raceTimeValueSet;
    }

    public SortedSet<SubValue> getRaceRankValueSet() {
        return raceRankValueSet;
    }

    public QuarterTimes getFirstQuarter() {
        return firstQuarter;
    }

    public QuarterTimes getSecondQuarter() {
        return secondQuarter;
    }

    public QuarterTimes getLastQuarter() {
        return lastQuarter;
    }

    public QuarterTimes getFinalTimes() {
        return finalTimes;
    }
}
