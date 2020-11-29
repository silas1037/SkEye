package com.lavadip.skeye.device;

import android.content.Context;

public interface Device {
    void deselect();

    boolean getCurrRotationMatrixNoDec(float[] fArr);

    float getFieldStrength();

    double getFitnessTolerance();

    String getId();

    String getName();

    Sensitivity getSensitivity();

    String getSensorPresentMessage(Context context);

    int getSuggestedSampleWindow();

    boolean isAutoPossible();

    void select();

    void startListening();

    void stopListening();
}
