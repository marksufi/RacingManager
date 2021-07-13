package hippos.math;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: marktolo
 * Date: 29.12.2013
 * Time: 23:02
 * To change this template use File | Settings | File Templates.
 */
public interface EvaluableField {

    BigDecimal evaluate();
    void evaluete(BigDecimal value);

}
