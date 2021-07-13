package hippos;

import utils.Log;

import java.math.BigDecimal;
import java.util.Date;

public abstract class Horse {
    //List subStarts;
    protected RaceStart raceStart;
    //RaceResultHorse raceResultHorse;

    private String id;
    private String lid;
    private BigDecimal horseProgNumber;
    private String raceHorseName;
    //private Driver driver;
    private String racePlace;
    //private Date raceDate;
    private BigDecimal raceLength;
    private BigDecimal raceTrack;
    private String trackId;
    private String raceMode;
    //BigDecimal tasoitettuAika;
    private BigDecimal tasoitus;
    //SetValue value;
    //BigDecimal myRanking;

    public Horse() {
    }

    public Horse(RaceStart raceStart) {
        this.raceStart = raceStart;
        this.raceMode = raceStart.getRaceMode();
        this.lid = raceStart.getId();
    }

    /*
    public RaceResultHorse getRaceResultHorse() {
        return raceResultHorse;
    }

    public void setRaceResultHorse(RaceResultHorse raceResultHorse) {
        this.raceResultHorse = raceResultHorse;
    }*/

    public void setRaceHorseName(String raceHorseName) {
        if(raceHorseName.indexOf("(") >= 0) {
            raceHorseName = raceHorseName.substring(0, raceHorseName.indexOf("("));
        }
        if(raceHorseName.indexOf("*") >= 0) {
            raceHorseName = raceHorseName.substring(0, raceHorseName.indexOf("*"));
        }
        this.raceHorseName = raceHorseName.trim();
    }

    public BigDecimal getHorseProgNumber() {
        return horseProgNumber;
    }

    public void setHorseProgNumber(BigDecimal horseProgNumber) {
        this.horseProgNumber = horseProgNumber;
    }

    public String getRaceHorseName() {
        return raceHorseName;
    }

    public void setName(String name) {
        this.raceHorseName = name;
    }

    /*
    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public void setDriver(String driver) {
        this.driver = new Driver(driver);
    }
    */
    /*
    public void setRaceDriverName(String raceResultDriverName) {
        this.driver = new HorseJockey(raceResultDriverName);
    } */

    public BigDecimal getRaceLength() {
        return raceLength;
    }

    public void setRaceLength(BigDecimal raceLength) {
        this.raceLength = raceLength;
    }

    public BigDecimal getRaceTrack() {
        return raceTrack;
    }

    public void setRaceTrack(BigDecimal raceTrack) {
        this.raceTrack = raceTrack;
    }

    public abstract String toString();

    public RaceStart getRaceStart() {
        return raceStart;
    }

    public String getRaceMode() {
        return raceMode;
    }

    public BigDecimal getTasoitus() {
        return tasoitus;
    }

    public void setTasoitus(BigDecimal tasoitus) {
        this.tasoitus = tasoitus;
    }

    public String getId() {
        return id;
    }

    /*
    public void setId(String id) {
        this.id = id;
    }*/


    public String getLid() {
        return lid;
    }

    public void setLid(String lid) {
        this.lid = lid;
    }

    public String getRacePlace() {
        return racePlace;
    }

    public void setRacePlace(String racePlace) {
        this.racePlace = racePlace;
    }

    public Date getRaceDate() {
        return raceStart.getDate();
    }

    /*
    public void setRaceDate(Date raceDate) {
        this.raceDate = raceDate;
    }*/

    public void setId() {
        try {
            StringBuffer sb = new StringBuffer();
            sb.append(lid);
            sb.append("_");
            if(getHorseProgNumber().intValue() < 10) {
                sb.append("0");
            }
            sb.append(getHorseProgNumber().toString());
            this.id = sb.toString();
        } catch(Exception e) {
            Log.write(e, "Horse.setId: Cannot set horseId");
        }
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setRaceMode(String raceMode) {
        this.raceMode = raceMode;
    }

    public String getTrackId() {
        if(trackId == null)
            initTrackId();
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public void initTrackId() {
        try {
            StringBuffer sb = new StringBuffer();
            sb.append(tasoitus.toString());

            sb.append(":");
            sb.append(raceTrack.toString());

            if (raceMode.contains("a")) {
                sb.append("a");
            }

            this.trackId = sb.toString();

        } catch (Exception e) {
            // TODO: ota käyttöön, kun kanta on ajettu uudelleen
            //e.printStackTrace();
            //Log.write(e);
        }
    }
}


