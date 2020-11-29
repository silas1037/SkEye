package com.lavadip.skeye.fit;

import com.lavadip.skeye.Vector3d;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;

public class FitPoints {
    public RealVector center;
    public double[] evals;
    public RealVector evecs;
    public RealVector evecs1;
    public RealVector evecs2;
    public RealVector radii;
    private Array2DRowRealMatrix transformMatrix;

    public void fitEllipsoid(List<Vector3d> points) {
        RealMatrix a = formAlgebraicMatrix(solveSystem(points));
        this.center = findCenter(a);
        RealMatrix r = translateToCenter(this.center, a);
        RealMatrix subr = r.getSubMatrix(0, 2, 0, 2);
        double divr = -r.getEntry(3, 3);
        for (int i = 0; i < subr.getRowDimension(); i++) {
            for (int j = 0; j < subr.getRowDimension(); j++) {
                subr.setEntry(i, j, subr.getEntry(i, j) / divr);
            }
        }
        EigenDecomposition ed = new EigenDecomposition(subr);
        this.evals = ed.getRealEigenvalues();
        this.evecs = ed.getEigenvector(0);
        this.evecs1 = ed.getEigenvector(1);
        this.evecs2 = ed.getEigenvector(2);
        this.radii = findRadii(this.evals);
        if (this.radii != null) {
            setupTransformMatrix();
        }
    }

    private void setupTransformMatrix() {
        prepateTransformMatrix(new RealVector[]{this.evecs, this.evecs1, this.evecs2}, this.radii);
    }

    private void setupTransformMatrixOrdered() {
        int[] ordering = new int[3];
        RealVector[] vecs = {this.evecs, this.evecs1, this.evecs2};
        for (int o = 0; o < 3; o++) {
            ordering[o] = 0;
            for (int i = 1; i < 3; i++) {
                if (Math.abs(vecs[i].getEntry(o)) > Math.abs(vecs[ordering[o]].getEntry(o))) {
                    ordering[o] = i;
                }
            }
        }
        RealVector[] orderedVecs = new RealVector[3];
        RealVector orderedRadii = new ArrayRealVector(3);
        for (int i2 = 0; i2 < 3; i2++) {
            orderedVecs[i2] = vecs[ordering[i2]];
            orderedRadii.setEntry(i2, this.radii.getEntry(ordering[i2]));
        }
        prepateTransformMatrix(orderedVecs, orderedRadii);
    }

    private void prepateTransformMatrix(RealVector[] vecs, RealVector radii2) {
        this.transformMatrix = new Array2DRowRealMatrix(3, 3);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.transformMatrix.setEntry(i, j, 0.0d);
                for (int k = 0; k < 3; k++) {
                    this.transformMatrix.addToEntry(i, j, (vecs[k].getEntry(i) * vecs[k].getEntry(j)) / radii2.getEntry(k));
                }
            }
        }
    }

    private static RealVector solveSystem(List<Vector3d> points) {
        int numPoints = points.size();
        RealMatrix d = new Array2DRowRealMatrix(numPoints, 9);
        for (int i = 0; i < d.getRowDimension(); i++) {
            double xx = Math.pow(points.get(i).f16x, 2.0d);
            double yy = Math.pow(points.get(i).f17y, 2.0d);
            double zz = Math.pow(points.get(i).f18z, 2.0d);
            double xy = 2.0d * points.get(i).f16x * points.get(i).f17y;
            double xz = 2.0d * points.get(i).f16x * points.get(i).f18z;
            double x = 2.0d * points.get(i).f16x;
            double y = 2.0d * points.get(i).f17y;
            d.setEntry(i, 0, xx);
            d.setEntry(i, 1, yy);
            d.setEntry(i, 2, zz);
            d.setEntry(i, 3, xy);
            d.setEntry(i, 4, xz);
            d.setEntry(i, 5, 2.0d * points.get(i).f17y * points.get(i).f18z);
            d.setEntry(i, 6, x);
            d.setEntry(i, 7, y);
            d.setEntry(i, 8, 2.0d * points.get(i).f18z);
        }
        RealMatrix dtd = d.transpose().multiply(d);
        RealVector ones = new ArrayRealVector(numPoints);
        ones.mapAddToSelf(1.0d);
        return new SingularValueDecomposition(dtd).getSolver().getInverse().operate(d.transpose().operate(ones));
    }

    private static RealMatrix formAlgebraicMatrix(RealVector v) {
        RealMatrix a = new Array2DRowRealMatrix(4, 4);
        a.setEntry(0, 0, v.getEntry(0));
        a.setEntry(0, 1, v.getEntry(3));
        a.setEntry(0, 2, v.getEntry(4));
        a.setEntry(0, 3, v.getEntry(6));
        a.setEntry(1, 0, v.getEntry(3));
        a.setEntry(1, 1, v.getEntry(1));
        a.setEntry(1, 2, v.getEntry(5));
        a.setEntry(1, 3, v.getEntry(7));
        a.setEntry(2, 0, v.getEntry(4));
        a.setEntry(2, 1, v.getEntry(5));
        a.setEntry(2, 2, v.getEntry(2));
        a.setEntry(2, 3, v.getEntry(8));
        a.setEntry(3, 0, v.getEntry(6));
        a.setEntry(3, 1, v.getEntry(7));
        a.setEntry(3, 2, v.getEntry(8));
        a.setEntry(3, 3, -1.0d);
        return a;
    }

    private static RealVector findCenter(RealMatrix a) {
        RealMatrix subA = a.getSubMatrix(0, 2, 0, 2);
        for (int q = 0; q < subA.getRowDimension(); q++) {
            for (int s = 0; s < subA.getColumnDimension(); s++) {
                subA.multiplyEntry(q, s, -1.0d);
            }
        }
        return new SingularValueDecomposition(subA).getSolver().getInverse().operate(a.getRowVector(3).getSubVector(0, 3));
    }

    private static RealMatrix translateToCenter(RealVector center2, RealMatrix a) {
        RealMatrix t = MatrixUtils.createRealIdentityMatrix(4);
        RealMatrix centerMatrix = new Array2DRowRealMatrix(1, 3);
        centerMatrix.setRowVector(0, center2);
        t.setSubMatrix(centerMatrix.getData(), 3, 0);
        return t.multiply(a).multiply(t.transpose());
    }

    private static RealVector findRadii(double[] evals2) {
        RealVector radii2 = new ArrayRealVector(evals2.length);
        for (int i = 0; i < evals2.length; i++) {
            if (Double.isNaN(evals2[i]) || evals2[i] < 0.0d) {
                return null;
            }
            radii2.setEntry(i, Math.sqrt(1.0d / evals2[i]));
        }
        return radii2;
    }

    private void printLog() {
        System.out.println("");
        System.out.println(Arrays.toString(this.evals));
        System.out.println(this.evecs.toString());
        System.out.println(this.evecs1.toString());
        System.out.println(this.evecs2.toString());
        System.out.print("Center: " + this.center.toString());
        System.out.print(" Radii: " + this.radii.toString());
    }

    public Vector3d correctSimple(Vector3d m) {
        return new Vector3d((m.f16x - this.center.getEntry(0)) / this.radii.getEntry(0), (m.f17y - this.center.getEntry(1)) / this.radii.getEntry(1), (m.f18z - this.center.getEntry(2)) / this.radii.getEntry(2));
    }

    public Vector3d correct(Vector3d m) {
        if (this.radii == null) {
            return null;
        }
        double a = m.f16x - this.center.getEntry(0);
        double b = m.f17y - this.center.getEntry(1);
        RealVector v = new ArrayRealVector(3);
        v.setEntry(0, a);
        v.setEntry(1, b);
        v.setEntry(2, m.f18z - this.center.getEntry(2));
        RealVector tv = this.transformMatrix.preMultiply(v);
        return new Vector3d(tv.getEntry(0), tv.getEntry(1), tv.getEntry(2));
    }

    public List<Vector3d> correctAll(List<Vector3d> points) {
        ArrayList<Vector3d> correctedPoints = new ArrayList<>();
        for (Vector3d p : points) {
            correctedPoints.add(correct(p));
        }
        return correctedPoints;
    }

    public String serializeToString() {
        StringBuilder result = new StringBuilder();
        result.append(serializeRealVectoString(this.center));
        result.append(',');
        result.append(serializeRealVectoString(this.radii));
        result.append(',');
        result.append(serializeRealVectoString(this.evecs));
        result.append(',');
        result.append(serializeRealVectoString(this.evecs1));
        result.append(',');
        result.append(serializeRealVectoString(this.evecs2));
        return result.toString();
    }

    private static Object serializeRealVectoString(RealVector vec) {
        return vec.getEntry(0) + "," + vec.getEntry(1) + "," + vec.getEntry(2);
    }

    public void initFromString(StringTokenizer tokenizer) {
        this.center = readRealVecFromTokeniser(tokenizer);
        this.radii = readRealVecFromTokeniser(tokenizer);
        this.evecs = readRealVecFromTokeniser(tokenizer);
        this.evecs1 = readRealVecFromTokeniser(tokenizer);
        this.evecs2 = readRealVecFromTokeniser(tokenizer);
        setupTransformMatrix();
    }

    private static RealVector readRealVecFromTokeniser(StringTokenizer tokeniser) {
        ArrayRealVector av = new ArrayRealVector(3);
        av.setEntry(0, Double.parseDouble(tokeniser.nextToken()));
        av.setEntry(1, Double.parseDouble(tokeniser.nextToken()));
        av.setEntry(2, Double.parseDouble(tokeniser.nextToken()));
        return av;
    }
}
