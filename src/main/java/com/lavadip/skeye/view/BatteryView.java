package com.lavadip.skeye.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.lavadip.skeye.C0031R;

public final class BatteryView extends View {
    private final Paint bckPaint = new Paint();
    private int currLevel = 100;
    private float density = 1.0f;
    private final Paint highPaint = new Paint();
    private final Paint lowPaint = new Paint();
    private final Paint medPaint = new Paint();
    public BroadcastReceiver receiver = new BroadcastReceiver() {
        /* class com.lavadip.skeye.view.BatteryView.C01341 */

        public void onReceive(Context context, Intent intent) {
            BatteryView.this.currLevel = intent.getIntExtra("level", 0);
            BatteryView.this.scale = intent.getIntExtra("scale", 100);
            BatteryView.this.invalidate();
        }
    };
    private int scale = 100;

    public BatteryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        Resources res = getResources();
        this.bckPaint.setColor(res.getColor(C0031R.color.battery_back));
        this.bckPaint.setStyle(Paint.Style.FILL);
        this.lowPaint.setColor(res.getColor(C0031R.color.battery_low));
        this.lowPaint.setStyle(Paint.Style.FILL);
        this.medPaint.setColor(res.getColor(C0031R.color.battery_med));
        this.medPaint.setStyle(Paint.Style.FILL);
        this.highPaint.setColor(res.getColor(C0031R.color.battery_high));
        this.highPaint.setStyle(Paint.Style.FILL);
        this.density = getResources().getDisplayMetrics().density;
        Log.d("SKEYE", "Density = " + this.density);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int padLeft = getPaddingLeft();
        int padRight = getPaddingRight();
        int padTop = getPaddingTop();
        int padBottom = getPaddingBottom();
        float width = (float) (getWidth() - (padLeft + padRight));
        float anodeWidth = Math.min(0.1f * width, 5.0f * this.density);
        float batWidth = width - anodeWidth;
        int height = getHeight() - (padTop + padBottom);
        float anodeHeight = ((float) height) * 0.5f;
        float ratio = ((float) this.currLevel) / ((float) this.scale);
        float scaledWidth = (batWidth - 2.0f) * ratio;
        Paint paint = ((double) ratio) > 0.4d ? this.highPaint : ((double) ratio) > 0.15d ? this.medPaint : this.lowPaint;
        canvas.drawRect(((float) padLeft) + anodeWidth, (float) padTop, ((float) padLeft) + width, (float) (padTop + height), this.bckPaint);
        canvas.drawRect((float) padLeft, ((((float) height) - anodeHeight) / 2.0f) + ((float) padTop), ((float) padLeft) + anodeWidth, ((((float) height) + anodeHeight) / 2.0f) + ((float) padTop), this.bckPaint);
        canvas.drawRect(((((float) padLeft) + anodeWidth) + batWidth) - scaledWidth, (float) (padTop + 2), (((float) padLeft) + width) - 2.0f, (float) ((padTop + height) - 2), paint);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int measureSpec) {
        int paddingLeft = (int) (((float) (getPaddingLeft() + 80 + getPaddingRight())) * this.density);
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);
        if (specMode == 1073741824) {
            return specSize;
        }
        if (specMode == Integer.MIN_VALUE) {
            return specSize;
        }
        return specSize + 10;
    }

    private int measureHeight(int measureSpec) {
        int result = (int) (((float) (getPaddingTop() + 14 + getPaddingBottom())) * this.density);
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);
        if (specMode == 1073741824) {
            return specSize;
        }
        if (specMode == Integer.MIN_VALUE) {
            return Math.min(result, specSize);
        }
        return result;
    }
}
