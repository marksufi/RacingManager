package hippos.math;

import utils.Log;

import java.io.*;

/**
 * Saves or loads an object to file
 *
 * User: marktolo
 * Date: Mar 2, 2005
 * AlphaNumber: 6:56:50 PM
 * To change this template use Options | File Templates.
 */
public class FileContainer implements Container {
    File file;

    /**
     * @param filename whete to store the object
     **/
    public FileContainer(String filename) throws IOException {
        file = new File(filename);
        if(!file.exists()) {
            file.createNewFile();
        }
    }

    /**
     * @param filename whete to store the object
     */
    public FileContainer(String path, String filename) throws IOException {
        file = new File(path);
        if(!file.exists()) {
            file.mkdir();
        }
        file = new File(path, filename);
        if(!file.exists()) {
            file.createNewFile();
        }
    }

    public File getFile() {
        return file;
    }

    /**
     * @param file whete to store the object
     */
    public FileContainer(File file) throws IOException {
        file = new File(file.getAbsolutePath());
        if(!file.exists()) {
            file.createNewFile();
        }
    }

    /**
     * Load an object
     *
     * @return an stored object if file is found
     */
    public Object load() {
        Object o = null;
        ObjectInputStream vuo = null;
        FileInputStream tied = null;
        try {
            tied = new FileInputStream(file);
            if(tied.available() > 0) {
                vuo = new ObjectInputStream(tied);
                o =  vuo.readObject();
            }
        } catch (Exception e) {
            Log.write(e);
        } finally {
            try {vuo.close(); } catch(Exception e) { }
            try {tied.close(); } catch(Exception e) { }
        }
        return o;
    }

    /**
     * Saves an object to a file
     *
     * @param o
     */
    public void save(Object o) {
        FileOutputStream tied = null;
        ObjectOutputStream vuo = null;
        try {
            tied = new FileOutputStream(file);
            vuo = new ObjectOutputStream(tied);
            vuo.writeObject(o);
            vuo.flush();
        } catch (Exception e) {
            Log.write(e);
        } finally {
            try {vuo.close(); } catch(Exception e) { }
            try {tied.close(); } catch(Exception e) { }
        }
    }
}
