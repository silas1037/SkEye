package org.apache.commons.math3.geometry.partitioning;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.Space;
import org.apache.commons.math3.geometry.partitioning.BSPTree;
import org.apache.commons.math3.geometry.partitioning.BSPTreeVisitor;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;

public class RegionFactory<S extends Space> {
    private final RegionFactory<S>.NodesCleaner nodeCleaner = new NodesCleaner();

    public Region<S> buildConvex(Hyperplane<S>... hyperplanes) {
        SubHyperplane.SplitSubHyperplane<S> split;
        if (hyperplanes == null || hyperplanes.length == 0) {
            return null;
        }
        Region<S> region = hyperplanes[0].wholeSpace();
        BSPTree<S> node = region.getTree(false);
        node.setAttribute(Boolean.TRUE);
        for (Hyperplane<S> hyperplane : hyperplanes) {
            if (node.insertCut(hyperplane)) {
                node.setAttribute(null);
                node.getPlus().setAttribute(Boolean.FALSE);
                node = node.getMinus();
                node.setAttribute(Boolean.TRUE);
            } else {
                SubHyperplane<S> s = hyperplane.wholeHyperplane();
                for (BSPTree<S> tree = node; tree.getParent() != null && s != null; tree = tree.getParent()) {
                    Hyperplane<S> other = tree.getParent().getCut().getHyperplane();
                    switch (s.split(other).getSide()) {
                        case HYPER:
                            if (hyperplane.sameOrientationAs(other)) {
                                break;
                            } else {
                                return getComplement(hyperplanes[0].wholeSpace());
                            }
                        case PLUS:
                            throw new MathIllegalArgumentException(LocalizedFormats.NOT_CONVEX_HYPERPLANES, new Object[0]);
                        default:
                            s = split.getMinus();
                            break;
                    }
                }
            }
        }
        return region;
    }

    public Region<S> union(Region<S> region1, Region<S> region2) {
        BSPTree<S> tree = region1.getTree(false).merge(region2.getTree(false), new UnionMerger());
        tree.visit(this.nodeCleaner);
        return region1.buildNew(tree);
    }

    public Region<S> intersection(Region<S> region1, Region<S> region2) {
        BSPTree<S> tree = region1.getTree(false).merge(region2.getTree(false), new IntersectionMerger());
        tree.visit(this.nodeCleaner);
        return region1.buildNew(tree);
    }

    public Region<S> xor(Region<S> region1, Region<S> region2) {
        BSPTree<S> tree = region1.getTree(false).merge(region2.getTree(false), new XorMerger());
        tree.visit(this.nodeCleaner);
        return region1.buildNew(tree);
    }

    public Region<S> difference(Region<S> region1, Region<S> region2) {
        BSPTree<S> tree = region1.getTree(false).merge(region2.getTree(false), new DifferenceMerger(region1, region2));
        tree.visit(this.nodeCleaner);
        return region1.buildNew(tree);
    }

    public Region<S> getComplement(Region<S> region) {
        return region.buildNew(recurseComplement(region.getTree(false)));
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private BSPTree<S> recurseComplement(BSPTree<S> node) {
        BoundaryAttribute<S> original;
        Map<BSPTree<S>, BSPTree<S>> map = new HashMap<>();
        BSPTree<S> transformedTree = recurseComplement(node, map);
        for (Map.Entry<BSPTree<S>, BSPTree<S>> entry : map.entrySet()) {
            if (!(entry.getKey().getCut() == null || (original = (BoundaryAttribute) entry.getKey().getAttribute()) == null)) {
                BoundaryAttribute<S> transformed = (BoundaryAttribute) entry.getValue().getAttribute();
                Iterator i$ = original.getSplitters().iterator();
                while (i$.hasNext()) {
                    transformed.getSplitters().add(map.get(i$.next()));
                }
            }
        }
        return transformedTree;
    }

    private BSPTree<S> recurseComplement(BSPTree<S> node, Map<BSPTree<S>, BSPTree<S>> map) {
        BSPTree<S> transformedNode;
        Boolean bool;
        if (node.getCut() == null) {
            if (((Boolean) node.getAttribute()).booleanValue()) {
                bool = Boolean.FALSE;
            } else {
                bool = Boolean.TRUE;
            }
            transformedNode = new BSPTree<>(bool);
        } else {
            BoundaryAttribute<S> attribute = (BoundaryAttribute) node.getAttribute();
            if (attribute != null) {
                attribute = new BoundaryAttribute<>(attribute.getPlusInside() == null ? null : attribute.getPlusInside().copySelf(), attribute.getPlusOutside() == null ? null : attribute.getPlusOutside().copySelf(), new NodesSet());
            }
            transformedNode = new BSPTree<>(node.getCut().copySelf(), recurseComplement(node.getPlus(), map), recurseComplement(node.getMinus(), map), attribute);
        }
        map.put(node, transformedNode);
        return transformedNode;
    }

    /* access modifiers changed from: private */
    public class UnionMerger implements BSPTree.LeafMerger<S> {
        private UnionMerger() {
        }

        @Override // org.apache.commons.math3.geometry.partitioning.BSPTree.LeafMerger
        public BSPTree<S> merge(BSPTree<S> leaf, BSPTree<S> tree, BSPTree<S> parentTree, boolean isPlusChild, boolean leafFromInstance) {
            if (((Boolean) leaf.getAttribute()).booleanValue()) {
                leaf.insertInTree(parentTree, isPlusChild, new VanishingToLeaf(true));
                return leaf;
            }
            tree.insertInTree(parentTree, isPlusChild, new VanishingToLeaf(false));
            return tree;
        }
    }

    private class IntersectionMerger implements BSPTree.LeafMerger<S> {
        private IntersectionMerger() {
        }

        @Override // org.apache.commons.math3.geometry.partitioning.BSPTree.LeafMerger
        public BSPTree<S> merge(BSPTree<S> leaf, BSPTree<S> tree, BSPTree<S> parentTree, boolean isPlusChild, boolean leafFromInstance) {
            if (((Boolean) leaf.getAttribute()).booleanValue()) {
                tree.insertInTree(parentTree, isPlusChild, new VanishingToLeaf(true));
                return tree;
            }
            leaf.insertInTree(parentTree, isPlusChild, new VanishingToLeaf(false));
            return leaf;
        }
    }

    private class XorMerger implements BSPTree.LeafMerger<S> {
        private XorMerger() {
        }

        @Override // org.apache.commons.math3.geometry.partitioning.BSPTree.LeafMerger
        public BSPTree<S> merge(BSPTree<S> leaf, BSPTree<S> tree, BSPTree<S> parentTree, boolean isPlusChild, boolean leafFromInstance) {
            BSPTree<S> t = tree;
            if (((Boolean) leaf.getAttribute()).booleanValue()) {
                t = RegionFactory.this.recurseComplement(t);
            }
            t.insertInTree(parentTree, isPlusChild, new VanishingToLeaf(true));
            return t;
        }
    }

    /* access modifiers changed from: private */
    public class DifferenceMerger implements BSPTree.LeafMerger<S>, BSPTree.VanishingCutHandler<S> {
        private final Region<S> region1;
        private final Region<S> region2;

        DifferenceMerger(Region<S> region12, Region<S> region22) {
            this.region1 = region12.copySelf();
            this.region2 = region22.copySelf();
        }

        @Override // org.apache.commons.math3.geometry.partitioning.BSPTree.LeafMerger
        public BSPTree<S> merge(BSPTree<S> leaf, BSPTree<S> tree, BSPTree<S> parentTree, boolean isPlusChild, boolean leafFromInstance) {
            BSPTree<S> instanceTree;
            if (((Boolean) leaf.getAttribute()).booleanValue()) {
                RegionFactory regionFactory = RegionFactory.this;
                if (!leafFromInstance) {
                    tree = leaf;
                }
                BSPTree<S> argTree = regionFactory.recurseComplement(tree);
                argTree.insertInTree(parentTree, isPlusChild, this);
                return argTree;
            }
            if (leafFromInstance) {
                instanceTree = leaf;
            } else {
                instanceTree = tree;
            }
            instanceTree.insertInTree(parentTree, isPlusChild, this);
            return instanceTree;
        }

        @Override // org.apache.commons.math3.geometry.partitioning.BSPTree.VanishingCutHandler
        public BSPTree<S> fixNode(BSPTree<S> node) {
            Point<S> barycenter = this.region1.buildNew(node.pruneAroundConvexCell(Boolean.TRUE, Boolean.FALSE, null)).getBarycenter();
            return new BSPTree<>(Boolean.valueOf(this.region1.checkPoint(barycenter) == Region.Location.INSIDE && this.region2.checkPoint(barycenter) == Region.Location.OUTSIDE));
        }
    }

    /* access modifiers changed from: private */
    public class NodesCleaner implements BSPTreeVisitor<S> {
        private NodesCleaner() {
        }

        @Override // org.apache.commons.math3.geometry.partitioning.BSPTreeVisitor
        public BSPTreeVisitor.Order visitOrder(BSPTree<S> bSPTree) {
            return BSPTreeVisitor.Order.PLUS_SUB_MINUS;
        }

        @Override // org.apache.commons.math3.geometry.partitioning.BSPTreeVisitor
        public void visitInternalNode(BSPTree<S> node) {
            node.setAttribute(null);
        }

        @Override // org.apache.commons.math3.geometry.partitioning.BSPTreeVisitor
        public void visitLeafNode(BSPTree<S> bSPTree) {
        }
    }

    private class VanishingToLeaf implements BSPTree.VanishingCutHandler<S> {
        private final boolean inside;

        VanishingToLeaf(boolean inside2) {
            this.inside = inside2;
        }

        @Override // org.apache.commons.math3.geometry.partitioning.BSPTree.VanishingCutHandler
        public BSPTree<S> fixNode(BSPTree<S> node) {
            if (node.getPlus().getAttribute().equals(node.getMinus().getAttribute())) {
                return new BSPTree<>(node.getPlus().getAttribute());
            }
            return new BSPTree<>(Boolean.valueOf(this.inside));
        }
    }
}
