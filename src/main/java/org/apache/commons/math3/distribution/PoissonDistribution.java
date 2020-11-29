package org.apache.commons.math3.distribution;

import org.apache.commons.math3.analysis.integration.BaseAbstractUnivariateIntegrator;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.special.Gamma;
import org.apache.commons.math3.util.CombinatoricsUtils;
import org.apache.commons.math3.util.FastMath;

public class PoissonDistribution extends AbstractIntegerDistribution {
    public static final double DEFAULT_EPSILON = 1.0E-12d;
    public static final int DEFAULT_MAX_ITERATIONS = 10000000;
    private static final long serialVersionUID = -3349935121172596109L;
    private final double epsilon;
    private final ExponentialDistribution exponential;
    private final int maxIterations;
    private final double mean;
    private final NormalDistribution normal;

    public PoissonDistribution(double p) throws NotStrictlyPositiveException {
        this(p, 1.0E-12d, DEFAULT_MAX_ITERATIONS);
    }

    public PoissonDistribution(double p, double epsilon2, int maxIterations2) throws NotStrictlyPositiveException {
        this(new Well19937c(), p, epsilon2, maxIterations2);
    }

    public PoissonDistribution(RandomGenerator rng, double p, double epsilon2, int maxIterations2) throws NotStrictlyPositiveException {
        super(rng);
        if (p <= 0.0d) {
            throw new NotStrictlyPositiveException(LocalizedFormats.MEAN, Double.valueOf(p));
        }
        this.mean = p;
        this.epsilon = epsilon2;
        this.maxIterations = maxIterations2;
        this.normal = new NormalDistribution(rng, p, FastMath.sqrt(p), 1.0E-9d);
        this.exponential = new ExponentialDistribution(rng, 1.0d, 1.0E-9d);
    }

    public PoissonDistribution(double p, double epsilon2) throws NotStrictlyPositiveException {
        this(p, epsilon2, DEFAULT_MAX_ITERATIONS);
    }

    public PoissonDistribution(double p, int maxIterations2) {
        this(p, 1.0E-12d, maxIterations2);
    }

    public double getMean() {
        return this.mean;
    }

    @Override // org.apache.commons.math3.distribution.IntegerDistribution
    public double probability(int x) {
        double logProbability = logProbability(x);
        if (logProbability == Double.NEGATIVE_INFINITY) {
            return 0.0d;
        }
        return FastMath.exp(logProbability);
    }

    @Override // org.apache.commons.math3.distribution.AbstractIntegerDistribution
    public double logProbability(int x) {
        if (x < 0 || x == Integer.MAX_VALUE) {
            return Double.NEGATIVE_INFINITY;
        }
        if (x == 0) {
            return -this.mean;
        }
        return (((-SaddlePointExpansion.getStirlingError((double) x)) - SaddlePointExpansion.getDeviancePart((double) x, this.mean)) - (FastMath.log(6.283185307179586d) * 0.5d)) - (FastMath.log((double) x) * 0.5d);
    }

    @Override // org.apache.commons.math3.distribution.IntegerDistribution
    public double cumulativeProbability(int x) {
        if (x < 0) {
            return 0.0d;
        }
        if (x != Integer.MAX_VALUE) {
            return Gamma.regularizedGammaQ(1.0d + ((double) x), this.mean, this.epsilon, this.maxIterations);
        }
        return 1.0d;
    }

    public double normalApproximateProbability(int x) {
        return this.normal.cumulativeProbability(((double) x) + 0.5d);
    }

    @Override // org.apache.commons.math3.distribution.IntegerDistribution
    public double getNumericalMean() {
        return getMean();
    }

    @Override // org.apache.commons.math3.distribution.IntegerDistribution
    public double getNumericalVariance() {
        return getMean();
    }

    @Override // org.apache.commons.math3.distribution.IntegerDistribution
    public int getSupportLowerBound() {
        return 0;
    }

    @Override // org.apache.commons.math3.distribution.IntegerDistribution
    public int getSupportUpperBound() {
        return BaseAbstractUnivariateIntegrator.DEFAULT_MAX_ITERATIONS_COUNT;
    }

    @Override // org.apache.commons.math3.distribution.IntegerDistribution
    public boolean isSupportConnected() {
        return true;
    }

    @Override // org.apache.commons.math3.distribution.AbstractIntegerDistribution, org.apache.commons.math3.distribution.IntegerDistribution
    public int sample() {
        return (int) FastMath.min(nextPoisson(this.mean), 2147483647L);
    }

    private long nextPoisson(double meanPoisson) {
        long y2;
        double y;
        double x;
        double y3;
        double v;
        if (meanPoisson < 40.0d) {
            double p = FastMath.exp(-meanPoisson);
            long n = 0;
            double r = 1.0d;
            while (((double) n) < 1000.0d * meanPoisson) {
                r *= this.random.nextDouble();
                if (r < p) {
                    return n;
                }
                n++;
            }
            return n;
        }
        double lambda = FastMath.floor(meanPoisson);
        double lambdaFractional = meanPoisson - lambda;
        double logLambda = FastMath.log(lambda);
        double logLambdaFactorial = CombinatoricsUtils.factorialLog((int) lambda);
        if (lambdaFractional < Double.MIN_VALUE) {
            y2 = 0;
        } else {
            y2 = nextPoisson(lambdaFractional);
        }
        double delta = FastMath.sqrt(FastMath.log(((32.0d * lambda) / 3.141592653589793d) + 1.0d) * lambda);
        double halfDelta = delta / 2.0d;
        double twolpd = (2.0d * lambda) + delta;
        double a1 = FastMath.sqrt(3.141592653589793d * twolpd) * FastMath.exp(1.0d / (8.0d * lambda));
        double a2 = (twolpd / delta) * FastMath.exp(((-delta) * (1.0d + delta)) / twolpd);
        double aSum = a1 + a2 + 1.0d;
        double p1 = a1 / aSum;
        double p2 = a2 / aSum;
        double c1 = 1.0d / (8.0d * lambda);
        while (true) {
            double u = this.random.nextDouble();
            if (u <= p1) {
                double n2 = this.random.nextGaussian();
                x = (FastMath.sqrt(lambda + halfDelta) * n2) - 0.5d;
                if (x <= delta && x >= (-lambda)) {
                    y3 = x < 0.0d ? FastMath.floor(x) : FastMath.ceil(x);
                    v = ((-this.exponential.sample()) - ((n2 * n2) / 2.0d)) + c1;
                }
            } else if (u > p1 + p2) {
                y = lambda;
                break;
            } else {
                x = delta + ((twolpd / delta) * this.exponential.sample());
                y3 = FastMath.ceil(x);
                v = (-this.exponential.sample()) - (((1.0d + x) * delta) / twolpd);
            }
            int a = x < 0.0d ? 1 : 0;
            double t = ((1.0d + y3) * y3) / (2.0d * lambda);
            if (v < (-t) && a == 0) {
                y = y3 + lambda;
                break;
            }
            double qr = t * ((((2.0d * y3) + 1.0d) / (6.0d * lambda)) - 1.0d);
            if (v >= qr - ((t * t) / (3.0d * ((((double) a) * (1.0d + y3)) + lambda)))) {
                if (v <= qr && v < ((y3 * logLambda) - CombinatoricsUtils.factorialLog((int) (y3 + lambda))) + logLambdaFactorial) {
                    y = y3 + lambda;
                    break;
                }
            } else {
                y = y3 + lambda;
                break;
            }
        }
        return y2 + ((long) y);
    }
}
