package org.apache.commons.math3.ode.sampling;

import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;

public interface FieldStepHandler<T extends RealFieldElement<T>> {
    void handleStep(FieldStepInterpolator<T> fieldStepInterpolator, boolean z) throws MaxCountExceededException;

    void init(FieldODEStateAndDerivative<T> fieldODEStateAndDerivative, T t);
}
