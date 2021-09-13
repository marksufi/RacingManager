package hippos.math.racing;

import hippos.RaceProgramHorse;
import hippos.math.AlphaNumber;
import hippos.math.StatisticalValue;
import hippos.math.Value;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class QuarterTime extends StatisticalValue implements Comparable {

    private RaceProgramHorse raceProgramHorse;

    private BigDecimal propbabilty;
    private String raceMode;
    private BigDecimal tasoitus;
    private AlphaNumber aika;
    private Value palkinto = new Value();

    private TreeSet<AlphaNumber> aRecordSet = new TreeSet<>();
    private TreeSet<AlphaNumber> tRecordSet = new TreeSet<>();


    public QuarterTime(RaceProgramHorse raceProgramHorse) {
        this.raceProgramHorse = raceProgramHorse;
    }

    public QuarterTime(RaceProgramHorse raceProgramHorse, BigDecimal aika, String raceMode, BigDecimal tasoitus) {
        try {
            this.raceProgramHorse = raceProgramHorse;
            this.raceMode = raceMode;
            this.tasoitus = tasoitus;
            this.aika = new AlphaNumber(aika, raceMode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public QuarterTime(RaceProgramHorse raceProgramHorse, BigDecimal aika, String raceMode, BigDecimal tasoitus, BigDecimal qtCount, BigDecimal startCount, BigDecimal palkinto) {
        try {
            this.raceProgramHorse = raceProgramHorse;
            this.raceMode = raceMode;
            this.tasoitus = tasoitus;
            this.aika = new AlphaNumber(aika, raceMode);
            this.propbabilty = BigDecimal.ZERO;
            this.palkinto.add(palkinto);

            super.add(qtCount, startCount.intValue());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BigDecimal getPropbabilty() {
        return propbabilty;
    }

    public BigDecimal getPropabiltyProcents() {
        return propbabilty != null ? propbabilty.divide(BigDecimal.valueOf(100.00), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public int compareTo(Object o) {

        if (o.hashCode() == this.hashCode())
            return 0;

        try {
            int cmp = propbabilty.compareTo(((QuarterTime) o).getPropbabilty());

            if (cmp != 0) {
                return -cmp;
            }
        } catch (NullPointerException e) {

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            return getValueSet().first().compareTo(((QuarterTime) o).getValueSet().first());
        } catch (NullPointerException e) {
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void setProbability(BigDecimal trackProb, BigDecimal horseprob) {
        try {
            //BigDecimal prop = trackProb.multiply(horseprob).multiply(BigDecimal.valueOf(100.00));
            BigDecimal prop = trackProb.multiply(horseprob);
            this.propbabilty = BigDecimal.valueOf(Math.sqrt(prop.doubleValue())).setScale(1, RoundingMode.HALF_UP);
        } catch (Exception e) {
        }
    }

    public String getRaceMode() {
        return raceMode;
    }

    public BigDecimal getTasoitus() {
        return tasoitus;
    }

    public void setTasoitus(BigDecimal tasoitus) {
        this.tasoitus = tasoitus;
    }

    public void setRaceMode(String raceMode) {
        this.raceMode = raceMode;
    }

    public Value getPalkinto() {
        return palkinto;
    }

    public void add(QuarterTime quarterTime) {
        super.add(quarterTime.getQuarterTimeStatistics(), quarterTime.getAika());
        addRaceTypeRecord(quarterTime);
        palkinto.add(quarterTime.getPalkinto());
    }

    public AlphaNumber getAika() {
        return aika;
    }

    public void setAika(AlphaNumber aika) {
        this.aika = aika;
    }

    public StatisticalValue getQuarterTimeStatistics() {
        return this;
    }

    public RaceProgramHorse getRaceProgramHorse() {
        return raceProgramHorse;
    }



    public String toString() {
        StringBuffer sb = new StringBuffer();

        try {
            if (propbabilty != null) {
                sb.append(propbabilty + "%");
            }
            sb.append(getRecords());

            /*
            if(getPalkinto().getCount() > 0) {
                sb.append(" " + getPalkinto().average(0, BigDecimal.ZERO) + "€");
            }*/

            sb.append("/" + raceProgramHorse.getRaceHorseName());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private void addRaceTypeRecord(QuarterTime quarterTime) {
        try {
            if(quarterTime.getAika().getNumber() != null) {
                String raceType = quarterTime.getRaceMode().contains("a") ? "a" : "t";

                switch (raceType) {
                    case "a":
                        aRecordSet.add(quarterTime.getAika());
                        break;

                    default:
                        tRecordSet.add(quarterTime.getAika());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addTrackPropability(BigDecimal trackProb) {
        try {
            BigDecimal horseProp = procents(2);

            // Jockey ei toimi ääkkösille
            //BigDecimal jockeyProp = raceProgramHorse.getRaceProgramDriver().getForm().getKcodeProcents(BigDecimal.ZERO);

            //BigDecimal prop = trackProb.multiply(horseProp).multiply(jockeyProp);
            BigDecimal prop = trackProb.multiply(horseProp);

            double d = 1.00/2.00;
            this.propbabilty = BigDecimal.valueOf(Math.pow(prop.doubleValue(), d)).setScale(2, RoundingMode.HALF_DOWN);

            //this.propbabilty = BigDecimal.valueOf(Math.sqrt(prop.doubleValue())).setScale(1, RoundingMode.HALF_UP);
        } catch (Exception e) {
        }
    }

    public QuarterTime getQuarterTime() {
        return this;
    }

    public boolean isEmpty() {
        return getValueList().isEmpty();
    }

    protected List <AlphaNumber> getRecords() {
        List recordList = new ArrayList();

        try {
            recordList.add(aRecordSet.first());
        } catch (Exception e) {}

        try {
            recordList.add(tRecordSet.first());
        } catch (Exception e) {}

        return recordList;
    }
}


