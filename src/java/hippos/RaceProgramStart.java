package hippos;

import hippos.database.Statements;
import hippos.exception.RegressionModelException;
import hippos.io.RaceProgramFile;
import hippos.lang.stats.SubRaceTime;
import hippos.lang.toto.TotoGameStats;
import hippos.math.AlphaNumber;
import hippos.util.DissendingComparator;
import hippos.util.HObservable;
import hippos.util.HOpserverMapper;
import hippos.util.Mapper;
import hippos.math.racing.QuarterTime;
import hippos.math.regression.HipposUpdatingRegression;
import hippos.math.regression.LogisticRegression;
import hippos.utils.HorsesHelper;
import org.apache.commons.math3.stat.regression.ModelSpecificationException;
import utils.Log;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.util.*;

import static hippos.HarnessApp.*;
//import static hippos.effects.EffectEngine2.REGSIZE;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Feb 2, 2006
 * Time: 9:10:05 PM
 * To change this template use Options | File Templates.
 */
public class RaceProgramStart extends RaceStart {
    private RaceProgramFile raceProgramFile;
    private RaceResultStart raceResultStart;
    private ArrayList<RaceProgramHorse> raceProgramHorses = new ArrayList<>();
    private ArrayList<ValueHorse> valueHorseArrayList = new ArrayList<>();
    private HashMap<String, ValueHorse> valueHorseHashMap = new HashMap<>();

    private Mapper<SortedSet<QuarterTime>> quarterMap = new Mapper<>();
    private HOpserverMapper<SortedSet<HObservable>> observerMap = new HOpserverMapper<>();

    public static TreeMap <String, HipposUpdatingRegression> featuredReg = new TreeMap<>();

    public RaceProgramStart(RaceProgramFile raceProgramFile) {
        this.raceProgramFile = raceProgramFile;

        String filename = raceProgramFile.getName();
        setFileId(filename);
        setStartNumber(filename);
        setDate(raceProgramFile.getDate());
    }

    private void setStartNumber(String filename) {
        filename = filename.substring(filename.lastIndexOf("_"));
        filename = filename.substring(0, filename.indexOf("."));

        AlphaNumber fileNumber = new AlphaNumber(filename);
        setStartNumber(fileNumber.getBigDecimal());
    }

    public RaceProgramStart(String lid, String raceMode, java.util.Date date) throws IOException {
        //this();
        this.id = lid;
        setRaceMode(raceMode);
        setDate(date);
        //this.raceResultStart = raceProgramFile.getRaceResultStart();

        raceProgramFile = (RaceProgramFile)raceProgramDirectory.createFile(lid);
    }

    /**
     *  Luo 'RaceProgramStart' olion hakemalla tiedot tietokannasta
     *
     * @param conn  Tietokantayhteys
     * @param lid   Lähdön Id
     * @param date  Lähdön päivämäärä
     * @param raceResultStart   Lähdön tulostiedosto
     */
    public RaceProgramStart(Connection conn, String lid, Date date, RaceResultStart raceResultStart) {
        this.id = lid;
        this.setDate(date);
        this.raceResultStart = raceResultStart;

        PreparedStatement raceStmt = null;
        ResultSet raceSet = null;

        try {
            raceStmt = getInitStatement(conn, lid);
            raceSet = raceStmt.executeQuery();

            if (raceSet.next()) {
                /*
                    Lekee lähdön tiedot kannasta
                 */
                try {
                    //FILENAME, NUMERO, LAHTOTAPA, ROTU, MATKA, LYHENNE
                    setFileId(raceSet.getString("FILENAME"));
                    setStartNumber(raceSet.getBigDecimal("NUMERO"));
                    setRaceStartMode(raceSet.getString("LAHTOTAPA"));
                    setHorseRace(raceSet.getString("ROTU"));
                    addRaceLength(raceSet.getBigDecimal("MATKA"));
                    setRaceMode(raceSet.getString("LYHENNE"));
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

            // Hakee lähdön hevoset kannasta
            initRaceProgramHorses(conn, lid);

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try { raceSet.close();} catch (SQLException e) { e.printStackTrace(); }
            try { raceStmt.close();} catch (SQLException e) { e.printStackTrace(); }
        }
    }

    /**
     * Lukee tietokannasta kaikki lähdölle 'lid' kuuluvat hevoset ja luo ne
     *
     * @param conn
     * @param lid
     */
    private void initRaceProgramHorses(Connection conn, String lid) {

        PreparedStatement raceStmt = null;
        ResultSet raceSet = null;

        try {
            raceStmt = Statements.getProgramHorsesStatement(conn, lid);
            raceSet = raceStmt.executeQuery();

            while (raceSet.next()) {
                RaceProgramHorse raceProgramHorse = new RaceProgramHorse(raceSet, conn, this);

                /* tämä hidastaa jonkin verran, hakee parhaat väliajat hevosen historiasta */
                //raceProgramHorse.setStatistics(conn);

                raceProgramHorses.add(raceProgramHorse);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Log.write(e);
        } finally {
            try { raceSet.close();} catch (SQLException e) { e.printStackTrace(); }
            try { raceStmt.close();} catch (SQLException e) { e.printStackTrace(); }
        }
    }


    public void initValueHorses(TreeMap<String, HipposUpdatingRegression> linregMap) {
        try {
            ArrayList<RaceProgramHorse> raceProgramHorses = getRaceProgramHorses();

            for(RaceProgramHorse raceProgramHorse : raceProgramHorses) {
                try {

                    ValueHorse valueHorse = new ValueHorse(raceProgramHorse);

                    add(valueHorse);

                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

            mapValueHorses();

            TreeSet valueHorseSet = getValueHorseSet();
            //System.out.println(getId());
            System.out.println(this.toString());
            System.out.println(valueHorseSet);
            System.out.println("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void add(LogisticRegression logReg, ValueHorse valueHorse) {
        if(raceResultStart != null) {
            String valueHorseName = valueHorse.getRaceProgramHorse().getRaceHorseName();
            RaceResultHorse raceResultHorse = (RaceResultHorse) raceResultStart.getRaceResultHorse(valueHorseName);
            if(raceResultHorse != null) {
                valueHorse.getRaceProgramHorse().setRaceResultHorse(raceResultHorse);
            } else return;
        }

        valueHorseHashMap.put(valueHorse.getRaceProgramHorse().getRaceHorseName(), valueHorse);
    }

    public List getSortedHorseNumbers() {
        //TreeSet order = sortedValueHorses();
        List numberList = new Vector();
        Iterator horseItr = getValueHorseSet().iterator();
        while(horseItr.hasNext()) {
            Horse horse = (Horse)horseItr.next();
            numberList.add(horse.getHorseProgNumber());
        }
        return numberList;
    }

    public List getValueHorseHashMap() {
        return new ArrayList(valueHorseHashMap.values());
    }


    public TreeSet getValueHorseSet() {
        return new TreeSet(valueHorseHashMap.values());
    }

    public List getSortedHorseList() {
        return new ArrayList(new TreeSet(valueHorseHashMap.values()));
    }

    public ValueHorse getValueHorse(String name) {
        return (ValueHorse) valueHorseHashMap.get(name);
    }

    public void insert(Connection conn) throws SQLException {
        PreparedStatement stmt = null;

        try {

           if(!existsInDatabase(conn)) {
                stmt = conn.prepareStatement("insert into LAHTO(ID, FILENAME, NUMERO, LAHTOTAPA, ROTU, MATKA, LYHENNE) values(?, ?, ?, ?, ?, ?, ?)");
                stmt.setString(1, id);
                stmt.setString(2, getFileId());
                stmt.setBigDecimal(3, getStartNumber());
                stmt.setString(4, getRaceStartMode());
                stmt.setString(5, getHorseRace());
                stmt.setBigDecimal(6, getRaceLength());
                stmt.setString(7, getRaceMode());
                stmt.executeUpdate();
            }

            if(countOfHorses(conn) < raceProgramHorses.size()) {
               insertHorses(conn);
            }

            conn.commit();
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            Log.write(e, getId());
        } finally {
            try {stmt.close(); } catch (Exception fe) {}
        }
    }

    public boolean existsInDatabase(Connection conn) {
        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = conn.prepareStatement("select id from LAHTO where id=?");
            statement.setString(1, id);
            set = statement.executeQuery();
            if(set.next()) {
                return true;
            }
        } catch (SQLException e) {
            Log.write(e);
        } finally {
            try { set.close(); } catch (Exception e) { }
            try { statement.close();} catch (Exception e) { }
        }
        return false;
    }

    public int countOfHorses(Connection conn) {
        PreparedStatement statement = null;
        ResultSet set = null;

        try {   //select count(ID) from PROGRAMHORSE where LID='CF1591909200000_CET_CC06';

            statement = conn.prepareStatement("select count(*) from PROGRAMHORSE where LID=?");
            statement.setString(1, id);
            set = statement.executeQuery();
            if(set.next()) {
                return set.getInt(1);
            }
        } catch (Exception e) {
            Log.write(e);
        } finally {
            try { set.close(); } catch (Exception e) { }
            try { statement.close();} catch (Exception e) { }
        }
        return 0;
    }


    private void insertHorses(Connection conn) {
        for(RaceProgramHorse raceProgramHorse : raceProgramHorses) {
            try {
                raceProgramHorse.insert(conn);

                conn.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                Log.write(e, raceProgramHorse.getRaceHorseName() + "(" + raceProgramHorse.getId() + ")");
            }
        }
    }


    public RaceProgramFile getRaceProgramFile() {
        return raceProgramFile;
    }

    public RaceResultStart getRaceResultStart() {
        return raceResultStart;
    }

    public void setRaceResultStart(RaceResultStart raceResultStart) {
        this.raceResultStart = raceResultStart;
    }

    public ArrayList<RaceProgramHorse> getRaceProgramHorses() {
        return raceProgramHorses;
    }

    public void add(ValueHorse newValueHorse) {
        RaceResultHorse raceResultHorse = null;

        if(raceResultStart != null) {
            raceResultHorse = raceResultStart.getRaceResultHorse(newValueHorse.getRaceProgramHorse().getRaceHorseName());
            newValueHorse.setRaceResultHorse(raceResultHorse);
        }

        try {
            newValueHorse.setRegValues();

        } catch (ModelSpecificationException me) {
            // Regressiolla liian vähän dataa
            //me.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
        if(raceResultHorse != null) {
            for(SubStart subStart : newValueHorse.getRaceProgramHorse().getSubStartList()) {
                subStart.getSubTime().addObservation(newValueHorse.getRaceProgramHorse(), newValueHorse.getRaceResultHorse());
                subStart.getSubRank().addObservation(newValueHorse.getRaceProgramHorse(), newValueHorse.getRaceResultHorse());
            }
        }*/

        valueHorseArrayList.add(newValueHorse);
    }

    public void mapValueHorses() {
        for(ValueHorse valueHorse : valueHorseArrayList) {

            valueHorseHashMap.put(valueHorse.getRaceProgramHorse().getRaceHorseName(), valueHorse);
        }
    }

    public void add(RaceProgramHorse raceProgramHorse) {
        raceProgramHorses.add(raceProgramHorse);
    }

    private PreparedStatement getInitStatement(Connection conn, String lid) throws SQLException {
        PreparedStatement statement = null;
        StringBuffer sb = new StringBuffer();

        sb.append("select ID, FILENAME, NUMERO, LAHTOTAPA, ROTU, MATKA, LYHENNE ");
        sb.append("from LAHTO where ID=?");
        //sb.append("order by ID");

        statement = conn.prepareStatement(sb.toString());
        statement.setString(1, lid);

        return statement;
    }

    private String getQuarterString(int qt) {
        try {
            SortedSet quarterTimeSet = quarterMap.get(Collections.singletonList(BigDecimal.valueOf(qt)));
            return quarterTimeSet.toString();
        } catch (Exception e) {
            return "";
        }
    }

    public void addQuarterTime(int qt, QuarterTime quarterTime) {
        List qtList = Collections.singletonList(BigDecimal.valueOf(qt));
        addQuarterTime(qtList, quarterTime);
        //quarterMap.getOrCreate(qtList, new TreeSet<QuarterTime>()).add(quarterTime);
    }

    /*
        Asettaa väliajalle lähtöratakohtaisen todennäköisyyden, ja
        lisää uuden yhdistetyn väliajan väliakapuuhun
     */
    public void addQuarterTime(List quarter, QuarterTime quarterTime) {

        if (!quarterTime.isEmpty()) {
            quarterMap.getOrCreate(quarter, new TreeSet<QuarterTime>()).add(quarterTime);
        }
    }

    public SortedSet getQuarterTimeSet(int qt) {
        return quarterMap.get(Collections.singletonList(BigDecimal.valueOf(qt)));
    }

    public void addObservations() {

        for(RaceProgramHorse raceProgramHorse : raceProgramHorses) {
            try {
                raceProgramHorse.addObservations();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for(Object key : observerMap.getKeys()) {
            try {
                HObservable hobservable = observerMap.get(key).first();

                RaceResultHorse raceResultHorse = hobservable.getRaceProgramHorse().getRaceResultHorse();

                BigDecimal ranking = raceResultHorse.getRaceResultRanking().getNumber();
                BigDecimal winOdds = raceResultHorse.getRaceResultWinOdds();

                if(ranking == null || ranking.intValue() != 1)
                    winOdds = BigDecimal.ZERO;

                observerMap.add(key, winOdds);

            } catch (NullPointerException e) {
                // sijoitus puuttuu

            } catch (Exception e) {
                Log.write(e);
                e.printStackTrace();
            }

        }


    }

    public void getObservations() {
        for(RaceProgramHorse raceProgramHorse : raceProgramHorses) {
            try {
                Iterator itr = raceProgramHorse.getSubStartSet().iterator();
                for (int i = 0; itr.hasNext(); i++) {
                    try {
                        SubStart subStart = (SubStart) itr.next();

                        SubRaceTime subRaceTime = new SubRaceTime(subStart, raceProgramHorse);

                        StringBuilder key = new StringBuilder();
                        key.append("S");
                        key.append(i);

                        HObservable hobservable = new HObservable(subRaceTime,  null, raceProgramHorse);

                        observerMap.getOrCreate(key.toString(), new TreeSet<>()).add(hobservable);

                    } catch (RegressionModelException e) {
                        //aika puuttuu
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                String key = "DW";

                HObservable hobservable1 = new HObservable(
                        raceProgramHorse.getRaceProgramDriver().getDriverForm(),
                        raceProgramHorse.getRaceProgramDriver().getName(),
                        raceProgramHorse);

                observerMap.getOrCreate(key, new TreeSet<>()).add(hobservable1);

                /*
                    Voittoprosentti
                 */
                key = "WP";
                HObservable hobservable2 = new HObservable(
                        HorsesHelper.toProcents(raceProgramHorse.getYearStatistics().getForm().firstRate()),
                        raceProgramHorse.getYearStatistics().getForm().toTinyString(),
                        raceProgramHorse,
                        new DissendingComparator());

                observerMap.getOrCreate(key, new TreeSet<>()).add(hobservable2);

                /*
                    Sijoitusprosentti
                 */
                key = "SP";
                HObservable hobservable3 = new HObservable(
                        HorsesHelper.toProcents(raceProgramHorse.getYearStatistics().getForm().sijaRate()),
                        raceProgramHorse.getYearStatistics().getForm().toTinyString(),
                        raceProgramHorse,
                        new DissendingComparator());

                observerMap.getOrCreate(key, new TreeSet<>()).add(hobservable3);

                /*
                    1. Väliaika prosentteina
                 */
                try {
                    BigDecimal prop1 = raceProgramHorse.getTimeStatistics().getFirstQuarter().getPropbabilty();
                    if(prop1.compareTo(BigDecimal.ZERO) > 0) {

                        observerMap.getOrCreate("V1%", new TreeSet<>()).add(
                                new HObservable(
                                    raceProgramHorse.getTimeStatistics().getFirstQuarter().getPropabiltyProcents(),
                                    raceProgramHorse.getTimeStatistics().getFirstQuarter().getValueSet().first(),
                                    raceProgramHorse,
                                    new DissendingComparator()));
                    }
                } catch (Exception e) {
                    Log.write(e);
                    e.printStackTrace();
                }

                                /*
                    1. Väliaika aikana
                 */

                try {
                    if(!raceProgramHorse.getTimeStatistics().getFirstQuarter().getValueSet().isEmpty()) {

                        observerMap.getOrCreate("1T", new TreeSet<>()).add(
                                new HObservable(
                                    raceProgramHorse.getTimeStatistics().getFirstQuarter().getValueSet().first(),
                                    raceProgramHorse.getTimeStatistics().getFirstQuarter().getPropabiltyProcents(),
                                    raceProgramHorse));
                    }
                } catch (Exception e) {
                    Log.write(e);
                    e.printStackTrace();
                }

                 /*
                    2. Väliaika prosentteina
                 */
                try {
                    BigDecimal prop2 = raceProgramHorse.getTimeStatistics().getSecondQuarter().getPropbabilty();
                    if(prop2.compareTo(BigDecimal.ZERO) > 0) {
                        observerMap.getOrCreate("V2%", new TreeSet<>()).add(
                                new HObservable(
                                        raceProgramHorse.getTimeStatistics().getSecondQuarter().getPropabiltyProcents(),
                                        raceProgramHorse.getTimeStatistics().getSecondQuarter().getValueSet().first(),
                                        raceProgramHorse,
                                        new DissendingComparator()));
                    }
                } catch (Exception e) {
                    Log.write(e);
                    e.printStackTrace();
                }

                                /*
                    2. Väliaika aikana
                 */

                try {
                    if(!raceProgramHorse.getTimeStatistics().getSecondQuarter().getValueSet().isEmpty()) {

                        observerMap.getOrCreate("2T", new TreeSet<>()).add(
                                new HObservable(
                                        raceProgramHorse.getTimeStatistics().getSecondQuarter().getValueSet().first(),
                                        raceProgramHorse.getTimeStatistics().getSecondQuarter().getPropabiltyProcents(),
                                        raceProgramHorse));
                    }
                } catch (Exception e) {
                    Log.write(e);
                    e.printStackTrace();
                }


            } catch (Exception e) {
                Log.write(e);
                e.printStackTrace();
            }
        }
    }

    public String toString() {
        StringBuffer str = new StringBuffer();
        str.append(getId());
        str.append("\n" + getStartNumber() + ". Lähtö ");
        str.append(getRaceStartMode() + " " + getHorseRace() + " " + getRaceLength() + "m " + getDate());

        str.append("\n 500m: ");
        str.append(getQuarterString(1));
        str.append("\n 1000m: ");
        str.append(getQuarterString(2));
        str.append("\n V500m: ");
        str.append(getQuarterString(3));
        str.append("\n final: ");
        str.append(getQuarterString(4));

        str.append("\n\nObservables:\n");
        str.append(observerMap.toString());

        str.append("\n\n");

        return str.toString();
    }
}
