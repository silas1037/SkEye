package org.apache.commons.math3.analysis.solvers;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.analysis.RealFieldUnivariateFunction;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.util.IntegerSequence;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.util.Precision;

public class FieldBracketingNthOrderBrentSolver<T extends RealFieldElement<T>> implements BracketedRealFieldUnivariateSolver<T> {
    private static final int MAXIMAL_AGING = 2;
    private final T absoluteAccuracy;
    private IntegerSequence.Incrementor evaluations;
    private final Field<T> field;
    private final T functionValueAccuracy;
    private final int maximalOrder;
    private final T relativeAccuracy;

    public FieldBracketingNthOrderBrentSolver(T relativeAccuracy2, T absoluteAccuracy2, T functionValueAccuracy2, int maximalOrder2) throws NumberIsTooSmallException {
        if (maximalOrder2 < 2) {
            throw new NumberIsTooSmallException(Integer.valueOf(maximalOrder2), 2, true);
        }
        this.field = relativeAccuracy2.getField();
        this.maximalOrder = maximalOrder2;
        this.absoluteAccuracy = absoluteAccuracy2;
        this.relativeAccuracy = relativeAccuracy2;
        this.functionValueAccuracy = functionValueAccuracy2;
        this.evaluations = IntegerSequence.Incrementor.create();
    }

    public int getMaximalOrder() {
        return this.maximalOrder;
    }

    @Override // org.apache.commons.math3.analysis.solvers.BracketedRealFieldUnivariateSolver
    public int getMaxEvaluations() {
        return this.evaluations.getMaximalCount();
    }

    @Override // org.apache.commons.math3.analysis.solvers.BracketedRealFieldUnivariateSolver
    public int getEvaluations() {
        return this.evaluations.getCount();
    }

    @Override // org.apache.commons.math3.analysis.solvers.BracketedRealFieldUnivariateSolver
    public T getAbsoluteAccuracy() {
        return this.absoluteAccuracy;
    }

    @Override // org.apache.commons.math3.analysis.solvers.BracketedRealFieldUnivariateSolver
    public T getRelativeAccuracy() {
        return this.relativeAccuracy;
    }

    @Override // org.apache.commons.math3.analysis.solvers.BracketedRealFieldUnivariateSolver
    public T getFunctionValueAccuracy() {
        return this.functionValueAccuracy;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r7v0, resolved type: org.apache.commons.math3.analysis.solvers.FieldBracketingNthOrderBrentSolver<T extends org.apache.commons.math3.RealFieldElement<T>> */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.analysis.solvers.BracketedRealFieldUnivariateSolver
    public T solve(int maxEval, RealFieldUnivariateFunction<T> f, T min, T max, AllowedSolution allowedSolution) throws NullArgumentException, NoBracketingException {
        return (T) solve(maxEval, f, min, max, (RealFieldElement) ((RealFieldElement) min.add(max)).divide(2.0d), allowedSolution);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r35v0, types: [org.apache.commons.math3.analysis.solvers.FieldBracketingNthOrderBrentSolver<T extends org.apache.commons.math3.RealFieldElement<T>>, org.apache.commons.math3.analysis.solvers.FieldBracketingNthOrderBrentSolver] */
    /* JADX WARN: Type inference failed for: r37v0, types: [org.apache.commons.math3.analysis.RealFieldUnivariateFunction<T extends org.apache.commons.math3.RealFieldElement<T>>, java.lang.Object, org.apache.commons.math3.analysis.RealFieldUnivariateFunction] */
    /* JADX WARN: Type inference failed for: r24v9 */
    /* JADX WARN: Type inference failed for: r24v10 */
    @Override // org.apache.commons.math3.analysis.solvers.BracketedRealFieldUnivariateSolver
    public T solve(int maxEval, RealFieldUnivariateFunction<T> realFieldUnivariateFunction, T min, T max, T startValue, AllowedSolution allowedSolution) throws NullArgumentException, NoBracketingException {
        int nbPoints;
        int signChangeIndex;
        RealFieldElement realFieldElement;
        RealFieldElement realFieldElement2;
        RealFieldElement realFieldElement3;
        RealFieldElement xB;
        MathUtils.checkNotNull(realFieldUnivariateFunction);
        this.evaluations = this.evaluations.withMaximalCount(maxEval).withStart(0);
        T zero = this.field.getZero();
        RealFieldElement realFieldElement4 = (RealFieldElement) zero.add(Double.NaN);
        RealFieldElement[] realFieldElementArr = (RealFieldElement[]) MathArrays.buildArray(this.field, this.maximalOrder + 1);
        RealFieldElement[] realFieldElementArr2 = (RealFieldElement[]) MathArrays.buildArray(this.field, this.maximalOrder + 1);
        realFieldElementArr[0] = min;
        realFieldElementArr[1] = startValue;
        realFieldElementArr[2] = max;
        this.evaluations.increment();
        realFieldElementArr2[1] = realFieldUnivariateFunction.value(realFieldElementArr[1]);
        if (Precision.equals(realFieldElementArr2[1].getReal(), 0.0d, 1)) {
            return (T) realFieldElementArr[1];
        }
        this.evaluations.increment();
        realFieldElementArr2[0] = realFieldUnivariateFunction.value(realFieldElementArr[0]);
        if (Precision.equals(realFieldElementArr2[0].getReal(), 0.0d, 1)) {
            return (T) realFieldElementArr[0];
        }
        if (((RealFieldElement) realFieldElementArr2[0].multiply(realFieldElementArr2[1])).getReal() < 0.0d) {
            nbPoints = 2;
            signChangeIndex = 1;
        } else {
            this.evaluations.increment();
            realFieldElementArr2[2] = realFieldUnivariateFunction.value(realFieldElementArr[2]);
            if (Precision.equals(realFieldElementArr2[2].getReal(), 0.0d, 1)) {
                return (T) realFieldElementArr[2];
            }
            if (((RealFieldElement) realFieldElementArr2[1].multiply(realFieldElementArr2[2])).getReal() < 0.0d) {
                nbPoints = 3;
                signChangeIndex = 2;
            } else {
                throw new NoBracketingException(realFieldElementArr[0].getReal(), realFieldElementArr[2].getReal(), realFieldElementArr2[0].getReal(), realFieldElementArr2[2].getReal());
            }
        }
        RealFieldElement[] realFieldElementArr3 = (RealFieldElement[]) MathArrays.buildArray(this.field, realFieldElementArr.length);
        T xA = (T) realFieldElementArr[signChangeIndex - 1];
        RealFieldElement realFieldElement5 = realFieldElementArr2[signChangeIndex - 1];
        RealFieldElement realFieldElement6 = (RealFieldElement) xA.abs();
        RealFieldElement realFieldElement7 = (RealFieldElement) realFieldElement5.abs();
        int agingA = 0;
        T xB2 = (T) realFieldElementArr[signChangeIndex];
        RealFieldElement realFieldElement8 = realFieldElementArr2[signChangeIndex];
        RealFieldElement realFieldElement9 = (RealFieldElement) xB2.abs();
        RealFieldElement realFieldElement10 = (RealFieldElement) realFieldElement8.abs();
        int agingB = 0;
        while (true) {
            if (((RealFieldElement) realFieldElement6.subtract(realFieldElement9)).getReal() < 0.0d) {
                realFieldElement = realFieldElement9;
            } else {
                realFieldElement = realFieldElement6;
            }
            if (((RealFieldElement) realFieldElement7.subtract(realFieldElement10)).getReal() < 0.0d) {
                realFieldElement2 = realFieldElement10;
            } else {
                realFieldElement2 = realFieldElement7;
            }
            if (((RealFieldElement) ((RealFieldElement) xB2.subtract(xA)).subtract((RealFieldElement) this.absoluteAccuracy.add(this.relativeAccuracy.multiply(realFieldElement)))).getReal() > 0.0d && ((RealFieldElement) realFieldElement2.subtract(this.functionValueAccuracy)).getReal() >= 0.0d) {
                if (agingA >= 2) {
                    realFieldElement3 = (RealFieldElement) ((RealFieldElement) realFieldElement8.divide(16.0d)).negate();
                } else if (agingB >= 2) {
                    realFieldElement3 = (RealFieldElement) ((RealFieldElement) realFieldElement5.divide(16.0d)).negate();
                } else {
                    realFieldElement3 = zero;
                }
                int start = 0;
                int end = nbPoints;
                do {
                    System.arraycopy(realFieldElementArr, start, realFieldElementArr3, start, end - start);
                    xB = guessX(realFieldElement3, realFieldElementArr3, realFieldElementArr2, start, end);
                    if (((RealFieldElement) xB.subtract(xA)).getReal() <= 0.0d || ((RealFieldElement) xB.subtract(xB2)).getReal() >= 0.0d) {
                        if (signChangeIndex - start >= end - signChangeIndex) {
                            start++;
                        } else {
                            end--;
                        }
                        xB = (T) realFieldElement4;
                    }
                    if (!Double.isNaN(xB.getReal())) {
                        break;
                    }
                } while (end - start > 1);
                if (Double.isNaN(xB.getReal())) {
                    xB = (T) ((RealFieldElement) xA.add(((RealFieldElement) xB2.subtract(xA)).divide(2.0d)));
                    start = signChangeIndex - 1;
                    end = signChangeIndex;
                }
                this.evaluations.increment();
                RealFieldElement value = realFieldUnivariateFunction.value(xB);
                if (Precision.equals(value.getReal(), 0.0d, 1)) {
                    return (T) xB;
                }
                if (nbPoints > 2 && end - start != nbPoints) {
                    nbPoints = end - start;
                    System.arraycopy(realFieldElementArr, start, realFieldElementArr, 0, nbPoints);
                    System.arraycopy(realFieldElementArr2, start, realFieldElementArr2, 0, nbPoints);
                    signChangeIndex -= start;
                } else if (nbPoints == realFieldElementArr.length) {
                    nbPoints--;
                    if (signChangeIndex >= (realFieldElementArr.length + 1) / 2) {
                        System.arraycopy(realFieldElementArr, 1, realFieldElementArr, 0, nbPoints);
                        System.arraycopy(realFieldElementArr2, 1, realFieldElementArr2, 0, nbPoints);
                        signChangeIndex--;
                    }
                }
                System.arraycopy(realFieldElementArr, signChangeIndex, realFieldElementArr, signChangeIndex + 1, nbPoints - signChangeIndex);
                realFieldElementArr[signChangeIndex] = xB;
                System.arraycopy(realFieldElementArr2, signChangeIndex, realFieldElementArr2, signChangeIndex + 1, nbPoints - signChangeIndex);
                realFieldElementArr2[signChangeIndex] = value;
                nbPoints++;
                if (((RealFieldElement) value.multiply(realFieldElement5)).getReal() <= 0.0d) {
                    xB2 = xB;
                    realFieldElement8 = value;
                    realFieldElement10 = (RealFieldElement) realFieldElement8.abs();
                    agingA++;
                    agingB = 0;
                } else {
                    xA = xB;
                    realFieldElement5 = value;
                    realFieldElement7 = (RealFieldElement) realFieldElement5.abs();
                    agingA = 0;
                    agingB++;
                    signChangeIndex++;
                }
            }
        }
        switch (allowedSolution) {
            case ANY_SIDE:
                return ((RealFieldElement) realFieldElement7.subtract(realFieldElement10)).getReal() >= 0.0d ? xB2 : xA;
            case LEFT_SIDE:
                return xA;
            case RIGHT_SIDE:
                return xB2;
            case BELOW_SIDE:
                return realFieldElement5.getReal() > 0.0d ? xB2 : xA;
            case ABOVE_SIDE:
                if (realFieldElement5.getReal() >= 0.0d) {
                    xB2 = xA;
                }
                return xB2;
            default:
                throw new MathInternalError(null);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for r9v0, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v2, resolved type: org.apache.commons.math3.dfp.DfpDec */
    /* JADX DEBUG: Multi-variable search result rejected for r4v4, resolved type: org.apache.commons.math3.dfp.DfpDec */
    /* JADX DEBUG: Multi-variable search result rejected for r5v4, resolved type: java.lang.Object[] */
    /* JADX WARN: Multi-variable type inference failed */
    private T guessX(T targetY, T[] x, T[] y, int start, int end) {
        for (int i = start; i < end - 1; i++) {
            int delta = (i + 1) - start;
            for (int j = end - 1; j > i; j--) {
                x[j] = (RealFieldElement) ((RealFieldElement) x[j].subtract(x[j - 1])).divide(y[j].subtract(y[j - delta]));
            }
        }
        T x0 = this.field.getZero();
        for (int j2 = end - 1; j2 >= start; j2--) {
            x0 = (T) ((RealFieldElement) x[j2].add(x0.multiply(targetY.subtract(y[j2]))));
        }
        return x0;
    }
}
