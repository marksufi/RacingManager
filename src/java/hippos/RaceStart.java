package hippos;

import hippos.exception.UnvalidStartException;
import hippos.io.RaceProgramFile;
import hippos.io.RaceResultFile;
import hippos.utils.DateUtils;
import hippos.utils.HorsesHelper;
import utils.Log;

import java.math.BigDecimal;
import java.util.*;

public abstract class RaceStart {

    String id;
    private String fileId;
    private BigDecimal startNumber;
    private String raceStartMode;
    private String raceMode;
    private String horseRace;
    private Date date;
    private String raceLiteral;
    private TreeSet raceLength = new TreeSet();

    public String getRaceMode() {
        return raceMode;
    }

    /**
     * Muodostaa annetuista lähdön tiedoista aikalyhenteen
     *
     * @return Kaly, Lke
     *          K/L = kylmä-/lämminveriset
     *          a, , a, m = ajo-/tasoitus-/linja-/montelähtö
     *          ly/ke/kp/pi = matkalyhennys
     *
     *
    public void setRaceMode(String racemode) {
        this.raceMode = racemode;

        StringBuffer raceMode = new StringBuffer();

        if(horseRace != null) {
            String horseMode = horseRace.toLowerCase();
            if(horseMode.indexOf("lämmin") >= 0) raceMode.append("L");
            if(horseMode.indexOf("kylmä") >= 0) raceMode.append("K");
            if(horseMode.indexOf("suomen") >= 0) raceMode.append("K");

        } else {
            Log.write(fileId + ": Failed to evaluate raceStartMode: horseRace = " + horseRace);
            return;
        }

        if(raceStartMode != null) {
            String racestartmode = raceStartMode.toLowerCase().trim();
            if(racestartmode.indexOf("monte") >= 0) {
                raceMode.append("m");
            }
            if(racestartmode.indexOf("tasoitus") >= 0) {

            } else if(racestartmode.indexOf("linja") >= 0) {
                raceMode.append("a");
            }
            else if(racestartmode.indexOf("ryhmä") >= 0) {
                raceMode.append("a");
            } else {
                Log.write(fileId + ":Unrecognizable start type: raceStartMode = " + raceStartMode);
            }
        } else {
            Log.write(fileId + " :Failed to evaluate raceStartMode: raceStartMode = " + raceStartMode);
            return;
        }

        if(raceLength != null) {
            raceMode.append(HorsesHelper.raceLengthId(raceLength));
        } else {
            Log.write(fileId + ": Failed to evaluate raceStartMode: raceLength = " + raceLength);
            return;
        }
        //System.out.println(" => " + raceMode.toString());

        this.raceMode = raceMode.toString();
        //return raceMode.toString();


    }*/

    public BigDecimal getStartNumber() {
        return startNumber;
    }

    /*
    public String setMode(String mode) {
        timeMode = new String();
        if(mode != null) {
            if(mode.toLowerCase().indexOf("monte") >= 0) {
                timeMode += "m";
            } else if(mode.toLowerCase().indexOf("ryhmä") >= 0) {
                timeMode += "a";
            } else if(mode.toLowerCase().indexOf("tasoitus") >= 0) {
                timeMode += "";
            } else if(mode.toLowerCase().indexOf("linja") >= 0) {
                //timeMode += "L";
                timeMode += "a";
            } else {
                Log.write("Unknown horseRace raceStartMode: "+ mode);
            }
            this.raceStartMode = mode;
        }
        return this.raceStartMode;
    }*/

    public String setHorseRace(String race) throws UnvalidStartException {
        if(race != null) {
            //this.horseRace = race;
            if(race.toLowerCase().indexOf("lämminveriset") >= 0) {
                this.raceLiteral = "L";
                this.horseRace = race;
            }
            else if(race.toLowerCase().indexOf("kylmäveriset") >= 0) {
                this.raceLiteral = "K";
                this.horseRace = race;
            }
            else if(race.toLowerCase().indexOf("suomenhevoset") >= 0) {
                this.raceLiteral = "K";
                this.horseRace = race;
            } else if(race.toLowerCase().indexOf("yhdistetty") >= 0) {
                throw new UnvalidStartException();
            } else {
                Log.write("RaceStart:setHorseRace(): Unknown horse horseRace: " + race);
                //throw new Exception(fileId + ": Unknown horserace '" + horseRace + "'");
                //throw new UnvalidException();
            }
            return this.horseRace;
        } else {
            Log.write("RaceStart:setHorseRace(): Unknown horse horseRace: " + race);
            throw new UnvalidStartException();
        }
    }

    public void setId(RaceResultFile raceResultFile, BigDecimal number) {
        String filename = raceResultFile.getName();
        String fileId = filename.substring(0, filename.indexOf('.'));
        id = fileId + "_" + "CC";
        if(number.intValue() < 10)
            id += "0";
        id += number.toString();
    }

    public void setId(RaceProgramFile raceProgramFile, BigDecimal number) {
        StringBuffer sb = new StringBuffer();
        sb.append(raceProgramFile.getName().substring(0, raceProgramFile.getName().lastIndexOf('_')));
        sb.append("_CC");
        if(number.intValue() < 10)
            sb.append("0");
        sb.append(number.toString());
        id = sb.toString();
    }

    public String getId() {
        return id;
    }

    public String getHorseRace() {
        return horseRace;
    }

    /**
     * @return  tasoitusajo/ryhmäajo/linjalähtö
     */
    public String getRaceStartMode() {
        return raceStartMode;
    }

    public void setRaceStartMode(String raceStartMode) {
        this.raceStartMode = raceStartMode;
    }

    /**
     * @return  ly/aly/ke/ake...
     */
    public String getTimeMode() {
        return HorsesHelper.raceLengthId(getRaceLength(), raceStartMode);
    }

    public Date getDate() {
        return date;
    }

    public String getRaceLiteral() {
        return raceLiteral;
    }

    public BigDecimal getRaceLength() {
        try {
            return (BigDecimal) raceLength.first();
        } catch (Exception e) {
            e.printStackTrace();
            Log.write(e, id);

            return null;
        }
    }

    public void addRaceLength(BigDecimal raceLength) {
       this.raceLength.add(raceLength);
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setRaceMode(String raceMode) {
        this.raceMode = raceMode;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public void setStartNumber(BigDecimal startNumber) {
        this.startNumber = startNumber;
    }

    public java.sql.Date getSQLDate() {
        return DateUtils.toSQLDate(this.date);
    }
}


