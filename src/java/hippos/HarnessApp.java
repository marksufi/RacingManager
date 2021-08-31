package hippos;

import hippos.database.Database;
import hippos.exception.DatabaseException;
import hippos.exception.RacesCancelledException;
import hippos.exception.UnvalidStartException;
import hippos.io.*;
import hippos.lang.Pointer;
import hippos.lang.stats.Form;
import hippos.lang.toto.TotoEngine;
import hippos.lang.toto.Voittajapeli;
import hippos.math.betting.GameFactory;
import hippos.math.regression.HipposUpdatingRegression;
import hippos.utils.DateUtils;
import hippos.utils.HipposProperties;
import hippos.web.ProgramListener;
import hippos.web.ResultListener;
import utils.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * PROGRAM_FILE_PATH = C:/Users/Markus/Documents/Filebase/hippos/ohjelma/
 * Raviohjelman pääluokka
 *
 * kuljettajamuutoksen jälkeen:
 * Hits:   21.21%
 * Profit: -13087.4�/56347 = -0.23�/startti
 *
 * Ennen kuljettajamuutosta
 * Hits:   20.84%
 * Profit: -14233.9�/56068 = -0.25�/startti
 *
 */
public class HarnessApp {
    public static final boolean includeConstant = true;
    // Constants
    private static HarnessApp instance = null;
    public static final String raceCalendarEarliestYearUrl = HipposProperties.get("RACE_CALENDAR_EARLIEST_YEAR");
    public static final String raceCalendarEarlierYearUrl = HipposProperties.get("RACE_CALENDAR_EARLIER_YEAR");
    public static final String RaceResultsUrl = HipposProperties.get("RaceResultsUrl");
    public static final String RaceProgramMainUrl = HipposProperties.get("RaceProgramMainUrl");
    public static final String HipposUrl = HipposProperties.get("HipposUrl");
    public static final String dataPath = HipposProperties.get("DATA_FILE_PATH");
    public static final int trustLimit = 100;
    public static final boolean useNr = true;
    public static final boolean useKp = true;

    /**
     * Käytetäänkö hevosten vertailussa lähtörataa avainarvona
     */
    public static final boolean useTrackKey = false;

    /**
     * Haetaanko uudet käsiohjelma- ja tulostiedostot Hippoksen sivuilta
     */
    public static boolean fetchNewFiles = false;

    /**
     * Luetaako hevosen aiemmat startit Hippoksen kilpailuhistoria-sivuilta
     */
    public static final boolean useHorseRaceHistoryLink = true;

    /**
     * Parsitaanko myös lähtölistan taulu? jos true, niin parsitaan
     */
    public static boolean debugMode = false;

    /**
     * Määrittää kumpaa hakemistoa käytetään, Filebase vai TestBase
     */
    public static final boolean testMode = false;

    /*
     * Parsitaako uudet tiedostot ainoastaan ja talletetaan tietokantaan, ei tehdä vihjeitä ollenkaan.
     * Nopeuttaa uusien ohjelma- ja tulostiedostojen tallentamista tietokantan.
     */
    private static final boolean parseOnly = false;


    public static final double WEEKDIFF_MAX = 5.0;

    public static RaceProgramDirectory raceProgramDirectory;
    public static GameDirectory gameDirectory;
    public static RaceResultDirectory raceResultDirectory;

    public static TotoEngine totoEngine = new TotoEngine();

    public static final int REGSIZE = 7;

    public static TreeMap<String, HipposUpdatingRegression> linregMap = new TreeMap<>();

    //public static TreeMap<String, Form> driverFullYearFormPool = new TreeMap();
    public static TreeMap<String, Form> driverRaceTypePool = new TreeMap();

    public static HarnessApp getInstance() throws FileNotFoundException {
        if(instance == null) {
            instance = new HarnessApp();
        }
        return instance;
    }

    public static boolean isRunning() {
        return instance != null ? true : false;
    }

    /**
     * Alustaa, ja tarkastaa yhden lähdön
     *
     * @param conn
     * @param lid   Lähdön id
     * @param date  Lähdön päivämäärä
     */
    private RaceProgramStart initialize(Connection conn, String lid, java.sql.Date date) {
        RaceProgramStart raceProgramStart = null;

        try {
                /*
                    Yrittää hakea lähdön tulokset tietokannasta
                 */
                RaceResultStart raceResultStart = null;
                try {
                    raceResultStart = new RaceResultStart(conn, lid, date);
                } catch (DatabaseException de) {
                    // RaceResultStart not in database
                }


                /*
                    Hakee käsiohjelman lähtö ja hevokohtaiset tiedot, aiempiem lähtöjen tilastot ja
                    ja lähdöt tietokannasta.
                 */
                raceProgramStart = new RaceProgramStart(conn, lid, date, raceResultStart);

                raceProgramStart.initValueHorses(linregMap);

                /**
                 *  Jos lähtölistalle löytyy tulokset, niin päivittää toto-pelien tiedot.
                 *  Jos ei löydyy, niin tulostaa vihjeet
                 */
                if(raceResultStart != null) {
                        totoEngine.submit(raceProgramStart);
                        totoEngine.check(raceResultStart);
                        System.out.println(totoEngine.toString());

                        raceResultStart.updateStatistics();

                        raceProgramStart.learn();
                }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return raceProgramStart;
    }

    private void initialize(Connection conn, TreeMap<String, HipposUpdatingRegression> linregMap, TotoEngine totoEngine, java.sql.Date limitDate) {
        Pointer pointer = new Pointer("Initialising Effects");
        pointer.start();

        PreparedStatement raceStmt = null;
        ResultSet raceSet = null;

        try {
            raceStmt = conn.prepareStatement(getRaceStatement(limitDate));
            raceSet = raceStmt.executeQuery();

            while (raceSet.next()) {
                String lid = raceSet.getString("ID");
                java.sql.Date date = raceSet.getDate("PVM");
                //String raceMode = raceSet.getString("LYHENNE");

                initialize(conn, lid, date);

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { raceSet.close();} catch (SQLException e) { e.printStackTrace(); }
            try { raceStmt.close();} catch (SQLException e) { e.printStackTrace(); }
            pointer.interrupt();
        }
    }

    private void parseNewFiles(List newFiles, Connection conn) throws IOException {
        System.out.println("HarnessApp.parseNewFiles");

        Iterator fileItr = newFiles.iterator();
        List parsedProgramFiles = new ArrayList();
        RaceResultFile raceResultFile = null;
        String oldDate = null;

        while(fileItr.hasNext()) {
            String filename = (String)fileItr.next();
            if(filename.indexOf("_") < filename.lastIndexOf("_")) {
                RaceProgramFile raceProgramFile = null;
                RaceResultStart raceResultStart = null;
                try {
                    raceProgramFile = (RaceProgramFile) raceProgramDirectory.createFile(filename);
                    String newdate = filename.substring(2, filename.indexOf("_"));

                    if(oldDate == null || !newdate.equals(oldDate)) {
                        //driverFullYearFormPool = new TreeMap<>();
                        driverRaceTypePool = new TreeMap<>();
                        oldDate = newdate;
                    }

                    String resultFilename = filename.substring(0, filename.lastIndexOf('_')) + ".php";

                    if (raceProgramFile.exists() && raceProgramFile.isValid()) {
                        System.out.println("HarnessApp.parseNewFiles: " + raceProgramFile.getAbsolutePath());

                        /**
                         * Tarkistaa löytyykö lähtölistalle tulostiedostoa, jos löytyy niin parsii sen
                         * ja tallettaa tietokantaan.
                         */
                        if (raceResultFile == null || !raceResultFile.getName().equals(resultFilename)) {
                            raceResultFile = (RaceResultFile) raceResultDirectory.createFile(resultFilename);
                            if(raceResultFile.exists()) {
                                raceResultFile = (RaceResultFile) raceResultFile.parse();
                                //if (!raceResultFile.existsInDatabase(conn)) {
                                    raceResultFile.insert(conn);
                                //}
                            } else {
                                raceResultFile = null;
                            }
                        }

                        /* Parsii uuden ohjelmatiedon */
                        RaceProgramFileParser raceProgramFileParser = new RaceProgramFileParser(raceProgramFile, raceResultFile, conn);
                        raceProgramFile = (RaceProgramFile) raceProgramFileParser.parse();
                        raceProgramFile.insert(conn);

                        RaceProgramStart raceProgramStart = raceProgramFile.getRaceProgramStart();

                        if(HarnessApp.parseOnly == false) {

                            raceProgramStart = initialize(conn, raceProgramStart.getId(), DateUtils.toSQLDate(raceProgramStart.getDate()));

                            if (!raceProgramStart.getValueHorseSet().isEmpty()) {
                                GameFactory gameFactory = new GameFactory(raceProgramStart, conn);
                                GameFile gameFile = gameDirectory.createFile((raceProgramFile));

                                gameFile.write(raceProgramStart, gameFactory);
                                //System.out.println(raceProgramFile.toString());
                                //System.out.println(gameFactory.toString());
                            } else {
                                Log.write("Empty programfile: " + filename);
                            }
                        }

                        if (raceProgramStart.getRaceResultStart() != null) {

                            //totoEngine.check(raceResultStart);
                            //System.out.println(totoEngine.toString());

                            raceProgramFile.update(raceResultFile, conn);

                        }

                        conn.commit();

                    } else {
                        System.out.println("HarnessApp.parseNewFiles: " + raceProgramFile.getName());
                    }
                } catch (RacesCancelledException re) {
                    // Ravit peruttu
                    System.out.println("HarnessApp.parseNewFiles: races cancelled");
                } catch (UnvalidStartException e) {
                    // 5. lähtö  klo 18:44 Yhdistetty tasoitusajo 1640 m Avoin GLi Fire ’n Ice, Kylmäveriset vs. Lämminveriset P. 4000 e. (4000-2000-1200-800-400-400)
                } catch (Exception e) {
                    try { conn.rollback(); } catch (SQLException e1) { e1.printStackTrace(); }
                    Log.write( new Exception( raceProgramFile.getName(), e));
                }
            }
            System.out.print(""); // stop here if don't  want to loose data
        }
        //Log.write(GameFactory.toBettingsString(conn));
    }

    private String getRaceStatement(java.sql.Date limitDate) {
        StringBuffer sb = new StringBuffer();
        sb.append("select LAHTO.id, LAHTO.lyhenne, PROGRAMFILE.pvm ");
        sb.append("from LAHTO, PROGRAMFILE ");
        sb.append("where LAHTO.filename = PROGRAMFILE.filename ");
        sb.append("order by LAHTO.id");

        return sb.toString();
    }

    public static void main(String args[]) {
        long startTime = System.currentTimeMillis();
        Connection conn = null;
        List newFiles = new Vector();

        try {
            /* avaa tietokantayhteyden */
            conn = Database.getConnection();

            raceProgramDirectory = new RaceProgramDirectory();
            gameDirectory = new GameDirectory();
            raceResultDirectory = new RaceResultDirectory();

            /**
             * Puhdistaaa logitiedoston
             */
            Log.clear();

            if(!HarnessApp.isRunning()) {
                HarnessApp app = HarnessApp.getInstance();
                if (HarnessApp.fetchNewFiles) {
                    // Edellinen cuori
                    //app.haeUudetTiedostot(raceCalendarEarliestYearUrl, conn);

                    // Kuluva vuosi
                    app.haeUudetTiedostot(raceCalendarEarlierYearUrl, conn);
                }

                /* hakee uudet käsiohjelmat joita ei ole vielä tietokannassa */
                newFiles = raceProgramDirectory.listNewFiles(conn, newFiles);

                /* hakee uudet tulokset joita ei ole vielä tietokannassa */
                //newFiles  = raceResultDirectory.listNewFiles(conn, newFiles);

                Collections.sort(newFiles, new FileComparator());

                if(!newFiles.isEmpty()) {
                    String firstFile = (String) newFiles.get(0);
                    long dateasnumber = Double.valueOf(firstFile.substring(2, 15)).longValue();
                    java.sql.Date limitDate = new java.sql.Date(dateasnumber);

                    if(HarnessApp.parseOnly == false) {
                        // Alustaa pelit
                        totoEngine.add(new Voittajapeli("Voittaja yhdellä"));

                        app.initialize(conn, linregMap, totoEngine, limitDate);
                    }

                    app.parseNewFiles(newFiles, conn);

                    if(HarnessApp.parseOnly == false) {
                        Log.write(totoEngine.toString());
                    }
                }
            } else {
                System.out.println("You can have only one instance of this application running");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.write(e);
        } finally {
            System.out.println("HarnessApp.main: " + (System.currentTimeMillis() - startTime) / 1000 + " seconds");
            try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    /**
     * Hakee Hippoksen sivuilta uudet käsiohjelma- ja tulostiedostot
     *
     * @param raceCalendarEarlierYearUrl
     *      Linkki luettavaan Hippoksen kantaan
     *
     * @param conn
     *      Tietokantayhteys
     */
    private void haeUudetTiedostot(String raceCalendarUrl, Connection conn) {
        try {
            ProgramListener programListener = new ProgramListener();
            ResultListener resultListener = new ResultListener();

            List newResultFiles = new ArrayList();
            List newRaceFiles = new ArrayList();
            List newFiles = new Vector();

            /* hakee hippoksen sivuilta käsiohjelma- ja tulostiedostot */
            newFiles = raceProgramDirectory.listResultlessFiles(conn);
            //newFiles = raceResultDirectory.listResultFiles(newFiles);


            programListener.createProgramFiles(raceCalendarUrl, newRaceFiles, newResultFiles, newFiles);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
/*
Hits:   20.85%
        Profit: -15207.6�/57024 = -0.27�/startti
*/

/*
Hits:   20.95%
Profit: -15946.3�/57101 = -0.28�/startti

 */

