package hippos.lang.stats;

import hippos.RaceProgramHorse;
import hippos.database.Statements;
import hippos.util.Mapper;
import hippos.utils.DateUtils;
import utils.Log;

import java.sql.*;
import java.sql.Date;
import java.util.*;

public class FullStatistics extends TimeForm {
    private RaceProgramHorse raceProgramHorse;
    private java.util.Date endDate;
    private String name;
    private String race;

    private List<SubForm> subForms = new ArrayList<>();

    public FullStatistics(String label, RaceProgramHorse raceProgramHorse) {
        super(label);
        this.raceProgramHorse = raceProgramHorse;
        this.endDate = new Date(raceProgramHorse.getRaceProgramStart().getDate().getTime());
        this.name = raceProgramHorse.getRaceHorseName();
        this.race= raceProgramHorse.getRaceMode().substring(0, 1);
    }

    /*
    public void fetchForm(Connection conn) {
        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = Statements.getTimeFormStatement(conn, name, race, DateUtils.toSQLDate(endDate));

            set = statement.executeQuery();
            while(set.next()) {
                TimeForm form = new TimeForm(set);

                subForms.add()
                //List keyList = new ArrayList();
                //keyList.add(form.getLabel());

                try{

                    subForms.get(form.getLabel()).add(form);
                } catch (Exception e) {
                    subForms.put(form.getLabel(), form);
                }

                super.add(form);
            }

            //fetchFeaturedStats(conn);

        } catch (Exception e) {
            Log.write(e);
        } finally {
            try { set.close(); } catch (Exception e) { }
            try { statement.close();} catch (Exception e) { }
        }
    }*/

    public void fetchSubForms(Connection conn) {
        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = Statements.getTimeFormStatement(conn, name, race, DateUtils.toSQLDate(endDate));

            set = statement.executeQuery();
            while(set.next()) {
                SubForm subForm = new SubForm(set);

                subForms.add(subForm);

                super.add(subForm);
            }

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

        for(SubForm subForm : subForms) {
            str.append("\n\t" + subForm);
        }

        return str.toString();
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

    public void init(String label, String str) {
        /* Ei talleteta enään kantaan, jos reaaliaikainen lasku on riittävän nopea
        try {
            System.out.println("FullStatistics.init: " + str);

            TimeForm timeForm = new TimeForm(label);
            timeForm.init(str);

            subForms.put(Collections.singletonList(label), timeForm);

        } catch (NullPointerException e) {

        } catch (Exception e) {
            Log.write(e);
        }
         */
    }

    public void init(String str) {
        try {
            super.init(str);

        } catch (NullPointerException e) {

        } catch (Exception e) {
            Log.write(e);
        }
    }

    public Form getForm() {
        return this;
    }

    public List<SubForm> getSubForms() {
        return subForms;
    }

    public String getName() {
        return name;
    }

    public RaceProgramHorse getRaceProgramHorse() {
        return raceProgramHorse;
    }
}


