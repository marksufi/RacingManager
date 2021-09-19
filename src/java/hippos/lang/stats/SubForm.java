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
    private final BigDecimal lastRanking;
    private final BigDecimal recordTime;

    private double y = Double.NaN;

    public SubForm(ResultSet set) throws SQLException {
        super(set);
        this.raceMode = set.getString("LAHTOTYYPPI");
        setLabel(this.raceMode);

        this.lastRanking = set.getBigDecimal("VSIJOITUS");
        this.recordTime = set.getBigDecimal("AIKA");
    }

    private double[] getRegX(FullStatistics fullStatistics) throws RegressionModelException {
        try {
            RaceProgramHorse raceProgramHorse = fullStatistics.getRaceProgramHorse();
            BigDecimal tasoitus = raceProgramHorse.getTasoitus();

            List<BigDecimal> xList = new ArrayList();

            // tasoitus ekana
            xList.add(tasoitus);

            xList.add(getStarts());
            xList.add(getFirsts());
            xList.add(getSeconds());
            xList.add(getThirds());
            xList.add(getAwards());
            //xList.add(getXcode());
            xList.add(getKcode());
            xList.add(recordTime);
            xList.add(lastRanking);

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

    public double getRegY(FullStatistics fullStatistics) throws RegressionModelException {
        try {
            double[] x = getRegX(fullStatistics);

            y = HarnessApp.regMap.get(getLabel()).get(x);

            System.out.println("SubForm.getRegY: "  + fullStatistics.getName() + " " + getLabel() + ": " + Arrays.toString(x) + " ==> " + y);

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

    public void learn(BigDecimal raceResultPrize, FullStatistics fullStatistics) throws RegressionModelException {

        try {
            double[] x = getRegX(fullStatistics);

            HarnessApp.regMap.getOrCreate(getLabel(), new HipposUpdatingRegression(x.length)).add(x, raceResultPrize.doubleValue());

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
        return new AlphaNumber(recordTime, raceMode);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(super.toString());
        sb.append(" " + recordTime + getLabel());
        sb.append(" " + lastRanking);

        sb.append(" ==> " + y);
        return sb.toString();

    }

}
