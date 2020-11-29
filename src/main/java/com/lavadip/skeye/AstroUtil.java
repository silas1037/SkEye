package com.lavadip.skeye;

public final class AstroUtil {
    public static final double PI_BY2 = 1.5707963267948966d;
    public static final double TWO_PI = 6.283185307179586d;

    public interface DeltaFunction {
        double apply(double d);
    }

    public static double truncRad(double f) {
        return ((((3.141592653589793d + f) % 6.283185307179586d) - 6.283185307179586d) % 6.283185307179586d) + 3.141592653589793d;
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

    public static double computeAlt(double x, double y, double z) {
        return Math.atan2(z, Math.sqrt((x * x) + (y * y)));
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

    public static void normalize(double[] f) {
        Vector3d v = new Vector3d(f[0], f[1], f[2]);
        v.normalise();
        f[0] = v.f16x;
        f[1] = v.f17y;
        f[2] = v.f18z;
    }

    public static double newtonRaphson(double x, DeltaFunction xMinusfOverfDash, int remIterations, double accuracy) {
        if (remIterations <= 0) {
            return x;
        }
        double nextX = xMinusfOverfDash.apply(x);
        if (Math.abs(nextX - x) < accuracy) {
            return nextX;
        }
        return newtonRaphson(nextX, xMinusfOverfDash, remIterations - 1, accuracy);
    }

    public static String mkString(String[] array, String infix) {
        if (array.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length - 1; i++) {
            sb.append(array[i]);
            sb.append(infix);
        }
        sb.append(array[array.length - 1]);
        return sb.toString();
    }
}
