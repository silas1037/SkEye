package org.apache.commons.math3.analysis.function;

import org.apache.commons.math3.analysis.DifferentiableUnivariateFunction;
import org.apache.commons.math3.analysis.FunctionUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.util.FastMath;

public class Sinc implements UnivariateDifferentiableFunction, DifferentiableUnivariateFunction {
    private static final double SHORTCUT = 0.006d;
    private final boolean normalized;

    public Sinc() {
        this(false);
    }

    public Sinc(boolean normalized2) {
        this.normalized = normalized2;
    }

    @Override // org.apache.commons.math3.analysis.UnivariateFunction
    public double value(double x) {
        double scaledX;
        if (this.normalized) {
            scaledX = 3.141592653589793d * x;
        } else {
            scaledX = x;
        }
        if (FastMath.abs(scaledX) > SHORTCUT) {
            return FastMath.sin(scaledX) / scaledX;
        }
        double scaledX2 = scaledX * scaledX;
        return (((scaledX2 - 20.0d) * scaledX2) + 120.0d) / 120.0d;
    }

    @Override // org.apache.commons.math3.analysis.DifferentiableUnivariateFunction
    @Deprecated
    public UnivariateFunction derivative() {
        return FunctionUtils.toDifferentiableUnivariateFunction(this).derivative();
    }

    @Override // org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction
    public DerivativeStructure value(DerivativeStructure t) throws DimensionMismatchException {
        int kStart;
        double scaledX = (this.normalized ? 3.141592653589793d : 1.0d) * t.getValue();
        double scaledX2 = scaledX * scaledX;
        double[] f = new double[(t.getOrder() + 1)];
        if (FastMath.abs(scaledX) <= SHORTCUT) {
            for (int i = 0; i < f.length; i++) {
                int k = i / 2;
                if ((i & 1) == 0) {
                    f[i] = ((double) ((k & 1) == 0 ? 1 : -1)) * ((1.0d / ((double) (i + 1))) - (((1.0d / ((double) ((i * 2) + 6))) - (scaledX2 / ((double) ((i * 24) + 120)))) * scaledX2));
                } else {
                    f[i] = ((k & 1) == 0 ? -scaledX : scaledX) * ((1.0d / ((double) (i + 2))) - (((1.0d / ((double) ((i * 6) + 24))) - (scaledX2 / ((double) ((i * 120) + 720)))) * scaledX2));
                }
            }
        } else {
            double inv = 1.0d / scaledX;
            double cos = FastMath.cos(scaledX);
            double sin = FastMath.sin(scaledX);
            f[0] = inv * sin;
            double[] sc = new double[f.length];
            sc[0] = 1.0d;
            double coeff = inv;
            for (int n = 1; n < f.length; n++) {
                double s = 0.0d;
                double c = 0.0d;
                if ((n & 1) == 0) {
                    sc[n] = 0.0d;
                    kStart = n;
                } else {
                    sc[n] = sc[n - 1];
                    c = sc[n];
                    kStart = n - 1;
                }
                for (int k2 = kStart; k2 > 1; k2 -= 2) {
                    sc[k2] = (((double) (k2 - n)) * sc[k2]) - sc[k2 - 1];
                    s = (s * scaledX2) + sc[k2];
                    sc[k2 - 1] = (((double) ((k2 - 1) - n)) * sc[k2 - 1]) + sc[k2 - 2];
                    c = (c * scaledX2) + sc[k2 - 1];
                }
                sc[0] = sc[0] * ((double) (-n));
                coeff *= inv;
                f[n] = ((((s * scaledX2) + sc[0]) * sin) + (c * scaledX * cos)) * coeff;
            }
        }
        if (this.normalized) {
            double scale = 3.141592653589793d;
            for (int i2 = 1; i2 < f.length; i2++) {
                f[i2] = f[i2] * scale;
                scale *= 3.141592653589793d;
            }
        }
        return t.compose(f);
    }
}
