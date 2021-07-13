package hippos.math.betting;

import hippos.utils.StringUtils;
import utils.Log;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Apr 24, 2004
 * AlphaNumber: 11:01:43 PM
 * To change this template use Options | File Templates.
 */
public abstract class Game extends GeneralBet {
    String name;
    BigDecimal count = BigDecimal.ZERO;
    BigDecimal hits = BigDecimal.ZERO;
    BigDecimal losses = BigDecimal.ZERO;
    BigDecimal winnings = BigDecimal.ZERO;

    int rankingLimit = 0;
    int horsesBetLimit = 0;

    List odds;
    List horsesBet;
    private String databaseID;

    /**
     * @param title Pelin nimi
     */
    public Game(String title) {
        this.name = title;
    }

    public Game(String title, String databaseID) {
        this(title);
        this.databaseID = databaseID;
    }

    /**
     * @param title         Pelin nimi
     * @param rankingLimit  Minimisijoitus osumaan
     */
    public Game(String title, int rankingLimit) {
        super(rankingLimit);
        this.name = title;
        this.rankingLimit = rankingLimit;
    }

    /**
     * @param title         Pelin nimi
     * @param rankingLimit  Minimisijoitus osumaan
     * @param odds          Voittokertoimet
     */
    public Game(String title, int rankingLimit, List odds) {
        super(rankingLimit);
        this.name = title;
        this.rankingLimit = rankingLimit;
        this.odds = odds;
    }

    /**
     * @param title         Pelin nimi
     * @param rankingLimit  Minimisijoitus osumaan
     * @param horsesPlayed  Pelattujen hevosten määrä
     * @param odds          Voittokertoimet
     */
    public Game(String title, int rankingLimit, int horsesPlayed, List odds) {
        super(rankingLimit);
        this.name = title;
        this.rankingLimit = rankingLimit;
        this.horsesBetLimit = horsesPlayed;
        this.odds = odds;
    }

    public BigDecimal getLosses() {
        return losses;
    }

    public BigDecimal getWinnings() {
        return winnings;
    }

    public abstract List getHorsesToWin(List ranking, List myRanking);
    public abstract BigDecimal getLoss(int numberOfHorses);
    public abstract BigDecimal getWinnings(List ranking, List horsesBet, List odds);
    public abstract int getCombinations(int i);

    /**
     * Tarkastaa pelin
     *
     * @param ranking   Lähdön tulojärjestys
     * @param myRanking Omat pelihevoset järjestyksessä
     */
    public void check(List ranking, List myRanking) {
        horsesBet = getHorsesToWin(ranking, myRanking);
        if(horsesBet != null) {
            if(horsesBetLimit > 0) {
                if(horsesBet.size() > horsesBetLimit) {
                    horsesBet = horsesBet.subList(0, horsesBetLimit);
                } else {
                    winnings = getWinnings(ranking, horsesBet, odds);
                    if(winnings.compareTo(new BigDecimal("0.00")) > 0) {
                        hits = new BigDecimal(1);
                    }
                }
            } else {
                winnings = getWinnings(ranking, horsesBet, odds);
                if(winnings.compareTo(new BigDecimal("0.00")) > 0) {
                    hits = new BigDecimal(1);
                }
            }
            losses = getLoss(horsesBet.size());
        }
    }

    public void update(Connection conn) throws SQLException {
        update(conn, GameFactory.LOCALITY);
    }

    /**
     * Tallentaa pelitiedot tietokantaan
     *
     * @throws SQLException
     */
    public void update(Connection conn, String locality) throws SQLException {
        //if(horsesBet != null || count.compareTo(BigDecimal.ZERO) > 0) {
        if (horsesBet != null) {
            //Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet game = null;
            try {
                //conn = Database.getConnection();
                stmt = conn.prepareStatement("select * from GAME where TITLE=?");
                stmt.setString(1, generateId(locality));
                game = stmt.executeQuery();
                if (game.next()) {

                    // read old statistics
                    BigDecimal count = game.getBigDecimal("COUNT");
                    BigDecimal hits = game.getBigDecimal("HITS");
                    BigDecimal winnings = game.getBigDecimal("WINNINGS");
                    BigDecimal losses = game.getBigDecimal("LOSSES");
                    BigDecimal horses = game.getBigDecimal("HORSES");

                    // update old statistics
                    count = count.add(new BigDecimal("1"));
                    if (this.winnings.compareTo(new BigDecimal("0")) > 0) {
                        hits = hits.add(new BigDecimal("1"));
                    }
                    winnings = winnings.add(this.winnings);
                    losses = losses.add(this.losses);
                    if (horsesBet != null) {
                        horses = horses.add(new BigDecimal(horsesBet.size()));
                    }

                    // save updated statistics
                    PreparedStatement stmtUpdate = conn.prepareStatement("update GAME set COUNT=?, HITS=?, WINNINGS=?, LOSSES=?, HORSES=? where TITLE=?");
                    stmtUpdate.setBigDecimal(1, count);
                    stmtUpdate.setBigDecimal(2, hits);
                    stmtUpdate.setBigDecimal(3, winnings);
                    stmtUpdate.setBigDecimal(4, losses);
                    stmtUpdate.setBigDecimal(5, horses);
                    stmtUpdate.setString(6, generateId(locality));
                    stmtUpdate.executeUpdate();
                    stmtUpdate.close();

                } else {
                    insert(conn, locality);
                }
            } catch (SQLException se) {
                insert(conn, locality);
            } finally {
                //try {conn.commit(); } catch(Exception e) {}
                try {
                    game.close();
                } catch (Exception e) {
                }
                try {
                    stmt.close();
                } catch (Exception e) {
                }
                //try {conn.close(); } catch(Exception e) {}
            }
        }
    }


    public void insert(Connection conn) throws SQLException {
        insert(conn, GameFactory.LOCALITY);

    }

    public void insert(Connection conn, String locality) throws SQLException {
        if(horsesBet != null) {
            PreparedStatement stmt = null;
            count = new BigDecimal(1);
            try {
                stmt = conn.prepareStatement("insert into GAME(TITLE, COUNT, HITS, WINNINGS, LOSSES, HORSES) values(?, ?, ?, ?, ?, ?)");
                stmt.setString(1, generateId(locality));
                stmt.setBigDecimal(2, new BigDecimal("1"));
                stmt.setBigDecimal(3, this.hits);
                stmt.setBigDecimal(4, this.winnings);
                stmt.setBigDecimal(5, this.losses);
                stmt.setBigDecimal(6, new BigDecimal(horsesBet.size()));
                stmt.executeUpdate();
                //conn.commit();
                //stmt.clearParameters();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //try {conn.commit(); } catch(Exception e) {}
                try {stmt.close(); } catch(Exception e) {}
            }
        }
    }

    private String generateId(String locality) {
        return this.name + "_" + locality;
    }

    public String toString(Connection conn) {
        return toString(conn, GameFactory.LOCALITY);
    }

    public String toString(Connection conn, String locality) {
        //Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet game = null;
        StringBuffer str = new StringBuffer();
        try {
            //conn = Database.getConnection();
            stmt = conn.prepareStatement("select * from GAME where TITLE=?");
            stmt.setString(1, generateId(locality));
            game = stmt.executeQuery();

            if(game.next()) {
                BigDecimal count = game.getBigDecimal("COUNT");
                BigDecimal hits = game.getBigDecimal("HITS");
                BigDecimal winnings = game.getBigDecimal("WINNINGS");
                BigDecimal losses = game.getBigDecimal("LOSSES");
                BigDecimal horses = game.getBigDecimal("HORSES");

                str = new StringBuffer();
                str.append(StringUtils.toColumn(name, 15));

                str.append("pelejä: " + count + "\t");
                if(count.intValue() > 0) {
                    BigDecimal vp = hits.multiply(new BigDecimal(100)).divide(count, 2, BigDecimal.ROUND_HALF_UP);
                    BigDecimal tuotot = (winnings.subtract(losses)).setScale(2, BigDecimal.ROUND_HALF_UP);
                    str.append("voitto%: " + StringUtils.toColumn(vp + "%", 10));
                    str.append("tuotot: " + StringUtils.toColumn(tuotot + "€", 12));
                    str.append("hevoset: " + horses.divide(count, 2, BigDecimal.ROUND_HALF_UP) + "\t");

                    if(this.winnings != null && !this.winnings.equals(new BigDecimal(0))) {
                        if(this.winnings.subtract(this.losses).compareTo(new BigDecimal("0.00")) > 0) {
                            str.append("\t$$$$ " + this.winnings.subtract(this.losses).setScale(2, BigDecimal.ROUND_HALF_UP) + " $$$$");
                        }
                    }
                }
                game.close();
            }
        } catch (SQLException e) {
            Log.write(e);
        } finally {
            try {conn.commit(); } catch(Exception e) {}
            try {game.close(); } catch(Exception e) {}
            try {stmt.close(); } catch(Exception e) {}
            //try {conn.close(); } catch(Exception e) {}
        }
        return str.toString();
    }

    public String getDatabaseID() {
        return databaseID;
    }

    public void setDatabaseID(String databaseID) {
        this.databaseID = databaseID;
    }

}
