package hippos.io;

import hippos.Locality;
import hippos.database.Database;
import utils.Log;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;
//import java.sql.Date;

//public abstract class RaceFile extends File implements FileParser {
public abstract class RaceFile extends File {
    List starts = new Vector();
    String id;
    private Locality locality;

    public RaceFile(RaceFile raceFile) {
        super(raceFile.getAbsolutePath());
    }

    public RaceFile(String path) {
        super(path);
        int lind = path.indexOf("_CE");
        if(lind > 0) {
            String slind = path.substring(lind + 3);
            if((lind = slind.indexOf("_")) > 0 || (lind = slind.indexOf(".")) > 0) {
                String locality = slind.substring(0, lind);
                if(locality.length() > 1) {
                    locality = locality.substring(0, 1) + locality.substring(1).toLowerCase();
                }
                this.locality = new Locality(locality);
            }
        }
    }

    public RaceFile(String directoryPath, String filename) {
        super(directoryPath, filename);
        int lind = filename.indexOf("_CE");
        if(lind > 0) {
            String slind = filename.substring(lind + 3);
            if((lind = slind.indexOf("_")) > 0 || (lind = slind.indexOf(".")) > 0) {
                String locality = slind.substring(0, lind);
                if(locality.length() > 1) {
                    locality = locality.substring(0, 1) + locality.substring(1).toLowerCase();
                }
                this.locality = new Locality(locality);
            }
        }
    }

    public Date getDate() {
        return locality.getDate();
    }

    public void setDate(Date date) {
        this.locality.setDate(date);
    }

    public void setDate(int year, int month, int day) {
        Calendar date = Calendar.getInstance();
        date.clear();
        date.set(year, month - 1, day);

        this.locality.setDate(date.getTime());
    }

    public Locality getLocality() {
        return locality;
    }

    /*
    public void setLocality(String locality) {
        this.locality = locality;
    }*/

    /**
     * Writes the content of the web location into this file
     *
     * @param url
     * @throws IOException
     * @throws UnknownHostException
     */
    public void write(URL url) throws IOException {
        BufferedReader reader = null;
        PrintWriter writer = null;
        URLConnection URLConn = null;
        InputStream urlStream = null;

            try {
                URLConn = url.openConnection();
                urlStream = URLConn.getInputStream();
                reader = new BufferedReader(new InputStreamReader(urlStream));
                writer = new PrintWriter(new FileOutputStream(this));
                String line = null;

                while ((line = reader.readLine()) != null) {
                    writer.println(line);
                }
            } finally {
                try { writer.flush(); } catch (Exception e) { }
                try { writer.close(); } catch (Exception e) { }
                try { reader.close(); } catch (Exception e) { }
                try { urlStream.close(); } catch (Exception e) { }
            }
    }

    /**
     * Writes the content of the web location into parameter file
     *
     * @param url
     * @param file
     * @throws IOException
     * @throws UnknownHostException
     */
    public File write(URL url, File file) throws IOException {
        BufferedReader reader = null;
        PrintWriter writer = null;
        URLConnection URLConn = null;
        InputStream urlStream = null;

        try {
            URLConn = url.openConnection();
            urlStream = URLConn.getInputStream();
            reader = new BufferedReader(new InputStreamReader(urlStream));
            writer = new PrintWriter(new FileOutputStream(file));
            String line = null;

            while ((line = reader.readLine()) != null) {
                writer.println(line);
            }
        } finally {
            try { writer.flush(); } catch (Exception e) { }
            try { writer.close(); } catch (Exception e) { }
            try { reader.close(); } catch (Exception e) { }
            try { urlStream.close(); } catch (Exception e) { }
        }

        return file;
    }

    public void write( char[] cBuf ) throws IOException {
        Writer writer = null;
        OutputStream stream = null;

        try {
            stream = new FileOutputStream(this);
            writer = new PrintWriter(stream);
            writer.write(cBuf);
        } finally {
            try { writer.flush(); } catch (Exception e) { }
            try { writer.close(); } catch (Exception e) { }
            try { stream.close(); } catch (Exception e) { }
        }
    }


    public void add(Object start) {
        starts.add(start);
    }

    public void print(Writer os) throws IOException {
        Log.write(super.toString());
    }

    public abstract Date parseDate() throws ParseException;

    /*
    public String toString() {
        StringBuffer str = new StringBuffer();
        str.append(getRaceHorseName() + ": " + locality + " " + date);

        Iterator startsItr = starts.iterator();
        while(startsItr.hasNext()) {
            str.append((startsItr.next()).toString() + "\n\n");
        }
        return str.toString();
    }*/

    public List getStarts() {
        return starts;
    }

    public void insert() {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = Database.getConnection();
            insert(conn);
        } catch (Exception e) {
            try{conn.rollback();} catch(Exception re) {}
            e.printStackTrace();
        } finally {
            try{conn.close();} catch(Exception e) {}
        }
    }

    public abstract void insert(Connection conn);

    public boolean isValid() {
        //if(this.getRaceHorseName().indexOf("CEMA") >= 0) return false;
        if(this.getName().indexOf("_CC2") >= 0 && this.getName().indexOf("_CC2.") < 0)
            return false; // koelähtö
        if(this.getName().indexOf("_CC3") >= 0 && this.getName().indexOf("_CC3.") < 0) return false; // ponilähtö
        return true;
    }

    public void update() {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = Database.getConnection();
        } catch (Exception e) {
            try{conn.rollback();} catch(Exception re) {}
            e.printStackTrace();
        } finally {
            try{conn.close();} catch(Exception e) {}
        }
    }

    //public abstract void update(Connection conn);

}
