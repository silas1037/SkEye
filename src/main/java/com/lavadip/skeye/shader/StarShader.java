package com.lavadip.skeye.shader;

import android.annotation.TargetApi;
import android.opengl.GLES20;
import android.util.Log;
import com.lavadip.skeye.MyRenderer;
import java.nio.Buffer;

@TargetApi(8)
public final class StarShader extends Shader {
    public static final int ATTRIB_SIZE_BYTES = 8;
    static final int ATTRIB_SIZE_FLOATS = 2;
    public static final int POSITION_SIZE_BYTES = 8;
    static final int POSITION_SIZE_FLOATS = 2;
    private static final String fShaderStr = "precision mediump float;                             uniform sampler2D s_texture; varying vec4 v_color; varying float v_type; void main() {  vec4 texColor = texture2D( s_texture, gl_PointCoord);    float tColor = v_type > 1.1 ? texColor[1] : texColor[0];    gl_FragColor = v_color * tColor; }";
    private static final String vShaderStr = String.format(null, "\n  uniform mat4 u_mvpMatrix;\n  uniform mat4 u_geoCentricMatrix;\n  uniform float u_scale;\n  uniform float u_relScale;\n  uniform float u_relScalePivot;\n  uniform float u_gbFilter;\n  uniform float u_extinction;\n  attribute float a_vtype;\n  attribute float a_mag;\n  attribute vec2 a_position; \n  varying vec4 v_color; \n  varying float v_type; \n  void getZenAngle(in vec4 dir, in mat4 geoCentricMatrix, out float angle) {\n    vec4 geoDir = geoCentricMatrix * dir; \n    angle = acos(clamp(dot(geoDir.xyz,vec3(0, 0, 1)), -1.0, 1.0)); \n  }\n  void main() {\n    float ra = a_position.x;\n    float dec = a_position.y;\n    float cosd = cos(dec);\n    float x = 9.0 * sin(ra)*cosd;\n    float y = 9.0 * sin(dec);\n    float z = 9.0 * cos(ra)*cosd;\n    vec4 position4 = vec4(x, y, z, 1.0);\n    float zenAngle = 0.0;\n    float piBy2 = 3.141592653589793 / 2.0;\n    float airMass = 0.0;\n    float imag = ((11.0 - u_relScalePivot) + (u_relScalePivot - a_mag)*u_relScale) - airMass*u_extinction;\n    float factor = pow(2.512,imag*0.333);\n    float alpha =  clamp(u_scale*0.04642 * factor, 0.0, 1.0);\n    gl_PointSize = u_scale * factor;\n    v_type = gl_PointSize > 18.0 ? 2.2 : 1.0; \n    gl_PointSize *= v_type; \n    gl_Position = u_mvpMatrix * position4;           \n    if (a_vtype < 0.0) { \n      v_color = vec4(%f, %f * u_gbFilter, %f * u_gbFilter, alpha); \n    } else if (a_vtype < 1.5) { \n      v_color = vec4(%f, %f * u_gbFilter, %f * u_gbFilter, alpha); \n    } else if (a_vtype < 2.5) { \n      v_color = vec4(%f, %f * u_gbFilter, %f * u_gbFilter, alpha); \n    } else if (a_vtype < 4.5) { \n      v_color = vec4(%f, %f * u_gbFilter, %f * u_gbFilter, alpha); \n    } else if (a_vtype < 9.0) { \n      v_color = vec4(%f, %f * u_gbFilter, %f * u_gbFilter, alpha); \n    } else if (a_vtype < 18.0) { \n      v_color = vec4(%f, %f * u_gbFilter, %f * u_gbFilter, alpha); \n    } else if (a_vtype < 36.0) { \n      v_color = vec4(%f, %f * u_gbFilter, %f * u_gbFilter, alpha); \n    } else { \n      v_color = vec4(%f, %f * u_gbFilter, %f * u_gbFilter, alpha); \n    } \n  }", Float.valueOf(hexColor(255)), Float.valueOf(hexColor(255)), Float.valueOf(hexColor(255)), Float.valueOf(hexColor(155)), Float.valueOf(hexColor(176)), Float.valueOf(hexColor(255)), Float.valueOf(hexColor(170)), Float.valueOf(hexColor(191)), Float.valueOf(hexColor(255)), Float.valueOf(hexColor(202)), Float.valueOf(hexColor(215)), Float.valueOf(hexColor(255)), Float.valueOf(hexColor(248)), Float.valueOf(hexColor(247)), Float.valueOf(hexColor(255)), Float.valueOf(hexColor(255)), Float.valueOf(hexColor(244)), Float.valueOf(hexColor(234)), Float.valueOf(hexColor(255)), Float.valueOf(hexColor(210)), Float.valueOf(hexColor(161)), Float.valueOf(hexColor(255)), Float.valueOf(hexColor(204)), Float.valueOf(hexColor(111)));
    private final double displayScaleFactorSqRoot;
    final int mExtinctionLoc;
    private float mGBFilter = 1.0f;
    final int mGBFilterLoc;
    final int mGeoCentricLoc;
    final int mMagLoc;
    final int mMvpLoc;
    final int mPosLoc;
    final int mRelativeScaleLoc;
    final int mRelativeScalePivotLoc;
    final int mSamplerLoc;
    final int mScaleLoc;
    private final int mStarTextureId;
    final int mTypeLoc;

    private static float hexColor(int i) {
        return ((float) i) / 255.0f;
    }

    public void beginDrawing() {
        activate();
        GLES20.glBlendFunc(770, 1);
        GLES20.glBindTexture(3553, this.mStarTextureId);
    }

    public StarShader(float displayScaleFactor, int themeOrdinal, int starTextureId) {
        super(vShaderStr, fShaderStr);
        this.displayScaleFactorSqRoot = Math.sqrt((double) displayScaleFactor);
        this.mStarTextureId = starTextureId;
        setTheme(themeOrdinal);
        if (this.mProgramId == 0) {
            Log.e("SKEYE", "Error Loading StarShader");
            this.mRelativeScalePivotLoc = -1;
            this.mRelativeScaleLoc = -1;
            this.mExtinctionLoc = -1;
            this.mGBFilterLoc = -1;
            this.mSamplerLoc = -1;
            this.mPosLoc = -1;
            this.mTypeLoc = -1;
            this.mMagLoc = -1;
            this.mScaleLoc = -1;
            this.mGeoCentricLoc = -1;
            this.mMvpLoc = -1;
            return;
        }
        this.mMvpLoc = GLES20.glGetUniformLocation(this.mProgramId, "u_mvpMatrix");
        this.mGeoCentricLoc = GLES20.glGetUniformLocation(this.mProgramId, "u_geoCentricMatrix");
        this.mScaleLoc = GLES20.glGetUniformLocation(this.mProgramId, "u_scale");
        this.mGBFilterLoc = GLES20.glGetUniformLocation(this.mProgramId, "u_gbFilter");
        this.mExtinctionLoc = GLES20.glGetUniformLocation(this.mProgramId, "u_extinction");
        this.mRelativeScaleLoc = GLES20.glGetUniformLocation(this.mProgramId, "u_relScale");
        this.mRelativeScalePivotLoc = GLES20.glGetUniformLocation(this.mProgramId, "u_relScalePivot");
        this.mMagLoc = GLES20.glGetAttribLocation(this.mProgramId, "a_mag");
        this.mTypeLoc = GLES20.glGetAttribLocation(this.mProgramId, "a_vtype");
        this.mPosLoc = GLES20.glGetAttribLocation(this.mProgramId, "a_position");
        this.mSamplerLoc = GLES20.glGetUniformLocation(this.mProgramId, "s_texture");
    }

    public void setTheme(int themeOrdinal) {
        this.mGBFilter = themeOrdinal == 0 ? 1.0f : themeOrdinal == 1 ? 0.5f : 0.0f;
    }

    public void draw(float[] mvpMatrix, float[] geoCentricMatrix, Buffer vertexAttribBuffer, Buffer vertexBuffer, int offset, int count, float absScale, float relScale, float relScalePivot, float extinction) {
        float scaleFactor = absScale + ((float) Math.pow(this.displayScaleFactorSqRoot * ((double) (1.25f - ((MyRenderer.fov - 0.2f) / 74.8f))), 1.3d));
        GLES20.glUniformMatrix4fv(this.mMvpLoc, 1, false, mvpMatrix, 0);
        GLES20.glUniformMatrix4fv(this.mGeoCentricLoc, 1, false, geoCentricMatrix, 0);
        GLES20.glUniform1f(this.mScaleLoc, scaleFactor);
        GLES20.glUniform1f(this.mGBFilterLoc, this.mGBFilter);
        GLES20.glUniform1f(this.mExtinctionLoc, extinction);
        GLES20.glUniform1f(this.mRelativeScaleLoc, relScale);
        GLES20.glUniform1f(this.mRelativeScalePivotLoc, relScalePivot);
        vertexAttribBuffer.position(offset * 2);
        GLES20.glVertexAttribPointer(this.mMagLoc, 1, 5126, false, 8, vertexAttribBuffer);
        vertexAttribBuffer.position((offset * 2) + 1);
        GLES20.glVertexAttribPointer(this.mTypeLoc, 1, 5126, false, 8, vertexAttribBuffer);
        vertexBuffer.position(offset * 2);
        GLES20.glVertexAttribPointer(this.mPosLoc, 2, 5126, false, 8, vertexBuffer);
        GLES20.glEnableVertexAttribArray(this.mMagLoc);
        GLES20.glEnableVertexAttribArray(this.mTypeLoc);
        GLES20.glEnableVertexAttribArray(this.mPosLoc);
        GLES20.glUniform1i(this.mSamplerLoc, 0);
        GLES20.glDrawArrays(0, 0, count);
    }
}
