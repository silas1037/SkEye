package org.apache.commons.math3.p000ml.distance;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.util.MathArrays;

/* renamed from: org.apache.commons.math3.ml.distance.EuclideanDistance */
public class EuclideanDistance implements DistanceMeasure {
    private static final long serialVersionUID = 1717556319784040040L;

    @Override // org.apache.commons.math3.p000ml.distance.DistanceMeasure
    public double compute(double[] a, double[] b) throws DimensionMismatchException {
        return MathArrays.distance(a, b);
    }
}
