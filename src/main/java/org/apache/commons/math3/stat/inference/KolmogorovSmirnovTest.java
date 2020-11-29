package org.apache.commons.math3.stat.inference;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;
import org.apache.commons.math3.distribution.EnumeratedRealDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.exception.InsufficientDataException;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.TooManyIterationsException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.fraction.BigFraction;
import org.apache.commons.math3.fraction.BigFractionField;
import org.apache.commons.math3.fraction.FractionConversionException;
import org.apache.commons.math3.linear.Array2DRowFieldMatrix;
import org.apache.commons.math3.linear.FieldMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.util.CombinatoricsUtils;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;

public class KolmogorovSmirnovTest {
    protected static final double KS_SUM_CAUCHY_CRITERION = 1.0E-20d;
    protected static final int LARGE_SAMPLE_PRODUCT = 10000;
    protected static final int MAXIMUM_PARTIAL_SUM_COUNT = 100000;
    @Deprecated
    protected static final int MONTE_CARLO_ITERATIONS = 1000000;
    protected static final double PG_SUM_RELATIVE_ERROR = 1.0E-10d;
    @Deprecated
    protected static final int SMALL_SAMPLE_PRODUCT = 200;
    private final RandomGenerator rng;

    public KolmogorovSmirnovTest() {
        this.rng = new Well19937c();
    }

    @Deprecated
    public KolmogorovSmirnovTest(RandomGenerator rng2) {
        this.rng = rng2;
    }

    public double kolmogorovSmirnovTest(RealDistribution distribution, double[] data, boolean exact) {
        return 1.0d - cdf(kolmogorovSmirnovStatistic(distribution, data), data.length, exact);
    }

    public double kolmogorovSmirnovStatistic(RealDistribution distribution, double[] data) {
        checkArray(data);
        int n = data.length;
        double nd = (double) n;
        double[] dataCopy = new double[n];
        System.arraycopy(data, 0, dataCopy, 0, n);
        Arrays.sort(dataCopy);
        double d = 0.0d;
        for (int i = 1; i <= n; i++) {
            double yi = distribution.cumulativeProbability(dataCopy[i - 1]);
            double currD = FastMath.max(yi - (((double) (i - 1)) / nd), (((double) i) / nd) - yi);
            if (currD > d) {
                d = currD;
            }
        }
        return d;
    }

    public double kolmogorovSmirnovTest(double[] x, double[] y, boolean strict) {
        double[] xa;
        double[] ya;
        long lengthProduct = ((long) x.length) * ((long) y.length);
        if (lengthProduct >= 10000 || !hasTies(x, y)) {
            xa = x;
            ya = y;
        } else {
            xa = MathArrays.copyOf(x);
            ya = MathArrays.copyOf(y);
            fixTies(xa, ya);
        }
        if (lengthProduct < 10000) {
            return exactP(kolmogorovSmirnovStatistic(xa, ya), x.length, y.length, strict);
        }
        return approximateP(kolmogorovSmirnovStatistic(x, y), x.length, y.length);
    }

    public double kolmogorovSmirnovTest(double[] x, double[] y) {
        return kolmogorovSmirnovTest(x, y, true);
    }

    public double kolmogorovSmirnovStatistic(double[] x, double[] y) {
        return ((double) integralKolmogorovSmirnovStatistic(x, y)) / ((double) (((long) x.length) * ((long) y.length)));
    }

    private long integralKolmogorovSmirnovStatistic(double[] x, double[] y) {
        checkArray(x);
        checkArray(y);
        double[] sx = MathArrays.copyOf(x);
        double[] sy = MathArrays.copyOf(y);
        Arrays.sort(sx);
        Arrays.sort(sy);
        int n = sx.length;
        int m = sy.length;
        int rankX = 0;
        int rankY = 0;
        long curD = 0;
        long supD = 0;
        do {
            double z = Double.compare(sx[rankX], sy[rankY]) <= 0 ? sx[rankX] : sy[rankY];
            while (rankX < n && Double.compare(sx[rankX], z) == 0) {
                rankX++;
                curD += (long) m;
            }
            while (rankY < m && Double.compare(sy[rankY], z) == 0) {
                rankY++;
                curD -= (long) n;
            }
            if (curD > supD) {
                supD = curD;
            } else if ((-curD) > supD) {
                supD = -curD;
            }
            if (rankX >= n) {
                break;
            }
        } while (rankY < m);
        return supD;
    }

    public double kolmogorovSmirnovTest(RealDistribution distribution, double[] data) {
        return kolmogorovSmirnovTest(distribution, data, false);
    }

    public boolean kolmogorovSmirnovTest(RealDistribution distribution, double[] data, double alpha) {
        if (alpha <= 0.0d || alpha > 0.5d) {
            throw new OutOfRangeException(LocalizedFormats.OUT_OF_BOUND_SIGNIFICANCE_LEVEL, Double.valueOf(alpha), 0, Double.valueOf(0.5d));
        } else if (kolmogorovSmirnovTest(distribution, data) < alpha) {
            return true;
        } else {
            return false;
        }
    }

    public double bootstrap(double[] x, double[] y, int iterations, boolean strict) {
        int xLength = x.length;
        int yLength = y.length;
        double[] combined = new double[(xLength + yLength)];
        System.arraycopy(x, 0, combined, 0, xLength);
        System.arraycopy(y, 0, combined, xLength, yLength);
        EnumeratedRealDistribution dist = new EnumeratedRealDistribution(this.rng, combined);
        long d = integralKolmogorovSmirnovStatistic(x, y);
        int greaterCount = 0;
        int equalCount = 0;
        for (int i = 0; i < iterations; i++) {
            long curD = integralKolmogorovSmirnovStatistic(dist.sample(xLength), dist.sample(yLength));
            if (curD > d) {
                greaterCount++;
            } else if (curD == d) {
                equalCount++;
            }
        }
        return strict ? ((double) greaterCount) / ((double) iterations) : ((double) (greaterCount + equalCount)) / ((double) iterations);
    }

    public double bootstrap(double[] x, double[] y, int iterations) {
        return bootstrap(x, y, iterations, true);
    }

    public double cdf(double d, int n) throws MathArithmeticException {
        return cdf(d, n, false);
    }

    public double cdfExact(double d, int n) throws MathArithmeticException {
        return cdf(d, n, true);
    }

    public double cdf(double d, int n, boolean exact) throws MathArithmeticException {
        double ninv = 1.0d / ((double) n);
        double ninvhalf = 0.5d * ninv;
        if (d <= ninvhalf) {
            return 0.0d;
        }
        if (ninvhalf < d && d <= ninv) {
            double res = 1.0d;
            double f = (2.0d * d) - ninv;
            for (int i = 1; i <= n; i++) {
                res *= ((double) i) * f;
            }
            return res;
        } else if (1.0d - ninv <= d && d < 1.0d) {
            return 1.0d - (2.0d * Math.pow(1.0d - d, (double) n));
        } else {
            if (1.0d <= d) {
                return 1.0d;
            }
            if (exact) {
                return exactK(d, n);
            }
            if (n <= 140) {
                return roundedK(d, n);
            }
            return pelzGood(d, n);
        }
    }

    private double exactK(double d, int n) throws MathArithmeticException {
        int k = (int) Math.ceil(((double) n) * d);
        BigFraction pFrac = createExactH(d, n).power(n).getEntry(k - 1, k - 1);
        for (int i = 1; i <= n; i++) {
            pFrac = pFrac.multiply(i).divide(n);
        }
        return pFrac.bigDecimalValue(20, 4).doubleValue();
    }

    private double roundedK(double d, int n) {
        int k = (int) Math.ceil(((double) n) * d);
        double pFrac = createRoundedH(d, n).power(n).getEntry(k - 1, k - 1);
        for (int i = 1; i <= n; i++) {
            pFrac *= ((double) i) / ((double) n);
        }
        return pFrac;
    }

    public double pelzGood(double d, int n) {
        double sqrtN = FastMath.sqrt((double) n);
        double z = d * sqrtN;
        double z2 = d * d * ((double) n);
        double z4 = z2 * z2;
        double z6 = z4 * z2;
        double z8 = z4 * z4;
        double sum = 0.0d;
        double z2Term = 9.869604401089358d / (8.0d * z2);
        int k = 1;
        while (k < MAXIMUM_PARTIAL_SUM_COUNT) {
            double kTerm = (double) ((k * 2) - 1);
            double increment = FastMath.exp((-z2Term) * kTerm * kTerm);
            sum += increment;
            if (increment <= 1.0E-10d * sum) {
                break;
            }
            k++;
        }
        if (k == MAXIMUM_PARTIAL_SUM_COUNT) {
            throw new TooManyIterationsException(Integer.valueOf((int) MAXIMUM_PARTIAL_SUM_COUNT));
        }
        double ret = (FastMath.sqrt(6.283185307179586d) * sum) / z;
        double twoZ2 = 2.0d * z2;
        double sum2 = 0.0d;
        int k2 = 0;
        while (k2 < MAXIMUM_PARTIAL_SUM_COUNT) {
            double kTerm2 = ((double) k2) + 0.5d;
            double kTerm22 = kTerm2 * kTerm2;
            double increment2 = ((9.869604401089358d * kTerm22) - z2) * FastMath.exp((-9.869604401089358d * kTerm22) / twoZ2);
            sum2 += increment2;
            if (FastMath.abs(increment2) < 1.0E-10d * FastMath.abs(sum2)) {
                break;
            }
            k2++;
        }
        if (k2 == MAXIMUM_PARTIAL_SUM_COUNT) {
            throw new TooManyIterationsException(Integer.valueOf((int) MAXIMUM_PARTIAL_SUM_COUNT));
        }
        double sqrtHalfPi = FastMath.sqrt(1.5707963267948966d);
        double ret2 = ret + ((sum2 * sqrtHalfPi) / ((3.0d * z4) * sqrtN));
        double z4Term = 2.0d * z4;
        double z6Term = 6.0d * z6;
        double z2Term2 = 5.0d * z2;
        double sum3 = 0.0d;
        int k3 = 0;
        while (k3 < MAXIMUM_PARTIAL_SUM_COUNT) {
            double kTerm3 = ((double) k3) + 0.5d;
            double kTerm23 = kTerm3 * kTerm3;
            double increment3 = (z6Term + z4Term + (9.869604401089358d * (z4Term - z2Term2) * kTerm23) + (97.40909103400243d * (1.0d - twoZ2) * kTerm23 * kTerm23)) * FastMath.exp((-9.869604401089358d * kTerm23) / twoZ2);
            sum3 += increment3;
            if (FastMath.abs(increment3) < 1.0E-10d * FastMath.abs(sum3)) {
                break;
            }
            k3++;
        }
        if (k3 == MAXIMUM_PARTIAL_SUM_COUNT) {
            throw new TooManyIterationsException(Integer.valueOf((int) MAXIMUM_PARTIAL_SUM_COUNT));
        }
        double sum22 = 0.0d;
        int k4 = 1;
        while (k4 < MAXIMUM_PARTIAL_SUM_COUNT) {
            double kTerm24 = (double) (k4 * k4);
            double increment4 = 9.869604401089358d * kTerm24 * FastMath.exp((-9.869604401089358d * kTerm24) / twoZ2);
            sum22 += increment4;
            if (FastMath.abs(increment4) < 1.0E-10d * FastMath.abs(sum22)) {
                break;
            }
            k4++;
        }
        if (k4 == MAXIMUM_PARTIAL_SUM_COUNT) {
            throw new TooManyIterationsException(Integer.valueOf((int) MAXIMUM_PARTIAL_SUM_COUNT));
        }
        double ret3 = ret2 + ((sqrtHalfPi / ((double) n)) * ((sum3 / ((((36.0d * z2) * z2) * z2) * z)) - (sum22 / ((18.0d * z2) * z))));
        double sum4 = 0.0d;
        int k5 = 0;
        while (k5 < MAXIMUM_PARTIAL_SUM_COUNT) {
            double kTerm4 = ((double) k5) + 0.5d;
            double kTerm25 = kTerm4 * kTerm4;
            double kTerm42 = kTerm25 * kTerm25;
            double increment5 = ((((((961.3891935753043d * (kTerm42 * kTerm25)) * (5.0d - (30.0d * z2))) + ((97.40909103400243d * kTerm42) * ((-60.0d * z2) + (212.0d * z4)))) + ((9.869604401089358d * kTerm25) * ((135.0d * z4) - (96.0d * z6)))) - (30.0d * z6)) - (90.0d * z8)) * FastMath.exp((-9.869604401089358d * kTerm25) / twoZ2);
            sum4 += increment5;
            if (FastMath.abs(increment5) < 1.0E-10d * FastMath.abs(sum4)) {
                break;
            }
            k5++;
        }
        if (k5 == MAXIMUM_PARTIAL_SUM_COUNT) {
            throw new TooManyIterationsException(Integer.valueOf((int) MAXIMUM_PARTIAL_SUM_COUNT));
        }
        double sum23 = 0.0d;
        int k6 = 1;
        while (k6 < MAXIMUM_PARTIAL_SUM_COUNT) {
            double kTerm26 = (double) (k6 * k6);
            double increment6 = ((-97.40909103400243d * kTerm26 * kTerm26) + (29.608813203268074d * kTerm26 * z2)) * FastMath.exp((-9.869604401089358d * kTerm26) / twoZ2);
            sum23 += increment6;
            if (FastMath.abs(increment6) < 1.0E-10d * FastMath.abs(sum23)) {
                break;
            }
            k6++;
        }
        if (k6 != MAXIMUM_PARTIAL_SUM_COUNT) {
            return ((sqrtHalfPi / (((double) n) * sqrtN)) * ((sum4 / ((3240.0d * z6) * z4)) + (sum23 / (108.0d * z6)))) + ret3;
        }
        throw new TooManyIterationsException(Integer.valueOf((int) MAXIMUM_PARTIAL_SUM_COUNT));
    }

    private FieldMatrix<BigFraction> createExactH(double d, int n) throws NumberIsTooLargeException, FractionConversionException {
        BigFraction h;
        int k = (int) Math.ceil(((double) n) * d);
        int m = (k * 2) - 1;
        double hDouble = ((double) k) - (((double) n) * d);
        if (hDouble >= 1.0d) {
            throw new NumberIsTooLargeException(Double.valueOf(hDouble), Double.valueOf(1.0d), false);
        }
        try {
            h = new BigFraction(hDouble, KS_SUM_CAUCHY_CRITERION, 10000);
        } catch (FractionConversionException e) {
            try {
                h = new BigFraction(hDouble, 1.0E-10d, 10000);
            } catch (FractionConversionException e2) {
                h = new BigFraction(hDouble, 1.0E-5d, 10000);
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

    private RealMatrix createRoundedH(double d, int n) throws NumberIsTooLargeException {
        int k = (int) Math.ceil(((double) n) * d);
        int m = (k * 2) - 1;
        double h = ((double) k) - (((double) n) * d);
        if (h >= 1.0d) {
            throw new NumberIsTooLargeException(Double.valueOf(h), Double.valueOf(1.0d), false);
        }
        double[][] Hdata = (double[][]) Array.newInstance(Double.TYPE, m, m);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                if ((i - j) + 1 < 0) {
                    Hdata[i][j] = 0.0d;
                } else {
                    Hdata[i][j] = 1.0d;
                }
            }
        }
        double[] hPowers = new double[m];
        hPowers[0] = h;
        for (int i2 = 1; i2 < m; i2++) {
            hPowers[i2] = hPowers[i2 - 1] * h;
        }
        for (int i3 = 0; i3 < m; i3++) {
            Hdata[i3][0] = Hdata[i3][0] - hPowers[i3];
            double[] dArr = Hdata[m - 1];
            dArr[i3] = dArr[i3] - hPowers[(m - i3) - 1];
        }
        if (Double.compare(h, 0.5d) > 0) {
            double[] dArr2 = Hdata[m - 1];
            dArr2[0] = dArr2[0] + FastMath.pow((2.0d * h) - 1.0d, m);
        }
        for (int i4 = 0; i4 < m; i4++) {
            for (int j2 = 0; j2 < i4 + 1; j2++) {
                if ((i4 - j2) + 1 > 0) {
                    for (int g = 2; g <= (i4 - j2) + 1; g++) {
                        double[] dArr3 = Hdata[i4];
                        dArr3[j2] = dArr3[j2] / ((double) g);
                    }
                }
            }
        }
        return MatrixUtils.createRealMatrix(Hdata);
    }

    private void checkArray(double[] array) {
        if (array == null) {
            throw new NullArgumentException(LocalizedFormats.NULL_NOT_ALLOWED, new Object[0]);
        } else if (array.length < 2) {
            throw new InsufficientDataException(LocalizedFormats.INSUFFICIENT_OBSERVED_POINTS_IN_SAMPLE, Integer.valueOf(array.length), 2);
        }
    }

    public double ksSum(double t, double tolerance, int maxIterations) {
        if (t == 0.0d) {
            return 0.0d;
        }
        double x = -2.0d * t * t;
        int sign = -1;
        long i = 1;
        double partialSum = 0.5d;
        double delta = 1.0d;
        while (delta > tolerance && i < ((long) maxIterations)) {
            delta = FastMath.exp(((double) i) * x * ((double) i));
            partialSum += ((double) sign) * delta;
            sign *= -1;
            i++;
        }
        if (i != ((long) maxIterations)) {
            return 2.0d * partialSum;
        }
        throw new TooManyIterationsException(Integer.valueOf(maxIterations));
    }

    private static long calculateIntegralD(double d, int n, int m, boolean strict) {
        long nm = ((long) n) * ((long) m);
        long upperBound = (long) FastMath.ceil((d - 1.0E-12d) * ((double) nm));
        long lowerBound = (long) FastMath.floor((1.0E-12d + d) * ((double) nm));
        if (!strict || lowerBound != upperBound) {
            return upperBound;
        }
        return upperBound + 1;
    }

    public double exactP(double d, int n, int m, boolean strict) {
        return 1.0d - (m12n(m, n, m, n, calculateIntegralD(d, m, n, strict), strict) / CombinatoricsUtils.binomialCoefficientDouble(n + m, m));
    }

    public double approximateP(double d, int n, int m) {
        double dm = (double) m;
        double dn = (double) n;
        return 1.0d - ksSum(d * FastMath.sqrt((dm * dn) / (dm + dn)), KS_SUM_CAUCHY_CRITERION, MAXIMUM_PARTIAL_SUM_COUNT);
    }

    static void fillBooleanArrayRandomlyWithFixedNumberTrueValues(boolean[] b, int numberOfTrueValues, RandomGenerator rng2) {
        Arrays.fill(b, true);
        for (int k = numberOfTrueValues; k < b.length; k++) {
            int r = rng2.nextInt(k + 1);
            if (!b[r]) {
                r = k;
            }
            b[r] = false;
        }
    }

    public double monteCarloP(double d, int n, int m, boolean strict, int iterations) {
        return integralMonteCarloP(calculateIntegralD(d, n, m, strict), n, m, iterations);
    }

    private double integralMonteCarloP(long d, int n, int m, int iterations) {
        int nn = FastMath.max(n, m);
        int mm = FastMath.min(n, m);
        int tail = 0;
        boolean[] b = new boolean[(nn + mm)];
        for (int i = 0; i < iterations; i++) {
            fillBooleanArrayRandomlyWithFixedNumberTrueValues(b, nn, this.rng);
            long curD = 0;
            int j = 0;
            while (true) {
                if (j >= b.length) {
                    break;
                }
                if (!b[j]) {
                    curD -= (long) nn;
                    if (curD <= (-d)) {
                        tail++;
                        break;
                    }
                } else {
                    curD += (long) mm;
                    if (curD >= d) {
                        tail++;
                        break;
                    }
                }
                j++;
            }
        }
        return ((double) tail) / ((double) iterations);
    }

    private static void fixTies(double[] x, double[] y) {
        boolean ties;
        double[] values = MathArrays.unique(MathArrays.concatenate(x, y));
        if (values.length != x.length + y.length) {
            double minDelta = 1.0d;
            double prev = values[0];
            for (int i = 1; i < values.length; i++) {
                double delta = prev - values[i];
                if (delta < minDelta) {
                    minDelta = delta;
                }
                prev = values[i];
            }
            double minDelta2 = minDelta / 2.0d;
            RealDistribution dist = new UniformRealDistribution(new JDKRandomGenerator(100), -minDelta2, minDelta2);
            int ct = 0;
            do {
                jitter(x, dist);
                jitter(y, dist);
                ties = hasTies(x, y);
                ct++;
                if (!ties) {
                    break;
                }
            } while (ct < 1000);
            if (ties) {
                throw new MathInternalError();
            }
        }
    }

    private static boolean hasTies(double[] x, double[] y) {
        HashSet<Double> values = new HashSet<>();
        for (double d : x) {
            if (!values.add(Double.valueOf(d))) {
                return true;
            }
        }
        for (double d2 : y) {
            if (!values.add(Double.valueOf(d2))) {
                return true;
            }
        }
        return false;
    }

    private static void jitter(double[] data, RealDistribution dist) {
        for (int i = 0; i < data.length; i++) {
            data[i] = data[i] + dist.sample();
        }
    }

    /* renamed from: c */
    private static int m11c(int i, int j, int m, int n, long cmn, boolean strict) {
        return strict ? FastMath.abs((((long) i) * ((long) n)) - (((long) j) * ((long) m))) <= cmn ? 1 : 0 : FastMath.abs((((long) i) * ((long) n)) - (((long) j) * ((long) m))) >= cmn ? 0 : 1;
    }

    /* renamed from: n */
    private static double m12n(int i, int j, int m, int n, long cnm, boolean strict) {
        double[] lag = new double[n];
        double last = 0.0d;
        for (int k = 0; k < n; k++) {
            lag[k] = (double) m11c(0, k + 1, m, n, cnm, strict);
        }
        for (int k2 = 1; k2 <= i; k2++) {
            last = (double) m11c(k2, 0, m, n, cnm, strict);
            for (int l = 1; l <= j; l++) {
                lag[l - 1] = ((double) m11c(k2, l, m, n, cnm, strict)) * (lag[l - 1] + last);
                last = lag[l - 1];
            }
        }
        return last;
    }
}
