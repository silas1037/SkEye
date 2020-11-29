package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.special.Erf;
import org.apache.commons.math3.util.FastMath;

public class NormalDistribution extends AbstractRealDistribution {
    public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1.0E-9d;
    private static final double SQRT2 = FastMath.sqrt(2.0d);
    private static final long serialVersionUID = 8589540077390120676L;
    private final double logStandardDeviationPlusHalfLog2Pi;
    private final double mean;
    private final double solverAbsoluteAccuracy;
    private final double standardDeviation;

    public NormalDistribution() {
        this(0.0d, 1.0d);
    }

    public NormalDistribution(double mean2, double sd) throws NotStrictlyPositiveException {
        this(mean2, sd, 1.0E-9d);
    }

    public NormalDistribution(double mean2, double sd, double inverseCumAccuracy) throws NotStrictlyPositiveException {
        this(new Well19937c(), mean2, sd, inverseCumAccuracy);
    }

    public NormalDistribution(RandomGenerator rng, double mean2, double sd) throws NotStrictlyPositiveException {
        this(rng, mean2, sd, 1.0E-9d);
    }

    public NormalDistribution(RandomGenerator rng, double mean2, double sd, double inverseCumAccuracy) throws NotStrictlyPositiveException {
        super(rng);
        if (sd <= 0.0d) {
            throw new NotStrictlyPositiveException(LocalizedFormats.STANDARD_DEVIATION, Double.valueOf(sd));
        }
        this.mean = mean2;
        this.standardDeviation = sd;
        this.logStandardDeviationPlusHalfLog2Pi = FastMath.log(sd) + (0.5d * FastMath.log(6.283185307179586d));
        this.solverAbsoluteAccuracy = inverseCumAccuracy;
    }

    public double getMean() {
        return this.mean;
    }

    public double getStandardDeviation() {
        return this.standardDeviation;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double density(double x) {
        return FastMath.exp(logDensity(x));
    }

    @Override // org.apache.commons.math3.distribution.AbstractRealDistribution
    public double logDensity(double x) {
        double x1 = (x - this.mean) / this.standardDeviation;
        return ((-0.5d * x1) * x1) - this.logStandardDeviationPlusHalfLog2Pi;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double cumulativeProbability(double x) {
        double dev = x - this.mean;
        if (FastMath.abs(dev) <= 40.0d * this.standardDeviation) {
            return 0.5d * Erf.erfc((-dev) / (this.standardDeviation * SQRT2));
        }
        if (dev < 0.0d) {
            return 0.0d;
        }
        return 1.0d;
    }

    @Override // org.apache.commons.math3.distribution.AbstractRealDistribution, org.apache.commons.math3.distribution.RealDistribution
    public double inverseCumulativeProbability(double p) throws OutOfRangeException {
        if (p >= 0.0d && p <= 1.0d) {
            return this.mean + (this.standardDeviation * SQRT2 * Erf.erfInv((2.0d * p) - 1.0d));
        }
        throw new OutOfRangeException(Double.valueOf(p), 0, 1);
    }

    @Override // org.apache.commons.math3.distribution.AbstractRealDistribution, org.apache.commons.math3.distribution.RealDistribution
    @Deprecated
    public double cumulativeProbability(double x0, double x1) throws NumberIsTooLargeException {
        return probability(x0, x1);
    }

    @Override // org.apache.commons.math3.distribution.AbstractRealDistribution
    public double probability(double x0, double x1) throws NumberIsTooLargeException {
        if (x0 > x1) {
            throw new NumberIsTooLargeException(LocalizedFormats.LOWER_ENDPOINT_ABOVE_UPPER_ENDPOINT, Double.valueOf(x0), Double.valueOf(x1), true);
        }
        double denom = this.standardDeviation * SQRT2;
        return 0.5d * Erf.erf((x0 - this.mean) / denom, (x1 - this.mean) / denom);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.distribution.AbstractRealDistribution
    public double getSolverAbsoluteAccuracy() {
        return this.solverAbsoluteAccuracy;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double getNumericalMean() {
        return getMean();
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double getNumericalVariance() {
        double s = getStandardDeviation();
        return s * s;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double getSupportLowerBound() {
        return Double.NEGATIVE_INFINITY;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double getSupportUpperBound() {
        return Double.POSITIVE_INFINITY;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public boolean isSupportLowerBoundInclusive() {
        return false;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public boolean isSupportUpperBoundInclusive() {
        return false;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public boolean isSupportConnected() {
        return true;
    }

    @Override // org.apache.commons.math3.distribution.AbstractRealDistribution, org.apache.commons.math3.distribution.RealDistribution
    public double sample() {
        return (this.standardDeviation * this.random.nextGaussian()) + this.mean;
    }
}
