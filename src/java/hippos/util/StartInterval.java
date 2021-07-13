package hippos.util;

import hippos.HarnessApp;
import hippos.math.DecimalValue;
import hippos.math.DecimalValueList;

import java.math.BigDecimal;

/**
 * Created by Markus on 20.11.2016.
 */
public class StartInterval {
    //private DecimalValueList startInterval = new DecimalValueList(5, true);
    private BigDecimal startInterval = BigDecimal.ZERO;
    //private String horseName;

    /*
    public StartInterval(String horseName) {
        this.horseName = horseName;
    }*/

    public void add(double weekDiff) {
        if(!Double.isNaN(weekDiff)) {
            if(weekDiff < 1.0) {
                weekDiff = 1.0;
            }
            if(weekDiff > HarnessApp.WEEKDIFF_MAX) {
                startInterval = startInterval.subtract(BigDecimal.valueOf(weekDiff / HarnessApp.WEEKDIFF_MAX));
                if(startInterval.compareTo(BigDecimal.ZERO) < 0) {
                    startInterval = BigDecimal.ZERO;
                }
            } else {
                startInterval = startInterval.add(BigDecimal.valueOf(1.0 / weekDiff));
                if(startInterval.compareTo(BigDecimal.valueOf(3)) > 0)
                {
                    startInterval = new BigDecimal(3);
                }
            }
        } else {
            startInterval = BigDecimal.ZERO;
        }
    }

    public BigDecimal getStartInterval() {
        return startInterval.setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
