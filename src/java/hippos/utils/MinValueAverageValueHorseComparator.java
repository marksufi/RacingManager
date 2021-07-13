package hippos.utils;

import hippos.ValueHorse;

import java.math.BigDecimal;

/**
 * Created by Markus on 3.6.2016.
 */
public class MinValueAverageValueHorseComparator implements Comparable {
    private ValueHorse valueHorse;

    public MinValueAverageValueHorseComparator(ValueHorse valueHorse) {
        this.valueHorse = valueHorse;
    }

    public int compareTo(Object o) {
        ValueHorse anotherHorse = (ValueHorse)o;

        BigDecimal minListA = anotherHorse.getMinValue().average(2, BigDecimal.ZERO);
        BigDecimal minListB = valueHorse.getMinValue().average(2, BigDecimal.ZERO);

        if(minListA != null) {
            if(minListB != null) {
                if(!minListA.equals(minListB)){
                    int c = minListA.compareTo(minListB);
                    return c;
                }
            }
        }

        if(this.equals(anotherHorse)) {
            return 0;
        }
        return 1;
    }
}
