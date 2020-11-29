package org.apache.commons.math3.ode.events;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.AllowedSolution;
import org.apache.commons.math3.analysis.solvers.BracketedUnivariateSolver;
import org.apache.commons.math3.analysis.solvers.PegasusSolver;
import org.apache.commons.math3.analysis.solvers.UnivariateSolver;
import org.apache.commons.math3.analysis.solvers.UnivariateSolverUtils;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.ode.EquationsMapper;
import org.apache.commons.math3.ode.ExpandableStatefulODE;
import org.apache.commons.math3.ode.events.EventHandler;
import org.apache.commons.math3.ode.sampling.StepInterpolator;
import org.apache.commons.math3.util.FastMath;

public class EventState {
    private final double convergence;
    private ExpandableStatefulODE expandable = null;
    private boolean forward;

    /* renamed from: g0 */
    private double f257g0 = Double.NaN;
    private boolean g0Positive = true;
    private final EventHandler handler;
    private boolean increasing = true;
    private final double maxCheckInterval;
    private final int maxIterationCount;
    private EventHandler.Action nextAction = EventHandler.Action.CONTINUE;
    private boolean pendingEvent = false;
    private double pendingEventTime = Double.NaN;
    private double previousEventTime = Double.NaN;
    private final UnivariateSolver solver;

    /* renamed from: t0 */
    private double f258t0 = Double.NaN;

    public EventState(EventHandler handler2, double maxCheckInterval2, double convergence2, int maxIterationCount2, UnivariateSolver solver2) {
        this.handler = handler2;
        this.maxCheckInterval = maxCheckInterval2;
        this.convergence = FastMath.abs(convergence2);
        this.maxIterationCount = maxIterationCount2;
        this.solver = solver2;
    }

    public EventHandler getEventHandler() {
        return this.handler;
    }

    public void setExpandable(ExpandableStatefulODE expandable2) {
        this.expandable = expandable2;
    }

    public double getMaxCheckInterval() {
        return this.maxCheckInterval;
    }

    public double getConvergence() {
        return this.convergence;
    }

    public int getMaxIterationCount() {
        return this.maxIterationCount;
    }

    public void reinitializeBegin(StepInterpolator interpolator) throws MaxCountExceededException {
        this.f258t0 = interpolator.getPreviousTime();
        interpolator.setInterpolatedTime(this.f258t0);
        this.f257g0 = this.handler.mo2971g(this.f258t0, getCompleteState(interpolator));
        if (this.f257g0 == 0.0d) {
            double tStart = this.f258t0 + (0.5d * FastMath.max(this.solver.getAbsoluteAccuracy(), FastMath.abs(this.solver.getRelativeAccuracy() * this.f258t0)));
            interpolator.setInterpolatedTime(tStart);
            this.f257g0 = this.handler.mo2971g(tStart, getCompleteState(interpolator));
        }
        this.g0Positive = this.f257g0 >= 0.0d;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private double[] getCompleteState(StepInterpolator interpolator) {
        double[] complete = new double[this.expandable.getTotalDimension()];
        this.expandable.getPrimaryMapper().insertEquationData(interpolator.getInterpolatedState(), complete);
        EquationsMapper[] arr$ = this.expandable.getSecondaryMappers();
        int len$ = arr$.length;
        int i$ = 0;
        int index = 0;
        while (i$ < len$) {
            arr$[i$].insertEquationData(interpolator.getInterpolatedSecondaryState(index), complete);
            i$++;
            index++;
        }
        return complete;
    }

    public boolean evaluateStep(final StepInterpolator interpolator) throws MaxCountExceededException, NoBracketingException {
        double root;
        try {
            this.forward = interpolator.isForward();
            double t1 = interpolator.getCurrentTime();
            double dt = t1 - this.f258t0;
            if (FastMath.abs(dt) < this.convergence) {
                return false;
            }
            int n = FastMath.max(1, (int) FastMath.ceil(FastMath.abs(dt) / this.maxCheckInterval));
            double h = dt / ((double) n);
            C02931 r7 = new UnivariateFunction() {
                /* class org.apache.commons.math3.ode.events.EventState.C02931 */

                @Override // org.apache.commons.math3.analysis.UnivariateFunction
                public double value(double t) throws LocalMaxCountExceededException {
                    try {
                        interpolator.setInterpolatedTime(t);
                        return EventState.this.handler.mo2971g(t, EventState.this.getCompleteState(interpolator));
                    } catch (MaxCountExceededException mcee) {
                        throw new LocalMaxCountExceededException(mcee);
                    }
                }
            };
            double ta = this.f258t0;
            double ga = this.f257g0;
            int i = 0;
            while (i < n) {
                double tb = i == n + -1 ? t1 : this.f258t0 + (((double) (i + 1)) * h);
                interpolator.setInterpolatedTime(tb);
                double gb = this.handler.mo2971g(tb, getCompleteState(interpolator));
                if ((gb >= 0.0d) ^ this.g0Positive) {
                    this.increasing = gb >= ga;
                    if (this.solver instanceof BracketedUnivariateSolver) {
                        BracketedUnivariateSolver<UnivariateFunction> bracketing = (BracketedUnivariateSolver) this.solver;
                        if (this.forward) {
                            root = bracketing.solve(this.maxIterationCount, r7, ta, tb, AllowedSolution.RIGHT_SIDE);
                        } else {
                            root = bracketing.solve(this.maxIterationCount, r7, tb, ta, AllowedSolution.LEFT_SIDE);
                        }
                    } else {
                        double baseRoot = this.forward ? this.solver.solve(this.maxIterationCount, r7, ta, tb) : this.solver.solve(this.maxIterationCount, r7, tb, ta);
                        int remainingEval = this.maxIterationCount - this.solver.getEvaluations();
                        BracketedUnivariateSolver<UnivariateFunction> bracketing2 = new PegasusSolver(this.solver.getRelativeAccuracy(), this.solver.getAbsoluteAccuracy());
                        if (this.forward) {
                            root = UnivariateSolverUtils.forceSide(remainingEval, r7, bracketing2, baseRoot, ta, tb, AllowedSolution.RIGHT_SIDE);
                        } else {
                            root = UnivariateSolverUtils.forceSide(remainingEval, r7, bracketing2, baseRoot, tb, ta, AllowedSolution.LEFT_SIDE);
                        }
                    }
                    if (!Double.isNaN(this.previousEventTime) && FastMath.abs(root - ta) <= this.convergence && FastMath.abs(root - this.previousEventTime) <= this.convergence) {
                        do {
                            ta = this.forward ? ta + this.convergence : ta - this.convergence;
                            ga = r7.value(ta);
                            if (!((ga >= 0.0d) ^ this.g0Positive)) {
                                break;
                            }
                        } while ((ta >= tb) ^ this.forward);
                        if ((ta >= tb) ^ this.forward) {
                            i--;
                        } else {
                            this.pendingEventTime = root;
                            this.pendingEvent = true;
                            return true;
                        }
                    } else if (Double.isNaN(this.previousEventTime) || FastMath.abs(this.previousEventTime - root) > this.convergence) {
                        this.pendingEventTime = root;
                        this.pendingEvent = true;
                        return true;
                    } else {
                        ta = tb;
                        ga = gb;
                    }
                } else {
                    ta = tb;
                    ga = gb;
                }
                i++;
            }
            this.pendingEvent = false;
            this.pendingEventTime = Double.NaN;
            return false;
        } catch (LocalMaxCountExceededException lmcee) {
            throw lmcee.getException();
        }
    }

    public double getEventTime() {
        if (this.pendingEvent) {
            return this.pendingEventTime;
        }
        return this.forward ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
    }

    public void stepAccepted(double t, double[] y) {
        boolean z = true;
        this.f258t0 = t;
        this.f257g0 = this.handler.mo2971g(t, y);
        if (!this.pendingEvent || FastMath.abs(this.pendingEventTime - t) > this.convergence) {
            if (this.f257g0 < 0.0d) {
                z = false;
            }
            this.g0Positive = z;
            this.nextAction = EventHandler.Action.CONTINUE;
            return;
        }
        this.previousEventTime = t;
        this.g0Positive = this.increasing;
        EventHandler eventHandler = this.handler;
        if (this.increasing ^ this.forward) {
            z = false;
        }
        this.nextAction = eventHandler.eventOccurred(t, y, z);
    }

    public boolean stop() {
        return this.nextAction == EventHandler.Action.STOP;
    }

    public boolean reset(double t, double[] y) {
        if (!this.pendingEvent || FastMath.abs(this.pendingEventTime - t) > this.convergence) {
            return false;
        }
        if (this.nextAction == EventHandler.Action.RESET_STATE) {
            this.handler.resetState(t, y);
        }
        this.pendingEvent = false;
        this.pendingEventTime = Double.NaN;
        if (this.nextAction == EventHandler.Action.RESET_STATE || this.nextAction == EventHandler.Action.RESET_DERIVATIVES) {
            return true;
        }
        return false;
    }

    private static class LocalMaxCountExceededException extends RuntimeException {
        private static final long serialVersionUID = 20120901;
        private final MaxCountExceededException wrapped;

        LocalMaxCountExceededException(MaxCountExceededException exception) {
            this.wrapped = exception;
        }

        public MaxCountExceededException getException() {
            return this.wrapped;
        }
    }
}
