package com.lavadip.skeye.astro;

import android.content.Context;
import android.content.res.Resources;
import com.lavadip.skeye.AstroUtil;
import com.lavadip.skeye.C0031R;
import com.lavadip.skeye.Directions;
import com.lavadip.skeye.Vector3d;
import com.lavadip.skeye.util.Util;
import java.text.DecimalFormat;
import java.text.FieldPosition;

public abstract class LocationInSky {
    private static final DecimalFormat degreeFormat = new DecimalFormat("###.#°");
    private static final FieldPosition dummyPos = new FieldPosition(0);
    private static final DecimalFormat magFormat = new DecimalFormat(" #0.0#;-#0.0#");
    private static String[][] objectNames;
    private static final DecimalFormat sizeFormat = new DecimalFormat("####0.#''");
    private static String sizePrefix;
    public final double altitude;
    public final double azimuth;
    private final Vector3d positionVec;

    public abstract String getDescr();

    public abstract String getFullName();

    public abstract String getName();

    public abstract float getVisMag();

    public LocationInSky(Vector3d positionVec2) {
        this.positionVec = positionVec2;
        this.altitude = Util.truncRad(AstroUtil.computeAlt(positionVec2.f16x, positionVec2.f17y, positionVec2.f18z));
        this.azimuth = Math.atan2(positionVec2.f16x, positionVec2.f17y);
    }

    public String getFuzzyLocation(boolean reallyFuzzy, StringBuffer workBuffer) {
        if (workBuffer == null) {
            workBuffer = new StringBuffer(40);
        } else {
            workBuffer.setLength(0);
        }
        if (this.altitude > 1.5706963267948966d) {
            return "Near Zenith";
        }
        double azimuth2 = Math.toDegrees(this.azimuth);
        if (azimuth2 < 0.0d) {
            azimuth2 += 360.0d;
        }
        if (azimuth2 >= 360.0d) {
            azimuth2 -= 360.0d;
        }
        int dir = ((int) (22.5d + azimuth2)) / 45;
        if (reallyFuzzy) {
            workBuffer.append("Alt: ");
            degreeFormat.format(Math.toDegrees(this.altitude), workBuffer, dummyPos);
            workBuffer.append(" Azm: ");
            degreeFormat.format(azimuth2, workBuffer, dummyPos);
            workBuffer.append(" [ ");
            workBuffer.append(Directions.dirStr[dir]);
            workBuffer.append(" ]");
            return workBuffer.toString();
        }
        return String.format(" <small>Alt %3.2f° Azm %3.2f°</small> ", Double.valueOf(Math.toDegrees(this.altitude)), Double.valueOf(azimuth2));
    }

    public Vector3d getVector() {
        return this.positionVec;
    }

    public static void init(Context ctxt) {
        Resources res = ctxt.getResources();
        String[][] strArr = new String[5][];
        strArr[1] = res.getStringArray(C0031R.array.majortype_names_star);
        strArr[2] = res.getStringArray(C0031R.array.majortype_names_star_group);
        strArr[3] = res.getStringArray(C0031R.array.majortype_names_galaxy);
        strArr[4] = res.getStringArray(C0031R.array.majortype_names_ism);
        objectNames = strArr;
        sizePrefix = " " + res.getString(C0031R.string.size) + ":";
    }

    private static String formatSize(float size) {
        return sizeFormat.format((double) size);
    }

    public static String makeDescr(int majorType, int minorType, float sbr, float mag, float sizeX, float sizeY) {
        String str;
        StringBuilder sb = new StringBuilder(String.valueOf(objectNames[majorType][minorType]));
        if (majorType == 2 && minorType == 2) {
            str = " Mag: " + magFormat.format((double) mag);
        } else {
            str = " SBr: " + magFormat.format((double) sbr);
        }
        return sb.append(str).append(sizePrefix).append(formatSize(sizeX)).append("x").append(formatSize(sizeY)).toString();
    }

    public boolean matches(String filterStr) {
        return false;
    }
}
