package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.util.FastMath;

public class LaplaceDistribution extends AbstractRealDistribution {
    private static final long serialVersionUID = 20141003;
    private final double beta;

    /* renamed from: mu */
    private final double f159mu;

    public LaplaceDistribution(double mu, double beta2) {
        this(new Well19937c(), mu, beta2);
    }

    public LaplaceDistribution(RandomGenerator rng, double mu, double beta2) {
        super(rng);
        if (beta2 <= 0.0d) {
            throw new NotStrictlyPositiveException(LocalizedFormats.NOT_POSITIVE_SCALE, Double.valueOf(beta2));
        }
        this.f159mu = mu;
        this.beta = beta2;
    }

    public double getLocation() {
        return this.f159mu;
    }

    public double getScale() {
        return this.beta;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double density(double x) {
        return FastMath.exp((-FastMath.abs(x - this.f159mu)) / this.beta) / (2.0d * this.beta);
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double cumulativeProbability(double x) {
        if (x <= this.f159mu) {
            return FastMath.exp((x - this.f159mu) / this.beta) / 2.0d;
        }
        return 1.0d - (FastMath.exp((this.f159mu - x) / this.beta) / 2.0d);
    }

    @Override // org.apache.commons.math3.distribution.AbstractRealDistribution, org.apache.commons.math3.distribution.RealDistribution
    public double inverseCumulativeProbability(double p) throws OutOfRangeException {
        if (p < 0.0d || p > 1.0d) {
            throw new OutOfRangeException(Double.valueOf(p), Double.valueOf(0.0d), Double.valueOf(1.0d));
        } else if (p == 0.0d) {
            return Double.NEGATIVE_INFINITY;
        } else {
            if (p == 1.0d) {
                return Double.POSITIVE_INFINITY;
            }
            return this.f159mu + (this.beta * (p > 0.5d ? -Math.log(2.0d - (2.0d * p)) : Math.log(2.0d * p)));
        }
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double getNumericalMean() {
        return this.f159mu;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double getNumericalVariance() {
        return 2.0d * this.beta * this.beta;
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
