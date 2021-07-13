package hippos.math;

import org.apache.commons.math3.dfp.DfpField;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Mar 6, 2005
 * AlphaNumber: 9:26:27 PM
 * To change this template use Options | File Templates.
 */
//public class SetValue implements DataEntry {
public class Value implements Comparable {
    private BigDecimal sum = new BigDecimal(0);;
    private int count = 0;

    public Value() {
    }

    public Value(BigDecimal data) {
        this();
        if(data != null) {
            this.sum = data;
            this.count = 1;
        }
    }


    public Value(BigDecimal sum, int count) {
        this.sum = sum;
        this.count = count;
    }

    public Value(Value value) {
        if(value != null) {
            this.sum = value.sum;
            this.count = value.count;
        }
    }

    public Value(double initValue) {
        add(initValue);
    }

    public Value add(BigDecimal entry) {
        if(entry != null) {
            sum = sum.add(entry);
            count ++;
        }
        return this;
    }

    public void add(double v) {
        if(!Double.isInfinite(v) && !Double.isNaN(v)) {
            add(new BigDecimal(v));
        }
    }

    public void add(BigDecimal entry, int count) {
        if(entry != null) {
            sum = sum.add(entry);
            this.count += count;
        }
    }

    public Value getUnionValue(Value value2) {

        BigDecimal avgA = this.average(2, null);
        BigDecimal avgB = value2.average(2, null);
        BigDecimal countA = new BigDecimal(this.count);
        BigDecimal countB = new BigDecimal(value2.count);
        BigDecimal minCount = new BigDecimal(Math.min(countA.intValue(), countB.intValue()));

        BigDecimal sum = avgA.subtract(avgB);
        sum = sum.multiply(minCount);

        Value unionValue = new Value(sum, minCount.intValue());

        return unionValue;
    }

    public BigDecimal average() {
        try {
            return sum.divide(BigDecimal.valueOf(count), 10, RoundingMode.HALF_UP);
        } catch (Exception e) {
            throw e;
        }
    }

    public BigDecimal procents(int scale) {
        try {
            BigDecimal avg = average();
            avg = avg.multiply(BigDecimal.valueOf(100.00));
            return avg.setScale(scale, RoundingMode.HALF_UP);
        } catch (ArithmeticException e) {
            return BigDecimal.ZERO;
        } catch (Exception e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal average(int scale) {
        try {
            return sum.divide(BigDecimal.valueOf(count), scale, RoundingMode.HALF_UP);
        } catch (Exception e) {
            throw e;
        }
    }

    public BigDecimal average(int scale, BigDecimal returnIfEmpty) {
        if(count > 0)
            return sum.divide(BigDecimal.valueOf(count), scale, RoundingMode.HALF_UP);
        return returnIfEmpty;
    }

    public Value subtract(Value v) {
        sum = sum.subtract(v.sum);
        count+=v.count;
        return this;
    }

    public Value subtract(BigDecimal v) {
        sum = sum.subtract(v);
        count++;
        return this;
    }

    public Value remove(BigDecimal v) {
        sum = sum.subtract(v);
        count--;
        return this;
    }

    public String toString() {
        BigDecimal avg = average(2, BigDecimal.ZERO);

        return avg.toString() + "(" + count + ")";
    }

    public Value negate() {
        return new Value(sum.negate(), count);
    }

    public BigDecimal procents(int limit, int scale) {
        BigDecimal procents = new BigDecimal(1);
        if(count < limit) {
            procents = new BigDecimal((double)count / (double)limit);
        }
        //return average(4).multiply(new BigDecimal("100.00")).setScale(i, BigDecimal.ROUND_HALF_UP);

        BigDecimal avg = average(scale);
        if(avg != null)
            return average(scale).multiply(procents).setScale(scale, BigDecimal.ROUND_HALF_UP);
        return null;
    }

    public int size() {
        return count;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public int getCount() {
        return count;
    }

    public BigDecimal getBigDecimalCount() {
        return BigDecimal.valueOf(count);
    }

    public BigDecimal multiply(Value aValue) {
        return average(2, BigDecimal.ZERO).multiply(aValue.average(2, BigDecimal.ZERO));
    }

    @Override
    public int compareTo(Object o) {
        Value aValue = (Value)o;

        BigDecimal avg1 = average(2, null);
        BigDecimal avg2 = aValue.average(2, null);

        if(avg1 == null)
            return -1;

        if(avg2 == null)
            return 1;

        int cmp = avg1.compareTo(avg2);

        return cmp != 0 ? cmp : 1;
    }

    public void add(Value value) {
        sum = sum.add(value.getSum());
        count += value.getCount();
    }

    public boolean isEmpty() {
        return count > 0 ? false : true;
    }

    public BigDecimal divide(Value aValue) {
        try {
            BigDecimal avg = average(2);
            BigDecimal aAvg = aValue.average(2);

            return avg.divide(aAvg, 2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            throw e;
        }
    }

    public BigDecimal divide(Value aValue, int scale) {
        try {
            BigDecimal avg = average(scale);
            BigDecimal aAvg = aValue.average(scale);

            return avg.divide(aAvg, scale, RoundingMode.HALF_UP);
        } catch (Exception e) {
            throw e;
        }
    }

    public BigDecimal divide(Value aValue, int scale, BigDecimal returnIfZero) {
        try {
            if(aValue.count == 0)
                return returnIfZero;

            BigDecimal avg = average(scale);
            BigDecimal aAvg = aValue.average(scale, returnIfZero);

            return avg.divide(aAvg, scale, RoundingMode.HALF_UP);
        } catch (Exception e) {
            throw e;
        }
    }

}
