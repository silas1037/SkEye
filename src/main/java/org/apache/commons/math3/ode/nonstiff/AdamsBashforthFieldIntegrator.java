package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.linear.Array2DRowFieldMatrix;
import org.apache.commons.math3.linear.FieldMatrix;
import org.apache.commons.math3.ode.FieldExpandableODE;
import org.apache.commons.math3.ode.FieldODEState;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.util.MathArrays;

public class AdamsBashforthFieldIntegrator<T extends RealFieldElement<T>> extends AdamsFieldIntegrator<T> {
    private static final String METHOD_NAME = "Adams-Bashforth";

    public AdamsBashforthFieldIntegrator(Field<T> field, int nSteps, double minStep, double maxStep, double scalAbsoluteTolerance, double scalRelativeTolerance) throws NumberIsTooSmallException {
        super(field, METHOD_NAME, nSteps, nSteps, minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
    }

    public AdamsBashforthFieldIntegrator(Field<T> field, int nSteps, double minStep, double maxStep, double[] vecAbsoluteTolerance, double[] vecRelativeTolerance) throws IllegalArgumentException {
        super(field, METHOD_NAME, nSteps, nSteps, minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
    }

    private T errorEstimation(T[] previousState, T[] predictedState, T[] predictedScaled, FieldMatrix<T> predictedNordsieck) {
        T zero = getField().getZero();
        for (int i = 0; i < this.mainSetDimension; i++) {
            RealFieldElement realFieldElement = (RealFieldElement) predictedState[i].abs();
            RealFieldElement realFieldElement2 = this.vecAbsoluteTolerance == null ? (RealFieldElement) ((RealFieldElement) realFieldElement.multiply(this.scalRelativeTolerance)).add(this.scalAbsoluteTolerance) : (RealFieldElement) ((RealFieldElement) realFieldElement.multiply(this.vecRelativeTolerance[i])).add(this.vecAbsoluteTolerance[i]);
            T zero2 = getField().getZero();
            int sign = predictedNordsieck.getRowDimension() % 2 == 0 ? -1 : 1;
            for (int k = predictedNordsieck.getRowDimension() - 1; k >= 0; k--) {
                zero2 = (RealFieldElement) zero2.add(predictedNordsieck.getEntry(k, i).multiply(sign));
                sign = -sign;
            }
            RealFieldElement realFieldElement3 = (RealFieldElement) ((RealFieldElement) ((RealFieldElement) predictedState[i].subtract(previousState[i])).add((RealFieldElement) zero2.subtract(predictedScaled[i]))).divide(realFieldElement2);
            zero = (RealFieldElement) zero.add(realFieldElement3.multiply(realFieldElement3));
        }
        return (T) ((RealFieldElement) ((RealFieldElement) zero.divide((double) this.mainSetDimension)).sqrt());
    }

    /* JADX DEBUG: Multi-variable search result rejected for r30v0, resolved type: org.apache.commons.math3.ode.nonstiff.AdamsBashforthFieldIntegrator<T extends org.apache.commons.math3.RealFieldElement<T>> */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.ode.FirstOrderFieldIntegrator, org.apache.commons.math3.ode.nonstiff.AdamsFieldIntegrator
    public FieldODEStateAndDerivative<T> integrate(FieldExpandableODE<T> equations, FieldODEState<T> initialState, T finalTime) throws NumberIsTooSmallException, DimensionMismatchException, MaxCountExceededException, NoBracketingException {
        sanityChecks(initialState, finalTime);
        T t0 = initialState.getTime();
        T[] y = equations.getMapper().mapState(initialState);
        setStepStart(initIntegration(equations, t0, y, finalTime));
        boolean forward = ((RealFieldElement) finalTime.subtract(initialState.getTime())).getReal() > 0.0d;
        start(equations, getStepStart(), finalTime);
        FieldODEStateAndDerivative<T> stepStart = getStepStart();
        FieldODEStateAndDerivative<T> stepEnd = AdamsFieldStepInterpolator.taylor(stepStart, (RealFieldElement) stepStart.getTime().add(getStepSize()), getStepSize(), this.scaled, this.nordsieck);
        setIsLastStep(false);
        do {
            T[] predictedY = null;
            RealFieldElement[] realFieldElementArr = (RealFieldElement[]) MathArrays.buildArray(getField(), y.length);
            Array2DRowFieldMatrix<T> predictedNordsieck = null;
            RealFieldElement realFieldElement = (RealFieldElement) ((RealFieldElement) getField().getZero()).add(10.0d);
            while (((RealFieldElement) realFieldElement.subtract(1.0d)).getReal() >= 0.0d) {
                predictedY = stepEnd.getState();
                T[] yDot = computeDerivatives(stepEnd.getTime(), predictedY);
                for (int j = 0; j < realFieldElementArr.length; j++) {
                    realFieldElementArr[j] = (RealFieldElement) getStepSize().multiply(yDot[j]);
                }
                predictedNordsieck = updateHighOrderDerivativesPhase1(this.nordsieck);
                updateHighOrderDerivativesPhase2(this.scaled, realFieldElementArr, predictedNordsieck);
                realFieldElement = errorEstimation(y, predictedY, realFieldElementArr, predictedNordsieck);
                if (((RealFieldElement) realFieldElement.subtract(1.0d)).getReal() >= 0.0d) {
                    rescale(filterStep((RealFieldElement) getStepSize().multiply(computeStepGrowShrinkFactor(realFieldElement)), forward, false));
                    stepEnd = AdamsFieldStepInterpolator.taylor(getStepStart(), (RealFieldElement) getStepStart().getTime().add(getStepSize()), getStepSize(), this.scaled, this.nordsieck);
                }
            }
            setStepStart(acceptStep(new AdamsFieldStepInterpolator(getStepSize(), stepEnd, realFieldElementArr, predictedNordsieck, forward, getStepStart(), stepEnd, equations.getMapper()), finalTime));
            this.scaled = realFieldElementArr;
            this.nordsieck = predictedNordsieck;
            if (!isLastStep()) {
                System.arraycopy(predictedY, 0, y, 0, y.length);
                if (resetOccurred()) {
                    start(equations, getStepStart(), finalTime);
                }
                RealFieldElement realFieldElement2 = (RealFieldElement) getStepSize().multiply(computeStepGrowShrinkFactor(realFieldElement));
                RealFieldElement realFieldElement3 = (RealFieldElement) getStepStart().getTime().add(realFieldElement2);
                RealFieldElement filterStep = filterStep(realFieldElement2, forward, forward ? ((RealFieldElement) realFieldElement3.subtract(finalTime)).getReal() >= 0.0d : ((RealFieldElement) realFieldElement3.subtract(finalTime)).getReal() <= 0.0d);
                RealFieldElement realFieldElement4 = (RealFieldElement) getStepStart().getTime().add(filterStep);
                if (forward ? ((RealFieldElement) realFieldElement4.subtract(finalTime)).getReal() >= 0.0d : ((RealFieldElement) realFieldElement4.subtract(finalTime)).getReal() <= 0.0d) {
                    filterStep = (RealFieldElement) finalTime.subtract(getStepStart().getTime());
                }
                rescale(filterStep);
                stepEnd = AdamsFieldStepInterpolator.taylor(getStepStart(), (RealFieldElement) getStepStart().getTime().add(getStepSize()), getStepSize(), this.scaled, this.nordsieck);
            }
        } while (!isLastStep());
        FieldODEStateAndDerivative<T> finalState = getStepStart();
        setStepStart(null);
        setStepSize(null);
        return finalState;
    }
}
