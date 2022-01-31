package hippos;

import hippos.database.Database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Mar 9, 2006
 * Time: 10:46:35 AM
 * To change this template use Options | File Templates.
 */
public class Locality {
    private String shortLocality;
    private String longLocality;
    private Set trackRowSet = new TreeSet<TrackRowStat>();
    private Date date;

    public Locality(String shortLocality) {
        this.shortLocality = shortLocality;
    }

    /**
     * Lataa tietokannasta paikkakuntakohtaiset ohjastajatiedot. Näitä ovat:
     * - Kuljettajan nimi
     * - Ajettujen lähtöjen määrä
     * - Kokonaispalkintosumma
     * - Keskimääräinen palkintosumma lähtöä kohden
     * - Laukkaprosentti
     *
     * @param place Rata, jolle tiedot ladataan
     */
    public Map loadDriverStats(String place) {
        Map driverStatMap = new Hashtable();
        StringBuffer sb = new StringBuffer();

        sb.append("select h.kuljettaja, count(*), sum(prize), avg(hr.prize), count(hr.x)/count(*) x_pros ");
        sb.append("from horseresult hr, horse h, programfile p, lahto l ");
        sb.append("where h.id = hr.id and h.lid = l.id and l.filename = p.filename and p.locality='?' ");
        sb.append("group by h.kuljettaja");

        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet set = null;

            try {
                conn = Database.getConnection();
                statement = conn.prepareStatement(sb.toString());
                statement.setString(1, place);
                set = statement.executeQuery();

                while (set.next()) {
                    String driver = set.getString(1);
                    BigDecimal count = set.getBigDecimal(2);
                    BigDecimal awardsum = set.getBigDecimal(3);
                    BigDecimal avgAwardsum = set.getBigDecimal(4);
                    BigDecimal avgXcount = set.getBigDecimal(5);

                    //driverStatsMap.put(null, null);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try { set.close(); } catch (Exception e) { }
                try { statement.close();} catch (Exception e) { }
                try { conn.close(); } catch (Exception e) { }
            }

        return driverStatMap;
    }

    public void loadTrackRowStats(Connection conn) {
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {

            StringBuffer sb = new StringBuffer();

            sb.append("select rata, count(*), avg(s_1), avg(palkinto) ");
            sb.append("from subresult ");
            sb.append("where paikka=? and lahtotyyppi like '%ake' and rata is not null ");
            sb.append("group by rata");

            statement = conn.prepareStatement(sb.toString());
            statement.setString(1, this.shortLocality);

            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                TrackRowStat trackRowStat = new TrackRowStat(resultSet, conn);
                trackRowSet.add(trackRowStat);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { resultSet.close(); } catch (SQLException e) { }
            try { statement.close(); } catch (SQLException e) { }
        }
    }
    public String getShortLocality() {
        return shortLocality.toUpperCase();
    }

    public void setShortLocality(String shortLocality) {
        this.shortLocality = shortLocality;
    }

    public String getLongLocality() {
        return longLocality;
    }

    public void setLongLocality(String longLocality) {
        this.longLocality = longLocality;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("***************************************************************************************************");
        sb.append("\n\n" + longLocality);
        sb.append("\t" + date);
        sb.append("\n\nTrackRowStats:");

        Iterator itr = trackRowSet.iterator();
        while(itr.hasNext()) {
            sb.append("\n" + itr.next());
        }
        sb.append("\n***************************************************************************************************");

        return sb.toString();
    }

    private class TrackRowStat implements Comparable {
        //private String startRow;
        private String startNumber;
        private BigDecimal count;
        private BigDecimal winPro;
        private BigDecimal avgPrize;
        private TrackRowStat allTrackRowStats;

        public TrackRowStat(ResultSet resultSet, Connection conn) throws SQLException {
            //startRow = resultSet.getString(1);
            startNumber= resultSet.getString(1);
            count = resultSet.getBigDecimal(2);
            winPro = resultSet.getBigDecimal(3);
            avgPrize = resultSet.getBigDecimal(4);

            allTrackRowStats = new TrackRowStat(startNumber, conn);

            if(this.startNumber.length() < 2)
                this.startNumber = " " + this.startNumber;

            if(winPro != null)
                winPro = winPro.multiply(BigDecimal.valueOf(100.0)).setScale(1, BigDecimal.ROUND_HALF_UP);

            if(avgPrize != null)
                avgPrize = avgPrize.setScale(0, BigDecimal.ROUND_HALF_UP);

        }

        public TrackRowStat(String startNumber, Connection conn) {
            PreparedStatement statement = null;
            ResultSet resultSet = null;

            try {
                StringBuffer sb = new StringBuffer();

                sb.append("select count(*), avg(s_1), avg(palkinto) ");
                sb.append("from subresult ");
                sb.append("where lahtotyyppi like '%ake' and rata=?");

                statement = conn.prepareStatement(sb.toString());
                statement.setString(1, startNumber);

                resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    this.startNumber= startNumber;
                    count = resultSet.getBigDecimal(1);
                    winPro = resultSet.getBigDecimal(2);
                    avgPrize = resultSet.getBigDecimal(3);
                }

                if(this.startNumber.length() < 2)
                    this.startNumber = " " + this.startNumber;

                if(winPro != null)
                    winPro = winPro.multiply(BigDecimal.valueOf(100.0)).setScale(1, BigDecimal.ROUND_HALF_UP);

                if(avgPrize != null)
                    avgPrize = avgPrize.setScale(0, BigDecimal.ROUND_HALF_UP);

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try { resultSet.close(); } catch (SQLException e) { }
                try { statement.close(); } catch (SQLException e) { }
            }
        }

        @Override
        public int compareTo(Object o) {
            TrackRowStat aTrackRowStat = (TrackRowStat) o;

            return startNumber.compareTo(aTrackRowStat.startNumber);
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();

            sb.append(startNumber);
            sb.append("\t" + winPro + " %");
            if(allTrackRowStats != null) {
                sb.append("\t(" + winPro.subtract(allTrackRowStats.winPro) + " %)");
            }
            //sb.append("\t" + avgPrize + " €/s");

            return sb.toString();
        }
    }
}


