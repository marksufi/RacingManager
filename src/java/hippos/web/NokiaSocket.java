package hippos.web;

import utils.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by IntelliJ IDEA.
 * User: teantika
 * Date: Jul 15, 2003
 * AlphaNumber: 1:51:01 PM
 * To change this template use Options | File Templates.
 */
public class NokiaSocket extends Socket {

    public NokiaSocket(String url) throws IOException {
        super("172.23.70.170", 8080);
        //try {
            //System.out.println("NokiaSocket.NokiaSocket(" + url + ")");
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(getOutputStream()));
            writer.write("GET " + url);
            writer.newLine();
            writer.write("Host: www.hippos.fi");
            writer.newLine();
            writer.write("accept: text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,video/x-mng,image/png,image/jpeg,image/gif;q=0.2,text/css,*/*;q=0.1 " +
                    "");
            writer.newLine();
            writer.write("accept-charset: ISO-8859-1, utf-8;q=0.66, *;q=0.66");
            writer.newLine();
            writer.write("accept-language: en-us, en;q=0.50");
            writer.newLine();
            writer.write("user-agent: Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.0.1) Gecko/20021003");
            writer.newLine();
            writer.write("Proxy-Connection: keep-alive");
            writer.newLine();
            writer.newLine();
            writer.flush();
        //} catch (Exception e) {
        //    Log.write(e);
        //}
    }
}

