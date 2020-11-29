package org.apache.commons.math3.analysis.solvers;

import org.apache.commons.math3.analysis.DifferentiableUnivariateFunction;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.util.FastMath;

@Deprecated
public class NewtonSolver extends AbstractDifferentiableUnivariateSolver {
    private static final double DEFAULT_ABSOLUTE_ACCURACY = 1.0E-6d;

    public NewtonSolver() {
        this(1.0E-6d);
    }

    public NewtonSolver(double absoluteAccuracy) {
        super(absoluteAccuracy);
    }

    public double solve(int maxEval, DifferentiableUnivariateFunction f, double min, double max) throws TooManyEvaluationsException {
        return super.solve(maxEval, f, UnivariateSolverUtils.midpoint(min, max));
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.analysis.solvers.BaseAbstractUnivariateSolver
    public double doSolve() throws TooManyEvaluationsException {
        double startValue = getStartValue();
        double absoluteAccuracy = getAbsoluteAccuracy();
        double x0 = startValue;
        while (true) {
            double x1 = x0 - (computeObjectiveValue(x0) / computeDerivativeObjectiveValue(x0));
            if (FastMath.abs(x1 - x0) <= absoluteAccuracy) {
                return x1;
            }
            x0 = x1;
        }
    }
}
