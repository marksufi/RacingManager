package hippos.math.regression;

import org.apache.commons.math3.exception.util.ExceptionContextProvider;
import org.apache.commons.math3.stat.regression.MillerUpdatingRegression;
import org.apache.commons.math3.stat.regression.ModelSpecificationException;
import utils.Log;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Arrays;

/**
 * Created by marktolo on 8.12.2014.
 */
public class HipposUpdatingRegression extends MillerUpdatingRegression {

    public HipposUpdatingRegression(int numberOfVariables, boolean includeConstant) throws ModelSpecificationException {
        super(numberOfVariables, includeConstant);
        //System.out.println("HipposUpdatingRegression.HipposUpdatingRegression(" + numberOfVariables + ", " + includeConstant + ")");
    }

    public HipposUpdatingRegression(int numberOfVariables) {

        this(numberOfVariables, false);
    }

    public void add(double[] x, double y) {
        try {
            addObservation(x, y);

        } catch (Exception e) {
            Log.write(e, Arrays.toString(x) + y);
            throw e;

        }
    }

    public void add(double x, double y) {
        try {
            addObservation(new double[]{x}, y);
        } catch (Exception e) {
            e.printStackTrace();
            //Log.write(e, "(" + x + ", " + y + ")");
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
            System.out.println("getPartialCorrelations(X): " + Arrays.toString(x));
            System.out.println("getPartialCorrelations(B): " + Arrays.toString(B));
            System.out.println("getPartialCorrelations(0): " + Arrays.toString(this.getPartialCorrelations(0)));
            System.out.println("getPartialCorrelations(1): " + Arrays.toString(this.getPartialCorrelations(1)));
            System.out.println("getPartialCorrelations(2): " + Arrays.toString(this.getPartialCorrelations(2)));
            System.out.println("getDiagonalOfHatMatrix(x): " + this.getDiagonalOfHatMatrix(x));
            System.out.println("getOrderOfRegressors(): " + Arrays.toString(this.getOrderOfRegressors()));
            System.out.println("getParameterEstimates(): " + Arrays.toString(regress().getParameterEstimates()));

            System.out.println("getCovarianceOfParameters(1, 0): " + regress().getCovarianceOfParameters(1, 0));
            System.out.println("getCovarianceOfParameters(0, 1): " + regress().getCovarianceOfParameters(0, 1));
            System.out.println("getCovarianceOfParameters(0, 2): " + regress().getCovarianceOfParameters(0, 2));
            System.out.println("getCovarianceOfParameters(1, 2): " + regress().getCovarianceOfParameters(1, 2));
            */

            return b;
        } catch (ModelSpecificationException me) {
            throw me;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public double [] getWithR(double[] x) throws ModelSpecificationException {
        double [] value = new double[2];

        try {
            double B[] = regress().getParameterEstimates();
            boolean itc = hasIntercept();
            double b = itc ? B[0] : 0.0;
            //BigDecimal bb = itc ? BigDecimal.valueOf(B[0]) : BigDecimal.ZERO;

            for(int i = 0; i < x.length; i++) {
                b += itc ? x[i] * B[i+1] : x[i] * B[i];
                //bb = bb.add(BigDecimal.valueOf(itc ? x[i] * B[i+1] : x[i] * B[i]));
            }
            /*
            System.out.println();
            System.out.println("getPartialCorrelations(X): " + Arrays.toString(x));
            System.out.println("getPartialCorrelations(B): " + Arrays.toString(B));
            for(int i : super.getOrderOfRegressors()) {
                System.out.println("getPartialCorrelations(" + i + "): " + Arrays.toString(this.getPartialCorrelations(i)));
            }
            System.out.println("getDiagonalOfHatMatrix(x): " + this.getDiagonalOfHatMatrix(x));
            System.out.println("getOrderOfRegressors(): " + Arrays.toString(this.getOrderOfRegressors()));
            System.out.println("getParameterEstimates(): " + Arrays.toString(regress().getParameterEstimates()));
            System.out.println("regress():" + regress().getRSquared());
            System.out.println("regress(1):" + regress(1).getRSquared());
            System.out.println("regress(2):" + regress(2).getRSquared());
            /*
            System.out.println("getCovarianceOfParameters(1, 0): " + regress().getCovarianceOfParameters(1, 0));
            System.out.println("getCovarianceOfParameters(0, 1): " + regress().getCovarianceOfParameters(0, 1));
            System.out.println("getCovarianceOfParameters(0, 2): " + regress().getCovarianceOfParameters(0, 2));
            System.out.println("getCovarianceOfParameters(0, 3): " + regress().getCovarianceOfParameters(0, 3));
            System.out.println("getCovarianceOfParameters(1, 2): " + regress().getCovarianceOfParameters(1, 2));
            System.out.println("getCovarianceOfParameters(1, 3): " + regress().getCovarianceOfParameters(1, 3));
            System.out.println("getCovarianceOfParameters(2, 3): " + regress().getCovarianceOfParameters(2, 3));
            */

            value[0] = b;
            //value[0] = bb.doubleValue();
            value[1] = 1.0;
            //value[1] = regress().getRSquared();
        } catch (ModelSpecificationException e) {
            throw e;
        } catch (NumberFormatException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return value;
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

        double [] x1 = new double[] {12.0, 7};
        double [] x2 = new double[] {13.0, 6};
        double [] x3 = new double[] {14.0, 5};
        double [] x4 = new double[] {15.0, 4};
        double [] x5 = new double[] {16.0, 3};
        double [] x6 = new double[] {17.0, 2};
        double [] x7 = new double[] {18.0, 1};

        try {

            reg.add(x1, 1);
            reg.add(x2, 2);
            reg.add(x3, 3);
            reg.add(x4, 4);
            reg.add(x5, 5);
            reg.add(x6, 6);
            reg.add(x7, 7);

            System.out.println(Arrays.toString(x1) + "=>" + Arrays.toString(reg.getWithR(x1)));
            System.out.println(Arrays.toString(x2) + "=>" + Arrays.toString(reg.getWithR(x2)));
            System.out.println(Arrays.toString(x3) + "=>" + Arrays.toString(reg.getWithR(x3)));
            System.out.println(Arrays.toString(x4) + "=>" + Arrays.toString(reg.getWithR(x4)));
            System.out.println(Arrays.toString(x5) + "=>" + Arrays.toString(reg.getWithR(x5)));
            System.out.println(Arrays.toString(x6) + "=>" + Arrays.toString(reg.getWithR(x6)));
            System.out.println(Arrays.toString(x7) + "=>" + Arrays.toString(reg.getWithR(x7)));

            /*
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
            */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        try {
            sb.append("HipposUpdatingRegression: ");
            sb.append(super.getN());
            sb.append(Arrays.toString(super.getOrderOfRegressors()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

}
