package hippos.math;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class SumReg {
    Value xValue = new Value();
    Value yValue = new Value();

    public SumReg() {
    }

    public SumReg(BigDecimal x, BigDecimal y) {
        add(x, y);
    }

    public void add(BigDecimal x, BigDecimal y) {
        if(x!= null && y != null) {
            xValue.add(x);
            yValue.add(y);
        } else {
            // Jompikumpi null
        }
    }

    public BigDecimal getMultiplier(BigDecimal x) {
        try {
            BigDecimal multiplier = getMultiplier();

            return x.multiply(multiplier);
            //return x.add(subtract());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private BigDecimal getMultiplier() {
        try {
            BigDecimal xAvg = xValue.average(2);
            BigDecimal yAvg = yValue.average(2);

            return yAvg.divide(xAvg, 2, RoundingMode.HALF_UP);
        } catch (ArithmeticException e) {
            // divide by zero
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public BigDecimal change(BigDecimal x) {
        try {
            BigDecimal subY = getChange();

            return x.add(subY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public BigDecimal getChange() {
        try {
            BigDecimal xAvg = xValue.average(2);
            BigDecimal yAvg = yValue.average(2);

            return yAvg.subtract(xAvg);
        } catch (ArithmeticException e) {
            // divide by zero
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int size() {
        return xValue.getCount();
    }

}
