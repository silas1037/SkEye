package org.apache.commons.math3.analysis;

@Deprecated
public interface DifferentiableMultivariateFunction extends MultivariateFunction {
    MultivariateVectorFunction gradient();

    MultivariateFunction partialDerivative(int i);
}
