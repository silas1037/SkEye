package com.lavadip.skeye.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.View;

public class OverlaidFilterView extends View {
    private static int filterConstant = (-16777216 | redBand);
    private static int redBand = 16711680;
    private int filter = -1;
    private int reddness = 0;

    public OverlaidFilterView(Context context) {
        super(context);
    }

    public OverlaidFilterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OverlaidFilterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setReddness(int newReddness) {
        this.reddness = newReddness;
        int other = 255 - this.reddness;
        this.filter = filterConstant | (other << 8) | other;
        postInvalidate();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(this.filter, PorterDuff.Mode.MULTIPLY);
    }
}
