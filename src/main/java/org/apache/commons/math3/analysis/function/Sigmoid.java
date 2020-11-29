package org.apache.commons.math3.analysis.function;

import java.util.Arrays;
import org.apache.commons.math3.analysis.DifferentiableUnivariateFunction;
import org.apache.commons.math3.analysis.FunctionUtils;
import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.util.FastMath;

public class Sigmoid implements UnivariateDifferentiableFunction, DifferentiableUnivariateFunction {

    /* renamed from: hi */
    private final double f122hi;

    /* renamed from: lo */
    private final double f123lo;

    public Sigmoid() {
        this(0.0d, 1.0d);
    }

    public Sigmoid(double lo, double hi) {
        this.f123lo = lo;
        this.f122hi = hi;
    }

    @Override // org.apache.commons.math3.analysis.DifferentiableUnivariateFunction
    @Deprecated
    public UnivariateFunction derivative() {
        return FunctionUtils.toDifferentiableUnivariateFunction(this).derivative();
    }

    @Override // org.apache.commons.math3.analysis.UnivariateFunction
    public double value(double x) {
        return value(x, this.f123lo, this.f122hi);
    }

    public static class Parametric implements ParametricUnivariateFunction {
        @Override // org.apache.commons.math3.analysis.ParametricUnivariateFunction
        public double value(double x, double... param) throws NullArgumentException, DimensionMismatchException {
            validateParameters(param);
            return Sigmoid.value(x, param[0], param[1]);
        }

        @Override // org.apache.commons.math3.analysis.ParametricUnivariateFunction
        public double[] gradient(double x, double... param) throws NullArgumentException, DimensionMismatchException {
            validateParameters(param);
            double invExp1 = 1.0d / (FastMath.exp(-x) + 1.0d);
            return new double[]{1.0d - invExp1, invExp1};
        }

        private void validateParameters(double[] param) throws NullArgumentException, DimensionMismatchException {
            if (param == null) {
                throw new NullArgumentException();
            } else if (param.length != 2) {
                throw new DimensionMismatchException(param.length, 2);
            }
        }
    }

    /* access modifiers changed from: private */
    public static double value(double x, double lo, double hi) {
        return ((hi - lo) / (1.0d + FastMath.exp(-x))) + lo;
    }

    @Override // org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction
    public DerivativeStructure value(DerivativeStructure t) throws DimensionMismatchException {
        double[] f = new double[(t.getOrder() + 1)];
        double exp = FastMath.exp(-t.getValue());
        if (Double.isInfinite(exp)) {
            f[0] = this.f123lo;
            Arrays.fill(f, 1, f.length, 0.0d);
        } else {
            double[] p = new double[f.length];
            double inv = 1.0d / (1.0d + exp);
            double coeff = this.f122hi - this.f123lo;
            for (int n = 0; n < f.length; n++) {
                double v = 0.0d;
                p[n] = 1.0d;
                for (int k = n; k >= 0; k--) {
                    v = (v * exp) + p[k];
                    if (k > 1) {
                        p[k - 1] = (((double) ((n - k) + 2)) * p[k - 2]) - (((double) (k - 1)) * p[k - 1]);
                    } else {
                        p[0] = 0.0d;
                    }
                }
                coeff *= inv;
                f[n] = coeff * v;
            }
            f[0] = f[0] + this.f123lo;
        }
        return t.compose(f);
    }
}
