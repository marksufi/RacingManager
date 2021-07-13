package test.regression;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Created by marktolo on 7.9.2014.
 */
public class RegressionTest {
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Multilinear Tests");
        //suite.addTest(new TestSuite(MultilinearTests.class));
        //suite.addTest(new TestSuite(MultipleRegressionTest.class));
        suite.addTest(new TestSuite(RankTimeTest.class));

        return suite;
    }
}
