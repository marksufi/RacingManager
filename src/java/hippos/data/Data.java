package hippos.data;

import java.io.Serializable;

public interface Data extends Serializable{

    //public BigDecimal addComparison(Data data);

    /*
    public BigDecimal addComparison(Data data){
        //System.out.print(getData() + "/" +  data + "->");
        try {
            BigDecimal  sub = subtract(data),
                        add = add(data);
            return sub.divide(add, 2, BigDecimal.ROUND_HALF_UP);
        } catch (Exception e) {
            return new BigDecimal(0);
        }
    }
    */
}
