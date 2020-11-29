package com.lavadip.skeye.catalog;

import android.app.Activity;
import android.os.Build;
import android.util.Log;
import com.lavadip.skeye.LabelMaker;
import com.lavadip.skeye.LabelPaints;
import com.lavadip.skeye.MyShadyRenderer;
import com.lavadip.skeye.QuickSettingsManager;
import com.lavadip.skeye.SkEye;
import com.lavadip.skeye.Vector3d;
import com.lavadip.skeye.astro.IntList;
import com.lavadip.skeye.astro.Sky;
import com.lavadip.skeye.astro.sgp4v.SatElset;
import com.lavadip.skeye.astro.sgp4v.SatElsetException;
import com.lavadip.skeye.astro.sgp4v.Sgp4Data;
import com.lavadip.skeye.astro.sgp4v.Sgp4Unit;
import com.lavadip.skeye.shader.PointShader;
import com.lavadip.skeye.shader.VectorLineShader;
import com.lavadip.skeye.util.Util;
import com.lavadip.skeyepro.C0139R;
import com.lavadip.skeyepro.SatelliteConfigDialog;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Vector;

public final class SatelliteCatalog extends Catalog {
    private static final long MAX_DIFF_TIME_MILLIS = 13500;
    private static final int MAX_SAT = 1024;
    private static final int MIDDLE_POS = 4;
    private static final int NUM_POS = 9;
    private static final double TIME_INCREMENT = 0.5d;
    private static final float[] colorArray = {1.0f, 0.0f, 0.0f, 0.4f, 0.0f, 1.0f, 0.0f, 0.4f, 0.0f, 0.6f, 1.0f, 0.4f, 0.8f, 0.8f, 0.2f, 0.4f, 1.0f, 0.0f, 0.0f, 0.2f, 0.0f, 1.0f, 0.0f, 0.2f, 0.0f, 0.6f, 1.0f, 0.2f, 0.8f, 0.8f, 0.2f, 0.2f, 0.6f, 0.6f, 0.6f, 0.4f, 0.6f, 0.6f, 0.6f, 0.2f, 1.0f, 0.0f, 0.0f, 0.4f, 0.5f, 0.5f, 0.0f, 0.4f, 0.8f, 0.3f, 0.5f, 0.4f, 1.0f, 0.4f, 0.1f, 0.4f, 1.0f, 0.0f, 0.0f, 0.2f, 1.0f, 1.0f, 0.0f, 0.2f, 1.0f, 0.6f, 1.0f, 0.2f, 1.0f, 0.4f, 0.1f, 0.2f, 0.6f, 0.3f, 0.3f, 0.4f, 0.6f, 0.3f, 0.3f, 0.2f, 0.8f, 0.0f, 0.0f, 0.4f, 0.8f, 0.0f, 0.0f, 0.4f, 0.8f, 0.0f, 0.0f, 0.4f, 0.8f, 0.0f, 0.0f, 0.4f, 1.0f, 0.0f, 0.0f, 0.2f, 1.0f, 0.0f, 0.0f, 0.2f, 1.0f, 0.0f, 0.0f, 0.2f, 1.0f, 0.0f, 0.0f, 0.2f, 0.6f, 0.0f, 0.0f, 0.4f, 0.6f, 0.0f, 0.0f, 0.2f};
    private static final String labelAlphaKey = "satelliteLabelAlpha";
    private static final String satelliteAlphaKey = "satelliteAlpha";
    private boolean[] aboveHorizon = null;
    private IntList allObjsCached = null;
    private final boolean isNexusWorkaround = checkNexusVersion();
    private long lastUpdateTime = 0;
    private String[] names = null;
    private int nightColorOffset = 0;
    private int numSat = 0;
    private LabelPaints paints;
    final float[] posns = new float[2048];
    private Vector<Sgp4Data>[] sgp4Data = null;
    private Sgp4Unit[] sgp4units = null;
    private final float[] trailPositions = new float[27];
    private FloatBuffer trailVertexBuffer = null;

    SatelliteCatalog(int catalogId) {
        super(catalogId, C0139R.string.satellites, true, true, false, false, 50.0f, false, 9.0f, false, false);
    }

    private static boolean checkNexusVersion() {
        return Build.MODEL.contains("Nexus") && 22 == Build.VERSION.SDK_INT;
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public int getNumObjs() {
        return this.numSat;
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public void initLabels(LabelMaker labelMaker, LabelPaints paints2, float displayScaleFactor) {
        this.displayScaleFactor = displayScaleFactor;
        this.paints = paints2;
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public void drawLabelES(int objNum, float x, float y, MyShadyRenderer r, LabelMaker labelMaker, boolean centerLabelHoriz, boolean centerLabelVert) {
        labelMaker.drawDynamic(r, x, y, this.names[objNum], this.paints.satellitePaint, true, false, 6.0f * this.displayScaleFactor);
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public IntList getSelObjs() {
        int numObjs = this.numSat;
        if (this.allObjsCached == null || this.allObjsCached.size != numObjs) {
            IntList retVal = new IntList(numObjs);
            for (int i = 0; i < numObjs; i++) {
                retVal.add(makeObjId(i));
            }
            this.allObjsCached = retVal;
        }
        return this.allObjsCached;
    }

    /* access modifiers changed from: protected */
    @Override // com.lavadip.skeye.catalog.Catalog
    public boolean shouldPrecess() {
        return false;
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public String getName(int objNum) {
        return this.names[objNum];
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public float[] getPositions() {
        return this.posns;
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public float getMag(int objNum) {
        return Float.NaN;
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public void setTheme(int themeOrdinal) {
        this.nightColorOffset = Util.chooseColorOffset(colorArray.length, themeOrdinal);
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public void drawES20(MyShadyRenderer myRenderer, IntList currBlocks, float currFov) {
        if (!this.isNexusWorkaround) {
            this.trailVertexBuffer.position(0);
            VectorLineShader lineShader = myRenderer.mVectorLineShader;
            lineShader.activate();
            lineShader.setupLine(1.0f * this.displayScaleFactor);
            lineShader.setupMvp(myRenderer.mvpMatrix);
            lineShader.setupVertexBuffer(this.trailVertexBuffer);
            for (int i = 0; i < this.numSat; i++) {
                int colorOffset = this.nightColorOffset + ((i % 4) * 4);
                boolean isAboveHorizon = this.aboveHorizon[i];
                int adjustedColorOffset = isAboveHorizon ? colorOffset : colorOffset + 16;
                lineShader.setupColors(colorArray, (isAboveHorizon ? 32 : 36) + this.nightColorOffset);
                VectorLineShader.draw(3, i * 9, 5);
                lineShader.setupColors(colorArray, adjustedColorOffset);
                VectorLineShader.draw(3, (i * 9) + 4, 5);
            }
            PointShader pointShader = myRenderer.mPointShader;
            pointShader.beginDrawing(MyShadyRenderer.mSatelliteTextureId);
            pointShader.setupMVP(myRenderer.mvpMatrix);
            this.myVertexArray.position(0);
            pointShader.setupVertexBuffer(this.myVertexArray);
            for (int i2 = 0; i2 < this.numSat; i2++) {
                int colorOffset2 = this.nightColorOffset + ((i2 % 4) * 4);
                boolean isAboveHorizon2 = this.aboveHorizon[i2];
                pointShader.setupColors(colorArray, isAboveHorizon2 ? colorOffset2 : colorOffset2 + 16);
                pointShader.setupSize((isAboveHorizon2 ? 24.0f : 16.0f) * this.displayScaleFactor);
                pointShader.draw(i2, 1);
            }
        }
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public void updateSky(long currTime) {
        boolean updateRequiredBecauseOfTimeDiff = Math.abs(currTime - this.lastUpdateTime) > MAX_DIFF_TIME_MILLIS;
        float userLat = Sky.getUserLatitude();
        double r = 6378.137d + Sky.getUserAltitudeKm();
        double lmst = Sky.getLmst();
        Vector3d posnObs = new Vector3d(Math.cos(lmst) * r * Math.cos((double) userLat), Math.sin(lmst) * r * Math.cos((double) userLat), r * Math.sin((double) userLat));
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.setTimeInMillis(currTime);
        int year = cal.get(1);
        double day = ((double) cal.get(6)) + (((((double) cal.get(11)) + (((double) cal.get(12)) / 60.0d)) + (((double) cal.get(13)) / 3600.0d)) / 24.0d);
        this.trailVertexBuffer.position(0);
        for (int i = 0; i < this.numSat; i++) {
            boolean previouslyAboveHorizon = this.aboveHorizon[i];
            if (updateRequiredBecauseOfTimeDiff || previouslyAboveHorizon) {
                this.aboveHorizon[i] = false;
                try {
                    Vector<Sgp4Data> satData = this.sgp4units[i].runSgp4(year, day, 9, TIME_INCREMENT);
                    this.sgp4Data[i] = satData;
                    int m = 0;
                    int pindx = 0;
                    while (m < 9) {
                        Vector3d posn = satData.get(m).getPosn();
                        Vector3d posnECI = new Vector3d(posn.f16x * r, posn.f17y * r, posn.f18z * r);
                        Vector3d posnObsCentr = new Vector3d(posnECI.f16x - posnObs.f16x, posnECI.f17y - posnObs.f17y, posnECI.f18z - posnObs.f18z);
                        Vector3d topoCentrVec = new Vector3d(posnObsCentr.f17y, posnObsCentr.f18z, posnObsCentr.f16x, true);
                        topoCentrVec.putXYZ(this.trailPositions, pindx, 9.0d);
                        if (!this.aboveHorizon[i] && m >= 4 && topoCentrVec.rotateAboutYaxis(-lmst).rotateAboutXaxis((double) userLat).f18z > 0.0d) {
                            this.aboveHorizon[i] = true;
                        }
                        if (m == 4) {
                            this.posns[i * 2] = (float) Math.atan2(posnObsCentr.f17y, posnObsCentr.f16x);
                            this.posns[(i * 2) + 1] = (float) Math.asin(posnObsCentr.f18z / posnObsCentr.length());
                        }
                        m++;
                        pindx += 3;
                    }
                    this.trailVertexBuffer.position(i * 9 * 3);
                    this.trailVertexBuffer.put(this.trailPositions);
                } catch (Sgp4Unit.ObjectDecayed e) {
                    e.printStackTrace();
                    float[] empty = {0.0f, 0.0f, 0.0f};
                    for (int m2 = 0; m2 < 9; m2++) {
                        this.trailVertexBuffer.put(empty);
                    }
                    Log.e("SKEYE", "error with sat : " + i);
                } catch (SatElsetException e2) {
                    e2.printStackTrace();
                    float[] empty2 = {0.0f, 0.0f, 0.0f};
                    for (int m3 = 0; m3 < 9; m3++) {
                        this.trailVertexBuffer.put(empty2);
                    }
                    Log.e("SKEYE", "error with sat : " + i);
                }
            }
        }
        this.trailVertexBuffer.position(0);
        updateVecPositions();
        updateVertexArray(true);
        if (updateRequiredBecauseOfTimeDiff) {
            this.lastUpdateTime = currTime;
        }
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public void init(SkEye skeye) throws IOException {
        this.names = new String[MAX_SAT];
        this.sgp4units = new Sgp4Unit[MAX_SAT];
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(skeye.getAssets().openFd("sat_tle.jet").createInputStream()));
            boolean eof = false;
            int i = 0;
            do {
                String name = reader.readLine();
                if (name != null) {
                    this.names[i] = name.trim();
                    String card1 = reader.readLine();
                    if (card1 != null) {
                        String card2 = reader.readLine();
                        if (card2 != null) {
                            this.sgp4units[i] = new Sgp4Unit(new SatElset(this.names[i], card1, card2));
                            i++;
                            continue;
                        } else {
                            eof = true;
                            continue;
                        }
                    } else {
                        eof = true;
                        continue;
                    }
                } else {
                    eof = true;
                    continue;
                }
            } while (!eof);
            this.numSat = i;
            this.aboveHorizon = new boolean[this.numSat];
            reader.close();
        } catch (Sgp4Unit.ObjectDecayed e) {
            e.printStackTrace();
        } catch (SatElsetException e2) {
            e2.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        this.sgp4Data = new Vector[this.numSat];
        this.trailVertexBuffer = ByteBuffer.allocateDirect(this.numSat * 9 * 3 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public String getTypeDescr(int objNum) {
        Vector<Sgp4Data> data = this.sgp4Data[objNum];
        if (data == null) {
            return "Orbit decayed?";
        }
        Sgp4Data currPos = data.get(4);
        return String.format(Locale.US, "%s Alt: %.2f km Vel: %.2f km/s", this.sgp4units[objNum].getIntDesig(), Double.valueOf(currPos.getAltitudeKm()), Double.valueOf(currPos.getVelKmPerSec()));
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public void startConfig(Activity catalogActivity) {
        new SatelliteConfigDialog(catalogActivity, C0139R.style.NightTheme_NoTitle_FullScreen, this).show();
    }

    private static float getCurrKey(SkEye skeye, String key, float initialValue) {
        return skeye.settingsManager.getQuickPref(key, initialValue);
    }

    protected static void setSatelliteAlpha(float alpha) {
        float factor;
        for (int i = 0; i < 10; i++) {
            if (i < 4) {
                factor = 0.8f * alpha;
            } else if (i < 8) {
                factor = 0.4f * alpha;
            } else if (i < 9) {
                factor = 0.8f;
            } else {
                factor = 0.4f;
            }
            for (int mode = 0; mode < 3; mode++) {
                colorArray[(((mode * 10) + i) * 4) + 3] = factor * alpha;
            }
        }
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public QuickSettingsManager.QuickSettingsGroup getQuickSettings(SkEye skeye, MyShadyRenderer renderer) {
        return new QuickSettingsManager.QuickSettingsGroup(new QuickSettingsManager.SettingDetails[]{new QuickSettingsManager.SettingDetails(new QuickSettingsManager.FloatRangeQuickSetting(labelAlphaKey, skeye.getString(C0139R.string.label_opacity), "", 0.0f, 1.0f, 0.05f), new QuickSettingsManager.TypicalSettingChangeHandler<Float>(skeye) {
            /* class com.lavadip.skeye.catalog.SatelliteCatalog.C00831 */

            @Override // com.lavadip.skeye.QuickSettingsManager.SettingChangeListener
            public /* bridge */ /* synthetic */ void onChange(String str, Object obj, boolean z) {
                onChange(str, (Float) obj, z);
            }

            @Override // com.lavadip.skeye.QuickSettingsManager.TypicalSettingChangeHandler
            public void onGLThread(String key, Float newValue, boolean trackingStopped) {
                SatelliteCatalog.this.paints.satellitePaint.setAlpha((int) (255.0f * newValue.floatValue()));
            }
        }, Float.valueOf(getCurrKey(skeye, labelAlphaKey, 0.4f))), new QuickSettingsManager.SettingDetails(new QuickSettingsManager.FloatRangeQuickSetting(satelliteAlphaKey, skeye.getString(C0139R.string.marker_opacity), "", 0.0f, 1.0f, 0.02f), new QuickSettingsManager.TypicalSettingChangeHandler<Float>(skeye) {
            /* class com.lavadip.skeye.catalog.SatelliteCatalog.C00842 */

            @Override // com.lavadip.skeye.QuickSettingsManager.SettingChangeListener
            public /* bridge */ /* synthetic */ void onChange(String str, Object obj, boolean z) {
                onChange(str, (Float) obj, z);
            }

            @Override // com.lavadip.skeye.QuickSettingsManager.TypicalSettingChangeHandler
            public void onGLThread(String key, Float newValue, boolean trackingStopped) {
                SatelliteCatalog.setSatelliteAlpha(newValue.floatValue());
            }
        }, Float.valueOf(getCurrKey(skeye, satelliteAlphaKey, 0.5f)))}, skeye.getString(C0139R.string.satellites), "satellites");
    }
}
