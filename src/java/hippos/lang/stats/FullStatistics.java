package hippos.lang.stats;

import hippos.RaceProgramHorse;
import hippos.database.Statements;
import hippos.math.AlphaNumber;
import hippos.util.Mapper;
import hippos.utils.DateUtils;
import utils.Log;

import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class FullStatistics extends TimeForm {
    private RaceProgramHorse raceProgramHorse;
    private TimeStatistics timeStatistics;
    private java.util.Date endDate;
    private String name;
    private String race;

    private Mapper<TimeForm> forms = new Mapper<>();
    //private Mapper<Form> featuredStats = new Mapper<>();


    public FullStatistics(String label, RaceProgramHorse raceProgramHorse) {
        super(label);
        this.raceProgramHorse = raceProgramHorse;
        this.endDate = new Date(raceProgramHorse.getRaceProgramStart().getDate().getTime());
        this.name = raceProgramHorse.getRaceHorseName();
        this.race= raceProgramHorse.getRaceMode().substring(0, 1);
        this.timeStatistics = new TimeStatistics(raceProgramHorse);
    }

    public void fetchForm(Connection conn) {
        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = Statements.getTimeFormStatement(conn, name, race, DateUtils.toSQLDate(endDate));

            set = statement.executeQuery();
            while(set.next()) {
                TimeForm form = new TimeForm(set);

                List keyList = new ArrayList();
                keyList.add(form.getLabel());

                forms.getOrCreate(Collections.singletonList(form.getLabel()), new TimeForm(form.getLabel())).add(form);

                super.add(form);

            }

            //fetchFeaturedStats(conn);

        } catch (Exception e) {
            Log.write(e);
        } finally {
            try { set.close(); } catch (Exception e) { }
            try { statement.close();} catch (Exception e) { }
        }
    }

    public String toString() {
        StringBuffer str = new StringBuffer();

        str.append(super.toString());

        for(TimeForm timeForm : forms.getValues()) {
            str.append("\n\t" + timeForm);
        }

        return str.toString();
    }


    public TimeStatistics getTimeStatistics() {
        return timeStatistics;
    }

    /*
    private void fetchFeaturedStats(Connection conn) {
        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = conn.prepareStatement("select count(*), sum(S_1), sum(S_2), sum(S_3), sum(palkinto), sum(KCODE), count(XCODE), KCODE from SUBRESULT where NIMI=? and PVM<? group by KCODE");
            statement.setString(1, raceProgramHorse.getRaceHorseName());
            statement.setDate(2, raceProgramHorse.getRaceProgramStart().getSQLDate());
            set = statement.executeQuery();

            Form K0Form = new Form("K0");
            Form K1Form = new Form("K1");

            while(set.next()) {
                Form form = new Form(set);

                BigDecimal kcode = set.getBigDecimal(8);

                switch (kcode.intValue()) {
                    case 0: K0Form.add(form);
                        break;
                    case 1: K1Form.add(form);
                        break;
                    default:
                        continue;
                }
            }

            BigDecimal allStartCount = K0Form.getStarts().add(K1Form.getStarts());
            K0Form.setProbability(allStartCount);
            K1Form.setProbability(allStartCount);

            featuredStats.put(Collections.singletonList(BigDecimal.ZERO), K0Form);
            featuredStats.put(Collections.singletonList(BigDecimal.ONE), K1Form);

        } catch (Exception e) {
            Log.write(e);
        } finally {
            try { set.close(); } catch (Exception e) { }
            try { statement.close();} catch (Exception e) { }
        }
    }*/

    /*
    public Mapper<Form> getFeaturedStats() {
        return featuredStats;
    }*/

    public String getAsString() {
        StringBuffer sb = new StringBuffer();

        try {
            sb.append(super.getAsString());


        } catch (NullPointerException e) {
            // Etsittyä formia ei ole
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    public String getAsString(List keys) {
        StringBuffer sb = new StringBuffer();

        try {
            TimeForm form = forms.get(keys);
            sb.append(form.getAsString());

        } catch (NullPointerException e) {
            // Etsittyä formia ei ole
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    public void init(String label, String str) {
        try {
            System.out.println("FullStatistics.init: " + str);

            TimeForm timeForm = new TimeForm(label);
            timeForm.init(str);

            forms.put(Collections.singletonList(label), timeForm);

        } catch (NullPointerException e) {

        } catch (Exception e) {
            Log.write(e);
        }
    }

    public void init(String str) {
        try {
            super.init(str);

        } catch (NullPointerException e) {

        } catch (Exception e) {
            Log.write(e);
        }
    }

    public Form get(List y) {
        return forms.get(y);
    }

    public Form getForm() {
        return this;
    }


}


