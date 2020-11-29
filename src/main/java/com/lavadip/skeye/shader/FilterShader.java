package com.lavadip.skeye.shader;

import android.annotation.TargetApi;
import android.opengl.GLES20;
import android.util.Log;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

@TargetApi(8)
final class FilterShader extends Shader {
    private static final int POSITION_SIZE_BYTES = 8;
    private static final int POSITION_SIZE_FLOATS = 2;
    private static final String fShaderStr = "precision mediump float; uniform vec4 u_color; void main() {  gl_FragColor = u_color;  }";
    private static final String vShaderStr = "attribute vec2 a_position; void main() {  gl_Position.xy = a_position;  gl_Position.z = 0.0;  gl_Position.w = 1.0;}";
    private final ByteBuffer buffFullScreen = ByteBuffer.allocateDirect(32);
    private final float[] colorArray = {0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f};
    private final FloatBuffer fullScreenBuff = this.buffFullScreen.order(ByteOrder.nativeOrder()).asFloatBuffer();
    final int mColorLoc;

    FilterShader() {
        super(vShaderStr, fShaderStr);
        if (this.mProgramId == 0) {
            Log.e("SKEYE", "Error Loading FilterShader");
            this.mColorLoc = -1;
            return;
        }
        this.mColorLoc = GLES20.glGetUniformLocation(this.mProgramId, "u_color");
        this.fullScreenBuff.position(0);
        this.fullScreenBuff.put(-1.0f);
        this.fullScreenBuff.put(1.0f);
        this.fullScreenBuff.put(1.0f);
        this.fullScreenBuff.put(1.0f);
        this.fullScreenBuff.put(-1.0f);
        this.fullScreenBuff.put(-1.0f);
        this.fullScreenBuff.put(1.0f);
        this.fullScreenBuff.put(-1.0f);
        this.fullScreenBuff.position(0);
    }

    /* access modifiers changed from: package-private */
    public void draw() {
        activate();
        GLES20.glUniform4fv(this.mColorLoc, 1, this.colorArray, 0);
        GLES20.glVertexAttribPointer(0, 2, 5126, false, 8, (Buffer) this.fullScreenBuff);
        GLES20.glEnableVertexAttribArray(0);
        GLES20.glBlendFunc(769, 768);
        GLES20.glDrawArrays(5, 0, 4);
    }
}
