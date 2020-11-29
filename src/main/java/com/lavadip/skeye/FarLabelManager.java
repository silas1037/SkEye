package com.lavadip.skeye;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.lavadip.skeye.astro.IntList;
import com.lavadip.skeye.astro.Sky;
import com.lavadip.skeye.astro.SkyIndex;
import com.lavadip.skeye.catalog.Catalog;
import com.lavadip.skeye.catalog.CatalogManager;
import com.lavadip.skeye.util.IntIntMap;
import com.lavadip.skeye.util.Util;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public final class FarLabelManager {
    private static final double MAX_DIST = Math.toRadians(30.0d);
    private static final double MIN_DIST = Math.toRadians(1.0d);
    private static final int MSG_COMPUTE_FAR_LABELS = 0;
    static Context appContext = null;
    private static final float[] colorArray = {0.3f, 0.3f, 0.3f, 0.7f, 0.3f, 0.15f, 0.15f, 0.8f, 0.3f, 0.0f, 0.0f, 0.85f};
    private static int farLabelCount = 0;
    public static final IntList[] farLabelObjects = new IntList[CatalogManager.catalogs.length];
    public static boolean hasFarLabels = false;
    private static final IntIntMap idToIndexMap = new IntIntMap();
    private static ShortBuffer labelLineIndexArray = null;
    public static float[] labelPos;
    private static float[] labelRotAngle;
    private static Vector3d[] labelRotAxis;
    public static SkyIndex labelSkyIndex;
    private static FloatBuffer myVertexArray = null;
    public static Handler workerHandler;
    private static final HandlerThread workerThread = new HandlerThread("Far label worker") {
        /* class com.lavadip.skeye.FarLabelManager.HandlerThreadC00151 */

        public void run() {
            Looper.prepare();
            FarLabelManager.workerHandler = new WorkerHandler(null);
            Looper.loop();
        }
    };
    static final Vector3d yVec = new Vector3d(0.0f, 1.0f, 0.0f);

    static {
        workerThread.start();
    }

    private static final class WorkerHandler extends Handler {
        private WorkerHandler() {
        }

        /* synthetic */ WorkerHandler(WorkerHandler workerHandler) {
            this();
        }

        public void handleMessage(Message msg) {
            FarLabelManager.computeFarLabels();
        }
    }

    public void startProcessing() {
        workerHandler.obtainMessage(0).sendToTarget();
    }

    /* access modifiers changed from: private */
    public static void computeFarLabels() {
        Vector3d jObj;
        double force;
        synchronized (farLabelObjects) {
            hasFarLabels = false;
        }
        farLabelCount = 0;
        for (int i = 0; i < farLabelObjects.length; i++) {
            farLabelObjects[i] = new IntList(32);
        }
        SkyIndex objSkyIndex = new SkyIndex(Sky.DEFAULT_INDEX_RA_DIVS, Sky.DEFAULT_INDEX_DEC_DIVS);
        int maxObjects = 0;
        int countDynamic = 0;
        Catalog[] catalogArr = CatalogManager.catalogs;
        for (Catalog c : catalogArr) {
            IntList selObjs = c.getSelObjs();
            int numCatalogObjs = selObjs.size;
            boolean canHaveFarLabel = c.canHaveFarLabel;
            for (int i2 = 0; i2 < numCatalogObjs; i2++) {
                if (c.shouldDrawLabel(CatalogManager.getObjNum(selObjs.get(i2)))) {
                    maxObjects++;
                    if (canHaveFarLabel) {
                        countDynamic++;
                    }
                }
            }
        }
        Vector3d[] cartPos = new Vector3d[maxObjects];
        for (int i3 = 0; i3 < maxObjects; i3++) {
            cartPos[i3] = new Vector3d();
        }
        int[] objIds = new int[maxObjects];
        int[] dynamicObjs = new int[countDynamic];
        IntList[] neighbourBlocks = new IntList[countDynamic];
        int countDynamic2 = 0;
        int currObjIndex = 0;
        Catalog[] catalogArr2 = CatalogManager.catalogs;
        int length = catalogArr2.length;
        int i4 = 0;
        while (i4 < length) {
            Catalog c2 = catalogArr2[i4];
            IntList selObjs2 = c2.getSelObjs();
            int numCatalogObjs2 = selObjs2.size;
            float[] catPos = c2.getPositions();
            boolean canHaveFarLabel2 = c2.canHaveFarLabel;
            for (int i5 = 0; i5 < numCatalogObjs2; i5++) {
                int objId = selObjs2.get(i5);
                int objNum = CatalogManager.getObjNum(objId);
                if (c2.shouldDrawLabel(objNum)) {
                    float ra = catPos[objNum * 2];
                    float dec = catPos[(objNum * 2) + 1];
                    map3d((double) ra, (double) dec, cartPos, currObjIndex);
                    objIds[currObjIndex] = objId;
                    if (canHaveFarLabel2) {
                        objSkyIndex.addObject(currObjIndex, ra, dec);
                        neighbourBlocks[countDynamic2] = objSkyIndex.getNeighbours(ra, dec);
                        countDynamic2++;
                        dynamicObjs[countDynamic2] = currObjIndex;
                    } else {
                        countDynamic2 = countDynamic2;
                    }
                    currObjIndex++;
                } else {
                    countDynamic2 = countDynamic2;
                }
            }
            i4++;
            countDynamic2 = countDynamic2;
        }
        Vector3d[] cartPosCopy = new Vector3d[maxObjects];
        Vector3d[] cartPosOrig = new Vector3d[maxObjects];
        for (int i6 = 0; i6 < maxObjects; i6++) {
            cartPosOrig[i6] = new Vector3d(cartPos[i6]);
            cartPosCopy[i6] = new Vector3d(cartPos[i6]);
        }
        labelSkyIndex = new SkyIndex(Sky.DEFAULT_INDEX_RA_DIVS, Sky.DEFAULT_INDEX_DEC_DIVS);
        labelPos = new float[(countDynamic2 * 3)];
        boolean continueProcessing = true;
        for (int simStep = 0; simStep < 64 && continueProcessing; simStep++) {
            for (int iIndex = 0; iIndex < dynamicObjs.length; iIndex++) {
                int i7 = dynamicObjs[iIndex];
                Vector3d iObj = cartPos[i7];
                Vector3d iObjCopy = cartPosCopy[i7];
                iObjCopy.setXYZ(iObj.f16x, iObj.f17y, iObj.f18z);
                IntList myNeighbourBlocks = neighbourBlocks[iIndex];
                for (int nb = 0; nb < myNeighbourBlocks.size; nb++) {
                    IntList neighbours = objSkyIndex.getObjects(myNeighbourBlocks.get(nb));
                    for (int n = 0; n < neighbours.size; n++) {
                        int j = neighbours.get(n);
                        if (i7 != j) {
                            jObj = cartPos[j];
                            double angleIJ = iObj.angleBetweenMag(jObj);
                            force = Math.min(1.0E-5d / (angleIJ * angleIJ), 0.05d);
                        } else {
                            jObj = cartPosOrig[j];
                            force = -0.22d * iObj.angleBetweenMag(jObj);
                        }
                        if (simStep < 4) {
                            force *= 0.2d;
                        }
                        if (Math.abs(force) > 1.0E-7d) {
                            iObjCopy.rotateAwayFrom((double) ((float) force), jObj, iObjCopy);
                            iObjCopy.normalise();
                        }
                    }
                }
            }
            cartPos = cartPosCopy;
            cartPosCopy = cartPos;
            double maxChange = 0.0d;
            for (int i8 = 0; i8 < maxObjects; i8++) {
                double diff = cartPos[i8].angleBetweenMag(cartPosCopy[i8]);
                if (diff > maxChange) {
                    maxChange = diff;
                }
            }
            if (maxChange <= 0.001d || workerHandler.hasMessages(0)) {
                continueProcessing = false;
            } else {
                continueProcessing = true;
            }
        }
        if (!workerHandler.hasMessages(0)) {
            Vector3d workVec = new Vector3d();
            double[] eqResult = new double[2];
            for (int i9 : dynamicObjs) {
                double angularDist = cartPos[i9].angleBetweenMag(cartPosOrig[i9]);
                if (angularDist > MIN_DIST && angularDist < MAX_DIST) {
                    getEqCoords(cartPos[i9], workVec, eqResult);
                    addLabel(objIds[i9], (float) eqResult[0], (float) eqResult[1], cartPos[i9]);
                }
            }
            finaliseLabelData(false);
        }
    }

    private void readFromFile(AssetManager assetMgr) {
    }

    private static void finaliseLabelData(boolean saveToFile) {
        if (farLabelCount > 0) {
            Log.d("SKEYE", "Far labels for " + farLabelCount);
            synchronized (farLabelObjects) {
                short[] labelLineIndices = new short[(farLabelCount * 2)];
                int objIndex = 0;
                IntList[] intListArr = farLabelObjects;
                for (IntList objs : intListArr) {
                    for (int n = 0; n < objs.size; n++) {
                        labelLineIndices[objIndex * 2] = (short) objIndex;
                        labelLineIndices[(objIndex * 2) + 1] = (short) (farLabelCount + objIndex);
                        objIndex++;
                    }
                }
                labelLineIndexArray = setLineIndex(labelLineIndices);
                int objIndex2 = 0;
                float[] vertices = new float[(farLabelCount * 3)];
                for (int i = 0; i < farLabelObjects.length; i++) {
                    float[] positions = CatalogManager.catalogs[i].getVecPositions();
                    IntList objs2 = farLabelObjects[i];
                    for (int n2 = 0; n2 < objs2.size; n2++) {
                        System.arraycopy(positions, objs2.get(n2) * 3, vertices, objIndex2 * 3, 3);
                        objIndex2++;
                    }
                }
                labelRotAxis = new Vector3d[farLabelCount];
                labelRotAngle = new float[farLabelCount];
                for (int i2 = 0; i2 < farLabelCount; i2++) {
                    int index = i2 * 3;
                    Vector3d originVector = new Vector3d((double) vertices[index], (double) vertices[index + 1], (double) vertices[index + 2], true);
                    Vector3d destVector = new Vector3d((double) labelPos[index], (double) labelPos[index + 1], (double) labelPos[index + 2], true);
                    labelRotAxis[i2] = originVector.crossMult(destVector, true);
                    labelRotAngle[i2] = (float) originVector.angleBetweenMag(destVector);
                }
                for (int i3 = 0; i3 < farLabelCount * 3; i3++) {
                    vertices[i3] = vertices[i3] * 9.0f;
                }
                myVertexArray = initVertexArray(myVertexArray, vertices);
                hasFarLabels = true;
                if (saveToFile) {
                    writeToFile();
                }
            }
        }
    }

    static void updateLabelPos(float maxAngle) {
        Vector3d originVector = new Vector3d();
        Vector3d destVector = new Vector3d();
        myVertexArray.position(0);
        for (int i = 0; i < farLabelCount; i++) {
            int index = (farLabelCount + i) * 3;
            originVector.setXYZ((double) myVertexArray.get(index), (double) myVertexArray.get(index + 1), (double) myVertexArray.get(index + 2));
            originVector.normalise();
            originVector.rotate((double) Math.min(labelRotAngle[i], maxAngle), labelRotAxis[i], destVector);
            originVector.scalarMultiplyInPlace(9.0d);
            labelPos[i * 3] = (float) destVector.f16x;
            labelPos[(i * 3) + 1] = (float) destVector.f17y;
            labelPos[(i * 3) + 2] = (float) destVector.f18z;
            myVertexArray.put((float) destVector.f16x);
            myVertexArray.put((float) destVector.f17y);
            myVertexArray.put((float) destVector.f18z);
        }
        myVertexArray.position(0);
    }

    public static void writeToFile() {
    }

    private static void getEqCoords(Vector3d vec, Vector3d projection, double[] result) {
        double dec = AstroUtil.computeAlt(vec.f16x, vec.f18z, vec.f17y);
        vec.rotateAwayFrom((double) ((float) dec), yVec, projection);
        result[0] = Util.makeAnglePositive(-Math.atan2(-projection.f16x, projection.f18z));
        result[1] = dec;
    }

    private static void map3d(double ra, double dec, Vector3d[] dest, int offset) {
        double cosDec = Math.cos(dec);
        dest[offset].setXYZ((double) ((float) (Math.sin(ra) * cosDec)), (double) ((float) Math.sin(dec)), (double) ((float) (cosDec * Math.cos(ra))));
        dest[offset].normalise();
    }

    private static void addLabel(int objId, float ra, float dec, Vector3d vec) {
        int catalogId = CatalogManager.getCatalog(objId);
        int objNum = CatalogManager.getObjNum(objId);
        labelSkyIndex.addObject(objId, ra, dec);
        farLabelObjects[catalogId].addUnique(objNum);
        labelPos[farLabelCount * 3] = (float) vec.f16x;
        labelPos[(farLabelCount * 3) + 1] = (float) vec.f17y;
        labelPos[(farLabelCount * 3) + 2] = (float) vec.f18z;
        idToIndexMap.put(objId, farLabelCount);
        farLabelCount++;
    }

    public boolean isFar(int objId) {
        if (!hasFarLabels) {
            return false;
        }
        int catalogId = CatalogManager.getCatalog(objId);
        return farLabelObjects[catalogId].contains(CatalogManager.getObjNum(objId));
    }

    public int getFarCount() {
        if (hasFarLabels) {
            return farLabelCount;
        }
        return 0;
    }

    public int getFarLabelIndex(int objId) {
        return idToIndexMap.get(objId);
    }

    private static FloatBuffer initVertexArray(FloatBuffer initVA, float[] locations) {
        FloatBuffer va = initVA;
        int totalLength = (farLabelCount * 3) + locations.length;
        if (va == null || va.capacity() != totalLength) {
            va = ByteBuffer.allocateDirect(totalLength * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        }
        va.position(0);
        va.put(labelPos, 0, farLabelCount * 3);
        va.put(locations);
        va.position(0);
        return va;
    }

    private static ShortBuffer setLineIndex(short[] lineIndices) {
        ShortBuffer retVal = ByteBuffer.allocateDirect(lineIndices.length * 4).order(ByteOrder.nativeOrder()).asShortBuffer();
        retVal.position(0);
        retVal.put(lineIndices);
        retVal.position(0);
        return retVal;
    }

    /* access modifiers changed from: package-private */
    public void drawLabelLinesES(MyShadyRenderer r, int themeOrdinal) {
        synchronized (farLabelObjects) {
            if (myVertexArray != null) {
                r.mVectorLineShader.draw(2.0f, r.mvpMatrix, myVertexArray, labelLineIndexArray, 1, labelLineIndexArray.capacity(), colorArray, Util.chooseColorOffset(colorArray.length, themeOrdinal));
            }
        }
    }

    public void onDestroy() {
        myVertexArray = null;
        labelLineIndexArray = null;
    }

    public void updateSettings(AssetManager assets) {
    }
}
