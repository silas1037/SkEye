package org.apache.commons.math3.special;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;

public class BesselJ implements UnivariateFunction {
    private static final double ENMTEN = 8.9E-308d;
    private static final double ENSIG = 1.0E16d;
    private static final double ENTEN = 1.0E308d;
    private static final double[] FACT = {1.0d, 1.0d, 2.0d, 6.0d, 24.0d, 120.0d, 720.0d, 5040.0d, 40320.0d, 362880.0d, 3628800.0d, 3.99168E7d, 4.790016E8d, 6.2270208E9d, 8.71782912E10d, 1.307674368E12d, 2.0922789888E13d, 3.55687428096E14d, 6.402373705728E15d, 1.21645100408832E17d, 2.43290200817664E18d, 5.109094217170944E19d, 1.1240007277776077E21d, 2.585201673888498E22d, 6.204484017332394E23d};
    private static final double PI2 = 0.6366197723675814d;
    private static final double RTNSIG = 1.0E-4d;
    private static final double TOWPI1 = 6.28125d;
    private static final double TWOPI = 6.283185307179586d;
    private static final double TWOPI2 = 0.001935307179586477d;
    private static final double X_MAX = 10000.0d;
    private static final double X_MIN = 0.0d;
    private final double order;

    public BesselJ(double order2) {
        this.order = order2;
    }

    @Override // org.apache.commons.math3.analysis.UnivariateFunction
    public double value(double x) throws MathIllegalArgumentException, ConvergenceException {
        return value(this.order, x);
    }

    public static double value(double order2, double x) throws MathIllegalArgumentException, ConvergenceException {
        int n = (int) order2;
        int nb = n + 1;
        BesselJResult res = rjBesl(x, order2 - ((double) n), nb);
        if (res.nVals >= nb) {
            return res.vals[n];
        }
        if (res.nVals < 0) {
            throw new MathIllegalArgumentException(LocalizedFormats.BESSEL_FUNCTION_BAD_ARGUMENT, Double.valueOf(order2), Double.valueOf(x));
        } else if (FastMath.abs(res.vals[res.nVals - 1]) < 1.0E-100d) {
            return res.vals[n];
        } else {
            throw new ConvergenceException(LocalizedFormats.BESSEL_FUNCTION_FAILED_CONVERGENCE, Double.valueOf(order2), Double.valueOf(x));
        }
    }

    public static class BesselJResult {
        private final int nVals;
        private final double[] vals;

        public BesselJResult(double[] b, int n) {
            this.vals = MathArrays.copyOf(b, b.length);
            this.nVals = n;
        }

        public double[] getVals() {
            return MathArrays.copyOf(this.vals, this.vals.length);
        }

        public int getnVals() {
            return this.nVals;
        }
    }

    public static BesselJResult rjBesl(double x, double alpha, int nb) {
        int ncalc;
        int nend;
        int m;
        double tover;
        double[] b = new double[nb];
        int magx = (int) x;
        if (nb <= 0 || x < 0.0d || x > X_MAX || alpha < 0.0d || alpha >= 1.0d) {
            if (b.length > 0) {
                b[0] = 0.0d;
            }
            ncalc = FastMath.min(nb, 0) - 1;
        } else {
            ncalc = nb;
            for (int i = 0; i < nb; i++) {
                b[i] = 0.0d;
            }
            if (x < RTNSIG) {
                double tempa = 1.0d;
                double alpem = 1.0d + alpha;
                double halfx = 0.0d;
                if (x > ENMTEN) {
                    halfx = 0.5d * x;
                }
                if (alpha != 0.0d) {
                    tempa = FastMath.pow(halfx, alpha) / (Gamma.gamma(alpha) * alpha);
                }
                double tempb = 0.0d;
                if (1.0d + x > 1.0d) {
                    tempb = (-halfx) * halfx;
                }
                b[0] = ((tempa * tempb) / alpem) + tempa;
                if (x != 0.0d && b[0] == 0.0d) {
                    ncalc = 0;
                }
                if (nb != 1) {
                    if (x <= 0.0d) {
                        for (int n = 1; n < nb; n++) {
                            b[n] = 0.0d;
                        }
                    } else {
                        if (tempb != 0.0d) {
                            tover = ENMTEN / tempb;
                        } else {
                            tover = 1.78E-307d / x;
                        }
                        for (int n2 = 1; n2 < nb; n2++) {
                            double tempa2 = tempa / alpem;
                            alpem += 1.0d;
                            tempa = tempa2 * halfx;
                            if (tempa <= tover * alpem) {
                                tempa = 0.0d;
                            }
                            b[n2] = ((tempa * tempb) / alpem) + tempa;
                            if (b[n2] == 0.0d && ncalc > n2) {
                                ncalc = n2;
                            }
                        }
                    }
                }
            } else if (x <= 25.0d || nb > magx + 1) {
                int n3 = magx + 1;
                double en = 2.0d * (((double) n3) + alpha);
                double plast = 1.0d;
                double p = en / x;
                double test = 2.0E16d;
                boolean readyToInitialize = false;
                if (nb - magx >= 3) {
                    int nstart = magx + 2;
                    int nend2 = nb - 1;
                    en = 2.0d * (((double) (nstart - 1)) + alpha);
                    int k = nstart;
                    while (true) {
                        if (k > nend2) {
                            break;
                        }
                        n3 = k;
                        en += 2.0d;
                        plast = p;
                        p = ((en * plast) / x) - plast;
                        if (p > 1.0E292d) {
                            double p2 = p / ENTEN;
                            plast /= ENTEN;
                            double psave = p2;
                            double psavel = plast;
                            int nstart2 = n3 + 1;
                            do {
                                n3++;
                                en += 2.0d;
                                plast = p2;
                                p2 = ((en * plast) / x) - plast;
                            } while (p2 <= 1.0d);
                            double tempb2 = en / x;
                            test = ((plast * plast) * (0.5d - (0.5d / (tempb2 * tempb2)))) / ENSIG;
                            p = plast * ENTEN;
                            n3--;
                            en -= 2.0d;
                            nend2 = FastMath.min(nb, n3);
                            int l = nstart2;
                            while (true) {
                                if (l > nend2) {
                                    break;
                                }
                                psavel = psave;
                                psave = ((en * psavel) / x) - psavel;
                                if (psave * psavel > test) {
                                    int ncalc2 = l - 1;
                                    break;
                                }
                                l++;
                            }
                            ncalc = nend2;
                            readyToInitialize = true;
                        } else {
                            k++;
                        }
                    }
                    if (!readyToInitialize) {
                        n3 = nend2;
                        en = 2.0d * (((double) n3) + alpha);
                        test = FastMath.max(test, FastMath.sqrt(ENSIG * plast) * FastMath.sqrt(2.0d * p));
                    }
                }
                if (!readyToInitialize) {
                    do {
                        n3++;
                        en += 2.0d;
                        plast = p;
                        p = ((en * plast) / x) - plast;
                    } while (p < test);
                }
                int n4 = n3 + 1;
                double en2 = en + 2.0d;
                double tempb3 = 0.0d;
                double tempa3 = 1.0d / p;
                int m2 = (n4 * 2) - ((n4 / 2) * 4);
                double sum = 0.0d;
                double em = (double) (n4 / 2);
                double alpem2 = (em - 1.0d) + alpha;
                double alp2em = (2.0d * em) + alpha;
                if (m2 != 0) {
                    sum = ((tempa3 * alpem2) * alp2em) / em;
                }
                int nend3 = n4 - nb;
                boolean readyToNormalize = false;
                boolean calculatedB0 = false;
                for (int l2 = 1; l2 <= nend3; l2++) {
                    n4--;
                    en2 -= 2.0d;
                    tempb3 = tempa3;
                    tempa3 = ((en2 * tempb3) / x) - tempb3;
                    m2 = 2 - m2;
                    if (m2 != 0) {
                        em -= 1.0d;
                        double alp2em2 = (2.0d * em) + alpha;
                        if (n4 == 1) {
                            break;
                        }
                        double alpem3 = (em - 1.0d) + alpha;
                        if (alpem3 == 0.0d) {
                            alpem3 = 1.0d;
                        }
                        sum = (((tempa3 * alp2em2) + sum) * alpem3) / em;
                    }
                }
                b[n4 - 1] = tempa3;
                if (nend3 >= 0) {
                    if (nb <= 1) {
                        double alp2em3 = alpha;
                        if (1.0d + alpha == 1.0d) {
                            alp2em3 = 1.0d;
                        }
                        sum += b[0] * alp2em3;
                        readyToNormalize = true;
                    } else {
                        n4--;
                        en2 -= 2.0d;
                        b[n4 - 1] = ((en2 * tempa3) / x) - tempb3;
                        if (n4 == 1) {
                            calculatedB0 = true;
                        } else {
                            m2 = 2 - m2;
                            if (m2 != 0) {
                                em -= 1.0d;
                                double alp2em4 = (2.0d * em) + alpha;
                                double alpem4 = (em - 1.0d) + alpha;
                                if (alpem4 == 0.0d) {
                                    alpem4 = 1.0d;
                                }
                                sum = (((b[n4 - 1] * alp2em4) + sum) * alpem4) / em;
                            }
                        }
                    }
                }
                if (!readyToNormalize && !calculatedB0 && n4 - 2 != 0) {
                    for (int l3 = 1; l3 <= nend; l3++) {
                        n4--;
                        en2 -= 2.0d;
                        b[n4 - 1] = ((b[n4] * en2) / x) - b[n4 + 1];
                        m2 = 2 - m2;
                        if (m2 != 0) {
                            em -= 1.0d;
                            double alp2em5 = (2.0d * em) + alpha;
                            double alpem5 = (em - 1.0d) + alpha;
                            if (alpem5 == 0.0d) {
                                alpem5 = 1.0d;
                            }
                            sum = (((b[n4 - 1] * alp2em5) + sum) * alpem5) / em;
                        }
                    }
                }
                if (!readyToNormalize) {
                    if (!calculatedB0) {
                        b[0] = (((2.0d * (1.0d + alpha)) * b[1]) / x) - b[2];
                    }
                    double alp2em6 = (2.0d * (em - 1.0d)) + alpha;
                    if (alp2em6 == 0.0d) {
                        alp2em6 = 1.0d;
                    }
                    sum += b[0] * alp2em6;
                }
                if (FastMath.abs(alpha) > 1.0E-16d) {
                    sum *= Gamma.gamma(alpha) * FastMath.pow(0.5d * x, -alpha);
                }
                double tempa4 = ENMTEN;
                if (sum > 1.0d) {
                    tempa4 = ENMTEN * sum;
                }
                for (int n5 = 0; n5 < nb; n5++) {
                    if (FastMath.abs(b[n5]) < tempa4) {
                        b[n5] = 0.0d;
                    }
                    b[n5] = b[n5] / sum;
                }
            } else {
                double xc = FastMath.sqrt(PI2 / x);
                double mul = 0.125d / x;
                double xin = mul * mul;
                if (x >= 130.0d) {
                    m = 4;
                } else if (x >= 35.0d) {
                    m = 8;
                } else {
                    m = 11;
                }
                double xm = 4.0d * ((double) m);
                double t = (double) ((int) ((x / 6.283185307179586d) + 0.5d));
                double z = ((x - (TOWPI1 * t)) - (TWOPI2 * t)) - ((0.5d + alpha) / PI2);
                double vsin = FastMath.sin(z);
                double vcos = FastMath.cos(z);
                double gnu = 2.0d * alpha;
                for (int i2 = 1; i2 <= 2; i2++) {
                    double s = ((xm - 1.0d) - gnu) * ((xm - 1.0d) + gnu) * xin * 0.5d;
                    double t2 = (gnu - (xm - 3.0d)) * ((xm - 3.0d) + gnu);
                    double capp = (s * t2) / FACT[m * 2];
                    double capq = (s * ((gnu - (1.0d + xm)) * ((1.0d + xm) + gnu))) / FACT[(m * 2) + 1];
                    double xk = xm;
                    int k2 = m * 2;
                    double t1 = t2;
                    for (int j = 2; j <= m; j++) {
                        xk -= 4.0d;
                        double s2 = ((xk - 1.0d) - gnu) * ((xk - 1.0d) + gnu);
                        double t3 = (gnu - (xk - 3.0d)) * ((xk - 3.0d) + gnu);
                        capp = ((1.0d / FACT[k2 - 2]) + capp) * s2 * t3 * xin;
                        capq = ((1.0d / FACT[k2 - 1]) + capq) * s2 * t1 * xin;
                        k2 -= 2;
                        t1 = t3;
                    }
                    b[i2 - 1] = (((capp + 1.0d) * vcos) - ((((1.0d + capq) * ((gnu * gnu) - 1.0d)) * (0.125d / x)) * vsin)) * xc;
                    if (nb == 1) {
                        return new BesselJResult(MathArrays.copyOf(b, b.length), ncalc);
                    }
                    vsin = -vcos;
                    vcos = vsin;
                    gnu += 2.0d;
                }
                if (nb > 2) {
                    double gnu2 = (2.0d * alpha) + 2.0d;
                    for (int j2 = 2; j2 < nb; j2++) {
                        b[j2] = ((b[j2 - 1] * gnu2) / x) - b[j2 - 2];
                        gnu2 += 2.0d;
                    }
                }
            }
        }
        return new BesselJResult(MathArrays.copyOf(b, b.length), ncalc);
    }
}
