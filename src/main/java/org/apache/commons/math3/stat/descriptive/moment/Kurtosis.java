package org.apache.commons.math3.stat.descriptive.moment;

import java.io.Serializable;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;

public class Kurtosis extends AbstractStorelessUnivariateStatistic implements Serializable {
    private static final long serialVersionUID = 2784465764798260919L;
    protected boolean incMoment;
    protected FourthMoment moment;

    public Kurtosis() {
        this.incMoment = true;
        this.moment = new FourthMoment();
    }

    public Kurtosis(FourthMoment m4) {
        this.incMoment = false;
        this.moment = m4;
    }

    public Kurtosis(Kurtosis original) throws NullArgumentException {
        copy(original, this);
    }

    @Override // org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic
    public void increment(double d) {
        if (this.incMoment) {
            this.moment.increment(d);
        }
    }

    @Override // org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic
    public double getResult() {
        if (this.moment.getN() <= 3) {
            return Double.NaN;
        }
        double variance = this.moment.f390m2 / ((double) (this.moment.f388n - 1));
        if (this.moment.f388n <= 3 || variance < 1.0E-19d) {
            return 0.0d;
        }
        double n = (double) this.moment.f388n;
        return ((((1.0d + n) * n) * this.moment.getResult()) - (((3.0d * this.moment.f390m2) * this.moment.f390m2) * (n - 1.0d))) / (((((n - 1.0d) * (n - 2.0d)) * (n - 3.0d)) * variance) * variance);
    }

    @Override // org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic
    public void clear() {
        if (this.incMoment) {
            this.moment.clear();
        }
    }

    @Override // org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic
    public long getN() {
        return this.moment.getN();
    }

    @Override // org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.AbstractUnivariateStatistic, org.apache.commons.math3.stat.descriptive.UnivariateStatistic, org.apache.commons.math3.util.MathArrays.Function
    public double evaluate(double[] values, int begin, int length) throws MathIllegalArgumentException {
        if (!test(values, begin, length) || length <= 3) {
            return Double.NaN;
        }
        Variance variance = new Variance();
        variance.incrementAll(values, begin, length);
        double mean = variance.moment.f387m1;
        double stdDev = FastMath.sqrt(variance.getResult());
        double accum3 = 0.0d;
        for (int i = begin; i < begin + length; i++) {
            accum3 += FastMath.pow(values[i] - mean, 4.0d);
        }
        double accum32 = accum3 / FastMath.pow(stdDev, 4.0d);
        double n0 = (double) length;
        double coefficientOne = ((1.0d + n0) * n0) / (((n0 - 1.0d) * (n0 - 2.0d)) * (n0 - 3.0d));
        return (coefficientOne * accum32) - ((3.0d * FastMath.pow(n0 - 1.0d, 2.0d)) / ((n0 - 2.0d) * (n0 - 3.0d)));
    }

    @Override // org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.AbstractUnivariateStatistic, org.apache.commons.math3.stat.descriptive.UnivariateStatistic
    public Kurtosis copy() {
        Kurtosis result = new Kurtosis();
        copy(this, result);
        return result;
    }

    public static void copy(Kurtosis source, Kurtosis dest) throws NullArgumentException {
        MathUtils.checkNotNull(source);
        MathUtils.checkNotNull(dest);
        dest.setData(source.getDataRef());
        dest.moment = source.moment.copy();
        dest.incMoment = source.incMoment;
    }
}
