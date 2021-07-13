package hippos.math;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Markus on 6.9.2016.
 */
public class StringCounterSet {
    private TreeMap stringSet = new TreeMap();

    private void add(String str, BigDecimal value) {
        StringItem newStringItem;
        StringItem stringItem = (StringItem) stringSet.get((newStringItem = new StringItem(str)));

        if(stringItem == null)
            stringItem = newStringItem;

        stringItem.add(value);

        stringSet.put(stringItem, stringItem);
   }

    public String toString() {
        return stringSet.toString();
    }

    private class StringItem implements Comparable {
        private String id;
        private StatAvg statAvg = new StatAvg();

        public StringItem(String id) {
            this.id = id;
        }

        @Override
        public int compareTo(Object o) {
            StringItem aStringItem = (StringItem)o;

            if(this.id.equals(aStringItem.id))
                return 0;

            return statAvg.getAvg(2, BigDecimal.ZERO).compareTo(aStringItem.statAvg.getAvg(2, BigDecimal.ZERO)) > 0 ? 1 : 0;
        }

        public void add(BigDecimal value) {
            statAvg.add(value);
        }
    }

    public static void main(String args []) {
        StringCounterSet stringCounterSet = new StringCounterSet();

        stringCounterSet.add("a", BigDecimal.ONE);
        stringCounterSet.add("a", BigDecimal.ZERO);
        stringCounterSet.add("b", BigDecimal.ONE);

        System.out.println("StringCounterSet.main: " + stringCounterSet.toString());

    }

}
