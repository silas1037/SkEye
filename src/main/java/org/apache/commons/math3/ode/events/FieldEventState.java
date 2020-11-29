package org.apache.commons.math3.ode.events;

import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.analysis.solvers.BracketedRealFieldUnivariateSolver;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FieldODEState;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.ode.sampling.FieldStepInterpolator;
import org.apache.commons.math3.util.FastMath;

public class FieldEventState<T extends RealFieldElement<T>> {
    private final T convergence;
    private boolean forward;

    /* renamed from: g0 */
    private T f259g0 = null;
    private boolean g0Positive = true;
    private final FieldEventHandler<T> handler;
    private boolean increasing = true;
    private final double maxCheckInterval;
    private final int maxIterationCount;
    private Action nextAction = Action.CONTINUE;
    private boolean pendingEvent = false;
    private T pendingEventTime = null;
    private T previousEventTime = null;
    private final BracketedRealFieldUnivariateSolver<T> solver;

    /* renamed from: t0 */
    private T f260t0 = null;

    public FieldEventState(FieldEventHandler<T> handler2, double maxCheckInterval2, T convergence2, int maxIterationCount2, BracketedRealFieldUnivariateSolver<T> solver2) {
        this.handler = handler2;
        this.maxCheckInterval = maxCheckInterval2;
        this.convergence = (T) ((RealFieldElement) convergence2.abs());
        this.maxIterationCount = maxIterationCount2;
        this.solver = solver2;
    }

    public FieldEventHandler<T> getEventHandler() {
        return this.handler;
    }

    public double getMaxCheckInterval() {
        return this.maxCheckInterval;
    }

    public T getConvergence() {
        return this.convergence;
    }

    public int getMaxIterationCount() {
        return this.maxIterationCount;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r11v0, resolved type: org.apache.commons.math3.ode.sampling.FieldStepInterpolator<T extends org.apache.commons.math3.RealFieldElement<T>> */
    /* JADX WARN: Multi-variable type inference failed */
    public void reinitializeBegin(FieldStepInterpolator<T> interpolator) throws MaxCountExceededException {
        FieldODEStateAndDerivative<T> s0 = interpolator.getPreviousState();
        this.f260t0 = s0.getTime();
        this.f259g0 = this.handler.mo2987g(s0);
        if (this.f259g0.getReal() == 0.0d) {
            this.f259g0 = this.handler.mo2987g(interpolator.getInterpolatedState((RealFieldElement) this.f260t0.add(0.5d * FastMath.max(this.solver.getAbsoluteAccuracy().getReal(), FastMath.abs(((RealFieldElement) this.solver.getRelativeAccuracy().multiply(this.f260t0)).getReal())))));
        }
        this.g0Positive = this.f259g0.getReal() >= 0.0d;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v16, types: [org.apache.commons.math3.RealFieldElement] */
    /* JADX WARN: Type inference failed for: r2v62, types: [org.apache.commons.math3.RealFieldElement] */
    /* JADX WARN: Type inference failed for: r2v86, types: [org.apache.commons.math3.RealFieldElement] */
    /* JADX WARNING: Unknown variable types count: 3 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean evaluateStep(final org.apache.commons.math3.ode.sampling.FieldStepInterpolator<T> r23) throws org.apache.commons.math3.exception.MaxCountExceededException, org.apache.commons.math3.exception.NoBracketingException {
        /*
        // Method dump skipped, instructions count: 523
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.ode.events.FieldEventState.evaluateStep(org.apache.commons.math3.ode.sampling.FieldStepInterpolator):boolean");
    }

    public T getEventTime() {
        if (this.pendingEvent) {
            return this.pendingEventTime;
        }
        return (T) ((RealFieldElement) ((RealFieldElement) this.f260t0.getField().getZero()).add(this.forward ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY));
    }

    public void stepAccepted(FieldODEStateAndDerivative<T> state) {
        boolean z = true;
        this.f260t0 = state.getTime();
        this.f259g0 = this.handler.mo2987g(state);
        if (!this.pendingEvent || ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.pendingEventTime.subtract(state.getTime())).abs()).subtract(this.convergence)).getReal() > 0.0d) {
            if (this.f259g0.getReal() < 0.0d) {
                z = false;
            }
            this.g0Positive = z;
            this.nextAction = Action.CONTINUE;
            return;
        }
        this.previousEventTime = state.getTime();
        this.g0Positive = this.increasing;
        this.nextAction = this.handler.eventOccurred(state, !(this.increasing ^ this.forward));
    }

    public boolean stop() {
        return this.nextAction == Action.STOP;
    }

    public FieldODEState<T> reset(FieldODEStateAndDerivative<T> state) {
        FieldODEState<T> newState;
        if (!this.pendingEvent || ((RealFieldElement) ((RealFieldElement) ((RealFieldElement) this.pendingEventTime.subtract(state.getTime())).abs()).subtract(this.convergence)).getReal() > 0.0d) {
            return null;
        }
        if (this.nextAction == Action.RESET_STATE) {
            newState = this.handler.resetState(state);
        } else if (this.nextAction == Action.RESET_DERIVATIVES) {
            newState = state;
        } else {
            newState = null;
        }
        this.pendingEvent = false;
        this.pendingEventTime = null;
        return newState;
    }
}
