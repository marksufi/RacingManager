package hippos.util;

import hippos.math.AlphaNumber;
import hippos.math.MathHelper;
import hippos.math.Value;
import hippos.math.regression.HipposUpdatingRegression;
import hippos.utils.HorsesHelper;
import org.apache.commons.math3.stat.regression.ModelSpecificationException;
import utils.Log;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

public class ObservationFramework {
    Observation observerMap = new Observation(new ArrayList());
    private int level;

    public ObservationFramework(int level) {
        this.level = level;
    }

    public void addObservations(List <AlphaNumber> observerList, BigDecimal observerValue) {
        //System.out.println("\nObservationFramework.addObservations(" + observerList + ", " + observerValue + ")");
        List regX = new ArrayList();
        List keyList = new ArrayList();
        int startIndex = 0;

        observerMap = mapLevel(observerMap, observerList, observerValue, regX, keyList, startIndex, 1);

    }

    public Value getObservations(List <AlphaNumber> observerList) {
        List regX = new ArrayList();
        List keyList = new ArrayList();

        int startIndex = 0;
        Value observerValue = new Value();

        return getLevel(observerMap, observerList, observerValue, regX, keyList, startIndex, 1);

    }

    private Value
        getLevel(Observation map, List<AlphaNumber> observerList, Value observerValue, List baseRegX, List aKeyList, int aStartIndex, int iLevel) {
        //System.out.println("ObservationFramework.getLevel(" + map + ", " + observerList + ", " + observerValue + ", " + baseRegX + ", " + aKeyList + ", " + aStartIndex + ", " + iLevel + ")");

        for(int i = aStartIndex; i < observerList.size(); i++) {

            try {
                BigDecimal key = BigDecimal.valueOf(i);
                List keyList = new ArrayList(aKeyList);
                keyList.add(key);

                List regX = new ArrayList(baseRegX);
                regX.add(observerList.get(i));

                if(!map.containsKey(key))
                    continue;

                if(iLevel < level) {
                    Value yValue = getLevel(map.get(key), observerList, observerValue, regX, keyList, i, iLevel + 1);
                }

                double [] doubleValue = map.get(key).get(regX);
                //observerValue.add(doubleValue[0], doubleValue[0]);
                observerValue.add(doubleValue[0], doubleValue[1]);

            } catch (ModelSpecificationException e) {
            } catch (NullPointerException e) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return observerValue;
    }


    private Observation
        mapLevel(Observation map, List<AlphaNumber> observerList, BigDecimal observerValue, List baseRegX, List aKeyList, int aStartIndex, int iLevel) {
        //System.out.println("ObservationFramework.mapLevel(" + map + ", " + observerList + ", " + observerValue + ", " + baseRegX + ", " + aKeyList + ", " + aStartIndex + ", " + iLevel + ")");

        try {
            for(int i = aStartIndex; i < observerList.size(); i++) {

                BigDecimal key = BigDecimal.valueOf(i);
                List keyList = new ArrayList(aKeyList);
                keyList.add(key);

                List regX = new ArrayList(baseRegX);
                regX.add(observerList.get(i));

                if(!map.containsKey(key))
                    map.put(key, new Observation(keyList));

                if(iLevel < level) {
                    map.put(key, mapLevel(map.get(key), observerList, observerValue, regX, keyList, i, iLevel + 1));
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
        List keyList;

        public Observation(List keyList) {
            //System.out.println("Observation.Observation(" + keyList + ")");
            this.keyList = keyList;
        }

        public void add(List <AlphaNumber> regX, BigDecimal observerValue) throws ModelSpecificationException {
            //System.out.println("Observation.add(" + regX.toString()+ "," + observerValue + ") to " + keyList);

            try {
                List <String> alphas = new ArrayList();
                List <BigDecimal> numbers = new ArrayList();
                //double [] numbers = new double[regX.size()];

                int i = 0;
                for(AlphaNumber alphaNumber : regX) {
                    if(alphaNumber.getAlpha() != null) {
                        alphas.add(alphaNumber.getAlpha());
                    }

                    if(alphaNumber.getNumber() != null) {
                        numbers.add(alphaNumber.getNumber());
                    }
                    //numbers[i++] = alphaNumber.getNumber().doubleValue();
                }

                if(numbers.size() > 0) {
                    double[] dn = HorsesHelper.mapToDouble(numbers);

                    if (observationMap == null)
                        observationMap = new Mapper<>();


                    //observationMap.getOrCreate(alphas, new HipposUpdatingRegression(regX.size(), true)).add(numbers, observerValue.doubleValue());

                    //alphas.add(String.valueOf(dn.length));
                    //System.out.println("Observation.add: " + alphas + Arrays.toString(dn) + " to " + observationMap.get(alphas));
                    observationMap.getOrCreate(alphas, new HipposUpdatingRegression(dn.length, true)).add(dn, observerValue.doubleValue());

                }
            } catch (ModelSpecificationException e) {
                Log.write(e);

            } catch (NullPointerException e) {

            } catch (Exception e) {
                Log.write(e);
            }
        }

        public double [] get(List <AlphaNumber> regX) throws ModelSpecificationException {
            //System.out.println("Observation.get(" + regX.toString() + ") from " + keyList);
            try {
                List <String> alphas = new ArrayList();
                List <BigDecimal> numbers = new ArrayList();
                //double [] numbers = new double[regX.size()];

                int i = 0;
                for(AlphaNumber alphaNumber : regX) {
                    if(alphaNumber.getAlpha() != null) {
                        alphas.add(alphaNumber.getAlpha());
                    }
                    if(alphaNumber.getNumber() != null) {
                        numbers.add(alphaNumber.getNumber());
                    }
                    //numbers[i++] = alphaNumber.getNumber().doubleValue();
                }

                double [] dn = HorsesHelper.mapToDouble(numbers);

                //double [] regress = observationMap.get(alphas).getWithR(numbers);

                /**
                 * lisää avainlistaan lukujen määrän
                 */
                //alphas.add(String.valueOf(dn.length));
                double [] regress = observationMap.get(alphas).getWithR(dn);
                //System.out.println(Arrays.toString(numbers) + "=>"+ Arrays.toString(regress));

                return regress;
                //return observationMap.get(alphas).get(numbers);

            } catch (ModelSpecificationException e) {
                throw e;

            } catch (NullPointerException e) {
                throw e;

            } catch (Exception e) {
                Log.write(e);

                throw e;
            }
        }

        public String toString() {
            try {
                return keyList.toString() + observationMap.toString();
            } catch (Exception e) {
                return keyList + new String("[null]");
            }
        }
    }


}
