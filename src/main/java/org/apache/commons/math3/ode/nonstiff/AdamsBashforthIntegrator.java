package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.ode.EquationsMapper;
import org.apache.commons.math3.ode.ExpandableStatefulODE;
import org.apache.commons.math3.ode.sampling.NordsieckStepInterpolator;
import org.apache.commons.math3.util.FastMath;

public class AdamsBashforthIntegrator extends AdamsIntegrator {
    private static final String METHOD_NAME = "Adams-Bashforth";

    public AdamsBashforthIntegrator(int nSteps, double minStep, double maxStep, double scalAbsoluteTolerance, double scalRelativeTolerance) throws NumberIsTooSmallException {
        super(METHOD_NAME, nSteps, nSteps, minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
    }

    public AdamsBashforthIntegrator(int nSteps, double minStep, double maxStep, double[] vecAbsoluteTolerance, double[] vecRelativeTolerance) throws IllegalArgumentException {
        super(METHOD_NAME, nSteps, nSteps, minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
    }

    private double errorEstimation(double[] previousState, double[] predictedState, double[] predictedScaled, RealMatrix predictedNordsieck) {
        double error = 0.0d;
        for (int i = 0; i < this.mainSetDimension; i++) {
            double yScale = FastMath.abs(predictedState[i]);
            double tol = this.vecAbsoluteTolerance == null ? this.scalAbsoluteTolerance + (this.scalRelativeTolerance * yScale) : this.vecAbsoluteTolerance[i] + (this.vecRelativeTolerance[i] * yScale);
            double variation = 0.0d;
            int sign = predictedNordsieck.getRowDimension() % 2 == 0 ? -1 : 1;
            for (int k = predictedNordsieck.getRowDimension() - 1; k >= 0; k--) {
                variation += ((double) sign) * predictedNordsieck.getEntry(k, i);
                sign = -sign;
            }
            double ratio = ((predictedState[i] - previousState[i]) + (variation - predictedScaled[i])) / tol;
            error += ratio * ratio;
        }
        return FastMath.sqrt(error / ((double) this.mainSetDimension));
    }

    @Override // org.apache.commons.math3.ode.nonstiff.AdamsIntegrator, org.apache.commons.math3.ode.nonstiff.AdaptiveStepsizeIntegrator, org.apache.commons.math3.ode.AbstractIntegrator
    public void integrate(ExpandableStatefulODE equations, double t) throws NumberIsTooSmallException, DimensionMismatchException, MaxCountExceededException, NoBracketingException {
        sanityChecks(equations, t);
        setEquations(equations);
        boolean forward = t > equations.getTime();
        double[] y = equations.getCompleteState();
        double[] yDot = new double[y.length];
        NordsieckStepInterpolator interpolator = new NordsieckStepInterpolator();
        interpolator.reinitialize(y, forward, equations.getPrimaryMapper(), equations.getSecondaryMappers());
        initIntegration(equations.getTime(), y, t);
        start(equations.getTime(), y, t);
        interpolator.reinitialize(this.stepStart, this.stepSize, this.scaled, this.nordsieck);
        interpolator.storeTime(this.stepStart);
        double hNew = this.stepSize;
        interpolator.rescale(hNew);
        this.isLastStep = false;
        do {
            interpolator.shift();
            double[] predictedY = new double[y.length];
            double[] predictedScaled = new double[y.length];
            Array2DRowRealMatrix predictedNordsieck = null;
            double error = 10.0d;
            while (error >= 1.0d) {
                double stepEnd = this.stepStart + hNew;
                interpolator.storeTime(stepEnd);
                ExpandableStatefulODE expandable = getExpandable();
                expandable.getPrimaryMapper().insertEquationData(interpolator.getInterpolatedState(), predictedY);
                int index = 0;
                EquationsMapper[] arr$ = expandable.getSecondaryMappers();
                int len$ = arr$.length;
                for (int i$ = 0; i$ < len$; i$++) {
                    arr$[i$].insertEquationData(interpolator.getInterpolatedSecondaryState(index), predictedY);
                    index++;
                }
                computeDerivatives(stepEnd, predictedY, yDot);
                for (int j = 0; j < predictedScaled.length; j++) {
                    predictedScaled[j] = yDot[j] * hNew;
                }
                predictedNordsieck = updateHighOrderDerivativesPhase1(this.nordsieck);
                updateHighOrderDerivativesPhase2(this.scaled, predictedScaled, predictedNordsieck);
                error = errorEstimation(y, predictedY, predictedScaled, predictedNordsieck);
                if (error >= 1.0d) {
                    hNew = filterStep(hNew * computeStepGrowShrinkFactor(error), forward, false);
                    interpolator.rescale(hNew);
                }
            }
            this.stepSize = hNew;
            double stepEnd2 = this.stepStart + this.stepSize;
            interpolator.reinitialize(stepEnd2, this.stepSize, predictedScaled, predictedNordsieck);
            interpolator.storeTime(stepEnd2);
            System.arraycopy(predictedY, 0, y, 0, y.length);
            this.stepStart = acceptStep(interpolator, y, yDot, t);
            this.scaled = predictedScaled;
            this.nordsieck = predictedNordsieck;
            interpolator.reinitialize(stepEnd2, this.stepSize, this.scaled, this.nordsieck);
            if (!this.isLastStep) {
                interpolator.storeTime(this.stepStart);
                if (this.resetOccurred) {
                    start(this.stepStart, y, t);
                    interpolator.reinitialize(this.stepStart, this.stepSize, this.scaled, this.nordsieck);
                }
                double scaledH = this.stepSize * computeStepGrowShrinkFactor(error);
                double nextT = this.stepStart + scaledH;
                hNew = filterStep(scaledH, forward, forward ? nextT >= t : nextT <= t);
                double filteredNextT = this.stepStart + hNew;
                if (forward ? filteredNextT >= t : filteredNextT <= t) {
                    hNew = t - this.stepStart;
                }
                interpolator.rescale(hNew);
            }
        } while (!this.isLastStep);
        equations.setTime(this.stepStart);
        equations.setCompleteState(y);
        resetInternalState();
    }
}
