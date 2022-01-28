package hippos;

import hippos.exception.RegressionModelException;
import hippos.lang.stats.FullStatistics;
import hippos.lang.stats.SubForm;
import hippos.lang.stats.TimeForm;
import hippos.lang.stats.YearStatistics;
import hippos.math.*;
import hippos.math.regression.HipposRegressionResults;
import hippos.util.SubValueList;
import hippos.utils.AssendingValueComparator;
import hippos.utils.HorsesHelper;
import hippos.utils.ValueComparator;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.stat.regression.ModelSpecificationException;
import utils.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private Value value = new Value();

    private List <AlphaNumber> observaleList = new ArrayList();

    private Comparable valueComparator;
    private double[] splitTimeEffects;

    private BigDecimal startIntervalBefore = null;

    private Date exDate;
    private int index = -1;

    HipposRegressionResults hipposRegressionResults;

    public ValueHorse(RaceProgramHorse raceProgramHorse) {
        this.raceProgramHorse = raceProgramHorse;
        valueComparator = new ValueComparator(this);
        //valueComparator = new AssendingValueComparator(this);

    }

    public int getIndex() {
        if (index < 0)
            System.out.println("Warning! ValueHorse.getIndex(): index not set");
        return index;
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

    public Value getValue() {
        return value;
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

            this.value = raceProgramHorse.getObservation();

        } catch (ArithmeticException e) {
            e.printStackTrace();

        } catch (ModelSpecificationException e) {
            e.printStackTrace();

        } catch (Exception e) {
            Log.write(e);
        }
    }

    public List<AlphaNumber> getObservaleList() {
        return observaleList;
    }

    public void setObservaleList(List<AlphaNumber> observaleList) {
        this.observaleList = observaleList;
    }

    public String toString() {

        BigDecimal observerValue = null;
        AlphaNumber presentationValue = new AlphaNumber("N/A");
        try {
            observerValue = this.value.average(4, BigDecimal.ZERO);
            observerValue = BigDecimal.ONE.divide(observerValue, 4, RoundingMode.HALF_UP);
            presentationValue = HorsesHelper.toProcents(observerValue);
        } catch (Exception e) {
            //e.printStackTrace();
        }

        StringBuffer sb = new StringBuffer();
        sb.append("\n\n");
        sb.append(raceProgramHorse.toString());

        sb.append("\n\n\t    Value: " + presentationValue);
        sb.append(" " + raceProgramHorse.getObservableList());

        return sb.toString();
    }

}
