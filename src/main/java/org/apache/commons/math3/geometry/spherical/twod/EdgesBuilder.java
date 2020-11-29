package org.apache.commons.math3.geometry.spherical.twod;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.partitioning.BSPTree;
import org.apache.commons.math3.geometry.partitioning.BSPTreeVisitor;
import org.apache.commons.math3.geometry.partitioning.BoundaryAttribute;

/* access modifiers changed from: package-private */
public class EdgesBuilder implements BSPTreeVisitor<Sphere2D> {
    private final Map<Edge, BSPTree<Sphere2D>> edgeToNode = new IdentityHashMap();
    private final Map<BSPTree<Sphere2D>, List<Edge>> nodeToEdgesList = new IdentityHashMap();
    private final BSPTree<Sphere2D> root;
    private final double tolerance;

    EdgesBuilder(BSPTree<Sphere2D> root2, double tolerance2) {
        this.root = root2;
        this.tolerance = tolerance2;
    }

    @Override // org.apache.commons.math3.geometry.partitioning.BSPTreeVisitor
    public BSPTreeVisitor.Order visitOrder(BSPTree<Sphere2D> bSPTree) {
        return BSPTreeVisitor.Order.MINUS_SUB_PLUS;
    }

    @Override // org.apache.commons.math3.geometry.partitioning.BSPTreeVisitor
    public void visitInternalNode(BSPTree<Sphere2D> node) {
        this.nodeToEdgesList.put(node, new ArrayList());
        BoundaryAttribute<Sphere2D> attribute = (BoundaryAttribute) node.getAttribute();
        if (attribute.getPlusOutside() != null) {
            addContribution((SubCircle) attribute.getPlusOutside(), false, node);
        }
        if (attribute.getPlusInside() != null) {
            addContribution((SubCircle) attribute.getPlusInside(), true, node);
        }
    }

    @Override // org.apache.commons.math3.geometry.partitioning.BSPTreeVisitor
    public void visitLeafNode(BSPTree<Sphere2D> bSPTree) {
    }

    /* JADX WARN: Type inference failed for: r6v4, types: [org.apache.commons.math3.geometry.spherical.twod.S2Point] */
    /* JADX WARN: Type inference failed for: r6v6, types: [org.apache.commons.math3.geometry.spherical.twod.S2Point] */
    /* JADX WARNING: Unknown variable types count: 2 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void addContribution(org.apache.commons.math3.geometry.spherical.twod.SubCircle r16, boolean r17, org.apache.commons.math3.geometry.partitioning.BSPTree<org.apache.commons.math3.geometry.spherical.twod.Sphere2D> r18) {
        /*
        // Method dump skipped, instructions count: 124
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.geometry.spherical.twod.EdgesBuilder.addContribution(org.apache.commons.math3.geometry.spherical.twod.SubCircle, boolean, org.apache.commons.math3.geometry.partitioning.BSPTree):void");
    }

    private Edge getFollowingEdge(Edge previous) throws MathIllegalStateException {
        S2Point point = previous.getEnd().getLocation();
        List<BSPTree<Sphere2D>> candidates = this.root.getCloseCuts(point, this.tolerance);
        double closest = this.tolerance;
        Edge following = null;
        for (BSPTree<Sphere2D> node : candidates) {
            for (Edge edge : this.nodeToEdgesList.get(node)) {
                if (edge != previous && edge.getStart().getIncoming() == null) {
                    double gap = Vector3D.angle(point.getVector(), edge.getStart().getLocation().getVector());
                    if (gap <= closest) {
                        closest = gap;
                        following = edge;
                    }
                }
            }
        }
        if (following != null) {
            return following;
        }
        if (Vector3D.angle(point.getVector(), previous.getStart().getLocation().getVector()) <= this.tolerance) {
            return previous;
        }
        throw new MathIllegalStateException(LocalizedFormats.OUTLINE_BOUNDARY_LOOP_OPEN, new Object[0]);
    }

    public List<Edge> getEdges() throws MathIllegalStateException {
        for (Edge previous : this.edgeToNode.keySet()) {
            previous.setNextEdge(getFollowingEdge(previous));
        }
        return new ArrayList(this.edgeToNode.keySet());
    }
}
