package org.apache.commons.math3.geometry.euclidean.twod.hull;

import java.io.Serializable;
import org.apache.commons.math3.exception.InsufficientDataException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.geometry.euclidean.twod.Euclidean2D;
import org.apache.commons.math3.geometry.euclidean.twod.Line;
import org.apache.commons.math3.geometry.euclidean.twod.Segment;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.geometry.hull.ConvexHull;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.geometry.partitioning.RegionFactory;

public class ConvexHull2D implements ConvexHull<Euclidean2D, Vector2D>, Serializable {
    private static final long serialVersionUID = 20140129;
    private transient Segment[] lineSegments;
    private final double tolerance;
    private final Vector2D[] vertices;

    public ConvexHull2D(Vector2D[] vertices2, double tolerance2) throws MathIllegalArgumentException {
        this.tolerance = tolerance2;
        if (!isConvex(vertices2)) {
            throw new MathIllegalArgumentException(LocalizedFormats.NOT_CONVEX, new Object[0]);
        }
        this.vertices = (Vector2D[]) vertices2.clone();
    }

    /* JADX DEBUG: Multi-variable search result rejected for r14v0, resolved type: org.apache.commons.math3.geometry.euclidean.threed.Vector3D */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r11v0, types: [org.apache.commons.math3.geometry.euclidean.twod.Vector2D] */
    /* JADX WARN: Type inference failed for: r12v0, types: [org.apache.commons.math3.geometry.euclidean.twod.Vector2D] */
    /* JADX WARNING: Unknown variable types count: 2 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean isConvex(org.apache.commons.math3.geometry.euclidean.twod.Vector2D[] r19) {
        /*
        // Method dump skipped, instructions count: 113
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.geometry.euclidean.twod.hull.ConvexHull2D.isConvex(org.apache.commons.math3.geometry.euclidean.twod.Vector2D[]):boolean");
    }

    @Override // org.apache.commons.math3.geometry.hull.ConvexHull
    public Vector2D[] getVertices() {
        return (Vector2D[]) this.vertices.clone();
    }

    public Segment[] getLineSegments() {
        return (Segment[]) retrieveLineSegments().clone();
    }

    private Segment[] retrieveLineSegments() {
        int index;
        if (this.lineSegments == null) {
            int size = this.vertices.length;
            if (size <= 1) {
                this.lineSegments = new Segment[0];
            } else if (size == 2) {
                this.lineSegments = new Segment[1];
                Vector2D p1 = this.vertices[0];
                Vector2D p2 = this.vertices[1];
                this.lineSegments[0] = new Segment(p1, p2, new Line(p1, p2, this.tolerance));
            } else {
                this.lineSegments = new Segment[size];
                Vector2D firstPoint = null;
                Vector2D lastPoint = null;
                Vector2D[] arr$ = this.vertices;
                int len$ = arr$.length;
                int i$ = 0;
                int index2 = 0;
                while (i$ < len$) {
                    Vector2D point = arr$[i$];
                    if (lastPoint == null) {
                        firstPoint = point;
                        lastPoint = point;
                        index = index2;
                    } else {
                        index = index2 + 1;
                        this.lineSegments[index2] = new Segment(lastPoint, point, new Line(lastPoint, point, this.tolerance));
                        lastPoint = point;
                    }
                    i$++;
                    index2 = index;
                }
                this.lineSegments[index2] = new Segment(lastPoint, firstPoint, new Line(lastPoint, firstPoint, this.tolerance));
            }
        }
        return this.lineSegments;
    }

    @Override // org.apache.commons.math3.geometry.hull.ConvexHull
    public Region<Euclidean2D> createRegion() throws InsufficientDataException {
        if (this.vertices.length < 3) {
            throw new InsufficientDataException();
        }
        RegionFactory<Euclidean2D> factory = new RegionFactory<>();
        Segment[] segments = retrieveLineSegments();
        Line[] lineArray = new Line[segments.length];
        for (int i = 0; i < segments.length; i++) {
            lineArray[i] = segments[i].getLine();
        }
        return factory.buildConvex(lineArray);
    }
}
