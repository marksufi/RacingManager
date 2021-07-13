package test;

import junit.framework.TestCase;
import hippos.math.LinReg;

import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Dec 18, 2005
 * Time: 1:15:11 AM
 * To change this template use Options | File Templates.
 */
public class LinRegTest extends TestCase  {
    LinReg reg;

    public static void main(String args[]) {
        junit.textui.TestRunner.run(LinRegTest.class);
    }

    protected void setUp() {
        reg = new LinReg();
    }

    public void testadd() {
        reg.add(2, 1);
        TestCase.assertEquals(1.0, reg.get(2.0), 0.01);

        reg.add(2, 1);
        TestCase.assertEquals(1.0, reg.get(2.0), 0.01);


        reg.add(3, 2);
        reg.add(4, 3);

        TestCase.assertEquals(2.0, reg.get(3), 0.01);

        TestCase.assertEquals(1.0, reg.getRValue(), 0.01);

        System.out.println(reg.getRValue());
        System.out.println(reg.getRValue2());

        reg.add(2, 1000);
        reg.add(4000, 3);

        System.out.println(reg.getRValue());
        System.out.println(reg.getRValue2());

        TestCase.assertEquals(1.0, reg.getRValue(), 0.01);
        TestCase.assertEquals(1.0, reg.getRValue2(), 0.01);

        System.out.println(reg.getRValue());
    }
}
