package org.apache.commons.math3.optimization;

@Deprecated
public interface BaseOptimizer<PAIR> {
    ConvergenceChecker<PAIR> getConvergenceChecker();

    int getEvaluations();

    int getMaxEvaluations();
}
