package hippos.lang.stats;

import hippos.*;
import hippos.math.AlphaNumber;
import hippos.util.*;
import hippos.exception.RegressionModelException;
import hippos.utils.HorsesHelper;
import utils.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class SubRaceTime extends AlphaNumber {
    private static RegressionMapper regressionMapper = new RegressionMapper();
    //private static Mapper<SumReg> raceTypeMapper = new Mapper<>();
    private static ChangeMapper raceTypeMapper = new RaceTypeMapper();
    //private static Mapper<SumReg> seasonMapper = new Mapper<>();
    //private static Mapper<SumReg> driverMapper = new Mapper<>();
    private static ChangeMapper weatherMapper = new WeatherMapper();
    private double x[] = null;

    private static final int LENGTH = 3;

    private SubRank subRank;


    /**
     * lähtöön ja lähtötapaan suhteutettu aika
     *
     * @param newSubTime    Muunnettava tulosaika
     * @param listSubTime   Kohdeaika
     *
     * @throws RegressionModelException
     *                      Jos regressio menee pieleen
     */
    public static void addObservation(SubStart subStart, SubStart targetSubStart) {
        try {
            add(subStart, targetSubStart);

        } catch (Exception e) {
            e.printStackTrace();
            Log.write(e);
        }
    }

    /**
     * lähtöön ja lähtötapaan suhteutettu aika, jossa otetaan huomioon vuodenaika
     *
     * @param newSubTime        Muunnettava tulosaika
     * @param raceProgramHorse  Lähdön hevonen, jolle aikaa muunnetaan
     */
    public SubRaceTime(SubStart subStart, RaceProgramHorse raceProgramHorse) throws RegressionModelException {
        try {

            x = getRegX(subStart, raceProgramHorse);

            double regressionTime = x[0] + x[1];// + x[2];

            BigDecimal tasoitusAika = HorsesHelper.tasoitusaika(regressionTime, raceProgramHorse.getRaceLength().doubleValue(), raceProgramHorse.getTasoitus().doubleValue());

            x[2] = tasoitusAika.subtract(BigDecimal.valueOf(regressionTime)).setScale(1, RoundingMode.HALF_DOWN).doubleValue();
            //setNumber(new BigDecimal(regressionTime).setScale(1, RoundingMode.HALF_UP));
            setNumber(tasoitusAika);
            setAlpha(raceProgramHorse.getRaceMode());

            subRank = subStart.getSubRank();
        } catch (NumberFormatException e) {
        } catch (RegressionModelException e) {
            throw e;
        } catch (NullPointerException e) {
            throw new RegressionModelException();
        } catch (Exception e) {
            Log.write(e);
        }
    }


    private static void add(SubStart subStart, SubStart targetSubStart) {
        try {
            raceTypeMapper.addChange(subStart, targetSubStart);
            weatherMapper.addChange(subStart, targetSubStart);

        } catch (Exception e) {
            e.printStackTrace();
            Log.write(e);
        }
    }

    private double[] getRegX(SubStart subStart, RaceProgramHorse raceProgramHorse) throws RegressionModelException {
        try {
            //List raceModeKey = getRaceModeKey(newSubTime, raceProgramHorse);
            //List seasonKey = getSeasonKey(newSubTime, raceProgramHorse);

            //double raceTypeChange = raceTypeMapper.get(raceModeKey).getChange().doubleValue();
            double raceTypeChange = raceTypeMapper.getChange(subStart, raceProgramHorse);
            //double weatherChange = weatherMapper.getChange(subStart, raceProgramHorse);

            double[] x = new double[LENGTH];

            x[0] = subStart.getSubTime().getNumber().doubleValue();
            x[1] = raceTypeChange;
            // Tasoitus lisätään myöhemmin
            //x[2] = weatherChange;
            //x[3] = newSubTime.getDriverDiff(raceProgramHorse.getRaceProgramDriver()).doubleValue();

            return x;
        } catch (NullPointerException e) {
            throw new RegressionModelException();
        } catch (Exception e) {
            Log.write(e);
            throw new RegressionModelException();
        }

    }

    private List getRaceModeKey(SubTime subTime, RaceProgramHorse raceProgramHorse) throws RegressionModelException {

        try {
            //String newRaceMode = subTime.getAlpha() + (subTime.getX() ? "x" : "");
            String newRaceMode = subTime.getAlpha();
            String targetRaceMode = raceProgramHorse.getRaceMode();

            if(newRaceMode == null || targetRaceMode == null)
                throw new RegressionModelException();

            List key = new ArrayList();
            key.add(newRaceMode);
            key.add(targetRaceMode);

            return key;
        } catch (Exception e) {
            throw new RegressionModelException();
        }
    }

    private static List getRaceModeKey(SubTime subTime, SubTime targetSubTime) throws RegressionModelException {

        //String newRaceMode = subTime.getAlpha() + (subTime.getX() ? "x" : "");
        String newRaceMode = subTime.getAlpha();
        String targetRaceMode = targetSubTime.getAlpha();

        if(newRaceMode == null || targetRaceMode == null)
            throw new RegressionModelException();

        List key = new ArrayList();
        key.add(newRaceMode);
        key.add(targetRaceMode);

        return key;
    }

    public RaceMode getRaceMode() {
        return new RaceMode(getAlpha());
    }

    public SubRank getSubRank() {
        return subRank;
    }

    public void setSubRank(SubRank subRank) {
        this.subRank = subRank;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(super.toString());
        if(x!= null) {
            sb.append(Arrays.toString(x));
        }
        return sb.toString();
    }
}
