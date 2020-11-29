package com.lavadip.skeye;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;
import com.lavadip.skeye.device.Device;
import com.lavadip.skeye.util.Util;

public final class OrientationManager {
    private boolean currentlyListening = false;

    /* renamed from: dc */
    private volatile Device f4dc = null;
    private float geomagneticDeclination = 0.0f;
    private ViewManager parent;

    public OrientationManager(SensorManager sm) {
    }

    /* access modifiers changed from: package-private */
    public void setParent(ViewManager vm) {
        this.parent = vm;
        informAutoPossible();
    }

    public synchronized void setDeviceConnection(Device selectedDevice) {
        if (this.currentlyListening) {
            stopListening();
        }
        this.f4dc = selectedDevice;
        informAutoPossible();
        startListening();
    }

    private void informAutoPossible() {
        boolean autoPossible = this.f4dc.isAutoPossible();
        if (this.parent != null) {
            this.parent.setAutoPossible(autoPossible);
            if (autoPossible) {
                this.parent.getSkeye().myHandler.obtainMessage(2, 0, 0).sendToTarget();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public synchronized void startListening() {
        if (!this.currentlyListening) {
            this.f4dc.startListening();
            Log.d("SKEYE", "Started listening to sensors");
            this.currentlyListening = true;
        }
    }

    /* access modifiers changed from: package-private */
    public synchronized void disconnect() {
        this.f4dc.deselect();
        this.currentlyListening = false;
    }

    /* access modifiers changed from: package-private */
    public synchronized void stopListening() {
        if (this.currentlyListening) {
            this.f4dc.stopListening();
            Log.d("SKEYE", "Stopped listening to sensors from " + this.f4dc);
            this.currentlyListening = false;
        }
    }

    public void setGeomagneticDecl(float newDecl) {
        this.geomagneticDeclination = newDecl;
    }

    public float getFieldStrength() {
        return this.f4dc.getFieldStrength();
    }

    public boolean getCurrRotationMatrix(float[] currRotation) {
        boolean result = this.f4dc.getCurrRotationMatrixNoDec(currRotation);
        if (result) {
            Util.rotateMatrixAroundZ(currRotation, 0, Math.toRadians((double) this.geomagneticDeclination));
        }
        return result;
    }

    public void switchToAligned() {
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public boolean isAutoPossible() {
        return this.f4dc.isAutoPossible();
    }

    public String getSensorPresentMessage(Context skEye) {
        return this.f4dc.getSensorPresentMessage(skEye);
    }

    public void onOrientationChanged(boolean isRemote) {
        this.parent.onOrientationChanged(isRemote);
    }

    public boolean getCurrRotationMatrixNoDec(float[] rotation) {
        return this.f4dc.getCurrRotationMatrixNoDec(rotation);
    }
}
