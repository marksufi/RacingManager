package hippos.lang.toto;

import hippos.math.DecimalValue;
import hippos.math.Value;

import java.math.BigDecimal;

/**
 * Created by Markus on 26.9.2016.
 */
public class TotoGameStats {
    Value hits = new Value();
    Value profit = new Value();

    public void add(BigDecimal win) {
        if(win != null) {
            profit.add(win);
            hits.add(win.compareTo(BigDecimal.ZERO) > 0 ? BigDecimal.ONE : BigDecimal.ZERO);
        }
    }

    public Value getHits() {
        return hits;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("Hits:   " + hits.procents(2) + "%\t");
        sb.append("Profit: " + profit.getSum() + "" + "/" + profit.getCount() + " = " + profit.average(2, BigDecimal.ZERO) + "€/startti");

        return sb.toString();
    }

    public BigDecimal getWinProcents() {
        try {
            BigDecimal wp = hits.average(4, BigDecimal.ZERO).multiply(BigDecimal.valueOf(100.00)).setScale(2);

            return wp;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return BigDecimal.ZERO;
    }
}
