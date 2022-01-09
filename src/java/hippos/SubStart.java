package hippos;

import hippos.database.Database;
import hippos.math.AlphaNumber;
import hippos.math.Value;
import hippos.math.regression.HipposRegressionResults;
import hippos.utils.DateUtils;
import hippos.utils.StringUtils;
import utils.ExistsInDatabaseException;
import utils.Log;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SubStart implements Comparable {
    //private int raceIndexDiff;
    //private String name;
    //private BigDecimal horseNumber;

    /* Käsiohjelman tiedot */
    private SubDriver subDriver;
    private String locality;
    private String weather;
    protected Date date;
    BigDecimal startNumber;
    private BigDecimal raceLength;
    private BigDecimal raceTrack;
    private String raceType;
    private RaceMode raceMode;
    private BigDecimal rating;

    /* Lisäykset hevosen historiatiedoista */
    private Coach coach;

    protected Integer day;
    protected Integer month;
    protected Integer year = null;

    private Date exDate;
    private String gapString;
    private BigDecimal gapDays;

    List shiftList;
    private String raceLiteral;
    private RaceProgramHorse raceProgramHorse;
    private BigDecimal timeProgress;

    //private AlphaNumber[][] splitTimes = new AlphaNumber[2][SplitTimes.SIZE];
    private int index;
    private BigDecimal equalizedTime;
    private BigDecimal yearRankTimeAward;

    // RaceProgramHorse:n SubStart taulukon tiedot
    private String trackId;
    private BigDecimal dateDiff;
    private SubTime subTime;
    private SubRank subRank;
    private AlphaNumber xCode;
    private BigDecimal x = BigDecimal.ZERO;
    private BigDecimal kCode;
    private BigDecimal award;
    private BigDecimal driverRaceTypeClass;

    private double season;
    private List weeksKeyList = new ArrayList();



    public SubStart(RaceProgramHorse raceProgramHorse) {
        this.raceProgramHorse = raceProgramHorse;
        shiftList = new ArrayList();
    }

    public SubStart(ResultSet set, RaceProgramHorse raceProgramHorse) throws SQLException {
        this.raceProgramHorse = raceProgramHorse;

        trackId = set.getString("RATA_TUNNISTE");
        weather = set.getString("WEATHER");
        subDriver = new SubDriver(set.getString("KULJETTAJA"));
        coach = new Coach(set.getString("VALMENTAJA"));
        locality = set.getString("PAIKKA");
        date = set.getDate("PVM");
        startNumber = set.getBigDecimal("LAHTONUMERO");
        raceLength = set.getBigDecimal("MATKA");
        raceTrack = set.getBigDecimal("RATA");
        raceType = set.getString("TYYPPI");

        raceMode = new RaceMode(set.getString("LAHTOTYYPPI"));
        subTime = new SubTime(set.getBigDecimal("AIKA"), raceMode.toString(), this);

        subRank = new SubRank(set.getBigDecimal("SIJOITUS"), this);

        xCode = new AlphaNumber(set.getString("XCODE"));
        x = set .getBigDecimal("X");
        if(xCode.getNumber() != null && subRank.getNumber() == null) {
            // hlo 8, hyl 1...
            subRank.setNumber(xCode.getNumber());
        }

        dateDiff = BigDecimal.valueOf(DateUtils.getDayDiff(raceProgramHorse.getRaceDate(), getDate()));

        rating = set.getBigDecimal("KERROIN");
        kCode = set.getBigDecimal("KCODE");
        award = set.getBigDecimal("PALKINTO");

        subTime.setAlpha(raceMode.toString());
    }

    /**
     * Luo taulun lähdön PROGRAMHORSE-taulun SUBSTART rivistä
     *
     */
    public SubStart(String subStart, RaceProgramHorse raceProgramHorse) {
        this.raceProgramHorse = raceProgramHorse;

        try {
            int i = 0;
            if (subStart != null) {
                String[] tokens = subStart.split(";", 9);
                trackId = tokens[i++];
                if(trackId.contains("null"))
                    trackId = null;

                weather = tokens[i++];
                if(weather.contains("null"))
                    weather = null;

                subTime = new SubTime(tokens[i++], this);
                subRank = new SubRank(tokens[i++], this);
                xCode = new AlphaNumber(tokens[i++]);

                if(xCode.getAlpha() == null) {
                    xCode.setAlpha(new String());
                    x = BigDecimal.ZERO;
                } else {
                    if(xCode.getAlpha().contains("h") || xCode.getAlpha().contains("x")) {
                        x = BigDecimal.ONE;
                    }
                }

                String dateDiffStr = tokens[i++];
                dateDiff = new BigDecimal(dateDiffStr);

                award = new AlphaNumber(tokens[i++]).getBigDecimal();
                kCode = new BigDecimal(tokens[i++]);

                String driverClass = tokens[i++];
                this.driverRaceTypeClass = new BigDecimal(driverClass);

                setDate(raceProgramHorse.getRaceProgramStart().getDate(), new BigDecimal(dateDiffStr));
                setRaceMode(new RaceMode(subTime.getAlpha()));

                if(xCode.getNumber() != null && subRank.getNumber() == null)
                    subRank.setNumber(xCode.getNumber());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setDate(Date raceDate, BigDecimal dateDiff) {
        int daysToStart = dateDiff.intValue();
        this.date = DateUtils.rollDays(raceDate, -daysToStart);

        this.season = DateUtils.season(date);

    }


    public void print(Writer os) throws IOException {
        os.write(toString());
    }


    /**
     * Decreases the year of the date by one and updates the dateDiff horseProgNumber.
     *
    public Date deduceYear() {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.roll(Calendar.YEAR, false);
        date = new Date(c.getTimeInMillis());
        return date;
    }*/

    public String getxCode() {
        return xCode != null ? xCode.toString() : "";
    }

    /**
     * Must be set from outside, because substarts does not include raceResultPrize information
     *
     * @param award
     */
    public void setAward(BigDecimal award) {
        this.award = award;
    }

    public BigDecimal getAward() {
        return award;
    }

    public BigDecimal getTimeProgress() {
        return timeProgress;
    }

    public void setTimeProgress(BigDecimal timeProgress) {
        this.timeProgress = timeProgress;
    }

    public BigDecimal getGapDays() {
        return gapDays;
    }

    public Coach getCoach() {
        return coach;
    }

    public void setCoach(String coach) {
        this.coach = new Coach(coach);
    }

    public void setCoach(Coach coach) {
        this.coach = coach;
    }

    public void insert(RaceProgramHorse raceProgramHorse) throws ExistsInDatabaseException {
        PreparedStatement stmt = null;
        Connection conn;
        String name = raceProgramHorse.getRaceHorseName();
        int i = 1;

        try {
            conn = Database.getConnection();

            if(existsInDatabase(conn))
                throw new ExistsInDatabaseException();

            stmt = getInsertStatement(conn);

            stmt.setString(i++, name);
            stmt.setString(i++, this.raceLiteral);
            stmt.setString(i++, this.subDriver.toString());
            stmt.setString(i++, this.coach.toString());
            stmt.setString(i++, this.locality);
            stmt.setString(i++, this.weather);
            stmt.setDate(i++, getSQLDate());
            stmt.setBigDecimal(i++, this.startNumber);
            stmt.setBigDecimal(i++, this.raceLength);
            stmt.setBigDecimal(i++, this.raceTrack);
            stmt.setString(i++, this.raceType);
            stmt.setBigDecimal(i++, this.getSubTime().getNumber());
            stmt.setBigDecimal(i++, this.getkCode());
            stmt.setString(i++, raceMode.toString());
            stmt.setBigDecimal(i++, this.subRank != null ? this.subRank.getBigDecimal() : null);
            stmt.setBigDecimal(i++, (subRank != null && subRank.getNumber() != null) ? subRank.getNumber().intValue() == 1 ? BigDecimal.ONE : BigDecimal.ZERO : null);
            stmt.setBigDecimal(i++, (subRank != null && subRank.getNumber() != null) ? subRank.getNumber().intValue() == 2 ? BigDecimal.ONE : BigDecimal.ZERO : null);
            stmt.setBigDecimal(i++, (subRank != null && subRank.getNumber() != null) ? subRank.getNumber().intValue() == 3 ? BigDecimal.ONE : BigDecimal.ZERO : null);
            stmt.setString(i++, this.getxCode() != null ? this.getxCode() : null);
            stmt.setBigDecimal(i++, this.x);
            stmt.setBigDecimal(i++, this.rating != null ? this.rating : null);
            stmt.setBigDecimal(i++, this.award != null ? this.award : null);

            stmt.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            Log.write(e, this.toString());
        } catch (ExistsInDatabaseException e) {
            throw e;
        } catch (Exception e) {
            Log.write(e, this.toString());
        } finally {
            try { if(stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace();}
        }
    }

    public static PreparedStatement getInsertStatement(Connection conn) {
        PreparedStatement stmt = null;

        try {
            StringBuffer sb = new StringBuffer();
            sb.append("insert into SUBRESULT ");
            sb.append("(NIMI, LAJI, KULJETTAJA, VALMENTAJA, PAIKKA, WEATHER, PVM, LAHTONUMERO, MATKA, RATA, TYYPPI, AIKA, KCODE, LAHTOTYYPPI, SIJOITUS, S_1, S_2, S_3, XCODE, X, KERROIN, PALKINTO) ");
            sb.append("values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

            //driverGeneral = driverGeneral.subtract(driverYear);

            stmt = conn.prepareStatement(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stmt;
    }

    public void insert(Connection conn, String hid, RaceResultHorse raceResultHorse) throws ExistsInDatabaseException {
        PreparedStatement stmt = null;
        String name = raceResultHorse.getRaceHorseName().toString();
        int i = 1;

        //if(valueHorse.getRaceHorseName().equals("Solea Rivelliere"))
        //    System.out.print("");
        try {
            if(existsInDatabase(conn))
                throw new ExistsInDatabaseException();

            if(this.rating != null ) {
                if(this.rating.doubleValue() < 1.0) {
                    Log.write("SubStart.insert:[" + hid + ":" + this + "] voittokerroin pienempi kuin 1 = " + this.rating);
                }
            }

            StringBuffer sb = new StringBuffer();
            sb.append("insert into SUBRESULT ");
            sb.append("(NIMI, LAJI, OHJELMAKULJETTAJA, VALMENTAJA, PAIKKA, PVM, EXPVM, LAHTONUMERO, MATKA, RATA, TYYPPI, AIKA, KCODE, LAHTOTYYPPI, SIJOITUS, S_1, S_2, S_3, XCODE, X, KERROIN, PALKINTO, TAUKOVIIKOT) ");
            sb.append("values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

            stmt = conn.prepareStatement(sb.toString());
            stmt.setString(i++, name);
            stmt.setString(i++, this.raceLiteral);
            stmt.setString(i++, this.subDriver.toString());
            stmt.setString(i++, this.coach.toString());
            stmt.setString(i++, this.locality);
            stmt.setDate(i++, getSQLDate());
            stmt.setDate(i++, DateUtils.toSQLDate(exDate));
            stmt.setBigDecimal(i++, this.startNumber);
            stmt.setBigDecimal(i++, this.raceLength);
            stmt.setBigDecimal(i++, this.raceTrack);
            stmt.setString(i++, this.raceType);
            stmt.setBigDecimal(i++, this.subTime != null ? this.subTime.getBigDecimal() : null);
            stmt.setBigDecimal(i++, this.getkCode());
            stmt.setString(i++, raceMode.toString());
            stmt.setBigDecimal(i++, this.subRank != null ? this.subRank.getBigDecimal() : null);

            stmt.setBigDecimal(i++, (subRank != null && subRank.getNumber() != null) ? subRank.getNumber().intValue() == 1 ? BigDecimal.ONE : BigDecimal.ZERO : null);
            stmt.setBigDecimal(i++, (subRank != null && subRank.getNumber() != null) ? subRank.getNumber().intValue() == 2 ? BigDecimal.ONE : BigDecimal.ZERO : null);
            stmt.setBigDecimal(i++, (subRank != null && subRank.getNumber() != null) ? subRank.getNumber().intValue() == 3 ? BigDecimal.ONE : BigDecimal.ZERO : null);

            stmt.setString(i++, this.getxCode() != null ? this.getxCode() : null);
            stmt.setBigDecimal(i++, this.x);

            stmt.setBigDecimal(i++, this.rating != null ? this.rating : null);
            stmt.setBigDecimal(i++, this.award != null ? this.award : null);
            stmt.setString(i++, gapString);

            stmt.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            if(e.getErrorCode() == 1)
                throw new ExistsInDatabaseException();
            else
                Log.write(e);
        } catch (Exception e) {
            System.out.println("SubStart.insert: " + hid);
            Log.write(e);
        } finally {
            try { if(stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace();}
        }
    }

    public boolean existsInDatabase(Connection conn) {
        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            //String id = generateID();
            statement = conn.prepareStatement("select NIMI from SUBRESULT where NIMI=? and LAJI=? and PVM=? and LAHTONUMERO=?");
            statement.setString(1, this.raceProgramHorse.getRaceHorseName());
            statement.setString(2, this.raceLiteral);
            statement.setDate(3, DateUtils.toSQLDate(this.date));
            statement.setBigDecimal(4, this.startNumber);

            set = statement.executeQuery();
            if(set.next()) {
                return true;
            }
        } catch (SQLException e) {
            Log.write(e);
        } catch (Exception e) {
            Log.write(e);
        } finally {
            try { set.close(); } catch (Exception e) { }
            try { statement.close();} catch (Exception e) { }
        }
        return false;
    }

    public java.sql.Date getSQLDate() {
        java.sql.Date SQLDate = new java.sql.Date(this.date.getTime());
        return SQLDate;
    }

    public RaceProgramHorse getRaceProgramHorse() {
        return raceProgramHorse;
    }

    public RaceMode getRaceMode() {
        return raceMode;
    }

    public void setRaceMode(RaceMode raceMode) {
        this.raceMode = raceMode;
    }

    public boolean isValid() {
        if(subTime == null) return false;
        if(subTime.getBigDecimal() == null) return false;
        if(xCode != null && xCode.toString().length() > 0) return false;
        if(subRank == null) return false;
        //if(ranking != null && ranking.getNumber() != null && ranking.getNumber().equals(BigDecimal.ONE)) return false;
        //if(ranking.toString().indexOf("ol") >= 0) return false;
        //if(ranking.toString().indexOf("kl") >= 0) return false;
        return true;
    }

    public BigDecimal getRaceLength() {
        return raceLength;
    }

    public Date getDate() {
        return date;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public BigDecimal getEqualizedTime() {
        return equalizedTime;
    }

    public void setEqualizedTime(BigDecimal equalizedTime) {
        this.equalizedTime = equalizedTime;
    }

    public void setRaceTrack(BigDecimal raceTrack) {
        this.raceTrack = raceTrack;
    }

    public Object clone() {
        try {
            SubStart a = (SubStart) super.clone();
            return a;
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setYearRankTimeAward(BigDecimal yearRankTimeAward) {
        this.yearRankTimeAward = yearRankTimeAward;
    }

    public BigDecimal getYearRankTimeAward() {
        return yearRankTimeAward;
    }

    public BigDecimal getRaceTrack() {
        return raceTrack;
    }

    public SubDriver getSubDriver() {
        return subDriver;
    }

    public void setSubDriver(SubDriver subDriver) {
        this.subDriver = subDriver;
    }

    public void setSubDriver(String subDriver) {
        this.subDriver = new SubDriver(subDriver);
    }

    public void setSubTime(SubTime subTime) {
        this.subTime = subTime;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public Integer getYear() {
        return year;
    }

    public Integer getDay() {
        return day;
    }

    public Integer getMonth() {
        return month;
    }

    public void setGapDays(BigDecimal gapDays) {
        this.gapDays = gapDays;
    }

    public void setGapString(String gapString) {
        this.gapString = gapString;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public void setExDate(Date exDate) {
        if(exDate != null) {
            this.exDate = exDate;
            this.gapDays = BigDecimal.valueOf(DateUtils.getDayDiff(date, exDate));
            this.gapString = DateUtils.getGapString(gapDays.intValue());
        }
    }

    public Date setYear(Date date) {
        try {
            Calendar sc = Calendar.getInstance();

            if (getYear() == null) {
                Calendar rc = Calendar.getInstance();

                rc.setTime(date);
                int year = rc.get(Calendar.YEAR);

                sc.set(year, getMonth() - 1, getDay(), 0, 0, 0);
                sc.set(Calendar.MILLISECOND, 0);

                if (rc.getTime().compareTo(sc.getTime()) < 0) {
                    year--;
                    sc.set(year, getMonth() - 1, getDay(), 0, 0, 0);
                    sc.set(Calendar.MILLISECOND, 0);
                }
                this.year = Integer.valueOf(year);
            } else {
                sc.set(getYear(), getMonth() - 1, getDay(), 0, 0, 0);
                sc.set(Calendar.MILLISECOND, 0);
            }

            setDate(sc.getTime());
        } catch (Exception e) {
            Log.write(this.getRaceProgramHorse().getId() + ": " + this.toString() + ": " + "Failed to set Year to subStart");
        }
        return this.date;

    }

    public String getRaceLiteral() {
        return raceLiteral;
    }

    public void setRaceLiteral(String raceLiteral) {
        this.raceLiteral = raceLiteral;
    }

    public BigDecimal getStartNumber() {
        return startNumber;
    }

    public void setStartNumber(BigDecimal startNumber) {
        this.startNumber = startNumber;
    }

    public void setRaceLength(BigDecimal raceLength) {
        this.raceLength = raceLength;
    }

    public void setSubRank(BigDecimal subRank) {
        this.subRank = new SubRank(subRank, this);
    }


    public void setxCode(String xCode) {
        try {
            this.xCode = new AlphaNumber(xCode);
            if (xCode != null && (xCode.contains("h") || xCode.contains("x"))) {
                this.x = BigDecimal.ONE;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yy");

        StringBuffer p = new StringBuffer();
        try {
            p.append(StringUtils.parse(subDriver != null ? (String) subDriver.getName() : "", ' ', 25, StringUtils.ALIGN_LEFT));
            p.append(StringUtils.parse(locality != null ? locality : "", ' ', 4, StringUtils.ALIGN_LEFT));
            p.append(StringUtils.parse(date != null ? df.format(date) : "", ' ', 10, StringUtils.ALIGN_LEFT));
            //p.append(StringUtils.parse(gapString != null ? gapString : "", ' ', 4, StringUtils.ALIGN_LEFT));
            p.append(StringUtils.parse(startNumber != null ? startNumber.toString() : "", ' ', 3, StringUtils.ALIGN_LEFT));
            p.append(StringUtils.parse((raceLength != null ? raceLength : "") + "/" + raceTrack, ' ', 8, StringUtils.ALIGN_LEFT));
            p.append(StringUtils.toColumn(subTime != null ? subTime.getNumberString() : new String(), 15));
            p.append(StringUtils.toColumn(subRank != null ? subRank.getNumberString() : new String(), 3));
            p.append(getkCode() != null && getkCode().intValue() == 1 ? "-> " : "   ");
            p.append(StringUtils.toColumn(xCode != null ? xCode.toString() : new String(), 7));
            //p.append(StringUtils.toColumnString(rating != null ? rating.toString() : new String(), 5));
            p.append(award != null ? award.toString() + " €" : new String());

        } catch(Exception e) {
            e.printStackTrace();
        }
        return p.toString();
    }

    public String toValueString() {
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yy");

        StringBuffer p = new StringBuffer();
        try {
            p.append(StringUtils.parse(String.valueOf(trackId != null ? trackId : " "), ' ', 7, StringUtils.ALIGN_LEFT));
            p.append(StringUtils.parse(String.valueOf(weather != null ? weather : " "), ' ', 3, StringUtils.ALIGN_LEFT));
            p.append(StringUtils.parse(String.valueOf(driverRaceTypeClass), ' ', 5, StringUtils.ALIGN_LEFT));
            p.append(StringUtils.parse(date != null ? df.format(date) : "", ' ', 10, StringUtils.ALIGN_LEFT));
            //p.append(StringUtils.parse(gapString != null ? gapString : "", ' ', 4, StringUtils.ALIGN_LEFT));
            //p.append(StringUtils.parse(startNumber != null ? startNumber.toString() : "", ' ', 3, StringUtils.ALIGN_LEFT));
            //p.append(StringUtils.parse((raceLength != null ? raceLength : "") + "/" + raceTrack, ' ', 8, StringUtils.ALIGN_LEFT));
            //p.append(StringUtils.toColumn(subTime.getSubValue() != null ? subTime.getSubValue().toString() : new String(), 50));
            p.append(StringUtils.toColumn(subTime, 20));
            //p.append(StringUtils.toColumn(subRank.getSubValue() != null ? subRank.getSubValue().toString() : new String(), 50));
            p.append(StringUtils.toColumn(subRank, 5));
            p.append(getkCode() != null && getkCode().intValue() == 1 ? "-> " : "   ");
            p.append(StringUtils.toColumn(xCode != null ? xCode.toString() : new String(), 10));
            //p.append(StringUtils.toColumnString(rating != null ? rating.toString() : new String(), 5));
            p.append(award != null ? award.toString() + " €" : new String());
            //p.append("\t" + weeksKeyList);

        } catch(Exception e) {
            e.printStackTrace();
        }
        return p.toString();
    }

    public void setRaceType(String raceType) {
        try {
            this.raceType = raceType.substring(0, 1);

            if(!this.raceType.equals("R") && !this.raceType.equals("T")) {
                // mm. Linjalähtö, muuntaa tasoituslähdöksi
                this.raceType = "T";
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getRaceType() {
        return raceType;
    }

    public SubTime getSubTime() {
        return subTime;
    }

    public SubRank getSubRank() {
        return subRank;
    }

    public BigDecimal getkCode() {
        return kCode;
    }

    public void setkCode(BigDecimal kCode) {
        this.kCode = kCode;
    }

    public static void deleteAll(RaceProgramHorse raceProgramHorse) {
        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            Connection conn = Database.getConnection();
            statement = conn.prepareStatement("delete from SUBRESULT where NIMI=?");
            statement.setString(1, raceProgramHorse.getRaceHorseName());
            statement.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            Log.write(e);
        } finally {
            try { set.close(); } catch (Exception e) { }
            try { statement.close();} catch (Exception e) { }
        }
    }

    public void delete() {
        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            Connection conn = Database.getConnection();
            statement = conn.prepareStatement("delete from SUBRESULT where NIMI=? and LAJI=? and PVM=?");
            statement.setString(1, this.raceProgramHorse.getRaceHorseName());
            statement.setString(2, this.raceLiteral);
            statement.setDate(3, DateUtils.toSQLDate(this.date));
            statement.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            Log.write(e);
        } finally {
            try { set.close(); } catch (Exception e) { }
            try { statement.close();} catch (Exception e) { }
        }
    }

    public BigDecimal getDateDiff() {
        return dateDiff;
    }

    @Override
    public int compareTo(Object o) {
        try {
            SubStart oSubStart = (SubStart) o;

            if (getDate().equals(oSubStart.getDate()))
                return 0;

            Value timeValue = getSubTime().getSubValue().getValue();
            Value oTimeValue = oSubStart.getSubTime().getSubValue().getValue();

            Value rankValue = getSubRank().getSubValue().getValue();
            Value oRankValue = oSubStart.getSubRank().getSubValue().getValue();

            Value maxValue = timeValue.compareTo(rankValue) > 0 ? timeValue : rankValue;
            Value oMaxValue = oTimeValue.compareTo(oRankValue) > 0 ? oTimeValue : oRankValue;

            int c = oMaxValue.compareTo(maxValue);

            return c;
        } catch (Exception e) {
            Log.write(e);
        }
        return 0;
    }

    public String getRacePlace() {
        PreparedStatement statement = null;
        ResultSet set = null;
        Connection conn = null;

        try {
            conn = Database.getConnection();

            statement = conn.prepareStatement("select PAIKKA from SUBRESULT where NIMI=? and LAJI=? and PVM=?");
            statement.setString(1, this.raceProgramHorse.getRaceHorseName());
            statement.setString(2, this.raceLiteral);
            statement.setDate(3, DateUtils.toSQLDate(this.date));

            set = statement.executeQuery();
            if(set.next()) {
                return set.getString("PAIKKA");
            }
        } catch (SQLException e) {
            Log.write(e);
        } finally {
            try { set.close(); } catch (Exception e) { }
            try { statement.close();} catch (Exception e) { }
        }
        return null;

    }

    public void addWeeksKey(BigDecimal weeksKey) {
        if(weeksKey.intValue() >= 3)
            weeksKeyList = new ArrayList();

        if(weeksKeyList.size() > 3)
            weeksKeyList.remove(0);

        weeksKeyList.add(weeksKey);
    }

    public List getWeeksKeyList() {
        return weeksKeyList;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getWeather() {
        return weather;
    }

    public double getSeason() {
        return season;
    }

    public void setDriverRaceTypeClass(BigDecimal driverRaceTypeClass) {
        this.driverRaceTypeClass = driverRaceTypeClass;
    }

    public BigDecimal getDriverRaceTypeClass() {
        return driverRaceTypeClass;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }
}

