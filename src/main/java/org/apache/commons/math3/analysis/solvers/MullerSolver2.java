package org.apache.commons.math3.analysis.solvers;

import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.util.FastMath;

public class MullerSolver2 extends AbstractUnivariateSolver {
    private static final double DEFAULT_ABSOLUTE_ACCURACY = 1.0E-6d;

    public MullerSolver2() {
        this(1.0E-6d);
    }

    public MullerSolver2(double absoluteAccuracy) {
        super(absoluteAccuracy);
    }

    public MullerSolver2(double relativeAccuracy, double absoluteAccuracy) {
        super(relativeAccuracy, absoluteAccuracy);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.analysis.solvers.BaseAbstractUnivariateSolver
    public double doSolve() throws TooManyEvaluationsException, NumberIsTooLargeException, NoBracketingException {
        double denominator;
        double x;
        double min = getMin();
        double max = getMax();
        verifyInterval(min, max);
        double relativeAccuracy = getRelativeAccuracy();
        double absoluteAccuracy = getAbsoluteAccuracy();
        double functionValueAccuracy = getFunctionValueAccuracy();
        double x0 = min;
        double y0 = computeObjectiveValue(x0);
        if (FastMath.abs(y0) < functionValueAccuracy) {
            return x0;
        }
        double x1 = max;
        double y1 = computeObjectiveValue(x1);
        if (FastMath.abs(y1) < functionValueAccuracy) {
            return x1;
        }
        if (y0 * y1 > 0.0d) {
            throw new NoBracketingException(x0, x1, y0, y1);
        }
        double x2 = 0.5d * (x0 + x1);
        double y2 = computeObjectiveValue(x2);
        double oldx = Double.POSITIVE_INFINITY;
        while (true) {
            double q = (x2 - x1) / (x1 - x0);
            double b = ((((2.0d * q) + 1.0d) * y2) - (((1.0d + q) * (1.0d + q)) * y1)) + (q * q * y0);
            double c = (1.0d + q) * y2;
            double delta = (b * b) - ((4.0d * (q * ((y2 - ((1.0d + q) * y1)) + (q * y0)))) * c);
            if (delta >= 0.0d) {
                double dplus = b + FastMath.sqrt(delta);
                double dminus = b - FastMath.sqrt(delta);
                if (FastMath.abs(dplus) > FastMath.abs(dminus)) {
                    denominator = dplus;
                } else {
                    denominator = dminus;
                }
            } else {
                denominator = FastMath.sqrt((b * b) - delta);
            }
            if (denominator != 0.0d) {
                x = x2 - (((2.0d * c) * (x2 - x1)) / denominator);
                while (true) {
                    if (x != x1 && x != x2) {
                        break;
                    }
                    x += absoluteAccuracy;
                }
            } else {
                x = min + (FastMath.random() * (max - min));
                oldx = Double.POSITIVE_INFINITY;
            }
            double y = computeObjectiveValue(x);
            if (FastMath.abs(x - oldx) > FastMath.max(FastMath.abs(x) * relativeAccuracy, absoluteAccuracy) && FastMath.abs(y) > functionValueAccuracy) {
                x0 = x1;
                y0 = y1;
                x1 = x2;
                y1 = y2;
                x2 = x;
                y2 = y;
                oldx = x;
            }
        }
        return x;
    }
}
