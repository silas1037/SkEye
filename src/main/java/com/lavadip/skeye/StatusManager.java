package com.lavadip.skeye;

import android.app.Activity;
import android.util.Log;
import com.lavadip.skeye.astro.Sky;
import com.lavadip.skeye.view.FastTextView;
import java.text.DecimalFormat;
import java.text.FieldPosition;

/* access modifiers changed from: package-private */
public class StatusManager {
    private final DecimalFormat altFormat = new DecimalFormat(" 00.00°;-00.00°");
    private final DecimalFormat altShortFormat = new DecimalFormat(" 00.0°;-00.0°");
    private final DecimalFormat azmFormat = new DecimalFormat(" 000.00°;-000.00°");
    private final DecimalFormat azmShortFormat = new DecimalFormat("000.0°");
    private final DecimalFormat decFormat = new DecimalFormat(" 00.00°;-00.00°");
    private final DecimalFormat decShortFormat = new DecimalFormat(" 00.0°;-00.0°");
    private final DecimalFormat deltaFormat = new DecimalFormat("000.0°;000.0°");
    private final DecimalFormat deltaFormatArcMin = new DecimalFormat("000.0'';000.0''");
    private final FieldPosition dummyPosition = new FieldPosition(0);
    private final double[] eqCoords = new double[3];
    private final DecimalFormat hourFormat = new DecimalFormat(" 00.00h;-00.00h");
    private final DecimalFormat hourShortFormat = new DecimalFormat("00.0h");
    private final StringBuffer posStringBuffer = new StringBuffer(9);
    private FastTextView statusFOV = null;
    private FastTextView statusFinderAlt = null;
    private FastTextView statusFinderAzm = null;
    private FastTextView statusFinderDec = null;
    private FastTextView statusFinderDelta = null;
    private FastTextView statusFinderHA = null;
    private FastTextView statusFinderRA = null;
    private FastTextView statusPosAlt = null;
    private FastTextView statusPosAzm = null;
    private FastTextView statusPosDec = null;
    private FastTextView statusPosHA = null;
    private FastTextView statusPosRA = null;

    StatusManager() {
    }

    public void init(Activity a) {
        this.statusPosAlt = (FastTextView) a.findViewById(C0031R.C0032id.statusPosAlt);
        this.statusPosAzm = (FastTextView) a.findViewById(C0031R.C0032id.statusPosAzm);
        this.statusPosHA = (FastTextView) a.findViewById(C0031R.C0032id.statusPosHA);
        this.statusPosRA = (FastTextView) a.findViewById(C0031R.C0032id.statusPosRA);
        this.statusPosDec = (FastTextView) a.findViewById(C0031R.C0032id.statusPosDec);
        this.statusFinderAlt = (FastTextView) a.findViewById(C0031R.C0032id.statusFinderAlt);
        this.statusFinderAzm = (FastTextView) a.findViewById(C0031R.C0032id.statusFinderAzm);
        this.statusFinderRA = (FastTextView) a.findViewById(C0031R.C0032id.statusFinderRA);
        this.statusFinderHA = (FastTextView) a.findViewById(C0031R.C0032id.statusFinderHA);
        this.statusFinderDec = (FastTextView) a.findViewById(C0031R.C0032id.statusFinderDec);
        this.statusFinderDelta = (FastTextView) a.findViewById(C0031R.C0032id.statusFinderDelta);
        this.statusFOV = (FastTextView) a.findViewById(C0031R.C0032id.statusFOV);
    }

    private String angleToStr(DecimalFormat formatter, double angleRad) {
        this.posStringBuffer.setLength(0);
        formatter.format(Math.toDegrees(angleRad), this.posStringBuffer, this.dummyPosition);
        return this.posStringBuffer.toString();
    }

    public void showAltAz(Vector3d currZVector) {
        double currAlt = AstroUtil.computeAlt(currZVector.f16x, currZVector.f17y, currZVector.f18z);
        double currAzm = Math.atan2(currZVector.f16x, currZVector.f17y);
        if (currAzm < 0.0d) {
            currAzm += 6.283185307179586d;
        }
        try {
            this.statusPosAlt.setText(angleToStr(this.altFormat, currAlt));
            this.statusPosAzm.setText(angleToStr(this.azmFormat, currAzm));
            Sky.getEqCoords(currZVector, this.eqCoords);
            this.statusPosHA.setText(angleToStr(this.hourFormat, this.eqCoords[0] / 15.0d));
            this.statusPosRA.setText(angleToStr(this.hourFormat, this.eqCoords[1] / 15.0d));
            this.statusPosDec.setText(angleToStr(this.decFormat, this.eqCoords[2]));
        } catch (IndexOutOfBoundsException e) {
            this.statusPosAlt.setText("Alt/Azm display error. Plz report to author.");
        }
    }

    public void showHerringCoords(Vector3d herringVector) {
        double herringAlt = AstroUtil.computeAlt(herringVector.f16x, herringVector.f17y, herringVector.f18z);
        double herringAzm = Math.atan2(herringVector.f16x, herringVector.f17y);
        if (herringAzm < 0.0d) {
            herringAzm += 6.283185307179586d;
        }
        try {
            this.statusFinderAlt.setText(angleToStr(this.altShortFormat, herringAlt));
            this.statusFinderAzm.setText(angleToStr(this.azmShortFormat, herringAzm));
            Sky.getEqCoords(herringVector, this.eqCoords);
            this.statusFinderHA.setText(angleToStr(this.hourShortFormat, this.eqCoords[0] / 15.0d));
            this.statusFinderRA.setText(angleToStr(this.hourShortFormat, this.eqCoords[1] / 15.0d));
            this.statusFinderDec.setText(angleToStr(this.decShortFormat, this.eqCoords[2]));
        } catch (IndexOutOfBoundsException e) {
            Log.e("SKEYE", "index out of bounds in showHerringCoords");
        }
    }

    public void setHerringAngle(double herringAngle) {
        this.statusFinderDelta.setText(herringAngle < SkEye.ONE_DEG_IN_RADIANS ? angleToStr(this.deltaFormatArcMin, 60.0d * herringAngle) : angleToStr(this.deltaFormat, herringAngle));
    }

    public void setFov(float angle1, float angle2) {
        if (this.statusFOV != null) {
            this.statusFOV.setText(String.format("%.1fx%.1f", Float.valueOf(angle1), Float.valueOf(angle2)));
        }
    }
}
