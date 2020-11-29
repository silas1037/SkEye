package org.apache.commons.math3.geometry.partitioning;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.Space;
import org.apache.commons.math3.geometry.partitioning.BSPTreeVisitor;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.util.FastMath;

class BoundaryProjector<S extends Space, T extends Space> implements BSPTreeVisitor<S> {
    private BSPTree<S> leaf = null;
    private double offset = Double.POSITIVE_INFINITY;
    private final Point<S> original;
    private Point<S> projected = null;

    BoundaryProjector(Point<S> original2) {
        this.original = original2;
    }

    @Override // org.apache.commons.math3.geometry.partitioning.BSPTreeVisitor
    public BSPTreeVisitor.Order visitOrder(BSPTree<S> node) {
        if (node.getCut().getHyperplane().getOffset(this.original) <= 0.0d) {
            return BSPTreeVisitor.Order.MINUS_SUB_PLUS;
        }
        return BSPTreeVisitor.Order.PLUS_SUB_MINUS;
    }

    @Override // org.apache.commons.math3.geometry.partitioning.BSPTreeVisitor
    public void visitInternalNode(BSPTree<S> node) {
        Hyperplane<S> hyperplane = node.getCut().getHyperplane();
        double signedOffset = hyperplane.getOffset(this.original);
        if (FastMath.abs(signedOffset) < this.offset) {
            Point<S> regular = hyperplane.project(this.original);
            List<Region<T>> boundaryParts = boundaryRegions(node);
            boolean regularFound = false;
            for (Region<T> part : boundaryParts) {
                if (!regularFound && belongsToPart(regular, hyperplane, part)) {
                    this.projected = regular;
                    this.offset = FastMath.abs(signedOffset);
                    regularFound = true;
                }
            }
            if (!regularFound) {
                for (Region<T> part2 : boundaryParts) {
                    Point<S> spI = singularProjection(regular, hyperplane, part2);
                    if (spI != null) {
                        double distance = this.original.distance(spI);
                        if (distance < this.offset) {
                            this.projected = spI;
                            this.offset = distance;
                        }
                    }
                }
            }
        }
    }

    @Override // org.apache.commons.math3.geometry.partitioning.BSPTreeVisitor
    public void visitLeafNode(BSPTree<S> node) {
        if (this.leaf == null) {
            this.leaf = node;
        }
    }

    public BoundaryProjection<S> getProjection() {
        this.offset = FastMath.copySign(this.offset, ((Boolean) this.leaf.getAttribute()).booleanValue() ? -1.0d : 1.0d);
        return new BoundaryProjection<>(this.original, this.projected, this.offset);
    }

    private List<Region<T>> boundaryRegions(BSPTree<S> node) {
        List<Region<T>> regions = new ArrayList<>(2);
        BoundaryAttribute<S> ba = (BoundaryAttribute) node.getAttribute();
        addRegion(ba.getPlusInside(), regions);
        addRegion(ba.getPlusOutside(), regions);
        return regions;
    }

    private void addRegion(SubHyperplane<S> sub, List<Region<T>> list) {
        Region<T> region;
        if (sub != null && (region = ((AbstractSubHyperplane) sub).getRemainingRegion()) != null) {
            list.add(region);
        }
    }

    private boolean belongsToPart(Point<S> point, Hyperplane<S> hyperplane, Region<T> part) {
        return part.checkPoint(((Embedding) hyperplane).toSubSpace(point)) != Region.Location.OUTSIDE;
    }

    private Point<S> singularProjection(Point<S> point, Hyperplane<S> hyperplane, Region<T> part) {
        Embedding<S, T> embedding = (Embedding) hyperplane;
        BoundaryProjection<T> bp = part.projectToBoundary(embedding.toSubSpace(point));
        if (bp.getProjected() == null) {
            return null;
        }
        return embedding.toSpace(bp.getProjected());
    }
}
