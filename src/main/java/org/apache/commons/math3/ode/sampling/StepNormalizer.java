package org.apache.commons.math3.ode.sampling;

import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Precision;

public class StepNormalizer implements StepHandler {
    private final StepNormalizerBounds bounds;
    private double firstTime;
    private boolean forward;

    /* renamed from: h */
    private double f312h;
    private final FixedStepHandler handler;
    private double[] lastDerivatives;
    private double[] lastState;
    private double lastTime;
    private final StepNormalizerMode mode;

    public StepNormalizer(double h, FixedStepHandler handler2) {
        this(h, handler2, StepNormalizerMode.INCREMENT, StepNormalizerBounds.FIRST);
    }

    public StepNormalizer(double h, FixedStepHandler handler2, StepNormalizerMode mode2) {
        this(h, handler2, mode2, StepNormalizerBounds.FIRST);
    }

    public StepNormalizer(double h, FixedStepHandler handler2, StepNormalizerBounds bounds2) {
        this(h, handler2, StepNormalizerMode.INCREMENT, bounds2);
    }

    public StepNormalizer(double h, FixedStepHandler handler2, StepNormalizerMode mode2, StepNormalizerBounds bounds2) {
        this.f312h = FastMath.abs(h);
        this.handler = handler2;
        this.mode = mode2;
        this.bounds = bounds2;
        this.firstTime = Double.NaN;
        this.lastTime = Double.NaN;
        this.lastState = null;
        this.lastDerivatives = null;
        this.forward = true;
    }

    @Override // org.apache.commons.math3.ode.sampling.StepHandler
    public void init(double t0, double[] y0, double t) {
        this.firstTime = Double.NaN;
        this.lastTime = Double.NaN;
        this.lastState = null;
        this.lastDerivatives = null;
        this.forward = true;
        this.handler.init(t0, y0, t);
    }

    @Override // org.apache.commons.math3.ode.sampling.StepHandler
    public void handleStep(StepInterpolator interpolator, boolean isLast) throws MaxCountExceededException {
        double nextTime;
        boolean z;
        boolean z2 = false;
        if (this.lastState == null) {
            this.firstTime = interpolator.getPreviousTime();
            this.lastTime = interpolator.getPreviousTime();
            interpolator.setInterpolatedTime(this.lastTime);
            this.lastState = (double[]) interpolator.getInterpolatedState().clone();
            this.lastDerivatives = (double[]) interpolator.getInterpolatedDerivatives().clone();
            if (interpolator.getCurrentTime() >= this.lastTime) {
                z = true;
            } else {
                z = false;
            }
            this.forward = z;
            if (!this.forward) {
                this.f312h = -this.f312h;
            }
        }
        if (this.mode == StepNormalizerMode.INCREMENT) {
            nextTime = this.lastTime + this.f312h;
        } else {
            nextTime = (FastMath.floor(this.lastTime / this.f312h) + 1.0d) * this.f312h;
        }
        if (this.mode == StepNormalizerMode.MULTIPLES && Precision.equals(nextTime, this.lastTime, 1)) {
            nextTime += this.f312h;
        }
        boolean nextInStep = isNextInStep(nextTime, interpolator);
        while (nextInStep) {
            doNormalizedStep(false);
            storeStep(interpolator, nextTime);
            nextTime += this.f312h;
            nextInStep = isNextInStep(nextTime, interpolator);
        }
        if (isLast) {
            boolean addLast = this.bounds.lastIncluded() && this.lastTime != interpolator.getCurrentTime();
            if (!addLast) {
                z2 = true;
            }
            doNormalizedStep(z2);
            if (addLast) {
                storeStep(interpolator, interpolator.getCurrentTime());
                doNormalizedStep(true);
            }
        }
    }

    private boolean isNextInStep(double nextTime, StepInterpolator interpolator) {
        return this.forward ? nextTime <= interpolator.getCurrentTime() : nextTime >= interpolator.getCurrentTime();
    }

    private void doNormalizedStep(boolean isLast) {
        if (this.bounds.firstIncluded() || this.firstTime != this.lastTime) {
            this.handler.handleStep(this.lastTime, this.lastState, this.lastDerivatives, isLast);
        }
    }

    private void storeStep(StepInterpolator interpolator, double t) throws MaxCountExceededException {
        this.lastTime = t;
        interpolator.setInterpolatedTime(this.lastTime);
        System.arraycopy(interpolator.getInterpolatedState(), 0, this.lastState, 0, this.lastState.length);
        System.arraycopy(interpolator.getInterpolatedDerivatives(), 0, this.lastDerivatives, 0, this.lastDerivatives.length);
    }
}
