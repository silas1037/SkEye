package org.apache.commons.math3.geometry.euclidean.threed;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.euclidean.oned.Euclidean1D;
import org.apache.commons.math3.geometry.euclidean.oned.IntervalsSet;
import org.apache.commons.math3.geometry.euclidean.oned.Vector1D;
import org.apache.commons.math3.geometry.partitioning.Embedding;

public class Line implements Embedding<Euclidean3D, Euclidean1D> {
    private static final double DEFAULT_TOLERANCE = 1.0E-10d;
    private Vector3D direction;
    private final double tolerance;
    private Vector3D zero;

    public Line(Vector3D p1, Vector3D p2, double tolerance2) throws MathIllegalArgumentException {
        reset(p1, p2);
        this.tolerance = tolerance2;
    }

    public Line(Line line) {
        this.direction = line.direction;
        this.zero = line.zero;
        this.tolerance = line.tolerance;
    }

    @Deprecated
    public Line(Vector3D p1, Vector3D p2) throws MathIllegalArgumentException {
        this(p1, p2, 1.0E-10d);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r11v0, resolved type: org.apache.commons.math3.geometry.euclidean.threed.Vector3D */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r6v0, types: [org.apache.commons.math3.geometry.Vector, org.apache.commons.math3.geometry.euclidean.threed.Vector3D] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void reset(org.apache.commons.math3.geometry.euclidean.threed.Vector3D r11, org.apache.commons.math3.geometry.euclidean.threed.Vector3D r12) throws org.apache.commons.math3.exception.MathIllegalArgumentException {
        /*
            r10 = this;
            r1 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r6 = r12.subtract(r11)
            double r8 = r6.getNormSq()
            r4 = 0
            int r0 = (r8 > r4 ? 1 : (r8 == r4 ? 0 : -1))
            if (r0 != 0) goto L_0x001b
            org.apache.commons.math3.exception.MathIllegalArgumentException r0 = new org.apache.commons.math3.exception.MathIllegalArgumentException
            org.apache.commons.math3.exception.util.LocalizedFormats r1 = org.apache.commons.math3.exception.util.LocalizedFormats.ZERO_NORM
            r2 = 0
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r0.<init>(r1, r2)
            throw r0
        L_0x001b:
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r0 = new org.apache.commons.math3.geometry.euclidean.threed.Vector3D
            double r4 = org.apache.commons.math3.util.FastMath.sqrt(r8)
            double r4 = r1 / r4
            r0.<init>(r4, r6)
            r10.direction = r0
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r0 = new org.apache.commons.math3.geometry.euclidean.threed.Vector3D
            double r4 = r11.dotProduct(r6)
            double r4 = -r4
            double r4 = r4 / r8
            r3 = r11
            r0.<init>(r1, r3, r4, r6)
            r10.zero = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.geometry.euclidean.threed.Line.reset(org.apache.commons.math3.geometry.euclidean.threed.Vector3D, org.apache.commons.math3.geometry.euclidean.threed.Vector3D):void");
    }

    public double getTolerance() {
        return this.tolerance;
    }

    /* JADX WARN: Type inference failed for: r1v1, types: [org.apache.commons.math3.geometry.euclidean.threed.Vector3D] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.apache.commons.math3.geometry.euclidean.threed.Line revert() {
        /*
            r2 = this;
            org.apache.commons.math3.geometry.euclidean.threed.Line r0 = new org.apache.commons.math3.geometry.euclidean.threed.Line
            r0.<init>(r2)
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r1 = r0.direction
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r1 = r1.negate()
            r0.direction = r1
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.geometry.euclidean.threed.Line.revert():org.apache.commons.math3.geometry.euclidean.threed.Line");
    }

    public Vector3D getDirection() {
        return this.direction;
    }

    public Vector3D getOrigin() {
        return this.zero;
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.apache.commons.math3.geometry.euclidean.threed.Vector3D] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public double getAbscissa(org.apache.commons.math3.geometry.euclidean.threed.Vector3D r3) {
        /*
            r2 = this;
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r0 = r2.zero
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r0 = r3.subtract(r0)
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r1 = r2.direction
            double r0 = r0.dotProduct(r1)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.geometry.euclidean.threed.Line.getAbscissa(org.apache.commons.math3.geometry.euclidean.threed.Vector3D):double");
    }

    public Vector3D pointAt(double abscissa) {
        return new Vector3D(1.0d, this.zero, abscissa, this.direction);
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [org.apache.commons.math3.geometry.euclidean.oned.Vector1D] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.apache.commons.math3.geometry.euclidean.oned.Vector1D toSubSpace(org.apache.commons.math3.geometry.Vector<org.apache.commons.math3.geometry.euclidean.threed.Euclidean3D> r2) {
        /*
            r1 = this;
            org.apache.commons.math3.geometry.euclidean.oned.Vector1D r0 = r1.toSubSpace(r2)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.geometry.euclidean.threed.Line.toSubSpace(org.apache.commons.math3.geometry.Vector):org.apache.commons.math3.geometry.euclidean.oned.Vector1D");
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [org.apache.commons.math3.geometry.euclidean.threed.Vector3D] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.apache.commons.math3.geometry.euclidean.threed.Vector3D toSpace(org.apache.commons.math3.geometry.Vector<org.apache.commons.math3.geometry.euclidean.oned.Euclidean1D> r2) {
        /*
            r1 = this;
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r0 = r1.toSpace(r2)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.geometry.euclidean.threed.Line.toSpace(org.apache.commons.math3.geometry.Vector):org.apache.commons.math3.geometry.euclidean.threed.Vector3D");
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.oned.Vector1D' to match base method */
    @Override // org.apache.commons.math3.geometry.partitioning.Embedding
    public Point<Euclidean1D> toSubSpace(Point<Euclidean3D> point) {
        return new Vector1D(getAbscissa((Vector3D) point));
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.threed.Vector3D' to match base method */
    @Override // org.apache.commons.math3.geometry.partitioning.Embedding
    public Point<Euclidean3D> toSpace(Point<Euclidean1D> point) {
        return pointAt(((Vector1D) point).getX());
    }

    public boolean isSimilarTo(Line line) {
        double angle = Vector3D.angle(this.direction, line.direction);
        return (angle < this.tolerance || angle > 3.141592653589793d - this.tolerance) && contains(line.zero);
    }

    public boolean contains(Vector3D p) {
        return distance(p) < this.tolerance;
    }

    /* JADX WARN: Type inference failed for: r3v0, types: [org.apache.commons.math3.geometry.euclidean.threed.Vector3D] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public double distance(org.apache.commons.math3.geometry.euclidean.threed.Vector3D r8) {
        /*
            r7 = this;
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r1 = r7.zero
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r3 = r8.subtract(r1)
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r0 = new org.apache.commons.math3.geometry.euclidean.threed.Vector3D
            r1 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r4 = r7.direction
            double r4 = r3.dotProduct(r4)
            double r4 = -r4
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r6 = r7.direction
            r0.<init>(r1, r3, r4, r6)
            double r4 = r0.getNorm()
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.geometry.euclidean.threed.Line.distance(org.apache.commons.math3.geometry.euclidean.threed.Vector3D):double");
    }

    /* JADX WARN: Type inference failed for: r3v3, types: [org.apache.commons.math3.geometry.euclidean.threed.Vector3D] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public double distance(org.apache.commons.math3.geometry.euclidean.threed.Line r9) {
        /*
            r8 = this;
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r3 = r8.direction
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r6 = r9.direction
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r2 = org.apache.commons.math3.geometry.euclidean.threed.Vector3D.crossProduct(r3, r6)
            double r0 = r2.getNorm()
            double r6 = org.apache.commons.math3.util.Precision.SAFE_MIN
            int r3 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r3 >= 0) goto L_0x0019
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r3 = r9.zero
            double r6 = r8.distance(r3)
        L_0x0018:
            return r6
        L_0x0019:
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r3 = r9.zero
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r6 = r8.zero
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r3 = r3.subtract(r6)
            double r6 = r3.dotProduct(r2)
            double r4 = r6 / r0
            double r6 = org.apache.commons.math3.util.FastMath.abs(r4)
            goto L_0x0018
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.geometry.euclidean.threed.Line.distance(org.apache.commons.math3.geometry.euclidean.threed.Line):double");
    }

    /* JADX WARN: Type inference failed for: r9v0, types: [org.apache.commons.math3.geometry.euclidean.threed.Vector3D] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.apache.commons.math3.geometry.euclidean.threed.Vector3D closestPoint(org.apache.commons.math3.geometry.euclidean.threed.Line r19) {
        /*
            r18 = this;
            r0 = r18
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r2 = r0.direction
            r0 = r19
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r3 = r0.direction
            double r14 = r2.dotProduct(r3)
            r2 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            double r4 = r14 * r14
            double r16 = r2 - r4
            double r2 = org.apache.commons.math3.util.Precision.EPSILON
            int r2 = (r16 > r2 ? 1 : (r16 == r2 ? 0 : -1))
            if (r2 >= 0) goto L_0x001d
            r0 = r18
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r2 = r0.zero
        L_0x001c:
            return r2
        L_0x001d:
            r0 = r19
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r2 = r0.zero
            r0 = r18
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r3 = r0.zero
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r9 = r2.subtract(r3)
            r0 = r18
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r2 = r0.direction
            double r10 = r9.dotProduct(r2)
            r0 = r19
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r2 = r0.direction
            double r12 = r9.dotProduct(r2)
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r2 = new org.apache.commons.math3.geometry.euclidean.threed.Vector3D
            r3 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            r0 = r18
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r5 = r0.zero
            double r6 = r12 * r14
            double r6 = r10 - r6
            double r6 = r6 / r16
            r0 = r18
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r8 = r0.direction
            r2.<init>(r3, r5, r6, r8)
            goto L_0x001c
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.geometry.euclidean.threed.Line.closestPoint(org.apache.commons.math3.geometry.euclidean.threed.Line):org.apache.commons.math3.geometry.euclidean.threed.Vector3D");
    }

    public Vector3D intersection(Line line) {
        Vector3D closest = closestPoint(line);
        if (line.contains(closest)) {
            return closest;
        }
        return null;
    }

    public SubLine wholeLine() {
        return new SubLine(this, new IntervalsSet(this.tolerance));
    }
}
