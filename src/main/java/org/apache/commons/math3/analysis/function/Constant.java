package org.apache.commons.math3.analysis.function;

import org.apache.commons.math3.analysis.DifferentiableUnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;

public class Constant implements UnivariateDifferentiableFunction, DifferentiableUnivariateFunction {

    /* renamed from: c */
    private final double f112c;

    public Constant(double c) {
        this.f112c = c;
    }

    @Override // org.apache.commons.math3.analysis.UnivariateFunction
    public double value(double x) {
        return this.f112c;
    }

    @Override // org.apache.commons.math3.analysis.DifferentiableUnivariateFunction
    @Deprecated
    public DifferentiableUnivariateFunction derivative() {
        return new Constant(0.0d);
    }

    @Override // org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction
    public DerivativeStructure value(DerivativeStructure t) {
        return new DerivativeStructure(t.getFreeParameters(), t.getOrder(), this.f112c);
    }
}
