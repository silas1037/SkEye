package org.apache.commons.math3.distribution;

import java.io.Serializable;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.UnivariateSolverUtils;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomDataImpl;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.util.FastMath;

public abstract class AbstractRealDistribution implements RealDistribution, Serializable {
    public static final double SOLVER_DEFAULT_ABSOLUTE_ACCURACY = 1.0E-6d;
    private static final long serialVersionUID = -38038050983108802L;
    protected final RandomGenerator random;
    @Deprecated
    protected RandomDataImpl randomData;
    private double solverAbsoluteAccuracy;

    @Deprecated
    protected AbstractRealDistribution() {
        this.randomData = new RandomDataImpl();
        this.solverAbsoluteAccuracy = 1.0E-6d;
        this.random = null;
    }

    protected AbstractRealDistribution(RandomGenerator rng) {
        this.randomData = new RandomDataImpl();
        this.solverAbsoluteAccuracy = 1.0E-6d;
        this.random = rng;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    @Deprecated
    public double cumulativeProbability(double x0, double x1) throws NumberIsTooLargeException {
        return probability(x0, x1);
    }

    public double probability(double x0, double x1) {
        if (x0 <= x1) {
            return cumulativeProbability(x1) - cumulativeProbability(x0);
        }
        throw new NumberIsTooLargeException(LocalizedFormats.LOWER_ENDPOINT_ABOVE_UPPER_ENDPOINT, Double.valueOf(x0), Double.valueOf(x1), true);
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double inverseCumulativeProbability(final double p) throws OutOfRangeException {
        if (p < 0.0d || p > 1.0d) {
            throw new OutOfRangeException(Double.valueOf(p), 0, 1);
        }
        double lowerBound = getSupportLowerBound();
        if (p == 0.0d) {
            return lowerBound;
        }
        double upperBound = getSupportUpperBound();
        if (p == 1.0d) {
            return upperBound;
        }
        double mu = getNumericalMean();
        double sig = FastMath.sqrt(getNumericalVariance());
        boolean chebyshevApplies = !Double.isInfinite(mu) && !Double.isNaN(mu) && !Double.isInfinite(sig) && !Double.isNaN(sig);
        if (lowerBound == Double.NEGATIVE_INFINITY) {
            if (chebyshevApplies) {
                lowerBound = mu - (FastMath.sqrt((1.0d - p) / p) * sig);
            } else {
                lowerBound = -1.0d;
                while (cumulativeProbability(lowerBound) >= p) {
                    lowerBound *= 2.0d;
                }
            }
        }
        if (upperBound == Double.POSITIVE_INFINITY) {
            if (chebyshevApplies) {
                upperBound = mu + (FastMath.sqrt(p / (1.0d - p)) * sig);
            } else {
                upperBound = 1.0d;
                while (cumulativeProbability(upperBound) < p) {
                    upperBound *= 2.0d;
                }
            }
        }
        double x = UnivariateSolverUtils.solve(new UnivariateFunction() {
            /* class org.apache.commons.math3.distribution.AbstractRealDistribution.C02201 */

            @Override // org.apache.commons.math3.analysis.UnivariateFunction
            public double value(double x) {
                return AbstractRealDistribution.this.cumulativeProbability(x) - p;
            }
        }, lowerBound, upperBound, getSolverAbsoluteAccuracy());
        if (!isSupportConnected()) {
            double dx = getSolverAbsoluteAccuracy();
            if (x - dx >= getSupportLowerBound()) {
                double px = cumulativeProbability(x);
                if (cumulativeProbability(x - dx) == px) {
                    double upperBound2 = x;
                    while (upperBound2 - lowerBound > dx) {
                        double midPoint = 0.5d * (lowerBound + upperBound2);
                        if (cumulativeProbability(midPoint) < px) {
                            lowerBound = midPoint;
                        } else {
                            upperBound2 = midPoint;
                        }
                    }
                    return upperBound2;
                }
            }
        }
        return x;
    }

    /* access modifiers changed from: protected */
    public double getSolverAbsoluteAccuracy() {
        return this.solverAbsoluteAccuracy;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public void reseedRandomGenerator(long seed) {
        this.random.setSeed(seed);
        this.randomData.reSeed(seed);
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double sample() {
        return inverseCumulativeProbability(this.random.nextDouble());
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double[] sample(int sampleSize) {
        if (sampleSize <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.NUMBER_OF_SAMPLES, Integer.valueOf(sampleSize));
        }
        double[] out = new double[sampleSize];
        for (int i = 0; i < sampleSize; i++) {
            out[i] = sample();
        }
        return out;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double probability(double x) {
        return 0.0d;
    }

    public double logDensity(double x) {
        return FastMath.log(density(x));
    }
}
