package hippos.math;

import java.math.BigDecimal;

/**
 * Created by Markus on 4.12.2015.
 */
public class StatAvg {
    private BigDecimal sum = BigDecimal.ZERO;
    private BigDecimal count = BigDecimal.ZERO;

    public StatAvg() {
    }

    public StatAvg(BigDecimal sum, BigDecimal count) {
        if (sum != null && count != null) {
            this.sum = sum;
            this.count = count;
        }
    }

    public void add(BigDecimal sum) {
        if(sum != null) {
            this.sum = this.sum.add(sum);
            this.count = this.count.add(BigDecimal.ONE);
        }
    }

    public void add(BigDecimal sum, BigDecimal count) {
        if(sum != null && count != null) {
            this.sum = this.sum.add(sum);
            this.count = this.count.add(count);
        }
    }

    public void remove(BigDecimal sum) {
        if(sum != null) {
            this.sum = this.sum.subtract(sum);
            this.count = this.count.subtract(BigDecimal.ONE);
        }
    }

    public void remove(BigDecimal sum, BigDecimal count) {
        if(sum != null && count != null) {
            this.sum = this.sum.subtract(sum);
            this.count = this.count.subtract(count);
        }
    }

    public BigDecimal getAvg(int scale, BigDecimal nullvalue) {
        if(count.compareTo(BigDecimal.ZERO) > 0)
            return sum.divide(count, scale, BigDecimal.ROUND_HALF_UP);
        return nullvalue;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public BigDecimal getCount() {
        return count;
    }


}
