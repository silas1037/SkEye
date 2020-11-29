package com.lavadip.skeye.shader;

import android.annotation.TargetApi;
import android.opengl.GLES20;
import java.nio.Buffer;

@TargetApi(8)
public final class VectorLineShader extends Shader {
    private static final int POSITION_SIZE_BYTES = 12;
    private static final int POSITION_SIZE_FLOATS = 3;
    private static final String fLineShaderStr = "precision mediump float;uniform vec4 u_color;void main() {   gl_FragColor = u_color;}";
    private static final String vLineShaderStr = "uniform mat4 u_mvpMatrix;attribute vec3 a_position;void main(){  vec4 position4 = vec4(a_position, 1);  gl_Position = u_mvpMatrix * position4;}";
    private final int mLineColorLoc = GLES20.glGetUniformLocation(this.mProgramId, "u_color");
    private final int mLineMvpLoc = GLES20.glGetUniformLocation(this.mProgramId, "u_mvpMatrix");
    private final int mLinePosLoc = GLES20.glGetAttribLocation(this.mProgramId, "a_position");

    public VectorLineShader() {
        super(vLineShaderStr, fLineShaderStr);
    }

    public void setupLine(float lineWidth) {
        GLES20.glLineWidth(lineWidth);
    }

    public void setupMvp(float[] mvpMatrix) {
        GLES20.glUniformMatrix4fv(this.mLineMvpLoc, 1, false, mvpMatrix, 0);
    }

    public void setupColors(float[] colorArray, int colorOffset) {
        GLES20.glUniform4fv(this.mLineColorLoc, 1, colorArray, colorOffset);
    }

    public void setupVertexBuffer(Buffer vertexBuffer) {
        GLES20.glVertexAttribPointer(this.mLinePosLoc, 3, 5126, false, 12, vertexBuffer);
        GLES20.glEnableVertexAttribArray(this.mLinePosLoc);
    }

    public static void draw(int lineType, int start, int count) {
        GLES20.glDrawArrays(lineType, start, count);
    }

    /* access modifiers changed from: package-private */
    public void draw(float lineWidth, float[] mvpMatrix, Buffer vertexBuffer, int lineType, int count, float[] colorArray, int colorOffset) {
        activate();
        GLES20.glLineWidth(lineWidth);
        GLES20.glUniformMatrix4fv(this.mLineMvpLoc, 1, false, mvpMatrix, 0);
        GLES20.glUniform4fv(this.mLineColorLoc, 1, colorArray, colorOffset);
        GLES20.glVertexAttribPointer(this.mLinePosLoc, 3, 5126, false, 12, vertexBuffer);
        GLES20.glEnableVertexAttribArray(this.mLinePosLoc);
        GLES20.glDrawArrays(lineType, 0, count);
    }

    public void draw(float lineWidth, float[] mvpMatrix, Buffer vertexBuffer, Buffer lineIndexArray, int lineType, int count, float[] colorArray, int colorOffset) {
        activate();
        GLES20.glLineWidth(lineWidth);
        GLES20.glUniformMatrix4fv(this.mLineMvpLoc, 1, false, mvpMatrix, 0);
        GLES20.glUniform4fv(this.mLineColorLoc, 1, colorArray, colorOffset);
        GLES20.glVertexAttribPointer(this.mLinePosLoc, 3, 5126, false, 12, vertexBuffer);
        GLES20.glEnableVertexAttribArray(this.mLinePosLoc);
        GLES20.glDrawElements(lineType, count, 5123, lineIndexArray);
    }
}
