package hippos.math.jama;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by marktolo on 30.11.2014.
 */
public class HipposMultipleLinearRegression extends MultipleLinearRegression {
    // omat jutut
    private double sum = 0.0;
    private Matrix X, Y;
    ArrayList XL = new ArrayList();
    ArrayList YL = new ArrayList();
    QRDecomposition QR;

    public HipposMultipleLinearRegression(int size) {
        super(size);
        X = new Matrix(size);
        Y = new Matrix(1);
        QR = new QRDecomposition(size);
    }

    public void add(double [] x, double y) {
        //X.add(x);
        //Y.add(y);
        XL.add(x);
        YL.add(new double[] {y});

        N++;
        sum += y;
        double mean = sum / N;
        double dev = y - mean;
        SST += dev*dev;

        //QR.add(x);


    }

    public double get(double[] x) {
        // find least squares solution

        QRDecomposition qr = new QRDecomposition(X);
        beta = qr.solve(Y);

        // variation not accounted for
        Matrix residuals = X.times(beta).minus(Y);
        SSE = residuals.norm2() * residuals.norm2();

        double y = 0.0;
        for(int i = 0; i <  x.length; i++) {
            y += beta(i) * x[i];
        }
        return y;
    }

    public double getY(double[] x) {
        // find least squares solution
        Iterator itr = XL.iterator();
        double [][] XLL = new double[XL.size()][p];
        for(int i = 0; i < XL.size(); i++) {
            XLL [i] = (double []) itr.next();
        }

        Iterator itrY = YL.iterator();
        double [] YLL = new double[YL.size()];
        for(int i = 0; i < YL.size(); i++) {
            YLL [i] = ((double []) itrY.next())[0];
        }

        MultipleLinearRegression MLR = new MultipleLinearRegression(XLL, YLL);
        return MLR.get(x);
    }

    /*
    public double get(double [] x) {
        MultipleLinearRegression MLR = new MultipleLinearRegression(X.A, Y.A);
        return MLR.get(x);
    }*/

    public static void main(String args[]) {
        HipposMultipleLinearRegression jreg = new HipposMultipleLinearRegression(3);

        double x1[] = {-4,4,9}, y1 = 4;
        double x2[] = {-3,3,6}, y2 = 3;
        double x3[] = {-2,2,4}, y3 = 2;
        double x4[] = {-1,1,2}, y4 = 1;
        double x5[] = {0,0,0}, y5 = 0;
        double x6[] = {1,-1,-2}, y6 = -1;
        double x7[] = {2,-2,-4}, y7 = -2;
        double x8[] = {3,-3,-6}, y8 = -3;
        double x9[] = {4,-4,-8}, y9 = -4;

        jreg.add(x1, y1);
        jreg.add(x2, y2);
        jreg.add(x3, y3);
        jreg.add(x4, y4);
        jreg.add(x5, y5);
        jreg.add(x6, y6);
        jreg.add(x7, y7);
        jreg.add(x8, y8);
        jreg.add(x9, y9);

        System.out.println(jreg.get(x1));

    }


}
