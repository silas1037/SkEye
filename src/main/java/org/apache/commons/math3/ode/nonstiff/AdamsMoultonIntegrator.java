package org.apache.commons.math3.ode.nonstiff;

import java.util.Arrays;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrixPreservingVisitor;
import org.apache.commons.math3.ode.EquationsMapper;
import org.apache.commons.math3.ode.ExpandableStatefulODE;
import org.apache.commons.math3.ode.sampling.NordsieckStepInterpolator;
import org.apache.commons.math3.util.FastMath;

public class AdamsMoultonIntegrator extends AdamsIntegrator {
    private static final String METHOD_NAME = "Adams-Moulton";

    public AdamsMoultonIntegrator(int nSteps, double minStep, double maxStep, double scalAbsoluteTolerance, double scalRelativeTolerance) throws NumberIsTooSmallException {
        super(METHOD_NAME, nSteps, nSteps + 1, minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
    }

    public AdamsMoultonIntegrator(int nSteps, double minStep, double maxStep, double[] vecAbsoluteTolerance, double[] vecRelativeTolerance) throws IllegalArgumentException {
        super(METHOD_NAME, nSteps, nSteps + 1, minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
    }

    @Override // org.apache.commons.math3.ode.nonstiff.AdamsIntegrator, org.apache.commons.math3.ode.nonstiff.AdaptiveStepsizeIntegrator, org.apache.commons.math3.ode.AbstractIntegrator
    public void integrate(ExpandableStatefulODE equations, double t) throws NumberIsTooSmallException, DimensionMismatchException, MaxCountExceededException, NoBracketingException {
        sanityChecks(equations, t);
        setEquations(equations);
        boolean forward = t > equations.getTime();
        double[] y0 = equations.getCompleteState();
        double[] y = (double[]) y0.clone();
        double[] yDot = new double[y.length];
        double[] yTmp = new double[y.length];
        double[] predictedScaled = new double[y.length];
        NordsieckStepInterpolator interpolator = new NordsieckStepInterpolator();
        interpolator.reinitialize(y, forward, equations.getPrimaryMapper(), equations.getSecondaryMappers());
        initIntegration(equations.getTime(), y0, t);
        start(equations.getTime(), y, t);
        interpolator.reinitialize(this.stepStart, this.stepSize, this.scaled, this.nordsieck);
        interpolator.storeTime(this.stepStart);
        double hNew = this.stepSize;
        interpolator.rescale(hNew);
        this.isLastStep = false;
        Array2DRowRealMatrix nordsieckTmp = null;
        do {
            double error = 10.0d;
            while (error >= 1.0d) {
                this.stepSize = hNew;
                double stepEnd = this.stepStart + this.stepSize;
                interpolator.setInterpolatedTime(stepEnd);
                ExpandableStatefulODE expandable = getExpandable();
                expandable.getPrimaryMapper().insertEquationData(interpolator.getInterpolatedState(), yTmp);
                int index = 0;
                EquationsMapper[] arr$ = expandable.getSecondaryMappers();
                int len$ = arr$.length;
                for (int i$ = 0; i$ < len$; i$++) {
                    arr$[i$].insertEquationData(interpolator.getInterpolatedSecondaryState(index), yTmp);
                    index++;
                }
                computeDerivatives(stepEnd, yTmp, yDot);
                for (int j = 0; j < y0.length; j++) {
                    predictedScaled[j] = this.stepSize * yDot[j];
                }
                nordsieckTmp = updateHighOrderDerivativesPhase1(this.nordsieck);
                updateHighOrderDerivativesPhase2(this.scaled, predictedScaled, nordsieckTmp);
                error = nordsieckTmp.walkInOptimizedOrder(new Corrector(y, predictedScaled, yTmp));
                if (error >= 1.0d) {
                    hNew = filterStep(this.stepSize * computeStepGrowShrinkFactor(error), forward, false);
                    interpolator.rescale(hNew);
                }
            }
            double stepEnd2 = this.stepStart + this.stepSize;
            computeDerivatives(stepEnd2, yTmp, yDot);
            double[] correctedScaled = new double[y0.length];
            for (int j2 = 0; j2 < y0.length; j2++) {
                correctedScaled[j2] = this.stepSize * yDot[j2];
            }
            updateHighOrderDerivativesPhase2(predictedScaled, correctedScaled, nordsieckTmp);
            System.arraycopy(yTmp, 0, y, 0, y.length);
            interpolator.reinitialize(stepEnd2, this.stepSize, correctedScaled, nordsieckTmp);
            interpolator.storeTime(this.stepStart);
            interpolator.shift();
            interpolator.storeTime(stepEnd2);
            this.stepStart = acceptStep(interpolator, y, yDot, t);
            this.scaled = correctedScaled;
            this.nordsieck = nordsieckTmp;
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

    private class Corrector implements RealMatrixPreservingVisitor {
        private final double[] after;
        private final double[] before;
        private final double[] previous;
        private final double[] scaled;

        Corrector(double[] previous2, double[] scaled2, double[] state) {
            this.previous = previous2;
            this.scaled = scaled2;
            this.after = state;
            this.before = (double[]) state.clone();
        }

        @Override // org.apache.commons.math3.linear.RealMatrixPreservingVisitor
        public void start(int rows, int columns, int startRow, int endRow, int startColumn, int endColumn) {
            Arrays.fill(this.after, 0.0d);
        }

        @Override // org.apache.commons.math3.linear.RealMatrixPreservingVisitor
        public void visit(int row, int column, double value) {
            if ((row & 1) == 0) {
                double[] dArr = this.after;
                dArr[column] = dArr[column] - value;
                return;
            }
            double[] dArr2 = this.after;
            dArr2[column] = dArr2[column] + value;
        }

        @Override // org.apache.commons.math3.linear.RealMatrixPreservingVisitor
        public double end() {
            double error = 0.0d;
            for (int i = 0; i < this.after.length; i++) {
                double[] dArr = this.after;
                dArr[i] = dArr[i] + this.previous[i] + this.scaled[i];
                if (i < AdamsMoultonIntegrator.this.mainSetDimension) {
                    double yScale = FastMath.max(FastMath.abs(this.previous[i]), FastMath.abs(this.after[i]));
                    double ratio = (this.after[i] - this.before[i]) / (AdamsMoultonIntegrator.this.vecAbsoluteTolerance == null ? AdamsMoultonIntegrator.this.scalAbsoluteTolerance + (AdamsMoultonIntegrator.this.scalRelativeTolerance * yScale) : AdamsMoultonIntegrator.this.vecAbsoluteTolerance[i] + (AdamsMoultonIntegrator.this.vecRelativeTolerance[i] * yScale));
                    error += ratio * ratio;
                }
            }
            return FastMath.sqrt(error / ((double) AdamsMoultonIntegrator.this.mainSetDimension));
        }
    }
}
