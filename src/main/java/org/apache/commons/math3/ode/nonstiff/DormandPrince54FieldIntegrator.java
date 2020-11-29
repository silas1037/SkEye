package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.MathUtils;

public class DormandPrince54FieldIntegrator<T extends RealFieldElement<T>> extends EmbeddedRungeKuttaFieldIntegrator<T> {
    private static final String METHOD_NAME = "Dormand-Prince 5(4)";

    /* renamed from: e1 */
    private final T f263e1 = fraction(71, 57600);

    /* renamed from: e3 */
    private final T f264e3 = fraction(-71, 16695);

    /* renamed from: e4 */
    private final T f265e4 = fraction(71, 1920);

    /* renamed from: e5 */
    private final T f266e5 = fraction(-17253, 339200);

    /* renamed from: e6 */
    private final T f267e6 = fraction(22, 525);

    /* renamed from: e7 */
    private final T f268e7 = fraction(-1, 40);

    public DormandPrince54FieldIntegrator(Field<T> field, double minStep, double maxStep, double scalAbsoluteTolerance, double scalRelativeTolerance) {
        super(field, METHOD_NAME, 6, minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
    }

    public DormandPrince54FieldIntegrator(Field<T> field, double minStep, double maxStep, double[] vecAbsoluteTolerance, double[] vecRelativeTolerance) {
        super(field, METHOD_NAME, 6, minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
    }

    @Override // org.apache.commons.math3.ode.nonstiff.FieldButcherArrayProvider
    public T[] getC() {
        T[] c = (T[]) ((RealFieldElement[]) MathArrays.buildArray(getField(), 6));
        c[0] = fraction(1, 5);
        c[1] = fraction(3, 10);
        c[2] = fraction(4, 5);
        c[3] = fraction(8, 9);
        c[4] = getField().getOne();
        c[5] = getField().getOne();
        return c;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r0v1, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[][] */
    /* JADX DEBUG: Multi-variable search result rejected for r2v2, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r2v3, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r2v4, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r2v5, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r2v6, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r2v7, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r2v8, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r2v9, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r2v10, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r2v11, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r2v13, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r2v15, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r2v17, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r2v19, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r2v21, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r2v22, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r3v31, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r2v26, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r2v27, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r2v28, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r2v29, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.ode.nonstiff.FieldButcherArrayProvider
    public T[][] getA() {
        T[][] a = (T[][]) ((RealFieldElement[][]) MathArrays.buildArray(getField(), 6, -1));
        for (int i = 0; i < a.length; i++) {
            a[i] = (RealFieldElement[]) MathArrays.buildArray(getField(), i + 1);
        }
        a[0][0] = fraction(1, 5);
        a[1][0] = fraction(3, 40);
        a[1][1] = fraction(9, 40);
        a[2][0] = fraction(44, 45);
        a[2][1] = fraction(-56, 15);
        a[2][2] = fraction(32, 9);
        a[3][0] = fraction(19372, 6561);
        a[3][1] = fraction(-25360, 2187);
        a[3][2] = fraction(64448, 6561);
        a[3][3] = fraction(-212, 729);
        a[4][0] = fraction(9017, 3168);
        a[4][1] = fraction(-355, 33);
        a[4][2] = fraction(46732, 5247);
        a[4][3] = fraction(49, 176);
        a[4][4] = fraction(-5103, 18656);
        a[5][0] = fraction(35, 384);
        a[5][1] = getField().getZero();
        a[5][2] = fraction(500, 1113);
        a[5][3] = fraction(125, 192);
        a[5][4] = fraction(-2187, 6784);
        a[5][5] = fraction(11, 84);
        return a;
    }

    @Override // org.apache.commons.math3.ode.nonstiff.FieldButcherArrayProvider
    public T[] getB() {
        T[] b = (T[]) ((RealFieldElement[]) MathArrays.buildArray(getField(), 7));
        b[0] = fraction(35, 384);
        b[1] = getField().getZero();
        b[2] = fraction(500, 1113);
        b[3] = fraction(125, 192);
        b[4] = fraction(-2187, 6784);
        b[5] = fraction(11, 84);
        b[6] = getField().getZero();
        return b;
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.ode.nonstiff.EmbeddedRungeKuttaFieldIntegrator
    public DormandPrince54FieldStepInterpolator<T> createInterpolator(boolean forward, T[][] yDotK, FieldODEStateAndDerivative<T> globalPreviousState, FieldODEStateAndDerivative<T> globalCurrentState, FieldEquationsMapper<T> mapper) {
        return new DormandPrince54FieldStepInterpolator<>(getField(), forward, yDotK, globalPreviousState, globalCurrentState, globalPreviousState, globalCurrentState, mapper);
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
            RealFieldElement realFieldElement = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) yDotK[0][j].multiply(this.f263e1)).add(yDotK[2][j].multiply(this.f264e3))).add(yDotK[3][j].multiply(this.f265e4))).add(yDotK[4][j].multiply(this.f266e5))).add(yDotK[5][j].multiply(this.f267e6))).add(yDotK[6][j].multiply(this.f268e7));
            RealFieldElement max = MathUtils.max((RealFieldElement) y0[j].abs(), (RealFieldElement) y1[j].abs());
            RealFieldElement realFieldElement2 = (RealFieldElement) ((RealFieldElement) h.multiply(realFieldElement)).divide((RealFieldElement) (this.vecAbsoluteTolerance == null ? ((RealFieldElement) max.multiply(this.scalRelativeTolerance)).add(this.scalAbsoluteTolerance) : ((RealFieldElement) max.multiply(this.vecRelativeTolerance[j])).add(this.vecAbsoluteTolerance[j])));
            zero = (RealFieldElement) zero.add(realFieldElement2.multiply(realFieldElement2));
        }
        return (T) ((RealFieldElement) ((RealFieldElement) zero.divide((double) this.mainSetDimension)).sqrt());
    }
}
