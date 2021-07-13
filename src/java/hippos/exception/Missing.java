package hippos.exception;

public class Missing extends Exception {
    public Missing() {
    }

    public Missing(String s) {
        super(s);
    }

    public Missing(Throwable throwable) {
        super(throwable);
    }

    public Missing(String s, Throwable throwable) {
        super(s, throwable);
    }
}
