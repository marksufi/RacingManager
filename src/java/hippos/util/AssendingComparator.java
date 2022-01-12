package hippos.util;

import java.util.Comparator;


/**
 * Pienemmästä suurempaan 1, 2, 3...
 */
public class AssendingComparator implements Comparator {
    @Override
    public int compare(Object o1, Object o2) {
        if(o1.hashCode() == o2.hashCode())
            return 0;

        int cmp = ((Comparable)o1).compareTo(o2);

        return cmp == 0 ? -1 : cmp;

    }
}
