package hippos;

import hippos.math.AlphaNumber;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: marktolo
 * Date: 25.7.2013
 * Time: 0:13
 * To change this template use File | Settings | File Templates.
 */
public class SplitTimes {
    public static final int SIZE = 5;
    public static final int TYPE_HORSE = 0;
    public static final int TYPE_RACE = 1;

    private AlphaNumber splitTimes [][] = new AlphaNumber[2][SIZE];

    public SplitTimes() { }

    //public SplitTimes(AlphaNumber[][] splitTimes) {
    //    this.splitTimes = splitTimes;
    //}

    public void add(int type, int split, BigDecimal time, String racemode) {
        if(time != null) {
            split = split < SplitTimes.SIZE ? split : SplitTimes.SIZE - 1;
            /*
            if(split == 2 && splitTimes[TYPE_RACE][1] != null) {
                time = time.add(splitTimes[TYPE_RACE][1].getBigDecimal());
                time = time.divide(BigDecimal.valueOf(2.0), 2, BigDecimal.ROUND_HALF_UP);
            }*/

            splitTimes[type][split] = new AlphaNumber(time, racemode);
        }
    }


    public void add(int type, int split, AlphaNumber time) {
        if(time != null) {
            BigDecimal bTime = time.getBigDecimal();
            split = split < SplitTimes.SIZE ? split : SplitTimes.SIZE - 1;
            /*
            if(split == 2 && splitTimes[TYPE_RACE][1] != null) {
                bTime = bTime.add(splitTimes[TYPE_RACE][1].getBigDecimal());
                bTime = bTime.divide(BigDecimal.valueOf(2.0), 2, BigDecimal.ROUND_HALF_UP);
                time.setNumber(bTime);
            }*/

            splitTimes[type][split] = time;
        }
    }

    public AlphaNumber get(int type, int split) {
        return (AlphaNumber) splitTimes[type][split];
    }

    public BigDecimal getNumber(int type, int split) {
        return splitTimes[type][split] != null ? splitTimes[type][split].getNumber() : null;
    }

    public AlphaNumber [] get(int type) {
        return splitTimes[type];
    }

    public AlphaNumber[][] getSplitTimes() {
        return splitTimes;
    }
}
