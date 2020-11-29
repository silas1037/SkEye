package org.apache.commons.math3.analysis.interpolation;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.MathUtils;

public class UnivariatePeriodicInterpolator implements UnivariateInterpolator {
    public static final int DEFAULT_EXTEND = 5;
    private final int extend;
    private final UnivariateInterpolator interpolator;
    private final double period;

    public UnivariatePeriodicInterpolator(UnivariateInterpolator interpolator2, double period2, int extend2) {
        this.interpolator = interpolator2;
        this.period = period2;
        this.extend = extend2;
    }

    public UnivariatePeriodicInterpolator(UnivariateInterpolator interpolator2, double period2) {
        this(interpolator2, period2, 5);
    }

    @Override // org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator
    public UnivariateFunction interpolate(double[] xval, double[] yval) throws NumberIsTooSmallException, NonMonotonicSequenceException {
        if (xval.length < this.extend) {
            throw new NumberIsTooSmallException(Integer.valueOf(xval.length), Integer.valueOf(this.extend), true);
        }
        MathArrays.checkOrder(xval);
        final double offset = xval[0];
        int len = xval.length + (this.extend * 2);
        double[] x = new double[len];
        double[] y = new double[len];
        for (int i = 0; i < xval.length; i++) {
            int index = i + this.extend;
            x[index] = MathUtils.reduce(xval[i], this.period, offset);
            y[index] = yval[i];
        }
        for (int i2 = 0; i2 < this.extend; i2++) {
            int index2 = (xval.length - this.extend) + i2;
            x[i2] = MathUtils.reduce(xval[index2], this.period, offset) - this.period;
            y[i2] = yval[index2];
            int index3 = (len - this.extend) + i2;
            x[index3] = MathUtils.reduce(xval[i2], this.period, offset) + this.period;
            y[index3] = yval[i2];
        }
        MathArrays.sortInPlace(x, y);
        final UnivariateFunction f = this.interpolator.interpolate(x, y);
        return new UnivariateFunction() {
            /* class org.apache.commons.math3.analysis.interpolation.UnivariatePeriodicInterpolator.C02061 */

            @Override // org.apache.commons.math3.analysis.UnivariateFunction
            public double value(double x) throws MathIllegalArgumentException {
                return f.value(MathUtils.reduce(x, UnivariatePeriodicInterpolator.this.period, offset));
            }
        };
    }
}
