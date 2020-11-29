package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.special.Beta;
import org.apache.commons.math3.special.Gamma;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Precision;

public class BetaDistribution extends AbstractRealDistribution {
    public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1.0E-9d;
    private static final long serialVersionUID = -1221965979403477668L;
    private final double alpha;
    private final double beta;
    private final double solverAbsoluteAccuracy;

    /* renamed from: z */
    private double f156z;

    public BetaDistribution(double alpha2, double beta2) {
        this(alpha2, beta2, 1.0E-9d);
    }

    public BetaDistribution(double alpha2, double beta2, double inverseCumAccuracy) {
        this(new Well19937c(), alpha2, beta2, inverseCumAccuracy);
    }

    public BetaDistribution(RandomGenerator rng, double alpha2, double beta2) {
        this(rng, alpha2, beta2, 1.0E-9d);
    }

    public BetaDistribution(RandomGenerator rng, double alpha2, double beta2, double inverseCumAccuracy) {
        super(rng);
        this.alpha = alpha2;
        this.beta = beta2;
        this.f156z = Double.NaN;
        this.solverAbsoluteAccuracy = inverseCumAccuracy;
    }

    public double getAlpha() {
        return this.alpha;
    }

    public double getBeta() {
        return this.beta;
    }

    private void recomputeZ() {
        if (Double.isNaN(this.f156z)) {
            this.f156z = (Gamma.logGamma(this.alpha) + Gamma.logGamma(this.beta)) - Gamma.logGamma(this.alpha + this.beta);
        }
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double density(double x) {
        double logDensity = logDensity(x);
        if (logDensity == Double.NEGATIVE_INFINITY) {
            return 0.0d;
        }
        return FastMath.exp(logDensity);
    }

    @Override // org.apache.commons.math3.distribution.AbstractRealDistribution
    public double logDensity(double x) {
        recomputeZ();
        if (x < 0.0d || x > 1.0d) {
            return Double.NEGATIVE_INFINITY;
        }
        if (x == 0.0d) {
            if (this.alpha >= 1.0d) {
                return Double.NEGATIVE_INFINITY;
            }
            throw new NumberIsTooSmallException(LocalizedFormats.CANNOT_COMPUTE_BETA_DENSITY_AT_0_FOR_SOME_ALPHA, Double.valueOf(this.alpha), 1, false);
        } else if (x != 1.0d) {
            return (((this.alpha - 1.0d) * FastMath.log(x)) + ((this.beta - 1.0d) * FastMath.log1p(-x))) - this.f156z;
        } else if (this.beta >= 1.0d) {
            return Double.NEGATIVE_INFINITY;
        } else {
            throw new NumberIsTooSmallException(LocalizedFormats.CANNOT_COMPUTE_BETA_DENSITY_AT_1_FOR_SOME_BETA, Double.valueOf(this.beta), 1, false);
        }
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double cumulativeProbability(double x) {
        if (x <= 0.0d) {
            return 0.0d;
        }
        if (x >= 1.0d) {
            return 1.0d;
        }
        return Beta.regularizedBeta(x, this.alpha, this.beta);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.distribution.AbstractRealDistribution
    public double getSolverAbsoluteAccuracy() {
        return this.solverAbsoluteAccuracy;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double getNumericalMean() {
        double a = getAlpha();
        return a / (getBeta() + a);
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double getNumericalVariance() {
        double a = getAlpha();
        double b = getBeta();
        double alphabetasum = a + b;
        return (a * b) / ((alphabetasum * alphabetasum) * (1.0d + alphabetasum));
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double getSupportLowerBound() {
        return 0.0d;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double getSupportUpperBound() {
        return 1.0d;
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
        return ChengBetaSampler.sample(this.random, this.alpha, this.beta);
    }

    /* access modifiers changed from: private */
    public static final class ChengBetaSampler {
        private ChengBetaSampler() {
        }

        static double sample(RandomGenerator random, double alpha, double beta) {
            double a = FastMath.min(alpha, beta);
            double b = FastMath.max(alpha, beta);
            if (a > 1.0d) {
                return algorithmBB(random, alpha, a, b);
            }
            return algorithmBC(random, alpha, b, a);
        }

        private static double algorithmBB(RandomGenerator random, double a0, double a, double b) {
            double w;
            double r;
            double t;
            double alpha = a + b;
            double beta = FastMath.sqrt((alpha - 2.0d) / (((2.0d * a) * b) - alpha));
            double gamma = a + (1.0d / beta);
            do {
                double u1 = random.nextDouble();
                double u2 = random.nextDouble();
                double v = beta * (FastMath.log(u1) - FastMath.log1p(-u1));
                w = a * FastMath.exp(v);
                double z = u1 * u1 * u2;
                r = (gamma * v) - 1.3862944d;
                double s = (a + r) - w;
                if (2.609438d + s < 5.0d * z) {
                    t = FastMath.log(z);
                    if (s >= t) {
                        break;
                    }
                } else {
                    break;
                }
            } while (((FastMath.log(alpha) - FastMath.log(b + w)) * alpha) + r < t);
            double w2 = FastMath.min(w, Double.MAX_VALUE);
            if (Precision.equals(a, a0)) {
                return w2 / (b + w2);
            }
            return b / (b + w2);
        }

        private static double algorithmBC(RandomGenerator random, double a0, double a, double b) {
            double w;
            double alpha = a + b;
            double beta = 1.0d / b;
            double delta = (1.0d + a) - b;
            double k1 = ((0.0138889d + (0.0416667d * b)) * delta) / ((a * beta) - 0.777778d);
            double k2 = 0.25d + ((0.5d + (0.25d / delta)) * b);
            while (true) {
                double u1 = random.nextDouble();
                double u2 = random.nextDouble();
                double y = u1 * u2;
                double z = u1 * y;
                if (u1 < 0.5d) {
                    if (((0.25d * u2) + z) - y >= k1) {
                        continue;
                    }
                } else if (z <= 0.25d) {
                    w = a * FastMath.exp(beta * (FastMath.log(u1) - FastMath.log1p(-u1)));
                    break;
                } else if (z >= k2) {
                    continue;
                }
                double v = beta * (FastMath.log(u1) - FastMath.log1p(-u1));
                w = a * FastMath.exp(v);
                if ((((FastMath.log(alpha) - FastMath.log(b + w)) + v) * alpha) - 1.3862944d >= FastMath.log(z)) {
                    break;
                }
            }
            double w2 = FastMath.min(w, Double.MAX_VALUE);
            if (Precision.equals(a, a0)) {
                return w2 / (b + w2);
            }
            return b / (b + w2);
        }
    }
}
