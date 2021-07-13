package hippos.exception;

public class Ready extends Exception {
    public Ready() {
    }

    public Ready(String s) {
        super(s);
    }

    public Ready(Throwable throwable) {
        super(throwable);
    }

    public Ready(String s, Throwable throwable) {
        super(s, throwable);
    }
}
