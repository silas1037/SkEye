package com.lavadip.skeye.catalog;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.lavadip.skeye.C0031R;
import com.lavadip.skeye.CatalogActivity;
import com.lavadip.skeye.CommonNames;
import com.lavadip.skeye.LabelMaker;
import com.lavadip.skeye.LabelPaints;
import com.lavadip.skeye.MyShadyRenderer;
import com.lavadip.skeye.QuickSettingsManager;
import com.lavadip.skeye.SkEye;
import com.lavadip.skeye.astro.CatalogedLocation;
import com.lavadip.skeye.astro.IntList;
import com.lavadip.skeye.astro.Sky;
import com.lavadip.skeye.util.Util;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class Catalog {
    private static final float DEFAULT_MAX_MAG = 6.0f;
    private static final float DEFAULT_MAX_SIZE = 1.0E7f;
    private static final float DEFAULT_MIN_MAG = -27.0f;
    static final String GALAXY_PREF_PREFIX = "gal";
    static final String GLOBC_PREF_PREFIX = "globc";
    static final short IMPOSSIBLE_MAG = -32000;
    static final String NEB_PREF_PREFIX = "neb";
    static final String OPENC_PREF_PREFIX = "openc";
    private final float DEFAULT_MAX_SBR = getDefaultMaxSBR();
    private final float DEFAULT_MIN_SIZE = getDefaultMinSize();
    private IntList allObjsCached = null;
    protected final Comparator<CatalogedLocation> byObjectNumComparator = new Comparator<CatalogedLocation>() {
        /* class com.lavadip.skeye.catalog.Catalog.C00751 */

        public int compare(CatalogedLocation object1, CatalogedLocation object2) {
            return CatalogManager.getObjNum(object1.f21id) - CatalogManager.getObjNum(object2.f21id);
        }
    };
    public final boolean canBeConfigured;
    public final boolean canHaveFarLabel;
    public final float depth;
    protected float displayScaleFactor = 1.0f;
    public final boolean hasPointPos;

    /* renamed from: id */
    public final int f84id;
    public final boolean indexed;
    public final boolean isVeryDynamic;
    public final float maxFovValue;
    protected FloatBuffer myVertexArray = null;
    public final boolean searchable;
    private final Pattern spacePattern = Pattern.compile("\\s+");
    public final int tagResId;
    protected float[] vecPositions;

    public abstract float getMag(int i);

    public abstract String getName(int i);

    public abstract int getNumObjs();

    public abstract float[] getPositions();

    public abstract IntList getSelObjs();

    public abstract void initLabels(LabelMaker labelMaker, LabelPaints labelPaints, float f);

    Catalog(int catalogId, int tagResId2, boolean searchable2, boolean isVeryDynamic2, boolean hasPointPos2, boolean canHaveFarLabel2, float maxFovValue2, boolean indexed2, float depth2, boolean canBeConfigured2, boolean positionsAvailable) {
        this.f84id = catalogId;
        this.tagResId = tagResId2;
        this.searchable = searchable2;
        this.isVeryDynamic = isVeryDynamic2;
        this.hasPointPos = hasPointPos2;
        this.canHaveFarLabel = canHaveFarLabel2;
        this.maxFovValue = maxFovValue2;
        this.indexed = indexed2;
        this.depth = depth2;
        this.canBeConfigured = canBeConfigured2;
        if (positionsAvailable) {
            this.vecPositions = new float[(getNumObjs() * 3)];
            updateVecPositions();
        }
    }

    /* access modifiers changed from: protected */
    public boolean shouldPrecess() {
        return true;
    }

    public boolean isObjVisible(int objNum, float fov) {
        return true;
    }

    /* access modifiers changed from: package-private */
    public final void updateVecPositions() {
        boolean precess = shouldPrecess();
        int N = getNumObjs();
        float[] positions = getPositions();
        if (this.vecPositions == null) {
            this.vecPositions = new float[(N * 3)];
        }
        if (precess) {
            Sky.precess(positions, this.vecPositions, N, this.depth);
            return;
        }
        for (int i = 0; i < N; i++) {
            Util.map3d(positions[i * 2], positions[(i * 2) + 1], this.vecPositions, i * 3, this.depth);
        }
    }

    /* access modifiers changed from: package-private */
    public final void updateVertexArray(boolean forcedUpdate) {
        if (this.vecPositions == null) {
            updateVecPositions();
        }
        FloatBuffer va = this.myVertexArray;
        IntList selected = getSelObjs();
        if (va == null || va.capacity() != selected.size) {
            va = ByteBuffer.allocateDirect(selected.size * 3 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        }
        if (forcedUpdate || va != this.myVertexArray) {
            va.position(0);
            for (int i = 0; i < selected.size; i++) {
                va.put(this.vecPositions, CatalogManager.getObjNum(selected.get(i)) * 3, 3);
            }
            va.position(0);
        }
        this.myVertexArray = va;
    }

    public Comparator<CatalogedLocation> getByNameComparator(Comparator<CatalogedLocation> fallback) {
        return fallback;
    }

    public void drawLabelES(int objNum, float x, float y, MyShadyRenderer r, LabelMaker labelMaker, boolean centerLabelHoriz, boolean centerLabelVert) {
    }

    public boolean shouldDrawLabel(int objNum) {
        return true;
    }

    /* access modifiers changed from: package-private */
    public final int makeObjId(int objNum) {
        return CatalogManager.makeObjId(this.f84id, objNum);
    }

    public final IntList getAllObjs() {
        int numObjs = getNumObjs();
        if (this.allObjsCached != null && this.allObjsCached.size == numObjs) {
            return this.allObjsCached;
        }
        IntList retVal = new IntList(numObjs);
        for (int i = 0; i < numObjs; i++) {
            retVal.add(makeObjId(i));
        }
        this.allObjsCached = retVal;
        return retVal;
    }

    @TargetApi(8)
    public void drawES20(MyShadyRenderer myRenderer, IntList currBlocks, float currFov) {
    }

    public void updateSky(long currTime) {
    }

    public String getTypeDescr(int objNum) {
        return "";
    }

    public String getTypeDescrForSearch(int objNum) {
        return getTypeDescr(objNum);
    }

    private String removeSpace(String s) {
        return this.spacePattern.matcher(s).replaceAll("");
    }

    public int searchByName(String name) {
        String query = removeSpace(name.toLowerCase());
        for (int i = getNumObjs() - 1; i >= 0; i--) {
            if (removeSpace(getName(i).toLowerCase()).equals(query)) {
                return makeObjId(i);
            }
        }
        return -1;
    }

    public void onDestroy() {
        this.myVertexArray = null;
    }

    public boolean updateSettings(SharedPreferences pref) {
        updateVertexArray(false);
        return false;
    }

    public void setTheme(int themeOrdinal) {
    }

    public Map<Integer, Selection> getSelectionMap(Context ctxt) {
        return null;
    }

    /* access modifiers changed from: protected */
    public final Map<Integer, Selection> getSelectionMap(int NUM_OBJS, float[] sizes, short[] mags, short[] sbr, short[] majorType, short[] minorType, Context ctxt, String prefPrefix) {
        short[] visibilityValues;
        IntList selectedGalaxies = new IntList(8096);
        IntList selectedGlobClusters = new IntList(1024);
        IntList selectedOpenClusters = new IntList(1024);
        IntList selectedNebulae = new IntList(512);
        for (int i = 0; i < NUM_OBJS; i++) {
            if (sizes[i] > 0.0f) {
                boolean validSBr = sbr[i] > -32000;
                if (majorType[i] == 3 && validSBr) {
                    selectedGalaxies.add(i);
                } else if (majorType[i] == 2) {
                    if (minorType[i] == 1 && validSBr) {
                        selectedGlobClusters.add(i);
                    } else if (minorType[i] == 2 && mags[i] > -32000) {
                        selectedOpenClusters.add(i);
                    }
                } else if (majorType[i] == 4 && validSBr) {
                    selectedNebulae.add(i);
                }
            }
        }
        Map<Integer, Selection> selectionMap = new HashMap<>();
        int[] iArr = CatalogActivity.objectTypeResourceIds;
        int length = iArr.length;
        for (int i2 = 0; i2 < length; i2++) {
            int checkedId = iArr[i2];
            IntList selectedIds = null;
            String typePrefix = null;
            if (checkedId == C0031R.C0032id.valuesGal) {
                selectedIds = selectedGalaxies;
                typePrefix = GALAXY_PREF_PREFIX;
            } else if (checkedId == C0031R.C0032id.valuesGlobClusters) {
                selectedIds = selectedGlobClusters;
                typePrefix = GLOBC_PREF_PREFIX;
            } else if (checkedId == C0031R.C0032id.valuesOpenClusters) {
                selectedIds = selectedOpenClusters;
                typePrefix = OPENC_PREF_PREFIX;
            } else if (checkedId == C0031R.C0032id.valuesNeb) {
                selectedIds = selectedNebulae;
                typePrefix = NEB_PREF_PREFIX;
            }
            float[] selectedValuesA = new float[selectedIds.size];
            float[] selectedValuesB = new float[selectedIds.size];
            int[] sortedIdsA = new int[selectedIds.size];
            int[] sortedIdsB = new int[selectedIds.size];
            boolean useSBr = checkedId != C0031R.C0032id.valuesOpenClusters;
            if (useSBr) {
                visibilityValues = sbr;
            } else {
                visibilityValues = mags;
            }
            for (int i3 = 0; i3 < selectedIds.size; i3++) {
                int objId = selectedIds.get(i3);
                selectedValuesA[i3] = ((float) visibilityValues[objId]) / 1000.0f;
                selectedValuesB[i3] = sizes[objId];
                sortedIdsB[i3] = i3;
                sortedIdsA[i3] = i3;
            }
            CatalogActivity.sortIds(sortedIdsA, selectedValuesA);
            CatalogActivity.sortIds(sortedIdsB, selectedValuesB);
            String selPrefix = String.valueOf(prefPrefix) + typePrefix;
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctxt);
            selectionMap.put(Integer.valueOf(checkedId), new Selection(selectedIds, selectedValuesA, selectedValuesB, sortedIdsA, sortedIdsB, pref.getFloat(String.valueOf(selPrefix) + "leftA", selectedValuesA[sortedIdsA[0]]), pref.getFloat(String.valueOf(selPrefix) + "rightA", useSBr ? this.DEFAULT_MAX_SBR : DEFAULT_MAX_MAG), pref.getFloat(String.valueOf(selPrefix) + "leftB", this.DEFAULT_MIN_SIZE), pref.getFloat(String.valueOf(selPrefix) + "rightB", selectedValuesB[sortedIdsB[sortedIdsB.length - 1]]), selPrefix));
        }
        return selectionMap;
    }

    /* access modifiers changed from: protected */
    public float getDefaultMaxSBR() {
        return 14.0f;
    }

    /* access modifiers changed from: protected */
    public float getDefaultMinSize() {
        return DEFAULT_MAX_MAG;
    }

    /* access modifiers changed from: protected */
    public final IntList selectionFromPref(int NUM_OBJS, float[] sizes, short[] mags, short[] sbr, short[] majorType, short[] minorType, SharedPreferences prefs, String prefPrefix) {
        IntList newSelObjList = new IntList(NUM_OBJS);
        float sbrMinGal = prefs.getFloat(String.valueOf(prefPrefix) + GALAXY_PREF_PREFIX + "leftA", DEFAULT_MIN_MAG);
        float sbrMaxGal = prefs.getFloat(String.valueOf(prefPrefix) + GALAXY_PREF_PREFIX + "rightA", this.DEFAULT_MAX_SBR);
        float sizeMinGal = prefs.getFloat(String.valueOf(prefPrefix) + GALAXY_PREF_PREFIX + "leftB", this.DEFAULT_MIN_SIZE);
        float sizeMaxGal = prefs.getFloat(String.valueOf(prefPrefix) + GALAXY_PREF_PREFIX + "rightB", DEFAULT_MAX_SIZE);
        float sbrMinGlob = prefs.getFloat(String.valueOf(prefPrefix) + GLOBC_PREF_PREFIX + "leftA", DEFAULT_MIN_MAG);
        float sbrMaxGlob = prefs.getFloat(String.valueOf(prefPrefix) + GLOBC_PREF_PREFIX + "rightA", this.DEFAULT_MAX_SBR);
        float sizeMinGlob = prefs.getFloat(String.valueOf(prefPrefix) + GLOBC_PREF_PREFIX + "leftB", this.DEFAULT_MIN_SIZE);
        float sizeMaxGlob = prefs.getFloat(String.valueOf(prefPrefix) + GLOBC_PREF_PREFIX + "rightB", DEFAULT_MAX_SIZE);
        float magMinOpenc = prefs.getFloat(String.valueOf(prefPrefix) + OPENC_PREF_PREFIX + "leftA", DEFAULT_MIN_MAG);
        float magMaxOpenc = prefs.getFloat(String.valueOf(prefPrefix) + OPENC_PREF_PREFIX + "rightA", DEFAULT_MAX_MAG);
        float sizeMinOpenc = prefs.getFloat(String.valueOf(prefPrefix) + OPENC_PREF_PREFIX + "leftB", this.DEFAULT_MIN_SIZE);
        float sizeMaxOpenc = prefs.getFloat(String.valueOf(prefPrefix) + OPENC_PREF_PREFIX + "rightB", DEFAULT_MAX_SIZE);
        float sbrMinNeb = prefs.getFloat(String.valueOf(prefPrefix) + NEB_PREF_PREFIX + "leftA", DEFAULT_MIN_MAG);
        float sbrMaxNeb = prefs.getFloat(String.valueOf(prefPrefix) + NEB_PREF_PREFIX + "rightA", this.DEFAULT_MAX_SBR);
        float sizeMinNeb = prefs.getFloat(String.valueOf(prefPrefix) + NEB_PREF_PREFIX + "leftB", this.DEFAULT_MIN_SIZE);
        float sizeMaxNeb = prefs.getFloat(String.valueOf(prefPrefix) + NEB_PREF_PREFIX + "rightB", DEFAULT_MAX_SIZE);
        for (int i = 0; i < NUM_OBJS; i++) {
            float currSbr = ((float) sbr[i]) / 1000.0f;
            float currSize = sizes[i];
            float currMag = ((float) mags[i]) / 1000.0f;
            if (majorType[i] == 3 && currSbr >= sbrMinGal && currSbr <= sbrMaxGal && currSize >= sizeMinGal && currSize <= sizeMaxGal) {
                newSelObjList.add(makeObjId(i));
            } else if (majorType[i] == 2) {
                if (minorType[i] == 1 && currSbr >= sbrMinGlob && currSbr <= sbrMaxGlob && currSize >= sizeMinGlob && currSize <= sizeMaxGlob) {
                    newSelObjList.add(makeObjId(i));
                } else if (minorType[i] == 2 && currMag >= magMinOpenc && currMag <= magMaxOpenc && currSize >= sizeMinOpenc && currSize <= sizeMaxOpenc) {
                    newSelObjList.add(makeObjId(i));
                }
            } else if (majorType[i] == 4 && currSbr >= sbrMinNeb && currSbr <= sbrMaxNeb && currSize >= sizeMinNeb && currSize <= sizeMaxNeb) {
                newSelObjList.add(makeObjId(i));
            }
        }
        return newSelObjList;
    }

    public float[] getVecPositions() {
        return this.vecPositions;
    }

    public void init(SkEye skeye) throws IOException {
    }

    public void startConfig(Activity catalogActivity) {
    }

    public QuickSettingsManager.QuickSettingsGroup getQuickSettings(SkEye skeye, MyShadyRenderer renderer) {
        return null;
    }

    public boolean matches(int objNum, int objId, String filterStr) {
        if (getName(objNum).toLowerCase().contains(filterStr)) {
            return true;
        }
        int commonNameIndex = CommonNames.idToIndex.get(objId);
        if (commonNameIndex >= 0) {
            return CommonNames.names[commonNameIndex].toLowerCase().contains(filterStr);
        }
        return false;
    }
}
