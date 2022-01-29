package hippos;

import hippos.exception.DatabaseException;
import hippos.io.RaceResultFile;
import hippos.util.Mapper;
import hippos.math.Value;
import hippos.math.betting.*;
import hippos.math.racing.SectionalTime;
import hippos.utils.HorsesHelper;
import utils.Log;

import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.util.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RaceResultStart extends RaceStart {
    BigDecimal vk1, vk2;
    BigDecimal kk1;
    BigDecimal tk1, tk2;
    BigDecimal va1, va2, va3, va4, va5;
    BigDecimal loppuaika, parasaika;
    BigDecimal numero_1, numero_2, numero_3;

    BigDecimal raceResultTime = null;
    //BigDecimal winningTime = null;
    //BigDecimal winnerNumber = null;
    RaceResultFile raceResultFile;
    List <BetRate> betRates = new ArrayList();
    private BigDecimal bestResultTime;
    private Map oddsMap = new HashMap();
    private Map orderMap = new HashMap();
    private Map horseMap = new HashMap();
    private Map<String, RaceResultHorse> raceResultHorses = new HashMap<>();
    private List<SectionalTime> sectionalTimes = new ArrayList<>();

    public static Mapper <Value> trackSectionTimeV1 = new Mapper<>();
    public static Mapper <Value> trackSectionTimeV2 = new Mapper<>();
    /**
     * Parserit käyttää
     *
     * @param raceResultFile
     */
    public RaceResultStart(RaceResultFile raceResultFile) {
        this.raceResultFile = raceResultFile;
        this.setDate(raceResultFile.getDate());
    }

    public RaceResultStart(Connection conn, String id, Date date) throws DatabaseException {
        this.setDate(date);
        this.id = id;

        initRaceResultStart(conn, id, date);
        initRaceResultHorses(conn, id);
    }

    private void initRaceResultStart(Connection conn, String id, Date date) throws DatabaseException {
        this.setDate(date);

        PreparedStatement raceStmt = null;
        ResultSet raceSet = null;

        try {
            raceStmt = getRaceResultStartStatement(conn, id);
            raceSet = raceStmt.executeQuery();

            if (raceSet.next()) {
                this.setStartNumber(raceSet.getBigDecimal("NUMERO"));
                this.vk1 = raceSet.getBigDecimal("VK1");
                this.vk2 = raceSet.getBigDecimal("VK2");
                this.kk1 = raceSet.getBigDecimal("KK1");
                this.tk1 = raceSet.getBigDecimal("TK1");
                this.tk2 = raceSet.getBigDecimal("TK2");
                /*
                this.va1 = raceSet.getBigDecimal("VALIAIKA_1");
                this.va2 = raceSet.getBigDecimal("VALIAIKA_2");
                this.va3 = raceSet.getBigDecimal("VALIAIKA_3");
                this.va4 = raceSet.getBigDecimal("VALIAIKA_4");
                this.va5 = raceSet.getBigDecimal("VALIAIKA_5");
                this.loppuaika = raceSet.getBigDecimal("LOPPUAIKA");
                this.parasaika = raceSet.getBigDecimal("PARASAIKA");
                */

                this.numero_1 = raceSet.getBigDecimal("NUMERO_1");
                this.numero_2 = raceSet.getBigDecimal("NUMERO_2");
                this.numero_3 = raceSet.getBigDecimal("NUMERO_3");
            } else {
                throw new DatabaseException("RaceResultStart not found!");
            }

            if(vk1 != null) {
                BetRate betRate = new BetRate("Voittaja");
                betRate.getOdds().add(vk1);
                if(vk2 != null) {
                    betRate.getOdds().add(vk2);
                }
                this.betRates.add(betRate);
                oddsMap.put("Voittaja", betRate);
            }

            if(kk1 != null) {
                BetRate betRate = new BetRate("Kaksari");
                betRate.getOdds().add(kk1);
                this.betRates.add(betRate);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { raceSet.close();} catch (SQLException e) { e.printStackTrace(); }
            try { raceStmt.close();} catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private void initRaceResultHorses(Connection conn, String id) {

        PreparedStatement raceStmt = null;
        ResultSet raceSet = null;

        try {
            raceStmt = getRaceResultHorsesStatement(conn, id);
            raceSet = raceStmt.executeQuery();

            while (raceSet.next()) {
                RaceResultHorse raceResultHorse = new RaceResultHorse(raceSet, this);
                raceResultHorses.put(raceResultHorse.getRaceHorseName(), raceResultHorse);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { raceSet.close();} catch (SQLException e) { e.printStackTrace(); }
            try { raceStmt.close();} catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public List getBetRates() {
        return betRates;
    }

    public void add(RaceResultHorse raceResultHorse) {

        raceResultHorses.put(raceResultHorse.getRaceHorseName(), raceResultHorse);

        if(raceResultHorse.raceResultRanking != null && raceResultHorse.present == true) {
            BigDecimal position = raceResultHorse.raceResultRanking.getBigDecimal();
            String positionStr = raceResultHorse.raceResultRanking.toString();
            if(position != null) {
                List horses = (ArrayList) orderMap.get(position);
                if (horses == null)
                    horses = new ArrayList();
                horses.add(raceResultHorse);

                orderMap.put(position, horses);
            }
            if(positionStr != null) {
                horseMap.put(raceResultHorse.getRaceHorseName().toString(), positionStr);
            }
        }
    }

    public void print(Writer os) throws IOException {
        os.write(toString());
    }


    private List getHorseNumbers(List horses) {
        List numberList = new Vector();
        Iterator horseItr = horses.iterator();
        while(horseItr.hasNext()) {
            Horse horse = (Horse)horseItr.next();
            numberList.add(horse.getHorseProgNumber());
        }
        return numberList;
    }

    public void insert(Connection conn) throws SQLException {
        PreparedStatement stmt = null;

        List resultRankingList = new ArrayList(this.getRaceResultHorses());

        BigDecimal vk1=null, vk2=null, kk1=null, tk1=null, tk2=null;

        try {
            Iterator betItr = betRates.iterator();
            while (betItr.hasNext()) {
                BetRate betRate = (BetRate) betItr.next();
                if (betRate.getName().equals("Voittaja")) {
                    vk1 = betRate.getOdds().size() > 0 ? (BigDecimal) betRate.getOdds().get(0) : null;
                    vk2 = betRate.getOdds().size() > 1 ? (BigDecimal) betRate.getOdds().get(1) : null;
                }

                if (betRate.getName().equals("Kaksari")) {
                    kk1 = betRate.getOdds().size() > 0 ? (BigDecimal) betRate.getOdds().get(0) : null;
                }

                if (betRate.getName().equals("Troikka")) {
                    tk1 = betRate.getOdds().size() > 0 ? (BigDecimal) betRate.getOdds().get(0) : null;
                    tk2 = betRate.getOdds().size() > 1 ? (BigDecimal) betRate.getOdds().get(1) : null;
                }

            }
        } catch (Exception e) {
            Log.write(e);
        }

        try {
            StringBuffer sb = new StringBuffer();
            sb.append("insert into LAHTOTULOKSET(");
            sb.append("ID, NUMERO, VK1, VK2, KK1, TK1, TK2, ");
            sb.append("LOPPUAIKA, PARASAIKA, ");
            sb.append("NUMERO_1, NUMERO_2, NUMERO_3)  values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            stmt = conn.prepareStatement(sb.toString());
            int i = 1;
            stmt.setString(i++, id);
            stmt.setBigDecimal(i++, this.getStartNumber());
            stmt.setBigDecimal(i++, vk1);
            stmt.setBigDecimal(i++, vk2);
            stmt.setBigDecimal(i++, kk1);
            stmt.setBigDecimal(i++, tk1);
            stmt.setBigDecimal(i++, tk2);
            stmt.setBigDecimal(i++, raceResultTime);
            stmt.setBigDecimal(i++, bestResultTime);
            stmt.setBigDecimal(i++, (resultRankingList.size() > 0 && resultRankingList.get(0) != null) ? ((RaceResultHorse) resultRankingList.get(0)).getHorseProgNumber() : null);
            stmt.setBigDecimal(i++, (resultRankingList.size() > 1 && resultRankingList.get(1) != null) ? ((RaceResultHorse) resultRankingList.get(1)).getHorseProgNumber() : null);
            stmt.setBigDecimal(i++, (resultRankingList.size() > 2 && resultRankingList.get(2) != null) ? ((RaceResultHorse) resultRankingList.get(2)).getHorseProgNumber() : null);

            stmt.executeUpdate();

        } catch (SQLException e) {
            if (e.getErrorCode() != 1) {
                Log.write(e);
            }
        } catch (Exception e) {
            Log.write(e);
            conn.rollback();
            throw e;
        }

        try {
            insert(raceResultHorses, conn);

            conn.commit();
        } catch (Exception e) {
            Log.write(e);
            conn.rollback();
            throw e;
        } finally {
            try { stmt.close(); } catch(Exception e) { }
        }
    }

    private void insert(Map<String, RaceResultHorse> raceResultHorses, Connection conn) {
        for (RaceResultHorse raceResultHorse : raceResultHorses.values()) {
            try {
                if (raceResultHorse.present) {
                    raceResultHorse.insert(conn);

                    // Päivittää käsiohjelmahevoselle kuljettajan tiedot
                    //raceResultHorse.getRaceResultDriver().getDriverForm().fetchRaceTypeForm(conn, getDate());
                    raceResultHorse.updateRaceProgramDriver(conn);
                } else {
                    raceResultHorse.deleteRaceProgramHorse(conn);
                }
            } catch (SQLException se) {
                if(se.getErrorCode() != 1) {
                    Log.write(se);
                }
            } catch (Exception e) {
                Log.write(e, raceResultHorse.getRaceHorseName() + "(" + raceResultHorse.getId() + ")");
            }
        }
    }

    public String toString() {
        StringBuffer str = new StringBuffer();

        str.append(super.toString());

        return str.toString();
    }

    public Map getOddsMap() {
        return oddsMap;
    }

    public void setOddsMap(Map oddsMap) {
        this.oddsMap = oddsMap;
    }

    public Map getOrderMap() {
        return orderMap;
    }

    public Map getHorseMap() {
        return horseMap;
    }

    public RaceResultFile getRaceResultFile() {
        return raceResultFile;
    }

    public TreeSet<RaceResultHorse> getRaceResultHorses() {
        return new TreeSet(raceResultHorses.values());
    }

    private PreparedStatement getRaceResultStartStatement(Connection conn, String lid) throws SQLException {
        PreparedStatement statement = null;
        StringBuffer sb = new StringBuffer();

        sb.append("select NUMERO, VK1, VK2, KK1, TK1, TK2, ");
        //sb.append("VALIAIKA_1, VALIAIKA_2, VALIAIKA_3, VALIAIKA_4, VALIAIKA_5, ");
        //sb.append("LOPPUAIKA, PARASAIKA, ");
        sb.append("NUMERO_1, NUMERO_2, NUMERO_3 ");
        sb.append("from LAHTOTULOKSET where ID=?");
        //sb.append("order by ID");

        statement = conn.prepareStatement(sb.toString());
        statement.setString(1, lid);

        return statement;
    }

    /*
    private PreparedStatement getRaceResultHorsesStatement(Connection conn, String lid) throws SQLException {
        PreparedStatement statement = null;
        StringBuffer sb = new StringBuffer();

        sb.append("select SIJOITUS, NIMI, OHJELMAKULJETTAJA, ");
        sb.append("AIKA, LAHTOTYYPPI, XCODE, KERROIN, PALKINTO, MATKA, RATA ");
        sb.append("from SUBRESULT where PVM=? and LAHTONUMERO=?");
        sb.append("order by PVM");

        statement = conn.prepareStatement(sb.toString());
        statement.setString(1, );

        return statement;
    }*/

    private PreparedStatement getRaceResultHorsesStatement(Connection conn, String id) throws SQLException {
        PreparedStatement statement = null;

        try {

            String shortlocality = HorsesHelper.getShortLocalityFromId(id);

            StringBuffer sb = new StringBuffer();

            sb.append("select NUMERO, SIJOITUS, NIMI, KULJETTAJA, ");
            sb.append("AIKA, LAHTOTYYPPI, XCODE, X, KERROIN, PALKINTO, MATKA, RATA, TASOITUS, RATA_TUNNISTE, ");
            sb.append("VA_1, VA_2, V500 ");
            //sb.append("from RESULTHORSE where ID like ?");
            sb.append("from RESULTHORSE where PVM=? and LAHTONUMERO=? and PAIKKA=? ");
            sb.append("order by NUMERO");

            statement = conn.prepareStatement(sb.toString());
            //statement.setString(1, lid + "%");
            statement.setDate(1, this.getSQLDate());
            statement.setBigDecimal(2, this.getStartNumber());
            statement.setString(3, shortlocality);

        } catch (SQLException e) {
            Log.write(e);
            e.printStackTrace();

        }

        return statement;
    }

    public List <RaceResultHorse> getRaceResultHorseList() {
        return new ArrayList(raceResultHorses.values());
    }

    public RaceResultHorse getRaceResultHorse(String name) {
        return raceResultHorses.get(name);
    }

    public void add(SectionalTime sectionalTime) {
        this.sectionalTimes.add(sectionalTime);
    }

    public List<SectionalTime> getSectionalTimes() {
        return sectionalTimes;
    }

    public void add(BetRate betRate) {
        this.betRates.add(betRate);
    }

    /**
     * Päivittää ratakohtaisia väliaikatilastoja
     */
    public void updateTrackStats() {
        for(RaceResultHorse raceResultHorse : getRaceResultHorseList()) {
            List id = Collections.singletonList(raceResultHorse.getTrackId());
            BigDecimal va_1 = raceResultHorse.getVA_1();
            BigDecimal va_2 = raceResultHorse.getVA_2();

            trackSectionTimeV1.getOrCreate(id, new Value()).add(va_1 != null ? BigDecimal.ONE : BigDecimal.ZERO);
            trackSectionTimeV2.getOrCreate(id, new Value()).add(va_2 != null ? BigDecimal.ONE : BigDecimal.ZERO);
        }
    }

    public List getWinnerHorses() {
        List <RaceResultHorse> winnerHorses = new ArrayList<>();

        for(RaceResultHorse raceResultHorse : raceResultHorses.values()) {
            try {
                BigDecimal ranking = raceResultHorse.raceResultRanking.getNumber();
                if (ranking.equals(BigDecimal.ONE)) {
                    winnerHorses.add(raceResultHorse);
                }
            } catch (NullPointerException e) {
                // sijoitus puuttuu
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(winnerHorses.isEmpty()) {
            Log.write(this.getId() + ": RaceResultStart.getWinnerHorses empty");
        }
        return winnerHorses;
    }
}
