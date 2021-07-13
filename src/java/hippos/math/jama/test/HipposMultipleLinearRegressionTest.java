package hippos.math.jama.test;

import hippos.math.jama.HipposMultipleLinearRegression;
import hippos.math.jama.MultipleLinearRegression;
import junit.framework.TestCase;

/**
 * Created by marktolo on 30.11.2014.
 */
public class HipposMultipleLinearRegressionTest extends TestCase {
    MultipleLinearRegression jreg;
    HipposMultipleLinearRegression jreg2;

    public static void main(String args[]) {
        junit.textui.TestRunner.run(HipposMultipleLinearRegressionTest.class);
    }

    protected void setUp() {
        double jx [][] = {
                {-4,4,9},
                {-3,3,6},
                {-2,2,4},
                {-1,1,2},
                {0,0,0},
                {1,-1,-2},
                {2,-2,-4},
                {3,-3,-6},
                {4,-4,-8}};

        double jy [] = {4,3,2,1,0,-1,-2,-3,-4};
        jreg = new MultipleLinearRegression(jx, jy);

        double x1[] = {-4,4,9}, y1 = 4;
        double x2[] = {-3,3,6}, y2 = 3;
        double x3[] = {-2,2,4}, y3 = 2;
        double x4[] = {-1,1,2}, y4 = 1;
        double x5[] = {0,0,0}, y5 = 0;
        double x6[] = {1,-1,-2}, y6 = -1;
        double x7[] = {2,-2,-4}, y7 = -2;
        double x8[] = {3,-3,-6}, y8 = -3;
        double x9[] = {4,-4,-8}, y9 = -4;

        jreg2 = new HipposMultipleLinearRegression(3);
        jreg2.add(x1, y1);
        jreg2.add(x2, y2);
        jreg2.add(x3, y3);
        jreg2.add(x4, y4);
        jreg2.add(x5, y5);
        jreg2.add(x6, y6);
        jreg2.add(x7, y7);
        jreg2.add(x8, y8);
        jreg2.add(x9, y9);
    }

    public void testGetY() {
        double x[];

        x = new double[]{-5, 5, 10};
        assertEquals(5.0, jreg.get(x), 0.1);
        assertEquals(5.0, jreg2.get(x), 0.1);
        assertEquals(5.0, jreg2.getY(x), 0.1);

        x = new double[]{0, 0, 0};
        assertEquals(0.0, jreg.get(x), 0.1);
        assertEquals(0.0, jreg2.get(x), 0.1);
        assertEquals(0.0, jreg2.getY(x), 0.1);

        x = new double[] {5, -5, -10};
        assertEquals(-5.0, jreg.get(x), 0.1);
        assertEquals(-5.0, jreg2.get(x), 0.1);
        assertEquals(-5.0, jreg2.getY(x), 0.1);
    }

}
