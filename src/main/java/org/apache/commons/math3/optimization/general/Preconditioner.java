package org.apache.commons.math3.optimization.general;

@Deprecated
public interface Preconditioner {
    double[] precondition(double[] dArr, double[] dArr2);
}
