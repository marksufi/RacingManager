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
 * Date: Mar 10, 2006
 * Time: 3:15:33 PM
 * To change this template use Options | File Templates.
 */
public class HorseName extends ComparableCrossTableMapField {
    static CrossTableMap valueMap;
    private String name;

    public HorseName(String value) {
        super(value);
        this.name = name;
    }

    public CrossTableMap getCrossTableMap() {
        return valueMap;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public static void load() {
        try {
            if(valueMap == null) {
                valueMap = (CrossTableMap)new FileContainer(HarnessApp.dataPath, "horseName.dat").load();
                if(valueMap == null) {
                    //valueMap = new CrossTableMap(new ProcentualDataEntry());
                    valueMap = new CrossTableMap(new UnionCrossMapCompareMethod(HarnessApp.trustLimit), HarnessApp.trustLimit);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try {
            new FileContainer(HarnessApp.dataPath, "horseName.dat").save(valueMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void compareTo(Object o, BigDecimal result) {
        compareTo(valueMap, o, result);
    }

    public BigDecimal compareTo(Object o) {
        return compareTo(valueMap, o);
    }

    public BigDecimal getShare() {
        return new BigDecimal(1);
    }

    public String getName() {
        return name;
    }
}
