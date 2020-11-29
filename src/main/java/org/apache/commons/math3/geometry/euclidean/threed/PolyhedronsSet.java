package org.apache.commons.math3.geometry.euclidean.threed;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.geometry.euclidean.oned.Euclidean1D;
import org.apache.commons.math3.geometry.euclidean.twod.Euclidean2D;
import org.apache.commons.math3.geometry.euclidean.twod.PolygonsSet;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.geometry.partitioning.AbstractRegion;
import org.apache.commons.math3.geometry.partitioning.BSPTree;
import org.apache.commons.math3.geometry.partitioning.BSPTreeVisitor;
import org.apache.commons.math3.geometry.partitioning.BoundaryAttribute;
import org.apache.commons.math3.geometry.partitioning.Hyperplane;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.geometry.partitioning.RegionFactory;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;
import org.apache.commons.math3.geometry.partitioning.Transform;
import org.apache.commons.math3.util.FastMath;

public class PolyhedronsSet extends AbstractRegion<Euclidean3D, Euclidean2D> {
    private static final double DEFAULT_TOLERANCE = 1.0E-10d;

    public PolyhedronsSet(double tolerance) {
        super(tolerance);
    }

    public PolyhedronsSet(BSPTree<Euclidean3D> tree, double tolerance) {
        super(tree, tolerance);
    }

    public PolyhedronsSet(Collection<SubHyperplane<Euclidean3D>> boundary, double tolerance) {
        super(boundary, tolerance);
    }

    public PolyhedronsSet(List<Vector3D> vertices, List<int[]> facets, double tolerance) {
        super(buildBoundary(vertices, facets, tolerance), tolerance);
    }

    public PolyhedronsSet(double xMin, double xMax, double yMin, double yMax, double zMin, double zMax, double tolerance) {
        super(buildBoundary(xMin, xMax, yMin, yMax, zMin, zMax, tolerance), tolerance);
    }

    @Deprecated
    public PolyhedronsSet() {
        this(1.0E-10d);
    }

    @Deprecated
    public PolyhedronsSet(BSPTree<Euclidean3D> tree) {
        this(tree, 1.0E-10d);
    }

    @Deprecated
    public PolyhedronsSet(Collection<SubHyperplane<Euclidean3D>> boundary) {
        this(boundary, 1.0E-10d);
    }

    @Deprecated
    public PolyhedronsSet(double xMin, double xMax, double yMin, double yMax, double zMin, double zMax) {
        this(xMin, xMax, yMin, yMax, zMin, zMax, 1.0E-10d);
    }

    private static BSPTree<Euclidean3D> buildBoundary(double xMin, double xMax, double yMin, double yMax, double zMin, double zMax, double tolerance) {
        if (xMin >= xMax - tolerance || yMin >= yMax - tolerance || zMin >= zMax - tolerance) {
            return new BSPTree<>(Boolean.FALSE);
        }
        Plane pxMin = new Plane(new Vector3D(xMin, 0.0d, 0.0d), Vector3D.MINUS_I, tolerance);
        Plane pxMax = new Plane(new Vector3D(xMax, 0.0d, 0.0d), Vector3D.PLUS_I, tolerance);
        Plane pyMin = new Plane(new Vector3D(0.0d, yMin, 0.0d), Vector3D.MINUS_J, tolerance);
        Plane pyMax = new Plane(new Vector3D(0.0d, yMax, 0.0d), Vector3D.PLUS_J, tolerance);
        Plane pzMin = new Plane(new Vector3D(0.0d, 0.0d, zMin), Vector3D.MINUS_K, tolerance);
        Plane pzMax = new Plane(new Vector3D(0.0d, 0.0d, zMax), Vector3D.PLUS_K, tolerance);
        return new RegionFactory().buildConvex(pxMin, pxMax, pyMin, pyMax, pzMin, pzMax).getTree(false);
    }

    private static List<SubHyperplane<Euclidean3D>> buildBoundary(List<Vector3D> vertices, List<int[]> facets, double tolerance) {
        for (int i = 0; i < vertices.size() - 1; i++) {
            Vector3D vi = vertices.get(i);
            for (int j = i + 1; j < vertices.size(); j++) {
                if (Vector3D.distance(vi, vertices.get(j)) <= tolerance) {
                    throw new MathIllegalArgumentException(LocalizedFormats.CLOSE_VERTICES, Double.valueOf(vi.getX()), Double.valueOf(vi.getY()), Double.valueOf(vi.getZ()));
                }
            }
        }
        int[][] successors = successors(vertices, facets, findReferences(vertices, facets));
        int vA = 0;
        while (vA < vertices.size()) {
            int[] arr$ = successors[vA];
            int length = arr$.length;
            for (int i$ = 0; i$ < length; i$++) {
                int vB = arr$[i$];
                if (vB >= 0) {
                    boolean found = false;
                    int[] arr$2 = successors[vB];
                    int len$ = arr$2.length;
                    for (int i$2 = 0; i$2 < len$; i$2++) {
                        found = found || arr$2[i$2] == vA;
                    }
                    if (!found) {
                        Vector3D start = vertices.get(vA);
                        Vector3D end = vertices.get(vB);
                        throw new MathIllegalArgumentException(LocalizedFormats.EDGE_CONNECTED_TO_ONE_FACET, Double.valueOf(start.getX()), Double.valueOf(start.getY()), Double.valueOf(start.getZ()), Double.valueOf(end.getX()), Double.valueOf(end.getY()), Double.valueOf(end.getZ()));
                    }
                }
            }
            vA++;
        }
        List<SubHyperplane<Euclidean3D>> boundary = new ArrayList<>();
        for (int[] facet : facets) {
            Plane plane = new Plane(vertices.get(facet[0]), vertices.get(facet[1]), vertices.get(facet[2]), tolerance);
            Vector2D[] two2Points = new Vector2D[facet.length];
            for (int i2 = 0; i2 < facet.length; i2++) {
                Vector3D v = vertices.get(facet[i2]);
                if (!plane.contains(v)) {
                    throw new MathIllegalArgumentException(LocalizedFormats.OUT_OF_PLANE, Double.valueOf(v.getX()), Double.valueOf(v.getY()), Double.valueOf(v.getZ()));
                }
                two2Points[i2] = plane.toSubSpace((Vector<Euclidean3D>) v);
            }
            boundary.add(new SubPlane(plane, new PolygonsSet(tolerance, two2Points)));
        }
        return boundary;
    }

    private static int[][] findReferences(List<Vector3D> vertices, List<int[]> facets) {
        int[] nbFacets = new int[vertices.size()];
        int maxFacets = 0;
        for (int[] facet : facets) {
            if (facet.length < 3) {
                throw new NumberIsTooSmallException(LocalizedFormats.WRONG_NUMBER_OF_POINTS, 3, Integer.valueOf(facet.length), true);
            }
            for (int index : facet) {
                int i = nbFacets[index] + 1;
                nbFacets[index] = i;
                maxFacets = FastMath.max(maxFacets, i);
            }
        }
        int[][] references = (int[][]) Array.newInstance(Integer.TYPE, vertices.size(), maxFacets);
        for (int[] r : references) {
            Arrays.fill(r, -1);
        }
        for (int f = 0; f < facets.size(); f++) {
            int[] arr$ = facets.get(f);
            int len$ = arr$.length;
            for (int i$ = 0; i$ < len$; i$++) {
                int v = arr$[i$];
                int k = 0;
                while (k < maxFacets && references[v][k] >= 0) {
                    k++;
                }
                references[v][k] = f;
            }
        }
        return references;
    }

    private static int[][] successors(List<Vector3D> vertices, List<int[]> facets, int[][] references) {
        int[][] successors = (int[][]) Array.newInstance(Integer.TYPE, vertices.size(), references[0].length);
        for (int[] s : successors) {
            Arrays.fill(s, -1);
        }
        int v = 0;
        while (v < vertices.size()) {
            int k = 0;
            while (k < successors[v].length && references[v][k] >= 0) {
                int[] facet = facets.get(references[v][k]);
                int i = 0;
                while (i < facet.length && facet[i] != v) {
                    i++;
                }
                successors[v][k] = facet[(i + 1) % facet.length];
                for (int l = 0; l < k; l++) {
                    if (successors[v][l] == successors[v][k]) {
                        Vector3D start = vertices.get(v);
                        Vector3D end = vertices.get(successors[v][k]);
                        throw new MathIllegalArgumentException(LocalizedFormats.FACET_ORIENTATION_MISMATCH, Double.valueOf(start.getX()), Double.valueOf(start.getY()), Double.valueOf(start.getZ()), Double.valueOf(end.getX()), Double.valueOf(end.getY()), Double.valueOf(end.getZ()));
                    }
                }
                k++;
            }
            v++;
        }
        return successors;
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.threed.PolyhedronsSet' to match base method */
    @Override // org.apache.commons.math3.geometry.partitioning.AbstractRegion, org.apache.commons.math3.geometry.partitioning.AbstractRegion, org.apache.commons.math3.geometry.partitioning.Region
    public AbstractRegion<Euclidean3D, Euclidean2D> buildNew(BSPTree<Euclidean3D> tree) {
        return new PolyhedronsSet(tree, getTolerance());
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.geometry.partitioning.AbstractRegion
    public void computeGeometricalProperties() {
        getTree(true).visit(new FacetsContributionVisitor());
        if (getSize() < 0.0d) {
            setSize(Double.POSITIVE_INFINITY);
            setBarycenter((Point) Vector3D.NaN);
            return;
        }
        setSize(getSize() / 3.0d);
        setBarycenter((Point) new Vector3D(1.0d / (4.0d * getSize()), (Vector3D) getBarycenter()));
    }

    private class FacetsContributionVisitor implements BSPTreeVisitor<Euclidean3D> {
        FacetsContributionVisitor() {
            PolyhedronsSet.this.setSize(0.0d);
            PolyhedronsSet.this.setBarycenter((PolyhedronsSet) new Vector3D(0.0d, 0.0d, 0.0d));
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

        /* JADX WARN: Type inference failed for: r6v0, types: [org.apache.commons.math3.geometry.euclidean.threed.Vector3D] */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void addContribution(org.apache.commons.math3.geometry.partitioning.SubHyperplane<org.apache.commons.math3.geometry.euclidean.threed.Euclidean3D> r13, boolean r14) {
            /*
                r12 = this;
                r0 = r13
                org.apache.commons.math3.geometry.euclidean.threed.SubPlane r0 = (org.apache.commons.math3.geometry.euclidean.threed.SubPlane) r0
                org.apache.commons.math3.geometry.partitioning.Region r10 = r0.getRemainingRegion()
                double r8 = r10.getSize()
                boolean r0 = java.lang.Double.isInfinite(r8)
                if (r0 == 0) goto L_0x0020
                org.apache.commons.math3.geometry.euclidean.threed.PolyhedronsSet r0 = org.apache.commons.math3.geometry.euclidean.threed.PolyhedronsSet.this
                r2 = 9218868437227405312(0x7ff0000000000000, double:Infinity)
                org.apache.commons.math3.geometry.euclidean.threed.PolyhedronsSet.access$200(r0, r2)
                org.apache.commons.math3.geometry.euclidean.threed.PolyhedronsSet r0 = org.apache.commons.math3.geometry.euclidean.threed.PolyhedronsSet.this
                org.apache.commons.math3.geometry.euclidean.threed.Vector3D r1 = org.apache.commons.math3.geometry.euclidean.threed.Vector3D.NaN
                org.apache.commons.math3.geometry.euclidean.threed.PolyhedronsSet.access$300(r0, r1)
            L_0x001f:
                return
            L_0x0020:
                org.apache.commons.math3.geometry.partitioning.Hyperplane r7 = r13.getHyperplane()
                org.apache.commons.math3.geometry.euclidean.threed.Plane r7 = (org.apache.commons.math3.geometry.euclidean.threed.Plane) r7
                org.apache.commons.math3.geometry.Point r0 = r10.getBarycenter()
                org.apache.commons.math3.geometry.euclidean.threed.Vector3D r6 = r7.toSpace(r0)
                org.apache.commons.math3.geometry.euclidean.threed.Vector3D r0 = r7.getNormal()
                double r0 = r6.dotProduct(r0)
                double r4 = r8 * r0
                if (r14 == 0) goto L_0x003b
                double r4 = -r4
            L_0x003b:
                org.apache.commons.math3.geometry.euclidean.threed.PolyhedronsSet r0 = org.apache.commons.math3.geometry.euclidean.threed.PolyhedronsSet.this
                org.apache.commons.math3.geometry.euclidean.threed.PolyhedronsSet r1 = org.apache.commons.math3.geometry.euclidean.threed.PolyhedronsSet.this
                double r2 = r1.getSize()
                double r2 = r2 + r4
                org.apache.commons.math3.geometry.euclidean.threed.PolyhedronsSet.access$400(r0, r2)
                org.apache.commons.math3.geometry.euclidean.threed.PolyhedronsSet r11 = org.apache.commons.math3.geometry.euclidean.threed.PolyhedronsSet.this
                org.apache.commons.math3.geometry.euclidean.threed.Vector3D r0 = new org.apache.commons.math3.geometry.euclidean.threed.Vector3D
                r1 = 4607182418800017408(0x3ff0000000000000, double:1.0)
                org.apache.commons.math3.geometry.euclidean.threed.PolyhedronsSet r3 = org.apache.commons.math3.geometry.euclidean.threed.PolyhedronsSet.this
                org.apache.commons.math3.geometry.Point r3 = r3.getBarycenter()
                org.apache.commons.math3.geometry.euclidean.threed.Vector3D r3 = (org.apache.commons.math3.geometry.euclidean.threed.Vector3D) r3
                r0.<init>(r1, r3, r4, r6)
                org.apache.commons.math3.geometry.euclidean.threed.PolyhedronsSet.access$500(r11, r0)
                goto L_0x001f
            */
            throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.geometry.euclidean.threed.PolyhedronsSet.FacetsContributionVisitor.addContribution(org.apache.commons.math3.geometry.partitioning.SubHyperplane, boolean):void");
        }
    }

    public SubHyperplane<Euclidean3D> firstIntersection(Vector3D point, Line line) {
        return recurseFirstIntersection(getTree(true), point, line);
    }

    private SubHyperplane<Euclidean3D> recurseFirstIntersection(BSPTree<Euclidean3D> node, Vector3D point, Line line) {
        BSPTree<Euclidean3D> near;
        BSPTree<Euclidean3D> far;
        Vector3D hit3D;
        SubHyperplane<Euclidean3D> facet;
        SubHyperplane<Euclidean3D> facet2;
        SubHyperplane<Euclidean3D> cut = node.getCut();
        if (cut == null) {
            return null;
        }
        BSPTree<Euclidean3D> minus = node.getMinus();
        BSPTree<Euclidean3D> plus = node.getPlus();
        Plane plane = (Plane) cut.getHyperplane();
        double offset = plane.getOffset((Point<Euclidean3D>) point);
        boolean in = FastMath.abs(offset) < getTolerance();
        if (offset < 0.0d) {
            near = minus;
            far = plus;
        } else {
            near = plus;
            far = minus;
        }
        if (in && (facet2 = boundaryFacet(point, node)) != null) {
            return facet2;
        }
        SubHyperplane<Euclidean3D> crossed = recurseFirstIntersection(near, point, line);
        if (crossed != null) {
            return crossed;
        }
        return (in || (hit3D = plane.intersection(line)) == null || line.getAbscissa(hit3D) <= line.getAbscissa(point) || (facet = boundaryFacet(hit3D, node)) == null) ? recurseFirstIntersection(far, point, line) : facet;
    }

    private SubHyperplane<Euclidean3D> boundaryFacet(Vector3D point, BSPTree<Euclidean3D> node) {
        Point<Euclidean2D> subSpace = ((Plane) node.getCut().getHyperplane()).toSubSpace((Point<Euclidean3D>) point);
        BoundaryAttribute<Euclidean3D> attribute = (BoundaryAttribute) node.getAttribute();
        if (attribute.getPlusOutside() != null && ((SubPlane) attribute.getPlusOutside()).getRemainingRegion().checkPoint(subSpace) == Region.Location.INSIDE) {
            return attribute.getPlusOutside();
        }
        if (attribute.getPlusInside() == null || ((SubPlane) attribute.getPlusInside()).getRemainingRegion().checkPoint(subSpace) != Region.Location.INSIDE) {
            return null;
        }
        return attribute.getPlusInside();
    }

    public PolyhedronsSet rotate(Vector3D center, Rotation rotation) {
        return (PolyhedronsSet) applyTransform(new RotationTransform(center, rotation));
    }

    private static class RotationTransform implements Transform<Euclidean3D, Euclidean2D> {
        private Plane cachedOriginal;
        private Transform<Euclidean2D, Euclidean1D> cachedTransform;
        private Vector3D center;
        private Rotation rotation;

        RotationTransform(Vector3D center2, Rotation rotation2) {
            this.center = center2;
            this.rotation = rotation2;
        }

        /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.threed.Vector3D' to match base method */
        /* JADX DEBUG: Multi-variable search result rejected for r5v0, resolved type: org.apache.commons.math3.geometry.euclidean.threed.Rotation */
        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r8v0, types: [org.apache.commons.math3.geometry.euclidean.threed.Vector3D] */
        /* JADX WARNING: Unknown variable types count: 1 */
        @Override // org.apache.commons.math3.geometry.partitioning.Transform
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public org.apache.commons.math3.geometry.Point<org.apache.commons.math3.geometry.euclidean.threed.Euclidean3D> apply(org.apache.commons.math3.geometry.Point<org.apache.commons.math3.geometry.euclidean.threed.Euclidean3D> r10) {
            /*
                r9 = this;
                r2 = 4607182418800017408(0x3ff0000000000000, double:1.0)
                org.apache.commons.math3.geometry.euclidean.threed.Vector3D r10 = (org.apache.commons.math3.geometry.euclidean.threed.Vector3D) r10
                org.apache.commons.math3.geometry.euclidean.threed.Vector3D r1 = r9.center
                org.apache.commons.math3.geometry.euclidean.threed.Vector3D r8 = r10.subtract(r1)
                org.apache.commons.math3.geometry.euclidean.threed.Vector3D r1 = new org.apache.commons.math3.geometry.euclidean.threed.Vector3D
                org.apache.commons.math3.geometry.euclidean.threed.Vector3D r4 = r9.center
                org.apache.commons.math3.geometry.euclidean.threed.Rotation r5 = r9.rotation
                org.apache.commons.math3.geometry.euclidean.threed.Vector3D r7 = r5.applyTo(r8)
                r5 = r2
                r1.<init>(r2, r4, r5, r7)
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.geometry.euclidean.threed.PolyhedronsSet.RotationTransform.apply(org.apache.commons.math3.geometry.Point):org.apache.commons.math3.geometry.euclidean.threed.Vector3D");
        }

        /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.threed.Plane' to match base method */
        @Override // org.apache.commons.math3.geometry.partitioning.Transform
        public Hyperplane<Euclidean3D> apply(Hyperplane<Euclidean3D> hyperplane) {
            return ((Plane) hyperplane).rotate(this.center, this.rotation);
        }

        /* JADX WARN: Type inference failed for: r18v0, types: [org.apache.commons.math3.geometry.euclidean.twod.Vector2D] */
        /* JADX WARN: Type inference failed for: r20v0, types: [org.apache.commons.math3.geometry.euclidean.twod.Vector2D] */
        /* JADX WARN: Type inference failed for: r19v0, types: [org.apache.commons.math3.geometry.euclidean.twod.Vector2D] */
        /* JADX WARNING: Unknown variable types count: 3 */
        @Override // org.apache.commons.math3.geometry.partitioning.Transform
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public org.apache.commons.math3.geometry.partitioning.SubHyperplane<org.apache.commons.math3.geometry.euclidean.twod.Euclidean2D> apply(org.apache.commons.math3.geometry.partitioning.SubHyperplane<org.apache.commons.math3.geometry.euclidean.twod.Euclidean2D> r23, org.apache.commons.math3.geometry.partitioning.Hyperplane<org.apache.commons.math3.geometry.euclidean.threed.Euclidean3D> r24, org.apache.commons.math3.geometry.partitioning.Hyperplane<org.apache.commons.math3.geometry.euclidean.threed.Euclidean3D> r25) {
            /*
            // Method dump skipped, instructions count: 159
            */
            throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.geometry.euclidean.threed.PolyhedronsSet.RotationTransform.apply(org.apache.commons.math3.geometry.partitioning.SubHyperplane, org.apache.commons.math3.geometry.partitioning.Hyperplane, org.apache.commons.math3.geometry.partitioning.Hyperplane):org.apache.commons.math3.geometry.partitioning.SubHyperplane");
        }
    }

    public PolyhedronsSet translate(Vector3D translation) {
        return (PolyhedronsSet) applyTransform(new TranslationTransform(translation));
    }

    private static class TranslationTransform implements Transform<Euclidean3D, Euclidean2D> {
        private Plane cachedOriginal;
        private Transform<Euclidean2D, Euclidean1D> cachedTransform;
        private Vector3D translation;

        TranslationTransform(Vector3D translation2) {
            this.translation = translation2;
        }

        /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.threed.Vector3D' to match base method */
        @Override // org.apache.commons.math3.geometry.partitioning.Transform
        public Point<Euclidean3D> apply(Point<Euclidean3D> point) {
            return new Vector3D(1.0d, (Vector3D) point, 1.0d, this.translation);
        }

        /* Return type fixed from 'org.apache.commons.math3.geometry.euclidean.threed.Plane' to match base method */
        @Override // org.apache.commons.math3.geometry.partitioning.Transform
        public Hyperplane<Euclidean3D> apply(Hyperplane<Euclidean3D> hyperplane) {
            return ((Plane) hyperplane).translate(this.translation);
        }

        /* JADX WARN: Type inference failed for: r15v0, types: [org.apache.commons.math3.geometry.euclidean.twod.Vector2D] */
        /* JADX WARNING: Unknown variable types count: 1 */
        @Override // org.apache.commons.math3.geometry.partitioning.Transform
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public org.apache.commons.math3.geometry.partitioning.SubHyperplane<org.apache.commons.math3.geometry.euclidean.twod.Euclidean2D> apply(org.apache.commons.math3.geometry.partitioning.SubHyperplane<org.apache.commons.math3.geometry.euclidean.twod.Euclidean2D> r18, org.apache.commons.math3.geometry.partitioning.Hyperplane<org.apache.commons.math3.geometry.euclidean.threed.Euclidean3D> r19, org.apache.commons.math3.geometry.partitioning.Hyperplane<org.apache.commons.math3.geometry.euclidean.threed.Euclidean3D> r20) {
            /*
                r17 = this;
                r0 = r17
                org.apache.commons.math3.geometry.euclidean.threed.Plane r2 = r0.cachedOriginal
                r0 = r19
                if (r0 == r2) goto L_0x0040
                r14 = r19
                org.apache.commons.math3.geometry.euclidean.threed.Plane r14 = (org.apache.commons.math3.geometry.euclidean.threed.Plane) r14
                r16 = r20
                org.apache.commons.math3.geometry.euclidean.threed.Plane r16 = (org.apache.commons.math3.geometry.euclidean.threed.Plane) r16
                org.apache.commons.math3.geometry.euclidean.threed.Vector3D r2 = r14.getOrigin()
                r0 = r17
                org.apache.commons.math3.geometry.euclidean.threed.Vector3D r2 = r0.apply(r2)
                r0 = r16
                org.apache.commons.math3.geometry.euclidean.twod.Vector2D r15 = r0.toSubSpace(r2)
                org.apache.commons.math3.geometry.euclidean.threed.Plane r19 = (org.apache.commons.math3.geometry.euclidean.threed.Plane) r19
                r0 = r19
                r1 = r17
                r1.cachedOriginal = r0
                r2 = 4607182418800017408(0x3ff0000000000000, double:1.0)
                r4 = 0
                r6 = 0
                r8 = 4607182418800017408(0x3ff0000000000000, double:1.0)
                double r10 = r15.getX()
                double r12 = r15.getY()
                org.apache.commons.math3.geometry.partitioning.Transform r2 = org.apache.commons.math3.geometry.euclidean.twod.Line.getTransform(r2, r4, r6, r8, r10, r12)
                r0 = r17
                r0.cachedTransform = r2
            L_0x0040:
                org.apache.commons.math3.geometry.euclidean.twod.SubLine r18 = (org.apache.commons.math3.geometry.euclidean.twod.SubLine) r18
                r0 = r17
                org.apache.commons.math3.geometry.partitioning.Transform<org.apache.commons.math3.geometry.euclidean.twod.Euclidean2D, org.apache.commons.math3.geometry.euclidean.oned.Euclidean1D> r2 = r0.cachedTransform
                r0 = r18
                org.apache.commons.math3.geometry.partitioning.AbstractSubHyperplane r2 = r0.applyTransform(r2)
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.geometry.euclidean.threed.PolyhedronsSet.TranslationTransform.apply(org.apache.commons.math3.geometry.partitioning.SubHyperplane, org.apache.commons.math3.geometry.partitioning.Hyperplane, org.apache.commons.math3.geometry.partitioning.Hyperplane):org.apache.commons.math3.geometry.partitioning.SubHyperplane");
        }
    }
}
