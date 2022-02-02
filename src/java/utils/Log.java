package utils;

import hippos.utils.HipposProperties;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class Log {
    private static final File log = new File(HipposProperties.get("LOGFILE"));

    public static void clear() {
        try {
            PrintWriter out = new PrintWriter(new FileOutputStream(log));
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void write(String str) {
        try {
            PrintWriter out = new PrintWriter(new FileOutputStream(log.getAbsolutePath(), true));
            out.println(str);

            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static PrintWriter getPrintWriter() {
        try {
            return new PrintWriter(new FileOutputStream(log.getAbsolutePath(), true));
        } catch (FileNotFoundException e) {
            Log.write(e);
        }
        return null;
    }
    public static void write(Throwable e) {
        try {
            PrintWriter out = new PrintWriter(new FileOutputStream(log.getAbsolutePath(), true));
            out.println("\n");
            e.printStackTrace(out);
            e.printStackTrace();
            out.flush();
            out.close();
            //System.out.println("<<< LOG: " + e.getMessage() + ">>>");
        } catch (Exception le) {
            le.printStackTrace();
        }
    }

    public static void write(Throwable t, String str) {
        try {
            write(new Exception( str, t));
            t.printStackTrace();
        } catch (Exception de) {
            de.printStackTrace();
        }
    }

    public static void write(Throwable e, File file) {
        try {
            PrintWriter out = new PrintWriter(new FileOutputStream(log.getAbsolutePath(), true));
            out.println("\n" + file);
            e.printStackTrace(out);
            out.flush();
            out.close();
            System.out.println("<<< LOG: " + e.getMessage() + ": " + file + " >>>");
        } catch (Exception de) {
            de.printStackTrace();
        }
    }
}