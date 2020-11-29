package com.lavadip.skeye.catalog;

import android.annotation.TargetApi;
import android.content.res.Resources;
import com.lavadip.skeye.C0031R;
import com.lavadip.skeye.LabelMaker;
import com.lavadip.skeye.LabelPaints;
import com.lavadip.skeye.MyRenderer;
import com.lavadip.skeye.MyShadyRenderer;
import com.lavadip.skeye.QuickSettingsManager;
import com.lavadip.skeye.SkEye;
import com.lavadip.skeye.astro.ConstellationLabels;
import com.lavadip.skeye.astro.Constellations;
import com.lavadip.skeye.astro.IntList;
import com.lavadip.skeye.util.Util;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class ConstellationCatalog extends Catalog {
    private static final int NUM_OBJS = (ConstellationLabels.positions.length / 2);
    private static String constellation_string;
    private static String[] names;
    protected final float[] colorArray = {1.0f, 0.94f, 0.16f, 0.4f, 1.0f, 0.47f, 0.08f, 0.4f, 1.0f, 0.0f, 0.0f, 0.4f};
    private int colorOffset = 0;
    private ShortBuffer constLineIndexArray = null;
    private FloatBuffer constVertexArray = null;
    private float labelAlpha = 1.0f;
    private int labelBaseId = 0;

    /* access modifiers changed from: private */
    public enum SettingKeys {
        ConstellationLines("constLineAlpha"),
        LabelAlpha("constLabelAlpha");
        
        final String key;

        private SettingKeys(String key2) {
            this.key = key2;
        }
    }

    ConstellationCatalog(int catalogId) {
        super(catalogId, C0031R.string.constellations, true, false, false, false, 75.0f, true, 9.0f, false, true);
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public int getNumObjs() {
        return NUM_OBJS;
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public IntList getSelObjs() {
        return getAllObjs();
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public String getName(int objNum) {
        return names[objNum];
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public float[] getPositions() {
        return ConstellationLabels.positions;
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public float getMag(int objNum) {
        return Float.NaN;
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public String getTypeDescr(int objNum) {
        return constellation_string;
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public String getTypeDescrForSearch(int objNum) {
        return null;
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public void initLabels(LabelMaker labelMaker, LabelPaints paints, float a_displayScaleFactor) {
        for (int i = 0; i < NUM_OBJS; i++) {
            int labelId = labelMaker.add(getName(i), paints.constPaint);
            if (i == 0) {
                this.labelBaseId = labelId;
            }
        }
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public void drawLabelES(int objNum, float x, float y, MyShadyRenderer r, LabelMaker labelMaker, boolean centerLabelHoriz, boolean centerLabelVert) {
        labelMaker.drawES20(r, x, y, this.labelBaseId + objNum, true, false, 0.0f, this.labelAlpha);
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public void init(SkEye skeye) throws IOException {
        Resources res = skeye.getResources();
        names = res.getStringArray(C0031R.array.constellations);
        constellation_string = res.getString(C0031R.string.constellation);
        float[] constellationVertices = Constellations.starPos;
        this.constVertexArray = ByteBuffer.allocateDirect(constellationVertices.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.constVertexArray.position(0);
        this.constVertexArray.put(constellationVertices);
        this.constVertexArray.position(0);
        this.constLineIndexArray = MyRenderer.setLineIndex(Constellations.lines);
        setConstellationAlpha(getCurrConstLineAlpha(skeye));
        this.labelAlpha = getCurrLabelAlpha(skeye);
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    @TargetApi(8)
    public void drawES20(MyShadyRenderer myRenderer, IntList currBlocks, float currFov) {
        myRenderer.mLineShader.draw(2.0f * this.displayScaleFactor, myRenderer.mvpPrecessMatrix, this.colorArray, this.colorOffset, this.constVertexArray, this.constLineIndexArray, this.constLineIndexArray.capacity());
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public void onDestroy() {
        super.onDestroy();
        this.constLineIndexArray = null;
    }

    /* access modifiers changed from: protected */
    public void setConstellationAlpha(float alpha) {
        int stride = this.colorArray.length / 3;
        for (int i = 0; i < this.colorArray.length; i += stride) {
            this.colorArray[i + 3] = alpha;
        }
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public void setTheme(int themeOrdinal) {
        this.colorOffset = Util.chooseColorOffset(this.colorArray.length, themeOrdinal);
    }

    private static float getCurrConstLineAlpha(SkEye skeye) {
        return skeye.settingsManager.getQuickPref(SettingKeys.ConstellationLines.key, 0.3f);
    }

    private static float getCurrLabelAlpha(SkEye skeye) {
        return skeye.settingsManager.getQuickPref(SettingKeys.ConstellationLines.key, 0.5f);
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public QuickSettingsManager.QuickSettingsGroup getQuickSettings(SkEye skeye, MyShadyRenderer renderer) {
        return new QuickSettingsManager.QuickSettingsGroup(new QuickSettingsManager.SettingDetails[]{new QuickSettingsManager.SettingDetails(new QuickSettingsManager.FloatRangeQuickSetting(SettingKeys.ConstellationLines.key, skeye.getString(C0031R.string.line_art_opacity), "", 0.0f, 1.0f, 0.02f), new QuickSettingsManager.TypicalSettingChangeHandler<Float>(skeye) {
            /* class com.lavadip.skeye.catalog.ConstellationCatalog.C00781 */

            @Override // com.lavadip.skeye.QuickSettingsManager.SettingChangeListener
            public /* bridge */ /* synthetic */ void onChange(String str, Object obj, boolean z) {
                onChange(str, (Float) obj, z);
            }

            @Override // com.lavadip.skeye.QuickSettingsManager.TypicalSettingChangeHandler
            public void onGLThread(String key, Float newValue, boolean trackingStopped) {
                ConstellationCatalog.this.setConstellationAlpha(newValue.floatValue());
            }
        }, Float.valueOf(getCurrConstLineAlpha(skeye))), new QuickSettingsManager.SettingDetails(new QuickSettingsManager.FloatRangeQuickSetting(SettingKeys.ConstellationLines.key, skeye.getString(C0031R.string.label_opacity), "", 0.0f, 1.0f, 0.02f), new QuickSettingsManager.TypicalSettingChangeHandler<Float>(skeye) {
            /* class com.lavadip.skeye.catalog.ConstellationCatalog.C00792 */

            @Override // com.lavadip.skeye.QuickSettingsManager.SettingChangeListener
            public /* bridge */ /* synthetic */ void onChange(String str, Object obj, boolean z) {
                onChange(str, (Float) obj, z);
            }

            @Override // com.lavadip.skeye.QuickSettingsManager.TypicalSettingChangeHandler
            public void onGLThread(String key, Float newValue, boolean trackingStopped) {
                ConstellationCatalog.this.labelAlpha = newValue.floatValue();
            }
        }, Float.valueOf(getCurrLabelAlpha(skeye)))}, skeye.getString(C0031R.string.constellations), "constellations");
    }
}
