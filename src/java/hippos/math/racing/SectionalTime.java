package hippos.math.racing;

import hippos.math.AlphaNumber;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Sep 12, 2004
 * AlphaNumber: 11:59:58 PM
 * To change this template use Options | File Templates.
 */
public class SectionalTime implements Comparable {
    private String horse;
    private AlphaNumber horseSplit;
    private AlphaNumber raceSplit;
    private String placing;
    private Date date;
    private BigDecimal raceTrack;

    public SectionalTime(String horsename, BigDecimal quarterTime) {
        horse = horsename;
        horseSplit = new AlphaNumber(quarterTime);
    }

    /*
    public SectionalTime(String horse, BigDecimal number, AlphaNumber horseSplit, AlphaNumber raceSplit, String placing, Date date, BigDecimal raceTrack) {
        this.horse = horse;
        this.number = number;
        this.horseSplit = horseSplit;
        this.raceSplit = raceSplit;
        this.placing = placing;
        this.date = date;
        this.raceTrack = raceTrack;
        //System.out.println("raceDriverName: " + raceDriverName + " raceResultTime: " + raceResultTime);
    }*/

    public SectionalTime(String horse, AlphaNumber racemodeTime) {
        this.horse = horse;
        this.horseSplit = racemodeTime;
    }

    public SectionalTime(String raceHorseName) {
        this.horse = raceHorseName;
    }

    public String getHorse() {
        return horse;
    }

    public void setHorse(String horse) {
        this.horse = horse;
    }

    public AlphaNumber getHorseSplit() {
        return horseSplit;
    }

    public void setHorseSplit(AlphaNumber time) {
        this.horseSplit = horseSplit;
    }

    public AlphaNumber getRaceSplit() {
        return raceSplit;
    }

    public String toString() {
        return horseSplit + "/" + horse;
    }

    public int compareTo(Object o) {
        if(this.hashCode() == o.hashCode())
            return 0;

        SectionalTime anotherSectionalTime = (SectionalTime)o;
        int c = 0;

        if(horseSplit == null)
            return 1;

        if(anotherSectionalTime.getHorseSplit() == null)
            return -1;

        c = horseSplit.compareTo(anotherSectionalTime.getHorseSplit());

        if(c != 0)
            return c;

        return -1;
    }

    public BigDecimal getRaceTrack() {
        return raceTrack;
    }
}
