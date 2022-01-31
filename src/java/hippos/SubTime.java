package hippos;

import hippos.lang.stats.SubRaceTime;
import hippos.math.*;
import hippos.exception.RegressionModelException;
import hippos.util.RegressionMapObservation;
import hippos.util.RegressionMapper;
import hippos.utils.DateUtils;
import utils.Log;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Feb 1, 2006
 * Time: 12:53:24 AM
 * To change this template use Options | File Templates.
 */
public class SubTime extends AlphaNumber implements Comparable, RegressionMapObservation {
    private static RegressionMapper subTimeMapper = new RegressionMapper();
    private RaceProgramHorse raceProgramHorse;
    private RaceResultHorse raceResultHorse;

    private SubValue subValue;
    private SubStart subStart;

    private BigDecimal dateDiff;
    private boolean x = false;
    private BigDecimal k = BigDecimal.ZERO;
    private BigDecimal driverClass;
    private BigDecimal raceAward = BigDecimal.ZERO;
    private Date date;

    private SubRaceTime subRaceTime = null;
    private double season;
    private BigDecimal progressMultiplier;

    public SubTime(String str, SubStart subStart) {
        super(str);

        this.subStart = subStart;
        if (getNumber() == null) {
            if (getAlpha() != null) {
                setAlpha(getAlpha().replace("-", ""));
            }
        }
        this.subValue = new SubTimeValue(this);
    }

    public SubTime(BigDecimal aika, String raceMode, SubStart subStart) {
        super(aika, raceMode);
        this.subStart = subStart;
        this.subValue = new SubTimeValue(this);
    }

    public String getNumberString() {
        return super.toString();
    }

    public boolean getX() {
        String xCode = subStart.getxCode();
        if (xCode != null && xCode.indexOf("x") >= 0)
            return true;
        return false;
    }

    public BigDecimal getDateDiff() {
        return subStart.getDateDiff();
    }

    public boolean isX() {
        return x;
    }

    public BigDecimal getRaceAward() {
        return subStart.getAward();
    }

    public SubValue getSubValue() {
        return subValue;
    }

    public void setSubRaceTime(SubRaceTime subRaceTime) {
        this.subRaceTime = subRaceTime;
    }

    public SubRaceTime getSubRaceTime() {
        return subRaceTime;
    }


    public void setDate(Date raceDate, BigDecimal dateDiff) {
        int daysToStart = dateDiff.intValue();
        this.date = DateUtils.rollDays(raceDate, daysToStart);
    }

    public Date getDate() {
        return subStart.getDate();
    }

    public double getSeason() {
        return subStart.getSeason();
    }

    public void addObservation(RaceProgramHorse raceProgramHorse, RaceResultHorse raceResultHorse) {
        this.raceProgramHorse = raceProgramHorse;
        this.raceResultHorse = raceResultHorse;

        try {
            if (raceResultHorse.getX().intValue() == 0) {
                subTimeMapper.add(this);
            }
        } catch (RegressionModelException e) {
            //e.printStackTrace();
        } catch (Exception e) {
            Log.write(e);
        }
    }

    public void getObservation(RaceProgramHorse raceProgramHorse) {
        this.raceProgramHorse = raceProgramHorse;

        try {

            double y = subTimeMapper.get(this);

            subValue.updateValue(y);

        } catch (RegressionModelException e) {
            //e.printStackTrace();
        } catch (Exception e) {
            Log.write(e);
        }
    }

    @Override
    public List getRegKey() throws RegressionModelException {
        try {
            List keyList = new ArrayList();
            //keyList.add(getAlpha());
            keyList.add(raceProgramHorse.getRaceMode());
            //keyList.add(raceProgramHorse.getRaceHandicap());
            //keyList.add(raceProgramHorse.getTrackId());
            //keyList.add(getK());

            return keyList;
        } catch (NullPointerException e) {
            throw new RegressionModelException();
        } catch (Exception e) {
            Log.write(e);
            throw new RegressionModelException();
        }
    }

    /**
     * TODO: kuljettajakerroin ei vakuuta
     *
     * @return
     * @throws RegressionModelException
     */
    @Override
    public double[] getRegAddX() throws RegressionModelException {
        double[] x = new double[1];

        try {
            x[0] = getSubRaceTime().getBigDecimal().doubleValue();

            // Ei toimi ollenkaan
            //x[1] = getDriverDiff(raceProgramHorse.getRaceProgramDriver()).doubleValue();

            //x[2] = getLeadingChange();

            /* liian raju
            x[3] = getProgressMultiplier().doubleValue();

            if(x[3] > 2 || x[3] <= 0)
                x[3] = 1.0;
            */

            return x;
        } catch (NullPointerException e) {
            throw new RegressionModelException();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RegressionModelException();
        }
    }

    private double getLeadingChange() {
        try {
            BigDecimal subLead = getK();
            BigDecimal resultLead = raceResultHorse.getVA_2();

            resultLead = resultLead != null ? BigDecimal.ONE : BigDecimal.ZERO;

            return resultLead.subtract(subLead).doubleValue();
        } catch (Exception e) {
            //e.printStackTrace();

            return 0;
        }
    }


    /**
     * TODO: kuljettajakerroin ei vakuuta
     *
     * @return
     * @throws RegressionModelException
     */
    @Override
    public double[] getRegGetX() throws RegressionModelException {

        try {
            double[] x = getRegAddX();

            //x[2] = raceProgramHorse.getLeadingChange(subStart);

            /* liian raju
            x[1] = getProgressMultiplier().doubleValue();

            if(x[1] > 2 || x[1] <= 0)
                x[1] = 1.0;
            */

            return x;
        } catch (RegressionModelException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RegressionModelException();
        }
    }

    public BigDecimal getDriverDiff(RaceProgramDriver raceProgramDriver) {
        try {
            return raceProgramDriver.getDriverDiff(subStart);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    @Override
    public double getRegY() throws RegressionModelException {
        try {
            return raceResultHorse.getRaceResultPrize().doubleValue();
        } catch (NullPointerException e) {
            throw new RegressionModelException();
        } catch (Exception e) {
            Log.write(e);
            throw new RegressionModelException();
        }
    }

    public void setProgressMultiplier(BigDecimal progressMultiplier) {
        this.progressMultiplier = progressMultiplier;
    }

    public BigDecimal getProgressMultiplier() {
        return progressMultiplier;
    }

    /*
    public int compareTo(Object o) {
        SubTime aSubTime = (SubTime) o;

        if(this.hashCode() == o.hashCode())
            return 0;


        if (this.getNumber() == null)
            return 1;

        if(aSubTime.getNumber() == null)
            return -1;

        return getNumber().compareTo(aSubTime.getNumber());

        // Miksi oli näin?
        //return subValue.compareTo(aSubTime.getSubValue());
    }*/

    public BigDecimal getK() {
        return subStart.getkCode();
    }

    public SubStart getSubStart() {
        return subStart;
    }

    public String toString() {
        try {
            // Ei saa muuttaa, tulostaa programhorsen substart_ tauluihin

            return super.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }

}
