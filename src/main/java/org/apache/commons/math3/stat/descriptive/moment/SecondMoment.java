package org.apache.commons.math3.stat.descriptive.moment;

import java.io.Serializable;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.util.MathUtils;

public class SecondMoment extends FirstMoment implements Serializable {
    private static final long serialVersionUID = 3942403127395076445L;

    /* renamed from: m2 */
    protected double f390m2;

    @Override // org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.moment.FirstMoment
    public /* bridge */ /* synthetic */ long getN() {
        return super.getN();
    }

    public SecondMoment() {
        this.f390m2 = Double.NaN;
    }

    public SecondMoment(SecondMoment original) throws NullArgumentException {
        super(original);
        this.f390m2 = original.f390m2;
    }

    @Override // org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.moment.FirstMoment
    public void increment(double d) {
        if (this.f388n < 1) {
            this.f390m2 = 0.0d;
            this.f387m1 = 0.0d;
        }
        super.increment(d);
        this.f390m2 += (((double) this.f388n) - 1.0d) * this.dev * this.nDev;
    }

    @Override // org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.moment.FirstMoment
    public void clear() {
        super.clear();
        this.f390m2 = Double.NaN;
    }

    @Override // org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.moment.FirstMoment
    public double getResult() {
        return this.f390m2;
    }

    @Override // org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.AbstractUnivariateStatistic, org.apache.commons.math3.stat.descriptive.UnivariateStatistic, org.apache.commons.math3.stat.descriptive.moment.FirstMoment, org.apache.commons.math3.stat.descriptive.moment.FirstMoment, org.apache.commons.math3.stat.descriptive.moment.FirstMoment
    public SecondMoment copy() {
        SecondMoment result = new SecondMoment();
        copy(this, result);
        return result;
    }

    public static void copy(SecondMoment source, SecondMoment dest) throws NullArgumentException {
        MathUtils.checkNotNull(source);
        MathUtils.checkNotNull(dest);
        FirstMoment.copy(source, dest);
        dest.f390m2 = source.f390m2;
    }
}
