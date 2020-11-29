package org.apache.commons.math3.ode.events;

import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.ode.FieldODEState;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;

public interface FieldEventHandler<T extends RealFieldElement<T>> {
    Action eventOccurred(FieldODEStateAndDerivative<T> fieldODEStateAndDerivative, boolean z);

    /* renamed from: g */
    T mo2987g(FieldODEStateAndDerivative<T> fieldODEStateAndDerivative);

    void init(FieldODEStateAndDerivative<T> fieldODEStateAndDerivative, T t);

    FieldODEState<T> resetState(FieldODEStateAndDerivative<T> fieldODEStateAndDerivative);
}
