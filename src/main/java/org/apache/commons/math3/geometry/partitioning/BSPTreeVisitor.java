package org.apache.commons.math3.geometry.partitioning;

import org.apache.commons.math3.geometry.Space;

public interface BSPTreeVisitor<S extends Space> {

    public enum Order {
        PLUS_MINUS_SUB,
        PLUS_SUB_MINUS,
        MINUS_PLUS_SUB,
        MINUS_SUB_PLUS,
        SUB_PLUS_MINUS,
        SUB_MINUS_PLUS
    }

    void visitInternalNode(BSPTree<S> bSPTree);

    void visitLeafNode(BSPTree<S> bSPTree);

    Order visitOrder(BSPTree<S> bSPTree);
}
