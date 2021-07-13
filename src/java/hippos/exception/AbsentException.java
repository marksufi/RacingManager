package hippos.exception;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Sep 20, 2004
 * AlphaNumber: 7:36:12 PM
 * To change this template use Options | File Templates.
 */
public class AbsentException extends IOException {
    public AbsentException(String horseId) {
        super(horseId);
    }

    public AbsentException() {

    }
}
