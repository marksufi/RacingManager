package hippos.exception;

public class IncorrectParameterException extends Exception {

    public IncorrectParameterException(String parameter) {
        super(parameter);
    }
}
