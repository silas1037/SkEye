package com.lavadip.skeye.catalog;

import android.util.Log;
import com.lavadip.skeye.C0031R;
import com.lavadip.skeye.LabelMaker;
import com.lavadip.skeye.LabelPaints;
import com.lavadip.skeye.MyShadyRenderer;
import com.lavadip.skeye.QuickSettingsManager;
import com.lavadip.skeye.SkEye;
import com.lavadip.skeye.astro.IntList;
import com.lavadip.skeye.util.Util;
import com.lavadip.skeyepro.C0139R;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

final class ExtStarCatalog extends Catalog {
    private float absoluteScale = 0.0f;
    private int[] blockCounts = null;
    private int[] blockCountsL1 = null;
    private int[] blockStartIndex = null;
    private final float mExtinction = 0.0f;
    private float relativeScale = 1.0f;
    private float relativeScalePivot = 4.0f;
    private FloatBuffer vertexAttribBuffer = null;
    private FloatBuffer vertexBuffer = null;

    public ExtStarCatalog(int catalogId) {
        super(catalogId, C0031R.string.stars, false, false, false, false, 60.0f, true, 9.0f, false, true);
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public int getNumObjs() {
        return 0;
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public void initLabels(LabelMaker labelMaker, LabelPaints paints, float displayScaleFactor) {
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public IntList getSelObjs() {
        return getAllObjs();
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public String getName(int objNum) {
        return null;
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public float[] getPositions() {
        return null;
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public float getMag(int objNum) {
        return 0.0f;
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public void drawES20(MyShadyRenderer renderer, IntList currBlocks, float currFov) {
        renderer.mStarShader.beginDrawing();
        int numCurrBlocks = currBlocks.size;
        for (int i = 0; i < numCurrBlocks; i++) {
            int b = currBlocks.get(i);
            renderer.mStarShader.draw(renderer.mvpPrecessMatrix, renderer.mvpGeoCentricMatrix, this.vertexAttribBuffer, this.vertexBuffer, this.blockStartIndex[b], currFov > 15.0f ? this.blockCountsL1[b] : this.blockCounts[b], this.absoluteScale, this.relativeScale, this.relativeScalePivot, 0.0f);
        }
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public void init(SkEye skeye) throws IOException {
        readStarData(skeye);
        this.absoluteScale = getCurrAbsoluteScale(skeye);
        this.relativeScale = getCurrRelativeScale(skeye);
        this.relativeScalePivot = getCurrRelativeScalePivot(skeye);
    }

    private void readStarData(SkEye skeye) throws IOException {
        DataInputStream posStream = new DataInputStream(skeye.getResources().openRawResource(C0139R.raw.f105sp));
        int numTotalDivs = posStream.readInt() * posStream.readInt();
        this.blockStartIndex = new int[(numTotalDivs * 2)];
        this.blockCountsL1 = new int[(numTotalDivs * 2)];
        this.blockCounts = new int[(numTotalDivs * 2)];
        for (int i = 0; i < numTotalDivs; i++) {
            this.blockStartIndex[i] = posStream.readInt();
        }
        for (int i2 = 0; i2 < numTotalDivs; i2++) {
            this.blockCounts[i2] = posStream.readInt();
        }
        for (int i3 = 0; i3 < numTotalDivs; i3++) {
            this.blockCountsL1[i3] = posStream.readInt();
        }
        int numStars = posStream.readInt();
        Log.d("SKEYE", "Num stars = " + numStars);
        this.vertexBuffer = ByteBuffer.allocateDirect(numStars * 8).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.vertexBuffer.position(0);
        this.vertexAttribBuffer = ByteBuffer.allocateDirect(numStars * 8).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.vertexAttribBuffer.position(0);
        ByteBuffer readBuffer = ByteBuffer.allocate(4096).order(ByteOrder.BIG_ENDIAN);
        int n = numStars * 2 * 4;
        Util.readFloatsFromStream(n, readBuffer, posStream, this.vertexBuffer);
        this.vertexBuffer.position(0);
        posStream.close();
        DataInputStream attribStream = new DataInputStream(skeye.getResources().openRawResource(C0139R.raw.f104sa));
        Util.readFloatsFromStream(n, readBuffer, attribStream, this.vertexAttribBuffer);
        this.vertexAttribBuffer.position(0);
        attribStream.close();
    }

    /* access modifiers changed from: private */
    public enum SettingKeys {
        AbsoluteScale("starAbsoluteScale"),
        RelativeScale("starRelativeScale"),
        RelativeScalePivot("starRelativeScalePivot"),
        LabelAlpha("starLabelAlpha");
        
        final String key;

        private SettingKeys(String key2) {
            this.key = key2;
        }
    }

    private static float getCurrAbsoluteScale(SkEye skeye) {
        return skeye.settingsManager.getQuickPref(SettingKeys.AbsoluteScale.key, 0.0f);
    }

    private static float getCurrRelativeScale(SkEye skeye) {
        return skeye.settingsManager.getQuickPref(SettingKeys.RelativeScale.key, 1.0f);
    }

    private static float getCurrRelativeScalePivot(SkEye skeye) {
        return skeye.settingsManager.getQuickPref(SettingKeys.RelativeScale.key, 4.0f);
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public QuickSettingsManager.QuickSettingsGroup getQuickSettings(SkEye skeye, MyShadyRenderer renderer) {
        return new QuickSettingsManager.QuickSettingsGroup(new QuickSettingsManager.SettingDetails[]{new QuickSettingsManager.SettingDetails(new QuickSettingsManager.FloatRangeQuickSetting(SettingKeys.AbsoluteScale.key, skeye.getString(C0031R.string.absolute_scale), "", -2.0f, 2.0f, 0.05f), new QuickSettingsManager.TypicalSettingChangeHandler<Float>(skeye) {
            /* class com.lavadip.skeye.catalog.ExtStarCatalog.C00821 */

            @Override // com.lavadip.skeye.QuickSettingsManager.SettingChangeListener
            public /* bridge */ /* synthetic */ void onChange(String str, Object obj, boolean z) {
                onChange(str, (Float) obj, z);
            }

            @Override // com.lavadip.skeye.QuickSettingsManager.TypicalSettingChangeHandler
            public void onGLThread(String key, Float newValue, boolean trackingStopped) {
                ExtStarCatalog.this.absoluteScale = newValue.floatValue();
            }
        }, Float.valueOf(getCurrAbsoluteScale(skeye)))}, skeye.getString(C0031R.string.stars), "stars");
    }
}
