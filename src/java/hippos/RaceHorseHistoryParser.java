package hippos;

import hippos.exception.AbsentException;
import hippos.exception.UnvalidStartException;
import hippos.io.FileParser;
import hippos.io.RaceProgramFile;
import hippos.math.AlphaNumber;
import hippos.web.WebPage;
import utils.ExistsInDatabaseException;
import utils.HTMLParser;
import utils.Log;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by Markus on 28.9.2019.
 *
 * <tr><th>Rata</th>
 <th>P�iv�ys</th>
 <th>L�h</th>
 <th>Rata</th>
 <th>Matka</th>
 <th>Tyyppi</th>
 <th class="sorttable_racetime">Aika</th>
 <th>Sija</th>
 <th><span title="Hylk�yskoodit, keskeytykset, laukat, ep:t, koel�ht�jen hyv/hyl">&nbsp;&nbsp;&nbsp;</span></th>
 <th>Kerr</th>
 <th>Palkinto</th>
 <th>Ohjastaja</th>
 <th>SE</th>
 <th class="sorttable_nosort" colspan=2>Kengitys</th>
 <th class="sorttable_nosort"></th>
 <th class="sorttable_nosort"></th></tr>
 */
public class RaceHorseHistoryParser implements FileParser {
    private static final String raceHistoryPageUrlBody = " https://heppa.hippos.fi/heppa/horse/RacingHistory,horsesStarts.$DirectLink.sdirect?sp=";
    private WebPage raceLinkPage;
    private RaceProgramHorse raceProgramHorse;

    public RaceHorseHistoryParser(RaceProgramHorse raceProgramHorse) throws IOException {
        this(raceProgramHorse.getRegisterNumber(), raceProgramHorse);
    }

    public RaceHorseHistoryParser(String registerNumber, RaceProgramHorse raceProgramHorse) throws IOException {
        try {
            this.raceProgramHorse = raceProgramHorse;

            URL raceHistoryPageUrl = new URL(raceHistoryPageUrlBody + registerNumber);

            this.raceLinkPage = new WebPage(raceHistoryPageUrl);

            System.out.println("RaceHorseHistoryParser.RaceHorseHistoryParser: " + raceHistoryPageUrl);

            // Miksi tämä?
            SubStart.deleteAll(this.raceProgramHorse);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    public RaceHorseHistoryParser(String registerNumber) throws IOException {
        try {
            URL raceHistoryPageUrl = new URL(raceHistoryPageUrlBody + registerNumber);

            this.raceLinkPage = new WebPage(raceHistoryPageUrl);

            System.out.println("\n\nRaceHorseHistoryParser.RaceHorseHistoryParser: " + raceHistoryPageUrl);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Object parse() throws Exception {
        return null;
    }

    public Object parse(Connection conn) {
        List<SubStart> newSubStarts = new ArrayList<>();

        try {

            if (raceLinkPage != null) {

                String line;

                if (raceLinkPage.SearchBlockLine("h4", "Startit") != null) {
                    if (raceLinkPage.readBlock("tr") != null) {
                        while (raceLinkPage.findBefore("tr", "</div>") != null) {
                            SubStart subStart = new SubStart(raceProgramHorse);
                            try {
                                for (int i = 0; i < 15; i++) {
                                    if ((line = raceLinkPage.readBlock("td")) != null) {
                                        //System.out.println("RaceHorseHistoryParser.parse " + i + ": " + line);
                                        line = line.strip();
                                        switch (i) {
                                            case 0: // Rata
                                                String place = line.trim();
                                                subStart.setLocality(place);
                                                break;

                                            case 1: // S��
                                                String weather = line.trim();
                                                subStart.setWeather(weather);
                                                break;

                                            case 2: // P�iv�ys
                                                String aBlock = HTMLParser.readBlock(line, "a");
                                                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

                                                // Joskus, esim. Ruotsin raveissa, päivämäärä sisältyy jo td-lohkoon
                                                Date d = aBlock != null ? sdf.parse(aBlock) : sdf.parse(line);

                                                //System.out.println(d);
                                                subStart.setDate(d);
                                                break;

                                            case 3: // L�h
                                                try {
                                                    String startNumber = line.trim();
                                                    subStart.setStartNumber(new BigDecimal(startNumber));
                                                    //System.out.println(startNumber);
                                                } catch (Exception e) {
                                                    // Korvaus j�rjest�j�n virheest�
                                                    subStart.setSubTime(new SubTime("P", subStart));
                                                    //i = 15; // lopetus
                                                    throw new AbsentException(subStart.toString());
                                                }
                                                break;
                                            case 4: // Rata
                                                String trackNumber = line.trim();
                                                //System.out.println(trackNumber);
                                                subStart.setRaceTrack(new BigDecimal(trackNumber));
                                                break;
                                            case 5: // Matka
                                                String raceLength = line.trim();
                                                //System.out.println(raceLength);
                                                try {
                                                    subStart.setRaceLength(new BigDecimal(raceLength));
                                                } catch (Exception e) {
                                                    //e.printStackTrace();
                                                    throw new AbsentException(subStart.toString());
                                                }
                                                break;
                                            case 6: // Tyyppi
                                                String raceType = line.trim();
                                                //System.out.println(raceType);
                                                subStart.setRaceType(raceType);
                                                break;
                                            case 7: // Aika
                                                SubTime time = new SubTime(line, subStart);
                                                if (time.getNumber() != null) {
                                                    time.setNumber(time.getNumber().subtract(BigDecimal.valueOf(100)));
                                                }
                                                subStart.setSubTime(time);
                                                if (line.contains("P")) {
                                                    // Poissa
                                                    //i = 15;
                                                    throw new AbsentException(subStart.toString());
                                                }
                                                break;
                                            case 8: // 2. V�liaika
                                                String va = line.trim();
                                                subStart.setkCode(va.equals("K") ? BigDecimal.ONE : BigDecimal.ZERO);
                                                break;

                                            case 9: // Sijoitus
                                                AlphaNumber rankString = new AlphaNumber(line.trim());
                                                BigDecimal raceRanking = rankString.getBigDecimal();
                                                subStart.setSubRank(raceRanking);
                                                break;

                                            case 10: // X koodi
                                                line = line.strip();
                                                line = HTMLParser.removeBlock(line, "span");
                                                StringBuilder sb = new StringBuilder();
                                                StringTokenizer st = new StringTokenizer(line);
                                                while (st.hasMoreTokens()) {
                                                    String t = st.nextToken();
                                                    if(!t.contains("hy")) {
                                                        // ei hyv eikä hyl
                                                        sb.append(t);
                                                        if (st.hasMoreTokens()) {
                                                            sb.append(" ");
                                                        }
                                                    }
                                                }
                                                String xCode = sb.toString().strip();

                                                if (xCode.length() > 0) {
                                                    AlphaNumber xNumber = new AlphaNumber(xCode);
                                                    if(xNumber.getNumber() != null) {
                                                        // hml 3, hyl 5...
                                                        subStart.setSubRank(xNumber.getNumber());
                                                    }
                                                    subStart.setxCode(xCode);
                                                } else {
                                                    subStart.setxCode(null);

                                                    // Tarkistaa onko hevosella jo aika tai sijoitus, jos ei ole, niin
                                                    // lähtöä ei ole vielä ajetta ja vajaata tulosta ei voi tallettaa

                                                    if(subStart.getSubRank() == null && subStart.getSubTime().getNumber() == null) {
                                                        throw new AbsentException();
                                                    }
                                                }
                                                break;

                                            case 11:    // Kerroin
                                                AlphaNumber rawString = new AlphaNumber(line.trim());
                                                subStart.setRating(rawString.getBigDecimal());
                                                break;
                                            case 12:    // Palkinto
                                                AlphaNumber prize = new AlphaNumber();
                                                if (line.contains("€")) {
                                                    line = line.substring(0, line.indexOf("€") + 1);

                                                    prize = new AlphaNumber(line);
                                                } else if (subStart.getRating() != null) {
                                                    prize = new AlphaNumber("0,00 €");
                                                }
                                                subStart.setAward(prize.getNumber());
                                                //System.out.println(prize);
                                                break;
                                            case 13:    // Ohjastaja
                                                aBlock = HTMLParser.readBlock(line, "a");
                                                //System.out.println("driver=" + driver);

                                                String driver = aBlock != null ? aBlock : line;
                                                driver = driver.strip();

                                                subStart.setSubDriver(driver);
                                                break;
                                            case 14:    // Valmentaja
                                                aBlock = HTMLParser.readBlock(line, "a");

                                                String valmentaja = null;

                                                // Valmentajaa ei välttämättä ole laitettu, tai se voi sisältyä td-lohkoon
                                                if(aBlock != null)
                                                    valmentaja = aBlock.strip();
                                                else if(!line.strip().isEmpty())
                                                    valmentaja = line.strip();

                                                subStart.setCoach(valmentaja);
                                                break;
                                            /*
                                            case 15:    // Hoitaja
                                                        String hoitaja = line.trim();
                                                        break;
                                            */

                                        }
                                    }
                                }

                                //if (subStart.getRaceTime().toString().indexOf("P") < 0) {

                                if (raceProgramHorse != null) {
                                    subStart.setRaceLiteral(raceProgramHorse.raceProgramStart.getRaceLiteral());

                                    subStart.setRaceMode(
                                            new RaceMode(
                                                    subStart.getRaceLiteral(),
                                                    subStart.getRaceType(),
                                                    subStart.getSubTime(),
                                                    subStart.getRaceLength(),
                                                    subStart.getStartNumber()));

                                    subStart.getSubTime().setAlpha(subStart.getRaceMode().toString());

                                    if(subStart.getSubTime().getAlpha().lastIndexOf('L') > 0)
                                        Log.write("Incorrect subStart racemode: " + subStart);
                                    if(subStart.getSubTime().getAlpha().lastIndexOf('K') > 0)
                                        Log.write("Incorrect subStart racemode: " + subStart);

                                }

                                //Yrittää tallettaa tuloksen, ja heittää ExistsInDatabaseException(), jos jo tietokannassa
                                if(raceProgramHorse != null) {
                                    if(subStart.existsInDatabase(conn))
                                        throw new ExistsInDatabaseException();

                                    newSubStarts.add(0, subStart);
                                }
                                //}
                            } catch (AbsentException ae) {
                                // Poissa, hyppää seuraavan lähdön riville
                                //ae.printStackTrace();
                            } catch (UnvalidStartException ue) {
                                // Opetuslähtö
                                //System.out.println("RaceHorseHistoryParser.parse: Opetusläntö");
                            } catch (ExistsInDatabaseException e) {
                                /*
                                    Tarkistaa vielä löytyykö 'Ruotsalaista duplikattia', eli paikkakunta
                                    on eri, vaikka päivämäärä ja lähtö ovat samoja. Jos löytyy niin poistaa
                                    kannasta löytyvän, ja tallentaa uuden.
                                 */
                                String racePLace = subStart.getRacePlace();
                                if(!racePLace.equals(subStart.getLocality())) {
                                    subStart.delete();
                                    newSubStarts.add(0, subStart);
                                    //subStart.insert(raceProgramHorse);
                                    continue;
                                }

                                for(SubStart newSubStart : newSubStarts) {
                                    newSubStart.insert(conn, raceProgramHorse);
                                }

                                throw e;
                            } catch (Exception e) {
                                if(raceProgramHorse != null)
                                    Log.write(e, "RaceHorseHistoryParser.parse(" + raceProgramHorse.getRaceHorseName() + ") " + subStart);
                                else
                                    e.printStackTrace();
                            }
                        }

                        for(SubStart newSubStart : newSubStarts) {
                            newSubStart.insert(conn, raceProgramHorse);
                        }

                    }
                }

            }
        } catch (ExistsInDatabaseException e)  {
            //System.out.println(e);
        } catch (Exception e) {
            Log.write(e, raceProgramHorse.getId());
        } finally {
            if (this.raceLinkPage != null) {
                this.raceLinkPage.close();
            }
        }
        return raceProgramHorse;
    }


    public static void main(String[] args) {

        //
        // http://heppa.hippos.fi/heppa/horse/RacingHistory,horsesStarts.$DirectLink.sdirect?sp=l8677344796515912522
        // http://heppa.hippos.fi/heppa/horse/RacingHistory,horsesStarts.$DirectLink.sdirect?sp=l5019985649621011480
        //http://heppa.hippos.fi/heppa/horse/RacingHistory,horsesStarts.$DirectLink.sdirect?sp=l6704358645563052164

        //https://heppa.hippos.fi/heppa/app?page=horse%2FRacingHistory&service=external
        // http://heppa.hippos.fi/heppa/horse/RacingHistory,horsesStarts.$DirectLink.sdirect?sp=246001L00151197
        //l4401750071805637782

        // L�hd�n tulokset
        //<a href="/heppa/app;jsessionid=KftCsCLi68sgwn8e0623BszhjSDQNFPxKnDO0Qhf.app_1?page=racing%2FRaceResults&amp;service=external&amp;sp=CF1570395600000&amp;sp=CEJO&amp;sp=CC3">
        // http://heppa.hippos.fi/heppa/racing/RaceResults,results.$DirectLink.sdirect?sp=CF1584223200000&sp=CET&sp=CC5

        //http://heppa.hippos.fi/heppa/app?page=racing%2FRaceResults&service=external&sp=CF1599771600000&sp=CEO&sp=CC4

        try {
            RaceProgramFile racebookFile = new RaceProgramFile(
                    "C:\\Users\\marktolo\\My Projects\\Filebase\\hippos\\ohjelma",
                    "CF1359151200000_CEP_CC32.php");
            //ValueHorse valueHorse = new ValueHorse();

            //RaceHorseHistoryParser raceHorseHistoryParser = new RaceHorseHistoryParser("l8528865076867736824");
            //RaceHorseHistoryParser raceHorseHistoryParser = new RaceHorseHistoryParser("l8677344796515912522");
            //RaceHorseHistoryParser raceHorseHistoryParser = new RaceHorseHistoryParser("l5019985649621011480");
            RaceHorseHistoryParser raceHorseHistoryParser = new RaceHorseHistoryParser("l921981159288472081");


            raceHorseHistoryParser.parse();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
