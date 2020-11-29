package com.lavadip.skeye.device;

import com.lavadip.skeye.Vector3d;
import java.util.Locale;

public class SensorData {
    final Vector3d accPoint;
    final Vector3d gyroPoint;
    final Vector3d magPoint;
    final long timestamp;

    public SensorData(long timestamp2, double accX, double accY, double accZ, double gyroX, double gyroY, double gyroZ, double magX, double magY, double magZ) {
        this.timestamp = timestamp2;
        this.accPoint = new Vector3d(accX, accY, accZ);
        this.gyroPoint = new Vector3d(gyroX, gyroY, gyroZ);
        this.magPoint = new Vector3d(magX, magY, magZ);
    }

    public SensorData(long timestamp2, Vector3d accPoint2, Vector3d gyroPoint2, Vector3d magPoint2) {
        this.timestamp = timestamp2;
        this.accPoint = accPoint2;
        this.gyroPoint = gyroPoint2;
        this.magPoint = magPoint2;
    }

    public String serializeToString() {
        String serializeToString;
        Locale locale = Locale.US;
        Object[] objArr = new Object[4];
        objArr[0] = Long.valueOf(this.timestamp);
        objArr[1] = this.accPoint == null ? "null" : this.accPoint.serializeToString();
        objArr[2] = this.gyroPoint == null ? "null" : this.gyroPoint.serializeToString();
        if (this.magPoint == null) {
            serializeToString = "null";
        } else {
            serializeToString = this.magPoint.serializeToString();
        }
        objArr[3] = serializeToString;
        return String.format(locale, "%d,%s,%s,%s", objArr);
    }
}
