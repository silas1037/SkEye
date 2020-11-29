package org.apache.commons.math3.geometry.partitioning;

import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.Space;

public interface Region<S extends Space> {

    public enum Location {
        INSIDE,
        OUTSIDE,
        BOUNDARY
    }

    Region<S> buildNew(BSPTree<S> bSPTree);

    Location checkPoint(Point<S> point);

    boolean contains(Region<S> region);

    Region<S> copySelf();

    Point<S> getBarycenter();

    double getBoundarySize();

    double getSize();

    BSPTree<S> getTree(boolean z);

    SubHyperplane<S> intersection(SubHyperplane<S> subHyperplane);

    boolean isEmpty();

    boolean isEmpty(BSPTree<S> bSPTree);

    boolean isFull();

    boolean isFull(BSPTree<S> bSPTree);

    BoundaryProjection<S> projectToBoundary(Point<S> point);

    @Deprecated
    Side side(Hyperplane<S> hyperplane);
}
