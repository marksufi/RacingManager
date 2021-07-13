package hippos;

import hippos.lang.stats.Form;
import hippos.utils.DateUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class RaceProgramDriver extends Driver {
    public RaceProgramDriver(String value) {

        super(value);
    }

    public RaceProgramDriver(ResultSet raceSet) throws SQLException {
        super(raceSet);
    }

    public BigDecimal getDriverDiff(SubStart subStart) {
        try {
            BigDecimal raceProgramDriverAvg = getForm().awardRate(null);

            //BigDecimal subDriverAvg = this.driverClass;
            BigDecimal subDriverAvg = subStart.getDriverRaceTypeClass();
            BigDecimal totalAvgSum = subDriverAvg.add(raceProgramDriverAvg);

            if (totalAvgSum.doubleValue() < 0.0)
                System.out.println("SubTime.getDriverDiff: negative summary" + subDriverAvg + "+" + raceProgramDriverAvg + "=" + totalAvgSum);

            //BigDecimal diff = this.driverClass.subtract(raceProgramDriverAvg);
            BigDecimal diff = subDriverAvg.subtract(raceProgramDriverAvg);
            diff = diff.divide(totalAvgSum, 2, RoundingMode.HALF_UP);

            return diff.multiply(BigDecimal.valueOf(1.00));
        } catch (ArithmeticException e) {
            // divide by zero
        } catch (NullPointerException e) {
            //
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

}
