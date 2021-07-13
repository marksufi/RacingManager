package hippos.io;

import hippos.database.Database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: May 3, 2005
 * AlphaNumber: 7:14:10 PM
 * To change this template use Options | File Templates.
 */
public abstract class HipposDirectory extends File {
    public HipposDirectory(String directory) throws FileNotFoundException {
        super(directory);
        if(!exists())
            throw new FileNotFoundException(directory);
    }

    public HipposDirectory(URI uri) {
        super(uri);
    }

    public HipposDirectory(File file, String s) {
        super(file, s);
    }

    public HipposDirectory(String s, String s1) {
        super(s, s1);
    }

    /**
     * Arranges files into a map ordered by date. [date][file][null]
     *
     * @param filemap
     * @return
     * @throws ParseException
     *
    public Map list(Map filemap) throws IOException, ParseException {
        System.out.print("Searhing for new files...");
        List files = Arrays.asList(list());
        Iterator filesItr = files.iterator();
        while(filesItr.hasNext()) {
            String filename = (String)filesItr.next();
            if(!existsInDatabase(filename)) {
                RaceFile file = createFile(filename);
                Date date = file.parseDate();
                if(filemap.get(date) == null)
                    filemap.put(date, new TreeMap());
                ((Map)filemap.get(date)).put(file, null);
            }
        }
        System.out.println("done!");
        return filemap;
    }*/

    public abstract RaceFile createFile(String filename) throws IOException;

    public abstract RaceFile createFile(URL url) throws IOException;

    //public abstract boolean exists(String filename);

    //public abstract List listNewFiles();
    public List listNewFiles() {
        Connection conn = null;
        List newFiles =new Vector();
        try {
            conn = Database.getConnection();
            return listNewFiles(conn, newFiles);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { conn.close(); } catch (Exception e) { }
        }
        return null;
    }


    public List <File> listAllFiles() {
        File fileObjects [] = listFiles();
        return new Vector(Arrays.asList(fileObjects));
    }

    public abstract List listNewFiles(Connection conn, List newFiles);
}
