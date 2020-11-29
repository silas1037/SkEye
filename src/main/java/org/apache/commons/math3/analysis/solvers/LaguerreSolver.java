package org.apache.commons.math3.analysis.solvers;

import org.apache.commons.math3.analysis.integration.BaseAbstractUnivariateIntegrator;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexUtils;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;

public class LaguerreSolver extends AbstractPolynomialSolver {
    private static final double DEFAULT_ABSOLUTE_ACCURACY = 1.0E-6d;
    private final ComplexSolver complexSolver;

    public LaguerreSolver() {
        this(1.0E-6d);
    }

    public LaguerreSolver(double absoluteAccuracy) {
        super(absoluteAccuracy);
        this.complexSolver = new ComplexSolver();
    }

    public LaguerreSolver(double relativeAccuracy, double absoluteAccuracy) {
        super(relativeAccuracy, absoluteAccuracy);
        this.complexSolver = new ComplexSolver();
    }

    public LaguerreSolver(double relativeAccuracy, double absoluteAccuracy, double functionValueAccuracy) {
        super(relativeAccuracy, absoluteAccuracy, functionValueAccuracy);
        this.complexSolver = new ComplexSolver();
    }

    @Override // org.apache.commons.math3.analysis.solvers.BaseAbstractUnivariateSolver
    public double doSolve() throws TooManyEvaluationsException, NumberIsTooLargeException, NoBracketingException {
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
            return laguerre(min, initial, yMin, yInitial);
        }
        double yMax = computeObjectiveValue(max);
        if (FastMath.abs(yMax) <= functionValueAccuracy) {
            return max;
        }
        if (yInitial * yMax < 0.0d) {
            return laguerre(initial, max, yInitial, yMax);
        }
        throw new NoBracketingException(min, max, yMin, yMax);
    }

    @Deprecated
    public double laguerre(double lo, double hi, double fLo, double fHi) {
        Complex[] c = ComplexUtils.convertToComplex(getCoefficients());
        Complex initial = new Complex(0.5d * (lo + hi), 0.0d);
        Complex z = this.complexSolver.solve(c, initial);
        if (this.complexSolver.isRoot(lo, hi, z)) {
            return z.getReal();
        }
        Complex[] root = this.complexSolver.solveAll(c, initial);
        for (int i = 0; i < root.length; i++) {
            if (this.complexSolver.isRoot(lo, hi, root[i])) {
                return root[i].getReal();
            }
        }
        return Double.NaN;
    }

    public Complex[] solveAllComplex(double[] coefficients, double initial) throws NullArgumentException, NoDataException, TooManyEvaluationsException {
        return solveAllComplex(coefficients, initial, BaseAbstractUnivariateIntegrator.DEFAULT_MAX_ITERATIONS_COUNT);
    }

    public Complex[] solveAllComplex(double[] coefficients, double initial, int maxEval) throws NullArgumentException, NoDataException, TooManyEvaluationsException {
        setup(maxEval, new PolynomialFunction(coefficients), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, initial);
        return this.complexSolver.solveAll(ComplexUtils.convertToComplex(coefficients), new Complex(initial, 0.0d));
    }

    public Complex solveComplex(double[] coefficients, double initial) throws NullArgumentException, NoDataException, TooManyEvaluationsException {
        return solveComplex(coefficients, initial, BaseAbstractUnivariateIntegrator.DEFAULT_MAX_ITERATIONS_COUNT);
    }

    public Complex solveComplex(double[] coefficients, double initial, int maxEval) throws NullArgumentException, NoDataException, TooManyEvaluationsException {
        setup(maxEval, new PolynomialFunction(coefficients), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, initial);
        return this.complexSolver.solve(ComplexUtils.convertToComplex(coefficients), new Complex(initial, 0.0d));
    }

    /* access modifiers changed from: private */
    public class ComplexSolver {
        private ComplexSolver() {
        }

        public boolean isRoot(double min, double max, Complex z) {
            if (!LaguerreSolver.this.isSequence(min, z.getReal(), max)) {
                return false;
            }
            if (FastMath.abs(z.getImaginary()) <= FastMath.max(LaguerreSolver.this.getRelativeAccuracy() * z.abs(), LaguerreSolver.this.getAbsoluteAccuracy()) || z.abs() <= LaguerreSolver.this.getFunctionValueAccuracy()) {
                return true;
            }
            return false;
        }

        public Complex[] solveAll(Complex[] coefficients, Complex initial) throws NullArgumentException, NoDataException, TooManyEvaluationsException {
            if (coefficients == null) {
                throw new NullArgumentException();
            }
            int n = coefficients.length - 1;
            if (n == 0) {
                throw new NoDataException(LocalizedFormats.POLYNOMIAL);
            }
            Complex[] c = new Complex[(n + 1)];
            for (int i = 0; i <= n; i++) {
                c[i] = coefficients[i];
            }
            Complex[] root = new Complex[n];
            for (int i2 = 0; i2 < n; i2++) {
                Complex[] subarray = new Complex[((n - i2) + 1)];
                System.arraycopy(c, 0, subarray, 0, subarray.length);
                root[i2] = solve(subarray, initial);
                Complex newc = c[n - i2];
                for (int j = (n - i2) - 1; j >= 0; j--) {
                    Complex oldc = c[j];
                    c[j] = newc;
                    newc = oldc.add(newc.multiply(root[i2]));
                }
            }
            return root;
        }

        public Complex solve(Complex[] coefficients, Complex initial) throws NullArgumentException, NoDataException, TooManyEvaluationsException {
            Complex denominator;
            if (coefficients == null) {
                throw new NullArgumentException();
            }
            int n = coefficients.length - 1;
            if (n == 0) {
                throw new NoDataException(LocalizedFormats.POLYNOMIAL);
            }
            double absoluteAccuracy = LaguerreSolver.this.getAbsoluteAccuracy();
            double relativeAccuracy = LaguerreSolver.this.getRelativeAccuracy();
            double functionValueAccuracy = LaguerreSolver.this.getFunctionValueAccuracy();
            Complex nC = new Complex((double) n, 0.0d);
            Complex n1C = new Complex((double) (n - 1), 0.0d);
            Complex z = initial;
            Complex oldz = new Complex(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
            while (true) {
                Complex pv = coefficients[n];
                Complex dv = Complex.ZERO;
                Complex d2v = Complex.ZERO;
                for (int j = n - 1; j >= 0; j--) {
                    d2v = dv.add(z.multiply(d2v));
                    dv = pv.add(z.multiply(dv));
                    pv = coefficients[j].add(z.multiply(pv));
                }
                Complex d2v2 = d2v.multiply(new Complex(2.0d, 0.0d));
                if (z.subtract(oldz).abs() > FastMath.max(z.abs() * relativeAccuracy, absoluteAccuracy) && pv.abs() > functionValueAccuracy) {
                    Complex G = dv.divide(pv);
                    Complex G2 = G.multiply(G);
                    Complex deltaSqrt = n1C.multiply(nC.multiply(G2.subtract(d2v2.divide(pv))).subtract(G2)).sqrt();
                    Complex dplus = G.add(deltaSqrt);
                    Complex dminus = G.subtract(deltaSqrt);
                    if (dplus.abs() > dminus.abs()) {
                        denominator = dplus;
                    } else {
                        denominator = dminus;
                    }
                    if (denominator.equals(new Complex(0.0d, 0.0d))) {
                        z = z.add(new Complex(absoluteAccuracy, absoluteAccuracy));
                        oldz = new Complex(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
                    } else {
                        oldz = z;
                        z = z.subtract(nC.divide(denominator));
                    }
                    LaguerreSolver.this.incrementEvaluationCount();
                }
            }
            return z;
        }
    }
}
