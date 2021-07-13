package hippos.math.jama;

/*************************************************************************
 *  Compilation:  javac -classpath jama.jar:. MultipleLinearRegression.java
 *  Execution:    java  -classpath jama.jar:. MultipleLinearRegression
 *  Dependencies: jama.jar
 *
 *  Compute least squares solution to X beta = y using Jama library.
 *  Assumes X has full column rank.
 *
 *       http://math.nist.gov/javanumerics/jama/
 *       http://math.nist.gov/javanumerics/jama/Jama-1.0.1.jar
 *
 *************************************************************************/

public class MultipleLinearRegression {
    protected int N;        // number of
    protected int p;        // number of dependent variables
    protected Matrix beta;  // regression coefficients
    protected double SSE;         // sum of squared
    protected double SST;         // sum of squared


    public MultipleLinearRegression(int size) {
        N = 0;
        p = size;
    }

    public MultipleLinearRegression(double[][] x, double[] y) {
        if (x.length != y.length) throw new RuntimeException("dimensions don't agree");
        N = y.length;
        p = x[0].length;

        Matrix X = new Matrix(x);

        // create matrix from vector
        Matrix Y = new Matrix(y, N);

        // find least squares solution
        QRDecomposition qr = new QRDecomposition(X);
        beta = qr.solve(Y);

        // mean of y[] values
        double sum = 0.0;
        for (int i = 0; i < N; i++)
            sum += y[i];
        double mean = sum / N;

        // total variation to be accounted for
        for (int i = 0; i < N; i++) {
            double dev = y[i] - mean;
            SST += dev*dev;
        }

        // variation not accounted for
        Matrix residuals = X.times(beta).minus(Y);
        SSE = residuals.norm2() * residuals.norm2();

    }

    public double beta(int j) {
        return beta.get(j, 0);
    }

    public double R2() {
        return 1.0 - SSE/SST;
    }

    public double get(double[] x) {
        double y = 0.0;
        for(int i = 0; i <  x.length; i++) {
            y += beta(i) * x[i];
        }
        return y;
    }

    public static void main(String[] args) {
        double[][] x = { {  1,  10,  20 },
                {  1,  20,  40 },
                {  1,  40,  15 },
                {  1,  80, 100 },
                {  1, 160,  23 },
                {  1, 200,  18 } };
        double[] y = { 243, 483, 508, 1503, 1764, 2129 };
        MultipleLinearRegression regression = new MultipleLinearRegression(x, y);

        System.out.println(regression.beta(0) + "  " + regression.beta(1) + "  " + regression.beta(2) + "R^2 = " + regression.R2());
    }

}

