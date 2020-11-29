package com.lavadip.skeye.device;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.util.Log;
import com.lavadip.skeye.C0031R;

public final class LocalAndroidDevice extends DeviceBase implements SensorEventListener {
    private static final int ROT_VECTOR_SENSOR_TYPE = 11;
    private static boolean haveICS;
    private static boolean mUseGyros = true;
    private final boolean accelerometerPresent;

    /* renamed from: dm */
    private final DeviceManager f89dm;
    private final ExpoSmoother expSmootherAcc = new ExpoSmoother(Sensitivity.Normal.accAlpha, Sensitivity.Normal.accExp, 0.01f, 3);
    private final ExpoSmoother expSmootherMag = new ExpoSmoother(Sensitivity.Normal.magAlpha, Sensitivity.Normal.magExp, Sensitivity.Normal.magDAlpha, 3);
    private final ExpoSmoother expSmootherRotVec = new ExpoSmoother(Sensitivity.Normal.sensorFusionAlpha, Sensitivity.Normal.sensorFusionExp, Sensitivity.Normal.sensorFusionDAlpha, 6);
    private final boolean magnetometerPresent;
    private final boolean sensorFusionPresent;

    /* renamed from: sm */
    private final SensorManager f90sm;
    private boolean stoppedListening = false;

    public LocalAndroidDevice(DeviceManager dm, SensorManager sm) {
        this.f89dm = dm;
        this.f90sm = sm;
        this.accelerometerPresent = isAccelerometerPresent(sm);
        this.magnetometerPresent = isMagnetometerPresent(sm);
        this.sensorFusionPresent = sensorFusionAvailable(sm);
    }

    @Override // com.lavadip.skeye.device.Device
    public int getSuggestedSampleWindow() {
        return 1200;
    }

    @Override // com.lavadip.skeye.device.Device
    public double getFitnessTolerance() {
        return 0.02d;
    }

    @Override // com.lavadip.skeye.device.Device
    public Sensitivity getSensitivity() {
        return null;
    }

    @Override // com.lavadip.skeye.device.Device
    public boolean isAutoPossible() {
        return (this.accelerometerPresent && this.magnetometerPresent) || this.sensorFusionPresent;
    }

    @Override // com.lavadip.skeye.device.Device
    public String getSensorPresentMessage(Context c) {
        String str;
        String str2;
        String str3;
        String presentStr = c.getString(C0031R.string.present);
        String absentStr = c.getString(C0031R.string.absent);
        boolean gyroscope = isGyroPresent(this.f90sm);
        StringBuilder append = new StringBuilder(String.valueOf(c.getString(C0031R.string.accelerometer))).append(": ");
        if (this.accelerometerPresent) {
            str = presentStr;
        } else {
            str = absentStr;
        }
        String acclStr = append.append(str).toString();
        StringBuilder append2 = new StringBuilder(String.valueOf(c.getString(C0031R.string.magnetometer))).append(": ");
        if (this.magnetometerPresent) {
            str2 = presentStr;
        } else {
            str2 = absentStr;
        }
        String magStr = append2.append(str2).toString();
        StringBuilder append3 = new StringBuilder(String.valueOf(c.getString(C0031R.string.gyroscope))).append(": ");
        if (gyroscope) {
            str3 = presentStr;
        } else {
            str3 = absentStr;
        }
        String gyroStr = append3.append(str3).toString();
        StringBuilder append4 = new StringBuilder(String.valueOf(c.getString(C0031R.string.sensor_fusion))).append(": ");
        if (!this.sensorFusionPresent) {
            presentStr = absentStr;
        }
        return String.valueOf(acclStr) + "\n" + magStr + "\n" + gyroStr + "\n" + append4.append(presentStr).toString();
    }

    @Override // com.lavadip.skeye.device.Device
    public void select() {
        this.f89dm.connected();
    }

    @Override // com.lavadip.skeye.device.Device
    public void deselect() {
    }

    @Override // com.lavadip.skeye.device.Device
    public String getName() {
        return "Local Android device";
    }

    @Override // com.lavadip.skeye.device.Device
    public String getId() {
        return "LocalAndroid";
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        System.out.println("Accuracy changed: " + accuracy);
        if (accuracy > 0) {
            this.f89dm.connected();
        }
    }

    private static String describeAccuracy(int accuracy) {
        if (accuracy == 0) {
            return "unreliable!";
        }
        if (accuracy == 1) {
            return "low!";
        }
        if (accuracy == 2) {
            return "medium";
        }
        if (accuracy == 3) {
            return "high";
        }
        return "unknown!";
    }

    static {
        boolean z = true;
        if (Build.VERSION.SDK_INT < 14) {
            z = false;
        }
        haveICS = z;
    }

    @Override // com.lavadip.skeye.device.Device
    public void startListening() {
        Sensor rotVecSensor = getRotVecSensorIfSensorFused(this.f90sm);
        this.usingRotVector = mUseGyros && rotVecSensor != null;
        Log.d("SKEYE", "Gyro enabled by user? : " + mUseGyros);
        Log.d("SKEYE", "Using rot vector? : " + this.usingRotVector);
        if (this.usingRotVector) {
            this.f90sm.registerListener(this, rotVecSensor, 0);
            this.f90sm.registerListener(this, this.f90sm.getDefaultSensor(1), 3);
            this.f90sm.registerListener(this, this.f90sm.getDefaultSensor(2), 2);
        } else {
            this.f90sm.registerListener(this, this.f90sm.getDefaultSensor(1), 3);
            this.f90sm.registerListener(this, this.f90sm.getDefaultSensor(2), 0);
        }
        this.stoppedListening = false;
    }

    @Override // com.lavadip.skeye.device.Device
    public void stopListening() {
        this.stoppedListening = true;
    }

    private static boolean isAccelerometerPresent(SensorManager sm) {
        return sm.getDefaultSensor(1) != null;
    }

    private static boolean isMagnetometerPresent(SensorManager sm) {
        return sm.getDefaultSensor(2) != null;
    }

    private static boolean isGyroPresent(SensorManager sm) {
        return sm.getDefaultSensor(4) != null;
    }

    private static Sensor getRotVecSensor(SensorManager sm) {
        if (isGyroPresent(sm)) {
            return sm.getDefaultSensor(ROT_VECTOR_SENSOR_TYPE);
        }
        return null;
    }

    public static boolean sensorFusionAvailable(SensorManager sm) {
        return haveICS && getRotVecSensor(sm) != null;
    }

    private static Sensor getRotVecSensorIfSensorFused(SensorManager sm) {
        if (haveICS) {
            return getRotVecSensor(sm);
        }
        return null;
    }

    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        switch (sensorType) {
            case 1:
                if (!this.stoppedListening) {
                    this.expSmootherAcc.update(this.bufferedAcceleration, event.values);
                    break;
                } else {
                    System.arraycopy(event.values, 0, this.bufferedAcceleration, 0, this.bufferedAcceleration.length);
                    break;
                }
            case 2:
                if (this.stoppedListening) {
                    System.arraycopy(event.values, 0, this.bufferedMagneticField, 0, this.bufferedMagneticField.length);
                } else {
                    this.expSmootherMag.update(this.bufferedMagneticField, event.values);
                }
                float[] buff = this.bufferedMagneticField;
                this.fieldStrength = (float) Math.sqrt((double) ((buff[0] * buff[0]) + (buff[1] * buff[1]) + (buff[2] * buff[2])));
                break;
            case ROT_VECTOR_SENSOR_TYPE /*{ENCODED_INT: 11}*/:
                getNormalVectorsFromRotationVectorPartial(this.bufferedRotVectorsTemp, event.values);
                if (!this.stoppedListening) {
                    this.expSmootherRotVec.update(this.bufferedRotVectors, this.bufferedRotVectorsTemp);
                    break;
                } else {
                    System.arraycopy(this.bufferedRotVectorsTemp, 0, this.bufferedRotVectors, 0, this.bufferedRotVectors.length);
                    break;
                }
            default:
                Log.d("SKEYE", "Recvd reading " + sensorType);
                break;
        }
        if (!this.stoppedListening) {
            this.f89dm.onOrientationChanged(false);
        }
    }

    public void setPrefs(String accLevel, String magLevel, String sensorFusionLevel, boolean useGyros) {
        stopListening();
        Sensitivity accSensitivity = Sensitivity.valueOf(accLevel);
        this.expSmootherAcc.setParams(accSensitivity.accAlpha, accSensitivity.accExp, accSensitivity.accDAlpha);
        Sensitivity magSensitivity = Sensitivity.valueOf(magLevel);
        this.expSmootherMag.setParams(magSensitivity.magAlpha, magSensitivity.magExp, magSensitivity.magDAlpha);
        Sensitivity sensorFusionSensitivity = Sensitivity.valueOf(sensorFusionLevel);
        this.expSmootherRotVec.setParams(sensorFusionSensitivity.sensorFusionAlpha, sensorFusionSensitivity.sensorFusionExp, sensorFusionSensitivity.sensorFusionDAlpha);
        mUseGyros = useGyros;
        startListening();
    }

    private static boolean getNormalVectorsFromRotationVectorPartial(float[] R, float[] rotationVector) {
        float q0;
        float q1 = rotationVector[0];
        float q2 = rotationVector[1];
        float q3 = rotationVector[2];
        if (rotationVector.length == 4) {
            q0 = rotationVector[3];
        } else {
            float q02 = ((1.0f - (q1 * q1)) - (q2 * q2)) - (q3 * q3);
            q0 = q02 > 0.0f ? (float) Math.sqrt((double) q02) : 0.0f;
        }
        float sq_q3 = 2.0f * q3 * q3;
        float q1_q2 = 2.0f * q1 * q2;
        float q3_q0 = 2.0f * q3 * q0;
        R[0] = (1.0f - ((2.0f * q2) * q2)) - sq_q3;
        R[1] = q1_q2 + q3_q0;
        R[2] = ((2.0f * q1) * q3) - ((2.0f * q2) * q0);
        R[3] = q1_q2 - q3_q0;
        R[4] = (1.0f - ((2.0f * q1) * q1)) - sq_q3;
        R[5] = (2.0f * q2 * q3) + (2.0f * q1 * q0);
        return true;
    }

    public int getScreenOrientation() {
        double horizAngle = Math.acos(Math.max(-1.0d, Math.min(1.0d, ((double) this.bufferedAcceleration[0]) / Math.sqrt((double) (((this.bufferedAcceleration[0] * this.bufferedAcceleration[0]) + (this.bufferedAcceleration[1] * this.bufferedAcceleration[1])) + (this.bufferedAcceleration[2] * this.bufferedAcceleration[2]))))));
        if (horizAngle < 1.0995574287564276d) {
            return 1;
        }
        if (horizAngle > 2.0420352248333655d) {
            return 3;
        }
        return this.bufferedAcceleration[1] > 0.0f ? 0 : 2;
    }

    public void disconnectFully() {
        this.f90sm.unregisterListener(this);
    }
}
