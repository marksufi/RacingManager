package hippos;

import hippos.lang.ComparableCrossTableMapField;
import hippos.math.FileContainer;
import hippos.util.CrossTableMap;
import hippos.util.SimpleCrossMapCompareMethod;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Mar 9, 2006
 * Time: 11:27:49 AM
 * To change this template use Options | File Templates.
 */
//public class SubLength extends ComparableCrossTableMapField {
public class SubLength extends ComparableCrossTableMapField {
    static CrossTableMap KTimeMap;
    static CrossTableMap LTimeMap;
    static Map timeMap;

    public SubLength(String value) {
        super(value);
    }

    public static void load() {
        try {
            if(KTimeMap == null)  {
                KTimeMap = (CrossTableMap)new FileContainer(HarnessApp.dataPath, "subLengthKTime.dat").load();
                if(KTimeMap == null) {
                    KTimeMap = new CrossTableMap(new SimpleCrossMapCompareMethod(), 0);
                }
            }

            if(LTimeMap == null)  {
                LTimeMap = (CrossTableMap)new FileContainer(HarnessApp.dataPath, "subLengthLTime.dat").load();
                if(LTimeMap == null) {
                    LTimeMap = new CrossTableMap(new SimpleCrossMapCompareMethod(), 0);
                }
            }

            if(timeMap == null) {
                timeMap = (HashMap)new FileContainer(HarnessApp.dataPath, "subLengthTime.dat").load();
                if(timeMap == null) {
                    timeMap = new HashMap();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try {
            new FileContainer(HarnessApp.dataPath, "subLengthKTime.dat").save(KTimeMap);
            new FileContainer(HarnessApp.dataPath, "subLengthLTime.dat").save(LTimeMap);
            new FileContainer(HarnessApp.dataPath, "subLengthRanking.dat").save(timeMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setValue(String value) {
        super.setValue(value);
    }

    public BigDecimal getShare() {
        return new BigDecimal(1);
    }

    public CrossTableMap getCrossTableMap() {
        return null;
    }

    public BigDecimal getBigDecimal() {
        BigDecimal length = null;
        if(this.getField() != null) {
            length = new BigDecimal((String)this.getField());
        }
        return length;
    }

    /*
    public void update(SubStart subStart, int index, RaceResultHorse raceResultHorse) {
        BigDecimal time = subStart.getRaceTime().getBigDecimal();
        if(time != null) {
            BigDecimal price = raceResultHorse.getRaceResultPrize();

            String key = toKeyString(subStart, raceResultHorse);
            LinReg reg;
            if((reg = (LinReg) timeMap.get(key)) == null) {
                reg = new LinReg();
            }
            reg.add(time, price);
            timeMap.put(key, reg);
        }
    }*/

    /*
    private String toKeyString(SubStart subStart, Horse raceResultHorse) {
        String subRaceMode = subStart.getRaceMode();
        String raceMode = raceResultHorse.getRaceStart().getRaceMode();

        StringBuffer sb = new StringBuffer();
        sb.append(raceMode);
        sb.append("-");
        sb.append(subRaceMode);

        return sb.toString();
    }*/

    /*
    public BigDecimal get(SubStart subStart, ValueHorse horse) {
        BigDecimal time = subStart.getRaceTime() != null ? subStart.getRaceTime().getBigDecimal() : null;

        String key = toKeyString(subStart, horse.getRaceProgramHorse());
        LinReg reg;
        if( time != null && (reg = (LinReg) timeMap.get(key)) != null ) {
            BigDecimal award = reg.get(time);
            return award;
        } else {
            //System.out.println(key + "/" + originalTime);
        }

        return null;
    }*/


}
