package hippos.math;

import org.apache.commons.math3.dfp.DfpField;
import utils.Log;

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
    private BigDecimal sum = BigDecimal.ZERO;
    private BigDecimal count = BigDecimal.ZERO;

    public Value() {
    }

    public Value(BigDecimal data) {
        this();
        if(data != null) {
            this.sum = data;
            this.count = BigDecimal.ONE;
        }
    }

    public Value(BigDecimal sum, int count) {
        this.sum = sum;
        this.count = BigDecimal.valueOf(count);
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

    public Value(BigDecimal sum, BigDecimal count) {
        this.sum = sum;
        this.count = count;
    }

    public Value add(BigDecimal entry) {
        try {
            if(entry != null) {
                sum = sum.add(entry);
                count = count.add(BigDecimal.ONE);
            }
        } catch (Exception e) {
            Log.write(e);
        }
        return this;
    }

    public void add(double v) {
        try {
            add(BigDecimal.valueOf(v));
        } catch (Exception e) {
            Log.write(e);
        }
    }

    public void add(double [] v) {
        try {
            add(v[0], v[1]);
        } catch (Exception e) {
            Log.write(e);
        }
    }

    public void add(double entry, double count) {
        try {
            sum = sum.add(BigDecimal.valueOf(entry).multiply(BigDecimal.valueOf(count)));
            this.count = this.count.add(BigDecimal.valueOf(count));
        } catch (NumberFormatException e) {
            // entry: NaN
        } catch(Exception e) {
            Log.write(e);
        }
    }

    public Value getUnionValue(Value value2) {

        BigDecimal avgA = this.average(2, null);
        BigDecimal avgB = value2.average(2, null);
        BigDecimal countA = this.count;
        BigDecimal countB = value2.count;
        BigDecimal minCount = new BigDecimal(Math.min(countA.intValue(), countB.intValue()));

        BigDecimal sum = avgA.subtract(avgB);
        sum = sum.multiply(minCount);

        Value unionValue = new Value(sum, minCount.intValue());

        return unionValue;
    }

    public BigDecimal average() {
        try {
            return sum.divide(count, 10, RoundingMode.HALF_UP);
        } catch (ArithmeticException e) {
            throw e;
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
            return sum.divide(count, scale, RoundingMode.HALF_UP);
        } catch (Exception e) {
            throw e;
        }
    }

    public BigDecimal average(int scale, BigDecimal returnIfEmpty) {

        try {
            return sum.divide(count, scale, RoundingMode.HALF_UP);

        } catch (ArithmeticException e) {
            // Jako nollalla
        } catch(Exception e) {
            Log.write(e);
        }
        return returnIfEmpty;
    }

    public Value subtract(Value v) {
        sum = sum.subtract(v.sum);
        count = count.add(v.count);

        return this;
    }

    public Value subtract(BigDecimal v) {
        sum = sum.subtract(v);
        count = count.subtract(BigDecimal.ONE);
        return this;
    }

    public Value remove(BigDecimal v) {
        sum = sum.subtract(v);
        count = count.subtract(BigDecimal.ONE);
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
        if(count.compareTo(BigDecimal.valueOf(limit)) < 0) {
            procents = count.divide(BigDecimal.valueOf(limit), scale, RoundingMode.HALF_UP);
        }
        //return average(4).multiply(new BigDecimal("100.00")).setScale(i, BigDecimal.ROUND_HALF_UP);

        BigDecimal avg = average(scale);
        if(avg != null)
            return average(scale).multiply(procents).setScale(scale, RoundingMode.HALF_UP);
        return null;
    }

    public BigDecimal size() {
        return count;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public BigDecimal getCount() {
        return count;
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
        count = count.add(value.getCount());
    }

    public boolean isEmpty() {
        return count.compareTo(BigDecimal.ZERO) > 0 ? false : true;
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
            if(aValue.count.equals(BigDecimal.ZERO))
                return returnIfZero;

            BigDecimal avg = average(scale);
            BigDecimal aAvg = aValue.average(scale, returnIfZero);

            return avg.divide(aAvg, scale, RoundingMode.HALF_UP);
        } catch (Exception e) {
            throw e;
        }
    }

}
