package org.apache.commons.math3.geometry.partitioning;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.geometry.Space;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;

class Characterization<S extends Space> {
    private final NodesSet<S> insideSplitters = new NodesSet<>();
    private SubHyperplane<S> insideTouching = null;
    private final NodesSet<S> outsideSplitters = new NodesSet<>();
    private SubHyperplane<S> outsideTouching = null;

    Characterization(BSPTree<S> node, SubHyperplane<S> sub) {
        characterize(node, sub, new ArrayList());
    }

    private void characterize(BSPTree<S> node, SubHyperplane<S> sub, List<BSPTree<S>> splitters) {
        if (node.getCut() != null) {
            SubHyperplane.SplitSubHyperplane<S> split = sub.split(node.getCut().getHyperplane());
            switch (split.getSide()) {
                case PLUS:
                    characterize(node.getPlus(), sub, splitters);
                    return;
                case MINUS:
                    characterize(node.getMinus(), sub, splitters);
                    return;
                case BOTH:
                    splitters.add(node);
                    characterize(node.getPlus(), split.getPlus(), splitters);
                    characterize(node.getMinus(), split.getMinus(), splitters);
                    splitters.remove(splitters.size() - 1);
                    return;
                default:
                    throw new MathInternalError();
            }
        } else if (((Boolean) node.getAttribute()).booleanValue()) {
            addInsideTouching(sub, splitters);
        } else {
            addOutsideTouching(sub, splitters);
        }
    }

    private void addOutsideTouching(SubHyperplane<S> sub, List<BSPTree<S>> splitters) {
        if (this.outsideTouching == null) {
            this.outsideTouching = sub;
        } else {
            this.outsideTouching = this.outsideTouching.reunite(sub);
        }
        this.outsideSplitters.addAll(splitters);
    }

    private void addInsideTouching(SubHyperplane<S> sub, List<BSPTree<S>> splitters) {
        if (this.insideTouching == null) {
            this.insideTouching = sub;
        } else {
            this.insideTouching = this.insideTouching.reunite(sub);
        }
        this.insideSplitters.addAll(splitters);
    }

    public boolean touchOutside() {
        return this.outsideTouching != null && !this.outsideTouching.isEmpty();
    }

    public SubHyperplane<S> outsideTouching() {
        return this.outsideTouching;
    }

    public NodesSet<S> getOutsideSplitters() {
        return this.outsideSplitters;
    }

    public boolean touchInside() {
        return this.insideTouching != null && !this.insideTouching.isEmpty();
    }

    public SubHyperplane<S> insideTouching() {
        return this.insideTouching;
    }

    public NodesSet<S> getInsideSplitters() {
        return this.insideSplitters;
    }
}
