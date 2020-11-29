package org.apache.commons.math3.p000ml.distance;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;

/* renamed from: org.apache.commons.math3.ml.distance.EarthMoversDistance */
public class EarthMoversDistance implements DistanceMeasure {
    private static final long serialVersionUID = -5406732779747414922L;

    @Override // org.apache.commons.math3.p000ml.distance.DistanceMeasure
    public double compute(double[] a, double[] b) throws DimensionMismatchException {
        MathArrays.checkEqualLength(a, b);
        double lastDistance = 0.0d;
        double totalDistance = 0.0d;
        for (int i = 0; i < a.length; i++) {
            double currentDistance = (a[i] + lastDistance) - b[i];
            totalDistance += FastMath.abs(currentDistance);
            lastDistance = currentDistance;
        }
        return totalDistance;
    }
}
