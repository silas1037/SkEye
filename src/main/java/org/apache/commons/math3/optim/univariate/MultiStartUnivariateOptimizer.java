package org.apache.commons.math3.optim.univariate;

import java.util.Arrays;
import java.util.Comparator;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.random.RandomGenerator;

public class MultiStartUnivariateOptimizer extends UnivariateOptimizer {
    private RandomGenerator generator;
    private int maxEvalIndex = -1;
    private OptimizationData[] optimData;
    private UnivariatePointValuePair[] optima;
    private final UnivariateOptimizer optimizer;
    private int searchIntervalIndex = -1;
    private int starts;
    private int totalEvaluations;

    public MultiStartUnivariateOptimizer(UnivariateOptimizer optimizer2, int starts2, RandomGenerator generator2) {
        super(optimizer2.getConvergenceChecker());
        if (starts2 < 1) {
            throw new NotStrictlyPositiveException(Integer.valueOf(starts2));
        }
        this.optimizer = optimizer2;
        this.starts = starts2;
        this.generator = generator2;
    }

    @Override // org.apache.commons.math3.optim.BaseOptimizer
    public int getEvaluations() {
        return this.totalEvaluations;
    }

    public UnivariatePointValuePair[] getOptima() {
        if (this.optima != null) {
            return (UnivariatePointValuePair[]) this.optima.clone();
        }
        throw new MathIllegalStateException(LocalizedFormats.NO_OPTIMUM_COMPUTED_YET, new Object[0]);
    }

    @Override // org.apache.commons.math3.optim.univariate.UnivariateOptimizer, org.apache.commons.math3.optim.univariate.UnivariateOptimizer, org.apache.commons.math3.optim.BaseOptimizer
    public UnivariatePointValuePair optimize(OptimizationData... optData) {
        this.optimData = optData;
        return super.optimize(optData);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.optim.BaseOptimizer
    public UnivariatePointValuePair doOptimize() {
        for (int i = 0; i < this.optimData.length; i++) {
            if (this.optimData[i] instanceof MaxEval) {
                this.optimData[i] = null;
                this.maxEvalIndex = i;
            } else if (this.optimData[i] instanceof SearchInterval) {
                this.optimData[i] = null;
                this.searchIntervalIndex = i;
            }
        }
        if (this.maxEvalIndex == -1) {
            throw new MathIllegalStateException();
        } else if (this.searchIntervalIndex == -1) {
            throw new MathIllegalStateException();
        } else {
            RuntimeException lastException = null;
            this.optima = new UnivariatePointValuePair[this.starts];
            this.totalEvaluations = 0;
            int maxEval = getMaxEvaluations();
            double min = getMin();
            double max = getMax();
            double startValue = getStartValue();
            int i2 = 0;
            while (i2 < this.starts) {
                try {
                    this.optimData[this.maxEvalIndex] = new MaxEval(maxEval - this.totalEvaluations);
                    this.optimData[this.searchIntervalIndex] = new SearchInterval(min, max, i2 == 0 ? startValue : min + (this.generator.nextDouble() * (max - min)));
                    this.optima[i2] = this.optimizer.optimize(this.optimData);
                } catch (RuntimeException mue) {
                    lastException = mue;
                    this.optima[i2] = null;
                }
                this.totalEvaluations += this.optimizer.getEvaluations();
                i2++;
            }
            sortPairs(getGoalType());
            if (this.optima[0] != null) {
                return this.optima[0];
            }
            throw lastException;
        }
    }

    private void sortPairs(final GoalType goal) {
        Arrays.sort(this.optima, new Comparator<UnivariatePointValuePair>() {
            /* class org.apache.commons.math3.optim.univariate.MultiStartUnivariateOptimizer.C03141 */

            public int compare(UnivariatePointValuePair o1, UnivariatePointValuePair o2) {
                if (o1 == null) {
                    return o2 == null ? 0 : 1;
                }
                if (o2 == null) {
                    return -1;
                }
                double v1 = o1.getValue();
                double v2 = o2.getValue();
                return goal == GoalType.MINIMIZE ? Double.compare(v1, v2) : Double.compare(v2, v1);
            }
        });
    }
}
