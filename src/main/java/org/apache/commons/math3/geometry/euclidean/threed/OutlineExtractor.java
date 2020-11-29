package org.apache.commons.math3.geometry.euclidean.threed;

import org.apache.commons.math3.geometry.euclidean.twod.PolygonsSet;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.geometry.partitioning.BSPTree;
import org.apache.commons.math3.geometry.partitioning.BSPTreeVisitor;
import org.apache.commons.math3.geometry.partitioning.BoundaryAttribute;
import org.apache.commons.math3.util.FastMath;

public class OutlineExtractor {

    /* renamed from: u */
    private Vector3D f189u;

    /* renamed from: v */
    private Vector3D f190v;

    /* renamed from: w */
    private Vector3D f191w;

    public OutlineExtractor(Vector3D u, Vector3D v) {
        this.f189u = u;
        this.f190v = v;
        this.f191w = Vector3D.crossProduct(u, v);
    }

    public Vector2D[][] getOutline(PolyhedronsSet polyhedronsSet) {
        BoundaryProjector projector = new BoundaryProjector(polyhedronsSet.getTolerance());
        polyhedronsSet.getTree(true).visit(projector);
        Vector2D[][] outline = projector.getProjected().getVertices();
        for (int i = 0; i < outline.length; i++) {
            Vector2D[] rawLoop = outline[i];
            int end = rawLoop.length;
            int j = 0;
            while (j < end) {
                if (pointIsBetween(rawLoop, end, j)) {
                    for (int k = j; k < end - 1; k++) {
                        rawLoop[k] = rawLoop[k + 1];
                    }
                    end--;
                } else {
                    j++;
                }
            }
            if (end != rawLoop.length) {
                outline[i] = new Vector2D[end];
                System.arraycopy(rawLoop, 0, outline[i], 0, end);
            }
        }
        return outline;
    }

    private boolean pointIsBetween(Vector2D[] loop, int n, int i) {
        Vector2D previous = loop[((i + n) - 1) % n];
        Vector2D current = loop[i];
        Vector2D next = loop[(i + 1) % n];
        double dx1 = current.getX() - previous.getX();
        double dy1 = current.getY() - previous.getY();
        double dx2 = next.getX() - current.getX();
        double dy2 = next.getY() - current.getY();
        return FastMath.abs((dx1 * dy2) - (dx2 * dy1)) <= 1.0E-6d * FastMath.sqrt(((dx1 * dx1) + (dy1 * dy1)) * ((dx2 * dx2) + (dy2 * dy2))) && (dx1 * dx2) + (dy1 * dy2) >= 0.0d;
    }

    private class BoundaryProjector implements BSPTreeVisitor<Euclidean3D> {
        private PolygonsSet projected;
        private final double tolerance;

        BoundaryProjector(double tolerance2) {
            this.projected = new PolygonsSet(new BSPTree(Boolean.FALSE), tolerance2);
            this.tolerance = tolerance2;
        }

        @Override // org.apache.commons.math3.geometry.partitioning.BSPTreeVisitor
        public BSPTreeVisitor.Order visitOrder(BSPTree<Euclidean3D> bSPTree) {
            return BSPTreeVisitor.Order.MINUS_SUB_PLUS;
        }

        @Override // org.apache.commons.math3.geometry.partitioning.BSPTreeVisitor
        public void visitInternalNode(BSPTree<Euclidean3D> node) {
            BoundaryAttribute<Euclidean3D> attribute = (BoundaryAttribute) node.getAttribute();
            if (attribute.getPlusOutside() != null) {
                addContribution(attribute.getPlusOutside(), false);
            }
            if (attribute.getPlusInside() != null) {
                addContribution(attribute.getPlusInside(), true);
            }
        }

        @Override // org.apache.commons.math3.geometry.partitioning.BSPTreeVisitor
        public void visitLeafNode(BSPTree<Euclidean3D> bSPTree) {
        }

        /* JADX WARN: Type inference failed for: r31v0, types: [org.apache.commons.math3.geometry.euclidean.threed.Vector3D] */
        /* JADX WARN: Type inference failed for: r18v0, types: [org.apache.commons.math3.geometry.euclidean.threed.Vector3D] */
        /* JADX WARNING: Unknown variable types count: 2 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void addContribution(org.apache.commons.math3.geometry.partitioning.SubHyperplane<org.apache.commons.math3.geometry.euclidean.threed.Euclidean3D> r37, boolean r38) {
            /*
            // Method dump skipped, instructions count: 435
            */
            throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.geometry.euclidean.threed.OutlineExtractor.BoundaryProjector.addContribution(org.apache.commons.math3.geometry.partitioning.SubHyperplane, boolean):void");
        }

        public PolygonsSet getProjected() {
            return this.projected;
        }
    }
}
