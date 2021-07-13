package hippos.database;

import hippos.utils.StringUtils;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: marktolo
 * Date: 28.11.2013
 * Time: 3:46
 * To change this template use File | Settings | File Templates.
 */
public class RaceResults {
    Connection conn = null;

    public RaceResults() {
        if(conn == null) {
            try {
                conn = Database.getConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public RaceResultData selectRaceHorseResultData(String horseName) {
        //Connection conn = null;
        PreparedStatement statement = null;
        ResultSet set = null;
        RaceResultData raceResultData = new RaceResultData();

        StringBuffer sb = new StringBuffer();
        try {
            //conn = Database.getConnection();                                                 // ä = õ

            horseName = horseName.replaceAll("'", "''");
            sb.append("select * from HORSERESULT where name='");
            sb.append(horseName);
            sb.append("' order by PVM desc");

            statement = conn.prepareStatement(sb.toString());
            set = statement.executeQuery();

            while(set.next()) {
                StringBuffer sbset = new StringBuffer();
                Date date = set.getDate("PVM");
                String name = set.getString("NAME");
                String driver = set.getString("DRIVER");
                BigDecimal length = set.getBigDecimal("LENGTH");
                String trackrow = set.getString("TRACKROW");
                BigDecimal splitTime = set.getBigDecimal("VA_0");
                BigDecimal time = set.getBigDecimal("TIME");
                String racetype = set.getString("TIMETYPE");
                String x = set.getString("X");
                BigDecimal rank = set.getBigDecimal("RANK");
                BigDecimal rate = set.getBigDecimal("RATE");
                BigDecimal timediff = set.getBigDecimal("TIMEDIFF");
                BigDecimal award = set.getBigDecimal("MONEY");

                sbset.append(StringUtils.toColumn(name, 20));
                sbset.append(StringUtils.toColumn(driver, 20));
                sbset.append(date + "  ");
                sbset.append(StringUtils.toColumn(racetype, 6));
                sbset.append(StringUtils.toColumn(time != null ? time.toString() : new String(), 6));
                sbset.append(StringUtils.toColumn(x, 4));
                sbset.append(StringUtils.toColumn(rank != null ? rank.toString() : new String(), 4));
                sbset.append(award);

                System.out.println(sbset.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(sb.toString());
        } finally {
            try { set.close(); } catch (Exception e) { }
            try { statement.close();} catch (Exception e) { }
            //try { conn.close(); } catch (Exception e) { }
        }

        return null;
    }

    private List selectHorseNameList() {
        //Connection conn = null;
        PreparedStatement statement = null;
        ResultSet set = null;
        List nameList = new ArrayList();

        try {
            //conn = Database.getConnection();
            statement = conn.prepareStatement("select NAME from HORSERESULT group by NAME");
            set = statement.executeQuery();

            while(set.next()) {
                String name = set.getString("NAME");
                System.out.println(name);
                nameList.add(name);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { set.close(); } catch (Exception e) { }
            try { statement.close();} catch (Exception e) { }
            //try { conn.close(); } catch (Exception e) { }
        }

        return nameList;
    }

    public Map selectDriverClasses() {
        //Connection conn = null;
        Map driverClasses = new HashMap();
        PreparedStatement statement = null;
        ResultSet set = null;
        List nameList = new ArrayList();

        try {
            //conn = Database.getConnection();
            statement = conn.prepareStatement("select DRIVER, count(*), sum(MONEY), avg(MONEY), count(x)/count(*) from HORSERESULT group by DRIVER order by avg(MONEY) desc");
            set = statement.executeQuery();

            while(set.next()) {
                String driver = set.getString(1);
                DriverClass driverClass = new DriverClass(driver);

                driverClass.setRaceCount(set.getBigDecimal(2).setScale(0, BigDecimal.ROUND_HALF_UP));
                driverClass.setPriseSum(set.getBigDecimal(3).setScale(2, BigDecimal.ROUND_HALF_UP));
                driverClass.setDriverClass(set.getBigDecimal(4).setScale(2, BigDecimal.ROUND_HALF_UP));
                driverClass.setAvgXCount(set.getBigDecimal(5).setScale(4, BigDecimal.ROUND_HALF_UP));

                driverClasses.put(driver, driverClass);
                System.out.println(driverClass);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { set.close(); } catch (Exception e) { }
            try { statement.close();} catch (Exception e) { }
            //try { conn.close(); } catch (Exception e) { }
        }

        return driverClasses;
    }

    public Map selectLocalDriverClasses(String locality) {
        //Connection conn = null;
        Map driverClasses = new HashMap();
        PreparedStatement statement = null;
        ResultSet set = null;
        List nameList = new ArrayList();

        try {
            //conn = Database.getConnection();
            StringBuffer sb = new StringBuffer();
            sb.append("select h.kuljettaja, count(*), sum(money), avg(hr.money), count(hr.x)/count(*) ");
            sb.append("from horseresult hr, horse h, programfile p, lahto l ");
            sb.append("where h.id = hr.id and h.lid = l.id and l.filename = p.filename and p.locality='" + locality + "' ");
            sb.append("group by h.kuljettaja order by avg(hr.money) desc");

            statement = conn.prepareStatement(sb.toString());
            set = statement.executeQuery();

            while(set.next()) {
                String driver = set.getString(1);
                DriverClass driverClass = new DriverClass(driver);

                driverClass.setRaceCount(set.getBigDecimal(2).setScale(0, BigDecimal.ROUND_HALF_UP));
                driverClass.setPriseSum(set.getBigDecimal(3).setScale(2, BigDecimal.ROUND_HALF_UP));
                driverClass.setDriverClass(set.getBigDecimal(4).setScale(2, BigDecimal.ROUND_HALF_UP));
                driverClass.setAvgXCount(set.getBigDecimal(5).setScale(4, BigDecimal.ROUND_HALF_UP));

                driverClasses.put(driver, driverClass);
                System.out.println(driverClass);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { set.close(); } catch (Exception e) { }
            try { statement.close();} catch (Exception e) { }
            //try { conn.close(); } catch (Exception e) { }
        }

        return driverClasses;
    }

    public static void main( String args [] ) {
        RaceResults raceResults = new RaceResults();

        //Map driverClasses = raceResults.selectDriverClasses();
        Map localDriverClasses = raceResults.selectLocalDriverClasses("Oulu");
        List horseNames = raceResults.selectHorseNameList();
        Iterator namesItr = horseNames.iterator();

        while (namesItr.hasNext()) {
            //raceResults.selectRaceHorseResultData((String)namesItr.next());
        }
    }

    private class DriverClass {
        private String driverName;
        private BigDecimal driverClass;
        private BigDecimal raceCount;
        private BigDecimal priseSum;
        private BigDecimal avgXCount;

        public DriverClass(String driverName) {
            this.driverName = driverName;
        }

        public String getDriverName() {
            return driverName;
        }

        public BigDecimal getRaceCount() {
            return raceCount;
        }

        public void setRaceCount(BigDecimal raceCount) {
            this.raceCount = raceCount;
        }

        public BigDecimal getPriseSum() {
            return priseSum;
        }

        public void setPriseSum(BigDecimal priseSum) {
            this.priseSum = priseSum;
        }

        public BigDecimal getDriverClass() {
            return driverClass;
        }

        public void setDriverClass(BigDecimal driverClass) {
            this.driverClass = driverClass;
        }

        public BigDecimal getAvgXCount() {
            return avgXCount;
        }

        public void setAvgXCount(BigDecimal avgXCount) {
            this.avgXCount = avgXCount;
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append(StringUtils.toColumn(this.driverName, 20));
            sb.append(StringUtils.toColumn(this.driverClass != null ? this.driverClass.toString() : new String(), 15));
            sb.append(StringUtils.toColumn(this.priseSum != null ? this.priseSum.toString() : new String(), 15));
            sb.append(StringUtils.toColumn(this.raceCount != null ? this.raceCount.toString() : new String(), 10));
            sb.append(StringUtils.toColumn(this.avgXCount != null ? this.avgXCount.toString() : new String(), 5));

            return sb.toString();
        }
    }
}
