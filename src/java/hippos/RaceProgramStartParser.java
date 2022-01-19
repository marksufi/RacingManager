package hippos;

import hippos.exception.AbsentException;
import hippos.exception.DataObjectException;
import hippos.exception.OutOfHorsesException;
import hippos.exception.UnvalidStartException;
import hippos.io.FileParser;
import hippos.io.RaceProgramFile;
import hippos.io.RaceResultFile;
import hippos.utils.HorsesHelper;
import utils.HTMLParser;
import utils.Log;

import java.math.BigDecimal;
import java.sql.Connection;
import java.text.ParseException;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Nov 25, 2004
 * AlphaNumber: 3:50:34 PM
 * To change this template use Options | File Templates.
 */
public class RaceProgramStartParser implements FileParser {
    private RaceProgramStart raceProgramStart;
    private RaceProgramFile raceProgramFile;
    private Iterator lines;

    public RaceProgramStartParser(RaceProgramFile raceProgramFile, RaceResultFile raceResultFile, Iterator lines) throws ParseException {
        this.lines = lines;
        this.raceProgramFile = raceProgramFile;
        this.raceProgramStart = new RaceProgramStart(raceProgramFile);
        //this.raceProgramStart.setFileId(raceProgramFile.getName());
        //this.raceProgramStart.id = raceProgramStart.fileId.substring(0, raceProgramStart.fileId.length() - 4);
        this.raceProgramStart.setDate(raceProgramFile.getDate());

        if(raceResultFile != null) {
            RaceResultStart raceResultStart = raceResultFile.getRaceResultStart(raceProgramStart);
            raceProgramStart.setRaceResultStart(raceResultStart);
        }
    }

    public Object parse(Connection conn) throws UnvalidStartException {
        try {
            parseNumber();
            raceProgramStart.setId(raceProgramFile, raceProgramStart.getStartNumber());
            System.out.println(raceProgramStart.toString());
            parseHorses(conn);

        } catch (OutOfHorsesException e) {
            //System.out.print("");
        } catch (UnvalidStartException e) {
            throw e;
        } catch (Exception e) {
            Log.write(e);
            e.printStackTrace();
        }
        return raceProgramStart;
    }

    @Override
    public Object parse() throws Exception {
        return null;
    }

    /**
     * setValueA the raceProgNumber of this raceResultStart from the given line.
     *
     * <h3>1. lähtö  klo 15:00 Kylmäveriset tasoitusajo 2120 m Etus. Suom. synt. Hyv. enint. 4-v. 20 m. p. 100e, 20 m/ 500e, 40 m/ 1200e, 60 m/ 3000e. enintään 3000 e P. 1000 e.</h3>
     * <h3>1. lähtö  Kylmäveriset tasoitusajo 2120 m Etus. Suom. synt. Hyv. enint. 4-v. 20 m. p. 100e, 20 m/ 500e, 40 m/ 1200e, 60 m/ 3000e. enintään 3000 e P. 1000 e.</h3>
     * <h3>3. lähtö Lämminveriset Monte tasoitusajo 2140 m Winter Monté meeting -karsinta p. 10 000 e + 20 m/ 10 000 e. pt. 80 m. P. 700 e.</h3>
     * <h3>
     *     5. lähtö  klo 16:51 Lämminveriset ryhmäajo 2100 m enintään 16 000 e. P. 800 e. (800-400-240-160-80-80)
     * </h3>
     */
    private void parseNumber() throws UnvalidStartException {
        String line = null;

        try {
            while (lines.hasNext()) {
                line = HTMLParser.readBlock(lines, "h3", " lähtö ");

                //line = (String) lines.next();
                //System.out.println("RaceProgramStartParser.parseNumber(" + line + ")");
                if (line.indexOf("lähtö") >= 0) {
                    StringBuffer racemode = new StringBuffer();
                    StringTokenizer st = new StringTokenizer(line, "<[\t. ]>");

                    /*
                        Lähtönumero
                     */
                    String token = st.nextToken();
                    raceProgramStart.setStartNumber(new BigDecimal(token));

                    st.nextToken();
                    if (line.indexOf(" klo ") >= 0) {
                        st.nextToken(); //klo
                        st.nextToken(); //lähtöaika
                    }

                    /*
                        hevosen lajikirjain
                     */
                    token = st.nextToken().trim();
                    racemode.append(getHorseRaceId(token));
                    raceProgramStart.setHorseRace(token);

                    /*
                        Monte-tunnus, jos on
                     */

                    token = st.nextToken().trim();
                    if(token.contains("Monte")) {
                        racemode.append("m");
                        token = st.nextToken().trim();
                    }

                    /*
                        Lähtötavan tunnus
                     */
                    racemode.append(getRaceTypeId(token));
                    raceProgramStart.setRaceStartMode(token);

                    /*
                        Matkan pituuden tunnus
                     */
                    BigDecimal raceLength = new BigDecimal(st.nextToken("m").trim());
                    racemode.append(HorsesHelper.raceLengthId(raceLength));

                    raceProgramStart.addRaceLength(raceLength);
                    raceProgramStart.setRaceMode(racemode.toString());

                    return;
                }
            }
        } catch (UnvalidStartException e) {
            throw e;
        } catch (Exception e) {
            Log.write(e, line);
        }
    }

    private String getHorseRaceId(String token) {
        char fc = token.charAt(0);
        switch (fc) {
            case 'L':   return "L";
            case 'K':   return "K";
            case 'S':   return "K";
            default:    Log.write("RaceProgramStartParser.parseNumber - Unrecognizable horse race: " + token);
        }
        return "";
    }

    private String getRaceTypeId(String racetype) {
        if(racetype.contains("tasoitus"))
            return "";

        if(racetype.contains("ryhmä"))
            return "a";

        if(racetype.contains("linja"))
            return "a";

        Log.write("RaceProgramStartParser.getRaceTypeId - outo lähtötyyppi: " + racetype);

        return "";

    }

    /**
     * reads the mode, horseRace and raceLength variables from the given line
     *
     *      [Ryhm�ajo l�mminverisille    2100m, /enint.   7500 e    , 1. palkinto    1000 e]
     *      [ 	 ][ 	 ][ V4-01 ][ 	 ][ 	 ][ 	 ]
     *      [ Troikka ][ 	 ][ 	 ][ 	 ][ 	 ][ 	 ]
     *
     *
    private void parseForm() {
        while(lines.hasNext()) {
            String line = (String)lines.next();
            try {
                StringTokenizer st = new StringTokenizer(line, "[ \t]");
                raceProgramStart.setMode(st.nextToken().trim());
                raceProgramStart.setHorseRace(st.nextToken().trim());
                raceProgramStart.setRaceLength(new BigDecimal(st.nextToken("m").trim()));
                return;
            } catch (Exception e) {
            }
        }
    } */

    private void parseHorses(Connection conn) throws OutOfHorsesException {
        RaceProgramHorseParser raceProgramHorseParser = null;
        while(lines.hasNext()) {
            try {
                raceProgramHorseParser = new RaceProgramHorseParser(raceProgramStart, lines);
                RaceProgramHorse raceProgramHorse = null;
                try {
                    raceProgramHorse = (RaceProgramHorse) raceProgramHorseParser.parse(conn);

                    RaceResultHorse raceResultHorse = null;
                    if(raceProgramStart.getRaceResultStart() != null) {
                        raceResultHorse = raceProgramStart.getRaceResultStart().getRaceResultHorse(raceProgramHorse.getRaceHorseName());
                        if(raceResultHorse != null && raceResultHorse.present == false) {
                            throw new AbsentException();
                        }
                    }
                    raceProgramHorse.setRaceResultHorse(raceResultHorse);

                    raceProgramHorse.setStatistics(conn);

                    System.out.println(raceProgramHorse.toString());
                    raceProgramStart.add(raceProgramHorse);
                } catch (DataObjectException e) {
                    // Jotain meni parsimisessa pieleen, ei lisää hevosta lähtöön
                    Log.write(e);
                }
            } catch (AbsentException e) {
                //  Hevonen poissa
            }
        }
        //System.out.print("");
    }
}
