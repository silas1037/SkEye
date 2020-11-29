package org.apache.commons.math3.geometry.partitioning;

import org.apache.commons.math3.geometry.Space;

public class BoundaryAttribute<S extends Space> {
    private final SubHyperplane<S> plusInside;
    private final SubHyperplane<S> plusOutside;
    private final NodesSet<S> splitters;

    @Deprecated
    public BoundaryAttribute(SubHyperplane<S> plusOutside2, SubHyperplane<S> plusInside2) {
        this(plusOutside2, plusInside2, null);
    }

    BoundaryAttribute(SubHyperplane<S> plusOutside2, SubHyperplane<S> plusInside2, NodesSet<S> splitters2) {
        this.plusOutside = plusOutside2;
        this.plusInside = plusInside2;
        this.splitters = splitters2;
    }

    public SubHyperplane<S> getPlusOutside() {
        return this.plusOutside;
    }

    public SubHyperplane<S> getPlusInside() {
        return this.plusInside;
    }

    public NodesSet<S> getSplitters() {
        return this.splitters;
    }
}
