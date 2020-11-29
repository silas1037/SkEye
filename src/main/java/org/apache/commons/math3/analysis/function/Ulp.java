package org.apache.commons.math3.analysis.function;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.util.FastMath;

public class Ulp implements UnivariateFunction {
    @Override // org.apache.commons.math3.analysis.UnivariateFunction
    public double value(double x) {
        return FastMath.ulp(x);
    }
}
