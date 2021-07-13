package hippos.math;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: May 9, 2006
 * Time: 2:01:53 AM
 * To change this template use Options | File Templates.
 */
public interface DataEntry extends Serializable {
    public BigDecimal average(int scale);
    public BigDecimal procentualAverage(int limit, int scale);
    public DataEntry add(DataEntry v);
    public DataEntry add(BigDecimal v);
    public BigDecimal compareTo(DataEntry value2);
}
