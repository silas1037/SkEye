package org.apache.commons.math3.ode.nonstiff;

import java.util.Arrays;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.linear.Array2DRowFieldMatrix;
import org.apache.commons.math3.linear.FieldMatrixPreservingVisitor;
import org.apache.commons.math3.ode.FieldExpandableODE;
import org.apache.commons.math3.ode.FieldODEState;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.MathUtils;

public class AdamsMoultonFieldIntegrator<T extends RealFieldElement<T>> extends AdamsFieldIntegrator<T> {
    private static final String METHOD_NAME = "Adams-Moulton";

    public AdamsMoultonFieldIntegrator(Field<T> field, int nSteps, double minStep, double maxStep, double scalAbsoluteTolerance, double scalRelativeTolerance) throws NumberIsTooSmallException {
        super(field, METHOD_NAME, nSteps, nSteps + 1, minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
    }

    public AdamsMoultonFieldIntegrator(Field<T> field, int nSteps, double minStep, double maxStep, double[] vecAbsoluteTolerance, double[] vecRelativeTolerance) throws IllegalArgumentException {
        super(field, METHOD_NAME, nSteps, nSteps + 1, minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r33v0, resolved type: org.apache.commons.math3.ode.nonstiff.AdamsMoultonFieldIntegrator<T extends org.apache.commons.math3.RealFieldElement<T>> */
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
            T t = (RealFieldElement) ((RealFieldElement) getField().getZero()).add(10.0d);
            while (((RealFieldElement) t.subtract(1.0d)).getReal() >= 0.0d) {
                predictedY = stepEnd.getState();
                T[] yDot = computeDerivatives(stepEnd.getTime(), predictedY);
                for (int j = 0; j < realFieldElementArr.length; j++) {
                    realFieldElementArr[j] = (RealFieldElement) getStepSize().multiply(yDot[j]);
                }
                predictedNordsieck = updateHighOrderDerivativesPhase1(this.nordsieck);
                updateHighOrderDerivativesPhase2(this.scaled, realFieldElementArr, predictedNordsieck);
                t = predictedNordsieck.walkInOptimizedOrder(new Corrector(y, realFieldElementArr, predictedY));
                if (((RealFieldElement) t.subtract(1.0d)).getReal() >= 0.0d) {
                    rescale(filterStep((RealFieldElement) getStepSize().multiply(computeStepGrowShrinkFactor(t)), forward, false));
                    stepEnd = AdamsFieldStepInterpolator.taylor(getStepStart(), (RealFieldElement) getStepStart().getTime().add(getStepSize()), getStepSize(), this.scaled, this.nordsieck);
                } else {
                    stepEnd = stepEnd;
                }
            }
            T[] correctedYDot = computeDerivatives(stepEnd.getTime(), predictedY);
            RealFieldElement[] realFieldElementArr2 = (RealFieldElement[]) MathArrays.buildArray(getField(), y.length);
            for (int j2 = 0; j2 < realFieldElementArr2.length; j2++) {
                realFieldElementArr2[j2] = (RealFieldElement) getStepSize().multiply(correctedYDot[j2]);
            }
            updateHighOrderDerivativesPhase2(realFieldElementArr, realFieldElementArr2, predictedNordsieck);
            stepEnd = new FieldODEStateAndDerivative<>(stepEnd.getTime(), predictedY, correctedYDot);
            setStepStart(acceptStep(new AdamsFieldStepInterpolator(getStepSize(), stepEnd, realFieldElementArr2, predictedNordsieck, forward, getStepStart(), stepEnd, equations.getMapper()), finalTime));
            this.scaled = realFieldElementArr2;
            this.nordsieck = predictedNordsieck;
            if (!isLastStep()) {
                System.arraycopy(predictedY, 0, y, 0, y.length);
                if (resetOccurred()) {
                    start(equations, getStepStart(), finalTime);
                }
                RealFieldElement realFieldElement = (RealFieldElement) getStepSize().multiply(computeStepGrowShrinkFactor(t));
                RealFieldElement realFieldElement2 = (RealFieldElement) getStepStart().getTime().add(realFieldElement);
                RealFieldElement filterStep = filterStep(realFieldElement, forward, forward ? ((RealFieldElement) realFieldElement2.subtract(finalTime)).getReal() >= 0.0d : ((RealFieldElement) realFieldElement2.subtract(finalTime)).getReal() <= 0.0d);
                RealFieldElement realFieldElement3 = (RealFieldElement) getStepStart().getTime().add(filterStep);
                if (forward ? ((RealFieldElement) realFieldElement3.subtract(finalTime)).getReal() >= 0.0d : ((RealFieldElement) realFieldElement3.subtract(finalTime)).getReal() <= 0.0d) {
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

    private class Corrector implements FieldMatrixPreservingVisitor<T> {
        private final T[] after;
        private final T[] before;
        private final T[] previous;
        private final T[] scaled;

        Corrector(T[] previous2, T[] scaled2, T[] state) {
            this.previous = previous2;
            this.scaled = scaled2;
            this.after = state;
            this.before = (T[]) ((RealFieldElement[]) state.clone());
        }

        @Override // org.apache.commons.math3.linear.FieldMatrixPreservingVisitor
        public void start(int rows, int columns, int startRow, int endRow, int startColumn, int endColumn) {
            Arrays.fill(this.after, AdamsMoultonFieldIntegrator.this.getField().getZero());
        }

        /* JADX DEBUG: Multi-variable search result rejected for r1v0, resolved type: T[] */
        /* JADX DEBUG: Multi-variable search result rejected for r1v1, resolved type: T[] */
        /* JADX WARN: Multi-variable type inference failed */
        public void visit(int row, int column, T value) {
            if ((row & 1) == 0) {
                this.after[column] = (RealFieldElement) this.after[column].subtract(value);
            } else {
                this.after[column] = (RealFieldElement) this.after[column].add(value);
            }
        }

        /* JADX DEBUG: Multi-variable search result rejected for r6v1, resolved type: T[] */
        /* JADX WARN: Multi-variable type inference failed */
        @Override // org.apache.commons.math3.linear.FieldMatrixPreservingVisitor
        public T end() {
            T zero = AdamsMoultonFieldIntegrator.this.getField().getZero();
            for (int i = 0; i < this.after.length; i++) {
                this.after[i] = (RealFieldElement) this.after[i].add(this.previous[i].add(this.scaled[i]));
                if (i < AdamsMoultonFieldIntegrator.this.mainSetDimension) {
                    RealFieldElement max = MathUtils.max((RealFieldElement) this.previous[i].abs(), (RealFieldElement) this.after[i].abs());
                    RealFieldElement realFieldElement = (RealFieldElement) ((RealFieldElement) this.after[i].subtract(this.before[i])).divide(AdamsMoultonFieldIntegrator.this.vecAbsoluteTolerance == null ? (RealFieldElement) ((RealFieldElement) max.multiply(AdamsMoultonFieldIntegrator.this.scalRelativeTolerance)).add(AdamsMoultonFieldIntegrator.this.scalAbsoluteTolerance) : (RealFieldElement) ((RealFieldElement) max.multiply(AdamsMoultonFieldIntegrator.this.vecRelativeTolerance[i])).add(AdamsMoultonFieldIntegrator.this.vecAbsoluteTolerance[i]));
                    zero = (RealFieldElement) zero.add(realFieldElement.multiply(realFieldElement));
                }
            }
            return (T) ((RealFieldElement) ((RealFieldElement) zero.divide((double) AdamsMoultonFieldIntegrator.this.mainSetDimension)).sqrt());
        }
    }
}
