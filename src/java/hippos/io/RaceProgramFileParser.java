package hippos.io;

import hippos.HarnessApp;
import hippos.RaceProgramStart;
import hippos.RaceProgramStartParser;
import hippos.database.Database;
import hippos.exception.UnvalidStartException;
import utils.HTMLParser;
import utils.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.rmi.UnexpectedException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import static hippos.HarnessApp.debugMode;
import static hippos.HarnessApp.raceProgramDirectory;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Feb 26, 2005
 * AlphaNumber: 9:56:48 PM
 * To change this template use Options | File Templates.
 */
//public class RaceProgramFileParser implements FileParser {
public class RaceProgramFileParser {
    private RaceProgramFile raceProgramFile;
    private RaceResultFile raceResultFile;
    private Connection conn;

    public RaceProgramFileParser(RaceProgramFile raceProgramFile, Connection conn) {
        this.raceProgramFile = raceProgramFile;
        this.raceResultFile = null;
        this.conn = conn;
    }

    public RaceProgramFileParser(RaceProgramFile raceProgramFile, RaceResultFile raceResultFile, Connection conn) {
        this.raceProgramFile = raceProgramFile;
        this.raceResultFile = raceResultFile;
        this.conn = conn;

    }

    /*
    public RaceProgramFileParser(RaceProgramFile raceProgramFile) throws FileNotFoundException {
        this.raceProgramFile = raceProgramFile;
    }*/

    public Object parse() throws UnvalidStartException {
        try {
            HTMLParser reader = new HTMLParser(raceProgramFile);
            List lineList = reader.getLines();
            Iterator lines = lineList.iterator();
            parseLocality(lines);
            RaceProgramStartParser raceProgramStartParser = new RaceProgramStartParser(raceProgramFile, raceResultFile, lines);
            RaceProgramStart raceProgramStart = (RaceProgramStart)raceProgramStartParser.parse();
            //raceProgramStart.setValues();
            raceProgramFile.setRaceProgramStart(raceProgramStart);
            return raceProgramFile;
        } catch (UnvalidStartException e) {
            throw e;
        } catch (Exception e) {
            Log.write(e, raceProgramFile.toString());
        }
        return null;
    }

    /**
     *      [Kaustinen 27.8.2004][Tulokset]
     *      Kaustinen            27. 8.2004
     *
     * */
    private void parseLocality(Iterator lines) throws IOException {
        //setTrackRowStats();

        while(lines.hasNext()) {
            String line = (String)lines.next();
            //System.out.println("RaceProgramFileParser.parseLocality:" + line);
            if(line.indexOf("Käsiohjelma") >= 0) {
                //System.out.println("RaceProgramFileParser.parseLocality:" + line);
                StringTokenizer st = new StringTokenizer(line, "[ \t.,]");

                st.nextToken(); // <title>Käsiohjelma
                int d = Integer.parseInt(st.nextToken());
                int m = Integer.parseInt(st.nextToken());
                int y = Integer.parseInt(st.nextToken());
                raceProgramFile.setDate(y, m, d);

                raceProgramFile.getLocality().setLongLocality(st.nextToken());
                //raceProgramFile.getLocality().loadTrackRowStats(conn);

                return;
            }
        }
    }

    public static void main(String [] args) {
        final String filename = "CF1361829600000_CETK_CC5.php";

        try {
            HarnessApp.debugMode = true;

            Connection conn = Database.getConnection();
            raceProgramDirectory = new RaceProgramDirectory();

            RaceProgramFile raceProgramFile = (RaceProgramFile) raceProgramDirectory.createFile(filename);
            RaceProgramFileParser raceProgramFileParser = new RaceProgramFileParser(raceProgramFile, conn);
            raceProgramFile = (RaceProgramFile)raceProgramFileParser.parse();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
