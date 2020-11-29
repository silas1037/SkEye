package org.apache.commons.math3.p000ml.distance;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;

/* renamed from: org.apache.commons.math3.ml.distance.CanberraDistance */
public class CanberraDistance implements DistanceMeasure {
    private static final long serialVersionUID = -6972277381587032228L;

    @Override // org.apache.commons.math3.p000ml.distance.DistanceMeasure
    public double compute(double[] a, double[] b) throws DimensionMismatchException {
        MathArrays.checkEqualLength(a, b);
        double sum = 0.0d;
        for (int i = 0; i < a.length; i++) {
            double num = FastMath.abs(a[i] - b[i]);
            double denom = FastMath.abs(a[i]) + FastMath.abs(b[i]);
            sum += (num == 0.0d && denom == 0.0d) ? 0.0d : num / denom;
        }
        return sum;
    }
}
