package com.lavadip.skeye;

import com.lavadip.skeye.astro.LocationInSky;
import com.lavadip.skeye.util.Util;

public final class AlignManager {
    private static final int MAX_ALIGNS = 16;
    private static final double MIN_ANGLE_DIFF = Math.toRadians(1.0d);
    static final int xNegVector = 0;
    static final int yNegVector = 1;
    static final int zNegVector = 2;
    private final Vector3d[] adjTargetVectors = new Vector3d[16];
    private final double[] diffAngles = new double[16];
    private final Vector3d[] initialXVector = new Vector3d[16];
    private final Vector3d[] initialYVector = new Vector3d[16];
    private final Vector3d[] initialZVector = new Vector3d[16];
    volatile int numAligns = 0;
    private final LocationInSky[] targetObjs = new LocationInSky[16];
    private final Vector3d[] targetVectors = new Vector3d[16];

    public int getNumAligns() {
        return this.numAligns;
    }

    public void clearAll() {
        this.numAligns = 0;
    }

    public LocationInSky[] getTargetObjs() {
        return this.targetObjs;
    }

    public void addAlignment(float[] currRotation, LocationInSky targetObj) {
        updateAlignment(this.numAligns, currRotation, targetObj);
        this.numAligns++;
        updateTargets();
    }

    public void updateLastTarget(LocationInSky newTargetObj, float[] currRotation) {
        updateAlignment(this.numAligns - 1, currRotation, newTargetObj);
        updateTargets();
    }

    private void setTargetObj(int alignIndex, LocationInSky targetObj) {
        Vector3d targetVector = targetObj.getVector();
        targetVector.normalise();
        this.targetObjs[alignIndex] = targetObj;
        this.targetVectors[alignIndex] = targetVector;
    }

    private void updateAlignment(int alignIndex, float[] currRotation, LocationInSky targetObj) {
        setTargetObj(alignIndex, targetObj);
        this.initialZVector[alignIndex] = new Vector3d(currRotation, 2);
        this.initialXVector[alignIndex] = new Vector3d(currRotation, 0);
        this.initialYVector[alignIndex] = new Vector3d(currRotation, 1);
    }

    private void updateTargets() {
        if (this.numAligns > 0) {
            this.adjTargetVectors[0] = this.targetVectors[0];
            for (int i = 1; i < this.numAligns; i++) {
                this.adjTargetVectors[i] = transformOrientation(this.initialZVector[0], this.initialXVector[0], this.initialXVector[i], this.initialYVector[i], this.initialZVector[i], this.targetVectors[i]);
            }
        }
    }

    private static Vector3d transformOrientation(Vector3d currZVector, Vector3d currXVector, Vector3d initX, Vector3d initY, Vector3d initZ, Vector3d target) {
        double zDiffAngle = currZVector.angleBetweenMag(initZ);
        Vector3d zAlignAxis = currZVector.crossMult(initZ, true);
        Vector3d alignX = currXVector;
        if (!zAlignAxis.isZero()) {
            alignX = currXVector.rotate((double) ((float) zDiffAngle), zAlignAxis);
        }
        return target.rotate((double) ((float) (-(((double) (initY.angleBetweenMag(alignX) < 1.5707963267948966d ? 1 : -1)) * alignX.angleBetweenMag(initX)))), initZ).rotate((double) ((float) (-zDiffAngle)), zAlignAxis);
    }

    /* access modifiers changed from: package-private */
    public void getAlignedOrientationNew(float[] rotation, Orientation3d outCurrOrientation) {
        if (this.numAligns > 0) {
            Vector3d currZVector = new Vector3d(rotation, 2);
            Vector3d currXVector = new Vector3d(rotation, 0);
            Vector3d finalTargetVect = transformOrientation(currZVector, currXVector, this.initialXVector[0], this.initialYVector[0], this.initialZVector[0], findAvgTargetVector(currZVector, currXVector));
            Util.vectorToRotMatrix(finalTargetVect, rotation);
            outCurrOrientation.zDir = finalTargetVect;
            return;
        }
        outCurrOrientation.zDir = new Vector3d(rotation, 2);
    }

    private Vector3d findAvgTargetVector(Vector3d currZVector, Vector3d currXVector) {
        if (this.numAligns == 1) {
            return this.adjTargetVectors[0];
        }
        float x = 0.0f;
        float y = 0.0f;
        float z = 0.0f;
        double totalDiff = 0.0d;
        for (int i = 0; i < this.numAligns; i++) {
            double currDiff = Math.abs(Util.truncRad(currZVector.angleBetweenMag(this.initialZVector[i]))) + Math.abs(Util.truncRad(currXVector.angleBetweenMag(this.initialXVector[i])));
            double[] dArr = this.diffAngles;
            if (currDiff < MIN_ANGLE_DIFF) {
                currDiff = MIN_ANGLE_DIFF;
            }
            dArr[i] = 1.0d / currDiff;
            totalDiff += this.diffAngles[i];
        }
        for (int i2 = 0; i2 < this.numAligns; i2++) {
            double weight = this.diffAngles[i2] / totalDiff;
            x = (float) (((double) x) + (this.adjTargetVectors[i2].f16x * weight));
            y = (float) (((double) y) + (this.adjTargetVectors[i2].f17y * weight));
            z = (float) (((double) z) + (this.adjTargetVectors[i2].f18z * weight));
        }
        Vector3d avgVector = new Vector3d(x, y, z);
        avgVector.normalise();
        return avgVector;
    }

    public void removeLastAlignment() {
        this.targetObjs[this.numAligns - 1] = null;
        this.numAligns--;
        updateTargets();
    }

    public void removeAlignment(int alignIndex) {
        for (int i = alignIndex; i < this.numAligns; i++) {
            this.targetObjs[i] = null;
        }
        this.numAligns = alignIndex;
        updateTargets();
    }

    public boolean hasAligns() {
        return this.numAligns > 0;
    }
}
