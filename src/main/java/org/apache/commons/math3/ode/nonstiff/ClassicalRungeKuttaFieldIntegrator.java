package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.util.MathArrays;

public class ClassicalRungeKuttaFieldIntegrator<T extends RealFieldElement<T>> extends RungeKuttaFieldIntegrator<T> {
    public ClassicalRungeKuttaFieldIntegrator(Field<T> field, T step) {
        super(field, "classical Runge-Kutta", step);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r0v1, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r2v2, resolved type: java.lang.Object[] */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.ode.nonstiff.FieldButcherArrayProvider
    public T[] getC() {
        T[] c = (T[]) ((RealFieldElement[]) MathArrays.buildArray(getField(), 3));
        c[0] = (RealFieldElement) getField().getOne().multiply(0.5d);
        c[1] = c[0];
        c[2] = getField().getOne();
        return c;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r0v1, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[][] */
    /* JADX DEBUG: Multi-variable search result rejected for r2v2, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v2, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r2v6, resolved type: java.lang.Object[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v3, resolved type: java.lang.Object[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v4, resolved type: java.lang.Object[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v5, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v6, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v7, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.ode.nonstiff.FieldButcherArrayProvider
    public T[][] getA() {
        T[][] a = (T[][]) ((RealFieldElement[][]) MathArrays.buildArray(getField(), 3, -1));
        for (int i = 0; i < a.length; i++) {
            a[i] = (RealFieldElement[]) MathArrays.buildArray(getField(), i + 1);
        }
        a[0][0] = fraction(1, 2);
        a[1][0] = getField().getZero();
        a[1][1] = a[0][0];
        a[2][0] = getField().getZero();
        a[2][1] = getField().getZero();
        a[2][2] = getField().getOne();
        return a;
    }

    @Override // org.apache.commons.math3.ode.nonstiff.FieldButcherArrayProvider
    public T[] getB() {
        T[] b = (T[]) ((RealFieldElement[]) MathArrays.buildArray(getField(), 4));
        b[0] = fraction(1, 6);
        b[1] = fraction(1, 3);
        b[2] = b[1];
        b[3] = b[0];
        return b;
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.ode.nonstiff.RungeKuttaFieldIntegrator
    public ClassicalRungeKuttaFieldStepInterpolator<T> createInterpolator(boolean forward, T[][] yDotK, FieldODEStateAndDerivative<T> globalPreviousState, FieldODEStateAndDerivative<T> globalCurrentState, FieldEquationsMapper<T> mapper) {
        return new ClassicalRungeKuttaFieldStepInterpolator<>(getField(), forward, yDotK, globalPreviousState, globalCurrentState, globalPreviousState, globalCurrentState, mapper);
    }
}
