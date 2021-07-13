package hippos.math.regression;

import hippos.math.matrix.DoubleMatrix;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: May 19, 2006
 * Time: 3:25:23 PM
 * To change this template use Options | File Templates.
 */
public class MultipleRegression implements Serializable {
    LinearRegression XX[][];    // X-values
    LinearRegression XY[];      // Y-values
    LinearRegression Y;
    private int size = 0;

    public MultipleRegression(int size) {
        try {
            XX = new LinearRegression[size][size];
            XY = new LinearRegression[size];
            for(int i = 0; i < size; i++)
                for(int j = 0; j < size; j++)
                    XX[i][j] = new LinearRegression();
            for(int i = 0; i < size; i++)
                    XY[i] = new LinearRegression();
            Y = new LinearRegression();
            this.size = size;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Add new row of x-values assisiated to y-value
     *
     * @param x1 a list of parameter values
     * @param y1 resultvalue
     */
    public void add(double[] x1, double y1) {
        try {
            for(int i = 0; i < x1.length; i++) {
                for(int j = 0, s = x1.length; j < s; j++) {
                    XX[i][j].add(x1[i], x1[j]);
                    //XX[i][j].add(x1[i], y1);
                }
            }

            for(int i = 0, s = x1.length; i < s; i++) {
                XY[i].add(x1[i], y1);
            }
            Y.add(y1, y1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DoubleMatrix getNormalMatrix() {
        int size = XX.length;
        DoubleMatrix normalGroup= new DoubleMatrix(size);
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                normalGroup.set(i, j, getSxx(i, j));
            }
        }
        return normalGroup;
    }

    public double getB(int i) {
        DoubleMatrix normal = getNormalMatrix();
        double [][] trs = normal.getTransposed();
        double b = BigDecimal.ZERO.doubleValue();
        for(int x = 0, s = trs.length; x < s; x++ ) {
            double tx = trs[i][x];
            double xy = XY[x].getSxy();
            b += tx * xy;
        }
        return b;
    }


    public double getSxx(int i, int j) {
        return XX[i][j].getSxy();
    }

    public double getSxy(int i) {
        return XY[i].getSxy();
    }

    public double getSyy() {
        return Y.getSxy();
    }

    public double getSx(int i) {
        return XX[i][i].getSx();
    }

    public double getSy(int i) {
        return XY[i].getSy();
    }

    public double getY(double[] x) {
        double sy = getSy(0);
        double y = sy;
        double n = 0.0;
        double Sb = 0.0;
        double Sx= 0.0;
        double Sy= 0.0;
        double Syy = 0.0;
        double Sxy = 0.0;
        double Sxx = 0.0;
        double Xi = 0.0;

        for(int i = 0, s = x.length; i < s; i++) {
            //double b = getB(i);
            double b = getB(i); Sb += b;
            double sx = getSx(i);
            double syy = getSyy(); Syy += syy;
            double sxy = getSxy(i); Sxy += sxy;
            double sxx = getSxx(i, i); Sxx += sxx;
            Xi += x[i];
            Sy += getSy(i);

            if(sxx != 0.0) {
                //b = sxy/sxx;
                y += b * x[i];
                y -= b * sx;
                n++;
                Sx += b * sx;
            } else {
                y += b * x[i];
                y -= b * sx;
                n += 0.0;
            }
            //System.out.println("x[" + i + "]=" + x[i] + ", y=" + y + ", b=" + b + ", sx=" + sx + ", sy=" + sy + ", syy=" + syy + ", sxy=" + sxy + ", sxx=" + sxx);
            //System.out.println("SS=" + getSS() + ", SSd=" + getSSd() + ", SSr=" + getSSr() + ", SSt=" + getSSt());
        }
        //System.out.println("Xi=" + Xi + ", y=" + y + ", Sb=" + Sb + ", Sx=" + Sx + ", Sy=" + Sy + ", Syy=" + Syy + ", Sxy=" + Sxy + ", Sxx=" + Sxx);
        return y;
        //return n != 0.0 ? y/n - 1 : y;
    }

    public double getSSt() {
        return getSyy();
    }

    public double getSSd() {
        double SSd = BigDecimal.ZERO.doubleValue();
        for(int y = 0, s = XY.length; y < s; y++) {
            double b = getB(y);
            double xy = getSxy(y);
            SSd += b * xy;
        }
        return SSd/XY.length;
    }

    public double getSSr() {
        return getSSt() - getSSd();
    }

    public double getR2() {
        return getSSd()/getSSt();
    }

    public double getSS() {
        return getSSr() / (Y.getNumberPlotPoints() - XX.length - 1);
    }

    /**
     * Palauttaa lista-alkio x:n varianssi
     *
     * @param x
     * @return
     */
    public double getR2(int x) {
        DoubleMatrix normalMatrix = getNormalMatrix();
        return normalMatrix.getTransposed()[x][x]*getSS();
    }


    /*
    public static void main(String args []) {
        MultipleRegression reg = new MultipleRegression(5);

        double [] x1 = new double [] {1, 1, -2, 4};
        double [] x2 = new double [] {1, 2, -4, 8};
        double [] x3 = new double [] {1, 3, -6, 12};

        reg.add(x1, 2);
        reg.add(x2, 4);
        reg.add(x3, 6);

        System.out.println(reg.getY(x1));
        System.out.println(reg.getY(x2));
        System.out.println(reg.getY(x3));
        System.out.println(reg.getR2());
    }*/

    public static void main(String args []) {
        double [] x1 = new double [] {3, 1, -2, 4};
        double [] x2 = new double [] {2, 2, -4, 8};
        double [] x3 = new double [] {1, 3, -6, 12};

        MultipleRegression reg = new MultipleRegression(x1.length);

        reg.add(x1, 1);
        reg.add(x2, 2);
        reg.add(x3, 3);
        /*
        double x1[] = {1,-4,4,8}, y1 = 4;
        double x2[] = {1,-3,3,6}, y2 = 3;
        double x3[] = {1,-2,2,4}, y3 = 2;
        double x4[] = {1,-1,1,2}, y4 = 1;
        double x5[] = {1,0,0,0}, y5 = 0;
        double x6[] = {1,1,-1,-2}, y6 = -1;
        double x7[] = {1,2,-2,-4}, y7 = -2;
        double x8[] = {1,3,-3,-6}, y8 = -3;
        double x9[] = {1,4,-4,-8}, y9 = -4;

        MultipleRegression reg = new MultipleRegression(x1.length);
        reg.add(x1, y1);
        reg.add(x2, y2);
        reg.add(x3, y3);
        reg.add(x4, y4);
        reg.add(x5, y5);
        reg.add(x6, y6);
        reg.add(x7, y7);
        reg.add(x8, y8);
        reg.add(x9, y9);
        */

        System.out.println(reg.getY(x1));
        System.out.println(reg.getY(x2));
        System.out.println(reg.getY(x3));
        System.out.println(reg.getR2());
    }


}
