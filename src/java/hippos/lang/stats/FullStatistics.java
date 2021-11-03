package hippos.lang.stats;

import hippos.RaceProgramHorse;
import hippos.database.Statements;
import hippos.util.Mapper;
import hippos.utils.DateUtils;
import utils.Log;

import java.sql.*;
import java.sql.Date;
import java.util.*;

public class FullStatistics extends Statistics {

    public FullStatistics(String label, RaceProgramHorse raceProgramHorse) {
        super(label, raceProgramHorse);
    }

    protected PreparedStatement getStatement(Connection conn) {
        return Statements.getTimeFormStatement(conn, name, race, DateUtils.toSQLDate(endDate));
    }

}


