package hippos.exception;

/**
 *  Indicates that ProgramHorseParse has finished parsing the horse data
 *  and the jorse is ready for storing
 */

public class NoMoreDataException extends Exception {
    public NoMoreDataException(String s) {
        super(s);
    }
}
