package org.apache.commons.math3.optim;

import java.io.Serializable;
import org.apache.commons.math3.util.Pair;

public class PointValuePair extends Pair<double[], Double> implements Serializable {
    private static final long serialVersionUID = 20120513;

    public PointValuePair(double[] point, double value) {
        this(point, value, true);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public PointValuePair(double[] point, double value, boolean copyArray) {
        super(copyArray ? point == null ? null : (double[]) point.clone() : point, Double.valueOf(value));
    }

    public double[] getPoint() {
        double[] p = (double[]) getKey();
        if (p == null) {
            return null;
        }
        return (double[]) p.clone();
    }

    public double[] getPointRef() {
        return (double[]) getKey();
    }

    private Object writeReplace() {
        return new DataTransferObject((double[]) getKey(), ((Double) getValue()).doubleValue());
    }

    private static class DataTransferObject implements Serializable {
        private static final long serialVersionUID = 20120513;
        private final double[] point;
        private final double value;

        DataTransferObject(double[] point2, double value2) {
            this.point = (double[]) point2.clone();
            this.value = value2;
        }

        private Object readResolve() {
            return new PointValuePair(this.point, this.value, false);
        }
    }
}
