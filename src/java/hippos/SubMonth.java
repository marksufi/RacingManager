package hippos;

import hippos.lang.ComparableCrossTableMapField;
import hippos.math.FileContainer;
import hippos.util.CrossTableMap;
import hippos.util.UnionCrossMapCompareMethod;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Mar 9, 2006
 * Time: 11:15:44 AM
 * To change this template use Options | File Templates.
 */
public class SubMonth extends ComparableCrossTableMapField {
    static CrossTableMap KTimeMap;
    static CrossTableMap LTimeMap;
    static CrossTableMap timeMap;

    //public SubMonth() {
    //    super();
    //}

    public SubMonth(String value) {
        super(value);
    }


    public static void load() {
        try {
            if(KTimeMap == null)  {
                KTimeMap = (CrossTableMap)new FileContainer(HarnessApp.dataPath, "subMonthKTime.dat").load();
                if(KTimeMap == null) {
                    KTimeMap = new CrossTableMap(new UnionCrossMapCompareMethod(HarnessApp.trustLimit), HarnessApp.trustLimit);
                }
            }

            if(LTimeMap == null)  {
                LTimeMap = (CrossTableMap)new FileContainer(HarnessApp.dataPath, "subMonthLTime.dat").load();
                if(LTimeMap == null) {
                    LTimeMap = new CrossTableMap(new UnionCrossMapCompareMethod(HarnessApp.trustLimit), HarnessApp.trustLimit);
                }
            }

            if(timeMap == null) {
                timeMap = (CrossTableMap)new FileContainer(HarnessApp.dataPath, "subMonthTime.dat").load();
                if(timeMap == null) {
                    timeMap = new CrossTableMap(new UnionCrossMapCompareMethod(HarnessApp.trustLimit), HarnessApp.trustLimit);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try {
            new FileContainer(HarnessApp.dataPath, "subMonthKTime.dat").save(KTimeMap);
            new FileContainer(HarnessApp.dataPath, "subMonthLTime.dat").save(LTimeMap);
            new FileContainer(HarnessApp.dataPath, "subMonthTime.dat").save(timeMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BigDecimal getShare() {
        return new BigDecimal(1);
    }

    public CrossTableMap getCrossTableMap() {
        return timeMap;
    }

}
