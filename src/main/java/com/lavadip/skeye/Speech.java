package com.lavadip.skeye;

/* access modifiers changed from: package-private */
public final class Speech {
    private long lastRateCheck = 0;
    private Vector3d prevZDir = null;

    Speech() {
    }

    /* access modifiers changed from: package-private */
    public double getRateOfChange(Vector3d zDir) {
        long now = System.currentTimeMillis();
        if (this.prevZDir != null) {
            double changeRate = Math.toDegrees(zDir.angleBetweenMag(this.prevZDir)) / ((double) (now - this.lastRateCheck));
            this.lastRateCheck = now;
            if (changeRate <= 0.004d) {
                return changeRate;
            }
            this.prevZDir = new Vector3d(zDir);
            return changeRate;
        }
        this.lastRateCheck = now;
        this.prevZDir = new Vector3d(zDir);
        return Double.NEGATIVE_INFINITY;
    }

    public static String makeSpokenGuide(double changeRate, Vector3d vec, Vector3d zDir) {
        if (changeRate == Double.NEGATIVE_INFINITY || changeRate > 0.004d) {
            return null;
        }
        Vector3d vecXY = new Vector3d(vec.f16x, vec.f17y, 0.0d, true);
        Vector3d zDirXY = new Vector3d(zDir.f16x, zDir.f17y, 0.0d, true);
        double azmDiff = Math.toDegrees(vecXY.angleBetweenMag(zDirXY));
        if (azmDiff > 3.0d) {
            return makeSignedDirection(vecXY, zDirXY, azmDiff);
        }
        int altDiffDegrees = (int) Math.toDegrees(Math.abs(Math.asin(vec.f18z) - Math.asin(zDir.f18z)));
        if (altDiffDegrees > 0) {
            Object[] objArr = new Object[2];
            objArr[0] = Integer.valueOf(altDiffDegrees);
            objArr[1] = vec.f18z > zDir.f18z ? "up" : "down";
            return String.format("%d %s.", objArr);
        } else if (azmDiff <= 0.1d) {
            return null;
        } else {
            return String.format("%5.1f %s.", Double.valueOf(azmDiff), makeAzmDirStr(vecXY, zDirXY));
        }
    }

    private static String makeSignedDirection(Vector3d vecXY, Vector3d zDirXY, double azmDiff) {
        return String.format("%s %5.0f Azimuth.", getAzimuthSign(vecXY, zDirXY), Double.valueOf(azmDiff));
    }

    private static String makeClockbasedDirection(Vector3d vecXY, Vector3d zDirXY, double azmDiff) {
        return String.format("%5.0f %s.", Double.valueOf(azmDiff), makeAzmDirStr(vecXY, zDirXY));
    }

    private static String makeAzmDirStr(Vector3d vecXY, Vector3d zDirXY) {
        return vecXY.f16x * zDirXY.f17y > vecXY.f17y * zDirXY.f16x ? "clock wise" : "anticlock";
    }

    private static String getAzimuthSign(Vector3d vecXY, Vector3d zDirXY) {
        return vecXY.f16x * zDirXY.f17y > vecXY.f17y * zDirXY.f16x ? "plus" : "minus";
    }
}
