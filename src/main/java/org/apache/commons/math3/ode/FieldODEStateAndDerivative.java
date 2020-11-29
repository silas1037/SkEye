package org.apache.commons.math3.ode;

import org.apache.commons.math3.RealFieldElement;

public class FieldODEStateAndDerivative<T extends RealFieldElement<T>> extends FieldODEState<T> {
    private final T[] derivative;
    private final T[][] secondaryDerivative;

    public FieldODEStateAndDerivative(T time, T[] state, T[] derivative2) {
        this(time, state, derivative2, null, null);
    }

    public FieldODEStateAndDerivative(T time, T[] state, T[] derivative2, T[][] secondaryState, T[][] secondaryDerivative2) {
        super(time, state, secondaryState);
        this.derivative = (T[]) ((RealFieldElement[]) derivative2.clone());
        this.secondaryDerivative = copy(time.getField(), secondaryDerivative2);
    }

    public T[] getDerivative() {
        return (T[]) ((RealFieldElement[]) this.derivative.clone());
    }

    public T[] getSecondaryDerivative(int index) {
        return index == 0 ? (T[]) ((RealFieldElement[]) this.derivative.clone()) : (T[]) ((RealFieldElement[]) this.secondaryDerivative[index - 1].clone());
    }
}
