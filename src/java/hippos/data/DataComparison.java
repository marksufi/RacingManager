package hippos.data;

import java.math.BigDecimal;

public class DataComparison {

    DataComparison(BigDecimal d1, BigDecimal d2) {
        this.compare(d1, d2);
    }

    public void compare( BigDecimal d1, BigDecimal d2 ) {
        System.out.println("DataComparison.compare(" + d1 + ", " + d2 + ")");
    }
}
