package org.apache.commons.math3.p000ml.distance;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.util.MathArrays;

/* renamed from: org.apache.commons.math3.ml.distance.ChebyshevDistance */
public class ChebyshevDistance implements DistanceMeasure {
    private static final long serialVersionUID = -4694868171115238296L;

    @Override // org.apache.commons.math3.p000ml.distance.DistanceMeasure
    public double compute(double[] a, double[] b) throws DimensionMismatchException {
        return MathArrays.distanceInf(a, b);
    }
}
