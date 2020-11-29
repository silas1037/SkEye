package com.lavadip.skeye.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import com.lavadip.skeye.C0031R;

public final class FastTextView extends View {
    private static final int DEFAULT_SIZE = 18;
    private boolean alignCenter = false;
    private final Rect bounds = new Rect();
    private String currText = "";
    private final float density;
    private final String formatText;
    private int mAscent;
    private final Paint textPaint = new Paint();

    public FastTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Resources res = getResources();
        this.textPaint.setColor(res.getColor(C0031R.color.bright_color));
        this.density = res.getDisplayMetrics().scaledDensity;
        float textSize = 18.0f * this.density;
        this.textPaint.setAntiAlias(true);
        String myFormatText = null;
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, C0031R.styleable.com_lavadip_skeye_FastTextView);
            myFormatText = a.getString(C0031R.styleable.com_lavadip_skeye_FastTextView_format);
            myFormatText = myFormatText == null ? "" : myFormatText;
            this.alignCenter = a.getBoolean(C0031R.styleable.com_lavadip_skeye_FastTextView_alignCenter, false);
            a.recycle();
            TypedArray b = context.obtainStyledAttributes(attrs, new int[]{16842901});
            textSize = b.getDimension(0, textSize);
            b.recycle();
        }
        this.textPaint.setTextSize(textSize);
        this.formatText = myFormatText;
    }

    public void setText(String text) {
        this.currText = text;
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int centeringOffset = 0;
        if (this.alignCenter) {
            this.textPaint.getTextBounds(this.currText, 0, this.currText.length(), this.bounds);
            centeringOffset = Math.max(0, ((getWidth() - getPaddingRight()) - this.bounds.width()) / 2);
        }
        canvas.drawText(this.currText, (float) (getPaddingLeft() + centeringOffset), (float) (getPaddingTop() - this.mAscent), this.textPaint);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int measureSpec) {
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);
        int result = getPaddingLeft() + getPaddingRight() + ((int) this.textPaint.measureText(this.formatText)) + getPaddingLeft() + getPaddingRight();
        if (specMode == 1073741824) {
            return specSize;
        }
        if (specMode == Integer.MIN_VALUE) {
            return Math.min(result, specSize);
        }
        return result;
    }

    private int measureHeight(int measureSpec) {
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);
        this.mAscent = (int) this.textPaint.ascent();
        int result = ((int) (((float) (-this.mAscent)) + this.textPaint.descent())) + getPaddingTop() + getPaddingBottom();
        if (specMode == 1073741824) {
            return Math.min(result, specSize);
        }
        if (specMode == Integer.MIN_VALUE) {
            return Math.min(result, specSize);
        }
        return result;
    }
}
