package org.apache.commons.math3.geometry.partitioning;

import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.Space;

public interface Hyperplane<S extends Space> {
    Hyperplane<S> copySelf();

    double getOffset(Point<S> point);

    double getTolerance();

    Point<S> project(Point<S> point);

    boolean sameOrientationAs(Hyperplane<S> hyperplane);

    SubHyperplane<S> wholeHyperplane();

    Region<S> wholeSpace();
}
