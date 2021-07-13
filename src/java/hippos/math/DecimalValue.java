package hippos.math;

import hippos.HarnessApp;
import utils.Log;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by marktolo on 3.2.2015.
 */
public class DecimalValue implements Comparable {
    private BigDecimal sum = BigDecimal.ZERO;
    private BigDecimal count = BigDecimal.ZERO;
    private Comparable compareBehaviour = new AvgCompareMethod(this);
    private TreeSet sumSet = new TreeSet();

    public DecimalValue() {
    }

    public DecimalValue(DecimalValue decimalValue) {
        this.sum = decimalValue.getSum();
        this.count = decimalValue.getCount();
    }

    public DecimalValue(BigDecimal sum, BigDecimal count) {
        if (sum != null && count != null) {
            //this.sum = sum.multiply(count);
            this.sum = sum;
            this.count = count;
        }
    }

    public DecimalValue(BigDecimal sum) {
        if (sum != null) {
            this.sum = sum;
            this.count = BigDecimal.ONE;
        }
    }

    public DecimalValue(double entry) {
        try {
            if (!Double.isNaN(entry)) {
                add(BigDecimal.valueOf(entry));
            }
        } catch (Exception e) {
            Log.write(e, Double.toString(entry));
        }
    }

    public DecimalValue(double[] entry) {
        this(BigDecimal.valueOf(entry[0]), BigDecimal.valueOf(entry[1]));
    }

    public void add(BigDecimal entry) {
        add(entry, BigDecimal.ONE);
    }

    public BigDecimal getMin() {
        return !sumSet.isEmpty() ? (BigDecimal)sumSet.first() : null;
    }

    public BigDecimal getMax() {
        return !sumSet.isEmpty() ? (BigDecimal)sumSet.last() : null;
    }

    public void add(double entry) {
        add(!Double.isNaN(entry) ? BigDecimal.valueOf(entry) : null, BigDecimal.ONE);
    }

    public void add(BigDecimal sum, BigDecimal count) {
        if (sum != null && count != null) {
            this.sum = this.sum.add(sum.multiply(count));
            this.count = this.count.add(count);


            sumSet.add(sum);
        }
    }

    public void add(DecimalValue decimalValue) {
        if(decimalValue.sum != null && decimalValue.count != null) {
            this.sum = this.sum.add(decimalValue.sum);
            this.count = this.count.add(decimalValue.count);
        }
    }

    public void subtract(DecimalValue decimalValue) {
        if(decimalValue.sum != null && decimalValue.count != null) {
            this.sum = this.sum.subtract(decimalValue.sum);
            this.count = this.count.subtract(decimalValue.count);
        }
    }

    public void subtract(BigDecimal toBeRemoved) {
        sum = sum.subtract(toBeRemoved);
        count = count.subtract(BigDecimal.ONE);
    }

    public DecimalValue reduce(BigDecimal reduceAmount) {
        DecimalValue reduceDecimalValue = new DecimalValue();
        if(reduceAmount != null) {
            reduceDecimalValue.add(this.getAverage(BigDecimal.ZERO, 2), reduceAmount);
            this.sum = this.sum.subtract(this.getAverage(BigDecimal.ZERO, 2).multiply(reduceAmount));
            this.count = this.count.subtract(reduceAmount);
        }
        return reduceDecimalValue;
    }

    public void add(double entry, double count) {
        BigDecimal e = !Double.isNaN(entry) ? BigDecimal.valueOf(entry) : null;
        BigDecimal c = !Double.isNaN(count) ? BigDecimal.valueOf(count) : null;
        add(e, c);
    }

    public int compareTo(Object o) {
        return compareBehaviour.compareTo(o);
    }

    public boolean isEmpty() {
        return count.compareTo(BigDecimal.ZERO) > 0 ? false : true;
    }

    public BigDecimal getAverage(BigDecimal returnIfCountZero, int scale) {
        if(count.compareTo(BigDecimal.ZERO) > 0 && sum != null) {
            return sum.divide(count, scale, BigDecimal.ROUND_HALF_UP);
        }
        return returnIfCountZero;
    }

    public String toString(){
        StringBuffer sb = new StringBuffer();
        try {
            if(sum != null && count != null) {
                double s = sum.doubleValue();
                double c = count.doubleValue();
                if(c > 0.0) {
                    s /= c;
                    //sb.append(BigDecimal.valueOf(s).setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "/" + count.setScale(2, BigDecimal.ROUND_HALF_UP));
                    sb.append(BigDecimal.valueOf(s).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public BigDecimal getSum() { return sum; }
    public BigDecimal getCount() { return count; }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }

    public void setCount(BigDecimal count) {
        this.count = count;
    }

    public DecimalValue reduceCount(BigDecimal reduceAmount) {
        this.count = this.count.subtract(reduceAmount);

        if(this.count.compareTo(BigDecimal.ZERO) < 0)
            this.count = BigDecimal.ZERO;

        return this;
    }
    /*
    public void add(RankTime ranktime) {
        BigDecimal ranktimeA = ranktime.getRanktime();
        if(ranktimeA != null) {
            this.sum = this.sum.add(ranktimeA);
            this.count = this.count.add(BigDecimal.ONE);
        }
    }*/

    private class CountCompareMethod implements Comparable {
        DecimalValue decimalValue;

        private CountCompareMethod(DecimalValue decimalValue) {
            this.decimalValue = decimalValue;
        }

        public int compareTo(Object o) {
            DecimalValue anotherDecimalValue =(DecimalValue) o;

            if(decimalValue.count.compareTo(anotherDecimalValue.count) >= 0){
                return -1;
            }
            return 1;
        }
    }

    private class AvgCompareMethod implements Comparable {
        DecimalValue decimalValue;

        private AvgCompareMethod(DecimalValue decimalValue) {
            this.decimalValue = decimalValue;
        }

        public int compareTo(Object o) {
            DecimalValue anotherDecimalValue =(DecimalValue) o;

            if(this.hashCode() == anotherDecimalValue.hashCode())
                return 0;
            if(decimalValue.getAverage(BigDecimal.ZERO, 2).compareTo(anotherDecimalValue.getAverage(BigDecimal.ZERO, 2)) >= 0){
                return -1;
            }
            return 1;
        }
    }

}
