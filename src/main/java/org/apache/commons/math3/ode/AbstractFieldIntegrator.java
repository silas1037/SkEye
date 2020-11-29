package org.apache.commons.math3.ode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.analysis.integration.BaseAbstractUnivariateIntegrator;
import org.apache.commons.math3.analysis.solvers.BracketedRealFieldUnivariateSolver;
import org.apache.commons.math3.analysis.solvers.FieldBracketingNthOrderBrentSolver;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.ode.events.FieldEventHandler;
import org.apache.commons.math3.ode.events.FieldEventState;
import org.apache.commons.math3.ode.sampling.AbstractFieldStepInterpolator;
import org.apache.commons.math3.ode.sampling.FieldStepHandler;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.IntegerSequence;

public abstract class AbstractFieldIntegrator<T extends RealFieldElement<T>> implements FirstOrderFieldIntegrator<T> {
    private static final double DEFAULT_FUNCTION_VALUE_ACCURACY = 1.0E-15d;
    private static final double DEFAULT_RELATIVE_ACCURACY = 1.0E-14d;
    private transient FieldExpandableODE<T> equations;
    private IntegerSequence.Incrementor evaluations = IntegerSequence.Incrementor.create().withMaximalCount(BaseAbstractUnivariateIntegrator.DEFAULT_MAX_ITERATIONS_COUNT);
    private Collection<FieldEventState<T>> eventsStates = new ArrayList();
    private final Field<T> field;
    private boolean isLastStep;
    private final String name;
    private boolean resetOccurred;
    private boolean statesInitialized = false;
    private Collection<FieldStepHandler<T>> stepHandlers = new ArrayList();
    private T stepSize = null;
    private FieldODEStateAndDerivative<T> stepStart = null;

    protected AbstractFieldIntegrator(Field<T> field2, String name2) {
        this.field = field2;
        this.name = name2;
    }

    public Field<T> getField() {
        return this.field;
    }

    @Override // org.apache.commons.math3.ode.FirstOrderFieldIntegrator
    public String getName() {
        return this.name;
    }

    @Override // org.apache.commons.math3.ode.FirstOrderFieldIntegrator
    public void addStepHandler(FieldStepHandler<T> handler) {
        this.stepHandlers.add(handler);
    }

    @Override // org.apache.commons.math3.ode.FirstOrderFieldIntegrator
    public Collection<FieldStepHandler<T>> getStepHandlers() {
        return Collections.unmodifiableCollection(this.stepHandlers);
    }

    @Override // org.apache.commons.math3.ode.FirstOrderFieldIntegrator
    public void clearStepHandlers() {
        this.stepHandlers.clear();
    }

    @Override // org.apache.commons.math3.ode.FirstOrderFieldIntegrator
    public void addEventHandler(FieldEventHandler<T> handler, double maxCheckInterval, double convergence, int maxIterationCount) {
        addEventHandler(handler, maxCheckInterval, convergence, maxIterationCount, new FieldBracketingNthOrderBrentSolver((RealFieldElement) this.field.getZero().add(DEFAULT_RELATIVE_ACCURACY), (RealFieldElement) this.field.getZero().add(convergence), (RealFieldElement) this.field.getZero().add(1.0E-15d), 5));
    }

    @Override // org.apache.commons.math3.ode.FirstOrderFieldIntegrator
    public void addEventHandler(FieldEventHandler<T> handler, double maxCheckInterval, double convergence, int maxIterationCount, BracketedRealFieldUnivariateSolver<T> solver) {
        this.eventsStates.add(new FieldEventState<>(handler, maxCheckInterval, (RealFieldElement) this.field.getZero().add(convergence), maxIterationCount, solver));
    }

    @Override // org.apache.commons.math3.ode.FirstOrderFieldIntegrator
    public Collection<FieldEventHandler<T>> getEventHandlers() {
        List<FieldEventHandler<T>> list = new ArrayList<>(this.eventsStates.size());
        for (FieldEventState<T> state : this.eventsStates) {
            list.add(state.getEventHandler());
        }
        return Collections.unmodifiableCollection(list);
    }

    @Override // org.apache.commons.math3.ode.FirstOrderFieldIntegrator
    public void clearEventHandlers() {
        this.eventsStates.clear();
    }

    @Override // org.apache.commons.math3.ode.FirstOrderFieldIntegrator
    public FieldODEStateAndDerivative<T> getCurrentStepStart() {
        return this.stepStart;
    }

    @Override // org.apache.commons.math3.ode.FirstOrderFieldIntegrator
    public T getCurrentSignedStepsize() {
        return this.stepSize;
    }

    @Override // org.apache.commons.math3.ode.FirstOrderFieldIntegrator
    public void setMaxEvaluations(int maxEvaluations) {
        IntegerSequence.Incrementor incrementor = this.evaluations;
        if (maxEvaluations < 0) {
            maxEvaluations = BaseAbstractUnivariateIntegrator.DEFAULT_MAX_ITERATIONS_COUNT;
        }
        this.evaluations = incrementor.withMaximalCount(maxEvaluations);
    }

    @Override // org.apache.commons.math3.ode.FirstOrderFieldIntegrator
    public int getMaxEvaluations() {
        return this.evaluations.getMaximalCount();
    }

    @Override // org.apache.commons.math3.ode.FirstOrderFieldIntegrator
    public int getEvaluations() {
        return this.evaluations.getCount();
    }

    /* access modifiers changed from: protected */
    public FieldODEStateAndDerivative<T> initIntegration(FieldExpandableODE<T> eqn, T t0, T[] y0, T t) {
        this.equations = eqn;
        this.evaluations = this.evaluations.withStart(0);
        eqn.init(t0, y0, t);
        FieldODEStateAndDerivative<T> state0 = new FieldODEStateAndDerivative<>(t0, y0, computeDerivatives(t0, y0));
        for (FieldEventState<T> state : this.eventsStates) {
            state.getEventHandler().init(state0, t);
        }
        for (FieldStepHandler<T> handler : this.stepHandlers) {
            handler.init(state0, t);
        }
        setStateInitialized(false);
        return state0;
    }

    /* access modifiers changed from: protected */
    public FieldExpandableODE<T> getEquations() {
        return this.equations;
    }

    /* access modifiers changed from: protected */
    public IntegerSequence.Incrementor getEvaluationsCounter() {
        return this.evaluations;
    }

    public T[] computeDerivatives(T t, T[] y) throws DimensionMismatchException, MaxCountExceededException, NullPointerException {
        this.evaluations.increment();
        return this.equations.computeDerivatives(t, y);
    }

    /* access modifiers changed from: protected */
    public void setStateInitialized(boolean stateInitialized) {
        this.statesInitialized = stateInitialized;
    }

    /* access modifiers changed from: protected */
    public FieldODEStateAndDerivative<T> acceptStep(AbstractFieldStepInterpolator<T> interpolator, T tEnd) throws MaxCountExceededException, DimensionMismatchException, NoBracketingException {
        FieldODEStateAndDerivative<T> previousState = interpolator.getGlobalPreviousState();
        FieldODEStateAndDerivative<T> currentState = interpolator.getGlobalCurrentState();
        if (!this.statesInitialized) {
            for (FieldEventState<T> state : this.eventsStates) {
                state.reinitializeBegin(interpolator);
            }
            this.statesInitialized = true;
        }
        final int orderingSign = interpolator.isForward() ? 1 : -1;
        SortedSet<FieldEventState<T>> occurringEvents = new TreeSet<>(new Comparator<FieldEventState<T>>() {
            /* class org.apache.commons.math3.ode.AbstractFieldIntegrator.C02891 */

            @Override // java.util.Comparator
            public /* bridge */ /* synthetic */ int compare(Object obj, Object obj2) {
                return compare((FieldEventState) ((FieldEventState) obj), (FieldEventState) ((FieldEventState) obj2));
            }

            public int compare(FieldEventState<T> es0, FieldEventState<T> es1) {
                return orderingSign * Double.compare(es0.getEventTime().getReal(), es1.getEventTime().getReal());
            }
        });
        for (FieldEventState<T> state2 : this.eventsStates) {
            if (state2.evaluateStep(interpolator)) {
                occurringEvents.add(state2);
            }
        }
        AbstractFieldStepInterpolator<T> restricted = interpolator;
        while (!occurringEvents.isEmpty()) {
            Iterator<FieldEventState<T>> iterator = occurringEvents.iterator();
            FieldEventState<T> currentEvent = iterator.next();
            iterator.remove();
            FieldODEStateAndDerivative<T> eventState = restricted.getInterpolatedState(currentEvent.getEventTime());
            AbstractFieldStepInterpolator<T> restricted2 = restricted.restrictStep(previousState, eventState);
            for (FieldEventState<T> state3 : this.eventsStates) {
                state3.stepAccepted(eventState);
                this.isLastStep = this.isLastStep || state3.stop();
            }
            for (FieldStepHandler<T> handler : this.stepHandlers) {
                handler.handleStep(restricted2, this.isLastStep);
            }
            if (this.isLastStep) {
                return eventState;
            }
            this.resetOccurred = false;
            for (FieldEventState<T> state4 : this.eventsStates) {
                FieldODEState<T> newState = state4.reset(eventState);
                if (newState != null) {
                    T[] y = this.equations.getMapper().mapState(newState);
                    T[] yDot = computeDerivatives(newState.getTime(), y);
                    this.resetOccurred = true;
                    return this.equations.getMapper().mapStateAndDerivative(newState.getTime(), y, yDot);
                }
            }
            previousState = eventState;
            restricted = restricted2.restrictStep(eventState, currentState);
            if (currentEvent.evaluateStep(restricted)) {
                occurringEvents.add(currentEvent);
            }
        }
        for (FieldEventState<T> state5 : this.eventsStates) {
            state5.stepAccepted(currentState);
            this.isLastStep = this.isLastStep || state5.stop();
        }
        this.isLastStep = this.isLastStep || ((RealFieldElement) ((RealFieldElement) currentState.getTime().subtract(tEnd)).abs()).getReal() <= FastMath.ulp(tEnd.getReal());
        for (FieldStepHandler<T> handler2 : this.stepHandlers) {
            handler2.handleStep(restricted, this.isLastStep);
        }
        return currentState;
    }

    /* access modifiers changed from: protected */
    public void sanityChecks(FieldODEState<T> eqn, T t) throws NumberIsTooSmallException, DimensionMismatchException {
        double threshold = 1000.0d * FastMath.ulp(FastMath.max(FastMath.abs(eqn.getTime().getReal()), FastMath.abs(t.getReal())));
        double dt = ((RealFieldElement) ((RealFieldElement) eqn.getTime().subtract(t)).abs()).getReal();
        if (dt <= threshold) {
            throw new NumberIsTooSmallException(LocalizedFormats.TOO_SMALL_INTEGRATION_INTERVAL, Double.valueOf(dt), Double.valueOf(threshold), false);
        }
    }

    /* access modifiers changed from: protected */
    public boolean resetOccurred() {
        return this.resetOccurred;
    }

    /* access modifiers changed from: protected */
    public void setStepSize(T stepSize2) {
        this.stepSize = stepSize2;
    }

    /* access modifiers changed from: protected */
    public T getStepSize() {
        return this.stepSize;
    }

    /* access modifiers changed from: protected */
    public void setStepStart(FieldODEStateAndDerivative<T> stepStart2) {
        this.stepStart = stepStart2;
    }

    /* access modifiers changed from: protected */
    public FieldODEStateAndDerivative<T> getStepStart() {
        return this.stepStart;
    }

    /* access modifiers changed from: protected */
    public void setIsLastStep(boolean isLastStep2) {
        this.isLastStep = isLastStep2;
    }

    /* access modifiers changed from: protected */
    public boolean isLastStep() {
        return this.isLastStep;
    }
}
