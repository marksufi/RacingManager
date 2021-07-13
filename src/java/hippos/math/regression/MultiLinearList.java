package hippos.math.regression;

import hippos.math.matrix.*;

/**
 * Created by marktolo on 8.9.2014.
 */
public class MultiLinearList {
    private MatrixList X;
    private MatrixList Y;

    public MultiLinearList() {
        this.X = new MatrixList();
        this.Y = new MatrixList();
    }

    /**
     * beta a matrix which gives the coefficients of a multi-linear regression equation Y = sum_of(beta_i * x_i)
     * beta = Inverse_of(X_transpose * X) * X_transpose * Y
     * @return
     * @throws hippos.math.matrix.NoSquareException
     */
    public Matrix calculate() throws NoSquareException {
        checkDiemnsion();
        final Matrix Xtr = ListMatrixMathematics.transpose(X); //X'
        final Matrix XXtr = ListMatrixMathematics.multiply(Xtr,X); //XX'
        final Matrix inverse_of_XXtr = ListMatrixMathematics.inverse(XXtr); //(XX')^-1

        if (inverse_of_XXtr == null) {
            System.out.println("Matrix X'X does not have any inverse. So MLR failed to create the model for these data.");
            return null;
        }

        final Matrix XtrY = ListMatrixMathematics.multiply(Xtr,Y); //X'Y
        return MatrixMathematics.multiply(inverse_of_XXtr,XtrY); //(XX')^-1 X'Y
    }

    /**
     * Preconditions checks
     */
    public void checkDiemnsion() {
        if (X == null)
            throw new NullPointerException("X matrix cannot be null.");
        if (Y == null)
            throw new NullPointerException("Y matrix cannot be null.");

        if (X.getNcols() > X.getNrows()) {
            throw new IllegalArgumentException("The number of columns in X matrix (descriptors) cannot be more than the number of rows");
        }
        if (X.getNrows() != Y.getNrows()) {
            throw new IllegalArgumentException("The number of rows in X matrix should be the same as the number of rows in Y matrix. ");
        }
    }

    public void add(double[] X, double[] Y) {
        this.X.add( X );
        this.Y.add( Y );
    }

    public double get(double[] X) {
        try {
            Matrix beta = calculate();
            double predictedY =  beta.getValueAt(0, 0);
            for (int j=1; j< beta.getNrows(); j++) {
                predictedY += beta.getValueAt(j, 0) * X [j-1];
            }
            return predictedY;
        } catch (NoSquareException e) {
            e.printStackTrace();
        }
        return Double.NaN;

    }
}
