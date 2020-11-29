package com.lavadip.skeye.config;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.lavadip.skeye.C0031R;
import com.lavadip.skeye.device.Sensitivity;

public final class SettingsManager {
    public static final String PREF_ACC_SENSE = "acc_sensitivity_new";
    private static final String PREF_COUNT_USEAGE = "count_useage";
    private static final String PREF_FULLSCREEN = "fullScreen";
    public static final String PREF_MAG_SENSE = "mag_sensitivity_new";
    public static final String PREF_PREV_ALTITUDE = "prevAlt";
    public static final String PREF_PREV_LAT = "prevLat";
    public static final String PREF_PREV_LONG = "prevLong";
    private static final String PREF_PRINCIPAL_ORIENTATION = "principal_orientation";
    private static final String PREF_ROT_LABELS_HH = "rotate_labels_enabled_hh";
    private static final String PREF_ROT_LABELS_INDIRECT = "rotate_labels_enabled_indirect";
    public static final String PREF_SENSOR_FUSION_SENSE = "sensor_fusion_sensitivity";
    private static final String PREF_SHOWN_AUTO_MODE_DISABLED = "shown_auto_mode_disabled";
    private static final String PREF_SHOW_SENSOR_WARNINGS = "show_sensor_warnings";
    public static final String PREF_USE_SENSOR_FUSION = "use_sensor_fusion";
    private static final String PREF_ZOOM_BY_VOLUME_KEYS = "zoom_by_volume_keys";
    private final SharedPreferences appPrefs;
    private final Context context;
    private final SharedPreferences pref;

    public enum ExitConfirmation {
        Never,
        Aligned,
        Always
    }

    public SettingsManager(Context context2) {
        this.appPrefs = getAppPrefs(context2);
        this.context = context2;
        this.pref = PreferenceManager.getDefaultSharedPreferences(context2);
    }

    public SharedPreferences getDefaultSharedPrefs() {
        return this.pref;
    }

    public static SharedPreferences getAppPrefs(Context c) {
        return c.getSharedPreferences("SkEye", 0);
    }

    public ExitConfirmation getExitConfirmationPref() {
        return ExitConfirmation.valueOf(this.pref.getString(this.context.getString(C0031R.string.exit_confirmation_key), this.context.getString(C0031R.string.exit_confirmation_default)));
    }

    public boolean getFullScreenPref() {
        return this.appPrefs.getBoolean(PREF_FULLSCREEN, true);
    }

    public boolean getRotLabelsHHPref() {
        return this.pref.getBoolean(PREF_ROT_LABELS_HH, true);
    }

    public boolean getRotLabelsHHIndirectPref() {
        return this.pref.getBoolean(PREF_ROT_LABELS_INDIRECT, true);
    }

    public boolean getShowSensorWarningsPref() {
        return this.pref.getBoolean(PREF_SHOW_SENSOR_WARNINGS, true);
    }

    public boolean getZoomByVolumeKeysPref() {
        return this.pref.getBoolean(PREF_ZOOM_BY_VOLUME_KEYS, true);
    }

    public String getAcclSensitivityPref() {
        return this.pref.getString(PREF_ACC_SENSE, Sensitivity.Normal.name());
    }

    public String getMagSensitivityPref() {
        return this.pref.getString(PREF_MAG_SENSE, Sensitivity.Normal.name());
    }

    public String getSensorFusionSensitivityPref() {
        return this.pref.getString(PREF_SENSOR_FUSION_SENSE, Sensitivity.Normal.name());
    }

    public Boolean getUseSensorFusionPref() {
        return Boolean.valueOf(this.pref.getBoolean(PREF_USE_SENSOR_FUSION, true));
    }

    public Boolean isLocationSet() {
        return Boolean.valueOf(this.appPrefs.contains(PREF_PREV_LAT));
    }

    public LocationOnEarth getPrevLocationPref() {
        return new LocationOnEarth(this.appPrefs.getFloat(PREF_PREV_LAT, 0.0f), this.appPrefs.getFloat(PREF_PREV_LONG, 0.0f), this.appPrefs.getFloat(PREF_PREV_ALTITUDE, 0.0f));
    }

    public void setPrevLocationPref(LocationOnEarth location) {
        this.appPrefs.edit().putFloat(PREF_PREV_LAT, location.latitude).putFloat(PREF_PREV_LONG, location.longitude).putFloat(PREF_PREV_ALTITUDE, location.altitude).commit();
    }

    public int getPrincipalOrientationPref() {
        return mapOrientation(this.pref.getString(PREF_PRINCIPAL_ORIENTATION, this.context.getString(C0031R.string.principal_orientation_default)));
    }

    private static int mapOrientation(String orientStr) {
        if (orientStr.equals("orient0")) {
            return 1;
        }
        if (orientStr.equals("orient90")) {
            return 0;
        }
        return -1;
    }

    public void toggleFullScreenPref() {
        boolean z = true;
        boolean fullScreenPrev = this.appPrefs.getBoolean(PREF_FULLSCREEN, true);
        SharedPreferences.Editor edit = this.appPrefs.edit();
        if (fullScreenPrev) {
            z = false;
        }
        edit.putBoolean(PREF_FULLSCREEN, z).commit();
    }

    public static void updateFullScreen(Activity a) {
        a.getWindow().setFlags(new SettingsManager(a).getFullScreenPref() ? 1024 : 0, 1024);
    }

    public void incrAndCheckUseage() {
        this.pref.edit().putInt(PREF_COUNT_USEAGE, this.pref.getInt(PREF_COUNT_USEAGE, 0) + 1).commit();
    }

    public boolean getShownAutoModeDisabled() {
        return this.appPrefs.getBoolean(PREF_SHOWN_AUTO_MODE_DISABLED, false);
    }

    public void setShownAutoModeDisabled() {
        this.appPrefs.edit().putBoolean(PREF_SHOWN_AUTO_MODE_DISABLED, true).commit();
    }

    public void saveQuickPref(String key, float value) {
        this.pref.edit().putFloat(key, value).commit();
    }

    public float getQuickPref(String key, float defValue) {
        return this.pref.getFloat(key, defValue);
    }
}
