package org.apache.commons.math3.optim.nonlinear.scalar.gradient;

import org.apache.commons.math3.analysis.solvers.UnivariateSolver;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.exception.MathUnsupportedOperationException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.GradientMultivariateOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.LineSearch;

public class NonLinearConjugateGradientOptimizer extends GradientMultivariateOptimizer {
    private final LineSearch line;
    private final Preconditioner preconditioner;
    private final Formula updateFormula;

    public enum Formula {
        FLETCHER_REEVES,
        POLAK_RIBIERE
    }

    @Deprecated
    public static class BracketingStep implements OptimizationData {
        private final double initialStep;

        public BracketingStep(double step) {
            this.initialStep = step;
        }

        public double getBracketingStep() {
            return this.initialStep;
        }
    }

    public NonLinearConjugateGradientOptimizer(Formula updateFormula2, ConvergenceChecker<PointValuePair> checker) {
        this(updateFormula2, checker, 1.0E-8d, 1.0E-8d, 1.0E-8d, new IdentityPreconditioner());
    }

    @Deprecated
    public NonLinearConjugateGradientOptimizer(Formula updateFormula2, ConvergenceChecker<PointValuePair> checker, UnivariateSolver lineSearchSolver) {
        this(updateFormula2, checker, lineSearchSolver, new IdentityPreconditioner());
    }

    public NonLinearConjugateGradientOptimizer(Formula updateFormula2, ConvergenceChecker<PointValuePair> checker, double relativeTolerance, double absoluteTolerance, double initialBracketingRange) {
        this(updateFormula2, checker, relativeTolerance, absoluteTolerance, initialBracketingRange, new IdentityPreconditioner());
    }

    @Deprecated
    public NonLinearConjugateGradientOptimizer(Formula updateFormula2, ConvergenceChecker<PointValuePair> checker, UnivariateSolver lineSearchSolver, Preconditioner preconditioner2) {
        this(updateFormula2, checker, lineSearchSolver.getRelativeAccuracy(), lineSearchSolver.getAbsoluteAccuracy(), lineSearchSolver.getAbsoluteAccuracy(), preconditioner2);
    }

    public NonLinearConjugateGradientOptimizer(Formula updateFormula2, ConvergenceChecker<PointValuePair> checker, double relativeTolerance, double absoluteTolerance, double initialBracketingRange, Preconditioner preconditioner2) {
        super(checker);
        this.updateFormula = updateFormula2;
        this.preconditioner = preconditioner2;
        this.line = new LineSearch(this, relativeTolerance, absoluteTolerance, initialBracketingRange);
    }

    @Override // org.apache.commons.math3.optim.nonlinear.scalar.GradientMultivariateOptimizer, org.apache.commons.math3.optim.nonlinear.scalar.GradientMultivariateOptimizer, org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer, org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer, org.apache.commons.math3.optim.BaseOptimizer, org.apache.commons.math3.optim.BaseMultivariateOptimizer
    public PointValuePair optimize(OptimizationData... optData) throws TooManyEvaluationsException {
        return super.optimize(optData);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.optim.BaseOptimizer
    public PointValuePair doOptimize() {
        double beta;
        ConvergenceChecker<PointValuePair> checker = getConvergenceChecker();
        double[] point = getStartPoint();
        GoalType goal = getGoalType();
        int n = point.length;
        double[] r = computeObjectiveGradient(point);
        if (goal == GoalType.MINIMIZE) {
            for (int i = 0; i < n; i++) {
                r[i] = -r[i];
            }
        }
        double[] steepestDescent = this.preconditioner.precondition(point, r);
        double[] searchDirection = (double[]) steepestDescent.clone();
        double delta = 0.0d;
        for (int i2 = 0; i2 < n; i2++) {
            delta += r[i2] * searchDirection[i2];
        }
        PointValuePair current = null;
        while (true) {
            incrementIterationCount();
            current = new PointValuePair(point, computeObjectiveValue(point));
            if (current != null && checker.converged(getIterations(), current, current)) {
                return current;
            }
            double step = this.line.search(point, searchDirection).getPoint();
            for (int i3 = 0; i3 < point.length; i3++) {
                point[i3] = point[i3] + (searchDirection[i3] * step);
            }
            double[] r2 = computeObjectiveGradient(point);
            if (goal == GoalType.MINIMIZE) {
                for (int i4 = 0; i4 < n; i4++) {
                    r2[i4] = -r2[i4];
                }
            }
            double[] newSteepestDescent = this.preconditioner.precondition(point, r2);
            delta = 0.0d;
            for (int i5 = 0; i5 < n; i5++) {
                delta += r2[i5] * newSteepestDescent[i5];
            }
            switch (this.updateFormula) {
                case FLETCHER_REEVES:
                    beta = delta / delta;
                    break;
                case POLAK_RIBIERE:
                    double deltaMid = 0.0d;
                    for (int i6 = 0; i6 < r2.length; i6++) {
                        deltaMid += r2[i6] * steepestDescent[i6];
                    }
                    beta = (delta - deltaMid) / delta;
                    break;
                default:
                    throw new MathInternalError();
            }
            steepestDescent = newSteepestDescent;
            if (getIterations() % n == 0 || beta < 0.0d) {
                searchDirection = (double[]) steepestDescent.clone();
            } else {
                for (int i7 = 0; i7 < n; i7++) {
                    searchDirection[i7] = steepestDescent[i7] + (searchDirection[i7] * beta);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.optim.nonlinear.scalar.GradientMultivariateOptimizer, org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer, org.apache.commons.math3.optim.BaseOptimizer, org.apache.commons.math3.optim.BaseMultivariateOptimizer
    public void parseOptimizationData(OptimizationData... optData) {
        super.parseOptimizationData(optData);
        checkParameters();
    }

    public static class IdentityPreconditioner implements Preconditioner {
        @Override // org.apache.commons.math3.optim.nonlinear.scalar.gradient.Preconditioner
        public double[] precondition(double[] variables, double[] r) {
            return (double[]) r.clone();
        }
    }

    private void checkParameters() {
        if (getLowerBound() != null || getUpperBound() != null) {
            throw new MathUnsupportedOperationException(LocalizedFormats.CONSTRAINT, new Object[0]);
        }
    }
}
