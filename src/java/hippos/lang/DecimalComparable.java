package hippos.lang;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Jan 31, 2006
 * Time: 11:38:52 PM
 * To change this template use Options | File Templates.
 */
public interface DecimalComparable {
    public BigDecimal compareTo(DecimalComparable o);
    public boolean isComparable();
}
