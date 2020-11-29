package org.apache.commons.math3.ode.sampling;

import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;

public abstract class AbstractFieldStepInterpolator<T extends RealFieldElement<T>> implements FieldStepInterpolator<T> {
    private final boolean forward;
    private final FieldODEStateAndDerivative<T> globalCurrentState;
    private final FieldODEStateAndDerivative<T> globalPreviousState;
    private FieldEquationsMapper<T> mapper;
    private final FieldODEStateAndDerivative<T> softCurrentState;
    private final FieldODEStateAndDerivative<T> softPreviousState;

    /* access modifiers changed from: protected */
    public abstract FieldODEStateAndDerivative<T> computeInterpolatedStateAndDerivatives(FieldEquationsMapper<T> fieldEquationsMapper, T t, T t2, T t3, T t4) throws MaxCountExceededException;

    /* access modifiers changed from: protected */
    public abstract AbstractFieldStepInterpolator<T> create(boolean z, FieldODEStateAndDerivative<T> fieldODEStateAndDerivative, FieldODEStateAndDerivative<T> fieldODEStateAndDerivative2, FieldODEStateAndDerivative<T> fieldODEStateAndDerivative3, FieldODEStateAndDerivative<T> fieldODEStateAndDerivative4, FieldEquationsMapper<T> fieldEquationsMapper);

    protected AbstractFieldStepInterpolator(boolean isForward, FieldODEStateAndDerivative<T> globalPreviousState2, FieldODEStateAndDerivative<T> globalCurrentState2, FieldODEStateAndDerivative<T> softPreviousState2, FieldODEStateAndDerivative<T> softCurrentState2, FieldEquationsMapper<T> equationsMapper) {
        this.forward = isForward;
        this.globalPreviousState = globalPreviousState2;
        this.globalCurrentState = globalCurrentState2;
        this.softPreviousState = softPreviousState2;
        this.softCurrentState = softCurrentState2;
        this.mapper = equationsMapper;
    }

    public AbstractFieldStepInterpolator<T> restrictStep(FieldODEStateAndDerivative<T> previousState, FieldODEStateAndDerivative<T> currentState) {
        return create(this.forward, this.globalPreviousState, this.globalCurrentState, previousState, currentState, this.mapper);
    }

    public FieldODEStateAndDerivative<T> getGlobalPreviousState() {
        return this.globalPreviousState;
    }

    public FieldODEStateAndDerivative<T> getGlobalCurrentState() {
        return this.globalCurrentState;
    }

    @Override // org.apache.commons.math3.ode.sampling.FieldStepInterpolator
    public FieldODEStateAndDerivative<T> getPreviousState() {
        return this.softPreviousState;
    }

    @Override // org.apache.commons.math3.ode.sampling.FieldStepInterpolator
    public FieldODEStateAndDerivative<T> getCurrentState() {
        return this.softCurrentState;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r6v0, resolved type: org.apache.commons.math3.ode.sampling.AbstractFieldStepInterpolator<T extends org.apache.commons.math3.RealFieldElement<T>> */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.ode.sampling.FieldStepInterpolator
    public FieldODEStateAndDerivative<T> getInterpolatedState(T time) {
        RealFieldElement realFieldElement = (RealFieldElement) time.subtract(this.globalPreviousState.getTime());
        RealFieldElement realFieldElement2 = (RealFieldElement) realFieldElement.divide(this.globalCurrentState.getTime().subtract(this.globalPreviousState.getTime()));
        return computeInterpolatedStateAndDerivatives(this.mapper, time, realFieldElement2, realFieldElement, (RealFieldElement) this.globalCurrentState.getTime().subtract(time));
    }

    @Override // org.apache.commons.math3.ode.sampling.FieldStepInterpolator
    public boolean isForward() {
        return this.forward;
    }
}
