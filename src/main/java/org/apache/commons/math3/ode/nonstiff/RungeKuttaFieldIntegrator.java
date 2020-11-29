package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.ode.AbstractFieldIntegrator;
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldExpandableODE;
import org.apache.commons.math3.ode.FieldODEState;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.ode.FirstOrderFieldDifferentialEquations;
import org.apache.commons.math3.util.MathArrays;

public abstract class RungeKuttaFieldIntegrator<T extends RealFieldElement<T>> extends AbstractFieldIntegrator<T> implements FieldButcherArrayProvider<T> {

    /* renamed from: a */
    private final T[][] f304a = getA();

    /* renamed from: b */
    private final T[] f305b = getB();

    /* renamed from: c */
    private final T[] f306c = getC();
    private final T step;

    /* access modifiers changed from: protected */
    public abstract RungeKuttaFieldStepInterpolator<T> createInterpolator(boolean z, T[][] tArr, FieldODEStateAndDerivative<T> fieldODEStateAndDerivative, FieldODEStateAndDerivative<T> fieldODEStateAndDerivative2, FieldEquationsMapper<T> fieldEquationsMapper);

    protected RungeKuttaFieldIntegrator(Field<T> field, String name, T step2) {
        super(field, name);
        this.step = (T) ((RealFieldElement) step2.abs());
    }

    /* access modifiers changed from: protected */
    public T fraction(int p, int q) {
        return (T) ((RealFieldElement) ((RealFieldElement) getField().getZero().add((double) p)).divide((double) q));
    }

    /* JADX DEBUG: Multi-variable search result rejected for r30v0, resolved type: org.apache.commons.math3.ode.nonstiff.RungeKuttaFieldIntegrator<T extends org.apache.commons.math3.RealFieldElement<T>> */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.ode.FirstOrderFieldIntegrator
    public FieldODEStateAndDerivative<T> integrate(FieldExpandableODE<T> equations, FieldODEState<T> initialState, T finalTime) throws NumberIsTooSmallException, DimensionMismatchException, MaxCountExceededException, NoBracketingException {
        sanityChecks(initialState, finalTime);
        T t0 = initialState.getTime();
        T[] y0 = equations.getMapper().mapState(initialState);
        setStepStart(initIntegration(equations, t0, y0, finalTime));
        boolean forward = ((RealFieldElement) finalTime.subtract(initialState.getTime())).getReal() > 0.0d;
        int stages = this.f306c.length + 1;
        RealFieldElement[][] realFieldElementArr = (RealFieldElement[][]) MathArrays.buildArray(getField(), stages, -1);
        RealFieldElement[] realFieldElementArr2 = (RealFieldElement[]) MathArrays.buildArray(getField(), y0.length);
        if (forward) {
            if (((RealFieldElement) ((RealFieldElement) getStepStart().getTime().add(this.step)).subtract(finalTime)).getReal() >= 0.0d) {
                setStepSize((RealFieldElement) finalTime.subtract(getStepStart().getTime()));
            } else {
                setStepSize(this.step);
            }
        } else if (((RealFieldElement) ((RealFieldElement) getStepStart().getTime().subtract(this.step)).subtract(finalTime)).getReal() <= 0.0d) {
            setStepSize((RealFieldElement) finalTime.subtract(getStepStart().getTime()));
        } else {
            setStepSize((RealFieldElement) this.step.negate());
        }
        setIsLastStep(false);
        do {
            T[] y = equations.getMapper().mapState(getStepStart());
            realFieldElementArr[0] = equations.getMapper().mapDerivative(getStepStart());
            for (int k = 1; k < stages; k++) {
                for (int j = 0; j < y0.length; j++) {
                    RealFieldElement realFieldElement = (RealFieldElement) realFieldElementArr[0][j].multiply(this.f304a[k - 1][0]);
                    for (int l = 1; l < k; l++) {
                        realFieldElement = (RealFieldElement) realFieldElement.add(realFieldElementArr[l][j].multiply(this.f304a[k - 1][l]));
                    }
                    realFieldElementArr2[j] = (RealFieldElement) y[j].add(getStepSize().multiply(realFieldElement));
                }
                realFieldElementArr[k] = computeDerivatives((RealFieldElement) getStepStart().getTime().add(getStepSize().multiply(this.f306c[k - 1])), realFieldElementArr2);
            }
            for (int j2 = 0; j2 < y0.length; j2++) {
                RealFieldElement realFieldElement2 = (RealFieldElement) realFieldElementArr[0][j2].multiply(this.f305b[0]);
                for (int l2 = 1; l2 < stages; l2++) {
                    realFieldElement2 = (RealFieldElement) realFieldElement2.add(realFieldElementArr[l2][j2].multiply(this.f305b[l2]));
                }
                realFieldElementArr2[j2] = (RealFieldElement) y[j2].add(getStepSize().multiply(realFieldElement2));
            }
            RealFieldElement realFieldElement3 = (RealFieldElement) getStepStart().getTime().add(getStepSize());
            FieldODEStateAndDerivative<T> stateTmp = new FieldODEStateAndDerivative<>(realFieldElement3, realFieldElementArr2, computeDerivatives(realFieldElement3, realFieldElementArr2));
            System.arraycopy(realFieldElementArr2, 0, y, 0, y0.length);
            setStepStart(acceptStep(createInterpolator(forward, realFieldElementArr, getStepStart(), stateTmp, equations.getMapper()), finalTime));
            if (!isLastStep()) {
                RealFieldElement realFieldElement4 = (RealFieldElement) getStepStart().getTime().add(getStepSize());
                if (forward ? ((RealFieldElement) realFieldElement4.subtract(finalTime)).getReal() >= 0.0d : ((RealFieldElement) realFieldElement4.subtract(finalTime)).getReal() <= 0.0d) {
                    setStepSize((RealFieldElement) finalTime.subtract(getStepStart().getTime()));
                }
            }
        } while (!isLastStep());
        FieldODEStateAndDerivative<T> finalState = getStepStart();
        setStepStart(null);
        setStepSize(null);
        return finalState;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r14v0, resolved type: org.apache.commons.math3.ode.FirstOrderFieldDifferentialEquations<T extends org.apache.commons.math3.RealFieldElement<T>> */
    /* JADX DEBUG: Multi-variable search result rejected for r7v1, resolved type: T extends org.apache.commons.math3.RealFieldElement<T>[] */
    /* JADX DEBUG: Multi-variable search result rejected for r10v8, resolved type: org.apache.commons.math3.dfp.DfpDec */
    /* JADX DEBUG: Multi-variable search result rejected for r10v24, resolved type: org.apache.commons.math3.dfp.DfpDec */
    /* JADX WARN: Multi-variable type inference failed */
    public T[] singleStep(FirstOrderFieldDifferentialEquations<T> equations, T t0, T[] y0, T t) {
        T[] y = (T[]) ((RealFieldElement[]) y0.clone());
        int stages = this.f306c.length + 1;
        RealFieldElement[][] realFieldElementArr = (RealFieldElement[][]) MathArrays.buildArray(getField(), stages, -1);
        RealFieldElement[] realFieldElementArr2 = (RealFieldElement[]) y0.clone();
        RealFieldElement realFieldElement = (RealFieldElement) t.subtract(t0);
        realFieldElementArr[0] = equations.computeDerivatives(t0, y);
        for (int k = 1; k < stages; k++) {
            for (int j = 0; j < y0.length; j++) {
                RealFieldElement realFieldElement2 = (RealFieldElement) realFieldElementArr[0][j].multiply(this.f304a[k - 1][0]);
                for (int l = 1; l < k; l++) {
                    realFieldElement2 = (RealFieldElement) realFieldElement2.add(realFieldElementArr[l][j].multiply(this.f304a[k - 1][l]));
                }
                realFieldElementArr2[j] = (RealFieldElement) y[j].add(realFieldElement.multiply(realFieldElement2));
            }
            realFieldElementArr[k] = equations.computeDerivatives((RealFieldElement) t0.add(realFieldElement.multiply(this.f306c[k - 1])), realFieldElementArr2);
        }
        for (int j2 = 0; j2 < y0.length; j2++) {
            RealFieldElement realFieldElement3 = (RealFieldElement) realFieldElementArr[0][j2].multiply(this.f305b[0]);
            for (int l2 = 1; l2 < stages; l2++) {
                realFieldElement3 = (RealFieldElement) realFieldElement3.add(realFieldElementArr[l2][j2].multiply(this.f305b[l2]));
            }
            y[j2] = (RealFieldElement) y[j2].add(realFieldElement.multiply(realFieldElement3));
        }
        return y;
    }
}
