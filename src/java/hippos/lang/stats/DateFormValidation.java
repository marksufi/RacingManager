package hippos.lang.stats;

import hippos.SubStart;

import java.util.Date;

public class DateFormValidation implements FormValidation {
    private Date startDate;
    private Date endDate;

    public DateFormValidation(Date endDate) {
        this.startDate = null;
        this.endDate = endDate;
    }

    public DateFormValidation(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public boolean validate(SubStart subStart) {
        if(startDate != null)
            if(subStart.getDate().compareTo(startDate) < 0)
                return false;
        if(subStart.getDate().compareTo(endDate) > 0)
            return false;
        return true;
    }

    @Override
    public Date getStartDate() {
        return startDate;
    }

    @Override
    public Date getEndDate() {
        return endDate;
    }
}
