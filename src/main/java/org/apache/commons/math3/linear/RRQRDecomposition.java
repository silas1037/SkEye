package org.apache.commons.math3.linear;

import org.apache.commons.math3.util.FastMath;

public class RRQRDecomposition extends QRDecomposition {
    private RealMatrix cachedP;

    /* renamed from: p */
    private int[] f223p;

    public RRQRDecomposition(RealMatrix matrix) {
        this(matrix, 0.0d);
    }

    public RRQRDecomposition(RealMatrix matrix, double threshold) {
        super(matrix, threshold);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.linear.QRDecomposition
    public void decompose(double[][] qrt) {
        this.f223p = new int[qrt.length];
        for (int i = 0; i < this.f223p.length; i++) {
            this.f223p[i] = i;
        }
        super.decompose(qrt);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.linear.QRDecomposition
    public void performHouseholderReflection(int minor, double[][] qrt) {
        double l2NormSquaredMax = 0.0d;
        int l2NormSquaredMaxIndex = minor;
        for (int i = minor; i < qrt.length; i++) {
            double l2NormSquared = 0.0d;
            for (int j = 0; j < qrt[i].length; j++) {
                l2NormSquared += qrt[i][j] * qrt[i][j];
            }
            if (l2NormSquared > l2NormSquaredMax) {
                l2NormSquaredMax = l2NormSquared;
                l2NormSquaredMaxIndex = i;
            }
        }
        if (l2NormSquaredMaxIndex != minor) {
            double[] tmp1 = qrt[minor];
            qrt[minor] = qrt[l2NormSquaredMaxIndex];
            qrt[l2NormSquaredMaxIndex] = tmp1;
            int tmp2 = this.f223p[minor];
            this.f223p[minor] = this.f223p[l2NormSquaredMaxIndex];
            this.f223p[l2NormSquaredMaxIndex] = tmp2;
        }
        super.performHouseholderReflection(minor, qrt);
    }

    public RealMatrix getP() {
        if (this.cachedP == null) {
            int n = this.f223p.length;
            this.cachedP = MatrixUtils.createRealMatrix(n, n);
            for (int i = 0; i < n; i++) {
                this.cachedP.setEntry(this.f223p[i], i, 1.0d);
            }
        }
        return this.cachedP;
    }

    public int getRank(double dropThreshold) {
        RealMatrix r = getR();
        int rows = r.getRowDimension();
        int columns = r.getColumnDimension();
        int rank = 1;
        double lastNorm = r.getFrobeniusNorm();
        while (rank < FastMath.min(rows, columns)) {
            double thisNorm = r.getSubMatrix(rank, rows - 1, rank, columns - 1).getFrobeniusNorm();
            if (thisNorm == 0.0d || (thisNorm / lastNorm) * lastNorm < dropThreshold) {
                break;
            }
            lastNorm = thisNorm;
            rank++;
        }
        return rank;
    }

    @Override // org.apache.commons.math3.linear.QRDecomposition
    public DecompositionSolver getSolver() {
        return new Solver(super.getSolver(), getP());
    }

    private static class Solver implements DecompositionSolver {

        /* renamed from: p */
        private RealMatrix f224p;
        private final DecompositionSolver upper;

        private Solver(DecompositionSolver upper2, RealMatrix p) {
            this.upper = upper2;
            this.f224p = p;
        }

        @Override // org.apache.commons.math3.linear.DecompositionSolver
        public boolean isNonSingular() {
            return this.upper.isNonSingular();
        }

        @Override // org.apache.commons.math3.linear.DecompositionSolver
        public RealVector solve(RealVector b) {
            return this.f224p.operate(this.upper.solve(b));
        }

        @Override // org.apache.commons.math3.linear.DecompositionSolver
        public RealMatrix solve(RealMatrix b) {
            return this.f224p.multiply(this.upper.solve(b));
        }

        @Override // org.apache.commons.math3.linear.DecompositionSolver
        public RealMatrix getInverse() {
            return solve(MatrixUtils.createRealIdentityMatrix(this.f224p.getRowDimension()));
        }
    }
}
