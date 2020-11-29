package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.util.FastMath;

public class CholeskyDecomposition {
    public static final double DEFAULT_ABSOLUTE_POSITIVITY_THRESHOLD = 1.0E-10d;
    public static final double DEFAULT_RELATIVE_SYMMETRY_THRESHOLD = 1.0E-15d;
    private RealMatrix cachedL;
    private RealMatrix cachedLT;
    private double[][] lTData;

    public CholeskyDecomposition(RealMatrix matrix) {
        this(matrix, 1.0E-15d, 1.0E-10d);
    }

    public CholeskyDecomposition(RealMatrix matrix, double relativeSymmetryThreshold, double absolutePositivityThreshold) {
        if (!matrix.isSquare()) {
            throw new NonSquareMatrixException(matrix.getRowDimension(), matrix.getColumnDimension());
        }
        int order = matrix.getRowDimension();
        this.lTData = matrix.getData();
        this.cachedL = null;
        this.cachedLT = null;
        for (int i = 0; i < order; i++) {
            double[] lI = this.lTData[i];
            for (int j = i + 1; j < order; j++) {
                double[] lJ = this.lTData[j];
                double lIJ = lI[j];
                double lJI = lJ[i];
                if (FastMath.abs(lIJ - lJI) > relativeSymmetryThreshold * FastMath.max(FastMath.abs(lIJ), FastMath.abs(lJI))) {
                    throw new NonSymmetricMatrixException(i, j, relativeSymmetryThreshold);
                }
                lJ[i] = 0.0d;
            }
        }
        for (int i2 = 0; i2 < order; i2++) {
            double[] ltI = this.lTData[i2];
            if (ltI[i2] <= absolutePositivityThreshold) {
                throw new NonPositiveDefiniteMatrixException(ltI[i2], i2, absolutePositivityThreshold);
            }
            ltI[i2] = FastMath.sqrt(ltI[i2]);
            double inverse = 1.0d / ltI[i2];
            for (int q = order - 1; q > i2; q--) {
                ltI[q] = ltI[q] * inverse;
                double[] ltQ = this.lTData[q];
                for (int p = q; p < order; p++) {
                    ltQ[p] = ltQ[p] - (ltI[q] * ltI[p]);
                }
            }
        }
    }

    public RealMatrix getL() {
        if (this.cachedL == null) {
            this.cachedL = getLT().transpose();
        }
        return this.cachedL;
    }

    public RealMatrix getLT() {
        if (this.cachedLT == null) {
            this.cachedLT = MatrixUtils.createRealMatrix(this.lTData);
        }
        return this.cachedLT;
    }

    public double getDeterminant() {
        double determinant = 1.0d;
        for (int i = 0; i < this.lTData.length; i++) {
            double lTii = this.lTData[i][i];
            determinant *= lTii * lTii;
        }
        return determinant;
    }

    public DecompositionSolver getSolver() {
        return new Solver(this.lTData);
    }

    private static class Solver implements DecompositionSolver {
        private final double[][] lTData;

        private Solver(double[][] lTData2) {
            this.lTData = lTData2;
        }

        @Override // org.apache.commons.math3.linear.DecompositionSolver
        public boolean isNonSingular() {
            return true;
        }

        @Override // org.apache.commons.math3.linear.DecompositionSolver
        public RealVector solve(RealVector b) {
            int m = this.lTData.length;
            if (b.getDimension() != m) {
                throw new DimensionMismatchException(b.getDimension(), m);
            }
            double[] x = b.toArray();
            for (int j = 0; j < m; j++) {
                double[] lJ = this.lTData[j];
                x[j] = x[j] / lJ[j];
                double xJ = x[j];
                for (int i = j + 1; i < m; i++) {
                    x[i] = x[i] - (lJ[i] * xJ);
                }
            }
            for (int j2 = m - 1; j2 >= 0; j2--) {
                x[j2] = x[j2] / this.lTData[j2][j2];
                double xJ2 = x[j2];
                for (int i2 = 0; i2 < j2; i2++) {
                    x[i2] = x[i2] - (this.lTData[i2][j2] * xJ2);
                }
            }
            return new ArrayRealVector(x, false);
        }

        @Override // org.apache.commons.math3.linear.DecompositionSolver
        public RealMatrix solve(RealMatrix b) {
            int m = this.lTData.length;
            if (b.getRowDimension() != m) {
                throw new DimensionMismatchException(b.getRowDimension(), m);
            }
            int nColB = b.getColumnDimension();
            double[][] x = b.getData();
            for (int j = 0; j < m; j++) {
                double[] lJ = this.lTData[j];
                double lJJ = lJ[j];
                double[] xJ = x[j];
                for (int k = 0; k < nColB; k++) {
                    xJ[k] = xJ[k] / lJJ;
                }
                for (int i = j + 1; i < m; i++) {
                    double[] xI = x[i];
                    double lJI = lJ[i];
                    for (int k2 = 0; k2 < nColB; k2++) {
                        xI[k2] = xI[k2] - (xJ[k2] * lJI);
                    }
                }
            }
            for (int j2 = m - 1; j2 >= 0; j2--) {
                double lJJ2 = this.lTData[j2][j2];
                double[] xJ2 = x[j2];
                for (int k3 = 0; k3 < nColB; k3++) {
                    xJ2[k3] = xJ2[k3] / lJJ2;
                }
                for (int i2 = 0; i2 < j2; i2++) {
                    double[] xI2 = x[i2];
                    double lIJ = this.lTData[i2][j2];
                    for (int k4 = 0; k4 < nColB; k4++) {
                        xI2[k4] = xI2[k4] - (xJ2[k4] * lIJ);
                    }
                }
            }
            return new Array2DRowRealMatrix(x);
        }

        @Override // org.apache.commons.math3.linear.DecompositionSolver
        public RealMatrix getInverse() {
            return solve(MatrixUtils.createRealIdentityMatrix(this.lTData.length));
        }
    }
}
