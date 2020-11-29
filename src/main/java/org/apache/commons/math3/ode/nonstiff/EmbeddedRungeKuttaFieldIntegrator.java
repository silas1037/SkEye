package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldExpandableODE;
import org.apache.commons.math3.ode.FieldODEState;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.MathUtils;

public abstract class EmbeddedRungeKuttaFieldIntegrator<T extends RealFieldElement<T>> extends AdaptiveStepsizeFieldIntegrator<T> implements FieldButcherArrayProvider<T> {

    /* renamed from: a */
    private final T[][] f294a = ((T[][]) getA());

    /* renamed from: b */
    private final T[] f295b = ((T[]) getB());

    /* renamed from: c */
    private final T[] f296c = ((T[]) getC());
    private final T exp;
    private final int fsal;
    private T maxGrowth;
    private T minReduction;
    private T safety;

    /* access modifiers changed from: protected */
    public abstract RungeKuttaFieldStepInterpolator<T> createInterpolator(boolean z, T[][] tArr, FieldODEStateAndDerivative<T> fieldODEStateAndDerivative, FieldODEStateAndDerivative<T> fieldODEStateAndDerivative2, FieldEquationsMapper<T> fieldEquationsMapper);

    /* access modifiers changed from: protected */
    public abstract T estimateError(T[][] tArr, T[] tArr2, T[] tArr3, T t);

    public abstract int getOrder();

    /* JADX DEBUG: Multi-variable search result rejected for r12v0, resolved type: org.apache.commons.math3.ode.nonstiff.EmbeddedRungeKuttaFieldIntegrator<T extends org.apache.commons.math3.RealFieldElement<T>> */
    /* JADX WARN: Multi-variable type inference failed */
    protected EmbeddedRungeKuttaFieldIntegrator(Field<T> field, String name, int fsal2, double minStep, double maxStep, double scalAbsoluteTolerance, double scalRelativeTolerance) {
        super(field, name, minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
        this.fsal = fsal2;
        this.exp = (T) ((RealFieldElement) field.getOne().divide((double) (-getOrder())));
        setSafety((RealFieldElement) field.getZero().add(0.9d));
        setMinReduction((RealFieldElement) field.getZero().add(0.2d));
        setMaxGrowth((RealFieldElement) field.getZero().add(10.0d));
    }

    /* JADX DEBUG: Multi-variable search result rejected for r10v0, resolved type: org.apache.commons.math3.ode.nonstiff.EmbeddedRungeKuttaFieldIntegrator<T extends org.apache.commons.math3.RealFieldElement<T>> */
    /* JADX WARN: Multi-variable type inference failed */
    protected EmbeddedRungeKuttaFieldIntegrator(Field<T> field, String name, int fsal2, double minStep, double maxStep, double[] vecAbsoluteTolerance, double[] vecRelativeTolerance) {
        super(field, name, minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
        this.fsal = fsal2;
        this.exp = (T) ((RealFieldElement) field.getOne().divide((double) (-getOrder())));
        setSafety((RealFieldElement) field.getZero().add(0.9d));
        setMinReduction((RealFieldElement) field.getZero().add(0.2d));
        setMaxGrowth((RealFieldElement) field.getZero().add(10.0d));
    }

    /* access modifiers changed from: protected */
    public T fraction(int p, int q) {
        return (T) ((RealFieldElement) ((RealFieldElement) getField().getOne().multiply(p)).divide((double) q));
    }

    /* access modifiers changed from: protected */
    public T fraction(double p, double q) {
        return (T) ((RealFieldElement) ((RealFieldElement) getField().getOne().multiply(p)).divide(q));
    }

    public T getSafety() {
        return this.safety;
    }

    public void setSafety(T safety2) {
        this.safety = safety2;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r40v0, resolved type: org.apache.commons.math3.ode.nonstiff.EmbeddedRungeKuttaFieldIntegrator<T extends org.apache.commons.math3.RealFieldElement<T>> */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.ode.FirstOrderFieldIntegrator
    public FieldODEStateAndDerivative<T> integrate(FieldExpandableODE<T> equations, FieldODEState<T> initialState, T finalTime) throws NumberIsTooSmallException, DimensionMismatchException, MaxCountExceededException, NoBracketingException {
        RealFieldElement[] computeDerivatives;
        sanityChecks(initialState, finalTime);
        T t0 = initialState.getTime();
        T[] y0 = equations.getMapper().mapState(initialState);
        setStepStart(initIntegration(equations, t0, y0, finalTime));
        boolean forward = ((RealFieldElement) finalTime.subtract(initialState.getTime())).getReal() > 0.0d;
        int stages = this.f296c.length + 1;
        T[] y = y0;
        RealFieldElement[][] realFieldElementArr = (RealFieldElement[][]) MathArrays.buildArray(getField(), stages, -1);
        RealFieldElement[] realFieldElementArr2 = (RealFieldElement[]) MathArrays.buildArray(getField(), y0.length);
        RealFieldElement realFieldElement = (RealFieldElement) getField().getZero();
        boolean firstTime = true;
        setIsLastStep(false);
        do {
            RealFieldElement realFieldElement2 = (RealFieldElement) ((RealFieldElement) getField().getZero()).add(10.0d);
            while (((RealFieldElement) realFieldElement2.subtract(1.0d)).getReal() >= 0.0d) {
                y = equations.getMapper().mapState(getStepStart());
                realFieldElementArr[0] = equations.getMapper().mapDerivative(getStepStart());
                if (firstTime) {
                    RealFieldElement[] realFieldElementArr3 = (RealFieldElement[]) MathArrays.buildArray(getField(), this.mainSetDimension);
                    if (this.vecAbsoluteTolerance == null) {
                        for (int i = 0; i < realFieldElementArr3.length; i++) {
                            realFieldElementArr3[i] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) y[i].abs()).multiply(this.scalRelativeTolerance)).add(this.scalAbsoluteTolerance);
                        }
                    } else {
                        for (int i2 = 0; i2 < realFieldElementArr3.length; i2++) {
                            realFieldElementArr3[i2] = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) y[i2].abs()).multiply(this.vecRelativeTolerance[i2])).add(this.vecAbsoluteTolerance[i2]);
                        }
                    }
                    realFieldElement = initializeStep(forward, getOrder(), realFieldElementArr3, getStepStart(), equations.getMapper());
                    firstTime = false;
                }
                setStepSize(realFieldElement);
                if (forward) {
                    if (((RealFieldElement) ((RealFieldElement) getStepStart().getTime().add(getStepSize())).subtract(finalTime)).getReal() >= 0.0d) {
                        setStepSize((RealFieldElement) finalTime.subtract(getStepStart().getTime()));
                    }
                } else if (((RealFieldElement) ((RealFieldElement) getStepStart().getTime().add(getStepSize())).subtract(finalTime)).getReal() <= 0.0d) {
                    setStepSize((RealFieldElement) finalTime.subtract(getStepStart().getTime()));
                }
                for (int k = 1; k < stages; k++) {
                    for (int j = 0; j < y0.length; j++) {
                        RealFieldElement realFieldElement3 = (RealFieldElement) realFieldElementArr[0][j].multiply(this.f294a[k - 1][0]);
                        for (int l = 1; l < k; l++) {
                            realFieldElement3 = (RealFieldElement) realFieldElement3.add(realFieldElementArr[l][j].multiply(this.f294a[k - 1][l]));
                        }
                        realFieldElementArr2[j] = (RealFieldElement) y[j].add(getStepSize().multiply(realFieldElement3));
                    }
                    realFieldElementArr[k] = computeDerivatives((RealFieldElement) getStepStart().getTime().add(getStepSize().multiply(this.f296c[k - 1])), realFieldElementArr2);
                }
                for (int j2 = 0; j2 < y0.length; j2++) {
                    RealFieldElement realFieldElement4 = (RealFieldElement) realFieldElementArr[0][j2].multiply(this.f295b[0]);
                    for (int l2 = 1; l2 < stages; l2++) {
                        realFieldElement4 = (RealFieldElement) realFieldElement4.add(realFieldElementArr[l2][j2].multiply(this.f295b[l2]));
                    }
                    realFieldElementArr2[j2] = (RealFieldElement) y[j2].add(getStepSize().multiply(realFieldElement4));
                }
                realFieldElement2 = estimateError(realFieldElementArr, y, realFieldElementArr2, getStepSize());
                if (((RealFieldElement) realFieldElement2.subtract(1.0d)).getReal() >= 0.0d) {
                    realFieldElement = filterStep((RealFieldElement) getStepSize().multiply(MathUtils.min(this.maxGrowth, MathUtils.max(this.minReduction, (RealFieldElement) this.safety.multiply(realFieldElement2.pow(this.exp))))), forward, false);
                }
            }
            RealFieldElement realFieldElement5 = (RealFieldElement) getStepStart().getTime().add(getStepSize());
            if (this.fsal >= 0) {
                computeDerivatives = realFieldElementArr[this.fsal];
            } else {
                computeDerivatives = computeDerivatives(realFieldElement5, realFieldElementArr2);
            }
            FieldODEStateAndDerivative<T> stateTmp = new FieldODEStateAndDerivative<>(realFieldElement5, realFieldElementArr2, computeDerivatives);
            System.arraycopy(realFieldElementArr2, 0, y, 0, y0.length);
            setStepStart(acceptStep(createInterpolator(forward, realFieldElementArr, getStepStart(), stateTmp, equations.getMapper()), finalTime));
            if (!isLastStep()) {
                RealFieldElement realFieldElement6 = (RealFieldElement) getStepSize().multiply(MathUtils.min(this.maxGrowth, MathUtils.max(this.minReduction, (RealFieldElement) this.safety.multiply(realFieldElement2.pow(this.exp)))));
                RealFieldElement realFieldElement7 = (RealFieldElement) getStepStart().getTime().add(realFieldElement6);
                realFieldElement = filterStep(realFieldElement6, forward, forward ? ((RealFieldElement) realFieldElement7.subtract(finalTime)).getReal() >= 0.0d : ((RealFieldElement) realFieldElement7.subtract(finalTime)).getReal() <= 0.0d);
                RealFieldElement realFieldElement8 = (RealFieldElement) getStepStart().getTime().add(realFieldElement);
                if (forward ? ((RealFieldElement) realFieldElement8.subtract(finalTime)).getReal() >= 0.0d : ((RealFieldElement) realFieldElement8.subtract(finalTime)).getReal() <= 0.0d) {
                    realFieldElement = (RealFieldElement) finalTime.subtract(getStepStart().getTime());
                }
            }
        } while (!isLastStep());
        FieldODEStateAndDerivative<T> finalState = getStepStart();
        resetInternalState();
        return finalState;
    }

    public T getMinReduction() {
        return this.minReduction;
    }

    public void setMinReduction(T minReduction2) {
        this.minReduction = minReduction2;
    }

    public T getMaxGrowth() {
        return this.maxGrowth;
    }

    public void setMaxGrowth(T maxGrowth2) {
        this.maxGrowth = maxGrowth2;
    }
}
