package com.lavadip.skeye;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

public final class Vector3d {

    /* renamed from: x */
    public double f16x;

    /* renamed from: y */
    public double f17y;

    /* renamed from: z */
    public double f18z;

    public Vector3d(double x, double y, double z) {
        this.f16x = x;
        this.f17y = y;
        this.f18z = z;
    }

    public Vector3d(float x, float y, float z) {
        this.f16x = (double) x;
        this.f17y = (double) y;
        this.f18z = (double) z;
    }

    public void setXYZ(double x, double y, double z) {
        this.f16x = x;
        this.f17y = y;
        this.f18z = z;
    }

    public void setXYZ(DoubleBuffer points, int pointOffset) {
        this.f16x = points.get(pointOffset);
        this.f17y = points.get(pointOffset + 1);
        this.f18z = points.get(pointOffset + 2);
    }

    public void setXYZ(FloatBuffer points, int pointOffset) {
        this.f16x = (double) points.get(pointOffset);
        this.f17y = (double) points.get(pointOffset + 1);
        this.f18z = (double) points.get(pointOffset + 2);
    }

    public void putXYZ(FloatBuffer points) {
        points.put((float) this.f16x);
        points.put((float) this.f17y);
        points.put((float) this.f18z);
    }

    public void putXYZ(FloatBuffer points, double scale) {
        points.put((float) (this.f16x * scale));
        points.put((float) (this.f17y * scale));
        points.put((float) (this.f18z * scale));
    }

    public void putXYZ(float[] points, int destIndex, double scale) {
        points[destIndex] = (float) (this.f16x * scale);
        points[destIndex + 1] = (float) (this.f17y * scale);
        points[destIndex + 2] = (float) (this.f18z * scale);
    }

    public void setXYZ(double[] points, int pointOffset) {
        this.f16x = points[pointOffset];
        this.f17y = points[pointOffset + 1];
        this.f18z = points[pointOffset + 2];
    }

    public void setXYZ(float[] points, int pointOffset) {
        this.f16x = (double) points[pointOffset];
        this.f17y = (double) points[pointOffset + 1];
        this.f18z = (double) points[pointOffset + 2];
    }

    public Vector3d(double[] rotationMatrix, int axis) {
        this.f16x = -rotationMatrix[axis];
        this.f17y = -rotationMatrix[axis + 4];
        this.f18z = -rotationMatrix[axis + 8];
        normalise();
    }

    public Vector3d(float[] rotationMatrix, int axis) {
        this.f16x = (double) (-rotationMatrix[axis]);
        this.f17y = (double) (-rotationMatrix[axis + 4]);
        this.f18z = (double) (-rotationMatrix[axis + 8]);
        normalise();
    }

    public Vector3d() {
    }

    public Vector3d(Vector3d that) {
        setXYZ(that.f16x, that.f17y, that.f18z);
    }

    public Vector3d(double x, double y, double z, boolean normalise) {
        if (normalise) {
            double length = Math.sqrt((x * x) + (y * y) + (z * z));
            this.f16x = x / length;
            this.f17y = y / length;
            this.f18z = z / length;
            return;
        }
        this.f16x = x;
        this.f17y = y;
        this.f18z = z;
    }

    public void crossMult(Vector3d b, boolean normalised, Vector3d outResult) {
        outResult.f16x = (this.f17y * b.f18z) - (this.f18z * b.f17y);
        outResult.f17y = (this.f18z * b.f16x) - (this.f16x * b.f18z);
        outResult.f18z = (this.f16x * b.f17y) - (this.f17y * b.f16x);
        if (normalised) {
            outResult.normalise();
        }
    }

    public Vector3d crossMult(Vector3d b, boolean normalised) {
        Vector3d result = new Vector3d();
        crossMult(b, normalised, result);
        return result;
    }

    public void normalise() {
        double length = length();
        if (length != 0.0d) {
            this.f16x /= length;
            this.f17y /= length;
            this.f18z /= length;
        }
    }

    public Vector3d normalised() {
        Vector3d res = new Vector3d(this);
        res.normalise();
        return res;
    }

    public double dotMult(Vector3d b) {
        return (this.f16x * b.f16x) + (this.f17y * b.f17y) + (this.f18z * b.f18z);
    }

    public double length() {
        return Math.sqrt((this.f16x * this.f16x) + (this.f17y * this.f17y) + (this.f18z * this.f18z));
    }

    public double angleBetweenMag(Vector3d b) {
        double antiAngle = dotMult(b);
        if (antiAngle < -1.0d) {
            antiAngle = -1.0d;
        } else if (antiAngle > 1.0d) {
            antiAngle = 1.0d;
        }
        return Math.acos(antiAngle);
    }

    public boolean isZero() {
        return this.f16x == 0.0d && this.f17y == 0.0d && this.f18z == 0.0d;
    }

    public String toString() {
        return String.format("V[%.8f, %.8f, %.8f] len:%.8f", Double.valueOf(this.f16x), Double.valueOf(this.f17y), Double.valueOf(this.f18z), Double.valueOf(length()));
    }

    public void rotate(double angle, Vector3d axis, Vector3d result) {
        double cosA = Math.cos(angle);
        double sinA = Math.sin(angle);
        double u = axis.f16x;
        double v = axis.f17y;
        double w = axis.f18z;
        double u2 = u * u;
        double v2 = v * v;
        double w2 = w * w;
        double c1 = (this.f16x * u) + (this.f17y * v) + (this.f18z * w);
        result.f16x = (u * c1) + (((this.f16x * (v2 + w2)) - (((this.f17y * v) + (this.f18z * w)) * u)) * cosA) + (((this.f18z * v) - (this.f17y * w)) * sinA);
        result.f17y = (v * c1) + (((this.f17y * (u2 + w2)) - (((this.f16x * u) + (this.f18z * w)) * v)) * cosA) + (((this.f16x * w) - (this.f18z * u)) * sinA);
        result.f18z = (w * c1) + (((this.f18z * (v2 + u2)) - (((this.f17y * v) + (this.f16x * u)) * w)) * cosA) + (((this.f17y * u) - (this.f16x * v)) * sinA);
    }

    public Vector3d rotateAboutXaxis(double angle) {
        double cosA = Math.cos(angle);
        double sinA = Math.sin(angle);
        return new Vector3d(this.f16x, (this.f17y * cosA) - (this.f18z * sinA), (this.f18z * cosA) + (this.f17y * sinA));
    }

    public Vector3d rotateAboutYaxis(double angle) {
        double cosA = Math.cos(angle);
        double sinA = Math.sin(angle);
        return new Vector3d((this.f16x * cosA) + (this.f18z * sinA), this.f17y, (this.f18z * cosA) - (this.f16x * sinA));
    }

    public Vector3d rotateAboutZaxis(double angle) {
        double cosA = Math.cos(angle);
        double sinA = Math.sin(angle);
        return new Vector3d((this.f16x * cosA) - (this.f17y * sinA), (this.f17y * cosA) + (this.f16x * sinA), this.f18z);
    }

    public Vector3d rotate(double angle, Vector3d axis) {
        Vector3d result = new Vector3d();
        rotate(angle, axis, result);
        return result;
    }

    public void rotateAwayFrom(double angle, Vector3d fromVec, Vector3d result) {
        double axisx = (fromVec.f17y * this.f18z) - (fromVec.f18z * this.f17y);
        double axisy = (fromVec.f18z * this.f16x) - (fromVec.f16x * this.f18z);
        double axisz = (fromVec.f16x * this.f17y) - (fromVec.f17y * this.f16x);
        double length = Math.sqrt((axisx * axisx) + (axisy * axisy) + (axisz * axisz));
        double cosA = Math.cos(angle);
        double sinA = Math.sin(angle);
        double u = axisx / length;
        double v = axisy / length;
        double w = axisz / length;
        result.f16x = (this.f16x * cosA) + (((this.f18z * v) - (this.f17y * w)) * sinA);
        result.f17y = (this.f17y * cosA) + (((this.f16x * w) - (this.f18z * u)) * sinA);
        result.f18z = (this.f18z * cosA) + (((this.f17y * u) - (this.f16x * v)) * sinA);
    }

    public Vector3d rotateAwayFrom(double angle, Vector3d fromVec) {
        Vector3d result = new Vector3d();
        rotateAwayFrom(angle, fromVec, result);
        return result;
    }

    /* access modifiers changed from: package-private */
    public void copyFrom(Vector3d other) {
        this.f16x = other.f16x;
        this.f17y = other.f17y;
        this.f18z = other.f18z;
    }

    public void scalarMultiplyInPlace(double m) {
        this.f16x *= m;
        this.f17y *= m;
        this.f18z *= m;
    }

    public Vector3d scalarMultiply(double m) {
        return new Vector3d(this.f16x * m, this.f17y * m, this.f18z * m);
    }

    public Vector3d add(Vector3d that) {
        return new Vector3d(this.f16x + that.f16x, this.f17y + that.f17y, this.f18z + that.f18z);
    }

    public Vector3d sub(Vector3d that) {
        return new Vector3d(this.f16x - that.f16x, this.f17y - that.f17y, this.f18z - that.f18z);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Vector3d other = (Vector3d) obj;
        if (Double.doubleToLongBits(this.f16x) != Double.doubleToLongBits(other.f16x)) {
            return false;
        }
        if (Double.doubleToLongBits(this.f17y) != Double.doubleToLongBits(other.f17y)) {
            return false;
        }
        return Double.doubleToLongBits(this.f18z) == Double.doubleToLongBits(other.f18z);
    }

    public String serializeToString() {
        return String.format("%.2f,%.2f,%.2f", Double.valueOf(this.f16x), Double.valueOf(this.f17y), Double.valueOf(this.f18z));
    }
}
