package org.apache.commons.math3.random;

import java.util.Random;

public class RandomAdaptor extends Random implements RandomGenerator {
    private static final long serialVersionUID = 2306581345647615033L;
    private final RandomGenerator randomGenerator;

    private RandomAdaptor() {
        this.randomGenerator = null;
    }

    public RandomAdaptor(RandomGenerator randomGenerator2) {
        this.randomGenerator = randomGenerator2;
    }

    public static Random createAdaptor(RandomGenerator randomGenerator2) {
        return new RandomAdaptor(randomGenerator2);
    }

    @Override // org.apache.commons.math3.random.RandomGenerator
    public boolean nextBoolean() {
        return this.randomGenerator.nextBoolean();
    }

    @Override // org.apache.commons.math3.random.RandomGenerator
    public void nextBytes(byte[] bytes) {
        this.randomGenerator.nextBytes(bytes);
    }

    @Override // org.apache.commons.math3.random.RandomGenerator
    public double nextDouble() {
        return this.randomGenerator.nextDouble();
    }

    @Override // org.apache.commons.math3.random.RandomGenerator
    public float nextFloat() {
        return this.randomGenerator.nextFloat();
    }

    @Override // org.apache.commons.math3.random.RandomGenerator
    public double nextGaussian() {
        return this.randomGenerator.nextGaussian();
    }

    @Override // org.apache.commons.math3.random.RandomGenerator
    public int nextInt() {
        return this.randomGenerator.nextInt();
    }

    @Override // org.apache.commons.math3.random.RandomGenerator
    public int nextInt(int n) {
        return this.randomGenerator.nextInt(n);
    }

    @Override // org.apache.commons.math3.random.RandomGenerator
    public long nextLong() {
        return this.randomGenerator.nextLong();
    }

    @Override // org.apache.commons.math3.random.RandomGenerator
    public void setSeed(int seed) {
        if (this.randomGenerator != null) {
            this.randomGenerator.setSeed(seed);
        }
    }

    @Override // org.apache.commons.math3.random.RandomGenerator
    public void setSeed(int[] seed) {
        if (this.randomGenerator != null) {
            this.randomGenerator.setSeed(seed);
        }
    }

    @Override // org.apache.commons.math3.random.RandomGenerator
    public void setSeed(long seed) {
        if (this.randomGenerator != null) {
            this.randomGenerator.setSeed(seed);
        }
    }
}
