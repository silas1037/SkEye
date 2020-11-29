package com.lavadip.skeye.config;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.ViewGroup;
import com.lavadip.skeye.C0031R;
import com.lavadip.skeye.SkEye;
import com.lavadip.skeye.device.DeviceManager;
import com.lavadip.skeye.view.OverlaidFilterView;
import com.lavadip.skeyepro.C0139R;
import java.util.List;

public class SettingsActivity extends PreferenceActivity {
    static final String ACTION_PREFS_GENERAL = "com.lavadip.skeye.config.PREFS_GENERAL";
    static final String ACTION_PREFS_SENSORS = "com.lavadip.skeye.config.PREFS_SENSORS";
    private static SharedPreferences.OnSharedPreferenceChangeListener prefChangeListener;

    /* access modifiers changed from: package-private */
    public interface Updater {
        void updateViews(SharedPreferences sharedPreferences);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OverlaidFilterView overlaidView = new OverlaidFilterView(this);
        overlaidView.setId(C0031R.C0032id.overlaidFilterView);
        getWindow().addContentView(overlaidView, new ViewGroup.LayoutParams(-1, -1));
        SkEye.setupActivity(this);
    }

    /* access modifiers changed from: protected */
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        String action = getIntent().getAction();
        if (ACTION_PREFS_GENERAL.equals(action)) {
            addPreferencesFromResource(C0031R.xml.pref_general);
        } else if (ACTION_PREFS_SENSORS.equals(action)) {
            addPreferencesFromResource(C0031R.xml.pref_sensors);
            setupSensorPrefs(getPreferenceScreen());
        } else if (Build.VERSION.SDK_INT < 11) {
            addPreferencesFromResource(C0139R.xml.pref_headers_legacy);
        }
    }

    /* access modifiers changed from: private */
    public static void setupSensorPrefs(PreferenceScreen prefScreen) {
        final boolean sensorFusionAvailable = DeviceManager.sensorFusionAvailable((SensorManager) prefScreen.getContext().getSystemService("sensor"));
        PreferenceCategory sensitivityPrefCategory = (PreferenceCategory) prefScreen.findPreference("pref_category_sensor_sensitivity");
        CheckBoxPreference sensorFusionPref = (CheckBoxPreference) prefScreen.findPreference(SettingsManager.PREF_USE_SENSOR_FUSION);
        final CustomListPreferenceWithSummary sensorFusionSensitivityPref = (CustomListPreferenceWithSummary) prefScreen.findPreference(SettingsManager.PREF_SENSOR_FUSION_SENSE);
        final CustomListPreferenceWithSummary accSensitivityPref = (CustomListPreferenceWithSummary) prefScreen.findPreference(SettingsManager.PREF_ACC_SENSE);
        final CustomListPreferenceWithSummary magSensitivityPref = (CustomListPreferenceWithSummary) prefScreen.findPreference(SettingsManager.PREF_MAG_SENSE);
        if (!sensorFusionAvailable) {
            prefScreen.removePreference(sensorFusionPref);
            sensitivityPrefCategory.removePreference(sensorFusionSensitivityPref);
        }
        final Updater updater = new Updater() {
            /* class com.lavadip.skeye.config.SettingsActivity.C01181 */

            @Override // com.lavadip.skeye.config.SettingsActivity.Updater
            public void updateViews(SharedPreferences sharedPrefs) {
                boolean sensorFusionEnabled;
                boolean z;
                boolean z2 = false;
                if (!sensorFusionAvailable || !sharedPrefs.getBoolean(SettingsManager.PREF_USE_SENSOR_FUSION, sensorFusionAvailable)) {
                    sensorFusionEnabled = false;
                } else {
                    sensorFusionEnabled = true;
                }
                sensorFusionSensitivityPref.setEnabled(sensorFusionEnabled);
                CustomListPreferenceWithSummary customListPreferenceWithSummary = accSensitivityPref;
                if (sensorFusionEnabled) {
                    z = false;
                } else {
                    z = true;
                }
                customListPreferenceWithSummary.setEnabled(z);
                CustomListPreferenceWithSummary customListPreferenceWithSummary2 = magSensitivityPref;
                if (!sensorFusionEnabled) {
                    z2 = true;
                }
                customListPreferenceWithSummary2.setEnabled(z2);
            }
        };
        updater.updateViews(prefScreen.getSharedPreferences());
        prefChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            /* class com.lavadip.skeye.config.SettingsActivity.SharedPreferences$OnSharedPreferenceChangeListenerC01192 */

            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                Updater.this.updateViews(sharedPreferences);
            }
        };
        prefScreen.getSharedPreferences().registerOnSharedPreferenceChangeListener(prefChangeListener);
    }

    public boolean onIsMultiPane() {
        return isLargeScreen(this);
    }

    private static boolean isLargeScreen(Context context) {
        return (context.getResources().getConfiguration().screenLayout & 15) >= 3;
    }

    @Override // android.preference.PreferenceActivity
    @TargetApi(11)
    public void onBuildHeaders(List<PreferenceActivity.Header> target) {
        loadHeadersFromResource(C0031R.xml.pref_headers, target);
    }

    @TargetApi(11)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(C0031R.xml.pref_general);
        }
    }

    @TargetApi(11)
    public static class SensorsPreferenceFragment extends PreferenceFragment {
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(C0031R.xml.pref_sensors);
            SettingsActivity.setupSensorPrefs(getPreferenceScreen());
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        prefChangeListener = null;
    }
}
