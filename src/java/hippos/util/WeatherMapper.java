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

public class WeatherMapper implements ChangeMapper {
    private static Mapper<SumReg> weatherMapper = new Mapper<>();

    private static List getWeatherKey(SubStart subStart, RaceProgramHorse raceProgramHorse) throws RegressionModelException {

        try {
            if(subStart.getWeather().equals("hk"))
                throw new RegressionModelException();

            if(subStart.getWeather().length() < 2)
                throw new RegressionModelException();

            List key = new ArrayList();

            key.add(subStart.getWeather());
            key.add("hk");

            return key;

        } catch (Exception e) {
            throw new RegressionModelException();
        }
    }

    private static List getWeatherKey(SubStart subStart, SubStart targetSubStart) throws RegressionModelException {

        try {
            if(subStart.getWeather().equals(targetSubStart.getWeather()))
                throw new RegressionModelException();

            List key = new ArrayList();

            key.add(subStart.getWeather());
            key.add(targetSubStart.getWeather());

            return key;
        } catch (RegressionModelException e) {
            throw e;
        } catch (NullPointerException e) {
            throw new RegressionModelException();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RegressionModelException();
        }
    }

    public double getChange(SubStart subStart, RaceProgramHorse raceProgramHorse) {

        try {
            List weatherKey = getWeatherKey(subStart, raceProgramHorse);

            return weatherMapper.get(weatherKey).getChange().doubleValue();
        } catch (RegressionModelException e) {
            // Avaimen luonnissa ongelmia
        } catch (NullPointerException e) {
            // Avainta ei löytynyt
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public void addChange(SubStart subStart, SubStart targetSubStart) {
        try {
            List seasonKey = getWeatherKey(subStart, targetSubStart);

            SubTime subTime = subStart.getSubTime();
            SubTime targetSubTime = targetSubStart.getSubTime();

            BigDecimal xTime = subTime.getBigDecimal();
            BigDecimal yTime = targetSubTime.getBigDecimal();

            String targetRaceMode = targetSubTime.getAlpha();
            String subRaceMode = subTime.getAlpha();

            if (targetRaceMode.equals(subRaceMode)) {
                weatherMapper.getOrCreate(seasonKey, new SumReg()).add(xTime, yTime);

                Collections.reverse(seasonKey);

                weatherMapper.getOrCreate(seasonKey, new SumReg()).add(yTime, xTime);
            }
        } catch (RegressionModelException e) {
            // Avaimet samoja
        } catch (NullPointerException e) {
            // aika puuttuu
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
