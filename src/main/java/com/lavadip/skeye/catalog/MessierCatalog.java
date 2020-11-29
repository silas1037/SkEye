package com.lavadip.skeye.catalog;

import com.lavadip.skeye.C0031R;
import com.lavadip.skeye.MyShadyRenderer;
import com.lavadip.skeye.QuickSettingsManager;
import com.lavadip.skeye.SkEye;

final class MessierCatalog extends DetailedCatalog {
    private static final int NUM_OBJS = 110;

    /* access modifiers changed from: package-private */
    @Override // com.lavadip.skeye.catalog.DetailedCatalog
    public String getPrefPrefix() {
        return "Messier";
    }

    /* access modifiers changed from: package-private */
    @Override // com.lavadip.skeye.catalog.DetailedCatalog
    public String getNamePrefix() {
        return "M ";
    }

    /* access modifiers changed from: package-private */
    @Override // com.lavadip.skeye.catalog.DetailedCatalog
    public String getLabelPrefix() {
        return "M";
    }

    /* access modifiers changed from: package-private */
    @Override // com.lavadip.skeye.catalog.DetailedCatalog
    public String getDataFileName() {
        return "messier_catalog.jet";
    }

    MessierCatalog(int catalogId) {
        super(catalogId, C0031R.string.messiers);
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public int getNumObjs() {
        return NUM_OBJS;
    }

    /* access modifiers changed from: protected */
    @Override // com.lavadip.skeye.catalog.Catalog
    public float getDefaultMaxSBR() {
        return 14.91f;
    }

    /* access modifiers changed from: protected */
    @Override // com.lavadip.skeye.catalog.Catalog
    public float getDefaultMinSize() {
        return 10.0f;
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public QuickSettingsManager.QuickSettingsGroup getQuickSettings(SkEye skeye, MyShadyRenderer renderer) {
        return getQuickSettingsBase(skeye, renderer, "Messier");
    }
}
