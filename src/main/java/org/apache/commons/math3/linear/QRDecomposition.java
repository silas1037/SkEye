package org.apache.commons.math3.linear;

import java.lang.reflect.Array;
import java.util.Arrays;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.util.FastMath;

public class QRDecomposition {
    private RealMatrix cachedH;
    private RealMatrix cachedQ;
    private RealMatrix cachedQT;
    private RealMatrix cachedR;
    private double[][] qrt;
    private double[] rDiag;
    private final double threshold;

    public QRDecomposition(RealMatrix matrix) {
        this(matrix, 0.0d);
    }

    public QRDecomposition(RealMatrix matrix, double threshold2) {
        this.threshold = threshold2;
        int m = matrix.getRowDimension();
        int n = matrix.getColumnDimension();
        this.qrt = matrix.transpose().getData();
        this.rDiag = new double[FastMath.min(m, n)];
        this.cachedQ = null;
        this.cachedQT = null;
        this.cachedR = null;
        this.cachedH = null;
        decompose(this.qrt);
    }

    /* access modifiers changed from: protected */
    public void decompose(double[][] matrix) {
        for (int minor = 0; minor < FastMath.min(matrix.length, matrix[0].length); minor++) {
            performHouseholderReflection(minor, matrix);
        }
    }

    /* access modifiers changed from: protected */
    public void performHouseholderReflection(int minor, double[][] matrix) {
        double[] qrtMinor = matrix[minor];
        double xNormSqr = 0.0d;
        for (int row = minor; row < qrtMinor.length; row++) {
            double c = qrtMinor[row];
            xNormSqr += c * c;
        }
        double a = qrtMinor[minor] > 0.0d ? -FastMath.sqrt(xNormSqr) : FastMath.sqrt(xNormSqr);
        this.rDiag[minor] = a;
        if (a != 0.0d) {
            qrtMinor[minor] = qrtMinor[minor] - a;
            for (int col = minor + 1; col < matrix.length; col++) {
                double[] qrtCol = matrix[col];
                double alpha = 0.0d;
                for (int row2 = minor; row2 < qrtCol.length; row2++) {
                    alpha -= qrtCol[row2] * qrtMinor[row2];
                }
                double alpha2 = alpha / (qrtMinor[minor] * a);
                for (int row3 = minor; row3 < qrtCol.length; row3++) {
                    qrtCol[row3] = qrtCol[row3] - (qrtMinor[row3] * alpha2);
                }
            }
        }
    }

    public RealMatrix getR() {
        if (this.cachedR == null) {
            int n = this.qrt.length;
            int m = this.qrt[0].length;
            double[][] ra = (double[][]) Array.newInstance(Double.TYPE, m, n);
            for (int row = FastMath.min(m, n) - 1; row >= 0; row--) {
                ra[row][row] = this.rDiag[row];
                for (int col = row + 1; col < n; col++) {
                    ra[row][col] = this.qrt[col][row];
                }
            }
            this.cachedR = MatrixUtils.createRealMatrix(ra);
        }
        return this.cachedR;
    }

    public RealMatrix getQ() {
        if (this.cachedQ == null) {
            this.cachedQ = getQT().transpose();
        }
        return this.cachedQ;
    }

    public RealMatrix getQT() {
        if (this.cachedQT == null) {
            int n = this.qrt.length;
            int m = this.qrt[0].length;
            double[][] qta = (double[][]) Array.newInstance(Double.TYPE, m, m);
            for (int minor = m - 1; minor >= FastMath.min(m, n); minor--) {
                qta[minor][minor] = 1.0d;
            }
            for (int minor2 = FastMath.min(m, n) - 1; minor2 >= 0; minor2--) {
                double[] qrtMinor = this.qrt[minor2];
                qta[minor2][minor2] = 1.0d;
                if (qrtMinor[minor2] != 0.0d) {
                    for (int col = minor2; col < m; col++) {
                        double alpha = 0.0d;
                        for (int row = minor2; row < m; row++) {
                            alpha -= qta[col][row] * qrtMinor[row];
                        }
                        double alpha2 = alpha / (this.rDiag[minor2] * qrtMinor[minor2]);
                        for (int row2 = minor2; row2 < m; row2++) {
                            double[] dArr = qta[col];
                            dArr[row2] = dArr[row2] + ((-alpha2) * qrtMinor[row2]);
                        }
                    }
                }
            }
            this.cachedQT = MatrixUtils.createRealMatrix(qta);
        }
        return this.cachedQT;
    }

    public RealMatrix getH() {
        if (this.cachedH == null) {
            int n = this.qrt.length;
            int m = this.qrt[0].length;
            double[][] ha = (double[][]) Array.newInstance(Double.TYPE, m, n);
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < FastMath.min(i + 1, n); j++) {
                    ha[i][j] = this.qrt[j][i] / (-this.rDiag[j]);
                }
            }
            this.cachedH = MatrixUtils.createRealMatrix(ha);
        }
        return this.cachedH;
    }

    public DecompositionSolver getSolver() {
        return new Solver(this.qrt, this.rDiag, this.threshold);
    }

    /* access modifiers changed from: private */
    public static class Solver implements DecompositionSolver {
        private final double[][] qrt;
        private final double[] rDiag;
        private final double threshold;

        private Solver(double[][] qrt2, double[] rDiag2, double threshold2) {
            this.qrt = qrt2;
            this.rDiag = rDiag2;
            this.threshold = threshold2;
        }

        @Override // org.apache.commons.math3.linear.DecompositionSolver
        public boolean isNonSingular() {
            for (double diag : this.rDiag) {
                if (FastMath.abs(diag) <= this.threshold) {
                    return false;
                }
            }
            return true;
        }

        @Override // org.apache.commons.math3.linear.DecompositionSolver
        public RealVector solve(RealVector b) {
            int n = this.qrt.length;
            int m = this.qrt[0].length;
            if (b.getDimension() != m) {
                throw new DimensionMismatchException(b.getDimension(), m);
            } else if (!isNonSingular()) {
                throw new SingularMatrixException();
            } else {
                double[] x = new double[n];
                double[] y = b.toArray();
                for (int minor = 0; minor < FastMath.min(m, n); minor++) {
                    double[] qrtMinor = this.qrt[minor];
                    double dotProduct = 0.0d;
                    for (int row = minor; row < m; row++) {
                        dotProduct += y[row] * qrtMinor[row];
                    }
                    double dotProduct2 = dotProduct / (this.rDiag[minor] * qrtMinor[minor]);
                    for (int row2 = minor; row2 < m; row2++) {
                        y[row2] = y[row2] + (qrtMinor[row2] * dotProduct2);
                    }
                }
                for (int row3 = this.rDiag.length - 1; row3 >= 0; row3--) {
                    y[row3] = y[row3] / this.rDiag[row3];
                    double yRow = y[row3];
                    double[] qrtRow = this.qrt[row3];
                    x[row3] = yRow;
                    for (int i = 0; i < row3; i++) {
                        y[i] = y[i] - (qrtRow[i] * yRow);
                    }
                }
                return new ArrayRealVector(x, false);
            }
        }

        @Override // org.apache.commons.math3.linear.DecompositionSolver
        public RealMatrix solve(RealMatrix b) {
            int n = this.qrt.length;
            int m = this.qrt[0].length;
            if (b.getRowDimension() != m) {
                throw new DimensionMismatchException(b.getRowDimension(), m);
            } else if (!isNonSingular()) {
                throw new SingularMatrixException();
            } else {
                int columns = b.getColumnDimension();
                int cBlocks = ((columns + 52) - 1) / 52;
                double[][] xBlocks = BlockRealMatrix.createBlocksLayout(n, columns);
                double[][] y = (double[][]) Array.newInstance(Double.TYPE, b.getRowDimension(), 52);
                double[] alpha = new double[52];
                for (int kBlock = 0; kBlock < cBlocks; kBlock++) {
                    int kStart = kBlock * 52;
                    int kEnd = FastMath.min(kStart + 52, columns);
                    int kWidth = kEnd - kStart;
                    b.copySubMatrix(0, m - 1, kStart, kEnd - 1, y);
                    for (int minor = 0; minor < FastMath.min(m, n); minor++) {
                        double[] qrtMinor = this.qrt[minor];
                        double factor = 1.0d / (this.rDiag[minor] * qrtMinor[minor]);
                        Arrays.fill(alpha, 0, kWidth, 0.0d);
                        for (int row = minor; row < m; row++) {
                            double d = qrtMinor[row];
                            double[] yRow = y[row];
                            for (int k = 0; k < kWidth; k++) {
                                alpha[k] = alpha[k] + (yRow[k] * d);
                            }
                        }
                        for (int k2 = 0; k2 < kWidth; k2++) {
                            alpha[k2] = alpha[k2] * factor;
                        }
                        for (int row2 = minor; row2 < m; row2++) {
                            double d2 = qrtMinor[row2];
                            double[] yRow2 = y[row2];
                            for (int k3 = 0; k3 < kWidth; k3++) {
                                yRow2[k3] = yRow2[k3] + (alpha[k3] * d2);
                            }
                        }
                    }
                    for (int j = this.rDiag.length - 1; j >= 0; j--) {
                        int jBlock = j / 52;
                        double factor2 = 1.0d / this.rDiag[j];
                        double[] yJ = y[j];
                        double[] xBlock = xBlocks[(jBlock * cBlocks) + kBlock];
                        int index = (j - (jBlock * 52)) * kWidth;
                        for (int k4 = 0; k4 < kWidth; k4++) {
                            yJ[k4] = yJ[k4] * factor2;
                            index++;
                            xBlock[index] = yJ[k4];
                        }
                        double[] qrtJ = this.qrt[j];
                        for (int i = 0; i < j; i++) {
                            double rIJ = qrtJ[i];
                            double[] yI = y[i];
                            for (int k5 = 0; k5 < kWidth; k5++) {
                                yI[k5] = yI[k5] - (yJ[k5] * rIJ);
                            }
                        }
                    }
                }
                return new BlockRealMatrix(n, columns, xBlocks, false);
            }
        }

        @Override // org.apache.commons.math3.linear.DecompositionSolver
        public RealMatrix getInverse() {
            return solve(MatrixUtils.createRealIdentityMatrix(this.qrt[0].length));
        }
    }
}
