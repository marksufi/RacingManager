package hippos;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class RaceResultDriver extends Person implements RaceDriver {
    private DriverForm driverForm = null;

    public RaceResultDriver(String name) {

        super(name);

        driverForm = new DriverForm(name);
    }

    public RaceResultDriver(String name, Connection conn, Date date) {

        super(name);

        driverForm = new DriverForm(name, conn, date);
    }

    public DriverForm getDriverForm() {
        return driverForm;
    }
}
