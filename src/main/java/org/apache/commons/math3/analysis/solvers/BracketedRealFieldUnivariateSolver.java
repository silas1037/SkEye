package org.apache.commons.math3.analysis.solvers;

import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.analysis.RealFieldUnivariateFunction;

public interface BracketedRealFieldUnivariateSolver<T extends RealFieldElement<T>> {
    T getAbsoluteAccuracy();

    int getEvaluations();

    T getFunctionValueAccuracy();

    int getMaxEvaluations();

    T getRelativeAccuracy();

    T solve(int i, RealFieldUnivariateFunction<T> realFieldUnivariateFunction, T t, T t2, T t3, AllowedSolution allowedSolution);

    T solve(int i, RealFieldUnivariateFunction<T> realFieldUnivariateFunction, T t, T t2, AllowedSolution allowedSolution);
}
