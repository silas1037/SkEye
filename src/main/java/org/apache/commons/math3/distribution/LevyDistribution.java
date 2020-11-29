package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.special.Erf;
import org.apache.commons.math3.util.FastMath;

public class LevyDistribution extends AbstractRealDistribution {
    private static final long serialVersionUID = 20130314;

    /* renamed from: c */
    private final double f160c;
    private final double halfC;

    /* renamed from: mu */
    private final double f161mu;

    public LevyDistribution(double mu, double c) {
        this(new Well19937c(), mu, c);
    }

    public LevyDistribution(RandomGenerator rng, double mu, double c) {
        super(rng);
        this.f161mu = mu;
        this.f160c = c;
        this.halfC = 0.5d * c;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double density(double x) {
        if (x < this.f161mu) {
            return Double.NaN;
        }
        double delta = x - this.f161mu;
        double f = this.halfC / delta;
        return (FastMath.sqrt(f / 3.141592653589793d) * FastMath.exp(-f)) / delta;
    }

    @Override // org.apache.commons.math3.distribution.AbstractRealDistribution
    public double logDensity(double x) {
        if (x < this.f161mu) {
            return Double.NaN;
        }
        double delta = x - this.f161mu;
        double f = this.halfC / delta;
        return ((0.5d * FastMath.log(f / 3.141592653589793d)) - f) - FastMath.log(delta);
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double cumulativeProbability(double x) {
        if (x < this.f161mu) {
            return Double.NaN;
        }
        return Erf.erfc(FastMath.sqrt(this.halfC / (x - this.f161mu)));
    }

    @Override // org.apache.commons.math3.distribution.AbstractRealDistribution, org.apache.commons.math3.distribution.RealDistribution
    public double inverseCumulativeProbability(double p) throws OutOfRangeException {
        if (p < 0.0d || p > 1.0d) {
            throw new OutOfRangeException(Double.valueOf(p), 0, 1);
        }
        double t = Erf.erfcInv(p);
        return this.f161mu + (this.halfC / (t * t));
    }

    public double getScale() {
        return this.f160c;
    }

    public double getLocation() {
        return this.f161mu;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double getNumericalMean() {
        return Double.POSITIVE_INFINITY;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double getNumericalVariance() {
        return Double.POSITIVE_INFINITY;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double getSupportLowerBound() {
        return this.f161mu;
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
