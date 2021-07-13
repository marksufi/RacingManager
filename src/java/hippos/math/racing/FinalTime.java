package hippos.math.racing;

import hippos.RaceProgramHorse;
import hippos.math.AlphaNumber;

import java.math.BigDecimal;

public class FinalTime extends QuarterTime {

    public FinalTime(RaceProgramHorse raceProgramHorse) {
        super(raceProgramHorse);
    }

    public FinalTime(RaceProgramHorse raceProgramHorse, BigDecimal loppuaika, String racemode, BigDecimal tasoitus) {
        this(raceProgramHorse);
        setAika(new AlphaNumber(loppuaika, racemode));
        setTasoitus(tasoitus);
        setRaceMode(racemode);
    }

    public BigDecimal tasoitusErotus(BigDecimal tasoitus) {
        return tasoitus.subtract(getRaceProgramHorse().getTasoitus());
    }

}
