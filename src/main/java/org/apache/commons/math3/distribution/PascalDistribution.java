package org.apache.commons.math3.distribution;

import org.apache.commons.math3.analysis.integration.BaseAbstractUnivariateIntegrator;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.special.Beta;
import org.apache.commons.math3.util.CombinatoricsUtils;
import org.apache.commons.math3.util.FastMath;

public class PascalDistribution extends AbstractIntegerDistribution {
    private static final long serialVersionUID = 6751309484392813623L;
    private final double log1mProbabilityOfSuccess;
    private final double logProbabilityOfSuccess;
    private final int numberOfSuccesses;
    private final double probabilityOfSuccess;

    public PascalDistribution(int r, double p) throws NotStrictlyPositiveException, OutOfRangeException {
        this(new Well19937c(), r, p);
    }

    public PascalDistribution(RandomGenerator rng, int r, double p) throws NotStrictlyPositiveException, OutOfRangeException {
        super(rng);
        if (r <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.NUMBER_OF_SUCCESSES, Integer.valueOf(r));
        } else if (p < 0.0d || p > 1.0d) {
            throw new OutOfRangeException(Double.valueOf(p), 0, 1);
        } else {
            this.numberOfSuccesses = r;
            this.probabilityOfSuccess = p;
            this.logProbabilityOfSuccess = FastMath.log(p);
            this.log1mProbabilityOfSuccess = FastMath.log1p(-p);
        }
    }

    public int getNumberOfSuccesses() {
        return this.numberOfSuccesses;
    }

    public double getProbabilityOfSuccess() {
        return this.probabilityOfSuccess;
    }

    @Override // org.apache.commons.math3.distribution.IntegerDistribution
    public double probability(int x) {
        if (x < 0) {
            return 0.0d;
        }
        return CombinatoricsUtils.binomialCoefficientDouble((this.numberOfSuccesses + x) - 1, this.numberOfSuccesses - 1) * FastMath.pow(this.probabilityOfSuccess, this.numberOfSuccesses) * FastMath.pow(1.0d - this.probabilityOfSuccess, x);
    }

    @Override // org.apache.commons.math3.distribution.AbstractIntegerDistribution
    public double logProbability(int x) {
        if (x < 0) {
            return Double.NEGATIVE_INFINITY;
        }
        return CombinatoricsUtils.binomialCoefficientLog((this.numberOfSuccesses + x) - 1, this.numberOfSuccesses - 1) + (this.logProbabilityOfSuccess * ((double) this.numberOfSuccesses)) + (this.log1mProbabilityOfSuccess * ((double) x));
    }

    @Override // org.apache.commons.math3.distribution.IntegerDistribution
    public double cumulativeProbability(int x) {
        if (x < 0) {
            return 0.0d;
        }
        return Beta.regularizedBeta(this.probabilityOfSuccess, (double) this.numberOfSuccesses, ((double) x) + 1.0d);
    }

    @Override // org.apache.commons.math3.distribution.IntegerDistribution
    public double getNumericalMean() {
        double p = getProbabilityOfSuccess();
        return ((1.0d - p) * ((double) getNumberOfSuccesses())) / p;
    }

    @Override // org.apache.commons.math3.distribution.IntegerDistribution
    public double getNumericalVariance() {
        double p = getProbabilityOfSuccess();
        return ((1.0d - p) * ((double) getNumberOfSuccesses())) / (p * p);
    }

    @Override // org.apache.commons.math3.distribution.IntegerDistribution
    public int getSupportLowerBound() {
        return 0;
    }

    @Override // org.apache.commons.math3.distribution.IntegerDistribution
    public int getSupportUpperBound() {
        return BaseAbstractUnivariateIntegrator.DEFAULT_MAX_ITERATIONS_COUNT;
    }

    @Override // org.apache.commons.math3.distribution.IntegerDistribution
    public boolean isSupportConnected() {
        return true;
    }
}
