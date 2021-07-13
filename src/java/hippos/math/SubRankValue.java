package hippos.math;

import hippos.SubRank;
import hippos.math.regression.RegressionModelException;
import utils.Log;

import java.util.Arrays;

public class SubRankValue implements Comparable, SubValue {
    SubRank subRank;
    Value value = new Value();

    public SubRankValue(SubRank subRank) {
        this.subRank = subRank;
    }
    public void updateValue(double y) {

        value.add(y);
    }

    @Override
    public boolean onLaukaton() {
        return !subRank.getX();
    }

    public Value getValue() {
        return value;
    }

    @Override
    public int compareTo(Object o) {
        SubValue subValue = (SubValue) o;

        if(this.hashCode() == subValue.hashCode())
            return 0;

        return (-value.compareTo(subValue.getValue()));
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(value);
        sb.append("{");

        try {
            sb.append(subRank.getRegKey());
            sb.append(Arrays.toString(subRank.getRegGetX()));
        } catch (RegressionModelException e) {
            //e.printStackTrace();
        } catch (Exception e) {
            Log.write(e);
        }

        sb.append("}");

        return sb.toString();
    }
}
