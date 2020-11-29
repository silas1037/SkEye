package org.apache.commons.math3.stat.descriptive.moment;

import java.io.Serializable;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;

public class Skewness extends AbstractStorelessUnivariateStatistic implements Serializable {
    private static final long serialVersionUID = 7101857578996691352L;
    protected boolean incMoment;
    protected ThirdMoment moment;

    public Skewness() {
        this.moment = null;
        this.incMoment = true;
        this.moment = new ThirdMoment();
    }

    public Skewness(ThirdMoment m3) {
        this.moment = null;
        this.incMoment = false;
        this.moment = m3;
    }

    public Skewness(Skewness original) throws NullArgumentException {
        this.moment = null;
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
        if (this.moment.f388n < 3) {
            return Double.NaN;
        }
        double variance = this.moment.f390m2 / ((double) (this.moment.f388n - 1));
        if (variance < 1.0E-19d) {
            return 0.0d;
        }
        double n0 = (double) this.moment.getN();
        return (this.moment.f391m3 * n0) / ((((n0 - 1.0d) * (n0 - 2.0d)) * FastMath.sqrt(variance)) * variance);
    }

    @Override // org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic
    public long getN() {
        return this.moment.getN();
    }

    @Override // org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic
    public void clear() {
        if (this.incMoment) {
            this.moment.clear();
        }
    }

    @Override // org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.AbstractUnivariateStatistic, org.apache.commons.math3.stat.descriptive.UnivariateStatistic, org.apache.commons.math3.util.MathArrays.Function
    public double evaluate(double[] values, int begin, int length) throws MathIllegalArgumentException {
        if (!test(values, begin, length) || length <= 2) {
            return Double.NaN;
        }
        double m = new Mean().evaluate(values, begin, length);
        double accum = 0.0d;
        double accum2 = 0.0d;
        for (int i = begin; i < begin + length; i++) {
            double d = values[i] - m;
            accum += d * d;
            accum2 += d;
        }
        double variance = (accum - ((accum2 * accum2) / ((double) length))) / ((double) (length - 1));
        double accum3 = 0.0d;
        for (int i2 = begin; i2 < begin + length; i2++) {
            double d2 = values[i2] - m;
            accum3 += d2 * d2 * d2;
        }
        double n0 = (double) length;
        return (n0 / ((n0 - 1.0d) * (n0 - 2.0d))) * (accum3 / (FastMath.sqrt(variance) * variance));
    }

    @Override // org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.AbstractUnivariateStatistic, org.apache.commons.math3.stat.descriptive.UnivariateStatistic
    public Skewness copy() {
        Skewness result = new Skewness();
        copy(this, result);
        return result;
    }

    public static void copy(Skewness source, Skewness dest) throws NullArgumentException {
        MathUtils.checkNotNull(source);
        MathUtils.checkNotNull(dest);
        dest.setData(source.getDataRef());
        dest.moment = new ThirdMoment(source.moment.copy());
        dest.incMoment = source.incMoment;
    }
}
