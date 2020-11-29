package com.lavadip.skeye;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.hardware.GeomagneticField;
import android.hardware.SensorManager;
import android.media.AudioTrack;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import com.lavadip.skeye.AnimatedSimpleCustomMenu;
import com.lavadip.skeye.CustomDialog;
import com.lavadip.skeye.NightModeMgr;
import com.lavadip.skeye.QuickSettingsManager;
import com.lavadip.skeye.astro.CatalogedLocation;
import com.lavadip.skeye.astro.LocationInSky;
import com.lavadip.skeye.astro.Sky;
import com.lavadip.skeye.audio.SoundWave;
import com.lavadip.skeye.catalog.Catalog;
import com.lavadip.skeye.catalog.CatalogManager;
import com.lavadip.skeye.catalog.SolarSysCatalog;
import com.lavadip.skeye.config.GetAlignmentActivity;
import com.lavadip.skeye.config.GetMultiAlignActivity;
import com.lavadip.skeye.config.LocationOnEarth;
import com.lavadip.skeye.config.SelectLocationActivity;
import com.lavadip.skeye.config.SettingsActivity;
import com.lavadip.skeye.config.SettingsManager;
import com.lavadip.skeye.device.DeviceManager;
import com.lavadip.skeye.util.Util;
import com.lavadip.skeye.view.BatteryView;
import com.lavadip.skeye.view.FinderView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import org.apache.commons.math3.dfp.Dfp;
import org.apache.commons.math3.random.EmpiricalDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public final class SkEye extends Activity implements View.OnTouchListener, AnimatedSimpleCustomMenu.OnMenuItemSelectedListener {

    /* renamed from: $SWITCH_TABLE$com$lavadip$skeye$config$SettingsManager$ExitConfirmation */
    private static /* synthetic */ int[] f8xfaeeb0eb = null;
    private static final int DIALOG_ABOUT = 0;
    private static final int DIALOG_AUTO_MODE_DISABLED = 4;
    private static final int DIALOG_SENSOR_TIPS = 2;
    static final int FREE_VERSION = 1;
    private static final int GET_ALIGN_REQUEST = 1;
    private static final int GET_CATALOGS = 5;
    private static final int GET_HERRING_REQUEST = 3;
    private static final int GET_LOCATION_REQUEST = 0;
    private static final int GET_SETTINGS = 4;
    private static final int MENU_ABOUT = 9;
    private static final int MENU_CATALOGS = 2;
    private static final int MENU_CHANGE_COLOR_THEME = 5;
    private static final int MENU_DEVICE_MANAGER = 11;
    private static final int MENU_FULL_SCREEN = 6;
    private static final int MENU_INDIRECT_MODE = 1;
    private static final int MENU_QUICK_SETTINGS = 7;
    private static final int MENU_SEARCH = 4;
    private static final int MENU_SETTINGS = 8;
    private static final int MENU_SET_LOCATION = 0;
    private static final int MENU_TIME_MACHINE = 10;
    public static final int MSG_AUTO_POSSIBLE = 2;
    public static final int MSG_SHOW_TOAST = 3;
    public static final int MSG_UPDATE_FOV = 1;
    public static final int MSG_UPDATE_HERRING = 0;
    static final double ONE_DEG_IN_RADIANS = Math.toRadians(1.0d);
    public static final int PRO_VERSION = 0;
    private static final long SCREEN_REFRESH_INTERVAL = 71;
    private static AlignManager alignMgr = null;

    /* renamed from: dm */
    private static DeviceManager f9dm = null;
    private static final boolean enableDeviceManager = true;
    private static final boolean enableTTS = true;

    /* renamed from: om */
    private static OrientationManager f10om = null;
    static final Vector3d xVec = new Vector3d(1.0f, 0.0f, 0.0f);
    static final Vector3d zVec = new Vector3d(0.0f, 0.0f, 1.0f);
    static final Vector3d zinv = new Vector3d(0.0f, 0.0f, -1.0f);
    private volatile boolean activityPaused = false;
    private final AudioTrack audioTrack = SoundWave.genTone(440, 200);
    private int centeredObj = -1;
    private View centeredSearchIcon;
    private int currOrientInt;
    private float expectedGeoMagneticFieldStrength = 0.0f;
    volatile boolean findHerringMode = false;
    private View finderStatusDiv = null;
    private TextView finderStatusTxt = null;
    private FinderView finderView = null;
    private GLSurfaceView gl3d = null;
    volatile float[] herringCoords = new float[3];
    volatile Vector3d herringVector;
    volatile boolean herringVisible = false;
    private boolean intentProcessed = false;
    private boolean isLocationSet = false;
    private long lastObjRefresh = 0;
    private long lastPlayTime;
    private long lastScreenRefresh = 0;
    private long lastSkyUpdate = 0;
    private final Object lockCenterObj = new Object();
    private final float[] mDispRotationMatrix = {1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f};
    private GestureDetector mGestureDetector;
    private int mHerringId;
    private String mHerringName;
    private AnimatedSimpleCustomMenu mMenu;
    private boolean mRotateLabelsHandHeld = true;
    private boolean mRotateLabelsIndirect = true;
    private boolean mSensorNoPause = false;
    private boolean mShowSensorWarnings = true;
    private boolean mZoomByVolumeKeys = true;
    final Handler myHandler = new Handler() {
        /* class com.lavadip.skeye.SkEye.HandlerC00331 */

        public void handleMessage(Message msg) {
            synchronized (SkEye.this) {
                if (msg.what == 0) {
                    if (SkEye.this.findHerringMode) {
                        SkEye.this.statusManager.showHerringCoords(SkEye.this.herringVector);
                    }
                } else if (msg.what == 1) {
                    float angle1 = ((float) msg.arg1) / 100.0f;
                    float angle2 = ((float) msg.arg2) / 100.0f;
                    SkEye.this.finderView.setFov(angle1);
                    SkEye.this.statusManager.setFov(angle1, angle2);
                } else if (msg.what == 2) {
                    if (!SkEye.this.getIntent().getAction().equals("android.intent.action.VIEW")) {
                        SkEye.this.f11vm.exitManual();
                    }
                } else if (msg.what == 3) {
                    Toast.makeText(SkEye.this, (CharSequence) msg.obj, msg.arg1).show();
                }
            }
        }
    };
    private MyShadyRenderer myRenderer;
    private final NightModeMgr nightModeMgr = new NightModeMgr();
    private TextView objStatus;
    private int prevCenteredObj = -1;
    private double prevHerringAngle;
    private long prevSpokenTime;
    private QuickSettingsManager quickSettingsManager;
    private Vector3d savedOrientationZDir = xVec;
    public SettingsManager settingsManager;
    private final Speech speech = new Speech();
    private BatteryView statusBattery = null;
    private final StatusManager statusManager = new StatusManager();
    private TextView statusView;
    private View statusViewContainer;
    private TimeMgr timeMgr;
    private TextToSpeech tts = null;
    private final HashMap<String, String> ttsParam = new HashMap<>();
    private LocationOnEarth userLocation;

    /* renamed from: vm */
    private ViewManager f11vm;
    private boolean voiceInstructionsEnabled;

    /* renamed from: $SWITCH_TABLE$com$lavadip$skeye$config$SettingsManager$ExitConfirmation */
    static /* synthetic */ int[] m0xfaeeb0eb() {
        int[] iArr = f8xfaeeb0eb;
        if (iArr == null) {
            iArr = new int[SettingsManager.ExitConfirmation.values().length];
            try {
                iArr[SettingsManager.ExitConfirmation.Aligned.ordinal()] = 2;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[SettingsManager.ExitConfirmation.Always.ordinal()] = 3;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[SettingsManager.ExitConfirmation.Never.ordinal()] = 1;
            } catch (NoSuchFieldError e3) {
            }
            f8xfaeeb0eb = iArr;
        }
        return iArr;
    }

    static {
        Sky.init();
    }

    public static AlignManager getAlignMgr() {
        return alignMgr;
    }

    public static OrientationManager getOrientationMgr() {
        return f10om;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.f11vm.cancelInstaAlign(false);
        pauseViews();
        updateView();
        resumeViews();
        this.mMenu.refreshView(this);
        NightModeMgr.setThemeForActivity(this);
        this.finderView.setTheme(NightModeMgr.getTheme(this));
    }

    @TargetApi(8)
    private void setupShadyGL() {
        this.gl3d.setEGLContextClientVersion(2);
        this.gl3d.setEGLConfigChooser(new MultisampleConfigChooser());
    }

    private void updateView() {
        int i;
        setContentView(C0031R.layout.activity_main);
        this.statusManager.init(this);
        this.statusBattery = (BatteryView) findViewById(C0031R.C0032id.statusBattery);
        this.statusViewContainer = findViewById(C0031R.C0032id.statusMsgContainer);
        this.statusView = (TextView) findViewById(C0031R.C0032id.statusMsg);
        this.finderView = (FinderView) findViewById(C0031R.C0032id.finderView);
        this.finderView.initViews(this);
        this.finderStatusTxt = (TextView) findViewById(C0031R.C0032id.finderStatusText);
        this.finderStatusDiv = findViewById(C0031R.C0032id.finderStatusDiv);
        this.objStatus = (TextView) findViewById(C0031R.C0032id.centerObjStatus);
        this.centeredSearchIcon = findViewById(C0031R.C0032id.centeredSearchIcon);
        boolean hardwareMenuButtonPresent = Build.VERSION.SDK_INT <= MENU_TIME_MACHINE || (Build.VERSION.SDK_INT >= 14 && hasHardwareMenuButtons());
        View findViewById = findViewById(C0031R.C0032id.menuButton);
        if (hardwareMenuButtonPresent) {
            i = 8;
        } else {
            i = 0;
        }
        findViewById.setVisibility(i);
        enableFinderView();
        this.gl3d = (GLSurfaceView) findViewById(C0031R.C0032id.gl3d);
        setupShadyGL();
        this.gl3d.setRenderer(this.myRenderer);
        this.gl3d.setRenderMode(0);
        this.gl3d.setOnTouchListener(this);
        this.currOrientInt = ((WindowManager) getSystemService("window")).getDefaultDisplay().getOrientation();
        this.f11vm.updateViews(this.gl3d, (TextView) findViewById(C0031R.C0032id.alignToObj));
        this.timeMgr.updateViews(this);
    }

    @TargetApi(SolarSysCatalog.NUM_OBJS)
    private boolean hasHardwareMenuButtons() {
        return ViewConfiguration.get(this).hasPermanentMenuKey();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.settingsManager = new SettingsManager(this);
        FarLabelManager.appContext = this;
        Directions.init(this);
        try {
            NumComponents.readFrom(getAssets().openFd("numericComponents.jet").createInputStream());
            for (Catalog c : CatalogManager.catalogs) {
                c.init(this);
            }
            LocationInSky.init(this);
            SensorManager sm = (SensorManager) getSystemService("sensor");
            f10om = new OrientationManager(sm);
            f9dm = new DeviceManager(f10om, this, sm);
            alignMgr = new AlignManager();
            DisplayMetrics dmetrics = getResources().getDisplayMetrics();
            float screenDensity = ((dmetrics.xdpi + dmetrics.ydpi) / 2.0f) / 160.0f;
            this.myRenderer = new MyShadyRenderer(screenDensity, this, this.mDispRotationMatrix);
            this.quickSettingsManager = new QuickSettingsManager(this, this.myRenderer);
            this.mGestureDetector = new GestureDetector(this, new GestureListener(this, null));
            updateSettings(true);
            this.timeMgr = new TimeMgr();
            this.f11vm = new ViewManager(alignMgr, f10om, this, this.myRenderer, screenDensity, this.timeMgr);
            updateView();
            SettingsManager.updateFullScreen(this);
            this.settingsManager.incrAndCheckUseage();
            if (this.settingsManager.isLocationSet().booleanValue()) {
                this.userLocation = this.settingsManager.getPrevLocationPref();
                this.isLocationSet = true;
                completeUserLocationSet();
            } else {
                startActivityForLocation();
            }
            if (!f10om.isAutoPossible()) {
                updateTimes();
                this.f11vm.setAutoPossible(false);
                this.f11vm.switchToManual(0.0d, 1.5707963267948966d);
            }
            if (!f10om.isAutoPossible() && !this.settingsManager.getShownAutoModeDisabled()) {
                this.settingsManager.setShownAutoModeDisabled();
                showDialog(4);
            }
            setupMenu();
            setupActivity(this);
            this.tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                /* class com.lavadip.skeye.SkEye.C00372 */

                public void onInit(int status) {
                    if (status == 0) {
                        SkEye.this.ttsParam.put(Build.VERSION.SDK_INT >= SkEye.MENU_DEVICE_MANAGER ? "volume" : "volume", "0.9");
                    }
                }
            });
        } catch (IOException e) {
            Log.e("SKEYE", "Error " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("SkEye initialisation error");
        }
    }

    private void startActivityForLocation() {
        startActivityForResult(new Intent(this, SelectLocationActivity.class), 0);
    }

    private void resumeViews() {
        this.gl3d.onResume();
        updateGLNightMode();
        registerReceiver(this.statusBattery.receiver, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
    }

    private void updateTimes() {
        Date date = new Date(this.timeMgr.getCurrentTime());
        Sky.setTime(date);
        this.timeMgr.updateDisplay(date);
        skyUpdated(System.currentTimeMillis());
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        this.f11vm.resume();
        this.activityPaused = false;
        if (f10om.isAutoPossible()) {
            f10om.startListening();
        }
        updateTimes();
        NightModeMgr.setThemeForActivity(this);
        this.finderView.setTheme(NightModeMgr.getTheme(this));
        resumeViews();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        f10om.stopListening();
        f9dm.stopLocalListening();
        this.myRenderer.onDestroy();
        if (this.tts != null) {
            this.tts.shutdown();
        }
        this.tts = null;
        f9dm = null;
        Sky.onDestroy();
    }

    private void pauseViews() {
        if (!this.activityPaused) {
            this.gl3d.onPause();
            unregisterReceiver(this.statusBattery.receiver);
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        this.f11vm.pause();
        pauseViews();
        if (!this.mSensorNoPause) {
            f10om.stopListening();
        }
        this.activityPaused = true;
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        this.mSensorNoPause = false;
        if (requestCode == 0) {
            switch (resultCode) {
                case DescriptiveStatistics.INFINITE_WINDOW /*{ENCODED_INT: -1}*/:
                    this.isLocationSet = true;
                    this.userLocation = new LocationOnEarth(resultData.getFloatExtra("latitude", 0.0f), resultData.getFloatExtra("longitude", 0.0f), resultData.getFloatExtra("altitude", 0.0f));
                    this.settingsManager.setPrevLocationPref(this.userLocation);
                    completeUserLocationSet();
                    Util.showToast(this, String.valueOf(getString(C0031R.string.location_set)) + " " + resultData.getStringExtra("name"));
                    return;
                default:
                    if (!this.isLocationSet) {
                        finish();
                        return;
                    } else {
                        Util.showToast(this, C0031R.string.location_not_changed);
                        return;
                    }
            }
        } else if (requestCode == 1) {
            switch (resultCode) {
                case DescriptiveStatistics.INFINITE_WINDOW /*{ENCODED_INT: -1}*/:
                    this.f11vm.exitManual();
                    return;
                default:
                    return;
            }
        } else if (requestCode == 3) {
            if (resultCode == -1) {
                startFinding(resultData.getIntExtra("selectedId", 0));
            }
        } else if (requestCode == 4 || requestCode == 5) {
            updateSettings(false);
        }
    }

    private void enableFinderView() {
        if (this.findHerringMode) {
            ((CheckBox) findViewById(C0031R.C0032id.checkboxPlayVoiceInstructions)).setChecked(this.voiceInstructionsEnabled);
            this.finderStatusTxt.setText(Html.fromHtml(String.format(getString(C0031R.string.looking_for, new Object[]{this.mHerringName}), new Object[0])));
            this.finderView.setFindingMode(true);
            this.finderStatusDiv.setVisibility(0);
            this.statusManager.showHerringCoords(this.herringVector);
        }
    }

    private void startFinding(int herringId) {
        this.mHerringId = herringId;
        CatalogedLocation v = Sky.getSkyObject(herringId);
        this.mHerringName = v.getName();
        synchronized (this) {
            this.herringVector = v.getVector();
            this.findHerringMode = true;
            enableFinderView();
        }
    }

    private void updateSettings(boolean immediate) {
        final SharedPreferences sharedPrefs = this.settingsManager.getDefaultSharedPrefs();
        setRequestedOrientation(this.settingsManager.getPrincipalOrientationPref());
        this.mRotateLabelsHandHeld = this.settingsManager.getRotLabelsHHPref();
        this.mRotateLabelsIndirect = this.settingsManager.getRotLabelsHHIndirectPref();
        this.mShowSensorWarnings = this.settingsManager.getShowSensorWarningsPref();
        this.mZoomByVolumeKeys = this.settingsManager.getZoomByVolumeKeysPref();
        if (this.statusViewContainer != null) {
            this.statusViewContainer.setVisibility(8);
        }
        if (immediate) {
            this.myRenderer.updateSettings(sharedPrefs);
        } else {
            this.gl3d.queueEvent(new Runnable() {
                /* class com.lavadip.skeye.SkEye.RunnableC00383 */

                public void run() {
                    SkEye.this.myRenderer.updateSettings(sharedPrefs);
                }
            });
        }
        if (f10om.isAutoPossible()) {
            f9dm.setPrefs(this.settingsManager.getAcclSensitivityPref(), this.settingsManager.getMagSensitivityPref(), this.settingsManager.getSensorFusionSensitivityPref(), this.settingsManager.getUseSensorFusionPref());
        }
    }

    private void completeUserLocationSet() {
        Sky.setUserLocation(this.userLocation);
        GeomagneticField geomagneticField = this.userLocation.getGeoMagneticField();
        double geomagneticDeclination = (double) geomagneticField.getDeclination();
        Log.d("SKEYE", "Geomagnetic decl : " + geomagneticDeclination);
        this.expectedGeoMagneticFieldStrength = geomagneticField.getFieldStrength() / 1000.0f;
        Log.d("SKEYE", "Geomagnetic field strength : " + geomagneticField.getFieldStrength());
        f10om.setGeomagneticDecl((float) geomagneticDeclination);
        if (alignMgr.getNumAligns() > 0) {
            alignMgr.clearAll();
            Util.showToast(this, C0031R.string.cleared_all_alignments);
        }
    }

    /* access modifiers changed from: package-private */
    public void onOrientationChanged(float[] rotation, final Orientation3d currOrientation, boolean manualMode, Long newSkyTime, boolean isRemote) {
        final int dispOrnt;
        long skyTime;
        synchronized (this) {
            this.savedOrientationZDir = currOrientation.zDir;
            long currTime = System.currentTimeMillis();
            boolean newSkyTimeSet = newSkyTime != null;
            if (newSkyTimeSet || Math.abs(currTime - this.lastSkyUpdate) > this.timeMgr.getSkyRefreshInterval()) {
                if (newSkyTimeSet) {
                    skyTime = newSkyTime.longValue();
                } else {
                    skyTime = this.timeMgr.getCurrentTime();
                }
                Date skyDate = new Date(skyTime);
                Sky.setTime(skyDate);
                this.timeMgr.updateDisplay(skyDate);
                skyUpdated(skyTime);
            }
            double changeRate = this.speech.getRateOfChange(currOrientation.zDir);
            if (currTime - this.lastPlayTime > 800 && GetAlignmentActivity.shouldPingNow(this) && changeRate != Double.NEGATIVE_INFINITY) {
                this.lastPlayTime = currTime;
                this.audioTrack.stop();
                this.audioTrack.reloadStaticData();
                this.audioTrack.setPlaybackRate(Math.max(4000, Math.min(AudioTrack.getNativeOutputSampleRate(0), ((int) ((((double) SoundWave.getSamplerate()) * Math.sqrt(changeRate / 5.0E-4d)) / 1000.0d)) * EmpiricalDistribution.DEFAULT_BIN_COUNT)));
                this.audioTrack.play();
            }
            boolean refreshDue = currTime - this.lastScreenRefresh > SCREEN_REFRESH_INTERVAL;
            if (this.isLocationSet && !this.activityPaused && (refreshDue || newSkyTimeSet)) {
                if (manualMode || !this.mShowSensorWarnings) {
                    this.statusViewContainer.setVisibility(8);
                } else {
                    float diffField = Math.abs(f10om.getFieldStrength() - this.expectedGeoMagneticFieldStrength);
                    if (diffField > f9dm.getMagneticToleranceExtreme()) {
                        this.statusView.setText(getText(C0031R.string.sensor_warning_extreme));
                        this.statusViewContainer.setVisibility(0);
                    } else if (diffField > f9dm.getMagneticTolerance()) {
                        this.statusView.setText(getText(C0031R.string.sensor_warning));
                        this.statusViewContainer.setVisibility(0);
                    } else {
                        this.statusViewContainer.setVisibility(8);
                    }
                }
                if (!this.intentProcessed) {
                    Intent myIntent = getIntent();
                    Log.d("SKEYE", "Intent action: " + myIntent.getAction());
                    String action = myIntent.getAction();
                    if (action.equals("android.intent.action.SEARCH")) {
                        int id = CatalogManager.searchObjByUri(myIntent.getData());
                        if (id >= 0) {
                            startFinding(id);
                        } else {
                            Util.showToast(this, "Couldn't find the requested object");
                        }
                    } else if (action.equals("android.intent.action.VIEW")) {
                        this.f11vm.switchToManual(myIntent.getDoubleExtra("RA", 0.0d), myIntent.getDoubleExtra("Declination", 0.0d));
                    }
                    this.intentProcessed = true;
                }
                if (isRemote) {
                    dispOrnt = getBestOrientation();
                } else if (alignMgr.hasAligns()) {
                    dispOrnt = this.mRotateLabelsIndirect ? getBestOrientation() : this.currOrientInt;
                } else {
                    dispOrnt = this.mRotateLabelsHandHeld ? getBestOrientation() : this.currOrientInt;
                }
                if (manualMode || alignMgr.hasAligns()) {
                    System.arraycopy(rotation, 0, this.mDispRotationMatrix, 0, 16);
                } else {
                    remapByOrient(rotation, this.mDispRotationMatrix);
                }
                currOrientation.xDir = new Vector3d(this.mDispRotationMatrix, 0);
                this.gl3d.queueEvent(new Runnable() {
                    /* class com.lavadip.skeye.SkEye.RunnableC00394 */

                    public void run() {
                        SkEye.this.myRenderer.setRotationMatrix(currOrientation, dispOrnt, SkEye.this.currOrientInt);
                        SkEye.this.gl3d.requestRender();
                    }
                });
                synchronized (this) {
                    if (this.findHerringMode) {
                        double herringAngle = this.finderView.setHerringCoords(this.herringVisible, this.herringCoords, this.herringVector, currOrientation, this.currOrientInt, dispOrnt);
                        this.statusManager.setHerringAngle(herringAngle);
                        speakAngle(changeRate, herringAngle, this.herringVector, currOrientation.zDir);
                    } else {
                        this.finderView.setHerringCoords(false, null, null, currOrientation, this.currOrientInt, dispOrnt);
                    }
                    this.statusManager.showAltAz(currOrientation.zDir);
                    synchronized (this.lockCenterObj) {
                        if (this.prevCenteredObj != this.centeredObj || currTime - this.lastObjRefresh > this.timeMgr.getSkyRefreshInterval()) {
                            this.lastObjRefresh = System.currentTimeMillis();
                            this.prevCenteredObj = this.centeredObj;
                            if (this.centeredObj < 0) {
                                this.centeredSearchIcon.setVisibility(4);
                                this.objStatus.setVisibility(4);
                            } else {
                                this.objStatus.setText(Html.fromHtml(Sky.getSkyObject(this.centeredObj).toString()));
                                this.objStatus.setVisibility(0);
                                this.centeredSearchIcon.setVisibility(0);
                            }
                        }
                    }
                    this.lastScreenRefresh = System.currentTimeMillis();
                }
            }
        }
    }

    private void speakAngle(double changeRate, double herringAngle, Vector3d vec, Vector3d zDir) {
        if (this.tts != null && this.voiceInstructionsEnabled) {
            long currentTimeMillis = System.currentTimeMillis();
            double change = Math.abs(this.prevHerringAngle - herringAngle);
            boolean spoken = false;
            long changeTime = System.currentTimeMillis() - this.prevSpokenTime;
            boolean targetAcquired = herringAngle < 0.017453292519943295d;
            int changeTimeThreshold = targetAcquired ? Dfp.RADIX : 4000;
            if (change > 0.017453292519943295d || changeTime > ((long) changeTimeThreshold)) {
                if (targetAcquired) {
                    spoken = speak(String.format("Target acquired!", Double.valueOf(Math.toDegrees(herringAngle))), true);
                } else {
                    spoken = speak(Speech.makeSpokenGuide(changeRate, vec, zDir), false);
                }
            }
            if (spoken) {
                this.prevHerringAngle = herringAngle;
                this.prevSpokenTime = currentTimeMillis;
            }
        }
    }

    private boolean speak(String message, boolean highPriority) {
        if (this.tts == null) {
            return false;
        }
        if (!highPriority && this.tts.isSpeaking()) {
            return false;
        }
        this.tts.speak(message, 0, this.ttsParam);
        return true;
    }

    private int getBestOrientation() {
        return f9dm.getScreenOrientation(this.currOrientInt);
    }

    /* access modifiers changed from: package-private */
    public void remapByOrient(float[] inRotation, float[] outRotation) {
        switch (this.currOrientInt) {
            case 1:
                SensorManager.remapCoordinateSystem(inRotation, 2, 129, outRotation);
                return;
            case 2:
            default:
                System.arraycopy(inRotation, 0, outRotation, 0, 16);
                return;
            case 3:
                SensorManager.remapCoordinateSystem(inRotation, 130, 1, outRotation);
                return;
        }
    }

    private void skyUpdated(final long newSkyTime) {
        this.gl3d.queueEvent(new Runnable() {
            /* class com.lavadip.skeye.SkEye.RunnableC00405 */

            public void run() {
                SkEye.this.myRenderer.updateSky(newSkyTime);
                synchronized (this) {
                    if (SkEye.this.findHerringMode) {
                        LocationInSky v = Sky.getSkyObject(SkEye.this.mHerringId);
                        SkEye.this.herringVector = v.getVector();
                        SkEye.this.myHandler.obtainMessage(0).sendToTarget();
                    }
                }
            }
        });
        this.lastSkyUpdate = System.currentTimeMillis();
    }

    public boolean onTouch(View v, MotionEvent event) {
        try {
            int actionCode = event.getAction() & 255;
            if (actionCode == 6) {
                this.f11vm.resetPinchTwist();
            }
            if (event.getPointerCount() == 1) {
                this.mGestureDetector.onTouchEvent(event);
                return true;
            } else if (event.getPointerCount() != 2) {
                return false;
            } else {
                if (actionCode == 2) {
                    int pId0 = event.getPointerId(0);
                    int pId1 = event.getPointerId(1);
                    float dx = event.getX(pId1) - event.getX(pId0);
                    float dy = event.getY(pId1) - event.getY(pId0);
                    float dist = (float) Math.sqrt((double) ((dx * dx) + (dy * dy)));
                    float angle = (float) Math.atan2((double) dy, (double) dx);
                    if (angle < 0.0f) {
                        angle = (float) (((double) angle) + 6.283185307179586d);
                    }
                    this.f11vm.handlePinchTwist(event.getDownTime(), dist, angle);
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    /* access modifiers changed from: package-private */
    public void setCenteredObj(int centeredObject) {
        synchronized (this.lockCenterObj) {
            if (centeredObject != this.prevCenteredObj) {
                this.prevCenteredObj = this.centeredObj;
                this.centeredObj = centeredObject;
                this.f11vm.setCenteredObj(centeredObject);
            }
        }
    }

    public void onBackPressed() {
        if (this.mMenu.isShowing()) {
            this.mMenu.hide(this);
        } else if (this.findHerringMode) {
            stopFinding(null);
        } else if (exitConfirmationRequired()) {
            confirmAndExit();
        } else {
            doExit();
        }
    }

    private boolean exitConfirmationRequired() {
        switch (m0xfaeeb0eb()[this.settingsManager.getExitConfirmationPref().ordinal()]) {
            case 1:
                return false;
            case 2:
                return alignMgr.hasAligns();
            case 3:
            default:
                return true;
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void setColorTheme(NightModeMgr.Theme newTheme) {
        this.nightModeMgr.setTheme(this, newTheme);
        NightModeMgr.setThemeForActivity(this);
        this.finderView.setTheme(newTheme);
        updateGLNightMode();
    }

    private void askColorTheme() {
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        View content = getLayoutInflater().inflate(C0031R.layout.dialog_choose_color_theme, (ViewGroup) null);
        builder.setTitle(getString(C0031R.string.choose_color_theme));
        builder.setContentView(content);
        builder.setNegativeButton(17039369, new DialogInterface.OnClickListener() {
            /* class com.lavadip.skeye.SkEye.DialogInterface$OnClickListenerC00416 */

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final CustomDialog dialog = builder.create();
        content.findViewById(C0031R.C0032id.color_theme_preview_day_img).setOnClickListener(new View.OnClickListener() {
            /* class com.lavadip.skeye.SkEye.View$OnClickListenerC00427 */

            public void onClick(View v) {
                dialog.dismiss();
                SkEye.this.setColorTheme(NightModeMgr.Theme.Day);
            }
        });
        content.findViewById(C0031R.C0032id.color_theme_preview_dusk_img).setOnClickListener(new View.OnClickListener() {
            /* class com.lavadip.skeye.SkEye.View$OnClickListenerC00438 */

            public void onClick(View v) {
                dialog.dismiss();
                SkEye.this.setColorTheme(NightModeMgr.Theme.Dusk);
            }
        });
        content.findViewById(C0031R.C0032id.color_theme_preview_night_img).setOnClickListener(new View.OnClickListener() {
            /* class com.lavadip.skeye.SkEye.View$OnClickListenerC00449 */

            public void onClick(View v) {
                dialog.dismiss();
                SkEye.this.setColorTheme(NightModeMgr.Theme.Night);
            }
        });
        dialog.show();
    }

    private void confirmAndExit() {
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setMessage(C0031R.string.really_quit);
        builder.setPositiveButton(17039379, new DialogInterface.OnClickListener() {
            /* class com.lavadip.skeye.SkEye.DialogInterface$OnClickListenerC003410 */

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                SkEye.this.doExit();
            }
        });
        builder.setNegativeButton(17039369, new DialogInterface.OnClickListener() {
            /* class com.lavadip.skeye.SkEye.DialogInterface$OnClickListenerC003511 */

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void doExit() {
        boolean z = true;
        f10om.disconnect();
        double[] eqCoords = new double[3];
        Sky.getEqCoords(this.savedOrientationZDir, eqCoords);
        Intent data = new Intent();
        data.putExtra("RA", eqCoords[1]);
        data.putExtra("Declination", eqCoords[2]);
        if (this.centeredObj < 0) {
            z = false;
        }
        data.putExtra("centeredObjPresent", z);
        if (this.centeredObj >= 0) {
            data.putExtra("centeredObjName", Sky.getSkyObject(this.centeredObj).getName());
            Catalog catalog = CatalogManager.catalogs[CatalogManager.getCatalog(this.centeredObj)];
            int objNum = CatalogManager.getObjNum(this.centeredObj);
            float[] positions = catalog.getPositions();
            data.putExtra("centeredObjRa", (double) positions[objNum * 2]);
            data.putExtra("centeredObjDec", (double) positions[(objNum * 2) + 1]);
        }
        setResult(-1, data);
        super.onBackPressed();
    }

    private void enableMenuItem(int itemid, boolean enable) {
        this.mMenu.setEnabled(itemid, enable);
    }

    private void updateMenuItems() {
        boolean z;
        boolean z2 = false;
        if (this.f11vm.isInstaAlignMode()) {
            z = false;
        } else {
            z = true;
        }
        enableMenuItem(0, z);
        if (!this.f11vm.isInstaAlignMode() && f10om.isAutoPossible()) {
            z2 = true;
        }
        enableMenuItem(1, z2);
    }

    public void startActivityForResult(Intent intent, int requestCode) {
        this.mSensorNoPause = true;
        super.startActivityForResult(intent, requestCode);
    }

    private void updateGLNightMode() {
        this.gl3d.queueEvent(new Runnable() {
            /* class com.lavadip.skeye.SkEye.RunnableC003612 */

            public void run() {
                SkEye.this.myRenderer.setTheme(NightModeMgr.getCurrentThemeOrdinal(SkEye.this));
            }
        });
    }

    private void handleMenu(int menuItemId) {
        boolean z = true;
        switch (menuItemId) {
            case 0:
                startActivityForLocation();
                return;
            case 1:
                startActivityForResult(new Intent(this, GetMultiAlignActivity.class), 1);
                return;
            case 2:
                startActivityForResult(new Intent(this, CatalogActivity.class), 5);
                return;
            case 3:
            default:
                return;
            case 4:
                onSearchRequested();
                return;
            case 5:
                askColorTheme();
                return;
            case 6:
                this.settingsManager.toggleFullScreenPref();
                SettingsManager.updateFullScreen(this);
                return;
            case 7:
                QuickSettingsManager quickSettingsManager2 = this.quickSettingsManager;
                if (getResources().getConfiguration().orientation != 1) {
                    z = false;
                }
                quickSettingsManager2.start(z);
                return;
            case 8:
                startActivityForResult(new Intent(this, SettingsActivity.class), 4);
                return;
            case 9:
                showDialog(0);
                return;
            case MENU_TIME_MACHINE /*{ENCODED_INT: 10}*/:
                this.timeMgr.showTimeBar();
                return;
            case MENU_DEVICE_MANAGER /*{ENCODED_INT: 11}*/:
                f9dm.showManagerDialog();
                return;
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        handleMenu(item.getItemId());
        return false;
    }

    /* access modifiers changed from: protected */
    public Dialog onCreateDialog(int id) {
        if (id == 0) {
            return AboutDialogBuilder.create(this);
        }
        if (id == 2) {
            return CustomDialog.createMessageDialog(this, C0031R.string.sensor_tips_title, C0031R.string.sensor_tips);
        }
        if (id == 4) {
            return new CustomDialog.Builder(this).setIcon(17301543).setTitle(C0031R.string.auto_mode_disabled).setMessage(((Object) getText(C0031R.string.sensors_not_present_message)) + "\n\n" + f10om.getSensorPresentMessage(this)).setPositiveButton(getString(17039370), (DialogInterface.OnClickListener) null).create();
        }
        return null;
    }

    public boolean onSearchRequested() {
        Intent searchIntent = new Intent(this, GetAlignmentActivity.class);
        searchIntent.putExtra("search", true);
        startActivityForResult(searchIntent, 3);
        return true;
    }

    public void stopFinding(View v) {
        if (this.findHerringMode) {
            this.finderView.setFindingMode(false);
            this.finderStatusDiv.setVisibility(8);
            this.findHerringMode = false;
        }
    }

    public void clickSensorTips(View v) {
        showDialog(2);
    }

    public void clickCancelInsta(View v) {
        this.f11vm.cancelInstaAlign(false);
    }

    public void clickAddInsta(View v) {
        this.f11vm.completeInstaAlign(true, false);
    }

    public void clickAlignToObj(View v) {
        this.f11vm.alignToObj();
    }

    public void clickAlignToObjManual(View v) {
        this.f11vm.alignToObjManual();
    }

    public void clickSwitchToManual(View v) {
        this.f11vm.switchToManual();
    }

    public void clickExitManual(View v) {
        this.f11vm.exitManual();
    }

    public void clickZoomPlus(View v) {
        this.f11vm.updateFov(0.8f);
    }

    public void clickZoomMinus(View v) {
        this.f11vm.updateFov(1.25f);
    }

    public void clickSearchCentered(View v) {
        if (this.centeredObj >= 0) {
            startFinding(this.centeredObj);
        }
    }

    public void clickMenuButton(View v) {
        toggleMenu();
    }

    public void clickPlayVoiceInstructions(View v) {
        this.voiceInstructionsEnabled = ((CheckBox) v).isChecked();
    }

    public void clickViewTarget(View v) {
        double[] eqCoords = new double[3];
        Sky.getEqCoords(this.herringVector, eqCoords);
        this.f11vm.switchToManual(eqCoords[1], eqCoords[2]);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (!this.mZoomByVolumeKeys || !this.f11vm.handleKeyEvent(event)) {
            return super.dispatchKeyEvent(event);
        }
        return true;
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private GestureListener() {
        }

        /* synthetic */ GestureListener(SkEye skEye, GestureListener gestureListener) {
            this();
        }

        public boolean onDoubleTap(MotionEvent e) {
            SkEye.this.toggleMenu();
            return true;
        }

        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float x1 = e1.getX();
            float x2 = e2.getX();
            float relX = x2 - x1;
            float relY = e2.getY() - e1.getY();
            return SkEye.this.f11vm.onScroll(e1.getDownTime(), Math.sqrt((double) ((relX * relX) + (relY * relY))), Math.atan2((double) relY, (double) relX), e1, e2);
        }
    }

    public static void setupActivity(Activity a) {
        SettingsManager.updateFullScreen(a);
        a.getWindow().addFlags(128);
        View root = a.findViewById(16908290);
        if (root != null) {
            root.setKeepScreenOn(true);
        }
        NightModeMgr.setThemeForActivity(a);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 82) {
            return super.onKeyDown(keyCode, event);
        }
        toggleMenu();
        return true;
    }

    private void setupMenu() {
        this.mMenu = new AnimatedSimpleCustomMenu(C0031R.C0032id.menuAnchor, this, getLayoutInflater(), false);
        this.mMenu.setHideOnSelect(true);
        this.mMenu.setItemsPerLineInPortraitOrientation(3);
        this.mMenu.setItemsPerLineInLandscapeOrientation(4);
        ArrayList<CustomMenuItem> menuItems = new ArrayList<>();
        createCustomMenuItem(menuItems, 4, C0031R.string.menu_opt_search, 17301583);
        createCustomMenuItem(menuItems, MENU_TIME_MACHINE, C0031R.string.menu_opt_timemachine, 17301563);
        createCustomMenuItem(menuItems, 1, C0031R.string.menu_opt_indirect_mode, 17301581);
        createCustomMenuItem(menuItems, 2, C0031R.string.catalogs, 17301556);
        createCustomMenuItem(menuItems, 0, C0031R.string.menu_opt_location, 17301575);
        createCustomMenuItem(menuItems, 5, C0031R.string.menu_opt_change_color_theme, 17301591);
        createCustomMenuItem(menuItems, 7, C0031R.string.menu_opt_quick_settings, 17301577);
        createCustomMenuItem(menuItems, 8, C0031R.string.menu_opt_settings, 17301577);
        createCustomMenuItem(menuItems, MENU_DEVICE_MANAGER, C0031R.string.menu_opt_device_manager, 17301577);
        createCustomMenuItem(menuItems, 9, C0031R.string.menu_opt_about, 17301569);
        createCustomMenuItem(menuItems, 6, C0031R.string.menu_opt_full_screen, 17301562);
        if (!this.mMenu.isShowing()) {
            this.mMenu.setMenuItems(menuItems);
        }
    }

    private void createCustomMenuItem(ArrayList<CustomMenuItem> menuItems, int id, int captionResId, int iconResId) {
        menuItems.add(new CustomMenuItem(getString(captionResId), iconResId, id));
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void toggleMenu() {
        if (this.mMenu.isShowing()) {
            this.mMenu.hide(this);
        } else if (this.quickSettingsManager.currState == QuickSettingsManager.state.HIDDEN) {
            updateMenuItems();
            this.mMenu.show(this);
        }
    }

    @Override // com.lavadip.skeye.AnimatedSimpleCustomMenu.OnMenuItemSelectedListener
    public void MenuItemSelectedEvent(CustomMenuItem selection) {
        handleMenu(selection.getId());
    }

    public void runOnGLThread(Runnable r) {
        this.gl3d.queueEvent(r);
    }

    public void requestGLRedraw() {
        this.gl3d.requestRender();
    }

    public QuickSettingsManager.QuickSettingsGroup mkQuickSettingGroup() {
        return this.finderView.mkQuickSettingGroup(this);
    }
}
