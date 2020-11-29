package com.lavadip.skeye.catalog;

import com.lavadip.skeye.MyShadyRenderer;
import com.lavadip.skeye.QuickSettingsManager;
import com.lavadip.skeye.SkEye;
import com.lavadip.skeyepro.C0139R;

public final class NGCCatalog extends DetailedCatalog {
    private static final int NUM_OBJS = 7840;

    /* access modifiers changed from: package-private */
    @Override // com.lavadip.skeye.catalog.DetailedCatalog
    public String getPrefPrefix() {
        return "NGC";
    }

    /* access modifiers changed from: package-private */
    @Override // com.lavadip.skeye.catalog.DetailedCatalog
    public String getNamePrefix() {
        return "NGC ";
    }

    /* access modifiers changed from: package-private */
    @Override // com.lavadip.skeye.catalog.DetailedCatalog
    public String getLabelPrefix() {
        return "N";
    }

    /* access modifiers changed from: package-private */
    @Override // com.lavadip.skeye.catalog.DetailedCatalog
    public String getDataFileName() {
        return "ngc_catalog.jet";
    }

    public NGCCatalog(int catalogId) {
        super(catalogId, C0139R.string.ngc_tag);
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public int getNumObjs() {
        return NUM_OBJS;
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public QuickSettingsManager.QuickSettingsGroup getQuickSettings(SkEye skeye, MyShadyRenderer renderer) {
        return getQuickSettingsBase(skeye, renderer, "NGC");
    }
}
