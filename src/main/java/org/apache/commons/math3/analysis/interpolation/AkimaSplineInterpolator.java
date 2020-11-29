package org.apache.commons.math3.analysis.interpolation;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.Precision;

public class AkimaSplineInterpolator implements UnivariateInterpolator {
    private static final int MINIMUM_NUMBER_POINTS = 5;

    @Override // org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator
    public PolynomialSplineFunction interpolate(double[] xvals, double[] yvals) throws DimensionMismatchException, NumberIsTooSmallException, NonMonotonicSequenceException {
        if (xvals == null || yvals == null) {
            throw new NullArgumentException();
        } else if (xvals.length != yvals.length) {
            throw new DimensionMismatchException(xvals.length, yvals.length);
        } else if (xvals.length < 5) {
            throw new NumberIsTooSmallException(LocalizedFormats.NUMBER_OF_POINTS, Integer.valueOf(xvals.length), 5, true);
        } else {
            MathArrays.checkOrder(xvals);
            int numberOfDiffAndWeightElements = xvals.length - 1;
            double[] differences = new double[numberOfDiffAndWeightElements];
            double[] weights = new double[numberOfDiffAndWeightElements];
            for (int i = 0; i < differences.length; i++) {
                differences[i] = (yvals[i + 1] - yvals[i]) / (xvals[i + 1] - xvals[i]);
            }
            for (int i2 = 1; i2 < weights.length; i2++) {
                weights[i2] = FastMath.abs(differences[i2] - differences[i2 - 1]);
            }
            double[] firstDerivatives = new double[xvals.length];
            for (int i3 = 2; i3 < firstDerivatives.length - 2; i3++) {
                double wP = weights[i3 + 1];
                double wM = weights[i3 - 1];
                if (!Precision.equals(wP, 0.0d) || !Precision.equals(wM, 0.0d)) {
                    firstDerivatives[i3] = ((differences[i3 - 1] * wP) + (differences[i3] * wM)) / (wP + wM);
                } else {
                    double xv = xvals[i3];
                    double xvP = xvals[i3 + 1];
                    double xvM = xvals[i3 - 1];
                    firstDerivatives[i3] = (((xvP - xv) * differences[i3 - 1]) + ((xv - xvM) * differences[i3])) / (xvP - xvM);
                }
            }
            firstDerivatives[0] = differentiateThreePoint(xvals, yvals, 0, 0, 1, 2);
            firstDerivatives[1] = differentiateThreePoint(xvals, yvals, 1, 0, 1, 2);
            firstDerivatives[xvals.length - 2] = differentiateThreePoint(xvals, yvals, xvals.length - 2, xvals.length - 3, xvals.length - 2, xvals.length - 1);
            firstDerivatives[xvals.length - 1] = differentiateThreePoint(xvals, yvals, xvals.length - 1, xvals.length - 3, xvals.length - 2, xvals.length - 1);
            return interpolateHermiteSorted(xvals, yvals, firstDerivatives);
        }
    }

    private double differentiateThreePoint(double[] xvals, double[] yvals, int indexOfDifferentiation, int indexOfFirstSample, int indexOfSecondsample, int indexOfThirdSample) {
        double x0 = yvals[indexOfFirstSample];
        double x1 = yvals[indexOfSecondsample];
        double x2 = yvals[indexOfThirdSample];
        double t = xvals[indexOfDifferentiation] - xvals[indexOfFirstSample];
        double t1 = xvals[indexOfSecondsample] - xvals[indexOfFirstSample];
        double t2 = xvals[indexOfThirdSample] - xvals[indexOfFirstSample];
        double a = ((x2 - x0) - ((t2 / t1) * (x1 - x0))) / ((t2 * t2) - (t1 * t2));
        return (2.0d * a * t) + (((x1 - x0) - ((a * t1) * t1)) / t1);
    }

    private PolynomialSplineFunction interpolateHermiteSorted(double[] xvals, double[] yvals, double[] firstDerivatives) {
        if (xvals.length != yvals.length) {
            throw new DimensionMismatchException(xvals.length, yvals.length);
        } else if (xvals.length != firstDerivatives.length) {
            throw new DimensionMismatchException(xvals.length, firstDerivatives.length);
        } else if (xvals.length < 2) {
            throw new NumberIsTooSmallException(LocalizedFormats.NUMBER_OF_POINTS, Integer.valueOf(xvals.length), 2, true);
        } else {
            PolynomialFunction[] polynomials = new PolynomialFunction[(xvals.length - 1)];
            double[] coefficients = new double[4];
            for (int i = 0; i < polynomials.length; i++) {
                double w = xvals[i + 1] - xvals[i];
                double yv = yvals[i];
                double yvP = yvals[i + 1];
                double fd = firstDerivatives[i];
                double fdP = firstDerivatives[i + 1];
                coefficients[0] = yv;
                coefficients[1] = firstDerivatives[i];
                coefficients[2] = ((((3.0d * (yvP - yv)) / w) - (2.0d * fd)) - fdP) / w;
                coefficients[3] = ((((2.0d * (yv - yvP)) / w) + fd) + fdP) / (w * w);
                polynomials[i] = new PolynomialFunction(coefficients);
            }
            return new PolynomialSplineFunction(xvals, polynomials);
        }
    }
}
