package hippos.math.betting;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Apr 18, 2006
 * Time: 10:40:59 PM
 * To change this template use Options | File Templates.
 */
public class Kaksari extends Game {
    public static final int RANKINGLIMIT = 2;
    public static final String DEFAULTNAME = "Kaksari";

    private List winners = new ArrayList();
    private List seconds = new ArrayList();

    public BigDecimal bet = BigDecimal.ONE;


    public Kaksari(List odds) {
        this(DEFAULTNAME, RANKINGLIMIT, 2, odds);
    }

    public Kaksari(String title, int rankingLimit, List odds) {
        super(title, rankingLimit, odds);
    }

    public Kaksari(String title, int rankingLimit, int myRankingLimit, List odds) {
        super(title, rankingLimit, myRankingLimit, odds);
    }

    public Kaksari(List odds, BigDecimal bet) {
        this(odds);
        this.bet = bet;
    }

    public Kaksari(String title, List odds, BigDecimal bet) {
        super(title, RANKINGLIMIT, odds);
        this.bet = bet;
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
                if(seconds.size() > 0) {
                    if(winners.contains(ranking.get(0)) && seconds.contains(ranking.get(1))) {
                        winnings = ((BigDecimal)odds.get(0)).multiply(bet);
                    } else if(winners.contains(ranking.get(1)) && seconds.contains(ranking.get(0))) {
                        winnings = ((BigDecimal)odds.get(0)).multiply(bet);
                    }
                } else if(winners.contains(ranking.get(0)) && winners.contains(ranking.get(1))) {
                    winnings = ((BigDecimal)odds.get(0)).multiply(bet);
                }
                horsesBet = new Vector(winners);
            }
        }
    }

    public BigDecimal getLoss(int numberOfHorses) {
        return new BigDecimal(getCombinations(numberOfHorses)).setScale(0, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getLoss() {
        int combinations = 0;
        if(seconds.size() > 0) {
            combinations = winners.size() * seconds.size();
        } else {
            for(int i = 1; i < winners.size(); i++) {
                combinations += i;
            }
        }
        return BigDecimal.valueOf(combinations).multiply(bet);
    }

    public BigDecimal getWinnings(List ranking, List horsesBet, List odds) {
        if(odds != null && odds.size() > 0) {
            return ((BigDecimal)odds.get(0)).multiply(bet);
        }
        return new BigDecimal("0");
    }


    public List getHorsesToWin(List ranking, List myRanking) {
        Iterator itr = myRanking.iterator();
        List horsesToWin = new Vector();
        int qualifiedHorses = 0;
        while(itr.hasNext()) {
            BigDecimal myNumber = (BigDecimal)itr.next();
            int index = ranking.indexOf(myNumber);
            if(index >= 0) {
                horsesToWin.add(myNumber);
                if(index < rankingLimit) {
                    if(++qualifiedHorses >= rankingLimit) {
                        if(horsesToWin.size() >= horsesBetLimit) {
                            return horsesToWin;
                        }
                    }
                }
            }
        }
        return null;
    }

    public int getCombinations(int i) {
        int combinations = 0;
        while(i-- > 0) {
            combinations += i;
        }
        return combinations;
    }

    public void addWinner(List winners) {
        this.winners.addAll(winners);
    }

    public void addWinner(BigDecimal winner) {
        this.winners.add(winner);
    }

    public void addSecond(List seconds) {
        this.seconds.addAll(seconds);
    }

    public void addSecond(BigDecimal second) {
        this.seconds.add(second);
    }

    public static void main(String [] args) {
        Kaksari kaksari = new Kaksari(null);
        for(int i = 2; i < 10; i++) {
            System.out.println(i + ": " + kaksari.getCombinations(i));
        }
    }
}
