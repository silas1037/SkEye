package org.apache.commons.math3.optimization;

import java.util.Arrays;
import java.util.Comparator;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomVectorGenerator;

@Deprecated
public class BaseMultivariateVectorMultiStartOptimizer<FUNC extends MultivariateVectorFunction> implements BaseMultivariateVectorOptimizer<FUNC> {
    private RandomVectorGenerator generator;
    private int maxEvaluations;
    private PointVectorValuePair[] optima;
    private final BaseMultivariateVectorOptimizer<FUNC> optimizer;
    private int starts;
    private int totalEvaluations;

    protected BaseMultivariateVectorMultiStartOptimizer(BaseMultivariateVectorOptimizer<FUNC> optimizer2, int starts2, RandomVectorGenerator generator2) {
        if (optimizer2 == null || generator2 == null) {
            throw new NullArgumentException();
        } else if (starts2 < 1) {
            throw new NotStrictlyPositiveException(Integer.valueOf(starts2));
        } else {
            this.optimizer = optimizer2;
            this.starts = starts2;
            this.generator = generator2;
        }
    }

    public PointVectorValuePair[] getOptima() {
        if (this.optima != null) {
            return (PointVectorValuePair[]) this.optima.clone();
        }
        throw new MathIllegalStateException(LocalizedFormats.NO_OPTIMUM_COMPUTED_YET, new Object[0]);
    }

    @Override // org.apache.commons.math3.optimization.BaseOptimizer
    public int getMaxEvaluations() {
        return this.maxEvaluations;
    }

    @Override // org.apache.commons.math3.optimization.BaseOptimizer
    public int getEvaluations() {
        return this.totalEvaluations;
    }

    @Override // org.apache.commons.math3.optimization.BaseOptimizer
    public ConvergenceChecker<PointVectorValuePair> getConvergenceChecker() {
        return this.optimizer.getConvergenceChecker();
    }

    @Override // org.apache.commons.math3.optimization.BaseMultivariateVectorOptimizer
    public PointVectorValuePair optimize(int maxEval, FUNC f, double[] target, double[] weights, double[] startPoint) {
        this.maxEvaluations = maxEval;
        RuntimeException lastException = null;
        this.optima = new PointVectorValuePair[this.starts];
        this.totalEvaluations = 0;
        int i = 0;
        while (i < this.starts) {
            try {
                this.optima[i] = this.optimizer.optimize(maxEval - this.totalEvaluations, f, target, weights, i == 0 ? startPoint : this.generator.nextVector());
            } catch (ConvergenceException e) {
                this.optima[i] = null;
            } catch (RuntimeException mue) {
                lastException = mue;
                this.optima[i] = null;
            }
            this.totalEvaluations += this.optimizer.getEvaluations();
            i++;
        }
        sortPairs(target, weights);
        if (this.optima[0] != null) {
            return this.optima[0];
        }
        throw lastException;
    }

    private void sortPairs(final double[] target, final double[] weights) {
        Arrays.sort(this.optima, new Comparator<PointVectorValuePair>() {
            /* class org.apache.commons.math3.optimization.BaseMultivariateVectorMultiStartOptimizer.C03161 */

            public int compare(PointVectorValuePair o1, PointVectorValuePair o2) {
                if (o1 == null) {
                    return o2 == null ? 0 : 1;
                }
                if (o2 == null) {
                    return -1;
                }
                return Double.compare(weightedResidual(o1), weightedResidual(o2));
            }

            private double weightedResidual(PointVectorValuePair pv) {
                double[] value = pv.getValueRef();
                double sum = 0.0d;
                for (int i = 0; i < value.length; i++) {
                    double ri = value[i] - target[i];
                    sum += weights[i] * ri * ri;
                }
                return sum;
            }
        });
    }
}
