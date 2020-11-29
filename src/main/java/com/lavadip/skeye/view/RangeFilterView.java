package com.lavadip.skeye.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public final class RangeFilterView extends View {
    private static final int MARK_BOTH = 3;
    private static final int MARK_LEFT = 1;
    private static final int MARK_NONE = 0;
    private static final int MARK_RIGHT = 2;
    private final Paint areaBasePaint;
    private final Path areaBasePath = new Path();
    private final Paint areaOverlayPaint;
    private final Path areaOverlayPath = new Path();
    private final float bottomMargin;
    private float[] counts;
    private final float density;
    private float graphHeight;
    private float graphWidth;
    private final float horizLabelHeight;
    private String horizLabelText = "<Magnitude>";
    private final float horizLabelWidth;
    private float[] horizRangeMarks;
    private String labelLeftText;
    private float labelLeftWidth;
    private final Paint labelPaint;
    private final float labelPaintBottom;
    private String labelRightText;
    private float labelRightWidth;
    private final float leftMargin;
    private int leftMark = 0;
    private float leftMarkX;
    private final Path lineBasePath = new Path();
    private final Paint lineOverlayPaint;
    private final Path lineOverlayPath = new Path();
    private MarkChangeListener mListener = null;
    private final float markerHeight;
    private final Path markerLeftPath = new Path();
    private final Paint markerLinePaint;
    private final Paint markerPaint;
    private final Path markerRightPath = new Path();
    private final Paint markerSelectedLinePaint;
    private final Paint markerSelectedPaint;
    private final float markerWidth;
    private int myHeight;
    private int myWidth;
    private float[] overlayCounts;
    private boolean overlayEnabled = false;
    private final float rightMargin;
    private int rightMark = 0;
    private float rightMarkX;
    private final Paint selAreaPaint;
    private final Paint selLinePaint;
    private int selectedForDragging = 0;
    private final float topMargin;
    private final float vertLabelWidth;
    private float xScale;
    private float yScale;

    public interface MarkChangeListener {
        void onMarkChanged(int i, float f, float f2);
    }

    public RangeFilterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.density = context.getResources().getDisplayMetrics().density;
        this.markerWidth = 25.0f * this.density;
        this.markerHeight = 15.0f * this.density;
        this.lineOverlayPaint = new Paint();
        this.lineOverlayPaint.setStyle(Paint.Style.STROKE);
        this.lineOverlayPaint.setColor(-13426125);
        this.lineOverlayPaint.setStrokeWidth(this.density * 2.0f);
        this.lineOverlayPaint.setAntiAlias(true);
        this.selLinePaint = new Paint();
        this.selLinePaint.setStyle(Paint.Style.STROKE);
        this.selLinePaint.setColor(-8960939);
        this.selLinePaint.setStrokeWidth(this.density * 2.0f);
        this.selLinePaint.setAntiAlias(true);
        this.markerPaint = new Paint();
        this.markerPaint.setStyle(Paint.Style.FILL);
        this.markerPaint.setColor(-8960939);
        this.markerPaint.setAntiAlias(true);
        this.areaBasePaint = new Paint();
        this.areaBasePaint.setStyle(Paint.Style.FILL);
        this.areaOverlayPaint = new Paint();
        this.areaOverlayPaint.setStyle(Paint.Style.FILL);
        this.selAreaPaint = new Paint();
        this.selAreaPaint.setStyle(Paint.Style.FILL);
        this.labelPaint = new Paint();
        this.labelPaint.setColor(-2236963);
        this.labelPaint.setTextSize(18.0f * this.density);
        this.labelPaint.setAntiAlias(true);
        this.labelPaintBottom = this.labelPaint.getFontMetrics().bottom;
        this.markerSelectedPaint = new Paint();
        this.markerSelectedPaint.setColor(-3390379);
        this.markerSelectedPaint.setStyle(Paint.Style.FILL);
        this.markerSelectedPaint.setAntiAlias(true);
        this.markerLinePaint = new Paint();
        this.markerLinePaint.setColor(-1720232585);
        this.markerLinePaint.setPathEffect(new DashPathEffect(new float[]{4.0f * this.density, 4.0f * this.density}, 0.0f));
        this.markerLinePaint.setStrokeWidth(1.0f * this.density);
        this.markerSelectedLinePaint = new Paint();
        this.markerSelectedLinePaint.setColor(-1144241067);
        this.markerSelectedLinePaint.setStrokeWidth(this.density * 2.0f);
        this.markerLeftPath.moveTo(0.0f, 0.0f);
        this.markerLeftPath.lineTo(this.markerWidth * 0.7f, 0.0f);
        this.markerLeftPath.lineTo(this.markerWidth, this.markerHeight / 2.0f);
        this.markerLeftPath.lineTo(this.markerWidth * 0.7f, this.markerHeight);
        this.markerLeftPath.lineTo(0.0f, this.markerHeight);
        this.markerLeftPath.close();
        Matrix reflectHorizMatrix = new Matrix();
        reflectHorizMatrix.setScale(-1.0f, 1.0f, this.markerWidth / 2.0f, this.markerHeight / 2.0f);
        this.markerLeftPath.transform(reflectHorizMatrix, this.markerRightPath);
        this.vertLabelWidth = 0.0f;
        Rect bounds = new Rect();
        this.labelPaint.getTextBounds(this.horizLabelText, 0, this.horizLabelText.length(), bounds);
        this.horizLabelHeight = (float) bounds.height();
        this.horizLabelWidth = this.labelPaint.measureText(this.horizLabelText) / 2.0f;
        this.leftMargin = this.vertLabelWidth + this.markerWidth;
        this.bottomMargin = this.horizLabelHeight + (8.0f * this.density);
        this.rightMargin = this.markerWidth;
        this.topMargin = 3.0f * this.markerHeight;
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.myWidth = w;
        this.myHeight = h;
        this.graphWidth = ((float) this.myWidth) - (this.leftMargin + this.rightMargin);
        this.graphHeight = ((float) this.myHeight) - (this.topMargin + this.bottomMargin);
        this.areaBasePaint.setShader(new LinearGradient(0.0f, 0.0f, 0.0f, (float) this.myHeight, -14540237, -16318464, Shader.TileMode.CLAMP));
        this.areaOverlayPaint.setShader(new LinearGradient(0.0f, 0.0f, 0.0f, (float) this.myHeight, -11202270, -16777216, Shader.TileMode.CLAMP));
        this.selAreaPaint.setShader(new LinearGradient(0.0f, 0.0f, 0.0f, (float) this.myHeight, -8956553, -14741217, Shader.TileMode.CLAMP));
        createGraphPaths(this.counts, this.lineBasePath, this.areaBasePath, false);
        if (this.overlayEnabled) {
            createGraphPaths(this.overlayCounts, this.lineOverlayPath, this.areaOverlayPath, true);
        }
        invalidate();
    }

    private void createGraphPaths(float[] values, Path linePath, Path areaPath, boolean isOverlay) {
        if (values != null) {
            int N = values.length;
            int maxIndex = 0;
            for (int i = 1; i < N; i++) {
                if (values[i] > values[maxIndex]) {
                    maxIndex = i;
                }
            }
            if (!isOverlay) {
                this.xScale = this.graphWidth / ((float) N);
                this.yScale = this.graphHeight / values[maxIndex];
            }
            linePath.rewind();
            areaPath.rewind();
            areaPath.moveTo(this.leftMargin, (float) this.myHeight);
            for (int i2 = 0; i2 < N; i2++) {
                float x = this.leftMargin + (this.xScale * 0.5f) + (((float) i2) * this.xScale);
                float y = (this.topMargin + this.graphHeight) - (values[i2] * this.yScale);
                if (i2 == 0) {
                    linePath.moveTo(x, y);
                } else {
                    linePath.lineTo(x, y);
                }
                areaPath.lineTo(x, y);
            }
            areaPath.lineTo(this.leftMargin + (((float) N) * this.xScale), (float) this.myHeight);
            areaPath.close();
        }
    }

    static float[] convertIntFloat(int[] newValues) {
        int size = newValues.length;
        float[] floatValues = new float[size];
        for (int i = 0; i < size; i++) {
            floatValues[i] = (float) newValues[i];
        }
        return floatValues;
    }

    /* access modifiers changed from: package-private */
    public void setData(float[] newValues, int leftMark2, int rightMark2, float[] newHorizRangeMarks) {
        this.counts = newValues;
        this.horizRangeMarks = newHorizRangeMarks;
        this.labelLeftText = mapHorizRange(0);
        this.labelRightText = mapHorizRange(this.horizRangeMarks.length - 1);
        this.labelLeftWidth = this.labelPaint.measureText(this.labelLeftText) / 2.0f;
        this.labelRightWidth = this.labelPaint.measureText(this.labelRightText);
        this.leftMark = leftMark2;
        this.rightMark = rightMark2;
        createGraphPaths(this.counts, this.lineBasePath, this.areaBasePath, false);
        invalidate();
        broadcastChange();
    }

    private Divisions makeDivisions(int[] sortedIds, float[] values, boolean makeLinear, int numDivsRequested) {
        int numDivisions;
        int N = values.length;
        int origNumDivisions = numDivsRequested > 0 ? numDivsRequested : (int) Math.floor((double) (((float) this.myWidth) / (8.0f * this.density)));
        if (origNumDivisions > N) {
            numDivisions = N;
        } else {
            numDivisions = origNumDivisions;
        }
        int numElemsPerDiv = N / numDivisions;
        int[] divCount = new int[numDivisions];
        float[] horizMarks = new float[(numDivisions + 1)];
        float minVal = values[sortedIds[0]];
        float maxVal = values[sortedIds[N - 1]];
        float rangeValPerDiv = (maxVal - minVal) / ((float) numDivisions);
        int prevDivEnd = -1;
        horizMarks[0] = minVal;
        if (makeLinear) {
            for (int d = 0; d < numDivisions; d++) {
                float maxValDiv = minVal + (((float) (d + 1)) * rangeValPerDiv);
                int j = prevDivEnd + 1;
                while (j < N && values[sortedIds[j]] < maxValDiv) {
                    divCount[d] = divCount[d] + 1;
                    prevDivEnd = j;
                    j++;
                }
                horizMarks[d + 1] = values[sortedIds[prevDivEnd]];
            }
        } else {
            float upperLimit = ((float) numElemsPerDiv) * 1.6f;
            float lowerLimit = ((float) numElemsPerDiv) / 1.6f;
            float prevMaxVal = minVal;
            float prevRange = 0.0f;
            int i = 0;
            while (i < numDivisions) {
                float maxValDiv2 = prevMaxVal + (1.2f * prevRange);
                divCount[i] = 0;
                int j2 = prevDivEnd + 1;
                while (j2 < N && ((float) divCount[i]) < upperLimit && (((float) divCount[i]) < lowerLimit || values[sortedIds[j2]] < maxValDiv2)) {
                    divCount[i] = divCount[i] + 1;
                    prevDivEnd = j2;
                    j2++;
                }
                if (prevDivEnd > 0) {
                    int j3 = prevDivEnd + 1;
                    while (j3 < N && values[sortedIds[j3]] == values[sortedIds[prevDivEnd]]) {
                        divCount[i] = divCount[i] + 1;
                        prevDivEnd = j3;
                        j3++;
                    }
                    horizMarks[i + 1] = values[sortedIds[prevDivEnd]];
                } else {
                    horizMarks[i + 1] = minVal;
                }
                prevRange = horizMarks[i + 1] - prevMaxVal;
                prevMaxVal = horizMarks[i + 1];
                i++;
            }
        }
        int i2 = numDivisions - 1;
        divCount[i2] = divCount[i2] + ((N - 1) - prevDivEnd);
        horizMarks[numDivisions] = maxVal;
        int i3 = numDivisions - 1;
        while (i3 > 0 && divCount[i3] == 0) {
            i3--;
        }
        int trimmedNumDivs = i3 + 1;
        float[] trimmedHorizMarks = horizMarks;
        int[] trimmedDivCount = divCount;
        if (trimmedNumDivs != numDivisions) {
            trimmedHorizMarks = new float[(trimmedNumDivs + 1)];
            System.arraycopy(horizMarks, 0, trimmedHorizMarks, 0, trimmedNumDivs);
            trimmedHorizMarks[trimmedNumDivs] = horizMarks[numDivisions];
            trimmedDivCount = new int[trimmedNumDivs];
            System.arraycopy(divCount, 0, trimmedDivCount, 0, trimmedNumDivs);
        }
        return new Divisions(trimmedNumDivs, trimmedHorizMarks, convertIntFloat(trimmedDivCount));
    }

    /* access modifiers changed from: package-private */
    public final class Divisions {
        final float[] divCounts;
        final float[] horizMarks;
        final int numDivisions;

        public Divisions(int numDivisions2, float[] horizMarks2, float[] divMarks) {
            this.numDivisions = numDivisions2;
            this.horizMarks = horizMarks2;
            this.divCounts = divMarks;
        }
    }

    public void setData(int[] sortedIds, float[] values, float leftMarkScaled, float rightMarkScaled, String label, boolean makeLinear, int numDivsRequested) {
        this.horizLabelText = label;
        Divisions result = makeDivisions(sortedIds, values, makeLinear, numDivsRequested);
        int i = 0;
        int N = result.numDivisions;
        float[] resultMarks = result.horizMarks;
        while (i <= N && resultMarks[i] < leftMarkScaled) {
            i++;
        }
        int leftMark2 = Math.min(i, N);
        while (i <= N && resultMarks[i] < rightMarkScaled) {
            i++;
        }
        setData(result.divCounts, leftMark2, Math.min(i, N), resultMarks);
        this.overlayCounts = new float[result.numDivisions];
    }

    public void setOverlayData(float[] values) {
        int numDivs = this.counts.length;
        int len = values.length;
        int i = 0;
        int div = 0;
        while (div < numDivs) {
            this.overlayCounts[div] = 0.0f;
            while (i < len && values[i] <= this.horizRangeMarks[div + 1]) {
                float[] fArr = this.overlayCounts;
                fArr[div] = fArr[div] + 1.0f;
                i++;
            }
            div++;
        }
        createGraphPaths(this.overlayCounts, this.lineOverlayPath, this.areaOverlayPath, true);
        this.overlayEnabled = true;
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        boolean rightMarkSelected;
        if (this.counts != null) {
            canvas.drawPath(this.areaBasePath, this.areaBasePaint);
            this.leftMarkX = this.leftMargin + (((float) this.leftMark) * this.xScale);
            this.rightMarkX = this.leftMargin + (((float) this.rightMark) * this.xScale);
            if (this.overlayEnabled) {
                canvas.drawPath(this.lineOverlayPath, this.lineOverlayPaint);
            }
            canvas.save();
            canvas.clipRect(this.leftMarkX, 0.0f, this.rightMarkX, ((float) this.myHeight) - this.bottomMargin);
            canvas.drawPath(this.areaOverlayPath, this.selAreaPaint);
            canvas.drawPath(this.lineOverlayPath, this.selLinePaint);
            canvas.restore();
            boolean leftMarkSelected = (this.selectedForDragging & 1) != 0;
            canvas.save();
            canvas.translate(this.leftMarkX - this.markerWidth, this.markerHeight * 1.5f);
            canvas.drawPath(this.markerLeftPath, leftMarkSelected ? this.markerSelectedPaint : this.markerPaint);
            canvas.drawLine(this.markerWidth, 0.0f, this.markerWidth, (((float) this.myHeight) - this.bottomMargin) - (this.markerHeight * 1.5f), leftMarkSelected ? this.markerSelectedLinePaint : this.markerLinePaint);
            canvas.drawText(mapHorizRange(this.leftMark), 1.2f * this.markerWidth, (this.markerHeight + this.labelPaint.getTextSize()) / 2.0f, this.labelPaint);
            canvas.restore();
            if ((this.selectedForDragging & 2) != 0) {
                rightMarkSelected = true;
            } else {
                rightMarkSelected = false;
            }
            canvas.save();
            canvas.translate(this.rightMarkX, 0.0f);
            canvas.drawPath(this.markerRightPath, rightMarkSelected ? this.markerSelectedPaint : this.markerPaint);
            canvas.drawLine(0.0f, 0.0f, 0.0f, ((float) this.myHeight) - this.bottomMargin, rightMarkSelected ? this.markerSelectedLinePaint : this.markerLinePaint);
            String rightText = mapHorizRange(this.rightMark);
            canvas.drawText(rightText, -(this.labelPaint.measureText(rightText) + (0.2f * this.markerWidth)), (this.markerHeight + this.labelPaint.getTextSize()) / 2.0f, this.labelPaint);
            canvas.restore();
            canvas.drawText(this.labelLeftText, this.leftMargin - this.labelLeftWidth, ((float) this.myHeight) - this.labelPaintBottom, this.labelPaint);
            canvas.drawText(this.labelRightText, (this.leftMargin + this.graphWidth) - this.labelRightWidth, ((float) this.myHeight) - this.labelPaintBottom, this.labelPaint);
            canvas.drawText(this.horizLabelText, (this.leftMargin + (this.graphWidth / 2.0f)) - this.horizLabelWidth, ((float) this.myHeight) - this.labelPaintBottom, this.labelPaint);
        }
    }

    private float mapHorizRangeFloat(int mark) {
        return this.horizRangeMarks[mark];
    }

    private String mapHorizRange(int mark) {
        return String.format("%.2f", Float.valueOf(mapHorizRangeFloat(mark)));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.myWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        this.myHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(this.myWidth, this.myHeight);
    }

    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == 0) {
            float x = event.getX();
            float y = event.getY();
            float leftDist = Math.abs(x - this.leftMarkX);
            float rightDist = Math.abs(x - this.rightMarkX);
            boolean leftSat = leftDist < 2.0f * this.markerWidth;
            boolean rightSat = rightDist < 2.0f * this.markerWidth;
            boolean rightYCorrect = y <= this.markerHeight;
            boolean leftYCorrect = y > this.markerHeight && y <= 2.0f * this.markerHeight;
            boolean rightSelected = rightSat && (rightYCorrect || rightDist <= leftDist);
            boolean leftSelected = leftSat && (leftYCorrect || leftDist <= rightDist);
            this.selectedForDragging = 0;
            if (leftSelected) {
                this.selectedForDragging |= 1;
            }
            if (rightSelected) {
                this.selectedForDragging |= 2;
            }
            if (this.selectedForDragging != 0) {
                invalidate();
                return true;
            }
        } else if (action == 1) {
            this.selectedForDragging = 0;
            invalidate();
            return true;
        }
        if (action != 2 || this.selectedForDragging == 0) {
            return false;
        }
        float mark = (event.getX() - this.leftMargin) / this.xScale;
        if (this.selectedForDragging == 3) {
            this.selectedForDragging = mark < ((float) this.leftMark) ? 1 : 2;
        }
        if (this.selectedForDragging == 1) {
            setLeftMark(mark);
        } else {
            setRightMark(mark);
        }
        return true;
    }

    private void setRightMark(float f) {
        int rounded = Math.round(Math.min((float) (this.horizRangeMarks.length - 1), Math.max((float) this.leftMark, f)));
        if (this.rightMark != rounded) {
            this.rightMark = rounded;
            invalidate();
            broadcastChange();
        }
    }

    private void setLeftMark(float f) {
        int rounded = Math.round(Math.max(Math.min(f, (float) this.rightMark), 0.0f));
        if (this.leftMark != rounded) {
            this.leftMark = rounded;
            invalidate();
            broadcastChange();
        }
    }

    public void setMarkChangeListener(MarkChangeListener listener) {
        this.mListener = listener;
        broadcastChange();
    }

    /* access modifiers changed from: package-private */
    public void removeMarkChangeListener() {
        this.mListener = null;
    }

    private void broadcastChange() {
        if (this.mListener != null) {
            this.mListener.onMarkChanged(getId(), mapHorizRangeFloat(this.leftMark), mapHorizRangeFloat(this.rightMark));
        }
    }

    public void reset() {
        removeMarkChangeListener();
        this.overlayEnabled = false;
        invalidate();
    }
}
