package com.lavadip.skeye.catalog;

import android.graphics.Paint;
import com.lavadip.skeye.C0031R;
import com.lavadip.skeye.LabelMaker;
import com.lavadip.skeye.LabelPaints;
import com.lavadip.skeye.MyShadyRenderer;
import com.lavadip.skeye.QuickSettingsManager;
import com.lavadip.skeye.SkEye;
import com.lavadip.skeye.astro.IntList;
import com.lavadip.skeye.astro.StarList;
import com.lavadip.skeye.astro.StarList2;

public final class BasicStarCatalog extends Catalog {
    private static final int NUM_OBJS = StarList.starNames.length;
    private static final String labelAlphaKey = "starsLabelAlpha";
    private static final float[] limitMagMap = {2.1f, 2.4f};
    private static String stars_string = null;
    private static final int zoomLevel = 1;
    private float labelAlpha = 0.5f;
    private int labelBaseId = -1;
    private final int[] limitMagIndexMap = new int[limitMagMap.length];

    BasicStarCatalog(int catalogId) {
        super(catalogId, C0031R.string.stars, true, false, true, false, 60.0f, true, 9.0f, false, true);
        int starIndex = 0;
        for (int i = 0; i < limitMagMap.length; i++) {
            while (StarList2.visMag[starIndex] < limitMagMap[i] && starIndex < StarList2.visMag.length) {
                starIndex++;
            }
            this.limitMagIndexMap[i] = starIndex;
        }
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
        String name = StarList.starNames[objNum];
        String designation = StarList.designations[objNum];
        if (name == null) {
            return designation;
        }
        return designation == null ? name : String.valueOf(name) + " (" + designation + ")";
    }

    private static String getLabel(int objNum) {
        String name = StarList.starNames[objNum];
        return name == null ? StarList.designations[objNum] : name;
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public float[] getPositions() {
        return StarList2.positions;
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public float getMag(int objNum) {
        return StarList2.visMag[objNum];
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public String getTypeDescr(int objNum) {
        return String.valueOf(stars_string) + ", Mag:" + StarList2.visMag[objNum];
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public String getTypeDescrForSearch(int objNum) {
        return "Mag:" + StarList2.visMag[objNum];
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public boolean shouldDrawLabel(int objNum) {
        return isBeingDisplayed(objNum);
    }

    private boolean isBeingDisplayed(int objNum) {
        return objNum < this.limitMagIndexMap[1];
    }

    private static Paint getPaint(int objNum, LabelPaints paints) {
        float visMag = StarList2.visMag[objNum];
        if (visMag < 0.5f) {
            return paints.extraBrightStarPaint;
        }
        return visMag < 1.0f ? paints.brightStarPaint : paints.starPaint;
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public void initLabels(LabelMaker labelMaker, LabelPaints paints, float displayScaleFactor) {
        this.displayScaleFactor = displayScaleFactor;
        for (int i = 0; i < NUM_OBJS; i++) {
            if (isBeingDisplayed(i)) {
                int labelId = labelMaker.add(getLabel(i), getPaint(i, paints));
                if (this.labelBaseId < 0) {
                    this.labelBaseId = labelId;
                }
            }
        }
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public void init(SkEye skeye) {
        stars_string = skeye.getResources().getString(C0031R.string.star_str);
        this.labelAlpha = getCurrLabelAlpha(skeye);
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public void drawLabelES(int objNum, float x, float y, MyShadyRenderer r, LabelMaker labelMaker, boolean centerLabelHoriz, boolean centerLabelVert) {
        if (shouldDrawLabel(objNum)) {
            labelMaker.drawES20(r, x, y, this.labelBaseId + objNum, centerLabelHoriz, centerLabelVert, objNum < 20 ? 8.0f : 4.0f, this.labelAlpha);
        }
    }

    private static float getCurrLabelAlpha(SkEye skeye) {
        return skeye.settingsManager.getQuickPref(labelAlphaKey, 0.3f);
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public QuickSettingsManager.QuickSettingsGroup getQuickSettings(SkEye skeye, MyShadyRenderer renderer) {
        return new QuickSettingsManager.QuickSettingsGroup(new QuickSettingsManager.SettingDetails[]{new QuickSettingsManager.SettingDetails(new QuickSettingsManager.FloatRangeQuickSetting(labelAlphaKey, skeye.getString(C0031R.string.label_opacity), "", 0.0f, 1.0f, 0.05f), new QuickSettingsManager.TypicalSettingChangeHandler<Float>(skeye) {
            /* class com.lavadip.skeye.catalog.BasicStarCatalog.C00741 */

            @Override // com.lavadip.skeye.QuickSettingsManager.SettingChangeListener
            public /* bridge */ /* synthetic */ void onChange(String str, Object obj, boolean z) {
                onChange(str, (Float) obj, z);
            }

            @Override // com.lavadip.skeye.QuickSettingsManager.TypicalSettingChangeHandler
            public void onGLThread(String key, Float newValue, boolean trackingStopped) {
                BasicStarCatalog.this.labelAlpha = newValue.floatValue();
            }
        }, Float.valueOf(getCurrLabelAlpha(skeye)))}, skeye.getString(C0031R.string.stars), "stars");
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public boolean matches(int objNum, int objId, String filterStr) {
        String expanded;
        if (super.matches(objNum, objId, filterStr)) {
            return true;
        }
        String designation = StarList.designations[objNum];
        if (designation == null || designation.length() <= 0 || (expanded = StarList.expandDesignation(designation.charAt(0))) == null) {
            return false;
        }
        return expanded.contains(filterStr);
    }
}
