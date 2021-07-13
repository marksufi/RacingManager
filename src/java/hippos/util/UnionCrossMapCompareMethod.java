package hippos.util;

import hippos.math.Value;
import hippos.util.CrossMapCompareMethod;
import utils.Log;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: marktolo
 * Date: 16.8.2013
 * Time: 11:05
 * To change this template use File | Settings | File Templates.
 */
public class UnionCrossMapCompareMethod implements CrossMapCompareMethod, Serializable {
    private int trustLimit;

    public UnionCrossMapCompareMethod(int trustLimit) {
        this.trustLimit = trustLimit;
    }

    public void add(Map x, String s1, String s2, BigDecimal input) {
        crossAdd(x, s1, s2, input);
        crossAdd(x, s2, s1, input.negate());
    }

    /**
     * Gets an average value of(x(s1) and y(s2). Average value is an averageof x-y - y-x + union(x, y)
     *
     * @param s1 x-vakue
     * @param s2 y-value
     * @return unionValue
     */
    public Value get(Map x, String s1, String s2) {
        Value value = new Value();
        try {
            if(s1 != null && s2 != null && !s1.equals(s2)) {
                Map y1 = (Map)x.get(s1);
                Map y2 = (Map)x.get(s2);
                Value v;
                Value vu;

                if(y1 != null && (v = (Value)y1.get(s2)) != null) {
                    value.add(v);
                }
                //if(value.size() < this.trustLimit) {
                    if(y1 != null && y2 != null) {
                        vu = getUnionValue(y1, y2);
                        value.add(vu);
                    }
                //}
            }
        } catch (Exception e) {
            Log.write(e, "UnionCrossMapCompareMethod.get: ");
        }

        return value;
    }

    private void crossAdd(Map x, Object s1, Object s2, BigDecimal result) {
        if(s1 != null && s2 != null && !s1.equals(s2) && result != null) {
            try {
                ((Value) ((Map) x.get(s1)).get(s2)).add(result);
            } catch (Exception e) {
                Value d;
                Map y;

                if ((y = (Map) x.get(s1)) == null) {
                    y = new TreeMap();
                }
                if ((d = (Value) y.get(s2)) == null) {
                    d = new Value();
                }
                d.add(result);
                y.put(s2, d.average(2));
                x.put(s1, y);
            }
        }
    }

    /**
     * Calculates union value for the parameter Arrays
     *
     * @param y1 array1
     * @param y2 array2
     *
     * @return average
     */
    private Value getUnionValue(Map y1, Map y2) {
        Value unionValue = new Value();
        try {
            //Iterator keyItrY2 = y2.keySet().iterator();
            //Iterator valueItrY2 = y2.values().iterator();

            if(y1.keySet().size() < y2.keySet().size()) {
                Iterator keyItr1 = y1.keySet().iterator();
                Iterator valueItr1 = y1.values().iterator();

                while(keyItr1.hasNext()) {
                    Object key1 = keyItr1.next();
                    Value value1 = (Value)valueItr1.next();
                    Value value2 = (Value)y2.get(key1);

                    if(value2 != null) {
                        unionValue.add(value1.getUnionValue(value2));
                    }
                }

            } else {
                Iterator keyItr2 = y2.keySet().iterator();
                Iterator valueItr2 = y2.values().iterator();

                while(keyItr2.hasNext()) {
                    Object key2 = keyItr2.next();
                    Value value2 = (Value) valueItr2.next();
                    Value value1 = (Value) y1.get(key2);

                    if (value1 != null) {
                        unionValue.add(value2.getUnionValue(value1).negate());
                    }
                }
            }

            /*
            while(keyItrY2.hasNext()) {
                Object key2 = keyItrY2.next();
                Value value1 = (Value)y1.get(key2);
                Value value2 = (Value)valueItrY2.next();
                if(value1 != null) {
                    unionValue.add(value1.getUnionValue(value2));
                }
            }*/

            /*
            Set<String> intersect = new TreeSet(y1.keySet());
            intersect.retainAll(y2.keySet());

            Iterator kItr = intersect.iterator();

            while(kItr.hasNext()) {
                Object key = kItr.next();
                Value value1 = (Value)y1.get(key);
                Value value2 = (Value)y2.get(key);
                if(value1 != null) {
                    unionValue.add(value1.getUnionValue(value2));
                }
            }*/
            /*
            Iterator yItr = null;
            if(y1.keySet().size() < y2.keySet().size()) {
                yItr = y1.keySet().iterator();
            } else {
                yItr = y2.keySet().iterator();
            }
            while(yItr.hasNext()) {
                Object key = yItr.next();
                Value value1 = (Value)y1.get(key);
                Value value2 = (Value)y2.get(key);
                if(value1 != null && value2 != null) {
                    unionValue.add(value1.getUnionValue(value2));
                }
            }*/

        } catch (Exception e) {
            Log.write(e);
        }
        return unionValue;
    }
}
