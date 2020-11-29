package org.apache.commons.math3.geometry.euclidean.twod;

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

public class Vector2D implements Vector<Euclidean2D> {
    public static final Vector2D NEGATIVE_INFINITY = new Vector2D(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
    public static final Vector2D NaN = new Vector2D(Double.NaN, Double.NaN);
    public static final Vector2D POSITIVE_INFINITY = new Vector2D(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    public static final Vector2D ZERO = new Vector2D(0.0d, 0.0d);
    private static final long serialVersionUID = 266938651998679754L;

    /* renamed from: x */
    private final double f210x;

    /* renamed from: y */
    private final double f211y;

    public Vector2D(double x, double y) {
        this.f210x = x;
        this.f211y = y;
    }

    public Vector2D(double[] v) throws DimensionMismatchException {
        if (v.length != 2) {
            throw new DimensionMismatchException(v.length, 2);
        }
        this.f210x = v[0];
        this.f211y = v[1];
    }

    public Vector2D(double a, Vector2D u) {
        this.f210x = u.f210x * a;
        this.f211y = u.f211y * a;
    }

    public Vector2D(double a1, Vector2D u1, double a2, Vector2D u2) {
        this.f210x = (u1.f210x * a1) + (u2.f210x * a2);
        this.f211y = (u1.f211y * a1) + (u2.f211y * a2);
    }

    public Vector2D(double a1, Vector2D u1, double a2, Vector2D u2, double a3, Vector2D u3) {
        this.f210x = (u1.f210x * a1) + (u2.f210x * a2) + (u3.f210x * a3);
        this.f211y = (u1.f211y * a1) + (u2.f211y * a2) + (u3.f211y * a3);
    }

    public Vector2D(double a1, Vector2D u1, double a2, Vector2D u2, double a3, Vector2D u3, double a4, Vector2D u4) {
        this.f210x = (u1.f210x * a1) + (u2.f210x * a2) + (u3.f210x * a3) + (u4.f210x * a4);
        this.f211y = (u1.f211y * a1) + (u2.f211y * a2) + (u3.f211y * a3) + (u4.f211y * a4);
    }

    public double getX() {
        return this.f210x;
    }

    public double getY() {
        return this.f211y;
    }

    public double[] toArray() {
        return new double[]{this.f210x, this.f211y};
    }

    @Override // org.apache.commons.math3.geometry.Point
    public Space getSpace() {
        return Euclidean2D.getInstance();
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.twod.Vector2D' to match base method */
    @Override // org.apache.commons.math3.geometry.Vector
    public Vector<Euclidean2D> getZero() {
        return ZERO;
    }

    @Override // org.apache.commons.math3.geometry.Vector
    public double getNorm1() {
        return FastMath.abs(this.f210x) + FastMath.abs(this.f211y);
    }

    @Override // org.apache.commons.math3.geometry.Vector
    public double getNorm() {
        return FastMath.sqrt((this.f210x * this.f210x) + (this.f211y * this.f211y));
    }

    @Override // org.apache.commons.math3.geometry.Vector
    public double getNormSq() {
        return (this.f210x * this.f210x) + (this.f211y * this.f211y);
    }

    @Override // org.apache.commons.math3.geometry.Vector
    public double getNormInf() {
        return FastMath.max(FastMath.abs(this.f210x), FastMath.abs(this.f211y));
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.twod.Vector2D' to match base method */
    @Override // org.apache.commons.math3.geometry.Vector
    public Vector<Euclidean2D> add(Vector<Euclidean2D> v) {
        Vector2D v2 = (Vector2D) v;
        return new Vector2D(this.f210x + v2.getX(), this.f211y + v2.getY());
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.twod.Vector2D' to match base method */
    @Override // org.apache.commons.math3.geometry.Vector
    public Vector<Euclidean2D> add(double factor, Vector<Euclidean2D> v) {
        Vector2D v2 = (Vector2D) v;
        return new Vector2D(this.f210x + (v2.getX() * factor), this.f211y + (v2.getY() * factor));
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.twod.Vector2D' to match base method */
    @Override // org.apache.commons.math3.geometry.Vector
    public Vector<Euclidean2D> subtract(Vector<Euclidean2D> p) {
        Vector2D p3 = (Vector2D) p;
        return new Vector2D(this.f210x - p3.f210x, this.f211y - p3.f211y);
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.twod.Vector2D' to match base method */
    @Override // org.apache.commons.math3.geometry.Vector
    public Vector<Euclidean2D> subtract(double factor, Vector<Euclidean2D> v) {
        Vector2D v2 = (Vector2D) v;
        return new Vector2D(this.f210x - (v2.getX() * factor), this.f211y - (v2.getY() * factor));
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.twod.Vector2D' to match base method */
    /* JADX WARN: Type inference failed for: r2v4, types: [org.apache.commons.math3.geometry.euclidean.twod.Vector2D] */
    /* JADX WARNING: Unknown variable types count: 1 */
    @Override // org.apache.commons.math3.geometry.Vector
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.apache.commons.math3.geometry.Vector<org.apache.commons.math3.geometry.euclidean.twod.Euclidean2D> normalize() throws org.apache.commons.math3.exception.MathArithmeticException {
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
            org.apache.commons.math3.geometry.euclidean.twod.Vector2D r2 = r5.scalarMultiply(r2)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.geometry.euclidean.twod.Vector2D.normalize():org.apache.commons.math3.geometry.euclidean.twod.Vector2D");
    }

    public static double angle(Vector2D v1, Vector2D v2) throws MathArithmeticException {
        double normProduct = v1.getNorm() * v2.getNorm();
        if (normProduct == 0.0d) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_NORM, new Object[0]);
        }
        double dot = v1.dotProduct(v2);
        double threshold = normProduct * 0.9999d;
        if (dot >= (-threshold) && dot <= threshold) {
            return FastMath.acos(dot / normProduct);
        }
        double n = FastMath.abs(MathArrays.linearCombination(v1.f210x, v2.f211y, -v1.f211y, v2.f210x));
        if (dot >= 0.0d) {
            return FastMath.asin(n / normProduct);
        }
        return 3.141592653589793d - FastMath.asin(n / normProduct);
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.twod.Vector2D' to match base method */
    @Override // org.apache.commons.math3.geometry.Vector
    public Vector<Euclidean2D> negate() {
        return new Vector2D(-this.f210x, -this.f211y);
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.twod.Vector2D' to match base method */
    @Override // org.apache.commons.math3.geometry.Vector
    public Vector<Euclidean2D> scalarMultiply(double a) {
        return new Vector2D(this.f210x * a, this.f211y * a);
    }

    @Override // org.apache.commons.math3.geometry.Point
    public boolean isNaN() {
        return Double.isNaN(this.f210x) || Double.isNaN(this.f211y);
    }

    @Override // org.apache.commons.math3.geometry.Vector
    public boolean isInfinite() {
        return !isNaN() && (Double.isInfinite(this.f210x) || Double.isInfinite(this.f211y));
    }

    @Override // org.apache.commons.math3.geometry.Vector
    public double distance1(Vector<Euclidean2D> p) {
        Vector2D p3 = (Vector2D) p;
        return FastMath.abs(p3.f210x - this.f210x) + FastMath.abs(p3.f211y - this.f211y);
    }

    @Override // org.apache.commons.math3.geometry.Vector
    public double distance(Vector<Euclidean2D> p) {
        return distance((Point<Euclidean2D>) p);
    }

    @Override // org.apache.commons.math3.geometry.Point
    public double distance(Point<Euclidean2D> p) {
        Vector2D p3 = (Vector2D) p;
        double dx = p3.f210x - this.f210x;
        double dy = p3.f211y - this.f211y;
        return FastMath.sqrt((dx * dx) + (dy * dy));
    }

    @Override // org.apache.commons.math3.geometry.Vector
    public double distanceInf(Vector<Euclidean2D> p) {
        Vector2D p3 = (Vector2D) p;
        return FastMath.max(FastMath.abs(p3.f210x - this.f210x), FastMath.abs(p3.f211y - this.f211y));
    }

    @Override // org.apache.commons.math3.geometry.Vector
    public double distanceSq(Vector<Euclidean2D> p) {
        Vector2D p3 = (Vector2D) p;
        double dx = p3.f210x - this.f210x;
        double dy = p3.f211y - this.f211y;
        return (dx * dx) + (dy * dy);
    }

    @Override // org.apache.commons.math3.geometry.Vector
    public double dotProduct(Vector<Euclidean2D> v) {
        Vector2D v2 = (Vector2D) v;
        return MathArrays.linearCombination(this.f210x, v2.f210x, this.f211y, v2.f211y);
    }

    public double crossProduct(Vector2D p1, Vector2D p2) {
        double x1 = p2.getX() - p1.getX();
        double y1 = getY() - p1.getY();
        double x2 = getX() - p1.getX();
        return MathArrays.linearCombination(x1, y1, -x2, p2.getY() - p1.getY());
    }

    public static double distance(Vector2D p1, Vector2D p2) {
        return p1.distance((Vector<Euclidean2D>) p2);
    }

    public static double distanceInf(Vector2D p1, Vector2D p2) {
        return p1.distanceInf(p2);
    }

    public static double distanceSq(Vector2D p1, Vector2D p2) {
        return p1.distanceSq(p2);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Vector2D)) {
            return false;
        }
        Vector2D rhs = (Vector2D) other;
        if (rhs.isNaN()) {
            return isNaN();
        }
        return this.f210x == rhs.f210x && this.f211y == rhs.f211y;
    }

    public int hashCode() {
        if (isNaN()) {
            return 542;
        }
        return ((MathUtils.hash(this.f210x) * 76) + MathUtils.hash(this.f211y)) * 122;
    }

    public String toString() {
        return Vector2DFormat.getInstance().format(this);
    }

    @Override // org.apache.commons.math3.geometry.Vector
    public String toString(NumberFormat format) {
        return new Vector2DFormat(format).format(this);
    }
}
