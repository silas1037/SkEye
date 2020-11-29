package org.apache.commons.math3.analysis.solvers;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.util.IntegerSequence;
import org.apache.commons.math3.util.MathUtils;

public abstract class BaseAbstractUnivariateSolver<FUNC extends UnivariateFunction> implements BaseUnivariateSolver<FUNC> {
    private static final double DEFAULT_FUNCTION_VALUE_ACCURACY = 1.0E-15d;
    private static final double DEFAULT_RELATIVE_ACCURACY = 1.0E-14d;
    private final double absoluteAccuracy;
    private IntegerSequence.Incrementor evaluations;
    private FUNC function;
    private final double functionValueAccuracy;
    private final double relativeAccuracy;
    private double searchMax;
    private double searchMin;
    private double searchStart;

    /* access modifiers changed from: protected */
    public abstract double doSolve() throws TooManyEvaluationsException, NoBracketingException;

    protected BaseAbstractUnivariateSolver(double absoluteAccuracy2) {
        this(DEFAULT_RELATIVE_ACCURACY, absoluteAccuracy2, 1.0E-15d);
    }

    protected BaseAbstractUnivariateSolver(double relativeAccuracy2, double absoluteAccuracy2) {
        this(relativeAccuracy2, absoluteAccuracy2, 1.0E-15d);
    }

    protected BaseAbstractUnivariateSolver(double relativeAccuracy2, double absoluteAccuracy2, double functionValueAccuracy2) {
        this.absoluteAccuracy = absoluteAccuracy2;
        this.relativeAccuracy = relativeAccuracy2;
        this.functionValueAccuracy = functionValueAccuracy2;
        this.evaluations = IntegerSequence.Incrementor.create();
    }

    @Override // org.apache.commons.math3.analysis.solvers.BaseUnivariateSolver
    public int getMaxEvaluations() {
        return this.evaluations.getMaximalCount();
    }

    @Override // org.apache.commons.math3.analysis.solvers.BaseUnivariateSolver
    public int getEvaluations() {
        return this.evaluations.getCount();
    }

    public double getMin() {
        return this.searchMin;
    }

    public double getMax() {
        return this.searchMax;
    }

    public double getStartValue() {
        return this.searchStart;
    }

    @Override // org.apache.commons.math3.analysis.solvers.BaseUnivariateSolver
    public double getAbsoluteAccuracy() {
        return this.absoluteAccuracy;
    }

    @Override // org.apache.commons.math3.analysis.solvers.BaseUnivariateSolver
    public double getRelativeAccuracy() {
        return this.relativeAccuracy;
    }

    @Override // org.apache.commons.math3.analysis.solvers.BaseUnivariateSolver
    public double getFunctionValueAccuracy() {
        return this.functionValueAccuracy;
    }

    /* access modifiers changed from: protected */
    public double computeObjectiveValue(double point) throws TooManyEvaluationsException {
        incrementEvaluationCount();
        return this.function.value(point);
    }

    /* access modifiers changed from: protected */
    public void setup(int maxEval, FUNC f, double min, double max, double startValue) throws NullArgumentException {
        MathUtils.checkNotNull(f);
        this.searchMin = min;
        this.searchMax = max;
        this.searchStart = startValue;
        this.function = f;
        this.evaluations = this.evaluations.withMaximalCount(maxEval).withStart(0);
    }

    @Override // org.apache.commons.math3.analysis.solvers.BaseUnivariateSolver
    public double solve(int maxEval, FUNC f, double min, double max, double startValue) throws TooManyEvaluationsException, NoBracketingException {
        setup(maxEval, f, min, max, startValue);
        return doSolve();
    }

    @Override // org.apache.commons.math3.analysis.solvers.BaseUnivariateSolver
    public double solve(int maxEval, FUNC f, double min, double max) {
        return solve(maxEval, f, min, max, min + (0.5d * (max - min)));
    }

    @Override // org.apache.commons.math3.analysis.solvers.BaseUnivariateSolver
    public double solve(int maxEval, FUNC f, double startValue) throws TooManyEvaluationsException, NoBracketingException {
        return solve(maxEval, f, Double.NaN, Double.NaN, startValue);
    }

    /* access modifiers changed from: protected */
    public boolean isBracketing(double lower, double upper) {
        return UnivariateSolverUtils.isBracketing(this.function, lower, upper);
    }

    /* access modifiers changed from: protected */
    public boolean isSequence(double start, double mid, double end) {
        return UnivariateSolverUtils.isSequence(start, mid, end);
    }

    /* access modifiers changed from: protected */
    public void verifyInterval(double lower, double upper) throws NumberIsTooLargeException {
        UnivariateSolverUtils.verifyInterval(lower, upper);
    }

    /* access modifiers changed from: protected */
    public void verifySequence(double lower, double initial, double upper) throws NumberIsTooLargeException {
        UnivariateSolverUtils.verifySequence(lower, initial, upper);
    }

    /* access modifiers changed from: protected */
    public void verifyBracketing(double lower, double upper) throws NullArgumentException, NoBracketingException {
        UnivariateSolverUtils.verifyBracketing(this.function, lower, upper);
    }

    /* access modifiers changed from: protected */
    public void incrementEvaluationCount() throws TooManyEvaluationsException {
        try {
            this.evaluations.increment();
        } catch (MaxCountExceededException e) {
            throw new TooManyEvaluationsException(e.getMax());
        }
    }
}
