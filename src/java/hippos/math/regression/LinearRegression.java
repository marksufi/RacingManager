package hippos.math.regression;

import hippos.database.Database;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Nov 9, 2005
 * Time: 11:40:37 PM
 * To change this template use Options | File Templates.
 */
public class LinearRegression implements Serializable {
    private int numberPlotPoints = 0;
	private double sumxx = BigDecimal.ZERO.doubleValue();
    private double sumyy = BigDecimal.ZERO.doubleValue();
    private double sumxy = BigDecimal.ZERO.doubleValue();
    private double sumx = BigDecimal.ZERO.doubleValue();
    private double sumy = BigDecimal.ZERO.doubleValue();
    //private double a, b;
    private double slope = BigDecimal.ZERO.doubleValue();
    private double intercept = BigDecimal.ZERO.doubleValue();;

    public void add(double x, double y) {
        numberPlotPoints ++;
        sumx += x;
        sumy += y;
        sumxx += x*x;
        sumyy += y*y;
        sumxy += x*y;

        slope = ( numberPlotPoints * sumxy - sumx * sumy ) / ( numberPlotPoints * sumxx - sumx * sumx );  // slope
        intercept = ( sumy - slope * sumx ) / numberPlotPoints;                            // intercept
    }

    public double get(double x){
        return intercept + slope*x;
    }

    /*
    public double get(double x){
        int n = numberPlotPoints;
        double Sxx, Sxy, Syy;

        if( n > 0 ) {
            Sxx = getSxx();
            Syy = getSyy(); //mihin tätä tarvii?
            Sxy = getSxy();
            if(Sxx != 0.0)
                b = Sxy/Sxx;
            else
                b = 0.0;
            a = (sumy-b*sumx)/n;
            return intercept + slope*x;
        }
        return 0.0;
    }*/

    public double getRValue() {
        return (numberPlotPoints*sumxy-sumx*sumy)/
                Math.sqrt(
                        ((numberPlotPoints*sumxx)-sumx*sumx)*
                        ((numberPlotPoints*sumyy)-sumy*sumy)
                );
    }

    public double getSxx() {
        int n = numberPlotPoints;

        if( n > 0 ) {
            return sumxx-sumx*sumx/n;
        }
        return 0.0;
    }


    public double getSyy() {
        int n = numberPlotPoints;

        if( n > 0 ) {
            return sumyy-sumy*sumy/n;
        }
        return 0.0;
    }

    public double getSxy() {
        int n = numberPlotPoints;

        if( n > 0 ) {
            return sumxy-sumx*sumy/n;
        }
        return 0.0;
    }

    /**
     * @return average of x values
     */
    public double getSx() {
        int n = numberPlotPoints;

        if( n > 0 ) {
            return sumx/n;
        }
        return 0.0;
    }

    /**
     * @return average of x values
     */
    public double getSy() {
        int n = numberPlotPoints;

        if( n > 0 ) {
            return sumy/n;
        }
        return 0.0;
    }

    public int getNumberPlotPoints() {
        return numberPlotPoints;
    }

    public double getSlope() {
        return slope;
    }

    public double getIntercept() {
        return intercept;
    }

    public static void main(String args []) {

        PreparedStatement statement = null;
        ResultSet set = null;
        Connection conn = null;
        LinearRegression cf = new LinearRegression();

        try {
            conn = Database.getConnection();
            statement = conn.prepareStatement("select lahtotyyppi,aika, palkinto, id from subresult");
            set = statement.executeQuery();

            List keyList = new ArrayList();
            double [] x = new double[1];

            while (set.next()) {
                keyList = new ArrayList();

                keyList.add(set.getString(1));
                BigDecimal time = set.getBigDecimal(2);

                if(time != null) {
                    x[0] = time.doubleValue();
                    BigDecimal prize = set.getBigDecimal(3);

                    if (prize != null) {

                        cf.add(time.doubleValue(), prize.doubleValue());

                        System.out.println(time + "<-" + prize);
                        System.out.println(cf.get(20));
                        System.out.println(cf.get(15));
                        System.out.println(cf.get(10));

                        if(cf.getSlope() > 0 || prize.doubleValue() > 1000000) {
                            String id = set.getString(4);
                            System.out.println("slope > 0: id=" + id);
                        }
                        System.out.println(cf.getSlope());
                        System.out.println();


                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { conn.close(); } catch (Exception e) {e.printStackTrace();}
            try { conn.close(); } catch (Exception e) {e.printStackTrace();}
            try { conn.close(); } catch (Exception e) {e.printStackTrace();}
        }

        System.out.println(cf.get(25));
        System.out.println(cf.get(20));
        System.out.println(cf.get(15));
        System.out.println(cf.get(10));

        System.out.println(cf.getSlope());
    }

}
