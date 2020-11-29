package org.apache.commons.math3.stat.inference;

import java.lang.reflect.Array;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.ZeroException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;

public class GTest {
    /* renamed from: g */
    public double mo3895g(double[] expected, long[] observed) throws NotPositiveException, NotStrictlyPositiveException, DimensionMismatchException {
        if (expected.length < 2) {
            throw new DimensionMismatchException(expected.length, 2);
        } else if (expected.length != observed.length) {
            throw new DimensionMismatchException(expected.length, observed.length);
        } else {
            MathArrays.checkPositive(expected);
            MathArrays.checkNonNegative(observed);
            double sumExpected = 0.0d;
            double sumObserved = 0.0d;
            for (int i = 0; i < observed.length; i++) {
                sumExpected += expected[i];
                sumObserved += (double) observed[i];
            }
            double ratio = 1.0d;
            boolean rescale = false;
            if (FastMath.abs(sumExpected - sumObserved) > 1.0E-5d) {
                ratio = sumObserved / sumExpected;
                rescale = true;
            }
            double sum = 0.0d;
            for (int i2 = 0; i2 < observed.length; i2++) {
                sum += ((double) observed[i2]) * (rescale ? FastMath.log(((double) observed[i2]) / (expected[i2] * ratio)) : FastMath.log(((double) observed[i2]) / expected[i2]));
            }
            return 2.0d * sum;
        }
    }

    public double gTest(double[] expected, long[] observed) throws NotPositiveException, NotStrictlyPositiveException, DimensionMismatchException, MaxCountExceededException {
        return 1.0d - new ChiSquaredDistribution((RandomGenerator) null, ((double) expected.length) - 1.0d).cumulativeProbability(mo3895g(expected, observed));
    }

    public double gTestIntrinsic(double[] expected, long[] observed) throws NotPositiveException, NotStrictlyPositiveException, DimensionMismatchException, MaxCountExceededException {
        return 1.0d - new ChiSquaredDistribution((RandomGenerator) null, ((double) expected.length) - 2.0d).cumulativeProbability(mo3895g(expected, observed));
    }

    public boolean gTest(double[] expected, long[] observed, double alpha) throws NotPositiveException, NotStrictlyPositiveException, DimensionMismatchException, OutOfRangeException, MaxCountExceededException {
        if (alpha <= 0.0d || alpha > 0.5d) {
            throw new OutOfRangeException(LocalizedFormats.OUT_OF_BOUND_SIGNIFICANCE_LEVEL, Double.valueOf(alpha), 0, Double.valueOf(0.5d));
        } else if (gTest(expected, observed) < alpha) {
            return true;
        } else {
            return false;
        }
    }

    private double entropy(long[][] k) {
        double h = 0.0d;
        double sum_k = 0.0d;
        for (int i = 0; i < k.length; i++) {
            for (int j = 0; j < k[i].length; j++) {
                sum_k += (double) k[i][j];
            }
        }
        for (int i2 = 0; i2 < k.length; i2++) {
            for (int j2 = 0; j2 < k[i2].length; j2++) {
                if (k[i2][j2] != 0) {
                    double p_ij = ((double) k[i2][j2]) / sum_k;
                    h += FastMath.log(p_ij) * p_ij;
                }
            }
        }
        return -h;
    }

    private double entropy(long[] k) {
        double h = 0.0d;
        double sum_k = 0.0d;
        for (long j : k) {
            sum_k += (double) j;
        }
        for (int i = 0; i < k.length; i++) {
            if (k[i] != 0) {
                double p_i = ((double) k[i]) / sum_k;
                h += FastMath.log(p_i) * p_i;
            }
        }
        return -h;
    }

    public double gDataSetsComparison(long[] observed1, long[] observed2) throws DimensionMismatchException, NotPositiveException, ZeroException {
        if (observed1.length < 2) {
            throw new DimensionMismatchException(observed1.length, 2);
        } else if (observed1.length != observed2.length) {
            throw new DimensionMismatchException(observed1.length, observed2.length);
        } else {
            MathArrays.checkNonNegative(observed1);
            MathArrays.checkNonNegative(observed2);
            long countSum1 = 0;
            long countSum2 = 0;
            long[] collSums = new long[observed1.length];
            long[][] k = (long[][]) Array.newInstance(Long.TYPE, 2, observed1.length);
            for (int i = 0; i < observed1.length; i++) {
                if (observed1[i] == 0 && observed2[i] == 0) {
                    throw new ZeroException(LocalizedFormats.OBSERVED_COUNTS_BOTTH_ZERO_FOR_ENTRY, Integer.valueOf(i));
                }
                countSum1 += observed1[i];
                countSum2 += observed2[i];
                collSums[i] = observed1[i] + observed2[i];
                k[0][i] = observed1[i];
                k[1][i] = observed2[i];
            }
            if (countSum1 == 0 || countSum2 == 0) {
                throw new ZeroException();
            }
            return 2.0d * (((double) countSum1) + ((double) countSum2)) * ((entropy(new long[]{countSum1, countSum2}) + entropy(collSums)) - entropy(k));
        }
    }

    public double rootLogLikelihoodRatio(long k11, long k12, long k21, long k22) {
        double sqrt = FastMath.sqrt(gDataSetsComparison(new long[]{k11, k12}, new long[]{k21, k22}));
        if (((double) k11) / ((double) (k11 + k12)) < ((double) k21) / ((double) (k21 + k22))) {
            return -sqrt;
        }
        return sqrt;
    }

    public double gTestDataSetsComparison(long[] observed1, long[] observed2) throws DimensionMismatchException, NotPositiveException, ZeroException, MaxCountExceededException {
        return 1.0d - new ChiSquaredDistribution((RandomGenerator) null, ((double) observed1.length) - 1.0d).cumulativeProbability(gDataSetsComparison(observed1, observed2));
    }

    public boolean gTestDataSetsComparison(long[] observed1, long[] observed2, double alpha) throws DimensionMismatchException, NotPositiveException, ZeroException, OutOfRangeException, MaxCountExceededException {
        if (alpha <= 0.0d || alpha > 0.5d) {
            throw new OutOfRangeException(LocalizedFormats.OUT_OF_BOUND_SIGNIFICANCE_LEVEL, Double.valueOf(alpha), 0, Double.valueOf(0.5d));
        } else if (gTestDataSetsComparison(observed1, observed2) < alpha) {
            return true;
        } else {
            return false;
        }
    }
}
