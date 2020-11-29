package org.apache.commons.math3.linear;

import java.lang.reflect.Array;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.MathUnsupportedOperationException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Precision;

public class EigenDecomposition {
    private static final double EPSILON = 1.0E-12d;
    private RealMatrix cachedD;
    private RealMatrix cachedV;
    private RealMatrix cachedVt;
    private ArrayRealVector[] eigenvectors;
    private double[] imagEigenvalues;
    private final boolean isSymmetric;
    private double[] main;
    private byte maxIter;
    private double[] realEigenvalues;
    private double[] secondary;
    private TriDiagonalTransformer transformer;

    public EigenDecomposition(RealMatrix matrix) throws MathArithmeticException {
        this.maxIter = 30;
        this.isSymmetric = MatrixUtils.isSymmetric(matrix, ((double) (matrix.getRowDimension() * 10 * matrix.getColumnDimension())) * Precision.EPSILON);
        if (this.isSymmetric) {
            transformToTridiagonal(matrix);
            findEigenVectors(this.transformer.getQ().getData());
            return;
        }
        findEigenVectorsFromSchur(transformToSchur(matrix));
    }

    @Deprecated
    public EigenDecomposition(RealMatrix matrix, double splitTolerance) throws MathArithmeticException {
        this(matrix);
    }

    public EigenDecomposition(double[] main2, double[] secondary2) {
        this.maxIter = 30;
        this.isSymmetric = true;
        this.main = (double[]) main2.clone();
        this.secondary = (double[]) secondary2.clone();
        this.transformer = null;
        int size = main2.length;
        double[][] z = (double[][]) Array.newInstance(Double.TYPE, size, size);
        for (int i = 0; i < size; i++) {
            z[i][i] = 1.0d;
        }
        findEigenVectors(z);
    }

    @Deprecated
    public EigenDecomposition(double[] main2, double[] secondary2, double splitTolerance) {
        this(main2, secondary2);
    }

    public RealMatrix getV() {
        if (this.cachedV == null) {
            int m = this.eigenvectors.length;
            this.cachedV = MatrixUtils.createRealMatrix(m, m);
            for (int k = 0; k < m; k++) {
                this.cachedV.setColumnVector(k, this.eigenvectors[k]);
            }
        }
        return this.cachedV;
    }

    public RealMatrix getD() {
        if (this.cachedD == null) {
            this.cachedD = MatrixUtils.createRealDiagonalMatrix(this.realEigenvalues);
            for (int i = 0; i < this.imagEigenvalues.length; i++) {
                if (Precision.compareTo(this.imagEigenvalues[i], 0.0d, 1.0E-12d) > 0) {
                    this.cachedD.setEntry(i, i + 1, this.imagEigenvalues[i]);
                } else if (Precision.compareTo(this.imagEigenvalues[i], 0.0d, 1.0E-12d) < 0) {
                    this.cachedD.setEntry(i, i - 1, this.imagEigenvalues[i]);
                }
            }
        }
        return this.cachedD;
    }

    public RealMatrix getVT() {
        if (this.cachedVt == null) {
            int m = this.eigenvectors.length;
            this.cachedVt = MatrixUtils.createRealMatrix(m, m);
            for (int k = 0; k < m; k++) {
                this.cachedVt.setRowVector(k, this.eigenvectors[k]);
            }
        }
        return this.cachedVt;
    }

    public boolean hasComplexEigenvalues() {
        for (int i = 0; i < this.imagEigenvalues.length; i++) {
            if (!Precision.equals(this.imagEigenvalues[i], 0.0d, 1.0E-12d)) {
                return true;
            }
        }
        return false;
    }

    public double[] getRealEigenvalues() {
        return (double[]) this.realEigenvalues.clone();
    }

    public double getRealEigenvalue(int i) {
        return this.realEigenvalues[i];
    }

    public double[] getImagEigenvalues() {
        return (double[]) this.imagEigenvalues.clone();
    }

    public double getImagEigenvalue(int i) {
        return this.imagEigenvalues[i];
    }

    public RealVector getEigenvector(int i) {
        return this.eigenvectors[i].copy();
    }

    public double getDeterminant() {
        double determinant = 1.0d;
        for (double lambda : this.realEigenvalues) {
            determinant *= lambda;
        }
        return determinant;
    }

    public RealMatrix getSquareRoot() {
        if (!this.isSymmetric) {
            throw new MathUnsupportedOperationException();
        }
        double[] sqrtEigenValues = new double[this.realEigenvalues.length];
        for (int i = 0; i < this.realEigenvalues.length; i++) {
            double eigen = this.realEigenvalues[i];
            if (eigen <= 0.0d) {
                throw new MathUnsupportedOperationException();
            }
            sqrtEigenValues[i] = FastMath.sqrt(eigen);
        }
        RealMatrix sqrtEigen = MatrixUtils.createRealDiagonalMatrix(sqrtEigenValues);
        RealMatrix v = getV();
        return v.multiply(sqrtEigen).multiply(getVT());
    }

    public DecompositionSolver getSolver() {
        if (!hasComplexEigenvalues()) {
            return new Solver(this.realEigenvalues, this.imagEigenvalues, this.eigenvectors);
        }
        throw new MathUnsupportedOperationException();
    }

    private static class Solver implements DecompositionSolver {
        private final ArrayRealVector[] eigenvectors;
        private double[] imagEigenvalues;
        private double[] realEigenvalues;

        private Solver(double[] realEigenvalues2, double[] imagEigenvalues2, ArrayRealVector[] eigenvectors2) {
            this.realEigenvalues = realEigenvalues2;
            this.imagEigenvalues = imagEigenvalues2;
            this.eigenvectors = eigenvectors2;
        }

        @Override // org.apache.commons.math3.linear.DecompositionSolver
        public RealVector solve(RealVector b) {
            if (!isNonSingular()) {
                throw new SingularMatrixException();
            }
            int m = this.realEigenvalues.length;
            if (b.getDimension() != m) {
                throw new DimensionMismatchException(b.getDimension(), m);
            }
            double[] bp = new double[m];
            for (int i = 0; i < m; i++) {
                ArrayRealVector v = this.eigenvectors[i];
                double[] vData = v.getDataRef();
                double s = v.dotProduct(b) / this.realEigenvalues[i];
                for (int j = 0; j < m; j++) {
                    bp[j] = bp[j] + (vData[j] * s);
                }
            }
            return new ArrayRealVector(bp, false);
        }

        @Override // org.apache.commons.math3.linear.DecompositionSolver
        public RealMatrix solve(RealMatrix b) {
            if (!isNonSingular()) {
                throw new SingularMatrixException();
            }
            int m = this.realEigenvalues.length;
            if (b.getRowDimension() != m) {
                throw new DimensionMismatchException(b.getRowDimension(), m);
            }
            int nColB = b.getColumnDimension();
            double[][] bp = (double[][]) Array.newInstance(Double.TYPE, m, nColB);
            double[] tmpCol = new double[m];
            for (int k = 0; k < nColB; k++) {
                for (int i = 0; i < m; i++) {
                    tmpCol[i] = b.getEntry(i, k);
                    bp[i][k] = 0.0d;
                }
                for (int i2 = 0; i2 < m; i2++) {
                    ArrayRealVector v = this.eigenvectors[i2];
                    double[] vData = v.getDataRef();
                    double s = 0.0d;
                    for (int j = 0; j < m; j++) {
                        s += v.getEntry(j) * tmpCol[j];
                    }
                    double s2 = s / this.realEigenvalues[i2];
                    for (int j2 = 0; j2 < m; j2++) {
                        double[] dArr = bp[j2];
                        dArr[k] = dArr[k] + (vData[j2] * s2);
                    }
                }
            }
            return new Array2DRowRealMatrix(bp, false);
        }

        @Override // org.apache.commons.math3.linear.DecompositionSolver
        public boolean isNonSingular() {
            double largestEigenvalueNorm = 0.0d;
            for (int i = 0; i < this.realEigenvalues.length; i++) {
                largestEigenvalueNorm = FastMath.max(largestEigenvalueNorm, eigenvalueNorm(i));
            }
            if (largestEigenvalueNorm == 0.0d) {
                return false;
            }
            for (int i2 = 0; i2 < this.realEigenvalues.length; i2++) {
                if (Precision.equals(eigenvalueNorm(i2) / largestEigenvalueNorm, 0.0d, 1.0E-12d)) {
                    return false;
                }
            }
            return true;
        }

        private double eigenvalueNorm(int i) {
            double re = this.realEigenvalues[i];
            double im = this.imagEigenvalues[i];
            return FastMath.sqrt((re * re) + (im * im));
        }

        @Override // org.apache.commons.math3.linear.DecompositionSolver
        public RealMatrix getInverse() {
            if (!isNonSingular()) {
                throw new SingularMatrixException();
            }
            int m = this.realEigenvalues.length;
            double[][] invData = (double[][]) Array.newInstance(Double.TYPE, m, m);
            for (int i = 0; i < m; i++) {
                double[] invI = invData[i];
                for (int j = 0; j < m; j++) {
                    double invIJ = 0.0d;
                    for (int k = 0; k < m; k++) {
                        double[] vK = this.eigenvectors[k].getDataRef();
                        invIJ += (vK[i] * vK[j]) / this.realEigenvalues[k];
                    }
                    invI[j] = invIJ;
                }
            }
            return MatrixUtils.createRealMatrix(invData);
        }
    }

    private void transformToTridiagonal(RealMatrix matrix) {
        this.transformer = new TriDiagonalTransformer(matrix);
        this.main = this.transformer.getMainDiagonalRef();
        this.secondary = this.transformer.getSecondaryDiagonalRef();
    }

    private void findEigenVectors(double[][] householderMatrix) {
        int m;
        double q;
        double[][] z = (double[][]) householderMatrix.clone();
        int n = this.main.length;
        this.realEigenvalues = new double[n];
        this.imagEigenvalues = new double[n];
        double[] e = new double[n];
        for (int i = 0; i < n - 1; i++) {
            this.realEigenvalues[i] = this.main[i];
            e[i] = this.secondary[i];
        }
        this.realEigenvalues[n - 1] = this.main[n - 1];
        e[n - 1] = 0.0d;
        double maxAbsoluteValue = 0.0d;
        for (int i2 = 0; i2 < n; i2++) {
            if (FastMath.abs(this.realEigenvalues[i2]) > maxAbsoluteValue) {
                maxAbsoluteValue = FastMath.abs(this.realEigenvalues[i2]);
            }
            if (FastMath.abs(e[i2]) > maxAbsoluteValue) {
                maxAbsoluteValue = FastMath.abs(e[i2]);
            }
        }
        if (maxAbsoluteValue != 0.0d) {
            for (int i3 = 0; i3 < n; i3++) {
                if (FastMath.abs(this.realEigenvalues[i3]) <= Precision.EPSILON * maxAbsoluteValue) {
                    this.realEigenvalues[i3] = 0.0d;
                }
                if (FastMath.abs(e[i3]) <= Precision.EPSILON * maxAbsoluteValue) {
                    e[i3] = 0.0d;
                }
            }
        }
        for (int j = 0; j < n; j++) {
            int its = 0;
            do {
                m = j;
                while (m < n - 1) {
                    double delta = FastMath.abs(this.realEigenvalues[m]) + FastMath.abs(this.realEigenvalues[m + 1]);
                    if (FastMath.abs(e[m]) + delta == delta) {
                        break;
                    }
                    m++;
                }
                if (m != j) {
                    if (its == this.maxIter) {
                        throw new MaxCountExceededException(LocalizedFormats.CONVERGENCE_FAILED, Byte.valueOf(this.maxIter), new Object[0]);
                    }
                    its++;
                    double q2 = (this.realEigenvalues[j + 1] - this.realEigenvalues[j]) / (2.0d * e[j]);
                    double t = FastMath.sqrt(1.0d + (q2 * q2));
                    if (q2 < 0.0d) {
                        q = (this.realEigenvalues[m] - this.realEigenvalues[j]) + (e[j] / (q2 - t));
                    } else {
                        q = (this.realEigenvalues[m] - this.realEigenvalues[j]) + (e[j] / (q2 + t));
                    }
                    double u = 0.0d;
                    double s = 1.0d;
                    double c = 1.0d;
                    int i4 = m - 1;
                    while (true) {
                        if (i4 < j) {
                            break;
                        }
                        double p = s * e[i4];
                        double h = c * e[i4];
                        if (FastMath.abs(p) >= FastMath.abs(q)) {
                            double c2 = q / p;
                            t = FastMath.sqrt((c2 * c2) + 1.0d);
                            e[i4 + 1] = p * t;
                            s = 1.0d / t;
                            c = c2 * s;
                        } else {
                            double s2 = p / q;
                            t = FastMath.sqrt((s2 * s2) + 1.0d);
                            e[i4 + 1] = q * t;
                            c = 1.0d / t;
                            s = s2 * c;
                        }
                        if (e[i4 + 1] == 0.0d) {
                            double[] dArr = this.realEigenvalues;
                            int i5 = i4 + 1;
                            dArr[i5] = dArr[i5] - u;
                            e[m] = 0.0d;
                            break;
                        }
                        double q3 = this.realEigenvalues[i4 + 1] - u;
                        t = ((this.realEigenvalues[i4] - q3) * s) + (2.0d * c * h);
                        u = s * t;
                        this.realEigenvalues[i4 + 1] = q3 + u;
                        q = (c * t) - h;
                        for (int ia = 0; ia < n; ia++) {
                            double p2 = z[ia][i4 + 1];
                            z[ia][i4 + 1] = (z[ia][i4] * s) + (c * p2);
                            z[ia][i4] = (z[ia][i4] * c) - (s * p2);
                        }
                        i4--;
                    }
                    if (t != 0.0d || i4 < j) {
                        double[] dArr2 = this.realEigenvalues;
                        dArr2[j] = dArr2[j] - u;
                        e[j] = q;
                        e[m] = 0.0d;
                        continue;
                    }
                }
            } while (m != j);
        }
        for (int i6 = 0; i6 < n; i6++) {
            int k = i6;
            double p3 = this.realEigenvalues[i6];
            for (int j2 = i6 + 1; j2 < n; j2++) {
                if (this.realEigenvalues[j2] > p3) {
                    k = j2;
                    p3 = this.realEigenvalues[j2];
                }
            }
            if (k != i6) {
                this.realEigenvalues[k] = this.realEigenvalues[i6];
                this.realEigenvalues[i6] = p3;
                for (int j3 = 0; j3 < n; j3++) {
                    double p4 = z[j3][i6];
                    z[j3][i6] = z[j3][k];
                    z[j3][k] = p4;
                }
            }
        }
        double maxAbsoluteValue2 = 0.0d;
        for (int i7 = 0; i7 < n; i7++) {
            if (FastMath.abs(this.realEigenvalues[i7]) > maxAbsoluteValue2) {
                maxAbsoluteValue2 = FastMath.abs(this.realEigenvalues[i7]);
            }
        }
        if (maxAbsoluteValue2 != 0.0d) {
            for (int i8 = 0; i8 < n; i8++) {
                if (FastMath.abs(this.realEigenvalues[i8]) < Precision.EPSILON * maxAbsoluteValue2) {
                    this.realEigenvalues[i8] = 0.0d;
                }
            }
        }
        this.eigenvectors = new ArrayRealVector[n];
        double[] tmp = new double[n];
        for (int i9 = 0; i9 < n; i9++) {
            for (int j4 = 0; j4 < n; j4++) {
                tmp[j4] = z[j4][i9];
            }
            this.eigenvectors[i9] = new ArrayRealVector(tmp);
        }
    }

    private SchurTransformer transformToSchur(RealMatrix matrix) {
        SchurTransformer schurTransform = new SchurTransformer(matrix);
        double[][] matT = schurTransform.getT().getData();
        this.realEigenvalues = new double[matT.length];
        this.imagEigenvalues = new double[matT.length];
        int i = 0;
        while (i < this.realEigenvalues.length) {
            if (i == this.realEigenvalues.length - 1 || Precision.equals(matT[i + 1][i], 0.0d, 1.0E-12d)) {
                this.realEigenvalues[i] = matT[i][i];
            } else {
                double x = matT[i + 1][i + 1];
                double p = 0.5d * (matT[i][i] - x);
                double z = FastMath.sqrt(FastMath.abs((p * p) + (matT[i + 1][i] * matT[i][i + 1])));
                this.realEigenvalues[i] = x + p;
                this.imagEigenvalues[i] = z;
                this.realEigenvalues[i + 1] = x + p;
                this.imagEigenvalues[i + 1] = -z;
                i++;
            }
            i++;
        }
        return schurTransform;
    }

    private Complex cdiv(double xr, double xi, double yr, double yi) {
        return new Complex(xr, xi).divide(new Complex(yr, yi));
    }

    private void findEigenVectorsFromSchur(SchurTransformer schur) throws MathArithmeticException {
        double[][] matrixT = schur.getT().getData();
        double[][] matrixP = schur.getP().getData();
        int n = matrixT.length;
        double norm = 0.0d;
        for (int i = 0; i < n; i++) {
            for (int j = FastMath.max(i - 1, 0); j < n; j++) {
                norm += FastMath.abs(matrixT[i][j]);
            }
        }
        if (Precision.equals(norm, 0.0d, 1.0E-12d)) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_NORM, new Object[0]);
        }
        double r = 0.0d;
        double s = 0.0d;
        double z = 0.0d;
        for (int idx = n - 1; idx >= 0; idx--) {
            double p = this.realEigenvalues[idx];
            double q = this.imagEigenvalues[idx];
            if (Precision.equals(q, 0.0d)) {
                int l = idx;
                matrixT[idx][idx] = 1.0d;
                for (int i2 = idx - 1; i2 >= 0; i2--) {
                    double w = matrixT[i2][i2] - p;
                    r = 0.0d;
                    for (int j2 = l; j2 <= idx; j2++) {
                        r += matrixT[i2][j2] * matrixT[j2][idx];
                    }
                    if (Precision.compareTo(this.imagEigenvalues[i2], 0.0d, 1.0E-12d) < 0) {
                        z = w;
                        s = r;
                    } else {
                        l = i2;
                        if (!Precision.equals(this.imagEigenvalues[i2], 0.0d)) {
                            double x = matrixT[i2][i2 + 1];
                            double y = matrixT[i2 + 1][i2];
                            double t = ((x * s) - (z * r)) / (((this.realEigenvalues[i2] - p) * (this.realEigenvalues[i2] - p)) + (this.imagEigenvalues[i2] * this.imagEigenvalues[i2]));
                            matrixT[i2][idx] = t;
                            if (FastMath.abs(x) > FastMath.abs(z)) {
                                matrixT[i2 + 1][idx] = ((-r) - (w * t)) / x;
                            } else {
                                matrixT[i2 + 1][idx] = ((-s) - (y * t)) / z;
                            }
                        } else if (w != 0.0d) {
                            matrixT[i2][idx] = (-r) / w;
                        } else {
                            matrixT[i2][idx] = (-r) / (Precision.EPSILON * norm);
                        }
                        double t2 = FastMath.abs(matrixT[i2][idx]);
                        if (Precision.EPSILON * t2 * t2 > 1.0d) {
                            for (int j3 = i2; j3 <= idx; j3++) {
                                double[] dArr = matrixT[j3];
                                dArr[idx] = dArr[idx] / t2;
                            }
                        }
                    }
                }
            } else if (q < 0.0d) {
                int l2 = idx - 1;
                if (FastMath.abs(matrixT[idx][idx - 1]) > FastMath.abs(matrixT[idx - 1][idx])) {
                    matrixT[idx - 1][idx - 1] = q / matrixT[idx][idx - 1];
                    matrixT[idx - 1][idx] = (-(matrixT[idx][idx] - p)) / matrixT[idx][idx - 1];
                } else {
                    Complex result = cdiv(0.0d, -matrixT[idx - 1][idx], matrixT[idx - 1][idx - 1] - p, q);
                    matrixT[idx - 1][idx - 1] = result.getReal();
                    matrixT[idx - 1][idx] = result.getImaginary();
                }
                matrixT[idx][idx - 1] = 0.0d;
                matrixT[idx][idx] = 1.0d;
                for (int i3 = idx - 2; i3 >= 0; i3--) {
                    double ra = 0.0d;
                    double sa = 0.0d;
                    for (int j4 = l2; j4 <= idx; j4++) {
                        ra += matrixT[i3][j4] * matrixT[j4][idx - 1];
                        sa += matrixT[i3][j4] * matrixT[j4][idx];
                    }
                    double w2 = matrixT[i3][i3] - p;
                    if (Precision.compareTo(this.imagEigenvalues[i3], 0.0d, 1.0E-12d) < 0) {
                        z = w2;
                        r = ra;
                        s = sa;
                    } else {
                        l2 = i3;
                        if (Precision.equals(this.imagEigenvalues[i3], 0.0d)) {
                            Complex c = cdiv(-ra, -sa, w2, q);
                            matrixT[i3][idx - 1] = c.getReal();
                            matrixT[i3][idx] = c.getImaginary();
                        } else {
                            double x2 = matrixT[i3][i3 + 1];
                            double y2 = matrixT[i3 + 1][i3];
                            double vr = (((this.realEigenvalues[i3] - p) * (this.realEigenvalues[i3] - p)) + (this.imagEigenvalues[i3] * this.imagEigenvalues[i3])) - (q * q);
                            double vi = (this.realEigenvalues[i3] - p) * 2.0d * q;
                            if (Precision.equals(vr, 0.0d) && Precision.equals(vi, 0.0d)) {
                                vr = Precision.EPSILON * norm * (FastMath.abs(w2) + FastMath.abs(q) + FastMath.abs(x2) + FastMath.abs(y2) + FastMath.abs(z));
                            }
                            Complex c2 = cdiv(((x2 * r) - (z * ra)) + (q * sa), ((x2 * s) - (z * sa)) - (q * ra), vr, vi);
                            matrixT[i3][idx - 1] = c2.getReal();
                            matrixT[i3][idx] = c2.getImaginary();
                            if (FastMath.abs(x2) > FastMath.abs(z) + FastMath.abs(q)) {
                                matrixT[i3 + 1][idx - 1] = (((-ra) - (matrixT[i3][idx - 1] * w2)) + (matrixT[i3][idx] * q)) / x2;
                                matrixT[i3 + 1][idx] = (((-sa) - (matrixT[i3][idx] * w2)) - (matrixT[i3][idx - 1] * q)) / x2;
                            } else {
                                Complex c22 = cdiv((-r) - (matrixT[i3][idx - 1] * y2), (-s) - (matrixT[i3][idx] * y2), z, q);
                                matrixT[i3 + 1][idx - 1] = c22.getReal();
                                matrixT[i3 + 1][idx] = c22.getImaginary();
                            }
                        }
                        double t3 = FastMath.max(FastMath.abs(matrixT[i3][idx - 1]), FastMath.abs(matrixT[i3][idx]));
                        if (Precision.EPSILON * t3 * t3 > 1.0d) {
                            for (int j5 = i3; j5 <= idx; j5++) {
                                double[] dArr2 = matrixT[j5];
                                int i4 = idx - 1;
                                dArr2[i4] = dArr2[i4] / t3;
                                double[] dArr3 = matrixT[j5];
                                dArr3[idx] = dArr3[idx] / t3;
                            }
                        }
                    }
                }
            }
        }
        for (int j6 = n - 1; j6 >= 0; j6--) {
            for (int i5 = 0; i5 <= n - 1; i5++) {
                double z2 = 0.0d;
                for (int k = 0; k <= FastMath.min(j6, n - 1); k++) {
                    z2 += matrixP[i5][k] * matrixT[k][j6];
                }
                matrixP[i5][j6] = z2;
            }
        }
        this.eigenvectors = new ArrayRealVector[n];
        double[] tmp = new double[n];
        for (int i6 = 0; i6 < n; i6++) {
            for (int j7 = 0; j7 < n; j7++) {
                tmp[j7] = matrixP[j7][i6];
            }
            this.eigenvectors[i6] = new ArrayRealVector(tmp);
        }
    }
}
