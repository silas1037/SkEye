package org.apache.commons.math3.optim.univariate;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Precision;

public class BrentOptimizer extends UnivariateOptimizer {
    private static final double GOLDEN_SECTION = (0.5d * (3.0d - FastMath.sqrt(5.0d)));
    private static final double MIN_RELATIVE_TOLERANCE = (2.0d * FastMath.ulp(1.0d));
    private final double absoluteThreshold;
    private final double relativeThreshold;

    public BrentOptimizer(double rel, double abs, ConvergenceChecker<UnivariatePointValuePair> checker) {
        super(checker);
        if (rel < MIN_RELATIVE_TOLERANCE) {
            throw new NumberIsTooSmallException(Double.valueOf(rel), Double.valueOf(MIN_RELATIVE_TOLERANCE), true);
        } else if (abs <= 0.0d) {
            throw new NotStrictlyPositiveException(Double.valueOf(abs));
        } else {
            this.relativeThreshold = rel;
            this.absoluteThreshold = abs;
        }
    }

    public BrentOptimizer(double rel, double abs) {
        this(rel, abs, null);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.optim.BaseOptimizer
    public UnivariatePointValuePair doOptimize() {
        double a;
        double b;
        double d;
        double u;
        double d2;
        boolean isMinim = getGoalType() == GoalType.MINIMIZE;
        double lo = getMin();
        double mid = getStartValue();
        double hi = getMax();
        ConvergenceChecker<UnivariatePointValuePair> checker = getConvergenceChecker();
        if (lo < hi) {
            a = lo;
            b = hi;
        } else {
            a = hi;
            b = lo;
        }
        double x = mid;
        double v = x;
        double w = x;
        double d3 = 0.0d;
        double e = 0.0d;
        double fx = computeObjectiveValue(x);
        if (!isMinim) {
            fx = -fx;
        }
        double fv = fx;
        double fw = fx;
        UnivariatePointValuePair previous = null;
        if (isMinim) {
            d = fx;
        } else {
            d = -fx;
        }
        UnivariatePointValuePair current = new UnivariatePointValuePair(x, d);
        UnivariatePointValuePair best = current;
        while (true) {
            double m = 0.5d * (a + b);
            double tol1 = (this.relativeThreshold * FastMath.abs(x)) + this.absoluteThreshold;
            double tol2 = 2.0d * tol1;
            if (FastMath.abs(x - m) <= tol2 - (0.5d * (b - a))) {
                return best(best, best(previous, current, isMinim), isMinim);
            }
            if (FastMath.abs(e) > tol1) {
                double r = (x - w) * (fx - fv);
                double q = (x - v) * (fx - fw);
                double p = ((x - v) * q) - ((x - w) * r);
                double q2 = 2.0d * (q - r);
                if (q2 > 0.0d) {
                    p = -p;
                } else {
                    q2 = -q2;
                }
                e = d3;
                if (p <= (a - x) * q2 || p >= (b - x) * q2 || FastMath.abs(p) >= FastMath.abs(0.5d * q2 * e)) {
                    if (x < m) {
                        e = b - x;
                    } else {
                        e = a - x;
                    }
                    d3 = GOLDEN_SECTION * e;
                } else {
                    d3 = p / q2;
                    double u2 = x + d3;
                    if (u2 - a < tol2 || b - u2 < tol2) {
                        if (x <= m) {
                            d3 = tol1;
                        } else {
                            d3 = -tol1;
                        }
                    }
                }
            } else {
                if (x < m) {
                    e = b - x;
                } else {
                    e = a - x;
                }
                d3 = GOLDEN_SECTION * e;
            }
            if (FastMath.abs(d3) >= tol1) {
                u = x + d3;
            } else if (d3 >= 0.0d) {
                u = x + tol1;
            } else {
                u = x - tol1;
            }
            double fu = computeObjectiveValue(u);
            if (!isMinim) {
                fu = -fu;
            }
            previous = current;
            if (isMinim) {
                d2 = fu;
            } else {
                d2 = -fu;
            }
            current = new UnivariatePointValuePair(u, d2);
            best = best(best, best(previous, current, isMinim), isMinim);
            if (checker != null && checker.converged(getIterations(), previous, current)) {
                return best;
            }
            if (fu <= fx) {
                if (u < x) {
                    b = x;
                } else {
                    a = x;
                }
                v = w;
                fv = fw;
                w = x;
                fw = fx;
                x = u;
                fx = fu;
            } else {
                if (u < x) {
                    a = u;
                } else {
                    b = u;
                }
                if (fu <= fw || Precision.equals(w, x)) {
                    v = w;
                    fv = fw;
                    w = u;
                    fw = fu;
                } else if (fu <= fv || Precision.equals(v, x) || Precision.equals(v, w)) {
                    v = u;
                    fv = fu;
                }
            }
            incrementIterationCount();
        }
    }

    private UnivariatePointValuePair best(UnivariatePointValuePair a, UnivariatePointValuePair b, boolean isMinim) {
        if (a == null) {
            return b;
        }
        if (b != null) {
            return isMinim ? a.getValue() > b.getValue() ? b : a : a.getValue() < b.getValue() ? b : a;
        }
        return a;
    }
}
