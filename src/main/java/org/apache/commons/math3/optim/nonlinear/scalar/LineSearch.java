package org.apache.commons.math3.optim.nonlinear.scalar;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.BaseAbstractUnivariateIntegrator;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.univariate.BracketFinder;
import org.apache.commons.math3.optim.univariate.BrentOptimizer;
import org.apache.commons.math3.optim.univariate.SearchInterval;
import org.apache.commons.math3.optim.univariate.SimpleUnivariateValueChecker;
import org.apache.commons.math3.optim.univariate.UnivariateObjectiveFunction;
import org.apache.commons.math3.optim.univariate.UnivariateOptimizer;
import org.apache.commons.math3.optim.univariate.UnivariatePointValuePair;

public class LineSearch {
    private static final double ABS_TOL_UNUSED = Double.MIN_VALUE;
    private static final double REL_TOL_UNUSED = 1.0E-15d;
    private final BracketFinder bracket = new BracketFinder();
    private final double initialBracketingRange;
    private final UnivariateOptimizer lineOptimizer;
    private final MultivariateOptimizer mainOptimizer;

    public LineSearch(MultivariateOptimizer optimizer, double relativeTolerance, double absoluteTolerance, double initialBracketingRange2) {
        this.mainOptimizer = optimizer;
        this.lineOptimizer = new BrentOptimizer(1.0E-15d, ABS_TOL_UNUSED, new SimpleUnivariateValueChecker(relativeTolerance, absoluteTolerance));
        this.initialBracketingRange = initialBracketingRange2;
    }

    public UnivariatePointValuePair search(final double[] startPoint, final double[] direction) {
        final int n = startPoint.length;
        UnivariateFunction f = new UnivariateFunction() {
            /* class org.apache.commons.math3.optim.nonlinear.scalar.LineSearch.C03071 */

            @Override // org.apache.commons.math3.analysis.UnivariateFunction
            public double value(double alpha) {
                double[] x = new double[n];
                for (int i = 0; i < n; i++) {
                    x[i] = startPoint[i] + (direction[i] * alpha);
                }
                return LineSearch.this.mainOptimizer.computeObjectiveValue(x);
            }
        };
        GoalType goal = this.mainOptimizer.getGoalType();
        this.bracket.search(f, goal, 0.0d, this.initialBracketingRange);
        return this.lineOptimizer.optimize(new MaxEval(BaseAbstractUnivariateIntegrator.DEFAULT_MAX_ITERATIONS_COUNT), new UnivariateObjectiveFunction(f), goal, new SearchInterval(this.bracket.getLo(), this.bracket.getHi(), this.bracket.getMid()));
    }
}
