package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;

class ClassicalRungeKuttaFieldStepInterpolator<T extends RealFieldElement<T>> extends RungeKuttaFieldStepInterpolator<T> {
    ClassicalRungeKuttaFieldStepInterpolator(Field<T> field, boolean forward, T[][] yDotK, FieldODEStateAndDerivative<T> globalPreviousState, FieldODEStateAndDerivative<T> globalCurrentState, FieldODEStateAndDerivative<T> softPreviousState, FieldODEStateAndDerivative<T> softCurrentState, FieldEquationsMapper<T> mapper) {
        super(field, forward, yDotK, globalPreviousState, globalCurrentState, softPreviousState, softCurrentState, mapper);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.ode.nonstiff.RungeKuttaFieldStepInterpolator
    public ClassicalRungeKuttaFieldStepInterpolator<T> create(Field<T> newField, boolean newForward, T[][] newYDotK, FieldODEStateAndDerivative<T> newGlobalPreviousState, FieldODEStateAndDerivative<T> newGlobalCurrentState, FieldODEStateAndDerivative<T> newSoftPreviousState, FieldODEStateAndDerivative<T> newSoftCurrentState, FieldEquationsMapper<T> newMapper) {
        return new ClassicalRungeKuttaFieldStepInterpolator<>(newField, newForward, newYDotK, newGlobalPreviousState, newGlobalCurrentState, newSoftPreviousState, newSoftCurrentState, newMapper);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r22v0, resolved type: org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaFieldStepInterpolator<T extends org.apache.commons.math3.RealFieldElement<T>> */
    /* JADX WARN: Multi-variable type inference failed */
    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.ode.sampling.AbstractFieldStepInterpolator
    public FieldODEStateAndDerivative<T> computeInterpolatedStateAndDerivatives(FieldEquationsMapper<T> fieldEquationsMapper, T time, T theta, T thetaH, T oneMinusThetaH) {
        T[] interpolatedState;
        T[] interpolatedDerivatives;
        RealFieldElement realFieldElement = (RealFieldElement) time.getField().getOne();
        RealFieldElement realFieldElement2 = (RealFieldElement) realFieldElement.subtract(theta);
        RealFieldElement realFieldElement3 = (RealFieldElement) realFieldElement.subtract(theta.multiply(2));
        RealFieldElement realFieldElement4 = (RealFieldElement) realFieldElement2.multiply(realFieldElement3);
        RealFieldElement realFieldElement5 = (RealFieldElement) ((RealFieldElement) theta.multiply(realFieldElement2)).multiply(2);
        RealFieldElement realFieldElement6 = (RealFieldElement) ((RealFieldElement) theta.multiply(realFieldElement3)).negate();
        if (getGlobalPreviousState() == null || theta.getReal() > 0.5d) {
            RealFieldElement realFieldElement7 = (RealFieldElement) theta.multiply(4);
            RealFieldElement realFieldElement8 = (RealFieldElement) oneMinusThetaH.divide(6.0d);
            RealFieldElement realFieldElement9 = (RealFieldElement) realFieldElement8.multiply(((RealFieldElement) theta.multiply(realFieldElement7.subtract(2.0d))).subtract(2.0d));
            interpolatedState = currentStateLinearCombination((RealFieldElement) realFieldElement8.multiply(((RealFieldElement) theta.multiply(((RealFieldElement) realFieldElement7.negate()).add(5.0d))).subtract(1.0d)), realFieldElement9, realFieldElement9, (RealFieldElement) realFieldElement8.multiply(((RealFieldElement) theta.multiply(((RealFieldElement) realFieldElement7.negate()).subtract(1.0d))).subtract(1.0d)));
            interpolatedDerivatives = derivativeLinearCombination(realFieldElement4, realFieldElement5, realFieldElement5, realFieldElement6);
        } else {
            RealFieldElement realFieldElement10 = (RealFieldElement) ((RealFieldElement) theta.multiply(theta)).multiply(4);
            RealFieldElement realFieldElement11 = (RealFieldElement) thetaH.divide(6.0d);
            RealFieldElement realFieldElement12 = (RealFieldElement) realFieldElement11.multiply(((RealFieldElement) theta.multiply(6)).subtract(realFieldElement10));
            interpolatedState = previousStateLinearCombination((RealFieldElement) realFieldElement11.multiply(((RealFieldElement) realFieldElement10.subtract(theta.multiply(9))).add(6.0d)), realFieldElement12, realFieldElement12, (RealFieldElement) realFieldElement11.multiply(realFieldElement10.subtract(theta.multiply(3))));
            interpolatedDerivatives = derivativeLinearCombination(realFieldElement4, realFieldElement5, realFieldElement5, realFieldElement6);
        }
        return new FieldODEStateAndDerivative<>(time, interpolatedState, interpolatedDerivatives);
    }
}
