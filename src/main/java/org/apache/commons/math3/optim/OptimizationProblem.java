package org.apache.commons.math3.optim;

import org.apache.commons.math3.util.Incrementor;

public interface OptimizationProblem<PAIR> {
    ConvergenceChecker<PAIR> getConvergenceChecker();

    Incrementor getEvaluationCounter();

    Incrementor getIterationCounter();
}
