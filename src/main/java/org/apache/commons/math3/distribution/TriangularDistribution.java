package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.util.FastMath;

public class TriangularDistribution extends AbstractRealDistribution {
    private static final long serialVersionUID = 20120112;

    /* renamed from: a */
    private final double f165a;

    /* renamed from: b */
    private final double f166b;

    /* renamed from: c */
    private final double f167c;
    private final double solverAbsoluteAccuracy;

    public TriangularDistribution(double a, double c, double b) throws NumberIsTooLargeException, NumberIsTooSmallException {
        this(new Well19937c(), a, c, b);
    }

    public TriangularDistribution(RandomGenerator rng, double a, double c, double b) throws NumberIsTooLargeException, NumberIsTooSmallException {
        super(rng);
        if (a >= b) {
            throw new NumberIsTooLargeException(LocalizedFormats.LOWER_BOUND_NOT_BELOW_UPPER_BOUND, Double.valueOf(a), Double.valueOf(b), false);
        } else if (c < a) {
            throw new NumberIsTooSmallException(LocalizedFormats.NUMBER_TOO_SMALL, Double.valueOf(c), Double.valueOf(a), true);
        } else if (c > b) {
            throw new NumberIsTooLargeException(LocalizedFormats.NUMBER_TOO_LARGE, Double.valueOf(c), Double.valueOf(b), true);
        } else {
            this.f165a = a;
            this.f167c = c;
            this.f166b = b;
            this.solverAbsoluteAccuracy = FastMath.max(FastMath.ulp(a), FastMath.ulp(b));
        }
    }

    public double getMode() {
        return this.f167c;
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.distribution.AbstractRealDistribution
    public double getSolverAbsoluteAccuracy() {
        return this.solverAbsoluteAccuracy;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double density(double x) {
        if (x < this.f165a) {
            return 0.0d;
        }
        if (this.f165a <= x && x < this.f167c) {
            return (2.0d * (x - this.f165a)) / ((this.f166b - this.f165a) * (this.f167c - this.f165a));
        }
        if (x == this.f167c) {
            return 2.0d / (this.f166b - this.f165a);
        }
        if (this.f167c >= x || x > this.f166b) {
            return 0.0d;
        }
        return (2.0d * (this.f166b - x)) / ((this.f166b - this.f165a) * (this.f166b - this.f167c));
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double cumulativeProbability(double x) {
        if (x < this.f165a) {
            return 0.0d;
        }
        if (this.f165a <= x && x < this.f167c) {
            return ((x - this.f165a) * (x - this.f165a)) / ((this.f166b - this.f165a) * (this.f167c - this.f165a));
        }
        if (x == this.f167c) {
            return (this.f167c - this.f165a) / (this.f166b - this.f165a);
        }
        if (this.f167c >= x || x > this.f166b) {
            return 1.0d;
        }
        return 1.0d - (((this.f166b - x) * (this.f166b - x)) / ((this.f166b - this.f165a) * (this.f166b - this.f167c)));
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double getNumericalMean() {
        return ((this.f165a + this.f166b) + this.f167c) / 3.0d;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double getNumericalVariance() {
        return ((((((this.f165a * this.f165a) + (this.f166b * this.f166b)) + (this.f167c * this.f167c)) - (this.f165a * this.f166b)) - (this.f165a * this.f167c)) - (this.f166b * this.f167c)) / 18.0d;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double getSupportLowerBound() {
        return this.f165a;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double getSupportUpperBound() {
        return this.f166b;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public boolean isSupportLowerBoundInclusive() {
        return true;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public boolean isSupportUpperBoundInclusive() {
        return true;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public boolean isSupportConnected() {
        return true;
    }

    @Override // org.apache.commons.math3.distribution.AbstractRealDistribution, org.apache.commons.math3.distribution.RealDistribution
    public double inverseCumulativeProbability(double p) throws OutOfRangeException {
        if (p < 0.0d || p > 1.0d) {
            throw new OutOfRangeException(Double.valueOf(p), 0, 1);
        } else if (p == 0.0d) {
            return this.f165a;
        } else {
            if (p == 1.0d) {
                return this.f166b;
            }
            if (p < (this.f167c - this.f165a) / (this.f166b - this.f165a)) {
                return this.f165a + FastMath.sqrt((this.f166b - this.f165a) * p * (this.f167c - this.f165a));
            }
            return this.f166b - FastMath.sqrt(((1.0d - p) * (this.f166b - this.f165a)) * (this.f166b - this.f167c));
        }
    }
}
