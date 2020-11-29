package org.apache.commons.math3.analysis.interpolation;

import java.lang.reflect.Array;
import org.apache.commons.math3.analysis.BivariateFunction;
import org.apache.commons.math3.exception.OutOfRangeException;

/* compiled from: BicubicSplineInterpolatingFunction */
class BicubicSplineFunction implements BivariateFunction {

    /* renamed from: N */
    private static final short f129N = 4;

    /* renamed from: a */
    private final double[][] f130a;
    private final BivariateFunction partialDerivativeX;
    private final BivariateFunction partialDerivativeXX;
    private final BivariateFunction partialDerivativeXY;
    private final BivariateFunction partialDerivativeY;
    private final BivariateFunction partialDerivativeYY;

    BicubicSplineFunction(double[] coeff) {
        this(coeff, false);
    }

    BicubicSplineFunction(double[] coeff, boolean initializeDerivatives) {
        this.f130a = (double[][]) Array.newInstance(Double.TYPE, 4, 4);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                this.f130a[i][j] = coeff[(i * 4) + j];
            }
        }
        if (initializeDerivatives) {
            final double[][] aX = (double[][]) Array.newInstance(Double.TYPE, 4, 4);
            final double[][] aY = (double[][]) Array.newInstance(Double.TYPE, 4, 4);
            final double[][] aXX = (double[][]) Array.newInstance(Double.TYPE, 4, 4);
            final double[][] aYY = (double[][]) Array.newInstance(Double.TYPE, 4, 4);
            final double[][] aXY = (double[][]) Array.newInstance(Double.TYPE, 4, 4);
            for (int i2 = 0; i2 < 4; i2++) {
                for (int j2 = 0; j2 < 4; j2++) {
                    double c = this.f130a[i2][j2];
                    aX[i2][j2] = ((double) i2) * c;
                    aY[i2][j2] = ((double) j2) * c;
                    aXX[i2][j2] = ((double) (i2 - 1)) * aX[i2][j2];
                    aYY[i2][j2] = ((double) (j2 - 1)) * aY[i2][j2];
                    aXY[i2][j2] = ((double) j2) * aX[i2][j2];
                }
            }
            this.partialDerivativeX = new BivariateFunction() {
                /* class org.apache.commons.math3.analysis.interpolation.BicubicSplineFunction.C01991 */

                @Override // org.apache.commons.math3.analysis.BivariateFunction
                public double value(double x, double y) {
                    double y2 = y * y;
                    return BicubicSplineFunction.this.apply(new double[]{0.0d, 1.0d, x, x * x}, new double[]{1.0d, y, y2, y2 * y}, aX);
                }
            };
            this.partialDerivativeY = new BivariateFunction() {
                /* class org.apache.commons.math3.analysis.interpolation.BicubicSplineFunction.C02002 */

                @Override // org.apache.commons.math3.analysis.BivariateFunction
                public double value(double x, double y) {
                    double x2 = x * x;
                    return BicubicSplineFunction.this.apply(new double[]{1.0d, x, x2, x2 * x}, new double[]{0.0d, 1.0d, y, y * y}, aY);
                }
            };
            this.partialDerivativeXX = new BivariateFunction() {
                /* class org.apache.commons.math3.analysis.interpolation.BicubicSplineFunction.C02013 */

                @Override // org.apache.commons.math3.analysis.BivariateFunction
                public double value(double x, double y) {
                    double y2 = y * y;
                    return BicubicSplineFunction.this.apply(new double[]{0.0d, 0.0d, 1.0d, x}, new double[]{1.0d, y, y2, y2 * y}, aXX);
                }
            };
            this.partialDerivativeYY = new BivariateFunction() {
                /* class org.apache.commons.math3.analysis.interpolation.BicubicSplineFunction.C02024 */

                @Override // org.apache.commons.math3.analysis.BivariateFunction
                public double value(double x, double y) {
                    double x2 = x * x;
                    return BicubicSplineFunction.this.apply(new double[]{1.0d, x, x2, x2 * x}, new double[]{0.0d, 0.0d, 1.0d, y}, aYY);
                }
            };
            this.partialDerivativeXY = new BivariateFunction() {
                /* class org.apache.commons.math3.analysis.interpolation.BicubicSplineFunction.C02035 */

                @Override // org.apache.commons.math3.analysis.BivariateFunction
                public double value(double x, double y) {
                    return BicubicSplineFunction.this.apply(new double[]{0.0d, 1.0d, x, x * x}, new double[]{0.0d, 1.0d, y, y * y}, aXY);
                }
            };
            return;
        }
        this.partialDerivativeX = null;
        this.partialDerivativeY = null;
        this.partialDerivativeXX = null;
        this.partialDerivativeYY = null;
        this.partialDerivativeXY = null;
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
            return apply(new double[]{1.0d, x, x2, x2 * x}, new double[]{1.0d, y, y2, y2 * y}, this.f130a);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private double apply(double[] pX, double[] pY, double[][] coeff) {
        double result = 0.0d;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result += coeff[i][j] * pX[i] * pY[j];
            }
        }
        return result;
    }

    public BivariateFunction partialDerivativeX() {
        return this.partialDerivativeX;
    }

    public BivariateFunction partialDerivativeY() {
        return this.partialDerivativeY;
    }

    public BivariateFunction partialDerivativeXX() {
        return this.partialDerivativeXX;
    }

    public BivariateFunction partialDerivativeYY() {
        return this.partialDerivativeYY;
    }

    public BivariateFunction partialDerivativeXY() {
        return this.partialDerivativeXY;
    }
}
