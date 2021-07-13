package hippos.math.betting;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Mar 30, 2006
 * Time: 11:33:29 PM
 * To change this template use Options | File Templates.
 */
public class GeneralBet {
    BigDecimal count = null;
    int requiredRanking;
    int horsesUsed = 0;

    public GeneralBet() {

    }

    public GeneralBet(int requiredRanking) {
        this.requiredRanking = requiredRanking;
        this.count = new BigDecimal(0);
    }

    public void check(int ranking, int myRanking) {
        if(myRanking <= requiredRanking) {
            horsesUsed = ranking;
        }
    }

    public String toString() {
        return null;
    }
}
