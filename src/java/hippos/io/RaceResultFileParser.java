package hippos.io;

import hippos.RaceResultStart;
import hippos.RaceResultStartParser;
import hippos.exception.FileFormatException;
import hippos.exception.OutOfStartsException;
import hippos.exception.RacesCancelledException;
import hippos.exception.UnvalidStartException;
import utils.HTMLParser;
import utils.Log;

import java.io.IOException;
import java.sql.Connection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Oct 30, 2005
 * AlphaNumber: 2:58:29 AM
 * To change this template use Options | File Templates.
 */
public class RaceResultFileParser implements FileParser {
    HTMLParser reader;
    List lineList;
    Iterator lines;
    RaceResultFile raceResultFile;

    public RaceResultFileParser(RaceFile resultFile) {
        this.raceResultFile = (RaceResultFile)resultFile;
    }

    public Object parse() throws RacesCancelledException {
        try {
            reader = new HTMLParser(raceResultFile);
            lineList = reader.getLines();
            lines = lineList.iterator();

            parsePlaceAndDate();
            parseStarts();

        } catch (RacesCancelledException e) {
            throw e;
        } catch (Exception e) {
            Log.write(e, raceResultFile.getName());
        }

        return raceResultFile;
    }

    @Override
    public Object parse(Connection conn) throws Exception {
        return null;
    }

    /**
     *  <h2><span class="page_header">Tulokset, 1.1.2013 15:00 Pori</span></h2>
     *
     *  entinen:
     *      [Kaustinen 27.8.2004][Tulokset]
     *      Kaustinen            27. 8.2004
     *
     */
    private void parsePlaceAndDate() {
        try {
            while(lines.hasNext()) {
                String line = HTMLParser.readBlock(lines, "h2");
                if(line.indexOf("Tulokset") >= 0) {
                    //System.out.println("RaceResultFileParser.parsePlaceAndDate: " + line);

                    line = line.substring(line.indexOf("Tulokset"));

                    if(line.contains("<"))
                        line = line.substring(0, line.indexOf("<"));

                    StringTokenizer st = new StringTokenizer(line, "[ ,.");
                    st.nextToken(); // Tulokset

                    int d = Integer.parseInt(st.nextToken());
                    int m = Integer.parseInt(st.nextToken());
                    int y = Integer.parseInt(st.nextToken());

                    raceResultFile.setDate(y, m, d);

                    st.nextToken(); // klo
                    raceResultFile.setLongLocality(st.nextToken());
                    return;
                }
            }
        } catch (Exception e) {
            throw e;
        }
    }

    private void parseStarts() throws RacesCancelledException {
        while(lines.hasNext()) {
            try {
                RaceResultStartParser raceResultStartParser = new RaceResultStartParser(raceResultFile);
                RaceResultStart raceResultStart = (RaceResultStart) raceResultStartParser.parse(lines);
                raceResultFile.add(raceResultStart);
            } catch (RacesCancelledException e) {
                throw e;
            } catch (OutOfStartsException e) {
                parseVolumes(lines);
                parseTrackCondition(lines);
            } catch (UnvalidStartException e) {
                e.printStackTrace();
                /*
                    Ei ole vielä aika parsia volyymejä. Koelähdöt yleensä lopussa,
                    mutta yhdistetty lähtöjä voi olla keskellä raveja
                 */
                //parseVolumes(lines);
                //parseTrackCondition(lines);
            } catch (Exception e) {
                Log.write(e);
            }
        }

    }

    private void parseVolumes(Iterator lines) {
        String pelimuoto = null;
        String vaihto = null;
        String kerroin = null;
        StringBuffer td = new StringBuffer();
        boolean tdOpen = false;
        boolean begin = false;

        try {
            while (lines.hasNext()) {
                String line = (String) lines.next();
                if (line.indexOf("Ravipäivän toto-tiedot") >= 0) {
                    begin = true;
                }
                if (begin) {
                    if (line.indexOf("<td") >= 0 || tdOpen) {
                        td.append(line);
                        tdOpen = true;
                    }
                    if (line.indexOf("</td>") >= 0 && tdOpen) {
                        String ln = td.toString().trim();

                        int i = ln.indexOf('>');
                        int j = ln.lastIndexOf('<');
                        if (i > 0 && j > 0) {
                            ln = ln.substring(i + 1, j).trim();

                            if (pelimuoto == null) {
                                pelimuoto = ln;
                            } else if (vaihto == null) {
                                vaihto = ln;
                            } else if (kerroin == null) {
                                kerroin = ln;
                                this.raceResultFile.addPelimuoto(pelimuoto, vaihto, kerroin);
                            }
                        }
                        tdOpen = false;
                        td = new StringBuffer();
                    }
                }
                if (line.indexOf("Kokonaistotovaihto") >= 0) {
                    return;
                }
            }
        } catch (Exception e) {
            Log.write(e);
        }
    }

    private void parseTrackCondition(Iterator lines) {
        String line;
        int ind1, ind2;

        try {
            while (lines.hasNext()) {
                line = (String) lines.next();
                if ((ind1 = line.indexOf("trackCondition")) >= 0) {
                    line = line.substring(ind1);
                    ind1 = line.indexOf(">");
                    ind2 = line.indexOf("<");
                    if (ind1 > -1 && ind2 > -1) {
                        raceResultFile.setTrackCondition(line.substring(ind1 + 1, ind2));
                    }
                    return;
                }
            }
        } catch (Exception e) {
            Log.write(e);
        }
    }

}
