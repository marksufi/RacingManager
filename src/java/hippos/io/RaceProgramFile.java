package hippos.io;

import hippos.HarnessApp;
import hippos.RaceProgramStart;
import hippos.RaceResultStart;
import hippos.database.Database;
import hippos.math.betting.GameFactory;
import utils.Log;

import java.io.File;
import java.net.URL;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RaceProgramFile extends RaceFile {
    private RaceResultStart raceResultStart;
    private RaceProgramStart raceProgramStart;

    public RaceProgramFile(File file) {
        super(file.getAbsolutePath());
    }

    public RaceProgramFile(String path, String filename) {
        super(path, filename);
    }

    public RaceProgramFile(String path, URL url) {
        super(path, readUrlFilename(url));
    }

    public RaceResultStart getRaceResultStart() {
        return raceResultStart;
    }

    public void setRaceResultStart(RaceResultStart raceResultStart) {
        this.raceResultStart = raceResultStart;
    }

    /**
     * Generates a filename from the Race URL
     *
     * @param url
     *      https://heppa.hippos.fi/heppa/app;jsessionid=Ur8KJr-9HGDdvDgoGjyeww**.app2?page=racing%2FRaceProgramOneRace&service=external&sp=CF1357941600000&sp=CEH&sp=CC12
     * @return
     *      CF1357941600000_CEH_CC12.php
     */
    public static String readUrlFilename(URL url) {
        //System.out.println("RaceFile.readUrlFilename(" + url + ")");

        String file = url.getFile();
        String filename = file.substring(file.indexOf("sp=")+3);
        filename = filename.replaceAll("&sp=", "_");
        filename = filename + ".php";

        return filename;
    }

    /**
     * Adds the year into the programfilename
     *
     * @param filename
     *      to050809.php
     * @return
     *      to2005050809.php
     */
    public static String addYearToFilename(String filename) {
        Calendar today = Calendar.getInstance();
        String year = new Integer(today.get(Calendar.YEAR)).toString();
        StringBuffer newFilename = new StringBuffer();

        newFilename.append(filename.substring(0, filename.length() - 10));
        newFilename.append(year);
        newFilename.append(filename.substring(filename.length() - 10));

        return newFilename.toString();
    }

    public boolean existsInDatabase(Connection conn) {
        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = conn.prepareStatement("select FILENAME from PROGRAMFILE where FILENAME=?");
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
        String dateStr = filename.substring(filename.length() - 14, filename.length() - 6);
        return df.parse(dateStr.toString());
    }

    public void insert(Connection conn) {
        PreparedStatement stmt = null;
        try {
            if(!existsInDatabase(conn)) {
                stmt = conn.prepareStatement("insert into PROGRAMFILE(FILENAME, LOCALITY, PVM, RESULTFILE) values(?, ?, ?, ?)");
                stmt.setString(1, getName());
                stmt.setString(2, getLocality().getLongLocality());
                //stmt.setDate(3, new java.sql.Date(parseDate().getRaceResultTime()));
                stmt.setDate(3, new java.sql.Date(getDate().getTime()));
                stmt.setNull(4, Types.VARCHAR);
                stmt.executeUpdate();
            }
            if(!raceProgramStartExists(conn)) {
                if(raceProgramStart != null) {
                    raceProgramStart.insert(conn);
                } else {
                    Log.write("RaceProgramFile.insert: raceProgramStart missing");
                }
            }
            conn.commit();
       } catch (Exception e) {
            Log.write(e, getName());
        } finally {
            try{stmt.close();} catch(Exception e) {}
        }
    }

    public boolean raceProgramStartExists(Connection conn) {
        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = conn.prepareStatement("select id from LAHTO where filename=?");
            statement.setString(1, id);
            set = statement.executeQuery();
            if(set.next()) {
                return true;
            }
        } catch (Exception e) {
            Log.write(e);
        } finally {
            try { set.close(); } catch (Exception e) { }
            try { statement.close();} catch (Exception e) { }
        }
        return false;
    }


    /*
    public void delete(Connection conn) {
        PreparedStatement stmt = null;
        try {
            deleteStart(conn);
            stmt = conn.prepareStatement("delete from PROGRAMFILE where FILENAME like ?");
            stmt.setString(1, getName());
            stmt.executeUpdate();
            conn.commit();
        } catch (Exception e) {
            Log.write(e);
        } finally {
            try{stmt.close();} catch(Exception e) {}
        }
    }*/

    /*
    public boolean deleteFromDatabase() {
        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            String filename = getName();
            conn = Database.getConnection();
            deleteStart(conn);
            stmt = conn.prepareStatement("delete from PROGRAMFILE where FILENAME like ?");
            stmt.setString(1, filename);
            stmt.executeUpdate();
            conn.commit();

            return true;
        } catch (Exception e) {
            Log.write(e);
        } finally {
            try { stmt.close(); } catch(Exception e) { }
            try { conn.close(); } catch (Exception e) { }
        }
        return false;
    }*/

    /*
    private void deleteStarts(Connection conn) {
        PreparedStatement stmt = null;
        try {
            Iterator itr = starts.iterator();
            while(itr.hasNext()) {
                RaceStart raceProgramStart = (RaceStart)itr.next();
                raceProgramStart.deleteHorses(conn);
            }
            stmt = conn.prepareStatement("delete from LAHTO where FILENAME like ?");
            stmt.setString(1, getRaceHorseName());
            stmt.executeUpdate();
        } catch (SQLException e) {
            Log.write(e);
        } finally {
            try{stmt.close();} catch(Exception e) {}
        }
    } */

    /*
    private void deleteStart(Connection conn) {
        PreparedStatement stmt = null;
        try {
            if(raceProgramStart != null) {
                raceProgramStart.deleteHorses(conn);
                stmt = conn.prepareStatement("delete from LAHTO where FILENAME like ?");
                stmt.setString(1, getName());
                stmt.executeUpdate();
                conn.commit();
            }
        } catch (SQLException e) {
            Log.write(e);
        } finally {
            try{stmt.close();} catch(Exception e) {}
        }
    }*/

    /*
    private void submitStarts(Connection conn) throws SQLException {
        Iterator itr = starts.iterator();
        while(itr.hasNext()) {
            RaceStart raceProgramStart = (RaceStart)itr.next();
            if(raceProgramStart.getHorseRace() != null) {
                raceProgramStart.insert(conn);
            }
        }
    }*/


    /*
    public RaceProgramStart parse() throws Exception {

        raceProgramStart = (RaceProgramStart) new RaceProgramFileParser(this).parse();
        //return new RaceProgramFileParser(this).parse();
        return raceProgramStart;
    }*/

    public String toString() {
        StringBuffer str = new StringBuffer();
        str.append(getName());
        if(getLocality().getLongLocality() != null)
            str.append("\t" + getLocality().getLongLocality());
        if(this.getDate() != null)
            str.append("\t" + this.getDate());

        if(raceProgramStart != null) {
            str.append(raceProgramStart.toString());
        }
        return str.toString();
    }

    /**
     * Päivittää tietokantaan ohjelmatiedolle tulostiedoston, sen merkiksi että lähdön tulokset on parsittu
     *
     */
    public void update(RaceResultFile raceResultFile, RaceResultStart raceResultStart, GameFactory gameFactory, Connection conn) {

        //update(conn);
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("update PROGRAMFILE set RESULTFILE=? where FILENAME like ?");
            stmt.setString(1, raceResultFile.getName());
            stmt.setString(2, getName());
            stmt.executeUpdate();

            /*
            if(raceProgramStart != null) {
                raceProgramStart.update(conn, raceResultStart);
            }
             */

            conn.commit();
        } catch (Exception e) {
            try{conn.rollback();} catch(Exception re) {}
            e.printStackTrace();
        } finally {
            try{stmt.close();} catch(Exception e) {}
            //try{conn.close();} catch(Exception e) {}
        }
    }

    /**
     * Päivittää tietokantaan ohjelmatiedolle tulostiedoston, sen merkiksi että lähdön tulokset on parsittu
     *
     */
    public void update(RaceResultFile raceResultFile, Connection conn) {

        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("update PROGRAMFILE set RESULTFILE=? where FILENAME=?");
            stmt.setString(1, raceResultFile.getName());
            stmt.setString(2, getName());
            stmt.executeUpdate();

            conn.commit();
        } catch (Exception e) {
            try{conn.rollback();} catch(Exception re) {}
            e.printStackTrace();
        } finally {
            try{stmt.close();} catch(Exception e) {}
        }
    }

    public String getResultFileName(Connection conn) {
        PreparedStatement statement = null;
        ResultSet set = null;
        String resultFile = null;

        try {
            statement = conn.prepareStatement("select RESULTFILE from PROGRAMFILE where FILENAME like ?");
            statement.setString(1, getName());
            set = statement.executeQuery();
            if(set.next()) {
                resultFile = set.getString(1);
            }
        } catch (SQLException e) {
            Log.write(e);
        } finally {
            try { set.close(); } catch (Exception e) { }
            try { statement.close();} catch (Exception e) { }
        }
        return resultFile;
    }

    public RaceProgramStart getRaceProgramStart() {
        return raceProgramStart;
    }

    public void setRaceProgramStart(RaceProgramStart raceProgramStart) {
        this.raceProgramStart = raceProgramStart;
    }

    public static void main(String args []) {
        Connection conn = null;
        try {
            //HarnessApp.loadContainers();
            conn = Database.getConnection();

            RaceProgramFile racebookFile = new RaceProgramFile(
                            "C:\\Users\\Markus\\My Projects\\Filebase\\hippos\\ohjelma",
                            "CF1471986000000_CEH_CC5.php");
            RaceProgramFileParser raceProgramFileParser = new RaceProgramFileParser(racebookFile, conn);
            racebookFile = (RaceProgramFile)raceProgramFileParser.parse(conn);
            //racebookFile.delete(conn);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { conn.close(); } catch (Exception e) { }
        }
    }

    public void updateAdapters(RaceResultFile raceResultFile) {
        List raceResultStarts = raceResultFile.getStarts();

    }

    /*
    public void update(Connection conn) {
        PreparedStatement stmt = null;
        try {
            if(raceProgramStart != null) {
                raceProgramStart.update(conn);
            }
        } catch (Exception e) {
           e.printStackTrace();
        } finally {
            try{stmt.close();} catch(Exception e) {}
        }
    }*/
}

