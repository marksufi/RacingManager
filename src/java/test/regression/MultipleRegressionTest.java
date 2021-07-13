package test.regression;

import hippos.math.regression.MultipleRegression;
import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: May 24, 2006
 * Time: 7:00:12 PM
 * To change this template use Options | File Templates.
 */
public class MultipleRegressionTest extends TestCase {
    MultipleRegression reg;

    public static void main(String args[]) {
        junit.textui.TestRunner.run(MultipleRegressionTest.class);
    }

    protected void setUp() {
        /*
        double x1[] = {-4,4,8}, y1 = 4;
        double x2[] = {-3,3,6}, y2 = 3;
        double x3[] = {-2,2,4}, y3 = 2;
        double x4[] = {-1,1,2}, y4 = 1;
        double x5[] = {0,0,0}, y5 = 0;
        double x6[] = {1,-1,-2}, y6 = -1;
        double x7[] = {2,-2,-4}, y7 = -2;
        double x8[] = {3,-3,-6}, y8 = -3;
        double x9[] = {4,-4,-8}, y9 = -4;
        */
        double x1[] = {1,-4,4,8}, y1 = 4;
        double x2[] = {1,-3,3,6}, y2 = 3;
        double x3[] = {1,-2,2,4}, y3 = 2;
        double x4[] = {1,-1,1,2}, y4 = 1;
        double x5[] = {1,0,0,0}, y5 = 0;
        double x6[] = {1,1,-1,-2}, y6 = -1;
        double x7[] = {1,2,-2,-4}, y7 = -2;
        double x8[] = {1,3,-3,-6}, y8 = -3;
        double x9[] = {1,4,-4,-8}, y9 = -4;

        reg = new MultipleRegression(x1.length);
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

    /*
    public void testGetSx() {
        assertEquals(73.5, reg.getSx(0), 0.1);
        assertEquals(157.6, reg.getSx(1), 0.1);
    }

    public void testGetSy() {
        assertEquals(191, reg.getSy(0), 0.1);
    }

    public void testGetSxx() {
        assertEquals(1710.5, reg.getSxx(0, 0), 0.1);
        assertEquals(266, reg.getSxx(0, 1), 0.1);
        assertEquals(2098.4, reg.getSxx(1, 1), 0.1);
        assertEquals(266, reg.getSxx(1, 0), 0.1);
    }

    public void testGetSxy() {
        assertEquals(-11481, reg.getSxy(0), 0.01);
        assertEquals(-8383, reg.getSxy(1), 0.01);
    }

    public void testGetSyy() {
        assertEquals(108576, reg.getSyy(), 0.1);
    }

    public void testGetB() {
        assertEquals(-6.213, reg.getB(0), 0.01);
        assertEquals(-3.207, reg.getB(1), 0.01);
    }

    public void testSSd() {
        assertEquals(98222, reg.getSSd(), 0.1);
    }

    public void testSSr() {
        assertEquals(10354, reg.getSSr(), 0.1);
    }
    */
    /*
    public void testR2() {
        assertEquals(1.0, reg.getR2(), 0.01);
    }*/

    /*
    public void testRX() {
        assertEquals(0.882, reg.getR2(0), 0.01);
        assertEquals(0.719, reg.getR2(1), 0.01);
    } */

    /*
    public void testgetSS() {
        assertEquals(1479.1, reg.getSS(), 0.1);
    } */

    public void testGetY() {
        double x[];

        x = new double[]{1,-4, 4, 8};
        assertEquals(4.16, reg.getY(x), 0.1);

        x = new double[]{1,-5, 5, 10};
        assertEquals(5.0, reg.getY(x), 0.1);

        x = new double[]{1,0, 0, 0};
        assertEquals(0.0, reg.getY(x), 0.1);

        x = new double[] {1,5, -5, -10};
        assertEquals(-5.0, reg.getY(x), 0.1);
    }
}
