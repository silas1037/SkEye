package com.lavadip.skeye.util;

import android.opengl.Matrix;
import com.lavadip.skeye.Vector3d;

final class Quaternion {
    private static final double QUATERNION_TRACE_ZERO_TOLERANCE = 1.0E-8d;

    /* renamed from: w */
    private final double f94w;

    /* renamed from: x */
    private final double f95x;

    /* renamed from: y */
    private final double f96y;

    /* renamed from: z */
    private final double f97z;

    public Quaternion(double x0, double x1, double x2, double x3) {
        this.f94w = x0;
        this.f95x = x1;
        this.f96y = x2;
        this.f97z = x3;
    }

    public Quaternion(double angle, Vector3d axis) {
        double sinA = Math.sin(angle / 2.0d);
        this.f94w = Math.cos(angle / 2.0d);
        this.f95x = axis.f16x * sinA;
        this.f96y = axis.f17y * sinA;
        this.f97z = axis.f18z * sinA;
    }

    public Quaternion(float[] origMatrix, boolean transpose) {
        float[] matrix;
        int biggest;
        if (transpose) {
            float[] matrixT = new float[16];
            Matrix.transposeM(matrixT, 0, origMatrix, 0);
            matrix = matrixT;
        } else {
            matrix = origMatrix;
        }
        double trace = (double) (matrix[0] + matrix[5] + matrix[10]);
        if (trace > 1.0E-8d) {
            double s = Math.sqrt(1.0d + trace);
            this.f94w = 0.5d * s;
            double s2 = 0.5d / s;
            this.f95x = ((double) (matrix[9] - matrix[6])) * s2;
            this.f96y = ((double) (matrix[2] - matrix[8])) * s2;
            this.f97z = ((double) (matrix[4] - matrix[1])) * s2;
            return;
        }
        if (matrix[0] > matrix[5]) {
            if (matrix[10] > matrix[0]) {
                biggest = 2;
            } else {
                biggest = 0;
            }
        } else if (matrix[10] > matrix[0]) {
            biggest = 2;
        } else {
            biggest = 1;
        }
        switch (biggest) {
            case 0:
                double s3 = Math.sqrt((double) ((matrix[0] - (matrix[5] + matrix[10])) + 1.0f));
                if (s3 > 1.0E-8d) {
                    this.f95x = 0.5d * s3;
                    double s4 = 0.5d / s3;
                    this.f94w = ((double) (matrix[9] - matrix[6])) * s4;
                    this.f96y = ((double) (matrix[1] + matrix[4])) * s4;
                    this.f97z = ((double) (matrix[2] + matrix[8])) * s4;
                    return;
                }
                double s5 = Math.sqrt((double) ((matrix[10] - (matrix[0] + matrix[5])) + 1.0f));
                if (s5 > 1.0E-8d) {
                    this.f97z = 0.5d * s5;
                    double s6 = 0.5d / s5;
                    this.f94w = ((double) (matrix[4] - matrix[1])) * s6;
                    this.f95x = ((double) (matrix[8] + matrix[2])) * s6;
                    this.f96y = ((double) (matrix[9] + matrix[6])) * s6;
                    return;
                }
                double s7 = Math.sqrt((double) ((matrix[5] - (matrix[10] + matrix[0])) + 1.0f));
                if (s7 > 1.0E-8d) {
                    this.f96y = 0.5d * s7;
                    double s8 = 0.5d / s7;
                    this.f94w = ((double) (matrix[2] - matrix[8])) * s8;
                    this.f97z = ((double) (matrix[6] + matrix[9])) * s8;
                    this.f95x = ((double) (matrix[4] + matrix[1])) * s8;
                    return;
                }
                this.f97z = 0.0d;
                this.f96y = 0.0d;
                this.f95x = 0.0d;
                this.f94w = 0.0d;
                return;
            case 1:
                double s9 = Math.sqrt((double) ((matrix[5] - (matrix[10] + matrix[0])) + 1.0f));
                if (s9 > 1.0E-8d) {
                    this.f96y = 0.5d * s9;
                    double s10 = 0.5d / s9;
                    this.f94w = ((double) (matrix[2] - matrix[8])) * s10;
                    this.f97z = ((double) (matrix[6] + matrix[9])) * s10;
                    this.f95x = ((double) (matrix[4] + matrix[1])) * s10;
                    return;
                }
                double s11 = Math.sqrt((double) ((matrix[10] - (matrix[0] + matrix[5])) + 1.0f));
                if (s11 > 1.0E-8d) {
                    this.f97z = 0.5d * s11;
                    double s12 = 0.5d / s11;
                    this.f94w = ((double) (matrix[4] - matrix[1])) * s12;
                    this.f95x = ((double) (matrix[8] + matrix[2])) * s12;
                    this.f96y = ((double) (matrix[9] + matrix[6])) * s12;
                    return;
                }
                double s13 = Math.sqrt((double) ((matrix[0] - (matrix[5] + matrix[10])) + 1.0f));
                if (s13 > 1.0E-8d) {
                    this.f95x = 0.5d * s13;
                    double s14 = 0.5d / s13;
                    this.f94w = ((double) (matrix[9] - matrix[6])) * s14;
                    this.f96y = ((double) (matrix[1] + matrix[4])) * s14;
                    this.f97z = ((double) (matrix[2] + matrix[8])) * s14;
                    return;
                }
                this.f97z = 0.0d;
                this.f96y = 0.0d;
                this.f95x = 0.0d;
                this.f94w = 0.0d;
                return;
            case 2:
                double s15 = Math.sqrt((double) ((matrix[10] - (matrix[0] + matrix[5])) + 1.0f));
                if (s15 > 1.0E-8d) {
                    this.f97z = 0.5d * s15;
                    double s16 = 0.5d / s15;
                    this.f94w = ((double) (matrix[4] - matrix[1])) * s16;
                    this.f95x = ((double) (matrix[8] + matrix[2])) * s16;
                    this.f96y = ((double) (matrix[9] + matrix[6])) * s16;
                    return;
                }
                double s17 = Math.sqrt((double) ((matrix[0] - (matrix[5] + matrix[10])) + 1.0f));
                if (s17 > 1.0E-8d) {
                    this.f95x = 0.5d * s17;
                    double s18 = 0.5d / s17;
                    this.f94w = ((double) (matrix[9] - matrix[6])) * s18;
                    this.f96y = ((double) (matrix[1] + matrix[4])) * s18;
                    this.f97z = ((double) (matrix[2] + matrix[8])) * s18;
                    return;
                }
                double s19 = Math.sqrt((double) ((matrix[5] - (matrix[10] + matrix[0])) + 1.0f));
                if (s19 > 1.0E-8d) {
                    this.f96y = 0.5d * s19;
                    double s20 = 0.5d / s19;
                    this.f94w = ((double) (matrix[2] - matrix[8])) * s20;
                    this.f97z = ((double) (matrix[6] + matrix[9])) * s20;
                    this.f95x = ((double) (matrix[4] + matrix[1])) * s20;
                    return;
                }
                this.f97z = 0.0d;
                this.f96y = 0.0d;
                this.f95x = 0.0d;
                this.f94w = 0.0d;
                return;
            default:
                this.f97z = 0.0d;
                this.f96y = 0.0d;
                this.f95x = 0.0d;
                this.f94w = 0.0d;
                return;
        }
    }

    public String toString() {
        return String.format("[%.2f %.2fi %.2fj %.2fk] Len:%.2f", Double.valueOf(this.f94w), Double.valueOf(this.f95x), Double.valueOf(this.f96y), Double.valueOf(this.f97z), Double.valueOf(norm()));
    }

    public double norm() {
        return Math.sqrt((this.f94w * this.f94w) + (this.f95x * this.f95x) + (this.f96y * this.f96y) + (this.f97z * this.f97z));
    }

    public Quaternion conjugate() {
        return new Quaternion(this.f94w, -this.f95x, -this.f96y, -this.f97z);
    }

    public Quaternion plus(Quaternion b) {
        return new Quaternion(this.f94w + b.f94w, this.f95x + b.f95x, this.f96y + b.f96y, this.f97z + b.f97z);
    }

    public Quaternion times(Quaternion b) {
        return new Quaternion((((this.f94w * b.f94w) - (this.f95x * b.f95x)) - (this.f96y * b.f96y)) - (this.f97z * b.f97z), (((this.f94w * b.f95x) + (this.f95x * b.f94w)) + (this.f96y * b.f97z)) - (this.f97z * b.f96y), ((this.f94w * b.f96y) - (this.f95x * b.f97z)) + (this.f96y * b.f94w) + (this.f97z * b.f95x), (((this.f94w * b.f97z) + (this.f95x * b.f96y)) - (this.f96y * b.f95x)) + (this.f97z * b.f94w));
    }

    public Quaternion inverse() {
        double d = (this.f94w * this.f94w) + (this.f95x * this.f95x) + (this.f96y * this.f96y) + (this.f97z * this.f97z);
        return new Quaternion(this.f94w / d, (-this.f95x) / d, (-this.f96y) / d, (-this.f97z) / d);
    }

    public Quaternion divides(Quaternion b) {
        return inverse().times(b);
    }

    public float[] getMatrix(boolean transpose) {
        double x2 = this.f95x * this.f95x;
        double y2 = this.f96y * this.f96y;
        double z2 = this.f97z * this.f97z;
        if (transpose) {
            return new float[]{(float) (1.0d - (2.0d * (y2 + z2))), (float) (2.0d * ((this.f95x * this.f96y) + (this.f94w * this.f97z))), (float) (2.0d * ((this.f95x * this.f97z) - (this.f94w * this.f96y))), 0.0f, (float) (2.0d * ((this.f95x * this.f96y) - (this.f94w * this.f97z))), (float) (1.0d - (2.0d * (x2 + z2))), (float) (2.0d * ((this.f96y * this.f97z) + (this.f94w * this.f95x))), 0.0f, (float) (2.0d * ((this.f95x * this.f97z) + (this.f94w * this.f96y))), (float) (2.0d * ((this.f96y * this.f97z) - (this.f94w * this.f95x))), (float) (1.0d - (2.0d * (x2 + y2))), 0.0f, 0.0f, 0.0f, 0.0f, 1.0f};
        }
        return new float[]{(float) (1.0d - (2.0d * (y2 + z2))), (float) (2.0d * ((this.f95x * this.f96y) - (this.f94w * this.f97z))), (float) (2.0d * ((this.f95x * this.f97z) + (this.f94w * this.f96y))), 0.0f, (float) (2.0d * ((this.f95x * this.f96y) + (this.f94w * this.f97z))), (float) (1.0d - (2.0d * (x2 + z2))), (float) (2.0d * ((this.f96y * this.f97z) - (this.f94w * this.f95x))), 0.0f, (float) (2.0d * ((this.f95x * this.f97z) - (this.f94w * this.f96y))), (float) (2.0d * ((this.f96y * this.f97z) + (this.f94w * this.f95x))), (float) (1.0d - (2.0d * (x2 + y2))), 0.0f, 0.0f, 0.0f, 0.0f, 1.0f};
    }
}
