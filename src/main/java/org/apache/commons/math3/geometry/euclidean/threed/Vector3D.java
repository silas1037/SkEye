package org.apache.commons.math3.geometry.euclidean.threed;

import java.io.Serializable;
import java.text.NumberFormat;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.Space;
import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.MathUtils;

public class Vector3D implements Serializable, Vector<Euclidean3D> {
    public static final Vector3D MINUS_I = new Vector3D(-1.0d, 0.0d, 0.0d);
    public static final Vector3D MINUS_J = new Vector3D(0.0d, -1.0d, 0.0d);
    public static final Vector3D MINUS_K = new Vector3D(0.0d, 0.0d, -1.0d);
    public static final Vector3D NEGATIVE_INFINITY = new Vector3D(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
    public static final Vector3D NaN = new Vector3D(Double.NaN, Double.NaN, Double.NaN);
    public static final Vector3D PLUS_I = new Vector3D(1.0d, 0.0d, 0.0d);
    public static final Vector3D PLUS_J = new Vector3D(0.0d, 1.0d, 0.0d);
    public static final Vector3D PLUS_K = new Vector3D(0.0d, 0.0d, 1.0d);
    public static final Vector3D POSITIVE_INFINITY = new Vector3D(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    public static final Vector3D ZERO = new Vector3D(0.0d, 0.0d, 0.0d);
    private static final long serialVersionUID = 1313493323784566947L;

    /* renamed from: x */
    private final double f207x;

    /* renamed from: y */
    private final double f208y;

    /* renamed from: z */
    private final double f209z;

    public Vector3D(double x, double y, double z) {
        this.f207x = x;
        this.f208y = y;
        this.f209z = z;
    }

    public Vector3D(double[] v) throws DimensionMismatchException {
        if (v.length != 3) {
            throw new DimensionMismatchException(v.length, 3);
        }
        this.f207x = v[0];
        this.f208y = v[1];
        this.f209z = v[2];
    }

    public Vector3D(double alpha, double delta) {
        double cosDelta = FastMath.cos(delta);
        this.f207x = FastMath.cos(alpha) * cosDelta;
        this.f208y = FastMath.sin(alpha) * cosDelta;
        this.f209z = FastMath.sin(delta);
    }

    public Vector3D(double a, Vector3D u) {
        this.f207x = u.f207x * a;
        this.f208y = u.f208y * a;
        this.f209z = u.f209z * a;
    }

    public Vector3D(double a1, Vector3D u1, double a2, Vector3D u2) {
        this.f207x = MathArrays.linearCombination(a1, u1.f207x, a2, u2.f207x);
        this.f208y = MathArrays.linearCombination(a1, u1.f208y, a2, u2.f208y);
        this.f209z = MathArrays.linearCombination(a1, u1.f209z, a2, u2.f209z);
    }

    public Vector3D(double a1, Vector3D u1, double a2, Vector3D u2, double a3, Vector3D u3) {
        this.f207x = MathArrays.linearCombination(a1, u1.f207x, a2, u2.f207x, a3, u3.f207x);
        this.f208y = MathArrays.linearCombination(a1, u1.f208y, a2, u2.f208y, a3, u3.f208y);
        this.f209z = MathArrays.linearCombination(a1, u1.f209z, a2, u2.f209z, a3, u3.f209z);
    }

    public Vector3D(double a1, Vector3D u1, double a2, Vector3D u2, double a3, Vector3D u3, double a4, Vector3D u4) {
        this.f207x = MathArrays.linearCombination(a1, u1.f207x, a2, u2.f207x, a3, u3.f207x, a4, u4.f207x);
        this.f208y = MathArrays.linearCombination(a1, u1.f208y, a2, u2.f208y, a3, u3.f208y, a4, u4.f208y);
        this.f209z = MathArrays.linearCombination(a1, u1.f209z, a2, u2.f209z, a3, u3.f209z, a4, u4.f209z);
    }

    public double getX() {
        return this.f207x;
    }

    public double getY() {
        return this.f208y;
    }

    public double getZ() {
        return this.f209z;
    }

    public double[] toArray() {
        return new double[]{this.f207x, this.f208y, this.f209z};
    }

    @Override // org.apache.commons.math3.geometry.Point
    public Space getSpace() {
        return Euclidean3D.getInstance();
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.threed.Vector3D' to match base method */
    @Override // org.apache.commons.math3.geometry.Vector
    public Vector<Euclidean3D> getZero() {
        return ZERO;
    }

    @Override // org.apache.commons.math3.geometry.Vector
    public double getNorm1() {
        return FastMath.abs(this.f207x) + FastMath.abs(this.f208y) + FastMath.abs(this.f209z);
    }

    @Override // org.apache.commons.math3.geometry.Vector
    public double getNorm() {
        return FastMath.sqrt((this.f207x * this.f207x) + (this.f208y * this.f208y) + (this.f209z * this.f209z));
    }

    @Override // org.apache.commons.math3.geometry.Vector
    public double getNormSq() {
        return (this.f207x * this.f207x) + (this.f208y * this.f208y) + (this.f209z * this.f209z);
    }

    @Override // org.apache.commons.math3.geometry.Vector
    public double getNormInf() {
        return FastMath.max(FastMath.max(FastMath.abs(this.f207x), FastMath.abs(this.f208y)), FastMath.abs(this.f209z));
    }

    public double getAlpha() {
        return FastMath.atan2(this.f208y, this.f207x);
    }

    public double getDelta() {
        return FastMath.asin(this.f209z / getNorm());
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.threed.Vector3D' to match base method */
    @Override // org.apache.commons.math3.geometry.Vector
    public Vector<Euclidean3D> add(Vector<Euclidean3D> v) {
        Vector3D v3 = (Vector3D) v;
        return new Vector3D(this.f207x + v3.f207x, this.f208y + v3.f208y, this.f209z + v3.f209z);
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.threed.Vector3D' to match base method */
    @Override // org.apache.commons.math3.geometry.Vector
    public Vector<Euclidean3D> add(double factor, Vector<Euclidean3D> v) {
        return new Vector3D(1.0d, this, factor, (Vector3D) v);
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.threed.Vector3D' to match base method */
    @Override // org.apache.commons.math3.geometry.Vector
    public Vector<Euclidean3D> subtract(Vector<Euclidean3D> v) {
        Vector3D v3 = (Vector3D) v;
        return new Vector3D(this.f207x - v3.f207x, this.f208y - v3.f208y, this.f209z - v3.f209z);
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.threed.Vector3D' to match base method */
    @Override // org.apache.commons.math3.geometry.Vector
    public Vector<Euclidean3D> subtract(double factor, Vector<Euclidean3D> v) {
        return new Vector3D(1.0d, this, -factor, (Vector3D) v);
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.threed.Vector3D' to match base method */
    /* JADX WARN: Type inference failed for: r2v4, types: [org.apache.commons.math3.geometry.euclidean.threed.Vector3D] */
    /* JADX WARNING: Unknown variable types count: 1 */
    @Override // org.apache.commons.math3.geometry.Vector
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.apache.commons.math3.geometry.Vector<org.apache.commons.math3.geometry.euclidean.threed.Euclidean3D> normalize() throws org.apache.commons.math3.exception.MathArithmeticException {
        /*
            r5 = this;
            double r0 = r5.getNorm()
            r2 = 0
            int r2 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r2 != 0) goto L_0x0015
            org.apache.commons.math3.exception.MathArithmeticException r2 = new org.apache.commons.math3.exception.MathArithmeticException
            org.apache.commons.math3.exception.util.LocalizedFormats r3 = org.apache.commons.math3.exception.util.LocalizedFormats.CANNOT_NORMALIZE_A_ZERO_NORM_VECTOR
            r4 = 0
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r2.<init>(r3, r4)
            throw r2
        L_0x0015:
            r2 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            double r2 = r2 / r0
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r2 = r5.scalarMultiply(r2)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.geometry.euclidean.threed.Vector3D.normalize():org.apache.commons.math3.geometry.euclidean.threed.Vector3D");
    }

    public Vector3D orthogonal() throws MathArithmeticException {
        double threshold = 0.6d * getNorm();
        if (threshold == 0.0d) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_NORM, new Object[0]);
        } else if (FastMath.abs(this.f207x) <= threshold) {
            double inverse = 1.0d / FastMath.sqrt((this.f208y * this.f208y) + (this.f209z * this.f209z));
            return new Vector3D(0.0d, this.f209z * inverse, (-inverse) * this.f208y);
        } else if (FastMath.abs(this.f208y) <= threshold) {
            double inverse2 = 1.0d / FastMath.sqrt((this.f207x * this.f207x) + (this.f209z * this.f209z));
            return new Vector3D((-inverse2) * this.f209z, 0.0d, this.f207x * inverse2);
        } else {
            double inverse3 = 1.0d / FastMath.sqrt((this.f207x * this.f207x) + (this.f208y * this.f208y));
            return new Vector3D(this.f208y * inverse3, (-inverse3) * this.f207x, 0.0d);
        }
    }

    public static double angle(Vector3D v1, Vector3D v2) throws MathArithmeticException {
        double normProduct = v1.getNorm() * v2.getNorm();
        if (normProduct == 0.0d) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_NORM, new Object[0]);
        }
        double dot = v1.dotProduct(v2);
        double threshold = normProduct * 0.9999d;
        if (dot >= (-threshold) && dot <= threshold) {
            return FastMath.acos(dot / normProduct);
        }
        Vector3D v3 = crossProduct(v1, v2);
        if (dot >= 0.0d) {
            return FastMath.asin(v3.getNorm() / normProduct);
        }
        return 3.141592653589793d - FastMath.asin(v3.getNorm() / normProduct);
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.threed.Vector3D' to match base method */
    @Override // org.apache.commons.math3.geometry.Vector
    public Vector<Euclidean3D> negate() {
        return new Vector3D(-this.f207x, -this.f208y, -this.f209z);
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.threed.Vector3D' to match base method */
    @Override // org.apache.commons.math3.geometry.Vector
    public Vector<Euclidean3D> scalarMultiply(double a) {
        return new Vector3D(this.f207x * a, this.f208y * a, this.f209z * a);
    }

    @Override // org.apache.commons.math3.geometry.Point
    public boolean isNaN() {
        return Double.isNaN(this.f207x) || Double.isNaN(this.f208y) || Double.isNaN(this.f209z);
    }

    @Override // org.apache.commons.math3.geometry.Vector
    public boolean isInfinite() {
        return !isNaN() && (Double.isInfinite(this.f207x) || Double.isInfinite(this.f208y) || Double.isInfinite(this.f209z));
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Vector3D)) {
            return false;
        }
        Vector3D rhs = (Vector3D) other;
        if (rhs.isNaN()) {
            return isNaN();
        }
        return this.f207x == rhs.f207x && this.f208y == rhs.f208y && this.f209z == rhs.f209z;
    }

    public int hashCode() {
        if (isNaN()) {
            return 642;
        }
        return ((MathUtils.hash(this.f207x) * 164) + (MathUtils.hash(this.f208y) * 3) + MathUtils.hash(this.f209z)) * 643;
    }

    @Override // org.apache.commons.math3.geometry.Vector
    public double dotProduct(Vector<Euclidean3D> v) {
        Vector3D v3 = (Vector3D) v;
        return MathArrays.linearCombination(this.f207x, v3.f207x, this.f208y, v3.f208y, this.f209z, v3.f209z);
    }

    public Vector3D crossProduct(Vector<Euclidean3D> v) {
        Vector3D v3 = (Vector3D) v;
        return new Vector3D(MathArrays.linearCombination(this.f208y, v3.f209z, -this.f209z, v3.f208y), MathArrays.linearCombination(this.f209z, v3.f207x, -this.f207x, v3.f209z), MathArrays.linearCombination(this.f207x, v3.f208y, -this.f208y, v3.f207x));
    }

    @Override // org.apache.commons.math3.geometry.Vector
    public double distance1(Vector<Euclidean3D> v) {
        Vector3D v3 = (Vector3D) v;
        double dx = FastMath.abs(v3.f207x - this.f207x);
        double dy = FastMath.abs(v3.f208y - this.f208y);
        return dx + dy + FastMath.abs(v3.f209z - this.f209z);
    }

    @Override // org.apache.commons.math3.geometry.Vector
    public double distance(Vector<Euclidean3D> v) {
        return distance((Point<Euclidean3D>) v);
    }

    @Override // org.apache.commons.math3.geometry.Point
    public double distance(Point<Euclidean3D> v) {
        Vector3D v3 = (Vector3D) v;
        double dx = v3.f207x - this.f207x;
        double dy = v3.f208y - this.f208y;
        double dz = v3.f209z - this.f209z;
        return FastMath.sqrt((dx * dx) + (dy * dy) + (dz * dz));
    }

    @Override // org.apache.commons.math3.geometry.Vector
    public double distanceInf(Vector<Euclidean3D> v) {
        Vector3D v3 = (Vector3D) v;
        double dx = FastMath.abs(v3.f207x - this.f207x);
        double dy = FastMath.abs(v3.f208y - this.f208y);
        return FastMath.max(FastMath.max(dx, dy), FastMath.abs(v3.f209z - this.f209z));
    }

    @Override // org.apache.commons.math3.geometry.Vector
    public double distanceSq(Vector<Euclidean3D> v) {
        Vector3D v3 = (Vector3D) v;
        double dx = v3.f207x - this.f207x;
        double dy = v3.f208y - this.f208y;
        double dz = v3.f209z - this.f209z;
        return (dx * dx) + (dy * dy) + (dz * dz);
    }

    public static double dotProduct(Vector3D v1, Vector3D v2) {
        return v1.dotProduct(v2);
    }

    public static Vector3D crossProduct(Vector3D v1, Vector3D v2) {
        return v1.crossProduct(v2);
    }

    public static double distance1(Vector3D v1, Vector3D v2) {
        return v1.distance1(v2);
    }

    public static double distance(Vector3D v1, Vector3D v2) {
        return v1.distance((Vector<Euclidean3D>) v2);
    }

    public static double distanceInf(Vector3D v1, Vector3D v2) {
        return v1.distanceInf(v2);
    }

    public static double distanceSq(Vector3D v1, Vector3D v2) {
        return v1.distanceSq(v2);
    }

    public String toString() {
        return Vector3DFormat.getInstance().format(this);
    }

    @Override // org.apache.commons.math3.geometry.Vector
    public String toString(NumberFormat format) {
        return new Vector3DFormat(format).format(this);
    }
}
