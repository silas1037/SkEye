package org.apache.commons.math3.distribution;

import java.io.Serializable;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomDataImpl;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.util.FastMath;

public abstract class AbstractIntegerDistribution implements IntegerDistribution, Serializable {
    private static final long serialVersionUID = -1146319659338487221L;
    protected final RandomGenerator random;
    @Deprecated
    protected final RandomDataImpl randomData;

    @Deprecated
    protected AbstractIntegerDistribution() {
        this.randomData = new RandomDataImpl();
        this.random = null;
    }

    protected AbstractIntegerDistribution(RandomGenerator rng) {
        this.randomData = new RandomDataImpl();
        this.random = rng;
    }

    @Override // org.apache.commons.math3.distribution.IntegerDistribution
    public double cumulativeProbability(int x0, int x1) throws NumberIsTooLargeException {
        if (x1 >= x0) {
            return cumulativeProbability(x1) - cumulativeProbability(x0);
        }
        throw new NumberIsTooLargeException(LocalizedFormats.LOWER_ENDPOINT_ABOVE_UPPER_ENDPOINT, Integer.valueOf(x0), Integer.valueOf(x1), true);
    }

    @Override // org.apache.commons.math3.distribution.IntegerDistribution
    public int inverseCumulativeProbability(double p) throws OutOfRangeException {
        if (p < 0.0d || p > 1.0d) {
            throw new OutOfRangeException(Double.valueOf(p), 0, 1);
        }
        int lower = getSupportLowerBound();
        if (p == 0.0d) {
            return lower;
        }
        if (lower != Integer.MIN_VALUE) {
            lower--;
        } else if (checkedCumulativeProbability(lower) >= p) {
            return lower;
        }
        int upper = getSupportUpperBound();
        if (p == 1.0d) {
            return upper;
        }
        double mu = getNumericalMean();
        double sigma = FastMath.sqrt(getNumericalVariance());
        if (!Double.isInfinite(mu) && !Double.isNaN(mu) && !Double.isInfinite(sigma) && !Double.isNaN(sigma) && sigma != 0.0d) {
            double k = FastMath.sqrt((1.0d - p) / p);
            double tmp = mu - (k * sigma);
            if (tmp > ((double) lower)) {
                lower = ((int) FastMath.ceil(tmp)) - 1;
            }
            double tmp2 = mu + ((1.0d / k) * sigma);
            if (tmp2 < ((double) upper)) {
                upper = ((int) FastMath.ceil(tmp2)) - 1;
            }
        }
        return solveInverseCumulativeProbability(p, lower, upper);
    }

    /* access modifiers changed from: protected */
    public int solveInverseCumulativeProbability(double p, int lower, int upper) {
        while (lower + 1 < upper) {
            int xm = (lower + upper) / 2;
            if (xm < lower || xm > upper) {
                xm = lower + ((upper - lower) / 2);
            }
            if (checkedCumulativeProbability(xm) >= p) {
                upper = xm;
            } else {
                lower = xm;
            }
        }
        return upper;
    }

    @Override // org.apache.commons.math3.distribution.IntegerDistribution
    public void reseedRandomGenerator(long seed) {
        this.random.setSeed(seed);
        this.randomData.reSeed(seed);
    }

    @Override // org.apache.commons.math3.distribution.IntegerDistribution
    public int sample() {
        return inverseCumulativeProbability(this.random.nextDouble());
    }

    @Override // org.apache.commons.math3.distribution.IntegerDistribution
    public int[] sample(int sampleSize) {
        if (sampleSize <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.NUMBER_OF_SAMPLES, Integer.valueOf(sampleSize));
        }
        int[] out = new int[sampleSize];
        for (int i = 0; i < sampleSize; i++) {
            out[i] = sample();
        }
        return out;
    }

    private double checkedCumulativeProbability(int argument) throws MathInternalError {
        double result = cumulativeProbability(argument);
        if (!Double.isNaN(result)) {
            return result;
        }
        throw new MathInternalError(LocalizedFormats.DISCRETE_CUMULATIVE_PROBABILITY_RETURNED_NAN, Integer.valueOf(argument));
    }

    public double logProbability(int x) {
        return FastMath.log(probability(x));
    }
}
