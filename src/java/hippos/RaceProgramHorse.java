package hippos;

import hippos.database.Database;
import hippos.database.Statements;
import hippos.exception.RegressionModelException;
import hippos.lang.stats.*;
import hippos.math.AlphaNumber;
import hippos.math.Value;
import hippos.math.regression.HipposUpdatingRegression;
import hippos.util.Mapper;
import hippos.math.Progress;
import hippos.math.racing.QuarterTime;
import hippos.util.ObservationFramework;
import hippos.util.QuarterTimes;
import hippos.utils.DateUtils;
import hippos.utils.StringUtils;
import org.apache.commons.math3.stat.regression.ModelSpecificationException;
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
    private TimeStatistics timeStatistics;
    private YearStatistics yearStatistics;

    protected RaceProgramStart raceProgramStart;
    protected RaceResultHorse raceResultHorse;

    //private static final int regressionSize = 9;
    //private static HipposUpdatingRegression regressionMap = new HipposUpdatingRegression(regressionSize, false);
    //private static HipposUpdatingRegression regressionMap;
    private static ObservationFramework observationFramework = new ObservationFramework(3);
    private List<BigDecimal> xList = new ArrayList();

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
    private List<AlphaNumber> observableList;

    public RaceProgramHorse(RaceProgramStart raceProgramStart) {
        super(raceProgramStart);
        this.raceProgramStart = raceProgramStart;

        this.timeStatistics = new TimeStatistics(this);
        this.fullStatistics = new FullStatistics("Yht", this);
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
        this(raceProgramStart);

        this.raceProgramStart = raceProgramStart;

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
            setCoach(new Coach(raceSet.getString("VALMENTAJA")));

            setRaceResultHorse(raceProgramStart.getRaceResultStart());

            initSubStart(raceSet.getString("SUBSTART_1"));
            initSubStart(raceSet.getString("SUBSTART_2"));
            initSubStart(raceSet.getString("SUBSTART_3"));
            initSubStart(raceSet.getString("SUBSTART_4"));
            initSubStart(raceSet.getString("SUBSTART_5"));
            //initSubStart(raceSet.getString("SUBSTART_6"));
            //initSubStart(raceSet.getString("SUBSTART_7"));
            //initSubStart(raceSet.getString("SUBSTART_8"));

            //this.timeStatistics = new TimeStatistics(this);

            addWeeksKey();

            fetchQuarterTimes(conn);

            initFullStatistics(conn);
            initYearStatistics(conn);

        } catch (Exception e) {
            e.printStackTrace();
        }
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
            stmt.setString( i++, getRaceHorseName());
            stmt.setString( i++, getRaceProgramDriver().getName());
            stmt.setString( i++, coach.toString());
            stmt.setBigDecimal( i++, getRaceLength());
            stmt.setBigDecimal( i++, getRaceTrack());
            stmt.setBigDecimal( i++, getTasoitus());

            Form driverStats = getRaceProgramDriver().getDriverForm().raceTypeForm;
            stmt.setBigDecimal( i++, driverStats.getStarts());
            stmt.setBigDecimal( i++, driverStats.getFirsts());
            stmt.setBigDecimal( i++, driverStats.getSeconds());
            stmt.setBigDecimal( i++, driverStats.getThirds());
            stmt.setBigDecimal( i++, driverStats.getAwards());
            stmt.setBigDecimal( i++, driverStats.getKcode());
            stmt.setBigDecimal( i++, driverStats.getXcode());

            for(int is = 0; is < 5; is++) {
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
                        sb.append(subStart.getSubDriver().getName());
                        sb.append(";");
                        sb.append(subStart.getSubDriver().getWinRate());
                        sb.append(";");

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
            e.printStackTrace();
        } finally {
            try{ stmt.close(); } catch (Exception e) {}
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

            timeStatistics.fetchQuarterTimesStatistics(conn);

            try {
                QuarterTime quarterTime = timeStatistics.getFirstQuarter().getQuarterTime();
                quarterTime.addTrackPropability(getTrackFirstQuarterPropability());

                raceProgramStart.addQuarterTime(1, quarterTime);
            } catch (NoSuchElementException e) {
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                QuarterTime quarterTime = timeStatistics.getSecondQuarter().getQuarterTime();
                quarterTime.addTrackPropability(getTrackSecondQuarterPropability());

                raceProgramStart.addQuarterTime(2, quarterTime);
                setQuarterTime2(quarterTime);

            } catch (NoSuchElementException e) {
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                QuarterTime quarterTime = timeStatistics.getLastQuarter().getQuarterTime();
                raceProgramStart.addQuarterTime(3, quarterTime);

            } catch (NoSuchElementException e) {
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                QuarterTime quarterTime = timeStatistics.getFinalTimes().getQuarterTime();
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

               //addObservations(newSubStart);

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

        timeStatistics.init();
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
                    //subStart.getSubDriver().fetchRaceTypeForm(conn, subStart.getDate(), subStart.getRaceMode().toString());
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

    public YearStatistics getYearStatistics() {
        return yearStatistics;
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
    public void setStatistics(Connection conn) {
        try {

            //if(getRaceHorseName().equals("Saran Vappu"))
            //    System.out.println("RaceProgramHorse.setStatistics: Saran Vappu");

            //Connection conn = Database.getConnection();

            initFullStatistics(conn);

            if(raceResultHorse != null) {
                DriverForm raceResultdriverForm = raceResultHorse.getRaceResultDriver().getDriverForm();
                raceResultdriverForm.fetchRaceTypeForm(conn, getRaceDate(), getRaceMode());

                raceProgramDriver.setName(raceResultdriverForm.getName());
                raceProgramDriver.getDriverForm().setForm(raceResultdriverForm.getForm());

            } else {
                raceProgramDriver.getDriverForm().fetchRaceTypeForm(conn, this.getRaceDate(), this.getRaceMode());
            }

            fetchSubStarts(conn, 5);

        } catch (Exception e) {
            Log.write(e);
            e.printStackTrace();
        }
    }

    /**
     * Tämä vie aikaa
     */
    public void initFullStatistics(Connection conn) {
        try {

            fullStatistics = new FullStatistics("Yht", this);
            fullStatistics.fetchSubForms(conn);

        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
    }

    public void initYearStatistics(Connection conn) {
        try {

            yearStatistics = new YearStatistics("Year", this);
            yearStatistics.fetchSubForms(conn);

        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
    }

    public void fetchDriverStats() {
        try {

            Connection conn = Database.getConnection();

            if(raceResultHorse != null) {
                DriverForm raceResultdriverForm = raceResultHorse.getRaceResultDriver().getDriverForm();
                raceResultdriverForm.fetchRaceTypeForm(conn, getRaceDate(), getRaceMode());

                raceProgramDriver.setName(raceResultdriverForm.getName());
                raceProgramDriver.getDriverForm().setForm(raceResultdriverForm.getForm());

            } else {
                raceProgramDriver.getDriverForm().fetchRaceTypeForm(conn, this.getRaceDate(), this.getRaceMode());
            }

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

    private void appendSubStartString(StringBuilder str, int line) {
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

    private String toString(int line) {
        final int LEN = 30;
        StringBuilder str = new StringBuilder();
        switch(line) {
            case 0: str.append(age + " v " + color + " " + sex + " " + sire + " -");
                appendSubStartString(str, line);
                break;
            case 1: str.append(dam + " - " + sireOfDam);
                appendSubStartString(str, line);
                break;
            case 2: str.append(color1 + " - " + color2);
                appendSubStartString(str, line);
                break;
            case 3: str.append(owner + ", " + ownerLocation);
                appendSubStartString(str, line);
                break;
            case 4: str.append("(" + coach + ")");
                appendSubStartString(str, line);
                break;
            case 5: str.append(getRaceProgramDriver() != null ? getRaceProgramDriver().toString().toUpperCase() : "");
                appendSubStartString(str, line);
                break;
            //if (driver.getDriverStats(conn, this).jockeyClass != null) str.append(" (" + driver.getDriverStats(conn, this).jockeyClass + ")"); break;
            case 6: str.append(StringUtils.toColumn(getRaceProgramDriver().getDriverForm().getForm().toString(), 40));
                appendSubStartString(str, line);
                break;
            case 7: //str.append(driver.getDriverStats().getYearForm().toString());
                appendSubStartString(str, line);
                break;
            case 8: //str.append(driver.getFullYearForm().toString());
                break;
        }
        //str = new StringBuffer(StringUtils.toColumnString(str.toString(), LEN));

        return str.toString();
    }

    public double[] getRegX() {
        try {

            xList = new ArrayList();

            Form form0 = fullStatistics.getcForm();
            Form form1 = fullStatistics.getkForm();

            xList.add(fullStatistics.getStarts());
            //xList.add(form0.getFirsts());
            xList.add(fullStatistics.firstRate());
            xList.add(fullStatistics.sijaRate());
            xList.add(fullStatistics.getAwardRate());

            QuarterTimes qt1 = timeStatistics.getSecondQuarter();
            BigDecimal p1 = qt1.getPropabiltyProcents().getBigDecimal();

            xList.add(form1.getKcode(p1));
            xList.add(form1.firstRate(p1));
            xList.add(form1.sijaRate(p1));
            xList.add(form1.getAwardRate(p1));

            xList.add(getTasoitus());

            BigDecimal driverFirstRate = getRaceProgramDriver().getDriverForm().getForm().firstRate();

            xList.add(driverFirstRate);

            double bestTime = 0.0;
            for(SubForm subForm : yearStatistics.getSubForms().getValues()) {
                try {
                    double regY = subForm.getRegY(yearStatistics);

                    if(regY > bestTime) {
                        bestTime = regY;
                    }

                    //maxValue.add(new Value(regY));
                } catch (RegressionModelException e) {
                    // Ei onnistunut
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            xList.add(BigDecimal.valueOf(bestTime));


            /*
            xList.add(form1.getStarts().multiply(p1));
            xList.add(form1.getFirsts().multiply(p1));
            xList.add(form1.getSeconds().multiply(p1));
            xList.add(form1.getThirds().multiply(p1));
            xList.add(form1.getAwards().multiply(p1));
            */

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

    public List<AlphaNumber> getObservableList() {
        List <AlphaNumber> observableList = new ArrayList();

        try {
            observableList.add(new AlphaNumber(fullStatistics.getStarts()));
            observableList.add(new AlphaNumber(fullStatistics.getFirsts()));
            observableList.add(new AlphaNumber(fullStatistics.getSeconds()));
            observableList.add(new AlphaNumber(fullStatistics.getThirds()));

            Set<AlphaNumber> recordTimes = fullStatistics.getRecordTimes();

            int size = 0;
            for(AlphaNumber recordTime : recordTimes) {
                observableList.add(new AlphaNumber(recordTime));
                if(size++ > 0)
                    break;
            }
        } catch (Exception e) {
            Log.write(e);
        }

        return observableList;
    }

    public void addObservations() {
        try {

            BigDecimal ranking = raceResultHorse.getRaceRanking();
            BigDecimal x = raceResultHorse.getX();

            if(ranking != null && x.intValue() == 0) {
                List observableList = getObservableList();

                observationFramework.addObservations(observableList, ranking);
            }

            /*
            for (SubForm subForm : fullStatistics.getSubForms().getValues()) {
                try {
                    subForm.addObservations(raceResultPrize, fullStatistics);

                } catch (RegressionModelException e) {

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            for (SubForm subForm : yearStatistics.getSubForms().getValues()) {
                try {
                    subForm.addObservations(raceResultPrize, yearStatistics);

                } catch (RegressionModelException e) {

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }*/
        } catch (NullPointerException e) {
            if(raceResultHorse != null)
                e.printStackTrace();

        } catch (Exception e) {
            Log.write(e);

        }
    }

    public Value getObservation() throws ModelSpecificationException, NullPointerException {
        try {
            this.observableList = getObservableList();

            return observationFramework.getObservations(this.observableList);

        } catch (ModelSpecificationException e) {
            throw e;

        } catch (NullPointerException e) {

            throw e;

        } catch (Exception e) {
            e.printStackTrace();

            throw e;
        }
    }

    public List<BigDecimal> getxList() {
        return xList;
    }

    public TimeStatistics getTimeStatistics() {
        return timeStatistics;
    }

    public String toString() {
        try {
            StringBuffer str = new StringBuffer();

            str.append(getHorseProgNumber());
            str.append("(" + getTrackId() + ")");
            str.append("\t" + getRaceHorseName());
            if (raceResultHorse != null) {
                str.append(" => " + raceResultHorse.toString());
            }

            str.append("\n");

            str.append("\t" + fullStatistics + "\n");

            for(Object key : fullStatistics.getForms().getKeys()) {
                str.append("\t\t" + fullStatistics.getForms().get(key) + "\n");
            }

            str.append("\t" + yearStatistics + "\n");

            str.append("\t" + fullStatistics.getSubForms().get(this.getRaceMode()) + "\n");
            str.append("\n");
            /*
            for(SubForm subForm : yearStatistics.getSubForms()) {
                str.append("\n\t" + subForm);
            }*/

            //str.append("\t" + fullStatistics != null ? fullStatistics.getTimeStatistics().getFinalTimes().toString() : "");

            //str.append("\n\t" + fullStatistics.getFeaturedStats().get(Collections.singletonList(BigDecimal.ZERO)));
            //str.append("\n\t" + fullStatistics.getFeaturedStats().get(Collections.singletonList(BigDecimal.ONE)));

            if(fullStatistics != null) {
                str.append("\n[" + trackFirstQuarterPropability + "%]");
                str.append("\t500m:  " + timeStatistics.getFirstQuarter().toString());

                str.append("\n[" + trackSecondQuarterPropability + "%]");
                str.append("\t1000m: " + timeStatistics.getSecondQuarter().toString());

                str.append("\n");
                str.append("\tV500m: " + timeStatistics.getLastQuarter().toString());
            }

            //str.append("\n\t" + coach);
            if(this.raceProgramDriver != null) {
                str.append("\n\t" + getRaceProgramDriver().toString());
                str.append("\n\t" + raceProgramDriver.getDriverForm().getForm().toString());
            }

            for(SubStart subStart : subStartList) {
                str.append("\n\t\t" + subStart);
            }
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

}
