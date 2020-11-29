package org.apache.commons.math3.optimization.general;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.BrentSolver;
import org.apache.commons.math3.analysis.solvers.UnivariateSolver;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.optimization.ConvergenceChecker;
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.optimization.SimpleValueChecker;
import org.apache.commons.math3.util.FastMath;

@Deprecated
public class NonLinearConjugateGradientOptimizer extends AbstractScalarDifferentiableOptimizer {
    private double initialStep;
    private double[] point;
    private final Preconditioner preconditioner;
    private final UnivariateSolver solver;
    private final ConjugateGradientFormula updateFormula;

    @Deprecated
    public NonLinearConjugateGradientOptimizer(ConjugateGradientFormula updateFormula2) {
        this(updateFormula2, new SimpleValueChecker());
    }

    public NonLinearConjugateGradientOptimizer(ConjugateGradientFormula updateFormula2, ConvergenceChecker<PointValuePair> checker) {
        this(updateFormula2, checker, new BrentSolver(), new IdentityPreconditioner());
    }

    public NonLinearConjugateGradientOptimizer(ConjugateGradientFormula updateFormula2, ConvergenceChecker<PointValuePair> checker, UnivariateSolver lineSearchSolver) {
        this(updateFormula2, checker, lineSearchSolver, new IdentityPreconditioner());
    }

    public NonLinearConjugateGradientOptimizer(ConjugateGradientFormula updateFormula2, ConvergenceChecker<PointValuePair> checker, UnivariateSolver lineSearchSolver, Preconditioner preconditioner2) {
        super(checker);
        this.updateFormula = updateFormula2;
        this.solver = lineSearchSolver;
        this.preconditioner = preconditioner2;
        this.initialStep = 1.0d;
    }

    public void setInitialStep(double initialStep2) {
        if (initialStep2 <= 0.0d) {
            this.initialStep = 1.0d;
        } else {
            this.initialStep = initialStep2;
        }
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.optimization.direct.BaseAbstractMultivariateOptimizer
    public PointValuePair doOptimize() {
        double beta;
        ConvergenceChecker<PointValuePair> checker = getConvergenceChecker();
        this.point = getStartPoint();
        GoalType goal = getGoalType();
        int n = this.point.length;
        double[] r = computeObjectiveGradient(this.point);
        if (goal == GoalType.MINIMIZE) {
            for (int i = 0; i < n; i++) {
                r[i] = -r[i];
            }
        }
        double[] steepestDescent = this.preconditioner.precondition(this.point, r);
        double[] searchDirection = (double[]) steepestDescent.clone();
        double delta = 0.0d;
        for (int i2 = 0; i2 < n; i2++) {
            delta += r[i2] * searchDirection[i2];
        }
        PointValuePair current = null;
        int iter = 0;
        int maxEval = getMaxEvaluations();
        while (true) {
            iter++;
            current = new PointValuePair(this.point, computeObjectiveValue(this.point));
            if (current != null && checker.converged(iter, current, current)) {
                return current;
            }
            LineSearchFunction lineSearchFunction = new LineSearchFunction(searchDirection);
            double step = this.solver.solve(maxEval, lineSearchFunction, 0.0d, findUpperBound(lineSearchFunction, 0.0d, this.initialStep), 1.0E-15d);
            maxEval -= this.solver.getEvaluations();
            for (int i3 = 0; i3 < this.point.length; i3++) {
                double[] dArr = this.point;
                dArr[i3] = dArr[i3] + (searchDirection[i3] * step);
            }
            double[] r2 = computeObjectiveGradient(this.point);
            if (goal == GoalType.MINIMIZE) {
                for (int i4 = 0; i4 < n; i4++) {
                    r2[i4] = -r2[i4];
                }
            }
            double[] newSteepestDescent = this.preconditioner.precondition(this.point, r2);
            delta = 0.0d;
            for (int i5 = 0; i5 < n; i5++) {
                delta += r2[i5] * newSteepestDescent[i5];
            }
            if (this.updateFormula == ConjugateGradientFormula.FLETCHER_REEVES) {
                beta = delta / delta;
            } else {
                double deltaMid = 0.0d;
                for (int i6 = 0; i6 < r2.length; i6++) {
                    deltaMid += r2[i6] * steepestDescent[i6];
                }
                beta = (delta - deltaMid) / delta;
            }
            steepestDescent = newSteepestDescent;
            if (iter % n == 0 || beta < 0.0d) {
                searchDirection = (double[]) steepestDescent.clone();
            } else {
                for (int i7 = 0; i7 < n; i7++) {
                    searchDirection[i7] = steepestDescent[i7] + (searchDirection[i7] * beta);
                }
            }
        }
    }

    private double findUpperBound(UnivariateFunction f, double a, double h) {
        double yA = f.value(a);
        double step = h;
        while (step < Double.MAX_VALUE) {
            double b = a + step;
            double yB = f.value(b);
            if (yA * yB <= 0.0d) {
                return b;
            }
            step *= FastMath.max(2.0d, yA / yB);
        }
        throw new MathIllegalStateException(LocalizedFormats.UNABLE_TO_BRACKET_OPTIMUM_IN_LINE_SEARCH, new Object[0]);
    }

    public static class IdentityPreconditioner implements Preconditioner {
        @Override // org.apache.commons.math3.optimization.general.Preconditioner
        public double[] precondition(double[] variables, double[] r) {
            return (double[]) r.clone();
        }
    }

    private class LineSearchFunction implements UnivariateFunction {
        private final double[] searchDirection;

        LineSearchFunction(double[] searchDirection2) {
            this.searchDirection = searchDirection2;
        }

        @Override // org.apache.commons.math3.analysis.UnivariateFunction
        public double value(double x) {
            double[] shiftedPoint = (double[]) NonLinearConjugateGradientOptimizer.this.point.clone();
            for (int i = 0; i < shiftedPoint.length; i++) {
                shiftedPoint[i] = shiftedPoint[i] + (this.searchDirection[i] * x);
            }
            double[] gradient = NonLinearConjugateGradientOptimizer.this.computeObjectiveGradient(shiftedPoint);
            double dotProduct = 0.0d;
            for (int i2 = 0; i2 < gradient.length; i2++) {
                dotProduct += gradient[i2] * this.searchDirection[i2];
            }
            return dotProduct;
        }
    }
}
