package org.apache.commons.math3.analysis.polynomials;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.fraction.BigFraction;
import org.apache.commons.math3.util.CombinatoricsUtils;
import org.apache.commons.math3.util.FastMath;

public class PolynomialsUtils {
    private static final List<BigFraction> CHEBYSHEV_COEFFICIENTS = new ArrayList();
    private static final List<BigFraction> HERMITE_COEFFICIENTS = new ArrayList();
    private static final Map<JacobiKey, List<BigFraction>> JACOBI_COEFFICIENTS = new HashMap();
    private static final List<BigFraction> LAGUERRE_COEFFICIENTS = new ArrayList();
    private static final List<BigFraction> LEGENDRE_COEFFICIENTS = new ArrayList();

    /* access modifiers changed from: private */
    public interface RecurrenceCoefficientsGenerator {
        BigFraction[] generate(int i);
    }

    static {
        CHEBYSHEV_COEFFICIENTS.add(BigFraction.ONE);
        CHEBYSHEV_COEFFICIENTS.add(BigFraction.ZERO);
        CHEBYSHEV_COEFFICIENTS.add(BigFraction.ONE);
        HERMITE_COEFFICIENTS.add(BigFraction.ONE);
        HERMITE_COEFFICIENTS.add(BigFraction.ZERO);
        HERMITE_COEFFICIENTS.add(BigFraction.TWO);
        LAGUERRE_COEFFICIENTS.add(BigFraction.ONE);
        LAGUERRE_COEFFICIENTS.add(BigFraction.ONE);
        LAGUERRE_COEFFICIENTS.add(BigFraction.MINUS_ONE);
        LEGENDRE_COEFFICIENTS.add(BigFraction.ONE);
        LEGENDRE_COEFFICIENTS.add(BigFraction.ZERO);
        LEGENDRE_COEFFICIENTS.add(BigFraction.ONE);
    }

    private PolynomialsUtils() {
    }

    public static PolynomialFunction createChebyshevPolynomial(int degree) {
        return buildPolynomial(degree, CHEBYSHEV_COEFFICIENTS, new RecurrenceCoefficientsGenerator() {
            /* class org.apache.commons.math3.analysis.polynomials.PolynomialsUtils.C02071 */
            private final BigFraction[] coeffs = {BigFraction.ZERO, BigFraction.TWO, BigFraction.ONE};

            @Override // org.apache.commons.math3.analysis.polynomials.PolynomialsUtils.RecurrenceCoefficientsGenerator
            public BigFraction[] generate(int k) {
                return this.coeffs;
            }
        });
    }

    public static PolynomialFunction createHermitePolynomial(int degree) {
        return buildPolynomial(degree, HERMITE_COEFFICIENTS, new RecurrenceCoefficientsGenerator() {
            /* class org.apache.commons.math3.analysis.polynomials.PolynomialsUtils.C02082 */

            @Override // org.apache.commons.math3.analysis.polynomials.PolynomialsUtils.RecurrenceCoefficientsGenerator
            public BigFraction[] generate(int k) {
                return new BigFraction[]{BigFraction.ZERO, BigFraction.TWO, new BigFraction(k * 2)};
            }
        });
    }

    public static PolynomialFunction createLaguerrePolynomial(int degree) {
        return buildPolynomial(degree, LAGUERRE_COEFFICIENTS, new RecurrenceCoefficientsGenerator() {
            /* class org.apache.commons.math3.analysis.polynomials.PolynomialsUtils.C02093 */

            @Override // org.apache.commons.math3.analysis.polynomials.PolynomialsUtils.RecurrenceCoefficientsGenerator
            public BigFraction[] generate(int k) {
                int kP1 = k + 1;
                return new BigFraction[]{new BigFraction((k * 2) + 1, kP1), new BigFraction(-1, kP1), new BigFraction(k, kP1)};
            }
        });
    }

    public static PolynomialFunction createLegendrePolynomial(int degree) {
        return buildPolynomial(degree, LEGENDRE_COEFFICIENTS, new RecurrenceCoefficientsGenerator() {
            /* class org.apache.commons.math3.analysis.polynomials.PolynomialsUtils.C02104 */

            @Override // org.apache.commons.math3.analysis.polynomials.PolynomialsUtils.RecurrenceCoefficientsGenerator
            public BigFraction[] generate(int k) {
                int kP1 = k + 1;
                return new BigFraction[]{BigFraction.ZERO, new BigFraction(k + kP1, kP1), new BigFraction(k, kP1)};
            }
        });
    }

    public static PolynomialFunction createJacobiPolynomial(int degree, final int v, final int w) {
        JacobiKey key = new JacobiKey(v, w);
        if (!JACOBI_COEFFICIENTS.containsKey(key)) {
            List<BigFraction> list = new ArrayList<>();
            JACOBI_COEFFICIENTS.put(key, list);
            list.add(BigFraction.ONE);
            list.add(new BigFraction(v - w, 2));
            list.add(new BigFraction(v + 2 + w, 2));
        }
        return buildPolynomial(degree, JACOBI_COEFFICIENTS.get(key), new RecurrenceCoefficientsGenerator() {
            /* class org.apache.commons.math3.analysis.polynomials.PolynomialsUtils.C02115 */

            @Override // org.apache.commons.math3.analysis.polynomials.PolynomialsUtils.RecurrenceCoefficientsGenerator
            public BigFraction[] generate(int k) {
                int k2 = k + 1;
                int kvw = v + k2 + w;
                int twoKvw = kvw + k2;
                int twoKvwM1 = twoKvw - 1;
                int twoKvwM2 = twoKvw - 2;
                int den = k2 * 2 * kvw * twoKvwM2;
                return new BigFraction[]{new BigFraction(((v * v) - (w * w)) * twoKvwM1, den), new BigFraction(twoKvwM1 * twoKvw * twoKvwM2, den), new BigFraction(((v + k2) - 1) * 2 * ((w + k2) - 1) * twoKvw, den)};
            }
        });
    }

    private static class JacobiKey {

        /* renamed from: v */
        private final int f140v;

        /* renamed from: w */
        private final int f141w;

        JacobiKey(int v, int w) {
            this.f140v = v;
            this.f141w = w;
        }

        public int hashCode() {
            return (this.f140v << 16) ^ this.f141w;
        }

        public boolean equals(Object key) {
            if (key == null || !(key instanceof JacobiKey)) {
                return false;
            }
            JacobiKey otherK = (JacobiKey) key;
            if (this.f140v == otherK.f140v && this.f141w == otherK.f141w) {
                return true;
            }
            return false;
        }
    }

    public static double[] shift(double[] coefficients, double shift) {
        int dp1 = coefficients.length;
        double[] newCoefficients = new double[dp1];
        int[][] coeff = (int[][]) Array.newInstance(Integer.TYPE, dp1, dp1);
        for (int i = 0; i < dp1; i++) {
            for (int j = 0; j <= i; j++) {
                coeff[i][j] = (int) CombinatoricsUtils.binomialCoefficient(i, j);
            }
        }
        for (int i2 = 0; i2 < dp1; i2++) {
            newCoefficients[0] = newCoefficients[0] + (coefficients[i2] * FastMath.pow(shift, i2));
        }
        int d = dp1 - 1;
        for (int i3 = 0; i3 < d; i3++) {
            for (int j2 = i3; j2 < d; j2++) {
                int i4 = i3 + 1;
                newCoefficients[i4] = newCoefficients[i4] + (((double) coeff[j2 + 1][j2 - i3]) * coefficients[j2 + 1] * FastMath.pow(shift, j2 - i3));
            }
        }
        return newCoefficients;
    }

    private static PolynomialFunction buildPolynomial(int degree, List<BigFraction> coefficients, RecurrenceCoefficientsGenerator generator) {
        synchronized (coefficients) {
            int maxDegree = ((int) FastMath.floor(FastMath.sqrt((double) (coefficients.size() * 2)))) - 1;
            if (degree > maxDegree) {
                computeUpToDegree(degree, maxDegree, generator, coefficients);
            }
        }
        int start = ((degree + 1) * degree) / 2;
        double[] a = new double[(degree + 1)];
        for (int i = 0; i <= degree; i++) {
            a[i] = coefficients.get(start + i).doubleValue();
        }
        return new PolynomialFunction(a);
    }

    private static void computeUpToDegree(int degree, int maxDegree, RecurrenceCoefficientsGenerator generator, List<BigFraction> coefficients) {
        int startK = ((maxDegree - 1) * maxDegree) / 2;
        for (int k = maxDegree; k < degree; k++) {
            startK += k;
            BigFraction[] ai = generator.generate(k);
            BigFraction ck = coefficients.get(startK);
            coefficients.add(ck.multiply(ai[0]).subtract(coefficients.get(startK).multiply(ai[2])));
            for (int i = 1; i < k; i++) {
                ck = coefficients.get(startK + i);
                coefficients.add(ck.multiply(ai[0]).add(ck.multiply(ai[1])).subtract(coefficients.get(startK + i).multiply(ai[2])));
            }
            BigFraction ck2 = coefficients.get(startK + k);
            coefficients.add(ck2.multiply(ai[0]).add(ck.multiply(ai[1])));
            coefficients.add(ck2.multiply(ai[1]));
        }
    }
}
