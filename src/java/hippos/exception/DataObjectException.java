package hippos.exception;

import utils.Log;


public class DataObjectException extends Exception {

    public DataObjectException() {}

    public DataObjectException(String exception) {
        super(exception);
    }

    public DataObjectException(Throwable e, String exception) {
        super(exception, e);
    }

    public DataObjectException(Throwable e) {
        super(e);
    }
}
