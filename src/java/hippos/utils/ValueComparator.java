package hippos.utils;

import hippos.ValueHorse;

import java.math.BigDecimal;

public class ValueComparator implements Comparable {
        private ValueHorse valueHorse;

        public ValueComparator(ValueHorse valueHorse) {
            this.valueHorse = valueHorse;
        }

        public int compareTo(Object o) {
            ValueHorse anotherHorse = (ValueHorse)o;

            //BigDecimal maxListA = horse.getPowerValue();
            //BigDecimal maxListB = this.getPowerValue();

            BigDecimal maxListA = anotherHorse.getValue();
            BigDecimal maxListB = valueHorse.getValue();

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


