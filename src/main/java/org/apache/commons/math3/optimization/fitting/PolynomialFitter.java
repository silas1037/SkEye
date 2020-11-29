package org.apache.commons.math3.optimization.fitting;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.optimization.DifferentiableMultivariateVectorOptimizer;

@Deprecated
public class PolynomialFitter extends CurveFitter<PolynomialFunction.Parametric> {
    @Deprecated
    private final int degree;

    @Deprecated
    public PolynomialFitter(int degree2, DifferentiableMultivariateVectorOptimizer optimizer) {
        super(optimizer);
        this.degree = degree2;
    }

    public PolynomialFitter(DifferentiableMultivariateVectorOptimizer optimizer) {
        super(optimizer);
        this.degree = -1;
    }

    @Deprecated
    public double[] fit() {
        return fit(new PolynomialFunction.Parametric(), new double[(this.degree + 1)]);
    }

    public double[] fit(int maxEval, double[] guess) {
        return fit(maxEval, new PolynomialFunction.Parametric(), guess);
    }

    public double[] fit(double[] guess) {
        return fit(new PolynomialFunction.Parametric(), guess);
    }
}
