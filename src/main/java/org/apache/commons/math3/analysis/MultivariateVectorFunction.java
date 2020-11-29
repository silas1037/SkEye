package org.apache.commons.math3.analysis;

public interface MultivariateVectorFunction {
    double[] value(double[] dArr) throws IllegalArgumentException;
}
