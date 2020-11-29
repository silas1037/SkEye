package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.ode.AbstractIntegrator;
import org.apache.commons.math3.ode.ExpandableStatefulODE;
import org.apache.commons.math3.util.FastMath;

public abstract class AdaptiveStepsizeIntegrator extends AbstractIntegrator {
    private double initialStep;
    protected int mainSetDimension;
    private double maxStep;
    private double minStep;
    protected double scalAbsoluteTolerance;
    protected double scalRelativeTolerance;
    protected double[] vecAbsoluteTolerance;
    protected double[] vecRelativeTolerance;

    @Override // org.apache.commons.math3.ode.AbstractIntegrator
    public abstract void integrate(ExpandableStatefulODE expandableStatefulODE, double d) throws NumberIsTooSmallException, DimensionMismatchException, MaxCountExceededException, NoBracketingException;

    public AdaptiveStepsizeIntegrator(String name, double minStep2, double maxStep2, double scalAbsoluteTolerance2, double scalRelativeTolerance2) {
        super(name);
        setStepSizeControl(minStep2, maxStep2, scalAbsoluteTolerance2, scalRelativeTolerance2);
        resetInternalState();
    }

    public AdaptiveStepsizeIntegrator(String name, double minStep2, double maxStep2, double[] vecAbsoluteTolerance2, double[] vecRelativeTolerance2) {
        super(name);
        setStepSizeControl(minStep2, maxStep2, vecAbsoluteTolerance2, vecRelativeTolerance2);
        resetInternalState();
    }

    public void setStepSizeControl(double minimalStep, double maximalStep, double absoluteTolerance, double relativeTolerance) {
        this.minStep = FastMath.abs(minimalStep);
        this.maxStep = FastMath.abs(maximalStep);
        this.initialStep = -1.0d;
        this.scalAbsoluteTolerance = absoluteTolerance;
        this.scalRelativeTolerance = relativeTolerance;
        this.vecAbsoluteTolerance = null;
        this.vecRelativeTolerance = null;
    }

    public void setStepSizeControl(double minimalStep, double maximalStep, double[] absoluteTolerance, double[] relativeTolerance) {
        this.minStep = FastMath.abs(minimalStep);
        this.maxStep = FastMath.abs(maximalStep);
        this.initialStep = -1.0d;
        this.scalAbsoluteTolerance = 0.0d;
        this.scalRelativeTolerance = 0.0d;
        this.vecAbsoluteTolerance = (double[]) absoluteTolerance.clone();
        this.vecRelativeTolerance = (double[]) relativeTolerance.clone();
    }

    public void setInitialStepSize(double initialStepSize) {
        if (initialStepSize < this.minStep || initialStepSize > this.maxStep) {
            this.initialStep = -1.0d;
        } else {
            this.initialStep = initialStepSize;
        }
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.ode.AbstractIntegrator
    public void sanityChecks(ExpandableStatefulODE equations, double t) throws DimensionMismatchException, NumberIsTooSmallException {
        super.sanityChecks(equations, t);
        this.mainSetDimension = equations.getPrimaryMapper().getDimension();
        if (this.vecAbsoluteTolerance != null && this.vecAbsoluteTolerance.length != this.mainSetDimension) {
            throw new DimensionMismatchException(this.mainSetDimension, this.vecAbsoluteTolerance.length);
        } else if (this.vecRelativeTolerance != null && this.vecRelativeTolerance.length != this.mainSetDimension) {
            throw new DimensionMismatchException(this.mainSetDimension, this.vecRelativeTolerance.length);
        }
    }

    public double initializeStep(boolean forward, int order, double[] scale, double t0, double[] y0, double[] yDot0, double[] y1, double[] yDot1) throws MaxCountExceededException, DimensionMismatchException {
        if (this.initialStep <= 0.0d) {
            double yOnScale2 = 0.0d;
            double yDotOnScale2 = 0.0d;
            for (int j = 0; j < scale.length; j++) {
                double ratio = y0[j] / scale[j];
                yOnScale2 += ratio * ratio;
                double ratio2 = yDot0[j] / scale[j];
                yDotOnScale2 += ratio2 * ratio2;
            }
            double h = (yOnScale2 < 1.0E-10d || yDotOnScale2 < 1.0E-10d) ? 1.0E-6d : 0.01d * FastMath.sqrt(yOnScale2 / yDotOnScale2);
            if (!forward) {
                h = -h;
            }
            for (int j2 = 0; j2 < y0.length; j2++) {
                y1[j2] = y0[j2] + (yDot0[j2] * h);
            }
            computeDerivatives(t0 + h, y1, yDot1);
            double yDDotOnScale = 0.0d;
            for (int j3 = 0; j3 < scale.length; j3++) {
                double ratio3 = (yDot1[j3] - yDot0[j3]) / scale[j3];
                yDDotOnScale += ratio3 * ratio3;
            }
            double maxInv2 = FastMath.max(FastMath.sqrt(yDotOnScale2), FastMath.sqrt(yDDotOnScale) / h);
            double h2 = FastMath.max(FastMath.min(100.0d * FastMath.abs(h), maxInv2 < 1.0E-15d ? FastMath.max(1.0E-6d, 0.001d * FastMath.abs(h)) : FastMath.pow(0.01d / maxInv2, 1.0d / ((double) order))), 1.0E-12d * FastMath.abs(t0));
            if (h2 < getMinStep()) {
                h2 = getMinStep();
            }
            if (h2 > getMaxStep()) {
                h2 = getMaxStep();
            }
            if (!forward) {
                h2 = -h2;
            }
            return h2;
        } else if (forward) {
            return this.initialStep;
        } else {
            return -this.initialStep;
        }
    }

    /* access modifiers changed from: protected */
    public double filterStep(double h, boolean forward, boolean acceptSmall) throws NumberIsTooSmallException {
        double filteredH = h;
        if (FastMath.abs(h) < this.minStep) {
            if (acceptSmall) {
                filteredH = forward ? this.minStep : -this.minStep;
            } else {
                throw new NumberIsTooSmallException(LocalizedFormats.MINIMAL_STEPSIZE_REACHED_DURING_INTEGRATION, Double.valueOf(FastMath.abs(h)), Double.valueOf(this.minStep), true);
            }
        }
        if (filteredH > this.maxStep) {
            return this.maxStep;
        }
        if (filteredH < (-this.maxStep)) {
            return -this.maxStep;
        }
        return filteredH;
    }

    @Override // org.apache.commons.math3.ode.AbstractIntegrator, org.apache.commons.math3.ode.ODEIntegrator
    public double getCurrentStepStart() {
        return this.stepStart;
    }

    /* access modifiers changed from: protected */
    public void resetInternalState() {
        this.stepStart = Double.NaN;
        this.stepSize = FastMath.sqrt(this.minStep * this.maxStep);
    }

    public double getMinStep() {
        return this.minStep;
    }

    public double getMaxStep() {
        return this.maxStep;
    }
}
