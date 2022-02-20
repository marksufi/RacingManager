package hippos.math;

import utils.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by IntelliJ IDEA.
 * User: marktolo
 * Date: Nov 2, 2005
 * AlphaNumber: 11:23:28 PM
 * To change this template use Options | File Templates.
 */
public class AlphaNumber implements Comparable {
    private BigDecimal number = null;
    private String alpha = null;

    public AlphaNumber() { }

    public AlphaNumber(AlphaNumber alphaNumber) {
        if(alphaNumber != null) {
            this.number = alphaNumber.number;
            this.alpha = alphaNumber.alpha;
        }
    }

    public AlphaNumber(String str) {
        //System.out.println("AlphaNumber.AlphaNumber(" + str + ")");
        try {
            if(str != null) {
                str = str.trim();
                StringBuffer numbers = new StringBuffer();
                StringBuffer alphas = new StringBuffer();
                boolean decimal = false;
                char c;
                int scale = 0;
                for(int i = 0; i < str.length(); i++) {
                    switch((c = str.charAt(i))) {
                        case '0': numbers.append(c); if(decimal == true) scale++; break;
                        case '1': numbers.append(c); if(decimal == true) scale++; break;
                        case '2': numbers.append(c); if(decimal == true) scale++; break;
                        case '3': numbers.append(c); if(decimal == true) scale++; break;
                        case '4': numbers.append(c); if(decimal == true) scale++; break;
                        case '5': numbers.append(c); if(decimal == true) scale++; break;
                        case '6': numbers.append(c); if(decimal == true) scale++; break;
                        case '7': numbers.append(c); if(decimal == true) scale++; break;
                        case '8': numbers.append(c); if(decimal == true) scale++; break;
                        case '9': numbers.append(c); if(decimal == true) scale++; break;
                        case ',': decimal = true; scale = 0; break;
                        case '.': decimal = true; scale = 0; break;
                        case '\t': break;
                        default: alphas.append(c);
                    }
                }
                if(alphas.length() > 0) {
                    alpha = alphas.toString();
                }
                if(numbers.length() > 0) {
                    BigDecimal divider = new BigDecimal(Math.pow(10, (double)scale));
                    number = new BigDecimal(numbers.toString());
                    number = number.divide(divider, scale, RoundingMode.HALF_UP);
                }
            }
        } catch (Exception e) {
            Log.write(e);
        }
    }

    public AlphaNumber(BigDecimal number) {
        this.number = number;
    }

    public AlphaNumber(BigDecimal number, String alpha) {
        this.number = number;
        this.alpha = alpha;
    }

    public AlphaNumber(double newValue) {
        try {
            this.number = BigDecimal.valueOf(newValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AlphaNumber add(AlphaNumber alphaNumber) {
        if((alpha == null && alphaNumber.alpha == null) || alpha.equals(alphaNumber.alpha)) {
            if(number == null)
                number = alphaNumber.number;
            else if(alphaNumber.number != null)
                number = number.add(alphaNumber.number);
        }
        return this;
    }


    public int compareTo(Object o) {
        AlphaNumber alphaNumber = (AlphaNumber)o;

        if(this.hashCode() == alphaNumber.hashCode())
            return 0;

        if(MathHelper.xor(number == null, alphaNumber.number == null)) {
            // Jompikumpi on null, mutta ei molemmat
            if (number == null) return 1;
            if (alphaNumber.number == null) return -1;
        }

        try {
            // molemmat ei nulleja
            int cmp = number.compareTo(alphaNumber.number);
            if(cmp != 0)
                return cmp;

            // yhtä suuria, vertailu korjaimilla

        } catch (NullPointerException e) {
            // molemmat tyhjiä, vertailu kirjaimilla
        }

        if(MathHelper.xor(alpha != null, alphaNumber.alpha != null)) {
            // Jompikumpi on null, mutta ei molemmat
            if (alpha == null) return 1;
            if (alphaNumber.alpha == null) return -1;
        }

        try {
            return alpha.compareTo(alphaNumber.alpha);

        } catch (NullPointerException e) {
            // Molemmat tyhjiä

        }

        return -1;
    }

    /*
    public boolean equals(AlphaNumber alphaNumber) {
        return this.alpha != null && alphaNumber.alpha != null && this.alpha.equals(alphaNumber.alpha);
    }*/

    public BigDecimal getBigDecimal() {
        return number;
    }

    public boolean isComparable() {
        if( number != null && number.intValue() > 0)
            return true;

        return false;
    }

    public String getAlpha() {
        return alpha;
    }

    public BigDecimal getNumber() {
        return number;
    }

    public AlphaNumber subtract(AlphaNumber money) {
        if(number != null && money != null && money.getNumber() != null);
            this.number = this.number.subtract(money.getNumber());

        return this;
    }

    public String substring(int beginindex) {
        StringBuffer sb = new StringBuffer();
        sb.append(number);
        sb.append(alpha != null ? alpha.substring(beginindex) : "");
        return sb.toString();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        if(number != null) {
            sb.append(number.toString());
        }
        if(alpha != null)
            sb.append(alpha);
        return sb.toString();
    }

    public void add(BigDecimal prize) {
        if(this.number != null && prize != null)
            this.number = this.number.add(prize);
    }

    public void setNumber(BigDecimal number) {
        this.number = number;
    }

    public void setAlpha(String alpha) {
        this.alpha = alpha;
    }
}
