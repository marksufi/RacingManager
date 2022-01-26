package hippos.utils;

import hippos.ValueHorse;
import utils.Log;

import java.math.BigDecimal;

public class ValueComparator implements Comparable {
        private ValueHorse valueHorse;

        public ValueComparator(ValueHorse valueHorse) {
            this.valueHorse = valueHorse;
        }

        public int compareTo(Object o) {
            BigDecimal valueA = null;
            BigDecimal valueB = null;
            try {
                ValueHorse anotherHorse = (ValueHorse)o;

                valueA = valueHorse.getValue().average();
                valueB = anotherHorse.getValue().average();

                if(valueHorse.hashCode() == anotherHorse.hashCode())
                    return 0;

                if(valueA.compareTo(valueB) == 0)
                    return -1;

                return valueA.compareTo(valueB);
            } catch (ArithmeticException e) {

            } catch (Exception e) {
                Log.write(e);
            }

            if(valueA == null)
                return -1;
            if(valueB == null)
                return 1;
            return -1;
        }
}


