package org.apache.commons.math3.stat.descriptive.moment;

import java.io.Serializable;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.util.MathUtils;

/* access modifiers changed from: package-private */
public class ThirdMoment extends SecondMoment implements Serializable {
    private static final long serialVersionUID = -7818711964045118679L;

    /* renamed from: m3 */
    protected double f391m3;
    protected double nDevSq;

    ThirdMoment() {
        this.f391m3 = Double.NaN;
        this.nDevSq = Double.NaN;
    }

    ThirdMoment(ThirdMoment original) throws NullArgumentException {
        copy(original, this);
    }

    @Override // org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.moment.SecondMoment, org.apache.commons.math3.stat.descriptive.moment.FirstMoment
    public void increment(double d) {
        if (this.f388n < 1) {
            this.f387m1 = 0.0d;
            this.f390m2 = 0.0d;
            this.f391m3 = 0.0d;
        }
        double prevM2 = this.f390m2;
        super.increment(d);
        this.nDevSq = this.nDev * this.nDev;
        double n0 = (double) this.f388n;
        this.f391m3 = (this.f391m3 - ((3.0d * this.nDev) * prevM2)) + ((n0 - 1.0d) * (n0 - 2.0d) * this.nDevSq * this.dev);
    }

    @Override // org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.moment.SecondMoment, org.apache.commons.math3.stat.descriptive.moment.FirstMoment
    public double getResult() {
        return this.f391m3;
    }

    @Override // org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.moment.SecondMoment, org.apache.commons.math3.stat.descriptive.moment.FirstMoment
    public void clear() {
        super.clear();
        this.f391m3 = Double.NaN;
        this.nDevSq = Double.NaN;
    }

    @Override // org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.moment.SecondMoment, org.apache.commons.math3.stat.descriptive.moment.SecondMoment, org.apache.commons.math3.stat.descriptive.moment.SecondMoment, org.apache.commons.math3.stat.descriptive.moment.SecondMoment, org.apache.commons.math3.stat.descriptive.AbstractUnivariateStatistic, org.apache.commons.math3.stat.descriptive.UnivariateStatistic, org.apache.commons.math3.stat.descriptive.moment.FirstMoment, org.apache.commons.math3.stat.descriptive.moment.FirstMoment, org.apache.commons.math3.stat.descriptive.moment.FirstMoment
    public ThirdMoment copy() {
        ThirdMoment result = new ThirdMoment();
        copy(this, result);
        return result;
    }

    public static void copy(ThirdMoment source, ThirdMoment dest) throws NullArgumentException {
        MathUtils.checkNotNull(source);
        MathUtils.checkNotNull(dest);
        SecondMoment.copy((SecondMoment) source, (SecondMoment) dest);
        dest.f391m3 = source.f391m3;
        dest.nDevSq = source.nDevSq;
    }
}
