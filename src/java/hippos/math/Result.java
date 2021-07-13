package hippos.math;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Sep 8, 2005
 * AlphaNumber: 11:06:15 PM
 * To change this template use Options | File Templates.
 */
public class Result {
    BigDecimal sum;
    int sumCount;
    int aNullCount;
    int bNullCount;
    int totalCount;

    /**
     * Construct an new empty datalist
     */
    public Result() {
        sum = new BigDecimal(0);
        sumCount = 0;
        aNullCount = 0;
        bNullCount = 0;
        totalCount = 0;
    }

    public Result(BigDecimal t1, BigDecimal t2) {
        this();
        if(t1 != null && t2 != null) {
            sum = sum.add(t1.subtract(t2));
            sumCount++;
        } else {
            if(t1 == null) aNullCount++;
            if(t2 == null) bNullCount++;
        }
        totalCount++;
    }

    /**
     * Adds new number into this datalist
     *
     * @param result an item to add
     */
    public void add(Result result) {
        sum = sum.add(result.sum);
        sumCount += result.sumCount;
        aNullCount += result.aNullCount;
        bNullCount += result.bNullCount;
        totalCount += result.totalCount;
    }

    /**
     * Calculates the average of the items of this datalist
     *
     * @param scale the accuracy of retusn sum
     * @return average of the items
     * @throws java.lang.ArithmeticException if there's no items in this list leading to divide by zero
     */
    public BigDecimal average(int scale) {
        if(sumCount > 0)
            return sum.divide(new BigDecimal(sumCount), scale, BigDecimal.ROUND_HALF_UP);
        return new BigDecimal(0);
    }

    public BigDecimal aNullAverage(int scale) {
        if(aNullCount > 0)
            return new BigDecimal(aNullCount).divide(new BigDecimal(totalCount), scale, BigDecimal.ROUND_HALF_UP);
        return new BigDecimal(0);
    }

    public BigDecimal bNullAverage(int scale) {
        if(bNullCount > 0)
            return new BigDecimal(bNullCount).divide(new BigDecimal(totalCount), scale, BigDecimal.ROUND_HALF_UP);
        return new BigDecimal(0);
    }

    public BigDecimal negate() {
        return sum.negate();
    }
}
