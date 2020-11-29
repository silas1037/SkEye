package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.util.MathArrays;

public class GillFieldIntegrator<T extends RealFieldElement<T>> extends RungeKuttaFieldIntegrator<T> {
    public GillFieldIntegrator(Field<T> field, T step) {
        super(field, "Gill", step);
    }

    @Override // org.apache.commons.math3.ode.nonstiff.FieldButcherArrayProvider
    public T[] getC() {
        T[] c = (T[]) ((RealFieldElement[]) MathArrays.buildArray(getField(), 3));
        c[0] = fraction(1, 2);
        c[1] = c[0];
        c[2] = getField().getOne();
        return c;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r0v1, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[][] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v6, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r5v2, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r5v3, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r5v4, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r5v5, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r5v6, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.ode.nonstiff.FieldButcherArrayProvider
    public T[][] getA() {
        RealFieldElement realFieldElement = (RealFieldElement) ((RealFieldElement) getField().getZero().add(2.0d)).sqrt();
        T[][] a = (T[][]) ((RealFieldElement[][]) MathArrays.buildArray(getField(), 3, -1));
        for (int i = 0; i < a.length; i++) {
            a[i] = (RealFieldElement[]) MathArrays.buildArray(getField(), i + 1);
        }
        a[0][0] = fraction(1, 2);
        a[1][0] = (RealFieldElement) ((RealFieldElement) realFieldElement.subtract(1.0d)).multiply(0.5d);
        a[1][1] = (RealFieldElement) ((RealFieldElement) realFieldElement.subtract(2.0d)).multiply(-0.5d);
        a[2][0] = getField().getZero();
        a[2][1] = (RealFieldElement) realFieldElement.multiply(-0.5d);
        a[2][2] = (RealFieldElement) ((RealFieldElement) realFieldElement.add(2.0d)).multiply(0.5d);
        return a;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r0v1, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v3, resolved type: java.lang.Object[] */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.ode.nonstiff.FieldButcherArrayProvider
    public T[] getB() {
        RealFieldElement realFieldElement = (RealFieldElement) ((RealFieldElement) getField().getZero().add(2.0d)).sqrt();
        T[] b = (T[]) ((RealFieldElement[]) MathArrays.buildArray(getField(), 4));
        b[0] = fraction(1, 6);
        b[1] = (RealFieldElement) ((RealFieldElement) realFieldElement.subtract(2.0d)).divide(-6.0d);
        b[2] = (RealFieldElement) ((RealFieldElement) realFieldElement.add(2.0d)).divide(6.0d);
        b[3] = b[0];
        return b;
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.ode.nonstiff.RungeKuttaFieldIntegrator
    public GillFieldStepInterpolator<T> createInterpolator(boolean forward, T[][] yDotK, FieldODEStateAndDerivative<T> globalPreviousState, FieldODEStateAndDerivative<T> globalCurrentState, FieldEquationsMapper<T> mapper) {
        return new GillFieldStepInterpolator<>(getField(), forward, yDotK, globalPreviousState, globalCurrentState, globalPreviousState, globalCurrentState, mapper);
    }
}
