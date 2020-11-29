package org.apache.commons.math3.linear;

import java.lang.reflect.Array;
import org.apache.commons.math3.util.FastMath;

class BiDiagonalTransformer {
    private RealMatrix cachedB = null;
    private RealMatrix cachedU = null;
    private RealMatrix cachedV = null;
    private final double[][] householderVectors;
    private final double[] main;
    private final double[] secondary;

    BiDiagonalTransformer(RealMatrix matrix) {
        int m = matrix.getRowDimension();
        int n = matrix.getColumnDimension();
        int p = FastMath.min(m, n);
        this.householderVectors = matrix.getData();
        this.main = new double[p];
        this.secondary = new double[(p - 1)];
        if (m >= n) {
            transformToUpperBiDiagonal();
        } else {
            transformToLowerBiDiagonal();
        }
    }

    public RealMatrix getU() {
        if (this.cachedU == null) {
            int m = this.householderVectors.length;
            int n = this.householderVectors[0].length;
            int p = this.main.length;
            int diagOffset = m >= n ? 0 : 1;
            double[] diagonal = m >= n ? this.main : this.secondary;
            double[][] ua = (double[][]) Array.newInstance(Double.TYPE, m, m);
            for (int k = m - 1; k >= p; k--) {
                ua[k][k] = 1.0d;
            }
            for (int k2 = p - 1; k2 >= diagOffset; k2--) {
                double[] hK = this.householderVectors[k2];
                ua[k2][k2] = 1.0d;
                if (hK[k2 - diagOffset] != 0.0d) {
                    for (int j = k2; j < m; j++) {
                        double alpha = 0.0d;
                        for (int i = k2; i < m; i++) {
                            alpha -= ua[i][j] * this.householderVectors[i][k2 - diagOffset];
                        }
                        double alpha2 = alpha / (diagonal[k2 - diagOffset] * hK[k2 - diagOffset]);
                        for (int i2 = k2; i2 < m; i2++) {
                            double[] dArr = ua[i2];
                            dArr[j] = dArr[j] + ((-alpha2) * this.householderVectors[i2][k2 - diagOffset]);
                        }
                    }
                }
            }
            if (diagOffset > 0) {
                ua[0][0] = 1.0d;
            }
            this.cachedU = MatrixUtils.createRealMatrix(ua);
        }
        return this.cachedU;
    }

    public RealMatrix getB() {
        if (this.cachedB == null) {
            int m = this.householderVectors.length;
            int n = this.householderVectors[0].length;
            double[][] ba = (double[][]) Array.newInstance(Double.TYPE, m, n);
            for (int i = 0; i < this.main.length; i++) {
                ba[i][i] = this.main[i];
                if (m < n) {
                    if (i > 0) {
                        ba[i][i - 1] = this.secondary[i - 1];
                    }
                } else if (i < this.main.length - 1) {
                    ba[i][i + 1] = this.secondary[i];
                }
            }
            this.cachedB = MatrixUtils.createRealMatrix(ba);
        }
        return this.cachedB;
    }

    public RealMatrix getV() {
        if (this.cachedV == null) {
            int m = this.householderVectors.length;
            int n = this.householderVectors[0].length;
            int p = this.main.length;
            int diagOffset = m >= n ? 1 : 0;
            double[] diagonal = m >= n ? this.secondary : this.main;
            double[][] va = (double[][]) Array.newInstance(Double.TYPE, n, n);
            for (int k = n - 1; k >= p; k--) {
                va[k][k] = 1.0d;
            }
            for (int k2 = p - 1; k2 >= diagOffset; k2--) {
                double[] hK = this.householderVectors[k2 - diagOffset];
                va[k2][k2] = 1.0d;
                if (hK[k2] != 0.0d) {
                    for (int j = k2; j < n; j++) {
                        double beta = 0.0d;
                        for (int i = k2; i < n; i++) {
                            beta -= va[i][j] * hK[i];
                        }
                        double beta2 = beta / (diagonal[k2 - diagOffset] * hK[k2]);
                        for (int i2 = k2; i2 < n; i2++) {
                            double[] dArr = va[i2];
                            dArr[j] = dArr[j] + ((-beta2) * hK[i2]);
                        }
                    }
                }
            }
            if (diagOffset > 0) {
                va[0][0] = 1.0d;
            }
            this.cachedV = MatrixUtils.createRealMatrix(va);
        }
        return this.cachedV;
    }

    /* access modifiers changed from: package-private */
    public double[][] getHouseholderVectorsRef() {
        return this.householderVectors;
    }

    /* access modifiers changed from: package-private */
    public double[] getMainDiagonalRef() {
        return this.main;
    }

    /* access modifiers changed from: package-private */
    public double[] getSecondaryDiagonalRef() {
        return this.secondary;
    }

    /* access modifiers changed from: package-private */
    public boolean isUpperBiDiagonal() {
        return this.householderVectors.length >= this.householderVectors[0].length;
    }

    private void transformToUpperBiDiagonal() {
        int m = this.householderVectors.length;
        int n = this.householderVectors[0].length;
        for (int k = 0; k < n; k++) {
            double xNormSqr = 0.0d;
            for (int i = k; i < m; i++) {
                double c = this.householderVectors[i][k];
                xNormSqr += c * c;
            }
            double[] hK = this.householderVectors[k];
            double a = hK[k] > 0.0d ? -FastMath.sqrt(xNormSqr) : FastMath.sqrt(xNormSqr);
            this.main[k] = a;
            if (a != 0.0d) {
                hK[k] = hK[k] - a;
                for (int j = k + 1; j < n; j++) {
                    double alpha = 0.0d;
                    for (int i2 = k; i2 < m; i2++) {
                        double[] hI = this.householderVectors[i2];
                        alpha -= hI[j] * hI[k];
                    }
                    double alpha2 = alpha / (this.householderVectors[k][k] * a);
                    for (int i3 = k; i3 < m; i3++) {
                        double[] hI2 = this.householderVectors[i3];
                        hI2[j] = hI2[j] - (hI2[k] * alpha2);
                    }
                }
            }
            if (k < n - 1) {
                double xNormSqr2 = 0.0d;
                for (int j2 = k + 1; j2 < n; j2++) {
                    double c2 = hK[j2];
                    xNormSqr2 += c2 * c2;
                }
                double b = hK[k + 1] > 0.0d ? -FastMath.sqrt(xNormSqr2) : FastMath.sqrt(xNormSqr2);
                this.secondary[k] = b;
                if (b != 0.0d) {
                    int i4 = k + 1;
                    hK[i4] = hK[i4] - b;
                    for (int i5 = k + 1; i5 < m; i5++) {
                        double[] hI3 = this.householderVectors[i5];
                        double beta = 0.0d;
                        for (int j3 = k + 1; j3 < n; j3++) {
                            beta -= hI3[j3] * hK[j3];
                        }
                        double beta2 = beta / (hK[k + 1] * b);
                        for (int j4 = k + 1; j4 < n; j4++) {
                            hI3[j4] = hI3[j4] - (hK[j4] * beta2);
                        }
                    }
                }
            }
        }
    }

    private void transformToLowerBiDiagonal() {
        int m = this.householderVectors.length;
        int n = this.householderVectors[0].length;
        for (int k = 0; k < m; k++) {
            double[] hK = this.householderVectors[k];
            double xNormSqr = 0.0d;
            for (int j = k; j < n; j++) {
                double c = hK[j];
                xNormSqr += c * c;
            }
            double a = hK[k] > 0.0d ? -FastMath.sqrt(xNormSqr) : FastMath.sqrt(xNormSqr);
            this.main[k] = a;
            if (a != 0.0d) {
                hK[k] = hK[k] - a;
                for (int i = k + 1; i < m; i++) {
                    double[] hI = this.householderVectors[i];
                    double alpha = 0.0d;
                    for (int j2 = k; j2 < n; j2++) {
                        alpha -= hI[j2] * hK[j2];
                    }
                    double alpha2 = alpha / (this.householderVectors[k][k] * a);
                    for (int j3 = k; j3 < n; j3++) {
                        hI[j3] = hI[j3] - (hK[j3] * alpha2);
                    }
                }
            }
            if (k < m - 1) {
                double[] hKp1 = this.householderVectors[k + 1];
                double xNormSqr2 = 0.0d;
                for (int i2 = k + 1; i2 < m; i2++) {
                    double c2 = this.householderVectors[i2][k];
                    xNormSqr2 += c2 * c2;
                }
                double b = hKp1[k] > 0.0d ? -FastMath.sqrt(xNormSqr2) : FastMath.sqrt(xNormSqr2);
                this.secondary[k] = b;
                if (b != 0.0d) {
                    hKp1[k] = hKp1[k] - b;
                    for (int j4 = k + 1; j4 < n; j4++) {
                        double beta = 0.0d;
                        for (int i3 = k + 1; i3 < m; i3++) {
                            double[] hI2 = this.householderVectors[i3];
                            beta -= hI2[j4] * hI2[k];
                        }
                        double beta2 = beta / (hKp1[k] * b);
                        for (int i4 = k + 1; i4 < m; i4++) {
                            double[] hI3 = this.householderVectors[i4];
                            hI3[j4] = hI3[j4] - (hI3[k] * beta2);
                        }
                    }
                }
            }
        }
    }
}
