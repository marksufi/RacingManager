package test;

import junit.framework.TestCase;

import java.sql.Date;
import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Jan 15, 2004
 * AlphaNumber: 4:06:57 PM
 * To change this template use Options | File Templates.
 */
public class SubStartTest extends TestCase {
    public static void main(String args[]) {
        junit.textui.TestRunner.run(SubStartTest.class);
    }

    public void testNormal() {
        //String [] s = new String [] {"Ke Rantala", "P ", "1.8.  ", "3 ", "2100/ 1", "20,9a          ", "10             ", "500 "};
        String s = "[Ke Rantala][P ][1.8.  ][3 ][2100/ 1][20,9a          ][10             ][500 ]";
        Calendar c = Calendar.getInstance();
        c.set(2004, 7, 3, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        Date raceDate = new Date(c.getTimeInMillis());
        System.out.println("raceDate: " + raceDate.getTime());

        c.set(2004, 7, 1, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        Date expectedDate = new Date(c.getTimeInMillis());
        System.out.println("expectedDate: " + expectedDate.getTime());

        try {
            /*
            SubStartParser st = new SubStartParser(new StringTokenizer(s));
            assertEquals(st.jockey, new String("Ke Rantala"));
            assertEquals(st.getLocality(), new String("P"));
            assertEquals(st.getRaceDate().parseDate().getRaceResultTime(), expectedDate.getRaceResultTime());
            assertEquals(st.getStartNumber(), new Integer(3));
            assertEquals(st.getRaceLength(), new Integer(2100));
            assertEquals(st.getRaceTrack(), new Integer(1));
            assertEquals(st.getRaceResultTime(), new BigDecimal(20.9).setScale(1, BigDecimal.ROUND_HALF_UP));
            assertEquals(st.getA(), new String("a"));
            assertEquals(st.getRanking(), new Integer(10));
            assertEquals(2, st.getRelaxionTime(raceDate));

            st.deduceYear();

            c.setValueA(2003, 7, 1, 0, 0, 0);
            c.setValueA(Calendar.MILLISECOND, 0);

            assertEquals(st.getRaceDate().parseDate(), c.getRaceResultTime());
            */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
