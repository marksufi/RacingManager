package test.regression;

import hippos.math.regression.HipposUpdatingRegression;
import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: May 24, 2006
 * Time: 7:00:12 PM
 * To change this template use Options | File Templates.
 */
public class HipposUpdatingRegressionTest extends TestCase {
    HipposUpdatingRegression reg;

    public static void main(String args[]) {
        junit.textui.TestRunner.run(MultipleRegressionTest.class);
    }

    protected void setUp1() {
        double x1[] = {-5,4,8}, y1 = -4;
        double x2[] = {-3,5,6}, y2 = -3;
        double x3[] = {-2,2,5}, y3 = -2;
        double x4[] = {-1,1,2}, y4 = -1;
        double x5[] = {0,0,0}, y5 = 0;
        double x6[] = {1,-1,-2}, y6 = 1;
        double x7[] = {2,-2,-4}, y7 = 2;
        double x8[] = {3,-3,-6}, y8 = 3;
        double x9[] = {4,-4,-8}, y9 = 4;

        reg = new HipposUpdatingRegression(x1.length);
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

    protected void setUp2() {
        double x1[] = {-4,5,8}, y1 = 5;
        double x2[] = {-3,3,6}, y2 = 3;
        double x3[] = {-2,2,4}, y3 = 2;
        double x4[] = {-1,1,2}, y4 = 1;
        double x5[] = {0,0,0}, y5 = 0;
        double x6[] = {1,-1,-2}, y6 = -1;
        double x7[] = {2,-2,-4}, y7 = -2;
        double x8[] = {3,-3,-6}, y8 = -3;
        double x9[] = {4,-4,-8}, y9 = -4;

        reg = new HipposUpdatingRegression(x1.length);
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

    protected void setUp3() {
        double x1[] = {-4,4,9}, y1 = 8;
        double x2[] = {-3,3,6}, y2 = 6;
        double x3[] = {-2,2,4}, y3 = 4;
        double x4[] = {-1,1,2}, y4 = 2;
        double x5[] = {0,0,0}, y5 = 0;
        double x6[] = {1,-1,-2}, y6 = -2;
        double x7[] = {2,-2,-4}, y7 = -4;
        double x8[] = {3,-3,-6}, y8 = -6;
        double x9[] = {4,-4,-8}, y9 = -8;

        reg = new HipposUpdatingRegression(x1.length);
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

    public void testGet1() {
        setUp1();
        double x[];

        /*
        x = new double[]{-4, 4, 8};
        try {
            assertEquals(-4.0, reg.get(x)[0], 0.1);
        } catch (EntryItemNullException e) {
            e.printStackTrace();
        }

        x = new double[]{-5, 5, 10};
        assertEquals(-5.0, reg.get(x), 0.1);

        x = new double[]{0, 0, 0};
        assertEquals(0.0, reg.get(x), 0.1);

        x = new double[] {5, -5, -10};
        assertEquals(5.0, reg.get(x), 0.1);
        */
    }

    public void testGet2() {
        setUp2();
        double x[];
        /*
        x = new double[]{-4, 4, 8};
        assertEquals(4.0, reg.get(x), 0.1);

        x = new double[]{-5, 5, 10};
        assertEquals(5.0, reg.get(x), 0.1);

        x = new double[]{0, 0, 0};
        assertEquals(0.0, reg.get(x), 0.1);

        x = new double[] {5, -5, -10};
        assertEquals(-5.0, reg.get(x), 0.1);
        */
    }

    public void testGet3() {
        setUp3();
        double x[];
        /*
        x = new double[]{-4, 4, 8};
        assertEquals(8.0, reg.get(x), 0.1);

        x = new double[]{-5, 5, 10};
        assertEquals(10.0, reg.get(x), 0.1);

        x = new double[]{0, 0, 0};
        assertEquals(0.0, reg.get(x), 0.1);

        x = new double[] {5, -5, -10};
        assertEquals(-10.0, reg.get(x), 0.1);
        */
    }

}
