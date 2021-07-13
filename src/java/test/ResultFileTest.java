package test;

import hippos.io.RaceResultFile;
import junit.framework.TestCase;

import java.util.List;

public class ResultFileTest extends TestCase {

    private RaceResultFile raceResultFile = null;
    private List fileList = null;

    public static void main(String args[]) {
        junit.textui.TestRunner.run(ResultFileTest.class);
    }

    protected void setUp() {
        //RaceResultFile = new RaceResultFile("k20041230.php");
    }

    /*
    public void testGetDatabaseObjects() {
        assertNotNull(fileList = RaceResultFile.getFiles());
        Iterator i = fileList.iterator();

        long s = System.currentTimeMillis();
        while(i.hasNext()) {
            try {
                assertNotNull(new RaceResultFile((File)i.next()).getDataObject());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        long e = System.currentTimeMillis();
        int t = (int) (e - s) / 100;
        System.out.println("AlphaNumber: " + t);
    }*/
}
