package com.lavadip.skeye.catalog;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.lavadip.skeye.LabelMaker;
import com.lavadip.skeye.LabelPaints;
import com.lavadip.skeye.MyShadyRenderer;
import com.lavadip.skeye.QuickSettingsManager;
import com.lavadip.skeye.SkEye;
import com.lavadip.skeye.astro.Comet;
import com.lavadip.skeye.astro.CometParser;
import com.lavadip.skeye.astro.Instant;
import com.lavadip.skeye.astro.IntList;
import com.lavadip.skeye.astro.keplerian.Orbit;
import com.lavadip.skeye.shader.PointShader;
import com.lavadip.skeye.shader.VectorLineShader;
import com.lavadip.skeye.util.BufferedReaderIterator;
import com.lavadip.skeye.util.Util;
import com.lavadip.skeyepro.C0139R;
import com.lavadip.skeyepro.CometConfigDialog;
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

public final class CometCatalog extends Catalog {
    private static final int MAX_COMETS = 1024;
    private static final int MIDDLE_POS = 7;
    private static final int NUM_POS = 16;
    private static final long REFRESH_PERIOD = 300000;
    private static final double TIME_INCREMENT_DAYS = 0.0625d;
    private static final float[] colorArray = {1.0f, 0.0f, 0.0f, 0.4f, 0.0f, 1.0f, 0.0f, 0.4f, 0.0f, 0.6f, 1.0f, 0.4f, 0.8f, 0.8f, 0.2f, 0.4f, 0.6f, 0.6f, 0.6f, 0.4f, 1.0f, 0.0f, 0.0f, 0.4f, 0.5f, 0.5f, 0.0f, 0.4f, 0.8f, 0.3f, 0.5f, 0.4f, 1.0f, 0.4f, 0.1f, 0.4f, 0.6f, 0.3f, 0.3f, 0.4f, 0.8f, 0.0f, 0.0f, 0.4f, 0.8f, 0.0f, 0.0f, 0.4f, 0.8f, 0.0f, 0.0f, 0.4f, 0.8f, 0.0f, 0.0f, 0.4f, 0.6f, 0.0f, 0.0f, 0.4f};
    private static final String cometAlphaKey = "cometAlpha";
    private static final String labelAlphaKey = "cometLabelAlpha";
    private static final String prefPrefix = "comets";
    private Vector<Comet.CometData>[] cometData = null;
    private String cometStr = "Comet";
    private Comet[] comets = null;
    private long lastUpdate = 0;
    private float maxMagnitude = 0.0f;
    private int nightColorOffset = 0;
    private int numComets = 0;
    private LabelPaints paints;
    private final float[] posns = new float[2048];
    private IntList selObjsCached = null;
    private FloatBuffer trailVertexBuffer = null;

    CometCatalog(int catalogId) {
        super(catalogId, C0139R.string.comets, true, true, false, false, 50.0f, false, 9.0f, true, false);
    }

    public void setMaxMagnitude(Context context, float maxMag) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putFloat("cometsmaxMagnitude", maxMag).commit();
    }

    public float getMaxMagnitude(Context context) {
        return getMaxMagnitude(PreferenceManager.getDefaultSharedPreferences(context));
    }

    private static float getMaxMagnitude(SharedPreferences pref) {
        return pref.getFloat("cometsmaxMagnitude", 16.0f);
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public int getNumObjs() {
        return this.numComets;
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public void initLabels(LabelMaker labelMaker, LabelPaints paints2, float displayScaleFactor) {
        this.displayScaleFactor = displayScaleFactor;
        this.paints = paints2;
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public void drawLabelES(int objNum, float x, float y, MyShadyRenderer r, LabelMaker labelMaker, boolean centerLabelHoriz, boolean centerLabelVert) {
        labelMaker.drawDynamic(r, x, y, this.comets[objNum].name, this.paints.cometPaint, true, false, 6.0f * this.displayScaleFactor);
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public IntList getSelObjs() {
        if (this.selObjsCached == null) {
            updateSelObjCache();
        }
        return this.selObjsCached;
    }

    private void updateSelObjCache() {
        IntList retVal = new IntList(this.numComets);
        if (this.cometData != null) {
            for (int i = 0; i < this.numComets; i++) {
                if (this.cometData[i] != null && this.cometData[i].get(7).apparentMagnitude < ((double) this.maxMagnitude)) {
                    retVal.add(makeObjId(i));
                }
            }
        }
        this.selObjsCached = retVal;
    }

    /* access modifiers changed from: protected */
    @Override // com.lavadip.skeye.catalog.Catalog
    public boolean shouldPrecess() {
        return false;
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public String getName(int objNum) {
        return this.comets[objNum].name;
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public float[] getPositions() {
        return this.posns;
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public synchronized float getMag(int objNum) {
        return (float) this.cometData[objNum].get(7).apparentMagnitude;
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public void setTheme(int themeOrdinal) {
        this.nightColorOffset = Util.chooseColorOffset(colorArray.length, themeOrdinal);
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public void drawES20(MyShadyRenderer myRenderer, IntList currBlocks, float currFov) {
        this.trailVertexBuffer.position(0);
        VectorLineShader lineShader = myRenderer.mVectorLineShader;
        lineShader.activate();
        lineShader.setupLine(1.5f * this.displayScaleFactor);
        lineShader.setupMvp(myRenderer.mvpMatrix);
        lineShader.setupVertexBuffer(this.trailVertexBuffer);
        lineShader.setupColors(colorArray, this.nightColorOffset + 16);
        for (int i = 0; i < this.selObjsCached.size; i++) {
            VectorLineShader.draw(3, CatalogManager.getObjNum(this.selObjsCached.get(i)) * 16, 8);
        }
        for (int i2 = 0; i2 < this.selObjsCached.size; i2++) {
            int id = CatalogManager.getObjNum(this.selObjsCached.get(i2));
            lineShader.setupColors(colorArray, this.nightColorOffset + ((i2 % 4) * 4));
            VectorLineShader.draw(3, (id * 16) + 7, 9);
        }
        PointShader pointShader = myRenderer.mPointShader;
        pointShader.beginDrawing(MyShadyRenderer.mCometTextureId);
        pointShader.setupMVP(myRenderer.mvpMatrix);
        pointShader.setupVertexBuffer(this.myVertexArray);
        for (int i3 = 0; i3 < this.selObjsCached.size; i3++) {
            int id2 = CatalogManager.getObjNum(this.selObjsCached.get(i3));
            int colorOffset = this.nightColorOffset + ((i3 % 4) * 4);
            float scale = Math.max((float) (32.0d - this.cometData[id2].get(7).apparentMagnitude), 1.0f) * this.displayScaleFactor;
            pointShader.setupColors(colorArray, colorOffset);
            pointShader.setupSize(scale);
            pointShader.draw(i3, 1);
        }
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public synchronized void updateSky(long currTime) {
        if (Math.abs(currTime - this.lastUpdate) > REFRESH_PERIOD) {
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            cal.setTimeInMillis(currTime);
            int year = cal.get(1);
            int month = cal.get(2) + 1;
            int day = cal.get(5);
            double dayFraction = ((((double) cal.get(11)) + (((double) cal.get(12)) / 60.0d)) + (((double) cal.get(13)) / 3600.0d)) / 24.0d;
            this.trailVertexBuffer.position(0);
            for (int i = 0; i < this.numComets; i++) {
                this.cometData[i] = getOrbitData(this.comets[i], year, month, day, dayFraction);
                for (int m = 0; m < 16; m++) {
                    Orbit.OrbitInstantData posn = this.cometData[i].get(m).posData;
                    Util.map3d(posn.f48RA, posn.Dec).putXYZ(this.trailVertexBuffer, 9.0d);
                    if (m == 7) {
                        this.posns[i * 2] = (float) posn.f48RA;
                        this.posns[(i * 2) + 1] = (float) posn.Dec;
                    }
                }
            }
            this.trailVertexBuffer.position(0);
            updateSelObjCache();
            updateVecPositions();
            updateVertexArray(true);
            this.lastUpdate = currTime;
        }
    }

    private static Vector<Comet.CometData> getOrbitData(Comet comet, int year, int month, int day, double dayFraction) {
        Vector<Comet.CometData> cometInstantData = new Vector<>(16);
        for (int i = 0; i < 16; i++) {
            cometInstantData.add(comet.getDataForInstant(new Instant(year, month, day, dayFraction + (TIME_INCREMENT_DAYS * ((double) (i - 7))))));
        }
        return cometInstantData;
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public void init(SkEye skeye) throws IOException {
        System.out.println("Init comets");
        this.cometStr = skeye.getString(C0139R.string.comet);
        this.maxMagnitude = getMaxMagnitude(skeye);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(skeye.getAssets().openFd("comet_tle.jet").createInputStream()));
            this.comets = CometParser.parseComets(new BufferedReaderIterator(reader));
            this.numComets = this.comets.length;
            reader.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        this.cometData = new Vector[this.numComets];
        this.lastUpdate = 0;
        updateSelObjCache();
        this.trailVertexBuffer = ByteBuffer.allocateDirect(this.numComets * 16 * 3 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        updateSky(System.currentTimeMillis());
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public String getTypeDescr(int objNum) {
        Vector<Comet.CometData> data = this.cometData[objNum];
        if (data == null) {
            return "Comet orbit couldn't be calculated.";
        }
        return String.format(Locale.US, String.valueOf(this.cometStr) + ", Apparent Mag: %.2f.", Double.valueOf(data.get(7).apparentMagnitude));
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public String getTypeDescrForSearch(int objNum) {
        Vector<Comet.CometData> data = this.cometData[objNum];
        if (data == null) {
            return "Comet orbit couldn't be calculated.";
        }
        String dateStr = this.comets[objNum].orbit.elems.epochPeriapsis.formatYYYYMMDD();
        return String.format(Locale.US, "Apparent Mag: %.2f. Perihelion: %s", Double.valueOf(data.get(7).apparentMagnitude), dateStr);
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public void startConfig(Activity catalogActivity) {
        new CometConfigDialog(catalogActivity, C0139R.style.NightTheme_NoTitle_FullScreen, this).show();
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public boolean updateSettings(SharedPreferences pref) {
        float newMaxMagnitude = getMaxMagnitude(pref);
        if (newMaxMagnitude == this.maxMagnitude) {
            return false;
        }
        this.maxMagnitude = newMaxMagnitude;
        updateSelObjCache();
        updateVecPositions();
        updateVertexArray(true);
        return true;
    }

    private static float getCurrKey(SkEye skeye, String key, float initialValue) {
        return skeye.settingsManager.getQuickPref(key, initialValue);
    }

    protected static void setCometAlpha(float alpha) {
        for (int i = 0; i < 5; i++) {
            for (int mode = 0; mode < 3; mode++) {
                colorArray[(((mode * 5) + i) * 4) + 3] = 0.8f * alpha;
            }
        }
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public QuickSettingsManager.QuickSettingsGroup getQuickSettings(SkEye skeye, MyShadyRenderer renderer) {
        return new QuickSettingsManager.QuickSettingsGroup(new QuickSettingsManager.SettingDetails[]{new QuickSettingsManager.SettingDetails(new QuickSettingsManager.FloatRangeQuickSetting(labelAlphaKey, skeye.getString(C0139R.string.label_opacity), "", 0.0f, 1.0f, 0.05f), new QuickSettingsManager.TypicalSettingChangeHandler<Float>(skeye) {
            /* class com.lavadip.skeye.catalog.CometCatalog.C00761 */

            @Override // com.lavadip.skeye.QuickSettingsManager.SettingChangeListener
            public /* bridge */ /* synthetic */ void onChange(String str, Object obj, boolean z) {
                onChange(str, (Float) obj, z);
            }

            @Override // com.lavadip.skeye.QuickSettingsManager.TypicalSettingChangeHandler
            public void onGLThread(String key, Float newValue, boolean trackingStopped) {
                CometCatalog.this.paints.cometPaint.setAlpha((int) (255.0f * newValue.floatValue()));
            }
        }, Float.valueOf(getCurrKey(skeye, labelAlphaKey, 0.4f))), new QuickSettingsManager.SettingDetails(new QuickSettingsManager.FloatRangeQuickSetting(cometAlphaKey, skeye.getString(C0139R.string.marker_opacity), "", 0.0f, 1.0f, 0.02f), new QuickSettingsManager.TypicalSettingChangeHandler<Float>(skeye) {
            /* class com.lavadip.skeye.catalog.CometCatalog.C00772 */

            @Override // com.lavadip.skeye.QuickSettingsManager.SettingChangeListener
            public /* bridge */ /* synthetic */ void onChange(String str, Object obj, boolean z) {
                onChange(str, (Float) obj, z);
            }

            @Override // com.lavadip.skeye.QuickSettingsManager.TypicalSettingChangeHandler
            public void onGLThread(String key, Float newValue, boolean trackingStopped) {
                CometCatalog.setCometAlpha(newValue.floatValue());
            }
        }, Float.valueOf(getCurrKey(skeye, cometAlphaKey, 0.5f)))}, skeye.getString(C0139R.string.comets), prefPrefix);
    }
}
