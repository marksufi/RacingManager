package test;

import junit.framework.TestCase;
import utils.HTMLParser;

import java.io.File;

public class HTMLReaderTest extends TestCase {

    private HTMLParser fileReader = null;

    public static void main(String args[]) {
        junit.textui.TestRunner.run(HTMLReaderTest.class);
    }

    protected void setUp() {
        try {
            fileReader = new HTMLParser(new File("C:/USERS/mtolonen/database/hippos/tulokset/f20030725.htm"));
        } catch (Exception e) {
        }
    }

    public void testSetup() {
        assertNotNull(fileReader);
    }

    public void testGetLines() {
        assertNotNull(fileReader.getLines());
    }
}
