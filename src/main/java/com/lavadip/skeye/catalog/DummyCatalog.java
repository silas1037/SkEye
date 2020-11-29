package com.lavadip.skeye.catalog;

import com.lavadip.skeye.LabelMaker;
import com.lavadip.skeye.LabelPaints;
import com.lavadip.skeye.astro.IntList;

public abstract class DummyCatalog extends Catalog {
    DummyCatalog(int catalogId, int name) {
        super(catalogId, name, false, true, false, false, 50.0f, false, 9.0f, false, false);
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
}
