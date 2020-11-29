package com.lavadip.skeye.shader;

import android.annotation.TargetApi;
import android.opengl.GLES20;
import android.util.Log;
import java.nio.Buffer;

@TargetApi(8)
public final class PointShader extends Shader {
    static final int POSITION_SIZE_BYTES = 12;
    static final int POSITION_SIZE_FLOATS = 3;
    private static final String fShaderStr = "precision mediump float;                             vec2 origin = vec2(0.5, 0.5);  uniform sampler2D s_texture; uniform mat2 u_rotMatrix;uniform vec4 u_color; void main() {  vec2 coords = u_rotMatrix * (gl_PointCoord - origin);    float tColor = texture2D( s_texture, ((coords*0.7071) + origin)).r;    gl_FragColor = u_color * tColor; }";
    private static final String vShaderStr = "uniform mat4 u_mvpMatrix;uniform float u_scale;attribute vec3 a_position; void main() {  vec4 position4 = vec4(a_position, 1);  gl_Position = u_mvpMatrix * position4;  gl_PointSize = u_scale;}";
    private final float[] dummyRotMatrix = {1.0f, 0.0f, 0.0f, 1.0f};
    final int mColorLoc;
    final int mMvpLoc;
    final int mPosLoc;
    final int mRotLoc;
    final int mSamplerLoc;
    final int mScaleLoc;

    public PointShader() {
        super(vShaderStr, fShaderStr);
        if (this.mProgramId == 0) {
            Log.e("SKEYE", "Error Loading PointShader");
            this.mRotLoc = -1;
            this.mColorLoc = -1;
            this.mSamplerLoc = -1;
            this.mPosLoc = -1;
            this.mScaleLoc = -1;
            this.mMvpLoc = -1;
            return;
        }
        this.mRotLoc = GLES20.glGetUniformLocation(this.mProgramId, "u_rotMatrix");
        this.mMvpLoc = GLES20.glGetUniformLocation(this.mProgramId, "u_mvpMatrix");
        this.mScaleLoc = GLES20.glGetUniformLocation(this.mProgramId, "u_scale");
        this.mColorLoc = GLES20.glGetUniformLocation(this.mProgramId, "u_color");
        this.mPosLoc = GLES20.glGetAttribLocation(this.mProgramId, "a_position");
        this.mSamplerLoc = GLES20.glGetUniformLocation(this.mProgramId, "s_texture");
    }

    public void beginDrawing(int textureId) {
        activate();
        GLES20.glBindTexture(3553, textureId);
    }

    public static void makeRotMatrix(double theta, float[] matrix2D) {
        float cos = (float) Math.cos(theta);
        matrix2D[3] = cos;
        matrix2D[0] = cos;
        float sinTheta = (float) Math.sin(-theta);
        matrix2D[1] = -sinTheta;
        matrix2D[2] = sinTheta;
    }

    public void draw(float[] mvpMatrix, float[] colorArray, int colorOffset, float size, Buffer vertexBuffer, int count) {
        draw(mvpMatrix, colorArray, colorOffset, size, vertexBuffer, count, this.dummyRotMatrix);
    }

    public void setupMVP(float[] mvpMatrix) {
        GLES20.glUniformMatrix2fv(this.mRotLoc, 1, false, this.dummyRotMatrix, 0);
        GLES20.glUniformMatrix4fv(this.mMvpLoc, 1, false, mvpMatrix, 0);
    }

    public void setupColors(float[] colorArray, int colorOffset) {
        GLES20.glUniform4fv(this.mColorLoc, 1, colorArray, colorOffset);
    }

    public void setupSize(float size) {
        GLES20.glUniform1f(this.mScaleLoc, size);
    }

    public void setupVertexBuffer(Buffer vertexBuffer) {
        GLES20.glVertexAttribPointer(this.mPosLoc, 3, 5126, false, 12, vertexBuffer);
        GLES20.glEnableVertexAttribArray(this.mPosLoc);
    }

    public void draw(int start, int count) {
        GLES20.glUniform1i(this.mSamplerLoc, 0);
        GLES20.glDrawArrays(0, start, count);
    }

    public void draw(float[] mvpMatrix, float[] colorArray, int colorOffset, float size, Buffer vertexBuffer, int count, float[] rotMatrix) {
        GLES20.glUniformMatrix2fv(this.mRotLoc, 1, false, rotMatrix, 0);
        GLES20.glUniformMatrix4fv(this.mMvpLoc, 1, false, mvpMatrix, 0);
        GLES20.glUniform1f(this.mScaleLoc, size);
        GLES20.glUniform4fv(this.mColorLoc, 1, colorArray, colorOffset);
        GLES20.glVertexAttribPointer(this.mPosLoc, 3, 5126, false, 12, vertexBuffer);
        GLES20.glEnableVertexAttribArray(this.mPosLoc);
        GLES20.glUniform1i(this.mSamplerLoc, 0);
        GLES20.glDrawArrays(0, 0, count);
    }
}
