package org.apache.commons.math3.geometry.euclidean.threed;

import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.euclidean.oned.Euclidean1D;
import org.apache.commons.math3.geometry.euclidean.oned.Vector1D;
import org.apache.commons.math3.geometry.euclidean.twod.Euclidean2D;
import org.apache.commons.math3.geometry.euclidean.twod.Line;
import org.apache.commons.math3.geometry.euclidean.twod.PolygonsSet;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.geometry.partitioning.AbstractSubHyperplane;
import org.apache.commons.math3.geometry.partitioning.BSPTree;
import org.apache.commons.math3.geometry.partitioning.Hyperplane;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;

public class SubPlane extends AbstractSubHyperplane<Euclidean3D, Euclidean2D> {
    public SubPlane(Hyperplane<Euclidean3D> hyperplane, Region<Euclidean2D> remainingRegion) {
        super(hyperplane, remainingRegion);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.geometry.partitioning.AbstractSubHyperplane
    public AbstractSubHyperplane<Euclidean3D, Euclidean2D> buildNew(Hyperplane<Euclidean3D> hyperplane, Region<Euclidean2D> remainingRegion) {
        return new SubPlane(hyperplane, remainingRegion);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r14v1, resolved type: org.apache.commons.math3.geometry.euclidean.twod.Vector2D */
    /* JADX DEBUG: Multi-variable search result rejected for r12v1, resolved type: org.apache.commons.math3.geometry.euclidean.twod.Vector2D */
    /* JADX DEBUG: Multi-variable search result rejected for r12v2, resolved type: org.apache.commons.math3.geometry.euclidean.twod.Vector2D */
    /* JADX DEBUG: Multi-variable search result rejected for r14v2, resolved type: org.apache.commons.math3.geometry.euclidean.twod.Vector2D */
    /* JADX DEBUG: Multi-variable search result rejected for r12v3, resolved type: org.apache.commons.math3.geometry.euclidean.twod.Vector2D */
    /* JADX DEBUG: Multi-variable search result rejected for r14v3, resolved type: org.apache.commons.math3.geometry.euclidean.twod.Vector2D */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.apache.commons.math3.geometry.partitioning.SubHyperplane, org.apache.commons.math3.geometry.partitioning.AbstractSubHyperplane
    public SubHyperplane.SplitSubHyperplane<Euclidean3D> split(Hyperplane<Euclidean3D> hyperplane) {
        Plane otherPlane = (Plane) hyperplane;
        Plane thisPlane = (Plane) getHyperplane();
        Line inter = otherPlane.intersection(thisPlane);
        double tolerance = thisPlane.getTolerance();
        if (inter == null) {
            double global = otherPlane.getOffset(thisPlane);
            if (global < (-tolerance)) {
                return new SubHyperplane.SplitSubHyperplane<>(null, this);
            }
            if (global > tolerance) {
                return new SubHyperplane.SplitSubHyperplane<>(this, null);
            }
            return new SubHyperplane.SplitSubHyperplane<>(null, null);
        }
        Point<Euclidean2D> subSpace = thisPlane.toSubSpace(inter.toSpace((Point<Euclidean1D>) Vector1D.ZERO));
        Point<Euclidean2D> subSpace2 = thisPlane.toSubSpace(inter.toSpace((Point<Euclidean1D>) Vector1D.ONE));
        Vector2D p = subSpace;
        Vector2D q = subSpace2;
        if (Vector3D.crossProduct(inter.getDirection(), thisPlane.getNormal()).dotProduct(otherPlane.getNormal()) < 0.0d) {
            p = subSpace2;
            q = subSpace;
        }
        SubHyperplane<Euclidean2D> l2DMinus = new Line(p, q, tolerance).wholeHyperplane();
        SubHyperplane<Euclidean2D> l2DPlus = new Line(q, p, tolerance).wholeHyperplane();
        BSPTree<Euclidean2D> splitTree = getRemainingRegion().getTree(false).split(l2DMinus);
        return new SubHyperplane.SplitSubHyperplane<>(new SubPlane(thisPlane.copySelf(), new PolygonsSet(getRemainingRegion().isEmpty(splitTree.getPlus()) ? new BSPTree<>(Boolean.FALSE) : new BSPTree<>(l2DPlus, new BSPTree(Boolean.FALSE), splitTree.getPlus(), null), tolerance)), new SubPlane(thisPlane.copySelf(), new PolygonsSet(getRemainingRegion().isEmpty(splitTree.getMinus()) ? new BSPTree<>(Boolean.FALSE) : new BSPTree<>(l2DMinus, new BSPTree(Boolean.FALSE), splitTree.getMinus(), null), tolerance)));
    }
}
