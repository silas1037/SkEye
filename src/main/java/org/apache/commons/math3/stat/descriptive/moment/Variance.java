package org.apache.commons.math3.stat.descriptive.moment;

import java.io.Serializable;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic;
import org.apache.commons.math3.stat.descriptive.WeightedEvaluation;
import org.apache.commons.math3.util.MathUtils;

public class Variance extends AbstractStorelessUnivariateStatistic implements Serializable, WeightedEvaluation {
    private static final long serialVersionUID = -9111962718267217978L;
    protected boolean incMoment;
    private boolean isBiasCorrected;
    protected SecondMoment moment;

    public Variance() {
        this.moment = null;
        this.incMoment = true;
        this.isBiasCorrected = true;
        this.moment = new SecondMoment();
    }

    public Variance(SecondMoment m2) {
        this.moment = null;
        this.incMoment = true;
        this.isBiasCorrected = true;
        this.incMoment = false;
        this.moment = m2;
    }

    public Variance(boolean isBiasCorrected2) {
        this.moment = null;
        this.incMoment = true;
        this.isBiasCorrected = true;
        this.moment = new SecondMoment();
        this.isBiasCorrected = isBiasCorrected2;
    }

    public Variance(boolean isBiasCorrected2, SecondMoment m2) {
        this.moment = null;
        this.incMoment = true;
        this.isBiasCorrected = true;
        this.incMoment = false;
        this.moment = m2;
        this.isBiasCorrected = isBiasCorrected2;
    }

    public Variance(Variance original) throws NullArgumentException {
        this.moment = null;
        this.incMoment = true;
        this.isBiasCorrected = true;
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
        if (this.moment.f388n == 0) {
            return Double.NaN;
        }
        if (this.moment.f388n == 1) {
            return 0.0d;
        }
        if (this.isBiasCorrected) {
            return this.moment.f390m2 / (((double) this.moment.f388n) - 1.0d);
        }
        return this.moment.f390m2 / ((double) this.moment.f388n);
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
    public double evaluate(double[] values) throws MathIllegalArgumentException {
        if (values != null) {
            return evaluate(values, 0, values.length);
        }
        throw new NullArgumentException(LocalizedFormats.INPUT_ARRAY, new Object[0]);
    }

    @Override // org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.AbstractUnivariateStatistic, org.apache.commons.math3.stat.descriptive.UnivariateStatistic, org.apache.commons.math3.util.MathArrays.Function
    public double evaluate(double[] values, int begin, int length) throws MathIllegalArgumentException {
        if (!test(values, begin, length)) {
            return Double.NaN;
        }
        clear();
        if (length == 1) {
            return 0.0d;
        }
        if (length > 1) {
            return evaluate(values, new Mean().evaluate(values, begin, length), begin, length);
        }
        return Double.NaN;
    }

    @Override // org.apache.commons.math3.stat.descriptive.WeightedEvaluation
    public double evaluate(double[] values, double[] weights, int begin, int length) throws MathIllegalArgumentException {
        if (!test(values, weights, begin, length)) {
            return Double.NaN;
        }
        clear();
        if (length == 1) {
            return 0.0d;
        }
        if (length > 1) {
            return evaluate(values, weights, new Mean().evaluate(values, weights, begin, length), begin, length);
        }
        return Double.NaN;
    }

    @Override // org.apache.commons.math3.stat.descriptive.WeightedEvaluation
    public double evaluate(double[] values, double[] weights) throws MathIllegalArgumentException {
        return evaluate(values, weights, 0, values.length);
    }

    public double evaluate(double[] values, double mean, int begin, int length) throws MathIllegalArgumentException {
        if (!test(values, begin, length)) {
            return Double.NaN;
        }
        if (length == 1) {
            return 0.0d;
        }
        if (length <= 1) {
            return Double.NaN;
        }
        double accum = 0.0d;
        double accum2 = 0.0d;
        for (int i = begin; i < begin + length; i++) {
            double dev = values[i] - mean;
            accum += dev * dev;
            accum2 += dev;
        }
        double len = (double) length;
        if (this.isBiasCorrected) {
            return (accum - ((accum2 * accum2) / len)) / (len - 1.0d);
        }
        return (accum - ((accum2 * accum2) / len)) / len;
    }

    public double evaluate(double[] values, double mean) throws MathIllegalArgumentException {
        return evaluate(values, mean, 0, values.length);
    }

    public double evaluate(double[] values, double[] weights, double mean, int begin, int length) throws MathIllegalArgumentException {
        if (!test(values, weights, begin, length)) {
            return Double.NaN;
        }
        if (length == 1) {
            return 0.0d;
        }
        if (length <= 1) {
            return Double.NaN;
        }
        double accum = 0.0d;
        double accum2 = 0.0d;
        for (int i = begin; i < begin + length; i++) {
            double dev = values[i] - mean;
            accum += weights[i] * dev * dev;
            accum2 += weights[i] * dev;
        }
        double sumWts = 0.0d;
        for (int i2 = begin; i2 < begin + length; i2++) {
            sumWts += weights[i2];
        }
        if (this.isBiasCorrected) {
            return (accum - ((accum2 * accum2) / sumWts)) / (sumWts - 1.0d);
        }
        return (accum - ((accum2 * accum2) / sumWts)) / sumWts;
    }

    public double evaluate(double[] values, double[] weights, double mean) throws MathIllegalArgumentException {
        return evaluate(values, weights, mean, 0, values.length);
    }

    public boolean isBiasCorrected() {
        return this.isBiasCorrected;
    }

    public void setBiasCorrected(boolean biasCorrected) {
        this.isBiasCorrected = biasCorrected;
    }

    @Override // org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic, org.apache.commons.math3.stat.descriptive.AbstractUnivariateStatistic, org.apache.commons.math3.stat.descriptive.UnivariateStatistic
    public Variance copy() {
        Variance result = new Variance();
        copy(this, result);
        return result;
    }

    public static void copy(Variance source, Variance dest) throws NullArgumentException {
        MathUtils.checkNotNull(source);
        MathUtils.checkNotNull(dest);
        dest.setData(source.getDataRef());
        dest.moment = source.moment.copy();
        dest.isBiasCorrected = source.isBiasCorrected;
        dest.incMoment = source.incMoment;
    }
}
