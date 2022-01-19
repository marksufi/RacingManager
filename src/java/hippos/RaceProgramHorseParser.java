package hippos;

import hippos.exception.*;
import hippos.io.FileParser;
import hippos.lang.stats.TimeStatistics;
import utils.HTMLParser;
import utils.Log;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Date;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Nov 25, 2004
 * AlphaNumber: 4:12:00 PM
 * To change this template use Options | File Templates.
 */
public class RaceProgramHorseParser implements FileParser {
    RaceProgramHorse raceProgramHorse;
    //RaceResultHorse raceResultHorse;
    Date raceDate;
    Iterator lines;
    private RaceProgramStart raceProgramStart;
    static BigDecimal defaultDistance;
    static BigDecimal defaultTrack;

    private RaceProgramHorseParser(Iterator lines) {
        this.lines = lines;
    }

    public RaceProgramHorseParser(RaceProgramStart raceProgramStart, Iterator lines) {
        this(lines);
        //racebookHorse = new ValueHorse(raceProgramStart);
        raceProgramHorse = new RaceProgramHorse(raceProgramStart);
        raceProgramHorse.setLid(raceProgramStart.id);
        raceDate = raceProgramStart.getDate();
        this.raceProgramStart = raceProgramStart;
        if(raceProgramStart.getRaceProgramHorses().isEmpty()) {
            defaultDistance = raceProgramStart.getRaceLength();
            defaultTrack = new BigDecimal(0);
        }
    }

    public Object parse(Connection conn) throws OutOfHorsesException, DataObjectException, AbsentException {
        try {
            // Etsii seuraavan hevosen aloituskohdan
            HTMLParser.readBlock(lines, "td", "program_divider");

            // Parsii tiedot
            while(lines.hasNext()) {
                //waitForNewHorse((String)lines.next());
                parseNumber();

                /*racebookHorse.hy = */
                parseFormLine();
                /*racebookHorse.hv = */
                parseFormLine();
                /*racebookHorse.he = */
                parseFormLine();

                parseNameLine();

                // vasta tässä vaiheessä pystyy hakemaan tuloksia
                if (raceProgramStart.getRaceResultStart() != null) {
                    raceProgramHorse.raceResultHorse = raceProgramStart.getRaceResultStart().getRaceResultHorse(raceProgramHorse.getRaceHorseName());

                    if (raceProgramHorse.raceResultHorse != null && raceProgramHorse.raceResultHorse.present == false) {
                        throw new AbsentException(raceProgramHorse.getId());
                    }
                }

                parseLength();
                parseRecords(new TimeStatistics(raceProgramHorse));
                parseAgeLine();
                parseDamLine();
                parseColorLine();
                parseOwnerLine();
                parseTraineeLine();
                parseJockeyLine();
                parseJockeyStatisticLine();
                parseJockeyYearStatisticLine();

                if(HarnessApp.useHorseRaceHistoryLink) {
                    if(!raceProgramHorse.existsInDatabase() || HarnessApp.debugMode == true) {
                        try {
                            RaceHorseHistoryParser raceHorseHistoryParser = new RaceHorseHistoryParser(raceProgramHorse);

                            raceProgramHorse = (RaceProgramHorse) raceHorseHistoryParser.parse(conn);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("RaceProgramHorseParser.parse: " + raceProgramHorse.getId());
                    }
                }

                break;
            }
        } catch (AbsentException e) {
            throw e;
        } catch (OutOfHorsesException e) {
            throw e;
        } catch (DataObjectException e) {
            throw e;
        } catch (Exception e) {
            Log.write(e);
            e.printStackTrace();
        }

        return this.raceProgramHorse;
    }

    @Override
    public Object parse() throws Exception {
        return null;
    }

    /**
     *
     * <h1>1</h1>
     *
     * @throws OutOfHorsesException
     */
    private void parseNumber() throws OutOfHorsesException, AbsentException {
        String td = HTMLParser.readBlock(lines, "td");
        String number = HTMLParser.readBlock(td, "h1");

        try {
            raceProgramHorse.setHorseProgNumber(new BigDecimal(number.strip()));
        } catch (Exception e) {
            throw new OutOfHorsesException();
        }

        raceProgramHorse.setId();
    }

    /**
     *   <td>yht:16 0-4-2</td>
     *   <td colspan="5">13:15 0-3-2   31,1    2.270 €</td>
     *   <td colspan="7">12:0 0-0-0        0 € </td>
     *
    private Form parseFormLine(Form form) throws IOException {
        String line;
        while(lines.hasNext()) {
            line = (String)lines.next();
            if(line.indexOf("<td") >=0) {
                line = line.substring(line.indexOf(">") + 1);
                line = line.substring(0, line.indexOf("<"));
                try {
                    form = new FormParser(this.raceProgramStart.getRaceLiteral()).parse(line);
                } catch (Exception e) {
                    throw new IOException(line);
                }
                return form;
            }
        }
        return form;
    }*/

    /**
     *  Käsiohjelman tilastot ovat virheellistä tietoa. Rivi vain etsitään ja luetaan,
     *  mutta ei parsita mitenkään.
     *
     *   <td>yht:16 0-4-2</td>
     *   <td colspan="5">13:15 0-3-2   31,1    2.270 €</td>
     *   <td colspan="7">12:0 0-0-0        0 € </td>
     *
     * @return palauttaa luetun rivin
     * @throws IOException
     */
    private String parseFormLine() {
        String form = HTMLParser.readBlock(lines, "td");

        return form;
    }

    /**
     *  Parsii nimen
     */
    private void parseNameLine() throws OutOfHorsesException, DataObjectException {
        //System.out.println("RaceProgramHorseParser.parseNameLine(" + line + ")");
        String line;
        while(lines.hasNext()) {
            // Etsii ja lukee seuraavan <td>-lohkon sisällön
            line = HTMLParser.readBlock(lines, "td", "getHorse");
            if(line != null) {
                try {
                    // Asettaa linkistä hevoselle rekisterinumeron
                    raceProgramHorse.setRegisterNumber(parseRegisterId(line));

                } catch (DataObjectException e) {

                    Log.write(e, this.raceProgramHorse.getId());
                }

                // Lukee td-lohkosta <a ref-lohkon sisällön
                line = HTMLParser.readBlock(line, "a");

                raceProgramHorse.setRaceHorseName(line.strip());

                if (raceProgramHorse.getRaceHorseName().isEmpty()) {
                    throw new DataObjectException("Horsename is empty: " + line);
                }
                return;
            } else {
                throw new DataObjectException("Could not find 'getHorse' keyword from horse name link: " + line);
            }
        }
        throw new OutOfHorsesException();
    }

    private String parseRegisterId(String line) throws DataObjectException {
        String registerIdStr = "";
        int startIndex = line.indexOf("?sp=");
        if(startIndex > 0) {
            int e = -1;
            registerIdStr = line.substring(startIndex + 4);
            //int endIndex = registerIdStr.indexOf("&amp;sp=X");
            //if(endIndex > 0) {
                registerIdStr = registerIdStr.substring(0, 20);
            if((e = registerIdStr.indexOf("&")) > 0) {
                registerIdStr = registerIdStr.substring(0, e);
            }
            //} else throw new DataObjectException("Horse register number parsing failed: " + line);
        } else throw new DataObjectException("Horse register number parsing failed: " + line);

        return registerIdStr;
    }

    private void parseLength() throws OutOfHorsesException, DataObjectException, AbsentException {

        String line = HTMLParser.readBlock(lines, "h2");

        if(line.length() > 0) {
            if(line.indexOf("POISSA") >= 0) {
                defaultTrack = defaultTrack.add(BigDecimal.ONE);
                throw new AbsentException(this.raceProgramHorse.getId());
            }
            StringTokenizer st = new StringTokenizer(line, ":");
            switch (st.countTokens()) {
                case 1:
                    raceProgramHorse.setRaceTrack(new BigDecimal(st.nextToken().trim()));
                    if(raceProgramHorse.getRaceTrack().compareTo(defaultTrack) <= 0) {
                        defaultDistance = defaultDistance.add(new BigDecimal("20"));
                    }
                    raceProgramHorse.setRaceLength(defaultDistance);
                    break;
                case 2:
                    raceProgramHorse.setRaceLength(new BigDecimal(st.nextToken().trim()));
                    defaultDistance = raceProgramHorse.getRaceLength();
                    raceProgramHorse.setRaceTrack(new BigDecimal(st.nextToken().trim()));
                    break;
                default:
                    throw new DataObjectException("not a raceLength:raceTrack data: " + line);
            }
            defaultTrack = raceProgramHorse.getRaceTrack();
            raceProgramHorse.setRaceMode(raceProgramStart.getRaceMode());
            raceProgramStart.addRaceLength(raceProgramHorse.getRaceLength());

            raceProgramHorse.setRaceHandicap(raceProgramStart);
            raceProgramHorse.initTrackId();

            return;
        }
        throw new OutOfHorsesException();
    }

    /**
     *  <td colspan="2"><h2></h2></td>
     *  <td colspan="3"><h2>31,1ke</h2></td>
     *  <td colspan="5"><h2>2.320 €</h2></td>
     *
     *  </h2></td>
     *  <td><h2>
     *      2100:1
     *  </h2></td>
     *  <td colspan="2"><h2>
     *      14,6aly
     *  </h2></td>
     *  <td colspan="3"><h2>
     *      17,4ke
     *  </h2></td>
     *  <td colspan="6"><h2>
     *      5.640 €
     *  </h2></td>
     */
    private TimeStatistics parseRecords(TimeStatistics form) {
        String line;

        line = HTMLParser.readBlock(lines, "h2");
        // virheellistä tietoa ei kannata tallettaa
        //form.addaRecord(new AlphaNumber(line));

        line = HTMLParser.readBlock(lines, "h2");
        // virheellistä tietoa ei kannata tallettaa
        //form.addtRecord(new AlphaNumber(line));

        line = HTMLParser.readBlock(lines, "h2");
        //line = line.replace(".", "");
        // virheellistä tietoa ei kannata tallettaa
        //form.setAwards(new AlphaNumber(line));

        return form;
    }





    /*
     * 9 v prt T
     * 4 v  O
     */
    private void parseAgeLine() throws AbsentException, DataObjectException {
        String line = HTMLParser.readBlock(lines, "td");

        try {
            StringTokenizer st = new StringTokenizer(line, " ");

            raceProgramHorse.age = new BigDecimal(st.nextToken());
            st.nextToken(); // v
            if(st.countTokens() >= 2) {
                // Väri saattaa puutua
                raceProgramHorse.color = st.nextToken();
            } else {
                System.out.println("RaceProgramHorseParser.parseAgeLine");
            }
            raceProgramHorse.sex = st.nextToken();

            /* <a href="/heppa/racing/RaceProgramOneRace,getSire.$DirectLink.sdirect;jsessionid=p6+QtwpVje2pJ3mNO95X1Q**.app3?sp=l8484551630642229556&amp;sp=X" onclick="return setStatusAndEnsure(selectText, this);" class="">
             *      Taikuri
             *      </a>
             */
            line = HTMLParser.readBlock(line, "a");
            raceProgramHorse.sire = line.strip();

            if(raceProgramHorse.sire.length() == 0) {
                Log.write("raceProgramHorse.sire of zero length: " + this.raceProgramHorse.getId());
            }
        } catch (Exception e) {
            Log.write("Failed to parse Age line for the racebook horse '" + this.raceProgramHorse.getId() + "': line = " + line);
            //throw new IOException(raceProgramHorse.getId());
        }
        parseSubStart(line);
    }

    /**
     * Set the dam and mothers' sire information and adds substart information if existsInDatabase
     *
     * 	<a href="/heppa/racing/RaceProgramOneRace,getDam.$DirectLink.sdirect;jsessionid=p6+QtwpVje2pJ3mNO95X1Q**.app3?sp=l4384465477373875123&amp;sp=X" onclick="return setStatusAndEnsure(selectText, this);" class="">
     *      Kaikun Kaija
     *   </a>
     *
     *  <a href="/heppa/racing/RaceProgramOneRace,getSireOfDam.$DirectLink.sdirect;jsessionid=p6+QtwpVje2pJ3mNO95X1Q**.app3?sp=l7437605342387421828&amp;sp=X" onclick="return setStatusAndEnsure(selectText, this);" class="">
     *      Kaarlon-Kaiku
     *  </a>
     *
     */
    private void parseDamLine() throws IOException, DataObjectException {
        String line = "";
        try {
            line = HTMLParser.readBlock(lines, "a", "getDam");
            raceProgramHorse.dam = line.strip();

            if(raceProgramHorse.dam.length() == 0) {
                Log.write("Could not find getDam. raceProgramHorse.dam of zero length: " + this.raceProgramHorse.getId());
            }

            line = HTMLParser.readBlock(lines, "a", "getSireOfDam");
            raceProgramHorse.sireOfDam = line.strip();

            if(raceProgramHorse.sireOfDam.length() == 0) {
                Log.write("Could not find getSireOfDam. raceProgramHorse.sireOfDam of zero length: " + this.raceProgramHorse.getId());
            }

        } catch (Exception e) {
            Log.write(e);
        }

        parseSubStart(line);
    }

    public void parseSubStart(String line) throws DataObjectException, AbsentException {
        try {
            if(HarnessApp.useHorseRaceHistoryLink == false || HarnessApp.debugMode == true) {
                SubStartParser subStartParser = new SubStartParser(lines, raceDate, raceProgramHorse);
                SubStart subStart = (SubStart)subStartParser.parse();

                System.out.println("RaceProgramHorseParser.parseSubStart: " + subStart.toString());
                if(HarnessApp.useHorseRaceHistoryLink == false) {
                    // Ottaa lähtölistojen startit käyttöön, jos historialinkkiä ei käytetä
                    raceProgramHorse.add(subStart);
                }
            } else {
                // Etsii rivin loppukohdan
                while(line.indexOf("</tr>") < 0 && lines.hasNext()) {
                    line = (String) lines.next();
                }
            }

        } catch (SubStartMissingException se) {
            // do nothing
        } catch (UnvalidStartException ue) {
            // Tarkista jatkuuko ohjemla tästä oikein
            System.out.println("RaceProgramHorseParser.parseSubStart: " + ue.toString());

        }
    }

    /**
     * Set the color information and adds substart information if existsInDatabase
     *
     * <td>vihr musta - vihr</td>
     *
     * @param line string containing the required information
     * f.e. "[VALK MUSTA PUN - VALK][C Mayr][T ][5.8.  ][3 ][2100/11][22,9a          ][11             ][309 ]"
     * @throws IOException if line format doesn't match
     */
    private void parseColorLine() throws IOException {
        try {
            String line = HTMLParser.readBlock(lines, "td");
            StringTokenizer ct = new StringTokenizer(line, "-");

            if (ct.hasMoreTokens()) raceProgramHorse.color1 = ct.nextToken().trim();
            if (ct.hasMoreTokens()) raceProgramHorse.color2 = ct.nextToken().trim();

            parseSubStart(line);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    /**
     * Reads the owner and location information
     *
     ****Esimerkkki 1
     *  <td>
     *
     * 		<a href="/heppa/racing/RaceProgramOneRace,getOwnersName.$DirectLink.sdirect;jsessionid=lbCRfokeu9uHCsIqqv4VTg**.app3?sp=l8844053822602338770&amp;sp=SGROUP&amp;sp=X" onclick="setStatusText(selectText)" class="">
     * 						Team Press Photo
     * 		</a>
     * ,&nbsp;Vehkataipale
     * 	                    </td>
     **** Esimerkki 2
     * 	<td>Team Kaalimaan Vartijat,&nbsp;Sulkava</td>
     *
     ****
     * @throws IOException if line format doesn't match
     */
    private void parseOwnerLine() throws IOException {
        StringBuilder sbLine = new StringBuilder(HTMLParser.readBlock(lines, "td"));
        try {
            if(sbLine != null) {
                if (sbLine.toString().contains("getOwnersName")) {
                    String owner = HTMLParser.readBlock(sbLine, "a");
                    if(owner != null) {
                        raceProgramHorse.setOwner(owner.strip());
                    }
                    if (sbLine.indexOf(",") > 0) {
                        raceProgramHorse.setOwnerLocation(sbLine.substring(sbLine.indexOf(",") + 1).strip());
                    }
                } else {
                    if (sbLine.indexOf(",") > 0) {
                        raceProgramHorse.setOwner(sbLine.substring(0, sbLine.indexOf(",")).strip());
                        raceProgramHorse.setOwnerLocation(sbLine.substring(sbLine.indexOf(",") + 1).strip());
                    }
                }
            }
            parseSubStart("");
        } catch (Exception e) {
            throw new IOException(this.raceProgramStart.getId() + ":" + sbLine);
        }
    }

    /**
     * Reads the trainer information and adds substart information if existsInDatabase
     *
     *  <a href="/heppa/racing/RaceProgramOneRace,getTrainersName.$DirectLink.sdirect;jsessionid=p6+QtwpVje2pJ3mNO95X1Q**.app3?sp=l6963760377956648714&amp;sp=SPERSON&amp;sp=X" onclick="setStatusText(selectText)" class="">
     *      Mia Käri
     *  </a>
     *
     *  */
    private void parseTraineeLine() throws IOException {
        String line = HTMLParser.readBlock(lines, "td");
        try {
            if(line != null) {
                if(line.contains("getTrainersName")) {
                    line = HTMLParser.readBlock(line, "a");
                }
                if(line != null && !line.strip().isEmpty()) {
                    // Valmentajatieto voi puuttua, jos kuljettaja on myös valmentaja
                    raceProgramHorse.setCoach(line.strip());
                }
            }

            parseSubStart(line);
        } catch (Exception e) {
            throw new IOException(line);
        }
    }

    /**
     * Set the raceDriverName and raceDriverName class information and adds substart information if existsInDatabase
     *
     * 		<a href="/heppa/racing/RaceProgramOneRace,getDriverName.$DirectLink.sdirect;jsessionid=p6+QtwpVje2pJ3mNO95X1Q**.app3?sp=l1008590282525332929&amp;sp=SPERSON&amp;sp=X" onclick="setStatusText(selectText)" class="original_driver">
     *          S Markkula
     *      </a>
     *      (aA)</td>
     *
     *      <td> <a href="/heppa/racing/RaceProgramOneRace,getDriverName.$DirectLink.sdirect?sp=l5965274316357593994&amp;sp=SPERSON&amp;sp=X" class="original_driver" onclick="setStatusText(selectText)"> Kim Åkerlund
     * </a>
     * (A)</td>
     */
    private void parseJockeyLine() throws IOException {
        try {
            String line = HTMLParser.readBlock(lines, "td", "getDriverName");
            if(line != null) {
                String sDriver = HTMLParser.readBlock(line, "a");
                String driverClass = null;
                if(line.contains("(")) {
                    driverClass = line.substring(line.lastIndexOf("(") + 1);
                    driverClass = driverClass.substring(0, driverClass.indexOf(")"));
                }
                RaceProgramDriver raceProgramDriver = new RaceProgramDriver(sDriver.strip());

                if(driverClass != null) {
                    if(driverClass.length() < 4) {
                        raceProgramDriver.getDriverForm().setJockeyClass(driverClass);
                    } else {
                        Log.write("Too long driverClass: " + driverClass + ": " + raceProgramHorse.getId());
                    }
                }

                raceProgramHorse.setRaceProgramDriver(raceProgramDriver);
            } else {
                System.out.println("RaceProgramHorseParser.parseJockeyLine");
            }

            if (raceProgramHorse.getCoach() == null || raceProgramHorse.getCoach().toString().length() == 0) {
                raceProgramHorse.setCoach(formatName(raceProgramHorse.getRaceProgramDriver().toString()));
            }

            parseSubStart(line);
        } catch (Exception e) {
            Log.write(e, raceProgramHorse.getId());
            throw new IOException();
        }
    }

    /**
     * Set the total statistics of the raceDriverName and adds substart information if existsInDatabase
     *
     * @param line string containing the required information
     * f.e. "[Yht:31478 3877-3310-2940   8.958.423 e][H Koivunen][P ][19.12.][3 ][2100/ 4][23,7a          ][10             ][*52 ]"
     * @throws IOException if line format doesn't match
     */
    //private void parseJockeyStatisticLine() throws IOException {
    private void parseJockeyStatisticLine() throws IOException {
        String line = "";
        try {
            String form = HTMLParser.readBlock(lines, "td");
            /*
            while(lines.hasNext()) {
                line = (String)lines.next();
                if(line.indexOf("<td>") >=0) {
                    line = HTMLParser.clean(line);
                    //raceProgramHorse.getDriver().setTotalForm(new FormParser().parse(line));
                    break;
                }
            }*/

            parseSubStart(line);
        } catch (Exception e) {
            Log.write(e, line);
            //throw new IOException("'" + line + "'");
        }
    }

    /**
     * Set the year statistics of the raceDriverName and adds substart information if existsInDatabase
     *
     * @param line string containing the required information
     * f.e. "[04 :   48    6-   5-   1      13.150 e][To Salo][T ][30.12.][8 ][2100/ 2][21,1a          ][7              ][160 ]"
     * @throws IOException if line format doesn't match
     */
    //private void parseJockeyYearStatisticLine() throws IOException {
    private void parseJockeyYearStatisticLine() {
        String line= "";
        try {
            String form = HTMLParser.readBlock(lines, "td");

            /*
            while(lines.hasNext()) {
                line = (String)lines.next();
                if(line.indexOf("<td>") >=0) {
                    line = HTMLParser.clean(line);
                    //raceProgramHorse.getDriver().setYearForm( new FormParser().parse(line));
                    break;
                }
            }*/

            parseSubStart(line);
        } catch (Exception e) {
            Log.write(e, line);
            //throw new IOException("'" + line + "'");
        }
    }

    /**
     * Formats input string to a namecase format where first chars of the raceHorseName are uppercase and other lowercase
     *
     * @param unformalName unformatted string
     * @return A string with a namecase format
     */
    public static String formatName(String unformalName) {
        //System.out.println("Horse.formatName(" + unformalName + ")");
        unformalName = unformalName.toLowerCase();
        StringBuffer formalName = new StringBuffer();
        StringTokenizer s = new StringTokenizer(unformalName, " -", true);
        while(s.hasMoreTokens()) {
            String p = s.nextToken();
            formalName.append(Character.toUpperCase(p.charAt(0)));
            if(p.length() > 1)
                formalName.append(p.substring(1));
        }
        //System.out.println("-> formalName: " + formalName);
        return formalName.toString();
    }

    public RaceProgramStart getRaceProgramStart() {
        return raceProgramStart;
    }
}