package org.apache.commons.math3.optim;

import org.apache.commons.math3.analysis.integration.BaseAbstractUnivariateIntegrator;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;

public class MaxIter implements OptimizationData {
    private final int maxIter;

    public MaxIter(int max) {
        if (max <= 0) {
            throw new NotStrictlyPositiveException(Integer.valueOf(max));
        }
        this.maxIter = max;
    }

    public int getMaxIter() {
        return this.maxIter;
    }

    public static MaxIter unlimited() {
        return new MaxIter(BaseAbstractUnivariateIntegrator.DEFAULT_MAX_ITERATIONS_COUNT);
    }
}
