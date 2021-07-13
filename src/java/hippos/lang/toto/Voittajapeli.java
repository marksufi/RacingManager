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

        /*
        totoHorses = new ArrayList();
        win = null;
        if(!valueHorseSet.isEmpty()) {
            totoHorses.add(((ValueHorse)valueHorseSet.first()).getRaceProgramHorse().getRaceHorseName());
        }*/
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
        /*
        totoHorses = new ArrayList();

        if(!totoStartHorses.containsKey(lid))
            totoStartHorses.put(lid, new ArrayList());

        if(!startHorseSet.isEmpty()) {
            totoHorses.add(((ValueHorse) startHorseSet.first()).getRaceProgramHorse().getRaceHorseName());
            ((ArrayList<String>)totoStartHorses.get(lid)).add(((ValueHorse) startHorseSet.first()).getRaceProgramHorse().getRaceHorseName());
        }*/
    }

    /*
    private void check(String lid, TreeSet resultHorseSet) {
        Iterator itr = resultHorseSet.iterator();
        totoHorses = (ArrayList<String>) totoStartHorses.get(lid);

        if(totoHorses != null) {
            for (int i = 0; itr.hasNext(); i++) {
                RaceResultHorse raceResultHorse = (RaceResultHorse) itr.next();
                BigDecimal rank = raceResultHorse.getRaceResultRanking().getNumber();
                BigDecimal winOdd = raceResultHorse.getRaceResultWinOdds();

                if (rank != null && winOdd != null) {
                    if (rank.equals(BigDecimal.ZERO)) {
                        System.out.print("");
                    } else if (rank.equals(BigDecimal.ONE) && !totoHorses.isEmpty()) {
                        //BigDecimal horseProgNumber = raceResultHorse.getHorseProgNumber();
                        String raceResultHorseName = raceResultHorse.getRaceHorseName();
                        if (raceResultHorseName != null) {
                            if (totoHorses.contains(raceResultHorseName)) {
                                gameStats.add(bet.add(winOdd));
                                System.out.println(bet.add(winOdd) + "�");
                            }
                            gameStats.add(bet);
                        } else {
                            System.out.println("Voittajapeli.check: horseProgNumber null");
                        }
                    } else {
                        break;
                    }
                } else
                    break;
            }
            totoStartHorses.remove(lid);
        }
    }*/

    public void check(RaceResultStart raceResultStart) {
        List <RaceResultHorse> voittajaHevoset = raceResultStart.getWinnerHorses();

        for(ValueHorse totoHevonen : totoHorses) {
            BigDecimal panos = BigDecimal.valueOf(-1);

            for(RaceResultHorse raceResultHorse : voittajaHevoset) {
                String totoNimi = totoHevonen.getRaceProgramHorse().getRaceHorseName();
                String tulosNimi = raceResultHorse.getRaceHorseName();

                if(totoNimi.equals(tulosNimi)) {
                    // Voitto
                    BigDecimal voittokerroin = raceResultHorse.getRaceResultWinOdds();
                    panos = panos.add(voittokerroin);

                    System.out.println(panos + "€");
                }
            }
            gameStats.add(panos);
        }
        //check(raceResultStart.getId(), raceResultStart.getRaceResultHorses());

        /*
        BetRate betRate = (BetRate)raceResultStart.getOddsMap().get(FILEID);

        if(betRate != null) {
            win = BigDecimal.ONE.negate();
            List odds = betRate.getOdds();
            //List horseList = raceResultStart.getHorseList();
            List horseList = raceResultStart.getRaceResultHorseList();

            Iterator itr = horseList.iterator();
            for(int i = 0; itr.hasNext() && i < odds.size(); i++) {
                RaceResultHorse raceResultHorse = (RaceResultHorse)itr.next();
                SubRanking ranking = raceResultHorse.getRaceResultRanking();
                BigDecimal rank = ranking.getNumber();
                String raceResultHorseName = raceResultHorse.getRaceHorseName();
                if(rank != null && rank.equals(BigDecimal.ONE) && totoHorses.contains(raceResultHorseName)) {
                    win = win.add((BigDecimal)odds.get(i));
                }
            }
        }*/
    }

    /*
    private void check(String lid, TreeSet resultHorseSet) {
        Iterator itr = resultHorseSet.iterator();
        totoHorses = (ArrayList<String>) totoStartHorses.get(lid);

        if(totoHorses != null) {
            for (int i = 0; itr.hasNext(); i++) {
                RaceResultHorse raceResultHorse = (RaceResultHorse) itr.next();
                BigDecimal rank = raceResultHorse.getRaceResultRanking().getNumber();
                BigDecimal winOdd = raceResultHorse.getRaceResultWinOdds();

                if (rank != null && winOdd != null) {
                    if (rank.equals(BigDecimal.ZERO)) {
                        System.out.print("");
                    } else if (rank.equals(BigDecimal.ONE) && !totoHorses.isEmpty()) {
                        //BigDecimal horseProgNumber = raceResultHorse.getHorseProgNumber();
                        String raceResultHorseName = raceResultHorse.getRaceHorseName();
                        if (raceResultHorseName != null) {
                            if (totoHorses.contains(raceResultHorseName)) {
                                gameStats.add(bet.add(winOdd));
                                System.out.println(bet.add(winOdd) + "�");
                            }
                            gameStats.add(bet);
                        } else {
                            System.out.println("Voittajapeli.check: horseProgNumber null");
                        }
                    } else {
                        break;
                    }
                } else
                    break;
            }
            totoStartHorses.remove(lid);
        }
    }*/

    public String toString() {
        return gameStats.toString();
    }
}
