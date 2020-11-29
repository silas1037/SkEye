package org.apache.commons.math3.primes;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.util.FastMath;

class PollardRho {
    private PollardRho() {
    }

    public static List<Integer> primeFactors(int n) {
        List<Integer> factors = new ArrayList<>();
        int n2 = SmallPrimes.smallTrialDivision(n, factors);
        if (1 != n2) {
            if (SmallPrimes.millerRabinPrimeTest(n2)) {
                factors.add(Integer.valueOf(n2));
            } else {
                int divisor = rhoBrent(n2);
                factors.add(Integer.valueOf(divisor));
                factors.add(Integer.valueOf(n2 / divisor));
            }
        }
        return factors;
    }

    static int rhoBrent(int n) {
        int cst = SmallPrimes.PRIMES_LAST;
        int y = 2;
        int r = 1;
        while (true) {
            for (int i = 0; i < r; i++) {
                y = (int) ((((long) cst) + (((long) y) * ((long) y))) % ((long) n));
            }
            int k = 0;
            do {
                int bound = FastMath.min(25, r - k);
                int q = 1;
                int i2 = -3;
                while (true) {
                    if (i2 >= bound) {
                        break;
                    }
                    y = (int) ((((long) cst) + (((long) y) * ((long) y))) % ((long) n));
                    long divisor = (long) FastMath.abs(y - y);
                    if (0 == divisor) {
                        cst += SmallPrimes.PRIMES_LAST;
                        k = -25;
                        y = 2;
                        r = 1;
                        break;
                    }
                    q = (int) ((divisor * ((long) q)) % ((long) n));
                    if (q == 0) {
                        return gcdPositive(FastMath.abs((int) divisor), n);
                    }
                    i2++;
                }
                int out = gcdPositive(FastMath.abs(q), n);
                if (1 != out) {
                    return out;
                }
                k += 25;
            } while (k < r);
            r *= 2;
        }
    }

    static int gcdPositive(int a, int b) {
        if (a == 0) {
            return b;
        }
        if (b == 0) {
            return a;
        }
        int aTwos = Integer.numberOfTrailingZeros(a);
        int a2 = a >> aTwos;
        int bTwos = Integer.numberOfTrailingZeros(b);
        int b2 = b >> bTwos;
        int shift = FastMath.min(aTwos, bTwos);
        while (a2 != b2) {
            int delta = a2 - b2;
            b2 = FastMath.min(a2, b2);
            int a3 = FastMath.abs(delta);
            a2 = a3 >> Integer.numberOfTrailingZeros(a3);
        }
        return a2 << shift;
    }
}
