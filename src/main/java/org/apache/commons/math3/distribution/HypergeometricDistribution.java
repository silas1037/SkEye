package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.util.FastMath;

public class HypergeometricDistribution extends AbstractIntegerDistribution {
    private static final long serialVersionUID = -436928820673516179L;
    private final int numberOfSuccesses;
    private double numericalVariance;
    private boolean numericalVarianceIsCalculated;
    private final int populationSize;
    private final int sampleSize;

    public HypergeometricDistribution(int populationSize2, int numberOfSuccesses2, int sampleSize2) throws NotPositiveException, NotStrictlyPositiveException, NumberIsTooLargeException {
        this(new Well19937c(), populationSize2, numberOfSuccesses2, sampleSize2);
    }

    public HypergeometricDistribution(RandomGenerator rng, int populationSize2, int numberOfSuccesses2, int sampleSize2) throws NotPositiveException, NotStrictlyPositiveException, NumberIsTooLargeException {
        super(rng);
        this.numericalVariance = Double.NaN;
        this.numericalVarianceIsCalculated = false;
        if (populationSize2 <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.POPULATION_SIZE, Integer.valueOf(populationSize2));
        } else if (numberOfSuccesses2 < 0) {
            throw new NotPositiveException(LocalizedFormats.NUMBER_OF_SUCCESSES, Integer.valueOf(numberOfSuccesses2));
        } else if (sampleSize2 < 0) {
            throw new NotPositiveException(LocalizedFormats.NUMBER_OF_SAMPLES, Integer.valueOf(sampleSize2));
        } else if (numberOfSuccesses2 > populationSize2) {
            throw new NumberIsTooLargeException(LocalizedFormats.NUMBER_OF_SUCCESS_LARGER_THAN_POPULATION_SIZE, Integer.valueOf(numberOfSuccesses2), Integer.valueOf(populationSize2), true);
        } else if (sampleSize2 > populationSize2) {
            throw new NumberIsTooLargeException(LocalizedFormats.SAMPLE_SIZE_LARGER_THAN_POPULATION_SIZE, Integer.valueOf(sampleSize2), Integer.valueOf(populationSize2), true);
        } else {
            this.numberOfSuccesses = numberOfSuccesses2;
            this.populationSize = populationSize2;
            this.sampleSize = sampleSize2;
        }
    }

    @Override // org.apache.commons.math3.distribution.IntegerDistribution
    public double cumulativeProbability(int x) {
        int[] domain = getDomain(this.populationSize, this.numberOfSuccesses, this.sampleSize);
        if (x < domain[0]) {
            return 0.0d;
        }
        if (x >= domain[1]) {
            return 1.0d;
        }
        return innerCumulativeProbability(domain[0], x, 1);
    }

    private int[] getDomain(int n, int m, int k) {
        return new int[]{getLowerDomain(n, m, k), getUpperDomain(m, k)};
    }

    private int getLowerDomain(int n, int m, int k) {
        return FastMath.max(0, m - (n - k));
    }

    public int getNumberOfSuccesses() {
        return this.numberOfSuccesses;
    }

    public int getPopulationSize() {
        return this.populationSize;
    }

    public int getSampleSize() {
        return this.sampleSize;
    }

    private int getUpperDomain(int m, int k) {
        return FastMath.min(k, m);
    }

    @Override // org.apache.commons.math3.distribution.IntegerDistribution
    public double probability(int x) {
        double logProbability = logProbability(x);
        if (logProbability == Double.NEGATIVE_INFINITY) {
            return 0.0d;
        }
        return FastMath.exp(logProbability);
    }

    @Override // org.apache.commons.math3.distribution.AbstractIntegerDistribution
    public double logProbability(int x) {
        int[] domain = getDomain(this.populationSize, this.numberOfSuccesses, this.sampleSize);
        if (x < domain[0] || x > domain[1]) {
            return Double.NEGATIVE_INFINITY;
        }
        double p = ((double) this.sampleSize) / ((double) this.populationSize);
        double q = ((double) (this.populationSize - this.sampleSize)) / ((double) this.populationSize);
        double p1 = SaddlePointExpansion.logBinomialProbability(x, this.numberOfSuccesses, p, q);
        double p2 = SaddlePointExpansion.logBinomialProbability(this.sampleSize - x, this.populationSize - this.numberOfSuccesses, p, q);
        return (p1 + p2) - SaddlePointExpansion.logBinomialProbability(this.sampleSize, this.populationSize, p, q);
    }

    public double upperCumulativeProbability(int x) {
        int[] domain = getDomain(this.populationSize, this.numberOfSuccesses, this.sampleSize);
        if (x <= domain[0]) {
            return 1.0d;
        }
        if (x > domain[1]) {
            return 0.0d;
        }
        return innerCumulativeProbability(domain[1], x, -1);
    }

    private double innerCumulativeProbability(int x0, int x1, int dx) {
        double ret = probability(x0);
        while (x0 != x1) {
            x0 += dx;
            ret += probability(x0);
        }
        return ret;
    }

    @Override // org.apache.commons.math3.distribution.IntegerDistribution
    public double getNumericalMean() {
        return ((double) getSampleSize()) * (((double) getNumberOfSuccesses()) / ((double) getPopulationSize()));
    }

    @Override // org.apache.commons.math3.distribution.IntegerDistribution
    public double getNumericalVariance() {
        if (!this.numericalVarianceIsCalculated) {
            this.numericalVariance = calculateNumericalVariance();
            this.numericalVarianceIsCalculated = true;
        }
        return this.numericalVariance;
    }

    /* access modifiers changed from: protected */
    public double calculateNumericalVariance() {
        double N = (double) getPopulationSize();
        double m = (double) getNumberOfSuccesses();
        double n = (double) getSampleSize();
        return (((n * m) * (N - n)) * (N - m)) / ((N * N) * (N - 1.0d));
    }

    @Override // org.apache.commons.math3.distribution.IntegerDistribution
    public int getSupportLowerBound() {
        return FastMath.max(0, (getSampleSize() + getNumberOfSuccesses()) - getPopulationSize());
    }

    @Override // org.apache.commons.math3.distribution.IntegerDistribution
    public int getSupportUpperBound() {
        return FastMath.min(getNumberOfSuccesses(), getSampleSize());
    }

    @Override // org.apache.commons.math3.distribution.IntegerDistribution
    public boolean isSupportConnected() {
        return true;
    }
}
