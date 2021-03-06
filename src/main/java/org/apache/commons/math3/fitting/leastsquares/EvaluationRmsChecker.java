package org.apache.commons.math3.fitting.leastsquares;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.util.Precision;

public class EvaluationRmsChecker implements ConvergenceChecker<LeastSquaresProblem.Evaluation> {
    private final double absTol;
    private final double relTol;

    public EvaluationRmsChecker(double tol) {
        this(tol, tol);
    }

    public EvaluationRmsChecker(double relTol2, double absTol2) {
        this.relTol = relTol2;
        this.absTol = absTol2;
    }

    public boolean converged(int iteration, LeastSquaresProblem.Evaluation previous, LeastSquaresProblem.Evaluation current) {
        double prevRms = previous.getRMS();
        double currRms = current.getRMS();
        return Precision.equals(prevRms, currRms, this.absTol) || Precision.equalsWithRelativeTolerance(prevRms, currRms, this.relTol);
    }
}
