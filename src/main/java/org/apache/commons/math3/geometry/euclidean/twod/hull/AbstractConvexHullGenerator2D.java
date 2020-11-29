package org.apache.commons.math3.geometry.euclidean.twod.hull;

import java.util.Collection;
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.geometry.euclidean.twod.Euclidean2D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.geometry.hull.ConvexHull;
import org.apache.commons.math3.util.MathUtils;

abstract class AbstractConvexHullGenerator2D implements ConvexHullGenerator2D {
    private static final double DEFAULT_TOLERANCE = 1.0E-10d;
    private final boolean includeCollinearPoints;
    private final double tolerance;

    /* access modifiers changed from: protected */
    public abstract Collection<Vector2D> findHullVertices(Collection<Vector2D> collection);

    protected AbstractConvexHullGenerator2D(boolean includeCollinearPoints2) {
        this(includeCollinearPoints2, 1.0E-10d);
    }

    protected AbstractConvexHullGenerator2D(boolean includeCollinearPoints2, double tolerance2) {
        this.includeCollinearPoints = includeCollinearPoints2;
        this.tolerance = tolerance2;
    }

    public double getTolerance() {
        return this.tolerance;
    }

    public boolean isIncludeCollinearPoints() {
        return this.includeCollinearPoints;
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.twod.hull.ConvexHull2D' to match base method */
    @Override // org.apache.commons.math3.geometry.euclidean.twod.hull.ConvexHullGenerator2D, org.apache.commons.math3.geometry.hull.ConvexHullGenerator
    public ConvexHull<Euclidean2D, Vector2D> generate(Collection<Vector2D> points) throws NullArgumentException, ConvergenceException {
        Collection<Vector2D> hullVertices;
        MathUtils.checkNotNull(points);
        if (points.size() < 2) {
            hullVertices = points;
        } else {
            hullVertices = findHullVertices(points);
        }
        try {
            return new ConvexHull2D((Vector2D[]) hullVertices.toArray(new Vector2D[hullVertices.size()]), this.tolerance);
        } catch (MathIllegalArgumentException e) {
            throw new ConvergenceException();
        }
    }
}
