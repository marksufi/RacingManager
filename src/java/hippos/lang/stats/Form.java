package hippos.lang.stats;

import hippos.SubStart;
import hippos.math.AlphaNumber;
import org.apache.commons.math3.dfp.DfpField;
import utils.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Nov 25, 2004
 * AlphaNumber: 5:37:11 PM
 * To change this template use Options | File Templates.
 */
public class Form {
    private String label;
    private BigDecimal starts = BigDecimal.ZERO;
    private BigDecimal firsts = BigDecimal.ZERO;
    private BigDecimal seconds = BigDecimal.ZERO;
    private BigDecimal thirds = BigDecimal.ZERO;
    private BigDecimal awards = BigDecimal.ZERO;
    private BigDecimal kcode = BigDecimal.ZERO;
    private BigDecimal xcode = BigDecimal.ZERO;

    private BigDecimal probability = null;

    String raceLiteral = null;
    FormValidation formValidation = null;

    public Form(String label) {
        this(label, null);
    }

    public Form(String label, FormValidation formValidation) {
        this.label = label;
        this.formValidation = formValidation;
    }

    public Form(ResultSet set) throws SQLException {
        try {
            int i = 1;
            setStarts(set.getBigDecimal(i++));
            setFirsts(set.getBigDecimal(i++));
            setSeconds(set.getBigDecimal(i++));
            setThirds(set.getBigDecimal(i++));
            setAwards(set.getBigDecimal(i++));
            setKcode(set.getBigDecimal(i++));
            setXcode(set.getBigDecimal(i++));
        } catch (Exception e) {
            Log.write(e);
        }
    }

    /*
    public Form(String raceLiteral) {
        this();
        this.raceLiteral = raceLiteral;
    }

    public String getRaceLiteral() {
        return raceLiteral;
    }*/

    public BigDecimal firstRate() {
        if(starts != null && starts.intValue() != 0) {
            return firsts.divide(starts, 2, RoundingMode.HALF_UP);
        }
        return new BigDecimal(0);
    }

    public BigDecimal secondRate() {
        if(starts != null && starts.intValue() != 0) {
            return seconds.divide(starts, 2, RoundingMode.HALF_UP);
        }
        return new BigDecimal(0);
    }

    public BigDecimal thirdRate() {
        if(starts != null && starts.intValue() != 0) {
            return thirds.divide(starts, 2, BigDecimal.ROUND_HALF_UP);
        }
        return new BigDecimal(0);
    }

    public BigDecimal awardRate() {
        BigDecimal awardRate = BigDecimal.ZERO;
        if(awards != null && starts != null && starts.intValue() != 0) {
            awardRate = awards.divide(starts, 0, RoundingMode.HALF_UP);
        }
        return awardRate;
    }

    public BigDecimal awardRate(BigDecimal ifZero) {
        if(starts.intValue() > 0) {
            BigDecimal awardRate = BigDecimal.ZERO;
            if (awards != null && starts != null && starts.intValue() != 0) {
                awardRate = awards.divide(starts, 0, RoundingMode.HALF_UP);
            }
            return awardRate;
        }
        return ifZero;
    }

    public BigDecimal sijaRate() {
        BigDecimal sijat = new BigDecimal(0);
        sijat = sijat.add(firsts);
        sijat = sijat.add(seconds);
        sijat = sijat.add(thirds);
        BigDecimal erotus = starts.subtract(sijat);
        return erotus;
    }

    public void add(Form hv) {
        try { starts = starts.add(hv.starts); } catch (NullPointerException e) {}
        try { firsts = firsts.add(hv.firsts); } catch (NullPointerException e) {}
        try { seconds = seconds.add(hv.seconds); } catch (NullPointerException e) {}
        try { thirds = thirds.add(hv.thirds); } catch (NullPointerException e) {}
        try { awards = awards.add(hv.awards); } catch (NullPointerException e) {}
        try { kcode = kcode.add(hv.kcode); } catch (NullPointerException e) {}
        try { xcode = xcode.add(hv.xcode); } catch (NullPointerException e) {}
    }

    public BigDecimal getRankRate() {
        BigDecimal rankRate = firsts.add(seconds.multiply(BigDecimal.valueOf(0.50))).add(thirds.multiply(BigDecimal.valueOf(0.33)));

        rankRate = starts.intValue() > 0 ? rankRate.divide(starts, 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;

        return rankRate;
    }

    public BigDecimal getRankRate(BigDecimal ifZero) {
        if(starts.intValue() > 0) {
            BigDecimal rankRate = firsts;
            rankRate = rankRate.add(seconds.multiply(BigDecimal.valueOf(0.50)));
            rankRate = rankRate.add(thirds.multiply(BigDecimal.valueOf(0.33)));

            rankRate = rankRate.multiply(BigDecimal.valueOf(100.00));
            rankRate = rankRate.divide(starts, 2, RoundingMode.HALF_UP);

            //rankRate.setScale(2, RoundingMode.HALF_UP);

            return rankRate;

        }

        return ifZero;
    }

    public String getLabel() {
        return label;
    }

    public BigDecimal getStarts() {
        return starts;
    }

    public BigDecimal getFirsts() {
        return firsts;
    }

    public BigDecimal getSeconds() {
        return seconds;
    }

    public BigDecimal getThirds() {
        return thirds;
    }

    public BigDecimal getAwards() {
        return (awards);
    }

    public BigDecimal getAwardRate() {
        return (starts.intValue() > 0 ? awards.divide(starts, 0, RoundingMode.HALF_UP) : BigDecimal.ZERO);
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setStarts(BigDecimal starts) {
        if(starts != null)
            this.starts = starts;
    }

    public void setFirsts(BigDecimal firsts) {
        if(firsts != null)
            this.firsts = firsts;
    }

    public void setSeconds(BigDecimal seconds) {
        if(seconds != null)
            this.seconds = seconds;
    }

    public void setThirds(BigDecimal thirds) {
        if(thirds != null)
            this.thirds = thirds;
    }

    public void setAwards(BigDecimal awards) {
        if(awards != null)
            this.awards = awards;

        //if(awards != null && awards.getNumber() != null)
        //    this.awards = awards.getNumber();
    }

    public void add(SubStart subStart) {

        if(formValidation == null || formValidation.validate(subStart)) {
            BigDecimal raceResultPrize = subStart.getAward();
            BigDecimal raceRanking = subStart.getSubRank().getNumber();
            BigDecimal raceResultWinOdds = subStart.getRating();
            String gallop = subStart.getxCode();

            this.starts = this.starts.add(BigDecimal.ONE);
            if(raceRanking != null) {
                switch(raceRanking.intValue()) {
                    case 1: firsts = firsts.add(BigDecimal.ONE);
                            break;
                    case 2: seconds = seconds.add(BigDecimal.ONE);
                            break;
                    case 3: thirds = thirds.add(BigDecimal.ONE);
                            break;
                }
            }
            if(raceResultPrize != null) {
                awards.add(raceResultPrize);
                //awards = awards.add(raceResultPrize);
                //prize = new AlphaNumber(awards.toString());
            }
        }
   }

    public FormValidation getFormValidation() {
        return formValidation;
    }

    /*
    public void setPrize(BigDecimal prize) {
        if(prize != null) {
            this.setAwards(prize);
        }

    }*/

    public BigDecimal getKcode() {
        return kcode;
    }

    public BigDecimal getKcodeProcents(BigDecimal ifZero) {
        try {
            BigDecimal kcoderate = this.kcode.multiply(BigDecimal.valueOf(100.00));
            kcoderate = kcoderate.divide(starts, 0, RoundingMode.HALF_UP);

            return kcoderate;
        } catch (ArithmeticException e) {
            // starts is zero
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ifZero;
    }

    public void setKcode(BigDecimal kcode) {
        if(kcode != null) {
            this.kcode = kcode;
        }
    }

    public BigDecimal getXcode() {
        return xcode;
    }

    public void setXcode(BigDecimal xcode) {
        if(xcode != null) {
            this.xcode = xcode;
        } else {
            System.out.println("");
        }
    }



    public void init(ResultSet set) throws SQLException {
        if(set.next()) {
            int i = 1;
            setStarts(set.getBigDecimal(i++));
            setFirsts(set.getBigDecimal(i++));
            setSeconds(set.getBigDecimal(i++));
            setThirds(set.getBigDecimal(i++));
            setAwards(set.getBigDecimal(i++));
            setKcode(set.getBigDecimal(i++));
            setXcode(set.getBigDecimal(i++));
        }
    }

    public String toString() {
        StringBuffer str = new StringBuffer();
        str.append(label + ": " + starts + " " + firsts + "-" + seconds + "-" + thirds );

        if(kcode != null) {
            str.append("->" + kcode + " ");
            str.append("(");
            str.append(probability != null ? probability : getKcodeProcents(BigDecimal.ZERO));
            str.append("%)");
        }


        str.append("-" + xcode + "x");
        str.append("(");

        try {
            // Laukkaprosentti
            str.append((xcode.divide(starts, 2, RoundingMode.HALF_UP)).multiply(BigDecimal.valueOf(100).setScale(0, RoundingMode.HALF_UP)) + "%");
        } catch (ArithmeticException e) { /* starts nolla */
        } catch (Exception e) { e.printStackTrace(); }

        str.append(")");


        if(awards != null) {
            str.append("  " + awards + "???");
            str.append(" (");
            str.append(starts.intValue() > 0 ? awards.divide(starts, 0, RoundingMode.HALF_UP) : "0");
            str.append("???/s)");
        }


        return str.toString();
    }

    public void setProbability(BigDecimal allStartCount) {
        try {
            probability = starts.divide(allStartCount, 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        } catch (ArithmeticException e) {
            //divide by zero
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BigDecimal getProbability() {
        return probability;
    }

    public static String getSelect() {
        return "select count(*), sum(S_1), sum(S_2), sum(S_3), sum(palkinto), sum(KCODE), count(XCODE)";
    }

    public static PreparedStatement getStatement(Connection conn, String name, String race, Date startDate, Date endDate) {
        PreparedStatement statement = null;

        try {
            StringBuffer stmt = new StringBuffer();
            stmt.append(getSelect());
            stmt.append(" from SUBRESULT ");
            stmt.append("where NIMI=? and laji=? and PVM < ? ");
            if(startDate != null) {
                stmt.append("and PVM > ? ");
            }
            //stmt.append("and KERROIN is not null");

            statement = conn.prepareStatement(stmt.toString());

            statement.setString(1, name);
            statement.setString(2, race);
            statement.setDate(3, endDate);

            if(startDate != null)
                statement.setDate(4, startDate);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return statement;
    }

}
