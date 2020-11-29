package org.apache.commons.math3.analysis.differentiation;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;

public interface UnivariateDifferentiableFunction extends UnivariateFunction {
    DerivativeStructure value(DerivativeStructure derivativeStructure) throws DimensionMismatchException;
}
