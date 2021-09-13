package hippos;

import hippos.exception.RegressionModelException;
import hippos.lang.stats.FullStatistics;
import hippos.lang.stats.SubForm;
import hippos.lang.stats.TimeForm;
import hippos.math.*;
import hippos.math.regression.HipposRegressionResults;
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

    public void setRegValues() {
        try {
            FullStatistics fullStatistics = raceProgramHorse.getFullStatistics();

            for(SubForm subForm : fullStatistics.getSubForms()) {
                try {
                    double regY = subForm.getRegY(fullStatistics);

                    maxValue.add(new Value(regY));
                } catch (RegressionModelException e) {
                    // Ei onnistunut
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            minValue = maxValue;
            value = maxValue;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String toString() {

        StringBuffer sb = new StringBuffer();
        sb.append("\n\n");
        sb.append(raceProgramHorse.toString());

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
