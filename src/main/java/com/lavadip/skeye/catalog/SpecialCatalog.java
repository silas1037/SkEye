package com.lavadip.skeye.catalog;

import android.annotation.TargetApi;
import android.opengl.GLES20;
import com.lavadip.skeye.C0031R;
import com.lavadip.skeye.LabelMaker;
import com.lavadip.skeye.LabelPaints;
import com.lavadip.skeye.MyRenderer;
import com.lavadip.skeye.MyShadyRenderer;
import com.lavadip.skeye.QuickSettingsManager;
import com.lavadip.skeye.SkEye;
import com.lavadip.skeye.astro.IntList;
import com.lavadip.skeye.util.Util;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public final class SpecialCatalog extends Catalog {
    private static final short NUM_DEC_DIVS = 8;
    private static final short NUM_POS = 798;
    private static final short NUM_RA_DIVS = 24;
    private static final int NUM_RA_POINTS = 48;
    private static final int SEMISPHERE_POINTS = 384;
    private static final int SOUTHERN_OFFSET = 768;
    private static final String eqGridAlphaKey = "eqGridAlpha";
    private static final float[] eqGridPos = new float[1596];
    private final float[] colorArray = {0.25f, 0.25f, 0.7f, 0.7f, 0.25f, 0.5f, 0.7f, 0.7f, 0.4f, 0.125f, 0.35f, 0.7f, 0.4f, 0.25f, 0.35f, 0.7f, 0.5f, 0.0f, 0.0f, 0.7f, 0.5f, 0.0f, 0.0f, 0.7f};
    private int colorOffset = 0;
    private ShortBuffer eqGridLineIndexArray;

    SpecialCatalog(int catalogId) {
        super(catalogId, C0031R.string.special_catalog_tag, false, false, false, false, 0.0f, false, MyRenderer.DEPTH_GRID * 2.0f, false, initGridPos());
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public void init(SkEye skeye) throws IOException {
        setEqGridAlpha(getCurrEqGridAlpha(skeye));
    }

    /* access modifiers changed from: protected */
    @Override // com.lavadip.skeye.catalog.Catalog
    public boolean shouldPrecess() {
        return false;
    }

    private static boolean initGridPos() {
        eqGridPos[0] = 0.0f;
        eqGridPos[1] = Util.toRadians(90.0f);
        eqGridPos[2] = 0.0f;
        eqGridPos[3] = Util.toRadians(-90.0f);
        float[] fArr = eqGridPos;
        float[] fArr2 = eqGridPos;
        float[] fArr3 = eqGridPos;
        eqGridPos[11] = 0.0f;
        fArr3[9] = 0.0f;
        fArr2[7] = 0.0f;
        fArr[5] = 0.0f;
        eqGridPos[4] = Util.toRadians(0.0f);
        eqGridPos[6] = Util.toRadians(90.0f);
        eqGridPos[8] = Util.toRadians(180.0f);
        eqGridPos[10] = Util.toRadians(270.0f);
        for (short i = 0; i < 24; i = (short) (i + 1)) {
            eqGridPos[(i + 6) * 2] = Util.toRadians((float) (i * 15));
            eqGridPos[((i + 6) * 2) + 1] = 0.0f;
        }
        for (short i2 = 0; i2 < 8; i2 = (short) (i2 + 1)) {
            float decAngle = Util.toRadians(((float) (i2 + 1)) * 10.0f);
            int baseIndex = ((i2 * 48) + 30) * 2;
            for (short j = 0; j < NUM_RA_POINTS; j = (short) (j + 1)) {
                float raAngle = Util.toRadians(((float) j) * 7.5f);
                int offset = j * 2;
                eqGridPos[baseIndex + offset] = raAngle;
                eqGridPos[baseIndex + offset + 1] = decAngle;
                eqGridPos[baseIndex + offset + SOUTHERN_OFFSET] = raAngle;
                eqGridPos[baseIndex + offset + SOUTHERN_OFFSET + 1] = -decAngle;
            }
        }
        return true;
    }

    private void initEqGrid() {
        short[] eqLineIndices = new short[1640];
        eqLineIndices[0] = 3;
        eqLineIndices[1] = 4;
        eqLineIndices[2] = 4;
        eqLineIndices[3] = 5;
        eqLineIndices[4] = 5;
        eqLineIndices[5] = 6;
        eqLineIndices[6] = 6;
        eqLineIndices[7] = 3;
        for (short i = 0; i < 24; i = (short) (i + 1)) {
            int base = (i * 4) + 8;
            eqLineIndices[base + 0] = 0;
            eqLineIndices[base + 1] = (short) (i + 6);
            eqLineIndices[base + 2] = 1;
            eqLineIndices[base + 3] = (short) (i + 6);
        }
        for (short i2 = 0; i2 < 8; i2 = (short) (i2 + 1)) {
            short baseVertIndex = (short) ((i2 * 48) + 30);
            int baseLineIndex = ((i2 * 48) + 52) * 2;
            for (short j = 0; j < NUM_RA_POINTS; j = (short) (j + 1)) {
                int offset = baseLineIndex + (j * 2);
                int currIndex = baseVertIndex + j;
                int nextIndex = baseVertIndex + ((j + 1) % NUM_RA_POINTS);
                eqLineIndices[offset] = (short) currIndex;
                eqLineIndices[offset + 1] = (short) nextIndex;
                eqLineIndices[offset + SOUTHERN_OFFSET] = (short) (currIndex + SEMISPHERE_POINTS);
                eqLineIndices[offset + SOUTHERN_OFFSET + 1] = (short) (nextIndex + SEMISPHERE_POINTS);
            }
        }
        this.eqGridLineIndexArray = Util.setLineIndex(eqLineIndices);
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public int getNumObjs() {
        return 798;
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public IntList getSelObjs() {
        return getAllObjs();
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public String getName(int objNum) {
        return "Special object";
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public float[] getPositions() {
        return eqGridPos;
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public float getMag(int objNum) {
        return 0.0f;
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    @TargetApi(8)
    public void drawES20(MyShadyRenderer myRenderer, IntList currBlocks, float currFov) {
        GLES20.glBlendFunc(770, 770);
        FloatBuffer vertexArray = this.myVertexArray;
        if (this.eqGridLineIndexArray != null && vertexArray != null) {
            this.eqGridLineIndexArray.position(0);
            myRenderer.mVectorLineShader.draw(3.0f * this.displayScaleFactor, myRenderer.mvpMatrix, vertexArray, this.eqGridLineIndexArray, 1, 8, this.colorArray, this.colorOffset + 4);
            this.eqGridLineIndexArray.position(8);
            myRenderer.mVectorLineShader.draw(2.0f * this.displayScaleFactor, myRenderer.mvpMatrix, vertexArray, this.eqGridLineIndexArray, 1, this.eqGridLineIndexArray.capacity() - 8, this.colorArray, this.colorOffset);
        }
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public void initLabels(LabelMaker labelMaker, LabelPaints paints, float displayScaleFactor) {
        this.displayScaleFactor = displayScaleFactor;
        initEqGrid();
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public void setTheme(int themeOrdinal) {
        this.colorOffset = Util.chooseColorOffset(this.colorArray.length, themeOrdinal);
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public void onDestroy() {
        super.onDestroy();
        this.eqGridLineIndexArray = null;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void setEqGridAlpha(float alpha) {
        for (int i = 3; i < this.colorArray.length; i += 4) {
            this.colorArray[i] = alpha;
        }
    }

    private static float getCurrEqGridAlpha(SkEye skeye) {
        return skeye.settingsManager.getQuickPref(eqGridAlphaKey, 0.2f);
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public QuickSettingsManager.QuickSettingsGroup getQuickSettings(SkEye skeye, MyShadyRenderer renderer) {
        return new QuickSettingsManager.QuickSettingsGroup(new QuickSettingsManager.SettingDetails[]{new QuickSettingsManager.SettingDetails<>(new QuickSettingsManager.FloatRangeQuickSetting(eqGridAlphaKey, skeye.getString(C0031R.string.eq_grid_opacity), "", 0.0f, 1.0f, 0.02f), new QuickSettingsManager.TypicalSettingChangeHandler<Float>(skeye) {
            /* class com.lavadip.skeye.catalog.SpecialCatalog.C00861 */

            @Override // com.lavadip.skeye.QuickSettingsManager.SettingChangeListener
            public /* bridge */ /* synthetic */ void onChange(String str, Object obj, boolean z) {
                onChange(str, (Float) obj, z);
            }

            @Override // com.lavadip.skeye.QuickSettingsManager.TypicalSettingChangeHandler
            public void onGLThread(String key, Float newValue, boolean trackingStopped) {
                SpecialCatalog.this.setEqGridAlpha(newValue.floatValue());
            }
        }, Float.valueOf(getCurrEqGridAlpha(skeye)))}, skeye.getString(C0031R.string.general), "general");
    }
}
