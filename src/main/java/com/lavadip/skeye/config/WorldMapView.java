package com.lavadip.skeye.config;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public final class WorldMapView extends ImageView {
    private static final float SCALE_MARKER_CROSSHAIRS = 1.5f;
    private float latitude;
    private float longitude;
    private ValueAnimator mMarkerAnimator;
    private float mMarkerRadius;
    private float mStrokeWidth;
    private Paint markerBorderDarkPaint;
    private Paint markerBorderPaint;
    private Paint markerBorderShadowPaint;
    private Paint markerFillPaint;
    private OnTapListener onTapListener;

    public interface OnTapListener {
        void publishLatLong(float f, float f2);
    }

    public WorldMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WorldMapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        this.mStrokeWidth = 4.0f * density;
        this.mMarkerRadius = 20.0f * density;
        this.markerBorderPaint = new Paint();
        this.markerBorderPaint.setColor(-8140494);
        this.markerBorderPaint.setStyle(Paint.Style.STROKE);
        this.markerBorderPaint.setStrokeWidth(this.mStrokeWidth);
        this.markerBorderDarkPaint = new Paint();
        this.markerBorderDarkPaint.setColor(-14798840);
        this.markerBorderDarkPaint.setStyle(Paint.Style.STROKE);
        this.markerBorderDarkPaint.setStrokeWidth(this.mStrokeWidth);
        this.markerBorderShadowPaint = new Paint();
        this.markerBorderShadowPaint.setColor(-1716868438);
        this.markerBorderShadowPaint.setStyle(Paint.Style.STROKE);
        this.markerBorderShadowPaint.setStrokeWidth(this.mStrokeWidth * SCALE_MARKER_CROSSHAIRS);
        this.markerFillPaint = new Paint();
        this.markerFillPaint.setColor(894600726);
        this.markerFillPaint.setStyle(Paint.Style.FILL);
        this.mMarkerAnimator = ValueAnimator.ofFloat(0.0f, 360.0f).setDuration(5000L);
        this.mMarkerAnimator.setInterpolator(null);
        this.mMarkerAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            /* class com.lavadip.skeye.config.WorldMapView.C01201 */

            public void onAnimationUpdate(ValueAnimator animation) {
                WorldMapView.this.invalidate();
            }
        });
        this.mMarkerAnimator.setRepeatCount(-1);
        this.mMarkerAnimator.start();
        setOnTouchListener(new View.OnTouchListener() {
            /* class com.lavadip.skeye.config.WorldMapView.View$OnTouchListenerC01212 */

            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == 0) {
                    float x = event.getX(event.getActionIndex());
                    float y = event.getY(event.getActionIndex());
                    Rect bounds = WorldMapView.this.getDrawable().getBounds();
                    Matrix matrix = WorldMapView.this.getImageMatrix();
                    float[] pts = {0.0f, 0.0f, (float) bounds.width(), (float) bounds.height()};
                    matrix.mapPoints(pts);
                    float lat = 90.0f - (((y - pts[1]) / (pts[3] - pts[1])) * 180.0f);
                    float lng = (((x - pts[0]) / (pts[2] - pts[0])) * 360.0f) - 180.0f;
                    if (lat >= -90.0f && lat <= 90.0f && lng >= -180.0f && lng <= 180.0f) {
                        WorldMapView.this.onTapListener.publishLatLong(lat, lng);
                    }
                }
                return false;
            }
        });
    }

    public void setLatLong(double latitude2, double longitude2) {
        this.latitude = (float) latitude2;
        this.longitude = (float) longitude2;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.concat(getImageMatrix());
        Rect bounds = getDrawable().getBounds();
        float centerX = bounds.exactCenterX();
        float centerY = bounds.exactCenterY();
        float x = centerX + (((this.longitude / 180.0f) * ((float) bounds.width())) / 2.0f);
        float y = centerY - (((this.latitude / 90.0f) * ((float) bounds.height())) / 2.0f);
        canvas.rotate(((Float) this.mMarkerAnimator.getAnimatedValue()).floatValue(), x, y);
        drawMarker(canvas, x, y, this.markerBorderShadowPaint, this.markerBorderShadowPaint, null);
        drawMarker(canvas, x, y, this.markerBorderPaint, this.markerBorderDarkPaint, this.markerFillPaint);
        canvas.restore();
    }

    private void drawMarker(Canvas canvas, float x, float y, Paint borderPaint, Paint borderDarkPaint, Paint fillPaint) {
        if (fillPaint != null) {
            canvas.drawCircle(x, y, this.mMarkerRadius, fillPaint);
        }
        canvas.drawCircle(x, y, this.mMarkerRadius, borderPaint);
        canvas.drawLine(x - (this.mMarkerRadius * SCALE_MARKER_CROSSHAIRS), y, x + (this.mMarkerRadius * SCALE_MARKER_CROSSHAIRS), y, borderDarkPaint);
        canvas.drawLine(x, y - (this.mMarkerRadius * SCALE_MARKER_CROSSHAIRS), x, y + (this.mMarkerRadius * SCALE_MARKER_CROSSHAIRS), borderDarkPaint);
    }

    public void setOnTapListener(OnTapListener onTapListener2) {
        this.onTapListener = onTapListener2;
    }
}
