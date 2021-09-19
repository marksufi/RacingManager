package hippos.lang.stats;

import hippos.RaceProgramStart;
import hippos.exception.RegressionModelException;
import hippos.math.AlphaNumber;
import hippos.math.regression.HipposUpdatingRegression;
import hippos.util.QuarterTimes;
import org.apache.commons.math3.stat.regression.ModelSpecificationException;
import utils.Log;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class TimeForm extends Form {
    private String raceMode;
    private BigDecimal lastRanking;
    private Form cForm = new Form("C"); // Puhtaat juoksut
    private Form kForm = new Form("->"); // Keulapaikkajuoksut
    private Form xForm = new Form("-x"); // Laukkajuoksut

    //private AlphaNumber recordTime;
    private SortedSet<AlphaNumber> recordTimes = new TreeSet<>();
    private SortedSet<AlphaNumber> aRecordTimes = new TreeSet<>();
    private SortedSet<AlphaNumber> tRecordTimes = new TreeSet<>();

    private List<TimeForm> forms = new ArrayList();

    public TimeForm(String label) {
        super(label);
    }

    public TimeForm(String label, String initstring) {
        super(label);
        init(initstring);
    }

    /**
     * Parseri kutsuu tätä
     *
     * @param set Tilastotiedot kannasta
     * @throws SQLException
     */
    public TimeForm(ResultSet set) throws SQLException {
        super(set);
        //setLabel(set.getString("TYYPPI"));
        this.raceMode = set.getString("LAHTOTYYPPI");
        setLabel(this.raceMode);

        this.lastRanking = set.getBigDecimal("VSIJOITUS");
        addRecordTime(set.getBigDecimal("AIKA"));
    }

    public void init(String str) {
        try {
            String[] tokens = str.split(":");

            setStarts(new BigDecimal(tokens[0]));
            setFirsts(new BigDecimal(tokens[1]));
            setSeconds(new BigDecimal(tokens[2]));
            setThirds(new BigDecimal(tokens[3]));
            setAwards(new BigDecimal(tokens[4]));
            setKcode(new BigDecimal(tokens[5]));
            setXcode(new BigDecimal(tokens[6]));

            kForm.setStarts(new BigDecimal(tokens[7]));
            kForm.setFirsts(new BigDecimal(tokens[8]));
            kForm.setSeconds(new BigDecimal(tokens[9]));
            kForm.setThirds(new BigDecimal(tokens[10]));
            kForm.setAwards(new BigDecimal(tokens[11]));
            kForm.setKcode(new BigDecimal(tokens[12]));
            kForm.setXcode(new BigDecimal(tokens[13]));

            // Lisää ennätykset
            String times = tokens[14];
            StringTokenizer st = new StringTokenizer(times, "[,]");

            while (st.hasMoreTokens()) {
                addRecordTime(new AlphaNumber(st.nextToken()));
            }

            kForm.setProbability(getStarts().add(kForm.getStarts()));

        } catch (NullPointerException e) {
            // str tai joku muu on null
        } catch (Exception e) {
            Log.write(e);
        }

    }

    public void addRecordTime(BigDecimal recordTime) {
        if (recordTime != null) {
            recordTimes.add(new AlphaNumber(recordTime, this.raceMode));
            if (this.raceMode.contains("a")) {
                aRecordTimes.add(new AlphaNumber(recordTime, this.raceMode));
            } else {
                tRecordTimes.add(new AlphaNumber(recordTime, this.raceMode));
            }
        }
    }

    public void addRecordTime(AlphaNumber recordTime) {
        if (recordTime != null && recordTime.getNumber() != null) {
            recordTimes.add(recordTime);
            if (recordTime.getAlpha().contains("a")) {
                aRecordTimes.add(recordTime);
            } else {
                tRecordTimes.add(recordTime);
            }
        }
    }

    public Form getkForm() {
        return kForm;
    }

    public void setkForm(Form kForm) {
        this.kForm = kForm;
    }

    public void add(SubForm form) {
        try {
            if(form.getXcode().intValue() > 0) {
                // Laukkatilastot omaan
                xForm.add(form);
            } else if (form.getKcode().intValue() > 0) {
                // Paalutilastot omaan
                kForm.add(form);
            } else {
                cForm.add(form);
            }

            super.add(form);

            addRecordTime(form.getRaceModeTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getLabel() + ": " + getStarts() + " " + getFirsts() + "-" + getSeconds() + "-" + getThirds() + " (" + getAwardRate() + "€/s) " );
        sb.append("-> " + getKcode() + "(" + getKcodeProcents(BigDecimal.ZERO) + "%)");
        sb.append(" - " + getXcode() + "x(" + getXcodeProcents(BigDecimal.ZERO) + "%)");
        //sb.append("{" + getProbability() + "%} ");

        sb.append(" " + recordTimes);

        sb.append("\n\t" + cForm.toString());
        sb.append("\n\t" + kForm.toString());
        sb.append(xForm.toString());



        return sb.toString();
    }

    public String getAsString() {
        StringBuffer sb = new StringBuffer();

        sb.append(super.getAsString());

        sb.append(kForm.getAsString());

        sb.append(GetRecordTimeList());

        return sb.toString();
    }

    private List GetRecordTimeList() {
        List recordTimeList = new ArrayList<>();

        if (!aRecordTimes.isEmpty()) {
            recordTimeList.add(aRecordTimes.first());
            if (tRecordTimes.isEmpty() && aRecordTimes.size() > 1) {
                recordTimeList.add(aRecordTimes.toArray()[1]);
            }
        }

        if (!tRecordTimes.isEmpty()) {
            recordTimeList.add(tRecordTimes.first());
            if (aRecordTimes.isEmpty()) {
                if (tRecordTimes.size() > 1) {
                    recordTimeList.add(tRecordTimes.toArray()[1]);
                }
            }
        }

        return recordTimeList;

    }

    /*
    private double[] getRegX(TimeStatistics timeStatistics) throws RegressionModelException {
        try {

            List<BigDecimal> xList = new ArrayList();

            QuarterTimes qt2 = timeStatistics.getSecondQuarter();

            //BigDecimal kp = qt2.getPropabiltyProcents();
            BigDecimal kp = BigDecimal.ONE;
            BigDecimal xp = BigDecimal.ONE;
            BigDecimal cp = BigDecimal.ONE;

            xList.add(cForm.getStarts().multiply(cp));
            xList.add(cForm.getFirsts().multiply(cp));
            xList.add(cForm.getSeconds().multiply(cp));
            xList.add(cForm.getThirds().multiply(cp));
            xList.add(cForm.getAwards().multiply(cp));

            xList.add(xForm.getStarts().multiply(xp));
            xList.add(xForm.getFirsts().multiply(xp));
            xList.add(xForm.getSeconds().multiply(xp));
            xList.add(xForm.getThirds().multiply(xp));
            xList.add(xForm.getAwards().multiply(xp));

            xList.add(kForm.getStarts().multiply(kp));
            xList.add(kForm.getFirsts().multiply(kp));
            xList.add(kForm.getSeconds().multiply(kp));
            xList.add(kForm.getThirds().multiply(kp));
            xList.add(kForm.getAwards().multiply(kp));

            xList.add(recordTimes.first().getBigDecimal());

            double[] x = new double[xList.size()];

            int xi = 0;
            for (BigDecimal b : xList) {
                x[xi++] = b.doubleValue();
            }

            //System.out.println(Arrays.toString(x) + " => " + RaceProgramStart.featuredReg.get(getLabel()).get(x));

            return x;
        } catch (NoSuchElementException e) {
            throw new RegressionModelException();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }*/
}
