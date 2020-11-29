package org.apache.commons.math3.geometry.partitioning;

import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.Space;

public interface Transform<S extends Space, T extends Space> {
    Point<S> apply(Point<S> point);

    Hyperplane<S> apply(Hyperplane<S> hyperplane);

    SubHyperplane<T> apply(SubHyperplane<T> subHyperplane, Hyperplane<S> hyperplane, Hyperplane<S> hyperplane2);
}
