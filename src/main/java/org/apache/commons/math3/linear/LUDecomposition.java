package org.apache.commons.math3.linear;

import java.lang.reflect.Array;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.util.FastMath;

public class LUDecomposition {
    private static final double DEFAULT_TOO_SMALL = 1.0E-11d;
    private RealMatrix cachedL;
    private RealMatrix cachedP;
    private RealMatrix cachedU;
    private boolean even;

    /* renamed from: lu */
    private final double[][] f221lu;
    private final int[] pivot;
    private boolean singular;

    public LUDecomposition(RealMatrix matrix) {
        this(matrix, DEFAULT_TOO_SMALL);
    }

    public LUDecomposition(RealMatrix matrix, double singularityThreshold) {
        if (!matrix.isSquare()) {
            throw new NonSquareMatrixException(matrix.getRowDimension(), matrix.getColumnDimension());
        }
        int m = matrix.getColumnDimension();
        this.f221lu = matrix.getData();
        this.pivot = new int[m];
        this.cachedL = null;
        this.cachedU = null;
        this.cachedP = null;
        for (int row = 0; row < m; row++) {
            this.pivot[row] = row;
        }
        this.even = true;
        this.singular = false;
        for (int col = 0; col < m; col++) {
            for (int row2 = 0; row2 < col; row2++) {
                double[] luRow = this.f221lu[row2];
                double sum = luRow[col];
                for (int i = 0; i < row2; i++) {
                    sum -= luRow[i] * this.f221lu[i][col];
                }
                luRow[col] = sum;
            }
            int max = col;
            double largest = Double.NEGATIVE_INFINITY;
            for (int row3 = col; row3 < m; row3++) {
                double[] luRow2 = this.f221lu[row3];
                double sum2 = luRow2[col];
                for (int i2 = 0; i2 < col; i2++) {
                    sum2 -= luRow2[i2] * this.f221lu[i2][col];
                }
                luRow2[col] = sum2;
                if (FastMath.abs(sum2) > largest) {
                    largest = FastMath.abs(sum2);
                    max = row3;
                }
            }
            if (FastMath.abs(this.f221lu[max][col]) < singularityThreshold) {
                this.singular = true;
                return;
            }
            if (max != col) {
                double[] luMax = this.f221lu[max];
                double[] luCol = this.f221lu[col];
                for (int i3 = 0; i3 < m; i3++) {
                    double tmp = luMax[i3];
                    luMax[i3] = luCol[i3];
                    luCol[i3] = tmp;
                }
                int temp = this.pivot[max];
                this.pivot[max] = this.pivot[col];
                this.pivot[col] = temp;
                this.even = !this.even;
            }
            double luDiag = this.f221lu[col][col];
            for (int row4 = col + 1; row4 < m; row4++) {
                double[] dArr = this.f221lu[row4];
                dArr[col] = dArr[col] / luDiag;
            }
        }
    }

    public RealMatrix getL() {
        if (this.cachedL == null && !this.singular) {
            int m = this.pivot.length;
            this.cachedL = MatrixUtils.createRealMatrix(m, m);
            for (int i = 0; i < m; i++) {
                double[] luI = this.f221lu[i];
                for (int j = 0; j < i; j++) {
                    this.cachedL.setEntry(i, j, luI[j]);
                }
                this.cachedL.setEntry(i, i, 1.0d);
            }
        }
        return this.cachedL;
    }

    public RealMatrix getU() {
        if (this.cachedU == null && !this.singular) {
            int m = this.pivot.length;
            this.cachedU = MatrixUtils.createRealMatrix(m, m);
            for (int i = 0; i < m; i++) {
                double[] luI = this.f221lu[i];
                for (int j = i; j < m; j++) {
                    this.cachedU.setEntry(i, j, luI[j]);
                }
            }
        }
        return this.cachedU;
    }

    public RealMatrix getP() {
        if (this.cachedP == null && !this.singular) {
            int m = this.pivot.length;
            this.cachedP = MatrixUtils.createRealMatrix(m, m);
            for (int i = 0; i < m; i++) {
                this.cachedP.setEntry(i, this.pivot[i], 1.0d);
            }
        }
        return this.cachedP;
    }

    public int[] getPivot() {
        return (int[]) this.pivot.clone();
    }

    public double getDeterminant() {
        if (this.singular) {
            return 0.0d;
        }
        int m = this.pivot.length;
        double determinant = this.even ? 1.0d : -1.0d;
        for (int i = 0; i < m; i++) {
            determinant *= this.f221lu[i][i];
        }
        return determinant;
    }

    public DecompositionSolver getSolver() {
        return new Solver(this.f221lu, this.pivot, this.singular);
    }

    /* access modifiers changed from: private */
    public static class Solver implements DecompositionSolver {

        /* renamed from: lu */
        private final double[][] f222lu;
        private final int[] pivot;
        private final boolean singular;

        private Solver(double[][] lu, int[] pivot2, boolean singular2) {
            this.f222lu = lu;
            this.pivot = pivot2;
            this.singular = singular2;
        }

        @Override // org.apache.commons.math3.linear.DecompositionSolver
        public boolean isNonSingular() {
            return !this.singular;
        }

        @Override // org.apache.commons.math3.linear.DecompositionSolver
        public RealVector solve(RealVector b) {
            int m = this.pivot.length;
            if (b.getDimension() != m) {
                throw new DimensionMismatchException(b.getDimension(), m);
            } else if (this.singular) {
                throw new SingularMatrixException();
            } else {
                double[] bp = new double[m];
                for (int row = 0; row < m; row++) {
                    bp[row] = b.getEntry(this.pivot[row]);
                }
                for (int col = 0; col < m; col++) {
                    double bpCol = bp[col];
                    for (int i = col + 1; i < m; i++) {
                        bp[i] = bp[i] - (this.f222lu[i][col] * bpCol);
                    }
                }
                for (int col2 = m - 1; col2 >= 0; col2--) {
                    bp[col2] = bp[col2] / this.f222lu[col2][col2];
                    double bpCol2 = bp[col2];
                    for (int i2 = 0; i2 < col2; i2++) {
                        bp[i2] = bp[i2] - (this.f222lu[i2][col2] * bpCol2);
                    }
                }
                return new ArrayRealVector(bp, false);
            }
        }

        @Override // org.apache.commons.math3.linear.DecompositionSolver
        public RealMatrix solve(RealMatrix b) {
            int m = this.pivot.length;
            if (b.getRowDimension() != m) {
                throw new DimensionMismatchException(b.getRowDimension(), m);
            } else if (this.singular) {
                throw new SingularMatrixException();
            } else {
                int nColB = b.getColumnDimension();
                double[][] bp = (double[][]) Array.newInstance(Double.TYPE, m, nColB);
                for (int row = 0; row < m; row++) {
                    double[] bpRow = bp[row];
                    int pRow = this.pivot[row];
                    for (int col = 0; col < nColB; col++) {
                        bpRow[col] = b.getEntry(pRow, col);
                    }
                }
                for (int col2 = 0; col2 < m; col2++) {
                    double[] bpCol = bp[col2];
                    for (int i = col2 + 1; i < m; i++) {
                        double[] bpI = bp[i];
                        double luICol = this.f222lu[i][col2];
                        for (int j = 0; j < nColB; j++) {
                            bpI[j] = bpI[j] - (bpCol[j] * luICol);
                        }
                    }
                }
                for (int col3 = m - 1; col3 >= 0; col3--) {
                    double[] bpCol2 = bp[col3];
                    double luDiag = this.f222lu[col3][col3];
                    for (int j2 = 0; j2 < nColB; j2++) {
                        bpCol2[j2] = bpCol2[j2] / luDiag;
                    }
                    for (int i2 = 0; i2 < col3; i2++) {
                        double[] bpI2 = bp[i2];
                        double luICol2 = this.f222lu[i2][col3];
                        for (int j3 = 0; j3 < nColB; j3++) {
                            bpI2[j3] = bpI2[j3] - (bpCol2[j3] * luICol2);
                        }
                    }
                }
                return new Array2DRowRealMatrix(bp, false);
            }
        }

        @Override // org.apache.commons.math3.linear.DecompositionSolver
        public RealMatrix getInverse() {
            return solve(MatrixUtils.createRealIdentityMatrix(this.pivot.length));
        }
    }
}
