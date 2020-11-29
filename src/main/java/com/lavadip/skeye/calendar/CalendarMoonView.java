package com.lavadip.skeye.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public final class CalendarMoonView extends View {
    private static final int SIZE = 12;
    private final Paint brightPaint = new Paint();
    private float crescentFactor;
    private boolean isBright;
    private boolean leftSide;
    private int mySize;
    private RectF semiCircleBounds;
    private final Paint shadowPaint = new Paint();
    private float sizeBy2;

    public CalendarMoonView(Context context) {
        super(context);
        init();
    }

    public CalendarMoonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        this.brightPaint.setARGB(255, 224, 224, 224);
        this.shadowPaint.setARGB(255, 55, 55, 55);
        this.brightPaint.setAntiAlias(true);
        this.shadowPaint.setAntiAlias(true);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        int measuredHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        this.mySize = (int) (12.0f * getContext().getResources().getDisplayMetrics().density);
        int width = this.mySize;
        int height = this.mySize;
        switch (View.MeasureSpec.getMode(widthMeasureSpec)) {
            case Integer.MIN_VALUE:
                width = Math.min(width, measuredWidth);
                break;
            case 1073741824:
                width = measuredWidth;
                break;
        }
        switch (View.MeasureSpec.getMode(heightMeasureSpec)) {
            case Integer.MIN_VALUE:
                height = Math.min(this.mySize, measuredHeight);
                break;
            case 1073741824:
                height = measuredHeight;
                break;
        }
        this.mySize = Math.min(width, height);
        this.sizeBy2 = ((float) this.mySize) * 0.5f;
        this.semiCircleBounds = new RectF(0.0f, 0.0f, (float) this.mySize, (float) this.mySize);
        setMeasuredDimension(this.mySize, this.mySize);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        float f = 0.0f;
        Paint foregroundPaint = this.isBright ? this.brightPaint : this.shadowPaint;
        Paint backgroundPaint = this.isBright ? this.shadowPaint : this.brightPaint;
        float ovalWidthBy2 = this.sizeBy2 * this.crescentFactor;
        RectF ovalBounds = new RectF(this.sizeBy2 - ovalWidthBy2, 0.0f, this.sizeBy2 + ovalWidthBy2, (float) this.mySize);
        float f2 = this.sizeBy2;
        float f3 = this.sizeBy2;
        float f4 = this.sizeBy2;
        if (!this.isBright) {
            f = 0.5f;
        }
        canvas.drawCircle(f2, f3, f4 - f, backgroundPaint);
        canvas.drawOval(ovalBounds, foregroundPaint);
        canvas.drawArc(this.semiCircleBounds, (float) (this.leftSide ? 90 : 270), 180.0f, false, foregroundPaint);
    }

    public void setPhase(boolean waxing, double angleSunMoonDegrees) {
        boolean z;
        boolean z2 = true;
        this.crescentFactor = (float) Math.abs((90.0d - angleSunMoonDegrees) / 90.0d);
        if (angleSunMoonDegrees > 90.0d) {
            z = true;
        } else {
            z = false;
        }
        this.isBright = z;
        if ((!waxing || this.isBright) && (waxing || !this.isBright)) {
            z2 = false;
        }
        this.leftSide = z2;
        invalidate();
    }
}
