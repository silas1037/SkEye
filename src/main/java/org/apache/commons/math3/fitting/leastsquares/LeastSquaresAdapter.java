package org.apache.commons.math3.fitting.leastsquares;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.util.Incrementor;

public class LeastSquaresAdapter implements LeastSquaresProblem {
    private final LeastSquaresProblem problem;

    public LeastSquaresAdapter(LeastSquaresProblem problem2) {
        this.problem = problem2;
    }

    @Override // org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem
    public RealVector getStart() {
        return this.problem.getStart();
    }

    @Override // org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem
    public int getObservationSize() {
        return this.problem.getObservationSize();
    }

    @Override // org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem
    public int getParameterSize() {
        return this.problem.getParameterSize();
    }

    @Override // org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem
    public LeastSquaresProblem.Evaluation evaluate(RealVector point) {
        return this.problem.evaluate(point);
    }

    @Override // org.apache.commons.math3.optim.OptimizationProblem
    public Incrementor getEvaluationCounter() {
        return this.problem.getEvaluationCounter();
    }

    @Override // org.apache.commons.math3.optim.OptimizationProblem
    public Incrementor getIterationCounter() {
        return this.problem.getIterationCounter();
    }

    @Override // org.apache.commons.math3.optim.OptimizationProblem
    public ConvergenceChecker<LeastSquaresProblem.Evaluation> getConvergenceChecker() {
        return this.problem.getConvergenceChecker();
    }
}
