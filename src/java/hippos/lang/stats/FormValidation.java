package hippos.lang.stats;

import hippos.SubStart;

import java.util.Date;

/**
 * Created by Markus on 4.5.2020.
 */
public interface FormValidation {

    boolean validate(SubStart subStart);
    Date getStartDate();
    Date getEndDate();
}
