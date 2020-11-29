package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.special.Beta;
import org.apache.commons.math3.util.FastMath;

public class BinomialDistribution extends AbstractIntegerDistribution {
    private static final long serialVersionUID = 6751309484392813623L;
    private final int numberOfTrials;
    private final double probabilityOfSuccess;

    public BinomialDistribution(int trials, double p) {
        this(new Well19937c(), trials, p);
    }

    public BinomialDistribution(RandomGenerator rng, int trials, double p) {
        super(rng);
        if (trials < 0) {
            throw new NotPositiveException(LocalizedFormats.NUMBER_OF_TRIALS, Integer.valueOf(trials));
        } else if (p < 0.0d || p > 1.0d) {
            throw new OutOfRangeException(Double.valueOf(p), 0, 1);
        } else {
            this.probabilityOfSuccess = p;
            this.numberOfTrials = trials;
        }
    }

    public int getNumberOfTrials() {
        return this.numberOfTrials;
    }

    public double getProbabilityOfSuccess() {
        return this.probabilityOfSuccess;
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
        double ret;
        if (this.numberOfTrials == 0) {
            return x == 0 ? 0.0d : Double.NEGATIVE_INFINITY;
        }
        if (x < 0 || x > this.numberOfTrials) {
            ret = Double.NEGATIVE_INFINITY;
        } else {
            ret = SaddlePointExpansion.logBinomialProbability(x, this.numberOfTrials, this.probabilityOfSuccess, 1.0d - this.probabilityOfSuccess);
        }
        return ret;
    }

    @Override // org.apache.commons.math3.distribution.IntegerDistribution
    public double cumulativeProbability(int x) {
        if (x < 0) {
            return 0.0d;
        }
        if (x >= this.numberOfTrials) {
            return 1.0d;
        }
        return 1.0d - Beta.regularizedBeta(this.probabilityOfSuccess, ((double) x) + 1.0d, (double) (this.numberOfTrials - x));
    }

    @Override // org.apache.commons.math3.distribution.IntegerDistribution
    public double getNumericalMean() {
        return ((double) this.numberOfTrials) * this.probabilityOfSuccess;
    }

    @Override // org.apache.commons.math3.distribution.IntegerDistribution
    public double getNumericalVariance() {
        double p = this.probabilityOfSuccess;
        return ((double) this.numberOfTrials) * p * (1.0d - p);
    }

    @Override // org.apache.commons.math3.distribution.IntegerDistribution
    public int getSupportLowerBound() {
        if (this.probabilityOfSuccess < 1.0d) {
            return 0;
        }
        return this.numberOfTrials;
    }

    @Override // org.apache.commons.math3.distribution.IntegerDistribution
    public int getSupportUpperBound() {
        if (this.probabilityOfSuccess > 0.0d) {
            return this.numberOfTrials;
        }
        return 0;
    }

    @Override // org.apache.commons.math3.distribution.IntegerDistribution
    public boolean isSupportConnected() {
        return true;
    }
}
