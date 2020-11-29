package org.apache.commons.math3.optim.nonlinear.scalar.noderiv;

import java.util.Comparator;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.exception.MathUnsupportedOperationException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.SimpleValueChecker;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer;

public class SimplexOptimizer extends MultivariateOptimizer {
    private AbstractSimplex simplex;

    public SimplexOptimizer(ConvergenceChecker<PointValuePair> checker) {
        super(checker);
    }

    public SimplexOptimizer(double rel, double abs) {
        this(new SimpleValueChecker(rel, abs));
    }

    @Override // org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer, org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer, org.apache.commons.math3.optim.BaseOptimizer, org.apache.commons.math3.optim.BaseMultivariateOptimizer
    public PointValuePair optimize(OptimizationData... optData) {
        return super.optimize(optData);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.optim.BaseOptimizer
    public PointValuePair doOptimize() {
        checkParameters();
        MultivariateFunction evalFunc = new MultivariateFunction() {
            /* class org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer.C03111 */

            @Override // org.apache.commons.math3.analysis.MultivariateFunction
            public double value(double[] point) {
                return SimplexOptimizer.this.computeObjectiveValue(point);
            }
        };
        final boolean isMinim = getGoalType() == GoalType.MINIMIZE;
        Comparator<PointValuePair> comparator = new Comparator<PointValuePair>() {
            /* class org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer.C03122 */

            public int compare(PointValuePair o1, PointValuePair o2) {
                double v1 = ((Double) o1.getValue()).doubleValue();
                double v2 = ((Double) o2.getValue()).doubleValue();
                return isMinim ? Double.compare(v1, v2) : Double.compare(v2, v1);
            }
        };
        this.simplex.build(getStartPoint());
        this.simplex.evaluate(evalFunc, comparator);
        PointValuePair[] previous = null;
        ConvergenceChecker<PointValuePair> checker = getConvergenceChecker();
        while (true) {
            if (getIterations() > 0) {
                boolean converged = true;
                for (int i = 0; i < this.simplex.getSize(); i++) {
                    converged = converged && checker.converged(0, previous[i], this.simplex.getPoint(i));
                }
                if (converged) {
                    return this.simplex.getPoint(0);
                }
            }
            previous = this.simplex.getPoints();
            this.simplex.iterate(evalFunc, comparator);
            incrementIterationCount();
        }
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer, org.apache.commons.math3.optim.BaseOptimizer, org.apache.commons.math3.optim.BaseMultivariateOptimizer
    public void parseOptimizationData(OptimizationData... optData) {
        super.parseOptimizationData(optData);
        for (OptimizationData data : optData) {
            if (data instanceof AbstractSimplex) {
                this.simplex = (AbstractSimplex) data;
                return;
            }
        }
    }

    private void checkParameters() {
        if (this.simplex == null) {
            throw new NullArgumentException();
        } else if (getLowerBound() != null || getUpperBound() != null) {
            throw new MathUnsupportedOperationException(LocalizedFormats.CONSTRAINT, new Object[0]);
        }
    }
}
