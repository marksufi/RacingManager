package hippos.math;

import org.apache.commons.math3.distribution.NormalDistribution;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class StatisticalValue extends Value {

    List <AlphaNumber> valueList = new ArrayList<>();
    TreeSet <AlphaNumber> valueSet = new TreeSet();

    public StatisticalValue() {
    }

    public StatisticalValue(BigDecimal sum, BigDecimal count) {
        super(sum, count.intValue());
        try {
            add(sum.doubleValue());
        } catch (NullPointerException e) {
            // sum is null
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * lisää tilastoihin uuden arvon
     * @param newValue
     */
    public void add(double newValue) {
        super.add(newValue);
        try {
            valueList.add(new AlphaNumber(newValue));
            valueSet.add(new AlphaNumber(newValue));
        } catch (NullPointerException e) {
            // newValue is null
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param scale Desimaalien tarkkuus
     * @return      Palauttaa keskiarvon
     */
    private BigDecimal getAvg(int scale) {
        return super.average(scale);
    }

    /**
     * @return Palauttaa keskihajonnan
     */
    private BigDecimal getDev() {
        try {
            BigDecimal avg = getAvg(2);
            Value dev = new Value();

            for (AlphaNumber decimal : valueList) {
                BigDecimal sub = decimal.getBigDecimal().subtract(avg);
                dev.add(Math.abs(sub.doubleValue()));
            }

            return dev.average(2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /*
    BigDecimal getBiggest() {
        return valueSet.last();
    }*/

    /**
     * @return Palauttaa normaalijakauman
     */
    public NormalDistribution getNormalDistribution() {
        BigDecimal avg = getAvg(2);
        BigDecimal std = getDev();

        return new NormalDistribution(avg.doubleValue(), std.doubleValue());
    }

    public List<AlphaNumber> getValueList() {
        return valueList;
    }

    public TreeSet<AlphaNumber> getValueSet() {
        return valueSet;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getSum());
        sb.append("(" + procents(2) + "%)");
        sb.append(valueSet);

        return sb.toString();
    }

    public static void main(String args[]) {
        StatisticalValue statisticalValue = new StatisticalValue();
        statisticalValue.add(451.46);
        statisticalValue.add(-44.62);
        statisticalValue.add(879.66);
        statisticalValue.add(948.12);
        statisticalValue.add(800.14);
        statisticalValue.add(740.50);
        statisticalValue.add(610.24);
        statisticalValue.add(843.01);

        StatisticalValue statisticalValue2 = new StatisticalValue();
        statisticalValue2.add(600.65);
        statisticalValue2.add(313.01);
        statisticalValue2.add(607.88);
        statisticalValue2.add(474.04);
        statisticalValue2.add(282.28);
        statisticalValue2.add(561.93);
        //statisticalValue2.add(993.70);
        //statisticalValue2.add(1442.55);

        BigDecimal avg = statisticalValue.getAvg(2);
        BigDecimal std = statisticalValue.getDev();

        System.out.println("Distrubution.main: avg1 = " + avg);
        System.out.println("Distrubution.main: std1 = " + std);
        NormalDistribution normalDistribution = new NormalDistribution(avg.doubleValue(), std.doubleValue());

        System.out.println("Distrubution.main: ndb1 = " + normalDistribution.inverseCumulativeProbability(0.95));
        System.out.println("Distrubution.main: ndb1 = " + normalDistribution.inverseCumulativeProbability(0.90));
        System.out.println("Distrubution.main: ndb1 = " + normalDistribution.inverseCumulativeProbability(0.10));
        System.out.println("Distrubution.main: ndb1 = " + normalDistribution.inverseCumulativeProbability(0.05));

        BigDecimal avg2 = statisticalValue2.getAvg(2);
        BigDecimal std2 = statisticalValue2.getDev();

        System.out.println("Distrubution.main: avg2 = " + avg2);
        System.out.println("Distrubution.main: std2 = " + std2);
        NormalDistribution normalDistribution2 = statisticalValue2.getNormalDistribution();

        System.out.println("Distrubution.main: ndb2 = " + normalDistribution2.inverseCumulativeProbability(0.95));
        System.out.println("Distrubution.main: ndb2 = " + normalDistribution2.inverseCumulativeProbability(0.90));
        System.out.println("Distrubution.main: ndb2 = " + normalDistribution2.inverseCumulativeProbability(0.10));
        System.out.println("Distrubution.main: ndb2 = " + normalDistribution2.inverseCumulativeProbability(0.05));

    }

    public void add(BigDecimal qtCount, BigDecimal startCount, AlphaNumber qtTime) {
        try {
            add(qtCount, startCount.intValue(), qtTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void add(BigDecimal qtCount, int startCount, AlphaNumber qtTime) {
        try {
            super.add(new Value(qtCount, startCount));
            if (qtTime.getNumber() != null) {
                valueList.add(qtTime);
                valueSet.add(qtTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void add(StatisticalValue statisticalValue, AlphaNumber qtTime) {
        try {
            super.add(statisticalValue);

            if (qtTime.getNumber() != null) {
                valueList.add(qtTime);
                valueSet.add(qtTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
