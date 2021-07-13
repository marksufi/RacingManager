package hippos.lang;

import hippos.HarnessApp;
import hippos.math.Correlation;
import hippos.math.FileContainer;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: marktolo
 * Date: 12.9.2013
 * Time: 21:26
 * To change this template use File | Settings | File Templates.
 */
public class AwardStats extends ComparableCorrelationField implements ComparableField {
    //static Correlation correlation;
    private Correlation correlation;
    private String filename;

    public AwardStats(ComparableField comparableField) {
        this("awardStats.dat", comparableField);
    }
    public AwardStats(String filename, ComparableField comparableField) {
        this(filename, comparableField, new BigDecimal(1));
    }

    public AwardStats(String filename, ComparableField comparableField, BigDecimal share) {
        super(comparableField, share);
        this.filename = filename;
    }

    public Correlation getCorrelation() {
        load();
        return correlation;
    }

    public void load() {
        if(correlation == null) {
            try {
                correlation = (Correlation)new FileContainer(HarnessApp.dataPath, filename).load();
                if(correlation == null) {
                    correlation = new Correlation();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void save() {
        try {
            new FileContainer(HarnessApp.dataPath, filename).save(correlation);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void putCorrelation(Correlation correlation) {
        this.correlation = correlation;
        save();
    }

}
