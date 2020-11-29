package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.special.Gamma;
import org.apache.commons.math3.util.FastMath;

public class NakagamiDistribution extends AbstractRealDistribution {
    public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1.0E-9d;
    private static final long serialVersionUID = 20141003;
    private final double inverseAbsoluteAccuracy;

    /* renamed from: mu */
    private final double f164mu;
    private final double omega;

    public NakagamiDistribution(double mu, double omega2) {
        this(mu, omega2, 1.0E-9d);
    }

    public NakagamiDistribution(double mu, double omega2, double inverseAbsoluteAccuracy2) {
        this(new Well19937c(), mu, omega2, inverseAbsoluteAccuracy2);
    }

    public NakagamiDistribution(RandomGenerator rng, double mu, double omega2, double inverseAbsoluteAccuracy2) {
        super(rng);
        if (mu < 0.5d) {
            throw new NumberIsTooSmallException(Double.valueOf(mu), Double.valueOf(0.5d), true);
        } else if (omega2 <= 0.0d) {
            throw new NotStrictlyPositiveException(LocalizedFormats.NOT_POSITIVE_SCALE, Double.valueOf(omega2));
        } else {
            this.f164mu = mu;
            this.omega = omega2;
            this.inverseAbsoluteAccuracy = inverseAbsoluteAccuracy2;
        }
    }

    public double getShape() {
        return this.f164mu;
    }

    public double getScale() {
        return this.omega;
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.distribution.AbstractRealDistribution
    public double getSolverAbsoluteAccuracy() {
        return this.inverseAbsoluteAccuracy;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double density(double x) {
        if (x <= 0.0d) {
            return 0.0d;
        }
        return ((FastMath.pow(this.f164mu, this.f164mu) * 2.0d) / (Gamma.gamma(this.f164mu) * FastMath.pow(this.omega, this.f164mu))) * FastMath.pow(x, (this.f164mu * 2.0d) - 1.0d) * FastMath.exp((((-this.f164mu) * x) * x) / this.omega);
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double cumulativeProbability(double x) {
        return Gamma.regularizedGammaP(this.f164mu, ((this.f164mu * x) * x) / this.omega);
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double getNumericalMean() {
        return (Gamma.gamma(this.f164mu + 0.5d) / Gamma.gamma(this.f164mu)) * FastMath.sqrt(this.omega / this.f164mu);
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double getNumericalVariance() {
        double v = Gamma.gamma(this.f164mu + 0.5d) / Gamma.gamma(this.f164mu);
        return this.omega * (1.0d - (((1.0d / this.f164mu) * v) * v));
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
}
