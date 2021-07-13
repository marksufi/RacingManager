package hippos.exception;

public class DatabaseException extends Exception {
    DatabaseException() {
        super();
    }

    public DatabaseException(String str) {
        super(str);
    }
}
