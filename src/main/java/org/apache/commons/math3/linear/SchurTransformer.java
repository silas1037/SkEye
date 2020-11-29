package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Precision;

/* access modifiers changed from: package-private */
public class SchurTransformer {
    private static final int MAX_ITERATIONS = 100;
    private RealMatrix cachedP;
    private RealMatrix cachedPt;
    private RealMatrix cachedT;
    private final double epsilon = Precision.EPSILON;
    private final double[][] matrixP;
    private final double[][] matrixT;

    SchurTransformer(RealMatrix matrix) {
        if (!matrix.isSquare()) {
            throw new NonSquareMatrixException(matrix.getRowDimension(), matrix.getColumnDimension());
        }
        HessenbergTransformer transformer = new HessenbergTransformer(matrix);
        this.matrixT = transformer.getH().getData();
        this.matrixP = transformer.getP().getData();
        this.cachedT = null;
        this.cachedP = null;
        this.cachedPt = null;
        transform();
    }

    public RealMatrix getP() {
        if (this.cachedP == null) {
            this.cachedP = MatrixUtils.createRealMatrix(this.matrixP);
        }
        return this.cachedP;
    }

    public RealMatrix getPT() {
        if (this.cachedPt == null) {
            this.cachedPt = getP().transpose();
        }
        return this.cachedPt;
    }

    public RealMatrix getT() {
        if (this.cachedT == null) {
            this.cachedT = MatrixUtils.createRealMatrix(this.matrixT);
        }
        return this.cachedT;
    }

    private void transform() {
        double z;
        int n = this.matrixT.length;
        double norm = getNorm();
        ShiftInfo shift = new ShiftInfo();
        int iteration = 0;
        int iu = n - 1;
        while (iu >= 0) {
            int il = findSmallSubDiagonalElement(iu, norm);
            if (il == iu) {
                double[] dArr = this.matrixT[iu];
                dArr[iu] = dArr[iu] + shift.exShift;
                iu--;
                iteration = 0;
            } else if (il == iu - 1) {
                double p = (this.matrixT[iu - 1][iu - 1] - this.matrixT[iu][iu]) / 2.0d;
                double q = (p * p) + (this.matrixT[iu][iu - 1] * this.matrixT[iu - 1][iu]);
                double[] dArr2 = this.matrixT[iu];
                dArr2[iu] = dArr2[iu] + shift.exShift;
                double[] dArr3 = this.matrixT[iu - 1];
                int i = iu - 1;
                dArr3[i] = dArr3[i] + shift.exShift;
                if (q >= 0.0d) {
                    double z2 = FastMath.sqrt(FastMath.abs(q));
                    if (p >= 0.0d) {
                        z = z2 + p;
                    } else {
                        z = p - z2;
                    }
                    double x = this.matrixT[iu][iu - 1];
                    double s = FastMath.abs(x) + FastMath.abs(z);
                    double p2 = x / s;
                    double q2 = z / s;
                    double r = FastMath.sqrt((p2 * p2) + (q2 * q2));
                    double p3 = p2 / r;
                    double q3 = q2 / r;
                    for (int j = iu - 1; j < n; j++) {
                        double z3 = this.matrixT[iu - 1][j];
                        this.matrixT[iu - 1][j] = (q3 * z3) + (this.matrixT[iu][j] * p3);
                        this.matrixT[iu][j] = (this.matrixT[iu][j] * q3) - (p3 * z3);
                    }
                    for (int i2 = 0; i2 <= iu; i2++) {
                        double z4 = this.matrixT[i2][iu - 1];
                        this.matrixT[i2][iu - 1] = (q3 * z4) + (this.matrixT[i2][iu] * p3);
                        this.matrixT[i2][iu] = (this.matrixT[i2][iu] * q3) - (p3 * z4);
                    }
                    for (int i3 = 0; i3 <= n - 1; i3++) {
                        double z5 = this.matrixP[i3][iu - 1];
                        this.matrixP[i3][iu - 1] = (q3 * z5) + (this.matrixP[i3][iu] * p3);
                        this.matrixP[i3][iu] = (this.matrixP[i3][iu] * q3) - (p3 * z5);
                    }
                }
                iu -= 2;
                iteration = 0;
            } else {
                computeShift(il, iu, iteration, shift);
                iteration++;
                if (iteration > 100) {
                    throw new MaxCountExceededException(LocalizedFormats.CONVERGENCE_FAILED, 100, new Object[0]);
                }
                double[] hVec = new double[3];
                performDoubleQRStep(il, initQRStep(il, iu, shift, hVec), iu, shift, hVec);
            }
        }
    }

    private double getNorm() {
        double norm = 0.0d;
        for (int i = 0; i < this.matrixT.length; i++) {
            for (int j = FastMath.max(i - 1, 0); j < this.matrixT.length; j++) {
                norm += FastMath.abs(this.matrixT[i][j]);
            }
        }
        return norm;
    }

    private int findSmallSubDiagonalElement(int startIdx, double norm) {
        int l = startIdx;
        while (l > 0) {
            double s = FastMath.abs(this.matrixT[l - 1][l - 1]) + FastMath.abs(this.matrixT[l][l]);
            if (s == 0.0d) {
                s = norm;
            }
            if (FastMath.abs(this.matrixT[l][l - 1]) < this.epsilon * s) {
                break;
            }
            l--;
        }
        return l;
    }

    private void computeShift(int l, int idx, int iteration, ShiftInfo shift) {
        shift.f230x = this.matrixT[idx][idx];
        shift.f229w = 0.0d;
        shift.f231y = 0.0d;
        if (l < idx) {
            shift.f231y = this.matrixT[idx - 1][idx - 1];
            shift.f229w = this.matrixT[idx][idx - 1] * this.matrixT[idx - 1][idx];
        }
        if (iteration == 10) {
            shift.exShift += shift.f230x;
            for (int i = 0; i <= idx; i++) {
                double[] dArr = this.matrixT[i];
                dArr[i] = dArr[i] - shift.f230x;
            }
            double s = FastMath.abs(this.matrixT[idx][idx - 1]) + FastMath.abs(this.matrixT[idx - 1][idx - 2]);
            shift.f230x = 0.75d * s;
            shift.f231y = 0.75d * s;
            shift.f229w = -0.4375d * s * s;
        }
        if (iteration == 30) {
            double s2 = (shift.f231y - shift.f230x) / 2.0d;
            double s3 = (s2 * s2) + shift.f229w;
            if (s3 > 0.0d) {
                double s4 = FastMath.sqrt(s3);
                if (shift.f231y < shift.f230x) {
                    s4 = -s4;
                }
                double s5 = shift.f230x - (shift.f229w / (((shift.f231y - shift.f230x) / 2.0d) + s4));
                for (int i2 = 0; i2 <= idx; i2++) {
                    double[] dArr2 = this.matrixT[i2];
                    dArr2[i2] = dArr2[i2] - s5;
                }
                shift.exShift += s5;
                shift.f229w = 0.964d;
                shift.f231y = 0.964d;
                shift.f230x = 0.964d;
            }
        }
    }

    private int initQRStep(int il, int iu, ShiftInfo shift, double[] hVec) {
        int im = iu - 2;
        while (im >= il) {
            double z = this.matrixT[im][im];
            double r = shift.f230x - z;
            double s = shift.f231y - z;
            hVec[0] = (((r * s) - shift.f229w) / this.matrixT[im + 1][im]) + this.matrixT[im][im + 1];
            hVec[1] = ((this.matrixT[im + 1][im + 1] - z) - r) - s;
            hVec[2] = this.matrixT[im + 2][im + 1];
            if (im != il) {
                if (FastMath.abs(this.matrixT[im][im - 1]) * (FastMath.abs(hVec[1]) + FastMath.abs(hVec[2])) < this.epsilon * FastMath.abs(hVec[0]) * (FastMath.abs(this.matrixT[im - 1][im - 1]) + FastMath.abs(z) + FastMath.abs(this.matrixT[im + 1][im + 1]))) {
                    break;
                }
                im--;
            } else {
                break;
            }
        }
        return im;
    }

    private void performDoubleQRStep(int il, int im, int iu, ShiftInfo shift, double[] hVec) {
        int n = this.matrixT.length;
        double p = hVec[0];
        double q = hVec[1];
        double r = hVec[2];
        int k = im;
        while (k <= iu - 1) {
            boolean notlast = k != iu + -1;
            if (k != im) {
                p = this.matrixT[k][k - 1];
                q = this.matrixT[k + 1][k - 1];
                r = notlast ? this.matrixT[k + 2][k - 1] : 0.0d;
                shift.f230x = FastMath.abs(p) + FastMath.abs(q) + FastMath.abs(r);
                if (Precision.equals(shift.f230x, 0.0d, this.epsilon)) {
                    k++;
                } else {
                    p /= shift.f230x;
                    q /= shift.f230x;
                    r /= shift.f230x;
                }
            }
            double s = FastMath.sqrt((p * p) + (q * q) + (r * r));
            if (p < 0.0d) {
                s = -s;
            }
            if (s != 0.0d) {
                if (k != im) {
                    this.matrixT[k][k - 1] = (-s) * shift.f230x;
                } else if (il != im) {
                    this.matrixT[k][k - 1] = -this.matrixT[k][k - 1];
                }
                p += s;
                shift.f230x = p / s;
                shift.f231y = q / s;
                double z = r / s;
                q /= p;
                r /= p;
                for (int j = k; j < n; j++) {
                    p = this.matrixT[k][j] + (this.matrixT[k + 1][j] * q);
                    if (notlast) {
                        p += this.matrixT[k + 2][j] * r;
                        double[] dArr = this.matrixT[k + 2];
                        dArr[j] = dArr[j] - (p * z);
                    }
                    double[] dArr2 = this.matrixT[k];
                    dArr2[j] = dArr2[j] - (shift.f230x * p);
                    double[] dArr3 = this.matrixT[k + 1];
                    dArr3[j] = dArr3[j] - (shift.f231y * p);
                }
                for (int i = 0; i <= FastMath.min(iu, k + 3); i++) {
                    p = (shift.f230x * this.matrixT[i][k]) + (shift.f231y * this.matrixT[i][k + 1]);
                    if (notlast) {
                        p += this.matrixT[i][k + 2] * z;
                        double[] dArr4 = this.matrixT[i];
                        int i2 = k + 2;
                        dArr4[i2] = dArr4[i2] - (p * r);
                    }
                    double[] dArr5 = this.matrixT[i];
                    dArr5[k] = dArr5[k] - p;
                    double[] dArr6 = this.matrixT[i];
                    int i3 = k + 1;
                    dArr6[i3] = dArr6[i3] - (p * q);
                }
                int high = this.matrixT.length - 1;
                for (int i4 = 0; i4 <= high; i4++) {
                    p = (shift.f230x * this.matrixP[i4][k]) + (shift.f231y * this.matrixP[i4][k + 1]);
                    if (notlast) {
                        p += this.matrixP[i4][k + 2] * z;
                        double[] dArr7 = this.matrixP[i4];
                        int i5 = k + 2;
                        dArr7[i5] = dArr7[i5] - (p * r);
                    }
                    double[] dArr8 = this.matrixP[i4];
                    dArr8[k] = dArr8[k] - p;
                    double[] dArr9 = this.matrixP[i4];
                    int i6 = k + 1;
                    dArr9[i6] = dArr9[i6] - (p * q);
                }
            }
            k++;
        }
        for (int i7 = im + 2; i7 <= iu; i7++) {
            this.matrixT[i7][i7 - 2] = 0.0d;
            if (i7 > im + 2) {
                this.matrixT[i7][i7 - 3] = 0.0d;
            }
        }
    }

    /* access modifiers changed from: private */
    public static class ShiftInfo {
        double exShift;

        /* renamed from: w */
        double f229w;

        /* renamed from: x */
        double f230x;

        /* renamed from: y */
        double f231y;

        private ShiftInfo() {
        }
    }
}
