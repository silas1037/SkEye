package org.apache.commons.math3.analysis.integration.gauss;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.Pair;

public class GaussIntegrator {
    private final double[] points;
    private final double[] weights;

    public GaussIntegrator(double[] points2, double[] weights2) throws NonMonotonicSequenceException, DimensionMismatchException {
        if (points2.length != weights2.length) {
            throw new DimensionMismatchException(points2.length, weights2.length);
        }
        MathArrays.checkOrder(points2, MathArrays.OrderDirection.INCREASING, true, true);
        this.points = (double[]) points2.clone();
        this.weights = (double[]) weights2.clone();
    }

    public GaussIntegrator(Pair<double[], double[]> pointsAndWeights) throws NonMonotonicSequenceException {
        this(pointsAndWeights.getFirst(), pointsAndWeights.getSecond());
    }

    public double integrate(UnivariateFunction f) {
        double s = 0.0d;
        double c = 0.0d;
        for (int i = 0; i < this.points.length; i++) {
            double x = this.points[i];
            double y = (f.value(x) * this.weights[i]) - c;
            double t = s + y;
            c = (t - s) - y;
            s = t;
        }
        return s;
    }

    public int getNumberOfPoints() {
        return this.points.length;
    }

    public double getPoint(int index) {
        return this.points[index];
    }

    public double getWeight(int index) {
        return this.weights[index];
    }
}
