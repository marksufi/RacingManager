package hippos.util;

import hippos.RaceResultHorse;
import hippos.lang.toto.TotoGameStats;
import utils.Log;

import java.math.BigDecimal;

public class HOpserverMapper<T> extends Mapper<T> {
    private static Mapper<TotoGameStats> observerStatistics = new Mapper<>();

    /**
     * Päivittää havainnon tilastoja
     *
     * @param key
     * @param winOdds   voittokerroin - 0 jos ei voittoa
     */
    public void add(Object key, BigDecimal winOdds) {
        observerStatistics.getOrCreate(key, new TotoGameStats()).add(winOdds);

    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        for(Object key : getKeys()) {
            sb.append(key);

            try {
                sb.append("[" + observerStatistics.get(key).getWinProcents() + "%" + "]");
            } catch (NullPointerException e) {
                // Avainta ei löytynyt

            } catch (Exception e) {
                Log.write(e);
                e.printStackTrace();
            }

            sb.append(":" + get(key));
            sb.append("\n");

        }

        return sb.toString();
    }
}
