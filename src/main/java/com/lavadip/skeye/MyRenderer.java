package com.lavadip.skeye;

import android.content.SharedPreferences;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.Log;
import com.lavadip.skeye.astro.GridLines;
import com.lavadip.skeye.astro.IntList;
import com.lavadip.skeye.astro.Sky;
import com.lavadip.skeye.catalog.Catalog;
import com.lavadip.skeye.catalog.CatalogManager;
import com.lavadip.skeye.util.Util;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import javax.microedition.khronos.opengles.GL10;

public abstract class MyRenderer implements GLSurfaceView.Renderer {
    public static final float DEPTH_BEFORE_GRID = 9.0f;
    public static final float DEPTH_GRID = (9.0f * ((float) Math.sqrt(2.0d)));
    public static final float DEPTH_MAX = (DEPTH_GRID * 2.0f);
    public static final float DEPTH_ZOOM_MIN = 0.9f;
    public static final float FOV_MAX = 75.0f;
    public static final float FOV_MIN = 0.2f;
    protected static final float FOV_RES_SCALE = ((float) Math.toRadians((double) (90 / Sky.DEFAULT_INDEX_RA_DIVS)));
    static final int HORIZON_INDEX = 0;
    private static final double MAX_ANGLE_FOR_REFRESH = Math.toRadians(1.0d);
    protected static final int MIN_LABEL_TEXTURE_WIDTH = 1024;
    public static float fov = 20.0f;
    protected static volatile boolean fovChanged = false;
    protected static float minFov = 20.0f;
    protected static float newFov = fov;
    protected final SkEye activity;
    protected ShortBuffer altAzmGridLineIndexArray = null;
    protected FloatBuffer altAzmGridVertexArray = null;
    private final Vector3d bottomLeft;
    protected boolean buffInitialised = false;
    protected float cachedFov = -1.0f;
    private final Vector3d cachedXVec;
    private final Vector3d cachedZVec;
    protected FloatBuffer centerBuffer = null;
    protected final CenteredObj centeredObj;
    protected final float[] colorArray;
    protected int colorOffset;
    protected final IntList currBlocks;
    protected final IntList currObjs;
    protected final float[] directionLabelPoints;
    protected final String[] directionLabels;
    protected final float displayScaleFactor;
    private boolean isFirst;
    protected final LabelMaker labelMaker;
    private boolean labelsAdded;
    private long lastBlockUpdate;
    protected ShortBuffer lineIndexArray = null;
    protected final short[] lineIndices;
    protected final float[] mRotationMatrix;
    private final float[] objCoords;
    protected final float[] projectionMatrix;
    private final double[] tempResult;
    protected int themeOrdinal = 0;
    private final Vector3d topLeft;
    private final Vector3d topRight;
    protected FloatBuffer vertexArray = null;
    protected final float[] vertices = {0.0f, -DEPTH_GRID, 0.0f, -DEPTH_GRID, 0.0f, 0.0f, 0.0f, DEPTH_GRID, 0.0f, DEPTH_GRID, 0.0f, 0.0f};
    protected final int[] viewMatrix;
    protected final float[] winCoords;
    private final Vector3d workXVec;
    private final Vector3d workZVec;

    /* access modifiers changed from: package-private */
    public abstract void changeFovValue(GL10 gl10);

    /* access modifiers changed from: protected */
    public abstract Point getBestLabelTextureSize(GL10 gl10);

    /* access modifiers changed from: package-private */
    public abstract void setRotationMatrix(Orientation3d orientation3d, int i, int i2);

    /* access modifiers changed from: protected */
    public abstract void updateCenterBuffer();

    /* access modifiers changed from: protected */
    public abstract void updateCenteredObj(int i);

    /* access modifiers changed from: protected */
    public abstract void updateTheme(int i);

    static float getFovValue() {
        return fov;
    }

    static void setFovValue(float value) {
        newFov = value;
        fovChanged = true;
    }

    /* access modifiers changed from: protected */
    public void addLabels(GL10 gl) {
        if (!this.labelsAdded) {
            LabelPaints paints = new LabelPaints(this.displayScaleFactor);
            this.labelMaker.beginAdding(getBestLabelTextureSize(gl));
            for (String directionLabel : this.directionLabels) {
                this.labelMaker.add(directionLabel, paints.directionPaint);
            }
            for (Catalog catalog : CatalogManager.catalogs) {
                catalog.initLabels(this.labelMaker, paints, this.displayScaleFactor);
            }
            this.labelMaker.endAdding();
            this.labelsAdded = true;
        }
    }

    MyRenderer(float displayScaleFactor2, SkEye skEye, float[] rotationMatrix) {
        short[] sArr = new short[4];
        sArr[1] = 1;
        sArr[2] = 2;
        sArr[3] = 3;
        this.lineIndices = sArr;
        this.projectionMatrix = new float[16];
        int[] iArr = new int[4];
        iArr[2] = 100;
        iArr[3] = 100;
        this.viewMatrix = iArr;
        this.directionLabelPoints = new float[]{0.0f, 1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f};
        this.labelsAdded = false;
        this.objCoords = new float[16];
        this.winCoords = new float[4];
        this.centeredObj = new CenteredObj();
        this.isFirst = true;
        this.colorArray = new float[]{0.5f, 1.0f, 0.5f, 0.5f, 1.0f, 0.94f, 0.16f, 0.4f, 1.0f, 0.25f, 0.25f, 0.8f, 0.0f, 0.0f, 0.05f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.5f, 0.5f, 0.25f, 0.5f, 1.0f, 0.47f, 0.08f, 0.4f, 1.0f, 0.125f, 0.125f, 0.8f, 0.0f, 0.0f, 0.02f, 1.0f, 1.0f, 0.5f, 0.5f, 1.0f, 0.5f, 0.0f, 0.0f, 0.5f, 1.0f, 0.0f, 0.0f, 0.4f, 1.0f, 0.0f, 0.0f, 0.8f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f};
        this.colorOffset = 0;
        this.cachedZVec = new Vector3d(0.0f, 0.0f, 1.0f);
        this.cachedXVec = new Vector3d(0.0f, 0.0f, 1.0f);
        this.workZVec = new Vector3d(0.0f, 0.0f, 0.0f);
        this.workXVec = new Vector3d(0.0f, 0.0f, 0.0f);
        this.currBlocks = new IntList(16);
        this.currObjs = new IntList(256);
        this.topLeft = new Vector3d();
        this.topRight = new Vector3d();
        this.bottomLeft = new Vector3d();
        this.tempResult = new double[3];
        this.lastBlockUpdate = 0;
        this.activity = skEye;
        this.mRotationMatrix = rotationMatrix;
        this.displayScaleFactor = displayScaleFactor2;
        this.directionLabels = Directions.mainDirStrs;
        this.labelMaker = new LabelMaker(true);
    }

    /* access modifiers changed from: protected */
    public void getProjectedVector(float x, float y, float[] rotMatrix, Vector3d targetVec) {
        if (GLU.gluUnProject(x, ((float) this.viewMatrix[3]) - y, 1.0f, rotMatrix, 0, this.projectionMatrix, 0, this.viewMatrix, 0, this.objCoords, 0) == 1) {
            targetVec.setXYZ(this.objCoords, 0);
            targetVec.normalise();
        }
    }

    private void getProjectedVector(float x, float y, Vector3d targetVec) {
        if (GLU.gluUnProject(x, y, 1.0f, this.mRotationMatrix, 0, this.projectionMatrix, 0, this.viewMatrix, 0, this.objCoords, 0) == 1) {
            targetVec.setXYZ(this.objCoords, 0);
            targetVec.normalise();
        }
    }

    public void onSurfaceChanged(GL10 gl, int w, int h) {
        this.viewMatrix[0] = 0;
        this.viewMatrix[1] = 0;
        this.viewMatrix[2] = w;
        this.viewMatrix[3] = h;
        gl.glViewport(0, 0, w, h);
        changeFovValueBase(gl);
    }

    /* access modifiers changed from: protected */
    public final void changeFovValueBase(GL10 gl) {
        if (fov > 75.0f) {
            fov = 75.0f;
        } else if (fov < 0.2f) {
            fov = 0.2f;
        }
        float ratioWToH = ((float) this.viewMatrix[2]) / ((float) this.viewMatrix[3]);
        minFov = ratioWToH < 1.0f ? fov * ratioWToH : fov;
        changeFovValue(gl);
        synchronized (FarLabelManager.farLabelObjects) {
            if (FarLabelManager.hasFarLabels) {
                FarLabelManager.updateLabelPos((float) (Math.toRadians((double) minFov) / 4.0d));
            }
        }
        this.activity.myHandler.obtainMessage(1, (int) (fov * ratioWToH * 100.0f), (int) (fov * 100.0f)).sendToTarget();
    }

    public static ShortBuffer setLineIndex(short[] lineIndices2) {
        ShortBuffer retVal = ByteBuffer.allocateDirect(lineIndices2.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
        retVal.position(0);
        retVal.put(lineIndices2);
        retVal.position(0);
        return retVal;
    }

    protected static void printMatrix(String name, float[] matrix) {
        String str = "";
        for (float element : matrix) {
            str = String.valueOf(str) + String.format("%6.3f, ", Float.valueOf(element));
        }
        Log.d("SKEYE", String.valueOf(name) + " Matrix: " + str);
    }

    /* access modifiers changed from: package-private */
    public void onDestroy() {
        if (this.buffInitialised) {
            this.vertexArray = null;
            this.lineIndexArray = null;
            this.altAzmGridVertexArray = null;
            this.altAzmGridLineIndexArray = null;
            this.centerBuffer = null;
            this.buffInitialised = false;
        }
        for (Catalog c : CatalogManager.catalogs) {
            c.onDestroy();
        }
        this.labelMaker.onDestroy();
    }

    /* access modifiers changed from: protected */
    public final void updateVertices() {
        this.centeredObj.skyChanged = true;
        int numPredefVertices = this.vertices.length / 3;
        if (!this.buffInitialised) {
            this.vertexArray = ByteBuffer.allocateDirect(numPredefVertices * 3 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        }
        this.vertexArray.position(0);
        this.vertexArray.put(this.vertices);
        this.vertexArray.position(0);
        if (!this.buffInitialised) {
            this.lineIndexArray = setLineIndex(this.lineIndices);
            this.altAzmGridVertexArray = ByteBuffer.allocateDirect(GridLines.altGridPoints.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
            this.altAzmGridVertexArray.position(0);
            this.altAzmGridVertexArray.put(GridLines.altGridPoints);
            this.altAzmGridVertexArray.position(0);
            this.altAzmGridLineIndexArray = setLineIndex(GridLines.altGridLineIndices);
            this.centerBuffer = ByteBuffer.allocateDirect(12).order(ByteOrder.nativeOrder()).asFloatBuffer();
        }
        this.buffInitialised = true;
    }

    /* access modifiers changed from: protected */
    public final class CenteredObj {
        static final int SOME_INVALID_ID = -1;
        static final double SOME_LARGE_DIST = 1000000.0d;
        private final Vector3d centerCoords = new Vector3d();
        private int centeredObjId = -1;
        private double minDist = SOME_LARGE_DIST;
        private int prevCenteredObjId = -1;
        private boolean skyChanged = false;

        protected CenteredObj() {
        }

        /* access modifiers changed from: package-private */
        public void update(int objectId, float[] points, int pointOffset) {
            if (objectId >= 0) {
                float xDist = MyRenderer.this.winCoords[0] - ((float) (MyRenderer.this.viewMatrix[2] / 2));
                float yDist = MyRenderer.this.winCoords[1] - ((float) (MyRenderer.this.viewMatrix[3] / 2));
                double distCenter = Math.sqrt((double) ((xDist * xDist) + (yDist * yDist)));
                if (distCenter < this.minDist) {
                    this.minDist = distCenter;
                    this.centeredObjId = objectId;
                    this.centerCoords.setXYZ(points, pointOffset);
                    this.centerCoords.normalise();
                }
            }
        }

        /* access modifiers changed from: package-private */
        public boolean changed() {
            return this.prevCenteredObjId != this.centeredObjId || this.skyChanged;
        }

        /* access modifiers changed from: package-private */
        public boolean isValid() {
            return this.centeredObjId >= 0;
        }

        /* access modifiers changed from: package-private */
        public Vector3d getCenterCoords() {
            return this.centerCoords;
        }

        /* access modifiers changed from: package-private */
        public int getObj() {
            return this.centeredObjId;
        }

        /* access modifiers changed from: package-private */
        public void reset() {
            this.prevCenteredObjId = this.centeredObjId;
            this.centeredObjId = -1;
            this.minDist = SOME_LARGE_DIST;
            this.skyChanged = false;
        }
    }

    /* access modifiers changed from: package-private */
    public final void updateSettings(SharedPreferences prefs) {
        boolean anyChange = this.isFirst;
        this.isFirst = false;
        Catalog[] catalogArr = CatalogManager.catalogs;
        for (Catalog c : catalogArr) {
            anyChange |= c.updateSettings(prefs);
            c.setTheme(this.themeOrdinal);
        }
        if (anyChange) {
            Sky.updateIndex();
            Sky.createFarLabelManager(this.activity.getAssets());
            Sky.farLabelMgr.startProcessing();
        }
    }

    /* access modifiers changed from: package-private */
    public final void updateSky(long currTime) {
        CatalogManager.updateSky(currTime);
        this.centeredObj.reset();
        this.centeredObj.skyChanged = true;
        figureOutBlocks(null);
        recomputeCenteredObj();
    }

    /* access modifiers changed from: protected */
    public void recomputeCenteredObj() {
        for (int i = 0; i < this.currObjs.size; i++) {
            updateCenteredObj(this.currObjs.get(i));
        }
        for (Catalog c : CatalogManager.dynamicCatalogs) {
            IntList objs = c.getSelObjs();
            for (int i2 = objs.size - 1; i2 >= 0; i2--) {
                updateCenteredObj(objs.get(i2));
            }
        }
        if (this.centeredObj.changed()) {
            updateCenterBuffer();
            this.activity.setCenteredObj(this.centeredObj.getObj());
        }
    }

    /* access modifiers changed from: package-private */
    public final void setTheme(int themeOrdinal2) {
        this.themeOrdinal = themeOrdinal2;
        this.colorOffset = Util.chooseColorOffset(this.colorArray.length, themeOrdinal2);
        for (Catalog c : CatalogManager.catalogs) {
            c.setTheme(themeOrdinal2);
        }
        updateTheme(themeOrdinal2);
    }

    protected static final void glErrorCheck(GL10 gl, String msg) {
        int error = gl.glGetError();
        if (error != 0) {
            Log.e("SKEYE", "Encountered error in " + msg + " : " + GLU.gluErrorString(error));
        }
    }

    /* access modifiers changed from: protected */
    public void figureOutBlocks(Orientation3d currOrientation) {
        if (currOrientation != null) {
            this.workZVec.copyFrom(currOrientation.zDir);
            this.workXVec.copyFrom(currOrientation.xDir);
        }
        long currSkyTime = Sky.getCurrentTime();
        if (this.cachedZVec.angleBetweenMag(this.workZVec) > MAX_ANGLE_FOR_REFRESH || this.cachedXVec.angleBetweenMag(this.workXVec) > MAX_ANGLE_FOR_REFRESH || ((double) Math.abs((this.cachedFov / fov) - 1.0f)) > 0.1d || Math.abs(currSkyTime - this.lastBlockUpdate) > 60000) {
            this.lastBlockUpdate = currSkyTime;
            this.cachedFov = fov;
            this.cachedZVec.copyFrom(this.workZVec);
            this.cachedXVec.copyFrom(this.workXVec);
            this.currBlocks.clearList();
            this.currObjs.clearList();
            getProjectedVector(0.0f, 0.0f, this.topLeft);
            getProjectedVector((float) this.viewMatrix[2], 0.0f, this.topRight);
            getProjectedVector(0.0f, (float) this.viewMatrix[3], this.bottomLeft);
            float horizFov = (fov * ((float) this.viewMatrix[2])) / ((float) this.viewMatrix[3]);
            Vector3d vertRotAxis = this.topLeft.crossMult(this.bottomLeft, true);
            Vector3d horizRotAxis = this.topLeft.crossMult(this.topRight, true);
            Vector3d currVertScan = new Vector3d();
            Vector3d currHorizScan = new Vector3d();
            float endVertAngle = (float) (Math.toRadians((double) fov) + ((double) FOV_RES_SCALE));
            float endHorizAngle = (float) (Math.toRadians((double) horizFov) + ((double) FOV_RES_SCALE));
            for (float i = 0.0f; i <= endVertAngle; i += FOV_RES_SCALE) {
                this.topLeft.rotate((double) i, vertRotAxis, currVertScan);
                for (float j = 0.0f; j <= endHorizAngle; j += FOV_RES_SCALE) {
                    currVertScan.rotate((double) j, horizRotAxis, currHorizScan);
                    Sky.getEqCoords(currHorizScan, this.tempResult);
                    int blockIndex = Sky.skyIndex.getBlockIndex((float) this.tempResult[1], (float) this.tempResult[2]);
                    if (this.currBlocks.addUnique(blockIndex)) {
                        this.currObjs.addUnique(Sky.skyIndex.getObjects(blockIndex));
                    }
                }
            }
        }
    }
}
