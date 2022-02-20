package hippos.utils;

import hippos.RaceResultHorse;
import hippos.SubStart;
import hippos.ValueHorse;
import hippos.exception.IncorrectParameterException;
import hippos.math.AlphaNumber;
import utils.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Dec 9, 2005
 * Time: 12:51:01 AM
 * To change this template use Options | File Templates.
 */
public class HorsesHelper {
    public static BigDecimal WEEK_REGRESSION = new BigDecimal("0.1");
    public static BigDecimal START_PROGRESSION = new BigDecimal("0.3");


    public static String raceLengthId(BigDecimal length) {
        if(length != null) {
            int bd = length.intValue();
            if (bd >= 3000) return "pi";
            if (bd >= 2500) return "kp";
            if (bd >= 2000) return "ke";
            return "ly";
        }
        return null;
    }

    public static String raceLengthId(BigDecimal length, String timealpha) {
        //System.out.print("HorsesHelper.LengthAcronym( " + length + ", " + timealpha + " )");
        StringBuilder lengthAcronym = new StringBuilder();
        if(timealpha != null) {
            timealpha = timealpha.toLowerCase().trim();
            if(timealpha.indexOf("monte") >= 0) {
                // liian suuri erotus ~2,3s
                //lengthAcronym.append("m");
            }
            if(timealpha.contains("tasoitus")) {

            } else if(timealpha.contains("linja") || timealpha.contains("l")) {
                lengthAcronym.append("a");
            }
            else if(timealpha.contains("ryhmä") || timealpha.contains("a")) {
                lengthAcronym.append("a");
            }
        }
        lengthAcronym.append(raceLengthId(length));
        //System.out.println(" => " + lengthAcronym);
        return lengthAcronym.toString();
    }

    public static String SubLengthAcronym(BigDecimal length, String timealpha) {
        //System.out.print("HorsesHelper.LengthAcronym( " + length + ", " + timealpha + " )");
        StringBuilder lengthAcronym = new StringBuilder();
        if(timealpha != null) {
            timealpha = timealpha.toLowerCase().trim();
            if(timealpha.contains("m")) {
                lengthAcronym.append("m");
            }
            if(timealpha.contains("a") || timealpha.contains("l")) {
                lengthAcronym.append("a");
            }
        }
        lengthAcronym.append(raceLengthId(length));
        return lengthAcronym.toString();
    }

    /*
    public static BigDecimal getEqualizerTime(BigDecimal distanceDiff) {
        BigDecimal tasoitus = new BigDecimal("0");

        if(distanceDiff!= null) {
            tasoitus = distanceDiff.divide(new BigDecimal("20"), BigDecimal.ROUND_HALF_UP);
            tasoitus = tasoitus.multiply(new BigDecimal("0.8"));
        }

        return tasoitus;
    }*/

    /**
     * Laskee tasoitusajan ja lisää sen hevosen aikaan
     *
     * @param distance      Hevoset matka
     * @param raceDistance  Lähdön perusmatka
     * @return Hevosen ajan lisättynä tasoituksiin kuluneeseen aikaan
     */
    public static BigDecimal getEqualizerEffect(BigDecimal distance, BigDecimal raceDistance, BigDecimal startnumber, BigDecimal track) {
        BigDecimal equalizerEffect = BigDecimal.ONE;

        if(distance != null) {
            if(raceDistance != null) {
                if(!distance.equals(raceDistance)) {
                    BigDecimal tasoitus = distance.subtract(raceDistance);
                    while(tasoitus.compareTo(BigDecimal.ZERO) > 0) {
                        equalizerEffect = equalizerEffect.multiply(BigDecimal.valueOf(0.9));
                        tasoitus = tasoitus.subtract(BigDecimal.valueOf(10));
                    }
                }
            } else {
                Log.write(new Exception("Racedistance missing"));
            }
        } else {
            Log.write(new Exception("Horse racedistance missing"));
        }

        return equalizerEffect;
    }

    /**
     * Laskee tasoitusajan ja lisää sen hevosen aikaan
     *
     * @param distance      Hevoset matka
     * @param raceDistance  Lähdön perusmatka
     * @param time          Hevosen aika
     *
     * @return Hevosen ajan lisättynä tasoituksiin kuluneeseen aikaan
     */
    public static BigDecimal getEqualizerTimeDiff(BigDecimal distance, BigDecimal raceDistance, BigDecimal time, BigDecimal diff) {
        if(time!= null && diff !=null) {
            if(distance != null) {
                if(raceDistance != null) {
                    if(!distance.equals(raceDistance)) {
                        //tasoitus = getEqualizerTime(tasoitus);
                        BigDecimal hdTime = time.add(new BigDecimal("60.0"));
                        BigDecimal dTime = hdTime.multiply(new BigDecimal("1000")).divide(distance, 2, RoundingMode.HALF_UP);
                        BigDecimal rTime = hdTime.multiply(new BigDecimal("1000")).divide(raceDistance, 2, RoundingMode.HALF_UP);
                        diff = diff.add(rTime.subtract(dTime)).setScale(2, RoundingMode.HALF_UP);
                    }
                } else {
                    Log.write("Warning: Racedistance missing. Unable to calculate handigabTime. ");
                }
            } else {
                Log.write("Warning: horse race distanse missing. Unable to calculate handigabTime.");
            }
        }

        return diff;
    }

    public static BigDecimal getTasoitus(BigDecimal distance, BigDecimal raceDistance) {
        BigDecimal tasoitus = new BigDecimal(0);

        if(distance != null) {
            if(raceDistance != null) {
                if(!distance.equals(raceDistance)) {
                    tasoitus = distance.subtract(raceDistance);
                }
            }
        }

        return tasoitus;
    }

    /**
     * Palauttaa hevosen kokonaisajan, joka = aika * matkan pituus
     *
     * @param raceResultHorse   Hevonen, jolla kokonaisaika lasketaan.
     *
     * @return  Kokonaisaika, tai null jos kilometriaikaa ei ole.
     */
    public static BigDecimal getTotalTime(RaceResultHorse raceResultHorse) {
        BigDecimal totalTime = null;

        if(raceResultHorse != null && raceResultHorse.getRaceResultTime() != null) {
            BigDecimal  kmTime = raceResultHorse.getRaceResultTime().getBigDecimal();
            if(kmTime != null) {
                kmTime = kmTime.add(new BigDecimal(60));
                if(raceResultHorse.getRaceLength() != null) {
                    BigDecimal length = raceResultHorse.getRaceLength().divide(new BigDecimal("1000"), 3, RoundingMode.HALF_UP);
                    totalTime = kmTime.multiply(length).setScale(1, RoundingMode.HALF_UP);
                } else {
                    Log.write("A horseresult without length: " + raceResultHorse.getId());
                }
            }
        }

        return totalTime;
    }

    /*
    public static BigDecimal getKmDiff(BigDecimal diff, BigDecimal distance) {
        if(diff != null && distance != null) {
            try {
                BigDecimal newDiff = diff.multiply(new BigDecimal("1000.00"));
                newDiff = newDiff.divide(distance, 2, BigDecimal.ROUND_HALF_UP);
                diff = newDiff;
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        return diff;
    }*/

    /**
     * Muuntaa aikaeron kirjainmuotoon
     *
     * @param days muunnettavat päivät
     *
     * @return (lkm)w, (lkm)m, tai (lkm)y
     */
    public static String getGapAsWeeks(int days) {

            BigDecimal gap = BigDecimal.valueOf(days);//(BigDecimal)this.gap.getField();
            BigDecimal gapWeeks = gap.divide(new BigDecimal(7), 0, RoundingMode.HALF_UP);
            if(gapWeeks.intValue() > 5) {
                BigDecimal gapMonths = gap.divide(new BigDecimal(30), 0, RoundingMode.HALF_UP);
                if(gapMonths.intValue() > 12) {
                    BigDecimal gapYears = gap.divide(new BigDecimal(365), 0, RoundingMode.HALF_UP);
                    return gapYears + "y";
                }
                return gapMonths + "m";
            }
            return gapWeeks + "w";
    }

    public static int getMonthDiff(Date racedate, Date date) {
        int dateDiff = DateUtils.getDayDiff(racedate, date);

        return (int) (dateDiff / 28.5);
    }

    public static int getWeekDiff(Date racedate, Date date) {
        int dayDiff = DateUtils.getDayDiff(racedate, date);

        return dayDiff / 7;
    }

    public static boolean equals(Object str1, Object str2) {
        if(str1 != null){
            if(str2 != null) {
                return str1.equals(str2);
            }
            return false;
        } else return str2 == null;
    }

    /**
     * Palauttaa parametreistä suureman
     *
     * @param c2
     *
     * @return suurempi parametri
     */
    public static BigDecimal max(BigDecimal c1, BigDecimal c2) {
        if(c1 != null && c2 != null) {
            if(c1.compareTo(c2) >= 0)
                return c1;
            return c2;
        }
        return null;
    }

    /**
     * Muokkaa sellaisesta ajasta, jossa on useampia desimaaleja kuin yksi, oikeanlaisen kilometriajan.
     *
     * @param str
     *          2,04,9 x
     *          3.04,8 	x
     * @return
     *          64,9 x
     *          123,8 x
     */
    public static String modifyResultTime(String str) {
        try {
            int i, j, k, l, m;
            if(str != null && str.indexOf(',') > 0) {
                str = str.replace('.', ',');
                if((( i = str.indexOf(",")) < (j = str.lastIndexOf(",")))) {
                    String end = str.substring(j);
                    k = Integer.parseInt(str.substring(0, i));
                    l = Integer.parseInt(str.substring(i + 1, j));
                    m = ( 60 * ( k - 1 )) + l;
                    str = String.valueOf(m) + end;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    /*
    public static BigDecimal getMax(List valueList) {
        BigDecimal max = null;
        if(valueList != null) {
            Iterator itr = valueList.iterator();
            while(itr.hasNext()) {
                Value value = (Value)itr.next();
                BigDecimal itrv = value.getAverageNull(2);
                if(itrv != null) {
                    if(max != null){
                        if(max.compareTo(itrv) <= 0){
                            max = itrv;
                        }
                    } else {
                        max = itrv;
                    }
                }
            }
        }
        return max;
    }*/

    public static BigDecimal min(BigDecimal c1, BigDecimal c2) {
        if(c1 != null && c2 != null) {
            if(c1.compareTo(c2) <= 0)
                return c1;
            return c2;
        }
        return null;
    }

    public static BigDecimal getTimeProgress(Date racedate, Date date, int index) {
            int weekDiff = getWeekDiff(racedate, date);

            BigDecimal regression = BigDecimal.valueOf(weekDiff).multiply(WEEK_REGRESSION);
            BigDecimal progression = BigDecimal.valueOf(index + 1).multiply(START_PROGRESSION);

            return regression.subtract( progression );
    }

    public static BigDecimal getTimeProgress(Date racedate, Date date, int index, BigDecimal minLimit, BigDecimal maxLimit) {
        BigDecimal progress = getTimeProgress(racedate, date, index);

        if(progress != null) {
            if(minLimit != null && progress.compareTo(minLimit) < 0) {
                return minLimit;
            }
            if(maxLimit != null && progress.compareTo(maxLimit) > 0) {
                return maxLimit;
            }
        }
        return progress;
    }

    /*
    public static BigDecimal max(Value valueA, Value valueB) {
        if(valueA != null && valueB != null) {
            if(valueA.size() > 0 && valueB.size() > 0) {
                return max(valueA.average(2), valueB.average(2));
            }
        }
        return null;
    }*/

    /**
     * Laskee keskiarvon listan SetValue tyyppisille arvoille, jotka eivät ole null tai tyhjiä
     *
     * @return  Listan arvojen keskiarvo tai null, jos lista on tyhjä
     *
    public static BigDecimal getAverage(List valueList) {
        Iterator itr = valueList.iterator();
        BigDecimal avg = new BigDecimal(0);
        int size = 0;

        try {
            while(itr.hasNext()) {
                Value v = (Value)itr.next();
                if(v != null && v.size() > 0) {
                    BigDecimal b = v.average(2);
                    avg = avg.add(b);
                    size++;
                }
            }
            if(size > 0) {
                return avg.divide(BigDecimal.valueOf(size), 2, BigDecimal.ROUND_HALF_UP);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }*/

    public static BigDecimal getStartInterval(ValueHorse valueHorse, SubStart subStart) {
        try {
            int raceIndexDiff = valueHorse.getIndex() - subStart.getIndex();
            if(raceIndexDiff != 0) {
                int raceDayDiff = DateUtils.getDayDiff(valueHorse.getRaceProgramHorse().getRaceStart().getDate(), subStart.getDate());

                double raceStartInterval = (double) raceDayDiff / (double) raceIndexDiff;
                raceStartInterval /= 7.00;

                return BigDecimal.valueOf(raceStartInterval).setScale(2, RoundingMode.HALF_UP);
            }
        } catch (Exception e){
            Log.write(e);
        }
        return null;
    }

    public static BigDecimal getStartInterval(SubStart after, SubStart before) {
        double raceStartInterval = 0.00;
        try {
            int raceIndexDiff = after.getIndex() - before.getIndex();
            if(raceIndexDiff != 0) {
                int raceDayDiff = DateUtils.getDayDiff(after.getDate(), before.getDate());

                raceStartInterval = (double) raceDayDiff / (double) raceIndexDiff;
                raceStartInterval /= 7.00;
                /*
                if(raceStartInterval > 10.00) {
                    raceStartInterval = 10.00;
                }*/
            }
        } catch (Exception e){
            Log.write(e);
        }
        return BigDecimal.valueOf(raceStartInterval).setScale(2, RoundingMode.HALF_UP);
    }

    /*
        public static BigDecimal generateHandicap(ValueHorse valueHorse) {
        BigDecimal raceLength = valueHorse.getRaceLength();
        BigDecimal startRaceLength = valueHorse.getRaceProgramStart().getRaceLength();
        BigDecimal raceHandicap = null;

        if(raceLength != null && startRaceLength !=null) {
            raceHandicap = raceLength.subtract(startRaceLength);
            if(raceHandicap.compareTo(BigDecimal.ZERO) >= 0 && raceHandicap.compareTo(BigDecimal.valueOf(200)) < 0) {
                return raceHandicap;
            }
        }

        return raceHandicap;
    }
    */

    public static BigDecimal generateHandicap(ValueHorse valueHorse) {
        BigDecimal raceLength = valueHorse.getRaceProgramHorse().getRaceLength();
        BigDecimal startRaceLength = valueHorse.getRaceProgramHorse().getRaceProgramStart().getRaceLength();
        BigDecimal raceHandicap = null;

        if(raceLength != null && startRaceLength != null) {
            String raceMode = valueHorse.getRaceProgramHorse().getRaceMode();
            BigDecimal raceTrack = valueHorse.getRaceProgramHorse().getRaceTrack();
            raceHandicap = raceLength.subtract(startRaceLength);
            //if(raceMode.indexOf("a") < 0 && raceTrack.intValue() > 7) {
            //    raceHandicap = raceHandicap.add(BigDecimal.TEN);
            //}
        }

        return raceHandicap;
    }

    public static BigDecimal generateHandicap(RaceResultHorse raceResultHorse) {
        String raceMode = raceResultHorse.getRaceMode();
        BigDecimal raceTrack = raceResultHorse.getRaceTrack();
        BigDecimal raceHandicap = raceResultHorse.getTasoitus();

        if(raceHandicap != null) {
            if(!raceMode.contains("a") && raceTrack.intValue() > 7) {
                raceHandicap = raceHandicap.add(BigDecimal.TEN);
            }
        }
        return raceHandicap;
    }


    public static String getRaceMode(SubStart subStart) {
        StringBuilder racemode = new StringBuilder();

        String raceLiteral = subStart.getRaceProgramHorse().getRaceProgramStart().getRaceLiteral();
        String length = SubLengthAcronym(subStart.getRaceLength(), subStart.getSubTime().getAlpha());

        racemode.append(raceLiteral);
        racemode.append(length);

        /*if(raceLiteral == null || length == null) {
            System.out.print("");
        }*/
        return racemode.toString();

    }


    public static BigDecimal getHandicapTime(BigDecimal subRacetime, BigDecimal keyRaceLength, BigDecimal keyRaceHandicap) {
        if( subRacetime != null && keyRaceHandicap != null) {
            if(keyRaceHandicap.intValue() > 0) {
                BigDecimal handicaps = keyRaceHandicap.divide(BigDecimal.valueOf(20), 0, RoundingMode.HALF_UP);

                BigDecimal kmTime = subRacetime.add(BigDecimal.valueOf(60));
                BigDecimal handicapTime = kmTime.divide(BigDecimal.valueOf(50.00), 2, RoundingMode.HALF_UP);
                keyRaceLength = keyRaceLength.divide(BigDecimal.valueOf(1000.00), 2, RoundingMode.HALF_UP);

                handicapTime = handicapTime.divide(keyRaceLength, 2, RoundingMode.HALF_UP);
                handicapTime = handicapTime.multiply(handicaps);
                subRacetime = subRacetime.add(handicapTime);
            }
        }
        return  subRacetime;
    }

    public static BigDecimal tasoitusaika(double loppuaika, double tasoitusmatka, double tasoitus) {
        try {
            double perusmatka = tasoitusmatka-tasoitus;
            double kmAika = loppuaika + 60.0;

            tasoitusmatka /= 1000.0;
            perusmatka /= 1000.0;
            tasoitus /= 1000;

            double perusmatkaaika = kmAika * (perusmatka);
            tasoitus = kmAika * tasoitus;
            tasoitus /= tasoitusmatka;

            double perusaika = perusmatkaaika / perusmatka;
            double tasoitusaika = perusaika + tasoitus;

            tasoitusaika -= 60.0;

            return new BigDecimal(tasoitusaika).setScale(1, RoundingMode.HALF_DOWN);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static double getRaceLength(String racemode) throws IncorrectParameterException {

        try {
            if (racemode.contains("ly"))
                return 1600;
            if (racemode.contains("ke"))
                return 2100;
            if (racemode.contains("kp"))
                return 2600;
            if (racemode.contains("pi"))
                return 3100;
            if (racemode.contains("kl"))
                return 2100;

        } catch (Exception e) {
            Log.write(e);
        }
        throw new IncorrectParameterException("HorsesHelper.getRaceLength(" + racemode + ")");

    }

    public static void main(String args []) {

        System.out.println(HorsesHelper.tasoitusaika(27.6, 3160, 20));
        System.out.println(HorsesHelper.tasoitusaika(27.5, 3180, 40));
        System.out.println(HorsesHelper.tasoitusaika(28.0, 3160, 20));
        System.out.println(HorsesHelper.tasoitusaika(27.6, 3180, 40));
        System.out.println(HorsesHelper.tasoitusaika(28.3, 3160, 20));
        System.out.println(HorsesHelper.tasoitusaika(29.0, 3140, 0));
        System.out.println(HorsesHelper.tasoitusaika(29.1, 3140, 0));
        System.out.println(HorsesHelper.tasoitusaika(29.6, 3140, 0));
        System.out.println(HorsesHelper.tasoitusaika(30.0, 3160, 20));

        System.out.println();
        System.out.println(HorsesHelper.tasoitusaika(27.3, 2160, 20));
        System.out.println(HorsesHelper.tasoitusaika(25.8, 2200, 60));
        System.out.println(HorsesHelper.tasoitusaika(27.4, 2160, 20));
        System.out.println(HorsesHelper.tasoitusaika(26.7, 2180, 40));
        System.out.println(HorsesHelper.tasoitusaika(27.2, 2180, 40));

        System.out.println();
        System.out.println(HorsesHelper.tasoitusaika(15.3, 2140, 0));
        System.out.println(HorsesHelper.tasoitusaika(14.8, 2160, 20));
        System.out.println(HorsesHelper.tasoitusaika(15.5, 2140, 0));
        System.out.println(HorsesHelper.tasoitusaika(15.0, 2160, 20));
        System.out.println(HorsesHelper.tasoitusaika(15.3, 2160, 20));
        System.out.println(HorsesHelper.tasoitusaika(16.2, 2140, 00));

        System.out.println();
        System.out.println(HorsesHelper.tasoitusaika(17.5, 3140, 20));
        System.out.println(HorsesHelper.tasoitusaika(18.1, 3120, 0));
        System.out.println(HorsesHelper.tasoitusaika(17.7, 3140, 20));
        System.out.println(HorsesHelper.tasoitusaika(18.0, 3140, 20));
        System.out.println(HorsesHelper.tasoitusaika(17.5, 3160, 40));
        System.out.println(HorsesHelper.tasoitusaika(18.5, 3120, 0));

        System.out.println();
        System.out.println(HorsesHelper.tasoitusaika(24.4, 1620, 0));
        System.out.println(HorsesHelper.tasoitusaika(24.5, 1620, 0));
        System.out.println(HorsesHelper.tasoitusaika(24.6, 1620, 0));
        System.out.println(HorsesHelper.tasoitusaika(23.6, 1640, 20));
        System.out.println(HorsesHelper.tasoitusaika(23.7, 1640, 20));
        System.out.println(HorsesHelper.tasoitusaika(24.7, 1620, 0));

        System.out.println();
        System.out.println(HorsesHelper.tasoitusaika(21.5, 500, -40));
        System.out.println(HorsesHelper.tasoitusaika(18.5, 1000, -40));



    }

    public static AlphaNumber toProcents(BigDecimal number) {
        number = number.multiply(BigDecimal.valueOf(100.00));

        return new AlphaNumber(number.setScale(2, RoundingMode.HALF_UP), "%");
    }

    public static String getShortLocalityFromId(String id) {

        String lind = null;

        try {
            lind = id.substring(id.indexOf("_CE") + 3);

            for(int i = lind.indexOf("_"); i >= 0; i=-1)
                lind = lind.substring(0, i);

        } catch (Exception e) {
            Log.write(e);
            e.printStackTrace();

        }

        return lind;

    }

    /*
    public static double [] mapToDouble(List <Double> list) {
        try {
            double[] arr = list.stream().mapToDouble(Double::doubleValue).toArray();

            return arr;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }*/

    public static double [] mapToDouble(List <BigDecimal> list) {
        try {
            return list.stream().mapToDouble(BigDecimal::doubleValue).toArray();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String removeBlanks(String line) {
        StringBuilder sb = new StringBuilder();

        StringTokenizer st = new StringTokenizer(line);
        while (st.hasMoreElements()) {
            sb.append(st.nextToken());
        }
        return sb.toString();
    }
}

