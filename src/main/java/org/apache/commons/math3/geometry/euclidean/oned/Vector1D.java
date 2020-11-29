package org.apache.commons.math3.geometry.euclidean.oned;

import java.text.NumberFormat;
import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.Space;
import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;

public class Vector1D implements Vector<Euclidean1D> {
    public static final Vector1D NEGATIVE_INFINITY = new Vector1D(Double.NEGATIVE_INFINITY);
    public static final Vector1D NaN = new Vector1D(Double.NaN);
    public static final Vector1D ONE = new Vector1D(1.0d);
    public static final Vector1D POSITIVE_INFINITY = new Vector1D(Double.POSITIVE_INFINITY);
    public static final Vector1D ZERO = new Vector1D(0.0d);
    private static final long serialVersionUID = 7556674948671647925L;

    /* renamed from: x */
    private final double f181x;

    public Vector1D(double x) {
        this.f181x = x;
    }

    public Vector1D(double a, Vector1D u) {
        this.f181x = u.f181x * a;
    }

    public Vector1D(double a1, Vector1D u1, double a2, Vector1D u2) {
        this.f181x = (u1.f181x * a1) + (u2.f181x * a2);
    }

    public Vector1D(double a1, Vector1D u1, double a2, Vector1D u2, double a3, Vector1D u3) {
        this.f181x = (u1.f181x * a1) + (u2.f181x * a2) + (u3.f181x * a3);
    }

    public Vector1D(double a1, Vector1D u1, double a2, Vector1D u2, double a3, Vector1D u3, double a4, Vector1D u4) {
        this.f181x = (u1.f181x * a1) + (u2.f181x * a2) + (u3.f181x * a3) + (u4.f181x * a4);
    }

    public double getX() {
        return this.f181x;
    }

    @Override // org.apache.commons.math3.geometry.Point
    public Space getSpace() {
        return Euclidean1D.getInstance();
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.oned.Vector1D' to match base method */
    @Override // org.apache.commons.math3.geometry.Vector
    public Vector<Euclidean1D> getZero() {
        return ZERO;
    }

    @Override // org.apache.commons.math3.geometry.Vector
    public double getNorm1() {
        return FastMath.abs(this.f181x);
    }

    @Override // org.apache.commons.math3.geometry.Vector
    public double getNorm() {
        return FastMath.abs(this.f181x);
    }

    @Override // org.apache.commons.math3.geometry.Vector
    public double getNormSq() {
        return this.f181x * this.f181x;
    }

    @Override // org.apache.commons.math3.geometry.Vector
    public double getNormInf() {
        return FastMath.abs(this.f181x);
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.oned.Vector1D' to match base method */
    @Override // org.apache.commons.math3.geometry.Vector
    public Vector<Euclidean1D> add(Vector<Euclidean1D> v) {
        return new Vector1D(this.f181x + ((Vector1D) v).getX());
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.oned.Vector1D' to match base method */
    @Override // org.apache.commons.math3.geometry.Vector
    public Vector<Euclidean1D> add(double factor, Vector<Euclidean1D> v) {
        return new Vector1D(this.f181x + (((Vector1D) v).getX() * factor));
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.oned.Vector1D' to match base method */
    @Override // org.apache.commons.math3.geometry.Vector
    public Vector<Euclidean1D> subtract(Vector<Euclidean1D> p) {
        return new Vector1D(this.f181x - ((Vector1D) p).f181x);
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.oned.Vector1D' to match base method */
    @Override // org.apache.commons.math3.geometry.Vector
    public Vector<Euclidean1D> subtract(double factor, Vector<Euclidean1D> v) {
        return new Vector1D(this.f181x - (((Vector1D) v).getX() * factor));
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.oned.Vector1D' to match base method */
    /* JADX WARN: Type inference failed for: r2v4, types: [org.apache.commons.math3.geometry.euclidean.oned.Vector1D] */
    /* JADX WARNING: Unknown variable types count: 1 */
    @Override // org.apache.commons.math3.geometry.Vector
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.apache.commons.math3.geometry.Vector<org.apache.commons.math3.geometry.euclidean.oned.Euclidean1D> normalize() throws org.apache.commons.math3.exception.MathArithmeticException {
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
            org.apache.commons.math3.geometry.euclidean.oned.Vector1D r2 = r5.scalarMultiply(r2)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.geometry.euclidean.oned.Vector1D.normalize():org.apache.commons.math3.geometry.euclidean.oned.Vector1D");
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.oned.Vector1D' to match base method */
    @Override // org.apache.commons.math3.geometry.Vector
    public Vector<Euclidean1D> negate() {
        return new Vector1D(-this.f181x);
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.oned.Vector1D' to match base method */
    @Override // org.apache.commons.math3.geometry.Vector
    public Vector<Euclidean1D> scalarMultiply(double a) {
        return new Vector1D(this.f181x * a);
    }

    @Override // org.apache.commons.math3.geometry.Point
    public boolean isNaN() {
        return Double.isNaN(this.f181x);
    }

    @Override // org.apache.commons.math3.geometry.Vector
    public boolean isInfinite() {
        return !isNaN() && Double.isInfinite(this.f181x);
    }

    @Override // org.apache.commons.math3.geometry.Vector
    public double distance1(Vector<Euclidean1D> p) {
        return FastMath.abs(((Vector1D) p).f181x - this.f181x);
    }

    @Override // org.apache.commons.math3.geometry.Vector
    @Deprecated
    public double distance(Vector<Euclidean1D> p) {
        return distance((Point<Euclidean1D>) p);
    }

    @Override // org.apache.commons.math3.geometry.Point
    public double distance(Point<Euclidean1D> p) {
        return FastMath.abs(((Vector1D) p).f181x - this.f181x);
    }

    @Override // org.apache.commons.math3.geometry.Vector
    public double distanceInf(Vector<Euclidean1D> p) {
        return FastMath.abs(((Vector1D) p).f181x - this.f181x);
    }

    @Override // org.apache.commons.math3.geometry.Vector
    public double distanceSq(Vector<Euclidean1D> p) {
        double dx = ((Vector1D) p).f181x - this.f181x;
        return dx * dx;
    }

    @Override // org.apache.commons.math3.geometry.Vector
    public double dotProduct(Vector<Euclidean1D> v) {
        return this.f181x * ((Vector1D) v).f181x;
    }

    public static double distance(Vector1D p1, Vector1D p2) {
        return p1.distance((Vector<Euclidean1D>) p2);
    }

    public static double distanceInf(Vector1D p1, Vector1D p2) {
        return p1.distanceInf(p2);
    }

    public static double distanceSq(Vector1D p1, Vector1D p2) {
        return p1.distanceSq(p2);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Vector1D)) {
            return false;
        }
        Vector1D rhs = (Vector1D) other;
        if (rhs.isNaN()) {
            return isNaN();
        }
        return this.f181x == rhs.f181x;
    }

    public int hashCode() {
        if (isNaN()) {
            return 7785;
        }
        return MathUtils.hash(this.f181x) * 997;
    }

    public String toString() {
        return Vector1DFormat.getInstance().format(this);
    }

    @Override // org.apache.commons.math3.geometry.Vector
    public String toString(NumberFormat format) {
        return new Vector1DFormat(format).format(this);
    }
}
