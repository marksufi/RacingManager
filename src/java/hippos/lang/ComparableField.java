package hippos.lang;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Jan 27, 2006
 * Time: 7:41:44 PM
 * To change this template use Options | File Templates.
 */
public interface ComparableField {
    public void compareTo(ComparableField field, BigDecimal value);
    public BigDecimal compareTo(ComparableField field);
    public void evaluateTo(BigDecimal value);
    public BigDecimal evaluateTo();
    public BigDecimal getShare();
    public Object getField();
    public Object getValue();



}
