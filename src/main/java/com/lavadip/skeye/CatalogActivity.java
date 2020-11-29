package com.lavadip.skeye;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import com.lavadip.skeye.astro.IntList;
import com.lavadip.skeye.catalog.Catalog;
import com.lavadip.skeye.catalog.CatalogManager;
import com.lavadip.skeye.catalog.IntersectionResult;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CatalogActivity extends ListActivity {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final int CUTOFF = 10;
    public static final int[] objectTypeResourceIds = {C0031R.C0032id.valuesGal, C0031R.C0032id.valuesOpenClusters, C0031R.C0032id.valuesGlobClusters, C0031R.C0032id.valuesNeb};
    static final Map<Integer, String> typeNames = new HashMap();

    static {
        boolean z;
        if (!CatalogActivity.class.desiredAssertionStatus()) {
            z = true;
        } else {
            z = false;
        }
        $assertionsDisabled = z;
        typeNames.put(Integer.valueOf(C0031R.C0032id.valuesGal), "Galaxies");
        typeNames.put(Integer.valueOf(C0031R.C0032id.valuesOpenClusters), "Open Cl");
        typeNames.put(Integer.valueOf(C0031R.C0032id.valuesGlobClusters), "Globular Cl");
        typeNames.put(Integer.valueOf(C0031R.C0032id.valuesNeb), "Nebulae");
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0031R.layout.activity_catalog);
        SkEye.setupActivity(this);
        final List<Catalog> configurableCatalogs = new LinkedList<>();
        List<String> catalogNameArray = new LinkedList<>();
        Catalog[] catalogArr = CatalogManager.catalogs;
        for (Catalog c : catalogArr) {
            if (c.canBeConfigured) {
                configurableCatalogs.add(c);
                catalogNameArray.add(getResources().getString(c.tagResId));
            }
        }
        setListAdapter(new ArrayAdapter(this, 17367043, catalogNameArray));
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /* class com.lavadip.skeye.CatalogActivity.C00051 */

            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                ((Catalog) configurableCatalogs.get(position)).startConfig(CatalogActivity.this);
            }
        });
    }

    public static IntersectionResult countIntersect(IntList ids1, IntList ids2, int maxSize, float[] values) {
        if ($assertionsDisabled || maxSize == values.length) {
            float[] filteredValues = new float[ids1.size];
            if (ids1.size == maxSize && ids2.size == maxSize) {
                for (int i = 0; i < ids2.size; i++) {
                    filteredValues[i] = values[ids2.get(i)];
                }
                return new IntersectionResult(filteredValues, maxSize);
            }
            IntList ascendingIds1 = ids1.makeCopy();
            ascendingIds1.sortAscending();
            IntList ascendingIds2 = ids2.makeCopy();
            ascendingIds2.sortAscending();
            int j = 0;
            int countCommon = 0;
            for (int i2 = 0; i2 < ascendingIds1.size; i2++) {
                int id1 = ascendingIds1.get(i2);
                while (j < ascendingIds2.size && ascendingIds2.get(j) < id1) {
                    j++;
                }
                if (j >= ascendingIds2.size) {
                    break;
                }
                if (ascendingIds2.get(j) == id1) {
                    countCommon++;
                    j++;
                }
            }
            for (int i3 = 0; i3 < ids1.size; i3++) {
                filteredValues[i3] = values[ids1.get(i3)];
            }
            Arrays.sort(filteredValues);
            return new IntersectionResult(filteredValues, countCommon);
        }
        throw new AssertionError();
    }

    public static void quicksort(int[] a, float[] values) {
        quicksort(a, values, 0, a.length - 1);
    }

    private static void quicksort(int[] a, float[] values, int low, int high) {
        if (low + CUTOFF > high) {
            insertionSort(a, values, low, high);
            return;
        }
        int middle = (low + high) / 2;
        if (values[a[middle]] < values[a[low]]) {
            swapReferences(a, low, middle);
        }
        if (values[a[high]] < values[a[low]]) {
            swapReferences(a, low, high);
        }
        if (values[a[high]] < values[a[middle]]) {
            swapReferences(a, middle, high);
        }
        swapReferences(a, middle, high - 1);
        float pivot = values[a[high - 1]];
        int i = low;
        int j = high - 1;
        while (true) {
            i++;
            if (values[a[i]] >= pivot) {
                do {
                    j--;
                } while (pivot < values[a[j]]);
                if (i >= j) {
                    swapReferences(a, i, high - 1);
                    quicksort(a, values, low, i - 1);
                    quicksort(a, values, i + 1, high);
                    return;
                }
                swapReferences(a, i, j);
            }
        }
    }

    private static void insertionSort(int[] a, float[] values, int low, int high) {
        for (int p = low + 1; p <= high; p++) {
            int tmp = a[p];
            float tmpValue = values[a[p]];
            int j = p;
            while (j > low && tmpValue < values[a[j - 1]]) {
                a[j] = a[j - 1];
                j--;
            }
            a[j] = tmp;
        }
    }

    public static final void swapReferences(int[] a, int index1, int index2) {
        int tmp = a[index1];
        a[index1] = a[index2];
        a[index2] = tmp;
    }

    public static void sortIds(int[] ids, float[] values) {
        quicksort(ids, values);
    }
}
