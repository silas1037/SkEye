package com.lavadip.skeye.catalog;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import com.lavadip.skeye.C0031R;
import com.lavadip.skeye.CatalogFilterDialog;
import com.lavadip.skeye.LabelMaker;
import com.lavadip.skeye.LabelPaints;
import com.lavadip.skeye.MyShadyRenderer;
import com.lavadip.skeye.NumberedLabels;
import com.lavadip.skeye.QuickSettingsManager;
import com.lavadip.skeye.SkEye;
import com.lavadip.skeye.Vector3d;
import com.lavadip.skeye.astro.CatalogedLocation;
import com.lavadip.skeye.astro.IntList;
import com.lavadip.skeye.astro.LocationInSky;
import com.lavadip.skeye.util.Util;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.util.Comparator;
import java.util.IllegalFormatWidthException;
import java.util.Map;

public abstract class DetailedCatalog extends Catalog {
    private static final int TEX_COORD_GALAXY_OFFSET = 0;
    private static final int TEX_COORD_GC_OFFSET = 16;
    private static final int TEX_COORD_LENGTH = 8;
    private static final int TEX_COORD_NEB_OFFSET = 24;
    private static final int TEX_COORD_OC_OFFSET = 8;
    private static final float[] texCoordinates = {0.0f, 0.5f, 0.0f, 1.0f, 0.5f, 1.0f, 0.5f, 0.5f, 0.0f, 0.0f, 0.0f, 0.5f, 0.5f, 0.5f, 0.5f, 0.0f, 0.5f, 0.0f, 0.5f, 0.5f, 1.0f, 0.5f, 1.0f, 0.0f, 0.5f, 0.5f, 0.5f, 1.0f, 1.0f, 1.0f, 1.0f, 0.5f};
    private final int NUM_OBJS = getNumObjs();
    private final float[] colorArray = {0.7f, 1.0f, 0.7f, 0.7f, 0.7f, 0.5f, 0.35f, 0.7f, 0.7f, 0.0f, 0.0f, 1.0f};
    private final String dataFileName = getDataFileName();
    private float labelAlpha = 1.0f;
    private final String labelPrefix = getLabelPrefix();
    private final short[] mags = new short[this.NUM_OBJS];
    private final short[] majorType = new short[this.NUM_OBJS];
    private final short[] minorType = new short[this.NUM_OBJS];
    private final String namePrefix = getNamePrefix();
    private NumberedLabels numberedLabels;
    private final short[] posAngles = new short[this.NUM_OBJS];
    private final float[] positions = new float[(this.NUM_OBJS * 2)];
    private final String prefPrefix = getPrefPrefix();
    private final short[] sbr = new short[this.NUM_OBJS];
    private IntList selObjList = new IntList(this.NUM_OBJS);
    private final float[] sizeAxes = new float[(this.NUM_OBJS * 2)];
    private final float[] sizes = new float[this.NUM_OBJS];
    private FloatBuffer texCoordBuffer = null;
    private ShortBuffer texIndexBuffer = null;
    private int themeOrdinal = 0;
    private final boolean useCatalogFilters = true;
    private FloatBuffer vertexTexBuffer = null;

    /* access modifiers changed from: package-private */
    public abstract String getDataFileName();

    /* access modifiers changed from: package-private */
    public abstract String getLabelPrefix();

    /* access modifiers changed from: package-private */
    public abstract String getNamePrefix();

    /* access modifiers changed from: package-private */
    public abstract String getPrefPrefix();

    @Override // com.lavadip.skeye.catalog.Catalog
    public Map<Integer, Selection> getSelectionMap(Context ctxt) {
        return getSelectionMap(this.NUM_OBJS, this.sizes, this.mags, this.sbr, this.majorType, this.minorType, ctxt, this.prefPrefix);
    }

    private void putVectorToBuffer(FloatBuffer buffer, Vector3d vec) {
        buffer.put((float) (vec.f16x * ((double) this.depth)));
        buffer.put((float) (vec.f17y * ((double) this.depth)));
        buffer.put((float) (vec.f18z * ((double) this.depth)));
    }

    private void readData(FileInputStream in) {
        int texCoordOffset;
        try {
            FileChannel ch = in.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate((this.NUM_OBJS * 30) + 2);
            ch.read(byteBuffer);
            byteBuffer.position(0);
            byteBuffer.order(ByteOrder.BIG_ENDIAN);
            short length = byteBuffer.getShort();
            if (length != this.NUM_OBJS) {
                throw new IllegalFormatWidthException(length);
            }
            FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
            floatBuffer.get(this.sizes);
            floatBuffer.get(this.sizeAxes);
            floatBuffer.get(this.positions);
            byteBuffer.position(byteBuffer.position() + (this.NUM_OBJS * 20));
            ShortBuffer shortBuffer = byteBuffer.asShortBuffer();
            shortBuffer.get(this.posAngles);
            shortBuffer.get(this.mags);
            shortBuffer.get(this.sbr);
            shortBuffer.get(this.majorType);
            shortBuffer.get(this.minorType);
            int remaining = shortBuffer.remaining();
            if (remaining != 0) {
                throw new IllegalFormatWidthException(remaining);
            }
            in.close();
            updateVecPositions();
            this.texCoordBuffer = null;
            this.vertexTexBuffer = null;
            this.vertexTexBuffer = ByteBuffer.allocateDirect(this.NUM_OBJS * 4 * 4 * 3).order(ByteOrder.nativeOrder()).asFloatBuffer();
            this.texCoordBuffer = ByteBuffer.allocateDirect(this.NUM_OBJS * 4 * 8).order(ByteOrder.nativeOrder()).asFloatBuffer();
            Vector3d workVec = new Vector3d();
            Vector3d workVec2 = new Vector3d();
            Vector3d workVec3 = new Vector3d();
            Vector3d yVec = new Vector3d(0.0f, 1.0f, 0.0f);
            for (int i = 0; i < this.NUM_OBJS; i++) {
                double majorAxis = Math.toRadians((double) (this.sizeAxes[i * 2] / 60.0f));
                double minorAxis = Math.toRadians((double) (this.sizeAxes[(i * 2) + 1] / 60.0f));
                float semiDiagonal = (float) (Math.sqrt((majorAxis * majorAxis) + (minorAxis * minorAxis)) / 2.0d);
                float angleBetweenDiagonals = ((float) Math.acos(minorAxis / ((double) (2.0f * semiDiagonal)))) * 2.0f;
                float posAngle = (float) (((double) (((float) (3.141592653589793d - ((double) angleBetweenDiagonals))) / 2.0f)) - Math.toRadians((double) this.posAngles[i]));
                float[] angleMultiples = {posAngle, posAngle + angleBetweenDiagonals, 3.1415927f + posAngle, ((float) (((double) angleBetweenDiagonals) - 3.141592653589793d)) + posAngle};
                workVec.setXYZ(this.vecPositions, i * 3);
                workVec.normalise();
                workVec.rotateAwayFrom((double) (-1.0f * semiDiagonal), yVec, workVec2);
                int length2 = angleMultiples.length;
                for (int i2 = 0; i2 < length2; i2++) {
                    workVec2.rotate((double) angleMultiples[i2], workVec, workVec3);
                    putVectorToBuffer(this.vertexTexBuffer, workVec3);
                }
                if (this.majorType[i] == 3) {
                    texCoordOffset = 0;
                } else if (this.majorType[i] == 2) {
                    texCoordOffset = this.minorType[i] == 1 ? 16 : 8;
                } else {
                    texCoordOffset = 24;
                }
                this.texCoordBuffer.put(texCoordinates, texCoordOffset, 8);
            }
            this.vertexTexBuffer.position(0);
            this.texCoordBuffer.position(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public Comparator<CatalogedLocation> getByNameComparator(Comparator<CatalogedLocation> comparator) {
        return this.byObjectNumComparator;
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public IntList getSelObjs() {
        return this.selObjList;
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public String getName(int objNum) {
        return String.valueOf(this.namePrefix) + (objNum + 1);
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public float[] getPositions() {
        return this.positions;
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public float getMag(int objNum) {
        return ((float) this.mags[objNum]) / 1000.0f;
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public void drawES20(MyShadyRenderer myRenderer, IntList currBlocks, float currFov) {
        myRenderer.mPointShader.beginDrawing(MyShadyRenderer.mDSOMarkTexId);
        myRenderer.mPointShader.draw(myRenderer.mvpMatrix, this.colorArray, Util.chooseColorOffset(this.colorArray.length, this.themeOrdinal), 6.0f * this.displayScaleFactor, this.myVertexArray, this.myVertexArray.capacity() / 3);
        myRenderer.mDSOShader.draw(this.vertexTexBuffer, this.texIndexBuffer, this.texCoordBuffer, this.themeOrdinal);
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public String getTypeDescr(int objNum) {
        return LocationInSky.makeDescr(this.majorType[objNum], this.minorType[objNum], ((float) this.sbr[objNum]) / 1000.0f, getMag(objNum), this.sizeAxes[objNum * 2], this.sizeAxes[(objNum * 2) + 1]);
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public void initLabels(LabelMaker labelMaker, LabelPaints paints, float displayScaleFactor) {
        this.displayScaleFactor = displayScaleFactor;
        this.numberedLabels = new NumberedLabels(this.labelPrefix, 0, labelMaker, paints.messierPaint);
    }

    /* access modifiers changed from: package-private */
    public int getObjId(int objNum) {
        return objNum + 1;
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public boolean updateSettings(SharedPreferences prefs) {
        IntList newSelObjList = selectionFromPref(this.NUM_OBJS, this.sizes, this.mags, this.sbr, this.majorType, this.minorType, prefs, this.prefPrefix);
        boolean changed = !newSelObjList.equals(this.selObjList);
        this.selObjList = newSelObjList;
        updateVertexArray(true);
        this.texIndexBuffer = ByteBuffer.allocateDirect(this.selObjList.size * 6 * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
        for (int i = 0; i < this.selObjList.size; i++) {
            int selectedPos = CatalogManager.getObjNum(this.selObjList.get(i)) * 4;
            this.texIndexBuffer.put((short) (selectedPos + 0));
            this.texIndexBuffer.put((short) (selectedPos + 1));
            this.texIndexBuffer.put((short) (selectedPos + 2));
            this.texIndexBuffer.put((short) (selectedPos + 0));
            this.texIndexBuffer.put((short) (selectedPos + 2));
            this.texIndexBuffer.put((short) (selectedPos + 3));
        }
        this.texIndexBuffer.position(0);
        return changed;
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public void init(SkEye skeye) throws IOException {
        readData(skeye.getAssets().openFd(this.dataFileName).createInputStream());
        this.labelAlpha = getCurrLabelAlpha(skeye);
        setMarkerAlpha(getCurrMarkerAlpha(skeye));
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public void drawLabelES(int objNum, float x, float y, MyShadyRenderer r, LabelMaker labelMaker, boolean centerLabelHoriz, boolean centerLabelVert) {
        this.numberedLabels.drawLabelES20(x, y, getObjId(objNum), labelMaker, centerLabelHoriz, centerLabelVert, r, this.labelAlpha);
    }

    public DetailedCatalog(int catalogId, int tag) {
        super(catalogId, tag, true, false, true, true, 50.0f, true, 9.0f, true, false);
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public void setTheme(int themeOrdinal2) {
        this.themeOrdinal = themeOrdinal2;
    }

    @Override // com.lavadip.skeye.catalog.Catalog
    public void startConfig(Activity catalogActivity) {
        new CatalogFilterDialog(catalogActivity, C0031R.style.NightTheme_NoTitle_FullScreen, this).show();
    }

    private String labelAlphaKey() {
        return String.valueOf(getPrefPrefix()) + "labelAlpha";
    }

    private String markerAlphaKey() {
        return String.valueOf(getPrefPrefix()) + "markerAlpha";
    }

    private float getCurrLabelAlpha(SkEye skeye) {
        return skeye.settingsManager.getQuickPref(labelAlphaKey(), 0.5f);
    }

    private float getCurrMarkerAlpha(SkEye skeye) {
        return skeye.settingsManager.getQuickPref(markerAlphaKey(), 0.5f);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void setMarkerAlpha(float alpha) {
        for (int i = 0; i < this.colorArray.length; i += 4) {
            this.colorArray[i + 3] = alpha;
        }
    }

    public QuickSettingsManager.QuickSettingsGroup getQuickSettingsBase(SkEye skeye, MyShadyRenderer renderer, String groupName) {
        return new QuickSettingsManager.QuickSettingsGroup(new QuickSettingsManager.SettingDetails[]{new QuickSettingsManager.SettingDetails(new QuickSettingsManager.FloatRangeQuickSetting(labelAlphaKey(), skeye.getString(C0031R.string.label_opacity), "", 0.0f, 1.0f, 0.05f), new QuickSettingsManager.TypicalSettingChangeHandler<Float>(skeye) {
            /* class com.lavadip.skeye.catalog.DetailedCatalog.C00801 */

            @Override // com.lavadip.skeye.QuickSettingsManager.SettingChangeListener
            public /* bridge */ /* synthetic */ void onChange(String str, Object obj, boolean z) {
                onChange(str, (Float) obj, z);
            }

            @Override // com.lavadip.skeye.QuickSettingsManager.TypicalSettingChangeHandler
            public void onGLThread(String key, Float newValue, boolean trackingStopped) {
                DetailedCatalog.this.labelAlpha = newValue.floatValue();
            }
        }, Float.valueOf(getCurrLabelAlpha(skeye))), new QuickSettingsManager.SettingDetails(new QuickSettingsManager.FloatRangeQuickSetting(markerAlphaKey(), skeye.getString(C0031R.string.marker_opacity), "", 0.0f, 1.0f, 0.05f), new QuickSettingsManager.TypicalSettingChangeHandler<Float>(skeye) {
            /* class com.lavadip.skeye.catalog.DetailedCatalog.C00812 */

            @Override // com.lavadip.skeye.QuickSettingsManager.SettingChangeListener
            public /* bridge */ /* synthetic */ void onChange(String str, Object obj, boolean z) {
                onChange(str, (Float) obj, z);
            }

            @Override // com.lavadip.skeye.QuickSettingsManager.TypicalSettingChangeHandler
            public void onGLThread(String key, Float newValue, boolean trackingStopped) {
                DetailedCatalog.this.setMarkerAlpha(newValue.floatValue());
            }
        }, Float.valueOf(getCurrMarkerAlpha(skeye)))}, groupName, getNamePrefix());
    }
}
