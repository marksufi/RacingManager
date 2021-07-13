package test.regression;

import hippos.math.regression.LinearRegression;
import junit.framework.TestCase;

/**
 * Created by marktolo on 30.11.2014.
 */
public class LinearRegressionTest extends TestCase {
    LinearRegression reg;

    public static void main(String args[]) {
        junit.textui.TestRunner.run(LinearRegressionTest.class);
    }

    protected void setUp() {
        reg = new LinearRegression();

        double x1 = -4, y1 = -4;
        double x2 = -3, y2 = -3;
        double x3 = -2, y3 = -2;
        double x4 = -1, y4 = -1;
        double x5 = 0, y5 = 0;
        double x6 = 1, y6 = 1;
        double x7 = 2, y7 = 2;
        double x8 = 3, y8 = 3;
        double x9 = 4, y9 = 4;

        reg.add(x1, y1);
        reg.add(x2, y2);
        reg.add(x3, y3);
        reg.add(x4, y4);
        reg.add(x5, y5);
        reg.add(x6, y6);
        reg.add(x7, y7);
        reg.add(x8, y8);
        reg.add(x9, y9);
    }

    public void testGet() {
        assertEquals(-1, reg.get(-1), 0.1);
        assertEquals(5, reg.get(5), 0.1);
        assertEquals(10, reg.get(10), 0.1);
    }

    public void testNegativeValues() {
        reg = new LinearRegression();

        double x1 = -1, y1 = 1;
        double x2 = -2, y2 = 2;
        double x3 = -3, y3 = 3;
        double x4 = -4, y4 = 4;
        double x5 = -5, y5 = 5;
        double x6 = -6, y6 = 6;
        double x7 = -7, y7 = 7;
        double x8 = -8, y8 = 8;
        double x9 = -9, y9 = 9;

        reg.add(x1, y1);
        reg.add(x2, y2);
        reg.add(x3, y3);
        reg.add(x4, y4);
        reg.add(x5, y5);
        reg.add(x6, y6);
        reg.add(x7, y7);
        reg.add(x8, y8);
        reg.add(x9, y9);

        assertEquals(1, reg.get(-1), 0.1);
        assertEquals(-5, reg.get(5), 0.1);
        assertEquals(-10, reg.get(10), 0.1);

    }

    public void testNegPosValues() {
        reg = new LinearRegression();

        double x1 = -4, y1 = 4;
        double x2 = -3, y2 = 3;
        double x3 = -2, y3 = 2;
        double x4 = -1, y4 = 1;
        double x5 = 0, y5 = 0;
        double x6 = 1, y6 = -1;
        double x7 = 2, y7 = -2;
        double x8 = 3, y8 = -3;
        double x9 = 4, y9 = -4;

        reg.add(x1, y1);
        reg.add(x2, y2);
        reg.add(x3, y3);
        reg.add(x4, y4);
        reg.add(x5, y5);
        reg.add(x6, y6);
        reg.add(x7, y7);
        reg.add(x8, y8);
        reg.add(x9, y9);

        assertEquals(5, reg.get(-5), 0.1);
        assertEquals(0, reg.get(0), 0.1);
        assertEquals(-5, reg.get(5), 0.1);

    }

    public void testNegNegValues() {
        reg = new LinearRegression();

        double x1 = -4, y1 = -1;
        double x2 = -3, y2 = -2;
        double x3 = -2, y3 = -3;
        double x4 = -1, y4 = -4;
        double x5 = 0, y5 = -5;
        double x6 = 1, y6 = -6;
        double x7 = 2, y7 = -7;
        double x8 = 3, y8 = -8;
        double x9 = 4, y9 = -9;

        reg.add(x1, y1);
        reg.add(x2, y2);
        reg.add(x3, y3);
        reg.add(x4, y4);
        reg.add(x5, y5);
        reg.add(x6, y6);
        reg.add(x7, y7);
        reg.add(x8, y8);
        reg.add(x9, y9);

        assertEquals(0, reg.get(-5), 0.1);
        assertEquals(-5, reg.get(0), 0.1);
        assertEquals(-10, reg.get(5), 0.1);

    }

    public void testBigNumbers() {
        reg = new LinearRegression();

        double x=-10000.0, y = 0.0;
        for(; x != -y; x++) {
            reg.add(x, -x);
            y = reg.get(x);
            assertEquals(-x, y, 0.1);
        }
        System.out.println("test.regression.LinearRegressionTest.testBigNumbers: x=" + x + ", y=" + y);
    }

}
