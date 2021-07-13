package utils;

import org.apache.commons.text.StringEscapeUtils;
import utils.html.Tag;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Jul 21, 2004
 * AlphaNumber: 11:50:21 AM
 * To change this template use Options | File Templates.
 */
public class HTMLParser extends FileReader {
    List content = new Vector();

    public static String removeBlock(String line, String tag) {
        StringBuffer sb = new StringBuffer();
        if(line.contains(tag)) {
            int is = line.indexOf("<" + tag);
            String ss = is > 0 ? line.substring(0, is ) : "";
            sb.append(ss);

            line = line.substring(is);

            String endTag = "</" + tag;
            int ie = line.indexOf(endTag);
            if(ie > 0) {
                line = line.substring(ie);

                int end = line.indexOf(">");
                sb.append(end > line.length() ? line.substring(end + 1) : "");
            }
        }

        return sb.toString();

    }

    public Vector getContent() { return (Vector) content; }

    public HTMLParser(Socket s) throws IOException {
        super(new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
        try {
            StringBuffer textContent = new StringBuffer();
            String line;
            while((line = super.readLine()) != null) {
                //line = StringEscapeUtils.unescapeHtml4(line);
                textContent.append(Scand.parse(line));
            }

            parseContent(textContent.toString());
        } catch (IOException e) {
            Log.write(e);
        }
    }

    public HTMLParser(InputStream inputStream) throws IOException {
        super(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        try {
            StringBuffer textContent = new StringBuffer();
            String line;
            while((line = super.readLine()) != null) {
                //line = StringEscapeUtils.unescapeHtml4(line);
                textContent.append(Scand.parse(line));
            }
            parseContent(textContent.toString());
        } catch (IOException e) {
            Log.write(e);
        }
    }

    public HTMLParser(File f) throws IOException {
        super(f);
        String line;

        try {
            while ((line = super.readLine()) != null) {
                lines.add(Scand.parse(line));
            }
            ;
        } catch (Exception e) {
            Log.write(e, f.getName());
        }
    }


    public String toString() {
        Iterator i = content.iterator();
        StringBuffer str = new StringBuffer();
        while(i.hasNext()) {
            str.append(i.next().toString());
            str.append("\n");
        }
        //System.out.println(str.toString());
        return str.toString();
    }

    private void parseContent(String s) {
        StringTokenizer tagTokenizer = new StringTokenizer(s, "<>", true);
        String line = new String();
        Tag tag = null;
        while(tagTokenizer.hasMoreElements()) {
            String t = tagTokenizer.nextToken();
            if(t.equals("<")) {
                tag = new Tag(tagTokenizer.nextToken());
                content.add( tag );
                if(tag.getName().equals("td"))
                    line += "[ ";
                else if (tag.getName().equals("/td"))
                    line += " ]";

                if (tag.isNewLine()) {
                    lines.add(line);
                    line = new String();
                }
            } else if(!t.equals(">")) {
                String str = Scand.parse(t);
                content.add( str );
                if (tag != null && !tag.isTitle()) {
                    line += str;
                }

            }
        }
    }

    public static String parseLineTag(String line, String tag) {

        if(line.indexOf(tag) >= 0) {
            String _line = line;

            _line = _line.substring(_line.indexOf(tag));
            _line = _line.substring(_line.indexOf("=") +1);
            _line = _line.substring(_line.indexOf("\"") +1);
            _line = _line.substring(0, _line.indexOf("\""));

            return Scand.parse(_line);
        }

        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    /**
     * Etsii ja kokoaa seuraavan html-lohkon sisällön yhdeksi jonoksi, mutta ei sisällä itse tagia.
     *
     * @param bufferedReader    Lähde
     * @param tag               Lohkon tunniste
     *
     * @return  Lohkon sisältö merkkojonona ilman tunnistetta, tai null, jos tunnistetta ei löytynyt
     */
    public static String readBlock(BufferedReader bufferedReader, String tag) {
        return readBlock(bufferedReader, tag, null);

    }

    /**
     * Etsii ja kokoaa seuraavan html-lohkon sisällön yhdeksi jonoksi, mutta ei sisällä itse tagia.
     *
     * @param bufferedReader    Lähde
     * @param tag               Lohkon tunniste
     * @param searchString      Etsittävä lisämerkkijono
     *
     * @return  Lohkon sisältö merkkojonona ilman tunnistetta, tai null, jos tunnistetta ei löytynyt
     */
    public static String readBlock(BufferedReader bufferedReader, String tag, String searchString) {

        Iterator lines = bufferedReader.lines().iterator();

        return readBlock(lines, tag, searchString);
        /*
        StringBuffer sb = new StringBuffer();
        boolean found = false;

        try {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                //line = StringEscapeUtils.unescapeHtml4(line);
                line = Scand.parse(line);
                if (line.contains("<" + tag)) {
                    if(searchString == null || line.contains(searchString)) {
                        found = true;
                        line = line.substring(line.indexOf("<" + tag));
                        line = line.substring(line.indexOf(">"));
                        line = line.replaceFirst(">", "");
                    }
                } if (line.contains("</" + tag) && found) {
                    line = line.substring(0, line.indexOf("</" + tag));
                    sb.append(line);
                    return sb.toString();
                } if (found) {
                    sb.append(line);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
        */
    }

    /**
     * Etsii ja parsii tagin rivijonosta
     *
     * @param lines tivi-iteraattori, joista tagia haetaan
     * @param tag   haettava tagi
     *
     * @return tagin sisällön. Jos ei löytynyt niin tyhjän merkkijonon.
     */
    public static String readBlock(Iterator lines, String tag) {

        return readBlock(lines, tag, null, null);
    }

    /**
     * Etsii tagin lisäksi myös merkkinoa, ja lukee lohkon sisällön
     *
     * @param lines
     * @param tag
     * @param searchString
     *
     * @return lohkon sisältö
     */
    public static String readBlock(Iterator lines, String tag, String searchString) {
        return readBlock(lines, tag, searchString, null);
    }

    /**
     * Etsii tagin lisäksi myös merkkinoa, ja lukee lohkon sisällön
     *
     * @param lines
     * @param tag
     * @param searchString
     *
     * @return lohkon sisältö
     */
    public static String readBlock(Iterator lines, String tag, String searchString, String endString) {
        StringBuffer content = new StringBuffer();
        StringBuffer block = new StringBuffer();
        boolean found = false;

        try {
            while (lines.hasNext()) {
                String line = (String) lines.next();
                line = StringEscapeUtils.unescapeHtml4(line);

                if (line.contains("<" + tag)) {
                    // tagi löytynyt
                    found = true;

                    line = line.substring(line.indexOf("<" + tag));
                    block.append(line);
                    line = line.substring(line.indexOf(">"));
                    line = line.replaceFirst(">", "");
                } if (line.contains("</" + tag) && found) {
                    line = line.substring(0, line.indexOf("</" + tag));
                    content.append(line);
                    if(searchString == null || block.toString().contains(searchString)) {
                        // Hakuehdot täyttyvät, poistuu
                        break;
                    }

                    // Hakuehdot eivät tättynee, jatkaa etsimistä seuraavaan lohkoon
                    content = new StringBuffer();
                    block = new StringBuffer();
                    found = false;

                } if (found) {
                    content.append(line);
                    block.append(line);
                } if (endString != null && line.contains(endString)) {
                    return line;
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return found ? content.toString() : null;
    }

    /*
    public static String readBlock(Iterator lines, String tag, String searchString, String endString) {
        StringBuffer sb = new StringBuffer();
        boolean found = false;

        try {
            while (lines.hasNext()) {
                String line = (String) lines.next();
                if (line.contains("<" + tag)) {
                    if(searchString == null || line.contains(searchString)) {
                        found = true;
                        line = line.substring(line.indexOf("<" + tag));
                        line = line.substring(line.indexOf(">"));
                        line = line.replaceFirst(">", "");
                    }
                }

                if (line.contains("</" + tag) && found) {
                    line = line.substring(0, line.indexOf("</" + tag));
                    sb.append(line);
                    break;
                }

                if (found) {
                    sb.append(line);
                }

                if(line.contains(endString)) {
                    return line;
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return found ? sb.toString() : null;
    }*/

    /**
     * Etsii ja parsii tagin rivistä
     *
     * @param line  rivi, josta tagia haetaan
     * @param tag   haettava tagi
     *
     * @return tagin sisältö
     */
    public static String readBlock(StringBuilder line, String tag) {
        StringBuffer sb = new StringBuffer();
        StringBuffer cleanLine = new StringBuffer();
        boolean found = false;

        try {
            String block = line.toString();

            int blockStartIndex = -1;
            if ((blockStartIndex = line.indexOf("<" + tag)) >= 0) {
                block = block.substring(block.indexOf("<" + tag));
                block = block.substring(block.indexOf(">"));
                block = block.replaceFirst(">", "");

                if(blockStartIndex > 0) {
                    cleanLine.append(line.substring(0, blockStartIndex));
                }

                found = true;
            }

            int blockEndIndex = -1;
            String endTag = "</" + tag + ">";

            if ((blockEndIndex = line.indexOf("</" + tag)) >= 0 && found) {
                //blockEndIndex += blockStartIndex;
                blockEndIndex += endTag.length();

                block = block.substring(0, block.indexOf("</" + tag));

                if(blockEndIndex < line.length()) {
                    cleanLine.append(line.substring(blockEndIndex));
                }
            }
            if (found) {
                sb.append(block);
            }

            if (found) {
                line.delete(blockStartIndex, blockEndIndex);
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
        return found ? StringEscapeUtils.unescapeHtml4(sb.toString()) : null;
    }

    /**
     * Etsii ja parsii tagin rivistä
     *
     * @param line  rivi, josta tagia haetaan
     * @param tag   haettava tagi
     *
     * @return tagin sisältö
     */
    public static String readBlock(String line, String tag) {
        StringBuffer sb = new StringBuffer();
        boolean found = false;

        if(line != null) {
            try {
                if (line.indexOf("<" + tag) >= 0) {
                    line = line.substring(line.indexOf("<" + tag));
                    line = line.substring(line.indexOf(">"));
                    line = line.replaceFirst(">", "");

                    found = true;
                }

                if (line.indexOf("</" + tag) >= 0 && found) {
                    line = line.substring(0, line.indexOf("</" + tag));
                }
                if (found) {
                    sb.append(line);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return found ? sb.toString() : null;
    }

    public static void main(String args[]) {

        ArrayList lineList = new ArrayList();
        lineList.add("<td rowspan=\"10\">");
        lineList.add("  <h1>");
        lineList.add("      1");
        lineList.add("  </h1>");
        lineList.add("</td>");
        lineList.add("");
        lineList.add("<td>yht:7 4-1-1</td>");
        lineList.add("<td colspan=\"5\">21:1 0-0-0   33,1    0 €</td>");
        lineList.add("<td colspan=\"7\">20:6 4-1-1 30,7a 32,2    4.500 €</td>");

        Iterator iLines = lineList.iterator();
        System.out.println(HTMLParser.readBlock(iLines, "h1").strip());
        System.out.println(HTMLParser.readBlock(iLines, "td"));
        System.out.println(HTMLParser.readBlock(iLines, "td"));
        System.out.println(HTMLParser.readBlock(iLines, "td"));
    }

}
