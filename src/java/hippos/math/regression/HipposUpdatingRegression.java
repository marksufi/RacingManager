package hippos.math.regression;

import org.apache.commons.math3.exception.util.ExceptionContextProvider;
import org.apache.commons.math3.stat.regression.MillerUpdatingRegression;
import org.apache.commons.math3.stat.regression.ModelSpecificationException;
import utils.Log;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Created by marktolo on 8.12.2014.
 */
public class HipposUpdatingRegression extends MillerUpdatingRegression {

    public HipposUpdatingRegression(int numberOfVariables, boolean includeConstant) throws ModelSpecificationException {
        super(numberOfVariables, includeConstant);
    }

    public HipposUpdatingRegression(int numberOfVariables) {
        this(numberOfVariables, false);
    }

    public void add(double[] x, double y) {
        try {
            addObservation(x, y);
        } catch (Exception e) {
            Log.write(e, "(" + Arrays.toString(x) + ", " + y + ")");
        }
    }

    public void add(double x, double y) {
        try {
            addObservation(new double[]{x}, y);
        } catch (Exception e) {
            Log.write(e, "(" + x + ", " + y + ")");
        }
    }

    public double get(double[] x) throws ModelSpecificationException {

        try {
            double B[] = regress().getParameterEstimates();
            boolean itc = hasIntercept();
            double b = itc ? B[0] : 0.0;

            for(int i = 0; i < x.length; i++) {
                b += itc ? x[i] * B[i+1] : x[i] * B[i];
            }

            /*
            System.out.println();
            System.out.println("getPartialCorrelations(0): " + Arrays.toString(this.getPartialCorrelations(0)));
            System.out.println("getPartialCorrelations(1): " + Arrays.toString(this.getPartialCorrelations(1)));
            */

            return b;
        } catch (ModelSpecificationException me) {
            throw me;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public double get(double x) throws ModelSpecificationException {

        double B[] = regress().getParameterEstimates();
        boolean itc = hasIntercept();
        double b = itc ? B[0] : 0.0;

        b += itc ? x * B[1] : x * B[0];

        return b;
    }

    public HipposRegressionResults getHipposRegressionResults (double[] x) {
        double b = Double.NaN;
        double y = Double.NaN;
        double r = 0.0, ra = 0.0;
        double [][] YX = new double[2][x.length];
        try {
            double B[] = regress().getParameterEstimates();
            boolean itc = hasIntercept();
            r = regress().getRSquared();
            //ra = regress().getAdjustedRSquared();

            y = b = itc ? B[0] : 0.0;

            for(int i = 0; i < x.length; i++) {
                YX [0][i] = itc ? x[i] * B[i+1] : x[i] * B[i];
                y += YX [0][i];
            }
            YX[1] = x;
            //System.out.println("hippos.math.regression.HipposUpdatingRegression.get: " + Arrays.toString(x) + " = " + Arrays.toString(Y));
        } catch (ModelSpecificationException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }
        //y = Math.pow(y, p);

        return new HipposRegressionResults(b, y, r, YX);
    }

    public static void main(String args []) {
        boolean includeConstant = true;
        HipposUpdatingRegression reg = new HipposUpdatingRegression(2, includeConstant);

        double [] x1 = new double[] {12.3,-2};
        double [] x2 = new double[] {12.3, -1};
        double [] x3 = new double[] {15.0, 0};
        double [] x4 = new double[] {15.0, 1};
        double [] x5 = new double[] {15.0, -2};
        double [] x6 = new double[] {18.0,0};
        double [] x7 = new double[] {18.0, 2};

        double [] x8 = new double[] {1800, 1};
        double [] x9 = new double[] {-20, -1};

        try {

            reg.add(x1, 14.3);
            reg.add(x2, 13.3);
            reg.add(x3, 15);
            reg.add(x4, 16);
            reg.add(x5, 17);
            reg.add(x6, 18);
            reg.add(x7, 16);

            System.out.println(Arrays.toString(x1) + "=>" + reg.get(x1));
            System.out.println(Arrays.toString(x2) + "=>" + reg.get(x2));
            System.out.println(Arrays.toString(x3) + "=>" + reg.get(x3));
            System.out.println(Arrays.toString(x4) + "=>" + reg.get(x4));
            System.out.println(Arrays.toString(x5) + "=>" + reg.get(x5));
            System.out.println(Arrays.toString(x6) + "=>" + reg.get(x6));
            System.out.println(Arrays.toString(x7) + "=>" + reg.get(x7));

            reg.add(x8, 1801);
            reg.add(x9, -19);


            System.out.println(Arrays.toString(x1) + "=>" + reg.get(x1));
            System.out.println(Arrays.toString(x2) + "=>" + reg.get(x2));
            System.out.println(Arrays.toString(x3) + "=>" + reg.get(x3));
            System.out.println(Arrays.toString(x4) + "=>" + reg.get(x4));
            System.out.println(Arrays.toString(x5) + "=>" + reg.get(x5));
            System.out.println(Arrays.toString(x6) + "=>" + reg.get(x6));
            System.out.println(Arrays.toString(x7) + "=>" + reg.get(x7));
            System.out.println(Arrays.toString(x8) + "=>" + reg.get(x8));
            System.out.println(Arrays.toString(x9) + "=>" + reg.get(x9));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
