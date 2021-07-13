package hippos.math;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by marktolo on 9.9.2014.
 */
public class DecimalValueList {
    private DecimalValue decimalValue;
    private int maxSize = 0;
    private BigDecimal reduceAmount = BigDecimal.ZERO;
    boolean useSmoothEdge = false;
    private List entryList = new ArrayList<DecimalValue>();
    //private int valid = 0;

    public DecimalValueList() {
        decimalValue = new DecimalValue();
    }

    public DecimalValueList(int maxSize) {
        this();
        if(maxSize > 0) {
            this.maxSize = maxSize;
            this.reduceAmount = BigDecimal.ONE.divide(BigDecimal.valueOf(maxSize), 2, BigDecimal.ROUND_HALF_DOWN);
        }
    }

    public DecimalValueList(int maxSize, boolean useSmoothEdge) {
        this(maxSize);
        this.useSmoothEdge = useSmoothEdge;
    }

    public DecimalValueList(BigDecimal entry) {
        this();
        this.decimalValue.add(entry, BigDecimal.ONE);
    }

    public DecimalValueList(BigDecimal entry, BigDecimal count) {
        this.decimalValue = new DecimalValue(entry, count);
        this.entryList.add(decimalValue);
    }

    public DecimalValueList(DecimalValueList decimalValueList) {
        this.decimalValue = decimalValueList.getDecimalValue();
        this.entryList = decimalValueList.getEntryList();
        this.useSmoothEdge = decimalValueList.useSmoothEdge;
    }

    /*
    public DecimalValueList(double[] entry) {
        this(BigDecimal.valueOf(entry[0]), BigDecimal.valueOf(entry[1]));
    }*/

    public void add(DecimalValueList entry) {
        for(Object v : entry.getEntryList()) add((DecimalValue) v);
    }

    public void add(DecimalValue entry) {
        if (maxSize > 0) {
            if (entryList.size() > 0 && entryList.size() >= maxSize) {
                entryList.remove(0);
            }

            if(!useSmoothEdge) {
                reduceAmount = BigDecimal.ZERO;
            }

            this.decimalValue = new DecimalValue();
            List newEntryList = new ArrayList<DecimalValue>();
            for(Object i : entryList) {
                DecimalValue reducedValue = ((DecimalValue) i).reduceCount(reduceAmount);
                reducedValue = new DecimalValue(reducedValue.getSum(), reducedValue.getCount());
                this.decimalValue.add(reducedValue);
                newEntryList.add(reducedValue);
            }
            this.entryList = newEntryList;
        }

        this.decimalValue.add(entry);
        this.entryList.add(entry);
    }

    public void add(BigDecimal entry) {
        DecimalValue decimalValue = new DecimalValue(entry);
        add(decimalValue);
    }

    public void add(double entry) {
        DecimalValue decimalValue = new DecimalValue(entry);
        add(decimalValue);
    }

    public List getEntryList() { return entryList; }

    public void setEntryList(List entryList) {
        this.entryList = entryList;
    }

    public TreeSet getEntrySet() {
        return new TreeSet(entryList);
    }

    public DecimalValue getDecimalValue() {
        return decimalValue;
    }

    public int size() {
        return entryList.size();
    }

    /*
    public void add(RankTime ranktime) {
        if(ranktime != null) {
            super.add(ranktime);
            entryList.add(new DecimalValue(ranktime.getRanktime()));

            if (ranktime.getSubRecord().isValid())
                valid++;
        }
    }*/

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(decimalValue.toString());
        sb.append("");
        sb.append(this.entryList);
        sb.append(")");
        return sb.toString();
    }
}
