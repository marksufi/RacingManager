package hippos;

import hippos.exception.*;
import hippos.io.RaceResultFile;
import hippos.math.AlphaNumber;
import hippos.math.betting.BetRate;
import hippos.math.racing.SectionalTime;
import hippos.utils.HorsesHelper;
import utils.HTMLParser;
import utils.Log;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.*;

public class RaceResultStartParser {
    RaceResultStart raceResultStart;
    RaceResultFile raceResultFile;
    Connection conn;

    public RaceResultStartParser(RaceResultFile raceResultFile, Connection conn) {
        this.raceResultFile = raceResultFile;
        this.conn = conn;
        this.raceResultStart = new RaceResultStart(raceResultFile);
    }

    public Object parse(Iterator lines) throws RacesCancelledException, OutOfStartsException, UnvalidStartException {
        try {
            parseStartData(lines);

            String line;
            while((line = HTMLParser.readBlock(lines, "td", "raceResultPlacing", "</div>")) != null) {
                if(line.contains("</div>"))
                    break;

                BigDecimal raceResultPlacing = null;
                if(line.contains(".")) {
                    // Hevoselta löytyy sijoitus, hylätyillä ei ole
                    line = line.strip();
                    line = line.substring(0, line.indexOf('.'));
                    raceResultPlacing = new BigDecimal(line);
                }
                RaceResultHorseParser parser = new RaceResultHorseParser(raceResultStart, conn, raceResultPlacing);

                RaceResultHorse raceResultHorse = (RaceResultHorse)parser.parse(lines);
                if(raceResultHorse.present) {
                    raceResultStart.add(raceResultHorse);
                } else {
                    raceResultStart.add(raceResultHorse);
                }
            }

            /*
                Asettaa hevosille tasoituksen, ei voi asettaa muualla kuin nyt lopussa, koska
                tulostiedostossa lähdön esittelyssä matka usein virheellinen
             */
            for(RaceResultHorse raceResultHorse : raceResultStart.getRaceResultHorses()) {
                raceResultHorse.setRaceHandicap(raceResultStart);
                raceResultHorse.initTrackId();
            }

            parsePartTimes(lines);
            parseBetRates(lines);

        } catch (RacesCancelledException e) {
            throw e;
        } catch (OutOfStartsException e) {
            throw e;
        } catch (OutOfHorsesException e) {
            //System.out.print("");
        } catch (UnvalidStartException e) {
            throw e;
        } catch (Exception e) {
            Log.write(e, this.raceResultFile.getId());
        }

        return raceResultStart;
    }

    /**
     * Initializes a raceResultStart with values at line
     *
     * <h3>1. lähtö  klo 15:00 Kylmäveriset tasoitusajo 2120 m Etus. Suom. synt. Hyv. enint. 4-v. 20 m. p. 100e, 20 m/ 500e, 40 m/ 1200e,
     * <h3>1. ponilähtö A-Ponit tasoitusajo 1060 m KIINTEISTÖHUOLTO MIKKILÄ TMI p. 3.20.0 + 20 m/ 4,0 s P. 100 e.</h3>
     * <h3>1. koelähtö  klo 17:20 Yhdistetty tasoitusajo 2100 m Avoin P. 0 e.</h3>
     * <h3>1. nuoret-lähtö  klo 17:00 Yhdistetty (nuoret) tasoitusajo 2100 m Avoin P. 300 e. (300-200-100-100-100-100)</h3>
     *
     * @throws IOException if the line syntax does not match
     */
    //private void parseStartData(Iterator lines) throws OutOfStartsException, UnvalidStartException, RacesCancelledException, FileFormatException {
    private void parseStartData(Iterator lines) throws FileFormatException, OutOfStartsException, UnvalidStartException, RacesCancelledException {
        try {
            while (lines.hasNext()) {
                String line = HTMLParser.readBlock(lines, "h3", "lähtö", "Ravipäivä on peruttu");
                if (line != null && line.contains(" lähtö")) {
                    StringTokenizer st = new StringTokenizer(line, " \t.");
                    StringBuffer racemode = new StringBuffer();
                    String token;

                    /*
                        Lähdön numero
                     */
                    token = st.nextToken().strip();
                    raceResultStart.setStartNumber(new BigDecimal(token));

                    /*
                        Lähtötieto
                     */
                    String startType = st.nextToken().strip();
                    if (startType == null || startType.toLowerCase().trim().indexOf("lähtö") < 0) {
                        throw new FileFormatException("lähtö word missing:\n " + raceResultFile.getName() + "\n" + line);
                    }

                    if (startType.indexOf("poni") >= 0) {
                        throw new UnvalidStartException();
                    }
                    if (startType.indexOf("opetus") >= 0) {
                        throw new UnvalidStartException();
                    }
                    if (startType.indexOf("koelähtö") >= 0 && raceResultStart.getStartNumber().compareTo(new BigDecimal("20")) < 0) {
                        raceResultStart.setStartNumber(raceResultStart.getStartNumber().add(BigDecimal.valueOf(20)));
                    }
                    if (startType.indexOf("nuoret") >= 0 && raceResultStart.getStartNumber().compareTo(new BigDecimal("20")) < 0) {
                        raceResultStart.setStartNumber(raceResultStart.getStartNumber().add(BigDecimal.valueOf(50)));
                    }

                    /*
                        Kellonaika
                     */

                    token = st.nextToken().strip();
                    if (token.contains("klo")) {
                        token = st.nextToken().strip();
                        token = st.nextToken().strip();
                    }

                    racemode.append(getHorseRaceId(token));
                    raceResultStart.setHorseRace(token);

                    token = st.nextToken();
                    if (token.contains("nuoret")) {
                        token = st.nextToken();
                    }

                    StringBuffer mode = new StringBuffer();
                    if (token.contains("Monte")) {
                        racemode.append("m");
                        mode.append(token);
                        mode.append("/");
                        token = st.nextToken();
                        mode.append(token);
                    }

                    racemode.append(getRaceTypeId(token));

                    /*
                    if (raceResultStart.setMode(mode.toString()) == null) {
                        Log.write("Cannon set raceResultHorse horseRace mode for " + raceResultStart.id + ":" + line);
                    }*/

                    /*
                        Matkan pituus
                     */
                    token = st.nextToken().strip();
                    BigDecimal raceLength = new BigDecimal(token);
                    racemode.append(HorsesHelper.raceLengthId(raceLength));

                    raceResultStart.setRaceMode(racemode.toString());
                    raceResultStart.addRaceLength(raceLength);
                    raceResultStart.setId(raceResultFile, raceResultStart.getStartNumber());

                    return;

                }
                if (line != null && line.indexOf("Ravipäivä on peruttu") >= 0) {
                    throw new RacesCancelledException();
                }
            }
            throw new OutOfStartsException();
        } catch (FileFormatException e) {
            throw e;
        } catch (OutOfStartsException e) {
            throw e;
        } catch (UnvalidStartException e) {
            throw e;
        } catch (RacesCancelledException e) {
                throw e;
        } catch (Exception e) {
            Log.write(e);
            throw e;
        }
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

    private String getHorseRaceId(String token) {
        try {
            char fc = token.charAt(0);
            switch (fc) {
                case 'L':
                    return "L";
                case 'K':
                    return "K";
                case 'S':
                    return "K";
                default:
                    Log.write("RaceResultStartParser.parseNumber - Unrecognizable horse race: " + token);
            }
        } catch (Exception e) {
            Log.write(e);
        }
        return "";
    }

    /**
     * Parses raceResultStart raceProgNumber string
     *
     * @param str
     *      1.
     *      6.
     * @return the decimal raceProgNumber of the raceResultStart raceProgNumber
     */
    private BigDecimal parseNumber(String str) {
        //System.out.println("RaceResultStartParser.parseNumber(" + str + ")");
         return new BigDecimal(str.substring(0, str.indexOf(".")));
    }


    /**
     * Searches and parses parttime row if found
     *
     * <label>Väliajat (lämminveriset):</label>
     *      27,0/Jaro's Domaine 27,0/Firebolt Fasett 24,0 26,0
     *
     * <label>Väliajat (suomenhevoset):</label>
     *       Ei väliaikoja.
     *
     * 15,5/Supertoy's Jen19,5/Grainfield Oz 15,0  21,5
     *
     * 33,5/Tähden Leevi 32,5 29,5 31,5/Tähti Kepu
     *
     * 12,5/Scarlet Collection14,5/Crossover 16,5  19,0
     *
     */

    private void parsePartTimes(Iterator lines) {
        String line = new String();

        try {
            StringBuilder sbLine = new StringBuilder(HTMLParser.readBlock(lines, "p"));

            HTMLParser.readBlock(sbLine, "label");

            line = sbLine.toString();
            line = line.strip();

            if(!line.contains("Ei väliaikoja")) {

                StringBuffer sbTime = new StringBuffer();
                StringBuffer sbName = new StringBuffer();

                for(char c : line.toCharArray()) {
                    if(Character.isDigit(c) || c == ',') {
                        if(sbTime.length() > 2
                                && Character.isDigit(sbTime.charAt(sbTime.length() - 1))
                                && sbTime.charAt(sbTime.length() - 2) ==',') {

                                // Tallentaa edellisen ajan ja alustaa uuden
                                String time = sbTime.toString().replace(',', '.');
                                String horse = sbName.toString().strip();

                                AlphaNumber racemodeTime = new AlphaNumber(new BigDecimal(time), this.raceResultStart.getRaceMode());
                                SectionalTime sectionalTime = new SectionalTime(horse, racemodeTime);
                                raceResultStart.add(sectionalTime);

                                sbTime = new StringBuffer();
                        }
                        sbTime.append(c);
                    } else if(c == '/') {
                        // alustaa uuden nimen
                        sbName = new StringBuffer();
                    } else {
                        // lisää merkin nimeen
                        sbName.append(c);
                    }
                }


                // Viimeiset 500 metriä voittajahevoselle
                List winnerHorses = (List) raceResultStart.getOrderMap().get(BigDecimal.ONE);

                if(winnerHorses != null && sbTime.length() > 0) {
                    String time = sbTime.toString().replace(',', '.');
                    RaceResultHorse winnerHorse = (RaceResultHorse) winnerHorses.get(0);
                    AlphaNumber racemodeTime = new AlphaNumber(new BigDecimal(time), this.raceResultStart.getRaceMode());
                    SectionalTime sectionalTime = new SectionalTime(winnerHorse.getRaceHorseName(), racemodeTime);
                    raceResultStart.add(sectionalTime);
                }

                Iterator <SectionalTime> itr = raceResultStart.getSectionalTimes().iterator();

                /*
                    Lisää väliajat niille hevosille jotka
                 */
                try {
                    SectionalTime sTime1 = raceResultStart.getSectionalTimes().get(0);
                    String horse1 = sTime1.getHorse();
                    raceResultStart.getRaceResultHorse(horse1).setVA_1(sTime1.getHorseSplit().getNumber());

                    SectionalTime sTime2 = raceResultStart.getSectionalTimes().get(1);
                    String horse2 = sTime2.getHorse();
                    raceResultStart.getRaceResultHorse(horse2).setVA_2(sTime2.getHorseSplit().getNumber());

                } catch (IndexOutOfBoundsException e) {
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            Log.write(e);
        }
    }


    /**
     * Parsii Toto-pelien tulokset. Huomioitavaa että kaikki lähdöt eivät sisällä totopelejä.
     *
     * @param lines
     * @throws OutOfHorsesException
     */
    private void parseBetRates(Iterator lines) throws OutOfHorsesException {
        Map oddsMap = new HashMap();

        try {
            StringBuilder table = new StringBuilder(HTMLParser.readBlock(lines, "table", null, "published"));

            if (!table.toString().contains("published")) {
                String tr;
                while ((tr = HTMLParser.readBlock(table, "tr")) != null) {
                    StringBuilder sbtr = new StringBuilder(tr);

                    String tdNimi = HTMLParser.readBlock(sbtr, "td");
                    String tdKerroin = HTMLParser.readBlock(sbtr, "td");
                    String tdVaihto = HTMLParser.readBlock(sbtr, "td");

                    List kertoimet = new ArrayList();

                    if (tdNimi != null && tdKerroin != null && tdVaihto != null) {
                        String nimi = tdNimi.strip();

                        if (nimi.contains("Lähtövaihto")) {
                            raceResultStart.setOddsMap(oddsMap);
                            throw new OutOfHorsesException();
                        }

                        StringTokenizer stKerroin = new StringTokenizer(tdKerroin, "-");
                        while (stKerroin.hasMoreTokens()) {
                            String kerroin = stKerroin.nextToken();
                            AlphaNumber aKerroin = new AlphaNumber(kerroin);
                            kertoimet.add(aKerroin.getBigDecimal());
                        }

                        BigDecimal vaihto = new AlphaNumber(tdVaihto).getBigDecimal();

                        BetRate betRate = new BetRate(nimi, kertoimet, vaihto);

                        oddsMap.put(nimi, betRate);
                        raceResultStart.add(betRate);
                    }
                }
            }
        } catch (OutOfHorsesException e) {
            throw e;
        } catch (Exception e) {
            Log.write(e);
        }
    }
}
