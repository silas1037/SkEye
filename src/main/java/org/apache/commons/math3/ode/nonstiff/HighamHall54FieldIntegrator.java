package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.MathUtils;

public class HighamHall54FieldIntegrator<T extends RealFieldElement<T>> extends EmbeddedRungeKuttaFieldIntegrator<T> {
    private static final String METHOD_NAME = "Higham-Hall 5(4)";

    /* renamed from: e */
    private final T[] f301e;

    public HighamHall54FieldIntegrator(Field<T> field, double minStep, double maxStep, double scalAbsoluteTolerance, double scalRelativeTolerance) {
        super(field, METHOD_NAME, -1, minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
        this.f301e = (T[]) ((RealFieldElement[]) MathArrays.buildArray(field, 7));
        this.f301e[0] = fraction(-1, 20);
        this.f301e[1] = field.getZero();
        this.f301e[2] = fraction(81, 160);
        this.f301e[3] = fraction(-6, 5);
        this.f301e[4] = fraction(25, 32);
        this.f301e[5] = fraction(1, 16);
        this.f301e[6] = fraction(-1, 10);
    }

    public HighamHall54FieldIntegrator(Field<T> field, double minStep, double maxStep, double[] vecAbsoluteTolerance, double[] vecRelativeTolerance) {
        super(field, METHOD_NAME, -1, minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
        this.f301e = (T[]) ((RealFieldElement[]) MathArrays.buildArray(field, 7));
        this.f301e[0] = fraction(-1, 20);
        this.f301e[1] = field.getZero();
        this.f301e[2] = fraction(81, 160);
        this.f301e[3] = fraction(-6, 5);
        this.f301e[4] = fraction(25, 32);
        this.f301e[5] = fraction(1, 16);
        this.f301e[6] = fraction(-1, 10);
    }

    @Override // org.apache.commons.math3.ode.nonstiff.FieldButcherArrayProvider
    public T[] getC() {
        T[] c = (T[]) ((RealFieldElement[]) MathArrays.buildArray(getField(), 6));
        c[0] = fraction(2, 9);
        c[1] = fraction(1, 3);
        c[2] = fraction(1, 2);
        c[3] = fraction(3, 5);
        c[4] = getField().getOne();
        c[5] = getField().getOne();
        return c;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r0v1, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[][] */
    /* JADX DEBUG: Multi-variable search result rejected for r2v3, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r2v4, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r2v5, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r2v6, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v5, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r2v10, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r2v11, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r2v12, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r2v13, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r2v14, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r2v15, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r2v16, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r2v17, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r2v18, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r2v19, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r2v20, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v24, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r2v24, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r2v25, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r2v26, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r2v27, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.ode.nonstiff.FieldButcherArrayProvider
    public T[][] getA() {
        T[][] a = (T[][]) ((RealFieldElement[][]) MathArrays.buildArray(getField(), 6, -1));
        for (int i = 0; i < a.length; i++) {
            a[i] = (RealFieldElement[]) MathArrays.buildArray(getField(), i + 1);
        }
        a[0][0] = fraction(2, 9);
        a[1][0] = fraction(1, 12);
        a[1][1] = fraction(1, 4);
        a[2][0] = fraction(1, 8);
        a[2][1] = getField().getZero();
        a[2][2] = fraction(3, 8);
        a[3][0] = fraction(91, 500);
        a[3][1] = fraction(-27, 100);
        a[3][2] = fraction(78, 125);
        a[3][3] = fraction(8, 125);
        a[4][0] = fraction(-11, 20);
        a[4][1] = fraction(27, 20);
        a[4][2] = fraction(12, 5);
        a[4][3] = fraction(-36, 5);
        a[4][4] = fraction(5, 1);
        a[5][0] = fraction(1, 12);
        a[5][1] = getField().getZero();
        a[5][2] = fraction(27, 32);
        a[5][3] = fraction(-4, 3);
        a[5][4] = fraction(125, 96);
        a[5][5] = fraction(5, 48);
        return a;
    }

    @Override // org.apache.commons.math3.ode.nonstiff.FieldButcherArrayProvider
    public T[] getB() {
        T[] b = (T[]) ((RealFieldElement[]) MathArrays.buildArray(getField(), 7));
        b[0] = fraction(1, 12);
        b[1] = getField().getZero();
        b[2] = fraction(27, 32);
        b[3] = fraction(-4, 3);
        b[4] = fraction(125, 96);
        b[5] = fraction(5, 48);
        b[6] = getField().getZero();
        return b;
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.ode.nonstiff.EmbeddedRungeKuttaFieldIntegrator
    public HighamHall54FieldStepInterpolator<T> createInterpolator(boolean forward, T[][] yDotK, FieldODEStateAndDerivative<T> globalPreviousState, FieldODEStateAndDerivative<T> globalCurrentState, FieldEquationsMapper<T> mapper) {
        return new HighamHall54FieldStepInterpolator<>(getField(), forward, yDotK, globalPreviousState, globalCurrentState, globalPreviousState, globalCurrentState, mapper);
    }

    @Override // org.apache.commons.math3.ode.nonstiff.EmbeddedRungeKuttaFieldIntegrator
    public int getOrder() {
        return 5;
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.ode.nonstiff.EmbeddedRungeKuttaFieldIntegrator
    public T estimateError(T[][] yDotK, T[] y0, T[] y1, T h) {
        T zero = getField().getZero();
        for (int j = 0; j < this.mainSetDimension; j++) {
            RealFieldElement realFieldElement = (RealFieldElement) yDotK[0][j].multiply(this.f301e[0]);
            for (int l = 1; l < this.f301e.length; l++) {
                realFieldElement = (RealFieldElement) realFieldElement.add(yDotK[l][j].multiply(this.f301e[l]));
            }
            RealFieldElement max = MathUtils.max((RealFieldElement) y0[j].abs(), (RealFieldElement) y1[j].abs());
            RealFieldElement realFieldElement2 = (RealFieldElement) ((RealFieldElement) h.multiply(realFieldElement)).divide((RealFieldElement) (this.vecAbsoluteTolerance == null ? ((RealFieldElement) max.multiply(this.scalRelativeTolerance)).add(this.scalAbsoluteTolerance) : ((RealFieldElement) max.multiply(this.vecRelativeTolerance[j])).add(this.vecAbsoluteTolerance[j])));
            zero = (RealFieldElement) zero.add(realFieldElement2.multiply(realFieldElement2));
        }
        return (T) ((RealFieldElement) ((RealFieldElement) zero.divide((double) this.mainSetDimension)).sqrt());
    }
}
