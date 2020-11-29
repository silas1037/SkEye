package org.apache.commons.math3.geometry.spherical.twod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.geometry.enclosing.EnclosingBall;
import org.apache.commons.math3.geometry.enclosing.WelzlEncloser;
import org.apache.commons.math3.geometry.euclidean.threed.Euclidean3D;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationConvention;
import org.apache.commons.math3.geometry.euclidean.threed.SphereGenerator;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.partitioning.AbstractRegion;
import org.apache.commons.math3.geometry.partitioning.BSPTree;
import org.apache.commons.math3.geometry.partitioning.BoundaryProjection;
import org.apache.commons.math3.geometry.partitioning.RegionFactory;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;
import org.apache.commons.math3.geometry.spherical.oned.Sphere1D;
import org.apache.commons.math3.util.FastMath;

public class SphericalPolygonsSet extends AbstractRegion<Sphere2D, Sphere1D> {
    private List<Vertex> loops;

    public SphericalPolygonsSet(double tolerance) {
        super(tolerance);
    }

    public SphericalPolygonsSet(Vector3D pole, double tolerance) {
        super(new BSPTree(new Circle(pole, tolerance).wholeHyperplane(), new BSPTree(Boolean.FALSE), new BSPTree(Boolean.TRUE), null), tolerance);
    }

    public SphericalPolygonsSet(Vector3D center, Vector3D meridian, double outsideRadius, int n, double tolerance) {
        this(tolerance, createRegularPolygonVertices(center, meridian, outsideRadius, n));
    }

    public SphericalPolygonsSet(BSPTree<Sphere2D> tree, double tolerance) {
        super(tree, tolerance);
    }

    public SphericalPolygonsSet(Collection<SubHyperplane<Sphere2D>> boundary, double tolerance) {
        super(boundary, tolerance);
    }

    public SphericalPolygonsSet(double hyperplaneThickness, S2Point... vertices) {
        super(verticesToTree(hyperplaneThickness, vertices), hyperplaneThickness);
    }

    private static S2Point[] createRegularPolygonVertices(Vector3D center, Vector3D meridian, double outsideRadius, int n) {
        S2Point[] array = new S2Point[n];
        array[0] = new S2Point(new Rotation(Vector3D.crossProduct(center, meridian), outsideRadius, RotationConvention.VECTOR_OPERATOR).applyTo(center));
        Rotation r = new Rotation(center, 6.283185307179586d / ((double) n), RotationConvention.VECTOR_OPERATOR);
        for (int i = 1; i < n; i++) {
            array[i] = new S2Point(r.applyTo(array[i - 1].getVector()));
        }
        return array;
    }

    private static BSPTree<Sphere2D> verticesToTree(double hyperplaneThickness, S2Point... vertices) {
        int n = vertices.length;
        if (n == 0) {
            return new BSPTree<>(Boolean.TRUE);
        }
        Vertex[] vArray = new Vertex[n];
        for (int i = 0; i < n; i++) {
            vArray[i] = new Vertex(vertices[i]);
        }
        List<Edge> edges = new ArrayList<>(n);
        Vertex end = vArray[n - 1];
        for (int i2 = 0; i2 < n; i2++) {
            end = vArray[i2];
            Circle circle = end.sharedCircleWith(end);
            if (circle == null) {
                circle = new Circle(end.getLocation(), end.getLocation(), hyperplaneThickness);
            }
            edges.add(new Edge(end, end, Vector3D.angle(end.getLocation().getVector(), end.getLocation().getVector()), circle));
            for (Vertex vertex : vArray) {
                if (!(vertex == end || vertex == end || FastMath.abs(circle.getOffset(vertex.getLocation())) > hyperplaneThickness)) {
                    vertex.bindWith(circle);
                }
            }
        }
        BSPTree<Sphere2D> tree = new BSPTree<>();
        insertEdges(hyperplaneThickness, tree, edges);
        return tree;
    }

    private static void insertEdges(double hyperplaneThickness, BSPTree<Sphere2D> node, List<Edge> edges) {
        Edge inserted = null;
        int index = 0;
        while (inserted == null && index < edges.size()) {
            int index2 = index + 1;
            inserted = edges.get(index);
            if (!node.insertCut(inserted.getCircle())) {
                inserted = null;
                index = index2;
            } else {
                index = index2;
            }
        }
        if (inserted == null) {
            BSPTree<Sphere2D> parent = node.getParent();
            if (parent == null || node == parent.getMinus()) {
                node.setAttribute(Boolean.TRUE);
            } else {
                node.setAttribute(Boolean.FALSE);
            }
        } else {
            List<Edge> outsideList = new ArrayList<>();
            List<Edge> insideList = new ArrayList<>();
            for (Edge edge : edges) {
                if (edge != inserted) {
                    edge.split(inserted.getCircle(), outsideList, insideList);
                }
            }
            if (!outsideList.isEmpty()) {
                insertEdges(hyperplaneThickness, node.getPlus(), outsideList);
            } else {
                node.getPlus().setAttribute(Boolean.FALSE);
            }
            if (!insideList.isEmpty()) {
                insertEdges(hyperplaneThickness, node.getMinus(), insideList);
            } else {
                node.getMinus().setAttribute(Boolean.TRUE);
            }
        }
    }

    /* Return type fixed from 'org.apache.commons.math3.geometry.spherical.twod.SphericalPolygonsSet' to match base method */
    @Override // org.apache.commons.math3.geometry.partitioning.AbstractRegion, org.apache.commons.math3.geometry.partitioning.AbstractRegion, org.apache.commons.math3.geometry.partitioning.Region
    public AbstractRegion<Sphere2D, Sphere1D> buildNew(BSPTree<Sphere2D> tree) {
        return new SphericalPolygonsSet(tree, getTolerance());
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.geometry.partitioning.AbstractRegion
    public void computeGeometricalProperties() throws MathIllegalStateException {
        BSPTree<Sphere2D> tree = getTree(true);
        if (tree.getCut() != null) {
            PropertiesComputer pc = new PropertiesComputer(getTolerance());
            tree.visit(pc);
            setSize(pc.getArea());
            setBarycenter(pc.getBarycenter());
        } else if (tree.getCut() != null || !((Boolean) tree.getAttribute()).booleanValue()) {
            setSize(0.0d);
            setBarycenter(S2Point.NaN);
        } else {
            setSize(12.566370614359172d);
            setBarycenter(new S2Point(0.0d, 0.0d));
        }
    }

    public List<Vertex> getBoundaryLoops() throws MathIllegalStateException {
        if (this.loops == null) {
            if (getTree(false).getCut() == null) {
                this.loops = Collections.emptyList();
            } else {
                BSPTree<Sphere2D> root = getTree(true);
                EdgesBuilder visitor = new EdgesBuilder(root, getTolerance());
                root.visit(visitor);
                List<Edge> edges = visitor.getEdges();
                this.loops = new ArrayList();
                while (!edges.isEmpty()) {
                    Edge edge = edges.get(0);
                    Vertex startVertex = edge.getStart();
                    this.loops.add(startVertex);
                    do {
                        Iterator<Edge> iterator = edges.iterator();
                        while (true) {
                            if (iterator.hasNext()) {
                                if (iterator.next() == edge) {
                                    iterator.remove();
                                    break;
                                }
                            } else {
                                break;
                            }
                        }
                        edge = edge.getEnd().getOutgoing();
                    } while (edge.getStart() != startVertex);
                }
            }
        }
        return Collections.unmodifiableList(this.loops);
    }

    public EnclosingBall<Sphere2D, S2Point> getEnclosingCap() {
        if (isEmpty()) {
            return new EnclosingBall<>(S2Point.PLUS_K, Double.NEGATIVE_INFINITY, new S2Point[0]);
        }
        if (isFull()) {
            return new EnclosingBall<>(S2Point.PLUS_K, Double.POSITIVE_INFINITY, new S2Point[0]);
        }
        BSPTree<Sphere2D> root = getTree(false);
        if (isEmpty(root.getMinus()) && isFull(root.getPlus())) {
            return new EnclosingBall<>(new S2Point(((Circle) root.getCut().getHyperplane()).getPole()).negate(), 1.5707963267948966d, new S2Point[0]);
        }
        if (isFull(root.getMinus()) && isEmpty(root.getPlus())) {
            return new EnclosingBall<>(new S2Point(((Circle) root.getCut().getHyperplane()).getPole()), 1.5707963267948966d, new S2Point[0]);
        }
        List<Vector3D> points = getInsidePoints();
        for (Vertex loopStart : getBoundaryLoops()) {
            int count = 0;
            Vertex v = loopStart;
            while (true) {
                if (count == 0 || v != loopStart) {
                    count++;
                    points.add(v.getLocation().getVector());
                    v = v.getOutgoing().getEnd();
                }
            }
        }
        EnclosingBall<Euclidean3D, Vector3D> enclosing3D = new WelzlEncloser<>(getTolerance(), new SphereGenerator()).enclose(points);
        Vector3D[] support3D = enclosing3D.getSupport();
        double r = enclosing3D.getRadius();
        double h = enclosing3D.getCenter().getNorm();
        if (h < getTolerance()) {
            EnclosingBall<Sphere2D, S2Point> enclosingS2 = new EnclosingBall<>(S2Point.PLUS_K, Double.POSITIVE_INFINITY, new S2Point[0]);
            for (Vector3D outsidePoint : getOutsidePoints()) {
                S2Point outsideS2 = new S2Point(outsidePoint);
                BoundaryProjection<Sphere2D> projection = projectToBoundary(outsideS2);
                if (3.141592653589793d - projection.getOffset() < enclosingS2.getRadius()) {
                    enclosingS2 = new EnclosingBall<>(outsideS2.negate(), 3.141592653589793d - projection.getOffset(), (S2Point) projection.getProjected());
                }
            }
            return enclosingS2;
        }
        S2Point[] support = new S2Point[support3D.length];
        for (int i = 0; i < support3D.length; i++) {
            support[i] = new S2Point(support3D[i]);
        }
        return new EnclosingBall<>(new S2Point(enclosing3D.getCenter()), FastMath.acos(((1.0d + (h * h)) - (r * r)) / (2.0d * h)), support);
    }

    private List<Vector3D> getInsidePoints() {
        PropertiesComputer pc = new PropertiesComputer(getTolerance());
        getTree(true).visit(pc);
        return pc.getConvexCellsInsidePoints();
    }

    private List<Vector3D> getOutsidePoints() {
        PropertiesComputer pc = new PropertiesComputer(getTolerance());
        ((SphericalPolygonsSet) new RegionFactory().getComplement(this)).getTree(true).visit(pc);
        return pc.getConvexCellsInsidePoints();
    }
}
