package hippos;

import hippos.lang.stats.Form;
import hippos.lang.stats.SubRaceTime;
import hippos.math.*;
import hippos.math.regression.HipposRegressionResults;
import hippos.math.regression.RegressionModelException;
import hippos.util.SubValueList;
import hippos.utils.ValueComparator;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import utils.Log;

import java.math.BigDecimal;
import java.util.*;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Mar 12, 2006
 * Time: 2:02:25 AM
 * To change this template use Options | File Templates.
 */
//public class ValueHorse extends Horse implements Comparable {

public class ValueHorse implements Comparable {
    private RaceProgramHorse raceProgramHorse;
    private RaceResultHorse raceResultHorse;

    private SubValueList value = new SubValueList();
    private SubValueList maxValue = new SubValueList();
    private SubValueList minValue = new SubValueList();
    //private Value timeValue = new Value();
    //private Value rankValue = new Value();

    private List valueListA = new Vector();

    private Comparable valueComparator;
    private double[] splitTimeEffects;

    private BigDecimal startIntervalBefore = null;

    private Date exDate;
    private int index = -1;

    HipposRegressionResults hipposRegressionResults;

    // setStatValues tallettaa tähän
    double [] regX;

    public ValueHorse(RaceProgramHorse raceProgramHorse) {
        this.raceProgramHorse = raceProgramHorse;
        valueComparator = new ValueComparator(this);

    }

    /*
    public ValueHorse(ResultSet raceSet, Connection conn, RaceProgramStart raceProgramStart) throws SQLException {
        this.raceProgramHorse = new RaceProgramHorse(raceSet, conn, raceProgramStart);
        //valueComparator = new MaxValueAverageValueHorseComparator(this);
        valueComparator = new ValueComparator(this);
    }*/

    public List getValueListA() {
        return valueListA;
    }

    public int getIndex() {
        if (index < 0)
            System.out.println("Warning! ValueHorse.getIndex(): index not set");
        return index;
    }

    public Value getMaxValue() {
        return maxValue.getValue();
    }

    public Value getMinValue() {
        return minValue.getValue();
    }

    public int compareTo(Object o) {
        return valueComparator.compareTo(o);
    }

    public void setValueComparator(Comparable valueComparator) {
        this.valueComparator = valueComparator;
    }

    public void setSplitTimeEffects(double[] splitTimeEffects) {
        this.splitTimeEffects = splitTimeEffects;
    }

    public double[] getSplitTimeEffects() {
        return splitTimeEffects;
    }

    public BigDecimal getStartIntervalBefore() {
        return startIntervalBefore;
    }

    public void setStartIntervalBefore(BigDecimal startIntervalBefore) {
        this.startIntervalBefore = startIntervalBefore;
    }

    public HipposRegressionResults getHipposRegressionResults() {
        return hipposRegressionResults;
    }

    public void setHipposRegressionResults(HipposRegressionResults hipposRegressionResults) {
        this.hipposRegressionResults = hipposRegressionResults;
    }

    public Date getDate() {
        if (getRaceProgramHorse().getRaceDate() != null)
            return getRaceProgramHorse().getRaceDate();

        return getRaceProgramHorse().raceProgramStart.getDate();
    }

    public void setExDate(Date exDate) {
        this.exDate = exDate;
    }

    public Date getExDate() {
        return exDate;
    }

    public RaceProgramHorse getRaceProgramHorse() {
        return raceProgramHorse;
    }

    public BigDecimal getValue() {
        return value.getValue().average(2, BigDecimal.ZERO);
    }

    public RaceResultHorse getRaceResultHorse() {
        return raceResultHorse;
    }

    public void setRaceResultHorse(RaceResultHorse raceResultHorse) {
        this.raceResultHorse = raceResultHorse;
    }

    public String generateTrackId() {

        StringBuffer sb = new StringBuffer();
        sb.append(getRaceProgramHorse().getTasoitus());

        if(HarnessApp.useTrackKey) {

            sb.append(":");
            sb.append(getRaceProgramHorse().getRaceTrack());

            String raceStartMode = getRaceProgramHorse().getRaceProgramStart().getRaceStartMode();
            if (raceStartMode != null) {
                if (raceStartMode.indexOf("ryhmä") >= 0) {
                    sb.append("a");
                }
                //System.out.println("ValueHorse.generateTrackId: " + raceStartMode);
            } else {
                Log.write("Failed to generate trackId because raceStartMode is missing from " + getRaceProgramHorse().getId());
            }
        }
        return sb.toString();
    }

    public void setStatValues() {
        try {
            regX = raceProgramHorse.getRegX();

            double y = RaceProgramStart.featuredReg.get(regX);

            maxValue.add(y);
            minValue.add(y * 0.90);
            value.add(y);
        } catch ( NullPointerException e) {
            // "hippos.RaceProgramStart.featuredReg" is null
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Hits:   26.90%
     * Profit: -4924.6/46087 = -0.11€/startti
     */
    public void setValues() {

        // 1372 seconds
        // 1396 seconds

        List <SubStart>  subStartList = this.raceProgramHorse.getSubStartList();
        //Set <SubStart>  subStartSet = this.raceProgramHorse.fullStatistics.getTimeStatistics().getSubStartSet();

        if(subStartList.isEmpty()) {
            Log.write("ValueHorse.setValues: subStartList is empty for " + this.raceProgramHorse.getId());
            raceProgramHorse.setStatistics();
        }

        TreeSet <SubRaceTime> subRaceTimeSet = new TreeSet<>();
        //Mapper <TreeSet> subRaceTimeRecordMap = new  Mapper();

        //Collections.reverse(subStartList);
        int exDateDiff = 0;
        for(SubStart subStart : subStartList) {
            try {

                BigDecimal progressMultiplier = RaceProgramHorse.progressMap.getOrCreate(subStart.getWeeksKeyList(), new Progress()).getMultiplier();
                SubRaceTime subRaceTime = new SubRaceTime(subStart, raceProgramHorse);
                SubRaceTime subRaceRankTime = subRaceTime;

                // nollaa ennätyksen, jos palaa tauolta
                if(exDateDiff - subStart.getDateDiff().intValue() > 40)
                    subRaceTimeSet = new TreeSet<>();
                exDateDiff = subStart.getDateDiff().intValue();

                subRaceTimeSet.add(subRaceTime);

                try {
                    // jos uudella ajalla on parempi tai yhtä suuri sijoitus, niin käytetään parasta edellistä aikaa
                    while(!subRaceTimeSet.isEmpty()) {
                        int subRank = subRaceTime.getSubRank().getNumber().intValue();
                        int subSetRank = subRaceTimeSet.first().getSubRank().getNumber().intValue();

                        if ((subRank > 0 && subRank <= subSetRank) || subSetRank == 0) {
                            subRaceRankTime = subRaceTimeSet.first();
                            subRaceTimeSet.first().setSubRank(subRaceTime.getSubRank());
                            break;
                        } else {
                            subRaceTimeSet.pollFirst();
                        }
                    }
                } catch (NullPointerException e) {
                    // Sijoitus puuttuu
                    subRaceTimeSet = new TreeSet<>();
                }


                //

                if(progressMultiplier.intValue() > 2 || progressMultiplier.intValue() < 0) {
                    System.out.println("ValueHorse.setValues - progressMultiplier overbounds " + progressMultiplier);

                    // resetoidaan
                    progressMultiplier = BigDecimal.ONE;
                }

                // Lisää sijoitukselle parhaan tämänhetkisen ajan
                subStart.getSubTime().setSubRaceTime(subRaceTime);
                subStart.getSubRank().setSubRaceTime(subRaceRankTime);
                //subStart.getSubRank().setSubRaceTime(subRaceRecordTime);

                subStart.getSubTime().setProgressMultiplier(progressMultiplier);
                subStart.getSubRank().setProgressMultiplier(progressMultiplier);

                // Hakee arviot
                subStart.getSubTime().getObservation(raceProgramHorse);
                subStart.getSubRank().getObservation(raceProgramHorse);

                SubValue subTimeValue = subStart.getSubTime().getSubValue();
                SubValue subRankValue = subStart.getSubRank().getSubValue();

                BigDecimal timeValue = subTimeValue.getValue().average(2, null);
                BigDecimal rankValue = subRankValue.getValue().average(2, null);

                value.add(subTimeValue);
                value.add(subRankValue);

                try { // uusi ennätys nollaa vanhat, vanhoja ei muistella
                    //if(subRaceTime.equals(subRaceTimeSet.first())) {
                    //BigDecimal max = maxValue.average(2);
                    //if (max.intValue() < timeValue.intValue() || max.intValue() < rankValue.intValue()) {
                        if(timeValue.compareTo(rankValue) >= 0) {
                            maxValue = poistaHuonommat(maxValue, timeValue);
                            minValue = poistaHuonommat(minValue, rankValue);
                        } else {
                            maxValue = poistaHuonommat(maxValue, rankValue);
                            minValue = poistaHuonommat(minValue, timeValue);
                        }
                        //value = maxValue;
                    //}
                } catch (Exception e) {
                }

                if (timeValue != null) {
                    if (rankValue != null) {
                        if (timeValue.compareTo(rankValue) >= 0) {
                            maxValue.add(subTimeValue);
                            minValue.add(subRankValue);
                        } else {
                            maxValue.add(subRankValue);
                            minValue.add(subTimeValue);
                        }
                    } else {
                        maxValue.add(subTimeValue);
                        minValue.add(subTimeValue);
                    }
                } else if (rankValue != null) {
                    maxValue.add(subRankValue);
                    minValue.add(subRankValue);
                }
            } catch (RegressionModelException e) {
                // subRaceTime fails
            } catch (Exception e) {
                Log.write(e);
            }
        }

        try {
            maxValue = poistaAlisuoritetut(maxValue, value);
            //maxValue = parhaanJälkeiset(maxValue);
            maxValue = laukatonArvo(maxValue);

            //SubValue minFirst = minValue.getSubValueList().get(0);

            //BigDecimal minFirstValue = minFirst.getValue().average(2, null);

            //maxValue = poistaHuonommat(maxValue, minFirstValue);

            //maxValue.add(minFirst);

            //minValue = poistaAlisuoritetut(minValue);
            //maxValue = siirräAlisuoritetut(maxValue, minValue);

            value = maxValue;
            /*
            value = new SubValueList();
            if(!maxValue.isEmpty())
                value.add(maxValue.getSubValueSet().first());
            if(!minValue.isEmpty())
                value.add(minValue.getSubValueSet().first());
            */
            /*
            BigDecimal coachAwardRate = raceProgramHorse.getCoach().getForm().awardRate(null);
            if(coachAwardRate != null) {
                // Valmentajan luokka
                value.add(coachAwardRate);
            }
            BigDecimal jockeyAwardRate = raceProgramHorse.getRaceProgramDriver().getForm().awardRate(null);
            if(jockeyAwardRate != null) {
                value.add(jockeyAwardRate);
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SubValueList siirräAlisuoritetut(SubValueList maxValue, SubValueList minValue) {
        BigDecimal minAverage = minValue.average(2);
        SubValueList newMaxValue = new SubValueList();

        for(SubValue subValue : maxValue.getSubValueList()) {
            if(subValue.getValue().average(2).compareTo(minAverage) >= 0) {
                newMaxValue.add(subValue);
            } else {
                minValue.add(subValue);
            }
        }
        if(newMaxValue.isEmpty())
            System.out.print("");

        return newMaxValue;
    }

    private SubValueList parhaanJälkeiset(SubValueList maxValue) {
        try {
            SubValue parasArvo = maxValue.getSubValueSet().first();
            int parasInd = maxValue.getSubValueList().indexOf(parasArvo);
            int loppuInd = maxValue.getSubValueList().size();

            List<SubValue> subList = maxValue.getSubValueList().subList(parasInd, loppuInd);

            maxValue = new SubValueList();
            for (SubValue subValue : subList) {
                maxValue.add(subValue);
            }
        } catch (NoSuchElementException e) {
            // tyhjä joukko
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maxValue;
    }

    private SubValueList poistaAlisuoritetut(SubValueList maxValueList, Value minValue) {
        SubValueList newMaxValueList = new SubValueList();

        for(SubValue subValue : maxValueList.getSubValueList()) {
            if (subValue.getValue().compareTo(minValue) >= 0) {
                newMaxValueList.add(subValue);
            }
        }

        return newMaxValueList;
    }

    private SubValueList poistaHuonommat(SubValueList valueList, BigDecimal rajaArvo) {
        SubValueList newMaxValueList = new SubValueList();

        for(SubValue subValue : valueList.getSubValueList()) {
            if (subValue.getValue().average(0, BigDecimal.ZERO).compareTo(rajaArvo) >= 0) {
                newMaxValueList.add(subValue);
            }
        }

        return newMaxValueList;
    }

    private SubValueList laukatonArvo(SubValueList maxValueList) {
        SubValueList newMaxValueList = new SubValueList();

        int laukattomat = maxValueList.getLaukattomat();

        for(SubValue subValue : maxValueList.getSubValueSet()) {
            newMaxValueList.add(subValue);

            if(--laukattomat <= 0)
                break;
        }

        return newMaxValueList;
    }

    private SubValueList poistaAlisuoritetut(SubValueList maxValueList, SubValueList valueList) {

        try {
            NormalDistribution normalDistribution2 = valueList.getNormalDistribution();
            double min = normalDistribution2.inverseCumulativeProbability(0.10);

            return poistaAlisuoritetut(maxValueList, new Value(min));
        } catch (ArithmeticException e) {
            // divide by zero
        } catch (NotStrictlyPositiveException e) {
            // Liian vähän arvoja normaalijakaumaaan§
        } catch (Exception e) {
            e.printStackTrace();
        }

        return maxValueList;
    }

    /**
     * Hits:   21.46%
     * Profit: -13977.8�/56323 = -0.25�/startti
     *
    public void setValues() {

        try {
            Set <SubValue> subValueSet = new TreeSet();
            subValueSet.addAll(this.raceProgramHorse.fullStatistics.getTimeStatistics().getRaceTimeValueSet());
            subValueSet.addAll(this.raceProgramHorse.fullStatistics.getTimeStatistics().getRaceRankValueSet());

            int m = (int)Math.ceil(subValueSet.size() / 2.0);
            int i = 0;
            for(SubValue subValue :  subValueSet) {
                if(i++ < m) {
                    maxValue.add(subValue);
                    value.add(subValue);
                } else {
                    minValue.add(subValue);
                }
            }

        } catch (NullPointerException e) {
            System.out.println(e);
        } catch (Exception e) {
            Log.write(e);
        }
    }*/

    public String toString() {

        StringBuffer sb = new StringBuffer();
        sb.append("\n\n");
        sb.append(raceProgramHorse.toString());

        sb.append("\n\t    Reg: " + Arrays.toString(regX));
        sb.append("\n\t    Max: " + maxValue);
        sb.append("\n\t    Min: " + minValue);
        //sb.append("\n\t    Val: " + value);

        //for(int i = raceProgramHorse.getSubStartList().size(); i > 0; i--) {
        for(SubStart subStart : raceProgramHorse.getSubStartList()) {
            sb.append("\n\t\t" + subStart.toValueString());
        }

        /*
        sb.append("\n" + raceProgramHorse.getRaceProgramDriver().toString());
        sb.append("\n" + raceProgramHorse.getRaceProgramDriver().getForm().toString());
        sb.append(" (" + raceProgramHorse.getRaceProgramDriver().getForm().getAwardRate() + "€/s)");
        */
        return sb.toString();
    }

}
