package utils;

import hippos.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: marktolo
 * Date: 13.11.2013
 * Time: 1:06
 * To change this template use File | Settings | File Templates.
 */
public class DatabaseHelper {
    private static Connection conn;

    /**
     * Lataa ja palauttaa tulostietokannasta receResultHorse objektin
     *
     * @param id ladattavan hevosen id
     */
    public static void selectRaceResultHorseById(String id) {
        PreparedStatement statement = null;
        ResultSet set = null;
        StringBuffer sqlString = new StringBuffer();

        try {
            if( conn == null ) conn = Database.getConnection();
            sqlString.append("select * from HORSERESULT where NAME = ?");
            statement = conn.prepareStatement(sqlString.toString());
            statement.setString(1, id);

        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    /**
     * Lataa ja palauttaa tulostietokannasta kaikki hevosen tulokset listamuodossa receResultHorse objektina
     *
     * @param id ladattavan hevosen id
     *
    public static List selectRaceResultHorseByName(String name) {
        List results = new ArrayList();
        PreparedStatement statement = null;
        ResultSet set = null;
        StringBuffer sqlString = new StringBuffer();

        try {
            if( conn == null ) conn = Database.getConnection();
            statement = conn.prepareStatement(SELECT_RACEHORSERESULTS);
            statement.setString(1, name);
            set = statement.executeQuery();
            Date exDate = null;
            int dayDiff = -1;

            while(set.next()) {
                RaceResultHorse horse = new RaceResultHorse();
                Date date = set.getDate("pvm");

                if(date != null && exDate != null) {
                    dayDiff = HorsesHelper.getDayDiff(date, exDate);
                }

                horse.setRaceResultPlacing(set.getBigDecimal("rank"));
                horse.setRaceProgNumber(set.getBigDecimal("numero"));
                horse.setName(set.getString("nimi"));
                horse.setRaceDriverName(set.getString("kuljettaja"));
                horse.setRaceResultTime(set.getBigDecimal("time"), set.getString("x"));
                horse.setRaceResultWinOdds(set.getBigDecimal("rate"));
                horse.setRaceResultPrice(set.getBigDecimal("money"));
                horse.setRaceLength(set.getBigDecimal("length"));
                horse.setRaceTrack(set.getBigDecimal("track"));
                horse.setTimeDiff(set.getBigDecimal("timediff"));
                horse.setDayDiff(dayDiff);

                exDate = date;
                results.add(horse);
            }
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }  finally {
            try { set.close(); } catch (Exception e) { }
            try { statement.close();} catch (Exception e) { }
        }

        return results;
    } */

    private static final String SELECT_RACEHORSERESULTS =
        new String("select hr.*, h.*, l.* from HORSERESULT hr, HORSE h, LAHTO l where h.ID = hr.ID and h.lid = l.id and hr.NAME = ? order by hr.PVM");


}
