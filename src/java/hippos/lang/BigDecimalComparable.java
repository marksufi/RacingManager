package hippos.lang;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: marktolo
 * Date: 1.9.2013
 * Time: 6:23
 * To change this template use File | Settings | File Templates.
 */
public class BigDecimalComparable implements ComparableField {
    private BigDecimal field;
    private BigDecimal share;

    public BigDecimalComparable(BigDecimal field) {
        this(field, new BigDecimal(1));
    }

    public BigDecimalComparable(BigDecimal field, BigDecimal share) {
        this.field = field;
        this.share = share;
    }

    public void compareTo(Object field, BigDecimal value) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public BigDecimal compareTo(Object field) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void compareTo(ComparableField field, BigDecimal value) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public BigDecimal compareTo(ComparableField field) {
        BigDecimalComparable pField = (BigDecimalComparable)field.getField();

        if(this.field != null && pField.getField() !=null) {
            return (this.field.subtract((BigDecimal)pField.getField())).multiply(share);
        }
        return null;
    }

    public void evaluateTo(BigDecimal value) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public BigDecimal evaluateTo() {
        if(this.field != null) {
            return this.field.multiply(share);
        }
        return null;
    }

    public BigDecimal getShare() {
        return this.share;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object getField() {
        return this.field;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object getValue() {
        return this.field;
    }

    public String toString() {
        if(field != null) {
            return field.toString();
        }
        return null;
    }
}
