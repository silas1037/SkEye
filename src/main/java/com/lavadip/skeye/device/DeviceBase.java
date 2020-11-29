package com.lavadip.skeye.device;

public abstract class DeviceBase implements Device {
    protected final float[] bufferedAcceleration = {0.0f, 0.0f, 9.0f};
    protected final float[] bufferedMagneticField = {0.0f, 0.0f, 42.0f};
    protected final float[] bufferedRotVectors = {1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f};
    protected final float[] bufferedRotVectorsTemp = {1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f};
    protected float fieldStrength = 45.0f;
    protected boolean usingRotVector = false;

    @Override // com.lavadip.skeye.device.Device
    public float getFieldStrength() {
        return this.fieldStrength;
    }

    @Override // com.lavadip.skeye.device.Device
    public boolean getCurrRotationMatrixNoDec(float[] currRotation) {
        if (this.usingRotVector) {
            float[] brm = this.bufferedRotVectors;
            float lenx = (float) Math.sqrt((double) ((brm[0] * brm[0]) + (brm[1] * brm[1]) + (brm[2] * brm[2])));
            currRotation[0] = brm[0] / lenx;
            currRotation[4] = brm[1] / lenx;
            currRotation[8] = brm[2] / lenx;
            float leny = (float) Math.sqrt((double) ((brm[3] * brm[3]) + (brm[4] * brm[4]) + (brm[5] * brm[5])));
            currRotation[1] = brm[3] / leny;
            currRotation[5] = brm[4] / leny;
            currRotation[9] = brm[5] / leny;
            float x = currRotation[0];
            float y = currRotation[4];
            float z = currRotation[8];
            float tx = currRotation[1];
            float ty = currRotation[5];
            float tz = currRotation[9];
            currRotation[2] = (y * tz) - (z * ty);
            currRotation[6] = (z * tx) - (x * tz);
            currRotation[10] = (x * ty) - (y * tx);
            float lenz = (float) Math.sqrt((double) ((currRotation[2] * currRotation[2]) + (currRotation[6] * currRotation[6]) + (currRotation[10] * currRotation[10])));
            currRotation[2] = currRotation[2] / lenz;
            currRotation[6] = currRotation[6] / lenz;
            currRotation[10] = currRotation[10] / lenz;
            currRotation[14] = 0.0f;
            currRotation[13] = 0.0f;
            currRotation[12] = 0.0f;
            currRotation[11] = 0.0f;
            currRotation[7] = 0.0f;
            currRotation[3] = 0.0f;
            currRotation[15] = 1.0f;
            return true;
        }
        getRotationMatrix(currRotation, this.bufferedAcceleration, this.bufferedMagneticField);
        return true;
    }

    private static void getRotationMatrix(float[] R, float[] acc, float[] mag) {
        float Ax = acc[0];
        float Ay = acc[1];
        float Az = acc[2];
        float Ex = mag[0];
        float Ey = mag[1];
        float Ez = mag[2];
        float Hx = (Ey * Az) - (Ez * Ay);
        float Hy = (Ez * Ax) - (Ex * Az);
        float Hz = (Ex * Ay) - (Ey * Ax);
        float invH = 1.0f / ((float) Math.sqrt((double) (((Hx * Hx) + (Hy * Hy)) + (Hz * Hz))));
        float Hx2 = Hx * invH;
        float Hy2 = Hy * invH;
        float Hz2 = Hz * invH;
        float invA = 1.0f / ((float) Math.sqrt((double) (((Ax * Ax) + (Ay * Ay)) + (Az * Az))));
        float Ax2 = Ax * invA;
        float Ay2 = Ay * invA;
        float Az2 = Az * invA;
        R[0] = Hx2;
        R[1] = Hy2;
        R[2] = Hz2;
        R[3] = 0.0f;
        R[4] = (Ay2 * Hz2) - (Az2 * Hy2);
        R[5] = (Az2 * Hx2) - (Ax2 * Hz2);
        R[6] = (Ax2 * Hy2) - (Ay2 * Hx2);
        R[7] = 0.0f;
        R[8] = Ax2;
        R[9] = Ay2;
        R[10] = Az2;
        R[11] = 0.0f;
        R[12] = 0.0f;
        R[13] = 0.0f;
        R[14] = 0.0f;
        R[15] = 1.0f;
    }

    public String toString() {
        return getName();
    }
}
