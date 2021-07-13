package utils;

public class DatabaseException extends Exception {

    public DatabaseException(String exception) {
        super(exception);
        Log.write(exception);
    }

    public DatabaseException(Exception e) {
        super(e);
        Log.write(e);
    }

    public DatabaseException(Exception e, String exception) {
        super(exception, e);
        Log.write(e, exception);
    }
}
