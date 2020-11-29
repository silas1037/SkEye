package org.apache.commons.math3.geometry.euclidean.twod;

import java.awt.geom.AffineTransform;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.geometry.euclidean.oned.Euclidean1D;
import org.apache.commons.math3.geometry.euclidean.oned.IntervalsSet;
import org.apache.commons.math3.geometry.euclidean.oned.Vector1D;
import org.apache.commons.math3.geometry.partitioning.Embedding;
import org.apache.commons.math3.geometry.partitioning.Hyperplane;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;
import org.apache.commons.math3.geometry.partitioning.Transform;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.MathUtils;

public class Line implements Hyperplane<Euclidean2D>, Embedding<Euclidean2D, Euclidean1D> {
    private static final double DEFAULT_TOLERANCE = 1.0E-10d;
    private double angle;
    private double cos;
    private double originOffset;
    private Line reverse;
    private double sin;
    private final double tolerance;

    public Line(Vector2D p1, Vector2D p2, double tolerance2) {
        reset(p1, p2);
        this.tolerance = tolerance2;
    }

    public Line(Vector2D p, double angle2, double tolerance2) {
        reset(p, angle2);
        this.tolerance = tolerance2;
    }

    private Line(double angle2, double cos2, double sin2, double originOffset2, double tolerance2) {
        this.angle = angle2;
        this.cos = cos2;
        this.sin = sin2;
        this.originOffset = originOffset2;
        this.tolerance = tolerance2;
        this.reverse = null;
    }

    @Deprecated
    public Line(Vector2D p1, Vector2D p2) {
        this(p1, p2, 1.0E-10d);
    }

    @Deprecated
    public Line(Vector2D p, double angle2) {
        this(p, angle2, 1.0E-10d);
    }

    public Line(Line line) {
        this.angle = MathUtils.normalizeAngle(line.angle, 3.141592653589793d);
        this.cos = line.cos;
        this.sin = line.sin;
        this.originOffset = line.originOffset;
        this.tolerance = line.tolerance;
        this.reverse = null;
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.twod.Line' to match base method */
    @Override // org.apache.commons.math3.geometry.partitioning.Hyperplane
    public Hyperplane<Euclidean2D> copySelf() {
        return new Line(this);
    }

    public void reset(Vector2D p1, Vector2D p2) {
        unlinkReverse();
        double dx = p2.getX() - p1.getX();
        double dy = p2.getY() - p1.getY();
        double d = FastMath.hypot(dx, dy);
        if (d == 0.0d) {
            this.angle = 0.0d;
            this.cos = 1.0d;
            this.sin = 0.0d;
            this.originOffset = p1.getY();
            return;
        }
        this.angle = 3.141592653589793d + FastMath.atan2(-dy, -dx);
        this.cos = dx / d;
        this.sin = dy / d;
        this.originOffset = MathArrays.linearCombination(p2.getX(), p1.getY(), -p1.getX(), p2.getY()) / d;
    }

    public void reset(Vector2D p, double alpha) {
        unlinkReverse();
        this.angle = MathUtils.normalizeAngle(alpha, 3.141592653589793d);
        this.cos = FastMath.cos(this.angle);
        this.sin = FastMath.sin(this.angle);
        this.originOffset = MathArrays.linearCombination(this.cos, p.getY(), -this.sin, p.getX());
    }

    public void revertSelf() {
        unlinkReverse();
        if (this.angle < 3.141592653589793d) {
            this.angle += 3.141592653589793d;
        } else {
            this.angle -= 3.141592653589793d;
        }
        this.cos = -this.cos;
        this.sin = -this.sin;
        this.originOffset = -this.originOffset;
    }

    private void unlinkReverse() {
        if (this.reverse != null) {
            this.reverse.reverse = null;
        }
        this.reverse = null;
    }

    public Line getReverse() {
        if (this.reverse == null) {
            this.reverse = new Line(this.angle < 3.141592653589793d ? this.angle + 3.141592653589793d : this.angle - 3.141592653589793d, -this.cos, -this.sin, -this.originOffset, this.tolerance);
            this.reverse.reverse = this;
        }
        return this.reverse;
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [org.apache.commons.math3.geometry.euclidean.oned.Vector1D] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.apache.commons.math3.geometry.euclidean.oned.Vector1D toSubSpace(org.apache.commons.math3.geometry.Vector<org.apache.commons.math3.geometry.euclidean.twod.Euclidean2D> r2) {
        /*
            r1 = this;
            org.apache.commons.math3.geometry.euclidean.oned.Vector1D r0 = r1.toSubSpace(r2)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.geometry.euclidean.twod.Line.toSubSpace(org.apache.commons.math3.geometry.Vector):org.apache.commons.math3.geometry.euclidean.oned.Vector1D");
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [org.apache.commons.math3.geometry.euclidean.twod.Vector2D] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.apache.commons.math3.geometry.euclidean.twod.Vector2D toSpace(org.apache.commons.math3.geometry.Vector<org.apache.commons.math3.geometry.euclidean.oned.Euclidean1D> r2) {
        /*
            r1 = this;
            org.apache.commons.math3.geometry.euclidean.twod.Vector2D r0 = r1.toSpace(r2)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.geometry.euclidean.twod.Line.toSpace(org.apache.commons.math3.geometry.Vector):org.apache.commons.math3.geometry.euclidean.twod.Vector2D");
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.oned.Vector1D' to match base method */
    @Override // org.apache.commons.math3.geometry.partitioning.Embedding
    public Point<Euclidean1D> toSubSpace(Point<Euclidean2D> point) {
        Vector2D p2 = (Vector2D) point;
        return new Vector1D(MathArrays.linearCombination(this.cos, p2.getX(), this.sin, p2.getY()));
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.twod.Vector2D' to match base method */
    @Override // org.apache.commons.math3.geometry.partitioning.Embedding
    public Point<Euclidean2D> toSpace(Point<Euclidean1D> point) {
        double abscissa = ((Vector1D) point).getX();
        return new Vector2D(MathArrays.linearCombination(abscissa, this.cos, -this.originOffset, this.sin), MathArrays.linearCombination(abscissa, this.sin, this.originOffset, this.cos));
    }

    public Vector2D intersection(Line other) {
        double d = MathArrays.linearCombination(this.sin, other.cos, -other.sin, this.cos);
        if (FastMath.abs(d) < this.tolerance) {
            return null;
        }
        return new Vector2D(MathArrays.linearCombination(this.cos, other.originOffset, -other.cos, this.originOffset) / d, MathArrays.linearCombination(this.sin, other.originOffset, -other.sin, this.originOffset) / d);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r1v0, resolved type: org.apache.commons.math3.geometry.euclidean.twod.Line */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v0, types: [org.apache.commons.math3.geometry.Vector, org.apache.commons.math3.geometry.euclidean.oned.Vector1D] */
    /* JADX WARNING: Unknown variable types count: 1 */
    @Override // org.apache.commons.math3.geometry.partitioning.Hyperplane
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.apache.commons.math3.geometry.Point<org.apache.commons.math3.geometry.euclidean.twod.Euclidean2D> project(org.apache.commons.math3.geometry.Point<org.apache.commons.math3.geometry.euclidean.twod.Euclidean2D> r2) {
        /*
            r1 = this;
            org.apache.commons.math3.geometry.euclidean.oned.Vector1D r0 = r1.toSubSpace(r2)
            org.apache.commons.math3.geometry.euclidean.twod.Vector2D r0 = r1.toSpace(r0)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.geometry.euclidean.twod.Line.project(org.apache.commons.math3.geometry.Point):org.apache.commons.math3.geometry.Point");
    }

    @Override // org.apache.commons.math3.geometry.partitioning.Hyperplane
    public double getTolerance() {
        return this.tolerance;
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.twod.SubLine' to match base method */
    @Override // org.apache.commons.math3.geometry.partitioning.Hyperplane
    public SubHyperplane<Euclidean2D> wholeHyperplane() {
        return new SubLine(this, new IntervalsSet(this.tolerance));
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.twod.PolygonsSet' to match base method */
    @Override // org.apache.commons.math3.geometry.partitioning.Hyperplane
    public Region<Euclidean2D> wholeSpace() {
        return new PolygonsSet(this.tolerance);
    }

    public double getOffset(Line line) {
        return (MathArrays.linearCombination(this.cos, line.cos, this.sin, line.sin) > 0.0d ? -line.originOffset : line.originOffset) + this.originOffset;
    }

    public double getOffset(Vector<Euclidean2D> vector) {
        return getOffset((Point<Euclidean2D>) vector);
    }

    @Override // org.apache.commons.math3.geometry.partitioning.Hyperplane
    public double getOffset(Point<Euclidean2D> point) {
        Vector2D p2 = (Vector2D) point;
        return MathArrays.linearCombination(this.sin, p2.getX(), -this.cos, p2.getY(), 1.0d, this.originOffset);
    }

    @Override // org.apache.commons.math3.geometry.partitioning.Hyperplane
    public boolean sameOrientationAs(Hyperplane<Euclidean2D> other) {
        Line otherL = (Line) other;
        return MathArrays.linearCombination(this.sin, otherL.sin, this.cos, otherL.cos) >= 0.0d;
    }

    public Vector2D getPointAt(Vector1D abscissa, double offset) {
        double x = abscissa.getX();
        double dOffset = offset - this.originOffset;
        return new Vector2D(MathArrays.linearCombination(x, this.cos, dOffset, this.sin), MathArrays.linearCombination(x, this.sin, -dOffset, this.cos));
    }

    public boolean contains(Vector2D p) {
        return FastMath.abs(getOffset(p)) < this.tolerance;
    }

    public double distance(Vector2D p) {
        return FastMath.abs(getOffset((Vector<Euclidean2D>) p));
    }

    public boolean isParallelTo(Line line) {
        return FastMath.abs(MathArrays.linearCombination(this.sin, line.cos, -this.cos, line.sin)) < this.tolerance;
    }

    public void translateToPoint(Vector2D p) {
        this.originOffset = MathArrays.linearCombination(this.cos, p.getY(), -this.sin, p.getX());
    }

    public double getAngle() {
        return MathUtils.normalizeAngle(this.angle, 3.141592653589793d);
    }

    public void setAngle(double angle2) {
        unlinkReverse();
        this.angle = MathUtils.normalizeAngle(angle2, 3.141592653589793d);
        this.cos = FastMath.cos(this.angle);
        this.sin = FastMath.sin(this.angle);
    }

    public double getOriginOffset() {
        return this.originOffset;
    }

    public void setOriginOffset(double offset) {
        unlinkReverse();
        this.originOffset = offset;
    }

    @Deprecated
    public static Transform<Euclidean2D, Euclidean1D> getTransform(AffineTransform transform) throws MathIllegalArgumentException {
        double[] m = new double[6];
        transform.getMatrix(m);
        return new LineTransform(m[0], m[1], m[2], m[3], m[4], m[5]);
    }

    public static Transform<Euclidean2D, Euclidean1D> getTransform(double cXX, double cYX, double cXY, double cYY, double cX1, double cY1) throws MathIllegalArgumentException {
        return new LineTransform(cXX, cYX, cXY, cYY, cX1, cY1);
    }

    /* access modifiers changed from: private */
    public static class LineTransform implements Transform<Euclidean2D, Euclidean1D> {
        private double c11;
        private double c1X;
        private double c1Y;
        private double cX1;
        private double cXX;
        private double cXY;
        private double cY1;
        private double cYX;
        private double cYY;

        LineTransform(double cXX2, double cYX2, double cXY2, double cYY2, double cX12, double cY12) throws MathIllegalArgumentException {
            this.cXX = cXX2;
            this.cYX = cYX2;
            this.cXY = cXY2;
            this.cYY = cYY2;
            this.cX1 = cX12;
            this.cY1 = cY12;
            this.c1Y = MathArrays.linearCombination(cXY2, cY12, -cYY2, cX12);
            this.c1X = MathArrays.linearCombination(cXX2, cY12, -cYX2, cX12);
            this.c11 = MathArrays.linearCombination(cXX2, cYY2, -cYX2, cXY2);
            if (FastMath.abs(this.c11) < 1.0E-20d) {
                throw new MathIllegalArgumentException(LocalizedFormats.NON_INVERTIBLE_TRANSFORM, new Object[0]);
            }
        }

        /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.twod.Vector2D' to match base method */
        @Override // org.apache.commons.math3.geometry.partitioning.Transform
        public Point<Euclidean2D> apply(Point<Euclidean2D> point) {
            Vector2D p2D = (Vector2D) point;
            double x = p2D.getX();
            double y = p2D.getY();
            return new Vector2D(MathArrays.linearCombination(this.cXX, x, this.cXY, y, this.cX1, 1.0d), MathArrays.linearCombination(this.cYX, x, this.cYY, y, this.cY1, 1.0d));
        }

        /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.twod.Line' to match base method */
        @Override // org.apache.commons.math3.geometry.partitioning.Transform
        public Hyperplane<Euclidean2D> apply(Hyperplane<Euclidean2D> hyperplane) {
            Line line = (Line) hyperplane;
            double rOffset = MathArrays.linearCombination(this.c1X, line.cos, this.c1Y, line.sin, this.c11, line.originOffset);
            double rCos = MathArrays.linearCombination(this.cXX, line.cos, this.cXY, line.sin);
            double rSin = MathArrays.linearCombination(this.cYX, line.cos, this.cYY, line.sin);
            double inv = 1.0d / FastMath.sqrt((rSin * rSin) + (rCos * rCos));
            return new Line(3.141592653589793d + FastMath.atan2(-rSin, -rCos), inv * rCos, inv * rSin, inv * rOffset, line.tolerance);
        }

        /* JADX DEBUG: Multi-variable search result rejected for r3v1, resolved type: org.apache.commons.math3.geometry.euclidean.twod.Line */
        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r4v2, types: [org.apache.commons.math3.geometry.Vector, org.apache.commons.math3.geometry.euclidean.twod.Vector2D] */
        /* JADX WARNING: Unknown variable types count: 1 */
        @Override // org.apache.commons.math3.geometry.partitioning.Transform
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public org.apache.commons.math3.geometry.partitioning.SubHyperplane<org.apache.commons.math3.geometry.euclidean.oned.Euclidean1D> apply(org.apache.commons.math3.geometry.partitioning.SubHyperplane<org.apache.commons.math3.geometry.euclidean.oned.Euclidean1D> r9, org.apache.commons.math3.geometry.partitioning.Hyperplane<org.apache.commons.math3.geometry.euclidean.twod.Euclidean2D> r10, org.apache.commons.math3.geometry.partitioning.Hyperplane<org.apache.commons.math3.geometry.euclidean.twod.Euclidean2D> r11) {
            /*
                r8 = this;
                org.apache.commons.math3.geometry.partitioning.Hyperplane r1 = r9.getHyperplane()
                org.apache.commons.math3.geometry.euclidean.oned.OrientedPoint r1 = (org.apache.commons.math3.geometry.euclidean.oned.OrientedPoint) r1
                r2 = r10
                org.apache.commons.math3.geometry.euclidean.twod.Line r2 = (org.apache.commons.math3.geometry.euclidean.twod.Line) r2
                r3 = r11
                org.apache.commons.math3.geometry.euclidean.twod.Line r3 = (org.apache.commons.math3.geometry.euclidean.twod.Line) r3
                org.apache.commons.math3.geometry.euclidean.oned.Vector1D r4 = r1.getLocation()
                org.apache.commons.math3.geometry.euclidean.twod.Vector2D r4 = r2.toSpace(r4)
                org.apache.commons.math3.geometry.euclidean.twod.Vector2D r4 = r8.apply(r4)
                org.apache.commons.math3.geometry.euclidean.oned.Vector1D r0 = r3.toSubSpace(r4)
                org.apache.commons.math3.geometry.euclidean.oned.OrientedPoint r4 = new org.apache.commons.math3.geometry.euclidean.oned.OrientedPoint
                boolean r5 = r1.isDirect()
                double r6 = org.apache.commons.math3.geometry.euclidean.twod.Line.access$300(r2)
                r4.<init>(r0, r5, r6)
                org.apache.commons.math3.geometry.euclidean.oned.SubOrientedPoint r4 = r4.wholeHyperplane()
                return r4
            */
            throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.geometry.euclidean.twod.Line.LineTransform.apply(org.apache.commons.math3.geometry.partitioning.SubHyperplane, org.apache.commons.math3.geometry.partitioning.Hyperplane, org.apache.commons.math3.geometry.partitioning.Hyperplane):org.apache.commons.math3.geometry.partitioning.SubHyperplane");
        }
    }
}
