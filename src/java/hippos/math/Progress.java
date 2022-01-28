package hippos.math;

import hippos.database.Database;
import hippos.util.Mapper;
import hippos.utils.DateUtils;
import utils.Log;

import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class Progress {
    private Value awards = new Value();
    private Value final_awards = new Value();

    public void add(BigDecimal award, BigDecimal final_award) {
        //System.out.println("ProgressReg.add(" + award + ", " + final_award + ")");

        try {
            awards.add(award);
            final_awards.add(final_award);
        } catch (NullPointerException e) {
            // Jompikumpi syöttöarvoista on null
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BigDecimal get(BigDecimal award) {
        try {
            //System.out.print("ProgressReg.get(" + award + ")");

            BigDecimal m = final_awards.divide(awards);

            BigDecimal final_award = award.multiply(m);

            //System.out.println(" => " + final_award);

            return final_award;

        } catch (Exception e) {
            throw e;
        }
    }

    public String toString() {
        return awards.toString() + " => " + final_awards.toString();
    }

    public static void main(String [] args) {

        try {
            Mapper<Progress> progressMap = new Mapper<>();

            Connection conn = Database.getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                        "SELECT nimi, " +
                            "pvm, " +
                            "palkinto, " +
                            "LEAD(pvm, 1, null) OVER (PARTITION BY nimi ORDER BY nimi, pvm) next_1_pvm, " +
                            "LEAD(palkinto, 1, null) OVER (PARTITION BY nimi ORDER BY nimi, pvm) next_1_palkinto, " +
                            "LEAD(pvm, 2, null) OVER (PARTITION BY nimi ORDER BY nimi, pvm) next_2_pvm, " +
                            "LEAD(palkinto, 2, null) OVER (PARTITION BY nimi ORDER BY nimi, pvm) next_2_palkinto " +
                            "FROM subresult");

            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                List keyList = new ArrayList();

                String nimi = rs.getString(1);
                Date pvm = rs.getDate(2);
                BigDecimal palkinto = rs.getBigDecimal(3);
                Date next_1_pvm = rs.getDate(4);
                BigDecimal next_1_palkinto = rs.getBigDecimal(5);
                Date next_2_pvm = rs.getDate(6);
                BigDecimal next_2_palkinto = rs.getBigDecimal(7);

                StringBuffer sb = new StringBuffer();
                sb.append(nimi);
                sb.append(", " + pvm);
                sb.append(", " + palkinto);
                sb.append(", " + next_1_pvm);
                sb.append(", " + next_1_palkinto);
                sb.append(", " + next_2_pvm);
                sb.append(", " + next_2_palkinto);

                System.out.println(sb.toString());

                try {
                    double dd = hippos.utils.DateUtils.getDayDiff(next_1_pvm, pvm);

                    BigDecimal weeks = getWeeksKey(dd);

                    keyList.add(weeks);

                    progressMap.getOrCreate(keyList, new Progress()).add(palkinto, next_1_palkinto);
                    System.out.println(keyList + " => " + progressMap.get(keyList));
                    System.out.println(progressMap.get(keyList).get(palkinto));

                    dd = hippos.utils.DateUtils.getDayDiff(next_2_pvm, next_1_pvm);
                    weeks = getWeeksKey(dd);
                    keyList.add(weeks);

                    progressMap.getOrCreate(keyList, new Progress()).add(palkinto, next_2_palkinto);
                    System.out.println(keyList + " => " + progressMap.get(keyList));
                    progressMap.get(keyList).get(palkinto);

                } catch (ArithmeticException e) {
                    // next_pvm null
                    //e.printStackTrace();
                } catch (NullPointerException e) {
                    // jompikumpi palkinnoista null
                    //e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            System.out.println(progressMap);

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static BigDecimal getWeeksKey(double dd) {
        BigDecimal weeks = BigDecimal.valueOf((int) (dd / 30));

        if (weeks.intValue() > 3) {
            weeks = BigDecimal.valueOf(3);
        }

        return weeks;
    }

    public static BigDecimal getWeeksKey(BigDecimal dateDiff, BigDecimal aDateDiff) {
        try {
            double dd = aDateDiff.subtract(dateDiff).doubleValue();

            if(dd < 0)
                Log.write("Progress.getWeeksKey( " + dateDiff + ", " + aDateDiff + ") - Päivien erotus negatiivinen, pitäis olla posotiivinen");

            return getWeeksKey(dd);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static BigDecimal getWeeksKey(Date date, Date aDate) {
        try {
            double dd = DateUtils.getDayDiff(aDate, date);

            if(dd < 0)
                Log.write("Progress.getWeeksKey( " + date + ", " + aDate + ") - Päivien erotus negatiivinen, pitäis olla posotiivinen");

            return getWeeksKey(dd);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


}
