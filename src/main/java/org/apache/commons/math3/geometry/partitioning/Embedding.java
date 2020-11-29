package org.apache.commons.math3.geometry.partitioning;

import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.Space;

public interface Embedding<S extends Space, T extends Space> {
    Point<S> toSpace(Point<T> point);

    Point<T> toSubSpace(Point<S> point);
}
