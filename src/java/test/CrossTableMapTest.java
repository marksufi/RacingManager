package test;

import hippos.util.CrossTableMap;
import hippos.util.UnionCrossMapCompareMethod;
import junit.framework.TestCase;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: marktolo
 * Date: 5.8.2013
 * Time: 23:00
 * To change this template use File | Settings | File Templates.
 */
public class CrossTableMapTest extends TestCase{
    static CrossTableMap ctm1;
    static CrossTableMap ctm2;

    public static void main(String args[]) {
        junit.textui.TestRunner.run(CrossTableMapTest.class);
    }

    protected void setUp() {
        if(ctm1 == null) ctm1 = new CrossTableMap(new UnionCrossMapCompareMethod(0), 0);
        if(ctm2 == null) ctm2 = new CrossTableMap(new UnionCrossMapCompareMethod(10), 10);
    }

    public void testAddA() {
        ctm1.add("A", "B", new BigDecimal(1.0));
        ctm2.add("A", "B", new BigDecimal(1.0));
        TestCase.assertEquals(new BigDecimal("1.00"), ctm1.get("A", "B"));
        TestCase.assertEquals(new BigDecimal("0.10"), ctm2.get("A", "B"));
        TestCase.assertEquals(new BigDecimal("-1.00"), ctm1.get("B", "A"));
        TestCase.assertEquals(new BigDecimal("-0.10"), ctm2.get("B", "A"));
    }

    public void testAddB() {
        ctm1.add("B", "A", new BigDecimal(-1.0));
        ctm2.add("B", "A", new BigDecimal(-1.0));
        TestCase.assertEquals(new BigDecimal("1.00"), ctm1.get("A", "B"));
        TestCase.assertEquals(new BigDecimal("0.20"), ctm2.get("A", "B"));
        TestCase.assertEquals(new BigDecimal("-1.00"), ctm1.get("B", "A"));
        TestCase.assertEquals(new BigDecimal("-0.20"), ctm2.get("B", "A"));
    }

    public void testScale() {
        ctm1.add("A", "B", new BigDecimal(1));
        ctm2.add("A", "B", new BigDecimal(1));
        ctm1.add("B", "A", new BigDecimal(-1.0));
        ctm2.add("B", "A", new BigDecimal(-1.0));

        ctm1.setScale(3);
        ctm2.setScale(3);
        TestCase.assertEquals(new BigDecimal("1.000"), ctm1.get("A", "B"));
        TestCase.assertEquals(new BigDecimal("0.400"), ctm2.get("A", "B"));

        ctm1.setScale(10);
        ctm2.setScale(10);
        TestCase.assertEquals(new BigDecimal("-1.0000000000"), ctm1.get("B", "A"));
        TestCase.assertEquals(new BigDecimal("-0.4000000000"), ctm2.get("B", "A"));
        ctm1.setScale(2);
        ctm2.setScale(2);
    }

    public void testUnions_C() {
        ctm1.add("A", "C", new BigDecimal(2));
        ctm1.add("B", "C", new BigDecimal(1));
        ctm1.add("C", "B", new BigDecimal(-1));

        ctm2.add("A", "C", new BigDecimal(2));
        ctm2.add("B", "C", new BigDecimal(1));
        ctm2.add("C", "B", new BigDecimal(-1));

        TestCase.assertEquals(new BigDecimal("1.00"), ctm1.get("A", "B"));
        TestCase.assertEquals(new BigDecimal("0.50"), ctm2.get("A", "B")); // a-b(4), ab-c(1)

        TestCase.assertEquals(new BigDecimal("2.00"), ctm1.get("A", "C"));
        TestCase.assertEquals(new BigDecimal("0.60"), ctm2.get("A", "C")); // a-c(1), ac-b(2)

        TestCase.assertEquals(new BigDecimal("1.00"), ctm1.get("B", "C"));
        TestCase.assertEquals(new BigDecimal("0.30"), ctm2.get("B", "C")); // b-c(2), bc-a(1)={avg(b-a) - avg(c-a)} * min{count(b-a, c-a)}

    }

    public void testUnions_D() {
        ctm1.add("A", "D", new BigDecimal(3));
        ctm1.add("D", "B", new BigDecimal(-2));
        ctm2.add("A", "D", new BigDecimal(3));
        ctm2.add("D", "B", new BigDecimal(-2));

        TestCase.assertEquals(new BigDecimal("1.00"), ctm1.get("A", "B"));
        TestCase.assertEquals(new BigDecimal("0.60"), ctm2.get("A", "B")); // a-b(4), ab-c(1), ab-d(1)

        TestCase.assertEquals(new BigDecimal("2.00"), ctm1.get("A", "C"));
        TestCase.assertEquals(new BigDecimal("0.60"), ctm2.get("A", "C")); // a-c(1), ac-b(2)

        TestCase.assertEquals(new BigDecimal("1.00"), ctm1.get("C", "D"));
        TestCase.assertEquals(new BigDecimal("0.20"), ctm2.get("C", "D")); // cd-a(1), cd-b(1)

        TestCase.assertEquals(new BigDecimal("3.00"), ctm1.get("A", "D"));
        TestCase.assertEquals(new BigDecimal("0.60"), ctm2.get("A", "D")); // a-d(1), ad-b(1)
    }

    public void testUnionsMassTest() {
        ctm1 = new CrossTableMap(new UnionCrossMapCompareMethod(0), 0);
        ctm2 = new CrossTableMap(new UnionCrossMapCompareMethod(100), 100);

        for(int i = 0; i < 100; i++) {
            ctm1.add("A", "B", new BigDecimal(1));
            ctm1.add("B ", "A", new BigDecimal(-1));

            ctm2.add("A", "B", new BigDecimal(1));
            ctm2.add("B ", "A", new BigDecimal(-1));
        }

        for(int i = 0; i < 50; i++) {
            ctm1.add("A", "C", new BigDecimal(2));
            ctm1.add("B ", "C", new BigDecimal(1));

            ctm2.add("A", "C", new BigDecimal(2));
            ctm2.add("B", "C", new BigDecimal(1));
        }

        for(int i = 0; i < 10; i++) {
            ctm1.add("A", "D", new BigDecimal(3));
            ctm2.add("A", "D", new BigDecimal(3));
        }

        for(int i = 0; i < 5; i++) {
            ctm1.add("B ", "D", new BigDecimal(2));
            ctm2.add("B ", "D", new BigDecimal(2));
        }

        TestCase.assertEquals(new BigDecimal("1.00"), ctm1.get("A", "B"));
        TestCase.assertEquals(new BigDecimal("1.00"), ctm2.get("A", "B")); // a-b(4), ab-c(1), ab-d(1)

        TestCase.assertEquals(new BigDecimal("2.00"), ctm1.get("A", "C"));
        TestCase.assertEquals(new BigDecimal("2.00"), ctm2.get("A", "C")); // ac(50), ac-b(50)

        TestCase.assertEquals(new BigDecimal("1.00"), ctm1.get("B", "C"));
        TestCase.assertEquals(new BigDecimal("1.00"), ctm2.get("B", "C")); // bc(50), bc-a(50)

        TestCase.assertEquals(new BigDecimal("3.00"), ctm1.get("A", "D"));
        TestCase.assertEquals(new BigDecimal("0.45"), ctm2.get("A", "D")); // ad(10), ad-b(5)
    }

    /*
    public void testSimpleMap_A() {
        ctm1 = new CrossTableMap(new SimpleCrossMapCompareMethod(), 0);
        ctm2 = new CrossTableMap(new SimpleCrossMapCompareMethod(), 10);

        ctm1.add("A", "B", new BigDecimal(1.0));
        ctm2.add("A", "B", new BigDecimal(1.0));
        TestCase.assertEquals(new BigDecimal("1.00"), ctm1.get("A", "B"));
        TestCase.assertEquals(new BigDecimal("0.10"), ctm2.get("A", "B"));
        TestCase.assertEquals(new BigDecimal("0.00"), ctm1.get("B", "A"));
        TestCase.assertEquals(new BigDecimal("0.00"), ctm2.get("B", "A"));

    }

    public void testSimpleMap_B() {
        ctm1.add("A", "B", new BigDecimal(3.0));
        ctm2.add("A", "B", new BigDecimal(3.0));
        TestCase.assertEquals(new BigDecimal("2.00"), ctm1.get("A", "B"));
        TestCase.assertEquals(new BigDecimal("0.40"), ctm2.get("A", "B"));
        TestCase.assertEquals(new BigDecimal("0.00"), ctm1.get("B", "A"));
        TestCase.assertEquals(new BigDecimal("0.00"), ctm2.get("B", "A"));

    }
    */

}
