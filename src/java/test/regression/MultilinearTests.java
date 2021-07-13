package test.regression;

import hippos.math.matrix.Matrix;
import hippos.math.matrix.MatrixList;
import hippos.math.regression.MultiLinear;
import hippos.math.regression.MultiLinearList;
import junit.framework.TestCase;

/**
 * Created by marktolo on 7.9.2014.
 */
public class MultilinearTests extends TestCase {
    Matrix X;
    Matrix Y;
    MultiLinear ml;

    public static void main(String args[]) {
        junit.textui.TestRunner.run(MultilinearTests.class);
    }

    protected void setUp() {
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    public void test_bmi() {
        try {
            final Matrix X = new Matrix(new double[][]{{4,0,1},{7,1,1},{6,1,0},{2,0,0},{3,0,1}});
            final Matrix Y = new Matrix(new double[][]{{27},{29},{23},{20},{21}});
            final MultiLinear ml = new MultiLinear(X, Y);
            final Matrix beta = ml.calculate();

            assertNotNull(beta);
            assertTrue(Math.abs(9.25 - beta.getValueAt(0, 0)) < 1e-5);
            assertTrue(Math.abs(4.75 - beta.getValueAt(1, 0)) < 1e-5);
            assertTrue(Math.abs(-13.5 - beta.getValueAt(2, 0)) < 1e-5);
            assertTrue(Math.abs(-1.25 - beta.getValueAt(3, 0)) < 1e-5);

            printY(Y, X, beta, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void test_withbias() {
        try {
            final Matrix X = new Matrix(new double[][]{{1,1},{2,8},{3,27},{4,64},{5,125}});
            final Matrix Y = new Matrix(new double[][]{{1},{2},{2},{2},{3}});
            final MultiLinear ml = new MultiLinear(X, Y);
            final Matrix beta = ml.calculate();

            assertNotNull(beta);
            assertTrue(Math.abs(0.89655 - beta.getValueAt(0, 0)) < 1e-5);
            assertTrue(Math.abs(0.336468 - beta.getValueAt(1, 0)) < 1e-5);
            assertTrue(Math.abs(0.002089864 - beta.getValueAt(2, 0)) < 1e-5);

            printY(Y, X, beta, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void test_nobias() {
        try {
            final Matrix X = new Matrix(new double[][]{{1,1},{2,8},{3,27},{4,64},{5,125}});
            final Matrix Y = new Matrix(new double[][]{{1},{2},{2},{2},{3}});
            final MultiLinear ml = new MultiLinear(X, Y, false);
            final Matrix beta = ml.calculate();

            assertNotNull(beta);
            assertTrue(Math.abs(0.797979798 - beta.getValueAt(0, 0)) < 1e-5);
            assertTrue(Math.abs(-0.01010101 - beta.getValueAt(1, 0)) < 1e-5);

            printY(Y, X, beta, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void test3() {
        try {
            final Matrix X = new Matrix(new double[][]{{1,1},{1,2},{1,3},{1,4},{2,1},{2,2},{2,3},{2,4},{3,1},{3,2},{3,3},{3,4}});
            final Matrix Y = new Matrix(new double[][]{{1},{1},{1},{-1},{1},{1},{-1},{-1},{1},{-1},{-1},{-1}});
            final MultiLinear ml = new MultiLinear(X, Y);
            final Matrix beta = ml.calculate();

            assertNotNull(beta);
            assertTrue(Math.abs(2.666666666666667 - beta.getValueAt(0, 0)) < 1e-5);
            assertTrue(Math.abs(-0.5 - beta.getValueAt(1, 0)) < 1e-5);
            assertTrue(Math.abs(-0.6666666666666666 - beta.getValueAt(2, 0)) < 1e-5);

            printY(Y, X, beta, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void test_checkDiemnsion_nullX() {
        boolean thrown = false;
        try {
            final Matrix X = null;
            final Matrix Y = new Matrix(5, 1);
            final MultiLinear ml = new MultiLinear(X, Y);
            ml.checkDiemnsion();
        } catch (NullPointerException e) {
            thrown = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(thrown);
    }

    public void test_checkDiemnsion_nullY() {
        boolean thrown = false;
        try {
            final Matrix X = new Matrix(5, 3);
            final Matrix Y = null;
            final MultiLinear ml = new MultiLinear(X, Y);
            ml.checkDiemnsion();
        } catch (NullPointerException e) {
            thrown = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(thrown);
    }

    public void test_checkDiemnsion_bigX() {
        boolean thrown = false;
        try {
            final Matrix X = new Matrix(5, 6);
            final Matrix Y = new Matrix(5,1);
            final MultiLinear ml = new MultiLinear(X, Y);
            ml.checkDiemnsion();
        } catch (IllegalArgumentException e) {
             thrown = true;
        } catch (Exception e) {
             e.printStackTrace();
        }
        assertTrue(thrown);
    }

    public void test_checkDiemnsion_wrongY() {
        boolean thrown = false;
        try {
            final Matrix X = new Matrix(5, 6);
            final Matrix Y = new Matrix(5,1);
            final MultiLinear ml = new MultiLinear(X, Y);
            ml.checkDiemnsion();
        } catch (IllegalArgumentException e) {
            thrown = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(thrown);
    } */

    public void test_rankTime() {
        try {
            final Matrix X = new Matrix(new double[][]{{14.5, 1},{14.5, 2},{14.5, 3},{14.5, 4},{14.5, 5}});
            final Matrix Y = new Matrix(new double[][]{{1000},{900},{800},{700},{600}});
            final double Z [] = new double[] {14.5, 0};

            final MultiLinear ml = new MultiLinear(X, Y);
            final Matrix beta = ml.calculate();

            printY(Y, X, beta, true);
            printY(Z, beta, true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void test_add_rankTime() {
        try {
            MultiLinearList ml = new MultiLinearList();
            ml.add(new double[] {1, 14.5, 1}, new double[] {1000});
            ml.add(new double[] {1, 14.5, 2}, new double[] {900});
            ml.add(new double[] {1, 14.5, 3}, new double[] {800});
            ml.add(new double[] {1, 14.5, 4}, new double[] {700});
            ml.add(new double[] {1, 14.5, 5}, new double[] {600});

            System.out.println("test.regression.MultilinearTests.test_add_rankTime: " + ml.get(new double[]{1, 14.5, 0}));

            final double Z [] = new double[] {14.5, 0};
            final Matrix beta = ml.calculate();

            //printY(Y, X, beta, true);
            printY(Z, beta, true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printY(final Matrix Y, final Matrix X, final Matrix beta, final boolean bias) {
        System.out.println("observed -> predicted");
        if (bias) {
            for (int i=0;i<Y.getNrows();i++) {
                double predictedY =  beta.getValueAt(0, 0);
                for (int j=1; j< beta.getNrows(); j++) {
                    predictedY += beta.getValueAt(j, 0) * X.getValueAt(i, j-1);
                }
                System.out.println(Y.getValueAt(i, 0) + " -> " + predictedY);
            }
        } else {
            for (int i=0;i<Y.getNrows();i++) {
                double predictedY =  0.0;
                for (int j=0; j< beta.getNrows(); j++) {
                    predictedY += beta.getValueAt(j, 0) * X.getValueAt(i, j);
                }
                System.out.println(Y.getValueAt(i, 0) + " -> " + predictedY);
            }
        }
    }

    private void printY( double [] X, final Matrix beta, final boolean bias) {
        System.out.println("observed -> predicted");
        if (bias) {
            double predictedY =  beta.getValueAt(0, 0);
            for (int j=1; j< beta.getNrows(); j++) {
                predictedY += beta.getValueAt(j, 0) * X [j-1];
            }
            System.out.println(X + " -> " + predictedY);

        } else {
            double predictedY =  0.0;
            for (int j=0; j< beta.getNrows(); j++) {
                predictedY += beta.getValueAt(j, 0) * X [j];
            }
            System.out.println(X + " -> " + predictedY);
        }
    }
}
