package com.lavadip.skeye.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.lavadip.skeye.AstroUtil;
import com.lavadip.skeye.C0031R;
import com.lavadip.skeye.NightModeMgr;
import com.lavadip.skeye.Vector3d;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public final class Util {
    public static final double PI_BY2 = 1.5707963267948966d;
    private static final float TO_RADIANS = 0.017453292f;
    public static final double TWO_PI = 6.283185307179586d;
    private static final int[] toastBgResMap = {C0031R.drawable.toast_bg, C0031R.drawable.toast_bg_dusk, C0031R.drawable.toast_bg_night};
    private static final int[] toastColorMap = {C0031R.color.toast_day_fg, C0031R.color.toast_dusk_fg, C0031R.color.toast_night_fg};
    final char[] builder = new char[9];

    public static float toRadians(float degrees) {
        return TO_RADIANS * degrees;
    }

    public static int chooseColorOffset(int arrayLen, int themeOrdinal) {
        return (arrayLen / 3) * themeOrdinal;
    }

    public static void map3d(float ra, float dec, float[] dest, int offset, float scale) {
        float cosDec = (float) Math.cos((double) dec);
        float x = cosDec * ((float) Math.sin((double) ra));
        float y = (float) Math.sin((double) dec);
        float z = cosDec * ((float) Math.cos((double) ra));
        dest[offset] = x * scale;
        dest[offset + 1] = y * scale;
        dest[offset + 2] = z * scale;
    }

    public static Vector3d map3d(double ra, double dec) {
        double cosDec = Math.cos(dec);
        return new Vector3d(cosDec * Math.sin(ra), Math.sin(dec), cosDec * Math.cos(ra));
    }

    public static void map3d(double ra, double dec, FloatBuffer dest, float scale) {
        double cosDec = Math.cos(dec);
        double x = cosDec * Math.sin(ra);
        double y = Math.sin(dec);
        double z = cosDec * Math.cos(ra);
        dest.put((float) (((double) scale) * x));
        dest.put((float) (((double) scale) * y));
        dest.put((float) (((double) scale) * z));
    }

    static float rootMeanSquareBuffer(float target, float values, float buffer) {
        float t0 = target + 2000.0f;
        float v0 = values + 2000.0f;
        return ((float) Math.sqrt((double) ((((t0 * t0) * buffer) + (v0 * v0)) * (1.0f / (1.0f + buffer))))) - 2000.0f;
    }

    static void rootMeanSquareBuffer(float[] target, float[] values, float buffer) {
        float invBuffer = 1.0f / (1.0f + buffer);
        float t0 = target[0] + 2000.0f;
        float t1 = target[1] + 2000.0f;
        float t2 = target[2] + 2000.0f;
        float v0 = values[0] + 2000.0f;
        float v1 = values[1] + 2000.0f;
        float v2 = values[2] + 2000.0f;
        target[0] = ((float) Math.sqrt((double) ((((t0 * t0) * buffer) + (v0 * v0)) * invBuffer))) - 2000.0f;
        target[1] = ((float) Math.sqrt((double) ((((t1 * t1) * buffer) + (v1 * v1)) * invBuffer))) - 2000.0f;
        target[2] = ((float) Math.sqrt((double) ((((t2 * t2) * buffer) + (v2 * v2)) * invBuffer))) - 2000.0f;
    }

    public static double truncRad(double f) {
        return ((((3.141592653589793d + f) % 6.283185307179586d) - 6.283185307179586d) % 6.283185307179586d) + 3.141592653589793d;
    }

    /* access modifiers changed from: package-private */
    public StringBuilder degreeFormater(double d) {
        boolean neg;
        char c;
        int sign = 1;
        if (d < 0.0d) {
            neg = true;
        } else {
            neg = false;
        }
        if (neg) {
            sign = -1;
        }
        char[] cArr = this.builder;
        if (neg) {
            c = '-';
        } else {
            c = ' ';
        }
        cArr[0] = c;
        this.builder[7] = 176;
        int intPart = (int) (((double) sign) * d);
        int count = 3;
        while (intPart > 0 && count > 0) {
            this.builder[count] = (char) ((intPart % 10) + 48);
            intPart /= 10;
            count--;
        }
        while (count > 0) {
            this.builder[count] = ' ';
            count--;
        }
        StringBuilder strBuilder = new StringBuilder(8);
        strBuilder.append(this.builder);
        Log.d("SKEYE", "Builder: " + ((Object) strBuilder));
        return strBuilder;
    }

    public static double makeAnglePositive(double angleRA) {
        while (angleRA < 0.0d) {
            angleRA += 6.283185307179586d;
        }
        while (angleRA > 6.283185307179586d) {
            angleRA -= 6.283185307179586d;
        }
        return angleRA;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {
            int totalHeight = 0;
            int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), Integer.MIN_VALUE);
            int numChildren = listAdapter.getCount();
            for (int i = 0; i < numChildren; i++) {
                View listItem = listAdapter.getView(i, null, listView);
                listItem.measure(desiredWidth, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = Math.max(0, (listView.getDividerHeight() * (numChildren - 1)) + totalHeight);
            Log.d("SKEYE", "Num children: " + numChildren);
            Log.d("SKEYE", "Setting height to " + params.height);
            listView.setLayoutParams(params);
            listView.requestLayout();
        }
    }

    public static ShortBuffer setLineIndex(short[] lineIndices) {
        ShortBuffer retVal = ByteBuffer.allocateDirect(lineIndices.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
        retVal.position(0);
        retVal.put(lineIndices);
        retVal.position(0);
        return retVal;
    }

    public static void vectorToRotMatrix(Vector3d targetVect, float[] rotation) {
        setRotateEulerMXY(rotation, Math.atan2(targetVect.f17y, targetVect.f16x) - 1.5707963267948966d, 1.5707963267948966d + AstroUtil.computeAlt(targetVect.f16x, targetVect.f17y, targetVect.f18z));
    }

    public static void setRotateEulerMXY(float[] rm, double zRotation, double xRotation) {
        float cx = (float) Math.cos(xRotation);
        float sx = (float) Math.sin(xRotation);
        float cz = (float) Math.cos(zRotation);
        float sz = (float) Math.sin(zRotation);
        rm[0] = cz;
        rm[1] = (-sz) * cx;
        rm[2] = sz * sx;
        rm[3] = 0.0f;
        rm[4] = sz;
        rm[5] = cx * cz;
        rm[6] = (-sx) * cz;
        rm[7] = 0.0f;
        rm[8] = 0.0f;
        rm[9] = sx;
        rm[10] = cx;
        rm[11] = 0.0f;
        rm[12] = 0.0f;
        rm[13] = 0.0f;
        rm[14] = 0.0f;
        rm[15] = 1.0f;
    }

    public static String showRA(double raRad) {
        double raH = Math.toDegrees(raRad) / 15.0d;
        int raInt = (int) raH;
        double raMin = (raH - ((double) raInt)) * 60.0d;
        int raMinInt = (int) raMin;
        return raInt + "h" + raMinInt + "m" + ((raMin - ((double) raMinInt)) * 60.0d) + "s";
    }

    public static String showDec(double decRad) {
        double decDeg = Math.toDegrees(decRad);
        int decInt = (int) decDeg;
        double decMin = (decDeg - ((double) decInt)) * 60.0d;
        int decMinInt = (int) decMin;
        return decInt + "d" + decMinInt + "'" + ((decMin - ((double) decMinInt)) * 60.0d) + "\"";
    }

    public static void showToast(Context cntxt, String s) {
        int themeOrdinal = NightModeMgr.getCurrentThemeOrdinal(cntxt);
        Resources resources = cntxt.getResources();
        int toastTextColor = resources.getColor(toastColorMap[themeOrdinal]);
        int padding = (int) (10.0f * resources.getDisplayMetrics().density);
        TextView txtView = new TextView(cntxt);
        txtView.setText(s);
        txtView.setBackgroundResource(toastBgResMap[themeOrdinal]);
        txtView.setTextColor(toastTextColor);
        txtView.setPadding(padding, padding, padding, padding);
        Toast toast = new Toast(cntxt);
        toast.setDuration(0);
        toast.setView(txtView);
        toast.show();
    }

    public static void showToast(Context cntxt, int resId) {
        showToast(cntxt, cntxt.getString(resId));
    }

    public static String mkString(float[] array) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);
            if (i != array.length) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    public static String mkString(String prefix, float[] array, String suffix) {
        return String.valueOf(prefix) + mkString(array) + suffix;
    }

    public static void rotateMatrixAroundZ(float[] m, int offset, double angle) {
        float c = (float) Math.cos(angle);
        float s = (float) Math.sin(angle);
        for (int i = offset; i < offset + 4; i++) {
            float x = m[i];
            float y = m[i + 4];
            m[i] = (c * x) + (s * y);
            m[i + 4] = (c * y) - (s * x);
        }
    }

    public static void rotateMatrixAroundX(float[] m, int offset, double angle) {
        float c = (float) Math.cos(angle);
        float s = (float) Math.sin(angle);
        for (int i = offset + 4; i < offset + 8; i++) {
            float x = m[i];
            float y = m[i + 4];
            m[i] = (c * x) + (s * y);
            m[i + 4] = (c * y) - (s * x);
        }
    }

    public static void rotateMatrixAroundY(float[] m, int offset, double angle) {
        float c = (float) Math.cos(angle);
        float s = (float) Math.sin(angle);
        for (int i = offset; i < offset + 4; i++) {
            float x = m[i];
            float y = m[i + 8];
            m[i] = (c * x) - (s * y);
            m[i + 8] = (c * y) + (s * x);
        }
    }

    public static int nextPowOf2(int n) {
        int n2 = n - 1;
        int n3 = n2 | (n2 >> 1);
        int n4 = n3 | (n3 >> 2);
        int n5 = n4 | (n4 >> 4);
        int n6 = n5 | (n5 >> 8);
        int n7 = n6 | (n6 >> 16);
        int i = n7 + 1;
        return n7;
    }

    public static void readFloatsFromStream(int n, ByteBuffer readBuffer, DataInputStream stream, FloatBuffer outBuffer) throws IOException {
        byte[] buffer = readBuffer.array();
        int BLOCK_SIZE = buffer.length;
        int remaining = n;
        while (remaining > 0) {
            if (remaining >= BLOCK_SIZE) {
                stream.readFully(buffer);
                outBuffer.put(readBuffer.asFloatBuffer());
                remaining -= BLOCK_SIZE;
            } else {
                stream.readFully(buffer, 0, remaining);
                readBuffer.limit(remaining);
                outBuffer.put(readBuffer.asFloatBuffer());
                remaining = 0;
            }
        }
        readBuffer.limit(readBuffer.capacity());
    }

    public static final class Tuple2<T1, T2> {

        /* renamed from: _1 */
        final T1 f98_1;

        /* renamed from: _2 */
        final T2 f99_2;

        public Tuple2(T1 t1, T2 t2) {
            this.f98_1 = t1;
            this.f99_2 = t2;
        }
    }

    public static final class Tuple3<T1, T2, T3> {

        /* renamed from: _1 */
        final T1 f100_1;

        /* renamed from: _2 */
        final T2 f101_2;

        /* renamed from: _3 */
        final T3 f102_3;

        public Tuple3(T1 t1, T2 t2, T3 t3) {
            this.f100_1 = t1;
            this.f101_2 = t2;
            this.f102_3 = t3;
        }
    }
}
