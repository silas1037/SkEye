package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.util.FastMath;

public class ParetoDistribution extends AbstractRealDistribution {
    public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1.0E-9d;
    private static final long serialVersionUID = 20130424;
    private final double scale;
    private final double shape;
    private final double solverAbsoluteAccuracy;

    public ParetoDistribution() {
        this(1.0d, 1.0d);
    }

    public ParetoDistribution(double scale2, double shape2) throws NotStrictlyPositiveException {
        this(scale2, shape2, 1.0E-9d);
    }

    public ParetoDistribution(double scale2, double shape2, double inverseCumAccuracy) throws NotStrictlyPositiveException {
        this(new Well19937c(), scale2, shape2, inverseCumAccuracy);
    }

    public ParetoDistribution(RandomGenerator rng, double scale2, double shape2) throws NotStrictlyPositiveException {
        this(rng, scale2, shape2, 1.0E-9d);
    }

    public ParetoDistribution(RandomGenerator rng, double scale2, double shape2, double inverseCumAccuracy) throws NotStrictlyPositiveException {
        super(rng);
        if (scale2 <= 0.0d) {
            throw new NotStrictlyPositiveException(LocalizedFormats.SCALE, Double.valueOf(scale2));
        } else if (shape2 <= 0.0d) {
            throw new NotStrictlyPositiveException(LocalizedFormats.SHAPE, Double.valueOf(shape2));
        } else {
            this.scale = scale2;
            this.shape = shape2;
            this.solverAbsoluteAccuracy = inverseCumAccuracy;
        }
    }

    public double getScale() {
        return this.scale;
    }

    public double getShape() {
        return this.shape;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double density(double x) {
        if (x < this.scale) {
            return 0.0d;
        }
        return (FastMath.pow(this.scale, this.shape) / FastMath.pow(x, this.shape + 1.0d)) * this.shape;
    }

    @Override // org.apache.commons.math3.distribution.AbstractRealDistribution
    public double logDensity(double x) {
        if (x < this.scale) {
            return Double.NEGATIVE_INFINITY;
        }
        return ((FastMath.log(this.scale) * this.shape) - (FastMath.log(x) * (this.shape + 1.0d))) + FastMath.log(this.shape);
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double cumulativeProbability(double x) {
        if (x <= this.scale) {
            return 0.0d;
        }
        return 1.0d - FastMath.pow(this.scale / x, this.shape);
    }

    @Override // org.apache.commons.math3.distribution.AbstractRealDistribution, org.apache.commons.math3.distribution.RealDistribution
    @Deprecated
    public double cumulativeProbability(double x0, double x1) throws NumberIsTooLargeException {
        return probability(x0, x1);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.distribution.AbstractRealDistribution
    public double getSolverAbsoluteAccuracy() {
        return this.solverAbsoluteAccuracy;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double getNumericalMean() {
        if (this.shape <= 1.0d) {
            return Double.POSITIVE_INFINITY;
        }
        return (this.shape * this.scale) / (this.shape - 1.0d);
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double getNumericalVariance() {
        if (this.shape <= 2.0d) {
            return Double.POSITIVE_INFINITY;
        }
        double s = this.shape - 1.0d;
        return (((this.scale * this.scale) * this.shape) / (s * s)) / (this.shape - 2.0d);
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double getSupportLowerBound() {
        return this.scale;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double getSupportUpperBound() {
        return Double.POSITIVE_INFINITY;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public boolean isSupportLowerBoundInclusive() {
        return true;
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
        return this.scale / FastMath.pow(this.random.nextDouble(), 1.0d / this.shape);
    }
}
