package org.apache.commons.math3.geometry.partitioning;

import org.apache.commons.math3.geometry.Space;
import org.apache.commons.math3.geometry.partitioning.BSPTreeVisitor;

/* access modifiers changed from: package-private */
public class BoundaryBuilder<S extends Space> implements BSPTreeVisitor<S> {
    BoundaryBuilder() {
    }

    @Override // org.apache.commons.math3.geometry.partitioning.BSPTreeVisitor
    public BSPTreeVisitor.Order visitOrder(BSPTree<S> bSPTree) {
        return BSPTreeVisitor.Order.PLUS_MINUS_SUB;
    }

    @Override // org.apache.commons.math3.geometry.partitioning.BSPTreeVisitor
    public void visitInternalNode(BSPTree<S> node) {
        SubHyperplane<S> plusOutside = null;
        SubHyperplane<S> plusInside = null;
        NodesSet<S> splitters = null;
        Characterization<S> plusChar = new Characterization<>(node.getPlus(), node.getCut().copySelf());
        if (plusChar.touchOutside()) {
            Characterization<S> minusChar = new Characterization<>(node.getMinus(), plusChar.outsideTouching());
            if (minusChar.touchInside()) {
                plusOutside = minusChar.insideTouching();
                splitters = new NodesSet<>();
                splitters.addAll(minusChar.getInsideSplitters());
                splitters.addAll(plusChar.getOutsideSplitters());
            }
        }
        if (plusChar.touchInside()) {
            Characterization<S> minusChar2 = new Characterization<>(node.getMinus(), plusChar.insideTouching());
            if (minusChar2.touchOutside()) {
                plusInside = minusChar2.outsideTouching();
                if (splitters == null) {
                    splitters = new NodesSet<>();
                }
                splitters.addAll(minusChar2.getOutsideSplitters());
                splitters.addAll(plusChar.getInsideSplitters());
            }
        }
        if (splitters != null) {
            for (BSPTree<S> up = node.getParent(); up != null; up = up.getParent()) {
                splitters.add(up);
            }
        }
        node.setAttribute(new BoundaryAttribute(plusOutside, plusInside, splitters));
    }

    @Override // org.apache.commons.math3.geometry.partitioning.BSPTreeVisitor
    public void visitLeafNode(BSPTree<S> bSPTree) {
    }
}
