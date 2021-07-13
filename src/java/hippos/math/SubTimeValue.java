package hippos.math;

import hippos.SubTime;
import hippos.lang.stats.SubRaceTime;

public class SubTimeValue implements SubValue {
    private Value value = new Value();
    private SubTime subTime;

    public SubTimeValue(SubTime subTime) {
        this.subTime = subTime;
    }

    public void updateValue(double y) {

        value.add(y);
    }

    @Override
    public boolean onLaukaton() {
        return !subTime.getX();
    }

    public Value getValue() {
        return value;
    }

    @Override
    public int compareTo(Object o) {
        SubValue subValue = (SubValue) o;

        if(this.hashCode() == subValue.hashCode())
            return 0;

        return -value.compareTo(subValue.getValue());
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(value);
        sb.append("{");
        sb.append(subTime.getSubRaceTime());
        sb.append(subTime.getAlpha());
        sb.append("}");

        return sb.toString();
    }

    public SubTime getSubTime() {
        return subTime;
    }
}
