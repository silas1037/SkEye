package org.apache.commons.math3.ode;

import java.util.Collection;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.analysis.solvers.BracketedRealFieldUnivariateSolver;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.ode.events.FieldEventHandler;
import org.apache.commons.math3.ode.sampling.FieldStepHandler;

public interface FirstOrderFieldIntegrator<T extends RealFieldElement<T>> {
    void addEventHandler(FieldEventHandler<T> fieldEventHandler, double d, double d2, int i);

    void addEventHandler(FieldEventHandler<T> fieldEventHandler, double d, double d2, int i, BracketedRealFieldUnivariateSolver<T> bracketedRealFieldUnivariateSolver);

    void addStepHandler(FieldStepHandler<T> fieldStepHandler);

    void clearEventHandlers();

    void clearStepHandlers();

    T getCurrentSignedStepsize();

    FieldODEStateAndDerivative<T> getCurrentStepStart();

    int getEvaluations();

    Collection<FieldEventHandler<T>> getEventHandlers();

    int getMaxEvaluations();

    String getName();

    Collection<FieldStepHandler<T>> getStepHandlers();

    FieldODEStateAndDerivative<T> integrate(FieldExpandableODE<T> fieldExpandableODE, FieldODEState<T> fieldODEState, T t) throws NumberIsTooSmallException, MaxCountExceededException, NoBracketingException;

    void setMaxEvaluations(int i);
}
