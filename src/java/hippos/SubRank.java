package hippos;

import hippos.lang.stats.SubRaceTime;
import hippos.math.*;
import hippos.exception.RegressionModelException;
import hippos.util.RegressionMapObservation;
import hippos.util.RegressionMapper;
import utils.Log;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Feb 23, 2006
 * Time: 12:27:34 AM
 * To change this template use Options | File Templates.
 */
public class SubRank extends AlphaNumber implements RegressionMapObservation {
    private static RegressionMapper subRankMapper = new RegressionMapper();
    private RaceProgramHorse raceProgramHorse;
    private RaceResultHorse raceResultHorse;

    private SubValue subValue;
    private SubStart subStart;

    private BigDecimal driverClass;
    private BigDecimal dateDiff;
    private boolean x = false;
    private BigDecimal k = BigDecimal.ZERO;
    private BigDecimal raceAward = BigDecimal.ZERO;
    private SubRaceTime subRaceTime = null;
    private BigDecimal progressMultiplier;

    public SubRank(String str, SubStart subStart) {
        super(str);
        this.subStart = subStart;
        subValue = new SubRankValue(this);
        //subValue = new SubRankValue(this);
    }

    public SubRank(BigDecimal ranking) {
        super(ranking);
        subValue = new SubRankValue(this);
        //subValue = new SubRankValue(this);
    }

    public SubRank(BigDecimal ranking, SubStart subStart) {
        super(ranking);
        this.subStart = subStart;
        subValue = new SubRankValue(this);
        //subValue = new SubRankValue(this);
    }

    public SubRank(BigDecimal ranking, String xCode, SubStart subStart) {
        super(ranking, xCode);
        this.subStart = subStart;
        subValue = new SubRankValue(this);
        //subValue = new SubRankValue(this);
    }

    public int compareTo(Object o) {
        SubRank ranking = (SubRank)o;
        if(getNumber() != null && (getAlpha() == null || !getAlpha().contains("h"))) {
            if(ranking.getNumber() != null && (ranking.getAlpha() == null || !ranking.getAlpha().contains("h"))) {
                if(!getNumber().equals(ranking.getNumber())) {
                    if(getNumber().intValue() > 0) {
                        if(ranking.getNumber().intValue() > 0) {
                            return getNumber().compareTo(ranking.getNumber());
                        }
                        return -1;
                    }
                    return 1;
                }
                return 1;
            }
            return -1;
        }
        return 1;
    }

    /*
    public void setX(String xCode) {
        if(xCode != null && xCode.indexOf("x") >= 0) {
            this.x = true;
            if(getAlpha() != null) {
                if(getAlpha().indexOf("x") >= 0) {
                   //System.out.println("Ei pitäisi olla");
                } else {
                    setAlpha(getAlpha() + "x");
                }
            } else {
                setAlpha("x");
            }
        }
    }*/

    public boolean getX() {
        String xCode = subStart.getxCode();
        if (xCode != null && xCode.indexOf("x") >= 0)
            return true;
        return false;
    }

    public BigDecimal getK() {
        return subStart.getkCode();
    }

    public boolean isComparable() {
        return (getNumber() != null && getAlpha() == null);
    }

    public BigDecimal getDateDiff() {
        return subStart.getDateDiff();
    }

    public BigDecimal getDriverClass() {
        return subStart.getDriverRaceTypeClass();
    }

    private BigDecimal getDriverDiff(RaceProgramDriver raceProgramDriver) {
        try {
            return raceProgramDriver.getDriverDiff(subStart);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getRaceAward() {
        return subStart.getAward();
    }

    /*
    public void setRaceAward(BigDecimal raceAward) {
        if(raceAward != null) {
            this.raceAward = raceAward;
        }
    }*/

    public String toString() {
        try {
            // Ei saa muuttaa, tulostaa programhorsen substart_?? tauluihin

            return super.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }

    public String getNumberString() {
        return super.toString();
    }

    public void setSubRaceTime(SubRaceTime subRaceTime) {
        this.subRaceTime = subRaceTime;
    }

    public SubRaceTime getSubRaceTime() {
        return subRaceTime;
    }

    public SubValue getSubValue() {
        return subValue;
    }

    public void addObservation(RaceProgramHorse raceProgramHorse, RaceResultHorse raceResultHorse) {
        this.raceProgramHorse = raceProgramHorse;
        this.raceResultHorse = raceResultHorse;

        try {
            if(raceResultHorse.getX().intValue() == 0) {
                subRankMapper.add(this);
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
            double y = subRankMapper.get(this);
            // TODO: aktivoi kun mapperi valmis
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
            //keyList.add(subStart.getSubTime().getAlpha());
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

    @Override
    public double[] getRegAddX() throws RegressionModelException {
        try {
            double [] x = new double[2];

            x[0] = getSubRaceTime().getBigDecimal().doubleValue();
            //x[1] = getRaceAward() != null ? getRaceAward().doubleValue() : 0.0;

            x[1] = getBigDecimal().doubleValue();
            if(x[1] == 0 || x[1] > 9) {
                // Ruotsalaishevosilla 0-arvoja
                x[1] = 9;
            }

            // ei toimi ollenkaan
            //x[2] = getDriverDiff(raceProgramHorse.getRaceProgramDriver()).doubleValue();

            // Liian raju
            //x[3] = getLeadingChange();

            /* Liian raju
            x[4] = getProgressMultiplier().doubleValue();

            if(x[4] > 2 || x[4] <= 0)
                x[4] = 1.0;
            */

            return x;
        } catch (NullPointerException e) {
            throw new RegressionModelException();
        } catch (Exception e) {
            Log.write(e);
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

    public double[] getRegGetX() throws RegressionModelException {
        try {
            double [] x = getRegAddX();

            //x[3] = raceProgramHorse.getLeadingChange(subStart);

            return x;
        } catch (RegressionModelException e) {
            throw e;
        } catch (Exception e) {
            Log.write(e);
            throw new RegressionModelException();
        }
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
    public void setK(BigDecimal k) {
        this.k = k;
    }*/
}
