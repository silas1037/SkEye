package org.apache.commons.math3.analysis.interpolation;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.MathArrays;

public class LinearInterpolator implements UnivariateInterpolator {
    @Override // org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator
    public PolynomialSplineFunction interpolate(double[] x, double[] y) throws DimensionMismatchException, NumberIsTooSmallException, NonMonotonicSequenceException {
        if (x.length != y.length) {
            throw new DimensionMismatchException(x.length, y.length);
        } else if (x.length < 2) {
            throw new NumberIsTooSmallException(LocalizedFormats.NUMBER_OF_POINTS, Integer.valueOf(x.length), 2, true);
        } else {
            int n = x.length - 1;
            MathArrays.checkOrder(x);
            double[] m = new double[n];
            for (int i = 0; i < n; i++) {
                m[i] = (y[i + 1] - y[i]) / (x[i + 1] - x[i]);
            }
            PolynomialFunction[] polynomials = new PolynomialFunction[n];
            double[] coefficients = new double[2];
            for (int i2 = 0; i2 < n; i2++) {
                coefficients[0] = y[i2];
                coefficients[1] = m[i2];
                polynomials[i2] = new PolynomialFunction(coefficients);
            }
            return new PolynomialSplineFunction(x, polynomials);
        }
    }
}
