package hippos.util;

import hippos.math.Value;
import hippos.util.CrossMapCompareMethod;
import utils.Log;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created with IntelliJ IDEA.
 * User: marktolo
 * Date: 16.8.2013
 * Time: 10:45
 * To change this template use File | Settings | File Templates.
 */
public class SimpleCrossMapCompareMethod implements CrossMapCompareMethod, Serializable {
    public void add(Map x, String s1, String s2, BigDecimal input) {
        try {
            if(s1 != null && s2 != null && !s1.equals(s2) && input != null) {
                Value d;
                Map y;

                if((y = (Map)x.get(s1)) == null) {
                    y = new TreeMap();
                }
                if((d = (Value)y.get(s2)) == null) {
                    d = new Value();
                }
                d.add(input.doubleValue());
                y.put(s2, d.average(2));
                x.put(s1, y);
            }
        } catch (Exception e) {
            e.printStackTrace(Log.getPrintWriter());
        }
    }

    public Value get(Map x, String s1, String s2) {
        Value value = new Value();
        try {
            if(s1 != null && s2 != null && !s1.equals(s2)) {
                Map y = (Map)x.get(s1);
                Value v;

                if(y != null && (v = (Value)y.get(s2)) != null) {
                    value.add(v);
                }
            }
        } catch (Exception e) {
            Log.write(e);
        }
        return value;
    }
}
