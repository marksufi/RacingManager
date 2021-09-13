package hippos.util;

import hippos.RaceProgramHorse;
import hippos.SubStart;
import hippos.SubTime;
import hippos.math.SumReg;
import hippos.exception.RegressionModelException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RaceTypeMapper implements ChangeMapper {
    private static final boolean useMonteKey = false;

    private static Mapper<SumReg> raceTypeMapper = new Mapper<>();

    @Override
    public double getChange(SubStart subStart, RaceProgramHorse raceProgramHorse) {

        try {
            List raceModeKey = getRaceModeKey(subStart, raceProgramHorse);

            return raceTypeMapper.get(raceModeKey).getChange().doubleValue();

        } catch (RegressionModelException e) {
            // Avaimen luonnissa ongelmia
        } catch (NullPointerException e) {
            // Avainta ei löytynyt
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public void addChange(SubStart subStart, SubStart targetSubStart) {
        try {
            List raceModeKey = getRaceModeKey(subStart, targetSubStart);

            SubTime subTime = subStart.getSubTime();
            SubTime targetSubTime = targetSubStart.getSubTime();

            BigDecimal xTime = subTime.getBigDecimal();
            BigDecimal yTime = targetSubTime.getBigDecimal();

            raceTypeMapper.getOrCreate(raceModeKey, new SumReg()).add(xTime, yTime);

            Collections.reverse(raceModeKey);

            raceTypeMapper.getOrCreate(raceModeKey, new SumReg()).add(yTime, xTime);
        } catch (RegressionModelException e) {
            // Avaimet samoja
        } catch (NullPointerException e) {
            // aika puuttuu
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List getRaceModeKey(SubStart subStart, SubStart targetSubStart) throws RegressionModelException {

        try {
            //String newRaceMode = subTime.getAlpha() + (subTime.getX() ? "x" : "");
            String newRaceMode = subStart.getSubTime().getAlpha();
            String targetRaceMode = targetSubStart.getSubTime().getAlpha();

            if(useMonteKey == false) {
                // poistaa m(monte) kirjaimen lähtötavasta

                newRaceMode = newRaceMode.replaceAll("m", "");
                targetRaceMode = targetRaceMode.replaceAll("m", "");
            }

            if (newRaceMode.equals(targetRaceMode))
                throw new RegressionModelException();

            List key = new ArrayList();
            key.add(newRaceMode);
            key.add(targetRaceMode);

            return key;
        } catch (Exception e) {
            throw  new RegressionModelException();
        }
    }

    private List getRaceModeKey(SubStart subStart, RaceProgramHorse raceProgramHorse) throws RegressionModelException {

        try {
            //String newRaceMode = subTime.getAlpha() + (subTime.getX() ? "x" : "");
            String newRaceMode = subStart.getSubTime().getAlpha();
            String targetRaceMode = raceProgramHorse.getRaceMode();

            if(useMonteKey == false) {
                // pointaas m(monte) kirjaimen lähtötavasta

                newRaceMode = newRaceMode.replaceAll("m", "");
                targetRaceMode = targetRaceMode.replaceAll("m", "");
            }

            if (newRaceMode.equals(targetRaceMode))
                throw new RegressionModelException();

            if(newRaceMode.contains("kl")) {
                //koelähtö
                try {
                    int subindex = raceProgramHorse.getSubStartList().indexOf(subStart);
                    if (subindex > 0) {
                        // Vanha koelähtö
                        newRaceMode = newRaceMode.replaceAll("kl", "ke");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            List key = new ArrayList();
            key.add(newRaceMode);
            key.add(targetRaceMode);

            return key;
        } catch (Exception e) {
            throw new RegressionModelException();
        }
    }


}
