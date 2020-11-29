package org.apache.commons.math3.optim;

import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.random.RandomVectorGenerator;

public abstract class BaseMultiStartMultivariateOptimizer<PAIR> extends BaseMultivariateOptimizer<PAIR> {
    private RandomVectorGenerator generator;
    private int initialGuessIndex = -1;
    private int maxEvalIndex = -1;
    private OptimizationData[] optimData;
    private final BaseMultivariateOptimizer<PAIR> optimizer;
    private int starts;
    private int totalEvaluations;

    /* access modifiers changed from: protected */
    public abstract void clear();

    public abstract PAIR[] getOptima();

    /* access modifiers changed from: protected */
    public abstract void store(PAIR pair);

    public BaseMultiStartMultivariateOptimizer(BaseMultivariateOptimizer<PAIR> optimizer2, int starts2, RandomVectorGenerator generator2) {
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

    @Override // org.apache.commons.math3.optim.BaseOptimizer, org.apache.commons.math3.optim.BaseMultivariateOptimizer
    public PAIR optimize(OptimizationData... optData) {
        this.optimData = optData;
        return (PAIR) super.optimize(optData);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.optim.BaseOptimizer
    public PAIR doOptimize() {
        for (int i = 0; i < this.optimData.length; i++) {
            if (this.optimData[i] instanceof MaxEval) {
                this.optimData[i] = null;
                this.maxEvalIndex = i;
            }
            if (this.optimData[i] instanceof InitialGuess) {
                this.optimData[i] = null;
                this.initialGuessIndex = i;
            }
        }
        if (this.maxEvalIndex == -1) {
            throw new MathIllegalStateException();
        } else if (this.initialGuessIndex == -1) {
            throw new MathIllegalStateException();
        } else {
            RuntimeException lastException = null;
            this.totalEvaluations = 0;
            clear();
            int maxEval = getMaxEvaluations();
            double[] min = getLowerBound();
            double[] max = getUpperBound();
            double[] startPoint = getStartPoint();
            for (int i2 = 0; i2 < this.starts; i2++) {
                try {
                    this.optimData[this.maxEvalIndex] = new MaxEval(maxEval - this.totalEvaluations);
                    double[] s = null;
                    if (i2 == 0) {
                        s = startPoint;
                    } else {
                        int attempts = 0;
                        while (s == null) {
                            int attempts2 = attempts + 1;
                            if (attempts >= getMaxEvaluations()) {
                                throw new TooManyEvaluationsException(Integer.valueOf(getMaxEvaluations()));
                            }
                            s = this.generator.nextVector();
                            int k = 0;
                            while (s != null && k < s.length) {
                                if ((min != null && s[k] < min[k]) || (max != null && s[k] > max[k])) {
                                    s = null;
                                }
                                k++;
                            }
                            attempts = attempts2;
                        }
                    }
                    this.optimData[this.initialGuessIndex] = new InitialGuess(s);
                    store(this.optimizer.optimize(this.optimData));
                } catch (RuntimeException mue) {
                    lastException = mue;
                }
                this.totalEvaluations += this.optimizer.getEvaluations();
            }
            PAIR[] optima = getOptima();
            if (optima.length != 0) {
                return optima[0];
            }
            throw lastException;
        }
    }
}
