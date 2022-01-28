package hippos.util;

import hippos.math.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Mar 2, 2005
 * AlphaNumber: 6:48:04 PM
 * To change this template use Options | File Templates.
 */
public class CrossTableMap implements Serializable {
    private Map x;
    private int scale = 2;
    private int trustLimit = 0;
    private CrossMapCompareMethod compareMethod;

    private CrossTableMap() {
        if(x == null)
            x = new TreeMap();
    }

    public CrossTableMap(CrossMapCompareMethod compareMethod) {
        this();
        this.compareMethod = compareMethod;
    }

    public CrossTableMap(CrossMapCompareMethod compareMethod, int trustLimit) {
        this();
        this.compareMethod = compareMethod;
        this.trustLimit = trustLimit;
    }

    public void add(String s1, String s2, BigDecimal input) {
        compareMethod.add(x, s1, s2, input);
    }

    public BigDecimal get(String s1, String s2) {
        Value value = compareMethod.get(x, s1,s2);

        if(value.size().compareTo(BigDecimal.ZERO) > 0)
            return value.procents(trustLimit, scale);
        return null;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public void setCompareMethod(CrossMapCompareMethod compareMethod) {
        this.compareMethod = compareMethod;
    }

    public void setTrustLimit(int trustLimit) {
        this.trustLimit = trustLimit;
    }


    public static void main(String args[]) {
        CrossTableMap container = new CrossTableMap(new UnionCrossMapCompareMethod(1));

        container.add("A", "B", new BigDecimal(0.5));
        System.out.println("union=" + container.get("A", "B"));
        System.out.println("union=" + container.get("B", "A"));
        container.add("A", "C", new BigDecimal(1.5));
        System.out.println("union=" + container.get("A", "B"));
        System.out.println("union=" + container.get("B", "A"));
        container.add("B", "A", new BigDecimal(0.5));
        System.out.println("union=" + container.get("A", "B"));
        System.out.println("union=" + container.get("B", "A"));
        container.add("B", "A", new BigDecimal(0.5));
        System.out.println("union=" + container.get("A", "B"));
        System.out.println("union=" + container.get("B", "A"));
        container.add("A", "C", new BigDecimal(1));
        System.out.println("union=" + container.get("A", "B"));
        System.out.println("union=" + container.get("B", "A"));
        container.add("B", "C", new BigDecimal(-1));
        System.out.println("union=" + container.get("A", "B"));
        System.out.println("union=" + container.get("B", "A"));
        container.add("A", "D", new BigDecimal(2));
        System.out.println("union=" + container.get("A", "B"));
        System.out.println("union=" + container.get("B", "A"));
        container.add("B", "D", new BigDecimal(2));
        System.out.println("union=" + container.get("A", "B"));
        System.out.println("union=" + container.get("B", "A"));
        container.add("A", "E", new BigDecimal(0));
        System.out.println("union=" + container.get("A", "B"));
        System.out.println("union=" + container.get("B", "A"));
        container.add("E", "B", new BigDecimal(0));
        System.out.println("union=" + container.get("A", "B"));
        System.out.println("union=" + container.get("B", "A"));

        //container.add("A", "B", new BigDecimal(1.0));

        System.out.println(container.toString());
    }

}
