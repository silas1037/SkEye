package org.apache.commons.math3.analysis.function;

import java.util.Arrays;
import org.apache.commons.math3.analysis.DifferentiableUnivariateFunction;
import org.apache.commons.math3.analysis.FunctionUtils;
import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Precision;

public class Gaussian implements UnivariateDifferentiableFunction, DifferentiableUnivariateFunction {
    private final double i2s2;

    /* renamed from: is */
    private final double f113is;
    private final double mean;
    private final double norm;

    public Gaussian(double norm2, double mean2, double sigma) throws NotStrictlyPositiveException {
        if (sigma <= 0.0d) {
            throw new NotStrictlyPositiveException(Double.valueOf(sigma));
        }
        this.norm = norm2;
        this.mean = mean2;
        this.f113is = 1.0d / sigma;
        this.i2s2 = 0.5d * this.f113is * this.f113is;
    }

    public Gaussian(double mean2, double sigma) throws NotStrictlyPositiveException {
        this(1.0d / (FastMath.sqrt(6.283185307179586d) * sigma), mean2, sigma);
    }

    public Gaussian() {
        this(0.0d, 1.0d);
    }

    @Override // org.apache.commons.math3.analysis.UnivariateFunction
    public double value(double x) {
        return value(x - this.mean, this.norm, this.i2s2);
    }

    @Override // org.apache.commons.math3.analysis.DifferentiableUnivariateFunction
    @Deprecated
    public UnivariateFunction derivative() {
        return FunctionUtils.toDifferentiableUnivariateFunction(this).derivative();
    }

    public static class Parametric implements ParametricUnivariateFunction {
        @Override // org.apache.commons.math3.analysis.ParametricUnivariateFunction
        public double value(double x, double... param) throws NullArgumentException, DimensionMismatchException, NotStrictlyPositiveException {
            validateParameters(param);
            return Gaussian.value(x - param[1], param[0], 1.0d / ((2.0d * param[2]) * param[2]));
        }

        @Override // org.apache.commons.math3.analysis.ParametricUnivariateFunction
        public double[] gradient(double x, double... param) throws NullArgumentException, DimensionMismatchException, NotStrictlyPositiveException {
            validateParameters(param);
            double norm = param[0];
            double diff = x - param[1];
            double sigma = param[2];
            double i2s2 = 1.0d / ((2.0d * sigma) * sigma);
            double n = Gaussian.value(diff, 1.0d, i2s2);
            double m = norm * n * 2.0d * i2s2 * diff;
            return new double[]{n, m, (m * diff) / sigma};
        }

        private void validateParameters(double[] param) throws NullArgumentException, DimensionMismatchException, NotStrictlyPositiveException {
            if (param == null) {
                throw new NullArgumentException();
            } else if (param.length != 3) {
                throw new DimensionMismatchException(param.length, 3);
            } else if (param[2] <= 0.0d) {
                throw new NotStrictlyPositiveException(Double.valueOf(param[2]));
            }
        }
    }

    /* access modifiers changed from: private */
    public static double value(double xMinusMean, double norm2, double i2s22) {
        return FastMath.exp((-xMinusMean) * xMinusMean * i2s22) * norm2;
    }

    @Override // org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction
    public DerivativeStructure value(DerivativeStructure t) throws DimensionMismatchException {
        double u = this.f113is * (t.getValue() - this.mean);
        double[] f = new double[(t.getOrder() + 1)];
        double[] p = new double[f.length];
        p[0] = 1.0d;
        double u2 = u * u;
        double coeff = this.norm * FastMath.exp(-0.5d * u2);
        if (coeff <= Precision.SAFE_MIN) {
            Arrays.fill(f, 0.0d);
        } else {
            f[0] = coeff;
            for (int n = 1; n < f.length; n++) {
                double v = 0.0d;
                p[n] = -p[n - 1];
                for (int k = n; k >= 0; k -= 2) {
                    v = (v * u2) + p[k];
                    if (k > 2) {
                        p[k - 2] = (((double) (k - 1)) * p[k - 1]) - p[k - 3];
                    } else if (k == 2) {
                        p[0] = p[1];
                    }
                }
                if ((n & 1) == 1) {
                    v *= u;
                }
                coeff *= this.f113is;
                f[n] = coeff * v;
            }
        }
        return t.compose(f);
    }
}
