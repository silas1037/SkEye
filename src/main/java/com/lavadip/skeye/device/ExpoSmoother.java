package com.lavadip.skeye.device;

/* access modifiers changed from: package-private */
public class ExpoSmoother {
    private float alpha;
    private float dAlpha;
    private final float[] diffs;
    private int exponent;
    private final float[] last;

    /* renamed from: n */
    private final int f88n;

    public ExpoSmoother(float alpha2, int exponent2, float dAlpha2, int n) {
        this.alpha = alpha2;
        this.exponent = exponent2;
        this.dAlpha = dAlpha2;
        this.f88n = n;
        this.last = new float[n];
        this.diffs = new float[n];
    }

    /* access modifiers changed from: package-private */
    public void setParams(float alpha2, int exponent2, float dAlpha2) {
        this.alpha = alpha2;
        this.exponent = exponent2;
        this.dAlpha = dAlpha2;
    }

    /* access modifiers changed from: package-private */
    public void update(float[] target, float[] values) {
        for (int i = 0; i < this.f88n; i++) {
            float value = values[i];
            float prevValue = this.last[i];
            float diff = value - prevValue;
            float absDiff = Math.abs(diff);
            float f3 = diff * this.alpha;
            for (int n = 1; n < this.exponent; n++) {
                f3 *= absDiff;
            }
            if (f3 < (-absDiff) || f3 > absDiff) {
                this.diffs[i] = 0.0f;
                float[] fArr = this.last;
                target[i] = value;
                fArr[i] = value;
            } else {
                float f32 = f3 + (this.diffs[i] * this.dAlpha);
                float[] fArr2 = this.diffs;
                fArr2[i] = fArr2[i] * 0.5f;
                float[] fArr3 = this.diffs;
                fArr3[i] = fArr3[i] + ((diff - f32) * 0.5f);
                float[] fArr4 = this.last;
                float f = prevValue + f32;
                target[i] = f;
                fArr4[i] = f;
            }
        }
    }
}
