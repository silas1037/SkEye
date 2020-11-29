package org.apache.commons.math3.geometry.partitioning;

import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.Space;

public class BoundaryProjection<S extends Space> {
    private final double offset;
    private final Point<S> original;
    private final Point<S> projected;

    public BoundaryProjection(Point<S> original2, Point<S> projected2, double offset2) {
        this.original = original2;
        this.projected = projected2;
        this.offset = offset2;
    }

    public Point<S> getOriginal() {
        return this.original;
    }

    public Point<S> getProjected() {
        return this.projected;
    }

    public double getOffset() {
        return this.offset;
    }
}
