package org.apache.commons.math3.geometry.spherical.oned;

import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.partitioning.Hyperplane;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;

public class LimitAngle implements Hyperplane<Sphere1D> {
    private boolean direct;
    private S1Point location;
    private final double tolerance;

    public LimitAngle(S1Point location2, boolean direct2, double tolerance2) {
        this.location = location2;
        this.direct = direct2;
        this.tolerance = tolerance2;
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.spherical.oned.LimitAngle' to match base method */
    @Override // org.apache.commons.math3.geometry.partitioning.Hyperplane
    public Hyperplane<Sphere1D> copySelf() {
        return this;
    }

    @Override // org.apache.commons.math3.geometry.partitioning.Hyperplane
    public double getOffset(Point<Sphere1D> point) {
        double delta = ((S1Point) point).getAlpha() - this.location.getAlpha();
        return this.direct ? delta : -delta;
    }

    public boolean isDirect() {
        return this.direct;
    }

    public LimitAngle getReverse() {
        return new LimitAngle(this.location, !this.direct, this.tolerance);
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.spherical.oned.SubLimitAngle' to match base method */
    @Override // org.apache.commons.math3.geometry.partitioning.Hyperplane
    public SubHyperplane<Sphere1D> wholeHyperplane() {
        return new SubLimitAngle(this, null);
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.spherical.oned.ArcsSet' to match base method */
    @Override // org.apache.commons.math3.geometry.partitioning.Hyperplane
    public Region<Sphere1D> wholeSpace() {
        return new ArcsSet(this.tolerance);
    }

    @Override // org.apache.commons.math3.geometry.partitioning.Hyperplane
    public boolean sameOrientationAs(Hyperplane<Sphere1D> other) {
        return !(this.direct ^ ((LimitAngle) other).direct);
    }

    public S1Point getLocation() {
        return this.location;
    }

    @Override // org.apache.commons.math3.geometry.partitioning.Hyperplane
    public Point<Sphere1D> project(Point<Sphere1D> point) {
        return this.location;
    }

    @Override // org.apache.commons.math3.geometry.partitioning.Hyperplane
    public double getTolerance() {
        return this.tolerance;
    }
}
