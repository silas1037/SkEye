package org.apache.commons.math3.analysis.function;

import org.apache.commons.math3.analysis.DifferentiableUnivariateFunction;
import org.apache.commons.math3.analysis.FunctionUtils;
import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.util.FastMath;

public class HarmonicOscillator implements UnivariateDifferentiableFunction, DifferentiableUnivariateFunction {
    private final double amplitude;
    private final double omega;
    private final double phase;

    public HarmonicOscillator(double amplitude2, double omega2, double phase2) {
        this.amplitude = amplitude2;
        this.omega = omega2;
        this.phase = phase2;
    }

    @Override // org.apache.commons.math3.analysis.UnivariateFunction
    public double value(double x) {
        return value((this.omega * x) + this.phase, this.amplitude);
    }

    @Override // org.apache.commons.math3.analysis.DifferentiableUnivariateFunction
    @Deprecated
    public UnivariateFunction derivative() {
        return FunctionUtils.toDifferentiableUnivariateFunction(this).derivative();
    }

    public static class Parametric implements ParametricUnivariateFunction {
        @Override // org.apache.commons.math3.analysis.ParametricUnivariateFunction
        public double value(double x, double... param) throws NullArgumentException, DimensionMismatchException {
            validateParameters(param);
            return HarmonicOscillator.value((param[1] * x) + param[2], param[0]);
        }

        @Override // org.apache.commons.math3.analysis.ParametricUnivariateFunction
        public double[] gradient(double x, double... param) throws NullArgumentException, DimensionMismatchException {
            validateParameters(param);
            double amplitude = param[0];
            double xTimesOmegaPlusPhase = (param[1] * x) + param[2];
            double a = HarmonicOscillator.value(xTimesOmegaPlusPhase, 1.0d);
            double p = (-amplitude) * FastMath.sin(xTimesOmegaPlusPhase);
            return new double[]{a, p * x, p};
        }

        private void validateParameters(double[] param) throws NullArgumentException, DimensionMismatchException {
            if (param == null) {
                throw new NullArgumentException();
            } else if (param.length != 3) {
                throw new DimensionMismatchException(param.length, 3);
            }
        }
    }

    /* access modifiers changed from: private */
    public static double value(double xTimesOmegaPlusPhase, double amplitude2) {
        return FastMath.cos(xTimesOmegaPlusPhase) * amplitude2;
    }

    @Override // org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction
    public DerivativeStructure value(DerivativeStructure t) throws DimensionMismatchException {
        double x = t.getValue();
        double[] f = new double[(t.getOrder() + 1)];
        double alpha = (this.omega * x) + this.phase;
        f[0] = this.amplitude * FastMath.cos(alpha);
        if (f.length > 1) {
            f[1] = (-this.amplitude) * this.omega * FastMath.sin(alpha);
            double mo2 = (-this.omega) * this.omega;
            for (int i = 2; i < f.length; i++) {
                f[i] = f[i - 2] * mo2;
            }
        }
        return t.compose(f);
    }
}
