package hippos.utils;

import hippos.ValueHorse;

import java.math.BigDecimal;

/**
 * Created by marktolo on 10.11.2014.
 */
public class MaxValueAverageValueHorseComparator implements Comparable {
    private ValueHorse valueHorse;

    public MaxValueAverageValueHorseComparator(ValueHorse valueHorse) {
        this.valueHorse = valueHorse;
    }

    public int compareTo(Object o) {
        ValueHorse anotherHorse = (ValueHorse)o;

        //BigDecimal maxListA = horse.getPowerValue();
        //BigDecimal maxListB = this.getPowerValue();

        BigDecimal maxListA = anotherHorse.getMaxValue().average(2, BigDecimal.ZERO);
        BigDecimal maxListB = valueHorse.getMaxValue().average(2, BigDecimal.ZERO);

        if(maxListA != null) {
            if(maxListB != null) {
                if(!maxListA.equals(maxListB)){
                    int c = maxListA.compareTo(maxListB);
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
