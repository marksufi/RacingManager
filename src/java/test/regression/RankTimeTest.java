package test.regression;

import hippos.math.regression.MultipleRegression;
import junit.framework.TestCase;

/**
 * Created by marktolo on 7.9.2014.
 */
public class RankTimeTest extends TestCase {
    MultipleRegression reg;
    final int size = 2;

    public static void main(String args[]) {
        junit.textui.TestRunner.run(RankTimeTest.class);
    }

    protected void setUp() {
        double x1[] = {14.5, 1}, y1 = 1000;
        double x2[] = {14.5, 2}, y2 = 900;
        double x3[] = {14.5, 3}, y3 = 800;
        double x4[] = {14.5, 4}, y4 = 700;
        double x5[] = {14.5, 5}, y5 = 600;

        reg = new MultipleRegression(size);
        reg.add(x1, y1);
        reg.add(x2, y2);
        reg.add(x3, y3);
        reg.add(x4, y4);
        reg.add(x5, y5);
    }

    public void testRanks() {
        System.out.println("test.regression.RankTimeTest.testRankTimes: " + reg.getY(new double[] {14.5, 0}));
        System.out.println("test.regression.RankTimeTest.testRankTimes: " + reg.getY(new double[] {14.5, 6}));
        assertEquals(1100.0, reg.getY(new double[]{14.5, 0}), 0);
        assertEquals(500.0, reg.getY(new double[]{14.5, 6}), 0);
    }

    public void testTimes() {
        double x1[] = {14.5, 1}, y1 = 1000;
        double x2[] = {14.6, 1}, y2 = 900;
        double x3[] = {14.7, 1}, y3 = 800;
        double x4[] = {14.8, 1}, y4 = 700;
        double x5[] = {14.9, 1}, y5 = 600;

        reg = new MultipleRegression(size);
        reg.add(x1, y1);
        reg.add(x2, y2);
        reg.add(x3, y3);
        reg.add(x4, y4);
        reg.add(x5, y5);

        System.out.println("test.regression.RankTimeTest.testTimes: " + reg.getY(new double[] {15.5, 1}));
        System.out.println("test.regression.RankTimeTest.testTimes: " + reg.getY(new double[] {13.5, 1}));
        assertEquals(2000.0, reg.getY(new double[]{13.5, 1}), 0.1);
        assertEquals(1100.0, reg.getY(new double[]{14.4, 1}), 0.1);
        assertEquals(500.0, reg.getY(new double[]{15.0, 1}), 0.1);
        assertEquals(0.0, reg.getY(new double[]{15.5, 1}), 0.1);
    }

    public void testRankTimes() {
        double x1[] = {15.8, 5}, y1 = 800;
        double x2[] = {14.5, 6}, y2 = 1500;
        double x3[] = {14.4, 4}, y3 = 1521;
        double x4[] = {14.5, 5}, y4 = 1320;
        double x5[] = {13.8, 2}, y5 = 2000;

        reg = new MultipleRegression(size);
        reg.add(x1, y1);
        reg.add(x2, y2);
        reg.add(x3, y3);
        reg.add(x4, y4);
        reg.add(x5, y5);

        System.out.println("test.regression.RankTimeTest.testRankTimes: " + reg.getY(new double[] {14.5, 5}));
        System.out.println("test.regression.RankTimeTest.testRankTimes: " + reg.getY(new double[] {14.5, 6}));
    }

}
