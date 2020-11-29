package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.util.MathArrays;

public class LutherFieldIntegrator<T extends RealFieldElement<T>> extends RungeKuttaFieldIntegrator<T> {
    public LutherFieldIntegrator(Field<T> field, T step) {
        super(field, "Luther", step);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r0v1, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.ode.nonstiff.FieldButcherArrayProvider
    public T[] getC() {
        RealFieldElement realFieldElement = (RealFieldElement) ((RealFieldElement) getField().getZero().add(21.0d)).sqrt();
        T[] c = (T[]) ((RealFieldElement[]) MathArrays.buildArray(getField(), 6));
        c[0] = getField().getOne();
        c[1] = fraction(1, 2);
        c[2] = fraction(2, 3);
        c[3] = (RealFieldElement) ((RealFieldElement) realFieldElement.subtract(7.0d)).divide(-14.0d);
        c[4] = (RealFieldElement) ((RealFieldElement) realFieldElement.add(7.0d)).divide(14.0d);
        c[5] = getField().getOne();
        return c;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r0v1, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[][] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v2, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v10, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v11, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v12, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v13, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v14, resolved type: java.lang.Object[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v11, resolved type: java.lang.Object[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v12, resolved type: java.lang.Object[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v13, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v14, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v15, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v16, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v17, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v18, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v19, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v20, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v21, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v22, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v84, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v24, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v25, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v26, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX DEBUG: Multi-variable search result rejected for r4v27, resolved type: org.apache.commons.math3.RealFieldElement[] */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.ode.nonstiff.FieldButcherArrayProvider
    public T[][] getA() {
        RealFieldElement realFieldElement = (RealFieldElement) ((RealFieldElement) getField().getZero().add(21.0d)).sqrt();
        T[][] a = (T[][]) ((RealFieldElement[][]) MathArrays.buildArray(getField(), 6, -1));
        for (int i = 0; i < a.length; i++) {
            a[i] = (RealFieldElement[]) MathArrays.buildArray(getField(), i + 1);
        }
        a[0][0] = getField().getOne();
        a[1][0] = fraction(3, 8);
        a[1][1] = fraction(1, 8);
        a[2][0] = fraction(8, 27);
        a[2][1] = fraction(2, 27);
        a[2][2] = a[2][0];
        a[3][0] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement.multiply(9)).add(-21.0d)).divide(392.0d);
        a[3][1] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement.multiply(8)).add(-56.0d)).divide(392.0d);
        a[3][2] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement.multiply(-48)).add(336.0d)).divide(392.0d);
        a[3][3] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement.multiply(3)).add(-63.0d)).divide(392.0d);
        a[4][0] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement.multiply(-255)).add(-1155.0d)).divide(1960.0d);
        a[4][1] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement.multiply(-40)).add(-280.0d)).divide(1960.0d);
        a[4][2] = (RealFieldElement) ((RealFieldElement) realFieldElement.multiply(-320)).divide(1960.0d);
        a[4][3] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement.multiply(363)).add(63.0d)).divide(1960.0d);
        a[4][4] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement.multiply(392)).add(2352.0d)).divide(1960.0d);
        a[5][0] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement.multiply(105)).add(330.0d)).divide(180.0d);
        a[5][1] = fraction(2, 3);
        a[5][2] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement.multiply(280)).add(-200.0d)).divide(180.0d);
        a[5][3] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement.multiply(-189)).add(126.0d)).divide(180.0d);
        a[5][4] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement.multiply(-126)).add(-686.0d)).divide(180.0d);
        a[5][5] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) realFieldElement.multiply(-70)).add(490.0d)).divide(180.0d);
        return a;
    }

    @Override // org.apache.commons.math3.ode.nonstiff.FieldButcherArrayProvider
    public T[] getB() {
        T[] b = (T[]) ((RealFieldElement[]) MathArrays.buildArray(getField(), 7));
        b[0] = fraction(1, 20);
        b[1] = getField().getZero();
        b[2] = fraction(16, 45);
        b[3] = getField().getZero();
        b[4] = fraction(49, 180);
        b[5] = b[4];
        b[6] = b[0];
        return b;
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.ode.nonstiff.RungeKuttaFieldIntegrator
    public LutherFieldStepInterpolator<T> createInterpolator(boolean forward, T[][] yDotK, FieldODEStateAndDerivative<T> globalPreviousState, FieldODEStateAndDerivative<T> globalCurrentState, FieldEquationsMapper<T> mapper) {
        return new LutherFieldStepInterpolator<>(getField(), forward, yDotK, globalPreviousState, globalCurrentState, globalPreviousState, globalCurrentState, mapper);
    }
}
