package org.apache.commons.math3.p000ml.distance;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.util.MathArrays;

/* renamed from: org.apache.commons.math3.ml.distance.ManhattanDistance */
public class ManhattanDistance implements DistanceMeasure {
    private static final long serialVersionUID = -9108154600539125566L;

    @Override // org.apache.commons.math3.p000ml.distance.DistanceMeasure
    public double compute(double[] a, double[] b) throws DimensionMismatchException {
        return MathArrays.distance1(a, b);
    }
}
