package org.apache.commons.math3.random;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.util.FastMath;

public abstract class AbstractRandomGenerator implements RandomGenerator {
    private double cachedNormalDeviate = Double.NaN;

    @Override // org.apache.commons.math3.random.RandomGenerator
    public abstract double nextDouble();

    @Override // org.apache.commons.math3.random.RandomGenerator
    public abstract void setSeed(long j);

    public void clear() {
        this.cachedNormalDeviate = Double.NaN;
    }

    @Override // org.apache.commons.math3.random.RandomGenerator
    public void setSeed(int seed) {
        setSeed((long) seed);
    }

    @Override // org.apache.commons.math3.random.RandomGenerator
    public void setSeed(int[] seed) {
        long combined = 0;
        for (int s : seed) {
            combined = (4294967291L * combined) + ((long) s);
        }
        setSeed(combined);
    }

    @Override // org.apache.commons.math3.random.RandomGenerator
    public void nextBytes(byte[] bytes) {
        int bytesOut = 0;
        while (bytesOut < bytes.length) {
            int randInt = nextInt();
            for (int i = 0; i < 3; i++) {
                if (i > 0) {
                    randInt >>= 8;
                }
                bytesOut++;
                bytes[bytesOut] = (byte) randInt;
                if (bytesOut == bytes.length) {
                    return;
                }
            }
            bytesOut = bytesOut;
        }
    }

    @Override // org.apache.commons.math3.random.RandomGenerator
    public int nextInt() {
        return (int) (((2.0d * nextDouble()) - 1.0d) * 2.147483647E9d);
    }

    @Override // org.apache.commons.math3.random.RandomGenerator
    public int nextInt(int n) {
        if (n <= 0) {
            throw new NotStrictlyPositiveException(Integer.valueOf(n));
        }
        int result = (int) (nextDouble() * ((double) n));
        return result < n ? result : n - 1;
    }

    @Override // org.apache.commons.math3.random.RandomGenerator
    public long nextLong() {
        return (long) (((2.0d * nextDouble()) - 1.0d) * 9.223372036854776E18d);
    }

    @Override // org.apache.commons.math3.random.RandomGenerator
    public boolean nextBoolean() {
        return nextDouble() <= 0.5d;
    }

    @Override // org.apache.commons.math3.random.RandomGenerator
    public float nextFloat() {
        return (float) nextDouble();
    }

    @Override // org.apache.commons.math3.random.RandomGenerator
    public double nextGaussian() {
        if (!Double.isNaN(this.cachedNormalDeviate)) {
            double dev = this.cachedNormalDeviate;
            this.cachedNormalDeviate = Double.NaN;
            return dev;
        }
        double v1 = 0.0d;
        double v2 = 0.0d;
        double s = 1.0d;
        while (s >= 1.0d) {
            v1 = (2.0d * nextDouble()) - 1.0d;
            v2 = (2.0d * nextDouble()) - 1.0d;
            s = (v1 * v1) + (v2 * v2);
        }
        if (s != 0.0d) {
            s = FastMath.sqrt((-2.0d * FastMath.log(s)) / s);
        }
        this.cachedNormalDeviate = v2 * s;
        return v1 * s;
    }
}
