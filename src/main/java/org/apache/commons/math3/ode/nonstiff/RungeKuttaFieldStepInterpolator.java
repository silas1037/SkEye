package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.ode.sampling.AbstractFieldStepInterpolator;
import org.apache.commons.math3.util.MathArrays;

/* access modifiers changed from: package-private */
public abstract class RungeKuttaFieldStepInterpolator<T extends RealFieldElement<T>> extends AbstractFieldStepInterpolator<T> {
    private final Field<T> field;
    private final T[][] yDotK;

    /* access modifiers changed from: protected */
    public abstract RungeKuttaFieldStepInterpolator<T> create(Field<T> field2, boolean z, T[][] tArr, FieldODEStateAndDerivative<T> fieldODEStateAndDerivative, FieldODEStateAndDerivative<T> fieldODEStateAndDerivative2, FieldODEStateAndDerivative<T> fieldODEStateAndDerivative3, FieldODEStateAndDerivative<T> fieldODEStateAndDerivative4, FieldEquationsMapper<T> fieldEquationsMapper);

    /* JADX DEBUG: Multi-variable search result rejected for r1v2, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[][] */
    /* JADX WARN: Multi-variable type inference failed */
    protected RungeKuttaFieldStepInterpolator(Field<T> field2, boolean forward, T[][] yDotK2, FieldODEStateAndDerivative<T> globalPreviousState, FieldODEStateAndDerivative<T> globalCurrentState, FieldODEStateAndDerivative<T> softPreviousState, FieldODEStateAndDerivative<T> softCurrentState, FieldEquationsMapper<T> mapper) {
        super(forward, globalPreviousState, globalCurrentState, softPreviousState, softCurrentState, mapper);
        this.field = field2;
        this.yDotK = (T[][]) ((RealFieldElement[][]) MathArrays.buildArray(field2, yDotK2.length, -1));
        for (int i = 0; i < yDotK2.length; i++) {
            this.yDotK[i] = (RealFieldElement[]) yDotK2[i].clone();
        }
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.ode.sampling.AbstractFieldStepInterpolator
    public RungeKuttaFieldStepInterpolator<T> create(boolean newForward, FieldODEStateAndDerivative<T> newGlobalPreviousState, FieldODEStateAndDerivative<T> newGlobalCurrentState, FieldODEStateAndDerivative<T> newSoftPreviousState, FieldODEStateAndDerivative<T> newSoftCurrentState, FieldEquationsMapper<T> newMapper) {
        return create(this.field, newForward, this.yDotK, newGlobalPreviousState, newGlobalCurrentState, newSoftPreviousState, newSoftCurrentState, newMapper);
    }

    /* access modifiers changed from: protected */
    public final T[] previousStateLinearCombination(T... coefficients) {
        return combine(getPreviousState().getState(), coefficients);
    }

    /* access modifiers changed from: protected */
    public T[] currentStateLinearCombination(T... coefficients) {
        return combine(getCurrentState().getState(), coefficients);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r3v0, resolved type: org.apache.commons.math3.ode.nonstiff.RungeKuttaFieldStepInterpolator<T extends org.apache.commons.math3.RealFieldElement<T>> */
    /* JADX WARN: Multi-variable type inference failed */
    /* access modifiers changed from: protected */
    public T[] derivativeLinearCombination(T... coefficients) {
        return (T[]) combine((RealFieldElement[]) MathArrays.buildArray(this.field, this.yDotK[0].length), coefficients);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r6v0, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r2v2, resolved type: org.apache.commons.math3.dfp.DfpDec */
    /* JADX WARN: Multi-variable type inference failed */
    private T[] combine(T[] a, T... coefficients) {
        for (int i = 0; i < a.length; i++) {
            for (int k = 0; k < coefficients.length; k++) {
                a[i] = (RealFieldElement) a[i].add(coefficients[k].multiply(this.yDotK[k][i]));
            }
        }
        return a;
    }
}
