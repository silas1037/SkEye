package org.apache.commons.math3.random;

import java.util.Random;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;

public class RandomGeneratorFactory {
    private RandomGeneratorFactory() {
    }

    public static RandomGenerator createRandomGenerator(final Random rng) {
        return new RandomGenerator() {
            /* class org.apache.commons.math3.random.RandomGeneratorFactory.C03261 */

            @Override // org.apache.commons.math3.random.RandomGenerator
            public void setSeed(int seed) {
                rng.setSeed((long) seed);
            }

            @Override // org.apache.commons.math3.random.RandomGenerator
            public void setSeed(int[] seed) {
                rng.setSeed(RandomGeneratorFactory.convertToLong(seed));
            }

            @Override // org.apache.commons.math3.random.RandomGenerator
            public void setSeed(long seed) {
                rng.setSeed(seed);
            }

            @Override // org.apache.commons.math3.random.RandomGenerator
            public void nextBytes(byte[] bytes) {
                rng.nextBytes(bytes);
            }

            @Override // org.apache.commons.math3.random.RandomGenerator
            public int nextInt() {
                return rng.nextInt();
            }

            @Override // org.apache.commons.math3.random.RandomGenerator
            public int nextInt(int n) {
                if (n > 0) {
                    return rng.nextInt(n);
                }
                throw new NotStrictlyPositiveException(Integer.valueOf(n));
            }

            @Override // org.apache.commons.math3.random.RandomGenerator
            public long nextLong() {
                return rng.nextLong();
            }

            @Override // org.apache.commons.math3.random.RandomGenerator
            public boolean nextBoolean() {
                return rng.nextBoolean();
            }

            @Override // org.apache.commons.math3.random.RandomGenerator
            public float nextFloat() {
                return rng.nextFloat();
            }

            @Override // org.apache.commons.math3.random.RandomGenerator
            public double nextDouble() {
                return rng.nextDouble();
            }

            @Override // org.apache.commons.math3.random.RandomGenerator
            public double nextGaussian() {
                return rng.nextGaussian();
            }
        };
    }

    public static long convertToLong(int[] seed) {
        long combined = 0;
        for (int s : seed) {
            combined = (4294967291L * combined) + ((long) s);
        }
        return combined;
    }
}
