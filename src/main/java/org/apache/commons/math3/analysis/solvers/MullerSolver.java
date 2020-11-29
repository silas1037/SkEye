package org.apache.commons.math3.analysis.solvers;

import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.util.FastMath;

public class MullerSolver extends AbstractUnivariateSolver {
    private static final double DEFAULT_ABSOLUTE_ACCURACY = 1.0E-6d;

    public MullerSolver() {
        this(1.0E-6d);
    }

    public MullerSolver(double absoluteAccuracy) {
        super(absoluteAccuracy);
    }

    public MullerSolver(double relativeAccuracy, double absoluteAccuracy) {
        super(relativeAccuracy, absoluteAccuracy);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.analysis.solvers.BaseAbstractUnivariateSolver
    public double doSolve() throws TooManyEvaluationsException, NumberIsTooLargeException, NoBracketingException {
        double min = getMin();
        double max = getMax();
        double initial = getStartValue();
        double functionValueAccuracy = getFunctionValueAccuracy();
        verifySequence(min, initial, max);
        double fMin = computeObjectiveValue(min);
        if (FastMath.abs(fMin) < functionValueAccuracy) {
            return min;
        }
        double fMax = computeObjectiveValue(max);
        if (FastMath.abs(fMax) < functionValueAccuracy) {
            return max;
        }
        double fInitial = computeObjectiveValue(initial);
        if (FastMath.abs(fInitial) < functionValueAccuracy) {
            return initial;
        }
        verifyBracketing(min, max);
        if (isBracketing(min, initial)) {
            return solve(min, initial, fMin, fInitial);
        }
        return solve(initial, max, fInitial, fMax);
    }

    private double solve(double min, double max, double fMin, double fMax) throws TooManyEvaluationsException {
        double x;
        double relativeAccuracy = getRelativeAccuracy();
        double absoluteAccuracy = getAbsoluteAccuracy();
        double functionValueAccuracy = getFunctionValueAccuracy();
        double x0 = min;
        double y0 = fMin;
        double x2 = max;
        double y2 = fMax;
        double x1 = 0.5d * (x0 + x2);
        double y1 = computeObjectiveValue(x1);
        double oldx = Double.POSITIVE_INFINITY;
        while (true) {
            double d01 = (y1 - y0) / (x1 - x0);
            double d012 = (((y2 - y1) / (x2 - x1)) - d01) / (x2 - x0);
            double c1 = d01 + ((x1 - x0) * d012);
            double delta = (c1 * c1) - ((4.0d * y1) * d012);
            double xplus = x1 + ((-2.0d * y1) / (FastMath.sqrt(delta) + c1));
            double xminus = x1 + ((-2.0d * y1) / (c1 - FastMath.sqrt(delta)));
            if (isSequence(x0, xplus, x2)) {
                x = xplus;
            } else {
                x = xminus;
            }
            double y = computeObjectiveValue(x);
            if (FastMath.abs(x - oldx) <= FastMath.max(FastMath.abs(x) * relativeAccuracy, absoluteAccuracy) || FastMath.abs(y) <= functionValueAccuracy) {
                return x;
            }
            if (!((x < x1 && x1 - x0 > 0.95d * (x2 - x0)) || (x > x1 && x2 - x1 > 0.95d * (x2 - x0)) || x == x1)) {
                if (x >= x1) {
                    x0 = x1;
                }
                if (x >= x1) {
                    y0 = y1;
                }
                if (x <= x1) {
                    x2 = x1;
                }
                if (x <= x1) {
                    y2 = y1;
                }
                x1 = x;
                y1 = y;
                oldx = x;
            } else {
                double xm = 0.5d * (x0 + x2);
                double ym = computeObjectiveValue(xm);
                if (FastMath.signum(y0) + FastMath.signum(ym) == 0.0d) {
                    x2 = xm;
                    y2 = ym;
                } else {
                    x0 = xm;
                    y0 = ym;
                }
                x1 = 0.5d * (x0 + x2);
                y1 = computeObjectiveValue(x1);
                oldx = Double.POSITIVE_INFINITY;
            }
        }
        return x;
    }
}
