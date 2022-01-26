package hippos.math.betting;

import hippos.Horse;
import hippos.RaceProgramStart;
import hippos.ValueHorse;
import hippos.io.RaceProgramFile;
import hippos.utils.StringUtils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Mar 29, 2006
 * Time: 2:46:13 AM
 * To change this template use Options | File Templates.
 */
public class GameFactory {
    public static final String LOCALITY = "GENERAL";
    //private Map horses;
    private List V_Horses = new ArrayList();
    private List A_Horses = new ArrayList();
    private List B_Horses = new ArrayList();
    private List C_Horses = new ArrayList();
    private List All_Horses = new ArrayList();

    private List betRates;
    private BigDecimal bet = BigDecimal.ONE;

    /*public GameFactory(Map horses) {
        this.horses = horses;
    }*/

    public GameFactory(RaceProgramStart raceProgramStart) {
        //this(raceProgramStart.getValueHorses());
        selectWinners(raceProgramStart.getSortedHorseList());
    }

    public GameFactory(RaceProgramStart raceProgramStart, Connection conn) {
        this(raceProgramStart);
        //toBettingsString(conn);
    }

    public static String toBettingsString(Connection conn) {
        return toBettingsString(conn, LOCALITY);
    }

    public static String toBettingsString(Connection conn, String locality) {
        StringBuffer sb = new StringBuffer();
        sb.append("\n\nKERTOIMET " + locality + "\n\n");

        if(locality != null) {
            PreparedStatement stmt = null;
            ResultSet game = null;
            try {
                String bind = "%_" + locality;
                stmt = conn.prepareStatement("select * from GAME where TITLE like ?");
                stmt.setString(1, bind);
                game = stmt.executeQuery();
                while(game.next()) {
                    String title = game.getString("TITLE");
                    BigDecimal count = game.getBigDecimal("COUNT");
                    BigDecimal hits = game.getBigDecimal("HITS");
                    BigDecimal winnings = game.getBigDecimal("WINNINGS");
                    BigDecimal losses = game.getBigDecimal("LOSSES");
                    BigDecimal horses = game.getBigDecimal("HORSES");

                    title = title.substring(0, title.lastIndexOf("_"));

                    if(count.compareTo(BigDecimal.ZERO) > 0) {
                        hits = hits.divide(count, 2, BigDecimal.ROUND_HALF_UP);
                        horses = horses.divide(count, 2, BigDecimal.ROUND_HALF_UP);
                    }
                    winnings = winnings.subtract(losses);

                    sb.append(StringUtils.toColumn(title, 15) + ": ");
                    sb.append(hits + " % \t");
                    sb.append(winnings + "\t");
                    sb.append(horses + "\n");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {game.close(); } catch(Exception e) {}
                try {stmt.close(); } catch(Exception e) {}
            }
        }
        return sb.toString();
    }

    public GameFactory(RaceProgramFile raceProgramFile) {
        this(raceProgramFile.getRaceProgramStart());
    }

    public GameFactory(RaceProgramFile raceProgramFile, List betRates) {
        this(raceProgramFile);
        this.betRates = betRates;
    }

    public static Game create(String name, List odds) {
        if(name != null) {
            if(name.toLowerCase().equals("voittaja")) return new Voittaja(odds);
            if(name.toLowerCase().equals("sija")) return new Sija(odds);
            if(name.toLowerCase().equals("kaksari")) return new Kaksari(odds);
            if(name.toLowerCase().equals("troikka")) return new Troikka(odds);
        }
        return null;
    }

    public List createGames(String name, List odds) {
        List games = new ArrayList();
        if(name != null) {
            if(name.toLowerCase().equals("voittaja") && A_Horses.size() > 0) {
                Voittaja v1 = new Voittaja("Voittaja", odds, bet);
                v1.add((BigDecimal)All_Horses.get(0));
                games.add(v1);

                Voittaja v2 = new Voittaja("V-Voittajat", odds, bet);
                v2.add(A_Horses.size() > 0 ? A_Horses : B_Horses.size() > 0 ? B_Horses : C_Horses);
                games.add(v2);
            }
            if(name.toLowerCase().equals("sija")) {
                //games.add(new Sija(odds, bet));
            }
            if(name.toLowerCase().equals("kaksari") && A_Horses.size() > 0) {
                Kaksari k1 = new Kaksari(odds, bet);
                k1.addWinner((BigDecimal) A_Horses.get(0));
                if(A_Horses.size() > 1) {
                    k1.addSecond((BigDecimal) A_Horses.get(1));
                } else if(B_Horses.size() > 0) {
                    k1.addSecond((BigDecimal) B_Horses.get(0));
                }
                games.add(k1);

                Kaksari k2 = new Kaksari("Kaksari_B", odds, bet);
                switch( A_Horses.size()) {
                    case 1: k2.addWinner( (BigDecimal) A_Horses.get(0));
                            k2.addSecond( B_Horses );
                            if( B_Horses.size() == 0 ) {
                                k2.addSecond( C_Horses );
                            }
                            break;
                    case 2: k2.addWinner( A_Horses );
                            break;
                    case 3: k2.addWinner( A_Horses );
                            break;
                    default:k2.addWinner( (BigDecimal) A_Horses.get(0));
                            k2.addSecond( A_Horses.subList(1, A_Horses.size()));
                }
                games.add(k2);

                Kaksari k3 = new Kaksari("Kaksari_3", odds, bet);
                if(All_Horses.size() > 2) {
                    k3.addWinner(All_Horses.subList(0, 3));
                    games.add(k3);
                }
            }
            if(name.toLowerCase().equals("troikka")) {
                //return new Troikka(odds);
            }
        }
        return games;
    }


    public void createGames() {
        for(int i = 0; i < betRates.size(); i++) {
            BetRate betRate = (BetRate)betRates.get(i);
            String name = betRate.name;
            List odds = betRate.odds;
            List games = createGames(name, odds);
            for (int j = 0; j < games.size(); j++) {
                ((BetRate)betRates.get(i)).addGame((Game) games.get(j));
            }
        }
    }

    public void selectWinners(List valueHorses) {
        if(valueHorses != null && !valueHorses.isEmpty()) {
            BigDecimal Amin = null;
            BigDecimal Bmin = null;
            BigDecimal Cmin = null;
            Iterator itr = valueHorses.iterator();
            while(itr.hasNext()) {
                ValueHorse horse = (ValueHorse)itr.next();

                //List valueList = horse.getValueListA();

                //BigDecimal listMax = horse.getPowerValue();
                //BigDecimal listAvg = HorsesHelper.getAverage(valueList);
                //BigDecimal listMax = horse.getMaxValue().average(2, BigDecimal.ZERO);
                //BigDecimal listAvg = horse.getMinValue().average(2, BigDecimal.ZERO);
                /*
                if(listMax != null && listAvg!= null) {
                    //BigDecimal avg = ( listMax.add(listAvg)).divide(BigDecimal.valueOf(2.0), 2, BigDecimal.ROUND_HALF_UP);
                    BigDecimal avg = listAvg;

                    if(Amin == null || avg.compareTo(Amin) > 0) {
                        Amin = avg;
                    }

                    if(listMax != null && listMax.compareTo(Amin) >= 0) {
                        this.A_Horses.add(horse.getRaceProgramHorse().getHorseProgNumber());
                        this.V_Horses.add(horse.getRaceProgramHorse().getHorseProgNumber());
                    } else if(Bmin == null || listMax.compareTo(Bmin) >= 0) {
                        this.B_Horses.add(horse.getRaceProgramHorse().getHorseProgNumber());
                        if(Bmin == null || avg.compareTo(Bmin) > 0){
                            Bmin = avg;
                        }
                    } else if(Cmin == null || listMax.compareTo(Cmin) >= 0) {
                        this.C_Horses.add(horse.getRaceProgramHorse().getHorseProgNumber());
                        if(Cmin == null || avg.compareTo(Cmin) > 0){
                            Cmin = avg;
                        }
                    }

                    this.All_Horses.add(horse.getRaceProgramHorse().getHorseProgNumber());
                } /*else {
                    System.out.print("");
                }*/
            }
        }
    }

    public void updateBettings(Connection conn) throws SQLException {
        Iterator j = betRates.iterator();
        while(j.hasNext()) {
            BetRate betRate = (BetRate)j.next();
            try{
                betRate.update(conn, LOCALITY);
            } catch(SQLException e) {
                betRate.insert(conn, LOCALITY);
            }
        }
    }

    public void updateBettings(Connection conn, String locality) throws SQLException {
        Iterator j = betRates.iterator();
        while(j.hasNext()) {
            BetRate betRate = (BetRate)j.next();
            try{
                betRate.update(conn, locality);
            } catch(SQLException e) {
                betRate.insert(conn, locality);
            }
        }
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

    public void checkBettings(List valueHorses, List resultHorses) {
        List valueHorseNumberList = getHorseNumbers(valueHorses);
        List resultHorseNumberList = getHorseNumbers(resultHorses);

        for (int i = 0; i < betRates.size(); i++) {
            ((BetRate)betRates.get(i)).check(resultHorseNumberList, valueHorseNumberList);
        }
    }

    public List getBetRates() {
        return betRates;
    }

    public  String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("V-peli: " + V_Horses + "\n");
        sb.append("Toto:   " + A_Horses + " / " + B_Horses + " / " + C_Horses);
        return sb.toString();
    }

    public Game getGame(String gameName) {
        Game game = null;
        if(betRates !=null) {
            for (int i = 0; i < betRates.size(); i++) {
                BetRate betRate = (BetRate)betRates.get(i);
                if((game = betRate.getGame(gameName)) != null) {
                    break;
                }
            }
        }
        return game;

    }
}
