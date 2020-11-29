package com.lavadip.skeye.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import com.lavadip.skeye.C0031R;
import com.lavadip.skeye.Directions;
import com.lavadip.skeye.NightModeMgr;
import com.lavadip.skeye.Orientation3d;
import com.lavadip.skeye.QuickSettingsManager;
import com.lavadip.skeye.SkEye;
import com.lavadip.skeye.Vector3d;

public final class FinderView extends View {
    private static final int BASE_SIZE = 180;
    private static final int MAX_DIR_LABEL_ANGLE = 135;
    private static final int MIN_DIR_LABEL_ANGLE = 45;
    private static final int TELRAD_COLOR = -1118482;
    private static final int TELRAD_LABEL_COLOR = -1;
    private static final Vector3d[] directionVecs = {new Vector3d(0.0f, 1.0f, 0.0f), new Vector3d(0.0f, -1.0f, 0.0f), new Vector3d(1.0f, 0.0f, 0.0f), new Vector3d(-1.0f, 0.0f, 0.0f), new Vector3d(0.0f, 0.0f, -1.0f), new Vector3d(0.0f, 0.0f, 1.0f)};
    private static final String finderAlphaKey = "finderAlpha";
    private static final String telradAlphaKey = "telradAlpha";
    private final int CIRCLE_THICKNESS;
    private final int SIZE;
    private final int SIZE_BY2;
    private final int SIZE_CIRCLE;
    private final int SIZE_POINTER;
    private float animateCoordAngle;
    private float animateCoordScale;
    private final Paint circlePaint;
    private final Path coPointer;
    private final Paint coordsNightPaint;
    private final Paint coordsPaint;
    private int corrLabelOrient;
    private float density;
    private final PointerAngles[] directionAngles;
    private float finderAlpha;
    private boolean findingMode;
    private float fov;
    private PointerAngles herringAngles;
    private volatile float[] herringCoords;
    private volatile float herringDist;
    private volatile boolean herringVisible;
    private boolean isClose;
    private boolean isVeryClose;
    private int myHeight;
    private float myHeightBy2;
    private int myWidth;
    private float myWidthBy2;
    private boolean nightMode;
    private final Path pointer;
    private final Path pointer180;
    private final Path pointer270;
    private final Path pointer90;
    private final Paint pointerPaint;
    private final Vector3d projHerring;
    private float signCoordScale;
    private final Paint telradLabelPaint;
    private final Paint telradPaint;

    public void setTheme(NightModeMgr.Theme theme) {
        this.nightMode = theme == NightModeMgr.Theme.Night;
        invalidate();
    }

    public void setFindingMode(boolean finding) {
        this.findingMode = finding;
        invalidate();
    }

    public FinderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.nightMode = false;
        this.findingMode = false;
        this.pointerPaint = new Paint();
        this.circlePaint = new Paint();
        this.telradPaint = new Paint();
        this.telradLabelPaint = new Paint();
        this.coordsPaint = new Paint();
        this.coordsNightPaint = new Paint();
        this.pointer = new Path();
        this.pointer90 = new Path();
        this.pointer180 = new Path();
        this.pointer270 = new Path();
        this.coPointer = new Path();
        this.fov = 1.0f;
        this.animateCoordAngle = 0.0f;
        this.animateCoordScale = 1.0f;
        this.signCoordScale = 1.0f;
        this.finderAlpha = 0.0f;
        this.herringVisible = false;
        this.herringCoords = null;
        this.herringDist = 100000.0f;
        this.projHerring = new Vector3d();
        this.directionAngles = new PointerAngles[directionVecs.length];
        this.corrLabelOrient = 0;
        this.density = context.getResources().getDisplayMetrics().density;
        this.SIZE = (int) (180.0f * this.density);
        this.SIZE_BY2 = this.SIZE / 2;
        this.SIZE_POINTER = (int) (25.0f * this.density);
        this.SIZE_CIRCLE = this.SIZE - this.SIZE_POINTER;
        this.CIRCLE_THICKNESS = (int) (10.0f * this.density);
        init();
    }

    public FinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.nightMode = false;
        this.findingMode = false;
        this.pointerPaint = new Paint();
        this.circlePaint = new Paint();
        this.telradPaint = new Paint();
        this.telradLabelPaint = new Paint();
        this.coordsPaint = new Paint();
        this.coordsNightPaint = new Paint();
        this.pointer = new Path();
        this.pointer90 = new Path();
        this.pointer180 = new Path();
        this.pointer270 = new Path();
        this.coPointer = new Path();
        this.fov = 1.0f;
        this.animateCoordAngle = 0.0f;
        this.animateCoordScale = 1.0f;
        this.signCoordScale = 1.0f;
        this.finderAlpha = 0.0f;
        this.herringVisible = false;
        this.herringCoords = null;
        this.herringDist = 100000.0f;
        this.projHerring = new Vector3d();
        this.directionAngles = new PointerAngles[directionVecs.length];
        this.corrLabelOrient = 0;
        this.density = context.getResources().getDisplayMetrics().density;
        this.SIZE = (int) (180.0f * this.density);
        this.SIZE_BY2 = this.SIZE / 2;
        this.SIZE_POINTER = (int) (25.0f * this.density);
        this.SIZE_CIRCLE = this.SIZE - this.SIZE_POINTER;
        this.CIRCLE_THICKNESS = (int) (10.0f * this.density);
        init();
    }

    private void init() {
        this.circlePaint.setColor(-1717986919);
        this.circlePaint.setStyle(Paint.Style.STROKE);
        this.circlePaint.setStrokeWidth((float) this.CIRCLE_THICKNESS);
        this.circlePaint.setAntiAlias(true);
        this.coordsPaint.setColor(-5592321);
        this.coordsPaint.setStyle(Paint.Style.FILL);
        this.coordsPaint.setAntiAlias(true);
        this.coordsNightPaint.setColor(-13312);
        this.coordsNightPaint.setStyle(Paint.Style.FILL);
        this.coordsNightPaint.setAntiAlias(true);
        this.pointerPaint.setColor(-1717986919);
        this.pointerPaint.setStyle(Paint.Style.FILL);
        this.pointerPaint.setAntiAlias(true);
        this.pointer.moveTo(0.0f, (float) (this.SIZE / 2));
        this.pointer.lineTo((float) this.SIZE_POINTER, (float) (this.SIZE_BY2 - this.SIZE_POINTER));
        this.pointer.lineTo((float) this.SIZE_POINTER, (float) (this.SIZE_BY2 + this.SIZE_POINTER));
        this.pointer.close();
        if (!isInEditMode()) {
            Matrix pointerRotMatrix = new Matrix();
            pointerRotMatrix.setRotate(90.0f, (float) this.SIZE_BY2, (float) this.SIZE_BY2);
            this.pointer.transform(pointerRotMatrix, this.pointer90);
            pointerRotMatrix.setRotate(180.0f, (float) this.SIZE_BY2, (float) this.SIZE_BY2);
            this.pointer.transform(pointerRotMatrix, this.pointer180);
            pointerRotMatrix.setRotate(270.0f, (float) this.SIZE_BY2, (float) this.SIZE_BY2);
            this.pointer.transform(pointerRotMatrix, this.pointer270);
            this.coPointer.moveTo(this.density * 5.0f, 0.0f);
            this.coPointer.lineTo(this.density * 20.0f, -5.0f * this.density);
            this.coPointer.lineTo(this.density * 20.0f, this.density * 5.0f);
            this.coPointer.close();
            pointerRotMatrix.setRotate(90.0f, 0.0f, 0.0f);
            this.coPointer.addPath(this.coPointer, pointerRotMatrix);
            pointerRotMatrix.setRotate(180.0f, 0.0f, 0.0f);
            this.coPointer.addPath(this.coPointer, pointerRotMatrix);
            this.telradPaint.setColor(TELRAD_COLOR);
            this.telradPaint.setTextSize(this.density * 12.0f);
            this.telradPaint.setStyle(Paint.Style.STROKE);
            this.telradPaint.setStrokeWidth(2.0f);
            this.telradPaint.setAntiAlias(true);
            this.telradLabelPaint.setColor(-1);
            this.telradLabelPaint.setTextSize(this.density * 12.0f);
            this.telradLabelPaint.setAntiAlias(true);
        }
    }

    public void initViews(SkEye skeye) {
        setTelradAlpha(getCurrTelradAlpha(skeye));
        setFinderAlpha(getCurrFinderAlpha(skeye));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.myWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        this.myHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        this.myWidthBy2 = ((float) this.myWidth) / 2.0f;
        this.myHeightBy2 = ((float) this.myHeight) / 2.0f;
        setMeasuredDimension(this.myWidth, this.myHeight);
    }

    public void setFov(float fov2) {
        this.fov = fov2;
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        float telradSize;
        if (this.findingMode && this.herringAngles != null) {
            drawFinder(canvas);
        }
        float labelAngle = (float) (this.corrLabelOrient * 90);
        if (this.fov > 20.0f) {
            telradSize = 16.0f;
        } else if (this.fov > 10.0f) {
            telradSize = 8.0f;
        } else if (this.fov > 4.8f) {
            telradSize = 4.0f;
        } else if (this.fov > 2.4f) {
            telradSize = 2.0f;
        } else if (this.fov > 1.2f) {
            telradSize = 1.0f;
        } else if (this.fov > 0.56f) {
            telradSize = 0.5f;
        } else if (this.fov > 0.3f) {
            telradSize = 0.25f;
        } else {
            telradSize = 0.125f;
        }
        this.telradLabelPaint.setTextAlign(Paint.Align.RIGHT);
        drawTelradCircle(telradSize, canvas, labelAngle);
        drawTelradCircle(telradSize / 2.0f, canvas, labelAngle);
        this.telradLabelPaint.setTextAlign(Paint.Align.CENTER);
        float labelHeight = this.telradLabelPaint.getTextSize();
        float yOffset = this.corrLabelOrient == 0 ? labelHeight / 2.0f : 0.0f;
        if (this.directionAngles[0] != null) {
            float radius = (this.myWidthBy2 * telradSize) / this.fov;
            int i = this.directionAngles.length;
            while (true) {
                i--;
                if (i > 0) {
                    if (this.directionAngles[i].dAngle > 45.0d && this.directionAngles[i].dAngle < 135.0d) {
                        String label = Directions.dirAbbrvs[i];
                        float dist = Math.max(labelHeight, this.telradLabelPaint.measureText(label));
                        float x = (float) (((double) this.myWidthBy2) - (((double) (radius + dist)) * Math.cos(this.directionAngles[i].oAngleRad)));
                        float y = (float) (((double) this.myHeightBy2) - (((double) (radius + dist)) * Math.sin(this.directionAngles[i].oAngleRad)));
                        canvas.save(1);
                        canvas.rotate(labelAngle, x, y);
                        canvas.drawText(label, x, y + yOffset, this.telradLabelPaint);
                        canvas.restore();
                    }
                } else {
                    return;
                }
            }
        }
    }

    private void drawTelradCircle(float angleDeg, Canvas canvas, float labelAngle) {
        String angleStr;
        float radius = (this.myWidthBy2 * angleDeg) / this.fov;
        float x = (this.myWidthBy2 + radius) - 2.0f;
        float y = this.myHeightBy2;
        canvas.drawCircle(this.myWidthBy2, this.myHeightBy2, radius, this.telradPaint);
        canvas.save(1);
        canvas.rotate(labelAngle, x, y);
        if (angleDeg >= 1.0f) {
            angleStr = String.format("%d°", Integer.valueOf((int) angleDeg));
        } else {
            angleStr = String.format("%d'", Integer.valueOf((int) (60.0f * angleDeg)));
        }
        canvas.drawText(angleStr, x, y, this.telradLabelPaint);
        canvas.restore();
    }

    public void setFinderAlpha(float newAlpha) {
        this.finderAlpha = newAlpha;
        int finderAlphaInt = (int) (255.0f * newAlpha);
        this.coordsNightPaint.setAlpha(finderAlphaInt);
        this.coordsPaint.setAlpha(finderAlphaInt);
    }

    public void setTelradAlpha(float newAlpha) {
        int telradAlpha = (int) (255.0f * newAlpha);
        this.telradPaint.setAlpha(telradAlpha);
        this.telradLabelPaint.setAlpha(telradAlpha);
    }

    private void drawFinder(Canvas canvas) {
        canvas.save(1);
        int redColor = Math.max(this.nightMode ? BASE_SIZE : 0, (int) ((this.herringAngles.dAngle / 180.0d) * 255.0d));
        int greenColor = (int) ((this.nightMode ? 0.2d : 1.0d) * ((double) (255 - redColor)));
        int blueColor = this.nightMode ? 0 : this.isClose ? 17 : 0;
        int circleAlpha = ((int) (((float) ((!this.isVeryClose || this.nightMode) ? this.isClose ? this.nightMode ? 234 : 202 : 255 : 163)) * this.finderAlpha)) << 24;
        this.circlePaint.setColor((redColor << 16) | circleAlpha | (greenColor << 8) | blueColor);
        float animateScale = this.isVeryClose ? 2.0f - (this.herringDist / ((float) this.SIZE)) : 1.0f;
        canvas.translate(this.myWidthBy2 - ((float) this.SIZE_BY2), this.myHeightBy2 - ((float) this.SIZE_BY2));
        canvas.scale(animateScale, animateScale, (float) this.SIZE_BY2, (float) this.SIZE_BY2);
        if (!this.isVeryClose) {
            this.pointerPaint.setColor((redColor << 16) | circleAlpha | (greenColor << 8) | blueColor);
            if (this.herringAngles.dAngle < 178.0d) {
                canvas.rotate(this.herringAngles.oAngle, (float) this.SIZE_BY2, (float) this.SIZE_BY2);
            } else {
                canvas.drawPath(this.pointer90, this.pointerPaint);
                canvas.drawPath(this.pointer180, this.pointerPaint);
                canvas.drawPath(this.pointer270, this.pointerPaint);
            }
            canvas.drawPath(this.pointer, this.pointerPaint);
        }
        canvas.drawCircle((float) this.SIZE_BY2, (float) this.SIZE_BY2, (float) (((this.SIZE_CIRCLE / 2) - (this.CIRCLE_THICKNESS / 2)) - (this.SIZE_POINTER / 2)), this.circlePaint);
        canvas.restore();
        if (this.herringVisible) {
            canvas.save(1);
            canvas.translate(this.herringCoords[0], ((float) this.myHeight) - this.herringCoords[1]);
            canvas.rotate(this.animateCoordAngle);
            this.animateCoordAngle += 4.0f;
            if (!this.isClose) {
                canvas.scale(this.animateCoordScale, this.animateCoordScale);
                this.animateCoordScale += this.signCoordScale * 0.1f;
                if (this.animateCoordScale > 1.5f) {
                    this.signCoordScale = -4.0f;
                } else if (((double) this.animateCoordScale) < 0.8d) {
                    this.signCoordScale = 1.0f;
                }
            }
            canvas.drawPath(this.coPointer, this.nightMode ? this.coordsNightPaint : this.coordsPaint);
            canvas.restore();
        }
    }

    private float computeDist(float[] coords) {
        float x = coords[0] - this.myWidthBy2;
        float y = coords[1] - this.myHeightBy2;
        return (float) Math.sqrt((double) ((x * x) + (y * y)));
    }

    /* access modifiers changed from: private */
    public final class PointerAngles {
        final double dAngle;
        final double dAngleRad;
        private final float oAngle;
        final double oAngleRad;

        PointerAngles(Vector3d vec, Orientation3d currOrientation) {
            Vector3d zDir = currOrientation.zDir;
            Vector3d xDir = currOrientation.xDir;
            this.dAngleRad = vec.angleBetweenMag(zDir);
            vec.rotateAwayFrom((double) ((float) (1.5707963267948966d - this.dAngleRad)), zDir, FinderView.this.projHerring);
            this.oAngleRad = FinderView.this.projHerring.angleBetweenMag(xDir) * (FinderView.this.projHerring.angleBetweenMag(zDir.crossMult(xDir, true)) < 1.5707963267948966d ? 1.0d : -1.0d);
            this.oAngle = (float) Math.toDegrees(this.oAngleRad);
            this.dAngle = Math.toDegrees(this.dAngleRad);
        }
    }

    public double setHerringCoords(boolean herringVisible2, float[] herringCoords2, Vector3d herringVector, Orientation3d currOrientation, int currOrientInt, int dispOrnt) {
        boolean z;
        boolean z2 = true;
        int i = directionVecs.length;
        while (true) {
            i--;
            if (i <= 0) {
                break;
            }
            this.directionAngles[i] = new PointerAngles(directionVecs[i], currOrientation);
        }
        this.corrLabelOrient = (dispOrnt - currOrientInt) % 4;
        if (this.corrLabelOrient < 0) {
            this.corrLabelOrient += 4;
        }
        if (this.corrLabelOrient == 2) {
            this.corrLabelOrient = 0;
        }
        double resAngle = 0.0d;
        if (herringVector != null) {
            this.herringCoords = herringCoords2;
            this.herringVisible = herringVisible2;
            this.herringDist = herringVisible2 ? computeDist(herringCoords2) : (float) (this.myHeight * this.myWidth);
            if (!herringVisible2 || this.herringDist >= ((float) this.SIZE)) {
                z = false;
            } else {
                z = true;
            }
            this.isClose = z;
            if (!herringVisible2 || this.herringDist >= ((float) this.SIZE_BY2)) {
                z2 = false;
            }
            this.isVeryClose = z2;
            this.herringAngles = new PointerAngles(herringVector, currOrientation);
            resAngle = this.herringAngles.dAngleRad;
        }
        invalidate();
        return resAngle;
    }

    private static float getCurrTelradAlpha(SkEye skeye) {
        return skeye.settingsManager.getQuickPref(telradAlphaKey, 0.2f);
    }

    private static float getCurrFinderAlpha(SkEye skeye) {
        return skeye.settingsManager.getQuickPref(finderAlphaKey, 0.2f);
    }

    public QuickSettingsManager.QuickSettingsGroup mkQuickSettingGroup(SkEye skeye) {
        return new QuickSettingsManager.QuickSettingsGroup(new QuickSettingsManager.SettingDetails[]{new QuickSettingsManager.SettingDetails<>(new QuickSettingsManager.FloatRangeQuickSetting(telradAlphaKey, skeye.getString(C0031R.string.telrad_opacity), "", 0.0f, 1.0f, 0.02f), new QuickSettingsManager.TypicalSettingChangeHandler<Float>(skeye) {
            /* class com.lavadip.skeye.view.FinderView.C01361 */

            @Override // com.lavadip.skeye.QuickSettingsManager.SettingChangeListener
            public /* bridge */ /* synthetic */ void onChange(String str, Object obj, boolean z) {
                onChange(str, (Float) obj, z);
            }

            @Override // com.lavadip.skeye.QuickSettingsManager.TypicalSettingChangeHandler
            public void onUIThread(String key, Float newValue, boolean trackingStopped) {
                FinderView.this.setTelradAlpha(newValue.floatValue());
            }

            @Override // com.lavadip.skeye.QuickSettingsManager.TypicalSettingChangeHandler
            public void onGLThread(String key, Float newValue, boolean trackingStopped) {
            }
        }, Float.valueOf(getCurrTelradAlpha(skeye))), new QuickSettingsManager.SettingDetails<>(new QuickSettingsManager.FloatRangeQuickSetting(finderAlphaKey, skeye.getString(C0031R.string.finder_opacity), "", 0.0f, 1.0f, 0.02f), new QuickSettingsManager.TypicalSettingChangeHandler<Float>(skeye) {
            /* class com.lavadip.skeye.view.FinderView.C01372 */

            @Override // com.lavadip.skeye.QuickSettingsManager.SettingChangeListener
            public /* bridge */ /* synthetic */ void onChange(String str, Object obj, boolean z) {
                onChange(str, (Float) obj, z);
            }

            @Override // com.lavadip.skeye.QuickSettingsManager.TypicalSettingChangeHandler
            public void onUIThread(String key, Float newValue, boolean trackingStopped) {
                FinderView.this.setFinderAlpha(newValue.floatValue());
            }

            @Override // com.lavadip.skeye.QuickSettingsManager.TypicalSettingChangeHandler
            public void onGLThread(String key, Float newValue, boolean trackingStopped) {
            }
        }, Float.valueOf(getCurrFinderAlpha(skeye)))}, skeye.getString(C0031R.string.general), "general");
    }
}
