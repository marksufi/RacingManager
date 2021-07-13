package hippos.io;

import hippos.HarnessApp;
import hippos.database.Database;
import hippos.utils.HipposProperties;
import utils.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: May 3, 2005
 * AlphaNumber: 11:31:12 PM
 * To change this template use Options | File Templates.
 */
public class RaceResultDirectory extends HipposDirectory {
    public static final String directoryPath = HarnessApp.testMode ? HipposProperties.get("TEST_RESULT_FILE_PATH") : HipposProperties.get("RESULT_FILE_PATH");
    //public static final String directoryPath = HipposProperties.get("TEST_RESULT_FILE_PATH");

    public RaceResultDirectory() throws FileNotFoundException {
        super(directoryPath);
    }

    public RaceFile createFile(String filename) throws IOException {
        RaceResultFile raceResultFile = new RaceResultFile(directoryPath, filename);
        return raceResultFile;
    }

    public RaceFile createFile(URL url) throws IOException {
        RaceResultFile raceResultFile = new RaceResultFile(directoryPath, url);
        return raceResultFile;
    }

    public boolean exists(String filename) {
        Connection conn = null;
        PreparedStatement stmt =  null;
        ResultSet rs = null;

        try {
            conn = Database.getConnection();
            stmt = conn.prepareStatement("select FILENAME from RESULTFILE");
            rs = stmt.executeQuery();

            if(rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            Log.write(e);
        } finally {
            try {rs.close();} catch(Exception e) {}
            try {stmt.close();} catch(Exception e) {}
            try {conn.close();} catch(Exception e) {}
        }
        return false;
    }

    public List listNewFiles(Connection conn, List newFiles) {
        System.out.println("RaceResultDirectory.listNewFiles");
        Object fileObjects [] = list();
        List files = new Vector(Arrays.asList(fileObjects));

        //Connection conn = null;
        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            //conn = Database.getConnection();
            statement = conn.prepareStatement("select FILENAME from RESULTFILE");
            set = statement.executeQuery();
            while(set.next()) {
                String filename = set.getString(1);

                files.remove(filename);
            }

            Iterator itr = files.iterator();
            while(itr.hasNext()) {
                String filename = (String)itr.next();
                if(!newFiles.contains(filename)) {
                    newFiles.add(filename);
                }
            }

        } catch (SQLException e) {
            Log.write(e);
        } finally {
            try { set.close(); } catch (Exception e) { }
            try { statement.close();} catch (Exception e) { }
            //try { conn.close(); } catch (Exception e) { }
        }
        return newFiles;
    }

    public List listResultFiles() {
        List resultFiles = new ArrayList();
        File[] files = listFiles();
        for(int i = 0; i < files.length; i++) {
            resultFiles.add(new RaceResultFile(files[i]));
        }
        return resultFiles;
    }


    public List listResultFiles(List programFiles) {
        Object fileObjects [] = list();
        List files = new Vector(Arrays.asList(fileObjects));
        Set resultFiles = new TreeSet();
        List newResultFiles = new Vector();

        Iterator itr = programFiles.iterator();
        while(itr.hasNext()) {
            String filename = (String) itr.next();
            String resultFilename = filename.substring(0, filename.lastIndexOf("_"));
            resultFilename += filename.substring(filename.indexOf("."));

            //if(files.contains(resultFilename) && !newFiles.contains(resultFilename)) {
                resultFiles.add(resultFilename);
            //}
        }
        programFiles.addAll(resultFiles);

        return programFiles;

    }
}
