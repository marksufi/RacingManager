package hippos.math.betting;

import hippos.math.betting.Game;

import java.math.BigDecimal;
import java.util.List;
import java.util.Iterator;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Apr 18, 2006
 * Time: 11:38:03 PM
 * To change this template use Options | File Templates.
 */
public class Sija extends Game {
    public static final int RANKINGLIMIT = 3;
    public static final String DEFAULTNAME = "Sija";
    public BigDecimal bet = BigDecimal.ONE;

    public Sija(List odds) {
        super(DEFAULTNAME, RANKINGLIMIT, 3, odds);
    }

    public Sija(List odds, BigDecimal bet) {
        this(odds);
        this.bet = bet;
    }

    public List getHorsesToWin(List ranking, List myRanking) {
        Iterator itr = myRanking.iterator();
        List horsesToWin = new Vector();

        while(itr.hasNext() && horsesToWin.size() < horsesBetLimit) {
            BigDecimal myNumber = (BigDecimal)itr.next();
            int index = ranking.indexOf(myNumber);
            if(index >= 0) {
                horsesToWin.add(myNumber);
            }
        }
        return horsesToWin;
    }

    public BigDecimal getLoss(int numberOfHorses) {
        return new BigDecimal(numberOfHorses).multiply(bet);
    }

    public BigDecimal getWinnings(List ranking, List horsesBet, List odds) {
        Iterator itr = horsesBet.iterator();
        BigDecimal winnings = new BigDecimal("0");
        BigDecimal winning;
        while(itr.hasNext()) {
            BigDecimal myNumber = (BigDecimal)itr.next();
            int rank = ranking.indexOf(myNumber);
            if(odds.size() > rank) {
                winning = (BigDecimal)odds.get(rank);
                winnings = winnings.add(winning);
            }
        }
        return winnings.multiply(bet);
    }

    public int getCombinations(int i) {
        return i;
    }
}
