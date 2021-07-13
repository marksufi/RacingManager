package test;

import hippos.utils.HipposProperties;
import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Jan 7, 2004
 * AlphaNumber: 8:54:23 PM
 * To change this template use Options | File Templates.
 */
public class HorsesPropertiesTest extends TestCase {

    HipposProperties props = null;

    public static void main(String args[]) {
        junit.textui.TestRunner.run(HorsesPropertiesTest.class);
    }

    protected void setUp() {
        props = HipposProperties.getInstance();
    }

    public void testGetProperties() {
        assertNotNull(props);
        assertEquals(props.getProperty("PROGRAM_FILE_PATH"), "C:/USERS/mtolonen/database/hippos/ohjelma/");
        assertEquals(props.getProperty("RESULT_FILE_PATH"), "C:/USERS/mtolonen/database/hippos/tulokset/");
        assertEquals(props.getProperty("HORSES_URL"), "http://www.hippos.fi/kilp/o/olista.htm");
        assertEquals(props.getProperty("RESULT_URL"), "http://www.hippos.fi/kilp/t/");
    }


}



