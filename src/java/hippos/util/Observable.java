package hippos.util;

import hippos.math.AlphaNumber;
import hippos.math.Value;

import java.math.BigDecimal;

public class Observable extends AlphaNumber {
    private Value value = new Value();

    public Observable() {
        super();
    }

    public Observable(BigDecimal number) {
        super(number);
    }

    public Observable(AlphaNumber alphaNumber) {
        super(alphaNumber);

    }

    public Observable(String string) {
        super(string);

    }

    public Observable(BigDecimal number, String string) {
        super(number, string);

    }

    public Value getValue() {
        return value;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append("[" + value.toString() + "]");
        return sb.toString();
    }
}
