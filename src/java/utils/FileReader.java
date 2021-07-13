package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.util.List;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: May 10, 2005
 * AlphaNumber: 8:18:44 PM
 * To change this template use Options | File Templates.
 */
public class FileReader extends BufferedReader {
    List lines = new Vector();

    public FileReader(File file) throws FileNotFoundException {
        super(new java.io.FileReader(file));
    }

    public FileReader(Reader reader) {
        super(reader);
    }

    public List getLines() {
        return lines;
    }
}
