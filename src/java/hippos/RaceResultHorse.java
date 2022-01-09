package hippos;

import hippos.math.AlphaNumber;
import hippos.math.racing.SectionalTime;
import hippos.utils.StringUtils;
import utils.Log;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Nov 2, 2005
 * AlphaNumber: 9:07:23 PM
 * To change this template use Options | File Templates.
 */
public class RaceResultHorse extends Horse implements Comparable {
    private RaceResultStart raceResultStart;
    private RaceResultDriver raceResultDriver;
    SubRank raceResultRanking;
    AlphaNumber raceResultTime;
    AlphaNumber totalTime;
    BigDecimal raceResultWinOdds;
    private AlphaNumber shortNote;
    private BigDecimal raceResultPrize;
    boolean present;
    BigDecimal timeDiff;
    private int dayDiff;
    private String filename;
    //private Date date;
    private int index;
    private SplitTimes splitTimes;
    private BigDecimal startIntervalBefore;
    private BigDecimal x;
    private BigDecimal VA_1;
    private BigDecimal VA_2;
    private BigDecimal V500;

    public RaceResultHorse(RaceResultStart raceResultStart) {
        super(raceResultStart);

        this.raceResultStart = raceResultStart;
        present = true;
        //splitTimes = new SplitTimes();

        filename = raceStart.getFileId();
        //setRaceDate(raceResultStart.getDate());
    }

    public RaceResultHorse(ResultSet resultSet, RaceResultStart raceResultStart) throws SQLException {
        //super(raceResultStart);

        try {
            //setRaceDate(raceResultStart.getDate());

            setHorseProgNumber(resultSet.getBigDecimal("NUMERO"));
            setRaceResultRanking(new SubRank(resultSet.getBigDecimal("SIJOITUS")));

            //setHorseProgNumber(resultSet.getBigDecimal("HEVOSEN_NUMERO"));
            setRaceHorseName(resultSet.getString("NIMI"));
            setRaceResultDriver(resultSet.getString("KULJETTAJA"));
            setRaceResultTime(new AlphaNumber(resultSet.getBigDecimal("AIKA")));
            setRaceMode(resultSet.getString("LAHTOTYYPPI"));
            setShortNote(new AlphaNumber(resultSet.getString("XCODE")));
            setX(resultSet.getBigDecimal("X"));
            setRaceResultWinOdds(resultSet.getBigDecimal("KERROIN"));
            setRaceResultPrize(resultSet.getBigDecimal("PALKINTO"));
            setRaceLength(resultSet.getBigDecimal("MATKA"));
            setRaceTrack(resultSet.getBigDecimal("RATA"));
            setTasoitus(resultSet.getBigDecimal("TASOITUS"));
            setTrackId(resultSet.getString("RATA_TUNNISTE"));
            setVA_1(resultSet.getBigDecimal("VA_1"));
            setVA_2(resultSet.getBigDecimal("VA_2"));
            setV500(resultSet.getBigDecimal("V500"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int compareTo(Object o) {
        RaceResultHorse resultHorse = (RaceResultHorse) o;
        int c = raceResultRanking.compareTo(resultHorse.raceResultRanking);

        if(c == 0) {
            return resultHorse.getId().compareTo(this.getId());
        }
        return c;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(raceResultRanking + ". ");
        //sb.append(getHorseProgNumber() != null ? getHorseProgNumber().toString() + "\t" : "\t");
        //sb.append(getRaceHorseName());
        //sb.append("(" + getRaceResultDriver() + ")");
        //sb.append("\t");
        sb.append(raceResultTime != null ? raceResultTime.toString() : "-");
        sb.append(getRaceMode() + " ");
        sb.append(getShortNote() + " ");

        sb.append(getVA_1() != null ? "->" + getVA_1().toString() : "");
        sb.append(getVA_2() != null ? "->>" + getVA_2().toString() : "");
        sb.append("\t");
        sb.append(raceResultWinOdds != null ? raceResultWinOdds.toString() + "\t" : "\t");
        sb.append(raceResultPrize != null ? raceResultPrize.toString() + "€" : "");

        return sb.toString();
    }

    public void setTotalTime(BigDecimal totalTime) {
        this.totalTime = new AlphaNumber(totalTime);
    }

    public BigDecimal getRaceResultPrize() {
        return raceResultPrize;
    }

    public AlphaNumber getRaceResultTime() {
        return this.raceResultTime;
    }

    public BigDecimal getRaceTime() {
        return raceResultTime != null ? this.raceResultTime.getNumber() : null;
    }

    public BigDecimal getX() {
        return x;
    }

    public BigDecimal getRaceRanking() {
        return raceResultRanking != null ? this.raceResultRanking.getNumber() : null;
    }

    public String getRaceRankingString() {
        return raceResultRanking != null ? raceResultRanking.toString() : null;
    }

    public java.util.Date getRaceDate() {
        return raceResultStart.getDate();
    }

    public java.sql.Date getSQLDate() {
        Date SQLDate = new Date(getRaceDate().getTime());
        return SQLDate;
    }

    public SubRank getRaceResultRanking() {
        return raceResultRanking;
    }

    public BigDecimal getRaceResultWinOdds() {
        return raceResultWinOdds;
    }

    public void setSplitTime(int partTimeIndex, SectionalTime sectionalTime) {
        // Jokaiselle hevoselle lähdön avauspuolikas, ei kuitenkaan koelähdöissä
        splitTimes.add(SplitTimes.TYPE_RACE, partTimeIndex, sectionalTime.getHorseSplit());
        if (sectionalTime.getHorse().equals(getRaceHorseName().toString())) {
            // Henkilökohtaiset väliajat niiden ommistajille
            splitTimes.add(SplitTimes.TYPE_HORSE, partTimeIndex, sectionalTime.getHorseSplit());
        }
    }

    public SplitTimes getSplitTimes() {
        return splitTimes;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setRaceResultPrize(BigDecimal raceResultPrize) {
        this.raceResultPrize = raceResultPrize;
    }

    public void setStartIntervalBefore(BigDecimal startIntervalBefore) {
        this.startIntervalBefore = startIntervalBefore;
    }

    public BigDecimal getStartIntervalBefore() {
        return startIntervalBefore;
    }

    public RaceResultStart getRaceResultStart() {
        return raceResultStart;
    }

    public boolean isValid() {
        if(x != null)
            return false;
        if(raceResultWinOdds == null)
            return false;
        if(raceResultTime.getBigDecimal() == null)
            return false;
        if(raceResultRanking.getBigDecimal() == null)
            return false;

        return true;
    }

    public void setX(BigDecimal x) {
        this.x = x;
    }

    public void setRaceResultRanking(SubRank raceResultRanking) {
        this.raceResultRanking = raceResultRanking;
    }

    public void setRaceResultTime(AlphaNumber raceResultTime) {
        this.raceResultTime = raceResultTime;
    }

    public void setRaceResultWinOdds(BigDecimal raceResultWinOdds) {
        this.raceResultWinOdds = raceResultWinOdds;
    }

    /*
    public void update(Connection conn) {
            PreparedStatement stmt = null;
            int i = 1;

            try {
                StringBuffer sb = new StringBuffer();
                sb.append("update SUBRESULT ");
                sb.append("set HID=?, HEVOSEN_NUMERO=?, TASOITUS=?, LAHTORIVI=?, ");
                sb.append("VA_1=?, VA_2=?, VA_3=?, VA_4=?, VA_5=?, ");
                sb.append("VA_R_1=?, VA_R_2=?, VA_R_3=?, VA_R_4=?, VA_R_5=? ");
                sb.append("where ID=?");

                stmt = conn.prepareStatement(sb.toString());

                stmt.setString(i++, getId());
                stmt.setBigDecimal(i++, getHorseProgNumber());
                stmt.setBigDecimal(i++, getRaceHandicap());
                stmt.setString(i++, getRaceTrackRow());
                stmt.setBigDecimal(i++, getSplitTimes().getNumber(SplitTimes.TYPE_HORSE, 0));
                stmt.setBigDecimal(i++, getSplitTimes().getNumber(SplitTimes.TYPE_HORSE, 1));
                stmt.setBigDecimal(i++, getSplitTimes().getNumber(SplitTimes.TYPE_HORSE, 2));
                stmt.setBigDecimal(i++, getSplitTimes().getNumber(SplitTimes.TYPE_HORSE, 3));
                stmt.setBigDecimal(i++, getSplitTimes().getNumber(SplitTimes.TYPE_HORSE, 4));
                stmt.setBigDecimal(i++, getSplitTimes().getNumber(SplitTimes.TYPE_RACE, 0));
                stmt.setBigDecimal(i++, getSplitTimes().getNumber(SplitTimes.TYPE_RACE, 1));
                stmt.setBigDecimal(i++, getSplitTimes().getNumber(SplitTimes.TYPE_RACE, 2));
                stmt.setBigDecimal(i++, getSplitTimes().getNumber(SplitTimes.TYPE_RACE, 3));
                stmt.setBigDecimal(i++, getSplitTimes().getNumber(SplitTimes.TYPE_RACE, 4));

                stmt.setString(i++, SubStart.generateID(this));

                stmt.executeUpdate();
                conn.commit();

            } catch (Exception e) {
                Log.write(e, getId());
            } finally {
                try { stmt.close(); } catch ( Exception e ) { }
            }
    }
    */

    public void insert(Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        int i = 1;

        try {
            stmt = getInsertStatement(conn);

            // [ID VARCHAR ( 30 ) NOT NULL,
            //  PVM DATE NOT NULL,
            //  LAHTONUMERO NUMBER ( 2 ) NOT NULL,
            // 	SIJOITUS NUMBER ( 2, 0 ),
            //	NUMERO NUMBER ( 2, 0 ),
            //	NIMI VARCHAR ( 30 ),
            //	LAJI VARCHAR ( 1 ),
            //	KULJETTAJA VARCHAR ( 30 ),
            //	AIKA NUMBER ( 4, 1 ),
            //	AIKATYYPPI VARCHAR (10),
            //	XKOODI VARCHAR ( 10 ),
            //	X NUMBER ( 1 ),
            //	KERROIN NUMBER ( 5, 0 ),
            //	PALKINTO NUMBER ( 7, 0 ),
            //	MATKA NUMBER ( 4 ),
            //	RATA NUMBER ( 2 ),
            //	VA_1 NUMBER ( 3, 1 ),
            //	VA_2 NUMBER ( 3, 1 ),
            //	V500 NUMBER ( 3, 1 ),

            //stmt.setString(i++, getId());
            stmt.setDate(i++, getSQLDate());
            stmt.setBigDecimal(i++, getRaceResultStart().getStartNumber());
            stmt.setBigDecimal(i++, getRaceRanking());
            stmt.setBigDecimal(i++, getHorseProgNumber());
            stmt.setString(i++, getRaceHorseName());
            stmt.setString(i++, getRaceResultStart().getRaceLiteral());
            stmt.setString(i++, getRaceResultDriver().toString());
            stmt.setBigDecimal(i++, getRaceResultTime().getBigDecimal());
            stmt.setString(i++, getRaceResultTime().getAlpha());
            stmt.setString(i++, shortNote.toString());
            stmt.setBigDecimal(i++, getX());
            stmt.setBigDecimal(i++, getRaceResultWinOdds());
            stmt.setBigDecimal(i++, getRaceResultPrize());
            stmt.setBigDecimal(i++, getRaceLength());
            stmt.setBigDecimal(i++, getRaceTrack());
            stmt.setBigDecimal(i++, getTasoitus());
            stmt.setString(i++, getTrackId());
            stmt.setBigDecimal(i++, getVA_1());
            stmt.setBigDecimal(i++, getVA_2());
            stmt.setBigDecimal(i++, getV500());

            stmt.executeUpdate();
            //conn.commit();

        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            Log.write(e, getId());
        } finally {
            try { stmt.close(); } catch ( Exception e ) { }
        }
    }

    public static PreparedStatement getInsertStatement(Connection conn) {
        PreparedStatement stmt = null;

        try {
            StringBuffer sb = new StringBuffer();
            sb.append("insert into RESULTHORSE ");
            sb.append("(PVM, LAHTONUMERO, SIJOITUS, NUMERO, NIMI, ");
            sb.append("LAJI, KULJETTAJA, AIKA, LAHTOTYYPPI, XCODE, ");
            sb.append("X, KERROIN, PALKINTO, MATKA, RATA, TASOITUS, RATA_TUNNISTE, ");
            sb.append("VA_1, VA_2, V500) ");

            sb.append("values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

            stmt = conn.prepareStatement(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stmt;
    }


    public BigDecimal getVA_1() {
        return VA_1;
        /*
        if(raceResultStart.getSectionalTimes().size() > 0) {
            SectionalTime sectionalTime = raceResultStart.getSectionalTimes().get(0);
            if (sectionalTime.getHorse().equals(this.getRaceHorseName())) {
                return sectionalTime.getNumber();
            }
        }
        return null;

         */
    }

    public void setVA_1(BigDecimal va_1) {
        this.VA_1 = va_1;
    }

    public BigDecimal getVA_2() {
        return VA_2;
        /*
        if(raceResultStart.getSectionalTimes().size() > 1) {
            SectionalTime sectionalTime = raceResultStart.getSectionalTimes().get(1);
            if (sectionalTime.getHorse().equals(this.getRaceHorseName())) {
                return sectionalTime.getNumber();
            }
        }
        return null;
        */
    }

    public void setVA_2(BigDecimal va_2) {
        this.VA_2 = va_2;
    }

    public BigDecimal getV500() {
        try {
            int size;
            List ones = (List)raceResultStart.getOrderMap().get(BigDecimal.ONE);

            if(ones.size() > 0) {
                BigDecimal winTime = ((RaceResultHorse) ones.get(0)).raceResultTime.getBigDecimal();
                BigDecimal thisTime = raceResultTime.getBigDecimal();
                if (winTime != null && thisTime != null) {
                    BigDecimal diffTime = thisTime.subtract(winTime);
                    if ((size = raceResultStart.getSectionalTimes().size()) > 0) {
                        SectionalTime sectionalTime = raceResultStart.getSectionalTimes().get(size - 1);
                        return sectionalTime.getHorseSplit().getNumber().add(diffTime);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setV500(BigDecimal v500) {
        V500 = v500;
    }

    public AlphaNumber getShortNote() {
        return shortNote;
    }

    public void setShortNote(AlphaNumber shortNote) {
        this.shortNote = shortNote;
    }

    public RaceResultDriver getRaceResultDriver() {
        return raceResultDriver;
    }

    public void setRaceResultDriver(RaceResultDriver raceResultDriver) {
        this.raceResultDriver = raceResultDriver;
    }

    public void setRaceResultDriver(String raceResultDriver) {
        this.raceResultDriver = new RaceResultDriver(raceResultDriver);
    }

    public void updateRaceProgramDriver(Connection conn) {
        PreparedStatement stmt = null;
        int i = 1;

        try {
            StringBuffer sb = new StringBuffer();
            sb.append("update PROGRAMHORSE ");
            sb.append("set KULJETTAJA=?, ");
            sb.append("K_S=?, K_1=?, K_2=?, K_3=?, K_R=? ");
            sb.append("where LID=? and NUMERO=?");

            stmt = conn.prepareStatement(sb.toString());

            stmt.setString(i++, getRaceResultDriver().getName());
            stmt.setBigDecimal(i++, getRaceResultDriver().getDriverForm().getForm().getStarts());
            stmt.setBigDecimal(i++, getRaceResultDriver().getDriverForm().getForm().getFirsts());
            stmt.setBigDecimal(i++, getRaceResultDriver().getDriverForm().getForm().getSeconds());
            stmt.setBigDecimal(i++, getRaceResultDriver().getDriverForm().getForm().getThirds());
            stmt.setBigDecimal(i++, getRaceResultDriver().getDriverForm().getForm().getAwards());

            stmt.setString(i++, getLid());
            stmt.setBigDecimal(i++, getHorseProgNumber());

            stmt.executeUpdate();
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Log.write(e, getId());
        } finally {
            try { stmt.close(); } catch ( Exception e ) { }
        }
    }

    public void deleteRaceProgramHorse(Connection conn) {
        PreparedStatement stmt = null;
        int i = 1;

        try {
            StringBuffer sb = new StringBuffer();
            sb.append("delete from PROGRAMHORSE ");
            sb.append("where LID=? and NUMERO=?");

            stmt = conn.prepareStatement(sb.toString());

            stmt.setString(i++, getLid());
            stmt.setBigDecimal(i++, getHorseProgNumber());

            stmt.executeUpdate();
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Log.write(e, getId());
        } finally {
            try { stmt.close(); } catch ( Exception e ) { }
        }


    }

    /**
     * Käytetään regressioissa y-arvona
     *
     * @return
     */
    public BigDecimal getYValue() {
        return raceResultPrize != null ? raceResultPrize : BigDecimal.ZERO;
    }

    public BigDecimal getK() {
        if(getVA_2() != null) {
            return BigDecimal.ONE;
        }
        return BigDecimal.ZERO;
    }

    public void setRaceHandicap(RaceResultStart raceResultStart) {
        try {
            BigDecimal tasoitus = getRaceLength().subtract(raceResultStart.getRaceLength());
            setTasoitus(tasoitus);
        } catch (Exception e) {
            Log.write(e, getId());
        }
    }

    public String toRankingString() {
        try {
            StringBuffer sb = new StringBuffer();

            sb.append(this.raceResultRanking + ". ");
            sb.append(this.raceResultTime);

            sb.append(StringUtils.toColumn(this.shortNote, 8));
            sb.append(StringUtils.toColumn(this.raceResultWinOdds, 8));
            sb.append(this.raceResultPrize + " €");

            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
