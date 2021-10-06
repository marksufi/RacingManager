package hippos.database;

import hippos.RaceProgramHorse;
import utils.Log;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;

public class Statements {

    public static PreparedStatement getSubStartsStatement(Connection conn, RaceProgramHorse raceProgramHorse, int rowCount) {

        PreparedStatement statement = null;
        Date endDate = new Date(raceProgramHorse.getRaceProgramStart().getDate().getTime());
        String name = raceProgramHorse.getRaceHorseName();
        String horseRace = raceProgramHorse.getRaceMode().substring(0, 1);

        try {
            StringBuilder stmt = new StringBuilder();

            if(!Database.useMySql) {
                /* Oracle */
                stmt.append("select * from (");
            }

            stmt.append("select r.RATA_TUNNISTE, s.WEATHER, s.KULJETTAJA, s.VALMENTAJA, s.PAIKKA, s.PVM, s.LAHTONUMERO, s.MATKA, s.RATA, s.TYYPPI, s.AIKA, s.LAHTOTYYPPI, s.SIJOITUS, s.XCODE, s.X, s.KERROIN, s.PALKINTO, s.KCODE ");
            stmt.append("from RESULTHORSE r right outer join SUBRESULT s ");
            stmt.append("on r.NIMI=s.NIMI and r.LAJI=s.LAJI and r.PVM=s.PVM and r.LAHTONUMERO=s.LAHTONUMERO ");
            stmt.append("where s.NIMI=? and s.LAJI=? and s.PVM < ? order by s.PVM desc");

            if(Database.useMySql) {
                stmt.append(" LIMIT ?");
            } else {
                /* Oracle */
                stmt.append(") where ROWNUM <= ?");
            }



            statement = conn.prepareStatement(stmt.toString());

            statement.setString(1, name);
            statement.setString(2, horseRace);
            statement.setDate(3, endDate);
            statement.setInt(4, rowCount);

            //if(startDate != null)
            //    statement.setDate(4, startDate);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return statement;

    }

    public static PreparedStatement getTimeFormStatement(Connection conn, String name, String race, Date endDate) {
        return getTimeFormStatement(conn, name, race, null, endDate);
    }

    public static PreparedStatement getTimeFormStatement(Connection conn, String name, String race, Date startDate, Date endDate) {
        PreparedStatement statement = null;

        try {
            StringBuilder stmt = new StringBuilder();
            stmt.append("select count(*), sum(S_1), sum(S_2), sum(S_3), sum(palkinto), sum(KCODE), sum(X), min(AIKA) Aika, LAHTOTYYPPI, MAX( PALKINTO ) KEEP ( DENSE_RANK LAST ORDER BY PVM ) AS VPALKINTO ");
            stmt.append("from SUBRESULT ");
            stmt.append("where NIMI=? and laji=? and PVM < ? ");
            if(startDate != null) {
                stmt.append("and PVM > ? ");
            }
            //stmt.append("and KERROIN is not null");
            //stmt.append("group by TYYPPI, LAHTOTYYPPI");
            stmt.append("group by LAHTOTYYPPI, KCODE, X");

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

    public static PreparedStatement getInsertRaceProgramHorseStamement(Connection conn) {
        try {
            StringBuilder sb = new StringBuilder();

            sb.append("insert into PROGRAMHORSE(");
            sb.append("LID, TRACKID, NUMERO, NIMI, KULJETTAJA, VALMENTAJA, ");
            sb.append("MATKA, RATA, TASOITUS, ");
            sb.append("K_S, K_1, K_2, K_3, K_R, K_PAALU, K_X, ");
            sb.append("SUBSTART_1, ");
            sb.append("SUBSTART_2, ");
            sb.append("SUBSTART_3, ");
            sb.append("SUBSTART_4, ");
            sb.append("SUBSTART_5, ");
            sb.append("SUBSTART_6, ");
            sb.append("SUBSTART_7, ");
            sb.append("SUBSTART_8) ");

            sb.append("values(?, ?, ?, ?, ?, ?, ");
            sb.append("?, ?, ?, ");
            sb.append("?, ?, ?, ?, ?, ?, ?, ");
            sb.append("?, ?, ?, ?, ?, ?, ?, ?)");

            return conn.prepareStatement(sb.toString());

        } catch (Exception e) {
            Log.write(e);
            return null;

        }
    }

    public static PreparedStatement getProgramHorsesStatement(Connection conn, String lid) {
        PreparedStatement statement = null;

        try {
            StringBuffer sb = new StringBuffer();

            sb.append("select TRACKID, NUMERO, NIMI, KULJETTAJA, VALMENTAJA, ");
            sb.append("MATKA, RATA, TASOITUS, ");
            sb.append("K_S, K_1, K_2, K_3, K_R, K_PAALU, K_X, ");
            sb.append("SUBSTART_1, SUBSTART_2, SUBSTART_3, SUBSTART_4, ");
            sb.append("SUBSTART_5, SUBSTART_6, SUBSTART_7, SUBSTART_8 ");
            sb.append("from PROGRAMHORSE ");
            sb.append("where LID = ? ");
            sb.append("order by NUMERO");

            statement = conn.prepareStatement(sb.toString());

            statement.setString(1, lid);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return statement;
    }

}
