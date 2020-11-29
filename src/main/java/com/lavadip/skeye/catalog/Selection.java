package com.lavadip.skeye.catalog;

import android.content.Context;
import android.preference.PreferenceManager;
import com.lavadip.skeye.CatalogActivity;
import com.lavadip.skeye.astro.IntList;

public final class Selection {
    final IntList idsInRangeA;
    final IntList idsInRangeB;
    public float leftMarkA;
    public float leftMarkB;
    final String prefPrefix;
    public float rightMarkA;
    public float rightMarkB;
    public int selectedCount;
    public final IntList selectedIds;
    public final int[] sortedIdsA;
    public final int[] sortedIdsB;
    public final float[] valuesA;
    public final float[] valuesB;

    public Selection(IntList selectedIds2, float[] valuesA2, float[] valuesB2, int[] sortedIdsA2, int[] sortedIdsB2, float initialLeftMarkA, float initialRightMarkA, float initialLeftMarkB, float initialRightMarkB, String prefPrefix2) {
        this.selectedIds = selectedIds2;
        this.valuesA = valuesA2;
        this.valuesB = valuesB2;
        this.sortedIdsA = sortedIdsA2;
        this.sortedIdsB = sortedIdsB2;
        this.leftMarkA = initialLeftMarkA;
        this.rightMarkA = initialRightMarkA;
        this.leftMarkB = initialLeftMarkB;
        this.rightMarkB = initialRightMarkB;
        this.prefPrefix = prefPrefix2;
        this.idsInRangeA = new IntList(valuesA2.length);
        this.idsInRangeB = new IntList(valuesB2.length);
        setSelectedIds(initialLeftMarkA, initialRightMarkA, valuesA2, sortedIdsA2, this.idsInRangeA);
        setSelectedIds(initialLeftMarkB, initialRightMarkB, valuesB2, sortedIdsB2, this.idsInRangeB);
        this.selectedCount = CatalogActivity.countIntersect(this.idsInRangeA, this.idsInRangeB, selectedIds2.size, valuesB2).countCommon;
    }

    public IntersectionResult setMarks(boolean isA, float leftSel, float rightSel) {
        if (isA) {
            this.leftMarkA = leftSel;
            this.rightMarkA = rightSel;
        } else {
            this.leftMarkB = leftSel;
            this.rightMarkB = rightSel;
        }
        float[] values = isA ? this.valuesA : this.valuesB;
        int[] sortedIds = isA ? this.sortedIdsA : this.sortedIdsB;
        float[] otherValues = isA ? this.valuesB : this.valuesA;
        IntList ids = isA ? this.idsInRangeA : this.idsInRangeB;
        IntList otherIds = isA ? this.idsInRangeB : this.idsInRangeA;
        setSelectedIds(leftSel, rightSel, values, sortedIds, ids);
        IntersectionResult result = CatalogActivity.countIntersect(ids, otherIds, this.selectedIds.size, otherValues);
        this.selectedCount = result.countCommon;
        return result;
    }

    private static void setSelectedIds(float leftSel, float rightSel, float[] values, int[] sortedIds, IntList ids) {
        int i = 0;
        ids.clearList();
        int len = values.length;
        while (i < len && values[sortedIds[i]] < leftSel) {
            i++;
        }
        while (i < len && values[sortedIds[i]] <= rightSel) {
            ids.add(sortedIds[i]);
            i++;
        }
    }

    public void saveCurrent(Context ctxt) {
        PreferenceManager.getDefaultSharedPreferences(ctxt).edit().putFloat(String.valueOf(this.prefPrefix) + "leftA", this.leftMarkA).putFloat(String.valueOf(this.prefPrefix) + "leftB", this.leftMarkB).putFloat(String.valueOf(this.prefPrefix) + "rightA", this.rightMarkA).putFloat(String.valueOf(this.prefPrefix) + "rightB", this.rightMarkB).commit();
    }
}
