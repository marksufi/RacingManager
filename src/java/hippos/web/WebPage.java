package hippos.web;

import org.apache.commons.text.StringEscapeUtils;
import utils.HTMLParser;
import utils.Scand;

import java.io.InputStream;
import java.net.URL;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Mar 25, 2010
 * Time: 7:22:58 PM
 * To change this template use Options | File Templates.
 */
public class WebPage  {
    private BufferedReader bufferedReader;
    private InputStream inputStream;
    private InputStreamReader inputStreamReader;
    private URL url;
    private Iterator lines;
    private StringBuilder itrLine = new StringBuilder();

    public WebPage(String path) throws IOException {
        url =  new URL(path);
        inputStream = url.openStream();
        inputStreamReader = new InputStreamReader(inputStream);
        bufferedReader = new BufferedReader(inputStreamReader);
        lines = bufferedReader.lines().iterator();
        //super(inputStreamReader);
    }

    public WebPage(URL path) throws IOException {
        this.url =  path;
        inputStream = url.openStream();
        inputStreamReader = new InputStreamReader(inputStream);
        bufferedReader = new BufferedReader(inputStreamReader);
        lines = bufferedReader.lines().iterator();

        //super(new InputStreamReader(path.openStream()));
    }

    public void print() throws IOException{
        String line;
        while((line = bufferedReader.readLine()) != null) {
            System.out.println(line);
        }

    }

    public String SearchBlockLine(String tag, String content) {
        String line;

        try{
            while((line = bufferedReader.readLine()) != null) {
                line = Scand.parse(line);
                if(line.indexOf("<"+ tag) >= 0) {

                    if(content == null || line.contains(content)) {
                        return line;
                    }
                }
            }
        } catch (IOException ie) {
            ie.printStackTrace();
        }
        return null;
    }

    public String SearchBlockLine(String tag) {
        return SearchBlockLine(tag, null);
    }

    public String newReadBlock(String tag) {
        StringBuilder block = new StringBuilder();
        String startStr = "<" + tag;
        String endStr = "</" + tag;
        boolean sFound = false;
        boolean eFound = false;
        int startIndex = -1;
        int endIndex = -1;

        while (lines.hasNext()) {
            if(!sFound) {
                startIndex = itrLine.indexOf(startStr);
                if(startIndex >= 0) {
                    sFound = true;
                }
            } else {
                startIndex = 0;
            }

            endIndex = itrLine.indexOf(endStr);
            if(endIndex >= 0) {
                endIndex += itrLine.substring(endIndex).indexOf(">") + 1;
                eFound = true;
            } else {
                endIndex = itrLine.length();
            }

            if(sFound) {
                if(eFound) {
                    block.append(itrLine.substring(startIndex, endIndex));
                    itrLine.delete(startIndex, endIndex);

                    block.delete(0, block.indexOf(">") + 1);
                    block.delete(block.lastIndexOf("<"), block.length());

                    return StringEscapeUtils.unescapeHtml4(block.toString());
                }
                block.append(itrLine.substring(startIndex, endIndex));
            }

            itrLine = new StringBuilder((String)lines.next());
        }

        // block not found
        return null;
    }

    public String readBlock(String tag) {
        return HTMLParser.readBlock(lines, tag);
    }

    public String findBefore(String target, String end) {
        String line;

        try{
            while((line = bufferedReader.readLine()) != null) {
                line = StringEscapeUtils.unescapeHtml4(line);
                if(line.indexOf("<"+ target) >= 0) {
                    return line;
                } else if(line.indexOf(end) >= 0) {
                    break;
                }
            }

        } catch (IOException ie) {
            ie.printStackTrace();
        }
        return null;
    }

    public void close() {
        try {
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            url= null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readLine() throws IOException {
        return bufferedReader.readLine();
    }
}


