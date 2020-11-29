package org.apache.commons.math3.analysis.solvers;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.BaseAbstractUnivariateIntegrator;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;

public class UnivariateSolverUtils {
    private UnivariateSolverUtils() {
    }

    public static double solve(UnivariateFunction function, double x0, double x1) throws NullArgumentException, NoBracketingException {
        if (function != null) {
            return new BrentSolver().solve(BaseAbstractUnivariateIntegrator.DEFAULT_MAX_ITERATIONS_COUNT, function, x0, x1);
        }
        throw new NullArgumentException(LocalizedFormats.FUNCTION, new Object[0]);
    }

    public static double solve(UnivariateFunction function, double x0, double x1, double absoluteAccuracy) throws NullArgumentException, NoBracketingException {
        if (function != null) {
            return new BrentSolver(absoluteAccuracy).solve(BaseAbstractUnivariateIntegrator.DEFAULT_MAX_ITERATIONS_COUNT, function, x0, x1);
        }
        throw new NullArgumentException(LocalizedFormats.FUNCTION, new Object[0]);
    }

    public static double forceSide(int maxEval, UnivariateFunction f, BracketedUnivariateSolver<UnivariateFunction> bracketing, double baseRoot, double min, double max, AllowedSolution allowedSolution) throws NoBracketingException {
        if (allowedSolution == AllowedSolution.ANY_SIDE) {
            return baseRoot;
        }
        double step = FastMath.max(bracketing.getAbsoluteAccuracy(), FastMath.abs(bracketing.getRelativeAccuracy() * baseRoot));
        double xLo = FastMath.max(min, baseRoot - step);
        double fLo = f.value(xLo);
        double xHi = FastMath.min(max, baseRoot + step);
        double fHi = f.value(xHi);
        int remainingEval = maxEval - 2;
        while (remainingEval > 0) {
            if ((fLo >= 0.0d && fHi <= 0.0d) || (fLo <= 0.0d && fHi >= 0.0d)) {
                return bracketing.solve(remainingEval, f, xLo, xHi, baseRoot, allowedSolution);
            }
            boolean changeLo = false;
            boolean changeHi = false;
            if (fLo < fHi) {
                if (fLo >= 0.0d) {
                    changeLo = true;
                } else {
                    changeHi = true;
                }
            } else if (fLo <= fHi) {
                changeLo = true;
                changeHi = true;
            } else if (fLo <= 0.0d) {
                changeLo = true;
            } else {
                changeHi = true;
            }
            if (changeLo) {
                xLo = FastMath.max(min, xLo - step);
                fLo = f.value(xLo);
                remainingEval--;
            }
            if (changeHi) {
                xHi = FastMath.min(max, xHi + step);
                fHi = f.value(xHi);
                remainingEval--;
            }
        }
        throw new NoBracketingException(LocalizedFormats.FAILED_BRACKETING, xLo, xHi, fLo, fHi, Integer.valueOf(maxEval - remainingEval), Integer.valueOf(maxEval), Double.valueOf(baseRoot), Double.valueOf(min), Double.valueOf(max));
    }

    public static double[] bracket(UnivariateFunction function, double initial, double lowerBound, double upperBound) throws NullArgumentException, NotStrictlyPositiveException, NoBracketingException {
        return bracket(function, initial, lowerBound, upperBound, 1.0d, 1.0d, BaseAbstractUnivariateIntegrator.DEFAULT_MAX_ITERATIONS_COUNT);
    }

    public static double[] bracket(UnivariateFunction function, double initial, double lowerBound, double upperBound, int maximumIterations) throws NullArgumentException, NotStrictlyPositiveException, NoBracketingException {
        return bracket(function, initial, lowerBound, upperBound, 1.0d, 1.0d, maximumIterations);
    }

    public static double[] bracket(UnivariateFunction function, double initial, double lowerBound, double upperBound, double q, double r, int maximumIterations) throws NoBracketingException {
        if (function == null) {
            throw new NullArgumentException(LocalizedFormats.FUNCTION, new Object[0]);
        } else if (q <= 0.0d) {
            throw new NotStrictlyPositiveException(Double.valueOf(q));
        } else if (maximumIterations <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.INVALID_MAX_ITERATIONS, Integer.valueOf(maximumIterations));
        } else {
            verifySequence(lowerBound, initial, upperBound);
            double a = initial;
            double b = initial;
            double fa = Double.NaN;
            double fb = Double.NaN;
            double delta = 0.0d;
            for (int numIterations = 0; numIterations < maximumIterations && (a > lowerBound || b < upperBound); numIterations++) {
                delta = (r * delta) + q;
                a = FastMath.max(initial - delta, lowerBound);
                b = FastMath.min(initial + delta, upperBound);
                fa = function.value(a);
                fb = function.value(b);
                if (numIterations == 0) {
                    if (fa * fb <= 0.0d) {
                        return new double[]{a, b};
                    }
                } else if (fa * fa <= 0.0d) {
                    return new double[]{a, a};
                } else if (fb * fb <= 0.0d) {
                    return new double[]{b, b};
                }
            }
            throw new NoBracketingException(a, b, fa, fb);
        }
    }

    public static double midpoint(double a, double b) {
        return (a + b) * 0.5d;
    }

    public static boolean isBracketing(UnivariateFunction function, double lower, double upper) throws NullArgumentException {
        if (function == null) {
            throw new NullArgumentException(LocalizedFormats.FUNCTION, new Object[0]);
        }
        double fLo = function.value(lower);
        double fHi = function.value(upper);
        if ((fLo < 0.0d || fHi > 0.0d) && (fLo > 0.0d || fHi < 0.0d)) {
            return false;
        }
        return true;
    }

    public static boolean isSequence(double start, double mid, double end) {
        return start < mid && mid < end;
    }

    public static void verifyInterval(double lower, double upper) throws NumberIsTooLargeException {
        if (lower >= upper) {
            throw new NumberIsTooLargeException(LocalizedFormats.ENDPOINTS_NOT_AN_INTERVAL, Double.valueOf(lower), Double.valueOf(upper), false);
        }
    }

    public static void verifySequence(double lower, double initial, double upper) throws NumberIsTooLargeException {
        verifyInterval(lower, initial);
        verifyInterval(initial, upper);
    }

    public static void verifyBracketing(UnivariateFunction function, double lower, double upper) throws NullArgumentException, NoBracketingException {
        if (function == null) {
            throw new NullArgumentException(LocalizedFormats.FUNCTION, new Object[0]);
        }
        verifyInterval(lower, upper);
        if (!isBracketing(function, lower, upper)) {
            throw new NoBracketingException(lower, upper, function.value(lower), function.value(upper));
        }
    }
}
