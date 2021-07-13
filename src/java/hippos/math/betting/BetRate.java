package hippos.math.betting;

import hippos.utils.StringUtils;
import utils.DatabaseException;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.util.*;
import java.sql.SQLException;

public class BetRate implements Betting {
    String name;
    List odds;
    BigDecimal volume;
    private List games;
    private HashMap gameByNames;
    private Game game;

    public BetRate() {
        odds = new Vector();
        games = new ArrayList();
        gameByNames = new HashMap();
    }

    public BetRate(String title) {
        this();
        this.name = title;
    }

    public BetRate(String nimi, List kertoimet, BigDecimal vaihto) {
        this.name = nimi;
        this.odds = kertoimet;
        this.volume = vaihto;
    }

    public void delete(String id) throws DatabaseException {
    }

    public List findById() {
        return null;
    }

    public void print(Writer os, Connection conn) throws IOException {
        os.write(toString(conn));
    }

    public void check(List ranking, List myRanking) {
        if(game != null) {
            game.check(ranking, myRanking);
        }
        Iterator gamesItr = games.iterator();
        while(gamesItr.hasNext()) {
            ((Game)gamesItr.next()).check(ranking, myRanking);
        }
    }

    public void update(Connection conn) throws SQLException {
        update(conn, GameFactory.LOCALITY);
    }

    public void update(Connection conn, String locality) throws SQLException {
        if(game != null)
            game.update(conn, locality);

        Iterator gamesItr = games.iterator();
        while(gamesItr.hasNext()) {
            ((Game)gamesItr.next()).update(conn, locality);
        }
    }

    public void insert(Connection conn) throws SQLException {
        insert(conn, GameFactory.LOCALITY);
    }

    public void insert(Connection conn, String locality) throws SQLException {
        if(game != null)
            game.insert(conn, locality);

        Iterator gamesItr = games.iterator();
        while(gamesItr.hasNext()) {
            ((Game)gamesItr.next()).insert(conn, locality);
        }
    }

    public String toString() {
        return name + "\t" + odds + "\t" + volume;
        /*
        Connection conn = null;
        try {
            conn = Database.getConnection();
            return toString(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return "";

         */
    }

    public String toString(Connection conn) {
        return toString(conn, GameFactory.LOCALITY);
    }

    public String toString(Connection conn, String locality) {
        StringBuffer str = new StringBuffer("\n");
        if(name != null) str.append(StringUtils.toColumn(name, 15));
        Iterator ri = odds.iterator();
        StringBuffer oddSb = new StringBuffer();
        while(ri.hasNext()) {
            BigDecimal odd = (BigDecimal)ri.next();
            oddSb.append(odd.setScale(2, RoundingMode.HALF_UP));
            if(ri.hasNext())
                oddSb.append("-");
        }
        str.append(StringUtils.toColumn(oddSb.toString(), 20));
        str.append(StringUtils.toColumn(volume != null ? volume.toString() : new String(), 10));

        Iterator itr = games.iterator();
        while(itr.hasNext()) {
            Game game = (Game)itr.next();
            str.append("\n" + game.toString(conn, locality));
        }

        return str.toString();
    }

    public List getOdds() {
        return odds;
    }

    public void addGame(Game game) {
        games.add(game);
        gameByNames.put(game.name, game);
    }

    public List getGames() {
        return games;
    }

    public Game getGame(String gameName) {
        return (Game)gameByNames.get(gameName);
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void addGames(List games) {
        this.games.add(games);
    }

    public String getName() {
        return name;
    }
}


