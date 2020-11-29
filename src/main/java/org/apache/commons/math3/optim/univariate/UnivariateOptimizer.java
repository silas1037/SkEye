package org.apache.commons.math3.optim.univariate;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.optim.BaseOptimizer;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;

public abstract class UnivariateOptimizer extends BaseOptimizer<UnivariatePointValuePair> {
    private UnivariateFunction function;
    private GoalType goal;
    private double max;
    private double min;
    private double start;

    protected UnivariateOptimizer(ConvergenceChecker<UnivariatePointValuePair> checker) {
        super(checker);
    }

    @Override // org.apache.commons.math3.optim.BaseOptimizer
    public UnivariatePointValuePair optimize(OptimizationData... optData) throws TooManyEvaluationsException {
        return (UnivariatePointValuePair) super.optimize(optData);
    }

    public GoalType getGoalType() {
        return this.goal;
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.optim.BaseOptimizer
    public void parseOptimizationData(OptimizationData... optData) {
        super.parseOptimizationData(optData);
        for (OptimizationData data : optData) {
            if (data instanceof SearchInterval) {
                SearchInterval interval = (SearchInterval) data;
                this.min = interval.getMin();
                this.max = interval.getMax();
                this.start = interval.getStartValue();
            } else if (data instanceof UnivariateObjectiveFunction) {
                this.function = ((UnivariateObjectiveFunction) data).getObjectiveFunction();
            } else if (data instanceof GoalType) {
                this.goal = (GoalType) data;
            }
        }
    }

    public double getStartValue() {
        return this.start;
    }

    public double getMin() {
        return this.min;
    }

    public double getMax() {
        return this.max;
    }

    /* access modifiers changed from: protected */
    public double computeObjectiveValue(double x) {
        super.incrementEvaluationCount();
        return this.function.value(x);
    }
}
