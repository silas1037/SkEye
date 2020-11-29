package com.lavadip.skeye;

import android.content.DialogInterface;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.TextView;
import com.lavadip.skeye.CustomDialog;
import com.lavadip.skeye.astro.CatalogedLocation;
import com.lavadip.skeye.astro.FreeLocation;
import com.lavadip.skeye.astro.LocationInSky;
import com.lavadip.skeye.astro.Sky;
import com.lavadip.skeye.catalog.CatalogManager;
import com.lavadip.skeye.util.Util;

/* access modifiers changed from: package-private */
public final class ViewManager {
    private static final double MIN_INSTA_ALIGN_DIST = 50.0d;
    private static final int MODE_ALIGNED = 0;
    private static final int MODE_INSTA_ALIGN = 2;
    private static final int MODE_MANUAL = 1;
    private TextView alignToObjText = null;

    /* renamed from: am */
    private final AlignManager f19am;
    private boolean autoPossible = true;
    private volatile int centeredObj = -1;
    private int currMode = 0;
    private long e1Time = 0;
    private GLSurfaceView gl3d;
    private long manualStartTime;
    private Vector3d manualStartXVec;
    private Vector3d manualStartZVec;
    private double minInstaAlignDist = MIN_INSTA_ALIGN_DIST;
    private final Orientation3d myCurrOrientation = new Orientation3d();
    private final Handler myHandler = new Handler() {
        /* class com.lavadip.skeye.ViewManager.HandlerC00551 */

        public void handleMessage(Message msg) {
            if (ViewManager.this.currMode == 1) {
                ViewManager.this.updateManualView();
                ViewManager.this.wakeUpLater();
            }
        }
    };
    private volatile int myPrevCenteredObj = -1;
    private final MyRenderer myRenderer;

    /* renamed from: om */
    private final OrientationManager f20om;
    private boolean paused = false;
    private long prevPinchStartTime = 0;
    private final float[] rotation = {1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f};
    private final float[] rotationCopy = new float[16];
    private final float[] rotationTemp = new float[16];
    private Vector3d savedPhoneZVector;
    private Vector3d savedXVec;
    private Vector3d savedZVec;
    final float screenDensity;
    private final SkEye skeye;
    private float startAngle = 0.0f;
    private float startDist = 0.0f;
    private float startFovValue = 0.0f;
    private final TimeMgr timeMgr;
    private final Vector3d touchStartVec = new Vector3d();
    private boolean twisting = false;
    private final Orientation3d workOrientation = new Orientation3d();
    private Vector3d workingXVec;
    private Vector3d workingZVec;

    ViewManager(AlignManager am, OrientationManager om, SkEye skeye2, MyRenderer myRenderer2, float screenDensity2, TimeMgr timeMgr2) {
        this.f19am = am;
        this.f20om = om;
        this.skeye = skeye2;
        this.timeMgr = timeMgr2;
        this.myRenderer = myRenderer2;
        this.screenDensity = screenDensity2;
        om.setParent(this);
        this.minInstaAlignDist = ((double) screenDensity2) * MIN_INSTA_ALIGN_DIST;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateManualView() {
        if (!this.twisting) {
            long newSkyTime = this.timeMgr.getCurrentTime();
            float angle = (float) (((double) (((float) (newSkyTime - this.manualStartTime)) / 8.616E7f)) * Math.toRadians(360.0d));
            Vector3d polarVec = Sky.getPolarVec();
            this.manualStartZVec.rotate((double) (-angle), polarVec, this.workingZVec);
            this.manualStartXVec.rotate((double) (-angle), polarVec, this.workingXVec);
            updateManualOrientation(true, Long.valueOf(newSkyTime));
        }
    }

    /* access modifiers changed from: package-private */
    public void wakeUpLater() {
        if (!this.paused) {
            this.myHandler.sendEmptyMessageDelayed(0, 550);
        }
    }

    /* access modifiers changed from: package-private */
    public void updateFov(final float factor) {
        final float oldFov = MyRenderer.getFovValue();
        this.gl3d.queueEvent(new Runnable() {
            /* class com.lavadip.skeye.ViewManager.RunnableC00562 */

            public void run() {
                MyRenderer.setFovValue(oldFov * factor);
            }
        });
        this.gl3d.requestRender();
    }

    /* access modifiers changed from: package-private */
    public boolean handleKeyEvent(KeyEvent event) {
        int action = event.getAction();
        switch (event.getKeyCode()) {
            case TimePicker.HOUR_24 /*{ENCODED_INT: 24}*/:
                if (action == 1) {
                    return true;
                }
                updateFov(0.94f);
                return true;
            case 25:
                if (action == 1) {
                    return true;
                }
                updateFov(1.06f);
                return true;
            default:
                return false;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isInstaAlignMode() {
        return this.currMode == 2;
    }

    /* access modifiers changed from: package-private */
    public synchronized void setCenteredObj(int newCenteredObj) {
        this.myPrevCenteredObj = this.centeredObj;
        this.centeredObj = newCenteredObj;
    }

    /* access modifiers changed from: package-private */
    public synchronized void alignToObjManual() {
        if (this.centeredObj > 0) {
            LocationInSky targetObj = Sky.getSkyObject(this.centeredObj);
            String okString = this.skeye.getString(17039370);
            new CustomDialog.Builder(this.skeye).setTitle(String.format(this.skeye.getString(C0031R.string.align_in_manual_mode_title_format), targetObj.getName())).setMessage(String.format(this.skeye.getString(C0031R.string.align_in_manual_mode_message_format), okString)).setPositiveButton(okString, new DialogInterface.OnClickListener() {
                /* class com.lavadip.skeye.ViewManager.DialogInterface$OnClickListenerC00573 */

                public void onClick(DialogInterface dialog, int which) {
                    ViewManager.this.myPrevCenteredObj = ViewManager.this.centeredObj;
                    dialog.dismiss();
                    ViewManager.this.f20om.getCurrRotationMatrix(ViewManager.this.rotationCopy);
                    CatalogedLocation targetObj = Sky.getSkyObject(ViewManager.this.centeredObj);
                    ViewManager.this.f19am.addAlignment(ViewManager.this.rotationCopy, targetObj);
                    if ((ViewManager.this.skeye.getApplicationContext().getApplicationInfo().flags & 2) != 0) {
                        ViewManager.debugAlignment(targetObj, "Post Manual Align: ", ViewManager.this.f20om, ViewManager.this.f19am);
                    }
                    ViewManager.this.exitManual();
                }
            }).setNegativeButton(this.skeye.getString(17039360), (DialogInterface.OnClickListener) null).setCancelable(true).create().show();
        } else {
            Util.showToast(this.skeye, C0031R.string.nothing_to_align);
        }
    }

    /* access modifiers changed from: package-private */
    public synchronized void alignToObj() {
        if (this.centeredObj < 0) {
            this.alignToObjText.setVisibility(8);
        } else if (this.centeredObj == this.myPrevCenteredObj) {
            this.f20om.getCurrRotationMatrix(this.rotationCopy);
            CatalogedLocation targetObj = Sky.getSkyObject(this.centeredObj);
            this.f19am.updateLastTarget(targetObj, this.rotationCopy);
            completeInstaAlign(true, false);
            if ((this.skeye.getApplicationContext().getApplicationInfo().flags & 2) != 0) {
                debugAlignment(targetObj, "Post Insta Align: ", this.f20om, this.f19am);
            }
        }
    }

    /* access modifiers changed from: private */
    public static void debugAlignment(CatalogedLocation targetObj, String msg, OrientationManager om, AlignManager am) {
        int objId = targetObj.f21id;
        int catalogId = CatalogManager.getCatalog(objId);
        int objNum = CatalogManager.getObjNum(objId);
        float[] eqPositions = CatalogManager.catalogs[catalogId].getVecPositions();
        Vector3d targetVec = targetObj.getVector();
        float[] rotationCopy2 = new float[16];
        om.getCurrRotationMatrixNoDec(rotationCopy2);
        Log.d("SKEYE", String.format("Added alignment: #%3d [%10s] 0x%8x %10dl (%13.10f, %13.10f, %13.10f) (%.10f, %.10f, %.10f)", Integer.valueOf(am.getNumAligns()), targetObj.getName(), Integer.valueOf(objId), Long.valueOf(System.currentTimeMillis()), Float.valueOf(eqPositions[(objNum * 3) + 0]), Float.valueOf(eqPositions[(objNum * 3) + 1]), Float.valueOf(eqPositions[(objNum * 3) + 2]), Double.valueOf(targetVec.f16x), Double.valueOf(targetVec.f17y), Double.valueOf(targetVec.f18z)));
        Log.d("SKEYE", Util.mkString(String.valueOf(msg) + " [", rotationCopy2, "]"));
    }

    private synchronized void startManual() {
        this.f20om.getCurrRotationMatrix(this.rotationTemp);
        this.f19am.getAlignedOrientationNew(this.rotationTemp, this.myCurrOrientation);
        this.skeye.remapByOrient(this.rotationTemp, this.rotationCopy);
        this.workingZVec = new Vector3d(this.rotationCopy, 2);
        this.workingXVec = new Vector3d(this.rotationCopy, 0);
        enterManual();
        wakeUpLater();
    }

    /* access modifiers changed from: package-private */
    public synchronized void switchToManual(double ra, double dec) {
        Sky.eqToRotMatrix(ra, dec, this.rotationCopy);
        this.workingZVec = new Vector3d(this.rotationCopy, 2);
        this.workingXVec = new Vector3d(this.rotationCopy, 0);
        enterManual();
        wakeUpLater();
        this.myCurrOrientation.zDir = this.workingZVec;
        updateManualOrientation(false, null);
    }

    /* access modifiers changed from: package-private */
    public synchronized void switchToManual() {
        startManual();
        this.myCurrOrientation.zDir = this.workingZVec;
        updateManualOrientation(true, null);
    }

    private void updateManualVecFromWorkingVec() {
        this.manualStartTime = Sky.getCurrentTime();
        this.manualStartZVec = new Vector3d(this.workingZVec);
        this.manualStartXVec = new Vector3d(this.workingXVec);
    }

    /* access modifiers changed from: package-private */
    public boolean onScroll(long eventTime, double dist, double angle, MotionEvent e1, MotionEvent e2) {
        if (!this.f19am.hasAligns() || this.currMode == 1) {
            if (this.currMode != 1) {
                startManual();
            }
            if (this.e1Time != eventTime) {
                this.e1Time = eventTime;
                this.savedZVec = this.workingZVec;
                this.savedXVec = this.workingXVec;
                System.arraycopy(this.rotationCopy, 0, this.rotationTemp, 0, 16);
                this.myRenderer.getProjectedVector(e1.getX(), e1.getY(), this.rotationTemp, this.touchStartVec);
            }
            Vector3d targetVec = new Vector3d();
            this.myRenderer.getProjectedVector(e2.getX(), e2.getY(), this.rotationTemp, targetVec);
            Vector3d rotAxis = this.touchStartVec.crossMult(targetVec, true);
            float rotAngle = (float) this.touchStartVec.angleBetweenMag(targetVec);
            Orientation3d orientation3d = this.myCurrOrientation;
            Vector3d rotate = this.savedZVec.rotate((double) (-rotAngle), rotAxis);
            this.workingZVec = rotate;
            orientation3d.zDir = rotate;
            this.workingXVec = this.savedXVec.rotate((double) (-rotAngle), rotAxis);
            this.timeMgr.slowDown();
            updateManualVecFromWorkingVec();
            updateManualOrientation(true, null);
            return true;
        } else if (this.currMode == 1) {
            return true;
        } else {
            this.f20om.getCurrRotationMatrix(this.rotationCopy);
            System.arraycopy(this.rotationCopy, 0, this.rotationTemp, 0, 16);
            Vector3d phoneZVector = new Vector3d(this.rotationTemp, 2);
            this.f19am.getAlignedOrientationNew(this.rotationTemp, this.myCurrOrientation);
            if (dist <= this.minInstaAlignDist && !isInstaAlignMode()) {
                return true;
            }
            if (!isInstaAlignMode()) {
                this.savedPhoneZVector = phoneZVector;
                this.f19am.addAlignment(this.rotationCopy, new FreeLocation(this.myCurrOrientation.zDir));
                enterInstaAlign();
            }
            if (isCurrentOrientationTolerable(phoneZVector)) {
                if (eventTime != this.e1Time) {
                    this.e1Time = eventTime;
                    this.savedZVec = this.myCurrOrientation.zDir;
                    this.savedXVec = new Vector3d(this.rotationTemp, 0);
                }
                this.f19am.updateLastTarget(new FreeLocation(this.savedZVec.rotate((double) (((float) dist) / (1000.0f * this.screenDensity)), this.savedZVec.crossMult(this.savedXVec.rotate((double) ((float) angle), this.savedZVec), true))), this.rotationCopy);
                return true;
            }
            cancelInstaAlign(false);
            return true;
        }
    }

    private void updateManualOrientation(boolean updateSkEye, Long newSkyTime) {
        Vector3d workingYVec = this.workingXVec.crossMult(this.workingZVec, true);
        this.rotationCopy[0] = (float) (-this.workingXVec.f16x);
        this.rotationCopy[1] = (float) (-workingYVec.f16x);
        this.rotationCopy[2] = (float) (-this.workingZVec.f16x);
        this.rotationCopy[3] = 0.0f;
        this.rotationCopy[4] = (float) (-this.workingXVec.f17y);
        this.rotationCopy[5] = (float) (-workingYVec.f17y);
        this.rotationCopy[6] = (float) (-this.workingZVec.f17y);
        this.rotationCopy[7] = 0.0f;
        this.rotationCopy[8] = (float) (-this.workingXVec.f18z);
        this.rotationCopy[9] = (float) (-workingYVec.f18z);
        this.rotationCopy[10] = (float) (-this.workingZVec.f18z);
        this.rotationCopy[11] = 0.0f;
        this.rotationCopy[12] = 0.0f;
        this.rotationCopy[13] = 0.0f;
        this.rotationCopy[14] = 0.0f;
        this.rotationCopy[15] = 1.0f;
        if (updateSkEye) {
            this.skeye.onOrientationChanged(this.rotationCopy, this.myCurrOrientation, true, newSkyTime, false);
        }
    }

    /* access modifiers changed from: package-private */
    public synchronized void checkAlignment(float[] rotation2) {
        if (isInstaAlignMode()) {
            if (!isCurrentOrientationTolerable(new Vector3d(rotation2, 2))) {
                cancelInstaAlign(false);
            } else if (this.centeredObj != this.myPrevCenteredObj) {
                this.myPrevCenteredObj = this.centeredObj;
                if (this.centeredObj >= 0) {
                    this.alignToObjText.setText(String.format(this.skeye.getString(C0031R.string.align_to), Sky.getSkyObject(this.centeredObj).getName()));
                    this.alignToObjText.setVisibility(0);
                } else {
                    this.alignToObjText.setVisibility(8);
                }
            }
        }
    }

    private boolean isCurrentOrientationTolerable(Vector3d phoneZVector) {
        return phoneZVector.angleBetweenMag(this.savedPhoneZVector) < Math.toRadians(4.0d);
    }

    private void showManualModeViews() {
        this.skeye.findViewById(C0031R.C0032id.switch_to_manual_button).setVisibility(8);
        if (this.autoPossible) {
            this.skeye.findViewById(C0031R.C0032id.align_in_manual_button).setVisibility(0);
            this.skeye.findViewById(C0031R.C0032id.manual_exit_button).setVisibility(0);
            return;
        }
        this.skeye.findViewById(C0031R.C0032id.align_in_manual_button).setVisibility(8);
        this.skeye.findViewById(C0031R.C0032id.manual_exit_button).setVisibility(8);
    }

    private synchronized void enterManual() {
        showManualModeViews();
        this.currMode = 1;
        updateManualVecFromWorkingVec();
        Util.showToast(this.skeye, C0031R.string.entering_manual_mode);
    }

    /* access modifiers changed from: package-private */
    public synchronized void exitManual() {
        this.currMode = 0;
        this.skeye.findViewById(C0031R.C0032id.switch_to_manual_button).setVisibility(0);
        this.skeye.findViewById(C0031R.C0032id.align_in_manual_button).setVisibility(8);
        this.skeye.findViewById(C0031R.C0032id.manual_exit_button).setVisibility(8);
    }

    private synchronized void enterAligned() {
        this.currMode = 0;
    }

    private synchronized void postExitInstaAlign() {
        this.skeye.findViewById(C0031R.C0032id.instaAlignGroup).setVisibility(8);
    }

    private synchronized void enterInstaAlign() {
        this.currMode = 2;
        this.skeye.findViewById(C0031R.C0032id.instaAlignGroup).setVisibility(0);
    }

    /* access modifiers changed from: package-private */
    public void completeInstaAlign(boolean added, boolean silent) {
        enterAligned();
        postExitInstaAlign();
        if (!silent) {
            Util.showToast(this.skeye, added ? C0031R.string.added_align : C0031R.string.cancelled_align);
        }
    }

    /* access modifiers changed from: package-private */
    public void cancelInstaAlign(boolean silent) {
        if (isInstaAlignMode()) {
            this.f19am.removeLastAlignment();
            completeInstaAlign(false, silent);
        }
    }

    /* access modifiers changed from: package-private */
    public void handlePinchTwist(long eventStartTime, float dist, float angle) {
        cancelInstaAlign(true);
        this.twisting = true;
        if (this.prevPinchStartTime != eventStartTime) {
            this.prevPinchStartTime = eventStartTime;
            this.startFovValue = MyRenderer.getFovValue();
            this.startDist = dist;
            this.startAngle = angle;
            this.savedXVec = this.workingXVec;
        }
        float changeRatio = this.startDist / dist;
        float sensitivity = changeRatio < 1.0f ? 0.75f : 0.65f;
        final float newFovValue = this.startFovValue * ((1.0f - sensitivity) + (sensitivity * changeRatio));
        this.gl3d.queueEvent(new Runnable() {
            /* class com.lavadip.skeye.ViewManager.RunnableC00584 */

            public void run() {
                MyRenderer.setFovValue(newFovValue);
            }
        });
        if (this.currMode != 1 || Float.isNaN(angle)) {
            this.gl3d.requestRender();
            return;
        }
        this.workingXVec = this.savedXVec.rotate((double) (-2.0f * (angle - this.startAngle)), this.workingZVec);
        this.workingXVec.normalise();
        updateManualOrientation(true, null);
    }

    /* access modifiers changed from: package-private */
    public void onOrientationChanged(boolean isRemote) {
        if (this.currMode != 1 && this.f20om.getCurrRotationMatrix(this.rotation)) {
            checkAlignment(this.rotation);
            this.f19am.getAlignedOrientationNew(this.rotation, this.workOrientation);
            this.skeye.onOrientationChanged(this.rotation, this.workOrientation, false, null, isRemote);
        }
    }

    /* access modifiers changed from: package-private */
    public synchronized void resetPinchTwist() {
        this.prevPinchStartTime = 0;
        updateManualVecFromWorkingVec();
        this.twisting = false;
    }

    public synchronized void updateViews(GLSurfaceView gl3d2, TextView alignToObjText2) {
        this.gl3d = gl3d2;
        this.alignToObjText = alignToObjText2;
        if (this.currMode == 1) {
            showManualModeViews();
        } else if (this.currMode == 2) {
            this.skeye.findViewById(C0031R.C0032id.instaAlignGroup).setVisibility(0);
        }
    }

    public void pause() {
        this.paused = true;
    }

    public void resume() {
        this.paused = false;
        wakeUpLater();
    }

    public void setAutoPossible(boolean autoPossible2) {
        this.autoPossible = autoPossible2;
    }

    public SkEye getSkeye() {
        return this.skeye;
    }
}
