package hippos;

import hippos.exception.UnvalidStartException;
import hippos.utils.HorsesHelper;
import utils.Log;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RaceMode {
    private String racemode;

    public RaceMode(String horseRace, String raceType, SubTime raceTime, BigDecimal raceLength, BigDecimal startNumber) throws UnvalidStartException {
        try {
            StringBuilder sb = new StringBuilder();

            // rodun tunnus L tai K
            sb.append(horseRace);

            // Monte,  jos 'm' löytyy ajasta
            if (raceTime.getAlpha() != null) {
                if (raceTime.getAlpha().indexOf('m') >= 0) {
                    // Montelähtö
                    sb.append("m");
                }
            }

            // Ryhmäajon tunnus 'a'
            if (raceType != null) {
                // Historiatiedoista ajolähdön tunnus luetaan raceType parametrista
                if(raceType.contains("R")) {
                    sb.append("a");
                }
            } else {
                // Lähtölistasta tunnus luetaan ajasta
                if (raceTime.getAlpha() != null && raceTime.getAlpha().contains("a")) {
                    sb.append("a");
                }
            }

            // Matkan tai koelähdön tunnus
            if (startNumber.intValue() < 20) {
                sb.append(HorsesHelper.raceLengthId(raceLength));
            } else if (startNumber.intValue() < 25) {
                sb.append("kl");
            } else if (startNumber.intValue() >= 50) {
                // nuoret
                sb.append("kl");
            } else {
                // Opetuslähtö
                throw new UnvalidStartException("Opetuslähtö");
            }

            this.racemode = sb.toString();
        } catch (UnvalidStartException e) {
            throw e;
        } catch (Exception e) {
            Log.write(e);
        }

    }

    public RaceMode(String racemode) {
        this.racemode = racemode;
    }

    public String toString() {
        return racemode;
    }
}
