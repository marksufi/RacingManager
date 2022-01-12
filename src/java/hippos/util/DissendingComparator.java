package hippos.util;

import java.util.Comparator;

/**
 * Suuremmasta pienempään 10, 9, 8...
 */
public class DissendingComparator implements Comparator {

    public int compare(Object o1, Object o2) {

        if(o1.hashCode() == o2.hashCode())
            return 0;

        int cmp = ((Comparable)o2).compareTo(((Comparable)o1));

        return cmp == 0 ? 1 : cmp;
    }

}
