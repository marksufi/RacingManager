package hippos.exception;

public class InformationMissingException extends Exception {
    public InformationMissingException() {
    }

    public InformationMissingException(String s) {
        super(s);
    }

    public InformationMissingException(Throwable throwable) {
        super(throwable);
    }

    public InformationMissingException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
