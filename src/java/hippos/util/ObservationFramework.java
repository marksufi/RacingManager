package hippos.util;

import hippos.math.AlphaNumber;
import hippos.math.Value;
import hippos.math.regression.HipposUpdatingRegression;
import org.apache.commons.math3.stat.regression.ModelSpecificationException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class ObservationFramework {
    Observation observerMap = new Observation();
    private int level;

    public ObservationFramework(int level) {
        this.level = level;
    }

    public void addObservations(List <AlphaNumber> observerList, BigDecimal observerValue) {
        List regX = new ArrayList();
        int startIndex = 0;

        observerMap = mapLevel(observerMap, observerList, observerValue, regX, startIndex, 1);

    }

    public Value getObservations(List <AlphaNumber> observerList) {
        List regX = new ArrayList();
        int startIndex = 0;
        Value observerValue = new Value();

        return getLevel(observerMap, observerList, observerValue, regX, startIndex, 1);

    }

    private Value
        getLevel(Observation map, List<AlphaNumber> observerList, Value observerValue, List baseRegX, int aStartIndex, int iLevel) {

        for(int i = aStartIndex; i < observerList.size(); i++) {

            try {
                BigDecimal key = BigDecimal.valueOf(i);

                List regX = new ArrayList(baseRegX);
                regX.add(observerList.get(i));

                if(!map.containsKey(key))
                    continue;

                if(iLevel < level) {
                    Value yValue = getLevel(map.get(key), observerList, observerValue, regX, i, iLevel + 1);
                }

                double doubleValue = map.get(key).get(regX);
                observerValue.add(doubleValue);
            } catch (ModelSpecificationException e) {
            } catch (NullPointerException e) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return observerValue;
    }


    private Observation
        mapLevel(Observation map, List<AlphaNumber> observerList, BigDecimal observerValue, List baseRegX, int aStartIndex, int iLevel) {

        try {
            for(int i = aStartIndex; i < observerList.size(); i++) {

                BigDecimal key = BigDecimal.valueOf(i);

                List regX = new ArrayList(baseRegX);
                regX.add(observerList.get(i));

                if(!map.containsKey(key))
                    map.put(key, new Observation());

                if(iLevel < level) {
                    map.put(key, mapLevel(map.get(key), observerList, observerValue, regX, i, iLevel + 1));
                }

                map.get(key).add(regX, observerValue);
            }
        } catch (ModelSpecificationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public static void main(String args[]) {
        ObservationFramework observationFramework = new ObservationFramework(3);

        List observerList1 = new ArrayList();
        observerList1.add(new AlphaNumber(22));
        observerList1.add(new AlphaNumber(7));
        observerList1.add(new AlphaNumber(3));
        observerList1.add(new AlphaNumber(2));
        observerList1.add(new AlphaNumber("27.9Kke"));
        observerList1.add(new AlphaNumber("28.0Kake"));
        observationFramework.addObservations(observerList1, BigDecimal.valueOf(1));

        List observerList2 = new ArrayList();
        observerList2.add(new AlphaNumber(37));
        observerList2.add(new AlphaNumber(6));
        observerList2.add(new AlphaNumber(2));
        observerList2.add(new AlphaNumber(6));
        observerList2.add(new AlphaNumber("28.9Kke"));
        observerList2.add(new AlphaNumber("29.6Kke"));
        observationFramework.addObservations(observerList2, BigDecimal.valueOf(5));

        List observerList3 = new ArrayList();
        observerList3.add(new AlphaNumber(24));
        observerList3.add(new AlphaNumber(2));
        observerList3.add(new AlphaNumber(2));
        observerList3.add(new AlphaNumber(4));
        observerList3.add(new AlphaNumber("27.4Kake"));
        observerList3.add(new AlphaNumber("29.1Kke"));
        observationFramework.addObservations(observerList3, BigDecimal.valueOf(4));

        List observerList4 = new ArrayList();
        observerList4.add(new AlphaNumber(24));
        observerList4.add(new AlphaNumber(6));
        observerList4.add(new AlphaNumber(2));
        observerList4.add(new AlphaNumber(4));
        observerList4.add(new AlphaNumber("29.6Kke"));
        observerList4.add(new AlphaNumber("30.2Kake"));
        observationFramework.addObservations(observerList4, BigDecimal.valueOf(3));

        List observerList5 = new ArrayList();
        observerList5.add(new AlphaNumber(25));
        observerList5.add(new AlphaNumber(2));
        observerList5.add(new AlphaNumber(3));
        observerList5.add(new AlphaNumber(0));
        observerList5.add(new AlphaNumber("28.5Kake"));
        observerList5.add(new AlphaNumber("29.1Kke"));
        observationFramework.addObservations(observerList5, BigDecimal.valueOf(7));

        List observerList6 = new ArrayList();
        observerList6.add(new AlphaNumber(52));
        observerList6.add(new AlphaNumber(5));
        observerList6.add(new AlphaNumber(3));
        observerList6.add(new AlphaNumber(0));
        observerList6.add(new AlphaNumber("29.7Kake"));
        observerList6.add(new AlphaNumber("29.7Kake"));
        observationFramework.addObservations(observerList6, BigDecimal.valueOf(2));

        List observerList7 = new ArrayList();
        observerList7.add(new AlphaNumber(85));
        observerList7.add(new AlphaNumber(3));
        observerList7.add(new AlphaNumber(2));
        observerList7.add(new AlphaNumber(8));
        observerList7.add(new AlphaNumber("27.4Kke"));
        observerList7.add(new AlphaNumber("28.2Kaly"));
        observationFramework.addObservations(observerList7, BigDecimal.valueOf(6));

        System.out.println(observationFramework.getObservations(observerList1));
        System.out.println(observationFramework.getObservations(observerList6));
        System.out.println(observationFramework.getObservations(observerList4));
        System.out.println(observationFramework.getObservations(observerList3));
        System.out.println(observationFramework.getObservations(observerList2));
        System.out.println(observationFramework.getObservations(observerList7));
        System.out.println(observationFramework.getObservations(observerList5));



    }

    public class Observation extends TreeMap <BigDecimal, Observation> {
        Mapper <HipposUpdatingRegression> observationMap;

        public void add(List <AlphaNumber> regX, BigDecimal observerValue) {

            try {
                List alphas = new ArrayList();
                double [] numbers = new double[regX.size()];

                int i = 0;
                for(AlphaNumber alphaNumber : regX) {
                    if(alphaNumber.getAlpha() != null) {
                        alphas.add(alphaNumber.getAlpha());
                    }
                    numbers[i++] = alphaNumber.getNumber().doubleValue();
                }


                if(observationMap == null)
                    observationMap = new Mapper<>();

                observationMap.getOrCreate(alphas, new HipposUpdatingRegression(regX.size(), true)).add(numbers, observerValue.doubleValue());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public double get(List <AlphaNumber> regX) throws ModelSpecificationException {
            try {
                List alphas = new ArrayList();
                double [] numbers = new double[regX.size()];

                int i = 0;
                for(AlphaNumber alphaNumber : regX) {
                    if(alphaNumber.getAlpha() != null) {
                        alphas.add(alphaNumber.getAlpha());
                    }
                    numbers[i++] = alphaNumber.getNumber().doubleValue();
                }

                return observationMap.get(alphas).get(numbers);

            } catch (ModelSpecificationException e) {
                throw e;

            } catch (NullPointerException e) {
                throw e;

            } catch (Exception e) {
                //Log.write(e);
                e.printStackTrace();

                throw e;
            }
        }

        public String toString() {
            return observationMap.toString();
        }
    }


}
