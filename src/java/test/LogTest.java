package test;

import junit.framework.TestCase;
import utils.Log;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Dec 31, 2003
 * AlphaNumber: 9:30:44 AM
 * To change this template use Options | File Templates.
 */
public class LogTest extends TestCase {
    public static void main(String args[]) {
        junit.textui.TestRunner.run(LogTest.class);
    }

    public void testWrite() {
        Log.write("this is a test");
        Log.write(new Exception("test exception 1"));
        Log.write(new Exception("test exception 2"), "test exception 2");
    }

    public void testClear() {
        Log.clear();
    }
}





