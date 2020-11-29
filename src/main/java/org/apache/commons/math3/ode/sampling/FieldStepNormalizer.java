package org.apache.commons.math3.ode.sampling;

import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Precision;

public class FieldStepNormalizer<T extends RealFieldElement<T>> implements FieldStepHandler<T> {
    private final StepNormalizerBounds bounds;
    private FieldODEStateAndDerivative<T> first;
    private boolean forward;

    /* renamed from: h */
    private double f311h;
    private final FieldFixedStepHandler<T> handler;
    private FieldODEStateAndDerivative<T> last;
    private final StepNormalizerMode mode;

    public FieldStepNormalizer(double h, FieldFixedStepHandler<T> handler2) {
        this(h, handler2, StepNormalizerMode.INCREMENT, StepNormalizerBounds.FIRST);
    }

    public FieldStepNormalizer(double h, FieldFixedStepHandler<T> handler2, StepNormalizerMode mode2) {
        this(h, handler2, mode2, StepNormalizerBounds.FIRST);
    }

    public FieldStepNormalizer(double h, FieldFixedStepHandler<T> handler2, StepNormalizerBounds bounds2) {
        this(h, handler2, StepNormalizerMode.INCREMENT, bounds2);
    }

    public FieldStepNormalizer(double h, FieldFixedStepHandler<T> handler2, StepNormalizerMode mode2, StepNormalizerBounds bounds2) {
        this.f311h = FastMath.abs(h);
        this.handler = handler2;
        this.mode = mode2;
        this.bounds = bounds2;
        this.first = null;
        this.last = null;
        this.forward = true;
    }

    @Override // org.apache.commons.math3.ode.sampling.FieldStepHandler
    public void init(FieldODEStateAndDerivative<T> initialState, T finalTime) {
        this.first = null;
        this.last = null;
        this.forward = true;
        this.handler.init(initialState, finalTime);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r10v0, resolved type: org.apache.commons.math3.ode.sampling.FieldStepNormalizer<T extends org.apache.commons.math3.RealFieldElement<T>> */
    /* JADX DEBUG: Multi-variable search result rejected for r11v0, resolved type: org.apache.commons.math3.ode.sampling.FieldStepInterpolator<T extends org.apache.commons.math3.RealFieldElement<T>> */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.ode.sampling.FieldStepHandler
    public void handleStep(FieldStepInterpolator<T> interpolator, boolean isLast) throws MaxCountExceededException {
        RealFieldElement realFieldElement;
        boolean z;
        if (this.last == null) {
            this.first = interpolator.getPreviousState();
            this.last = this.first;
            this.forward = interpolator.isForward();
            if (!this.forward) {
                this.f311h = -this.f311h;
            }
        }
        if (this.mode == StepNormalizerMode.INCREMENT) {
            realFieldElement = (RealFieldElement) this.last.getTime().add(this.f311h);
        } else {
            realFieldElement = (RealFieldElement) ((RealFieldElement) this.last.getTime().getField().getZero()).add((FastMath.floor(this.last.getTime().getReal() / this.f311h) + 1.0d) * this.f311h);
        }
        if (this.mode == StepNormalizerMode.MULTIPLES && Precision.equals(realFieldElement.getReal(), this.last.getTime().getReal(), 1)) {
            realFieldElement = (RealFieldElement) realFieldElement.add(this.f311h);
        }
        boolean nextInStep = isNextInStep(realFieldElement, interpolator);
        while (nextInStep) {
            doNormalizedStep(false);
            this.last = interpolator.getInterpolatedState(realFieldElement);
            realFieldElement = (RealFieldElement) realFieldElement.add(this.f311h);
            nextInStep = isNextInStep(realFieldElement, interpolator);
        }
        if (isLast) {
            boolean addLast = this.bounds.lastIncluded() && this.last.getTime().getReal() != interpolator.getCurrentState().getTime().getReal();
            if (!addLast) {
                z = true;
            } else {
                z = false;
            }
            doNormalizedStep(z);
            if (addLast) {
                this.last = interpolator.getCurrentState();
                doNormalizedStep(true);
            }
        }
    }

    private boolean isNextInStep(T nextTime, FieldStepInterpolator<T> interpolator) {
        return this.forward ? nextTime.getReal() <= interpolator.getCurrentState().getTime().getReal() : nextTime.getReal() >= interpolator.getCurrentState().getTime().getReal();
    }

    private void doNormalizedStep(boolean isLast) {
        if (this.bounds.firstIncluded() || this.first.getTime().getReal() != this.last.getTime().getReal()) {
            this.handler.handleStep(this.last, isLast);
        }
    }
}
