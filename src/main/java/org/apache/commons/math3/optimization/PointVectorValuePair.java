package org.apache.commons.math3.optimization;

import java.io.Serializable;
import org.apache.commons.math3.util.Pair;

@Deprecated
public class PointVectorValuePair extends Pair<double[], double[]> implements Serializable {
    private static final long serialVersionUID = 20120513;

    public PointVectorValuePair(double[] point, double[] value) {
        this(point, value, true);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public PointVectorValuePair(double[] point, double[] value, boolean copyArray) {
        super(copyArray ? point == null ? null : (double[]) point.clone() : point, copyArray ? value == null ? null : (double[]) value.clone() : value);
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

    @Override // org.apache.commons.math3.util.Pair
    public double[] getValue() {
        double[] v = (double[]) super.getValue();
        if (v == null) {
            return null;
        }
        return (double[]) v.clone();
    }

    public double[] getValueRef() {
        return (double[]) super.getValue();
    }

    private Object writeReplace() {
        return new DataTransferObject((double[]) getKey(), getValue());
    }

    private static class DataTransferObject implements Serializable {
        private static final long serialVersionUID = 20120513;
        private final double[] point;
        private final double[] value;

        DataTransferObject(double[] point2, double[] value2) {
            this.point = (double[]) point2.clone();
            this.value = (double[]) value2.clone();
        }

        private Object readResolve() {
            return new PointVectorValuePair(this.point, this.value, false);
        }
    }
}
