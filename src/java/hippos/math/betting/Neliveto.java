package hippos.math.betting;

import java.math.BigDecimal;
import java.util.List;
import java.util.Iterator;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Apr 18, 2006
 * Time: 11:27:00 PM
 * To change this template use Options | File Templates.
 */
public class Neliveto extends Game {

    public Neliveto(List odds) {
        super("Neliveto", 4, odds);
    }

    public Neliveto(String title, int rankingLimit, List odds) {
        super(title, rankingLimit, odds);
    }

    public Neliveto(String title, int rankingLimit, int myRankingLimit, List odds) {
        super(title, rankingLimit, myRankingLimit, odds);
    }

    public BigDecimal getLoss(int numberOfHorses) {
        return new BigDecimal(getCombinations(numberOfHorses)).setScale(0, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getWinnings(List ranking, List horsesBet, List odds) {
        if(odds != null && odds.size() > 0) {
            return (BigDecimal)odds.get(0);
        }
        return new BigDecimal("0");
    }

    public int getCombinations(int i) {
        return getPermutation(i)/getPermutation(i - 4);
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

    private int getPermutation(int x) {
        if(x > 0) {
            return x * getPermutation( x - 1);
        }
        return 1;
    }

    public static void main(String [] args) {
        Neliveto neliveto = new Neliveto(null);
        for(int i = 4; i < 10; i++) {
            System.out.println(i + ": " + neliveto.getCombinations(i));
            //System.out.println(i + ": " + troikka.getPermutation(i));
        }
    }
}
