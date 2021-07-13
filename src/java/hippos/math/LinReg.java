package hippos.math;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Nov 9, 2005
 * Time: 11:40:37 PM
 * To change this template use Options | File Templates.
 */
public class LinReg implements Serializable {
    private BigDecimal pow = BigDecimal.ONE;
    //private static final boolean usePow = true;

    int numberPlotPoints = 0;
	double sumxx = 0;
	double sumyy = 0;
	double sumxy = 0;
	double sumx = 0;
	double sumy = 0 ;
	double Sxx, Sxy, Syy;
	double a, b;    	//regression coefficients


    public LinReg() { }

    public LinReg(BigDecimal pow) {
        this.pow = pow;
    }

    public void add(double x, double y){
        if(pow != null) {
            if(y > 0.0) {
                y = Math.pow(y, 1.0 / pow.doubleValue());
                //if(y != Double.NaN ) {
                numberPlotPoints++;
                sumx += x;
                sumy += y;
                sumxx += x * x;
                sumyy += y * y;
                sumxy += x * y;
            }
        } else {
            numberPlotPoints++;
            sumx += x;
            sumy += y;
            sumxx += x * x;
            sumyy += y * y;
            sumxy += x * y;
        }
    }

    /*
    public void add(double x, double y){
        y = Math.pow(y, 2);
        add((double)x, (double)y);
    }*/

    public void add(BigDecimal x, BigDecimal y){
        if(x != null && y != null) {
            add(x.doubleValue(), y.doubleValue());
        }
    }

    /*
    public void add(BigDecimal a, BigDecimal b, BigDecimal target){
        if(a != null && b!= null) {
            add(a.subtract(b), target);
        } else {
            if(a == null) aNull++;
            if(b == null) bNull++;
            numberPlotPoints++;
        }
    }*/

    public double get(double x){

        double n = (double)numberPlotPoints;

        Sxx = sumxx-sumx*sumx/n;
        Syy = sumyy-sumy*sumy/n;
        Sxy = sumxy-sumx*sumy/n;
        if(Sxx != 0)
            b = Sxy/Sxx;
        else
            b = 0;
        a = (sumy-b*sumx)/n;

        double y = a + b*x;

        if(pow != null)
            y = Math.pow(y, pow.doubleValue());

        return y;
    }

    public BigDecimal get(BigDecimal x){
        try {
            return new BigDecimal(get(x.doubleValue()));
        } catch (Exception e) {

        }
        return null;
    }

    public BigDecimal get(BigDecimal x, int scale){
        try {
            return get(x).setScale(scale, BigDecimal.ROUND_HALF_UP);
        } catch (Exception e) {

        }
        return null;
    }

    public double getRValue() {
        return (numberPlotPoints*sumxy-sumx*sumy)/
                Math.sqrt(
                        ((numberPlotPoints*sumxx)-sumx*sumx)*
                                ((numberPlotPoints*sumyy)-sumy*sumy)
                );
    }

    public double getRValue2() {
        double a = (numberPlotPoints * sumxy) - (sumx * sumy);
        double b = (numberPlotPoints * sumxx) - (sumx * sumx);
        double c = (numberPlotPoints * sumyy) - (sumy * sumy);

        return a / Math.sqrt(b * c);
    }

    public double getSxx() {
        double n = (double)numberPlotPoints;
        if(n > 0) {
            return sumxx-sumx*sumx/n;
        }
        return 0;
    }

    public double getSyy() {
        double n = (double)numberPlotPoints;
        if(n > 0) {
            return sumyy-sumy*sumy/n;
        }
        return 0;
    }

    public double getSxy() {
        double n = (double)numberPlotPoints;
        if(n > 0) {
            return sumxy-sumx*sumy/n;
        }
        return 0;
    }

    /**
     * @return average of x values
     */
    public double getSx() {
        double n = (double)numberPlotPoints;
        if(n > 0) {
            return sumx/n;
        }
        return 0;
    }

    /**
     * @return average of x values
     */
    public double getSy() {
        double n = (double)numberPlotPoints;
        if(n > 0) {
            return sumy/n;
        }
        return 0;
    }

    public static void main(String [] args) {
        //LinReg reg = new LinReg(BigDecimal.valueOf(11));
        LinReg reg = new LinReg(BigDecimal.valueOf(1));


        /*
        1
        System.out.println(reg.getRValue());
        */


        //Segmented piecewise broken-stick regression

        reg.add(3, 1);
        reg.add(2, 1);
        reg.add(1, 1);
        reg.add(0.5, 1);
        reg.add(-0.5, -1);
        reg.add(-1, -1);
        reg.add(-2, -1);
        reg.add(-3, -1);

        System.out.println(reg.get(3));
        System.out.println(reg.get(2));
        System.out.println(reg.get(1));
        System.out.println(reg.get(-1));
        System.out.println(reg.get(-2));
        System.out.println(reg.get(-3));

    }
}
