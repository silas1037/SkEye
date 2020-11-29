package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;

class GillFieldStepInterpolator<T extends RealFieldElement<T>> extends RungeKuttaFieldStepInterpolator<T> {
    private final T one_minus_inv_sqrt_2;
    private final T one_plus_inv_sqrt_2;

    GillFieldStepInterpolator(Field<T> field, boolean forward, T[][] yDotK, FieldODEStateAndDerivative<T> globalPreviousState, FieldODEStateAndDerivative<T> globalCurrentState, FieldODEStateAndDerivative<T> softPreviousState, FieldODEStateAndDerivative<T> softCurrentState, FieldEquationsMapper<T> mapper) {
        super(field, forward, yDotK, globalPreviousState, globalCurrentState, softPreviousState, softCurrentState, mapper);
        RealFieldElement realFieldElement = (RealFieldElement) ((RealFieldElement) field.getZero().add(0.5d)).sqrt();
        this.one_minus_inv_sqrt_2 = (T) ((RealFieldElement) field.getOne().subtract(realFieldElement));
        this.one_plus_inv_sqrt_2 = (T) ((RealFieldElement) field.getOne().add(realFieldElement));
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.ode.nonstiff.RungeKuttaFieldStepInterpolator
    public GillFieldStepInterpolator<T> create(Field<T> newField, boolean newForward, T[][] newYDotK, FieldODEStateAndDerivative<T> newGlobalPreviousState, FieldODEStateAndDerivative<T> newGlobalCurrentState, FieldODEStateAndDerivative<T> newSoftPreviousState, FieldODEStateAndDerivative<T> newSoftCurrentState, FieldEquationsMapper<T> newMapper) {
        return new GillFieldStepInterpolator<>(newField, newForward, newYDotK, newGlobalPreviousState, newGlobalCurrentState, newSoftPreviousState, newSoftCurrentState, newMapper);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r24v0, resolved type: org.apache.commons.math3.ode.nonstiff.GillFieldStepInterpolator<T extends org.apache.commons.math3.RealFieldElement<T>> */
    /* JADX WARN: Multi-variable type inference failed */
    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.ode.sampling.AbstractFieldStepInterpolator
    public FieldODEStateAndDerivative<T> computeInterpolatedStateAndDerivatives(FieldEquationsMapper<T> fieldEquationsMapper, T time, T theta, T thetaH, T oneMinusThetaH) {
        T[] interpolatedState;
        T[] interpolatedDerivatives;
        RealFieldElement realFieldElement = (RealFieldElement) theta.multiply(2);
        RealFieldElement realFieldElement2 = (RealFieldElement) realFieldElement.multiply(realFieldElement);
        RealFieldElement realFieldElement3 = (RealFieldElement) ((RealFieldElement) theta.multiply(realFieldElement.subtract(3.0d))).add(1.0d);
        RealFieldElement realFieldElement4 = (RealFieldElement) realFieldElement.multiply(((RealFieldElement) time.getField().getOne()).subtract(theta));
        RealFieldElement realFieldElement5 = (RealFieldElement) realFieldElement4.multiply(this.one_minus_inv_sqrt_2);
        RealFieldElement realFieldElement6 = (RealFieldElement) realFieldElement4.multiply(this.one_plus_inv_sqrt_2);
        RealFieldElement realFieldElement7 = (RealFieldElement) theta.multiply(realFieldElement.subtract(1.0d));
        if (getGlobalPreviousState() == null || theta.getReal() > 0.5d) {
            RealFieldElement realFieldElement8 = (RealFieldElement) oneMinusThetaH.divide(-6.0d);
            RealFieldElement realFieldElement9 = (RealFieldElement) realFieldElement8.multiply(((RealFieldElement) realFieldElement.add(2.0d)).subtract(realFieldElement2));
            interpolatedState = currentStateLinearCombination((RealFieldElement) realFieldElement8.multiply(((RealFieldElement) realFieldElement2.subtract(theta.multiply(5))).add(1.0d)), (RealFieldElement) realFieldElement9.multiply(this.one_minus_inv_sqrt_2), (RealFieldElement) realFieldElement9.multiply(this.one_plus_inv_sqrt_2), (RealFieldElement) realFieldElement8.multiply(((RealFieldElement) realFieldElement2.add(theta)).add(1.0d)));
            interpolatedDerivatives = derivativeLinearCombination(realFieldElement3, realFieldElement5, realFieldElement6, realFieldElement7);
        } else {
            RealFieldElement realFieldElement10 = (RealFieldElement) thetaH.divide(6.0d);
            RealFieldElement realFieldElement11 = (RealFieldElement) realFieldElement10.multiply(((RealFieldElement) theta.multiply(6)).subtract(realFieldElement2));
            interpolatedState = previousStateLinearCombination((RealFieldElement) realFieldElement10.multiply(((RealFieldElement) realFieldElement2.subtract(theta.multiply(9))).add(6.0d)), (RealFieldElement) realFieldElement11.multiply(this.one_minus_inv_sqrt_2), (RealFieldElement) realFieldElement11.multiply(this.one_plus_inv_sqrt_2), (RealFieldElement) realFieldElement10.multiply(realFieldElement2.subtract(theta.multiply(3))));
            interpolatedDerivatives = derivativeLinearCombination(realFieldElement3, realFieldElement5, realFieldElement6, realFieldElement7);
        }
        return new FieldODEStateAndDerivative<>(time, interpolatedState, interpolatedDerivatives);
    }
}
