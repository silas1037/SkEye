package org.apache.commons.math3.analysis.function;

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

public class Logistic implements UnivariateDifferentiableFunction, DifferentiableUnivariateFunction {

    /* renamed from: a */
    private final double f114a;

    /* renamed from: b */
    private final double f115b;

    /* renamed from: k */
    private final double f116k;

    /* renamed from: m */
    private final double f117m;
    private final double oneOverN;

    /* renamed from: q */
    private final double f118q;

    public Logistic(double k, double m, double b, double q, double a, double n) throws NotStrictlyPositiveException {
        if (n <= 0.0d) {
            throw new NotStrictlyPositiveException(Double.valueOf(n));
        }
        this.f116k = k;
        this.f117m = m;
        this.f115b = b;
        this.f118q = q;
        this.f114a = a;
        this.oneOverN = 1.0d / n;
    }

    @Override // org.apache.commons.math3.analysis.UnivariateFunction
    public double value(double x) {
        return value(this.f117m - x, this.f116k, this.f115b, this.f118q, this.f114a, this.oneOverN);
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
            return Logistic.value(param[1] - x, param[0], param[2], param[3], param[4], 1.0d / param[5]);
        }

        @Override // org.apache.commons.math3.analysis.ParametricUnivariateFunction
        public double[] gradient(double x, double... param) throws NullArgumentException, DimensionMismatchException, NotStrictlyPositiveException {
            validateParameters(param);
            double b = param[2];
            double q = param[3];
            double mMinusX = param[1] - x;
            double oneOverN = 1.0d / param[5];
            double exp = FastMath.exp(b * mMinusX);
            double qExp = q * exp;
            double qExp1 = qExp + 1.0d;
            double factor1 = ((param[0] - param[4]) * oneOverN) / FastMath.pow(qExp1, oneOverN);
            double factor2 = (-factor1) / qExp1;
            return new double[]{Logistic.value(mMinusX, 1.0d, b, q, 0.0d, oneOverN), factor2 * b * qExp, factor2 * mMinusX * qExp, factor2 * exp, Logistic.value(mMinusX, 0.0d, b, q, 1.0d, oneOverN), FastMath.log(qExp1) * factor1 * oneOverN};
        }

        private void validateParameters(double[] param) throws NullArgumentException, DimensionMismatchException, NotStrictlyPositiveException {
            if (param == null) {
                throw new NullArgumentException();
            } else if (param.length != 6) {
                throw new DimensionMismatchException(param.length, 6);
            } else if (param[5] <= 0.0d) {
                throw new NotStrictlyPositiveException(Double.valueOf(param[5]));
            }
        }
    }

    /* access modifiers changed from: private */
    public static double value(double mMinusX, double k, double b, double q, double a, double oneOverN2) {
        return ((k - a) / FastMath.pow(1.0d + (FastMath.exp(b * mMinusX) * q), oneOverN2)) + a;
    }

    @Override // org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction
    public DerivativeStructure value(DerivativeStructure t) {
        return t.negate().add(this.f117m).multiply(this.f115b).exp().multiply(this.f118q).add(1.0d).pow(this.oneOverN).reciprocal().multiply(this.f116k - this.f114a).add(this.f114a);
    }
}
