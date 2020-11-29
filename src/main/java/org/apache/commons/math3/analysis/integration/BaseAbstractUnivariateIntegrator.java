package org.apache.commons.math3.analysis.integration;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.UnivariateSolverUtils;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.util.Incrementor;
import org.apache.commons.math3.util.IntegerSequence;
import org.apache.commons.math3.util.MathUtils;

public abstract class BaseAbstractUnivariateIntegrator implements UnivariateIntegrator {
    public static final double DEFAULT_ABSOLUTE_ACCURACY = 1.0E-15d;
    public static final int DEFAULT_MAX_ITERATIONS_COUNT = Integer.MAX_VALUE;
    public static final int DEFAULT_MIN_ITERATIONS_COUNT = 3;
    public static final double DEFAULT_RELATIVE_ACCURACY = 1.0E-6d;
    private final double absoluteAccuracy;
    private IntegerSequence.Incrementor count;
    private IntegerSequence.Incrementor evaluations;
    private UnivariateFunction function;
    @Deprecated
    protected Incrementor iterations;
    private double max;
    private double min;
    private final int minimalIterationCount;
    private final double relativeAccuracy;

    /* access modifiers changed from: protected */
    public abstract double doIntegrate() throws TooManyEvaluationsException, MaxCountExceededException;

    protected BaseAbstractUnivariateIntegrator(double relativeAccuracy2, double absoluteAccuracy2, int minimalIterationCount2, int maximalIterationCount) throws NotStrictlyPositiveException, NumberIsTooSmallException {
        this.relativeAccuracy = relativeAccuracy2;
        this.absoluteAccuracy = absoluteAccuracy2;
        if (minimalIterationCount2 <= 0) {
            throw new NotStrictlyPositiveException(Integer.valueOf(minimalIterationCount2));
        } else if (maximalIterationCount <= minimalIterationCount2) {
            throw new NumberIsTooSmallException(Integer.valueOf(maximalIterationCount), Integer.valueOf(minimalIterationCount2), false);
        } else {
            this.minimalIterationCount = minimalIterationCount2;
            this.count = IntegerSequence.Incrementor.create().withMaximalCount(maximalIterationCount);
            this.iterations = Incrementor.wrap(this.count);
            this.evaluations = IntegerSequence.Incrementor.create();
        }
    }

    protected BaseAbstractUnivariateIntegrator(double relativeAccuracy2, double absoluteAccuracy2) {
        this(relativeAccuracy2, absoluteAccuracy2, 3, DEFAULT_MAX_ITERATIONS_COUNT);
    }

    protected BaseAbstractUnivariateIntegrator(int minimalIterationCount2, int maximalIterationCount) throws NotStrictlyPositiveException, NumberIsTooSmallException {
        this(1.0E-6d, 1.0E-15d, minimalIterationCount2, maximalIterationCount);
    }

    @Override // org.apache.commons.math3.analysis.integration.UnivariateIntegrator
    public double getRelativeAccuracy() {
        return this.relativeAccuracy;
    }

    @Override // org.apache.commons.math3.analysis.integration.UnivariateIntegrator
    public double getAbsoluteAccuracy() {
        return this.absoluteAccuracy;
    }

    @Override // org.apache.commons.math3.analysis.integration.UnivariateIntegrator
    public int getMinimalIterationCount() {
        return this.minimalIterationCount;
    }

    @Override // org.apache.commons.math3.analysis.integration.UnivariateIntegrator
    public int getMaximalIterationCount() {
        return this.count.getMaximalCount();
    }

    @Override // org.apache.commons.math3.analysis.integration.UnivariateIntegrator
    public int getEvaluations() {
        return this.evaluations.getCount();
    }

    @Override // org.apache.commons.math3.analysis.integration.UnivariateIntegrator
    public int getIterations() {
        return this.count.getCount();
    }

    /* access modifiers changed from: protected */
    public void incrementCount() throws MaxCountExceededException {
        this.count.increment();
    }

    /* access modifiers changed from: protected */
    public double getMin() {
        return this.min;
    }

    /* access modifiers changed from: protected */
    public double getMax() {
        return this.max;
    }

    /* access modifiers changed from: protected */
    public double computeObjectiveValue(double point) throws TooManyEvaluationsException {
        try {
            this.evaluations.increment();
            return this.function.value(point);
        } catch (MaxCountExceededException e) {
            throw new TooManyEvaluationsException(e.getMax());
        }
    }

    /* access modifiers changed from: protected */
    public void setup(int maxEval, UnivariateFunction f, double lower, double upper) throws NullArgumentException, MathIllegalArgumentException {
        MathUtils.checkNotNull(f);
        UnivariateSolverUtils.verifyInterval(lower, upper);
        this.min = lower;
        this.max = upper;
        this.function = f;
        this.evaluations = this.evaluations.withMaximalCount(maxEval).withStart(0);
        this.count = this.count.withStart(0);
    }

    @Override // org.apache.commons.math3.analysis.integration.UnivariateIntegrator
    public double integrate(int maxEval, UnivariateFunction f, double lower, double upper) throws TooManyEvaluationsException, MaxCountExceededException, MathIllegalArgumentException, NullArgumentException {
        setup(maxEval, f, lower, upper);
        return doIntegrate();
    }
}
