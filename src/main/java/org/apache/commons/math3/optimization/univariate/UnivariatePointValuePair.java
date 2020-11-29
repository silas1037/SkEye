package org.apache.commons.math3.optimization.univariate;

import java.io.Serializable;

@Deprecated
public class UnivariatePointValuePair implements Serializable {
    private static final long serialVersionUID = 1003888396256744753L;
    private final double point;
    private final double value;

    public UnivariatePointValuePair(double point2, double value2) {
        this.point = point2;
        this.value = value2;
    }

    public double getPoint() {
        return this.point;
    }

    public double getValue() {
        return this.value;
    }
}
