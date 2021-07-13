package hippos.util;

import hippos.math.Value;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: marktolo
 * Date: 16.8.2013
 * Time: 10:42
 * To change this template use File | Settings | File Templates.
 */
public interface CrossMapCompareMethod {
    public void add(Map x,String s1, String s2, BigDecimal input);
    public Value get(Map x, String s1, String s2);
}
