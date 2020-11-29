package com.lavadip.skeye.device;

public enum Sensitivity {
    Lowest(0.02f, 4, 0.001f, 0.005f, 4, 0.001f, 0.005f, 4, 0.001f),
    Low(0.1f, 3, 0.005f, 0.01f, 3, 0.005f, 0.01f, 3, 0.005f),
    Normal(0.15f, 2, 0.01f, 0.02f, 3, 0.01f, 0.02f, 3, 0.01f),
    High(0.55f, 2, 0.05f, 0.05f, 3, 0.05f, 0.05f, 3, 0.05f),
    Highest(0.7f, 2, 0.1f, 0.1f, 2, 0.1f, 0.1f, 2, 0.1f);
    
    final float accAlpha;
    final float accDAlpha;
    final int accExp;
    final float magAlpha;
    final float magDAlpha;
    final int magExp;
    final float sensorFusionAlpha;
    final float sensorFusionDAlpha;
    final int sensorFusionExp;

    private Sensitivity(float accAlpha2, int accExp2, float accDAlpha2, float magAlpha2, int magExp2, float magDAlpha2, float sensorFusionAlpha2, int sensorFusionExp2, float sensorFusionDAlpha2) {
        this.accAlpha = accAlpha2;
        this.accExp = accExp2;
        this.accDAlpha = accDAlpha2;
        this.magAlpha = magAlpha2;
        this.magExp = magExp2;
        this.magDAlpha = magDAlpha2;
        this.sensorFusionAlpha = sensorFusionAlpha2;
        this.sensorFusionExp = sensorFusionExp2;
        this.sensorFusionDAlpha = sensorFusionDAlpha2;
    }
}
