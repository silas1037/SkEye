package org.apache.commons.math3.analysis.solvers;

import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.util.FastMath;

public class RiddersSolver extends AbstractUnivariateSolver {
    private static final double DEFAULT_ABSOLUTE_ACCURACY = 1.0E-6d;

    public RiddersSolver() {
        this(1.0E-6d);
    }

    public RiddersSolver(double absoluteAccuracy) {
        super(absoluteAccuracy);
    }

    public RiddersSolver(double relativeAccuracy, double absoluteAccuracy) {
        super(relativeAccuracy, absoluteAccuracy);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.analysis.solvers.BaseAbstractUnivariateSolver
    public double doSolve() throws TooManyEvaluationsException, NoBracketingException {
        double min = getMin();
        double max = getMax();
        double x1 = min;
        double y1 = computeObjectiveValue(x1);
        double x2 = max;
        double y2 = computeObjectiveValue(x2);
        if (y1 == 0.0d) {
            return min;
        }
        if (y2 == 0.0d) {
            return max;
        }
        verifyBracketing(min, max);
        double absoluteAccuracy = getAbsoluteAccuracy();
        double functionValueAccuracy = getFunctionValueAccuracy();
        double relativeAccuracy = getRelativeAccuracy();
        double oldx = Double.POSITIVE_INFINITY;
        while (true) {
            double x3 = 0.5d * (x1 + x2);
            double y3 = computeObjectiveValue(x3);
            if (FastMath.abs(y3) <= functionValueAccuracy) {
                return x3;
            }
            double correction = ((FastMath.signum(y2) * FastMath.signum(y3)) * (x3 - x1)) / FastMath.sqrt(1.0d - ((y1 * y2) / (y3 * y3)));
            double x = x3 - correction;
            double y = computeObjectiveValue(x);
            if (FastMath.abs(x - oldx) <= FastMath.max(FastMath.abs(x) * relativeAccuracy, absoluteAccuracy) || FastMath.abs(y) <= functionValueAccuracy) {
                return x;
            }
            if (correction > 0.0d) {
                if (FastMath.signum(y1) + FastMath.signum(y) == 0.0d) {
                    x2 = x;
                    y2 = y;
                } else {
                    x1 = x;
                    x2 = x3;
                    y1 = y;
                    y2 = y3;
                }
            } else if (FastMath.signum(y2) + FastMath.signum(y) == 0.0d) {
                x1 = x;
                y1 = y;
            } else {
                x1 = x3;
                x2 = x;
                y1 = y3;
                y2 = y;
            }
            oldx = x;
        }
    }
}
