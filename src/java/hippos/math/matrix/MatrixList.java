package hippos.math.matrix;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marktolo on 8.9.2014.
 */
public class MatrixList {
    private List datalist;

    public MatrixList() {
        this.datalist = new ArrayList();
    }

    public double[][] getValues() {
        return (double[][]) datalist.toArray();
    }

    public void setValues(double[][] values) {
        datalist.add(values);
    }

    public void setValueAt(int row, int col, double value) {
        ((double[][]) datalist.toArray())[row][col] = value;
    }

    public double getValueAt(int row, int col) {
        double [] d = (double []) datalist.get(row);
        return d[col];
    }

    public boolean isSquare() {
        return getNrows() == getNcols();
    }

    public int size() {
        if (isSquare())
            return getNrows();
        return -1;
    }

    /*
    public Matrix multiplyByConstant(double constant) {
        Matrix mat = new Matrix(nrows, ncols);
        for (int i = 0; i < nrows; i++) {
            for (int j = 0; j < ncols; j++) {
                mat.setValueAt(i, j, data[i][j] * constant);
            }
        }
        return mat;
    } */

    public int getNrows() {
        return datalist.size();
    }

    public int getNcols() {
        if(datalist.size() > 0) {
            double [] first = (double []) datalist.get(0);
            return first.length;
        }
        return 0;
    }

    public void add( double[] entry ) {
        datalist.add( entry );
    }
}
