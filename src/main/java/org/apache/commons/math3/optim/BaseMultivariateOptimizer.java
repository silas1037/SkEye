package org.apache.commons.math3.optim;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;

public abstract class BaseMultivariateOptimizer<PAIR> extends BaseOptimizer<PAIR> {
    private double[] lowerBound;
    private double[] start;
    private double[] upperBound;

    protected BaseMultivariateOptimizer(ConvergenceChecker<PAIR> checker) {
        super(checker);
    }

    @Override // org.apache.commons.math3.optim.BaseOptimizer
    public PAIR optimize(OptimizationData... optData) {
        return (PAIR) super.optimize(optData);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.optim.BaseOptimizer
    public void parseOptimizationData(OptimizationData... optData) {
        super.parseOptimizationData(optData);
        for (OptimizationData data : optData) {
            if (data instanceof InitialGuess) {
                this.start = ((InitialGuess) data).getInitialGuess();
            } else if (data instanceof SimpleBounds) {
                SimpleBounds bounds = (SimpleBounds) data;
                this.lowerBound = bounds.getLower();
                this.upperBound = bounds.getUpper();
            }
        }
        checkParameters();
    }

    public double[] getStartPoint() {
        if (this.start == null) {
            return null;
        }
        return (double[]) this.start.clone();
    }

    public double[] getLowerBound() {
        if (this.lowerBound == null) {
            return null;
        }
        return (double[]) this.lowerBound.clone();
    }

    public double[] getUpperBound() {
        if (this.upperBound == null) {
            return null;
        }
        return (double[]) this.upperBound.clone();
    }

    private void checkParameters() {
        if (this.start != null) {
            int dim = this.start.length;
            if (this.lowerBound != null) {
                if (this.lowerBound.length != dim) {
                    throw new DimensionMismatchException(this.lowerBound.length, dim);
                }
                for (int i = 0; i < dim; i++) {
                    double v = this.start[i];
                    double lo = this.lowerBound[i];
                    if (v < lo) {
                        throw new NumberIsTooSmallException(Double.valueOf(v), Double.valueOf(lo), true);
                    }
                }
            }
            if (this.upperBound == null) {
                return;
            }
            if (this.upperBound.length != dim) {
                throw new DimensionMismatchException(this.upperBound.length, dim);
            }
            for (int i2 = 0; i2 < dim; i2++) {
                double v2 = this.start[i2];
                double hi = this.upperBound[i2];
                if (v2 > hi) {
                    throw new NumberIsTooLargeException(Double.valueOf(v2), Double.valueOf(hi), true);
                }
            }
        }
    }
}
