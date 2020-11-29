package org.apache.commons.math3.geometry.partitioning;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.Space;
import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;
import org.apache.commons.math3.util.FastMath;

public class BSPTree<S extends Space> {
    private Object attribute;
    private SubHyperplane<S> cut;
    private BSPTree<S> minus;
    private BSPTree<S> parent;
    private BSPTree<S> plus;

    public interface LeafMerger<S extends Space> {
        BSPTree<S> merge(BSPTree<S> bSPTree, BSPTree<S> bSPTree2, BSPTree<S> bSPTree3, boolean z, boolean z2);
    }

    public interface VanishingCutHandler<S extends Space> {
        BSPTree<S> fixNode(BSPTree<S> bSPTree);
    }

    public BSPTree() {
        this.cut = null;
        this.plus = null;
        this.minus = null;
        this.parent = null;
        this.attribute = null;
    }

    public BSPTree(Object attribute2) {
        this.cut = null;
        this.plus = null;
        this.minus = null;
        this.parent = null;
        this.attribute = attribute2;
    }

    public BSPTree(SubHyperplane<S> cut2, BSPTree<S> plus2, BSPTree<S> minus2, Object attribute2) {
        this.cut = cut2;
        this.plus = plus2;
        this.minus = minus2;
        this.parent = null;
        this.attribute = attribute2;
        plus2.parent = this;
        minus2.parent = this;
    }

    public boolean insertCut(Hyperplane<S> hyperplane) {
        if (this.cut != null) {
            this.plus.parent = null;
            this.minus.parent = null;
        }
        SubHyperplane<S> chopped = fitToCell(hyperplane.wholeHyperplane());
        if (chopped == null || chopped.isEmpty()) {
            this.cut = null;
            this.plus = null;
            this.minus = null;
            return false;
        }
        this.cut = chopped;
        this.plus = new BSPTree<>();
        this.plus.parent = this;
        this.minus = new BSPTree<>();
        this.minus.parent = this;
        return true;
    }

    public BSPTree<S> copySelf() {
        if (this.cut == null) {
            return new BSPTree<>(this.attribute);
        }
        return new BSPTree<>(this.cut.copySelf(), this.plus.copySelf(), this.minus.copySelf(), this.attribute);
    }

    public SubHyperplane<S> getCut() {
        return this.cut;
    }

    public BSPTree<S> getPlus() {
        return this.plus;
    }

    public BSPTree<S> getMinus() {
        return this.minus;
    }

    public BSPTree<S> getParent() {
        return this.parent;
    }

    public void setAttribute(Object attribute2) {
        this.attribute = attribute2;
    }

    public Object getAttribute() {
        return this.attribute;
    }

    public void visit(BSPTreeVisitor<S> visitor) {
        if (this.cut == null) {
            visitor.visitLeafNode(this);
            return;
        }
        switch (visitor.visitOrder(this)) {
            case PLUS_MINUS_SUB:
                this.plus.visit(visitor);
                this.minus.visit(visitor);
                visitor.visitInternalNode(this);
                return;
            case PLUS_SUB_MINUS:
                this.plus.visit(visitor);
                visitor.visitInternalNode(this);
                this.minus.visit(visitor);
                return;
            case MINUS_PLUS_SUB:
                this.minus.visit(visitor);
                this.plus.visit(visitor);
                visitor.visitInternalNode(this);
                return;
            case MINUS_SUB_PLUS:
                this.minus.visit(visitor);
                visitor.visitInternalNode(this);
                this.plus.visit(visitor);
                return;
            case SUB_PLUS_MINUS:
                visitor.visitInternalNode(this);
                this.plus.visit(visitor);
                this.minus.visit(visitor);
                return;
            case SUB_MINUS_PLUS:
                visitor.visitInternalNode(this);
                this.minus.visit(visitor);
                this.plus.visit(visitor);
                return;
            default:
                throw new MathInternalError();
        }
    }

    private SubHyperplane<S> fitToCell(SubHyperplane<S> sub) {
        SubHyperplane<S> s = sub;
        for (BSPTree<S> tree = this; tree.parent != null && s != null; tree = tree.parent) {
            if (tree == tree.parent.plus) {
                s = s.split(tree.parent.cut.getHyperplane()).getPlus();
            } else {
                s = s.split(tree.parent.cut.getHyperplane()).getMinus();
            }
        }
        return s;
    }

    @Deprecated
    public BSPTree<S> getCell(Vector<S> point) {
        return getCell(point, 1.0E-10d);
    }

    public BSPTree<S> getCell(Point<S> point, double tolerance) {
        if (this.cut == null) {
            return this;
        }
        double offset = this.cut.getHyperplane().getOffset(point);
        if (FastMath.abs(offset) < tolerance) {
            return this;
        }
        if (offset <= 0.0d) {
            return this.minus.getCell(point, tolerance);
        }
        return this.plus.getCell(point, tolerance);
    }

    public List<BSPTree<S>> getCloseCuts(Point<S> point, double maxOffset) {
        List<BSPTree<S>> close = new ArrayList<>();
        recurseCloseCuts(point, maxOffset, close);
        return close;
    }

    private void recurseCloseCuts(Point<S> point, double maxOffset, List<BSPTree<S>> close) {
        if (this.cut != null) {
            double offset = this.cut.getHyperplane().getOffset(point);
            if (offset < (-maxOffset)) {
                this.minus.recurseCloseCuts(point, maxOffset, close);
            } else if (offset > maxOffset) {
                this.plus.recurseCloseCuts(point, maxOffset, close);
            } else {
                close.add(this);
                this.minus.recurseCloseCuts(point, maxOffset, close);
                this.plus.recurseCloseCuts(point, maxOffset, close);
            }
        }
    }

    private void condense() {
        if (this.cut == null || this.plus.cut != null || this.minus.cut != null) {
            return;
        }
        if ((this.plus.attribute == null && this.minus.attribute == null) || (this.plus.attribute != null && this.plus.attribute.equals(this.minus.attribute))) {
            this.attribute = this.plus.attribute == null ? this.minus.attribute : this.plus.attribute;
            this.cut = null;
            this.plus = null;
            this.minus = null;
        }
    }

    public BSPTree<S> merge(BSPTree<S> tree, LeafMerger<S> leafMerger) {
        return merge(tree, leafMerger, null, false);
    }

    private BSPTree<S> merge(BSPTree<S> tree, LeafMerger<S> leafMerger, BSPTree<S> parentTree, boolean isPlusChild) {
        if (this.cut == null) {
            return leafMerger.merge(this, tree, parentTree, isPlusChild, true);
        }
        if (tree.cut == null) {
            return leafMerger.merge(tree, this, parentTree, isPlusChild, false);
        }
        BSPTree<S> merged = tree.split(this.cut);
        if (parentTree != null) {
            merged.parent = parentTree;
            if (isPlusChild) {
                parentTree.plus = merged;
            } else {
                parentTree.minus = merged;
            }
        }
        this.plus.merge(merged.plus, leafMerger, merged, true);
        this.minus.merge(merged.minus, leafMerger, merged, false);
        merged.condense();
        if (merged.cut == null) {
            return merged;
        }
        merged.cut = merged.fitToCell(merged.cut.getHyperplane().wholeHyperplane());
        return merged;
    }

    public BSPTree<S> split(SubHyperplane<S> sub) {
        BSPTree<S> bSPTree;
        if (this.cut == null) {
            return new BSPTree<>(sub, copySelf(), new BSPTree(this.attribute), null);
        }
        Hyperplane<S> cHyperplane = this.cut.getHyperplane();
        Hyperplane<S> sHyperplane = sub.getHyperplane();
        SubHyperplane.SplitSubHyperplane<S> subParts = sub.split(cHyperplane);
        switch (subParts.getSide()) {
            case PLUS:
                BSPTree<S> split = this.plus.split(sub);
                if (this.cut.split(sHyperplane).getSide() == Side.PLUS) {
                    split.plus = new BSPTree<>(this.cut.copySelf(), split.plus, this.minus.copySelf(), this.attribute);
                    split.plus.condense();
                    split.plus.parent = split;
                    return split;
                }
                split.minus = new BSPTree<>(this.cut.copySelf(), split.minus, this.minus.copySelf(), this.attribute);
                split.minus.condense();
                split.minus.parent = split;
                return split;
            case MINUS:
                BSPTree<S> split2 = this.minus.split(sub);
                if (this.cut.split(sHyperplane).getSide() == Side.PLUS) {
                    split2.plus = new BSPTree<>(this.cut.copySelf(), this.plus.copySelf(), split2.plus, this.attribute);
                    split2.plus.condense();
                    split2.plus.parent = split2;
                    return split2;
                }
                split2.minus = new BSPTree<>(this.cut.copySelf(), this.plus.copySelf(), split2.minus, this.attribute);
                split2.minus.condense();
                split2.minus.parent = split2;
                return split2;
            case BOTH:
                SubHyperplane.SplitSubHyperplane<S> cutParts = this.cut.split(sHyperplane);
                BSPTree<S> split3 = new BSPTree<>(sub, this.plus.split(subParts.getPlus()), this.minus.split(subParts.getMinus()), null);
                split3.plus.cut = cutParts.getPlus();
                split3.minus.cut = cutParts.getMinus();
                BSPTree<S> tmp = split3.plus.minus;
                split3.plus.minus = split3.minus.plus;
                split3.plus.minus.parent = split3.plus;
                split3.minus.plus = tmp;
                split3.minus.plus.parent = split3.minus;
                split3.plus.condense();
                split3.minus.condense();
                return split3;
            default:
                if (cHyperplane.sameOrientationAs(sHyperplane)) {
                    bSPTree = new BSPTree<>(sub, this.plus.copySelf(), this.minus.copySelf(), this.attribute);
                } else {
                    bSPTree = new BSPTree<>(sub, this.minus.copySelf(), this.plus.copySelf(), this.attribute);
                }
                return bSPTree;
        }
    }

    @Deprecated
    public void insertInTree(BSPTree<S> parentTree, boolean isPlusChild) {
        insertInTree(parentTree, isPlusChild, new VanishingCutHandler<S>() {
            /* class org.apache.commons.math3.geometry.partitioning.BSPTree.C02491 */

            @Override // org.apache.commons.math3.geometry.partitioning.BSPTree.VanishingCutHandler
            public BSPTree<S> fixNode(BSPTree<S> bSPTree) {
                throw new MathIllegalStateException(LocalizedFormats.NULL_NOT_ALLOWED, new Object[0]);
            }
        });
    }

    public void insertInTree(BSPTree<S> parentTree, boolean isPlusChild, VanishingCutHandler<S> vanishingHandler) {
        this.parent = parentTree;
        if (parentTree != null) {
            if (isPlusChild) {
                parentTree.plus = this;
            } else {
                parentTree.minus = this;
            }
        }
        if (this.cut != null) {
            for (BSPTree<S> tree = this; tree.parent != null; tree = tree.parent) {
                Hyperplane<S> hyperplane = tree.parent.cut.getHyperplane();
                if (tree == tree.parent.plus) {
                    this.cut = this.cut.split(hyperplane).getPlus();
                    this.plus.chopOffMinus(hyperplane, vanishingHandler);
                    this.minus.chopOffMinus(hyperplane, vanishingHandler);
                } else {
                    this.cut = this.cut.split(hyperplane).getMinus();
                    this.plus.chopOffPlus(hyperplane, vanishingHandler);
                    this.minus.chopOffPlus(hyperplane, vanishingHandler);
                }
                if (this.cut == null) {
                    BSPTree<S> fixed = vanishingHandler.fixNode(this);
                    this.cut = fixed.cut;
                    this.plus = fixed.plus;
                    this.minus = fixed.minus;
                    this.attribute = fixed.attribute;
                    if (this.cut == null) {
                        break;
                    }
                }
            }
            condense();
        }
    }

    public BSPTree<S> pruneAroundConvexCell(Object cellAttribute, Object otherLeafsAttributes, Object internalAttributes) {
        BSPTree<S> tree;
        BSPTree<S> tree2 = new BSPTree<>(cellAttribute);
        for (BSPTree<S> current = this; current.parent != null; current = current.parent) {
            SubHyperplane<S> parentCut = current.parent.cut.copySelf();
            BSPTree<S> sibling = new BSPTree<>(otherLeafsAttributes);
            if (current == current.parent.plus) {
                tree = new BSPTree<>(parentCut, tree2, sibling, internalAttributes);
            } else {
                tree = new BSPTree<>(parentCut, sibling, tree2, internalAttributes);
            }
            tree2 = tree;
        }
        return tree2;
    }

    private void chopOffMinus(Hyperplane<S> hyperplane, VanishingCutHandler<S> vanishingHandler) {
        if (this.cut != null) {
            this.cut = this.cut.split(hyperplane).getPlus();
            this.plus.chopOffMinus(hyperplane, vanishingHandler);
            this.minus.chopOffMinus(hyperplane, vanishingHandler);
            if (this.cut == null) {
                BSPTree<S> fixed = vanishingHandler.fixNode(this);
                this.cut = fixed.cut;
                this.plus = fixed.plus;
                this.minus = fixed.minus;
                this.attribute = fixed.attribute;
            }
        }
    }

    private void chopOffPlus(Hyperplane<S> hyperplane, VanishingCutHandler<S> vanishingHandler) {
        if (this.cut != null) {
            this.cut = this.cut.split(hyperplane).getMinus();
            this.plus.chopOffPlus(hyperplane, vanishingHandler);
            this.minus.chopOffPlus(hyperplane, vanishingHandler);
            if (this.cut == null) {
                BSPTree<S> fixed = vanishingHandler.fixNode(this);
                this.cut = fixed.cut;
                this.plus = fixed.plus;
                this.minus = fixed.minus;
                this.attribute = fixed.attribute;
            }
        }
    }
}
