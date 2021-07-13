package hippos.util;

import hippos.database.Database;
import hippos.math.Value;
import hippos.math.regression.HipposUpdatingRegression;
import hippos.math.regression.RegressionModelException;
import org.apache.commons.math3.stat.regression.ModelSpecificationException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class RegressionMapper {

    private TreeMap keyMap = new TreeMap();

    public void add(RegressionMapObservation regressionMapObservation) throws RegressionModelException {
        List key = regressionMapObservation.getRegKey();
        double [] x = regressionMapObservation.getRegAddX();
        double y = regressionMapObservation.getRegY();

        add(key, x, y);
    }

    public double get(RegressionMapObservation regressionMapObservation) throws RegressionModelException {
        List key = regressionMapObservation.getRegKey();
        double [] x = regressionMapObservation.getRegGetX();

        double y = get(key, x);

        return y;
    }

    public void add(String [] keys, double[] xs, double y) {
        add(Arrays.asList(keys), xs, y);
    }

    public void add(List keys, double[] xs, double y) {
        //System.out.println("RegressionMapper.add(" + keys + ", " + Arrays.toString(xs) + ", " + y + ")");

        Iterator iKeys = keys.iterator();

        ((HipposUpdatingRegression)get(keyMap, iKeys, new String(), new HipposUpdatingRegression(xs.length, true))).add(xs, y);
    }

    public double get(String [] keys, double[] xs) throws RegressionModelException {

        return get(Arrays.asList(keys), xs);
    }

    public double get(List keyList,  double[] x) throws RegressionModelException {
        //System.out.print("RegressionMapper.get(" + keyList + ", " + Arrays.toString(xs) + ")");
        try {
            Iterator iKeys = keyList.iterator();

            HipposUpdatingRegression hur = (HipposUpdatingRegression)get(keyMap, iKeys, new String());

            if(hur != null) {
                double y = hur.get(x);

                return y;
            }

        } catch (ModelSpecificationException e) {
            throw new RegressionModelException();
        }

        throw new RegressionModelException();
    }

    private Object get(TreeMap treeMap, Iterator iKeys, Object lastKey, Object newValue) {
        if (iKeys.hasNext()) {
            Object iKey = iKeys.next();

            if (!treeMap.containsKey(iKey)) {
                treeMap.put(iKey, new TreeMap<>());
            }
            return get((TreeMap) treeMap.get(iKey), iKeys, iKey, newValue);
        }
        if(treeMap.values().isEmpty()) {
            treeMap.put(lastKey, newValue);
        }
        return treeMap.get(lastKey);
    }

    private Object get(TreeMap treeMap, Iterator iKeys, Object lastKey) {
        if (iKeys.hasNext()) {
            Object iKey = iKeys.next();

            if (!treeMap.containsKey(iKey)) {

                return null;
            }

            return get((TreeMap) treeMap.get(iKey), iKeys, iKey);
        }
        return treeMap.get(lastKey);
    }

    public static void main(String args []) {

        PreparedStatement statement = null;
        ResultSet set = null;
        Connection conn = null;
        RegressionMapper cf = new RegressionMapper();

        Value values [] = new Value []{new Value(), new Value(), new Value()};

        try {
            conn = Database.getConnection();
            statement = conn.prepareStatement("select lahtotyyppi,aika, palkinto from subresult");
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
                        //if(keyList.contains("Lake"))
                        //    System.out.println("RegressionMapper.main: " + keyList + Arrays.toString(x) + " <- " + prize);
                        prize = prize.divide(BigDecimal.valueOf(1000), RoundingMode.HALF_DOWN);
                        cf.add(keyList, x, prize.doubleValue());
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

        List keyList = new ArrayList();
        keyList.add("Lke");

        try {
            System.out.println(keyList + " -> " + cf.get(keyList, new double[]{25}));
            System.out.println(keyList + " -> " + cf.get(keyList, new double[]{20}));
            System.out.println(keyList + " -> " + cf.get(keyList, new double[]{19}));
            System.out.println(keyList + " -> " + cf.get(keyList, new double[]{18}));
            System.out.println(keyList + " -> " + cf.get(keyList, new double[]{17}));
            System.out.println(keyList + " -> " + cf.get(keyList, new double[]{16}));
            System.out.println(keyList + " -> " + cf.get(keyList, new double[]{15}));
            System.out.println(keyList + " -> " + cf.get(keyList, new double[]{14}));
            System.out.println(keyList + " -> " + cf.get(keyList, new double[]{13}));
            System.out.println(keyList + " -> " + cf.get(keyList, new double[]{12}));
            System.out.println(keyList + " -> " + cf.get(keyList, new double[]{11}));
            System.out.println(keyList + " -> " + cf.get(keyList, new double[]{10}));
        } catch (RegressionModelException e) {
            e.printStackTrace();
        }


        //System.out.println("RegressionMapper.main: " + Arrays.toString(values));
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

    /*
    public static void main(String args []) {
        RegressionMapper regressionMapper = new RegressionMapper();

        ArrayList keyList = new ArrayList();
        keyList.add("00");
        keyList.add("Lake");
        keyList.add("Laly");

        ArrayList keyListNotFound = new ArrayList();
        keyListNotFound.add("Lke");
        keyListNotFound.add("Laly");

        ArrayList keyListTooFew = new ArrayList();
        keyListTooFew.add("Lake");

        ArrayList keyListTooMany = new ArrayList();
        keyListTooMany.add("00");
        keyListTooMany.add("Lake");
        keyListTooMany.add("Laly");
        keyListTooMany.add("lkp");

        double [] xs1 = {19.0};
        double [] xs2 = {20};
        double [] xs3 = {21};
        double [] xs4 = {22};
        double [] xs5 = {23};

        regressionMapper.add(keyList, xs1, 17);
        regressionMapper.add(keyList, xs2, 18);
        regressionMapper.add(keyList, xs3, 19);
        regressionMapper.add(keyList, xs4, 20);
        regressionMapper.add(keyList, xs5, 21);

        System.out.println(keyList + " -> " + regressionMapper.get(keyList, xs1));
        System.out.println(keyListNotFound + " -> " + regressionMapper.get(keyListNotFound, xs1));
        System.out.println(keyListTooFew + " -> " + regressionMapper.get(keyListTooFew, xs1));
        System.out.println(keyListTooMany + " -> " + regressionMapper.get(keyListTooMany, xs1));
    }

     */

}
