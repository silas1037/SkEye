package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;

class DormandPrince54FieldStepInterpolator<T extends RealFieldElement<T>> extends RungeKuttaFieldStepInterpolator<T> {
    private final T a70;
    private final T a72;
    private final T a73;
    private final T a74;
    private final T a75;

    /* renamed from: d0 */
    private final T f269d0;

    /* renamed from: d2 */
    private final T f270d2;

    /* renamed from: d3 */
    private final T f271d3;

    /* renamed from: d4 */
    private final T f272d4;

    /* renamed from: d5 */
    private final T f273d5;

    /* renamed from: d6 */
    private final T f274d6;

    DormandPrince54FieldStepInterpolator(Field<T> field, boolean forward, T[][] yDotK, FieldODEStateAndDerivative<T> globalPreviousState, FieldODEStateAndDerivative<T> globalCurrentState, FieldODEStateAndDerivative<T> softPreviousState, FieldODEStateAndDerivative<T> softCurrentState, FieldEquationsMapper<T> mapper) {
        super(field, forward, yDotK, globalPreviousState, globalCurrentState, softPreviousState, softCurrentState, mapper);
        T one = field.getOne();
        this.a70 = (T) ((RealFieldElement) ((RealFieldElement) one.multiply(35.0d)).divide(384.0d));
        this.a72 = (T) ((RealFieldElement) ((RealFieldElement) one.multiply(500.0d)).divide(1113.0d));
        this.a73 = (T) ((RealFieldElement) ((RealFieldElement) one.multiply(125.0d)).divide(192.0d));
        this.a74 = (T) ((RealFieldElement) ((RealFieldElement) one.multiply(-2187.0d)).divide(6784.0d));
        this.a75 = (T) ((RealFieldElement) ((RealFieldElement) one.multiply(11.0d)).divide(84.0d));
        this.f269d0 = (T) ((RealFieldElement) ((RealFieldElement) one.multiply(-1.2715105075E10d)).divide(1.1282082432E10d));
        this.f270d2 = (T) ((RealFieldElement) ((RealFieldElement) one.multiply(8.74874797E10d)).divide(3.2700410799E10d));
        this.f271d3 = (T) ((RealFieldElement) ((RealFieldElement) one.multiply(-1.0690763975E10d)).divide(1.880347072E9d));
        this.f272d4 = (T) ((RealFieldElement) ((RealFieldElement) one.multiply(7.01980252875E11d)).divide(1.99316789632E11d));
        this.f273d5 = (T) ((RealFieldElement) ((RealFieldElement) one.multiply(-1.453857185E9d)).divide(8.22651844E8d));
        this.f274d6 = (T) ((RealFieldElement) ((RealFieldElement) one.multiply(6.9997945E7d)).divide(2.9380423E7d));
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.ode.nonstiff.RungeKuttaFieldStepInterpolator
    public DormandPrince54FieldStepInterpolator<T> create(Field<T> newField, boolean newForward, T[][] newYDotK, FieldODEStateAndDerivative<T> newGlobalPreviousState, FieldODEStateAndDerivative<T> newGlobalCurrentState, FieldODEStateAndDerivative<T> newSoftPreviousState, FieldODEStateAndDerivative<T> newSoftCurrentState, FieldEquationsMapper<T> newMapper) {
        return new DormandPrince54FieldStepInterpolator<>(newField, newForward, newYDotK, newGlobalPreviousState, newGlobalCurrentState, newSoftPreviousState, newSoftCurrentState, newMapper);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r34v0, resolved type: org.apache.commons.math3.ode.nonstiff.DormandPrince54FieldStepInterpolator<T extends org.apache.commons.math3.RealFieldElement<T>> */
    /* JADX WARN: Multi-variable type inference failed */
    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.ode.sampling.AbstractFieldStepInterpolator
    public FieldODEStateAndDerivative<T> computeInterpolatedStateAndDerivatives(FieldEquationsMapper<T> fieldEquationsMapper, T time, T theta, T thetaH, T oneMinusThetaH) {
        T[] interpolatedState;
        T[] interpolatedDerivatives;
        RealFieldElement realFieldElement = (RealFieldElement) time.getField().getOne();
        RealFieldElement realFieldElement2 = (RealFieldElement) realFieldElement.subtract(theta);
        RealFieldElement realFieldElement3 = (RealFieldElement) theta.multiply(2);
        RealFieldElement realFieldElement4 = (RealFieldElement) realFieldElement.subtract(realFieldElement3);
        RealFieldElement realFieldElement5 = (RealFieldElement) theta.multiply(((RealFieldElement) theta.multiply(-3)).add(2.0d));
        RealFieldElement realFieldElement6 = (RealFieldElement) realFieldElement3.multiply(((RealFieldElement) theta.multiply(realFieldElement3.subtract(3.0d))).add(1.0d));
        if (getGlobalPreviousState() == null || theta.getReal() > 0.5d) {
            RealFieldElement realFieldElement7 = (RealFieldElement) oneMinusThetaH.negate();
            RealFieldElement realFieldElement8 = (RealFieldElement) oneMinusThetaH.multiply(theta);
            RealFieldElement realFieldElement9 = (RealFieldElement) realFieldElement8.multiply(theta);
            RealFieldElement realFieldElement10 = (RealFieldElement) realFieldElement9.multiply(realFieldElement2);
            interpolatedState = currentStateLinearCombination((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement7.multiply(this.a70)).subtract(realFieldElement8.multiply(this.a70.subtract(1.0d)))).add(realFieldElement9.multiply(((RealFieldElement) this.a70.multiply(2)).subtract(1.0d)))).add(realFieldElement10.multiply(this.f269d0)), (RealFieldElement) time.getField().getZero(), (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement7.multiply(this.a72)).subtract(realFieldElement8.multiply(this.a72))).add(realFieldElement9.multiply(this.a72.multiply(2)))).add(realFieldElement10.multiply(this.f270d2)), (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement7.multiply(this.a73)).subtract(realFieldElement8.multiply(this.a73))).add(realFieldElement9.multiply(this.a73.multiply(2)))).add(realFieldElement10.multiply(this.f271d3)), (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement7.multiply(this.a74)).subtract(realFieldElement8.multiply(this.a74))).add(realFieldElement9.multiply(this.a74.multiply(2)))).add(realFieldElement10.multiply(this.f272d4)), (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement7.multiply(this.a75)).subtract(realFieldElement8.multiply(this.a75))).add(realFieldElement9.multiply(this.a75.multiply(2)))).add(realFieldElement10.multiply(this.f273d5)), (RealFieldElement) ((RealFieldElement) realFieldElement10.multiply(this.f274d6)).subtract(realFieldElement9));
            interpolatedDerivatives = derivativeLinearCombination((RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.a70.subtract(realFieldElement4.multiply(this.a70.subtract(1.0d)))).add(realFieldElement5.multiply(((RealFieldElement) this.a70.multiply(2)).subtract(1.0d)))).add(realFieldElement6.multiply(this.f269d0)), (RealFieldElement) time.getField().getZero(), (RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.a72.subtract(realFieldElement4.multiply(this.a72))).add(realFieldElement5.multiply(this.a72.multiply(2)))).add(realFieldElement6.multiply(this.f270d2)), (RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.a73.subtract(realFieldElement4.multiply(this.a73))).add(realFieldElement5.multiply(this.a73.multiply(2)))).add(realFieldElement6.multiply(this.f271d3)), (RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.a74.subtract(realFieldElement4.multiply(this.a74))).add(realFieldElement5.multiply(this.a74.multiply(2)))).add(realFieldElement6.multiply(this.f272d4)), (RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.a75.subtract(realFieldElement4.multiply(this.a75))).add(realFieldElement5.multiply(this.a75.multiply(2)))).add(realFieldElement6.multiply(this.f273d5)), (RealFieldElement) ((RealFieldElement) realFieldElement6.multiply(this.f274d6)).subtract(realFieldElement5));
        } else {
            RealFieldElement realFieldElement11 = (RealFieldElement) thetaH.multiply(realFieldElement2);
            RealFieldElement realFieldElement12 = (RealFieldElement) realFieldElement11.multiply(theta);
            RealFieldElement realFieldElement13 = (RealFieldElement) realFieldElement12.multiply(realFieldElement2);
            interpolatedState = previousStateLinearCombination((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) thetaH.multiply(this.a70)).subtract(realFieldElement11.multiply(this.a70.subtract(1.0d)))).add(realFieldElement12.multiply(((RealFieldElement) this.a70.multiply(2)).subtract(1.0d)))).add(realFieldElement13.multiply(this.f269d0)), (RealFieldElement) time.getField().getZero(), (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) thetaH.multiply(this.a72)).subtract(realFieldElement11.multiply(this.a72))).add(realFieldElement12.multiply(this.a72.multiply(2)))).add(realFieldElement13.multiply(this.f270d2)), (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) thetaH.multiply(this.a73)).subtract(realFieldElement11.multiply(this.a73))).add(realFieldElement12.multiply(this.a73.multiply(2)))).add(realFieldElement13.multiply(this.f271d3)), (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) thetaH.multiply(this.a74)).subtract(realFieldElement11.multiply(this.a74))).add(realFieldElement12.multiply(this.a74.multiply(2)))).add(realFieldElement13.multiply(this.f272d4)), (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) thetaH.multiply(this.a75)).subtract(realFieldElement11.multiply(this.a75))).add(realFieldElement12.multiply(this.a75.multiply(2)))).add(realFieldElement13.multiply(this.f273d5)), (RealFieldElement) ((RealFieldElement) realFieldElement13.multiply(this.f274d6)).subtract(realFieldElement12));
            interpolatedDerivatives = derivativeLinearCombination((RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.a70.subtract(realFieldElement4.multiply(this.a70.subtract(1.0d)))).add(realFieldElement5.multiply(((RealFieldElement) this.a70.multiply(2)).subtract(1.0d)))).add(realFieldElement6.multiply(this.f269d0)), (RealFieldElement) time.getField().getZero(), (RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.a72.subtract(realFieldElement4.multiply(this.a72))).add(realFieldElement5.multiply(this.a72.multiply(2)))).add(realFieldElement6.multiply(this.f270d2)), (RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.a73.subtract(realFieldElement4.multiply(this.a73))).add(realFieldElement5.multiply(this.a73.multiply(2)))).add(realFieldElement6.multiply(this.f271d3)), (RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.a74.subtract(realFieldElement4.multiply(this.a74))).add(realFieldElement5.multiply(this.a74.multiply(2)))).add(realFieldElement6.multiply(this.f272d4)), (RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.a75.subtract(realFieldElement4.multiply(this.a75))).add(realFieldElement5.multiply(this.a75.multiply(2)))).add(realFieldElement6.multiply(this.f273d5)), (RealFieldElement) ((RealFieldElement) realFieldElement6.multiply(this.f274d6)).subtract(realFieldElement5));
        }
        return new FieldODEStateAndDerivative<>(time, interpolatedState, interpolatedDerivatives);
    }
}
