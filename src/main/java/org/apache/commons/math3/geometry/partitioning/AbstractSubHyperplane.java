package org.apache.commons.math3.geometry.partitioning;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.math3.geometry.Space;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;

public abstract class AbstractSubHyperplane<S extends Space, T extends Space> implements SubHyperplane<S> {
    private final Hyperplane<S> hyperplane;
    private final Region<T> remainingRegion;

    /* access modifiers changed from: protected */
    public abstract AbstractSubHyperplane<S, T> buildNew(Hyperplane<S> hyperplane2, Region<T> region);

    @Override // org.apache.commons.math3.geometry.partitioning.SubHyperplane
    public abstract SubHyperplane.SplitSubHyperplane<S> split(Hyperplane<S> hyperplane2);

    protected AbstractSubHyperplane(Hyperplane<S> hyperplane2, Region<T> remainingRegion2) {
        this.hyperplane = hyperplane2;
        this.remainingRegion = remainingRegion2;
    }

    @Override // org.apache.commons.math3.geometry.partitioning.SubHyperplane
    public AbstractSubHyperplane<S, T> copySelf() {
        return buildNew(this.hyperplane.copySelf(), this.remainingRegion);
    }

    @Override // org.apache.commons.math3.geometry.partitioning.SubHyperplane
    public Hyperplane<S> getHyperplane() {
        return this.hyperplane;
    }

    public Region<T> getRemainingRegion() {
        return this.remainingRegion;
    }

    @Override // org.apache.commons.math3.geometry.partitioning.SubHyperplane
    public double getSize() {
        return this.remainingRegion.getSize();
    }

    @Override // org.apache.commons.math3.geometry.partitioning.SubHyperplane
    public AbstractSubHyperplane<S, T> reunite(SubHyperplane<S> other) {
        return buildNew(this.hyperplane, new RegionFactory().union(this.remainingRegion, ((AbstractSubHyperplane) other).remainingRegion));
    }

    public AbstractSubHyperplane<S, T> applyTransform(Transform<S, T> transform) {
        BoundaryAttribute<T> original;
        Hyperplane<S> tHyperplane = transform.apply(this.hyperplane);
        Map<BSPTree<T>, BSPTree<T>> map = new HashMap<>();
        BSPTree<T> tTree = recurseTransform(this.remainingRegion.getTree(false), tHyperplane, transform, map);
        for (Map.Entry<BSPTree<T>, BSPTree<T>> entry : map.entrySet()) {
            if (!(entry.getKey().getCut() == null || (original = (BoundaryAttribute) entry.getKey().getAttribute()) == null)) {
                BoundaryAttribute<T> transformed = (BoundaryAttribute) entry.getValue().getAttribute();
                Iterator i$ = original.getSplitters().iterator();
                while (i$.hasNext()) {
                    transformed.getSplitters().add(map.get(i$.next()));
                }
            }
        }
        return buildNew(tHyperplane, this.remainingRegion.buildNew(tTree));
    }

    private BSPTree<T> recurseTransform(BSPTree<T> node, Hyperplane<S> transformed, Transform<S, T> transform, Map<BSPTree<T>, BSPTree<T>> map) {
        BSPTree<T> transformedNode;
        if (node.getCut() == null) {
            transformedNode = new BSPTree<>(node.getAttribute());
        } else {
            BoundaryAttribute<T> attribute = (BoundaryAttribute) node.getAttribute();
            if (attribute != null) {
                attribute = new BoundaryAttribute<>(attribute.getPlusOutside() == null ? null : transform.apply(attribute.getPlusOutside(), this.hyperplane, transformed), attribute.getPlusInside() == null ? null : transform.apply(attribute.getPlusInside(), this.hyperplane, transformed), new NodesSet());
            }
            transformedNode = new BSPTree<>(transform.apply(node.getCut(), this.hyperplane, transformed), recurseTransform(node.getPlus(), transformed, transform, map), recurseTransform(node.getMinus(), transformed, transform, map), attribute);
        }
        map.put(node, transformedNode);
        return transformedNode;
    }

    @Override // org.apache.commons.math3.geometry.partitioning.SubHyperplane
    @Deprecated
    public Side side(Hyperplane<S> hyper) {
        return split(hyper).getSide();
    }

    @Override // org.apache.commons.math3.geometry.partitioning.SubHyperplane
    public boolean isEmpty() {
        return this.remainingRegion.isEmpty();
    }
}
