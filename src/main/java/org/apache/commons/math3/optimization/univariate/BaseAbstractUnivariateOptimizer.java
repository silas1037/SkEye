package org.apache.commons.math3.optimization.univariate;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.optimization.ConvergenceChecker;
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.util.Incrementor;

@Deprecated
public abstract class BaseAbstractUnivariateOptimizer implements UnivariateOptimizer {
    private final ConvergenceChecker<UnivariatePointValuePair> checker;
    private final Incrementor evaluations = new Incrementor();
    private UnivariateFunction function;
    private GoalType goal;
    private double searchMax;
    private double searchMin;
    private double searchStart;

    /* access modifiers changed from: protected */
    public abstract UnivariatePointValuePair doOptimize();

    protected BaseAbstractUnivariateOptimizer(ConvergenceChecker<UnivariatePointValuePair> checker2) {
        this.checker = checker2;
    }

    @Override // org.apache.commons.math3.optimization.BaseOptimizer
    public int getMaxEvaluations() {
        return this.evaluations.getMaximalCount();
    }

    @Override // org.apache.commons.math3.optimization.BaseOptimizer
    public int getEvaluations() {
        return this.evaluations.getCount();
    }

    public GoalType getGoalType() {
        return this.goal;
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

    /* access modifiers changed from: protected */
    public double computeObjectiveValue(double point) {
        try {
            this.evaluations.incrementCount();
            return this.function.value(point);
        } catch (MaxCountExceededException e) {
            throw new TooManyEvaluationsException(e.getMax());
        }
    }

    @Override // org.apache.commons.math3.optimization.univariate.BaseUnivariateOptimizer
    public UnivariatePointValuePair optimize(int maxEval, UnivariateFunction f, GoalType goalType, double min, double max, double startValue) {
        if (f == null) {
            throw new NullArgumentException();
        } else if (goalType == null) {
            throw new NullArgumentException();
        } else {
            this.searchMin = min;
            this.searchMax = max;
            this.searchStart = startValue;
            this.goal = goalType;
            this.function = f;
            this.evaluations.setMaximalCount(maxEval);
            this.evaluations.resetCount();
            return doOptimize();
        }
    }

    @Override // org.apache.commons.math3.optimization.univariate.BaseUnivariateOptimizer
    public UnivariatePointValuePair optimize(int maxEval, UnivariateFunction f, GoalType goalType, double min, double max) {
        return optimize(maxEval, f, goalType, min, max, min + (0.5d * (max - min)));
    }

    @Override // org.apache.commons.math3.optimization.BaseOptimizer
    public ConvergenceChecker<UnivariatePointValuePair> getConvergenceChecker() {
        return this.checker;
    }
}
