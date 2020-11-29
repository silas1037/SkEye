package org.apache.commons.math3.geometry.euclidean.twod.hull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.geometry.euclidean.twod.Euclidean2D;
import org.apache.commons.math3.geometry.euclidean.twod.Line;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Precision;

public class MonotoneChain extends AbstractConvexHullGenerator2D {
    /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.twod.hull.ConvexHull2D' to match base method */
    /* JADX DEBUG: Method arguments types fixed to match base method, original types: [java.util.Collection] */
    /* JADX WARN: Type inference failed for: r0v0, types: [org.apache.commons.math3.geometry.euclidean.twod.hull.ConvexHull2D] */
    /* JADX WARNING: Unknown variable types count: 1 */
    @Override // org.apache.commons.math3.geometry.euclidean.twod.hull.AbstractConvexHullGenerator2D, org.apache.commons.math3.geometry.euclidean.twod.hull.AbstractConvexHullGenerator2D, org.apache.commons.math3.geometry.euclidean.twod.hull.ConvexHullGenerator2D, org.apache.commons.math3.geometry.hull.ConvexHullGenerator
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* bridge */ /* synthetic */ org.apache.commons.math3.geometry.hull.ConvexHull<org.apache.commons.math3.geometry.euclidean.twod.Euclidean2D, org.apache.commons.math3.geometry.euclidean.twod.Vector2D> generate(java.util.Collection<org.apache.commons.math3.geometry.euclidean.twod.Vector2D> r2) throws org.apache.commons.math3.exception.NullArgumentException, org.apache.commons.math3.exception.ConvergenceException {
        /*
            r1 = this;
            org.apache.commons.math3.geometry.euclidean.twod.hull.ConvexHull2D r0 = super.generate(r2)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.geometry.euclidean.twod.hull.MonotoneChain.generate(java.util.Collection):org.apache.commons.math3.geometry.euclidean.twod.hull.ConvexHull2D");
    }

    @Override // org.apache.commons.math3.geometry.euclidean.twod.hull.AbstractConvexHullGenerator2D
    public /* bridge */ /* synthetic */ double getTolerance() {
        return super.getTolerance();
    }

    @Override // org.apache.commons.math3.geometry.euclidean.twod.hull.AbstractConvexHullGenerator2D
    public /* bridge */ /* synthetic */ boolean isIncludeCollinearPoints() {
        return super.isIncludeCollinearPoints();
    }

    public MonotoneChain() {
        this(false);
    }

    public MonotoneChain(boolean includeCollinearPoints) {
        super(includeCollinearPoints);
    }

    public MonotoneChain(boolean includeCollinearPoints, double tolerance) {
        super(includeCollinearPoints, tolerance);
    }

    @Override // org.apache.commons.math3.geometry.euclidean.twod.hull.AbstractConvexHullGenerator2D
    public Collection<Vector2D> findHullVertices(Collection<Vector2D> points) {
        List<Vector2D> pointsSortedByXAxis = new ArrayList<>(points);
        Collections.sort(pointsSortedByXAxis, new Comparator<Vector2D>() {
            /* class org.apache.commons.math3.geometry.euclidean.twod.hull.MonotoneChain.C02451 */

            public int compare(Vector2D o1, Vector2D o2) {
                double tolerance = MonotoneChain.this.getTolerance();
                int diff = Precision.compareTo(o1.getX(), o2.getX(), tolerance);
                if (diff == 0) {
                    return Precision.compareTo(o1.getY(), o2.getY(), tolerance);
                }
                return diff;
            }
        });
        List<Vector2D> lowerHull = new ArrayList<>();
        for (Vector2D p : pointsSortedByXAxis) {
            updateHull(p, lowerHull);
        }
        List<Vector2D> upperHull = new ArrayList<>();
        for (int idx = pointsSortedByXAxis.size() - 1; idx >= 0; idx--) {
            updateHull(pointsSortedByXAxis.get(idx), upperHull);
        }
        List<Vector2D> hullVertices = new ArrayList<>((lowerHull.size() + upperHull.size()) - 2);
        for (int idx2 = 0; idx2 < lowerHull.size() - 1; idx2++) {
            hullVertices.add(lowerHull.get(idx2));
        }
        for (int idx3 = 0; idx3 < upperHull.size() - 1; idx3++) {
            hullVertices.add(upperHull.get(idx3));
        }
        if (hullVertices.isEmpty() && !lowerHull.isEmpty()) {
            hullVertices.add(lowerHull.get(0));
        }
        return hullVertices;
    }

    private void updateHull(Vector2D point, List<Vector2D> hull) {
        int index;
        double tolerance = getTolerance();
        if (hull.size() != 1 || hull.get(0).distance((Vector<Euclidean2D>) point) >= tolerance) {
            while (hull.size() >= 2) {
                int size = hull.size();
                Vector2D p1 = hull.get(size - 2);
                Vector2D p2 = hull.get(size - 1);
                double offset = new Line(p1, p2, tolerance).getOffset((Vector<Euclidean2D>) point);
                if (FastMath.abs(offset) >= tolerance) {
                    if (offset <= 0.0d) {
                        break;
                    }
                    hull.remove(size - 1);
                } else {
                    double distanceToCurrent = p1.distance((Vector<Euclidean2D>) point);
                    if (distanceToCurrent >= tolerance && p2.distance((Vector<Euclidean2D>) point) >= tolerance) {
                        double distanceToLast = p1.distance((Vector<Euclidean2D>) p2);
                        if (isIncludeCollinearPoints()) {
                            if (distanceToCurrent < distanceToLast) {
                                index = size - 1;
                            } else {
                                index = size;
                            }
                            hull.add(index, point);
                            return;
                        } else if (distanceToCurrent > distanceToLast) {
                            hull.remove(size - 1);
                            hull.add(point);
                            return;
                        } else {
                            return;
                        }
                    } else {
                        return;
                    }
                }
            }
            hull.add(point);
        }
    }
}
