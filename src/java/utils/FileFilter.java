package utils;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Feb 16, 2004
 * AlphaNumber: 1:32:46 PM
 * To change this template use Options | File Templates.
 */
public class FileFilter implements FilenameFilter {
    private File dir;
    private String str;

    public FileFilter(File dir, String str) {
        this.dir = dir;
        this.str = str;
    }

    public boolean accept(File dir, String name) {
        return name.indexOf(str) >= 0;

    }
}
