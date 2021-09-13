package hippos;

import hippos.database.Database;
import hippos.database.Statements;
import hippos.lang.stats.*;
import hippos.util.Mapper;
import hippos.math.Progress;
import hippos.math.racing.QuarterTime;
import hippos.util.QuarterTimes;
import hippos.utils.DateUtils;
import hippos.utils.StringUtils;
import utils.Log;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static hippos.math.Progress.getWeeksKey;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Feb 2, 2006
 * Time: 9:55:03 PM
 * To change this template use Options | File Templates.
 */
public class RaceProgramHorse extends Horse {
    public static Mapper<Progress> progressMap = new Mapper<>();
    private FullStatistics fullStatistics;

    protected RaceProgramStart raceProgramStart;
    protected RaceResultHorse raceResultHorse;


    BigDecimal age;
    String color = "";
    String sex = "";
    String sire;
    String dam;
    String sireOfDam;
    String color1;
    String color2;
    String owner;
    String ownerLocation;
    private RaceProgramDriver raceProgramDriver;
    private Coach coach;
    private String registerNumber;

    //protected List <SubStart> subStarts = new Vector();
    private List<SubStart> subStartList = new ArrayList();
    private SortedSet <SubStart> subStartSet = new TreeSet();

    public static BigDecimal defaultLength;
    int gapDays = -1;

    private BigDecimal trackFirstQuarterPropability;
    private BigDecimal trackSecondQuarterPropability;
    private QuarterTime quarterTime2;

    public RaceProgramHorse(RaceProgramStart raceProgramStart) {
        super(raceProgramStart);
        this.raceProgramStart = raceProgramStart;
    }


    /**
     * Luo käsiohjelman hevosen hakemalla tiedot, tilastot ja edelliset lähdöt tietokannasta
     *
     * @param raceSet
     * @param conn
     * @param raceProgramStart
     * @throws SQLException
     */
    public RaceProgramHorse(ResultSet raceSet, Connection conn, RaceProgramStart raceProgramStart) throws SQLException {
        super(raceProgramStart);

        try {
            setRaceProgramStart(raceProgramStart);

            setRaceMode(raceProgramStart.getRaceMode());
            //setRaceDate(raceProgramStart.getDate());

            //setId(raceSet.getString("ID"));
            setTrackId(raceSet.getString("TRACKID"));
            setHorseProgNumber(raceSet.getBigDecimal("NUMERO"));
            setRaceLength(raceSet.getBigDecimal("MATKA"));
            setRaceTrack(raceSet.getBigDecimal("RATA"));
            setTasoitus(raceSet.getBigDecimal("TASOITUS"));
            setName(raceSet.getString("NIMI"));
            setRaceProgramDriver(new RaceProgramDriver(raceSet));
            setCoach(new Coach(raceSet));

            setRaceResultHorse(raceProgramStart.getRaceResultStart());

            //fullStatistics = new FullStatistics("Yht", raceSet, this);

            //fullStatistics = new FullStatistics("Y", this);
            //fullStatistics.fetchForm(conn);

            initSubStart(raceSet.getString("SUBSTART_1"));
            initSubStart(raceSet.getString("SUBSTART_2"));
            initSubStart(raceSet.getString("SUBSTART_3"));
            initSubStart(raceSet.getString("SUBSTART_4"));
            initSubStart(raceSet.getString("SUBSTART_5"));
            initSubStart(raceSet.getString("SUBSTART_6"));
            initSubStart(raceSet.getString("SUBSTART_7"));
            initSubStart(raceSet.getString("SUBSTART_8"));

            //fullStatistics = new FullStatistics("Y", this);

            //fullStatistics.init(raceSet.getString("Y_STATS"));
            //fullStatistics.init("R", raceSet.getString("R_STATS"));
            //fullStatistics.init("T", raceSet.getString("T_STATS"));

            addWeeksKey();

            fetchQuarterTimes(conn);

            fetchRaceModeStats();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchQuarterTimes(Connection conn) {
        try {
            /*
                Asettaa radoille tilastolliset paalupaikkojen prosentit
             */
            List trackIdL = Collections.singletonList(getTrackId());
            setTrackFirstQuarterPropability(trackIdL);
            setTrackSecondQuarterPropability(trackIdL);

            fullStatistics.getTimeStatistics().fetchQuarterTimesStatistics(conn);

            try {
                QuarterTime quarterTime = fullStatistics.getTimeStatistics().getFirstQuarter().getQuarterTime();
                quarterTime.addTrackPropability(getTrackFirstQuarterPropability());

                raceProgramStart.addQuarterTime(1, quarterTime);
            } catch (NoSuchElementException e) {
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                QuarterTime quarterTime = fullStatistics.getTimeStatistics().getSecondQuarter().getQuarterTime();
                quarterTime.addTrackPropability(getTrackSecondQuarterPropability());

                raceProgramStart.addQuarterTime(2, quarterTime);
                setQuarterTime2(quarterTime);

            } catch (NoSuchElementException e) {
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                QuarterTime quarterTime = fullStatistics.getTimeStatistics().getLastQuarter().getQuarterTime();
                raceProgramStart.addQuarterTime(3, quarterTime);

            } catch (NoSuchElementException e) {
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                QuarterTime quarterTime = fullStatistics.getTimeStatistics().getFinalTimes().getQuarterTime();
                raceProgramStart.addQuarterTime(4, quarterTime);

            } catch (NoSuchElementException e) {
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (NullPointerException e) {
            // rataa ei vielä löydy
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void addWeeksKey() {
        try {
            if (!subStartList.isEmpty()) {
                SubStart theLastSubStart = subStartList.get(subStartList.size() - 1);

                BigDecimal newWeeksKey = Progress.getWeeksKey(theLastSubStart.getDateDiff().doubleValue());
                for (SubStart subStart : subStartList) {
                    try {
                        if (!subStart.equals(theLastSubStart)) {
                            SubRaceTime.addObservation(subStart, theLastSubStart);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    subStart.addWeeksKey(newWeeksKey);
                }
            } else {
                Log.write("No substarts were fetched - " + getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setRaceResultHorse(RaceResultStart raceResultStart) {
        if(raceResultStart != null) {
            raceResultHorse = raceResultStart.getRaceResultHorse(getRaceHorseName());
        }
    }

    public void initSubStart(String subStartStr) {
        try {
            if (subStartStr != null && !subStartStr.isEmpty()) {
                SubStart newSubStart = new SubStart(subStartStr, this);

                addObservations(newSubStart);
                add(newSubStart);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void addObservations(SubStart newSubStart) {
        try {
            if(subStartList.size() > 0) {
                SubStart theLastSubStart = subStartList.get(subStartList.size() - 1);
                BigDecimal newWeeksKey = Progress.getWeeksKey(theLastSubStart.getSQLDate(), newSubStart.getSQLDate());

                for (SubStart subStart : subStartList) {
                    try {
                        //SubRaceTime.addObservation(subStart, newSubStart);
                    } catch (Exception e) {
                        //
                    }

                    subStart.addWeeksKey(newWeeksKey);
                    progressMap.getOrCreate(subStart.getWeeksKeyList(), new Progress()).add(subStart.getAward(), newSubStart.getAward());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void add(SubStart subStart) {
        //if(isKoeLähtö(subStart))
        //    init();

        //subStartList.add(0, subStart);
        subStartList.add(subStart);
        subStartSet.add(subStart);

        //fullStatistics.getTimeStatistics().add(subStart.getSubTime());
        //fullStatistics.getTimeStatistics().add(subStart.getSubRank());
    }

    public void init() {
        subStartList = new ArrayList();
        subStartSet = new TreeSet();

        fullStatistics.getTimeStatistics().init();
    }

    private void fetchSubStarts(Connection conn, int count) {
        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = Statements.getSubStartsStatement(conn, this, count);

            set = statement.executeQuery();

            while(set.next()) {
                try {
                    SubStart subStart = new SubStart(set, this);
                    subStart.getSubDriver().fetchRaceTypeForm(conn, subStart.getDate(), subStart.getRaceMode().toString());
                    add(subStart);
                } catch(Exception se) {
                    Log.write(se);
                }
            }

            Collections.reverse(subStartList);
        } catch (Exception e) {
            Log.write(e, "Error in RaceProgramHorse.fetchSubStarts");
        } finally {
            try { set.close(); } catch (Exception e) { }
            try { statement.close();} catch (Exception e) { }
        }

    }

    private boolean isKoeLähtö(SubStart subStart) {
        try {
            return subStart.getSubTime().getAlpha().contains("kl");
        } catch (NullPointerException e) {
        } catch (Exception e) {Log.write(e);};

        return false;
    }

    public String getRegisterNumber() {
        return registerNumber;
    }

    public void setRegisterNumber(String registerNumber) {
        this.registerNumber = registerNumber;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setOwnerLocation(String ownerLocation) {
        this.ownerLocation = ownerLocation;
    }


    public int getGapDays() {
        return gapDays;
    }

    public FullStatistics getFullStatistics() {
        return fullStatistics;
    }

    public RaceProgramStart getRaceProgramStart() {
        return raceProgramStart;
    }

    public void setRaceProgramStart(RaceProgramStart raceProgramStart) {
        this.raceProgramStart = raceProgramStart;
    }

    public List<SubStart> getSubStartList() {
        return subStartList;
    }

    public SortedSet<SubStart> getSubStartSet() {
        return subStartSet;
    }

    public void insert(Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        try {
            //if(getRaceHorseName().equals("Muiston Viljo"))
            //    System.out.print("");

            //if(!existsInDatabase(conn)) {
                int i = 1;
                stmt = Statements.getInsertRaceProgramHorseStamement(conn);

                //stmt.setString( i++, getId());
                stmt.setString( i++, getLid());
                stmt.setString( i++, getTrackId());
                stmt.setBigDecimal( i++, getHorseProgNumber());
                stmt.setString( i++, getRaceHorseName().toString());
                stmt.setString( i++, getRaceProgramDriver().toString());
                stmt.setString( i++, coach.toString());
                stmt.setBigDecimal( i++, getRaceLength());
                stmt.setBigDecimal( i++, getRaceTrack());
                stmt.setBigDecimal( i++, getTasoitus());

                /*
                stmt.setBigDecimal( i++, fullStatistics.getStarts());
                stmt.setBigDecimal( i++, fullStatistics.getXcode());
                stmt.setBigDecimal( i++, fullStatistics.getKcode());
                stmt.setBigDecimal( i++, fullStatistics.getFirsts());
                stmt.setBigDecimal( i++, fullStatistics.getSeconds());
                stmt.setBigDecimal( i++, fullStatistics.getThirds());
                stmt.setBigDecimal( i++, fullStatistics.getAwards());
                */

                Form coachStats = coach.fetchForm(conn, getRaceHorseName(), getRaceDate());
                stmt.setBigDecimal( i++, coachStats.getStarts());
                stmt.setBigDecimal( i++, coachStats.getFirsts());
                stmt.setBigDecimal( i++, coachStats.getSeconds());
                stmt.setBigDecimal( i++, coachStats.getThirds());
                stmt.setBigDecimal( i++, coachStats.getAwards());

                //Form driverStats = getRaceProgramDriver().getFullYearForm();
                Form driverStats = getRaceProgramDriver().getForm();
                stmt.setBigDecimal( i++, driverStats.getStarts());
                stmt.setBigDecimal( i++, driverStats.getFirsts());
                stmt.setBigDecimal( i++, driverStats.getSeconds());
                stmt.setBigDecimal( i++, driverStats.getThirds());
                stmt.setBigDecimal( i++, driverStats.getAwards());
                stmt.setBigDecimal( i++, driverStats.getKcode());
                stmt.setBigDecimal( i++, driverStats.getXcode());

                for(int is = 0; is < 8; is++) {
                    StringBuffer sb = new StringBuffer();

                    if(is < subStartList.size()) {
                        try {
                            SubStart subStart = (SubStart) subStartList.get(is);

                            if (subStart.getSubTime() != null && subStart.getSubTime().getAlpha() != null && subStart.getSubTime().getAlpha().contains("x")) {
                                System.out.println("RaceProgramHorse.insert: +  tietokantaan ei saa yhdistää aikaa ja xcodea");
                            }
                            sb.append(subStart.getTrackId() + ";");
                            sb.append(subStart.getWeather() + ";");

                            sb.append(subStart.getSubTime());

                            sb.append(";");
                            sb.append(subStart.getSubRank());

                            sb.append(";");
                            sb.append(subStart.getxCode());

                            sb.append(";");
                            int dateDiff = DateUtils.getDayDiff(this.getRaceDate(), subStart.getDate());
                            sb.append(dateDiff);

                            sb.append(";");
                            sb.append(subStart.getAward());

                            sb.append(";");
                            sb.append(subStart.getkCode());

                            sb.append(";");
                            BigDecimal subDriverFirstRate = subStart.getSubDriver().getForm().firstRate();
                            sb.append(subDriverFirstRate);

                        } catch (Exception e) {
                            e.printStackTrace();
                            sb = new StringBuffer();
                        }
                    }
                    stmt.setString( i++, sb.toString());
                }

                stmt.executeUpdate();
                stmt.close();

            //}
        } catch (Exception e) {
            Log.write(e, this.getId());
        } finally {
            try{ stmt.close(); } catch (Exception e) {}
        }
    }

    public boolean existsInDatabase(Connection conn) {
        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = conn.prepareStatement("select id from PROGRAMHORSE where id=?");
            statement.setString(1, getId());
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


    /**
     * Tämä vie aikaa
     */
    public void setStatistics() {
        try {

            //if(getRaceHorseName().equals("Saran Vappu"))
            //    System.out.println("RaceProgramHorse.setStatistics: Saran Vappu");

            Connection conn = Database.getConnection();

            fullStatistics = new FullStatistics("Yht", this);
            fullStatistics.fetchSubForms(conn);

            if(raceResultHorse != null) {
                Driver raceResultdriver = raceResultHorse.getRaceResultDriver();
                raceResultdriver.fetchRaceTypeForm(conn, getRaceDate(), getRaceMode());

                raceProgramDriver.setName(raceResultdriver.getName());
                raceProgramDriver.setForm(raceResultdriver.getForm());

            } else {
                raceProgramDriver.fetchRaceTypeForm(conn, this.getRaceDate(), this.getRaceMode());
            }

            coach.fetchForm(conn, this.getRaceHorseName(), this.getRaceDate());

            fetchSubStarts(conn, 8);

        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Tämä vie aikaa
     */
    public void fetchRaceModeStats() {
        try {

            Connection conn = Database.getConnection();

            fullStatistics = new FullStatistics("Yht", this);
            fullStatistics.fetchSubForms(conn);

        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
    }

    public void fetchDriverStats() {
        try {

            Connection conn = Database.getConnection();

            if(raceResultHorse != null) {
                Driver raceResultdriver = raceResultHorse.getRaceResultDriver();
                raceResultdriver.fetchRaceTypeForm(conn, getRaceDate(), getRaceMode());

                raceProgramDriver.setName(raceResultdriver.getName());
                raceProgramDriver.setForm(raceResultdriver.getForm());

            } else {
                raceProgramDriver.fetchRaceTypeForm(conn, this.getRaceDate(), this.getRaceMode());
            }

            coach.fetchForm(conn, this.getRaceHorseName(), this.getRaceDate());

            fetchSubStarts(conn, 8);

        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
    }

    public Coach getCoach() {
        return coach;
    }

    public void setCoach(Coach coach) {
        this.coach = coach;
    }

    public void setCoach(String name) {
        this.coach = new Coach(name);
    }


    public boolean existsInDatabase() {

        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            Connection conn = Database.getConnection();

            statement = conn.prepareStatement("select NIMI from SUBRESULT where NIMI=? and LAJI=? and PVM=? and LAHTONUMERO=?");
            statement.setString(1, this.getRaceHorseName());
            statement.setString(2, this.getRaceProgramStart().getRaceLiteral());
            statement.setDate(3, DateUtils.toSQLDate(this.getRaceDate()));
            statement.setBigDecimal(4, this.getRaceProgramStart().getStartNumber());

            set = statement.executeQuery();
            if(set.next()) {
                //setId(set.getString("ID"));
                return true;
            }

        } catch (Exception e) {
            Log.write(e);
        } finally {
            try { set.close(); } catch (Exception e) { }
            try { statement.close();} catch (Exception e) { }
        }
        return false;
    }



    public RaceProgramDriver getRaceProgramDriver() {
        return raceProgramDriver;
    }

    public void setRaceProgramDriver(RaceProgramDriver raceProgramDriver) {
        this.raceProgramDriver = raceProgramDriver;
    }

    public void setRaceProgramDriver(String raceProgramDriver) {
        this.raceProgramDriver = new RaceProgramDriver(raceProgramDriver);
    }

    public String getId() {
        return getRaceProgramStart().getId() + "_" + (getHorseProgNumber().intValue() < 10 ? "0" + getHorseProgNumber() : getHorseProgNumber());
    }

    public RaceResultHorse getRaceResultHorse() {
        return raceResultHorse;
    }

    public void setRaceResultHorse(RaceResultHorse raceResultHorse) {
        this.raceResultHorse = raceResultHorse;
    }

    public double getK() {
        try {
            return quarterTime2.getPropbabilty().doubleValue();
        } catch (Exception e) {
            return 0;
        }
    }

    private void appendSubStart(StringBuilder str, int line) {
        try {
            int startPoint = 36;
            if (line < subStartList.size()) {

                while (str.length() >= startPoint)
                    str.deleteCharAt(str.length() - 1);
                while (str.length() < startPoint)
                    str.append(" ");

                str.append(subStartList.get(line).toString());
            }
        } catch (Exception e) {
            Log.write(e, getId());
        }
    }

    public void setRaceHandicap(RaceProgramStart raceProgramStart) {
        try {
            BigDecimal tasoitus = this.getRaceLength().subtract(raceProgramStart.getRaceLength());
            setTasoitus(tasoitus);
        } catch (Exception e) {
            Log.write(e, getId());
        }
    }

    public BigDecimal getTrackFirstQuarterPropability() {

        return trackFirstQuarterPropability;
    }

    public void setTrackFirstQuarterPropability(List trackIdL) {
        try {
            this.trackFirstQuarterPropability = RaceResultStart.trackSectionTimeV1.get(trackIdL).procents(1);
        } catch (Exception e) {
            this.trackFirstQuarterPropability = BigDecimal.ONE;
        }
    }

    public BigDecimal getTrackSecondQuarterPropability() {
        return trackSecondQuarterPropability;
    }

    public void setTrackSecondQuarterPropability(List trackIdL) {
        try {
            this.trackSecondQuarterPropability = RaceResultStart.trackSectionTimeV2.get(trackIdL).procents(1);
        } catch (Exception e) {
            this.trackSecondQuarterPropability = BigDecimal.ONE;
        }
    }

    public QuarterTime getQuarterTime2() {
        return quarterTime2;
    }

    public void setQuarterTime2(QuarterTime quarterTime2) {
        this.quarterTime2 = quarterTime2;
    }

    /*
    public double getLeadingChange(SubStart subStart) {

        try {
            // hevosen todennäköisyys paalulle
            Form K1Form = fullStatistics.getFeaturedStats().get(Collections.singletonList(BigDecimal.ONE));
            BigDecimal horseProp = K1Form.getProbability();

            // substartin radan todennäköisyys paalulle
            List trackIdL = Collections.singletonList(subStart.getTrackId());
            BigDecimal subTrackProb = RaceResultStart.trackSectionTimeV2.get(trackIdL).procents(2);

            // hevosen radan todennäköisyys paalulle
            BigDecimal trackPropChange = trackSecondQuarterPropability.subtract(subTrackProb);
            trackPropChange = trackPropChange.divide(BigDecimal.valueOf(100.00), 2, RoundingMode.HALF_UP);

            BigDecimal leadingChange = trackPropChange.multiply(horseProp).setScale(2, RoundingMode.HALF_UP);

            return leadingChange.doubleValue();

        } catch (Exception e) {

            return 0.0;
        }
    }*/

    public String toString() {
        try {
            StringBuffer str = new StringBuffer();

            str.append(getHorseProgNumber());
            str.append("(" + getTrackId() + ")");
            str.append("\t" + getRaceHorseName() + " (" + getRaceProgramDriver() + ")");
            if (raceResultHorse != null) {
                str.append(" => " + raceResultHorse.toString());
            }

            str.append("\n");

            str.append("\t" + fullStatistics);
            //str.append("\t" + fullStatistics != null ? fullStatistics.getTimeStatistics().getFinalTimes().toString() : "");

            //str.append("\n\t" + fullStatistics.getFeaturedStats().get(Collections.singletonList(BigDecimal.ZERO)));
            //str.append("\n\t" + fullStatistics.getFeaturedStats().get(Collections.singletonList(BigDecimal.ONE)));

            if(fullStatistics != null) {
                str.append("\n[" + trackFirstQuarterPropability + "%]");
                str.append("\t500m:  " + fullStatistics.getTimeStatistics().getFirstQuarter().toString());

                str.append("\n[" + trackSecondQuarterPropability + "%]");
                str.append("\t1000m: " + fullStatistics.getTimeStatistics().getSecondQuarter().toString());

                str.append("\n");
                str.append("\tV500m: " + fullStatistics.getTimeStatistics().getLastQuarter().toString());
            }

            str.append("\n\t" + coach.getForm());
            str.append("\n\t" + raceProgramDriver.getForm());
            /*
            for (int i = 0; i < 9; i++) {
                str.append("\n" + toString(i));
            }*/

            return str.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String toString(int line) {
        final int LEN = 30;
        StringBuilder str = new StringBuilder();
        switch(line) {
            case 0: str.append(age + " v " + color + " " + sex + " " + sire + " -");
                appendSubStart(str, line);
                break;
            case 1: str.append(dam + " - " + sireOfDam);
                appendSubStart(str, line);
                break;
            case 2: str.append(color1 + " - " + color2);
                appendSubStart(str, line);
                break;
            case 3: str.append(owner + ", " + ownerLocation);
                appendSubStart(str, line);
                break;
            case 4: str.append("(" + coach + ")");
                appendSubStart(str, line);
                break;
            case 5: str.append(getRaceProgramDriver() != null ? getRaceProgramDriver().toString().toUpperCase() : "");
                appendSubStart(str, line);
                break;
            //if (driver.getDriverStats(conn, this).jockeyClass != null) str.append(" (" + driver.getDriverStats(conn, this).jockeyClass + ")"); break;
            case 6: str.append(StringUtils.toColumn(getRaceProgramDriver().getForm().toString(), 40));
                appendSubStart(str, line);
                break;
            case 7: //str.append(driver.getDriverStats().getYearForm().toString());
                appendSubStart(str, line);
                break;
            case 8: //str.append(driver.getFullYearForm().toString());
                break;
        }
        //str = new StringBuffer(StringUtils.toColumnString(str.toString(), LEN));

        return str.toString();
    }

    public double[] getRegX() {
        try {

            List<BigDecimal> xList = new ArrayList();

            Form form0 = fullStatistics.getForm();
            Form form1 = fullStatistics.getkForm();

            xList.add(form0.getStarts());
            xList.add(form0.getFirsts());
            xList.add(form0.getSeconds());
            xList.add(form0.getThirds());
            xList.add(form0.getAwards());

            QuarterTimes qt1 = getFullStatistics().getTimeStatistics().getSecondQuarter();
            BigDecimal p1 = qt1.getPropabiltyProcents();

            xList.add(form1.getStarts().multiply(p1));
            xList.add(form1.getFirsts().multiply(p1));
            xList.add(form1.getSeconds().multiply(p1));
            xList.add(form1.getThirds().multiply(p1));
            xList.add(form1.getAwards().multiply(p1));

            double[] x = new double[xList.size()];

            int xi = 0;
            for (BigDecimal b : xList) {
                x[xi++] = b.doubleValue();
            }

            return x;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
