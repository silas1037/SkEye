package org.apache.commons.math3.fitting.leastsquares;

import org.apache.commons.math3.analysis.MultivariateMatrixFunction;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.optim.PointVectorValuePair;

public class LeastSquaresBuilder {
    private ConvergenceChecker<LeastSquaresProblem.Evaluation> checker;
    private boolean lazyEvaluation;
    private int maxEvaluations;
    private int maxIterations;
    private MultivariateJacobianFunction model;
    private ParameterValidator paramValidator;
    private RealVector start;
    private RealVector target;
    private RealMatrix weight;

    public LeastSquaresProblem build() {
        return LeastSquaresFactory.create(this.model, this.target, this.start, this.weight, this.checker, this.maxEvaluations, this.maxIterations, this.lazyEvaluation, this.paramValidator);
    }

    public LeastSquaresBuilder maxEvaluations(int newMaxEvaluations) {
        this.maxEvaluations = newMaxEvaluations;
        return this;
    }

    public LeastSquaresBuilder maxIterations(int newMaxIterations) {
        this.maxIterations = newMaxIterations;
        return this;
    }

    public LeastSquaresBuilder checker(ConvergenceChecker<LeastSquaresProblem.Evaluation> newChecker) {
        this.checker = newChecker;
        return this;
    }

    public LeastSquaresBuilder checkerPair(ConvergenceChecker<PointVectorValuePair> newChecker) {
        return checker(LeastSquaresFactory.evaluationChecker(newChecker));
    }

    public LeastSquaresBuilder model(MultivariateVectorFunction value, MultivariateMatrixFunction jacobian) {
        return model(LeastSquaresFactory.model(value, jacobian));
    }

    public LeastSquaresBuilder model(MultivariateJacobianFunction newModel) {
        this.model = newModel;
        return this;
    }

    public LeastSquaresBuilder target(RealVector newTarget) {
        this.target = newTarget;
        return this;
    }

    public LeastSquaresBuilder target(double[] newTarget) {
        return target(new ArrayRealVector(newTarget, false));
    }

    public LeastSquaresBuilder start(RealVector newStart) {
        this.start = newStart;
        return this;
    }

    public LeastSquaresBuilder start(double[] newStart) {
        return start(new ArrayRealVector(newStart, false));
    }

    public LeastSquaresBuilder weight(RealMatrix newWeight) {
        this.weight = newWeight;
        return this;
    }

    public LeastSquaresBuilder lazyEvaluation(boolean newValue) {
        this.lazyEvaluation = newValue;
        return this;
    }

    public LeastSquaresBuilder parameterValidator(ParameterValidator newValidator) {
        this.paramValidator = newValidator;
        return this;
    }
}
