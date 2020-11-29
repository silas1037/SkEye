package org.apache.commons.math3.optimization.general;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.analysis.differentiation.GradientFunction;
import org.apache.commons.math3.analysis.differentiation.MultivariateDifferentiableFunction;
import org.apache.commons.math3.optimization.ConvergenceChecker;
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.InitialGuess;
import org.apache.commons.math3.optimization.OptimizationData;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.optimization.direct.BaseAbstractMultivariateOptimizer;

@Deprecated
public abstract class AbstractDifferentiableOptimizer extends BaseAbstractMultivariateOptimizer<MultivariateDifferentiableFunction> {
    private MultivariateVectorFunction gradient;

    protected AbstractDifferentiableOptimizer(ConvergenceChecker<PointValuePair> checker) {
        super(checker);
    }

    /* access modifiers changed from: protected */
    public double[] computeObjectiveGradient(double[] evaluationPoint) {
        return this.gradient.value(evaluationPoint);
    }

    /* access modifiers changed from: protected */
    @Deprecated
    public PointValuePair optimizeInternal(int maxEval, MultivariateDifferentiableFunction f, GoalType goalType, double[] startPoint) {
        return optimizeInternal(maxEval, f, goalType, new InitialGuess(startPoint));
    }

    /* access modifiers changed from: protected */
    public PointValuePair optimizeInternal(int maxEval, MultivariateDifferentiableFunction f, GoalType goalType, OptimizationData... optData) {
        this.gradient = new GradientFunction(f);
        return super.optimizeInternal(maxEval, (MultivariateFunction) f, goalType, optData);
    }
}
