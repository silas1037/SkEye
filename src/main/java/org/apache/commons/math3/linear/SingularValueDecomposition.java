package org.apache.commons.math3.linear;

import java.lang.reflect.Array;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Precision;

public class SingularValueDecomposition {
    private static final double EPS = 2.220446049250313E-16d;
    private static final double TINY = 1.6033346880071782E-291d;
    private RealMatrix cachedS;
    private final RealMatrix cachedU;
    private RealMatrix cachedUt;
    private final RealMatrix cachedV;
    private RealMatrix cachedVt;

    /* renamed from: m */
    private final int f232m;

    /* renamed from: n */
    private final int f233n;
    private final double[] singularValues;
    private final double tol;
    private final boolean transposed;

    public SingularValueDecomposition(RealMatrix matrix) {
        double[][] A;
        int kase;
        if (matrix.getRowDimension() < matrix.getColumnDimension()) {
            this.transposed = true;
            A = matrix.transpose().getData();
            this.f232m = matrix.getColumnDimension();
            this.f233n = matrix.getRowDimension();
        } else {
            this.transposed = false;
            A = matrix.getData();
            this.f232m = matrix.getRowDimension();
            this.f233n = matrix.getColumnDimension();
        }
        this.singularValues = new double[this.f233n];
        double[][] U = (double[][]) Array.newInstance(Double.TYPE, this.f232m, this.f233n);
        double[][] V = (double[][]) Array.newInstance(Double.TYPE, this.f233n, this.f233n);
        double[] e = new double[this.f233n];
        double[] work = new double[this.f232m];
        int nct = FastMath.min(this.f232m - 1, this.f233n);
        int nrt = FastMath.max(0, this.f233n - 2);
        for (int k = 0; k < FastMath.max(nct, nrt); k++) {
            if (k < nct) {
                this.singularValues[k] = 0.0d;
                for (int i = k; i < this.f232m; i++) {
                    this.singularValues[k] = FastMath.hypot(this.singularValues[k], A[i][k]);
                }
                if (this.singularValues[k] != 0.0d) {
                    if (A[k][k] < 0.0d) {
                        this.singularValues[k] = -this.singularValues[k];
                    }
                    for (int i2 = k; i2 < this.f232m; i2++) {
                        double[] dArr = A[i2];
                        dArr[k] = dArr[k] / this.singularValues[k];
                    }
                    double[] dArr2 = A[k];
                    dArr2[k] = dArr2[k] + 1.0d;
                }
                this.singularValues[k] = -this.singularValues[k];
            }
            for (int j = k + 1; j < this.f233n; j++) {
                if (k < nct && this.singularValues[k] != 0.0d) {
                    double t = 0.0d;
                    for (int i3 = k; i3 < this.f232m; i3++) {
                        t += A[i3][k] * A[i3][j];
                    }
                    double t2 = (-t) / A[k][k];
                    for (int i4 = k; i4 < this.f232m; i4++) {
                        double[] dArr3 = A[i4];
                        dArr3[j] = dArr3[j] + (A[i4][k] * t2);
                    }
                }
                e[j] = A[k][j];
            }
            if (k < nct) {
                for (int i5 = k; i5 < this.f232m; i5++) {
                    U[i5][k] = A[i5][k];
                }
            }
            if (k < nrt) {
                e[k] = 0.0d;
                for (int i6 = k + 1; i6 < this.f233n; i6++) {
                    e[k] = FastMath.hypot(e[k], e[i6]);
                }
                if (e[k] != 0.0d) {
                    if (e[k + 1] < 0.0d) {
                        e[k] = -e[k];
                    }
                    for (int i7 = k + 1; i7 < this.f233n; i7++) {
                        e[i7] = e[i7] / e[k];
                    }
                    int i8 = k + 1;
                    e[i8] = e[i8] + 1.0d;
                }
                e[k] = -e[k];
                if (k + 1 < this.f232m && e[k] != 0.0d) {
                    for (int i9 = k + 1; i9 < this.f232m; i9++) {
                        work[i9] = 0.0d;
                    }
                    for (int j2 = k + 1; j2 < this.f233n; j2++) {
                        for (int i10 = k + 1; i10 < this.f232m; i10++) {
                            work[i10] = work[i10] + (e[j2] * A[i10][j2]);
                        }
                    }
                    for (int j3 = k + 1; j3 < this.f233n; j3++) {
                        double t3 = (-e[j3]) / e[k + 1];
                        for (int i11 = k + 1; i11 < this.f232m; i11++) {
                            double[] dArr4 = A[i11];
                            dArr4[j3] = dArr4[j3] + (work[i11] * t3);
                        }
                    }
                }
                for (int i12 = k + 1; i12 < this.f233n; i12++) {
                    V[i12][k] = e[i12];
                }
            }
        }
        int p = this.f233n;
        if (nct < this.f233n) {
            this.singularValues[nct] = A[nct][nct];
        }
        if (this.f232m < p) {
            this.singularValues[p - 1] = 0.0d;
        }
        if (nrt + 1 < p) {
            e[nrt] = A[nrt][p - 1];
        }
        e[p - 1] = 0.0d;
        for (int j4 = nct; j4 < this.f233n; j4++) {
            for (int i13 = 0; i13 < this.f232m; i13++) {
                U[i13][j4] = 0.0d;
            }
            U[j4][j4] = 1.0d;
        }
        for (int k2 = nct - 1; k2 >= 0; k2--) {
            if (this.singularValues[k2] != 0.0d) {
                for (int j5 = k2 + 1; j5 < this.f233n; j5++) {
                    double t4 = 0.0d;
                    for (int i14 = k2; i14 < this.f232m; i14++) {
                        t4 += U[i14][k2] * U[i14][j5];
                    }
                    double t5 = (-t4) / U[k2][k2];
                    for (int i15 = k2; i15 < this.f232m; i15++) {
                        double[] dArr5 = U[i15];
                        dArr5[j5] = dArr5[j5] + (U[i15][k2] * t5);
                    }
                }
                for (int i16 = k2; i16 < this.f232m; i16++) {
                    U[i16][k2] = -U[i16][k2];
                }
                U[k2][k2] = 1.0d + U[k2][k2];
                for (int i17 = 0; i17 < k2 - 1; i17++) {
                    U[i17][k2] = 0.0d;
                }
            } else {
                for (int i18 = 0; i18 < this.f232m; i18++) {
                    U[i18][k2] = 0.0d;
                }
                U[k2][k2] = 1.0d;
            }
        }
        for (int k3 = this.f233n - 1; k3 >= 0; k3--) {
            if (k3 < nrt && e[k3] != 0.0d) {
                for (int j6 = k3 + 1; j6 < this.f233n; j6++) {
                    double t6 = 0.0d;
                    for (int i19 = k3 + 1; i19 < this.f233n; i19++) {
                        t6 += V[i19][k3] * V[i19][j6];
                    }
                    double t7 = (-t6) / V[k3 + 1][k3];
                    for (int i20 = k3 + 1; i20 < this.f233n; i20++) {
                        double[] dArr6 = V[i20];
                        dArr6[j6] = dArr6[j6] + (V[i20][k3] * t7);
                    }
                }
            }
            for (int i21 = 0; i21 < this.f233n; i21++) {
                V[i21][k3] = 0.0d;
            }
            V[k3][k3] = 1.0d;
        }
        int pp = p - 1;
        while (p > 0) {
            int k4 = p - 2;
            while (true) {
                if (k4 >= 0) {
                    if (FastMath.abs(e[k4]) <= TINY + (EPS * (FastMath.abs(this.singularValues[k4]) + FastMath.abs(this.singularValues[k4 + 1])))) {
                        e[k4] = 0.0d;
                    } else {
                        k4--;
                    }
                }
            }
            if (k4 == p - 2) {
                kase = 4;
            } else {
                int ks = p - 1;
                while (true) {
                    if (ks >= k4 && ks != k4) {
                        if (FastMath.abs(this.singularValues[ks]) <= TINY + (EPS * ((ks != p ? FastMath.abs(e[ks]) : 0.0d) + (ks != k4 + 1 ? FastMath.abs(e[ks - 1]) : 0.0d)))) {
                            this.singularValues[ks] = 0.0d;
                        } else {
                            ks--;
                        }
                    }
                }
                if (ks == k4) {
                    kase = 3;
                } else if (ks == p - 1) {
                    kase = 1;
                } else {
                    kase = 2;
                    k4 = ks;
                }
            }
            int k5 = k4 + 1;
            switch (kase) {
                case 1:
                    double f = e[p - 2];
                    e[p - 2] = 0.0d;
                    for (int j7 = p - 2; j7 >= k5; j7--) {
                        double t8 = FastMath.hypot(this.singularValues[j7], f);
                        double cs = this.singularValues[j7] / t8;
                        double sn = f / t8;
                        this.singularValues[j7] = t8;
                        if (j7 != k5) {
                            f = (-sn) * e[j7 - 1];
                            e[j7 - 1] = e[j7 - 1] * cs;
                        }
                        for (int i22 = 0; i22 < this.f233n; i22++) {
                            double t9 = (V[i22][j7] * cs) + (V[i22][p - 1] * sn);
                            V[i22][p - 1] = ((-sn) * V[i22][j7]) + (V[i22][p - 1] * cs);
                            V[i22][j7] = t9;
                        }
                    }
                    break;
                case 2:
                    double f2 = e[k5 - 1];
                    e[k5 - 1] = 0.0d;
                    for (int j8 = k5; j8 < p; j8++) {
                        double t10 = FastMath.hypot(this.singularValues[j8], f2);
                        double cs2 = this.singularValues[j8] / t10;
                        double sn2 = f2 / t10;
                        this.singularValues[j8] = t10;
                        f2 = (-sn2) * e[j8];
                        e[j8] = e[j8] * cs2;
                        for (int i23 = 0; i23 < this.f232m; i23++) {
                            double t11 = (U[i23][j8] * cs2) + (U[i23][k5 - 1] * sn2);
                            U[i23][k5 - 1] = ((-sn2) * U[i23][j8]) + (U[i23][k5 - 1] * cs2);
                            U[i23][j8] = t11;
                        }
                    }
                    break;
                case 3:
                    double scale = FastMath.max(FastMath.max(FastMath.max(FastMath.max(FastMath.abs(this.singularValues[p - 1]), FastMath.abs(this.singularValues[p - 2])), FastMath.abs(e[p - 2])), FastMath.abs(this.singularValues[k5])), FastMath.abs(e[k5]));
                    double sp = this.singularValues[p - 1] / scale;
                    double spm1 = this.singularValues[p - 2] / scale;
                    double epm1 = e[p - 2] / scale;
                    double sk = this.singularValues[k5] / scale;
                    double ek = e[k5] / scale;
                    double b = (((spm1 + sp) * (spm1 - sp)) + (epm1 * epm1)) / 2.0d;
                    double c = sp * epm1 * sp * epm1;
                    double shift = 0.0d;
                    if (!(b == 0.0d && c == 0.0d)) {
                        double shift2 = FastMath.sqrt((b * b) + c);
                        shift = c / (b + (b < 0.0d ? -shift2 : shift2));
                    }
                    double f3 = ((sk + sp) * (sk - sp)) + shift;
                    double g = sk * ek;
                    for (int j9 = k5; j9 < p - 1; j9++) {
                        double t12 = FastMath.hypot(f3, g);
                        double cs3 = f3 / t12;
                        double sn3 = g / t12;
                        if (j9 != k5) {
                            e[j9 - 1] = t12;
                        }
                        double f4 = (this.singularValues[j9] * cs3) + (e[j9] * sn3);
                        e[j9] = (e[j9] * cs3) - (this.singularValues[j9] * sn3);
                        double g2 = sn3 * this.singularValues[j9 + 1];
                        this.singularValues[j9 + 1] = this.singularValues[j9 + 1] * cs3;
                        for (int i24 = 0; i24 < this.f233n; i24++) {
                            double t13 = (V[i24][j9] * cs3) + (V[i24][j9 + 1] * sn3);
                            V[i24][j9 + 1] = ((-sn3) * V[i24][j9]) + (V[i24][j9 + 1] * cs3);
                            V[i24][j9] = t13;
                        }
                        double t14 = FastMath.hypot(f4, g2);
                        double cs4 = f4 / t14;
                        double sn4 = g2 / t14;
                        this.singularValues[j9] = t14;
                        f3 = (e[j9] * cs4) + (this.singularValues[j9 + 1] * sn4);
                        this.singularValues[j9 + 1] = ((-sn4) * e[j9]) + (this.singularValues[j9 + 1] * cs4);
                        g = sn4 * e[j9 + 1];
                        e[j9 + 1] = e[j9 + 1] * cs4;
                        if (j9 < this.f232m - 1) {
                            for (int i25 = 0; i25 < this.f232m; i25++) {
                                double t15 = (U[i25][j9] * cs4) + (U[i25][j9 + 1] * sn4);
                                U[i25][j9 + 1] = ((-sn4) * U[i25][j9]) + (U[i25][j9 + 1] * cs4);
                                U[i25][j9] = t15;
                            }
                        }
                    }
                    e[p - 2] = f3;
                    break;
                default:
                    if (this.singularValues[k5] <= 0.0d) {
                        this.singularValues[k5] = this.singularValues[k5] < 0.0d ? -this.singularValues[k5] : 0.0d;
                        for (int i26 = 0; i26 <= pp; i26++) {
                            V[i26][k5] = -V[i26][k5];
                        }
                    }
                    while (k5 < pp && this.singularValues[k5] < this.singularValues[k5 + 1]) {
                        double t16 = this.singularValues[k5];
                        this.singularValues[k5] = this.singularValues[k5 + 1];
                        this.singularValues[k5 + 1] = t16;
                        if (k5 < this.f233n - 1) {
                            for (int i27 = 0; i27 < this.f233n; i27++) {
                                double t17 = V[i27][k5 + 1];
                                V[i27][k5 + 1] = V[i27][k5];
                                V[i27][k5] = t17;
                            }
                        }
                        if (k5 < this.f232m - 1) {
                            for (int i28 = 0; i28 < this.f232m; i28++) {
                                double t18 = U[i28][k5 + 1];
                                U[i28][k5 + 1] = U[i28][k5];
                                U[i28][k5] = t18;
                            }
                        }
                        k5++;
                    }
                    p--;
                    break;
            }
        }
        this.tol = FastMath.max(((double) this.f232m) * this.singularValues[0] * EPS, FastMath.sqrt(Precision.SAFE_MIN));
        if (!this.transposed) {
            this.cachedU = MatrixUtils.createRealMatrix(U);
            this.cachedV = MatrixUtils.createRealMatrix(V);
            return;
        }
        this.cachedU = MatrixUtils.createRealMatrix(V);
        this.cachedV = MatrixUtils.createRealMatrix(U);
    }

    public RealMatrix getU() {
        return this.cachedU;
    }

    public RealMatrix getUT() {
        if (this.cachedUt == null) {
            this.cachedUt = getU().transpose();
        }
        return this.cachedUt;
    }

    public RealMatrix getS() {
        if (this.cachedS == null) {
            this.cachedS = MatrixUtils.createRealDiagonalMatrix(this.singularValues);
        }
        return this.cachedS;
    }

    public double[] getSingularValues() {
        return (double[]) this.singularValues.clone();
    }

    public RealMatrix getV() {
        return this.cachedV;
    }

    public RealMatrix getVT() {
        if (this.cachedVt == null) {
            this.cachedVt = getV().transpose();
        }
        return this.cachedVt;
    }

    public RealMatrix getCovariance(double minSingularValue) {
        int p = this.singularValues.length;
        int dimension = 0;
        while (dimension < p && this.singularValues[dimension] >= minSingularValue) {
            dimension++;
        }
        if (dimension == 0) {
            throw new NumberIsTooLargeException(LocalizedFormats.TOO_LARGE_CUTOFF_SINGULAR_VALUE, Double.valueOf(minSingularValue), Double.valueOf(this.singularValues[0]), true);
        }
        final double[][] data = (double[][]) Array.newInstance(Double.TYPE, dimension, p);
        getVT().walkInOptimizedOrder(new DefaultRealMatrixPreservingVisitor() {
            /* class org.apache.commons.math3.linear.SingularValueDecomposition.C02791 */

            @Override // org.apache.commons.math3.linear.RealMatrixPreservingVisitor, org.apache.commons.math3.linear.DefaultRealMatrixPreservingVisitor
            public void visit(int row, int column, double value) {
                data[row][column] = value / SingularValueDecomposition.this.singularValues[row];
            }
        }, 0, dimension - 1, 0, p - 1);
        RealMatrix jv = new Array2DRowRealMatrix(data, false);
        return jv.transpose().multiply(jv);
    }

    public double getNorm() {
        return this.singularValues[0];
    }

    public double getConditionNumber() {
        return this.singularValues[0] / this.singularValues[this.f233n - 1];
    }

    public double getInverseConditionNumber() {
        return this.singularValues[this.f233n - 1] / this.singularValues[0];
    }

    public int getRank() {
        int r = 0;
        for (int i = 0; i < this.singularValues.length; i++) {
            if (this.singularValues[i] > this.tol) {
                r++;
            }
        }
        return r;
    }

    public DecompositionSolver getSolver() {
        return new Solver(this.singularValues, getUT(), getV(), getRank() == this.f232m, this.tol);
    }

    /* access modifiers changed from: private */
    public static class Solver implements DecompositionSolver {
        private boolean nonSingular;
        private final RealMatrix pseudoInverse;

        private Solver(double[] singularValues, RealMatrix uT, RealMatrix v, boolean nonSingular2, double tol) {
            double a;
            double[][] suT = uT.getData();
            for (int i = 0; i < singularValues.length; i++) {
                if (singularValues[i] > tol) {
                    a = 1.0d / singularValues[i];
                } else {
                    a = 0.0d;
                }
                double[] suTi = suT[i];
                for (int j = 0; j < suTi.length; j++) {
                    suTi[j] = suTi[j] * a;
                }
            }
            this.pseudoInverse = v.multiply(new Array2DRowRealMatrix(suT, false));
            this.nonSingular = nonSingular2;
        }

        @Override // org.apache.commons.math3.linear.DecompositionSolver
        public RealVector solve(RealVector b) {
            return this.pseudoInverse.operate(b);
        }

        @Override // org.apache.commons.math3.linear.DecompositionSolver
        public RealMatrix solve(RealMatrix b) {
            return this.pseudoInverse.multiply(b);
        }

        @Override // org.apache.commons.math3.linear.DecompositionSolver
        public boolean isNonSingular() {
            return this.nonSingular;
        }

        @Override // org.apache.commons.math3.linear.DecompositionSolver
        public RealMatrix getInverse() {
            return this.pseudoInverse;
        }
    }
}
