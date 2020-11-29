package org.apache.commons.math3.geometry.spherical.twod;

import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.partitioning.Embedding;
import org.apache.commons.math3.geometry.partitioning.Hyperplane;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;
import org.apache.commons.math3.geometry.partitioning.Transform;
import org.apache.commons.math3.geometry.spherical.oned.Arc;
import org.apache.commons.math3.geometry.spherical.oned.ArcsSet;
import org.apache.commons.math3.geometry.spherical.oned.S1Point;
import org.apache.commons.math3.geometry.spherical.oned.Sphere1D;
import org.apache.commons.math3.util.FastMath;

public class Circle implements Hyperplane<Sphere2D>, Embedding<Sphere2D, Sphere1D> {
    private Vector3D pole;
    private final double tolerance;

    /* renamed from: x */
    private Vector3D f214x;

    /* renamed from: y */
    private Vector3D f215y;

    public Circle(Vector3D pole2, double tolerance2) {
        reset(pole2);
        this.tolerance = tolerance2;
    }

    public Circle(S2Point first, S2Point second, double tolerance2) {
        reset(first.getVector().crossProduct(second.getVector()));
        this.tolerance = tolerance2;
    }

    private Circle(Vector3D pole2, Vector3D x, Vector3D y, double tolerance2) {
        this.pole = pole2;
        this.f214x = x;
        this.f215y = y;
        this.tolerance = tolerance2;
    }

    public Circle(Circle circle) {
        this(circle.pole, circle.f214x, circle.f215y, circle.tolerance);
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.spherical.twod.Circle' to match base method */
    @Override // org.apache.commons.math3.geometry.partitioning.Hyperplane
    public Hyperplane<Sphere2D> copySelf() {
        return new Circle(this);
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [org.apache.commons.math3.geometry.euclidean.threed.Vector3D] */
    /* JADX WARN: Type inference failed for: r0v4, types: [org.apache.commons.math3.geometry.euclidean.threed.Vector3D] */
    /* JADX WARNING: Unknown variable types count: 2 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void reset(org.apache.commons.math3.geometry.euclidean.threed.Vector3D r2) {
        /*
            r1 = this;
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r0 = r2.normalize()
            r1.pole = r0
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r0 = r2.orthogonal()
            r1.f214x = r0
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r0 = r1.f214x
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r0 = org.apache.commons.math3.geometry.euclidean.threed.Vector3D.crossProduct(r2, r0)
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r0 = r0.normalize()
            r1.f215y = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.geometry.spherical.twod.Circle.reset(org.apache.commons.math3.geometry.euclidean.threed.Vector3D):void");
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.apache.commons.math3.geometry.euclidean.threed.Vector3D] */
    /* JADX WARN: Type inference failed for: r0v3, types: [org.apache.commons.math3.geometry.euclidean.threed.Vector3D] */
    /* JADX WARNING: Unknown variable types count: 2 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void revertSelf() {
        /*
            r1 = this;
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r0 = r1.f215y
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r0 = r0.negate()
            r1.f215y = r0
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r0 = r1.pole
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r0 = r0.negate()
            r1.pole = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.geometry.spherical.twod.Circle.revertSelf():void");
    }

    /* JADX WARN: Type inference failed for: r1v1, types: [org.apache.commons.math3.geometry.euclidean.threed.Vector3D] */
    /* JADX WARN: Type inference failed for: r3v1, types: [org.apache.commons.math3.geometry.euclidean.threed.Vector3D] */
    /* JADX WARNING: Unknown variable types count: 2 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.apache.commons.math3.geometry.spherical.twod.Circle getReverse() {
        /*
            r6 = this;
            org.apache.commons.math3.geometry.spherical.twod.Circle r0 = new org.apache.commons.math3.geometry.spherical.twod.Circle
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r1 = r6.pole
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r1 = r1.negate()
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r2 = r6.f214x
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r3 = r6.f215y
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r3 = r3.negate()
            double r4 = r6.tolerance
            r0.<init>(r1, r2, r3, r4)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.geometry.spherical.twod.Circle.getReverse():org.apache.commons.math3.geometry.spherical.twod.Circle");
    }

    @Override // org.apache.commons.math3.geometry.partitioning.Hyperplane
    public Point<Sphere2D> project(Point<Sphere2D> point) {
        return toSpace(toSubSpace(point));
    }

    @Override // org.apache.commons.math3.geometry.partitioning.Hyperplane
    public double getTolerance() {
        return this.tolerance;
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.spherical.oned.S1Point' to match base method */
    @Override // org.apache.commons.math3.geometry.partitioning.Embedding
    public Point<Sphere1D> toSubSpace(Point<Sphere2D> point) {
        return new S1Point(getPhase(((S2Point) point).getVector()));
    }

    public double getPhase(Vector3D direction) {
        return 3.141592653589793d + FastMath.atan2(-direction.dotProduct(this.f215y), -direction.dotProduct(this.f214x));
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.spherical.twod.S2Point' to match base method */
    @Override // org.apache.commons.math3.geometry.partitioning.Embedding
    public Point<Sphere2D> toSpace(Point<Sphere1D> point) {
        return new S2Point(getPointAt(((S1Point) point).getAlpha()));
    }

    public Vector3D getPointAt(double alpha) {
        return new Vector3D(FastMath.cos(alpha), this.f214x, FastMath.sin(alpha), this.f215y);
    }

    public Vector3D getXAxis() {
        return this.f214x;
    }

    public Vector3D getYAxis() {
        return this.f215y;
    }

    public Vector3D getPole() {
        return this.pole;
    }

    public Arc getInsideArc(Circle other) {
        double alpha = getPhase(other.pole);
        return new Arc(alpha - 1.5707963267948966d, 1.5707963267948966d + alpha, this.tolerance);
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.spherical.twod.SubCircle' to match base method */
    @Override // org.apache.commons.math3.geometry.partitioning.Hyperplane
    public SubHyperplane<Sphere2D> wholeHyperplane() {
        return new SubCircle(this, new ArcsSet(this.tolerance));
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.spherical.twod.SphericalPolygonsSet' to match base method */
    @Override // org.apache.commons.math3.geometry.partitioning.Hyperplane
    public Region<Sphere2D> wholeSpace() {
        return new SphericalPolygonsSet(this.tolerance);
    }

    @Override // org.apache.commons.math3.geometry.partitioning.Hyperplane
    public double getOffset(Point<Sphere2D> point) {
        return getOffset(((S2Point) point).getVector());
    }

    public double getOffset(Vector3D direction) {
        return Vector3D.angle(this.pole, direction) - 1.5707963267948966d;
    }

    @Override // org.apache.commons.math3.geometry.partitioning.Hyperplane
    public boolean sameOrientationAs(Hyperplane<Sphere2D> other) {
        return Vector3D.dotProduct(this.pole, ((Circle) other).pole) >= 0.0d;
    }

    public static Transform<Sphere2D, Sphere1D> getTransform(Rotation rotation) {
        return new CircleTransform(rotation);
    }

    private static class CircleTransform implements Transform<Sphere2D, Sphere1D> {
        private final Rotation rotation;

        CircleTransform(Rotation rotation2) {
            this.rotation = rotation2;
        }

        /* Return type fixed from 'org.apache.commons.math3.geometry.spherical.twod.S2Point' to match base method */
        @Override // org.apache.commons.math3.geometry.partitioning.Transform
        public Point<Sphere2D> apply(Point<Sphere2D> point) {
            return new S2Point(this.rotation.applyTo(((S2Point) point).getVector()));
        }

        /* Return type fixed from 'org.apache.commons.math3.geometry.spherical.twod.Circle' to match base method */
        @Override // org.apache.commons.math3.geometry.partitioning.Transform
        public Hyperplane<Sphere2D> apply(Hyperplane<Sphere2D> hyperplane) {
            Circle circle = (Circle) hyperplane;
            return new Circle(this.rotation.applyTo(circle.pole), this.rotation.applyTo(circle.f214x), this.rotation.applyTo(circle.f215y), circle.tolerance);
        }

        @Override // org.apache.commons.math3.geometry.partitioning.Transform
        public SubHyperplane<Sphere1D> apply(SubHyperplane<Sphere1D> sub, Hyperplane<Sphere2D> hyperplane, Hyperplane<Sphere2D> hyperplane2) {
            return sub;
        }
    }
}
