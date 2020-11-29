package org.apache.commons.math3.analysis.solvers;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Precision;

public class BracketingNthOrderBrentSolver extends AbstractUnivariateSolver implements BracketedUnivariateSolver<UnivariateFunction> {
    private static final double DEFAULT_ABSOLUTE_ACCURACY = 1.0E-6d;
    private static final int DEFAULT_MAXIMAL_ORDER = 5;
    private static final int MAXIMAL_AGING = 2;
    private static final double REDUCTION_FACTOR = 0.0625d;
    private AllowedSolution allowed;
    private final int maximalOrder;

    public BracketingNthOrderBrentSolver() {
        this(1.0E-6d, 5);
    }

    public BracketingNthOrderBrentSolver(double absoluteAccuracy, int maximalOrder2) throws NumberIsTooSmallException {
        super(absoluteAccuracy);
        if (maximalOrder2 < 2) {
            throw new NumberIsTooSmallException(Integer.valueOf(maximalOrder2), 2, true);
        }
        this.maximalOrder = maximalOrder2;
        this.allowed = AllowedSolution.ANY_SIDE;
    }

    public BracketingNthOrderBrentSolver(double relativeAccuracy, double absoluteAccuracy, int maximalOrder2) throws NumberIsTooSmallException {
        super(relativeAccuracy, absoluteAccuracy);
        if (maximalOrder2 < 2) {
            throw new NumberIsTooSmallException(Integer.valueOf(maximalOrder2), 2, true);
        }
        this.maximalOrder = maximalOrder2;
        this.allowed = AllowedSolution.ANY_SIDE;
    }

    public BracketingNthOrderBrentSolver(double relativeAccuracy, double absoluteAccuracy, double functionValueAccuracy, int maximalOrder2) throws NumberIsTooSmallException {
        super(relativeAccuracy, absoluteAccuracy, functionValueAccuracy);
        if (maximalOrder2 < 2) {
            throw new NumberIsTooSmallException(Integer.valueOf(maximalOrder2), 2, true);
        }
        this.maximalOrder = maximalOrder2;
        this.allowed = AllowedSolution.ANY_SIDE;
    }

    public int getMaximalOrder() {
        return this.maximalOrder;
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.analysis.solvers.BaseAbstractUnivariateSolver
    public double doSolve() throws TooManyEvaluationsException, NumberIsTooLargeException, NoBracketingException {
        int nbPoints;
        int signChangeIndex;
        double targetY;
        double nextX;
        double[] x = new double[(this.maximalOrder + 1)];
        double[] y = new double[(this.maximalOrder + 1)];
        x[0] = getMin();
        x[1] = getStartValue();
        x[2] = getMax();
        verifySequence(x[0], x[1], x[2]);
        y[1] = computeObjectiveValue(x[1]);
        if (Precision.equals(y[1], 0.0d, 1)) {
            return x[1];
        }
        y[0] = computeObjectiveValue(x[0]);
        if (Precision.equals(y[0], 0.0d, 1)) {
            return x[0];
        }
        if (y[0] * y[1] < 0.0d) {
            nbPoints = 2;
            signChangeIndex = 1;
        } else {
            y[2] = computeObjectiveValue(x[2]);
            if (Precision.equals(y[2], 0.0d, 1)) {
                return x[2];
            }
            if (y[1] * y[2] < 0.0d) {
                nbPoints = 3;
                signChangeIndex = 2;
            } else {
                throw new NoBracketingException(x[0], x[2], y[0], y[2]);
            }
        }
        double[] tmpX = new double[x.length];
        double xA = x[signChangeIndex - 1];
        double yA = y[signChangeIndex - 1];
        double absYA = FastMath.abs(yA);
        int agingA = 0;
        double xB = x[signChangeIndex];
        double yB = y[signChangeIndex];
        double absYB = FastMath.abs(yB);
        int agingB = 0;
        while (true) {
            if (xB - xA > getAbsoluteAccuracy() + (getRelativeAccuracy() * FastMath.max(FastMath.abs(xA), FastMath.abs(xB))) && FastMath.max(absYA, absYB) >= getFunctionValueAccuracy()) {
                if (agingA >= 2) {
                    int p = agingA - 2;
                    double weightA = (double) ((1 << p) - 1);
                    double weightB = (double) (p + 1);
                    targetY = ((weightA * yA) - ((REDUCTION_FACTOR * weightB) * yB)) / (weightA + weightB);
                } else if (agingB >= 2) {
                    int p2 = agingB - 2;
                    double weightA2 = (double) (p2 + 1);
                    double weightB2 = (double) ((1 << p2) - 1);
                    targetY = ((weightB2 * yB) - ((REDUCTION_FACTOR * weightA2) * yA)) / (weightA2 + weightB2);
                } else {
                    targetY = 0.0d;
                }
                int start = 0;
                int end = nbPoints;
                do {
                    System.arraycopy(x, start, tmpX, start, end - start);
                    nextX = guessX(targetY, tmpX, y, start, end);
                    if (nextX <= xA || nextX >= xB) {
                        if (signChangeIndex - start >= end - signChangeIndex) {
                            start++;
                        } else {
                            end--;
                        }
                        nextX = Double.NaN;
                    }
                    if (!Double.isNaN(nextX)) {
                        break;
                    }
                } while (end - start > 1);
                if (Double.isNaN(nextX)) {
                    nextX = xA + (0.5d * (xB - xA));
                    start = signChangeIndex - 1;
                    end = signChangeIndex;
                }
                double nextY = computeObjectiveValue(nextX);
                if (Precision.equals(nextY, 0.0d, 1)) {
                    return nextX;
                }
                if (nbPoints > 2 && end - start != nbPoints) {
                    nbPoints = end - start;
                    System.arraycopy(x, start, x, 0, nbPoints);
                    System.arraycopy(y, start, y, 0, nbPoints);
                    signChangeIndex -= start;
                } else if (nbPoints == x.length) {
                    nbPoints--;
                    if (signChangeIndex >= (x.length + 1) / 2) {
                        System.arraycopy(x, 1, x, 0, nbPoints);
                        System.arraycopy(y, 1, y, 0, nbPoints);
                        signChangeIndex--;
                    }
                }
                System.arraycopy(x, signChangeIndex, x, signChangeIndex + 1, nbPoints - signChangeIndex);
                x[signChangeIndex] = nextX;
                System.arraycopy(y, signChangeIndex, y, signChangeIndex + 1, nbPoints - signChangeIndex);
                y[signChangeIndex] = nextY;
                nbPoints++;
                if (nextY * yA <= 0.0d) {
                    xB = nextX;
                    yB = nextY;
                    absYB = FastMath.abs(yB);
                    agingA++;
                    agingB = 0;
                } else {
                    xA = nextX;
                    yA = nextY;
                    absYA = FastMath.abs(yA);
                    agingA = 0;
                    agingB++;
                    signChangeIndex++;
                }
            }
        }
        switch (this.allowed) {
            case ANY_SIDE:
                return absYA >= absYB ? xB : xA;
            case LEFT_SIDE:
                return xA;
            case RIGHT_SIDE:
                return xB;
            case BELOW_SIDE:
                return yA > 0.0d ? xB : xA;
            case ABOVE_SIDE:
                if (yA >= 0.0d) {
                    xB = xA;
                }
                return xB;
            default:
                throw new MathInternalError();
        }
    }

    private double guessX(double targetY, double[] x, double[] y, int start, int end) {
        for (int i = start; i < end - 1; i++) {
            int delta = (i + 1) - start;
            for (int j = end - 1; j > i; j--) {
                x[j] = (x[j] - x[j - 1]) / (y[j] - y[j - delta]);
            }
        }
        double x0 = 0.0d;
        for (int j2 = end - 1; j2 >= start; j2--) {
            x0 = x[j2] + ((targetY - y[j2]) * x0);
        }
        return x0;
    }

    @Override // org.apache.commons.math3.analysis.solvers.BracketedUnivariateSolver
    public double solve(int maxEval, UnivariateFunction f, double min, double max, AllowedSolution allowedSolution) throws TooManyEvaluationsException, NumberIsTooLargeException, NoBracketingException {
        this.allowed = allowedSolution;
        return super.solve(maxEval, f, min, max);
    }

    @Override // org.apache.commons.math3.analysis.solvers.BracketedUnivariateSolver
    public double solve(int maxEval, UnivariateFunction f, double min, double max, double startValue, AllowedSolution allowedSolution) throws TooManyEvaluationsException, NumberIsTooLargeException, NoBracketingException {
        this.allowed = allowedSolution;
        return super.solve(maxEval, f, min, max, startValue);
    }
}
