package com.lavadip.skeye;

public final class Vector2d {

    /* renamed from: x */
    public double f14x;

    /* renamed from: y */
    public double f15y;

    public Vector2d(double x, double y) {
        this.f14x = x;
        this.f15y = y;
    }

    public Vector2d(float x, float y) {
        this.f14x = (double) x;
        this.f15y = (double) y;
    }

    public void setXYZ(double x, double y) {
        this.f14x = x;
        this.f15y = y;
    }

    public void setXYZ(double[] points, int pointOffset) {
        this.f14x = points[pointOffset];
        this.f15y = points[pointOffset + 1];
    }

    public void setXYZ(float[] points, int pointOffset) {
        this.f14x = (double) points[pointOffset];
        this.f15y = (double) points[pointOffset + 1];
    }

    public Vector2d(Vector2d that) {
        setXYZ(that.f14x, that.f15y);
    }

    public Vector2d(double x, double y, boolean normalise) {
        if (normalise) {
            double length = Math.sqrt((x * x) + (y * y));
            this.f14x = x / length;
            this.f15y = y / length;
            return;
        }
        this.f14x = x;
        this.f15y = y;
    }

    public void normalise() {
        double length = length();
        if (length != 0.0d) {
            this.f14x /= length;
            this.f15y /= length;
        }
    }

    public double dotMult(Vector2d b) {
        return (this.f14x * b.f14x) + (this.f15y * b.f15y);
    }

    public double length() {
        return Math.sqrt((this.f14x * this.f14x) + (this.f15y * this.f15y));
    }

    public double angleBetweenMag(Vector2d b) {
        double antiAngle = dotMult(b);
        if (antiAngle < -1.0d) {
            antiAngle = -1.0d;
        } else if (antiAngle > 1.0d) {
            antiAngle = 1.0d;
        }
        return Math.acos(antiAngle);
    }

    public boolean isZero() {
        return this.f14x == 0.0d && this.f15y == 0.0d;
    }

    public String toString() {
        return String.format("V[%.8f, %.8f] len:%.8f", Double.valueOf(this.f14x), Double.valueOf(this.f15y), Double.valueOf(length()));
    }

    /* access modifiers changed from: package-private */
    public void copyFrom(Vector2d other) {
        this.f14x = other.f14x;
        this.f15y = other.f15y;
    }

    /* access modifiers changed from: package-private */
    public void scalarMultiply(double m) {
        this.f14x *= m;
        this.f15y *= m;
    }
}
