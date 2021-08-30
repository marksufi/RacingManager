package hippos;

import hippos.exception.AbsentException;
import hippos.exception.DataObjectException;
import hippos.exception.SubStartMissingException;
import hippos.exception.UnvalidStartException;
import hippos.math.AlphaNumber;
import hippos.utils.DateUtils;
import hippos.utils.HorsesHelper;
import utils.HTMLParser;
import utils.Log;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public class SubStartParser extends SubStart {
    StringTokenizer s;
    private Iterator lines;
    //Date raceDate;
    static String defaultJockey;
    //private StringBuffer racemode = new StringBuffer();

    /*
    public SubStartParser(StringTokenizer s, RaceProgramHorse horse) {
        super(horse);
        this.s = s;
        racemode.append(horse.raceProgramStart.getRaceLiteral());
    }

    public SubStartParser(StringTokenizer s, Date raceDate, RaceProgramHorse horse) {
        this(s, horse);
        this.raceDate = raceDate;
        racemode.append(horse.raceProgramStart.getRaceLiteral());
    }*/

    public SubStartParser(Iterator lines, Date raceDate, RaceProgramHorse horse) {
        super(horse);
        this.lines = lines;
        //this.raceDate = raceDate;
        setRaceLiteral(horse.getRaceProgramStart().getRaceLiteral());
    }

    /**
     *
     *  <a href="/heppa/racing/RaceProgramOneRace,getDriver_1.$DirectLink.sdirect;jsessionid=p6+QtwpVje2pJ3mNO95X1Q**.app3?sp=l5737685957006929007&amp;sp=SPERSON&amp;sp=X" onclick="setStatusText(selectText)" class="">
     *       M Saario
     *  </a>
     *  </td>
     *  <td>Tk</td>
     *  <td>7.6.2010</td>
     *  <td>21</td>
     *  <td>2180/1</td>
     *  <td>37,8&nbsp;x</td>
     *  <td>kl</td>
     *  <td><span class="programshoes_left">&nbsp;</span></td>
     *  <td><span class="programshoes_right">&nbsp;</span></td>
     *  <td>hyv</td>
     *  <td>45,6</td><td class="no_wrap">
     */
    public Object parse() throws SubStartMissingException, DataObjectException, UnvalidStartException, AbsentException {
        try {
            try {
                parseJockey();
                parseLocality();
                parseDate();
                parseStartNumber();
                parseLength();
                parseTime();
                parseRanking();
                lines.next();// etukengät
                lines.next(); // takakengät
                parseRating();
                parseDiff();
            } catch (SubStartMissingException se) {
                while(lines.hasNext() && !((String)lines.next()).contains("</tr>")) {
                    // ei sisällä rivinvaihtoa, eli seuraavalle riville vaan sitten
                }

                throw se;

            }

        } catch (IOException e) {
            Log.write(e);
        }

        return this;
    }

    private void parseStartNumber() {
        String line = HTMLParser.readBlock(lines, "td");
        try {
            startNumber = new BigDecimal(line);
        } catch(Exception e) {
            startNumber = BigDecimal.ZERO;
        }
    }

    private void parseJockey() throws IOException {
        String jockey;
        String line = HTMLParser.readBlock(lines, "td");
        if(line.contains("getDriver")) {
            jockey = Objects.requireNonNull(HTMLParser.readBlock(line, "a")).strip();
        } else {
            throw new IOException("Could not find jockey: " + line);
        }

        if (jockey.length() > 0)
            defaultJockey = jockey;
        setSubDriver(defaultJockey);
    }

    /**
     * Sets locality horseProgNumber from the parameter string. An empty locality horseProgNumber is ignored.
     * MANDATORY for success
     */
    private void parseLocality() throws SubStartMissingException {
        String locality = HTMLParser.readBlock(lines, "td").strip();

        if (!locality.isEmpty()) {
            if(locality.length() > 3)
                Log.write("Too long substart locality length (max 3) " + locality);
            this.setLocality(locality);
        } else
            throw new SubStartMissingException();
    }

    /**
     * Sets date horseProgNumber from the parameter string. The missing year horseProgNumber is substituted with
     * the current year horseProgNumber.
     *
     * @throws AbsentException jos poissa
     */
    private void parseDate() throws AbsentException {
        try {
            Date raceProgramDate = super.getRaceProgramHorse().getRaceDate();
            Calendar r = Calendar.getInstance();
            r.setTime(raceProgramDate);
            int raceProgramYear = r.get(Calendar.YEAR);

            String s = HTMLParser.readBlock(lines, "td");

            if(s.contains("POISSA"))
                throw new AbsentException();

            StringTokenizer st = new StringTokenizer(s, ". ");

            if (st.countTokens() >= 2) {
                day = Integer.valueOf(st.nextToken());
                month = Integer.valueOf(st.nextToken());
            }
            if (st.hasMoreTokens()) {
                year = Integer.valueOf(st.nextToken());
            } else {
                year = Integer.valueOf(raceProgramYear);
            }

            Calendar c = Calendar.getInstance();
            c.set(year, month - 1, day, 0, 0, 0);
            c.set(Calendar.MILLISECOND, 0);

            date = new Date(c.getTimeInMillis());

            if(date.equals(raceProgramDate))
                System.out.print("");

            if(date.after(raceProgramDate)) {
                date = DateUtils.rollYears(date, -1);
            }
            //this.raceMonth = new SubMonth(getRaceMonth(date));
        } catch (AbsentException ae) {
            throw new AbsentException();
        } catch (Exception e) {
            Log.write(e, "Failes to parse substart date: " + s);
        }
    }

    /**
     * Sets raceLength and raceTrack from the input string
     * @throws DataObjectException if format doesn't match
     */
    public void parseLength() throws DataObjectException {
        //System.out.println("SubStartParser.parseLength(" + s + ")");
        String s = HTMLParser.readBlock(lines, "td");

        try {
            StringTokenizer st = new StringTokenizer(s, " /");
            if (st.countTokens() > 1) {
                setRaceLength(new BigDecimal(st.nextToken()));
                                //System.out.println("--> raceLength: " + raceLength);
                setRaceTrack(new BigDecimal(st.nextToken()));
                //System.out.println("--> raceTrack: " + raceTrack);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataObjectException("Invalid raceLength / raceTrack data: " + s);
        }
    }

    private void parseTime() throws UnvalidStartException {
        String str = HTMLParser.readBlock(lines, "td");

        str = HorsesHelper.modifyResultTime(str);
        SubTime raceTime = new SubTime(str, this);

        if(raceTime.getAlpha() != null) {
            if(raceTime.getAlpha().contains("x")) {
                raceTime.setAlpha(raceTime.getAlpha().replace("x", ""));
                setxCode("x");
            }
        }

        setRaceMode(new RaceMode(getRaceLiteral(), null, raceTime, getRaceLength(), getStartNumber()));

        raceTime.setAlpha(getRaceMode().toString());
        setSubTime(raceTime);
    }

 /**
     * Sets the myRanking horseProgNumber from the input string. Only digits are countered
     *
  */
    private void parseRanking() {
        String s = HTMLParser.readBlock(lines, "td");
        AlphaNumber rawString = new AlphaNumber(s);

        try {
            setSubRank(rawString.getBigDecimal());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Set the rating horseProgNumber from the given input parameter. Only digital values are noticed
     *
     */
    private void parseRating() {
        //System.out.println("SubStartParser.parseRating: " + line);
        String line = HTMLParser.readBlock(lines, "td");
        if(line.contains("\t")) {
            // Joku ylipääräinen kenttä tullut jossain vaiheessa
            line = HTMLParser.readBlock(lines, "td");
        }

        AlphaNumber rawString = new AlphaNumber(line);

        setRating(rawString.getBigDecimal());
    }

    /**
     * Set the difference to winner.
     *
     */
    private void parseDiff() {
        while (lines.hasNext()) {
            String line = (String) lines.next();
            if (line.contains("</tr>")) {
                break;
            }
        }
    }
}
