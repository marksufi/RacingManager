package hippos.io;

import hippos.web.NokiaSocket;

import java.net.Socket;
import java.net.URL;
import java.io.*;

import utils.Log;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Mar 24, 2006
 * Time: 10:48:06 AM
 * To change this template use Options | File Templates.
 */
public class ResultURLValidator implements Validator {
    public boolean validate(Object o) {
        URL url = (URL)o;
        Socket socket = null;
        InputStream urlStream = null;
        Reader reader = null;

        try {
            socket = new NokiaSocket(url.toString());
            urlStream = socket.getInputStream();
            for(int i = 0; i < 150000000L; i++);
            if(urlStream.available() != 405) {
                char [] cBuf = new char[100000];
                reader = new InputStreamReader(urlStream);
                if(reader.read(cBuf) > 25000) {
                    return true;
                }
            }
        } catch (Exception e) {
            Log.write(e);
        } finally {
            try { reader.close(); } catch (Exception e) { }
            try { urlStream.close(); } catch (Exception e) { }
            try { socket.close(); } catch (Exception e) { }
        }
        return false;
    }
}
