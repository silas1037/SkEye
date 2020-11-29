package org.apache.commons.math3.geometry.spherical.twod;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.partitioning.BSPTree;
import org.apache.commons.math3.geometry.partitioning.BSPTreeVisitor;
import org.apache.commons.math3.util.FastMath;

/* access modifiers changed from: package-private */
public class PropertiesComputer implements BSPTreeVisitor<Sphere2D> {
    private final List<Vector3D> convexCellsInsidePoints = new ArrayList();
    private double summedArea = 0.0d;
    private Vector3D summedBarycenter = Vector3D.ZERO;
    private final double tolerance;

    PropertiesComputer(double tolerance2) {
        this.tolerance = tolerance2;
    }

    @Override // org.apache.commons.math3.geometry.partitioning.BSPTreeVisitor
    public BSPTreeVisitor.Order visitOrder(BSPTree<Sphere2D> bSPTree) {
        return BSPTreeVisitor.Order.MINUS_SUB_PLUS;
    }

    @Override // org.apache.commons.math3.geometry.partitioning.BSPTreeVisitor
    public void visitInternalNode(BSPTree<Sphere2D> bSPTree) {
    }

    @Override // org.apache.commons.math3.geometry.partitioning.BSPTreeVisitor
    public void visitLeafNode(BSPTree<Sphere2D> node) {
        if (((Boolean) node.getAttribute()).booleanValue()) {
            List<Vertex> boundary = new SphericalPolygonsSet(node.pruneAroundConvexCell(Boolean.TRUE, Boolean.FALSE, null), this.tolerance).getBoundaryLoops();
            if (boundary.size() != 1) {
                throw new MathInternalError();
            }
            double area = convexCellArea(boundary.get(0));
            Vector3D barycenter = convexCellBarycenter(boundary.get(0));
            this.convexCellsInsidePoints.add(barycenter);
            this.summedArea += area;
            this.summedBarycenter = new Vector3D(1.0d, this.summedBarycenter, area, barycenter);
        }
    }

    private double convexCellArea(Vertex start) {
        int n = 0;
        double sum = 0.0d;
        Edge e = start.getOutgoing();
        while (true) {
            if (n != 0 && e.getStart() == start) {
                return sum - (((double) (n - 2)) * 3.141592653589793d);
            }
            Vector3D previousPole = e.getCircle().getPole();
            Vector3D nextPole = e.getEnd().getOutgoing().getCircle().getPole();
            double alpha = FastMath.atan2(Vector3D.dotProduct(nextPole, Vector3D.crossProduct(e.getEnd().getLocation().getVector(), previousPole)), -Vector3D.dotProduct(nextPole, previousPole));
            if (alpha < 0.0d) {
                alpha += 6.283185307179586d;
            }
            sum += alpha;
            n++;
            e = e.getEnd().getOutgoing();
        }
    }

    /* JADX WARN: Type inference failed for: r1v3, types: [org.apache.commons.math3.geometry.euclidean.threed.Vector3D] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private org.apache.commons.math3.geometry.euclidean.threed.Vector3D convexCellBarycenter(org.apache.commons.math3.geometry.spherical.twod.Vertex r10) {
        /*
            r9 = this;
            r8 = 0
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r0 = org.apache.commons.math3.geometry.euclidean.threed.Vector3D.ZERO
            org.apache.commons.math3.geometry.spherical.twod.Edge r7 = r10.getOutgoing()
            r3 = r0
        L_0x0008:
            if (r8 == 0) goto L_0x0010
            org.apache.commons.math3.geometry.spherical.twod.Vertex r1 = r7.getStart()
            if (r1 == r10) goto L_0x002f
        L_0x0010:
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r0 = new org.apache.commons.math3.geometry.euclidean.threed.Vector3D
            r1 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            double r4 = r7.getLength()
            org.apache.commons.math3.geometry.spherical.twod.Circle r6 = r7.getCircle()
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r6 = r6.getPole()
            r0.<init>(r1, r3, r4, r6)
            int r8 = r8 + 1
            org.apache.commons.math3.geometry.spherical.twod.Vertex r1 = r7.getEnd()
            org.apache.commons.math3.geometry.spherical.twod.Edge r7 = r1.getOutgoing()
            r3 = r0
            goto L_0x0008
        L_0x002f:
            org.apache.commons.math3.geometry.euclidean.threed.Vector3D r1 = r3.normalize()
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.geometry.spherical.twod.PropertiesComputer.convexCellBarycenter(org.apache.commons.math3.geometry.spherical.twod.Vertex):org.apache.commons.math3.geometry.euclidean.threed.Vector3D");
    }

    public double getArea() {
        return this.summedArea;
    }

    public S2Point getBarycenter() {
        if (this.summedBarycenter.getNormSq() == 0.0d) {
            return S2Point.NaN;
        }
        return new S2Point(this.summedBarycenter);
    }

    public List<Vector3D> getConvexCellsInsidePoints() {
        return this.convexCellsInsidePoints;
    }
}
