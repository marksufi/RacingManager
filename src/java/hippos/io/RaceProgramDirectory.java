package hippos.io;

import hippos.HarnessApp;
import hippos.RaceResultStart;
import hippos.lang.Pointer;
import hippos.utils.HipposProperties;
import utils.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: May 3, 2005
 * AlphaNumber: 11:24:48 PM
 * To change this template use Options | File Templates.
 */
public class RaceProgramDirectory extends HipposDirectory {
    public static final String directoryPath = HarnessApp.testMode ? HipposProperties.get("TEST_PROGRAM_FILE_PATH") : HipposProperties.get("PROGRAM_FILE_PATH");

    public RaceProgramDirectory() throws FileNotFoundException {
        super(directoryPath);
    }

    public RaceFile createFile(String filename) throws IOException {
        return new RaceProgramFile(directoryPath, filename);
    }

    public RaceFile createFile(URL url) throws IOException {
        return new RaceProgramFile(directoryPath, url);
    }

    public RaceFile createFile(RaceResultFile raceResultFile, RaceResultStart raceResultStart) throws IOException {
        RaceProgramFile raceProgramFile = null;
        StringTokenizer st = new StringTokenizer(raceResultFile.getName(), ".");
        if(raceResultStart != null) {
            String snStr = Integer.toString(raceResultStart.getStartNumber().intValue());
            String filename = st.nextToken() + "_CC" + snStr + "." + st.nextToken();
            raceProgramFile = new RaceProgramFile(directoryPath, filename);
        }
        return raceProgramFile;
    }

    /**
     * Listaa tiedostosta sellaiset ohjelmatiedostot joilta puuttuu tulostiedosto
     *
     * @param conn
     * @param newFiles
     * @return
     *
     */
    public List listNewFiles(Connection conn, List newFiles) {
        //System.out.println("RaceProgramDirectory.listNewFiles");
        Pointer pointer = new Pointer("Removing programfiles with resultfile");
        pointer.start();

          // Listaa kaikki olemassa olevat käsiohjelmatiedostot
        Object fileObjects [] = list();
        newFiles = Arrays.asList(fileObjects);
        Vector oldFiles = new Vector(newFiles);

        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = conn.prepareStatement("select FILENAME,RESULTFILE from PROGRAMFILE");
            set = statement.executeQuery();
            while(set.next()) {
                try {
                    // Poistaa listasta sellaiset käsiohjelmat, joilla on kannassa jo tulostiedosto
                    String programFile = set.getString(1);
                    String resultfile = set.getString(2);

                    if(resultfile != null) {
                        if(oldFiles.contains(programFile)) {
                            oldFiles.remove(programFile);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            /*
            Iterator itr = newFiles.iterator();
            while(itr.hasNext()) {
                String filename = (String)itr.next();
                if(!newFiles.contains(filename)) {
                    newFiles.add(filename);
                }
            }*/
        } catch (Exception e) {
            Log.write(e);
        } finally {
            try { set.close(); } catch (Exception e) { }
            try { statement.close();} catch (Exception e) { }
        }

        pointer.interrupt();
        return new Vector(oldFiles);
    }

    /**
     * Listaa tietokannasta sellaiset ohjelmatiedostot joilta puuttuu tulostiedosto
     *
     * @param conn
     * @param newFiles
     * @return
     *
     */
    public List listResultlessFiles(Connection conn) {
        //System.out.println("RaceProgramDirectory.listNewFiles");
        Pointer pointer = new Pointer("Listing resultless program files");
        pointer.start();

        //List newFiles = new Vector();
        Object fileObjects [] = list();
        List newFiles = new Vector();
        //List newFiles = new Vector(Arrays.asList(fileObjects));
        //oldFiles = new Vector(oldFiles);

        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            statement = conn.prepareStatement("select FILENAME from PROGRAMFILE where RESULTFILE is null");
            set = statement.executeQuery();
            while(set.next()) {
                String filename = set.getString(1);
                newFiles.add(filename);
                /*
                if(newFiles.contains(filename)) {
                    newFiles.remove(filename);
                }*/
            }

        } catch (Exception e) {
            Log.write(e);
        } finally {
            try { set.close(); } catch (Exception e) { }
            try { statement.close();} catch (Exception e) { }
        }

        pointer.interrupt();
        return newFiles;
    }

    /*
    public void deleteResults(String resultFilename){
        Connection conn = null;
        PreparedStatement statement = null;
        try {
            conn = Database.getConnection();

            statement = conn.prepareStatement("DELETE FROM HORSERESULT WHERE FID LIKE ?");
            statement.setString(1, resultFilename);
            statement.executeUpdate();

            statement = conn.prepareStatement("DELETE FROM RESULTFILE WHERE FILENAME LIKE ?");
            statement.setString(1, resultFilename);
            statement.executeUpdate();

            Log.write("DELETED: " + resultFilename);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { statement.close(); } catch (Exception e) {}
            try { conn.commit(); } catch (Exception e) {}
            try { conn.close(); } catch (Exception e) {}
        }
    }
    */

    public static void main(String args[]) {
        try {
            List <File> files = new RaceProgramDirectory().listAllFiles();
            for(File file : files) {
                if(file.getName().contains("_CEP")) {
                    if(!file.getName().contains("_CEP.") &&
                            !file.getName().contains("_CEPI.")) {
                        //file.delete();
                        System.out.println(file.getName());
                    }
                }
            }

            /*
            newFiles.addAll(new RaceResultDirectory().listNewFiles());
            Collections.sort(newFiles, new FileComparator());
            System.out.println("RaceProgramDirectory.main: " + newFiles);
            System.out.println("RaceProgramDirectory.main: " + newFiles.size());

             */
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
    }

    public boolean isValidFile(String filename) {
        if(filename != null && filename.length() > 25)
            return true;
        return false;
    }
}
