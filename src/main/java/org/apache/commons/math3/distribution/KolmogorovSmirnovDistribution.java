package org.apache.commons.math3.distribution;

import java.io.Serializable;
import java.lang.reflect.Array;
import org.apache.commons.math3.dfp.Dfp;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.fraction.BigFraction;
import org.apache.commons.math3.fraction.BigFractionField;
import org.apache.commons.math3.fraction.FractionConversionException;
import org.apache.commons.math3.linear.Array2DRowFieldMatrix;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.FieldMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.util.FastMath;

public class KolmogorovSmirnovDistribution implements Serializable {
    private static final long serialVersionUID = -4670676796862967187L;

    /* renamed from: n */
    private int f158n;

    public KolmogorovSmirnovDistribution(int n) throws NotStrictlyPositiveException {
        if (n <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.NOT_POSITIVE_NUMBER_OF_SAMPLES, Integer.valueOf(n));
        }
        this.f158n = n;
    }

    public double cdf(double d) throws MathArithmeticException {
        return cdf(d, false);
    }

    public double cdfExact(double d) throws MathArithmeticException {
        return cdf(d, true);
    }

    public double cdf(double d, boolean exact) throws MathArithmeticException {
        double ninv = 1.0d / ((double) this.f158n);
        double ninvhalf = 0.5d * ninv;
        if (d <= ninvhalf) {
            return 0.0d;
        }
        if (ninvhalf < d && d <= ninv) {
            double res = 1.0d;
            double f = (2.0d * d) - ninv;
            for (int i = 1; i <= this.f158n; i++) {
                res *= ((double) i) * f;
            }
            return res;
        } else if (1.0d - ninv <= d && d < 1.0d) {
            return 1.0d - (2.0d * FastMath.pow(1.0d - d, this.f158n));
        } else {
            if (1.0d <= d) {
                return 1.0d;
            }
            return exact ? exactK(d) : roundedK(d);
        }
    }

    private double exactK(double d) throws MathArithmeticException {
        int k = (int) FastMath.ceil(((double) this.f158n) * d);
        BigFraction pFrac = createH(d).power(this.f158n).getEntry(k - 1, k - 1);
        for (int i = 1; i <= this.f158n; i++) {
            pFrac = pFrac.multiply(i).divide(this.f158n);
        }
        return pFrac.bigDecimalValue(20, 4).doubleValue();
    }

    private double roundedK(double d) throws MathArithmeticException {
        int k = (int) FastMath.ceil(((double) this.f158n) * d);
        FieldMatrix<BigFraction> HBigFraction = createH(d);
        int m = HBigFraction.getRowDimension();
        RealMatrix H = new Array2DRowRealMatrix(m, m);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                H.setEntry(i, j, HBigFraction.getEntry(i, j).doubleValue());
            }
        }
        double pFrac = H.power(this.f158n).getEntry(k - 1, k - 1);
        for (int i2 = 1; i2 <= this.f158n; i2++) {
            pFrac *= ((double) i2) / ((double) this.f158n);
        }
        return pFrac;
    }

    private FieldMatrix<BigFraction> createH(double d) throws NumberIsTooLargeException, FractionConversionException {
        BigFraction h;
        int k = (int) FastMath.ceil(((double) this.f158n) * d);
        int m = (k * 2) - 1;
        double hDouble = ((double) k) - (((double) this.f158n) * d);
        if (hDouble >= 1.0d) {
            throw new NumberIsTooLargeException(Double.valueOf(hDouble), Double.valueOf(1.0d), false);
        }
        try {
            h = new BigFraction(hDouble, 1.0E-20d, Dfp.RADIX);
        } catch (FractionConversionException e) {
            try {
                h = new BigFraction(hDouble, 1.0E-10d, Dfp.RADIX);
            } catch (FractionConversionException e2) {
                h = new BigFraction(hDouble, 1.0E-5d, Dfp.RADIX);
            }
        }
        BigFraction[][] Hdata = (BigFraction[][]) Array.newInstance(BigFraction.class, m, m);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                if ((i - j) + 1 < 0) {
                    Hdata[i][j] = BigFraction.ZERO;
                } else {
                    Hdata[i][j] = BigFraction.ONE;
                }
            }
        }
        BigFraction[] hPowers = new BigFraction[m];
        hPowers[0] = h;
        for (int i2 = 1; i2 < m; i2++) {
            hPowers[i2] = h.multiply(hPowers[i2 - 1]);
        }
        for (int i3 = 0; i3 < m; i3++) {
            Hdata[i3][0] = Hdata[i3][0].subtract(hPowers[i3]);
            Hdata[m - 1][i3] = Hdata[m - 1][i3].subtract(hPowers[(m - i3) - 1]);
        }
        if (h.compareTo(BigFraction.ONE_HALF) == 1) {
            Hdata[m - 1][0] = Hdata[m - 1][0].add(h.multiply(2).subtract(1).pow(m));
        }
        for (int i4 = 0; i4 < m; i4++) {
            for (int j2 = 0; j2 < i4 + 1; j2++) {
                if ((i4 - j2) + 1 > 0) {
                    for (int g = 2; g <= (i4 - j2) + 1; g++) {
                        Hdata[i4][j2] = Hdata[i4][j2].divide(g);
                    }
                }
            }
        }
        return new Array2DRowFieldMatrix(BigFractionField.getInstance(), Hdata);
    }
}
