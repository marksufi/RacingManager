package hippos.math.betting;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Apr 14, 2006
 * Time: 12:06:30 AM
 * To change this template use Options | File Templates.
 */
public class Voittaja extends Game {
    public static final int RANKINGLIMIT = 1;
    public static final String DEFAULTNAME = "Voittaja";

    private List winners = new ArrayList();
    public BigDecimal bet = BigDecimal.ONE;

    public Voittaja(List odds) {
        this(DEFAULTNAME, RANKINGLIMIT, 1, odds);
    }

    public Voittaja(String title, List odds) {
        this(title, RANKINGLIMIT, 1, odds);
    }

    public Voittaja(String title, int rankingLimit, List odds) {
        super(title, rankingLimit, odds);
    }

    public Voittaja(String title, int rankingLimit, int myRankingLimit, List odds) {
        super(title, rankingLimit, myRankingLimit, odds);
    }

    public Voittaja(String title, List odds, BigDecimal panos) {
        this(title, odds);
        this.bet = panos;
    }

    public Voittaja(String databaseId) {
        super(databaseId);
    }

    public Voittaja(String title, String databaseFieldName) {
        super(title, databaseFieldName);
    }

    public List getHorsesToWin(List ranking, List myRanking) {
        Iterator itr = myRanking.iterator();
        List horsesToWin = new Vector();
        while(itr.hasNext()) {
            BigDecimal myNumber = (BigDecimal)itr.next();
            int index = ranking.indexOf(myNumber);
            if(index >= 0) {
                horsesToWin.add(myNumber);
                if(index < rankingLimit) {
                    if(horsesToWin.size() >= horsesBetLimit) {
                        return horsesToWin;
                    }
                }
            }
        }
        return null;
    }

    public BigDecimal getLoss(int numberOfHorses) {
        return new BigDecimal(winners.size()).multiply(bet);
        //return new BigDecimal(numberOfHorses);
    }

    public BigDecimal getLoss() {
        return new BigDecimal(winners.size()).multiply(bet);
    }

    public BigDecimal getWinnings(List ranking, List horsesBet, List odds) {
        if(odds != null && odds.size() > 0) {
            return ((BigDecimal)odds.get(0)).multiply(bet);
        }
        return new BigDecimal("0");
    }

    public int getCombinations(int i) {
        return i;
    }

    public void add(List winners) {
        this.winners.addAll(winners);
    }

    /**
     * Tarkistaa pelin ja laskee menot ja tulot
     *
     * @param ranking   Lähdön tulojärjestys
     * @param myRanking Omat pelihevoset järjestyksessä
     */
    public void check(List ranking, List myRanking) {
        if(odds!= null && odds.size() > 0) {
            losses = getLoss();
            if(losses.compareTo(BigDecimal.ZERO) > 0) {
                count = BigDecimal.ONE;
                if(winners.contains(ranking.get(0))) {
                    winnings = ((BigDecimal)odds.get(0)).multiply(bet);
                }
                horsesBet = new Vector(winners);
            }
        }
    }

    public void add(BigDecimal raceProgNuber) {
        winners.add(raceProgNuber);
    }

    /*
    public String toString() {
        StringBuffer sb = new StringBuffer();
        if(!winners.isEmpty()) {
            sb.append("V: ");
            sb.append(winners.toString());
            sb.append("\n");
        }
        return sb.toString();
    }*/

}
