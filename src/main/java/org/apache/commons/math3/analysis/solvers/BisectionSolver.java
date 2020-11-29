package org.apache.commons.math3.analysis.solvers;

import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.util.FastMath;

public class BisectionSolver extends AbstractUnivariateSolver {
    private static final double DEFAULT_ABSOLUTE_ACCURACY = 1.0E-6d;

    public BisectionSolver() {
        this(1.0E-6d);
    }

    public BisectionSolver(double absoluteAccuracy) {
        super(absoluteAccuracy);
    }

    public BisectionSolver(double relativeAccuracy, double absoluteAccuracy) {
        super(relativeAccuracy, absoluteAccuracy);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.analysis.solvers.BaseAbstractUnivariateSolver
    public double doSolve() throws TooManyEvaluationsException {
        double min = getMin();
        double max = getMax();
        verifyInterval(min, max);
        double absoluteAccuracy = getAbsoluteAccuracy();
        do {
            double m = UnivariateSolverUtils.midpoint(min, max);
            if (computeObjectiveValue(m) * computeObjectiveValue(min) > 0.0d) {
                min = m;
            } else {
                max = m;
            }
        } while (FastMath.abs(max - min) > absoluteAccuracy);
        return UnivariateSolverUtils.midpoint(min, max);
    }
}
