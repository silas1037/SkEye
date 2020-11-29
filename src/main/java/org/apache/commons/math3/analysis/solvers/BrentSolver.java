package org.apache.commons.math3.analysis.solvers;

import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Precision;

public class BrentSolver extends AbstractUnivariateSolver {
    private static final double DEFAULT_ABSOLUTE_ACCURACY = 1.0E-6d;

    public BrentSolver() {
        this(1.0E-6d);
    }

    public BrentSolver(double absoluteAccuracy) {
        super(absoluteAccuracy);
    }

    public BrentSolver(double relativeAccuracy, double absoluteAccuracy) {
        super(relativeAccuracy, absoluteAccuracy);
    }

    public BrentSolver(double relativeAccuracy, double absoluteAccuracy, double functionValueAccuracy) {
        super(relativeAccuracy, absoluteAccuracy, functionValueAccuracy);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.analysis.solvers.BaseAbstractUnivariateSolver
    public double doSolve() throws NoBracketingException, TooManyEvaluationsException, NumberIsTooLargeException {
        double min = getMin();
        double max = getMax();
        double initial = getStartValue();
        double functionValueAccuracy = getFunctionValueAccuracy();
        verifySequence(min, initial, max);
        double yInitial = computeObjectiveValue(initial);
        if (FastMath.abs(yInitial) <= functionValueAccuracy) {
            return initial;
        }
        double yMin = computeObjectiveValue(min);
        if (FastMath.abs(yMin) <= functionValueAccuracy) {
            return min;
        }
        if (yInitial * yMin < 0.0d) {
            return brent(min, initial, yMin, yInitial);
        }
        double yMax = computeObjectiveValue(max);
        if (FastMath.abs(yMax) <= functionValueAccuracy) {
            return max;
        }
        if (yInitial * yMax < 0.0d) {
            return brent(initial, max, yInitial, yMax);
        }
        throw new NoBracketingException(min, max, yMin, yMax);
    }

    private double brent(double lo, double hi, double fLo, double fHi) {
        double p;
        double q;
        double a = lo;
        double fa = fLo;
        double b = hi;
        double fb = fHi;
        double c = a;
        double fc = fa;
        double d = b - a;
        double e = d;
        double t = getAbsoluteAccuracy();
        double eps = getRelativeAccuracy();
        while (true) {
            if (FastMath.abs(fc) < FastMath.abs(fb)) {
                a = b;
                b = c;
                c = a;
                fa = fb;
                fb = fc;
                fc = fa;
            }
            double tol = (2.0d * eps * FastMath.abs(b)) + t;
            double m = 0.5d * (c - b);
            if (FastMath.abs(m) <= tol || Precision.equals(fb, 0.0d)) {
                return b;
            }
            if (FastMath.abs(e) < tol || FastMath.abs(fa) <= FastMath.abs(fb)) {
                d = m;
                e = d;
            } else {
                double s = fb / fa;
                if (a == c) {
                    p = 2.0d * m * s;
                    q = 1.0d - s;
                } else {
                    double q2 = fa / fc;
                    double r = fb / fc;
                    p = s * ((((2.0d * m) * q2) * (q2 - r)) - ((b - a) * (r - 1.0d)));
                    q = (q2 - 1.0d) * (r - 1.0d) * (s - 1.0d);
                }
                if (p > 0.0d) {
                    q = -q;
                } else {
                    p = -p;
                }
                e = d;
                if (p >= ((1.5d * m) * q) - FastMath.abs(tol * q) || p >= FastMath.abs(0.5d * e * q)) {
                    d = m;
                    e = d;
                } else {
                    d = p / q;
                }
            }
            a = b;
            fa = fb;
            if (FastMath.abs(d) > tol) {
                b += d;
            } else if (m > 0.0d) {
                b += tol;
            } else {
                b -= tol;
            }
            fb = computeObjectiveValue(b);
            if ((fb > 0.0d && fc > 0.0d) || (fb <= 0.0d && fc <= 0.0d)) {
                c = a;
                fc = fa;
                d = b - a;
                e = d;
            }
        }
        return b;
    }
}
