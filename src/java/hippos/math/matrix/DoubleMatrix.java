package hippos.math.matrix;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: May 24, 2006
 * Time: 8:47:32 PM
 * To change this template use Options | File Templates.
 */
public class DoubleMatrix implements Serializable {
    private double matrix[][];

    public DoubleMatrix(int size) {
        matrix = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = BigDecimal.ZERO.doubleValue();
            }
        }
    }

    public double [][] getTransposed() {
        double [][] unitMatrix = getUnitMatrix(matrix.length);
        double [][] m = matrix;
        int s = m.length;
        for(int y = 0; y < s; y++) {
            if(m[y][y] != BigDecimal.ZERO.doubleValue()) {
                for(int yn = (y + 1) % s; yn != y; yn = (yn + 1) % s) {
                    double multiplier = Math.abs(m[yn][y])/m[y][y];
                    for(int x = 0; x < s; x++) {
                        //System.out.print("[" + y + "," + yn + "," + x + "]: " + m[yn][x] + " -= " + m[y][x] + " * " + multiplier);
                        m[yn][x] -= m[y][x]*multiplier;
                        //System.out.println(" = " + m[yn][x]);
                        unitMatrix[yn][x] -= unitMatrix[y][x] * multiplier;
                    }
                }
            }
        }
        for(int y = 0; y < s; y++) {
            double divider = m[y][y];
            if(divider != BigDecimal.ZERO.doubleValue()) {
                for(int x = 0; x < s; x++) {
                    m[y][x] /= divider;
                    unitMatrix[y][x] /= divider;
                }
            }
        }
        return unitMatrix;
    }

    public void set(int i, int j, double sxx) {
        matrix[i][j] = sxx;
    }

    public double get(int i, int j) {
        return matrix[i][j];
    }

    private double [][] getUnitMatrix(int size) {
        double unitMatrix [][] = new double[size][size];
        for(int y = 0, s = size; y < s; y++) {
            for(int x = 0; x < s; x++) {
                if(y != x)
                    unitMatrix [y][x] = BigDecimal.ZERO.doubleValue();
                else
                    unitMatrix [y][x] = BigDecimal.ONE.doubleValue();
            }
        }
        return unitMatrix;
    }

    public double [][] getMatrix() {
        return matrix;
    }

}
