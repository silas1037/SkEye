package org.apache.commons.math3.stat.descriptive.moment;

import java.io.Serializable;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic;
import org.apache.commons.math3.util.MathUtils;

/* access modifiers changed from: package-private */
public class FirstMoment extends AbstractStorelessUnivariateStatistic implements Serializable {
    private static final long serialVersionUID = 6112755307178490473L;
    protected double dev;

    /* renamed from: m1 */
    protected double f387m1;

    /* renamed from: n */
    protected long f388n;
    protected double nDev;

    FirstMoment() {
        this.f388n = 0;
        this.f387m1 = Double.NaN;
        this.dev = Double.NaN;
        this.nDev = Double.NaN;
    }

    FirstMoment(FirstMoment original) throws NullArgumentException {
        copy(original, this);
    }

    @Override // org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic
    public void increment(double d) {
        if (this.f388n == 0) {
            this.f387m1 = 0.0d;
        }
        this.f388n++;
        double n0 = (double) this.f388n;
        this.dev = d - this.f387m1;
        this.nDev = this.dev / n0;
        this.f387m1 += this.nDev;
    }

    @Override // org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic
    public void clear() {
        this.f387m1 = Double.NaN;
        this.f388n = 0;
        this.dev = Double.NaN;
        this.nDev = Double.NaN;
    }

    @Override // org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic
    public double getResult() {
        return this.f387m1;
    }

    @Override // org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic
    public long getN() {
        return this.f388n;
    }

    @Override // org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.AbstractUnivariateStatistic, org.apache.commons.math3.stat.descriptive.UnivariateStatistic
    public FirstMoment copy() {
        FirstMoment result = new FirstMoment();
        copy(this, result);
        return result;
    }

    public static void copy(FirstMoment source, FirstMoment dest) throws NullArgumentException {
        MathUtils.checkNotNull(source);
        MathUtils.checkNotNull(dest);
        dest.setData(source.getDataRef());
        dest.f388n = source.f388n;
        dest.f387m1 = source.f387m1;
        dest.dev = source.dev;
        dest.nDev = source.nDev;
    }
}
