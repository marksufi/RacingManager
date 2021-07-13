package test;

import hippos.io.RaceProgramFile;
import junit.framework.TestCase;

import java.util.List;

public class ProgramFileTest extends TestCase {

    private RaceProgramFile programFile = null;
    private List fileList = null;

    public static void main(String args[]) {
        junit.textui.TestRunner.run(ProgramFileTest.class);
    }

    protected void setUp() {
        //assertNotNull(programFile = new RaceProgramFile("k2004123001.php"));
    }

    /*
    public void testGetDatabaseObject() {
        long s = System.currentTimeMillis();
        try {
            assertNotNull(programFile.parse());
        } catch (Exception e) {
            e.printStackTrace();
        }
        long e = System.currentTimeMillis();
        int t = (int) (e - s) / 100;
        System.out.println("AlphaNumber: " + t);
    } */

    /*
    public void testGetDatabaseObjects() {
        String[] programFiles = new RaceProgramFile().getFile().list();
        long s = System.currentTimeMillis();
        for (int i = 0, l = programFiles.length; i < l; i++) {
            long s0 = System.currentTimeMillis();
            try {
                //assertNotNull(resultFile.getDatabaseObject());
                assertNotNull(new RaceProgramFile(programFiles[i]).getDataObject());
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            }
            long e0 = System.currentTimeMillis();
            double t0 = (double) (e0 - s0) / 1000.00;
            System.out.println("AlphaNumber: " + t0);
        }
        long e = System.currentTimeMillis();
        double t = (double) (e - s) / 1000.00;
        System.out.println("Total AlphaNumber: " + t);
    }


    public void testGetDatabaseObjectSpeed() {
        assertNotNull(fileList = new RaceProgramFile().getFiles());
        Iterator i = fileList.iterator();
        long s = System.currentTimeMillis();
        int count = 0;
        while(i.hasNext()) {
            try {
                programFile = new RaceProgramFile((File)i.next());
                System.out.println(programFile.toString());
                assertNotNull(programFile.parse());
            } catch (Exception e) {
                e.printStackTrace();
            }
            count++;
        }
        long e = System.currentTimeMillis();
        double t = (double) (e - s) / 1000.00;
        System.out.println("Total AlphaNumber: " + t + "s = " + t / count + "s per file");
    }
    */
}
