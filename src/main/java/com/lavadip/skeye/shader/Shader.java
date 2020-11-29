package com.lavadip.skeye.shader;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.util.Log;
import java.io.InputStream;
import java.nio.ByteBuffer;

@TargetApi(8)
public abstract class Shader {
    protected final int mProgramId;

    protected Shader(String vertShaderSrc, String fragShaderSrc) {
        this.mProgramId = loadProgram(vertShaderSrc, fragShaderSrc);
    }

    public void activate() {
        GLES20.glUseProgram(this.mProgramId);
    }

    private static int loadProgram(String vertShaderSrc, String fragShaderSrc) {
        int vertexShader = loadShader(35633, vertShaderSrc);
        if (vertexShader == 0) {
            Log.e("SKEYE", "Error Loading Vertex shader");
            return 0;
        }
        int fragmentShader = loadShader(35632, fragShaderSrc);
        if (fragmentShader == 0) {
            Log.e("SKEYE", "Error Loading Frag shader");
            GLES20.glDeleteShader(vertexShader);
            return 0;
        }
        int programObject = GLES20.glCreateProgram();
        if (programObject == 0) {
            return 0;
        }
        GLES20.glAttachShader(programObject, vertexShader);
        GLES20.glAttachShader(programObject, fragmentShader);
        GLES20.glLinkProgram(programObject);
        int[] linked = new int[1];
        GLES20.glGetProgramiv(programObject, 35714, linked, 0);
        if (linked[0] == 0) {
            Log.e("SKEYE", "Error linking program:");
            Log.e("SKEYE", GLES20.glGetProgramInfoLog(programObject));
            GLES20.glDeleteProgram(programObject);
            return 0;
        }
        GLES20.glDeleteShader(vertexShader);
        GLES20.glDeleteShader(fragmentShader);
        return programObject;
    }

    private static int loadShader(int type, String shaderSrc) {
        int shader = GLES20.glCreateShader(type);
        if (shader == 0) {
            return 0;
        }
        GLES20.glShaderSource(shader, shaderSrc);
        GLES20.glCompileShader(shader);
        int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, 35713, compiled, 0);
        if (compiled[0] != 0) {
            return shader;
        }
        Log.e("SKEYE", "Error compiling shader: " + GLES20.glGetShaderInfoLog(shader));
        GLES20.glDeleteShader(shader);
        return 0;
    }

    public static int loadTexture(InputStream is) {
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        byte[] buffer = new byte[(bitmap.getWidth() * bitmap.getHeight() * 3)];
        int bWidth = bitmap.getWidth();
        int bHeight = bitmap.getHeight();
        for (int y = 0; y < bHeight; y++) {
            int ybWidth = y * bWidth;
            for (int x = 0; x < bWidth; x++) {
                int pixel = bitmap.getPixel(x, y);
                buffer[((ybWidth + x) * 3) + 0] = (byte) ((pixel >> 16) & 255);
                buffer[((ybWidth + x) * 3) + 1] = (byte) ((pixel >> 8) & 255);
                buffer[((ybWidth + x) * 3) + 2] = (byte) ((pixel >> 0) & 255);
            }
        }
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bitmap.getWidth() * bitmap.getHeight() * 3);
        byteBuffer.put(buffer).position(0);
        int[] textureId = new int[1];
        GLES20.glGenTextures(1, textureId, 0);
        GLES20.glBindTexture(3553, textureId[0]);
        GLES20.glTexImage2D(3553, 0, 6407, bWidth, bHeight, 0, 6407, 5121, byteBuffer);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glTexParameteri(3553, 10240, 9729);
        GLES20.glTexParameteri(3553, 10242, 33071);
        GLES20.glTexParameteri(3553, 10243, 33071);
        return textureId[0];
    }

    public static int loadTextureRGBA(InputStream is) {
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        byte[] buffer = new byte[(bitmap.getWidth() * bitmap.getHeight() * 4)];
        int bWidth = bitmap.getWidth();
        int bHeight = bitmap.getHeight();
        for (int y = 0; y < bHeight; y++) {
            int ybWidth = y * bWidth;
            for (int x = 0; x < bWidth; x++) {
                int pixel = bitmap.getPixel(x, y);
                buffer[((ybWidth + x) * 4) + 3] = (byte) ((pixel >> 24) & 255);
                buffer[((ybWidth + x) * 4) + 0] = (byte) ((pixel >> 16) & 255);
                buffer[((ybWidth + x) * 4) + 1] = (byte) ((pixel >> 8) & 255);
                buffer[((ybWidth + x) * 4) + 2] = (byte) ((pixel >> 0) & 255);
            }
        }
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bitmap.getWidth() * bitmap.getHeight() * 4);
        byteBuffer.put(buffer).position(0);
        int[] textureId = new int[1];
        GLES20.glGenTextures(1, textureId, 0);
        GLES20.glBindTexture(3553, textureId[0]);
        GLES20.glTexImage2D(3553, 0, 6408, bWidth, bHeight, 0, 6408, 5121, byteBuffer);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glTexParameteri(3553, 10240, 9729);
        GLES20.glTexParameteri(3553, 10242, 33071);
        GLES20.glTexParameteri(3553, 10243, 33071);
        return textureId[0];
    }
}
