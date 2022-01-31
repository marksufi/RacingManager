package hippos.database;

import hippos.lang.Pointer;
import utils.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    public static boolean useMySql = false;
    public static String PROTOCOL = "jdbc:oracle:thin:@localhost:1521:hippos";
    public static String DRIVER = "oracle.jdbc.driver.OracleDriver";
    public static String USERNAME = "system";
    //public static String PASSWORD = "hcJ1CFJL";
    public static String PASSWORD = "HCj1cfjl";
    //public static final String PASSWORD = "BN3896jK";

    private static Database database = null;
    private static Connection conn = null;

    protected Database() {
        try {
            if(useMySql) {
                PROTOCOL = "jdbc:mysql://localhost:3306/hippos";
                DRIVER = "com.mysql.cj.jdbc.Driver";
                USERNAME = "root";
                PASSWORD = "";
            }

            Class.forName(DRIVER);
        } catch (Exception e) {
            //Log.write(e);
        }
    }

    public static Connection getConnection() throws SQLException {
        Pointer pointer = new Pointer("Opening Database");
        try {

            if(database == null) {
                database = new Database();
            }

            if(conn != null) {
                return conn;
            }

            pointer.start();
            conn = DriverManager.getConnection(PROTOCOL, USERNAME, PASSWORD);
            conn.setAutoCommit(false);

            System.out.println("Connection OK");
            pointer.interrupt();
            return conn;
        } catch (SQLException e) {
            //Log.write(e);
            pointer.interrupt();
            throw e;
        }
    }

    public static void main(String args []) {
        try {
            Database.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}

