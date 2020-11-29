package org.apache.commons.math3.util;

import java.util.Arrays;
import org.apache.commons.math3.RealFieldElement;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.NotFiniteNumberException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public final class MathUtils {
    public static final double PI_SQUARED = 9.869604401089358d;
    public static final double TWO_PI = 6.283185307179586d;

    private MathUtils() {
    }

    public static int hash(double value) {
        return new Double(value).hashCode();
    }

    public static boolean equals(double x, double y) {
        return new Double(x).equals(new Double(y));
    }

    public static int hash(double[] value) {
        return Arrays.hashCode(value);
    }

    public static double normalizeAngle(double a, double center) {
        return a - (FastMath.floor(((3.141592653589793d + a) - center) / 6.283185307179586d) * 6.283185307179586d);
    }

    public static <T extends RealFieldElement<T>> T max(T e1, T e2) {
        return ((RealFieldElement) e1.subtract(e2)).getReal() >= 0.0d ? e1 : e2;
    }

    public static <T extends RealFieldElement<T>> T min(T e1, T e2) {
        return ((RealFieldElement) e1.subtract(e2)).getReal() >= 0.0d ? e2 : e1;
    }

    public static double reduce(double a, double period, double offset) {
        double p = FastMath.abs(period);
        return (a - (FastMath.floor((a - offset) / p) * p)) - offset;
    }

    public static byte copySign(byte magnitude, byte sign) throws MathArithmeticException {
        if (magnitude >= 0 && sign >= 0) {
            return magnitude;
        }
        if (magnitude < 0 && sign < 0) {
            return magnitude;
        }
        if (sign < 0 || magnitude != Byte.MIN_VALUE) {
            return (byte) (-magnitude);
        }
        throw new MathArithmeticException(LocalizedFormats.OVERFLOW, new Object[0]);
    }

    public static short copySign(short magnitude, short sign) throws MathArithmeticException {
        if (magnitude >= 0 && sign >= 0) {
            return magnitude;
        }
        if (magnitude < 0 && sign < 0) {
            return magnitude;
        }
        if (sign < 0 || magnitude != Short.MIN_VALUE) {
            return (short) (-magnitude);
        }
        throw new MathArithmeticException(LocalizedFormats.OVERFLOW, new Object[0]);
    }

    public static int copySign(int magnitude, int sign) throws MathArithmeticException {
        if (magnitude >= 0 && sign >= 0) {
            return magnitude;
        }
        if (magnitude < 0 && sign < 0) {
            return magnitude;
        }
        if (sign < 0 || magnitude != Integer.MIN_VALUE) {
            return -magnitude;
        }
        throw new MathArithmeticException(LocalizedFormats.OVERFLOW, new Object[0]);
    }

    public static long copySign(long magnitude, long sign) throws MathArithmeticException {
        if (magnitude >= 0 && sign >= 0) {
            return magnitude;
        }
        if (magnitude < 0 && sign < 0) {
            return magnitude;
        }
        if (sign < 0 || magnitude != Long.MIN_VALUE) {
            return -magnitude;
        }
        throw new MathArithmeticException(LocalizedFormats.OVERFLOW, new Object[0]);
    }

    public static void checkFinite(double x) throws NotFiniteNumberException {
        if (Double.isInfinite(x) || Double.isNaN(x)) {
            throw new NotFiniteNumberException(Double.valueOf(x), new Object[0]);
        }
    }

    public static void checkFinite(double[] val) throws NotFiniteNumberException {
        for (int i = 0; i < val.length; i++) {
            double x = val[i];
            if (Double.isInfinite(x) || Double.isNaN(x)) {
                throw new NotFiniteNumberException(LocalizedFormats.ARRAY_ELEMENT, Double.valueOf(x), Integer.valueOf(i));
            }
        }
    }

    public static void checkNotNull(Object o, Localizable pattern, Object... args) throws NullArgumentException {
        if (o == null) {
            throw new NullArgumentException(pattern, args);
        }
    }

    public static void checkNotNull(Object o) throws NullArgumentException {
        if (o == null) {
            throw new NullArgumentException();
        }
    }
}
