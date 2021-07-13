package hippos;

import hippos.exception.UnvalidStartException;
import hippos.utils.HorsesHelper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RaceMode {
    private String racemode;

    public RaceMode(String horseRace, SubTime raceTime, BigDecimal raceLength, BigDecimal startNumber) throws UnvalidStartException {
        //try {
            StringBuffer sb = new StringBuffer();

            sb.append(horseRace);
            if(raceTime.getAlpha() != null) {
                if (raceTime.getAlpha().indexOf('m') >= 0) {
                    // Montelähtö
                    sb.append("m");
                }
                if (raceTime.getAlpha().indexOf('a') >= 0 || raceTime.getAlpha().indexOf('L') >= 0) {
                    // linjalähdöt muuttetaan ajolähdöiksi
                    sb.append("a");
                }
            }
            if(startNumber.intValue() < 20) {
                sb.append(HorsesHelper.raceLengthId(raceLength));
            } else if(startNumber.intValue() < 25) {
                sb.append("kl");
            } else if(startNumber.intValue() >= 50) {
                // nuoret
                sb.append("kl");
            } else {
                // Opetuslähtö
                //System.out.println("RaceMode.RaceMode Opetuslähtö?: " + startNumber.intValue());
                throw new UnvalidStartException("Opetuslähtö");
            }

            this.racemode = sb.toString();

            /** etsii myös pienet kirjaimet, outoa??
             *
            if(racemode.lastIndexOf('L') > 0 || racemode.lastIndexOf('K') > 0) {
                //Rodun kirjaimen pitää olla alussa
                Log.write("Incorrect racemode '" + racemode + "'");
            }
             */

            //System.out.println("RaceMode.RaceMode: " +  raceTime.getNumber() + "" + racemode);
        //} catch(Exception e) {
        //    Log.write("Failed to construct RaceMode(" + horseRace + ", " + raceTime + ", " + raceLength + ")");
        //}
    }

    public RaceMode(String racemode) {
        this.racemode = racemode;
    }

    public String toString() {
        return racemode;
    }

    public List toList() {

        return Collections.singletonList(racemode);
    }
}
