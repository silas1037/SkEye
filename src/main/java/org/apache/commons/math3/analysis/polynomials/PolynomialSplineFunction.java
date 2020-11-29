package org.apache.commons.math3.analysis.polynomials;

import java.util.Arrays;
import org.apache.commons.math3.analysis.DifferentiableUnivariateFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.MathArrays;

public class PolynomialSplineFunction implements UnivariateDifferentiableFunction, DifferentiableUnivariateFunction {
    private final double[] knots;

    /* renamed from: n */
    private final int f139n;
    private final PolynomialFunction[] polynomials;

    public PolynomialSplineFunction(double[] knots2, PolynomialFunction[] polynomials2) throws NullArgumentException, NumberIsTooSmallException, DimensionMismatchException, NonMonotonicSequenceException {
        if (knots2 == null || polynomials2 == null) {
            throw new NullArgumentException();
        } else if (knots2.length < 2) {
            throw new NumberIsTooSmallException(LocalizedFormats.NOT_ENOUGH_POINTS_IN_SPLINE_PARTITION, 2, Integer.valueOf(knots2.length), false);
        } else if (knots2.length - 1 != polynomials2.length) {
            throw new DimensionMismatchException(polynomials2.length, knots2.length);
        } else {
            MathArrays.checkOrder(knots2);
            this.f139n = knots2.length - 1;
            this.knots = new double[(this.f139n + 1)];
            System.arraycopy(knots2, 0, this.knots, 0, this.f139n + 1);
            this.polynomials = new PolynomialFunction[this.f139n];
            System.arraycopy(polynomials2, 0, this.polynomials, 0, this.f139n);
        }
    }

    @Override // org.apache.commons.math3.analysis.UnivariateFunction
    public double value(double v) {
        if (v < this.knots[0] || v > this.knots[this.f139n]) {
            throw new OutOfRangeException(Double.valueOf(v), Double.valueOf(this.knots[0]), Double.valueOf(this.knots[this.f139n]));
        }
        int i = Arrays.binarySearch(this.knots, v);
        if (i < 0) {
            i = (-i) - 2;
        }
        if (i >= this.polynomials.length) {
            i--;
        }
        return this.polynomials[i].value(v - this.knots[i]);
    }

    @Override // org.apache.commons.math3.analysis.DifferentiableUnivariateFunction
    public UnivariateFunction derivative() {
        return polynomialSplineDerivative();
    }

    public PolynomialSplineFunction polynomialSplineDerivative() {
        PolynomialFunction[] derivativePolynomials = new PolynomialFunction[this.f139n];
        for (int i = 0; i < this.f139n; i++) {
            derivativePolynomials[i] = this.polynomials[i].polynomialDerivative();
        }
        return new PolynomialSplineFunction(this.knots, derivativePolynomials);
    }

    @Override // org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction
    public DerivativeStructure value(DerivativeStructure t) {
        double t0 = t.getValue();
        if (t0 < this.knots[0] || t0 > this.knots[this.f139n]) {
            throw new OutOfRangeException(Double.valueOf(t0), Double.valueOf(this.knots[0]), Double.valueOf(this.knots[this.f139n]));
        }
        int i = Arrays.binarySearch(this.knots, t0);
        if (i < 0) {
            i = (-i) - 2;
        }
        if (i >= this.polynomials.length) {
            i--;
        }
        return this.polynomials[i].value(t.subtract(this.knots[i]));
    }

    public int getN() {
        return this.f139n;
    }

    public PolynomialFunction[] getPolynomials() {
        PolynomialFunction[] p = new PolynomialFunction[this.f139n];
        System.arraycopy(this.polynomials, 0, p, 0, this.f139n);
        return p;
    }

    public double[] getKnots() {
        double[] out = new double[(this.f139n + 1)];
        System.arraycopy(this.knots, 0, out, 0, this.f139n + 1);
        return out;
    }

    public boolean isValidPoint(double x) {
        if (x < this.knots[0] || x > this.knots[this.f139n]) {
            return false;
        }
        return true;
    }
}
