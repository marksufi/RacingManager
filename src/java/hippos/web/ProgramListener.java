package hippos.web;

import hippos.HarnessApp;
import hippos.io.RaceFile;
import hippos.io.RaceProgramDirectory;
import hippos.io.RaceResultDirectory;
import hippos.utils.FileUtils;
import utils.HTMLParser;
import utils.Log;
import utils.Scand;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Sep 1, 2004
 * AlphaNumber: 8:21:27 PM
 * To change this template use Options | File Templates.
 */
public class ProgramListener {
    RaceResultDirectory raceResultDir;
    RaceProgramDirectory raceDir;

    public ProgramListener() throws FileNotFoundException {
        raceResultDir =  new RaceResultDirectory();
        raceDir = new RaceProgramDirectory();
    }

    private static boolean isValidRace(RaceFile raceFile) {
        String name = raceFile.getName();
        String ccNumber = name.substring(name.indexOf("CC") + 2);
        ccNumber = ccNumber.substring(0, ccNumber.indexOf("."));
        if(Integer.parseInt(ccNumber) < 20) {
            return true;
        }
        return false;
    }


    private static List readOneRaceLinks(String raceLink) {
        List oneRaceLinks = new ArrayList();

        try {
            String line;
            WebPage raceLinkPage = new WebPage(raceLink);
            while((line = raceLinkPage.readLine()) != null) {
                if(line.indexOf("RaceProgramOneRace") > 0) {
                    String oneRaceLink = HTMLParser.parseLineTag(line, "href");
                    if(oneRaceLink != null) {
                        oneRaceLink = HarnessApp.HipposUrl + oneRaceLink;
                        //System.out.println("ProgramListener.readOneRaceLinks:" + oneRaceLink);
                        oneRaceLinks.add(oneRaceLink);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return oneRaceLinks;  //To change body of created methods use File | Settings | File Templates.
    }

    private List getRaceLinks(String raceCalendarUrl, List results, List races) {
        //System.out.println("ProgramListener.getRaceLinks(" +  raceCalendarEarliestYearUrl+ ")");

        boolean isValid = true;
        boolean isCancelled = false;
        try {
            String block;
            WebPage hipposCalendarPage = new WebPage(raceCalendarUrl);
            while((block = hipposCalendarPage.newReadBlock("tr")) != null) {

                StringBuilder sbBlock = new StringBuilder(block.strip());

                HTMLParser.readBlock(sbBlock, "td"); // Pvm
                String klo = HTMLParser.readBlock(sbBlock, "td"); // Klo tai PERUTTU

                if(klo != null && !klo.contains("PERUTTU")) {
                    HTMLParser.readBlock(sbBlock, "td"); // Rata
                    HTMLParser.readBlock(sbBlock, "td"); // Tyyppi
                    BigDecimal ratano = new BigDecimal(HTMLParser.readBlock(sbBlock, "td")); // Ratano.

                    if (ratano.intValue() > 0) {
                        String line;
                        while ((line = HTMLParser.readBlock(sbBlock, "td")) != null) {
                            if (line.indexOf("FRaceResults") > 0) {
                                String resultsLink = HTMLParser.parseLineTag(line, "href");
                                if (resultsLink != null) {
                                    resultsLink = HarnessApp.HipposUrl + resultsLink;
                                    results.add(resultsLink);
                                }
                            }

                            if (line.indexOf("FRaceProgramMain") > 0) {
                                line = line.substring(line.indexOf('\"') + 1);
                                line = line.substring(0, line.indexOf('\"'));
                                line = Scand.parse(line);
                                String racelink = HarnessApp.HipposUrl + line;

                                races.add(racelink);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.write(e);
        }
        return results;  //To change body of created methods use File | Settings | File Templates.
    }
/*
    private List getRaceLinks(String raceCalendarUrl, List results, List races) {
        //System.out.println("ProgramListener.getRaceLinks(" +  raceCalendarEarliestYearUrl+ ")");

        boolean isValid = true;
        boolean isCancelled = false;
        try {
            String line;
            WebPage raceCalendarEarliestYear = new WebPage(raceCalendarUrl);
            while((line = raceCalendarEarliestYear.readLine()) != null) {
                if(line.indexOf("Poni") > 0 || line.indexOf("Koe") > 0) {
                    isValid = false;
                    continue;
                }

                if(line.indexOf("PERUTTU") > 0) {
                    isCancelled = true;
                    continue;
                }

                if(line.indexOf("FRaceFields") > 0) {
                    isValid = true;
                    continue;
                }

                if(line.indexOf("FRaceResults") > 0) {
                    if(isValid || isCancelled) {
                        String resultsLink = HTMLParser.parseLineTag(line, "href");
                        if(resultsLink != null) {
                            resultsLink = HarnessApp.HipposUrl + resultsLink;
                            results.add(resultsLink);
                        }
                    }
                    isValid = true;
                    isCancelled = false;
                }

                if(line.indexOf("FRaceProgramMain") > 0) {
                    if(isValid) {
                        line = line.substring(line.indexOf('\"')+1);
                        line = line.substring(0, line.indexOf('\"'));
                        line = Scand.parse(line);
                        String racelink = HarnessApp.HipposUrl + line;

                        races.add(racelink);
                    }
                    isValid = true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return results;  //To change body of created methods use File | Settings | File Templates.
    }*/

    /**
     * https://heppa.hippos.fi/heppa/app?page=racing%2FRaceResults&service=external&sp=CF1420927200000&sp=CEKT
     *
     * @param resultsLink
     *      https://heppa.hippos.fi/heppa/app?page=racing%2FRaceResults&service=external&sp=CF1435957200000&sp=CEL
     *      https://heppa.hippos.fi/heppa/app?page=racing%2FRaceResults&service=external&sp=CF1419199200000&sp=CES
     *      https://heppa.hippos.fi/heppa/app?page=racing%2FRaceResults&service=external&sp=CF1424210400000&sp=CEH
     *
     * @return
     *      https://heppa.hippos.fi/heppa/app?page=racing%2FRaceResults&service=external&sp=CEL&sp=CF1423692000000&sp=CC0
     *      https://heppa.hippos.fi/heppa/app?page=racing%2FRaceResults&service=external&sp=CEL&sp=CF1435957200000&sp=CC0
     */
    private URL updateResultLink(String resultsLink) throws MalformedURLException {
        String part1 = resultsLink.substring(0, resultsLink.indexOf("&sp="));
        String part2 = resultsLink.substring(resultsLink.indexOf("&sp="), resultsLink.lastIndexOf("&sp="));
        String part3 = resultsLink.substring(resultsLink.lastIndexOf("&sp="));

        return new URL(part1 + part3 + part2 + "&sp=CC0");
    }


    /**
     * Creates a race link from the result link
     *
     * @param resultLink
     *          https://heppa.hippos.fi/heppa/app?page=racing%2FRaceResults&service=external&sp=CF1435957200000&sp=CEL
     *          https://heppa.hippos.fi/heppa/app?page=racing%2FRaceResults&service=external&sp=CF1369170000000&sp=CEO
     *
     * @return
     *          https://heppa.hippos.fi/heppa/app?page=racing%2FRaceProgramMain&service=external&sp=CF1369170000000&sp=CEO&sp=CC0
     */
    private static String createRaceLink(String resultLink) {
        //System.out.println("ProgramListener.createRaceLink(" + resultLink + ")");

        int ind1 = resultLink.indexOf("&sp=");
        int ind2 = resultLink.indexOf("&sp=", ind1 + 4);

        String tag1 = resultLink.substring(ind1 + 4, ind2);
        String tag2 = resultLink.substring(ind2 + 4);

        String raceLink = HarnessApp.RaceProgramMainUrl + tag1 + "&sp=" + tag2;

        return raceLink;  //To change body of created methods use File | Settings | File Templates.
    }

    /**
     * Etsii Hippoksen sivuilta uusia käsiohjelma ja tulossivuja ja luo niistä tiedostot
     *
     * @param newRaceFiles
     *          Uudet käsiohjelmat
     *
     * @param newResultFiles
     *          Uudet tulokset
     */
    public void createProgramFiles(String raceCalendarUrl, List newRaceFiles, List newResultFiles, List newFiles) {
        System.out.println("\nEtsii hippoksen sivuilta uusia käsiohjelma- ja tulostiedostoja");
        try {
            List results = new ArrayList();
            List races = new ArrayList();

            getRaceLinks(raceCalendarUrl, results, races);

            Iterator iResults = results.iterator();
            while(iResults.hasNext()) {
                String resultLink = (String)iResults.next();
                URL resultURL = new URL(resultLink);
                RaceFile resultFile = raceResultDir.createFile(resultURL);
                String resultFileName = resultFile.getName();

                boolean resultsFileExists;
                if( resultFile.isValid() && (!(resultsFileExists = resultFile.exists()) || newFiles.contains(resultFileName))) {
                        //strURL = FileUtils.read( resultURL );
                    String strURL = FileUtils.read(updateResultLink(resultLink));
                    FileUtils.write(strURL, resultFile);
                    newResultFiles.add(resultFile);
                    if(!resultsFileExists)
                        System.out.println("Uusi tulostiedosto haettu: " + resultFile);
                    else
                        System.out.println("Vanha tulostiedosto päivitetty: " + resultFile);

                    if(!newFiles.contains(resultFile.getName())) {
                        newFiles.add(resultFile.getName());
                    }

                    String raceLink = createRaceLink(resultLink);
                    List oneRaceLinks = readOneRaceLinks(raceLink);
                    Iterator iOneRace = oneRaceLinks.iterator();
                    while( iOneRace.hasNext()) {
                        String oneRaceLink = (String)iOneRace.next();
                        URL raceURL = new URL(oneRaceLink);
                        RaceFile raceFile = raceDir.createFile( raceURL );
                        boolean raceFileExists = raceFile.exists();

                        /**
                         * Päivittää myös olemassaolevat uusien tuloksien lähtölistat poissaolijoiden
                         * päivittämiseksi.
                         */
                        if( isValidRace( raceFile ) ) {

                            strURL = FileUtils.read( raceURL );
                            FileUtils.write(strURL, raceFile);
                            newRaceFiles.add(raceFile);

                            if(!newFiles.contains(raceFile.getName())) {
                                newFiles.add(raceFile.getName());
                            }

                            if(!raceFileExists)
                                System.out.println("Uusi käsiohjelma haettu: " + raceFile);
                            else
                                System.out.println("Vanha käsiohjelma päivitetty: " + raceFile);
                        }
                    }
                }
            }

            /* Lisää uudet käsiohjelmat tiedostoiksi */
            Iterator iRaces = races.iterator();
            while(iRaces.hasNext()) {
                String raceLink = (String)iRaces.next();
                List oneRaceLinks = readOneRaceLinks(raceLink);
                Iterator iOneRace = oneRaceLinks.iterator();
                while(iOneRace.hasNext()) {
                    String oneRaceLink = (String)iOneRace.next();
                    RaceFile raceFile = raceDir.createFile(new URL(oneRaceLink));
                    boolean raceFileExists = raceFile.exists();
                    if(isValidRace(raceFile) && raceFile.isValid()) {
                        raceFile.write(new URL(oneRaceLink));
                        newRaceFiles.add(raceFile);
                        if(!raceFileExists)
                            System.out.println("NEW RACE FILE CREATED: " + raceFile);
                        else
                            System.out.println("OLD RACE FILE UPDATED: " + raceFile);

                        if(!newFiles.contains(raceFile.getName())) {
                            newFiles.add(raceFile.getName());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    /**
     * Hakee Hippoksen sivuilta käsiohjelman uudestaa
     *
     * @param filename haettava käsiohjelma
     */
    public void updateProgramFile(String filename) throws UnknownHostException {
        try {
            String filesplit [] = filename.split("_");
            StringBuffer sb = new StringBuffer();
            sb.append(HarnessApp.HipposUrl + "/heppa/app?page=racing%2FRaceProgramOneRace&service=external&sp=");
            sb.append(filesplit[0]);
            sb.append("&sp=");
            sb.append(filesplit[1]);
            sb.append("&sp=");
            sb.append(filesplit[2].substring(0, filesplit [2].indexOf(".")));

            URL raceURL = new URL(sb.toString());
            RaceFile raceFile = raceDir.createFile(filename);
            FileUtils.write(raceURL, raceFile);
            System.out.println("EXISTING RACE FILE: " + raceFile + " UPDATED");
        } catch (UnknownHostException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

