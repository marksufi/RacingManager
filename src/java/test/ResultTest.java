package test;

import junit.framework.TestCase;
import hippos.math.Result;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Dec 27, 2005
 * Time: 2:04:24 AM
 * To change this template use Options | File Templates.
 */
public class ResultTest extends TestCase {
    Result result0;
    Result result1;
    Result result2;
    Result aNull;
    Result bNull;

    public static void main(String args[]) {
        junit.textui.TestRunner.run(LinRegTest.class);
    }

    protected void setUp() {
        result1 = new Result(new BigDecimal(2), new BigDecimal(1));
        result2 = new Result(new BigDecimal(3), new BigDecimal(4));
        aNull = new Result(null, new BigDecimal(1));
        bNull = new Result(new BigDecimal(1), null);
    }

    public void testAdd() {
        result0 = new Result();
        result0.add(result1);
        assertEquals(result0.average(0), new BigDecimal(1));
        result0.add(result2);
        assertEquals(result0.average(0), new BigDecimal(0));
    }

    public void testAddNull() {
        result0 = new Result();
        result0.add(aNull);
        result0.add(bNull);
        assertEquals(result0.aNullAverage(1), new BigDecimal(0.5));
        assertEquals(result0.bNullAverage(1), new BigDecimal(0.5));
        result0.add(result1);
        result0.add(result2);
        assertEquals(result0.average(0), new BigDecimal(0));
    }
}
