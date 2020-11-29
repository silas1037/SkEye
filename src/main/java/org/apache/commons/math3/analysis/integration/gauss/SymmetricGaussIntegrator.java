package org.apache.commons.math3.analysis.integration.gauss;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.util.Pair;

public class SymmetricGaussIntegrator extends GaussIntegrator {
    public SymmetricGaussIntegrator(double[] points, double[] weights) throws NonMonotonicSequenceException, DimensionMismatchException {
        super(points, weights);
    }

    public SymmetricGaussIntegrator(Pair<double[], double[]> pointsAndWeights) throws NonMonotonicSequenceException {
        this(pointsAndWeights.getFirst(), pointsAndWeights.getSecond());
    }

    @Override // org.apache.commons.math3.analysis.integration.gauss.GaussIntegrator
    public double integrate(UnivariateFunction f) {
        int ruleLength = getNumberOfPoints();
        if (ruleLength == 1) {
            return getWeight(0) * f.value(0.0d);
        }
        int iMax = ruleLength / 2;
        double s = 0.0d;
        double c = 0.0d;
        for (int i = 0; i < iMax; i++) {
            double p = getPoint(i);
            double y = ((f.value(p) + f.value(-p)) * getWeight(i)) - c;
            double t = s + y;
            c = (t - s) - y;
            s = t;
        }
        if (ruleLength % 2 == 0) {
            return s;
        }
        return s + ((f.value(0.0d) * getWeight(iMax)) - c);
    }
}
