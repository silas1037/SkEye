package com.lavadip.skeye.catalog;

import com.lavadip.skeye.C0031R;
import com.lavadip.skeye.MyShadyRenderer;
import com.lavadip.skeye.QuickSettingsManager;
import com.lavadip.skeye.SkEye;

public final class ICCatalog extends DetailedCatalog {
    private static final int NUM_OBJS = 5386;

    /* access modifiers changed from: package-private */
    @Override // com.lavadip.skeye.catalog.DetailedCatalog
    public String getPrefPrefix() {
        return "IC";
    }

    /* access modifiers changed from: package-private */
    @Override // com.lavadip.skeye.catalog.DetailedCatalog
    public String getNamePrefix() {
        return "IC ";
    }

    /* access modifiers changed from: package-private */
    @Override // com.lavadip.skeye.catalog.DetailedCatalog
    public String getLabelPrefix() {
        return "I";
    }

    /* access modifiers changed from: package-private */
    @Override // com.lavadip.skeye.catalog.DetailedCatalog
    public String getDataFileName() {
        return "ic_catalog.jet";
    }

    ICCatalog(int catalogId) {
        super(catalogId, C0031R.string.ic_tag);
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public int getNumObjs() {
        return NUM_OBJS;
    }

    /* access modifiers changed from: protected */
    @Override // com.lavadip.skeye.catalog.Catalog
    public float getDefaultMaxSBR() {
        return 13.2f;
    }

    /* access modifiers changed from: protected */
    @Override // com.lavadip.skeye.catalog.Catalog
    public float getDefaultMinSize() {
        return 2.0f;
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public QuickSettingsManager.QuickSettingsGroup getQuickSettings(SkEye skeye, MyShadyRenderer renderer) {
        return getQuickSettingsBase(skeye, renderer, "IC");
    }
}
