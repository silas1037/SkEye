package com.lavadip.skeye;

import android.graphics.Color;
import android.graphics.Paint;

public final class LabelPaints {
    public final Paint brightStarPaint = new LabelPaint(Color.rgb(255, 255, 255), 16.0f);
    public final Paint cometPaint;
    public final Paint constPaint = new LabelPaint(Color.rgb(255, 239, 40), 15.0f);
    final Paint directionPaint = new LabelPaint(Color.rgb(196, 255, 196), 16.0f);
    final float displayScaleFactor;
    public final Paint extraBrightStarPaint = new LabelPaint(Color.rgb(190, 190, 190), 17.0f);
    public final Paint messierPaint = new LabelPaint(Color.rgb(190, 190, 190), 14.0f);
    public final LabelPaint[] planetPaints;
    public final Paint satellitePaint;
    public final Paint starPaint = new LabelPaint(Color.rgb(200, 200, 200), 14.0f);

    /* access modifiers changed from: package-private */
    public class LabelPaint extends Paint {
        LabelPaint(int color, float size) {
            setAntiAlias(LabelPaints.this.displayScaleFactor >= 1.0f || size > 14.0f);
            setStrokeWidth(5.0f * LabelPaints.this.displayScaleFactor);
            setOptions(color, size);
        }

        /* access modifiers changed from: package-private */
        public void setOptions(int color, float size) {
            setColor(color);
            setTextSize(LabelPaints.this.displayScaleFactor * size);
        }
    }

    LabelPaints(float displayScaleFactor2) {
        this.displayScaleFactor = displayScaleFactor2;
        this.constPaint.setUnderlineText(displayScaleFactor2 >= 1.0f);
        this.satellitePaint = new LabelPaint(Color.argb(100, 255, 255, 255), 13.0f);
        this.cometPaint = new LabelPaint(Color.argb(100, 255, 255, 255), 13.0f);
        this.planetPaints = new LabelPaint[14];
        this.planetPaints[0] = new LabelPaint(Color.rgb(255, 128, 0), 18.0f);
        this.planetPaints[1] = new LabelPaint(Color.rgb(255, 255, 255), 16.0f);
        this.planetPaints[2] = new LabelPaint(Color.rgb(187, 187, 187), 12.0f);
        this.planetPaints[3] = new LabelPaint(Color.rgb(153, 153, 255), 14.0f);
        this.planetPaints[4] = new LabelPaint(Color.rgb(255, 187, 153), 12.0f);
        this.planetPaints[5] = new LabelPaint(Color.rgb(255, 255, 255), 14.0f);
        this.planetPaints[6] = new LabelPaint(Color.rgb(255, 255, 187), 14.0f);
        this.planetPaints[7] = new LabelPaint(Color.rgb(195, 233, 236), 12.0f);
        this.planetPaints[8] = new LabelPaint(Color.rgb(76, 114, 204), 12.0f);
        this.planetPaints[9] = new LabelPaint(Color.rgb(255, 128, 0), 11.0f);
        LabelPaint[] labelPaintArr = this.planetPaints;
        LabelPaint[] labelPaintArr2 = this.planetPaints;
        LabelPaint[] labelPaintArr3 = this.planetPaints;
        LabelPaint[] labelPaintArr4 = this.planetPaints;
        LabelPaint labelPaint = new LabelPaint(Color.rgb(255, 128, 0), 11.0f);
        labelPaintArr4[13] = labelPaint;
        labelPaintArr3[12] = labelPaint;
        labelPaintArr2[11] = labelPaint;
        labelPaintArr[10] = labelPaint;
    }
}
