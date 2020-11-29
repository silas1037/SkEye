package org.apache.commons.math3.ode;

import java.util.Collection;
import org.apache.commons.math3.analysis.solvers.UnivariateSolver;
import org.apache.commons.math3.ode.events.EventHandler;
import org.apache.commons.math3.ode.sampling.StepHandler;

public interface ODEIntegrator {
    void addEventHandler(EventHandler eventHandler, double d, double d2, int i);

    void addEventHandler(EventHandler eventHandler, double d, double d2, int i, UnivariateSolver univariateSolver);

    void addStepHandler(StepHandler stepHandler);

    void clearEventHandlers();

    void clearStepHandlers();

    double getCurrentSignedStepsize();

    double getCurrentStepStart();

    int getEvaluations();

    Collection<EventHandler> getEventHandlers();

    int getMaxEvaluations();

    String getName();

    Collection<StepHandler> getStepHandlers();

    void setMaxEvaluations(int i);
}
