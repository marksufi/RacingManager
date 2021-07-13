package hippos.lang.stats;

import hippos.exception.DataObjectException;
import utils.Log;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Nov 25, 2004
 * AlphaNumber: 5:37:33 PM
 * To change this template use Options | File Templates.
 */
public class FormParser {
    Form form;

    public FormParser(Form form) throws DataObjectException {
        this.form = form;
    }

    /**
     * Statistical data constructor: Line format:
     *
     * f.e. "Yht: 46 2-6-2"
     *      "Yht:31478 3877-3310-2940   8.958.423 e"
     *      "04 :   48    6-   5-   1      13.150 e"
     *      "    :          -    -                 e "
     *
     * @param line includes eth statistic data
     */
    public Form parse(String line) throws DataObjectException {
        //System.out.println("FormParser.parse(" + line + ")");
        try {
            StringTokenizer st = new StringTokenizer(line, ": -");
            if(form.getLabel() == null) form.setLabel(st.nextToken());
            if(form.getStarts() == null) form.setStarts(new BigDecimal(st.nextToken()));
            if(form.getFirsts() == null) form.setFirsts(new BigDecimal(st.nextToken()));
            if(form.getSeconds() == null) form.setSeconds(new BigDecimal(st.nextToken()));
            if(form.getThirds() == null) form.setThirds(new BigDecimal(st.nextToken()));

            while (st.hasMoreTokens()) {
                String s = st.nextToken();
                /*
                if (s.indexOf('a') > 0 && form.gegetaRecord() == null) {
                    form.addaRecord(new AlphaNumber(s));
                } else if (s.indexOf(',') > 0 && form.gettRecord() == null) {
                    form.addtRecord(new AlphaNumber(s));
                } else if (form.getAwards() == null) {
                    for(int i = s.indexOf("."); i > 0; i = s.indexOf(".")) {
                        s = s.substring(0, i) + s.substring(i + 1);
                    }
                    //s = s.replaceAll(".",""); ei toimi, Java bugi
                    form.setAwards(new AlphaNumber(s));
                }*/
            }
        } catch(NoSuchElementException ne) {
            form.setStarts(new BigDecimal(0));
            form.setFirsts(new BigDecimal(0));
            form.setSeconds(new BigDecimal(0));
            form.setThirds(new BigDecimal(0));
            form.setAwards(new BigDecimal(0));
        } catch (Exception e) {
            Log.write(new Exception(line, e));
            //throw new DataObjectException(e, "Form.parse(" + line + ")");
        }
        return form;
    }
}
