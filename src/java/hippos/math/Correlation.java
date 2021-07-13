package hippos.math;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: marktolo
 * Date: 22.7.2013
 * Time: 22:37
 * To change this template use File | Settings | File Templates.
 */
public class Correlation implements Serializable {
    int numberPlotPoints =0;
    double sumxx = 0;
    double sumyy = 0;
    double sumxy = 0;
    double sumx = 0;
    double sumy = 0 ;
    double Sxx, Sxy, Syy;
    double a, b;    	//regression coefficients
    int aNull = 0;
    int bNull = 0;

    public void add(double x, double y){
        numberPlotPoints++;
        sumx += x;
        sumy += y;
        sumxx += x*x;
        sumyy += y*y;
        sumxy += x*y;
    }

    public void add(int x, int y){
        add((double)x, (double)y);
    }

    public void add(BigDecimal x, BigDecimal y){
        add(x.doubleValue(), y.doubleValue());
    }

    public void add(BigDecimal a, BigDecimal b, BigDecimal target){
        if(a != null && b!= null) {
            add(a.subtract(b), target);
        } else {
            if(a == null) aNull++;
            if(b == null) bNull++;
            numberPlotPoints++;
        }
    }

    public double get(double x){
        double n = (double)numberPlotPoints;

        if( n > 0 ) {
            Sxx = sumxx-sumx*sumx/n;
            Syy = sumyy-sumy*sumy/n;
            Sxy = sumxy-sumx*sumy/n;
            if(Sxx != 0)
                b = Sxy/Sxx;
            else
                b = 0;
            a = (sumy-b*sumx)/n;

            return a + b*x;
        }
        return 0;
    }

    public BigDecimal get(BigDecimal x){
        return new BigDecimal(get(x.doubleValue()));
    }

    public BigDecimal get(BigDecimal x, int scale){
        return get(x).setScale(scale, BigDecimal.ROUND_HALF_UP);
    }

    public static void main(String args[]) {
        Correlation c = new Correlation();

        c.add(10, 2);
        c.add(20, 4);
        c.add(-10, -2);
        c.add(-20, -4);

        System.out.println("Correlation.main: " +  c.get(25));
        System.out.println("Correlation.main: " +  c.get(5));
        System.out.println("Correlation.main: " +  c.get(-5));
        System.out.println("Correlation.main: " +  c.get(-25));

        c = new Correlation();

        c.add(10, -2);
        c.add(20, -4);
        c.add(-10, 2);
        c.add(-20, 4);

        System.out.println("Correlation.main: " +  c.get(25));
        System.out.println("Correlation.main: " +  c.get(5));
        System.out.println("Correlation.main: " +  c.get(-5));
        System.out.println("Correlation.main: " +  c.get(-25));

        c = new Correlation();

        c.add(10, 1);
        c.add(20, 0);
        c.add(-10, 0);
        c.add(-20, 1);

        System.out.println("Correlation.main: " +  c.get(25));
        System.out.println("Correlation.main: " +  c.get(5));
        System.out.println("Correlation.main: " +  c.get(-5));
        System.out.println("Correlation.main: " +  c.get(-25));


        c = new Correlation();

        c.add(20, 100);
        c.add(19, 200);
        c.add(18, 300);
        c.add(21, 0);

        System.out.println("Correlation.main: " +  c.get(17));
        System.out.println("Correlation.main: " +  c.get(22));
    }

}
