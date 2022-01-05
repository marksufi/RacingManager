package hippos.math;

import hippos.exception.RegressionModelException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.TreeSet;

public class TimeSet {
    private TreeSet <BigDecimal> timeSet = new TreeSet<>();

    public void add(BigDecimal time) throws RegressionModelException{
        try {
            timeSet.add(time);
        } catch (NullPointerException e) {
            // yrittää lisätä null arvon
            throw new RegressionModelException();
        } catch (Exception e) {
            // jotain muuta kummallista
            e.printStackTrace();

            throw new RegressionModelException();
        }
    }

    public double get(BigDecimal time) throws RegressionModelException {
        try {
            int i = new ArrayList<>(timeSet).indexOf(time);

            if (i >= 0) {
                double size = timeSet.size();

                double rank = size - i;

                double y = rank / size;

                y *= 100.00;

                BigDecimal by = BigDecimal.valueOf(y).setScale(2, RoundingMode.HALF_UP);

                return by.doubleValue();
            }
        } catch (ArithmeticException e) {
            //

        } catch (Exception e) {
            e.printStackTrace();

        }

        throw new RegressionModelException();

    }
}
