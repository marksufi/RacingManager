package hippos.lang.stats;

import hippos.HarnessApp;
import hippos.RaceProgramHorse;
import hippos.exception.RegressionModelException;
import hippos.math.AlphaNumber;
import hippos.math.regression.HipposUpdatingRegression;
import hippos.util.Mapper;
import org.apache.commons.math3.stat.regression.ModelSpecificationException;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SubForm extends Form {

    private final String raceMode;
    private final BigDecimal lastAward;
    private BigDecimal recordTime;

    private double y = Double.NaN;

    public SubForm(ResultSet set) throws SQLException {
        super(set);
        this.raceMode = set.getString("LAHTOTYYPPI");

        StringBuilder label = new StringBuilder(this.raceMode);
        if(getXcode().intValue() > 0) {
            label.append("x");
        }
        if(getKcode().intValue() > 0) {
            label.append("->");
        }

        setLabel(label.toString());

        this.lastAward = set.getBigDecimal("VPALKINTO");
        this.recordTime = set.getBigDecimal("AIKA");
    }

    private double[] getRegX(Statistics statistics) throws RegressionModelException {
        try {
            RaceProgramHorse raceProgramHorse = statistics.getRaceProgramHorse();
            BigDecimal tasoitus = raceProgramHorse.getTasoitus();

            List<BigDecimal> xList = new ArrayList();

            // tasoitus ekana
            //xList.add(tasoitus);

            /*
            xList.add(getStarts());
            xList.add(firstRate());
            xList.add(sijaRate());
            xList.add(awardRate());
            //xList.add(getKcodeRate());
             */
            xList.add(recordTime);
            //xList.add(lastAward);

            double[] x = new double[xList.size()];

            int xi = 0;
            for (BigDecimal b : xList) {
                x[xi++] = b.doubleValue();
            }

            return x;
        } catch (NoSuchElementException e) {
            throw new RegressionModelException();
        } catch (NullPointerException e) {
            throw new RegressionModelException();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RegressionModelException();
        }
    }

    public double getRegY(Statistics statistics) throws RegressionModelException {
        try {
            double[] x = getRegX(statistics);

            List keys = getRegKey(statistics);

            //y = HarnessApp.regMap.get(getLabel()).get(x);
            y = HarnessApp.regMap.get(keys).get(x);

            //System.out.println("SubForm.getRegY: "  + statistics.getName() + " " + getLabel() + ": " + Arrays.toString(x) + " ==> " + y);

            return y;
        } catch (RegressionModelException e) {
            throw e;
        } catch (NullPointerException e) {
            throw new RegressionModelException();
        } catch (ModelSpecificationException e) {
            // Not enough data
            throw new RegressionModelException();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RegressionModelException();
        }
    }

    private List getRegKey(Statistics statistics) {
        List keys = new ArrayList();

        //keys.add(fullStatistics.getRaceProgramHorse().getTrackId());
        keys.add(getLabel());

        return keys;
    }

    public void addObservations(BigDecimal raceResultPrize, Statistics statistics) throws RegressionModelException {

        try {
            double[] x = getRegX(statistics);

            List keys = getRegKey(statistics);

            HarnessApp.regMap.getOrCreate(keys, new HipposUpdatingRegression(x.length)).add(x, raceResultPrize.doubleValue());

        } catch (NullPointerException e) {
            throw new RegressionModelException();
        } catch (RegressionModelException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RegressionModelException();
        }
    }

    public String getRaceMode() {
        return raceMode;
    }

    public BigDecimal getRecordTime() {
        return recordTime;
    }

    public AlphaNumber getRaceModeTime() {
        try {
            return new AlphaNumber(recordTime, raceMode);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(super.toString());
        sb.append(" " + recordTime + getLabel());
        sb.append(" " + lastAward);

        sb.append(" ==> " + y);
        return sb.toString();

    }

}
