package com.lavadip.skeye.catalog;

public final class IntersectionResult {
    public final int countCommon;
    public final float[] filteredValues;

    public IntersectionResult(float[] intersectList, int countCommon2) {
        this.filteredValues = intersectList;
        this.countCommon = countCommon2;
    }
}
