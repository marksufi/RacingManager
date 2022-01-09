package hippos;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RaceResultDriver extends Person {
    private DriverForm driverForm;

    public RaceResultDriver(String name) {

        super(name);
        driverForm = new DriverForm(name);
    }

    public DriverForm getDriverForm() {
        return driverForm;
    }
}
