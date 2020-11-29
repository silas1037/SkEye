package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.util.FastMath;

public class LogisticDistribution extends AbstractRealDistribution {
    private static final long serialVersionUID = 20141003;

    /* renamed from: mu */
    private final double f162mu;

    /* renamed from: s */
    private final double f163s;

    public LogisticDistribution(double mu, double s) {
        this(new Well19937c(), mu, s);
    }

    public LogisticDistribution(RandomGenerator rng, double mu, double s) {
        super(rng);
        if (s <= 0.0d) {
            throw new NotStrictlyPositiveException(LocalizedFormats.NOT_POSITIVE_SCALE, Double.valueOf(s));
        }
        this.f162mu = mu;
        this.f163s = s;
    }

    public double getLocation() {
        return this.f162mu;
    }

    public double getScale() {
        return this.f163s;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double density(double x) {
        double v = FastMath.exp(-((x - this.f162mu) / this.f163s));
        return ((1.0d / this.f163s) * v) / ((1.0d + v) * (1.0d + v));
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double cumulativeProbability(double x) {
        return 1.0d / (FastMath.exp(-((1.0d / this.f163s) * (x - this.f162mu))) + 1.0d);
    }

    @Override // org.apache.commons.math3.distribution.AbstractRealDistribution, org.apache.commons.math3.distribution.RealDistribution
    public double inverseCumulativeProbability(double p) throws OutOfRangeException {
        if (p < 0.0d || p > 1.0d) {
            throw new OutOfRangeException(Double.valueOf(p), Double.valueOf(0.0d), Double.valueOf(1.0d));
        } else if (p == 0.0d) {
            return 0.0d;
        } else {
            if (p == 1.0d) {
                return Double.POSITIVE_INFINITY;
            }
            return (this.f163s * Math.log(p / (1.0d - p))) + this.f162mu;
        }
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double getNumericalMean() {
        return this.f162mu;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double getNumericalVariance() {
        return 3.289868133696453d * (1.0d / (this.f163s * this.f163s));
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
