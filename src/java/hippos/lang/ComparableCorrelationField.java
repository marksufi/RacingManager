package hippos.lang;

import hippos.math.Correlation;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Feb 14, 2006
 * Time: 12:17:13 AM
 * To change this template use Options | File Templates.
 */
public abstract class ComparableCorrelationField implements ComparableField {
    BigDecimal share;
    ComparableField comparableField;

    abstract public Correlation getCorrelation();
    abstract public void putCorrelation(Correlation correlation);

    public ComparableCorrelationField(ComparableField comparableField) {
        this(comparableField, new BigDecimal(1));
    }

    public ComparableCorrelationField(ComparableField comparableField, BigDecimal share) {
        this.comparableField = comparableField;
        this.share = share;
    }

    public void compareTo(ComparableField comparableField, BigDecimal value) {
        if(value != null) {
            BigDecimal diff = this.comparableField.compareTo(comparableField);
            if(diff != null && diff.compareTo(new BigDecimal(0)) !=0) {
                Correlation c = getCorrelation();
                if(c != null) {
                    c.add(diff.doubleValue(), value.doubleValue());
                }
            }
        }
    }

    public BigDecimal compareTo(ComparableField comparableField) {
        BigDecimal diff = this.comparableField.compareTo(comparableField);
        if(diff != null && diff.compareTo(new BigDecimal(0)) != 0) {
            Correlation c = getCorrelation();
            if(c != null) {
                return new BigDecimal(c.get(diff.doubleValue()));
            }
        }
        return null;
    }
    public void evaluateTo( BigDecimal value ) {
        if( value != null ) {
            BigDecimal diff = this.comparableField.evaluateTo();
            if( diff != null ) {
                Correlation c = getCorrelation();
                if(c != null) {
                    c.add(diff.doubleValue(), value.doubleValue());
                }
                putCorrelation(c);
            }
        }
    }

    public BigDecimal evaluateTo() {
        BigDecimal diff = this.comparableField.evaluateTo();
        if( diff != null ) {
            Correlation c = getCorrelation();
            if(c != null) {
                return new BigDecimal( c.get( diff.doubleValue()) );
            }
        }
        return null;
    }

    /**
     * Palauttaa prosenttimäärän
     *
     * @return
     */
    public BigDecimal getShare() {
        return share;
    }

    public Object getField() {
        return this.comparableField;
    }

    public Object getValue() {
        return comparableField;
    }

    public String toString() {
        if(comparableField != null) {
            return comparableField.toString();
        }
        return null;
    }
}
