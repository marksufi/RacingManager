package hippos.utils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

/**
 * Created by marktolo on 4.9.2014.
 */
public class FileUtils {

    /**
     * Writes the content of the web location into parameter file
     *
     * @param url
     * @param file
     * @throws java.io.IOException
     * @throws java.net.UnknownHostException
     */
    public static File write(URL url, File file) throws UnknownHostException {
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
        } catch (UnknownHostException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { writer.flush(); } catch (Exception e) { }
            try { writer.close(); } catch (Exception e) { }
            try { reader.close(); } catch (Exception e) { }
            try { urlStream.close(); } catch (Exception e) { }
        }

        return file;
    }

    public static File write(File source, File target) {
        BufferedReader reader = null;
        PrintWriter writer = null;

        try {
            reader = new BufferedReader( new FileReader( source ));
            writer = new PrintWriter(new FileOutputStream( target ));
            String line = null;

            while ((line = reader.readLine()) != null) {
                writer.println(line);
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        } finally {
            try { writer.flush(); } catch (Exception e) { }
            try { writer.close(); } catch (Exception e) { }
            try { reader.close(); } catch (Exception e) { }
        }

        return target;
    }

    public static File write(String source, File target) {
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(new FileOutputStream( target, false ));
            writer.print(source);
        } catch ( Exception e ) {
            e.printStackTrace();
        } finally {
            try { writer.flush(); } catch (Exception e) { }
            try { writer.close(); } catch (Exception e) { }
        }

        return target;
    }

    public static String read( File source ) {
        BufferedReader reader = null;
        StringBuffer sb = new StringBuffer();

        try {
            reader = new BufferedReader( new FileReader( source ));
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        } finally {
            try { reader.close(); } catch (Exception e) { }
        }

        return sb.toString();
    }

    /**
     * Writes the content of the web location into parameter file
     *
     * @param url
     * @param file
     * @throws java.io.IOException
     * @throws java.net.UnknownHostException
     */
    public static String read( URL url ) throws IOException {
        BufferedReader reader = null;
        URLConnection URLConn = null;
        InputStream urlStream = null;
        StringBuffer sb = new StringBuffer();

        try {
            URLConn = url.openConnection();
            urlStream = URLConn.getInputStream();
            reader = new BufferedReader(new InputStreamReader(urlStream));
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append( line + "\n");
            }
        } finally {
            try { reader.close(); } catch (Exception e) { }
            try { urlStream.close(); } catch (Exception e) { }
        }

        return sb.toString();
    }

}
