package com.lavadip.skeye;

import android.opengl.GLSurfaceView;
import android.util.Log;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

public class MultisampleConfigChooser implements GLSurfaceView.EGLConfigChooser {
    private static final String kTag = "SKEYE";
    private boolean mUsesCoverageAa;
    private int[] mValue;

    public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
        this.mValue = new int[1];
        int[] configSpec = {12324, 5, 12323, 6, 12322, 5, 12325, 16, 12352, 4, 12338, 1, 12337, 2, 12344};
        if (!egl.eglChooseConfig(display, configSpec, null, 0, this.mValue)) {
            throw new IllegalArgumentException("eglChooseConfig failed");
        }
        int numConfigs = this.mValue[0];
        if (numConfigs <= 0) {
            configSpec = new int[]{12324, 5, 12323, 6, 12322, 5, 12325, 16, 12352, 4, 12512, 1, 12513, 2, 12344};
            if (egl.eglChooseConfig(display, configSpec, null, 0, this.mValue)) {
                numConfigs = this.mValue[0];
            } else {
                numConfigs = 0;
            }
            if (numConfigs <= 0) {
                configSpec = new int[]{12324, 5, 12323, 6, 12322, 5, 12325, 16, 12352, 4, 12344};
                if (!egl.eglChooseConfig(display, configSpec, null, 0, this.mValue)) {
                    throw new IllegalArgumentException("3rd eglChooseConfig failed");
                }
                numConfigs = this.mValue[0];
                if (numConfigs <= 0) {
                    throw new IllegalArgumentException("No configs match configSpec");
                }
            } else {
                Log.d(kTag, "Coverage sampling (Tegra device?)");
                this.mUsesCoverageAa = true;
            }
        }
        EGLConfig[] configs = new EGLConfig[numConfigs];
        if (!egl.eglChooseConfig(display, configSpec, configs, numConfigs, this.mValue)) {
            throw new IllegalArgumentException("data eglChooseConfig failed");
        }
        int index = -1;
        int i = 0;
        while (true) {
            if (i >= configs.length) {
                break;
            } else if (findConfigAttrib(egl, display, configs[i], 12324, 0) == 5) {
                index = i;
                break;
            } else {
                i++;
            }
        }
        if (index == -1) {
            Log.w(kTag, "Did not find sane config, using first");
            index = 0;
        }
        EGLConfig config = configs.length > 0 ? configs[index] : null;
        if (config != null) {
            return config;
        }
        throw new IllegalArgumentException("No config chosen");
    }

    private int findConfigAttrib(EGL10 egl, EGLDisplay display, EGLConfig config, int attribute, int defaultValue) {
        if (egl.eglGetConfigAttrib(display, config, attribute, this.mValue)) {
            return this.mValue[0];
        }
        return defaultValue;
    }

    public boolean usesCoverageAa() {
        return this.mUsesCoverageAa;
    }
}
