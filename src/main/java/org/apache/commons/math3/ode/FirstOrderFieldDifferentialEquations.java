package org.apache.commons.math3.ode;

import org.apache.commons.math3.RealFieldElement;

public interface FirstOrderFieldDifferentialEquations<T extends RealFieldElement<T>> {
    T[] computeDerivatives(T t, T[] tArr);

    int getDimension();

    void init(T t, T[] tArr, T t2);
}
