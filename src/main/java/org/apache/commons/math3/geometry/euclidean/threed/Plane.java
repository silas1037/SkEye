package org.apache.commons.math3.geometry.euclidean.threed;

import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.geometry.euclidean.twod.Euclidean2D;
import org.apache.commons.math3.geometry.euclidean.twod.PolygonsSet;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.geometry.partitioning.Embedding;
import org.apache.commons.math3.geometry.partitioning.Hyperplane;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;
import org.apache.commons.math3.util.FastMath;

public class Plane implements Hyperplane<Euclidean3D>, Embedding<Euclidean3D, Euclidean2D> {
    private static final double DEFAULT_TOLERANCE = 1.0E-10d;
    private Vector3D origin;
    private double originOffset;
    private final double tolerance;

    /* renamed from: u */
    private Vector3D f192u;

    /* renamed from: v */
    private Vector3D f193v;

    /* renamed from: w */
    private Vector3D f194w;

    public Plane(Vector3D normal, double tolerance2) throws MathArithmeticException {
        setNormal(normal);
        this.tolerance = tolerance2;
        this.originOffset = 0.0d;
        setFrame();
    }

    public Plane(Vector3D p, Vector3D normal, double tolerance2) throws MathArithmeticException {
        setNormal(normal);
        this.tolerance = tolerance2;
        this.originOffset = -p.dotProduct(this.f194w);
        setFrame();
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [org.apache.commons.math3.geometry.euclidean.threed.Vector3D] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public Plane(org.apache.commons.math3.geometry.euclidean.threed.Vector3D r3, org.apache.commons.math3.geometry.euclidean.threed.Vector3D r4, org.apache.commons.math3.geometry.euclidean.threed.Vector3D r5, double r6) throws org.apache.commons.math3.exception.MathArithmeticException {
        /*
            r2 = this;
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r0 = r4.subtract(r3)
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r1 = r5.subtract(r3)
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r0 = r0.crossProduct(r1)
            r2.<init>(r3, r0, r6)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.geometry.euclidean.threed.Plane.<init>(org.apache.commons.math3.geometry.euclidean.threed.Vector3D, org.apache.commons.math3.geometry.euclidean.threed.Vector3D, org.apache.commons.math3.geometry.euclidean.threed.Vector3D, double):void");
    }

    @Deprecated
    public Plane(Vector3D normal) throws MathArithmeticException {
        this(normal, 1.0E-10d);
    }

    @Deprecated
    public Plane(Vector3D p, Vector3D normal) throws MathArithmeticException {
        this(p, normal, 1.0E-10d);
    }

    @Deprecated
    public Plane(Vector3D p1, Vector3D p2, Vector3D p3) throws MathArithmeticException {
        this(p1, p2, p3, 1.0E-10d);
    }

    public Plane(Plane plane) {
        this.originOffset = plane.originOffset;
        this.origin = plane.origin;
        this.f192u = plane.f192u;
        this.f193v = plane.f193v;
        this.f194w = plane.f194w;
        this.tolerance = plane.tolerance;
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.threed.Plane' to match base method */
    @Override // org.apache.commons.math3.geometry.partitioning.Hyperplane
    public Hyperplane<Euclidean3D> copySelf() {
        return new Plane(this);
    }

    public void reset(Vector3D p, Vector3D normal) throws MathArithmeticException {
        setNormal(normal);
        this.originOffset = -p.dotProduct(this.f194w);
        setFrame();
    }

    public void reset(Plane original) {
        this.originOffset = original.originOffset;
        this.origin = original.origin;
        this.f192u = original.f192u;
        this.f193v = original.f193v;
        this.f194w = original.f194w;
    }

    private void setNormal(Vector3D normal) throws MathArithmeticException {
        double norm = normal.getNorm();
        if (norm < 1.0E-10d) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_NORM, new Object[0]);
        }
        this.f194w = new Vector3D(1.0d / norm, normal);
    }

    private void setFrame() {
        this.origin = new Vector3D(-this.originOffset, this.f194w);
        this.f192u = this.f194w.orthogonal();
        this.f193v = Vector3D.crossProduct(this.f194w, this.f192u);
    }

    public Vector3D getOrigin() {
        return this.origin;
    }

    public Vector3D getNormal() {
        return this.f194w;
    }

    public Vector3D getU() {
        return this.f192u;
    }

    public Vector3D getV() {
        return this.f193v;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r1v0, resolved type: org.apache.commons.math3.geometry.euclidean.threed.Plane */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v0, types: [org.apache.commons.math3.geometry.Vector, org.apache.commons.math3.geometry.euclidean.twod.Vector2D] */
    /* JADX WARNING: Unknown variable types count: 1 */
    @Override // org.apache.commons.math3.geometry.partitioning.Hyperplane
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.apache.commons.math3.geometry.Point<org.apache.commons.math3.geometry.euclidean.threed.Euclidean3D> project(org.apache.commons.math3.geometry.Point<org.apache.commons.math3.geometry.euclidean.threed.Euclidean3D> r2) {
        /*
            r1 = this;
            org.apache.commons.math3.geometry.euclidean.twod.Vector2D r0 = r1.toSubSpace(r2)
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r0 = r1.toSpace(r0)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.geometry.euclidean.threed.Plane.project(org.apache.commons.math3.geometry.Point):org.apache.commons.math3.geometry.Point");
    }

    @Override // org.apache.commons.math3.geometry.partitioning.Hyperplane
    public double getTolerance() {
        return this.tolerance;
    }

    /* JADX WARN: Type inference failed for: r1v2, types: [org.apache.commons.math3.geometry.euclidean.threed.Vector3D] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void revertSelf() {
        /*
            r4 = this;
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r0 = r4.f192u
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r1 = r4.f193v
            r4.f192u = r1
            r4.f193v = r0
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r1 = r4.f194w
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r1 = r1.negate()
            r4.f194w = r1
            double r2 = r4.originOffset
            double r2 = -r2
            r4.originOffset = r2
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.geometry.euclidean.threed.Plane.revertSelf():void");
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [org.apache.commons.math3.geometry.euclidean.twod.Vector2D] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.apache.commons.math3.geometry.euclidean.twod.Vector2D toSubSpace(org.apache.commons.math3.geometry.Vector<org.apache.commons.math3.geometry.euclidean.threed.Euclidean3D> r2) {
        /*
            r1 = this;
            org.apache.commons.math3.geometry.euclidean.twod.Vector2D r0 = r1.toSubSpace(r2)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.geometry.euclidean.threed.Plane.toSubSpace(org.apache.commons.math3.geometry.Vector):org.apache.commons.math3.geometry.euclidean.twod.Vector2D");
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [org.apache.commons.math3.geometry.euclidean.threed.Vector3D] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.apache.commons.math3.geometry.euclidean.threed.Vector3D toSpace(org.apache.commons.math3.geometry.Vector<org.apache.commons.math3.geometry.euclidean.twod.Euclidean2D> r2) {
        /*
            r1 = this;
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r0 = r1.toSpace(r2)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.geometry.euclidean.threed.Plane.toSpace(org.apache.commons.math3.geometry.Vector):org.apache.commons.math3.geometry.euclidean.threed.Vector3D");
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.twod.Vector2D' to match base method */
    @Override // org.apache.commons.math3.geometry.partitioning.Embedding
    public Point<Euclidean2D> toSubSpace(Point<Euclidean3D> point) {
        Vector3D p3D = (Vector3D) point;
        return new Vector2D(p3D.dotProduct(this.f192u), p3D.dotProduct(this.f193v));
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.threed.Vector3D' to match base method */
    @Override // org.apache.commons.math3.geometry.partitioning.Embedding
    public Point<Euclidean3D> toSpace(Point<Euclidean2D> point) {
        Vector2D p2D = (Vector2D) point;
        return new Vector3D(p2D.getX(), this.f192u, p2D.getY(), this.f193v, -this.originOffset, this.f194w);
    }

    public Vector3D getPointAt(Vector2D inPlane, double offset) {
        return new Vector3D(inPlane.getX(), this.f192u, inPlane.getY(), this.f193v, offset - this.originOffset, this.f194w);
    }

    public boolean isSimilarTo(Plane plane) {
        double angle = Vector3D.angle(this.f194w, plane.f194w);
        return (angle < 1.0E-10d && FastMath.abs(this.originOffset - plane.originOffset) < this.tolerance) || (angle > 3.141592653489793d && FastMath.abs(this.originOffset + plane.originOffset) < this.tolerance);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r8v0, resolved type: org.apache.commons.math3.geometry.euclidean.threed.Rotation */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v0, types: [org.apache.commons.math3.geometry.euclidean.threed.Vector3D] */
    /* JADX WARN: Type inference failed for: r2v2, types: [org.apache.commons.math3.geometry.euclidean.threed.Vector3D] */
    /* JADX WARNING: Unknown variable types count: 2 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.apache.commons.math3.geometry.euclidean.threed.Plane rotate(org.apache.commons.math3.geometry.euclidean.threed.Vector3D r7, org.apache.commons.math3.geometry.euclidean.threed.Rotation r8) {
        /*
            r6 = this;
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r2 = r6.origin
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r0 = r2.subtract(r7)
            org.apache.commons.math3.geometry.euclidean.threed.Plane r1 = new org.apache.commons.math3.geometry.euclidean.threed.Plane
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r2 = r8.applyTo(r0)
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r2 = r7.add(r2)
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r3 = r6.f194w
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r3 = r8.applyTo(r3)
            double r4 = r6.tolerance
            r1.<init>(r2, r3, r4)
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r2 = r6.f192u
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r2 = r8.applyTo(r2)
            r1.f192u = r2
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r2 = r6.f193v
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r2 = r8.applyTo(r2)
            r1.f193v = r2
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.geometry.euclidean.threed.Plane.rotate(org.apache.commons.math3.geometry.euclidean.threed.Vector3D, org.apache.commons.math3.geometry.euclidean.threed.Rotation):org.apache.commons.math3.geometry.euclidean.threed.Plane");
    }

    /* JADX WARN: Type inference failed for: r1v1, types: [org.apache.commons.math3.geometry.euclidean.threed.Vector3D] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.apache.commons.math3.geometry.euclidean.threed.Plane translate(org.apache.commons.math3.geometry.euclidean.threed.Vector3D r7) {
        /*
            r6 = this;
            org.apache.commons.math3.geometry.euclidean.threed.Plane r0 = new org.apache.commons.math3.geometry.euclidean.threed.Plane
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r1 = r6.origin
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r1 = r1.add(r7)
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r2 = r6.f194w
            double r4 = r6.tolerance
            r0.<init>(r1, r2, r4)
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r1 = r6.f192u
            r0.f192u = r1
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r1 = r6.f193v
            r0.f193v = r1
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.geometry.euclidean.threed.Plane.translate(org.apache.commons.math3.geometry.euclidean.threed.Vector3D):org.apache.commons.math3.geometry.euclidean.threed.Plane");
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: org.apache.commons.math3.geometry.euclidean.threed.Vector3D */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r3v0, types: [org.apache.commons.math3.geometry.Vector, org.apache.commons.math3.geometry.euclidean.threed.Vector3D] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.apache.commons.math3.geometry.euclidean.threed.Vector3D intersection(org.apache.commons.math3.geometry.euclidean.threed.Line r13) {
        /*
            r12 = this;
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r6 = r13.getDirection()
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r0 = r12.f194w
            double r8 = r0.dotProduct(r6)
            double r0 = org.apache.commons.math3.util.FastMath.abs(r8)
            r10 = 4457293557087583675(0x3ddb7cdfd9d7bdbb, double:1.0E-10)
            int r0 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r0 >= 0) goto L_0x0019
            r0 = 0
        L_0x0018:
            return r0
        L_0x0019:
            org.apache.commons.math3.geometry.euclidean.oned.Vector1D r0 = org.apache.commons.math3.geometry.euclidean.oned.Vector1D.ZERO
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r3 = r13.toSpace(r0)
            double r0 = r12.originOffset
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r2 = r12.f194w
            double r10 = r2.dotProduct(r3)
            double r0 = r0 + r10
            double r0 = -r0
            double r4 = r0 / r8
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r0 = new org.apache.commons.math3.geometry.euclidean.threed.Vector3D
            r1 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            r0.<init>(r1, r3, r4, r6)
            goto L_0x0018
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.geometry.euclidean.threed.Plane.intersection(org.apache.commons.math3.geometry.euclidean.threed.Line):org.apache.commons.math3.geometry.euclidean.threed.Vector3D");
    }

    /* JADX WARN: Type inference failed for: r3v1, types: [org.apache.commons.math3.geometry.euclidean.threed.Vector3D] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.apache.commons.math3.geometry.euclidean.threed.Line intersection(org.apache.commons.math3.geometry.euclidean.threed.Plane r7) {
        /*
            r6 = this;
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r2 = r6.f194w
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r3 = r7.f194w
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r0 = org.apache.commons.math3.geometry.euclidean.threed.Vector3D.crossProduct(r2, r3)
            double r2 = r0.getNorm()
            double r4 = r6.tolerance
            int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r2 >= 0) goto L_0x0014
            r2 = 0
        L_0x0013:
            return r2
        L_0x0014:
            org.apache.commons.math3.geometry.euclidean.threed.Plane r2 = new org.apache.commons.math3.geometry.euclidean.threed.Plane
            double r4 = r6.tolerance
            r2.<init>(r0, r4)
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r1 = intersection(r6, r7, r2)
            org.apache.commons.math3.geometry.euclidean.threed.Line r2 = new org.apache.commons.math3.geometry.euclidean.threed.Line
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r3 = r1.add(r0)
            double r4 = r6.tolerance
            r2.<init>(r1, r3, r4)
            goto L_0x0013
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.geometry.euclidean.threed.Plane.intersection(org.apache.commons.math3.geometry.euclidean.threed.Plane):org.apache.commons.math3.geometry.euclidean.threed.Line");
    }

    public static Vector3D intersection(Plane plane1, Plane plane2, Plane plane3) {
        double a1 = plane1.f194w.getX();
        double b1 = plane1.f194w.getY();
        double c1 = plane1.f194w.getZ();
        double d1 = plane1.originOffset;
        double a2 = plane2.f194w.getX();
        double b2 = plane2.f194w.getY();
        double c2 = plane2.f194w.getZ();
        double d2 = plane2.originOffset;
        double a3 = plane3.f194w.getX();
        double b3 = plane3.f194w.getY();
        double c3 = plane3.f194w.getZ();
        double d3 = plane3.originOffset;
        double a23 = (b2 * c3) - (b3 * c2);
        double b23 = (c2 * a3) - (c3 * a2);
        double c23 = (a2 * b3) - (a3 * b2);
        double determinant = (a1 * a23) + (b1 * b23) + (c1 * c23);
        if (FastMath.abs(determinant) < 1.0E-10d) {
            return null;
        }
        double r = 1.0d / determinant;
        return new Vector3D(((((-a23) * d1) - (((c1 * b3) - (c3 * b1)) * d2)) - (((c2 * b1) - (c1 * b2)) * d3)) * r, ((((-b23) * d1) - (((c3 * a1) - (c1 * a3)) * d2)) - (((c1 * a2) - (c2 * a1)) * d3)) * r, ((((-c23) * d1) - (((b1 * a3) - (b3 * a1)) * d2)) - (((b2 * a1) - (b1 * a2)) * d3)) * r);
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.threed.SubPlane' to match base method */
    @Override // org.apache.commons.math3.geometry.partitioning.Hyperplane
    public SubHyperplane<Euclidean3D> wholeHyperplane() {
        return new SubPlane(this, new PolygonsSet(this.tolerance));
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.threed.PolyhedronsSet' to match base method */
    @Override // org.apache.commons.math3.geometry.partitioning.Hyperplane
    public Region<Euclidean3D> wholeSpace() {
        return new PolyhedronsSet(this.tolerance);
    }

    public boolean contains(Vector3D p) {
        return FastMath.abs(getOffset(p)) < this.tolerance;
    }

    public double getOffset(Plane plane) {
        return (sameOrientationAs(plane) ? -plane.originOffset : plane.originOffset) + this.originOffset;
    }

    public double getOffset(Vector<Euclidean3D> vector) {
        return getOffset((Point<Euclidean3D>) vector);
    }

    @Override // org.apache.commons.math3.geometry.partitioning.Hyperplane
    public double getOffset(Point<Euclidean3D> point) {
        return ((Vector3D) point).dotProduct(this.f194w) + this.originOffset;
    }

    @Override // org.apache.commons.math3.geometry.partitioning.Hyperplane
    public boolean sameOrientationAs(Hyperplane<Euclidean3D> other) {
        return ((Plane) other).f194w.dotProduct(this.f194w) > 0.0d;
    }
}
