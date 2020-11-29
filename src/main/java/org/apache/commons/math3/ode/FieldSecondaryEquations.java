package org.apache.commons.math3.ode;

import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;

public interface FieldSecondaryEquations<T extends RealFieldElement<T>> {
    T[] computeDerivatives(T t, T[] tArr, T[] tArr2, T[] tArr3) throws MaxCountExceededException, DimensionMismatchException;

    int getDimension();

    void init(T t, T[] tArr, T[] tArr2, T t2);
}
