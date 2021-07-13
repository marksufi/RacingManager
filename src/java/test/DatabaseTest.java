package test;

import hippos.database.Database;
import junit.framework.TestCase;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseTest extends TestCase {

    Database db = null;

    public static void main(String args[]) {
        junit.textui.TestRunner.run(DatabaseTest.class);
    }

    protected void setUp() {
        Connection conn = null;
        try {
            conn = Database.getConnection();
            System.out.println(conn.hashCode());
            conn = Database.getConnection();
            System.out.println(conn.hashCode());
            conn = Database.getConnection();
            System.out.println(conn.hashCode());
            conn = Database.getConnection();
            System.out.println(conn.hashCode());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { conn.close(); } catch (SQLException e) {}
        }
    }


    public void testGetConnection() {
        try {
            assertNotNull(db.getConnection());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
