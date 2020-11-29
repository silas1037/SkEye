package org.apache.commons.math3.stat.descriptive.moment;

import java.io.Serializable;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.util.MathUtils;

/* access modifiers changed from: package-private */
public class FourthMoment extends ThirdMoment implements Serializable {
    private static final long serialVersionUID = 4763990447117157611L;

    /* renamed from: m4 */
    private double f389m4;

    FourthMoment() {
        this.f389m4 = Double.NaN;
    }

    FourthMoment(FourthMoment original) throws NullArgumentException {
        copy(original, this);
    }

    @Override // org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.moment.SecondMoment, org.apache.commons.math3.stat.descriptive.moment.ThirdMoment, org.apache.commons.math3.stat.descriptive.moment.FirstMoment
    public void increment(double d) {
        if (this.f388n < 1) {
            this.f389m4 = 0.0d;
            this.f391m3 = 0.0d;
            this.f390m2 = 0.0d;
            this.f387m1 = 0.0d;
        }
        double prevM3 = this.f391m3;
        double prevM2 = this.f390m2;
        super.increment(d);
        double n0 = (double) this.f388n;
        this.f389m4 = (this.f389m4 - ((4.0d * this.nDev) * prevM3)) + (6.0d * this.nDevSq * prevM2) + (((n0 * n0) - (3.0d * (n0 - 1.0d))) * this.nDevSq * this.nDevSq * (n0 - 1.0d) * n0);
    }

    @Override // org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.moment.SecondMoment, org.apache.commons.math3.stat.descriptive.moment.ThirdMoment, org.apache.commons.math3.stat.descriptive.moment.FirstMoment
    public double getResult() {
        return this.f389m4;
    }

    @Override // org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.moment.SecondMoment, org.apache.commons.math3.stat.descriptive.moment.ThirdMoment, org.apache.commons.math3.stat.descriptive.moment.FirstMoment
    public void clear() {
        super.clear();
        this.f389m4 = Double.NaN;
    }

    @Override // org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.moment.SecondMoment, org.apache.commons.math3.stat.descriptive.moment.SecondMoment, org.apache.commons.math3.stat.descriptive.moment.SecondMoment, org.apache.commons.math3.stat.descriptive.moment.SecondMoment, org.apache.commons.math3.stat.descriptive.AbstractUnivariateStatistic, org.apache.commons.math3.stat.descriptive.UnivariateStatistic, org.apache.commons.math3.stat.descriptive.moment.ThirdMoment, org.apache.commons.math3.stat.descriptive.moment.ThirdMoment, org.apache.commons.math3.stat.descriptive.moment.ThirdMoment, org.apache.commons.math3.stat.descriptive.moment.ThirdMoment, org.apache.commons.math3.stat.descriptive.moment.ThirdMoment, org.apache.commons.math3.stat.descriptive.moment.FirstMoment, org.apache.commons.math3.stat.descriptive.moment.FirstMoment, org.apache.commons.math3.stat.descriptive.moment.FirstMoment
    public FourthMoment copy() {
        FourthMoment result = new FourthMoment();
        copy(this, result);
        return result;
    }

    public static void copy(FourthMoment source, FourthMoment dest) throws NullArgumentException {
        MathUtils.checkNotNull(source);
        MathUtils.checkNotNull(dest);
        ThirdMoment.copy((ThirdMoment) source, (ThirdMoment) dest);
        dest.f389m4 = source.f389m4;
    }
}
