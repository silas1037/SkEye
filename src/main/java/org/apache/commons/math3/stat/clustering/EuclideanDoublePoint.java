package org.apache.commons.math3.stat.clustering;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import org.apache.commons.math3.util.MathArrays;

@Deprecated
public class EuclideanDoublePoint implements Clusterable<EuclideanDoublePoint>, Serializable {
    private static final long serialVersionUID = 8026472786091227632L;
    private final double[] point;

    public EuclideanDoublePoint(double[] point2) {
        this.point = point2;
    }

    @Override // org.apache.commons.math3.stat.clustering.Clusterable
    public EuclideanDoublePoint centroidOf(Collection<EuclideanDoublePoint> points) {
        double[] centroid = new double[getPoint().length];
        for (EuclideanDoublePoint p : points) {
            for (int i = 0; i < centroid.length; i++) {
                centroid[i] = centroid[i] + p.getPoint()[i];
            }
        }
        for (int i2 = 0; i2 < centroid.length; i2++) {
            centroid[i2] = centroid[i2] / ((double) points.size());
        }
        return new EuclideanDoublePoint(centroid);
    }

    public double distanceFrom(EuclideanDoublePoint p) {
        return MathArrays.distance(this.point, p.getPoint());
    }

    public boolean equals(Object other) {
        if (!(other instanceof EuclideanDoublePoint)) {
            return false;
        }
        return Arrays.equals(this.point, ((EuclideanDoublePoint) other).point);
    }

    public double[] getPoint() {
        return this.point;
    }

    public int hashCode() {
        return Arrays.hashCode(this.point);
    }

    public String toString() {
        return Arrays.toString(this.point);
    }
}
