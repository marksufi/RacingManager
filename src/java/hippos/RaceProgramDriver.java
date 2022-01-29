package hippos;

import utils.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RaceProgramDriver extends Person implements RaceDriver {
    private DriverForm driverForm;

    public RaceProgramDriver(String name) {

        super(name);

        driverForm = new DriverForm(getName());
    }

    public RaceProgramDriver(ResultSet raceSet) throws SQLException {
        this(raceSet.getString("KULJETTAJA"));

        driverForm = new DriverForm(getName(), raceSet);
    }

    public RaceProgramDriver(String name, Connection conn, java.util.Date date) {
        super(name);

        try {
            driverForm = new DriverForm(getName(), conn, date);

        } catch (Exception e) {
            Log.write(e);
        }
    }

    public BigDecimal getDriverDiff(SubStart subStart) {
        try {
            BigDecimal raceProgramDriverAvg = driverForm.getForm().awardRate(null);

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

    public DriverForm getDriverForm() {
        return driverForm;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        try {
            sb.append(super.toString());
            //sb.append("(" + driverForm.raceTypeForm.firstRateProcents(2) + "%)");
            //sb.append("(" + driverForm.getJockeyClass() + ")");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();

    }
}
