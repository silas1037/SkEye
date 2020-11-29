package org.apache.commons.math3.stat.inference;

import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.util.FastMath;

public class TTest {
    public double pairedT(double[] sample1, double[] sample2) throws NullArgumentException, NoDataException, DimensionMismatchException, NumberIsTooSmallException {
        checkSampleData(sample1);
        checkSampleData(sample2);
        double meanDifference = StatUtils.meanDifference(sample1, sample2);
        return mo3937t(meanDifference, 0.0d, StatUtils.varianceDifference(sample1, sample2, meanDifference), (double) sample1.length);
    }

    public double pairedTTest(double[] sample1, double[] sample2) throws NullArgumentException, NoDataException, DimensionMismatchException, NumberIsTooSmallException, MaxCountExceededException {
        double meanDifference = StatUtils.meanDifference(sample1, sample2);
        return tTest(meanDifference, 0.0d, StatUtils.varianceDifference(sample1, sample2, meanDifference), (double) sample1.length);
    }

    public boolean pairedTTest(double[] sample1, double[] sample2, double alpha) throws NullArgumentException, NoDataException, DimensionMismatchException, NumberIsTooSmallException, OutOfRangeException, MaxCountExceededException {
        checkSignificanceLevel(alpha);
        return pairedTTest(sample1, sample2) < alpha;
    }

    /* renamed from: t */
    public double mo3940t(double mu, double[] observed) throws NullArgumentException, NumberIsTooSmallException {
        checkSampleData(observed);
        return mo3937t(StatUtils.mean(observed), mu, StatUtils.variance(observed), (double) observed.length);
    }

    /* renamed from: t */
    public double mo3939t(double mu, StatisticalSummary sampleStats) throws NullArgumentException, NumberIsTooSmallException {
        checkSampleData(sampleStats);
        return mo3937t(sampleStats.getMean(), mu, sampleStats.getVariance(), (double) sampleStats.getN());
    }

    public double homoscedasticT(double[] sample1, double[] sample2) throws NullArgumentException, NumberIsTooSmallException {
        checkSampleData(sample1);
        checkSampleData(sample2);
        return homoscedasticT(StatUtils.mean(sample1), StatUtils.mean(sample2), StatUtils.variance(sample1), StatUtils.variance(sample2), (double) sample1.length, (double) sample2.length);
    }

    /* renamed from: t */
    public double mo3942t(double[] sample1, double[] sample2) throws NullArgumentException, NumberIsTooSmallException {
        checkSampleData(sample1);
        checkSampleData(sample2);
        return mo3938t(StatUtils.mean(sample1), StatUtils.mean(sample2), StatUtils.variance(sample1), StatUtils.variance(sample2), (double) sample1.length, (double) sample2.length);
    }

    /* renamed from: t */
    public double mo3941t(StatisticalSummary sampleStats1, StatisticalSummary sampleStats2) throws NullArgumentException, NumberIsTooSmallException {
        checkSampleData(sampleStats1);
        checkSampleData(sampleStats2);
        return mo3938t(sampleStats1.getMean(), sampleStats2.getMean(), sampleStats1.getVariance(), sampleStats2.getVariance(), (double) sampleStats1.getN(), (double) sampleStats2.getN());
    }

    public double homoscedasticT(StatisticalSummary sampleStats1, StatisticalSummary sampleStats2) throws NullArgumentException, NumberIsTooSmallException {
        checkSampleData(sampleStats1);
        checkSampleData(sampleStats2);
        return homoscedasticT(sampleStats1.getMean(), sampleStats2.getMean(), sampleStats1.getVariance(), sampleStats2.getVariance(), (double) sampleStats1.getN(), (double) sampleStats2.getN());
    }

    public double tTest(double mu, double[] sample) throws NullArgumentException, NumberIsTooSmallException, MaxCountExceededException {
        checkSampleData(sample);
        return tTest(StatUtils.mean(sample), mu, StatUtils.variance(sample), (double) sample.length);
    }

    public boolean tTest(double mu, double[] sample, double alpha) throws NullArgumentException, NumberIsTooSmallException, OutOfRangeException, MaxCountExceededException {
        checkSignificanceLevel(alpha);
        return tTest(mu, sample) < alpha;
    }

    public double tTest(double mu, StatisticalSummary sampleStats) throws NullArgumentException, NumberIsTooSmallException, MaxCountExceededException {
        checkSampleData(sampleStats);
        return tTest(sampleStats.getMean(), mu, sampleStats.getVariance(), (double) sampleStats.getN());
    }

    public boolean tTest(double mu, StatisticalSummary sampleStats, double alpha) throws NullArgumentException, NumberIsTooSmallException, OutOfRangeException, MaxCountExceededException {
        checkSignificanceLevel(alpha);
        return tTest(mu, sampleStats) < alpha;
    }

    public double tTest(double[] sample1, double[] sample2) throws NullArgumentException, NumberIsTooSmallException, MaxCountExceededException {
        checkSampleData(sample1);
        checkSampleData(sample2);
        return tTest(StatUtils.mean(sample1), StatUtils.mean(sample2), StatUtils.variance(sample1), StatUtils.variance(sample2), (double) sample1.length, (double) sample2.length);
    }

    public double homoscedasticTTest(double[] sample1, double[] sample2) throws NullArgumentException, NumberIsTooSmallException, MaxCountExceededException {
        checkSampleData(sample1);
        checkSampleData(sample2);
        return homoscedasticTTest(StatUtils.mean(sample1), StatUtils.mean(sample2), StatUtils.variance(sample1), StatUtils.variance(sample2), (double) sample1.length, (double) sample2.length);
    }

    public boolean tTest(double[] sample1, double[] sample2, double alpha) throws NullArgumentException, NumberIsTooSmallException, OutOfRangeException, MaxCountExceededException {
        checkSignificanceLevel(alpha);
        return tTest(sample1, sample2) < alpha;
    }

    public boolean homoscedasticTTest(double[] sample1, double[] sample2, double alpha) throws NullArgumentException, NumberIsTooSmallException, OutOfRangeException, MaxCountExceededException {
        checkSignificanceLevel(alpha);
        return homoscedasticTTest(sample1, sample2) < alpha;
    }

    public double tTest(StatisticalSummary sampleStats1, StatisticalSummary sampleStats2) throws NullArgumentException, NumberIsTooSmallException, MaxCountExceededException {
        checkSampleData(sampleStats1);
        checkSampleData(sampleStats2);
        return tTest(sampleStats1.getMean(), sampleStats2.getMean(), sampleStats1.getVariance(), sampleStats2.getVariance(), (double) sampleStats1.getN(), (double) sampleStats2.getN());
    }

    public double homoscedasticTTest(StatisticalSummary sampleStats1, StatisticalSummary sampleStats2) throws NullArgumentException, NumberIsTooSmallException, MaxCountExceededException {
        checkSampleData(sampleStats1);
        checkSampleData(sampleStats2);
        return homoscedasticTTest(sampleStats1.getMean(), sampleStats2.getMean(), sampleStats1.getVariance(), sampleStats2.getVariance(), (double) sampleStats1.getN(), (double) sampleStats2.getN());
    }

    public boolean tTest(StatisticalSummary sampleStats1, StatisticalSummary sampleStats2, double alpha) throws NullArgumentException, NumberIsTooSmallException, OutOfRangeException, MaxCountExceededException {
        checkSignificanceLevel(alpha);
        return tTest(sampleStats1, sampleStats2) < alpha;
    }

    /* access modifiers changed from: protected */
    /* renamed from: df */
    public double mo3926df(double v1, double v2, double n1, double n2) {
        return (((v1 / n1) + (v2 / n2)) * ((v1 / n1) + (v2 / n2))) / (((v1 * v1) / ((n1 * n1) * (n1 - 1.0d))) + ((v2 * v2) / ((n2 * n2) * (n2 - 1.0d))));
    }

    /* access modifiers changed from: protected */
    /* renamed from: t */
    public double mo3937t(double m, double mu, double v, double n) {
        return (m - mu) / FastMath.sqrt(v / n);
    }

    /* access modifiers changed from: protected */
    /* renamed from: t */
    public double mo3938t(double m1, double m2, double v1, double v2, double n1, double n2) {
        return (m1 - m2) / FastMath.sqrt((v1 / n1) + (v2 / n2));
    }

    /* access modifiers changed from: protected */
    public double homoscedasticT(double m1, double m2, double v1, double v2, double n1, double n2) {
        return (m1 - m2) / FastMath.sqrt(((1.0d / n1) + (1.0d / n2)) * ((((n1 - 1.0d) * v1) + ((n2 - 1.0d) * v2)) / ((n1 + n2) - 2.0d)));
    }

    /* access modifiers changed from: protected */
    public double tTest(double m, double mu, double v, double n) throws MaxCountExceededException, MathIllegalArgumentException {
        return 2.0d * new TDistribution((RandomGenerator) null, n - 1.0d).cumulativeProbability(-FastMath.abs(mo3937t(m, mu, v, n)));
    }

    /* access modifiers changed from: protected */
    public double tTest(double m1, double m2, double v1, double v2, double n1, double n2) throws MaxCountExceededException, NotStrictlyPositiveException {
        return 2.0d * new TDistribution((RandomGenerator) null, mo3926df(v1, v2, n1, n2)).cumulativeProbability(-FastMath.abs(mo3938t(m1, m2, v1, v2, n1, n2)));
    }

    /* access modifiers changed from: protected */
    public double homoscedasticTTest(double m1, double m2, double v1, double v2, double n1, double n2) throws MaxCountExceededException, NotStrictlyPositiveException {
        return 2.0d * new TDistribution((RandomGenerator) null, (n1 + n2) - 2.0d).cumulativeProbability(-FastMath.abs(homoscedasticT(m1, m2, v1, v2, n1, n2)));
    }

    private void checkSignificanceLevel(double alpha) throws OutOfRangeException {
        if (alpha <= 0.0d || alpha > 0.5d) {
            throw new OutOfRangeException(LocalizedFormats.SIGNIFICANCE_LEVEL, Double.valueOf(alpha), Double.valueOf(0.0d), Double.valueOf(0.5d));
        }
    }

    private void checkSampleData(double[] data) throws NullArgumentException, NumberIsTooSmallException {
        if (data == null) {
            throw new NullArgumentException();
        } else if (data.length < 2) {
            throw new NumberIsTooSmallException(LocalizedFormats.INSUFFICIENT_DATA_FOR_T_STATISTIC, Integer.valueOf(data.length), 2, true);
        }
    }

    private void checkSampleData(StatisticalSummary stat) throws NullArgumentException, NumberIsTooSmallException {
        if (stat == null) {
            throw new NullArgumentException();
        } else if (stat.getN() < 2) {
            throw new NumberIsTooSmallException(LocalizedFormats.INSUFFICIENT_DATA_FOR_T_STATISTIC, Long.valueOf(stat.getN()), 2, true);
        }
    }
}
