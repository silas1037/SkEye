package com.lavadip.skeye;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.FloatBuffer;
import javax.microedition.khronos.opengles.GL10;

final class Grid {

    /* renamed from: mH */
    private final int f0mH;
    private final CharBuffer mIndexBuffer;
    private final int mIndexCount;
    private final FloatBuffer mTexCoordBuffer;
    private final FloatBuffer mVertexBuffer;

    /* renamed from: mW */
    private final int f1mW;

    public Grid(int w, int h) {
        if (w < 0 || w >= 65536) {
            throw new IllegalArgumentException("w");
        } else if (h < 0 || h >= 65536) {
            throw new IllegalArgumentException("h");
        } else if (w * h >= 65536) {
            throw new IllegalArgumentException("w * h >= 65536");
        } else {
            this.f1mW = w;
            this.f0mH = h;
            int size = w * h;
            this.mVertexBuffer = ByteBuffer.allocateDirect(size * 4 * 3).order(ByteOrder.nativeOrder()).asFloatBuffer();
            this.mTexCoordBuffer = ByteBuffer.allocateDirect(size * 4 * 2).order(ByteOrder.nativeOrder()).asFloatBuffer();
            int quadW = this.f1mW - 1;
            int quadH = this.f0mH - 1;
            int indexCount = quadW * quadH * 6;
            this.mIndexCount = indexCount;
            this.mIndexBuffer = ByteBuffer.allocateDirect(indexCount * 2).order(ByteOrder.nativeOrder()).asCharBuffer();
            int i = 0;
            int y = 0;
            while (y < quadH) {
                int i2 = i;
                for (int x = 0; x < quadW; x++) {
                    char a = (char) ((this.f1mW * y) + x);
                    char b = (char) ((this.f1mW * y) + x + 1);
                    char c = (char) (((y + 1) * this.f1mW) + x);
                    int i3 = i2 + 1;
                    this.mIndexBuffer.put(i2, a);
                    int i4 = i3 + 1;
                    this.mIndexBuffer.put(i3, b);
                    int i5 = i4 + 1;
                    this.mIndexBuffer.put(i4, c);
                    int i6 = i5 + 1;
                    this.mIndexBuffer.put(i5, b);
                    int i7 = i6 + 1;
                    this.mIndexBuffer.put(i6, c);
                    i2 = i7 + 1;
                    this.mIndexBuffer.put(i7, (char) (((y + 1) * this.f1mW) + x + 1));
                }
                y++;
                i = i2;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void set(int i, int j, float x, float y, float z, float u, float v) {
        if (i < 0 || i >= this.f1mW) {
            throw new IllegalArgumentException("i");
        } else if (j < 0 || j >= this.f0mH) {
            throw new IllegalArgumentException("j");
        } else {
            int index = (this.f1mW * j) + i;
            int posIndex = index * 3;
            this.mVertexBuffer.put(posIndex, x);
            this.mVertexBuffer.put(posIndex + 1, y);
            this.mVertexBuffer.put(posIndex + 2, z);
            int texIndex = index * 2;
            this.mTexCoordBuffer.put(texIndex, u);
            this.mTexCoordBuffer.put(texIndex + 1, v);
        }
    }

    public void draw(GL10 gl, boolean useTexture) {
        gl.glEnableClientState(32884);
        gl.glVertexPointer(3, 5126, 0, this.mVertexBuffer);
        if (useTexture) {
            gl.glEnableClientState(32888);
            gl.glTexCoordPointer(2, 5126, 0, this.mTexCoordBuffer);
            gl.glEnable(3553);
        } else {
            gl.glDisableClientState(32888);
            gl.glDisable(3553);
        }
        gl.glDrawElements(4, this.mIndexCount, 5123, this.mIndexBuffer);
        gl.glDisableClientState(32884);
    }
}
