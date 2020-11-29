package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;

public interface MultivariateRealDistribution {
    double density(double[] dArr);

    int getDimension();

    void reseedRandomGenerator(long j);

    double[] sample();

    double[][] sample(int i) throws NotStrictlyPositiveException;
}
