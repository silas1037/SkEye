package org.apache.commons.math3.ode;

public interface SecondOrderDifferentialEquations {
    void computeSecondDerivatives(double d, double[] dArr, double[] dArr2, double[] dArr3);

    int getDimension();
}
