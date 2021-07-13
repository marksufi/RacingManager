package hippos.math;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Mar 1, 2006
 * Time: 10:24:49 PM
 * To change this template use Options | File Templates.
 */
public interface ListComparable {
    public void compareTo(List aList, List bList, BigDecimal diff);
    public Value compareTo(List aList, List bList);
}
