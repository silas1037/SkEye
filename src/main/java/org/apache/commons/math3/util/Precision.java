package org.apache.commons.math3.util;

import com.lavadip.skeye.catalog.CatalogManager;
import java.math.BigDecimal;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class Precision {
    public static final double EPSILON = Double.longBitsToDouble(4368491638549381120L);
    private static final long EXPONENT_OFFSET = 1023;
    private static final long NEGATIVE_ZERO_DOUBLE_BITS = Double.doubleToRawLongBits(-0.0d);
    private static final int NEGATIVE_ZERO_FLOAT_BITS = Float.floatToRawIntBits(-0.0f);
    private static final double POSITIVE_ZERO = 0.0d;
    private static final long POSITIVE_ZERO_DOUBLE_BITS = Double.doubleToRawLongBits(0.0d);
    private static final int POSITIVE_ZERO_FLOAT_BITS = Float.floatToRawIntBits(0.0f);
    public static final double SAFE_MIN = Double.longBitsToDouble(4503599627370496L);
    private static final long SGN_MASK = Long.MIN_VALUE;
    private static final int SGN_MASK_FLOAT = Integer.MIN_VALUE;

    private Precision() {
    }

    public static int compareTo(double x, double y, double eps) {
        if (equals(x, y, eps)) {
            return 0;
        }
        if (x < y) {
            return -1;
        }
        return 1;
    }

    public static int compareTo(double x, double y, int maxUlps) {
        if (equals(x, y, maxUlps)) {
            return 0;
        }
        if (x < y) {
            return -1;
        }
        return 1;
    }

    public static boolean equals(float x, float y) {
        return equals(x, y, 1);
    }

    public static boolean equalsIncludingNaN(float x, float y) {
        if (x == x && y == y) {
            return equals(x, y, 1);
        }
        return !(((y > y ? 1 : (y == y ? 0 : -1)) != 0) ^ ((x > x ? 1 : (x == x ? 0 : -1)) != 0));
    }

    public static boolean equals(float x, float y, float eps) {
        return equals(x, y, 1) || FastMath.abs(y - x) <= eps;
    }

    public static boolean equalsIncludingNaN(float x, float y, float eps) {
        return equalsIncludingNaN(x, y) || FastMath.abs(y - x) <= eps;
    }

    public static boolean equals(float x, float y, int maxUlps) {
        int deltaPlus;
        int deltaMinus;
        boolean isEqual;
        int xInt = Float.floatToRawIntBits(x);
        int yInt = Float.floatToRawIntBits(y);
        if (((xInt ^ yInt) & SGN_MASK_FLOAT) == 0) {
            isEqual = FastMath.abs(xInt - yInt) <= maxUlps;
        } else {
            if (xInt < yInt) {
                deltaPlus = yInt - POSITIVE_ZERO_FLOAT_BITS;
                deltaMinus = xInt - NEGATIVE_ZERO_FLOAT_BITS;
            } else {
                deltaPlus = xInt - POSITIVE_ZERO_FLOAT_BITS;
                deltaMinus = yInt - NEGATIVE_ZERO_FLOAT_BITS;
            }
            if (deltaPlus > maxUlps) {
                isEqual = false;
            } else {
                isEqual = deltaMinus <= maxUlps - deltaPlus;
            }
        }
        return isEqual && !Float.isNaN(x) && !Float.isNaN(y);
    }

    public static boolean equalsIncludingNaN(float x, float y, int maxUlps) {
        if (x == x && y == y) {
            return equals(x, y, maxUlps);
        }
        return !(((y > y ? 1 : (y == y ? 0 : -1)) != 0) ^ ((x > x ? 1 : (x == x ? 0 : -1)) != 0));
    }

    public static boolean equals(double x, double y) {
        return equals(x, y, 1);
    }

    public static boolean equalsIncludingNaN(double x, double y) {
        if (x == x && y == y) {
            return equals(x, y, 1);
        }
        return !(((y > y ? 1 : (y == y ? 0 : -1)) != 0) ^ ((x > x ? 1 : (x == x ? 0 : -1)) != 0));
    }

    public static boolean equals(double x, double y, double eps) {
        return equals(x, y, 1) || FastMath.abs(y - x) <= eps;
    }

    public static boolean equalsWithRelativeTolerance(double x, double y, double eps) {
        if (equals(x, y, 1)) {
            return true;
        }
        if (FastMath.abs((x - y) / FastMath.max(FastMath.abs(x), FastMath.abs(y))) > eps) {
            return false;
        }
        return true;
    }

    public static boolean equalsIncludingNaN(double x, double y, double eps) {
        return equalsIncludingNaN(x, y) || FastMath.abs(y - x) <= eps;
    }

    public static boolean equals(double x, double y, int maxUlps) {
        long deltaPlus;
        long deltaMinus;
        boolean isEqual;
        long xInt = Double.doubleToRawLongBits(x);
        long yInt = Double.doubleToRawLongBits(y);
        if (((xInt ^ yInt) & SGN_MASK) == 0) {
            isEqual = FastMath.abs(xInt - yInt) <= ((long) maxUlps);
        } else {
            if (xInt < yInt) {
                deltaPlus = yInt - POSITIVE_ZERO_DOUBLE_BITS;
                deltaMinus = xInt - NEGATIVE_ZERO_DOUBLE_BITS;
            } else {
                deltaPlus = xInt - POSITIVE_ZERO_DOUBLE_BITS;
                deltaMinus = yInt - NEGATIVE_ZERO_DOUBLE_BITS;
            }
            if (deltaPlus > ((long) maxUlps)) {
                isEqual = false;
            } else {
                isEqual = deltaMinus <= ((long) maxUlps) - deltaPlus;
            }
        }
        if (!isEqual || Double.isNaN(x) || Double.isNaN(y)) {
            return false;
        }
        return true;
    }

    public static boolean equalsIncludingNaN(double x, double y, int maxUlps) {
        if (x == x && y == y) {
            return equals(x, y, maxUlps);
        }
        return !(((y > y ? 1 : (y == y ? 0 : -1)) != 0) ^ ((x > x ? 1 : (x == x ? 0 : -1)) != 0));
    }

    public static double round(double x, int scale) {
        return round(x, scale, 4);
    }

    public static double round(double x, int scale, int roundingMethod) {
        try {
            double rounded = new BigDecimal(Double.toString(x)).setScale(scale, roundingMethod).doubleValue();
            if (rounded == 0.0d) {
                return 0.0d * x;
            }
            return rounded;
        } catch (NumberFormatException e) {
            if (Double.isInfinite(x)) {
                return x;
            }
            return Double.NaN;
        }
    }

    public static float round(float x, int scale) {
        return round(x, scale, 4);
    }

    public static float round(float x, int scale, int roundingMethod) throws MathArithmeticException, MathIllegalArgumentException {
        float sign = FastMath.copySign(1.0f, x);
        float factor = ((float) FastMath.pow(10.0d, scale)) * sign;
        return ((float) roundUnscaled((double) (x * factor), (double) sign, roundingMethod)) / factor;
    }

    private static double roundUnscaled(double unscaled, double sign, int roundingMethod) throws MathArithmeticException, MathIllegalArgumentException {
        switch (roundingMethod) {
            case 0:
                if (unscaled != FastMath.floor(unscaled)) {
                    return FastMath.ceil(FastMath.nextAfter(unscaled, Double.POSITIVE_INFINITY));
                }
                return unscaled;
            case 1:
                return FastMath.floor(FastMath.nextAfter(unscaled, Double.NEGATIVE_INFINITY));
            case 2:
                if (sign == -1.0d) {
                    return FastMath.floor(FastMath.nextAfter(unscaled, Double.NEGATIVE_INFINITY));
                }
                return FastMath.ceil(FastMath.nextAfter(unscaled, Double.POSITIVE_INFINITY));
            case 3:
                if (sign == -1.0d) {
                    return FastMath.ceil(FastMath.nextAfter(unscaled, Double.POSITIVE_INFINITY));
                }
                return FastMath.floor(FastMath.nextAfter(unscaled, Double.NEGATIVE_INFINITY));
            case 4:
                double unscaled2 = FastMath.nextAfter(unscaled, Double.POSITIVE_INFINITY);
                if (unscaled2 - FastMath.floor(unscaled2) >= 0.5d) {
                    return FastMath.ceil(unscaled2);
                }
                return FastMath.floor(unscaled2);
            case 5:
                double unscaled3 = FastMath.nextAfter(unscaled, Double.NEGATIVE_INFINITY);
                if (unscaled3 - FastMath.floor(unscaled3) > 0.5d) {
                    return FastMath.ceil(unscaled3);
                }
                return FastMath.floor(unscaled3);
            case 6:
                double fraction = unscaled - FastMath.floor(unscaled);
                if (fraction > 0.5d) {
                    return FastMath.ceil(unscaled);
                }
                if (fraction < 0.5d) {
                    return FastMath.floor(unscaled);
                }
                if (FastMath.floor(unscaled) / 2.0d == FastMath.floor(FastMath.floor(unscaled) / 2.0d)) {
                    return FastMath.floor(unscaled);
                }
                return FastMath.ceil(unscaled);
            case CatalogManager.CATALOG_ID_SATELLITE:
                if (unscaled == FastMath.floor(unscaled)) {
                    return unscaled;
                }
                throw new MathArithmeticException();
            default:
                throw new MathIllegalArgumentException(LocalizedFormats.INVALID_ROUNDING_METHOD, Integer.valueOf(roundingMethod), "ROUND_CEILING", 2, "ROUND_DOWN", 1, "ROUND_FLOOR", 3, "ROUND_HALF_DOWN", 5, "ROUND_HALF_EVEN", 6, "ROUND_HALF_UP", 4, "ROUND_UNNECESSARY", 7, "ROUND_UP", 0);
        }
    }

    public static double representableDelta(double x, double originalDelta) {
        return (x + originalDelta) - x;
    }
}
