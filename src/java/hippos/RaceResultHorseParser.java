package hippos;

import hippos.exception.DataObjectException;
import hippos.math.AlphaNumber;
import hippos.utils.HorsesHelper;
import utils.HTMLParser;
import utils.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.StringTokenizer;

 public class RaceResultHorseParser  {
    RaceResultStart raceResultStart;
    RaceResultHorse resultRaceHorse = null;

     public RaceResultHorseParser(RaceResultStart raceResultStart, BigDecimal raceResultPlacing) {
         this.raceResultStart = raceResultStart;
         resultRaceHorse = new RaceResultHorse(raceResultStart);
         resultRaceHorse.raceResultRanking = new SubRank(raceResultPlacing);
     }

     public Object parse(Iterator lines) {
        try {
            while(lines.hasNext()) {
                parseHorseNumber(lines);
                parseNames(lines);
                parseRaceResultTime(lines);
                parseShortNote(lines);
                parseRaceResultWinOdds(lines);
                parseRaceResultPrice(lines);
                parseRaceResultDistance(lines);

                resultRaceHorse.setId();

                break;
            }
            return resultRaceHorse;
        } catch (Exception e) {
            Log.write(e, resultRaceHorse.toString());
        }
        return null;
    }

    /**
     *  <td class="raceResultPlacing">
     *
     *       1.
     *
     *  <td class="raceResultPlacing">
     *
     *
     * &nbsp;
     *
     * <td  class="horseProgNumber">3</td>
     */

     /**
      * <td  class="raceResultProgNumber">

      1

      </td>
      */
    private void parseHorseNumber(Iterator lines) throws DataObjectException {

      try {
            String line = HTMLParser.readBlock(lines, "td", "raceResultProgNumber");
            line = line.strip();
            resultRaceHorse.setHorseProgNumber(new BigDecimal(line));
            resultRaceHorse.setId();
            return;
        } catch (Exception e) {
            throw new DataObjectException(e, resultRaceHorse.toString());
        }
    }

    /**
     *  <a href="/heppa/racing/RaceResults,horseName.$DirectLink.sdirect;jsessionid=WXHh55Ncbr2XuHcxg9X5-Q**.app3?sp=l2649671446026562387&amp;sp=X" onclick="return setStatusAndEnsure(selectText, this);" class="">
     *      P.S. Rotuli
     *      Windsong's Love*
     *  </a>
     */
    private void parseNames(Iterator lines) throws DataObjectException {
        try {
            String line = HTMLParser.readBlock(lines, "td", "raceResultHorseName");
            line = line.strip();

            StringBuilder sbLine = new StringBuilder(line);

            String raceResultHorseName = HTMLParser.readBlock(sbLine, "a");
            raceResultHorseName = raceResultHorseName.replace('*', ' ');
            raceResultHorseName = raceResultHorseName.trim();
            resultRaceHorse.setRaceHorseName(raceResultHorseName);

            String raceResultDriverName = HTMLParser.readBlock(sbLine, "a");
            resultRaceHorse.setRaceResultDriver(raceResultDriverName.strip());

        } catch (Exception e) {
            throw new DataObjectException(e, resultRaceHorse.toString());
        }
    }

    /**
     *  <td class="raceResultTime numeric">
     *      20,9&nbsp;
     *  </td>
     *
     *  <td class="shortNote">&nbsp;</td>
     *
     * *************
     *  <td class="raceResultTime numeric">
     *      &nbsp;
     *  </td>
     *
     *  <td class="shortNote">hpl &nbsp;</td>
     * ***********
     *
     *  <td class="raceResultTime numeric">
     *      Poissa&nbsp;
     *  </td>
     *
     *  <td class="shortNote">&nbsp;</td>
     *
     *
     *  <td class="raceResultWinOdds numeric">222</td>
     *  <td class="raceResultPrize numeric">800 €</td>
     *  <td class="raceLength numeric">2100:1  </td>
     *****
     *
     *  <td class="shortNote">hyl 1 &nbsp;</td>
     *  <td class="shortNote"> hyv&nbsp;</td>
     *  <td class="shortNote">x hyv&nbsp;</td>
     *  <td class="shortNote">hll &nbsp;</td>
     *  <td class="raceResultWinOdds numeric">25</td>
     *  <td class="raceResultPrize numeric">800 €</td>
     *  <td class="raceResultDistance numeric">2140:6  </td>
     */
    private void parseRaceResultTime(Iterator lines) throws DataObjectException {
        //System.out.println("RaceResultHorseParser.parseTime('" + s + "')");
        String time = new String();
        try {
            String line = HTMLParser.readBlock(lines, "td", "raceResultTime");
            line = line.strip();

            if (line.contains("Poissa")) {
                resultRaceHorse.present = false;
            } else {
                line = HorsesHelper.modifyResultTime(line);
                resultRaceHorse.raceResultTime = new AlphaNumber(line);
                resultRaceHorse.raceResultTime.setAlpha(raceResultStart.getRaceMode());
            }
        } catch (Exception e) {
            throw new DataObjectException(e, resultRaceHorse.toString());
        }
    }

     private void parseShortNote(Iterator lines) throws DataObjectException {
         //System.out.println("RaceResultHorseParser.parseTime('" + s + "')");
         try {
             String line = HTMLParser.readBlock(lines, "td","shortNote");
             line = line.strip();

             resultRaceHorse.setX(BigDecimal.ZERO);
             resultRaceHorse.setShortNote(new AlphaNumber(line));

             if(resultRaceHorse.getShortNote().toString().contains("x"))
                 resultRaceHorse.setX(BigDecimal.ONE);

             if(resultRaceHorse.getShortNote().toString().contains("h")) {
                 resultRaceHorse.setX(BigDecimal.ONE);
             }
         } catch (Exception e) {
             throw new DataObjectException(e, resultRaceHorse.toString());
         }
     }

     private void parseRaceResultWinOdds(Iterator lines) throws DataObjectException {
         //System.out.println("RaceResultHorseParser.parseTime('" + s + "')");
         try {
             String line = HTMLParser.readBlock(lines, "td","raceResultWinOdds");
             line = line.strip();

             AlphaNumber aNumber = new AlphaNumber(line);

             BigDecimal number = aNumber.getNumber();

             if (number != null) {
                 number = number.divide(BigDecimal.TEN, 1, BigDecimal.ROUND_HALF_UP);

                 if (number.compareTo(BigDecimal.ONE) > 0) {
                     resultRaceHorse.raceResultWinOdds = number;
                 }
             }

         } catch (Exception e) {
             throw new DataObjectException(e, resultRaceHorse.toString());
         }
     }

     private void parseRaceResultPrice(Iterator lines) throws DataObjectException {
         try {
             String line = HTMLParser.readBlock(lines, "td","raceResultPrice");
             line = line.strip();

             AlphaNumber prize = new AlphaNumber(line);
             if (prize.getNumber() != null) {
                 resultRaceHorse.setRaceResultPrize(prize.getNumber());
             } else {
                 resultRaceHorse.setRaceResultPrize(BigDecimal.ZERO);
             }
         } catch (Exception e) {
             throw new DataObjectException(e, resultRaceHorse.toString());
         }
     }

     private void parseRaceResultDistance(Iterator lines) throws DataObjectException {
         try {
             String line = HTMLParser.readBlock(lines, "td","raceResultDistance");
             line = line.strip();

             parseLengthAndTrack(line);

         } catch (Exception e) {
             throw new DataObjectException(e, resultRaceHorse.toString());
         }
     }

     /**
     *
     * @param s raceLength / raceTrack string
     *      '2120: 4' or empty
     *      '2100:8 SE'
     */
    private void parseLengthAndTrack(String s) throws DataObjectException {
        //System.out.println("RaceResultHorse.parseLengthAndTrack(" + s + ")");
        try {
            StringTokenizer st = new StringTokenizer(s, " :");
            resultRaceHorse.setRaceLength(new BigDecimal(st.nextToken()).setScale(0, RoundingMode.HALF_UP));
            resultRaceHorse.setRaceTrack(new BigDecimal(st.nextToken()).setScale(0, RoundingMode.HALF_UP));

            raceResultStart.addRaceLength(resultRaceHorse.getRaceLength());
        } catch (Exception e) {
            throw new DataObjectException(e, resultRaceHorse.toString());
        }
    }
}
