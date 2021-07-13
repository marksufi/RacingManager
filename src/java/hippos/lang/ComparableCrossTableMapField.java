package hippos.lang;

import hippos.util.CrossTableMap;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Jan 26, 2006
 * Time: 10:16:14 PM
 * To change this template use Options | File Templates.
 */
public abstract class ComparableCrossTableMapField implements ComparableField {
    String value;
    String field;
    BigDecimal share = new BigDecimal(1);

    public ComparableCrossTableMapField(String field) {
        this(field, field);
    }

    public ComparableCrossTableMapField(String field, String value) {
        this(field, value, new BigDecimal(1));
    }

    public ComparableCrossTableMapField(String field, String value, BigDecimal share) {
        this.field = field;
        this.value = value;
        this.share = share;
    }

    public Object getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Object getValue() {
        if(value != null)
            return value;
        return field;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void compareTo(CrossTableMap map, Object o, BigDecimal result) {
        ComparableField field = (ComparableField)o;
        if(getValue() != null && field.getValue() != null) {
            map.add((String)getValue(), (String)field.getValue(), result);
        }
    }

    public BigDecimal compareTo(CrossTableMap map, Object o) {
        ComparableField field = (ComparableField)o;
        if(getValue() != null && field.getValue() != null) {
            return map.get((String)getValue(), (String)field.getValue());
        }
        return null;
    }

    public void compareTo(ComparableField field, BigDecimal value) {
        CrossTableMap crossTableMap = getCrossTableMap();

        compareTo(crossTableMap, field, value);  //To change body of implemented methods use File | Settings | File Templates.
    }

    public BigDecimal compareTo(ComparableField field) {
        CrossTableMap crossTableMap = getCrossTableMap();

        return compareTo(crossTableMap, field);  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void evaluateTo(BigDecimal value) {
        System.out.println("ComparableCrossTableMapField.evaluateTo: What this should do?");
    }

    public BigDecimal evaluateTo() {
        System.out.println("ComparableCrossTableMapField.evaluateTo: What this should do?");
        return null;
    }

    public BigDecimal getShare() {
        return share;
    }

    public abstract CrossTableMap getCrossTableMap();

    public String toString() {
        if(field != null)
            return field.toString();
        return value;
    }

}
