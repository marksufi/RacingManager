package hippos.lang.toto;

import hippos.*;
import hippos.io.RaceProgramFile;
import hippos.math.betting.BetRate;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by marktolo on 18.11.2014.
 */
public class Voittajapeli implements Totopeli {
    private final String title;
    private static final String databaseFieldID = "V1";
    public static final String FILEID = "Voittaja";

    private List <ValueHorse> totoHorses = new ArrayList<>();
    private TotoGameStats gameStats = new TotoGameStats();
    private BigDecimal win = null;
    private BigDecimal bet = BigDecimal.ONE.negate();
    private Map <String, ArrayList> totoStartHorses = new TreeMap<>();

    public Voittajapeli(String title) {
        this.title = title;
    }

    public void submit(RaceProgramStart raceProgramStart) {

        TreeSet valueHorseSet = raceProgramStart.getValueHorseSet();

        submit(raceProgramStart.getId(), valueHorseSet);

    }

    public void update(Connection conn, RaceProgramFile raceProgramFile) {
        if(win !=  null) {
            PreparedStatement stmt= null;
            try {
                stmt = conn.prepareStatement("update PROGRAMFILE set v1=? where FILENAME=?");
                stmt.setBigDecimal(1, win);
                stmt.setString(2, raceProgramFile.getName());
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try{ stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }


    private void submit(String lid, TreeSet <ValueHorse> startHorseSet) {
        try {
            totoHorses = new ArrayList<>();
            totoHorses.add(startHorseSet.first());
        } catch (NoSuchElementException e) {
            // ei heppoja
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void check(RaceResultStart raceResultStart) {
        List <RaceResultHorse> voittajaHevoset = raceResultStart.getWinnerHorses();

        for(ValueHorse totoHevonen : totoHorses) {
            BigDecimal panos = BigDecimal.valueOf(-1);

            for(RaceResultHorse raceResultHorse : voittajaHevoset) {
                try {
                    String totoNimi = totoHevonen.getRaceProgramHorse().getRaceHorseName();
                    String tulosNimi = raceResultHorse.getRaceHorseName();

                    if(totoNimi.equals(tulosNimi)) {
                        // Voitto
                        BigDecimal voittokerroin = raceResultHorse.getRaceResultWinOdds();
                        panos = panos.add(voittokerroin);

                        System.out.println(panos + "€");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            gameStats.add(panos);
        }
    }

    public String toString() {
        return gameStats.toString();
    }
}
