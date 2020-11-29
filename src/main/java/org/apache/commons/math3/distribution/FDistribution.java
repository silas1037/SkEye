package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.special.Beta;
import org.apache.commons.math3.util.FastMath;

public class FDistribution extends AbstractRealDistribution {
    public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1.0E-9d;
    private static final long serialVersionUID = -8516354193418641566L;
    private final double denominatorDegreesOfFreedom;
    private final double numeratorDegreesOfFreedom;
    private double numericalVariance;
    private boolean numericalVarianceIsCalculated;
    private final double solverAbsoluteAccuracy;

    public FDistribution(double numeratorDegreesOfFreedom2, double denominatorDegreesOfFreedom2) throws NotStrictlyPositiveException {
        this(numeratorDegreesOfFreedom2, denominatorDegreesOfFreedom2, 1.0E-9d);
    }

    public FDistribution(double numeratorDegreesOfFreedom2, double denominatorDegreesOfFreedom2, double inverseCumAccuracy) throws NotStrictlyPositiveException {
        this(new Well19937c(), numeratorDegreesOfFreedom2, denominatorDegreesOfFreedom2, inverseCumAccuracy);
    }

    public FDistribution(RandomGenerator rng, double numeratorDegreesOfFreedom2, double denominatorDegreesOfFreedom2) throws NotStrictlyPositiveException {
        this(rng, numeratorDegreesOfFreedom2, denominatorDegreesOfFreedom2, 1.0E-9d);
    }

    public FDistribution(RandomGenerator rng, double numeratorDegreesOfFreedom2, double denominatorDegreesOfFreedom2, double inverseCumAccuracy) throws NotStrictlyPositiveException {
        super(rng);
        this.numericalVariance = Double.NaN;
        this.numericalVarianceIsCalculated = false;
        if (numeratorDegreesOfFreedom2 <= 0.0d) {
            throw new NotStrictlyPositiveException(LocalizedFormats.DEGREES_OF_FREEDOM, Double.valueOf(numeratorDegreesOfFreedom2));
        } else if (denominatorDegreesOfFreedom2 <= 0.0d) {
            throw new NotStrictlyPositiveException(LocalizedFormats.DEGREES_OF_FREEDOM, Double.valueOf(denominatorDegreesOfFreedom2));
        } else {
            this.numeratorDegreesOfFreedom = numeratorDegreesOfFreedom2;
            this.denominatorDegreesOfFreedom = denominatorDegreesOfFreedom2;
            this.solverAbsoluteAccuracy = inverseCumAccuracy;
        }
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double density(double x) {
        return FastMath.exp(logDensity(x));
    }

    @Override // org.apache.commons.math3.distribution.AbstractRealDistribution
    public double logDensity(double x) {
        double nhalf = this.numeratorDegreesOfFreedom / 2.0d;
        double mhalf = this.denominatorDegreesOfFreedom / 2.0d;
        double logx = FastMath.log(x);
        double logn = FastMath.log(this.numeratorDegreesOfFreedom);
        double logm = FastMath.log(this.denominatorDegreesOfFreedom);
        double lognxm = FastMath.log((this.numeratorDegreesOfFreedom * x) + this.denominatorDegreesOfFreedom);
        return ((((((nhalf * logn) + (nhalf * logx)) - logx) + (mhalf * logm)) - (nhalf * lognxm)) - (mhalf * lognxm)) - Beta.logBeta(nhalf, mhalf);
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double cumulativeProbability(double x) {
        if (x <= 0.0d) {
            return 0.0d;
        }
        double n = this.numeratorDegreesOfFreedom;
        double m = this.denominatorDegreesOfFreedom;
        return Beta.regularizedBeta((n * x) / ((n * x) + m), 0.5d * n, 0.5d * m);
    }

    public double getNumeratorDegreesOfFreedom() {
        return this.numeratorDegreesOfFreedom;
    }

    public double getDenominatorDegreesOfFreedom() {
        return this.denominatorDegreesOfFreedom;
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.distribution.AbstractRealDistribution
    public double getSolverAbsoluteAccuracy() {
        return this.solverAbsoluteAccuracy;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double getNumericalMean() {
        double denominatorDF = getDenominatorDegreesOfFreedom();
        if (denominatorDF > 2.0d) {
            return denominatorDF / (denominatorDF - 2.0d);
        }
        return Double.NaN;
    }

    @Override // org.apache.commons.math3.distribution.RealDistribution
    public double getNumericalVariance() {
        if (!this.numericalVarianceIsCalculated) {
            this.numericalVariance = calculateNumericalVariance();
            this.numericalVarianceIsCalculated = true;
        }
        return this.numericalVariance;
    }

    /* access modifiers changed from: protected */
    public double calculateNumericalVariance() {
        double denominatorDF = getDenominatorDegreesOfFreedom();
        if (denominatorDF <= 4.0d) {
            return Double.NaN;
        }
        double numeratorDF = getNumeratorDegreesOfFreedom();
        double denomDFMinusTwo = denominatorDF - 2.0d;
        return (((denominatorDF * denominatorDF) * 2.0d) * ((numeratorDF + denominatorDF) - 2.0d)) / (((denomDFMinusTwo * denomDFMinusTwo) * numeratorDF) * (denominatorDF - 4.0d));
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
