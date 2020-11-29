package org.apache.commons.math3.geometry.spherical.oned;

import org.apache.commons.math3.geometry.partitioning.AbstractSubHyperplane;
import org.apache.commons.math3.geometry.partitioning.Hyperplane;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;

public class SubLimitAngle extends AbstractSubHyperplane<Sphere1D, Sphere1D> {
    public SubLimitAngle(Hyperplane<Sphere1D> hyperplane, Region<Sphere1D> remainingRegion) {
        super(hyperplane, remainingRegion);
    }

    @Override // org.apache.commons.math3.geometry.partitioning.SubHyperplane, org.apache.commons.math3.geometry.partitioning.AbstractSubHyperplane
    public double getSize() {
        return 0.0d;
    }

    @Override // org.apache.commons.math3.geometry.partitioning.SubHyperplane, org.apache.commons.math3.geometry.partitioning.AbstractSubHyperplane
    public boolean isEmpty() {
        return false;
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.geometry.partitioning.AbstractSubHyperplane
    public AbstractSubHyperplane<Sphere1D, Sphere1D> buildNew(Hyperplane<Sphere1D> hyperplane, Region<Sphere1D> remainingRegion) {
        return new SubLimitAngle(hyperplane, remainingRegion);
    }

    @Override // org.apache.commons.math3.geometry.partitioning.SubHyperplane, org.apache.commons.math3.geometry.partitioning.AbstractSubHyperplane
    public SubHyperplane.SplitSubHyperplane<Sphere1D> split(Hyperplane<Sphere1D> hyperplane) {
        return hyperplane.getOffset(((LimitAngle) getHyperplane()).getLocation()) < -1.0E-10d ? new SubHyperplane.SplitSubHyperplane<>(null, this) : new SubHyperplane.SplitSubHyperplane<>(this, null);
    }
}
