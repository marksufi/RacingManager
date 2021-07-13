package hippos.database;

import hippos.lang.Pointer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLDatabase {
    public static final String PROTOCOL = "jdbc:mysql://localhost:3306/hippos";

    //public static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    public static final String DRIVER = "com.mysql.jdbc.Driver";
    public static final String USERNAME = "root";
    public static final String PASSWORD = "";

    private static Database database = null;
    private static Connection conn = null;

    private MySQLDatabase() {
        try {
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
            MySQLDatabase.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
