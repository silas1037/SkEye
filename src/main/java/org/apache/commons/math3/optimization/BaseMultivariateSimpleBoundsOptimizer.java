package org.apache.commons.math3.optimization;

import org.apache.commons.math3.analysis.MultivariateFunction;

@Deprecated
public interface BaseMultivariateSimpleBoundsOptimizer<FUNC extends MultivariateFunction> extends BaseMultivariateOptimizer<FUNC> {
    PointValuePair optimize(int i, FUNC func, GoalType goalType, double[] dArr, double[] dArr2, double[] dArr3);
}
