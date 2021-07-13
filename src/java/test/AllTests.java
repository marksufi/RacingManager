package test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("All Hippos Tests");
        suite.addTest(new TestSuite(LinRegTest.class));
        suite.addTest(new TestSuite(ResultTest.class));

//suite.addTest(new TestSuite(UrlListenerTest.class));
        //suite.addTest(new TestSuite(LogTest.class));
        //suite.addTest(new TestSuite(HorsesPropertiesTest.class));
        //suite.addTest(new TestSuite(HTMLReaderTest.class));
//suite.addTest(new TestSuite(ResultFileTest.class));
        return suite;
    }

    //c:\webgain\visualcafeee\Java\Lib;c:\webgain\visualcafeee\Java\Lib\SYMCLASS.ZIP;c:\webgain\visualcafeee\Java\Lib\CLASSES.ZIP;c:\webgain\visualcafeee\Java\Lib\COLLECTIONS.ZIP;c:\webgain\visualcafeee\Java\Lib\ICEBROWSERBEAN.JAR;c:\webgain\visualcafeee\Java\Lib\SYMTOOLS.JAR;c:\webgain\visualcafeee\JFC\SWINGALL.JAR;c:\webgain\visualcafeee\Bin\Components\SFC.JAR;
}

