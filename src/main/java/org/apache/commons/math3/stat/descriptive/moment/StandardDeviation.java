package org.apache.commons.math3.stat.descriptive.moment;

import java.io.Serializable;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;

public class StandardDeviation extends AbstractStorelessUnivariateStatistic implements Serializable {
    private static final long serialVersionUID = 5728716329662425188L;
    private Variance variance;

    public StandardDeviation() {
        this.variance = null;
        this.variance = new Variance();
    }

    public StandardDeviation(SecondMoment m2) {
        this.variance = null;
        this.variance = new Variance(m2);
    }

    public StandardDeviation(StandardDeviation original) throws NullArgumentException {
        this.variance = null;
        copy(original, this);
    }

    public StandardDeviation(boolean isBiasCorrected) {
        this.variance = null;
        this.variance = new Variance(isBiasCorrected);
    }

    public StandardDeviation(boolean isBiasCorrected, SecondMoment m2) {
        this.variance = null;
        this.variance = new Variance(isBiasCorrected, m2);
    }

    @Override // org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic
    public void increment(double d) {
        this.variance.increment(d);
    }

    @Override // org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic
    public long getN() {
        return this.variance.getN();
    }

    @Override // org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic
    public double getResult() {
        return FastMath.sqrt(this.variance.getResult());
    }

    @Override // org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic
    public void clear() {
        this.variance.clear();
    }

    @Override // org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.AbstractUnivariateStatistic, org.apache.commons.math3.stat.descriptive.UnivariateStatistic, org.apache.commons.math3.util.MathArrays.Function
    public double evaluate(double[] values) throws MathIllegalArgumentException {
        return FastMath.sqrt(this.variance.evaluate(values));
    }

    @Override // org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.AbstractUnivariateStatistic, org.apache.commons.math3.stat.descriptive.UnivariateStatistic, org.apache.commons.math3.util.MathArrays.Function
    public double evaluate(double[] values, int begin, int length) throws MathIllegalArgumentException {
        return FastMath.sqrt(this.variance.evaluate(values, begin, length));
    }

    public double evaluate(double[] values, double mean, int begin, int length) throws MathIllegalArgumentException {
        return FastMath.sqrt(this.variance.evaluate(values, mean, begin, length));
    }

    public double evaluate(double[] values, double mean) throws MathIllegalArgumentException {
        return FastMath.sqrt(this.variance.evaluate(values, mean));
    }

    public boolean isBiasCorrected() {
        return this.variance.isBiasCorrected();
    }

    public void setBiasCorrected(boolean isBiasCorrected) {
        this.variance.setBiasCorrected(isBiasCorrected);
    }

    @Override // org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.AbstractUnivariateStatistic, org.apache.commons.math3.stat.descriptive.UnivariateStatistic
    public StandardDeviation copy() {
        StandardDeviation result = new StandardDeviation();
        copy(this, result);
        return result;
    }

    public static void copy(StandardDeviation source, StandardDeviation dest) throws NullArgumentException {
        MathUtils.checkNotNull(source);
        MathUtils.checkNotNull(dest);
        dest.setData(source.getDataRef());
        dest.variance = source.variance.copy();
    }
}
