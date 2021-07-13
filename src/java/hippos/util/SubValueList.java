package hippos.util;

import hippos.math.SubValue;
import hippos.math.Value;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.math.BigDecimal;
import java.util.*;

public class SubValueList extends Value {
    private ArrayList <SubValue> subValueList = new ArrayList();
    private TreeSet<SubValue> subValueSet = new TreeSet();
    private int laukattomat = 0;

    public Value getValue() {
        return this;
    }

    public void add(SubValue subValue) {
        super.add(subValue.getValue().average(2, null));
        subValueList.add(subValue);
        subValueSet.add(subValue);

        if(subValue.onLaukaton()) {
            laukattomat++;
        }

    }

    /*
    public BigDecimal getOptimalAvg(int scale) {
        int i = 0;
        Value value = new Value();
        for(SubValue subValue : subValueSet) {
            value.add(subValue.getValue());

            if(i++ >= laukattomat)
                break;
        }
        return value.average(scale);
    }*/


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

            for (SubValue subValue : subValueList) {
                BigDecimal sub = subValue.getValue().average(2).subtract(avg);
                dev.add(Math.abs(sub.doubleValue()));
            }

            return dev.average(2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public int getLaukattomat() {
        return laukattomat;
    }

    /**
     * @return Palauttaa normaalijakauman
     */
    public NormalDistribution getNormalDistribution() {
        BigDecimal avg = getAvg(2);
        BigDecimal std = getDev();

        return new NormalDistribution(avg.doubleValue(), std.doubleValue());
    }


    public ArrayList<SubValue> getSubValueList() {
        return subValueList;
    }

    public TreeSet<SubValue> getSubValueSet() {
        return subValueSet;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("(" + super.toString() + ")");
        sb.append(Arrays.asList(subValueList.toArray()).toString());

        return sb.toString();
    }

}
