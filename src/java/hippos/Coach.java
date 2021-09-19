package hippos;

import hippos.lang.stats.Form;
import hippos.utils.DateUtils;
import utils.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.StringTokenizer;

public class Coach extends Person {

    public Coach(String coach) {
        super(coach);
    }


}
