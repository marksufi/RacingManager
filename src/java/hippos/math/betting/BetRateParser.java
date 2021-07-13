package hippos.math.betting;

import hippos.math.AlphaNumber;
import utils.HTMLParser;
import utils.Log;

import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Mar 29, 2006
 * Time: 10:34:32 PM
 * To change this template use Options | File Templates.
 */
public class BetRateParser {
    String line = null;
    Iterator lines;

    public BetRateParser(Iterator lines, String line) {
        this.lines =lines;
        this.line = line;
    }

    public BetRate parse() {
        BetRate betRate = new BetRate();
        betRate.name = line;
        String oddString = new String();

        try {
            // Kerroin
            line = HTMLParser.readBlock(lines, "td");
            line = line.strip();

            AlphaNumber odd = new AlphaNumber(line);
            betRate.odds.add(odd.getNumber());

            // Volume
            line = HTMLParser.readBlock(lines, "td");
            line = line.strip();

            if(line.indexOf('€') > 0) {
                AlphaNumber volume = new AlphaNumber(line);
                betRate.volume = volume.getNumber();
            }

            Game game = GameFactory.create(betRate.name, betRate.odds);
            if(game != null) {
                betRate.addGame(game);
            }
        } catch (Exception e) {
            Log.write(e);
        }
        return betRate;
    }

    private String makeBigDecimal(String str) {
        String bigDecimalString = new String();
        str = str.replaceAll(",",".").trim();
        for(int i=0; i< str.length(); i++) {
            if(Character.isDigit(str.charAt(i)) || str.charAt(i) == '.') {
                bigDecimalString += str.charAt(i);
            }
        }
        while(bigDecimalString.indexOf(".") != bigDecimalString.lastIndexOf(".")) {
            bigDecimalString = bigDecimalString.replace(".", "");
        }


        return bigDecimalString;
    }
}
