package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.special.Beta;
import org.apache.commons.math3.special.Gamma;
import org.apache.commons.math3.util.FastMath;

public class TDistribution extends AbstractRealDistribution {
    public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1.0E-9d;
    private static final long serialVersionUID = -5852615386664158222L;
    private final double degreesOfFreedom;
    private final double factor;
    private final double solverAbsoluteAccuracy;

    public TDistribution(double degreesOfFreedom2) throws NotStrictlyPositiveException {
        this(degreesOfFreedom2, 1.0E-9d);
    }

    public TDistribution(double degreesOfFreedom2, double inverseCumAccuracy) throws NotStrictlyPositiveException {
        this(new Well19937c(), degreesOfFreedom2, inverseCumAccuracy);
    }

    public TDistribution(RandomGenerator rng, double degreesOfFreedom2) throws NotStrictlyPositiveException {
        this(rng, degreesOfFreedom2, 1.0E-9d);
    }

    public TDistribution(RandomGenerator rng, double degreesOfFreedom2, double inverseCumAccuracy) throws NotStrictlyPositiveException {
        super(rng);
        if (degreesOfFreedom2 <= 0.0d) {
            throw new NotStrictlyPositiveException(LocalizedFormats.DEGREES_OF_FREEDOM, Double.valueOf(degreesOfFreedom2));
        }
        this.degreesOfFreedom = degreesOfFreedom2;
        this.solverAbsoluteAccuracy = inverseCumAccuracy;
        this.factor = (Gamma.logGamma((1.0d + degreesOfFreedom2) / 2.0d) - (0.5d * (FastMath.log(3.141592653589793d) + FastMath.log(degreesOfFreedom2)))) - Gamma.logGamma(degreesOfFreedom2 / 2.0d);
    }

    public double getDegreesOfFreedom() {
        return this.degreesOfFreedom;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double density(double x) {
        return FastMath.exp(logDensity(x));
    }

    @Override // org.apache.commons.math3.distribution.AbstractRealDistribution
    public double logDensity(double x) {
        double n = this.degreesOfFreedom;
        return this.factor - (FastMath.log(((x * x) / n) + 1.0d) * ((n + 1.0d) / 2.0d));
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double cumulativeProbability(double x) {
        if (x == 0.0d) {
            return 0.5d;
        }
        double t = Beta.regularizedBeta(this.degreesOfFreedom / (this.degreesOfFreedom + (x * x)), 0.5d * this.degreesOfFreedom, 0.5d);
        if (x < 0.0d) {
            return 0.5d * t;
        }
        return 1.0d - (0.5d * t);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.distribution.AbstractRealDistribution
    public double getSolverAbsoluteAccuracy() {
        return this.solverAbsoluteAccuracy;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double getNumericalMean() {
        if (getDegreesOfFreedom() > 1.0d) {
            return 0.0d;
        }
        return Double.NaN;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double getNumericalVariance() {
        double df = getDegreesOfFreedom();
        if (df > 2.0d) {
            return df / (df - 2.0d);
        }
        if (df <= 1.0d || df > 2.0d) {
            return Double.NaN;
        }
        return Double.POSITIVE_INFINITY;
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
}
