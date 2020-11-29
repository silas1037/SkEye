package org.apache.commons.math3.ode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.commons.math3.analysis.integration.BaseAbstractUnivariateIntegrator;
import org.apache.commons.math3.analysis.solvers.BracketingNthOrderBrentSolver;
import org.apache.commons.math3.analysis.solvers.UnivariateSolver;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.ode.events.EventHandler;
import org.apache.commons.math3.ode.events.EventState;
import org.apache.commons.math3.ode.sampling.StepHandler;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Incrementor;
import org.apache.commons.math3.util.IntegerSequence;

public abstract class AbstractIntegrator implements FirstOrderIntegrator {
    private IntegerSequence.Incrementor evaluations;
    private Collection<EventState> eventsStates;
    private transient ExpandableStatefulODE expandable;
    protected boolean isLastStep;
    private final String name;
    protected boolean resetOccurred;
    private boolean statesInitialized;
    protected Collection<StepHandler> stepHandlers;
    protected double stepSize;
    protected double stepStart;

    public abstract void integrate(ExpandableStatefulODE expandableStatefulODE, double d) throws NumberIsTooSmallException, DimensionMismatchException, MaxCountExceededException, NoBracketingException;

    public AbstractIntegrator(String name2) {
        this.name = name2;
        this.stepHandlers = new ArrayList();
        this.stepStart = Double.NaN;
        this.stepSize = Double.NaN;
        this.eventsStates = new ArrayList();
        this.statesInitialized = false;
        this.evaluations = IntegerSequence.Incrementor.create().withMaximalCount(BaseAbstractUnivariateIntegrator.DEFAULT_MAX_ITERATIONS_COUNT);
    }

    protected AbstractIntegrator() {
        this(null);
    }

    @Override // org.apache.commons.math3.ode.ODEIntegrator
    public String getName() {
        return this.name;
    }

    @Override // org.apache.commons.math3.ode.ODEIntegrator
    public void addStepHandler(StepHandler handler) {
        this.stepHandlers.add(handler);
    }

    @Override // org.apache.commons.math3.ode.ODEIntegrator
    public Collection<StepHandler> getStepHandlers() {
        return Collections.unmodifiableCollection(this.stepHandlers);
    }

    @Override // org.apache.commons.math3.ode.ODEIntegrator
    public void clearStepHandlers() {
        this.stepHandlers.clear();
    }

    @Override // org.apache.commons.math3.ode.ODEIntegrator
    public void addEventHandler(EventHandler handler, double maxCheckInterval, double convergence, int maxIterationCount) {
        addEventHandler(handler, maxCheckInterval, convergence, maxIterationCount, new BracketingNthOrderBrentSolver(convergence, 5));
    }

    @Override // org.apache.commons.math3.ode.ODEIntegrator
    public void addEventHandler(EventHandler handler, double maxCheckInterval, double convergence, int maxIterationCount, UnivariateSolver solver) {
        this.eventsStates.add(new EventState(handler, maxCheckInterval, convergence, maxIterationCount, solver));
    }

    @Override // org.apache.commons.math3.ode.ODEIntegrator
    public Collection<EventHandler> getEventHandlers() {
        List<EventHandler> list = new ArrayList<>(this.eventsStates.size());
        for (EventState state : this.eventsStates) {
            list.add(state.getEventHandler());
        }
        return Collections.unmodifiableCollection(list);
    }

    @Override // org.apache.commons.math3.ode.ODEIntegrator
    public void clearEventHandlers() {
        this.eventsStates.clear();
    }

    @Override // org.apache.commons.math3.ode.ODEIntegrator
    public double getCurrentStepStart() {
        return this.stepStart;
    }

    @Override // org.apache.commons.math3.ode.ODEIntegrator
    public double getCurrentSignedStepsize() {
        return this.stepSize;
    }

    @Override // org.apache.commons.math3.ode.ODEIntegrator
    public void setMaxEvaluations(int maxEvaluations) {
        IntegerSequence.Incrementor incrementor = this.evaluations;
        if (maxEvaluations < 0) {
            maxEvaluations = BaseAbstractUnivariateIntegrator.DEFAULT_MAX_ITERATIONS_COUNT;
        }
        this.evaluations = incrementor.withMaximalCount(maxEvaluations);
    }

    @Override // org.apache.commons.math3.ode.ODEIntegrator
    public int getMaxEvaluations() {
        return this.evaluations.getMaximalCount();
    }

    @Override // org.apache.commons.math3.ode.ODEIntegrator
    public int getEvaluations() {
        return this.evaluations.getCount();
    }

    /* access modifiers changed from: protected */
    public void initIntegration(double t0, double[] y0, double t) {
        this.evaluations = this.evaluations.withStart(0);
        for (EventState state : this.eventsStates) {
            state.setExpandable(this.expandable);
            state.getEventHandler().init(t0, y0, t);
        }
        for (StepHandler handler : this.stepHandlers) {
            handler.init(t0, y0, t);
        }
        setStateInitialized(false);
    }

    /* access modifiers changed from: protected */
    public void setEquations(ExpandableStatefulODE equations) {
        this.expandable = equations;
    }

    /* access modifiers changed from: protected */
    public ExpandableStatefulODE getExpandable() {
        return this.expandable;
    }

    /* access modifiers changed from: protected */
    @Deprecated
    public Incrementor getEvaluationsCounter() {
        return Incrementor.wrap(this.evaluations);
    }

    /* access modifiers changed from: protected */
    public IntegerSequence.Incrementor getCounter() {
        return this.evaluations;
    }

    @Override // org.apache.commons.math3.ode.FirstOrderIntegrator
    public double integrate(FirstOrderDifferentialEquations equations, double t0, double[] y0, double t, double[] y) throws DimensionMismatchException, NumberIsTooSmallException, MaxCountExceededException, NoBracketingException {
        if (y0.length != equations.getDimension()) {
            throw new DimensionMismatchException(y0.length, equations.getDimension());
        } else if (y.length != equations.getDimension()) {
            throw new DimensionMismatchException(y.length, equations.getDimension());
        } else {
            ExpandableStatefulODE expandableODE = new ExpandableStatefulODE(equations);
            expandableODE.setTime(t0);
            expandableODE.setPrimaryState(y0);
            integrate(expandableODE, t);
            System.arraycopy(expandableODE.getPrimaryState(), 0, y, 0, y.length);
            return expandableODE.getTime();
        }
    }

    public void computeDerivatives(double t, double[] y, double[] yDot) throws MaxCountExceededException, DimensionMismatchException, NullPointerException {
        this.evaluations.increment();
        this.expandable.computeDerivatives(t, y, yDot);
    }

    /* access modifiers changed from: protected */
    public void setStateInitialized(boolean stateInitialized) {
        this.statesInitialized = stateInitialized;
    }

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: CFG modification limit reached, blocks count: 178
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:72)
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:46)
        */
    protected double acceptStep(org.apache.commons.math3.ode.sampling.AbstractStepInterpolator r29, double[] r30, double[] r31, double r32) throws org.apache.commons.math3.exception.MaxCountExceededException, org.apache.commons.math3.exception.DimensionMismatchException, org.apache.commons.math3.exception.NoBracketingException {
        /*
        // Method dump skipped, instructions count: 637
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.ode.AbstractIntegrator.acceptStep(org.apache.commons.math3.ode.sampling.AbstractStepInterpolator, double[], double[], double):double");
    }

    /* access modifiers changed from: protected */
    public void sanityChecks(ExpandableStatefulODE equations, double t) throws NumberIsTooSmallException, DimensionMismatchException {
        double threshold = 1000.0d * FastMath.ulp(FastMath.max(FastMath.abs(equations.getTime()), FastMath.abs(t)));
        double dt = FastMath.abs(equations.getTime() - t);
        if (dt <= threshold) {
            throw new NumberIsTooSmallException(LocalizedFormats.TOO_SMALL_INTEGRATION_INTERVAL, Double.valueOf(dt), Double.valueOf(threshold), false);
        }
    }
}
