package hippos.util;

import hippos.RaceProgramHorse;
import hippos.math.AlphaNumber;
import hippos.math.racing.QuarterTime;
import hippos.utils.HorsesHelper;

import java.awt.desktop.PreferencesEvent;
import java.math.BigDecimal;
import java.security.AlgorithmParameterGenerator;
import java.util.*;

public class QuarterTimes extends QuarterTime {
    private BigDecimal qtLenght;

    private Mapper <QuarterTime> raceTypedMap = new Mapper<>();

    public QuarterTimes(String id, RaceProgramHorse raceProgramHorse) {
        this(id, raceProgramHorse, raceProgramHorse.getRaceLength().intValue());
    }

    public QuarterTimes(String id, RaceProgramHorse raceProgramHorse, int qtLenght) {
        this(id, raceProgramHorse, raceProgramHorse.getRaceLength().intValue(), null);
    }

    public QuarterTimes(String id, RaceProgramHorse raceProgramHorse, int qtLenght, BigDecimal trackPolePosProb) {
        super(raceProgramHorse);
        try {

            this.qtLenght = BigDecimal.valueOf(qtLenght);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void add(QuarterTime quarterTime) {
        try {
            BigDecimal tasoitus = quarterTime.getTasoitus();

            if (tasoitus.intValue() != 0) {
                addTasoitusErotus(quarterTime, tasoitus);
            }

            mapQuarterTime(quarterTime);

            super.add(quarterTime);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 'Mappaa' uuden väliajan lähtötyypin mukaan. Ei lisää, jos vanha parempi väliaika löytyy.
     *
     * @param quarterTime   Lisättävä väliaika
     *
     */
    private void mapQuarterTime(QuarterTime quarterTime) {
        try {
            mapRaceType(quarterTime);
            mapRaceMode(quarterTime);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*

     */
    private void mapRaceMode(QuarterTime quarterTime) {
        try {
            String raceType = quarterTime.getRaceMode();
            List raceTypeKey = Collections.singletonList(raceType);

            raceTypedMap.getOrCreate(raceTypeKey, new QuarterTime(getRaceProgramHorse())).add(quarterTime);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void mapRaceType(QuarterTime quarterTime) {
        try {
            String raceType = quarterTime.getRaceMode().contains("a") ? "a" : "t";
            List raceTypeKey = Collections.singletonList(raceType);

            raceTypedMap.getOrCreate(raceTypeKey, new QuarterTime(getRaceProgramHorse())).add(quarterTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
        Vähentää väliajasta tasoituksen vaikutukset. Olettaa että kaksi ensimmäistä väliaikaa ovat
        ovat paalulta mitattuja. Ei toimi loppuajoille.
     */
    private void addTasoitusErotus(QuarterTime quarterTime, BigDecimal tasoitusErotus) {
        try {
            BigDecimal aika = quarterTime.getAika().getBigDecimal();

            BigDecimal tasoitettuAika = HorsesHelper.tasoitusaika(aika.doubleValue(), qtLenght.doubleValue(), tasoitusErotus.doubleValue());

            quarterTime.getAika().setNumber(tasoitettuAika);

        } catch (NullPointerException e) {
            // Aika on null
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /*
    public String toString() {

        StringBuffer sb = new StringBuffer();

        if (getPropbabilty() != null) {
            sb.append(getPropbabilty() + "%");
        }

        sb.append(!super.isEmpty() ? super.getRecords() : "");

        if(getPalkinto().getCount() > 0) {
            sb.append(" " + getPalkinto().average(0, BigDecimal.ZERO) + "€/s");
        }

        return sb.toString();
    }*/

}
