package org.apache.commons.math3.geometry.enclosing;

import java.io.Serializable;
import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.Space;

public class EnclosingBall<S extends Space, P extends Point<S>> implements Serializable {
    private static final long serialVersionUID = 20140126;
    private final P center;
    private final double radius;
    private final P[] support;

    public EnclosingBall(P center2, double radius2, P... support2) {
        this.center = center2;
        this.radius = radius2;
        this.support = (P[]) ((Point[]) support2.clone());
    }

    public P getCenter() {
        return this.center;
    }

    public double getRadius() {
        return this.radius;
    }

    public P[] getSupport() {
        return (P[]) ((Point[]) this.support.clone());
    }

    public int getSupportSize() {
        return this.support.length;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r5v0, resolved type: P extends org.apache.commons.math3.geometry.Point<S> */
    /* JADX WARN: Multi-variable type inference failed */
    public boolean contains(P point) {
        return point.distance(this.center) <= this.radius;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r5v0, resolved type: P extends org.apache.commons.math3.geometry.Point<S> */
    /* JADX WARN: Multi-variable type inference failed */
    public boolean contains(P point, double margin) {
        return point.distance(this.center) <= this.radius + margin;
    }
}
