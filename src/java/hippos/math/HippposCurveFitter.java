package hippos.math;

import hippos.database.Database;
import org.apache.commons.math3.analysis.function.Gaussian;
import org.apache.commons.math3.fitting.GaussianCurveFitter;
import org.apache.commons.math3.fitting.HarmonicCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.apache.commons.math3.optimization.fitting.CurveFitter;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Markus on 12.2.2016.
 */
public class HippposCurveFitter {
    private int LEVEL = 2;
    private Map rMap = new TreeMap<String, WeightedObservedPoints>();
    double[] coeff;
    int size = 0;

    public HippposCurveFitter() {}

    public HippposCurveFitter(int level) {
        this.LEVEL = level;
    }

    public void add(String s, BigDecimal t, BigDecimal p) {
        try {
            if (t != null && p != null && s.equals("Lake")) {
                WeightedObservedPoints obs = null;

                if ((obs = (WeightedObservedPoints) rMap.get(s)) == null)
                    obs = new WeightedObservedPoints();

                obs.add(t.doubleValue(), p.doubleValue());
                if (obs.toList().size() > 3) {
                    //coeff = new GaussianCurveFitter.ParameterGuesser(obs.toList()).guess();
                    try {
                        coeff = GaussianCurveFitter.create().withMaxIterations(1000).fit(obs.toList());
                        System.out.println(t + "=" + new Gaussian.Parametric().value(t.doubleValue(), coeff));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                rMap.put(s, obs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BigDecimal get(String s, BigDecimal t) {
        WeightedObservedPoints obs = null;
        if(t != null && (obs = (WeightedObservedPoints) rMap.get(s)) != null) {
            try {
                //PolynomialCurveFitter fitter = PolynomialCurveFitter.create(LEVEL);
                //GaussianCurveFitter fitter = GaussianCurveFitter.create();
                //double[] coeff = new GaussianCurveFitter.ParameterGuesser(obs.toList()).guess();
                //double[] coeff = GaussianCurveFitter.create().fit(obs.toList());
                //double[] coeff = fitter.fit(obs.toList());
                //double[] coeff = fitter.fit(obs.toList());

                //PolynomialFunction pf = new PolynomialFunction(coeff);
                //double v = pf.value(t.doubleValue());
                double v = new Gaussian.Parametric().value(t.doubleValue(), coeff);


                return BigDecimal.valueOf(v);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /*
    public String toString() {
        return fitter.toString();
    }*/

    public static void main(String args []) {

        PreparedStatement statement = null;
        ResultSet set = null;
        Connection conn = null;
        HippposCurveFitter cf = new HippposCurveFitter(3);

        try {
            conn = Database.getConnection();
            statement = conn.prepareStatement("select lahtotyyppi,aika, palkinto from subresult");
            set = statement.executeQuery();
            BigDecimal c;
            while (set.next()) {
                String racemode = set.getString(1);
                BigDecimal time = set.getBigDecimal(2);
                BigDecimal prize = set.getBigDecimal(3);

                cf.add(racemode, time, prize);
                //c = cf.get(racemode, time);

                //System.out.println(racemode + ": " + time + " => " + prize + " <> " + c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { conn.close(); } catch (Exception e) {e.printStackTrace();}
            try { conn.close(); } catch (Exception e) {e.printStackTrace();}
            try { conn.close(); } catch (Exception e) {e.printStackTrace();}
        }

        System.out.println(cf.get("Lake", BigDecimal.valueOf(35)));
        System.out.println(cf.get("Lake", BigDecimal.valueOf(25)));
        System.out.println(cf.get("Lake", BigDecimal.valueOf(20)));
        System.out.println(cf.get("Lake", BigDecimal.valueOf(19)));
        System.out.println(cf.get("Lake", BigDecimal.valueOf(18)));
        System.out.println(cf.get("Lake", BigDecimal.valueOf(17)));
        System.out.println(cf.get("Lake", BigDecimal.valueOf(16)));
        System.out.println(cf.get("Lake", BigDecimal.valueOf(15)));
        System.out.println(cf.get("Lake", BigDecimal.valueOf(14)));
        System.out.println(cf.get("Lake", BigDecimal.valueOf(13)));
        System.out.println(cf.get("Lake", BigDecimal.valueOf(12)));
        System.out.println(cf.get("Lake", BigDecimal.valueOf(11)));
        System.out.println(cf.get("Lake", BigDecimal.valueOf(10)));

        System.out.println(cf.toString());
        /*
        final WeightedObservedPoints obs = new WeightedObservedPoints();
        obs.add(23, 12);
        obs.add(22, 25);
        obs.add(21, 50);
        obs.add(20, 100);
        obs.add(19, 200);
        obs.add(18, 400);
        obs.add(17, 800);
        obs.add(16, 1600);
        obs.add(15, 3200);
        obs.add(14, 6400);
        */

// Instantiate a third-degree polynomial fitter.
        //final PolynomialCurveFitter fitter = PolynomialCurveFitter.create(5);


// Retrieve fitted parameters (coefficients of the polynomial function).
        /*
        final double[] coeff = fitter.fit(obs.toList());

        PolynomialFunction pf = new PolynomialFunction(coeff);

        System.out.println(pf.value(25));
        System.out.println(pf.value(23));
        System.out.println(pf.value(22));
        System.out.println(pf.value(18));
        System.out.println(pf.value(15));
        System.out.println(pf.value(14));
        System.out.println(pf.value(12));
        System.out.println();
        System.out.println(pf.toString());
        */
   }


    public void add(BigDecimal racetime, BigDecimal raceResultPrize) {

    }
}
