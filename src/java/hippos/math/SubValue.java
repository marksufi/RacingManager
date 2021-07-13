package hippos.math;

import hippos.SubStart;

public interface SubValue extends Comparable {
    Value getValue();

    void updateValue(double y);

    boolean onLaukaton();
}
