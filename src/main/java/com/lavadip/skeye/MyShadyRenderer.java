package com.lavadip.skeye;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.Point;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;
import com.lavadip.skeye.QuickSettingsManager;
import com.lavadip.skeye.astro.IntList;
import com.lavadip.skeye.astro.Sky;
import com.lavadip.skeye.catalog.Catalog;
import com.lavadip.skeye.catalog.CatalogManager;
import com.lavadip.skeye.shader.PointShader;
import com.lavadip.skeye.shader.Shader;
import com.lavadip.skeye.shader.StarShader;
import com.lavadip.skeye.shader.VectorLineShader;
import com.lavadip.skeye.util.Util;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

@TargetApi(8)
public final class MyShadyRenderer extends MyRenderer {
    public static final int CIRCULAR_SHADER_NUM_POINTS = 32;
    public static final int CIRCULAR_SHADER_POSITION_SIZE_BYTES = 12;
    public static final int CIRCULAR_SHADER_POSITION_SIZE_FLOATS = 3;
    private static final String altAzmAlphaKey = "altAzmAlpha";
    private static final String directionLabelAlphaKey = "directionLabelAlpha";
    private static final String highlightAlphaKey = "highlightAlpha";
    private static final String horizonLineAlphaKey = "horizonLineAlpha";
    static int mCenterPointerTexId;
    public static int mCometTextureId;
    public static int mDSOMarkTexId;
    static int mDSOTexId;
    public static int mSatelliteTextureId;
    public static int mStarTextureId;
    private final float[] centerRotMatrix = {1.0f, 0.0f, 0.0f, 1.0f};
    private double centerRotation = 0.0d;
    private float directionLabelAlpha = 0.0f;
    private final float[] labelPoints4 = {0.0f, 0.0f, 0.0f, 1.0f};
    public DSOShader mDSOShader;
    public LineShader mLineShader;
    public MoonShader mMoonShader;
    public PlainShader mPlainShader;
    public PointShader mPointShader;
    public StarShader mStarShader;
    TextShader mTextShader;
    public VectorLineShader mVectorLineShader;
    public final float[] mvpGeoCentricMatrix = new float[16];
    public final float[] mvpMatrix = new float[32];
    public final float[] mvpPrecessMatrix = new float[32];
    final float[] mvpStationaryMatrix = new float[16];
    private final float[] precessedYVec = new float[4];

    MyShadyRenderer(float displayScaleFactor, SkEye skEye, float[] rotationMatrix) {
        super(displayScaleFactor, skEye, rotationMatrix);
        setHorizonAlpha(getCurrHorizonLineAlpha(skEye));
        setDirectionLabelAlpha(getCurrDirectionLabelAlpha(skEye));
        setAltAzmAlpha(getCurrAltAzmAlpha(skEye));
    }

    public void onDrawFrame(GL10 gl) {
        boolean z;
        setGLClearColor();
        GLES20.glClear(16640);
        if (fovChanged) {
            fovChanged = false;
            fov = newFov;
            changeFovValueBase(gl);
        }
        GLES20.glEnable(3042);
        GLES20.glActiveTexture(33984);
        GLES20.glBlendFunc(770, 771);
        this.mVectorLineShader.draw(2.0f * this.displayScaleFactor, this.mvpStationaryMatrix, this.vertexArray, this.lineIndexArray, 2, this.lineIndexArray.capacity(), this.colorArray, this.colorOffset);
        this.mVectorLineShader.draw(2.0f * this.displayScaleFactor, this.mvpStationaryMatrix, this.altAzmGridVertexArray, this.altAzmGridLineIndexArray, 1, this.altAzmGridLineIndexArray.capacity(), this.colorArray, this.colorOffset + 8);
        for (Catalog c : CatalogManager.catalogs) {
            c.drawES20(this, this.currBlocks, minFov);
        }
        if (fov < 50.0f) {
            Sky.farLabelMgr.drawLabelLinesES(this, this.themeOrdinal);
        }
        if (this.centeredObj.isValid()) {
            if (this.centeredObj.changed()) {
                updateCenterBuffer();
            }
            this.centerRotation += 0.08d;
            PointShader.makeRotMatrix(this.centerRotation, this.centerRotMatrix);
            this.mPointShader.beginDrawing(mCenterPointerTexId);
            this.mPointShader.draw(this.mvpMatrix, this.colorArray, this.colorOffset + 16, 32.0f * this.displayScaleFactor, this.centerBuffer, 1, this.centerRotMatrix);
        }
        this.centeredObj.reset();
        this.labelMaker.beginDrawingES20(this);
        synchronized (FarLabelManager.farLabelObjects) {
            boolean showFarLabels = FarLabelManager.hasFarLabels;
            for (int i = 0; i < this.currObjs.size; i++) {
                int objId = this.currObjs.get(i);
                int catalogId = CatalogManager.getCatalog(objId);
                int objNum = CatalogManager.getObjNum(objId);
                Catalog catalog = CatalogManager.catalogs[catalogId];
                if (!showFarLabels || !Sky.farLabelMgr.isFar(objId)) {
                    z = true;
                } else {
                    z = false;
                }
                drawCatalogLabel(catalog, objId, objNum, z, true, false);
            }
            if (showFarLabels) {
                for (int i2 = 0; i2 < this.currBlocks.size; i2++) {
                    IntList blockObjs = FarLabelManager.labelSkyIndex.getObjects(this.currBlocks.get(i2));
                    for (int j = 0; j < blockObjs.size; j++) {
                        int objId2 = blockObjs.get(j);
                        drawCatalogLabel(FarLabelManager.labelPos, Sky.farLabelMgr.getFarLabelIndex(objId2) * 3, CatalogManager.catalogs[CatalogManager.getCatalog(objId2)], -1, CatalogManager.getObjNum(objId2), true, true, true);
                    }
                }
            }
        }
        Catalog[] catalogArr = CatalogManager.dynamicCatalogs;
        for (Catalog c2 : catalogArr) {
            IntList objs = c2.getSelObjs();
            for (int i3 = objs.size - 1; i3 >= 0; i3--) {
                int objId3 = objs.get(i3);
                drawCatalogLabel(c2, objId3, CatalogManager.getObjNum(objId3), true, true, false);
            }
        }
        for (int i4 = 0; i4 < this.directionLabels.length; i4++) {
            updateLabelsInternal(true, true, this.directionLabelPoints, i4 * 3, i4, -1, this.directionLabelAlpha);
        }
        this.labelMaker.endDrawingES20();
        if (this.activity.findHerringMode) {
            Matrix.multiplyMV(this.winCoords, 0, this.mvpStationaryMatrix, 0, new float[]{(float) this.activity.herringVector.f16x, (float) this.activity.herringVector.f17y, (float) this.activity.herringVector.f18z, 1.0f}, 0);
            this.activity.herringVisible = isVisible(this.winCoords);
            if (this.activity.herringVisible) {
                projectToScreen(this.winCoords);
                this.activity.herringCoords[0] = this.winCoords[0];
                this.activity.herringCoords[1] = this.winCoords[1];
                this.activity.herringCoords[2] = this.winCoords[2];
            }
        }
        if (this.centeredObj.changed()) {
            this.activity.setCenteredObj(this.centeredObj.getObj());
        }
    }

    private void drawCatalogLabel(Catalog catalog, int objectId, int objNum, boolean drawLabel, boolean centerLabelHoriz, boolean centerLabelVert) {
        drawCatalogLabel(catalog.getVecPositions(), objNum * 3, catalog, objectId, objNum, drawLabel, centerLabelHoriz, centerLabelVert);
    }

    private void drawCatalogLabel(float[] points, int pointOffset, Catalog catalog, int objectId, int objNum, boolean drawLabel, boolean centerLabelHoriz, boolean centerLabelVert) {
        this.labelPoints4[0] = points[pointOffset];
        this.labelPoints4[1] = points[pointOffset + 1];
        this.labelPoints4[2] = points[pointOffset + 2];
        Matrix.multiplyMV(this.winCoords, 0, this.mvpMatrix, 0, this.labelPoints4, 0);
        if (isVisible(this.winCoords)) {
            projectToScreen(this.winCoords);
            if (catalog.isObjVisible(objNum, fov)) {
                this.centeredObj.update(objectId, points, pointOffset);
                if (drawLabel && catalog.maxFovValue > fov) {
                    catalog.drawLabelES(objNum, this.winCoords[0], this.winCoords[1], this, this.labelMaker, centerLabelHoriz, centerLabelVert);
                }
            }
        }
    }

    private void projectToScreen(float[] coords) {
        coords[0] = coords[0] / coords[3];
        coords[1] = coords[1] / coords[3];
        coords[0] = coords[0] + 1.0f;
        coords[1] = coords[1] + 1.0f;
        coords[0] = coords[0] * ((float) this.viewMatrix[2]) * 0.5f;
        coords[1] = coords[1] * ((float) this.viewMatrix[3]) * 0.5f;
    }

    private void updateMVPMatrix() {
        Matrix.multiplyMM(this.mvpStationaryMatrix, 0, this.projectionMatrix, 0, this.mRotationMatrix, 0);
        double era = Sky.getRotationAngle();
        Matrix.rotateM(this.mvpMatrix, 16, this.mRotationMatrix, 0, (float) Math.toDegrees((double) Sky.getUserLatitude()), 1.0f, 0.0f, 0.0f);
        Util.rotateMatrixAroundY(this.mvpMatrix, 16, -era);
        Matrix.multiplyMM(this.mvpMatrix, 0, this.projectionMatrix, 0, this.mvpMatrix, 16);
        float[] precessionMatrix = Sky.getPrecessionMatrix();
        Matrix.multiplyMV(this.precessedYVec, 0, precessionMatrix, 0, new float[]{0.0f, 1.0f, 0.0f, 1.0f}, 0);
        Matrix.multiplyMM(this.mvpPrecessMatrix, 16, this.mRotationMatrix, 0, precessionMatrix, 0);
        Util.rotateMatrixAroundX(this.mvpPrecessMatrix, 16, (double) Sky.getUserLatitude());
        Matrix.rotateM(this.mvpPrecessMatrix, 16, (float) Math.toDegrees(-era), -this.precessedYVec[0], this.precessedYVec[1], -this.precessedYVec[2]);
        Matrix.multiplyMM(this.mvpPrecessMatrix, 0, this.projectionMatrix, 0, this.mvpPrecessMatrix, 16);
        System.arraycopy(precessionMatrix, 0, this.mvpGeoCentricMatrix, 0, 16);
        Util.rotateMatrixAroundX(this.mvpGeoCentricMatrix, 0, (double) Sky.getUserLatitude());
        Matrix.rotateM(this.mvpGeoCentricMatrix, 0, (float) Math.toDegrees(-era), -this.precessedYVec[0], this.precessedYVec[1], -this.precessedYVec[2]);
    }

    private static boolean isVisible(float[] objCoords) {
        float w = objCoords[3];
        return objCoords[0] >= (-w) && objCoords[0] <= w && objCoords[1] >= (-w) && objCoords[1] <= w && objCoords[2] >= (-w) && objCoords[2] <= w;
    }

    private void updateLabelsInternal(boolean centerLabel, boolean drawLabels, float[] points, int pointOffset, int labelOffset, int objectId, float alpha) {
        Matrix.multiplyMV(this.winCoords, 0, this.mvpStationaryMatrix, 0, new float[]{points[pointOffset], points[pointOffset + 1], points[pointOffset + 2], 1.0f}, 0);
        if (isVisible(this.winCoords)) {
            projectToScreen(this.winCoords);
            if (drawLabels) {
                this.labelMaker.drawES20(this, this.winCoords[0], this.winCoords[1] + 2.0f, labelOffset, centerLabel, false, 0.0f, alpha);
            }
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.lavadip.skeye.MyRenderer
    public void changeFovValue(GL10 gl) {
        glhPerspectivef2(this.projectionMatrix, fov, ((float) this.viewMatrix[2]) / ((float) this.viewMatrix[3]), 0.9f, DEPTH_MAX * 1.2f);
    }

    private static void glhPerspectivef2(float[] matrix, float fovyInDegrees, float aspectRatio, float znear, float zfar) {
        float ymax = (float) (((double) znear) * Math.tan((((double) fovyInDegrees) * 3.141592653589793d) / 360.0d));
        float xmax = ymax * aspectRatio;
        Matrix.frustumM(matrix, 0, -xmax, xmax, -ymax, ymax, znear, zfar);
    }

    /* access modifiers changed from: protected */
    @Override // com.lavadip.skeye.MyRenderer
    public Point getBestLabelTextureSize(GL10 glUnused) {
        int requiredHeight;
        int bestWidth;
        if (((double) this.displayScaleFactor) < 1.0d) {
            requiredHeight = 512;
        } else if (((double) this.displayScaleFactor) < 2.5d) {
            requiredHeight = 2048;
        } else {
            requiredHeight = 4096;
        }
        int[] params = new int[1];
        GLES20.glGetIntegerv(3379, params, 0);
        int maxTextureSize = params[0];
        Log.d("SKEYE", "Max texture size = " + maxTextureSize);
        int bestHeight = Math.min(requiredHeight, maxTextureSize);
        if (((double) this.displayScaleFactor) < 2.5d) {
            bestWidth = 1024;
        } else {
            bestWidth = 2048;
        }
        Log.d("SKEYE", String.format("Best texture size (w x h) = %d x %d", Integer.valueOf(bestWidth), Integer.valueOf(bestHeight)));
        return new Point(bestWidth, bestHeight);
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig arg1) {
        addLabels(null);
        this.labelMaker.shutdown(gl);
        this.labelMaker.initializeES20();
        this.labelMaker.createTexturesES20();
        setGLClearColor();
        Resources resources = this.activity.getResources();
        mStarTextureId = Shader.loadTexture(resources.openRawResource(C0031R.raw.star_composite));
        mSatelliteTextureId = Shader.loadTexture(resources.openRawResource(C0031R.raw.satellite));
        mDSOTexId = Shader.loadTextureRGBA(resources.openRawResource(C0031R.raw.dso_texture));
        mDSOMarkTexId = Shader.loadTextureRGBA(resources.openRawResource(C0031R.raw.dso_mark));
        mCenterPointerTexId = Shader.loadTextureRGBA(resources.openRawResource(C0031R.raw.center_pointer));
        mCometTextureId = Shader.loadTexture(resources.openRawResource(C0031R.raw.comet_texture));
        this.mStarShader = new StarShader(this.displayScaleFactor, this.themeOrdinal, mStarTextureId);
        this.mLineShader = new LineShader();
        this.mVectorLineShader = new VectorLineShader();
        this.mTextShader = new TextShader();
        this.mPointShader = new PointShader();
        this.mPlainShader = new PlainShader();
        this.mMoonShader = new MoonShader();
        this.mDSOShader = new DSOShader();
        updateVertices();
        updateTheme(this.themeOrdinal);
    }

    /* access modifiers changed from: package-private */
    @Override // com.lavadip.skeye.MyRenderer
    public void setRotationMatrix(Orientation3d currOrientation, int displayOrientation, int currOrientInt) {
        updateMVPMatrix();
        figureOutBlocks(currOrientation);
        int correctedLabelOrientation = (displayOrientation - currOrientInt) % 4;
        if (correctedLabelOrientation < 0) {
            correctedLabelOrientation += 4;
        }
        this.labelMaker.setOrientation(correctedLabelOrientation);
    }

    private void setGLClearColor() {
        GLES20.glClearColor(this.colorArray[this.colorOffset + 12], this.colorArray[this.colorOffset + 13], this.colorArray[this.colorOffset + 14], this.colorArray[this.colorOffset + 15]);
    }

    /* access modifiers changed from: protected */
    @Override // com.lavadip.skeye.MyRenderer
    public void updateTheme(int a_themeOrdinal) {
        if (this.mStarShader != null) {
            this.mStarShader.setTheme(a_themeOrdinal);
            this.mTextShader.setTheme(a_themeOrdinal);
        }
    }

    public final class LineShader extends Shader {
        private static final int POSITION_SIZE_BYTES = 8;
        private static final int POSITION_SIZE_FLOATS = 2;
        private static final String fLineShaderStr = "precision mediump float;                             uniform vec4 u_color;void main()                                          {                                                      gl_FragColor = u_color;}";
        private static final String vLineShaderStr = "uniform mat4 u_mvpMatrix;attribute vec2 a_position;void main(){  float ra = a_position.x;  float dec = a_position.y;  float cosd = cos(dec);  float x = 9.0 * sin(ra)*cosd;  float y = 9.0 * sin(dec);  float z = 9.0 * cos(ra)*cosd;  vec4 position4 = vec4(x, y, z, 1);  gl_Position = u_mvpMatrix * position4;}";
        private final int mLineColorLoc = GLES20.glGetUniformLocation(this.mProgramId, "u_color");
        private final int mLineMvpLoc = GLES20.glGetUniformLocation(this.mProgramId, "u_mvpMatrix");
        private final int mLinePosLoc = GLES20.glGetAttribLocation(this.mProgramId, "a_position");

        LineShader() {
            super(vLineShaderStr, fLineShaderStr);
        }

        public void draw(float lineWidth, float[] a_mvpMatrix, float[] a_colorArray, int a_colorOffset, Buffer vertexBuffer, Buffer a_lineIndexArray, int count) {
            activate();
            GLES20.glLineWidth(lineWidth);
            GLES20.glUniformMatrix4fv(this.mLineMvpLoc, 1, false, a_mvpMatrix, 0);
            GLES20.glUniform4fv(this.mLineColorLoc, 1, a_colorArray, a_colorOffset);
            GLES20.glVertexAttribPointer(this.mLinePosLoc, 2, 5126, false, 8, vertexBuffer);
            GLES20.glEnableVertexAttribArray(this.mLinePosLoc);
            GLES20.glDrawElements(1, count, 5123, a_lineIndexArray);
        }
    }

    /* access modifiers changed from: package-private */
    public final class TextShader extends Shader {
        private static final int POSITION_SIZE_BYTES = 8;
        private static final int POSITION_SIZE_FLOATS = 2;
        private static final int TEX_COORD_BYTES = 8;
        private static final String fShaderStr = "precision mediump float; uniform sampler2D s_texture; uniform vec4 v_color_filter; varying vec2 v_texcoords; void main() {  gl_FragColor = texture2D( s_texture, v_texcoords) * v_color_filter;  }";
        private static final String vShaderStr = "attribute vec2 a_position; attribute vec2 a_texcoords; varying vec2 v_texcoords; void main() {  gl_Position.xy = a_position;  gl_Position.z = 0.0;  gl_Position.w = 1.0;  v_texcoords = a_texcoords;}";
        final int mColorFilter;
        final int mCropLoc;
        final int mPosLoc;
        final int mSamplerLoc;
        final int mTexCoordLoc;
        private final FloatBuffer texCoordBuffer = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder()).asFloatBuffer();
        private final float[] textColorFilterArray = {1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.5f, 0.5f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f};
        private int textColorOffset = 0;
        private final FloatBuffer vertexPosBuffer = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder()).asFloatBuffer();

        TextShader() {
            super(vShaderStr, fShaderStr);
            if (this.mProgramId == 0) {
                Log.e("SKEYE", "Error Loading TextShader");
                this.mColorFilter = -1;
                this.mSamplerLoc = -1;
                this.mPosLoc = -1;
                this.mCropLoc = -1;
                this.mTexCoordLoc = -1;
                return;
            }
            this.mPosLoc = GLES20.glGetAttribLocation(this.mProgramId, "a_position");
            this.mTexCoordLoc = GLES20.glGetAttribLocation(this.mProgramId, "a_texcoords");
            this.mCropLoc = GLES20.glGetUniformLocation(this.mProgramId, "u_crop");
            this.mSamplerLoc = GLES20.glGetUniformLocation(this.mProgramId, "s_texture");
            this.mColorFilter = GLES20.glGetUniformLocation(this.mProgramId, "v_color_filter");
        }

        /* access modifiers changed from: package-private */
        public void beginDrawing() {
            activate();
        }

        /* access modifiers changed from: package-private */
        public void draw(float[] crop, int orientation, float x, float y, float w, float h, float alpha) {
            this.textColorFilterArray[this.textColorOffset + 3] = alpha;
            GLES20.glUniform4fv(this.mColorFilter, 1, this.textColorFilterArray, this.textColorOffset);
            float scrLX = ((2.0f * ((float) ((int) x))) / ((float) MyShadyRenderer.this.viewMatrix[2])) - 1.0f;
            float scrBY = ((2.0f * ((float) ((int) y))) / ((float) MyShadyRenderer.this.viewMatrix[3])) - 1.0f;
            float scrRX = ((2.0f * ((float) ((int) (x + w)))) / ((float) MyShadyRenderer.this.viewMatrix[2])) - 1.0f;
            float scrTY = ((2.0f * ((float) ((int) (y + h)))) / ((float) MyShadyRenderer.this.viewMatrix[3])) - 1.0f;
            this.vertexPosBuffer.position(0);
            this.vertexPosBuffer.put(scrLX);
            this.vertexPosBuffer.put(scrBY);
            this.vertexPosBuffer.put(scrRX);
            this.vertexPosBuffer.put(scrBY);
            this.vertexPosBuffer.put(scrLX);
            this.vertexPosBuffer.put(scrTY);
            this.vertexPosBuffer.put(scrRX);
            this.vertexPosBuffer.put(scrTY);
            this.vertexPosBuffer.position(0);
            GLES20.glVertexAttribPointer(this.mPosLoc, 2, 5126, false, 8, (Buffer) this.vertexPosBuffer);
            GLES20.glEnableVertexAttribArray(this.mPosLoc);
            GLES20.glUniform1i(this.mSamplerLoc, 0);
            float texLX = crop[0];
            float texBY = crop[1];
            float texRX = crop[0] + crop[2];
            float texTY = crop[1] + crop[3];
            this.texCoordBuffer.position(0);
            switch (orientation) {
                case 0:
                    this.texCoordBuffer.put(texLX);
                    this.texCoordBuffer.put(texBY);
                    this.texCoordBuffer.put(texRX);
                    this.texCoordBuffer.put(texBY);
                    this.texCoordBuffer.put(texLX);
                    this.texCoordBuffer.put(texTY);
                    this.texCoordBuffer.put(texRX);
                    this.texCoordBuffer.put(texTY);
                    break;
                case 1:
                    this.texCoordBuffer.put(texRX);
                    this.texCoordBuffer.put(texBY);
                    this.texCoordBuffer.put(texRX);
                    this.texCoordBuffer.put(texTY);
                    this.texCoordBuffer.put(texLX);
                    this.texCoordBuffer.put(texBY);
                    this.texCoordBuffer.put(texLX);
                    this.texCoordBuffer.put(texTY);
                    break;
                case 3:
                    this.texCoordBuffer.put(texLX);
                    this.texCoordBuffer.put(texTY);
                    this.texCoordBuffer.put(texLX);
                    this.texCoordBuffer.put(texBY);
                    this.texCoordBuffer.put(texRX);
                    this.texCoordBuffer.put(texTY);
                    this.texCoordBuffer.put(texRX);
                    this.texCoordBuffer.put(texBY);
                    break;
            }
            this.texCoordBuffer.position(0);
            GLES20.glVertexAttribPointer(this.mTexCoordLoc, 2, 5126, false, 8, (Buffer) this.texCoordBuffer);
            GLES20.glEnableVertexAttribArray(this.mTexCoordLoc);
            GLES20.glDrawArrays(5, 0, this.vertexPosBuffer.capacity() / 2);
        }

        /* access modifiers changed from: package-private */
        public void setTheme(int themeOrdinal) {
            this.textColorOffset = Util.chooseColorOffset(this.textColorFilterArray.length, themeOrdinal);
        }
    }

    public final class MoonShader extends Shader {
        static final int TEX_COORD_BYTES = 8;
        private static final String fShaderStr = "precision mediump float; const float PI_BY2 = 1.5707963267948966;const float TWO_PI = 6.283185307179586;uniform float u_max_angle; uniform vec4 u_color; varying vec2 v_texcoords; void main() {  float x = v_texcoords[0];  float z = v_texcoords[1];  float y = sqrt(1.0 - x*x - z*z);  float angle = atan(x,-y) - PI_BY2;  if (angle < 0.0) angle += TWO_PI;  float color = angle < u_max_angle && angle > 0.0 ? 1.0 : 0.4;  gl_FragColor = u_color * vec4(color, color, color, 0.5);  }";
        private static final String vShaderStr = "uniform mat4 u_mvpMatrix;attribute vec3 a_position; attribute vec2 a_texcoords; varying vec2 v_texcoords; void main() {  gl_Position = u_mvpMatrix * vec4(a_position, 1.0);  v_texcoords = a_texcoords;}";
        final int mColorLoc;
        final int mMaxAngle;
        final int mMvpLoc;
        final int mPosLoc;
        final int mTexCoordLoc;
        private final float[] moonColorArray = {1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.5f, 0.5f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f};
        private final FloatBuffer texCoordBuffer = ByteBuffer.allocateDirect(272).order(ByteOrder.nativeOrder()).asFloatBuffer();

        MoonShader() {
            super(vShaderStr, fShaderStr);
            float texY;
            if (this.mProgramId == 0) {
                Log.e("SKEYE", "Error Loading Moon program");
                this.mColorLoc = -1;
                this.mMvpLoc = -1;
                this.mMaxAngle = -1;
                this.mPosLoc = -1;
                this.mTexCoordLoc = -1;
            } else {
                this.mPosLoc = GLES20.glGetAttribLocation(this.mProgramId, "a_position");
                this.mTexCoordLoc = GLES20.glGetAttribLocation(this.mProgramId, "a_texcoords");
                this.mMvpLoc = GLES20.glGetUniformLocation(this.mProgramId, "u_mvpMatrix");
                this.mMaxAngle = GLES20.glGetUniformLocation(this.mProgramId, "u_max_angle");
                this.mColorLoc = GLES20.glGetUniformLocation(this.mProgramId, "u_color");
            }
            this.texCoordBuffer.position(0);
            int i = -1;
            while (i < 33) {
                float texX = i < 0 ? 0.0f : (float) Math.cos((((double) i) * 6.283185307179586d) / 32.0d);
                if (i < 0) {
                    texY = 0.0f;
                } else {
                    texY = (float) Math.sin((((double) i) * 6.283185307179586d) / 32.0d);
                }
                this.texCoordBuffer.put(texX);
                this.texCoordBuffer.put(texY);
                i++;
            }
            this.texCoordBuffer.position(0);
        }

        public void draw(FloatBuffer vertexPosBuffer, float endAngle, int numPoints) {
            activate();
            GLES20.glUniformMatrix4fv(this.mMvpLoc, 1, false, MyShadyRenderer.this.mvpMatrix, 0);
            GLES20.glUniform4fv(this.mColorLoc, 1, this.moonColorArray, Util.chooseColorOffset(this.moonColorArray.length, MyShadyRenderer.this.themeOrdinal));
            GLES20.glVertexAttribPointer(this.mPosLoc, 3, 5126, false, 12, (Buffer) vertexPosBuffer);
            GLES20.glEnableVertexAttribArray(this.mPosLoc);
            GLES20.glUniform1f(this.mMaxAngle, endAngle);
            GLES20.glVertexAttribPointer(this.mTexCoordLoc, 2, 5126, false, 8, (Buffer) this.texCoordBuffer);
            GLES20.glEnableVertexAttribArray(this.mTexCoordLoc);
            GLES20.glBlendFunc(770, 771);
            GLES20.glDrawArrays(6, 0, numPoints);
        }
    }

    public final class PlainShader extends Shader {
        private static final String fShaderStr = "precision mediump float; uniform vec4 u_color; void main() {  gl_FragColor = u_color;  }";
        private static final String vShaderStr = "uniform mat4 u_mvpMatrix;attribute vec3 a_position; void main() {  gl_Position = u_mvpMatrix * vec4(a_position, 1.0);}";
        final int mColorLoc;
        final int mMvpLoc;
        final int mPosLoc;

        PlainShader() {
            super(vShaderStr, fShaderStr);
            if (this.mProgramId == 0) {
                Log.e("SKEYE", "Error Loading PlainShader");
                this.mColorLoc = -1;
                this.mMvpLoc = -1;
                this.mPosLoc = -1;
                return;
            }
            this.mPosLoc = GLES20.glGetAttribLocation(this.mProgramId, "a_position");
            this.mMvpLoc = GLES20.glGetUniformLocation(this.mProgramId, "u_mvpMatrix");
            this.mColorLoc = GLES20.glGetUniformLocation(this.mProgramId, "u_color");
        }

        public void draw(FloatBuffer vertexPosBuffer, int numPoints, float[] a_colorArray, int a_colorOffset) {
            activate();
            GLES20.glUniformMatrix4fv(this.mMvpLoc, 1, false, MyShadyRenderer.this.mvpMatrix, 0);
            GLES20.glUniform4fv(this.mColorLoc, 1, a_colorArray, a_colorOffset);
            GLES20.glVertexAttribPointer(this.mPosLoc, 3, 5126, false, 12, (Buffer) vertexPosBuffer);
            GLES20.glEnableVertexAttribArray(this.mPosLoc);
            GLES20.glBlendFunc(770, 771);
            GLES20.glDrawArrays(6, 0, numPoints);
        }
    }

    public final class DSOShader extends Shader {
        static final int POSITION_SIZE_BYTES = 12;
        static final int POSITION_SIZE_FLOATS = 3;
        public static final int TEX_COORD_BYTES = 8;
        private static final String fShaderStr = "precision mediump float; uniform sampler2D s_texture; uniform vec4 u_color; varying vec2 v_texcoords; void main() {  gl_FragColor = u_color * texture2D( s_texture, v_texcoords);  }";
        private static final String vShaderStr = "uniform mat4 u_mvpMatrix;attribute vec3 a_position; attribute vec2 a_texcoords; varying vec2 v_texcoords; void main() {  gl_Position = u_mvpMatrix * vec4(a_position, 1.0);  v_texcoords = a_texcoords;}";
        private final float[] dsoColorArray = {1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.5f, 0.5f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f};
        final int mColorLoc;
        final int mMvpLoc;
        final int mPosLoc;
        final int mTexCoordLoc;

        DSOShader() {
            super(vShaderStr, fShaderStr);
            if (this.mProgramId == 0) {
                Log.e("SKEYE", "Error Loading DSO shader");
                this.mColorLoc = -1;
                this.mMvpLoc = -1;
                this.mPosLoc = -1;
                this.mTexCoordLoc = -1;
                return;
            }
            this.mPosLoc = GLES20.glGetAttribLocation(this.mProgramId, "a_position");
            this.mTexCoordLoc = GLES20.glGetAttribLocation(this.mProgramId, "a_texcoords");
            this.mMvpLoc = GLES20.glGetUniformLocation(this.mProgramId, "u_mvpMatrix");
            this.mColorLoc = GLES20.glGetUniformLocation(this.mProgramId, "u_color");
        }

        public void draw(FloatBuffer vertexPosBuffer, Buffer quadIndexArray, Buffer texCoordBuffer, int a_themeOrdinal) {
            activate();
            GLES20.glBindTexture(3553, MyShadyRenderer.mDSOTexId);
            GLES20.glUniform4fv(this.mColorLoc, 1, this.dsoColorArray, Util.chooseColorOffset(this.dsoColorArray.length, a_themeOrdinal));
            GLES20.glUniformMatrix4fv(this.mMvpLoc, 1, false, MyShadyRenderer.this.mvpMatrix, 0);
            GLES20.glVertexAttribPointer(this.mPosLoc, 3, 5126, false, 12, (Buffer) vertexPosBuffer);
            GLES20.glEnableVertexAttribArray(this.mPosLoc);
            GLES20.glVertexAttribPointer(this.mTexCoordLoc, 2, 5126, false, 8, texCoordBuffer);
            GLES20.glEnableVertexAttribArray(this.mTexCoordLoc);
            GLES20.glBlendFunc(770, 771);
            GLES20.glDrawElements(4, quadIndexArray.capacity(), 5123, quadIndexArray);
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.lavadip.skeye.MyRenderer
    public void updateCenteredObj(int objId) {
        int catalogId = CatalogManager.getCatalog(objId);
        int objNum = CatalogManager.getObjNum(objId);
        Catalog catalog = CatalogManager.catalogs[catalogId];
        float[] points = catalog.getVecPositions();
        int pointOffset = objNum * 3;
        Matrix.multiplyMV(this.winCoords, 0, this.mvpMatrix, 0, new float[]{points[pointOffset], points[pointOffset + 1], points[pointOffset + 2], 1.0f}, 0);
        if (isVisible(this.winCoords)) {
            projectToScreen(this.winCoords);
            if (catalog.isObjVisible(objNum, fov)) {
                this.centeredObj.update(objId, points, pointOffset);
            }
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.lavadip.skeye.MyRenderer
    public void updateCenterBuffer() {
        if (this.buffInitialised) {
            this.centerBuffer.position(0);
            Vector3d coords = this.centeredObj.getCenterCoords();
            this.centerBuffer.put((float) (coords.f16x * 9.0d));
            this.centerBuffer.put((float) (coords.f17y * 9.0d));
            this.centerBuffer.put((float) (coords.f18z * 9.0d));
            this.centerBuffer.position(0);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void setHorizonAlpha(float alpha) {
        int stride = this.colorArray.length / 3;
        for (int i = 0; i < this.colorArray.length; i += stride) {
            this.colorArray[i + 0 + 3] = alpha;
        }
    }

    private static float getCurrHorizonLineAlpha(SkEye skeye) {
        return skeye.settingsManager.getQuickPref(horizonLineAlphaKey, 0.5f);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void setDirectionLabelAlpha(float alpha) {
        this.directionLabelAlpha = alpha;
    }

    private static float getCurrDirectionLabelAlpha(SkEye skeye) {
        return skeye.settingsManager.getQuickPref(directionLabelAlphaKey, 0.5f);
    }

    private static float getCurrAltAzmAlpha(SkEye skeye) {
        return skeye.settingsManager.getQuickPref(altAzmAlphaKey, 0.0f);
    }

    private static float getCurrHighlightAlpha(SkEye skeye) {
        return skeye.settingsManager.getQuickPref(highlightAlphaKey, 1.0f);
    }

    /* access modifiers changed from: protected */
    public void setAltAzmAlpha(float alpha) {
        int stride = this.colorArray.length / 3;
        for (int i = 0; i < this.colorArray.length; i += stride) {
            this.colorArray[i + 8 + 3] = alpha;
        }
    }

    /* access modifiers changed from: protected */
    public void setHighlightAlpha(float alpha) {
        int stride = this.colorArray.length / 3;
        for (int i = 0; i < this.colorArray.length; i += stride) {
            this.colorArray[i + 16 + 3] = alpha;
        }
    }

    public QuickSettingsManager.QuickSettingsGroup mkQuickSettingGroup(SkEye skeye) {
        return new QuickSettingsManager.QuickSettingsGroup(new QuickSettingsManager.SettingDetails[]{new QuickSettingsManager.SettingDetails(new QuickSettingsManager.FloatRangeQuickSetting(horizonLineAlphaKey, skeye.getString(C0031R.string.horizon_line_opacity), "", 0.0f, 1.0f, 0.02f), new QuickSettingsManager.TypicalSettingChangeHandler<Float>(skeye) {
            /* class com.lavadip.skeye.MyShadyRenderer.C00161 */

            @Override // com.lavadip.skeye.QuickSettingsManager.SettingChangeListener
            public /* bridge */ /* synthetic */ void onChange(String str, Object obj, boolean z) {
                onChange(str, (Float) obj, z);
            }

            @Override // com.lavadip.skeye.QuickSettingsManager.TypicalSettingChangeHandler
            public void onGLThread(String key, Float newValue, boolean trackingStopped) {
                MyShadyRenderer.this.setHorizonAlpha(newValue.floatValue());
            }
        }, Float.valueOf(getCurrHorizonLineAlpha(skeye))), new QuickSettingsManager.SettingDetails(new QuickSettingsManager.FloatRangeQuickSetting(directionLabelAlphaKey, skeye.getString(C0031R.string.direction_label_opacity), "", 0.0f, 1.0f, 0.02f), new QuickSettingsManager.TypicalSettingChangeHandler<Float>(skeye) {
            /* class com.lavadip.skeye.MyShadyRenderer.C00172 */

            @Override // com.lavadip.skeye.QuickSettingsManager.SettingChangeListener
            public /* bridge */ /* synthetic */ void onChange(String str, Object obj, boolean z) {
                onChange(str, (Float) obj, z);
            }

            @Override // com.lavadip.skeye.QuickSettingsManager.TypicalSettingChangeHandler
            public void onGLThread(String key, Float newValue, boolean trackingStopped) {
                MyShadyRenderer.this.setDirectionLabelAlpha(newValue.floatValue());
            }
        }, Float.valueOf(getCurrDirectionLabelAlpha(skeye))), new QuickSettingsManager.SettingDetails(new QuickSettingsManager.FloatRangeQuickSetting(altAzmAlphaKey, skeye.getString(C0031R.string.alt_azm_grid_opacity), "", 0.0f, 1.0f, 0.02f), new QuickSettingsManager.TypicalSettingChangeHandler<Float>(skeye) {
            /* class com.lavadip.skeye.MyShadyRenderer.C00183 */

            @Override // com.lavadip.skeye.QuickSettingsManager.SettingChangeListener
            public /* bridge */ /* synthetic */ void onChange(String str, Object obj, boolean z) {
                onChange(str, (Float) obj, z);
            }

            @Override // com.lavadip.skeye.QuickSettingsManager.TypicalSettingChangeHandler
            public void onGLThread(String key, Float newValue, boolean trackingStopped) {
                MyShadyRenderer.this.setAltAzmAlpha(newValue.floatValue());
            }
        }, Float.valueOf(getCurrAltAzmAlpha(skeye))), new QuickSettingsManager.SettingDetails(new QuickSettingsManager.FloatRangeQuickSetting(highlightAlphaKey, skeye.getString(C0031R.string.highlight_opacity), "", 0.2f, 1.0f, 0.02f), new QuickSettingsManager.TypicalSettingChangeHandler<Float>(skeye) {
            /* class com.lavadip.skeye.MyShadyRenderer.C00194 */

            @Override // com.lavadip.skeye.QuickSettingsManager.SettingChangeListener
            public /* bridge */ /* synthetic */ void onChange(String str, Object obj, boolean z) {
                onChange(str, (Float) obj, z);
            }

            @Override // com.lavadip.skeye.QuickSettingsManager.TypicalSettingChangeHandler
            public void onGLThread(String key, Float newValue, boolean trackingStopped) {
                MyShadyRenderer.this.setHighlightAlpha(newValue.floatValue());
            }
        }, Float.valueOf(getCurrHighlightAlpha(skeye)))}, skeye.getString(C0031R.string.general), "general");
    }
}
