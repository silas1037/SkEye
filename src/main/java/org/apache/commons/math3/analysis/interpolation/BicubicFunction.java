package org.apache.commons.math3.analysis.interpolation;

import java.lang.reflect.Array;
import org.apache.commons.math3.analysis.BivariateFunction;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.util.MathArrays;

/* compiled from: BicubicInterpolatingFunction */
class BicubicFunction implements BivariateFunction {

    /* renamed from: N */
    private static final short f127N = 4;

    /* renamed from: a */
    private final double[][] f128a = ((double[][]) Array.newInstance(Double.TYPE, 4, 4));

    BicubicFunction(double[] coeff) {
        for (int j = 0; j < 4; j++) {
            double[] aJ = this.f128a[j];
            for (int i = 0; i < 4; i++) {
                aJ[i] = coeff[(i * 4) + j];
            }
        }
    }

    @Override // org.apache.commons.math3.analysis.BivariateFunction
    public double value(double x, double y) {
        if (x < 0.0d || x > 1.0d) {
            throw new OutOfRangeException(Double.valueOf(x), 0, 1);
        } else if (y < 0.0d || y > 1.0d) {
            throw new OutOfRangeException(Double.valueOf(y), 0, 1);
        } else {
            double x2 = x * x;
            double y2 = y * y;
            return apply(new double[]{1.0d, x, x2, x2 * x}, new double[]{1.0d, y, y2, y2 * y}, this.f128a);
        }
    }

    private double apply(double[] pX, double[] pY, double[][] coeff) {
        double result = 0.0d;
        for (int i = 0; i < 4; i++) {
            result += pX[i] * MathArrays.linearCombination(coeff[i], pY);
        }
        return result;
    }
}
