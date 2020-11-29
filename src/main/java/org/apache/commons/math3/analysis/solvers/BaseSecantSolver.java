package org.apache.commons.math3.analysis.solvers;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.util.FastMath;

public abstract class BaseSecantSolver extends AbstractUnivariateSolver implements BracketedUnivariateSolver<UnivariateFunction> {
    protected static final double DEFAULT_ABSOLUTE_ACCURACY = 1.0E-6d;
    private AllowedSolution allowed = AllowedSolution.ANY_SIDE;
    private final Method method;

    protected enum Method {
        REGULA_FALSI,
        ILLINOIS,
        PEGASUS
    }

    protected BaseSecantSolver(double absoluteAccuracy, Method method2) {
        super(absoluteAccuracy);
        this.method = method2;
    }

    protected BaseSecantSolver(double relativeAccuracy, double absoluteAccuracy, Method method2) {
        super(relativeAccuracy, absoluteAccuracy);
        this.method = method2;
    }

    protected BaseSecantSolver(double relativeAccuracy, double absoluteAccuracy, double functionValueAccuracy, Method method2) {
        super(relativeAccuracy, absoluteAccuracy, functionValueAccuracy);
        this.method = method2;
    }

    @Override // org.apache.commons.math3.analysis.solvers.BracketedUnivariateSolver
    public double solve(int maxEval, UnivariateFunction f, double min, double max, AllowedSolution allowedSolution) {
        return solve(maxEval, f, min, max, min + (0.5d * (max - min)), allowedSolution);
    }

    @Override // org.apache.commons.math3.analysis.solvers.BracketedUnivariateSolver
    public double solve(int maxEval, UnivariateFunction f, double min, double max, double startValue, AllowedSolution allowedSolution) {
        this.allowed = allowedSolution;
        return super.solve(maxEval, f, min, max, startValue);
    }

    @Override // org.apache.commons.math3.analysis.solvers.BaseUnivariateSolver, org.apache.commons.math3.analysis.solvers.BaseAbstractUnivariateSolver
    public double solve(int maxEval, UnivariateFunction f, double min, double max, double startValue) {
        return solve(maxEval, f, min, max, startValue, AllowedSolution.ANY_SIDE);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.analysis.solvers.BaseAbstractUnivariateSolver
    public final double doSolve() throws ConvergenceException {
        double x0 = getMin();
        double x1 = getMax();
        double f0 = computeObjectiveValue(x0);
        double f1 = computeObjectiveValue(x1);
        if (f0 == 0.0d) {
            return x0;
        }
        if (f1 == 0.0d) {
            return x1;
        }
        verifyBracketing(x0, x1);
        double ftol = getFunctionValueAccuracy();
        double atol = getAbsoluteAccuracy();
        double rtol = getRelativeAccuracy();
        boolean inverted = false;
        do {
            double x = x1 - (((x1 - x0) * f1) / (f1 - f0));
            double fx = computeObjectiveValue(x);
            if (fx == 0.0d) {
                return x;
            }
            if (f1 * fx < 0.0d) {
                x0 = x1;
                f0 = f1;
                if (!inverted) {
                    inverted = true;
                } else {
                    inverted = false;
                }
            } else {
                switch (this.method) {
                    case ILLINOIS:
                        f0 *= 0.5d;
                        break;
                    case PEGASUS:
                        f0 *= f1 / (f1 + fx);
                        break;
                    case REGULA_FALSI:
                        if (x == x1) {
                            throw new ConvergenceException();
                        }
                        break;
                    default:
                        throw new MathInternalError();
                }
            }
            x1 = x;
            f1 = fx;
            if (FastMath.abs(f1) <= ftol) {
                switch (this.allowed) {
                    case ANY_SIDE:
                        return x1;
                    case LEFT_SIDE:
                        if (inverted) {
                            return x1;
                        }
                        break;
                    case RIGHT_SIDE:
                        if (!inverted) {
                            return x1;
                        }
                        break;
                    case BELOW_SIDE:
                        if (f1 <= 0.0d) {
                            return x1;
                        }
                        break;
                    case ABOVE_SIDE:
                        if (f1 >= 0.0d) {
                            return x1;
                        }
                        break;
                    default:
                        throw new MathInternalError();
                }
            }
        } while (FastMath.abs(x1 - x0) >= FastMath.max(FastMath.abs(x1) * rtol, atol));
        switch (this.allowed) {
            case ANY_SIDE:
                return x1;
            case LEFT_SIDE:
                return inverted ? x1 : x0;
            case RIGHT_SIDE:
                return inverted ? x0 : x1;
            case BELOW_SIDE:
                return f1 <= 0.0d ? x1 : x0;
            case ABOVE_SIDE:
                return f1 >= 0.0d ? x1 : x0;
            default:
                throw new MathInternalError();
        }
    }
}
