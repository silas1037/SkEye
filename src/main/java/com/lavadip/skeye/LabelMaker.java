package com.lavadip.skeye;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.opengl.GLES20;
import com.lavadip.skeye.MyShadyRenderer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import javax.microedition.khronos.opengles.GL10;

public final class LabelMaker {
    private static final int DYNAMIC_HEIGHT = 128;
    private static final int DYNAMIC_WIDTH = 512;
    private static final int NUM_TEXTURES = 2;
    private static final int STATE_ADDING = 2;
    private static final int STATE_DRAWING = 3;
    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_NEW = 0;
    private static final int TEXTURE_DYNAMIC = 1;
    private static final int TEXTURE_STATIC = 0;
    int actualHeight = 0;
    int actualHeightCiel = 0;
    private final IntBuffer bufferDynamic = IntBuffer.wrap(this.pixelsDynamic);
    private final float[] cropData = new float[4];
    private final int internalFormat = 6408;
    private Bitmap mBitmap;
    private Bitmap mBitmapDynamic;
    private Canvas mCanvas;
    private Canvas mCanvasDynamic;
    private final boolean mFullColor;
    private final ArrayList<Label> mLabels = new ArrayList<>();
    private int mLineHeight;
    private int mOrientation;
    private int mStrikeHeight;
    private int mStrikeWidth;
    private final int[] mTextureIDs = new int[2];

    /* renamed from: mU */
    private int f2mU;

    /* renamed from: mV */
    private int f3mV;
    private int[] pixels;
    private final int[] pixelsDynamic = new int[65536];

    LabelMaker(boolean fullColor) {
        this.mFullColor = fullColor;
        this.mOrientation = 0;
    }

    /* access modifiers changed from: package-private */
    @TargetApi(8)
    public void initializeES20() {
        GLES20.glGenTextures(2, this.mTextureIDs, 0);
        for (int i = 0; i < 2; i++) {
            GLES20.glBindTexture(3553, this.mTextureIDs[i]);
            GLES20.glTexParameteri(3553, 10241, 9728);
            GLES20.glTexParameteri(3553, 10240, 9728);
            GLES20.glTexParameteri(3553, 10242, 33071);
            GLES20.glTexParameteri(3553, 10243, 33071);
        }
        GLES20.glEnable(3042);
    }

    /* access modifiers changed from: package-private */
    public void shutdown(GL10 gl) {
        if (gl != null && this.mTextureIDs != null) {
            gl.glDeleteTextures(2, this.mTextureIDs, 0);
        }
    }

    /* access modifiers changed from: package-private */
    public void beginAdding(Point textureDimensions) {
        checkState(1, 2);
        this.mStrikeWidth = textureDimensions.x;
        this.mStrikeHeight = textureDimensions.y;
        this.mLabels.clear();
        this.f2mU = 0;
        this.f3mV = 0;
        this.mLineHeight = 0;
        Bitmap.Config config = this.mFullColor ? Bitmap.Config.ARGB_4444 : Bitmap.Config.ALPHA_8;
        this.mBitmap = Bitmap.createBitmap(this.mStrikeWidth, this.mStrikeHeight, config);
        this.mBitmapDynamic = Bitmap.createBitmap(DYNAMIC_WIDTH, DYNAMIC_HEIGHT, config);
        this.mCanvas = new Canvas(this.mBitmap);
        this.mCanvasDynamic = new Canvas(this.mBitmapDynamic);
        this.mBitmap.eraseColor(0);
    }

    public int add(String text, Paint textPaint) {
        return add((Drawable) null, text, textPaint);
    }

    /* access modifiers changed from: package-private */
    public int add(Drawable background, String text, Paint textPaint) {
        return add(background, text, textPaint, 0, 0);
    }

    /* access modifiers changed from: package-private */
    public int add(Drawable drawable, int minWidth, int minHeight) {
        return add(drawable, null, null, minWidth, minHeight);
    }

    /* access modifiers changed from: package-private */
    public int add(Drawable background, String text, Paint textPaint, int minWidth, int minHeight) {
        checkState(2, 2);
        boolean drawBackground = background != null;
        boolean drawText = (text == null || textPaint == null) ? false : true;
        Rect padding = new Rect();
        if (drawBackground) {
            background.getPadding(padding);
            minWidth = Math.max(minWidth, background.getMinimumWidth());
            minHeight = Math.max(minHeight, background.getMinimumHeight());
        }
        int ascent = (int) Math.ceil((double) (-textPaint.ascent()));
        int textHeight = ascent + ((int) Math.ceil((double) textPaint.descent()));
        int textWidth = Math.min(this.mStrikeWidth, (int) Math.ceil((double) textPaint.measureText(text)));
        int padHeight = padding.top + padding.bottom;
        int padWidth = padding.left + padding.right;
        int height = Math.max(minHeight, textHeight + padHeight);
        int width = Math.max(minWidth, textWidth + padWidth);
        int centerOffsetHeight = ((height - padHeight) - textHeight) / 2;
        int centerOffsetWidth = ((width - padWidth) - textWidth) / 2;
        int u = this.f2mU;
        int v = this.f3mV;
        int lineHeight = this.mLineHeight;
        if (width > this.mStrikeWidth) {
            width = this.mStrikeWidth;
        }
        if (u + width > this.mStrikeWidth) {
            u = 0;
            v += lineHeight;
            lineHeight = 0;
        }
        int lineHeight2 = Math.max(lineHeight, height);
        if (v + lineHeight2 > this.mStrikeHeight) {
            throw new IllegalArgumentException(String.format("Out of texture space. Allocated %d x %d. Labels so far: %d", Integer.valueOf(this.mStrikeWidth), Integer.valueOf(this.mStrikeHeight), Integer.valueOf(this.mLabels.size())));
        }
        if (drawBackground) {
            background.setBounds(u, v, u + width, v + height);
            background.draw(this.mCanvas);
        }
        if (drawText) {
            drawOutlinedText(this.mCanvas, text, textPaint, width, height, u, v, (float) (padding.left + u + centerOffsetWidth), (float) (padding.top + v + ascent + centerOffsetHeight));
        }
        this.f2mU = u + width;
        this.f3mV = v;
        this.mLineHeight = lineHeight2;
        this.mLabels.add(new Label((float) width, (float) height, (float) ascent, new float[]{(float) u, (float) (v + height), (float) width, (float) (-height)}));
        return this.mLabels.size() - 1;
    }

    private static void drawOutlinedText(Canvas canvas, String text, Paint textPaint, int width, int height, int u, int v, float leftStart, float topStart) {
        canvas.clipRect((float) u, (float) v, (float) (u + width), (float) (v + height), Region.Op.REPLACE);
        int origColor = textPaint.getColor();
        boolean origUnderline = textPaint.isUnderlineText();
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setColor(-1728053248);
        textPaint.setUnderlineText(false);
        canvas.drawText(text, leftStart, topStart, textPaint);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(origColor);
        textPaint.setUnderlineText(origUnderline);
        canvas.drawText(text, leftStart, topStart, textPaint);
    }

    public void drawDynamic(MyShadyRenderer r, float x, float y, String text, Paint textPaint, boolean centerLabelHoriz, boolean centerLabelVert, float labelOffset) {
        int ascent = (int) Math.ceil((double) (-textPaint.ascent()));
        int textHeight = Math.min((int) DYNAMIC_HEIGHT, ascent + ((int) Math.ceil((double) textPaint.descent())));
        int textWidth = Math.min((int) DYNAMIC_WIDTH, (int) Math.ceil((double) textPaint.measureText(text)));
        this.mBitmapDynamic.eraseColor(0);
        drawOutlinedText(this.mCanvasDynamic, text, textPaint, textWidth, textHeight, 0, 0, 0.0f, (float) ascent);
        this.mBitmapDynamic.getPixels(this.pixelsDynamic, 0, textWidth, 0, 0, textWidth, textHeight);
        swapRB(this.pixelsDynamic, textWidth * textHeight);
        GLES20.glBindTexture(3553, this.mTextureIDs[1]);
        GLES20.glTexSubImage2D(3553, 0, 0, 0, textWidth, textHeight, 6408, 5121, this.bufferDynamic);
        GLES20.glBlendFunc(770, 771);
        float cropHeight = ((float) textHeight) / 128.0f;
        this.cropData[1] = cropHeight;
        this.cropData[2] = ((float) textWidth) / 512.0f;
        this.cropData[3] = -cropHeight;
        drawConsideringRotations(r, this.cropData, x, y, (float) textWidth, (float) textHeight, centerLabelHoriz, centerLabelVert, labelOffset, 1.0f);
        GLES20.glBindTexture(3553, this.mTextureIDs[0]);
    }

    /* access modifiers changed from: package-private */
    public void endAdding() {
        this.actualHeight = Math.min(this.f3mV + 1 + this.mLineHeight, this.mStrikeHeight);
        this.actualHeightCiel = cielPower2(this.actualHeight);
        Iterator<Label> it = this.mLabels.iterator();
        while (it.hasNext()) {
            Label label = it.next();
            for (int i = 0; i < label.mCrop.length; i++) {
                float[] fArr = label.mCrop;
                fArr[i] = fArr[i] / ((float) (i % 2 == 0 ? this.mStrikeWidth : this.actualHeightCiel));
            }
        }
        this.pixels = new int[(this.actualHeightCiel * this.mStrikeWidth)];
        this.mBitmap.getPixels(this.pixels, 0, this.mStrikeWidth, 0, 0, this.mStrikeWidth, this.actualHeight);
        swapRB(this.pixels, this.mStrikeWidth * this.actualHeight);
        this.mBitmap.recycle();
        this.mBitmap = null;
        this.mCanvas = null;
    }

    private static void swapRB(int[] pixelArray, int len) {
        for (int i = 0; i < len; i++) {
            int value = pixelArray[i];
            pixelArray[i] = ((value << 16) & 16711680) | ((value >>> 16) & 255) | (-16711936 & value);
        }
    }

    static int filterGB(int color) {
        return (color & -16777216) | (color & 255) | (((16711680 & color) >> 17) << 16) | (((65280 & color) >> 9) << 8);
    }

    static void filterGB(int[] colorsSrc, int[] colorsDst) {
        int N = colorsSrc.length;
        for (int i = 0; i < N; i++) {
            colorsDst[i] = filterGB(colorsSrc[i]);
        }
    }

    /* access modifiers changed from: package-private */
    @TargetApi(8)
    public void createTexturesES20() {
        checkState(2, 1);
        GLES20.glBindTexture(3553, this.mTextureIDs[1]);
        GLES20.glTexImage2D(3553, 0, 6408, DYNAMIC_WIDTH, DYNAMIC_HEIGHT, 0, 6408, 5121, this.bufferDynamic);
        GLES20.glBindTexture(3553, this.mTextureIDs[0]);
        GLES20.glTexImage2D(3553, 0, 6408, this.mStrikeWidth, this.actualHeightCiel, 0, 6408, 5121, IntBuffer.wrap(this.pixels));
    }

    private static int cielPower2(int x) {
        int result = 1;
        while (result < x) {
            result <<= 1;
        }
        return result;
    }

    /* access modifiers changed from: package-private */
    public float getWidth(int labelID) {
        return this.mLabels.get(labelID).width;
    }

    /* access modifiers changed from: package-private */
    public float getHeight(int labelID) {
        return this.mLabels.get(labelID).height;
    }

    /* access modifiers changed from: package-private */
    public float getBaseline(int labelID) {
        return this.mLabels.get(labelID).baseline;
    }

    /* access modifiers changed from: package-private */
    @TargetApi(8)
    public void beginDrawingES20(MyShadyRenderer r) {
        checkState(1, 3);
        GLES20.glBindTexture(3553, this.mTextureIDs[0]);
        GLES20.glBlendFunc(770, 771);
        r.mTextShader.beginDrawing();
    }

    @TargetApi(8)
    public void drawES20(MyShadyRenderer r, float x, float y, int labelID, boolean centerLabelHoriz, boolean centerLabelVert, float labelOffset) {
        drawES20(r, x, y, labelID, centerLabelHoriz, centerLabelVert, labelOffset, 1.0f);
    }

    @TargetApi(8)
    public void drawES20(MyShadyRenderer r, float x, float y, int labelID, boolean centerLabelHoriz, boolean centerLabelVert, float labelOffset, float alpha) {
        checkState(3, 3);
        Label label = this.mLabels.get(labelID);
        drawConsideringRotations(r, label.mCrop, x, y, label.width, label.height, centerLabelHoriz, centerLabelVert, labelOffset, alpha);
    }

    private void drawConsideringRotations(MyShadyRenderer r, float[] crop, float x, float y, float width, float height, boolean centerLabelHoriz, boolean centerLabelVert, float labelOffset, float alpha) {
        float f;
        switch (this.mOrientation) {
            case 0:
                r.mTextShader.draw(crop, this.mOrientation, x - (centerLabelHoriz ? width / 2.0f : 0.0f), (labelOffset + y) - (centerLabelVert ? height / 2.0f : 0.0f), width, height, alpha);
                return;
            case 1:
                MyShadyRenderer.TextShader textShader = r.mTextShader;
                int i = this.mOrientation;
                float f2 = (labelOffset + x) - (centerLabelVert ? height / 2.0f : 0.0f);
                if (centerLabelHoriz) {
                    f = width / 2.0f;
                } else {
                    f = width;
                }
                textShader.draw(crop, i, f2, y - f, height, width, alpha);
                return;
            case 2:
            default:
                return;
            case 3:
                r.mTextShader.draw(crop, this.mOrientation, (((-labelOffset) + x) - height) + (centerLabelVert ? height / 2.0f : 0.0f), y - (centerLabelHoriz ? width / 2.0f : 0.0f), height, width, alpha);
                return;
        }
    }

    /* access modifiers changed from: package-private */
    @TargetApi(8)
    public void endDrawingES20() {
        checkState(3, 1);
    }

    private void checkState(int oldState, int newState) {
    }

    /* access modifiers changed from: private */
    public static class Label {
        final float baseline;
        final float height;
        final float[] mCrop;
        final float width;

        Label(float width2, float height2, float baseLine, float[] cropData) {
            this.width = width2;
            this.height = height2;
            this.baseline = baseLine;
            this.mCrop = cropData;
        }
    }

    /* access modifiers changed from: package-private */
    public void setOrientation(int rotation) {
        if (rotation == 2) {
            this.mOrientation = 0;
        } else {
            this.mOrientation = rotation;
        }
    }

    /* access modifiers changed from: package-private */
    public int getOrientation() {
        return this.mOrientation;
    }

    /* access modifiers changed from: package-private */
    public void onDestroy() {
        this.pixels = null;
    }
}
