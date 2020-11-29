package org.apache.commons.math3.fitting.leastsquares;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public interface ValueAndJacobianFunction extends MultivariateJacobianFunction {
    RealMatrix computeJacobian(double[] dArr);

    RealVector computeValue(double[] dArr);
}
