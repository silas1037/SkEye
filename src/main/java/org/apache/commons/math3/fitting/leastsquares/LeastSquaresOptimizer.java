package org.apache.commons.math3.fitting.leastsquares;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;

public interface LeastSquaresOptimizer {

    public interface Optimum extends LeastSquaresProblem.Evaluation {
        int getEvaluations();

        int getIterations();
    }

    Optimum optimize(LeastSquaresProblem leastSquaresProblem);
}
