package org.apache.commons.math3.analysis.solvers;

import org.apache.commons.math3.analysis.UnivariateFunction;

public interface BracketedUnivariateSolver<FUNC extends UnivariateFunction> extends BaseUnivariateSolver<FUNC> {
    double solve(int i, FUNC func, double d, double d2, double d3, AllowedSolution allowedSolution);

    double solve(int i, FUNC func, double d, double d2, AllowedSolution allowedSolution);
}
