package hippos.math;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Jan 26, 2006
 * Time: 11:49:50 PM
 * To change this template use Options | File Templates.
 */
public class Field {
    String field;

    public Field() {
        this.field = null;
    }

    public Field(String field) {
        this.field = field;
    }

    public Field(BigDecimal field) {
        if(field != null)
            this.field = field.toString();
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String toString() {
        if(field != null)
            return field.toString();
        return null;
    }
}
