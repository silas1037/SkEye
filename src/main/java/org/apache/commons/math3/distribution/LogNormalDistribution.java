package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.special.Erf;
import org.apache.commons.math3.util.FastMath;

public class LogNormalDistribution extends AbstractRealDistribution {
    public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1.0E-9d;
    private static final double SQRT2 = FastMath.sqrt(2.0d);
    private static final double SQRT2PI = FastMath.sqrt(6.283185307179586d);
    private static final long serialVersionUID = 20120112;
    private final double logShapePlusHalfLog2Pi;
    private final double scale;
    private final double shape;
    private final double solverAbsoluteAccuracy;

    public LogNormalDistribution() {
        this(0.0d, 1.0d);
    }

    public LogNormalDistribution(double scale2, double shape2) throws NotStrictlyPositiveException {
        this(scale2, shape2, 1.0E-9d);
    }

    public LogNormalDistribution(double scale2, double shape2, double inverseCumAccuracy) throws NotStrictlyPositiveException {
        this(new Well19937c(), scale2, shape2, inverseCumAccuracy);
    }

    public LogNormalDistribution(RandomGenerator rng, double scale2, double shape2) throws NotStrictlyPositiveException {
        this(rng, scale2, shape2, 1.0E-9d);
    }

    public LogNormalDistribution(RandomGenerator rng, double scale2, double shape2, double inverseCumAccuracy) throws NotStrictlyPositiveException {
        super(rng);
        if (shape2 <= 0.0d) {
            throw new NotStrictlyPositiveException(LocalizedFormats.SHAPE, Double.valueOf(shape2));
        }
        this.scale = scale2;
        this.shape = shape2;
        this.logShapePlusHalfLog2Pi = FastMath.log(shape2) + (0.5d * FastMath.log(6.283185307179586d));
        this.solverAbsoluteAccuracy = inverseCumAccuracy;
    }

    public double getScale() {
        return this.scale;
    }

    public double getShape() {
        return this.shape;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double density(double x) {
        if (x <= 0.0d) {
            return 0.0d;
        }
        double x1 = (FastMath.log(x) - this.scale) / this.shape;
        return FastMath.exp((-0.5d * x1) * x1) / ((this.shape * SQRT2PI) * x);
    }

    @Override // org.apache.commons.math3.distribution.AbstractRealDistribution
    public double logDensity(double x) {
        if (x <= 0.0d) {
            return Double.NEGATIVE_INFINITY;
        }
        double logX = FastMath.log(x);
        double x1 = (logX - this.scale) / this.shape;
        return ((-0.5d * x1) * x1) - (this.logShapePlusHalfLog2Pi + logX);
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double cumulativeProbability(double x) {
        if (x <= 0.0d) {
            return 0.0d;
        }
        double dev = FastMath.log(x) - this.scale;
        if (FastMath.abs(dev) <= 40.0d * this.shape) {
            return (Erf.erf(dev / (this.shape * SQRT2)) * 0.5d) + 0.5d;
        }
        if (dev >= 0.0d) {
            return 1.0d;
        }
        return 0.0d;
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
        } else if (x0 <= 0.0d || x1 <= 0.0d) {
            return super.probability(x0, x1);
        } else {
            double denom = this.shape * SQRT2;
            return 0.5d * Erf.erf((FastMath.log(x0) - this.scale) / denom, (FastMath.log(x1) - this.scale) / denom);
        }
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.distribution.AbstractRealDistribution
    public double getSolverAbsoluteAccuracy() {
        return this.solverAbsoluteAccuracy;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double getNumericalMean() {
        double s = this.shape;
        return FastMath.exp(this.scale + ((s * s) / 2.0d));
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double getNumericalVariance() {
        double s = this.shape;
        double ss = s * s;
        return FastMath.expm1(ss) * FastMath.exp((2.0d * this.scale) + ss);
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double getSupportLowerBound() {
        return 0.0d;
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
        return FastMath.exp(this.scale + (this.shape * this.random.nextGaussian()));
    }
}
