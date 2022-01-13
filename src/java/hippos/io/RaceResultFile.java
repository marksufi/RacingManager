package hippos.io;

import hippos.math.AlphaNumber;
import hippos.RaceProgramStart;
import hippos.RaceResultStart;
import hippos.database.Database;
import hippos.exception.RacesCancelledException;
import utils.DatabaseException;
import utils.Log;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class RaceResultFile extends RaceFile implements FileParser {
    private String longLocality;
    List pelivaihdot = new ArrayList();
    private String trackCondition;

    public RaceResultFile(File file) {
        super(file.getAbsolutePath());
    }

    public RaceResultFile(String path, String filename) {
        super(path, filename);
    }

    public RaceResultFile(String path, URL url) {
        super(path, readUrlFilename(url));
    }

    public String getId() {
        return getName();
    }

    private TreeMap<BigDecimal, RaceResultStart> raceResultStarts = new TreeMap<BigDecimal, RaceResultStart>();

    /**
     * Generates a filename from the Result URL
     *
     * @param url
     *      https://heppa.hippos.fi/heppa/app?page=racing%2FRaceProgramMain&service=external&sp=CF1373230800000&sp=CEKO
     * @return
     *      CF1373230800000_CEKO.php
     */
    public static String readUrlFilename(URL url) {
        //System.out.println("RaceFile.readUrlFilename(" + url + ")");
        String file = url.getFile();
        String filename = file.substring(file.indexOf("sp=")+3);
        filename =filename.replaceAll("&sp=", "_") + ".php";
        return filename;
    }

    public void deleteData() throws DatabaseException {
        Connection conn = null;
        PreparedStatement statement = null;
        try {
            String filename = getName();
            conn = Database.getConnection();

            statement = conn.prepareStatement("DELETE FROM RESULTFILE WHERE FILENAME LIKE ?");
            statement.setString(1, filename);
            statement.executeUpdate();

            Log.write("DELETED: " + filename);
        } catch (Exception e) {
            throw new DatabaseException(e);
        } finally {
            try { statement.close(); } catch (Exception e) {}
            try { conn.commit(); } catch (Exception e) {}
            try { conn.close(); } catch (Exception e) {}
        }
    }

    public boolean existsInDatabase(Connection conn) {
        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = conn.prepareStatement("select FILENAME from RESULTFILE where FILENAME=?");
            statement.setString(1, getName());
            set = statement.executeQuery();
            if(set.next()) {
                return true;
            }
        } catch (SQLException e) {
            Log.write(e);
        } finally {
            try { set.close(); } catch (Exception e) { }
            try { statement.close();} catch (Exception e) { }
        }
        return false;
    }

    public Date parseDate() throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String filename = getName();
        String dateStr = filename.substring(filename.length() - 12, filename.length() - 4);
        return df.parse(dateStr.toString());
    }

    public void insert(Connection conn) {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("insert into RESULTFILE(FILENAME, LOCALITY, PVM, TRACKCONDITION) values(?, ?, ?, ?)");
            stmt.setString(1, getName());
            stmt.setString(2, longLocality);
            stmt.setDate(3, new java.sql.Date(getDate().getTime()));
            stmt.setString(4, trackCondition);

            for(RaceResultStart raceResultStart : raceResultStarts.values()) {
                try {
                    raceResultStart.insert(conn);
                } catch (Exception e) {
                    Log.write(e, getName());
                    e.printStackTrace();
                }
            }

            stmt.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            if(e.getErrorCode() != 1) {
                Log.write(e);
                try{conn.rollback();} catch(Exception re) {Log.write(e);}
            }

        } catch (Exception e) {
            Log.write( e, toString());
            try{conn.rollback();} catch(Exception re) {Log.write(e);}
        } finally {
            try{stmt.close();} catch(Exception e) {}
        }
    }

    /*
    public void update(Connection conn) {
        //Connection conn = null;
        PreparedStatement stmt = null;
        try {
            //conn = Database.getConnection();
            stmt = conn.prepareStatement("update RESULTFILE set LOCALITY=?, PVM=?, TRACKCONDITION=? where FILENAME=?");
            stmt.setString(1, longLocality);
            //stmt.setDate(3, new java.sql.Date(parseDate().getRaceResultTime()));
            stmt.setDate(2, new java.sql.Date(getDate().getTime()));
            stmt.setString(3, trackCondition);
            stmt.setString(4, getName());

            stmt.executeUpdate();

            Iterator itr = starts.iterator();
            while(itr.hasNext()) {
                RaceResultStart raceResultStart = (RaceResultStart)itr.next();
                raceResultStart.update(conn);
            }
            conn.commit();
        } catch (Exception e) {
            Log.write( e, toString());
            try{conn.rollback();} catch(Exception re) {}
        } finally {
            try{stmt.close();} catch(Exception e) {}
        }
    }*/

    public Object parse() throws RacesCancelledException {
        try {
            return new RaceResultFileParser(this).parse();
        } catch (RacesCancelledException e) {
            throw e;
        } catch (Exception e) {
            Log.write(e);
        }
        return null;
    }

    public void setTrackCondition(String trackCondition) {
        this.trackCondition = trackCondition;
        //Log.write("TrackCondition: " + trackCondition);
    }

    public void addPelimuoto(String pelimuoto, String vaihto, String kerroin) {
        pelivaihdot.add(new Pelimuoto(pelimuoto, vaihto, kerroin));
    }

    /*
    public void updateProgramFiles(Connection conn) {
        PreparedStatement stmt = null;
        try {
            String likeStmt = getName().substring(0, getName().indexOf(".")) + "_%";
            stmt = conn.prepareStatement("update PROGRAMFILE set RESULTFILE=? where FILENAME like ?");
            stmt.setString(1, getName());
            stmt.setString(2, likeStmt);
            stmt.executeUpdate();

            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try{stmt.close();} catch(Exception e) {}
        }
    }*/

    public void add(RaceResultStart raceResultStart) {
        raceResultStarts.put(raceResultStart.getStartNumber(), raceResultStart);
    }


    public RaceResultStart getRaceResultStart(RaceProgramStart raceProgramStart) {
        return raceResultStarts.get(raceProgramStart.getStartNumber());
    }

    private class Pelimuoto {
        String pelimuoto;
        AlphaNumber vaihto;
        AlphaNumber kerroin;

        public Pelimuoto(String pelimuoto, String vaihto, String kerroin) {
            this.pelimuoto = pelimuoto;
            this.vaihto = new AlphaNumber(vaihto);
            this.kerroin = new AlphaNumber(kerroin);
        }


        public String toString() {
            return pelimuoto + ":\t vaihto: " + vaihto + "\t kerroin: " + kerroin;
        }
    }

    public void setLongLocality(String longLocality) {
        this.longLocality = longLocality;
    }
}
