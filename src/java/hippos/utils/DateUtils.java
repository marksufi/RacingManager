package hippos.utils;

import hippos.HarnessApp;
import hippos.SubStart;
import hippos.ValueHorse;
import utils.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by marktolo on 15.9.2014.
 */
public class DateUtils {
    /**
     * Laskee kahden eri päivämää erotuksen päivissä
     *
     * @param date      vertailupäivämäärä1
     *        exDate    vertailupäivämäärä2
     *
     * @return erotuksen päivissä
     */
    public static int getDayDiff(Date date, Date exDate) throws ArithmeticException {
        if(date == null || exDate == null)
            throw new ArithmeticException("Horseshelper.getDayDiff(" + date + ", " + exDate + "): Null value given");
        long diff = (date.getTime() - exDate.getTime()) / 86400000L;
        return (int)diff;
    }

    //public static double getWeekDiff(Date date, Date exDate) throws ArithmeticException {
    public static double getWeekDiff(Date date, Date exDate) {
        //if(date == null || exDate == null) throw new ArithmeticException("Horseshelper.getMonthDiff(" + date + ", " + exDate + "): Null value given");
        if(date == null || exDate == null)
            return Double.NaN;
        return getWeekDiff(date, exDate, -1);
    }

    public static double getWeekDiff(Date date, Date exDate, double max) {
        if(date == null || exDate == null)
            return max;
        double diff = (date.getTime() - exDate.getTime()) / 86400000.00 / 7.00;
        diff = BigDecimal.valueOf(diff).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return (diff < max || max < 0) ? diff : max;
    }

    public static double getMonthDiff(Date date, Date exDate) throws ArithmeticException {
        if(date == null || exDate == null) throw new ArithmeticException("Horseshelper.getMonthDiff(" + date + ", " + exDate + "): Null value given");
        double diff = (date.getTime() - exDate.getTime()) / 86400000.00 / 30.40;
        return diff;
    }

    public static Date rollDays(Date date, int days) {
        Date rolledDate = new Date();

        long time = date.getTime();
        time += 86400000L * days;

        rolledDate.setTime(time);

        if(days < 0) {
            if(rolledDate.after(date))
                Log.write("DateUtils.rollDays FAILURE: (" + date + ", " + date + ") = " + rolledDate );
        } else {
            if(rolledDate.before(date))
                Log.write("DateUtils.rollDays FAILURE: (" + date + ", " + date + ") = " + rolledDate );
        }

        return rolledDate;
    }

    public static Date rollWeeks(final Date date, int weeks) {
        Date rolledDate = new Date();

        long time = date.getTime();
        time += 86400000L * 7 * weeks;

        rolledDate.setTime(time);

        return rolledDate;
    }

    public static Date rollMonths(final Date date, int months) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.roll(Calendar.MONTH, months);

        return c.getTime();
    }

    public static Date rollYears(final Date date, int years) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.roll(Calendar.YEAR, years);

        return c.getTime();
    }

    public static double getStartInterval(ValueHorse valueHorse, SubStart subStart) {
        Date date = valueHorse.getRaceProgramHorse().getRaceProgramStart().getDate();
        Date exDate = subStart.getDate();
        int index = valueHorse.getIndex();
        int exIndex = subStart.getIndex();

        double weekDiff = getWeekDiff(date, exDate, HarnessApp.WEEKDIFF_MAX);
        double startInterval = weekDiff / (double) (index - exIndex);

        return startInterval;

    }

    public static double getSeasonValue(java.sql.Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        int dayOfYear = c.get(Calendar.DAY_OF_YEAR);
        double cos = Math.cos(Math.toRadians((double) dayOfYear));

        return cos;

    }

    public static double getSeasonValue(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        int dayOfYear = c.get(Calendar.DAY_OF_YEAR);
        double cos = Math.cos(Math.toRadians((double) dayOfYear));

        return cos;
    }

    public static java.sql.Date toSQLDate(Date startDate) {
        return startDate != null ? new java.sql.Date(startDate.getTime()) : null;
    }

    public static int getMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        int month = c.get(Calendar.MONTH) + 1;

        return month;
    }

    /**
     * @return gab as weeks limited to 10
     */
    public static String getGapString(int days) throws ArithmeticException {
        if(days >= 0) {
            BigDecimal gap = BigDecimal.valueOf(days);

            BigDecimal gapWeeks = gap.divide(new BigDecimal(7), 0, BigDecimal.ROUND_HALF_UP);
            if(gapWeeks.intValue() > 4) {
                BigDecimal gapMonths = gap.divide(new BigDecimal(28.5), 0, BigDecimal.ROUND_HALF_UP);
                if(gapMonths != null && gapMonths.compareTo(BigDecimal.valueOf(2)) > 0){
                    return "3m";
                } else {
                    return gapMonths.toString() + "m";
                }
            }
            return gapWeeks.toString() + "w";
        }
        throw new ArithmeticException("A gap value of a substart is less than zero: " + days);
    }

    //14-FEB-14
    public static long getMillis(String sDate) {
        Calendar c = Calendar.getInstance();
        String [] parts = sDate.split("-");

        int d = Integer.parseInt(parts[0]);
        int m = parseMonth(parts[1]);
        int y = Integer.parseInt(parts[2]) + 2000;


        c.set(y, m, d, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);

        return c.getTimeInMillis();
    }

    public static int parseMonth(String month) {
        switch( month.toUpperCase() ) {
            case "JAN": return 0;
            case "FEB": return 1;
            case "MAR": return 2;
            case "APR": return 3;
            case "MAY": return 4;
            case "JUN": return 5;
            case "JUL": return 6;
            case "AUG": return 7;
            case "SEP": return 8;
            case "OCT": return 9;
            case "NOV": return 10;
            case "DEC": return 11;
        };
        return -1;
    }

    /**
     * Palauttaa päivämäärälle vuodenaikaa vastaavan radiaaniarvon
     *
     * @param date  pävämäärä, jolle vuodenaika lasketaan
     *
     * @return  vuodenaika radiaaneina
     *          31.12 = 1.0
     *          31.3 = 0.0
     *          31.6 = -1.0
     *          30.9 = 0.0
     */
    public static double season(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        double dayOfYear = c.get(Calendar.DAY_OF_YEAR);
        dayOfYear *= (360.00/365.00);

        double radians = Math.toRadians(dayOfYear);

        double cos = Math.cos(radians);

        return cos;
        //return BigDecimal.valueOf(cos).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public static void main(String args []) {
        System.out.println("DateUtils.main: " + getMillis("05-OCT-13"));

        Date date = Calendar.getInstance().getTime();
        System.out.println("hippos.utils.DateUtils.main:" + date);

        date = rollWeeks(date, -5);
        System.out.println("hippos.utils.DateUtils.main:" + date);

        date = rollDays(date, 500);
        System.out.println("hippos.utils.DateUtils.main:" + date);

        date = rollDays(date, -1000);
        System.out.println("hippos.utils.DateUtils.main:" + date);

        date = rollWeeks(date, 100);
        System.out.println("hippos.utils.DateUtils.main:" + date);

        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        Calendar c3 = Calendar.getInstance();
        Calendar c4 = Calendar.getInstance();
        Calendar c5 = Calendar.getInstance();
        Calendar c6 = Calendar.getInstance();

        c1.set(2021, 0, 1); //-> -1
        c2.set(2021, 3, 1); //->  0
        c3.set(2021, 6, 1); //->  1
        c4.set(2021, 9, 1); //->  0
        c6.set(2022, 11, 31); //-> -1


        System.out.println(c1.getTime() + " => " + season(c1.getTime()));
        System.out.println(c2.getTime() + " => " + season(c2.getTime()));
        System.out.println(c3.getTime() + " => " + season(c3.getTime()));
        System.out.println(c4.getTime() + " => " + season(c4.getTime()));
        System.out.println(c5.getTime() + " => " + season(c5.getTime()));
        System.out.println(c6.getTime() + " => " + season(c6.getTime()));
    }

}
