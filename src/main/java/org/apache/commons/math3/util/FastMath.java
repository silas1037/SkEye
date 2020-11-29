package org.apache.commons.math3.util;

import java.io.PrintStream;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class FastMath {
    private static final double[] CBRTTWO = {0.6299605249474366d, 0.7937005259840998d, 1.0d, 1.2599210498948732d, 1.5874010519681994d};
    private static final double[] COSINE_TABLE_A = {1.0d, 0.9921976327896118d, 0.9689123630523682d, 0.9305076599121094d, 0.8775825500488281d, 0.8109631538391113d, 0.7316888570785522d, 0.6409968137741089d, 0.5403022766113281d, 0.4311765432357788d, 0.3153223395347595d, 0.19454771280288696d, 0.07073719799518585d, -0.05417713522911072d};
    private static final double[] COSINE_TABLE_B = {0.0d, 3.4439717236742845E-8d, 5.865827662008209E-8d, -3.7999795083850525E-8d, 1.184154459111628E-8d, -3.43338934259355E-8d, 1.1795268640216787E-8d, 4.438921624363781E-8d, 2.925681159240093E-8d, -2.6437112632041807E-8d, 2.2860509143963117E-8d, -4.813899778443457E-9d, 3.6725170580355583E-9d, 2.0217439756338078E-10d};

    /* renamed from: E */
    public static final double f415E = 2.718281828459045d;
    private static final double[] EIGHTHS = {0.0d, 0.125d, F_1_4, 0.375d, F_1_2, 0.625d, F_3_4, F_7_8, 1.0d, 1.125d, 1.25d, 1.375d, 1.5d, 1.625d};
    static final int EXP_FRAC_TABLE_LEN = 1025;
    static final int EXP_INT_TABLE_LEN = 1500;
    static final int EXP_INT_TABLE_MAX_INDEX = 750;
    private static final double F_11_12 = 0.9166666666666666d;
    private static final double F_13_14 = 0.9285714285714286d;
    private static final double F_15_16 = 0.9375d;
    private static final double F_1_11 = 0.09090909090909091d;
    private static final double F_1_13 = 0.07692307692307693d;
    private static final double F_1_15 = 0.06666666666666667d;
    private static final double F_1_17 = 0.058823529411764705d;
    private static final double F_1_2 = 0.5d;
    private static final double F_1_3 = 0.3333333333333333d;
    private static final double F_1_4 = 0.25d;
    private static final double F_1_5 = 0.2d;
    private static final double F_1_7 = 0.14285714285714285d;
    private static final double F_1_9 = 0.1111111111111111d;
    private static final double F_3_4 = 0.75d;
    private static final double F_5_6 = 0.8333333333333334d;
    private static final double F_7_8 = 0.875d;
    private static final double F_9_10 = 0.9d;
    private static final long HEX_40000000 = 1073741824;
    private static final long IMPLICIT_HIGH_BIT = 4503599627370496L;
    private static final double LN_2_A = 0.6931470632553101d;
    private static final double LN_2_B = 1.1730463525082348E-7d;
    private static final double[][] LN_HI_PREC_COEF = {new double[]{1.0d, -6.032174644509064E-23d}, new double[]{-0.25d, -0.25d}, new double[]{0.3333333134651184d, 1.9868161777724352E-8d}, new double[]{-0.2499999701976776d, -2.957007209750105E-8d}, new double[]{0.19999954104423523d, 1.5830993332061267E-10d}, new double[]{-0.16624879837036133d, -2.6033824355191673E-8d}};
    static final int LN_MANT_LEN = 1024;
    private static final double[][] LN_QUICK_COEF = {new double[]{1.0d, 5.669184079525E-24d}, new double[]{-0.25d, -0.25d}, new double[]{0.3333333134651184d, 1.986821492305628E-8d}, new double[]{-0.25d, -6.663542893624021E-14d}, new double[]{0.19999998807907104d, 1.1921056801463227E-8d}, new double[]{-0.1666666567325592d, -7.800414592973399E-9d}, new double[]{0.1428571343421936d, 5.650007086920087E-9d}, new double[]{-0.12502530217170715d, -7.44321345601866E-11d}, new double[]{0.11113807559013367d, 9.219544613762692E-9d}};
    private static final double LOG_MAX_VALUE = StrictMath.log(Double.MAX_VALUE);
    private static final long MASK_30BITS = -1073741824;
    private static final long MASK_DOUBLE_EXPONENT = 9218868437227405312L;
    private static final long MASK_DOUBLE_MANTISSA = 4503599627370495L;
    private static final int MASK_NON_SIGN_INT = Integer.MAX_VALUE;
    private static final long MASK_NON_SIGN_LONG = Long.MAX_VALUE;

    /* renamed from: PI */
    public static final double f416PI = 3.141592653589793d;
    private static final long[] PI_O_4_BITS = {-3958705157555305932L, -4267615245585081135L};
    private static final long[] RECIP_2PI = {2935890503282001226L, 9154082963658192752L, 3952090531849364496L, 9193070505571053912L, 7910884519577875640L, 113236205062349959L, 4577762542105553359L, -5034868814120038111L, 4208363204685324176L, 5648769086999809661L, 2819561105158720014L, -4035746434778044925L, -302932621132653753L, -2644281811660520851L, -3183605296591799669L, 6722166367014452318L, -3512299194304650054L, -7278142539171889152L};
    private static final boolean RECOMPUTE_TABLES_AT_RUNTIME = false;
    private static final double[] SINE_TABLE_A = {0.0d, 0.1246747374534607d, 0.24740394949913025d, 0.366272509098053d, 0.4794255495071411d, 0.5850973129272461d, 0.6816387176513672d, 0.7675435543060303d, 0.8414709568023682d, 0.902267575263977d, 0.9489846229553223d, 0.9808930158615112d, 0.9974949359893799d, 0.9985313415527344d};
    private static final double[] SINE_TABLE_B = {0.0d, -4.068233003401932E-9d, 9.755392680573412E-9d, 1.9987994582857286E-8d, -1.0902938113007961E-8d, -3.9986783938944604E-8d, 4.23719669792332E-8d, -5.207000323380292E-8d, 2.800552834259E-8d, 1.883511811213715E-8d, -3.5997360512765566E-9d, 4.116164446561962E-8d, 5.0614674548127384E-8d, -1.0129027912496858E-9d};
    private static final int SINE_TABLE_LEN = 14;
    private static final double[] TANGENT_TABLE_A = {0.0d, 0.1256551444530487d, 0.25534194707870483d, 0.3936265707015991d, 0.5463024377822876d, 0.7214844226837158d, 0.9315965175628662d, 1.1974215507507324d, 1.5574076175689697d, 2.092571258544922d, 3.0095696449279785d, 5.041914939880371d, 14.101419448852539d, -18.430862426757812d};
    private static final double[] TANGENT_TABLE_B = {0.0d, -7.877917738262007E-9d, -2.5857668567479893E-8d, 5.2240336371356666E-9d, 5.206150291559893E-8d, 1.8307188599677033E-8d, -5.7618793749770706E-8d, 7.848361555046424E-8d, 1.0708593250394448E-7d, 1.7827257129423813E-8d, 2.893485277253286E-8d, 3.1660099222737955E-7d, 4.983191803254889E-7d, -3.356118100840571E-7d};
    private static final double TWO_POWER_52 = 4.503599627370496E15d;

    private FastMath() {
    }

    private static double doubleHighPart(double d) {
        return (d <= (-Precision.SAFE_MIN) || d >= Precision.SAFE_MIN) ? Double.longBitsToDouble(Double.doubleToRawLongBits(d) & MASK_30BITS) : d;
    }

    public static double sqrt(double a) {
        return Math.sqrt(a);
    }

    public static double cosh(double x) {
        if (x != x) {
            return x;
        }
        if (x > 20.0d) {
            if (x < LOG_MAX_VALUE) {
                return F_1_2 * exp(x);
            }
            double t = exp(F_1_2 * x);
            return F_1_2 * t * t;
        } else if (x >= -20.0d) {
            double[] hiPrec = new double[2];
            if (x < 0.0d) {
                x = -x;
            }
            exp(x, 0.0d, hiPrec);
            double ya = hiPrec[0] + hiPrec[1];
            double yb = -((ya - hiPrec[0]) - hiPrec[1]);
            double temp = ya * 1.073741824E9d;
            double yaa = (ya + temp) - temp;
            double yab = ya - yaa;
            double recip = 1.0d / ya;
            double temp2 = recip * 1.073741824E9d;
            double recipa = (recip + temp2) - temp2;
            double recipb = recip - recipa;
            double recipb2 = recipb + (((((1.0d - (yaa * recipa)) - (yaa * recipb)) - (yab * recipa)) - (yab * recipb)) * recip) + ((-yb) * recip * recip);
            double temp3 = ya + recipa;
            double temp4 = temp3 + recipb2;
            return (temp4 + yb + (-((temp3 - ya) - recipa)) + (-((temp4 - temp3) - recipb2))) * F_1_2;
        } else if (x > (-LOG_MAX_VALUE)) {
            return F_1_2 * exp(-x);
        } else {
            double t2 = exp(-0.5d * x);
            return F_1_2 * t2 * t2;
        }
    }

    public static double sinh(double x) {
        double result;
        boolean negate = RECOMPUTE_TABLES_AT_RUNTIME;
        if (x != x) {
            return x;
        }
        if (x > 20.0d) {
            if (x < LOG_MAX_VALUE) {
                return F_1_2 * exp(x);
            }
            double t = exp(F_1_2 * x);
            return F_1_2 * t * t;
        } else if (x < -20.0d) {
            if (x > (-LOG_MAX_VALUE)) {
                return -0.5d * exp(-x);
            }
            double t2 = exp(-0.5d * x);
            return -0.5d * t2 * t2;
        } else if (x == 0.0d) {
            return x;
        } else {
            if (x < 0.0d) {
                x = -x;
                negate = true;
            }
            if (x > F_1_4) {
                double[] hiPrec = new double[2];
                exp(x, 0.0d, hiPrec);
                double ya = hiPrec[0] + hiPrec[1];
                double yb = -((ya - hiPrec[0]) - hiPrec[1]);
                double temp = ya * 1.073741824E9d;
                double yaa = (ya + temp) - temp;
                double yab = ya - yaa;
                double recip = 1.0d / ya;
                double temp2 = recip * 1.073741824E9d;
                double recipa = (recip + temp2) - temp2;
                double recipb = recip - recipa;
                double recipa2 = -recipa;
                double recipb2 = -(recipb + (((((1.0d - (yaa * recipa)) - (yaa * recipb)) - (yab * recipa)) - (yab * recipb)) * recip) + ((-yb) * recip * recip));
                double temp3 = ya + recipa2;
                double temp4 = temp3 + recipb2;
                result = (temp4 + yb + (-((temp3 - ya) - recipa2)) + (-((temp4 - temp3) - recipb2))) * F_1_2;
            } else {
                double[] hiPrec2 = new double[2];
                expm1(x, hiPrec2);
                double ya2 = hiPrec2[0] + hiPrec2[1];
                double yb2 = -((ya2 - hiPrec2[0]) - hiPrec2[1]);
                double denom = 1.0d + ya2;
                double denomr = 1.0d / denom;
                double ratio = ya2 * denomr;
                double temp5 = ratio * 1.073741824E9d;
                double ra = (ratio + temp5) - temp5;
                double rb = ratio - ra;
                double temp6 = denom * 1.073741824E9d;
                double za = (denom + temp6) - temp6;
                double zb = denom - za;
                double rb2 = rb + (((((ya2 - (za * ra)) - (za * rb)) - (zb * ra)) - (zb * rb)) * denomr) + (yb2 * denomr) + ((-ya2) * ((-((denom - 1.0d) - ya2)) + yb2) * denomr * denomr);
                double temp7 = ya2 + ra;
                double temp8 = temp7 + rb2;
                result = (temp8 + yb2 + (-((temp7 - ya2) - ra)) + (-((temp8 - temp7) - rb2))) * F_1_2;
            }
            if (negate) {
                result = -result;
            }
            return result;
        }
    }

    public static double tanh(double x) {
        double result;
        boolean negate = RECOMPUTE_TABLES_AT_RUNTIME;
        if (x != x) {
            return x;
        }
        if (x > 20.0d) {
            return 1.0d;
        }
        if (x < -20.0d) {
            return -1.0d;
        }
        if (x == 0.0d) {
            return x;
        }
        if (x < 0.0d) {
            x = -x;
            negate = true;
        }
        if (x >= F_1_2) {
            double[] hiPrec = new double[2];
            exp(2.0d * x, 0.0d, hiPrec);
            double ya = hiPrec[0] + hiPrec[1];
            double yb = -((ya - hiPrec[0]) - hiPrec[1]);
            double na = -1.0d + ya;
            double temp = na + yb;
            double nb = (-((1.0d + na) - ya)) + (-((temp - na) - yb));
            double da = 1.0d + ya;
            double temp2 = da + yb;
            double temp3 = temp2 * 1.073741824E9d;
            double daa = (temp2 + temp3) - temp3;
            double dab = temp2 - daa;
            double ratio = temp / temp2;
            double temp4 = ratio * 1.073741824E9d;
            double ratioa = (ratio + temp4) - temp4;
            double ratiob = ratio - ratioa;
            result = ratioa + ratiob + (((((temp - (daa * ratioa)) - (daa * ratiob)) - (dab * ratioa)) - (dab * ratiob)) / temp2) + (nb / temp2) + ((((-((-((da - 1.0d) - ya)) + (-((temp2 - da) - yb)))) * temp) / temp2) / temp2);
        } else {
            double[] hiPrec2 = new double[2];
            expm1(2.0d * x, hiPrec2);
            double ya2 = hiPrec2[0] + hiPrec2[1];
            double yb2 = -((ya2 - hiPrec2[0]) - hiPrec2[1]);
            double da2 = 2.0d + ya2;
            double temp5 = da2 + yb2;
            double temp6 = temp5 * 1.073741824E9d;
            double daa2 = (temp5 + temp6) - temp6;
            double dab2 = temp5 - daa2;
            double ratio2 = ya2 / temp5;
            double temp7 = ratio2 * 1.073741824E9d;
            double ratioa2 = (ratio2 + temp7) - temp7;
            double ratiob2 = ratio2 - ratioa2;
            result = ratioa2 + ratiob2 + (((((ya2 - (daa2 * ratioa2)) - (daa2 * ratiob2)) - (dab2 * ratioa2)) - (dab2 * ratiob2)) / temp5) + (yb2 / temp5) + ((((-((-((da2 - 2.0d) - ya2)) + (-((temp5 - da2) - yb2)))) * ya2) / temp5) / temp5);
        }
        if (negate) {
            result = -result;
        }
        return result;
    }

    public static double acosh(double a) {
        return log(sqrt((a * a) - 1.0d) + a);
    }

    public static double asinh(double a) {
        double absAsinh;
        boolean negative = RECOMPUTE_TABLES_AT_RUNTIME;
        if (a < 0.0d) {
            negative = true;
            a = -a;
        }
        if (a > 0.167d) {
            absAsinh = log(sqrt((a * a) + 1.0d) + a);
        } else {
            double a2 = a * a;
            if (a > 0.097d) {
                absAsinh = a * (1.0d - (((F_1_3 - (((F_1_5 - (((F_1_7 - (((F_1_9 - (((F_1_11 - (((F_1_13 - (((F_1_15 - ((F_1_17 * a2) * F_15_16)) * a2) * F_13_14)) * a2) * F_11_12)) * a2) * F_9_10)) * a2) * F_7_8)) * a2) * F_5_6)) * a2) * F_3_4)) * a2) * F_1_2));
            } else if (a > 0.036d) {
                absAsinh = a * (1.0d - (((F_1_3 - (((F_1_5 - (((F_1_7 - (((F_1_9 - (((F_1_11 - ((F_1_13 * a2) * F_11_12)) * a2) * F_9_10)) * a2) * F_7_8)) * a2) * F_5_6)) * a2) * F_3_4)) * a2) * F_1_2));
            } else if (a > 0.0036d) {
                absAsinh = a * (1.0d - (((F_1_3 - (((F_1_5 - (((F_1_7 - ((F_1_9 * a2) * F_7_8)) * a2) * F_5_6)) * a2) * F_3_4)) * a2) * F_1_2));
            } else {
                absAsinh = a * (1.0d - (((F_1_3 - ((F_1_5 * a2) * F_3_4)) * a2) * F_1_2));
            }
        }
        return negative ? -absAsinh : absAsinh;
    }

    public static double atanh(double a) {
        double absAtanh;
        boolean negative = RECOMPUTE_TABLES_AT_RUNTIME;
        if (a < 0.0d) {
            negative = true;
            a = -a;
        }
        if (a > 0.15d) {
            absAtanh = F_1_2 * log((1.0d + a) / (1.0d - a));
        } else {
            double a2 = a * a;
            if (a > 0.087d) {
                absAtanh = a * (1.0d + ((F_1_3 + ((F_1_5 + ((F_1_7 + ((F_1_9 + ((F_1_11 + ((F_1_13 + ((F_1_15 + (F_1_17 * a2)) * a2)) * a2)) * a2)) * a2)) * a2)) * a2)) * a2));
            } else if (a > 0.031d) {
                absAtanh = a * (1.0d + ((F_1_3 + ((F_1_5 + ((F_1_7 + ((F_1_9 + ((F_1_11 + (F_1_13 * a2)) * a2)) * a2)) * a2)) * a2)) * a2));
            } else if (a > 0.003d) {
                absAtanh = a * (1.0d + ((F_1_3 + ((F_1_5 + ((F_1_7 + (F_1_9 * a2)) * a2)) * a2)) * a2));
            } else {
                absAtanh = a * (1.0d + ((F_1_3 + (F_1_5 * a2)) * a2));
            }
        }
        return negative ? -absAtanh : absAtanh;
    }

    public static double signum(double a) {
        if (a < 0.0d) {
            return -1.0d;
        }
        if (a > 0.0d) {
            return 1.0d;
        }
        return a;
    }

    public static float signum(float a) {
        if (a < 0.0f) {
            return -1.0f;
        }
        if (a > 0.0f) {
            return 1.0f;
        }
        return a;
    }

    public static double nextUp(double a) {
        return nextAfter(a, Double.POSITIVE_INFINITY);
    }

    public static float nextUp(float a) {
        return nextAfter(a, Double.POSITIVE_INFINITY);
    }

    public static double nextDown(double a) {
        return nextAfter(a, Double.NEGATIVE_INFINITY);
    }

    public static float nextDown(float a) {
        return nextAfter(a, Double.NEGATIVE_INFINITY);
    }

    public static double random() {
        return Math.random();
    }

    public static double exp(double x) {
        return exp(x, 0.0d, null);
    }

    private static double exp(double x, double extra, double[] hiPrec) {
        double result;
        int intVal = (int) x;
        if (x < 0.0d) {
            if (x < -746.0d) {
                if (hiPrec != null) {
                    hiPrec[0] = 0.0d;
                    hiPrec[1] = 0.0d;
                }
                return 0.0d;
            } else if (intVal < -709) {
                double exp = exp(40.19140625d + x, extra, hiPrec) / 2.85040095144011776E17d;
                if (hiPrec == null) {
                    return exp;
                }
                hiPrec[0] = hiPrec[0] / 2.85040095144011776E17d;
                hiPrec[1] = hiPrec[1] / 2.85040095144011776E17d;
                return exp;
            } else if (intVal == -709) {
                double exp2 = exp(1.494140625d + x, extra, hiPrec) / 4.455505956692757d;
                if (hiPrec == null) {
                    return exp2;
                }
                hiPrec[0] = hiPrec[0] / 4.455505956692757d;
                hiPrec[1] = hiPrec[1] / 4.455505956692757d;
                return exp2;
            } else {
                intVal--;
            }
        } else if (intVal > 709) {
            if (hiPrec != null) {
                hiPrec[0] = Double.POSITIVE_INFINITY;
                hiPrec[1] = 0.0d;
            }
            return Double.POSITIVE_INFINITY;
        }
        double intPartA = ExpIntTable.EXP_INT_TABLE_A[intVal + EXP_INT_TABLE_MAX_INDEX];
        double intPartB = ExpIntTable.EXP_INT_TABLE_B[intVal + EXP_INT_TABLE_MAX_INDEX];
        int intFrac = (int) ((x - ((double) intVal)) * 1024.0d);
        double fracPartA = ExpFracTable.EXP_FRAC_TABLE_A[intFrac];
        double fracPartB = ExpFracTable.EXP_FRAC_TABLE_B[intFrac];
        double epsilon = x - (((double) intVal) + (((double) intFrac) / 1024.0d));
        double z = (((((((0.04168701738764507d * epsilon) + 0.1666666505023083d) * epsilon) + 0.5000000000042687d) * epsilon) + 1.0d) * epsilon) - 1.1409003175371524E20d;
        double tempA = intPartA * fracPartA;
        double tempB = (intPartA * fracPartB) + (intPartB * fracPartA) + (intPartB * fracPartB);
        double tempC = tempB + tempA;
        if (tempC == Double.POSITIVE_INFINITY) {
            return Double.POSITIVE_INFINITY;
        }
        if (extra != 0.0d) {
            result = (tempC * extra * z) + (tempC * extra) + (tempC * z) + tempB + tempA;
        } else {
            result = (tempC * z) + tempB + tempA;
        }
        if (hiPrec == null) {
            return result;
        }
        hiPrec[0] = tempA;
        hiPrec[1] = (tempC * extra * z) + (tempC * extra) + (tempC * z) + tempB;
        return result;
    }

    public static double expm1(double x) {
        return expm1(x, null);
    }

    private static double expm1(double x, double[] hiPrecOut) {
        if (x != x || x == 0.0d) {
            return x;
        }
        if (x <= -1.0d || x >= 1.0d) {
            double[] hiPrec = new double[2];
            exp(x, 0.0d, hiPrec);
            if (x > 0.0d) {
                return -1.0d + hiPrec[0] + hiPrec[1];
            }
            double ra = -1.0d + hiPrec[0];
            return ra + (-((1.0d + ra) - hiPrec[0])) + hiPrec[1];
        }
        boolean negative = RECOMPUTE_TABLES_AT_RUNTIME;
        if (x < 0.0d) {
            x = -x;
            negative = true;
        }
        int intFrac = (int) (1024.0d * x);
        double tempA = ExpFracTable.EXP_FRAC_TABLE_A[intFrac] - 1.0d;
        double tempB = ExpFracTable.EXP_FRAC_TABLE_B[intFrac];
        double temp = tempA + tempB;
        double tempB2 = -((temp - tempA) - tempB);
        double temp2 = temp * 1.073741824E9d;
        double baseA = (temp + temp2) - temp2;
        double baseB = tempB2 + (temp - baseA);
        double epsilon = x - (((double) intFrac) / 1024.0d);
        double zb = ((((((0.008336750013465571d * epsilon) + 0.041666663879186654d) * epsilon) + 0.16666666666745392d) * epsilon) + 0.49999999999999994d) * epsilon * epsilon;
        double temp3 = epsilon + zb;
        double zb2 = -((temp3 - epsilon) - zb);
        double temp4 = temp3 * 1.073741824E9d;
        double temp5 = (temp3 + temp4) - temp4;
        double zb3 = zb2 + (temp3 - temp5);
        double ya = temp5 * baseA;
        double temp6 = ya + (temp5 * baseB);
        double yb = -((temp6 - ya) - (temp5 * baseB));
        double temp7 = temp6 + (zb3 * baseA);
        double temp8 = temp7 + (zb3 * baseB);
        double temp9 = temp8 + baseA;
        double temp10 = temp9 + temp5;
        double temp11 = temp10 + baseB;
        double temp12 = temp11 + zb3;
        double yb2 = yb + (-((temp7 - temp6) - (zb3 * baseA))) + (-((temp8 - temp7) - (zb3 * baseB))) + (-((temp9 - baseA) - temp8)) + (-((temp10 - temp9) - temp5)) + (-((temp11 - temp10) - baseB)) + (-((temp12 - temp11) - zb3));
        double ya2 = temp12;
        if (negative) {
            double denom = 1.0d + ya2;
            double denomr = 1.0d / denom;
            double denomb = (-((denom - 1.0d) - ya2)) + yb2;
            double ratio = ya2 * denomr;
            double temp13 = ratio * 1.073741824E9d;
            double ra2 = (ratio + temp13) - temp13;
            double rb = ratio - ra2;
            double temp14 = denom * 1.073741824E9d;
            double za = (denom + temp14) - temp14;
            double zb4 = denom - za;
            ya2 = -ra2;
            yb2 = -(rb + (((((ya2 - (za * ra2)) - (za * rb)) - (zb4 * ra2)) - (zb4 * rb)) * denomr) + (yb2 * denomr) + ((-ya2) * denomb * denomr * denomr));
        }
        if (hiPrecOut != null) {
            hiPrecOut[0] = ya2;
            hiPrecOut[1] = yb2;
        }
        return ya2 + yb2;
    }

    public static double log(double x) {
        return log(x, (double[]) null);
    }

    private static double log(double x, double[] hiPrec) {
        double lnza;
        if (x == 0.0d) {
            return Double.NEGATIVE_INFINITY;
        }
        long bits = Double.doubleToRawLongBits(x);
        if (((Long.MIN_VALUE & bits) != 0 || x != x) && x != 0.0d) {
            if (hiPrec != null) {
                hiPrec[0] = Double.NaN;
            }
            return Double.NaN;
        } else if (x == Double.POSITIVE_INFINITY) {
            if (hiPrec != null) {
                hiPrec[0] = Double.POSITIVE_INFINITY;
            }
            return Double.POSITIVE_INFINITY;
        } else {
            int exp = ((int) (bits >> 52)) - 1023;
            if ((MASK_DOUBLE_EXPONENT & bits) == 0) {
                if (x != 0.0d) {
                    while (true) {
                        bits <<= 1;
                        if ((IMPLICIT_HIGH_BIT & bits) != 0) {
                            break;
                        }
                        exp--;
                    }
                } else {
                    if (hiPrec != null) {
                        hiPrec[0] = Double.NEGATIVE_INFINITY;
                    }
                    return Double.NEGATIVE_INFINITY;
                }
            }
            if ((exp == -1 || exp == 0) && x < 1.01d && x > 0.99d && hiPrec == null) {
                double xa = x - 1.0d;
                double d = (xa - x) + 1.0d;
                double tmp = xa * 1.073741824E9d;
                double aa = (xa + tmp) - tmp;
                double ab = xa - aa;
                double[] lnCoef_last = LN_QUICK_COEF[LN_QUICK_COEF.length - 1];
                double ya = lnCoef_last[0];
                double yb = lnCoef_last[1];
                for (int i = LN_QUICK_COEF.length - 2; i >= 0; i--) {
                    double aa2 = ya * aa;
                    double ab2 = (ya * ab) + (yb * aa) + (yb * ab);
                    double tmp2 = aa2 * 1.073741824E9d;
                    double ya2 = (aa2 + tmp2) - tmp2;
                    double yb2 = (aa2 - ya2) + ab2;
                    double[] lnCoef_i = LN_QUICK_COEF[i];
                    double aa3 = ya2 + lnCoef_i[0];
                    double tmp3 = aa3 * 1.073741824E9d;
                    ya = (aa3 + tmp3) - tmp3;
                    yb = (aa3 - ya) + yb2 + lnCoef_i[1];
                }
                double aa4 = ya * aa;
                double ab3 = (ya * ab) + (yb * aa) + (yb * ab);
                double tmp4 = aa4 * 1.073741824E9d;
                double ya3 = (aa4 + tmp4) - tmp4;
                return ya3 + (aa4 - ya3) + ab3;
            }
            double[] lnm = lnMant.LN_MANT[(int) ((4499201580859392L & bits) >> 42)];
            double epsilon = ((double) (4398046511103L & bits)) / (TWO_POWER_52 + ((double) (4499201580859392L & bits)));
            double lnzb = 0.0d;
            if (hiPrec != null) {
                double tmp5 = epsilon * 1.073741824E9d;
                double aa5 = (epsilon + tmp5) - tmp5;
                double ab4 = epsilon - aa5;
                double denom = TWO_POWER_52 + ((double) (4499201580859392L & bits));
                double xb = ab4 + (((((double) (4398046511103L & bits)) - (aa5 * denom)) - (ab4 * denom)) / denom);
                double[] lnCoef_last2 = LN_HI_PREC_COEF[LN_HI_PREC_COEF.length - 1];
                double ya4 = lnCoef_last2[0];
                double yb3 = lnCoef_last2[1];
                for (int i2 = LN_HI_PREC_COEF.length - 2; i2 >= 0; i2--) {
                    double aa6 = ya4 * aa5;
                    double ab5 = (ya4 * xb) + (yb3 * aa5) + (yb3 * xb);
                    double tmp6 = aa6 * 1.073741824E9d;
                    double ya5 = (aa6 + tmp6) - tmp6;
                    double yb4 = (aa6 - ya5) + ab5;
                    double[] lnCoef_i2 = LN_HI_PREC_COEF[i2];
                    double aa7 = ya5 + lnCoef_i2[0];
                    double tmp7 = aa7 * 1.073741824E9d;
                    ya4 = (aa7 + tmp7) - tmp7;
                    yb3 = (aa7 - ya4) + yb4 + lnCoef_i2[1];
                }
                double aa8 = ya4 * aa5;
                double ab6 = (ya4 * xb) + (yb3 * aa5) + (yb3 * xb);
                lnza = aa8 + ab6;
                lnzb = -((lnza - aa8) - ab6);
            } else {
                lnza = ((((((((((-0.16624882440418567d * epsilon) + 0.19999954120254515d) * epsilon) - 16.00000002972804d) * epsilon) + 0.3333333333332802d) * epsilon) - 8.0d) * epsilon) + 1.0d) * epsilon;
            }
            double a = LN_2_A * ((double) exp);
            double c = a + lnm[0];
            double c2 = c + lnza;
            double c3 = c2 + (LN_2_B * ((double) exp));
            double c4 = c3 + lnm[1];
            double c5 = c4 + lnzb;
            double b = 0.0d + (-((c - a) - lnm[0])) + (-((c2 - c) - lnza)) + (-((c3 - c2) - (LN_2_B * ((double) exp)))) + (-((c4 - c3) - lnm[1])) + (-((c5 - c4) - lnzb));
            if (hiPrec != null) {
                hiPrec[0] = c5;
                hiPrec[1] = b;
            }
            return c5 + b;
        }
    }

    public static double log1p(double x) {
        if (x == -1.0d) {
            return Double.NEGATIVE_INFINITY;
        }
        if (x == Double.POSITIVE_INFINITY) {
            return Double.POSITIVE_INFINITY;
        }
        if (x <= 1.0E-6d && x >= -1.0E-6d) {
            return ((((F_1_3 * x) - F_1_2) * x) + 1.0d) * x;
        }
        double xpa = 1.0d + x;
        double xpb = -((xpa - 1.0d) - x);
        double[] hiPrec = new double[2];
        double lores = log(xpa, hiPrec);
        if (Double.isInfinite(lores)) {
            return lores;
        }
        double fx1 = xpb / xpa;
        return (((F_1_2 * fx1) + 1.0d) * fx1) + hiPrec[1] + hiPrec[0];
    }

    public static double log10(double x) {
        double[] hiPrec = new double[2];
        double lores = log(x, hiPrec);
        if (Double.isInfinite(lores)) {
            return lores;
        }
        double tmp = hiPrec[0] * 1.073741824E9d;
        double lna = (hiPrec[0] + tmp) - tmp;
        double lnb = (hiPrec[0] - lna) + hiPrec[1];
        return (1.9699272335463627E-8d * lnb) + (1.9699272335463627E-8d * lna) + (0.4342944622039795d * lnb) + (0.4342944622039795d * lna);
    }

    public static double log(double base, double x) {
        return log(x) / log(base);
    }

    public static double pow(double x, double y) {
        if (y == 0.0d) {
            return 1.0d;
        }
        long yBits = Double.doubleToRawLongBits(y);
        int yRawExp = (int) ((MASK_DOUBLE_EXPONENT & yBits) >> 52);
        long yRawMantissa = yBits & MASK_DOUBLE_MANTISSA;
        long xBits = Double.doubleToRawLongBits(x);
        int xRawExp = (int) ((MASK_DOUBLE_EXPONENT & xBits) >> 52);
        long xRawMantissa = xBits & MASK_DOUBLE_MANTISSA;
        if (yRawExp <= 1085) {
            if (yRawExp >= 1023) {
                long yFullMantissa = IMPLICIT_HIGH_BIT | yRawMantissa;
                if (yRawExp >= 1075) {
                    long l = yFullMantissa << (yRawExp - 1075);
                    if (y < 0.0d) {
                        l = -l;
                    }
                    return pow(x, l);
                } else if ((yFullMantissa & (-1 << (1075 - yRawExp))) == yFullMantissa) {
                    long l2 = yFullMantissa >> (1075 - yRawExp);
                    if (y < 0.0d) {
                        l2 = -l2;
                    }
                    return pow(x, l2);
                }
            }
            if (x == 0.0d) {
                return y < 0.0d ? Double.POSITIVE_INFINITY : 0.0d;
            }
            if (xRawExp == 2047) {
                if (xRawMantissa == 0) {
                    return y < 0.0d ? 0.0d : Double.POSITIVE_INFINITY;
                }
                return Double.NaN;
            } else if (x < 0.0d) {
                return Double.NaN;
            } else {
                double tmp = y * 1.073741824E9d;
                double ya = (y + tmp) - tmp;
                double yb = y - ya;
                double[] lns = new double[2];
                double lores = log(x, lns);
                if (Double.isInfinite(lores)) {
                    return lores;
                }
                double lna = lns[0];
                double tmp1 = lna * 1.073741824E9d;
                double tmp2 = (lna + tmp1) - tmp1;
                double lnb = lns[1] + (lna - tmp2);
                double aa = tmp2 * ya;
                double ab = (tmp2 * yb) + (lnb * ya) + (lnb * yb);
                double lna2 = aa + ab;
                double lnb2 = -((lna2 - aa) - ab);
                return exp(lna2, ((((((((0.008333333333333333d * lnb2) + 0.041666666666666664d) * lnb2) + 0.16666666666666666d) * lnb2) + F_1_2) * lnb2) + 1.0d) * lnb2, null);
            }
        } else if ((yRawExp == 2047 && yRawMantissa != 0) || (xRawExp == 2047 && xRawMantissa != 0)) {
            return Double.NaN;
        } else {
            if (xRawExp != 1023 || xRawMantissa != 0) {
                if ((xRawExp < 1023 ? true : RECOMPUTE_TABLES_AT_RUNTIME) ^ (y > 0.0d)) {
                    return Double.POSITIVE_INFINITY;
                }
                return 0.0d;
            } else if (yRawExp == 2047) {
                return Double.NaN;
            } else {
                return 1.0d;
            }
        }
    }

    public static double pow(double d, int e) {
        return pow(d, (long) e);
    }

    public static double pow(double d, long e) {
        if (e == 0) {
            return 1.0d;
        }
        if (e > 0) {
            return new Split(d).pow(e).full;
        }
        return new Split(d).reciprocal().pow(-e).full;
    }

    /* access modifiers changed from: private */
    public static class Split {
        public static final Split NAN = new Split(Double.NaN, 0.0d);
        public static final Split NEGATIVE_INFINITY = new Split(Double.NEGATIVE_INFINITY, 0.0d);
        public static final Split POSITIVE_INFINITY = new Split(Double.POSITIVE_INFINITY, 0.0d);
        private final double full;
        private final double high;
        private final double low;

        Split(double x) {
            this.full = x;
            this.high = Double.longBitsToDouble(Double.doubleToRawLongBits(x) & -134217728);
            this.low = x - this.high;
        }

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        Split(double r10, double r12) {
            /*
                r9 = this;
                r2 = 0
                int r0 = (r10 > r2 ? 1 : (r10 == r2 ? 0 : -1))
                if (r0 != 0) goto L_0x001f
                int r0 = (r12 > r2 ? 1 : (r12 == r2 ? 0 : -1))
                if (r0 != 0) goto L_0x001d
                long r0 = java.lang.Double.doubleToRawLongBits(r10)
                r2 = -9223372036854775808
                int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r0 != 0) goto L_0x001d
                r2 = -9223372036854775808
            L_0x0016:
                r1 = r9
                r4 = r10
                r6 = r12
                r1.<init>(r2, r4, r6)
                return
            L_0x001d:
                r2 = r12
                goto L_0x0016
            L_0x001f:
                double r2 = r10 + r12
                goto L_0x0016
            */
            throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.math3.util.FastMath.Split.<init>(double, double):void");
        }

        Split(double full2, double high2, double low2) {
            this.full = full2;
            this.high = high2;
            this.low = low2;
        }

        public Split multiply(Split b) {
            Split mulBasic = new Split(this.full * b.full);
            return new Split(mulBasic.high, mulBasic.low + ((this.low * b.low) - (((mulBasic.full - (this.high * b.high)) - (this.low * b.high)) - (this.high * b.low))));
        }

        public Split reciprocal() {
            Split splitInv = new Split(1.0d / this.full);
            Split product = multiply(splitInv);
            double error = (product.high - 1.0d) + product.low;
            return Double.isNaN(error) ? splitInv : new Split(splitInv.high, splitInv.low - (error / this.full));
        }

        /* access modifiers changed from: private */
        /* access modifiers changed from: public */
        private Split pow(long e) {
            Split result = new Split(1.0d);
            Split d2p = new Split(this.full, this.high, this.low);
            for (long p = e; p != 0; p >>>= 1) {
                if ((1 & p) != 0) {
                    result = result.multiply(d2p);
                }
                d2p = d2p.multiply(d2p);
            }
            if (!Double.isNaN(result.full)) {
                return result;
            }
            if (Double.isNaN(this.full)) {
                return NAN;
            }
            if (FastMath.abs(this.full) < 1.0d) {
                return new Split(FastMath.copySign(0.0d, this.full), 0.0d);
            }
            if (this.full >= 0.0d || (1 & e) != 1) {
                return POSITIVE_INFINITY;
            }
            return NEGATIVE_INFINITY;
        }
    }

    private static double polySine(double x) {
        double x2 = x * x;
        return ((((((2.7553817452272217E-6d * x2) - 22521.49865654966d) * x2) + 0.008333333333329196d) * x2) - 26.666666666666668d) * x2 * x;
    }

    private static double polyCosine(double x) {
        double x2 = x * x;
        return ((((((2.479773539153719E-5d * x2) - 3231.288930800263d) * x2) + 0.041666666666621166d) * x2) - 8.000000000000002d) * x2;
    }

    private static double sinQ(double xa, double xb) {
        int idx = (int) ((8.0d * xa) + F_1_2);
        double epsilon = xa - EIGHTHS[idx];
        double sintA = SINE_TABLE_A[idx];
        double sintB = SINE_TABLE_B[idx];
        double costA = COSINE_TABLE_A[idx];
        double costB = COSINE_TABLE_B[idx];
        double sinEpsB = polySine(epsilon);
        double cosEpsB = polyCosine(epsilon);
        double temp = epsilon * 1.073741824E9d;
        double temp2 = (epsilon + temp) - temp;
        double sinEpsB2 = sinEpsB + (epsilon - temp2);
        double c = 0.0d + sintA;
        double t = costA * temp2;
        double c2 = c + t;
        double a = c2;
        double b = (sintA * cosEpsB) + 0.0d + (-((c - 0.0d) - sintA)) + (-((c2 - c) - t)) + (costA * sinEpsB2) + sintB + (costB * temp2) + (sintB * cosEpsB) + (costB * sinEpsB2);
        if (xb != 0.0d) {
            double t2 = (((costA + costB) * (1.0d + cosEpsB)) - ((sintA + sintB) * (temp2 + sinEpsB2))) * xb;
            double c3 = a + t2;
            a = c3;
            b += -((c3 - a) - t2);
        }
        return a + b;
    }

    private static double cosQ(double xa, double xb) {
        double a = 1.5707963267948966d - xa;
        return sinQ(a, (-((a - 1.5707963267948966d) + xa)) + (6.123233995736766E-17d - xb));
    }

    private static double tanQ(double xa, double xb, boolean cotanFlag) {
        int idx = (int) ((8.0d * xa) + F_1_2);
        double epsilon = xa - EIGHTHS[idx];
        double sintA = SINE_TABLE_A[idx];
        double sintB = SINE_TABLE_B[idx];
        double costA = COSINE_TABLE_A[idx];
        double costB = COSINE_TABLE_B[idx];
        double sinEpsB = polySine(epsilon);
        double cosEpsB = polyCosine(epsilon);
        double temp = epsilon * 1.073741824E9d;
        double temp2 = (epsilon + temp) - temp;
        double sinEpsB2 = sinEpsB + (epsilon - temp2);
        double c = 0.0d + sintA;
        double t = costA * temp2;
        double c2 = c + t;
        double b = 0.0d + (-((c - 0.0d) - sintA)) + (-((c2 - c) - t)) + (sintA * cosEpsB) + (costA * sinEpsB2) + (costB * temp2) + sintB + (sintB * cosEpsB) + (costB * sinEpsB2);
        double sina = c2 + b;
        double sinb = -((sina - c2) - b);
        double t2 = costA * 1.0d;
        double c3 = 0.0d + t2;
        double b2 = 0.0d + (-((c3 - 0.0d) - t2));
        double t3 = (-sintA) * temp2;
        double c4 = c3 + t3;
        double b3 = ((b2 + (-((c4 - c3) - t3))) + (((1.0d * costB) + (costA * cosEpsB)) + (costB * cosEpsB))) - (((sintB * temp2) + (sintA * sinEpsB2)) + (sintB * sinEpsB2));
        double cosa = c4 + b3;
        double cosb = -((cosa - c4) - b3);
        if (cotanFlag) {
            cosa = sina;
            sina = cosa;
            cosb = sinb;
            sinb = cosb;
        }
        double est = sina / cosa;
        double temp3 = est * 1.073741824E9d;
        double esta = (est + temp3) - temp3;
        double estb = est - esta;
        double temp4 = cosa * 1.073741824E9d;
        double cosaa = (cosa + temp4) - temp4;
        double cosab = cosa - cosaa;
        double err = (((((sina - (esta * cosaa)) - (esta * cosab)) - (estb * cosaa)) - (estb * cosab)) / cosa) + (sinb / cosa) + ((((-sina) * cosb) / cosa) / cosa);
        if (xb != 0.0d) {
            double xbadj = xb + (est * est * xb);
            if (cotanFlag) {
                xbadj = -xbadj;
            }
            err += xbadj;
        }
        return est + err;
    }

    private static void reducePayneHanek(double x, double[] result) {
        long shpi0;
        long shpiA;
        long shpiB;
        long inbits = Double.doubleToRawLongBits(x);
        int exponent = (((int) ((inbits >> 52) & 2047)) - 1023) + 1;
        long inbits2 = ((inbits & MASK_DOUBLE_MANTISSA) | IMPLICIT_HIGH_BIT) << 11;
        int idx = exponent >> 6;
        int shift = exponent - (idx << 6);
        if (shift != 0) {
            shpi0 = (idx == 0 ? 0 : RECIP_2PI[idx - 1] << shift) | (RECIP_2PI[idx] >>> (64 - shift));
            shpiA = (RECIP_2PI[idx] << shift) | (RECIP_2PI[idx + 1] >>> (64 - shift));
            shpiB = (RECIP_2PI[idx + 1] << shift) | (RECIP_2PI[idx + 2] >>> (64 - shift));
        } else {
            if (idx == 0) {
                shpi0 = 0;
            } else {
                shpi0 = RECIP_2PI[idx - 1];
            }
            shpiA = RECIP_2PI[idx];
            shpiB = RECIP_2PI[idx + 1];
        }
        long a = inbits2 >>> 32;
        long b = inbits2 & 4294967295L;
        long c = shpiA >>> 32;
        long d = shpiA & 4294967295L;
        long bd = b * d;
        long bc = b * c;
        long ad = a * d;
        long prodB = bd + (ad << 32);
        long prodA = (a * c) + (ad >>> 32);
        boolean bita = (Long.MIN_VALUE & bd) != 0 ? true : RECOMPUTE_TABLES_AT_RUNTIME;
        boolean bitb = (2147483648L & ad) != 0 ? true : RECOMPUTE_TABLES_AT_RUNTIME;
        boolean bitsum = (Long.MIN_VALUE & prodB) != 0 ? true : RECOMPUTE_TABLES_AT_RUNTIME;
        if ((bita && bitb) || ((bita || bitb) && !bitsum)) {
            prodA++;
        }
        boolean bita2 = (Long.MIN_VALUE & prodB) != 0 ? true : RECOMPUTE_TABLES_AT_RUNTIME;
        boolean bitb2 = (2147483648L & bc) != 0 ? true : RECOMPUTE_TABLES_AT_RUNTIME;
        long prodB2 = prodB + (bc << 32);
        long prodA2 = prodA + (bc >>> 32);
        boolean bitsum2 = (Long.MIN_VALUE & prodB2) != 0 ? true : RECOMPUTE_TABLES_AT_RUNTIME;
        if ((bita2 && bitb2) || ((bita2 || bitb2) && !bitsum2)) {
            prodA2++;
        }
        long c2 = shpiB >>> 32;
        long ac = (a * c2) + (((b * c2) + (a * (shpiB & 4294967295L))) >>> 32);
        boolean bita3 = (Long.MIN_VALUE & prodB2) != 0 ? true : RECOMPUTE_TABLES_AT_RUNTIME;
        boolean bitb3 = (Long.MIN_VALUE & ac) != 0 ? true : RECOMPUTE_TABLES_AT_RUNTIME;
        long prodB3 = prodB2 + ac;
        boolean bitsum3 = (Long.MIN_VALUE & prodB3) != 0 ? true : RECOMPUTE_TABLES_AT_RUNTIME;
        if ((bita3 && bitb3) || ((bita3 || bitb3) && !bitsum3)) {
            prodA2++;
        }
        long d2 = shpi0 & 4294967295L;
        long prodA3 = prodA2 + (((b * (shpi0 >>> 32)) + (a * d2)) << 32) + (b * d2);
        int intPart = (int) (prodA3 >>> 62);
        long prodA4 = (prodA3 << 2) | (prodB3 >>> 62);
        long prodB4 = prodB3 << 2;
        long a2 = prodA4 >>> 32;
        long b2 = prodA4 & 4294967295L;
        long c3 = PI_O_4_BITS[0] >>> 32;
        long d3 = PI_O_4_BITS[0] & 4294967295L;
        long bd2 = b2 * d3;
        long bc2 = b2 * c3;
        long ad2 = a2 * d3;
        long prod2B = bd2 + (ad2 << 32);
        long prod2A = (a2 * c3) + (ad2 >>> 32);
        boolean bita4 = (Long.MIN_VALUE & bd2) != 0 ? true : RECOMPUTE_TABLES_AT_RUNTIME;
        boolean bitb4 = (2147483648L & ad2) != 0 ? true : RECOMPUTE_TABLES_AT_RUNTIME;
        boolean bitsum4 = (Long.MIN_VALUE & prod2B) != 0 ? true : RECOMPUTE_TABLES_AT_RUNTIME;
        if ((bita4 && bitb4) || ((bita4 || bitb4) && !bitsum4)) {
            prod2A++;
        }
        boolean bita5 = (Long.MIN_VALUE & prod2B) != 0 ? true : RECOMPUTE_TABLES_AT_RUNTIME;
        boolean bitb5 = (2147483648L & bc2) != 0 ? true : RECOMPUTE_TABLES_AT_RUNTIME;
        long prod2B2 = prod2B + (bc2 << 32);
        long prod2A2 = prod2A + (bc2 >>> 32);
        boolean bitsum5 = (Long.MIN_VALUE & prod2B2) != 0 ? true : RECOMPUTE_TABLES_AT_RUNTIME;
        if ((bita5 && bitb5) || ((bita5 || bitb5) && !bitsum5)) {
            prod2A2++;
        }
        long c4 = PI_O_4_BITS[1] >>> 32;
        long ac2 = (a2 * c4) + (((b2 * c4) + (a2 * (PI_O_4_BITS[1] & 4294967295L))) >>> 32);
        boolean bita6 = (Long.MIN_VALUE & prod2B2) != 0 ? true : RECOMPUTE_TABLES_AT_RUNTIME;
        boolean bitb6 = (Long.MIN_VALUE & ac2) != 0 ? true : RECOMPUTE_TABLES_AT_RUNTIME;
        long prod2B3 = prod2B2 + ac2;
        boolean bitsum6 = (Long.MIN_VALUE & prod2B3) != 0 ? true : RECOMPUTE_TABLES_AT_RUNTIME;
        if ((bita6 && bitb6) || ((bita6 || bitb6) && !bitsum6)) {
            prod2A2++;
        }
        long a3 = prodB4 >>> 32;
        long c5 = PI_O_4_BITS[0] >>> 32;
        long ac3 = (a3 * c5) + ((((prodB4 & 4294967295L) * c5) + (a3 * (PI_O_4_BITS[0] & 4294967295L))) >>> 32);
        boolean bita7 = (Long.MIN_VALUE & prod2B3) != 0 ? true : RECOMPUTE_TABLES_AT_RUNTIME;
        boolean bitb7 = (Long.MIN_VALUE & ac3) != 0 ? true : RECOMPUTE_TABLES_AT_RUNTIME;
        long prod2B4 = prod2B3 + ac3;
        boolean bitsum7 = (Long.MIN_VALUE & prod2B4) != 0 ? true : RECOMPUTE_TABLES_AT_RUNTIME;
        if ((bita7 && bitb7) || ((bita7 || bitb7) && !bitsum7)) {
            prod2A2++;
        }
        double tmpA = ((double) (prod2A2 >>> 12)) / TWO_POWER_52;
        double tmpB = (((double) (((4095 & prod2A2) << 40) + (prod2B4 >>> 24))) / TWO_POWER_52) / TWO_POWER_52;
        double sumA = tmpA + tmpB;
        result[0] = (double) intPart;
        result[1] = 2.0d * sumA;
        result[2] = 2.0d * (-((sumA - tmpA) - tmpB));
    }

    public static double sin(double x) {
        boolean negative = RECOMPUTE_TABLES_AT_RUNTIME;
        int quadrant = 0;
        double xb = 0.0d;
        double xa = x;
        if (x < 0.0d) {
            negative = true;
            xa = -xa;
        }
        if (xa == 0.0d) {
            if (Double.doubleToRawLongBits(x) < 0) {
                return -0.0d;
            }
            return 0.0d;
        } else if (xa != xa || xa == Double.POSITIVE_INFINITY) {
            return Double.NaN;
        } else {
            if (xa > 3294198.0d) {
                double[] reduceResults = new double[3];
                reducePayneHanek(xa, reduceResults);
                quadrant = ((int) reduceResults[0]) & 3;
                xa = reduceResults[1];
                xb = reduceResults[2];
            } else if (xa > 1.5707963267948966d) {
                CodyWaite cw = new CodyWaite(xa);
                quadrant = cw.getK() & 3;
                xa = cw.getRemA();
                xb = cw.getRemB();
            }
            if (negative) {
                quadrant ^= 2;
            }
            switch (quadrant) {
                case 0:
                    return sinQ(xa, xb);
                case 1:
                    return cosQ(xa, xb);
                case 2:
                    return -sinQ(xa, xb);
                case 3:
                    return -cosQ(xa, xb);
                default:
                    return Double.NaN;
            }
        }
    }

    public static double cos(double x) {
        int quadrant = 0;
        double xa = x;
        if (x < 0.0d) {
            xa = -xa;
        }
        if (xa != xa || xa == Double.POSITIVE_INFINITY) {
            return Double.NaN;
        }
        double xb = 0.0d;
        if (xa > 3294198.0d) {
            double[] reduceResults = new double[3];
            reducePayneHanek(xa, reduceResults);
            quadrant = ((int) reduceResults[0]) & 3;
            xa = reduceResults[1];
            xb = reduceResults[2];
        } else if (xa > 1.5707963267948966d) {
            CodyWaite cw = new CodyWaite(xa);
            quadrant = cw.getK() & 3;
            xa = cw.getRemA();
            xb = cw.getRemB();
        }
        switch (quadrant) {
            case 0:
                return cosQ(xa, xb);
            case 1:
                return -sinQ(xa, xb);
            case 2:
                return -cosQ(xa, xb);
            case 3:
                return sinQ(xa, xb);
            default:
                return Double.NaN;
        }
    }

    public static double tan(double x) {
        double result;
        boolean negative = RECOMPUTE_TABLES_AT_RUNTIME;
        int quadrant = 0;
        double xa = x;
        if (x < 0.0d) {
            negative = true;
            xa = -xa;
        }
        if (xa == 0.0d) {
            if (Double.doubleToRawLongBits(x) < 0) {
                return -0.0d;
            }
            return 0.0d;
        } else if (xa != xa || xa == Double.POSITIVE_INFINITY) {
            return Double.NaN;
        } else {
            double xb = 0.0d;
            if (xa > 3294198.0d) {
                double[] reduceResults = new double[3];
                reducePayneHanek(xa, reduceResults);
                quadrant = ((int) reduceResults[0]) & 3;
                xa = reduceResults[1];
                xb = reduceResults[2];
            } else if (xa > 1.5707963267948966d) {
                CodyWaite cw = new CodyWaite(xa);
                quadrant = cw.getK() & 3;
                xa = cw.getRemA();
                xb = cw.getRemB();
            }
            if (xa > 1.5d) {
                double a = 1.5707963267948966d - xa;
                double b = (-((a - 1.5707963267948966d) + xa)) + (6.123233995736766E-17d - xb);
                xa = a + b;
                xb = -((xa - a) - b);
                quadrant ^= 1;
                negative = !negative;
            }
            if ((quadrant & 1) == 0) {
                result = tanQ(xa, xb, RECOMPUTE_TABLES_AT_RUNTIME);
            } else {
                result = -tanQ(xa, xb, true);
            }
            if (negative) {
                return -result;
            }
            return result;
        }
    }

    public static double atan(double x) {
        return atan(x, 0.0d, RECOMPUTE_TABLES_AT_RUNTIME);
    }

    private static double atan(double xa, double xb, boolean leftPlane) {
        boolean negate;
        int idx;
        double ya;
        double yb;
        if (xa == 0.0d) {
            return leftPlane ? copySign(3.141592653589793d, xa) : xa;
        }
        if (xa < 0.0d) {
            xa = -xa;
            xb = -xb;
            negate = true;
        } else {
            negate = RECOMPUTE_TABLES_AT_RUNTIME;
        }
        if (xa > 1.633123935319537E16d) {
            return negate ^ leftPlane ? -1.5707963267948966d : 1.5707963267948966d;
        }
        if (xa < 1.0d) {
            idx = (int) ((((-1.7168146928204135d * xa * xa) + 8.0d) * xa) + F_1_2);
        } else {
            double oneOverXa = 1.0d / xa;
            idx = (int) ((-(((-1.7168146928204135d * oneOverXa * oneOverXa) + 8.0d) * oneOverXa)) + 13.07d);
        }
        double ttA = TANGENT_TABLE_A[idx];
        double ttB = TANGENT_TABLE_B[idx];
        double epsA = xa - ttA;
        double epsB = (-((epsA - xa) + ttA)) + (xb - ttB);
        double temp = epsA + epsB;
        double epsB2 = -((temp - epsA) - epsB);
        double temp2 = xa * 1.073741824E9d;
        double ya2 = (xa + temp2) - temp2;
        double xb2 = xb + ((xb + xa) - ya2);
        if (idx == 0) {
            double denom = 1.0d / (1.0d + ((ya2 + xb2) * (ttA + ttB)));
            ya = temp * denom;
            yb = epsB2 * denom;
        } else {
            double temp22 = ya2 * ttA;
            double za = 1.0d + temp22;
            double zb = -((za - 1.0d) - temp22);
            double temp23 = (xb2 * ttA) + (ya2 * ttB);
            double temp3 = za + temp23;
            double zb2 = zb + (-((temp3 - za) - temp23)) + (xb2 * ttB);
            ya = temp / temp3;
            double temp4 = ya * 1.073741824E9d;
            double yaa = (ya + temp4) - temp4;
            double yab = ya - yaa;
            double temp5 = temp3 * 1.073741824E9d;
            double zaa = (temp3 + temp5) - temp5;
            double zab = temp3 - zaa;
            yb = (((((temp - (yaa * zaa)) - (yaa * zab)) - (yab * zaa)) - (yab * zab)) / temp3) + ((((-temp) * zb2) / temp3) / temp3) + (epsB2 / temp3);
        }
        double epsA2 = ya * ya;
        double yb2 = ((((((((((0.07490822288864472d * epsA2) - 0.09088450866185192d) * epsA2) + 0.11111095942313305d) * epsA2) - 0.1428571423679182d) * epsA2) + 0.19999999999923582d) * epsA2) - 0.33333333333333287d) * epsA2 * ya;
        double temp6 = ya + yb2;
        double yb3 = (-((temp6 - ya) - yb2)) + (yb / (1.0d + (ya * ya)));
        double eighths = EIGHTHS[idx];
        double za2 = eighths + temp6;
        double temp7 = za2 + yb3;
        double zb3 = (-((za2 - eighths) - temp6)) + (-((temp7 - za2) - yb3));
        double result = temp7 + zb3;
        if (leftPlane) {
            double za3 = 3.141592653589793d - result;
            result = za3 + (-((za3 - 3.141592653589793d) + result)) + (1.2246467991473532E-16d - (-((result - temp7) - zb3)));
        }
        if (negate ^ leftPlane) {
            result = -result;
        }
        return result;
    }

    public static double atan2(double y, double x) {
        if (x != x || y != y) {
            return Double.NaN;
        }
        if (y == 0.0d) {
            double d = x * y;
            double invx = 1.0d / x;
            double invy = 1.0d / y;
            if (invx == 0.0d) {
                if (x > 0.0d) {
                    return y;
                }
                return copySign(3.141592653589793d, y);
            } else if (x >= 0.0d && invx >= 0.0d) {
                return d;
            } else {
                if (y < 0.0d || invy < 0.0d) {
                    return -3.141592653589793d;
                }
                return 3.141592653589793d;
            }
        } else if (y == Double.POSITIVE_INFINITY) {
            if (x == Double.POSITIVE_INFINITY) {
                return 0.7853981633974483d;
            }
            if (x == Double.NEGATIVE_INFINITY) {
                return 2.356194490192345d;
            }
            return 1.5707963267948966d;
        } else if (y != Double.NEGATIVE_INFINITY) {
            if (x == Double.POSITIVE_INFINITY) {
                if (y > 0.0d || 1.0d / y > 0.0d) {
                    return 0.0d;
                }
                if (y < 0.0d || 1.0d / y < 0.0d) {
                    return -0.0d;
                }
            }
            if (x == Double.NEGATIVE_INFINITY) {
                if (y > 0.0d || 1.0d / y > 0.0d) {
                    return 3.141592653589793d;
                }
                if (y < 0.0d || 1.0d / y < 0.0d) {
                    return -3.141592653589793d;
                }
            }
            if (x == 0.0d) {
                if (y > 0.0d || 1.0d / y > 0.0d) {
                    return 1.5707963267948966d;
                }
                if (y < 0.0d || 1.0d / y < 0.0d) {
                    return -1.5707963267948966d;
                }
            }
            double r = y / x;
            if (Double.isInfinite(r)) {
                return atan(r, 0.0d, x < 0.0d ? true : RECOMPUTE_TABLES_AT_RUNTIME);
            }
            double ra = doubleHighPart(r);
            double rb = r - ra;
            double xa = doubleHighPart(x);
            double xb = x - xa;
            double rb2 = rb + (((((y - (ra * xa)) - (ra * xb)) - (rb * xa)) - (rb * xb)) / x);
            double temp = ra + rb2;
            double rb3 = -((temp - ra) - rb2);
            double ra2 = temp;
            if (ra2 == 0.0d) {
                ra2 = copySign(0.0d, y);
            }
            return atan(ra2, rb3, x < 0.0d ? true : RECOMPUTE_TABLES_AT_RUNTIME);
        } else if (x == Double.POSITIVE_INFINITY) {
            return -0.7853981633974483d;
        } else {
            if (x == Double.NEGATIVE_INFINITY) {
                return -2.356194490192345d;
            }
            return -1.5707963267948966d;
        }
    }

    public static double asin(double x) {
        if (x != x || x > 1.0d || x < -1.0d) {
            return Double.NaN;
        }
        if (x == 1.0d) {
            return 1.5707963267948966d;
        }
        if (x == -1.0d) {
            return -1.5707963267948966d;
        }
        if (x == 0.0d) {
            return x;
        }
        double temp = x * 1.073741824E9d;
        double xa = (x + temp) - temp;
        double xb = x - xa;
        double ya = -(xa * xa);
        double yb = -((xa * xb * 2.0d) + (xb * xb));
        double za = 1.0d + ya;
        double temp2 = za + yb;
        double y = sqrt(temp2);
        double temp3 = y * 1.073741824E9d;
        double ya2 = (y + temp3) - temp3;
        double yb2 = y - ya2;
        double yb3 = yb2 + ((((temp2 - (ya2 * ya2)) - ((2.0d * ya2) * yb2)) - (yb2 * yb2)) / (2.0d * y));
        double dx = ((-((za - 1.0d) - ya)) + (-((temp2 - za) - yb))) / (2.0d * y);
        double r = x / y;
        double temp4 = r * 1.073741824E9d;
        double ra = (r + temp4) - temp4;
        double rb = r - ra;
        double rb2 = rb + (((((x - (ra * ya2)) - (ra * yb3)) - (rb * ya2)) - (rb * yb3)) / y) + ((((-x) * dx) / y) / y);
        double temp5 = ra + rb2;
        return atan(temp5, -((temp5 - ra) - rb2), RECOMPUTE_TABLES_AT_RUNTIME);
    }

    public static double acos(double x) {
        if (x != x || x > 1.0d || x < -1.0d) {
            return Double.NaN;
        }
        if (x == -1.0d) {
            return 3.141592653589793d;
        }
        if (x == 1.0d) {
            return 0.0d;
        }
        if (x == 0.0d) {
            return 1.5707963267948966d;
        }
        double temp = x * 1.073741824E9d;
        double xa = (x + temp) - temp;
        double xb = x - xa;
        double ya = -(xa * xa);
        double yb = -((xa * xb * 2.0d) + (xb * xb));
        double za = 1.0d + ya;
        double temp2 = za + yb;
        double y = sqrt(temp2);
        double temp3 = y * 1.073741824E9d;
        double ya2 = (y + temp3) - temp3;
        double yb2 = y - ya2;
        double yb3 = yb2 + ((((temp2 - (ya2 * ya2)) - ((2.0d * ya2) * yb2)) - (yb2 * yb2)) / (2.0d * y)) + (((-((za - 1.0d) - ya)) + (-((temp2 - za) - yb))) / (2.0d * y));
        double y2 = ya2 + yb3;
        double yb4 = -((y2 - ya2) - yb3);
        double r = y2 / x;
        if (Double.isInfinite(r)) {
            return 1.5707963267948966d;
        }
        double ra = doubleHighPart(r);
        double rb = r - ra;
        double rb2 = rb + (((((y2 - (ra * xa)) - (ra * xb)) - (rb * xa)) - (rb * xb)) / x) + (yb4 / x);
        double temp4 = ra + rb2;
        return atan(temp4, -((temp4 - ra) - rb2), x < 0.0d ? true : RECOMPUTE_TABLES_AT_RUNTIME);
    }

    public static double cbrt(double x) {
        long inbits = Double.doubleToRawLongBits(x);
        int exponent = ((int) ((inbits >> 52) & 2047)) - 1023;
        boolean subnormal = RECOMPUTE_TABLES_AT_RUNTIME;
        if (exponent == -1023) {
            if (x == 0.0d) {
                return x;
            }
            subnormal = true;
            x *= 1.8014398509481984E16d;
            inbits = Double.doubleToRawLongBits(x);
            exponent = ((int) ((inbits >> 52) & 2047)) - 1023;
        }
        if (exponent == LN_MANT_LEN) {
            return x;
        }
        double p2 = Double.longBitsToDouble((Long.MIN_VALUE & inbits) | (((long) (((exponent / 3) + 1023) & 2047)) << 52));
        double mant = Double.longBitsToDouble((MASK_DOUBLE_MANTISSA & inbits) | 4607182418800017408L);
        double est = ((((((((-0.010714690733195933d * mant) + 0.0875862700108075d) * mant) - 14.214349574856733d) * mant) + 0.7249995199969751d) * mant) + 0.5039018405998233d) * CBRTTWO[(exponent % 3) + 2];
        double xs = x / ((p2 * p2) * p2);
        double est2 = est + ((xs - ((est * est) * est)) / ((3.0d * est) * est));
        double est3 = est2 + ((xs - ((est2 * est2) * est2)) / ((3.0d * est2) * est2));
        double temp = est3 * 1.073741824E9d;
        double ya = (est3 + temp) - temp;
        double yb = est3 - ya;
        double za = ya * ya;
        double temp2 = za * 1.073741824E9d;
        double temp22 = (za + temp2) - temp2;
        double zb = (ya * yb * 2.0d) + (yb * yb) + (za - temp22);
        double za2 = temp22 * ya;
        double na = xs - za2;
        double est4 = (est3 + ((na + ((-((na - xs) + za2)) - (((temp22 * yb) + (ya * zb)) + (zb * yb)))) / ((3.0d * est3) * est3))) * p2;
        if (subnormal) {
            est4 *= 3.814697265625E-6d;
        }
        return est4;
    }

    public static double toRadians(double x) {
        if (Double.isInfinite(x) || x == 0.0d) {
            return x;
        }
        double xa = doubleHighPart(x);
        double xb = x - xa;
        double result = (1.997844754509471E-9d * xb) + (0.01745329052209854d * xb) + (1.997844754509471E-9d * xa) + (0.01745329052209854d * xa);
        if (result == 0.0d) {
            return result * x;
        }
        return result;
    }

    public static double toDegrees(double x) {
        if (Double.isInfinite(x) || x == 0.0d) {
            return x;
        }
        double xa = doubleHighPart(x);
        double xb = x - xa;
        return (3.145894820876798E-6d * xb) + (57.2957763671875d * xb) + (3.145894820876798E-6d * xa) + (57.2957763671875d * xa);
    }

    public static int abs(int x) {
        int i = x >>> 31;
        return (((i ^ -1) + 1) ^ x) + i;
    }

    public static long abs(long x) {
        long l = x >>> 63;
        return (((-1 ^ l) + 1) ^ x) + l;
    }

    public static float abs(float x) {
        return Float.intBitsToFloat(Integer.MAX_VALUE & Float.floatToRawIntBits(x));
    }

    public static double abs(double x) {
        return Double.longBitsToDouble(MASK_NON_SIGN_LONG & Double.doubleToRawLongBits(x));
    }

    public static double ulp(double x) {
        if (Double.isInfinite(x)) {
            return Double.POSITIVE_INFINITY;
        }
        return abs(x - Double.longBitsToDouble(Double.doubleToRawLongBits(x) ^ 1));
    }

    public static float ulp(float x) {
        if (Float.isInfinite(x)) {
            return Float.POSITIVE_INFINITY;
        }
        return abs(x - Float.intBitsToFloat(Float.floatToIntBits(x) ^ 1));
    }

    public static double scalb(double d, int n) {
        if (n > -1023 && n < LN_MANT_LEN) {
            return Double.longBitsToDouble(((long) (n + 1023)) << 52) * d;
        }
        if (Double.isNaN(d) || Double.isInfinite(d) || d == 0.0d) {
            return d;
        }
        if (n < -2098) {
            return d > 0.0d ? 0.0d : -0.0d;
        }
        if (n > 2097) {
            return d > 0.0d ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
        }
        long bits = Double.doubleToRawLongBits(d);
        long sign = bits & Long.MIN_VALUE;
        int exponent = ((int) (bits >>> 52)) & 2047;
        long mantissa = bits & MASK_DOUBLE_MANTISSA;
        int scaledExponent = exponent + n;
        if (n < 0) {
            if (scaledExponent > 0) {
                return Double.longBitsToDouble((((long) scaledExponent) << 52) | sign | mantissa);
            }
            if (scaledExponent <= -53) {
                return sign == 0 ? 0.0d : -0.0d;
            }
            long mantissa2 = mantissa | IMPLICIT_HIGH_BIT;
            long mostSignificantLostBit = mantissa2 & (1 << (-scaledExponent));
            long mantissa3 = mantissa2 >>> (1 - scaledExponent);
            if (mostSignificantLostBit != 0) {
                mantissa3++;
            }
            return Double.longBitsToDouble(sign | mantissa3);
        } else if (exponent == 0) {
            while ((mantissa >>> 52) != 1) {
                mantissa <<= 1;
                scaledExponent--;
            }
            int scaledExponent2 = scaledExponent + 1;
            long mantissa4 = mantissa & MASK_DOUBLE_MANTISSA;
            if (scaledExponent2 < 2047) {
                return Double.longBitsToDouble((((long) scaledExponent2) << 52) | sign | mantissa4);
            }
            return sign == 0 ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
        } else if (scaledExponent < 2047) {
            return Double.longBitsToDouble((((long) scaledExponent) << 52) | sign | mantissa);
        } else {
            return sign == 0 ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
        }
    }

    public static float scalb(float f, int n) {
        float f2 = Float.POSITIVE_INFINITY;
        if (n > -127 && n < 128) {
            return Float.intBitsToFloat((n + 127) << 23) * f;
        }
        if (Float.isNaN(f) || Float.isInfinite(f) || f == 0.0f) {
            return f;
        }
        if (n < -277) {
            return f <= 0.0f ? -0.0f : 0.0f;
        }
        if (n > 276) {
            return f > 0.0f ? Float.POSITIVE_INFINITY : Float.NEGATIVE_INFINITY;
        }
        int bits = Float.floatToIntBits(f);
        int sign = bits & Integer.MIN_VALUE;
        int exponent = (bits >>> 23) & 255;
        int mantissa = bits & 8388607;
        int scaledExponent = exponent + n;
        if (n < 0) {
            if (scaledExponent > 0) {
                return Float.intBitsToFloat((scaledExponent << 23) | sign | mantissa);
            }
            if (scaledExponent <= -24) {
                return sign != 0 ? -0.0f : 0.0f;
            }
            int mantissa2 = mantissa | 8388608;
            int mostSignificantLostBit = mantissa2 & (1 << (-scaledExponent));
            int mantissa3 = mantissa2 >>> (1 - scaledExponent);
            if (mostSignificantLostBit != 0) {
                mantissa3++;
            }
            return Float.intBitsToFloat(sign | mantissa3);
        } else if (exponent == 0) {
            while ((mantissa >>> 23) != 1) {
                mantissa <<= 1;
                scaledExponent--;
            }
            int scaledExponent2 = scaledExponent + 1;
            int mantissa4 = mantissa & 8388607;
            if (scaledExponent2 < 255) {
                return Float.intBitsToFloat((scaledExponent2 << 23) | sign | mantissa4);
            }
            if (sign != 0) {
                f2 = Float.NEGATIVE_INFINITY;
            }
            return f2;
        } else if (scaledExponent < 255) {
            return Float.intBitsToFloat((scaledExponent << 23) | sign | mantissa);
        } else {
            if (sign != 0) {
                f2 = Float.NEGATIVE_INFINITY;
            }
            return f2;
        }
    }

    public static double nextAfter(double d, double direction) {
        if (Double.isNaN(d) || Double.isNaN(direction)) {
            return Double.NaN;
        }
        if (d == direction) {
            return direction;
        }
        if (Double.isInfinite(d)) {
            return d < 0.0d ? -1.7976931348623157E308d : Double.MAX_VALUE;
        }
        if (d == 0.0d) {
            return direction < 0.0d ? -4.9E-324d : Double.MIN_VALUE;
        }
        long bits = Double.doubleToRawLongBits(d);
        long sign = bits & Long.MIN_VALUE;
        if ((sign == 0 ? true : RECOMPUTE_TABLES_AT_RUNTIME) ^ (direction < d)) {
            return Double.longBitsToDouble(((MASK_NON_SIGN_LONG & bits) + 1) | sign);
        }
        return Double.longBitsToDouble(((MASK_NON_SIGN_LONG & bits) - 1) | sign);
    }

    public static float nextAfter(float f, double direction) {
        boolean z;
        boolean z2 = true;
        if (Double.isNaN((double) f) || Double.isNaN(direction)) {
            return Float.NaN;
        }
        if (((double) f) == direction) {
            return (float) direction;
        }
        if (Float.isInfinite(f)) {
            return f < 0.0f ? -3.4028235E38f : Float.MAX_VALUE;
        }
        if (f == 0.0f) {
            return direction < 0.0d ? -1.4E-45f : Float.MIN_VALUE;
        }
        int bits = Float.floatToIntBits(f);
        int sign = bits & Integer.MIN_VALUE;
        if (direction < ((double) f)) {
            z = true;
        } else {
            z = false;
        }
        if (sign != 0) {
            z2 = false;
        }
        if (z2 ^ z) {
            return Float.intBitsToFloat(((bits & Integer.MAX_VALUE) + 1) | sign);
        }
        return Float.intBitsToFloat(((bits & Integer.MAX_VALUE) - 1) | sign);
    }

    public static double floor(double x) {
        if (x != x || x >= TWO_POWER_52 || x <= -4.503599627370496E15d) {
            return x;
        }
        long y = (long) x;
        if (x < 0.0d && ((double) y) != x) {
            y--;
        }
        if (y == 0) {
            return x * ((double) y);
        }
        return (double) y;
    }

    public static double ceil(double x) {
        if (x != x) {
            return x;
        }
        double y = floor(x);
        if (y == x) {
            return y;
        }
        double y2 = y + 1.0d;
        if (y2 == 0.0d) {
            return y2 * x;
        }
        return y2;
    }

    public static double rint(double x) {
        double y = floor(x);
        double d = x - y;
        if (d <= F_1_2) {
            return (d < F_1_2 || (1 & ((long) y)) == 0) ? y : y + 1.0d;
        }
        if (y == -1.0d) {
            return -0.0d;
        }
        return y + 1.0d;
    }

    public static long round(double x) {
        return (long) floor(F_1_2 + x);
    }

    public static int round(float x) {
        return (int) floor((double) (0.5f + x));
    }

    public static int min(int a, int b) {
        return a <= b ? a : b;
    }

    public static long min(long a, long b) {
        return a <= b ? a : b;
    }

    public static float min(float a, float b) {
        if (a > b) {
            return b;
        }
        if (a < b) {
            return a;
        }
        if (a != b) {
            return Float.NaN;
        }
        return Float.floatToRawIntBits(a) == Integer.MIN_VALUE ? a : b;
    }

    public static double min(double a, double b) {
        if (a > b) {
            return b;
        }
        if (a < b) {
            return a;
        }
        if (a != b) {
            return Double.NaN;
        }
        return Double.doubleToRawLongBits(a) == Long.MIN_VALUE ? a : b;
    }

    public static int max(int a, int b) {
        return a <= b ? b : a;
    }

    public static long max(long a, long b) {
        return a <= b ? b : a;
    }

    public static float max(float a, float b) {
        if (a > b) {
            return a;
        }
        if (a < b) {
            return b;
        }
        if (a != b) {
            return Float.NaN;
        }
        return Float.floatToRawIntBits(a) == Integer.MIN_VALUE ? b : a;
    }

    public static double max(double a, double b) {
        if (a > b) {
            return a;
        }
        if (a < b) {
            return b;
        }
        if (a != b) {
            return Double.NaN;
        }
        return Double.doubleToRawLongBits(a) == Long.MIN_VALUE ? b : a;
    }

    public static double hypot(double x, double y) {
        if (Double.isInfinite(x) || Double.isInfinite(y)) {
            return Double.POSITIVE_INFINITY;
        }
        if (Double.isNaN(x) || Double.isNaN(y)) {
            return Double.NaN;
        }
        int expX = getExponent(x);
        int expY = getExponent(y);
        if (expX > expY + 27) {
            return abs(x);
        }
        if (expY > expX + 27) {
            return abs(y);
        }
        int middleExp = (expX + expY) / 2;
        double scaledX = scalb(x, -middleExp);
        double scaledY = scalb(y, -middleExp);
        return scalb(sqrt((scaledX * scaledX) + (scaledY * scaledY)), middleExp);
    }

    public static double IEEEremainder(double dividend, double divisor) {
        return StrictMath.IEEEremainder(dividend, divisor);
    }

    public static int toIntExact(long n) throws MathArithmeticException {
        if (n >= -2147483648L && n <= 2147483647L) {
            return (int) n;
        }
        throw new MathArithmeticException(LocalizedFormats.OVERFLOW, new Object[0]);
    }

    public static int incrementExact(int n) throws MathArithmeticException {
        if (n != Integer.MAX_VALUE) {
            return n + 1;
        }
        throw new MathArithmeticException(LocalizedFormats.OVERFLOW_IN_ADDITION, Integer.valueOf(n), 1);
    }

    public static long incrementExact(long n) throws MathArithmeticException {
        if (n != MASK_NON_SIGN_LONG) {
            return 1 + n;
        }
        throw new MathArithmeticException(LocalizedFormats.OVERFLOW_IN_ADDITION, Long.valueOf(n), 1);
    }

    public static int decrementExact(int n) throws MathArithmeticException {
        if (n != Integer.MIN_VALUE) {
            return n - 1;
        }
        throw new MathArithmeticException(LocalizedFormats.OVERFLOW_IN_SUBTRACTION, Integer.valueOf(n), 1);
    }

    public static long decrementExact(long n) throws MathArithmeticException {
        if (n != Long.MIN_VALUE) {
            return n - 1;
        }
        throw new MathArithmeticException(LocalizedFormats.OVERFLOW_IN_SUBTRACTION, Long.valueOf(n), 1);
    }

    public static int addExact(int a, int b) throws MathArithmeticException {
        int sum = a + b;
        if ((a ^ b) < 0 || (sum ^ b) >= 0) {
            return sum;
        }
        throw new MathArithmeticException(LocalizedFormats.OVERFLOW_IN_ADDITION, Integer.valueOf(a), Integer.valueOf(b));
    }

    public static long addExact(long a, long b) throws MathArithmeticException {
        long sum = a + b;
        if ((a ^ b) < 0 || (sum ^ b) >= 0) {
            return sum;
        }
        throw new MathArithmeticException(LocalizedFormats.OVERFLOW_IN_ADDITION, Long.valueOf(a), Long.valueOf(b));
    }

    public static int subtractExact(int a, int b) {
        int sub = a - b;
        if ((a ^ b) >= 0 || (sub ^ b) < 0) {
            return sub;
        }
        throw new MathArithmeticException(LocalizedFormats.OVERFLOW_IN_SUBTRACTION, Integer.valueOf(a), Integer.valueOf(b));
    }

    public static long subtractExact(long a, long b) {
        long sub = a - b;
        if ((a ^ b) >= 0 || (sub ^ b) < 0) {
            return sub;
        }
        throw new MathArithmeticException(LocalizedFormats.OVERFLOW_IN_SUBTRACTION, Long.valueOf(a), Long.valueOf(b));
    }

    public static int multiplyExact(int a, int b) {
        if ((b <= 0 || (a <= Integer.MAX_VALUE / b && a >= Integer.MIN_VALUE / b)) && ((b >= -1 || (a <= Integer.MIN_VALUE / b && a >= Integer.MAX_VALUE / b)) && (b != -1 || a != Integer.MIN_VALUE))) {
            return a * b;
        }
        throw new MathArithmeticException(LocalizedFormats.OVERFLOW_IN_MULTIPLICATION, Integer.valueOf(a), Integer.valueOf(b));
    }

    public static long multiplyExact(long a, long b) {
        if ((b <= 0 || (a <= MASK_NON_SIGN_LONG / b && a >= Long.MIN_VALUE / b)) && ((b >= -1 || (a <= Long.MIN_VALUE / b && a >= MASK_NON_SIGN_LONG / b)) && (b != -1 || a != Long.MIN_VALUE))) {
            return a * b;
        }
        throw new MathArithmeticException(LocalizedFormats.OVERFLOW_IN_MULTIPLICATION, Long.valueOf(a), Long.valueOf(b));
    }

    public static int floorDiv(int a, int b) throws MathArithmeticException {
        if (b == 0) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_DENOMINATOR, new Object[0]);
        }
        int m = a % b;
        if ((a ^ b) >= 0 || m == 0) {
            return a / b;
        }
        return (a / b) - 1;
    }

    public static long floorDiv(long a, long b) throws MathArithmeticException {
        if (b == 0) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_DENOMINATOR, new Object[0]);
        }
        long m = a % b;
        if ((a ^ b) >= 0 || m == 0) {
            return a / b;
        }
        return (a / b) - 1;
    }

    public static int floorMod(int a, int b) throws MathArithmeticException {
        if (b == 0) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_DENOMINATOR, new Object[0]);
        }
        int m = a % b;
        return ((a ^ b) >= 0 || m == 0) ? m : m + b;
    }

    public static long floorMod(long a, long b) {
        if (b == 0) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_DENOMINATOR, new Object[0]);
        }
        long m = a % b;
        return ((a ^ b) >= 0 || m == 0) ? m : m + b;
    }

    public static double copySign(double magnitude, double sign) {
        return (Double.doubleToRawLongBits(magnitude) ^ Double.doubleToRawLongBits(sign)) >= 0 ? magnitude : -magnitude;
    }

    public static float copySign(float magnitude, float sign) {
        return (Float.floatToRawIntBits(magnitude) ^ Float.floatToRawIntBits(sign)) >= 0 ? magnitude : -magnitude;
    }

    public static int getExponent(double d) {
        return ((int) ((Double.doubleToRawLongBits(d) >>> 52) & 2047)) - 1023;
    }

    public static int getExponent(float f) {
        return ((Float.floatToRawIntBits(f) >>> 23) & 255) - 127;
    }

    public static void main(String[] a) {
        PrintStream out = System.out;
        FastMathCalc.printarray(out, "EXP_INT_TABLE_A", (int) EXP_INT_TABLE_LEN, ExpIntTable.EXP_INT_TABLE_A);
        FastMathCalc.printarray(out, "EXP_INT_TABLE_B", (int) EXP_INT_TABLE_LEN, ExpIntTable.EXP_INT_TABLE_B);
        FastMathCalc.printarray(out, "EXP_FRAC_TABLE_A", (int) EXP_FRAC_TABLE_LEN, ExpFracTable.EXP_FRAC_TABLE_A);
        FastMathCalc.printarray(out, "EXP_FRAC_TABLE_B", (int) EXP_FRAC_TABLE_LEN, ExpFracTable.EXP_FRAC_TABLE_B);
        FastMathCalc.printarray(out, "LN_MANT", (int) LN_MANT_LEN, lnMant.LN_MANT);
        FastMathCalc.printarray(out, "SINE_TABLE_A", 14, SINE_TABLE_A);
        FastMathCalc.printarray(out, "SINE_TABLE_B", 14, SINE_TABLE_B);
        FastMathCalc.printarray(out, "COSINE_TABLE_A", 14, COSINE_TABLE_A);
        FastMathCalc.printarray(out, "COSINE_TABLE_B", 14, COSINE_TABLE_B);
        FastMathCalc.printarray(out, "TANGENT_TABLE_A", 14, TANGENT_TABLE_A);
        FastMathCalc.printarray(out, "TANGENT_TABLE_B", 14, TANGENT_TABLE_B);
    }

    /* access modifiers changed from: private */
    public static class ExpIntTable {
        private static final double[] EXP_INT_TABLE_A = FastMathLiteralArrays.loadExpIntA();
        private static final double[] EXP_INT_TABLE_B = FastMathLiteralArrays.loadExpIntB();

        private ExpIntTable() {
        }
    }

    /* access modifiers changed from: private */
    public static class ExpFracTable {
        private static final double[] EXP_FRAC_TABLE_A = FastMathLiteralArrays.loadExpFracA();
        private static final double[] EXP_FRAC_TABLE_B = FastMathLiteralArrays.loadExpFracB();

        private ExpFracTable() {
        }
    }

    /* access modifiers changed from: private */
    public static class lnMant {
        private static final double[][] LN_MANT = FastMathLiteralArrays.loadLnMant();

        private lnMant() {
        }
    }

    private static class CodyWaite {
        private final int finalK;
        private final double finalRemA;
        private final double finalRemB;

        CodyWaite(double xa) {
            int k = (int) (0.6366197723675814d * xa);
            while (true) {
                double a = ((double) (-k)) * 1.570796251296997d;
                double remA = xa + a;
                double remB = -((remA - xa) - a);
                double a2 = ((double) (-k)) * 7.549789948768648E-8d;
                double remA2 = a2 + remA;
                double remB2 = remB + (-((remA2 - remA) - a2));
                double a3 = ((double) (-k)) * 6.123233995736766E-17d;
                double remA3 = a3 + remA2;
                double remB3 = remB2 + (-((remA3 - remA2) - a3));
                if (remA3 > 0.0d) {
                    this.finalK = k;
                    this.finalRemA = remA3;
                    this.finalRemB = remB3;
                    return;
                }
                k--;
            }
        }

        /* access modifiers changed from: package-private */
        public int getK() {
            return this.finalK;
        }

        /* access modifiers changed from: package-private */
        public double getRemA() {
            return this.finalRemA;
        }

        /* access modifiers changed from: package-private */
        public double getRemB() {
            return this.finalRemB;
        }
    }
}
