package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;

class HighamHall54FieldStepInterpolator<T extends RealFieldElement<T>> extends RungeKuttaFieldStepInterpolator<T> {
    HighamHall54FieldStepInterpolator(Field<T> field, boolean forward, T[][] yDotK, FieldODEStateAndDerivative<T> globalPreviousState, FieldODEStateAndDerivative<T> globalCurrentState, FieldODEStateAndDerivative<T> softPreviousState, FieldODEStateAndDerivative<T> softCurrentState, FieldEquationsMapper<T> mapper) {
        super(field, forward, yDotK, globalPreviousState, globalCurrentState, softPreviousState, softCurrentState, mapper);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.ode.nonstiff.RungeKuttaFieldStepInterpolator
    public HighamHall54FieldStepInterpolator<T> create(Field<T> newField, boolean newForward, T[][] newYDotK, FieldODEStateAndDerivative<T> newGlobalPreviousState, FieldODEStateAndDerivative<T> newGlobalCurrentState, FieldODEStateAndDerivative<T> newSoftPreviousState, FieldODEStateAndDerivative<T> newSoftCurrentState, FieldEquationsMapper<T> newMapper) {
        return new HighamHall54FieldStepInterpolator<>(newField, newForward, newYDotK, newGlobalPreviousState, newGlobalCurrentState, newSoftPreviousState, newSoftCurrentState, newMapper);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r24v0, resolved type: org.apache.commons.math3.ode.nonstiff.HighamHall54FieldStepInterpolator<T extends org.apache.commons.math3.RealFieldElement<T>> */
    /* JADX WARN: Multi-variable type inference failed */
    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.ode.sampling.AbstractFieldStepInterpolator
    public FieldODEStateAndDerivative<T> computeInterpolatedStateAndDerivatives(FieldEquationsMapper<T> fieldEquationsMapper, T time, T theta, T thetaH, T t) {
        T[] interpolatedState;
        T[] interpolatedDerivatives;
        RealFieldElement realFieldElement = (RealFieldElement) ((RealFieldElement) theta.multiply(((RealFieldElement) theta.multiply(((RealFieldElement) theta.multiply(-10.0d)).add(16.0d))).add(-7.5d))).add(1.0d);
        RealFieldElement realFieldElement2 = (RealFieldElement) time.getField().getZero();
        RealFieldElement realFieldElement3 = (RealFieldElement) theta.multiply(((RealFieldElement) theta.multiply(((RealFieldElement) theta.multiply(67.5d)).add(-91.125d))).add(28.6875d));
        RealFieldElement realFieldElement4 = (RealFieldElement) theta.multiply(((RealFieldElement) theta.multiply(((RealFieldElement) theta.multiply(-120.0d)).add(152.0d))).add(-44.0d));
        RealFieldElement realFieldElement5 = (RealFieldElement) theta.multiply(((RealFieldElement) theta.multiply(((RealFieldElement) theta.multiply(62.5d)).add(-78.125d))).add(23.4375d));
        RealFieldElement realFieldElement6 = (RealFieldElement) ((RealFieldElement) theta.multiply(0.625d)).multiply(((RealFieldElement) theta.multiply(2)).subtract(1.0d));
        if (getGlobalPreviousState() == null || theta.getReal() > 0.5d) {
            RealFieldElement realFieldElement7 = (RealFieldElement) theta.multiply(theta);
            RealFieldElement realFieldElement8 = (RealFieldElement) thetaH.divide(theta);
            interpolatedState = currentStateLinearCombination((RealFieldElement) realFieldElement8.multiply(((RealFieldElement) theta.multiply(((RealFieldElement) theta.multiply(((RealFieldElement) theta.multiply(((RealFieldElement) theta.multiply(-2.5d)).add(5.333333333333333d))).add(-3.75d))).add(1.0d))).add(-0.08333333333333333d)), (RealFieldElement) time.getField().getZero(), (RealFieldElement) realFieldElement8.multiply(((RealFieldElement) realFieldElement7.multiply(((RealFieldElement) theta.multiply(((RealFieldElement) theta.multiply(16.875d)).add(-30.375d))).add(14.34375d))).add(-0.84375d)), (RealFieldElement) realFieldElement8.multiply(((RealFieldElement) realFieldElement7.multiply(((RealFieldElement) theta.multiply(((RealFieldElement) theta.multiply(-30.0d)).add(50.666666666666664d))).add(-22.0d))).add(1.3333333333333333d)), (RealFieldElement) realFieldElement8.multiply(((RealFieldElement) realFieldElement7.multiply(((RealFieldElement) theta.multiply(((RealFieldElement) theta.multiply(15.625d)).add(-26.041666666666668d))).add(11.71875d))).add(-1.3020833333333333d)), (RealFieldElement) realFieldElement8.multiply(((RealFieldElement) realFieldElement7.multiply(((RealFieldElement) theta.multiply(0.4166666666666667d)).add(-0.3125d))).add(-0.10416666666666667d)));
            interpolatedDerivatives = derivativeLinearCombination(realFieldElement, realFieldElement2, realFieldElement3, realFieldElement4, realFieldElement5, realFieldElement6);
        } else {
            interpolatedState = previousStateLinearCombination((RealFieldElement) thetaH.multiply(((RealFieldElement) theta.multiply(((RealFieldElement) theta.multiply(((RealFieldElement) theta.multiply(-2.5d)).add(5.333333333333333d))).add(-3.75d))).add(1.0d)), (RealFieldElement) time.getField().getZero(), (RealFieldElement) thetaH.multiply(theta.multiply(((RealFieldElement) theta.multiply(((RealFieldElement) theta.multiply(16.875d)).add(-30.375d))).add(14.34375d))), (RealFieldElement) thetaH.multiply(theta.multiply(((RealFieldElement) theta.multiply(((RealFieldElement) theta.multiply(-30.0d)).add(50.666666666666664d))).add(-22.0d))), (RealFieldElement) thetaH.multiply(theta.multiply(((RealFieldElement) theta.multiply(((RealFieldElement) theta.multiply(15.625d)).add(-26.041666666666668d))).add(11.71875d))), (RealFieldElement) thetaH.multiply(theta.multiply(((RealFieldElement) theta.multiply(0.4166666666666667d)).add(-0.3125d))));
            interpolatedDerivatives = derivativeLinearCombination(realFieldElement, realFieldElement2, realFieldElement3, realFieldElement4, realFieldElement5, realFieldElement6);
        }
        return new FieldODEStateAndDerivative<>(time, interpolatedState, interpolatedDerivatives);
    }
}
