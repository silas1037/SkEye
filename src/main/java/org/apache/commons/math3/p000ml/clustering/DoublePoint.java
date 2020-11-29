package org.apache.commons.math3.p000ml.clustering;

import java.io.Serializable;
import java.util.Arrays;

/* renamed from: org.apache.commons.math3.ml.clustering.DoublePoint */
public class DoublePoint implements Clusterable, Serializable {
    private static final long serialVersionUID = 3946024775784901369L;
    private final double[] point;

    public DoublePoint(double[] point2) {
        this.point = point2;
    }

    public DoublePoint(int[] point2) {
        this.point = new double[point2.length];
        for (int i = 0; i < point2.length; i++) {
            this.point[i] = (double) point2[i];
        }
    }

    @Override // org.apache.commons.math3.p000ml.clustering.Clusterable
    public double[] getPoint() {
        return this.point;
    }

    public boolean equals(Object other) {
        if (!(other instanceof DoublePoint)) {
            return false;
        }
        return Arrays.equals(this.point, ((DoublePoint) other).point);
    }

    public int hashCode() {
        return Arrays.hashCode(this.point);
    }

    public String toString() {
        return Arrays.toString(this.point);
    }
}
