package hippos.math.regression;

import hippos.math.DecimalValue;
import hippos.utils.StringUtils;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * Created by marktolo on 4.1.2015.
 */
public class HipposRegressionResults {
    private final BigDecimal B;
    private final BigDecimal Y;
    private final BigDecimal R;
    private final BigDecimal [] Yy;
    private final BigDecimal [] Xx;
    private BigDecimal result;
    private String key;

    public HipposRegressionResults(double b, double y, double r, double[][] yx) {
        this(b, y, r, yx, 2);
    }

    public HipposRegressionResults(double b, double y, double r, double[][] yx, int scale) {
        this.B = !Double.isNaN(b) ? BigDecimal.valueOf(b).setScale(scale, BigDecimal.ROUND_HALF_UP) : null;
        this.Y = !Double.isNaN(y) ? BigDecimal.valueOf(y).setScale(scale, BigDecimal.ROUND_HALF_UP) : null;
        this.R = !Double.isNaN(r) ? BigDecimal.valueOf(r).setScale(scale + 2, BigDecimal.ROUND_HALF_UP) : null;

        this.Yy = new BigDecimal[yx[0].length];
        this.Xx = new BigDecimal[yx[1].length];

        for(int i = 0; i < yx[0].length; i++)
            this.Yy[i] = !Double.isNaN(yx[0][i]) ? BigDecimal.valueOf(yx[0][i]).setScale(scale, BigDecimal.ROUND_HALF_UP) : null;

        for(int i = 0; i < yx[1].length; i++)
            this.Xx[i] = !Double.isNaN(yx[1][i]) ? BigDecimal.valueOf(yx[1][i]) : null;
    }

    public BigDecimal getB() {
        return B;
    }

    public BigDecimal getY() {
        return Y;
    }

    public BigDecimal getYB() {
        return getY();
        //return (Y != null && B != null) ? Y.subtract(B) : null;
    }

    public BigDecimal getR() {
        return R;
    }

    public BigDecimal[] getYy() {
        return Yy;
    }

    public BigDecimal[] getXx() {
        return Xx;
    }

    public double [] getYR() { return new double[] {Y!=null ? Y.doubleValue():Double.NaN, R != null ? R.doubleValue() : Double.NaN};};
    public double [] getYBR() {
        return getYR();
        //return new double[] {getYB()!=null ? getYB().doubleValue():Double.NaN, R != null ? R.doubleValue() : Double.NaN};
    };

    public String toString() {
        //return (Y != null && B != null) ? getKey() + "=" + getY().toString() + (Arrays.toString(Xx)) : null;
        return (Y != null && B != null) ? (StringUtils.toColumn(getY().toString(), 8) + Arrays.toString(Xx) + "\t") : null;
    }

    public void setResult(BigDecimal result) {
        this.result = result;
    }

    public DecimalValue getResult() {
        BigDecimal YB = getYB();
        BigDecimal R = getR();

        return new DecimalValue(YB, R);
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
