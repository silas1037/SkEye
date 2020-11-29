package org.apache.commons.math3.ode.nonstiff;

import java.util.Arrays;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.linear.Array2DRowFieldMatrix;
import org.apache.commons.math3.ode.FieldEquationsMapper;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.ode.sampling.AbstractFieldStepInterpolator;
import org.apache.commons.math3.util.MathArrays;

class AdamsFieldStepInterpolator<T extends RealFieldElement<T>> extends AbstractFieldStepInterpolator<T> {
    private final Array2DRowFieldMatrix<T> nordsieck;
    private final FieldODEStateAndDerivative<T> reference;
    private final T[] scaled;
    private T scalingH;

    AdamsFieldStepInterpolator(T stepSize, FieldODEStateAndDerivative<T> reference2, T[] scaled2, Array2DRowFieldMatrix<T> nordsieck2, boolean isForward, FieldODEStateAndDerivative<T> globalPreviousState, FieldODEStateAndDerivative<T> globalCurrentState, FieldEquationsMapper<T> equationsMapper) {
        this(stepSize, reference2, scaled2, nordsieck2, isForward, globalPreviousState, globalCurrentState, globalPreviousState, globalCurrentState, equationsMapper);
    }

    private AdamsFieldStepInterpolator(T stepSize, FieldODEStateAndDerivative<T> reference2, T[] scaled2, Array2DRowFieldMatrix<T> nordsieck2, boolean isForward, FieldODEStateAndDerivative<T> globalPreviousState, FieldODEStateAndDerivative<T> globalCurrentState, FieldODEStateAndDerivative<T> softPreviousState, FieldODEStateAndDerivative<T> softCurrentState, FieldEquationsMapper<T> equationsMapper) {
        super(isForward, globalPreviousState, globalCurrentState, softPreviousState, softCurrentState, equationsMapper);
        this.scalingH = stepSize;
        this.reference = reference2;
        this.scaled = (T[]) ((RealFieldElement[]) scaled2.clone());
        this.nordsieck = new Array2DRowFieldMatrix<>(nordsieck2.getData(), false);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.ode.sampling.AbstractFieldStepInterpolator
    public AdamsFieldStepInterpolator<T> create(boolean newForward, FieldODEStateAndDerivative<T> newGlobalPreviousState, FieldODEStateAndDerivative<T> newGlobalCurrentState, FieldODEStateAndDerivative<T> newSoftPreviousState, FieldODEStateAndDerivative<T> newSoftCurrentState, FieldEquationsMapper<T> newMapper) {
        return new AdamsFieldStepInterpolator<>(this.scalingH, this.reference, this.scaled, this.nordsieck, newForward, newGlobalPreviousState, newGlobalCurrentState, newSoftPreviousState, newSoftCurrentState, newMapper);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.ode.sampling.AbstractFieldStepInterpolator
    public FieldODEStateAndDerivative<T> computeInterpolatedStateAndDerivatives(FieldEquationsMapper<T> fieldEquationsMapper, T time, T t, T t2, T t3) {
        return taylor(this.reference, time, this.scalingH, this.scaled, this.nordsieck);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r13v13, resolved type: org.apache.commons.math3.dfp.DfpDec */
    /* JADX WARN: Multi-variable type inference failed */
    public static <S extends RealFieldElement<S>> FieldODEStateAndDerivative<S> taylor(FieldODEStateAndDerivative<S> reference2, S time, S stepSize, S[] scaled2, Array2DRowFieldMatrix<S> nordsieck2) {
        RealFieldElement realFieldElement = (RealFieldElement) time.subtract(reference2.getTime());
        RealFieldElement realFieldElement2 = (RealFieldElement) realFieldElement.divide(stepSize);
        RealFieldElement[] realFieldElementArr = (RealFieldElement[]) MathArrays.buildArray(time.getField(), scaled2.length);
        Arrays.fill(realFieldElementArr, time.getField().getZero());
        RealFieldElement[] realFieldElementArr2 = (RealFieldElement[]) MathArrays.buildArray(time.getField(), scaled2.length);
        Arrays.fill(realFieldElementArr2, time.getField().getZero());
        S[][] nData = nordsieck2.getDataRef();
        for (int i = nData.length - 1; i >= 0; i--) {
            int order = i + 2;
            S[] nDataI = nData[i];
            RealFieldElement realFieldElement3 = (RealFieldElement) realFieldElement2.pow(order);
            for (int j = 0; j < nDataI.length; j++) {
                RealFieldElement realFieldElement4 = (RealFieldElement) nDataI[j].multiply(realFieldElement3);
                realFieldElementArr[j] = (RealFieldElement) realFieldElementArr[j].add(realFieldElement4);
                realFieldElementArr2[j] = (RealFieldElement) realFieldElementArr2[j].add(realFieldElement4.multiply(order));
            }
        }
        S[] estimatedState = reference2.getState();
        for (int j2 = 0; j2 < realFieldElementArr.length; j2++) {
            realFieldElementArr[j2] = (RealFieldElement) realFieldElementArr[j2].add(scaled2[j2].multiply(realFieldElement2));
            estimatedState[j2] = (RealFieldElement) estimatedState[j2].add(realFieldElementArr[j2]);
            realFieldElementArr2[j2] = (RealFieldElement) ((RealFieldElement) realFieldElementArr2[j2].add(scaled2[j2].multiply(realFieldElement2))).divide(realFieldElement);
        }
        return new FieldODEStateAndDerivative<>(time, estimatedState, realFieldElementArr2);
    }
}
