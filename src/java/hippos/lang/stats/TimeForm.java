package hippos.lang.stats;

import hippos.exception.Missing;
import hippos.math.AlphaNumber;
import utils.Log;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class TimeForm extends Form {
    private String raceMode;
    private Form kForm = new Form("->");

    //private AlphaNumber recordTime;
    private SortedSet<AlphaNumber> recordTimes = new TreeSet<>();
    private SortedSet<AlphaNumber> aRecordTimes = new TreeSet<>();
    private SortedSet<AlphaNumber> tRecordTimes = new TreeSet<>();

    private List <TimeForm> forms = new ArrayList();

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
     * @param set   Tilastotiedot kannasta
     * @throws SQLException
     */
    public TimeForm(ResultSet set) throws SQLException {
        super(set);
        setLabel(set.getString("TYYPPI"));
        this.raceMode = set.getString("LAHTOTYYPPI");
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
        if(recordTime != null) {
            recordTimes.add(new AlphaNumber(recordTime, this.raceMode));
            if(this.raceMode.contains("a")) {
                aRecordTimes.add(new AlphaNumber(recordTime, this.raceMode));
            } else {
                tRecordTimes.add(new AlphaNumber(recordTime, this.raceMode));
            }
        }
    }

    public void addRecordTime(AlphaNumber recordTime) {
        if(recordTime != null && recordTime.getNumber() != null) {
            recordTimes.add(recordTime);
            if(recordTime.getAlpha().contains("a")) {
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

    public void add(TimeForm form) {
        if(form.getKcode().intValue() > 0) {
            // Paalutilastot omaan
            kForm.add(form);
        } else {
            super.add(form);
        }

        try { recordTimes.addAll(form.recordTimes);  } catch (NullPointerException e) {}
        try { aRecordTimes.addAll(form.aRecordTimes);  } catch (NullPointerException e) {}
        try { tRecordTimes.addAll(form.tRecordTimes);  } catch (NullPointerException e) {}
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getLabel() + ": " + getStarts() + " " + getFirsts() + "-" + getSeconds() + "-" + getThirds() + "-" + getXcode() + " (" + getAwardRate() + "€/s) " );

        //sb.append(kForm.getLabel() + kForm.getStarts() + " " + kForm.getFirsts() + "-" + kForm.getSeconds() + "-" + kForm.getThirds()+ "-" + kForm.getXcode() + " (" + kForm.getAwardRate() + "€/s) " );
        sb.append("->> " + kForm.getStarts() + " " + kForm.getFirsts() + "-" + kForm.getSeconds() + "-" + kForm.getThirds()+ "-" + kForm.getXcode() + " (" + kForm.getAwardRate() + "€/s) " );

        sb.append("{" + kForm.getProbability() + "%} ");
        /*
        for(TimeForm timeForm : forms) {
            sb.append(timeForm.toString());
        }*/

        while (sb.length() < 50)
            sb.append(" ");

        sb.append(recordTimes);

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

}
