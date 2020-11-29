package org.apache.commons.math3.analysis.solvers;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;

public abstract class AbstractPolynomialSolver extends BaseAbstractUnivariateSolver<PolynomialFunction> implements PolynomialSolver {
    private PolynomialFunction polynomialFunction;

    protected AbstractPolynomialSolver(double absoluteAccuracy) {
        super(absoluteAccuracy);
    }

    protected AbstractPolynomialSolver(double relativeAccuracy, double absoluteAccuracy) {
        super(relativeAccuracy, absoluteAccuracy);
    }

    protected AbstractPolynomialSolver(double relativeAccuracy, double absoluteAccuracy, double functionValueAccuracy) {
        super(relativeAccuracy, absoluteAccuracy, functionValueAccuracy);
    }

    /* access modifiers changed from: protected */
    public void setup(int maxEval, PolynomialFunction f, double min, double max, double startValue) {
        super.setup(maxEval, (UnivariateFunction) f, min, max, startValue);
        this.polynomialFunction = f;
    }

    /* access modifiers changed from: protected */
    public double[] getCoefficients() {
        return this.polynomialFunction.getCoefficients();
    }
}
