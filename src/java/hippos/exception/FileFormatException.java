package hippos.exception;

import utils.Log;

/**
 * Created by Markus on 10.1.2019.
 */
public class FileFormatException extends Exception {
    public FileFormatException(String s) {
        super(s);

        Log.write(this, s);
    }
}
