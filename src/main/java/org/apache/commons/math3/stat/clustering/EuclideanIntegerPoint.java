package org.apache.commons.math3.stat.clustering;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import org.apache.commons.math3.util.MathArrays;

@Deprecated
public class EuclideanIntegerPoint implements Clusterable<EuclideanIntegerPoint>, Serializable {
    private static final long serialVersionUID = 3946024775784901369L;
    private final int[] point;

    public EuclideanIntegerPoint(int[] point2) {
        this.point = point2;
    }

    public int[] getPoint() {
        return this.point;
    }

    public double distanceFrom(EuclideanIntegerPoint p) {
        return MathArrays.distance(this.point, p.getPoint());
    }

    @Override // org.apache.commons.math3.stat.clustering.Clusterable
    public EuclideanIntegerPoint centroidOf(Collection<EuclideanIntegerPoint> points) {
        int[] centroid = new int[getPoint().length];
        for (EuclideanIntegerPoint p : points) {
            for (int i = 0; i < centroid.length; i++) {
                centroid[i] = centroid[i] + p.getPoint()[i];
            }
        }
        for (int i2 = 0; i2 < centroid.length; i2++) {
            centroid[i2] = centroid[i2] / points.size();
        }
        return new EuclideanIntegerPoint(centroid);
    }

    public boolean equals(Object other) {
        if (!(other instanceof EuclideanIntegerPoint)) {
            return false;
        }
        return Arrays.equals(this.point, ((EuclideanIntegerPoint) other).point);
    }

    public int hashCode() {
        return Arrays.hashCode(this.point);
    }

    public String toString() {
        return Arrays.toString(this.point);
    }
}
