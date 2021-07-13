package hippos.data;

import java.math.BigDecimal;

public class NumberData implements Data{

    private BigDecimal number;

    public NumberData(BigDecimal number, int scale) {
        if(number != null) {
            this.number = number.setScale(scale, BigDecimal.ROUND_HALF_UP);
        }
    }

    public NumberData(BigDecimal number) {
        if(number != null) {
            this.number = number;
        }
    }

    public NumberData(int number) {
        this.number = new BigDecimal(number);
    }

    public NumberData(String number) {
        this.number = new BigDecimal(number);
    }

    public BigDecimal compareTo(Data data) {
        try {
            BigDecimal number = (BigDecimal)data;
            BigDecimal  sub = this.number.subtract(number),
                        add = this.number.add(number);
            return sub.divide(add, 2, BigDecimal.ROUND_HALF_UP);
        } catch (Exception e) {
            return new BigDecimal(0);
        }
   }

    public BigDecimal getData() {
        return number;
    }

    public String toString() {
        return number.toString();
    }
}
