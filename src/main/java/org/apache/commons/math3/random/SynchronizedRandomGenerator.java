package org.apache.commons.math3.random;

public class SynchronizedRandomGenerator implements RandomGenerator {
    private final RandomGenerator wrapped;

    public SynchronizedRandomGenerator(RandomGenerator rng) {
        this.wrapped = rng;
    }

    @Override // org.apache.commons.math3.random.RandomGenerator
    public synchronized void setSeed(int seed) {
        this.wrapped.setSeed(seed);
    }

    @Override // org.apache.commons.math3.random.RandomGenerator
    public synchronized void setSeed(int[] seed) {
        this.wrapped.setSeed(seed);
    }

    @Override // org.apache.commons.math3.random.RandomGenerator
    public synchronized void setSeed(long seed) {
        this.wrapped.setSeed(seed);
    }

    @Override // org.apache.commons.math3.random.RandomGenerator
    public synchronized void nextBytes(byte[] bytes) {
        this.wrapped.nextBytes(bytes);
    }

    @Override // org.apache.commons.math3.random.RandomGenerator
    public synchronized int nextInt() {
        return this.wrapped.nextInt();
    }

    @Override // org.apache.commons.math3.random.RandomGenerator
    public synchronized int nextInt(int n) {
        return this.wrapped.nextInt(n);
    }

    @Override // org.apache.commons.math3.random.RandomGenerator
    public synchronized long nextLong() {
        return this.wrapped.nextLong();
    }

    @Override // org.apache.commons.math3.random.RandomGenerator
    public synchronized boolean nextBoolean() {
        return this.wrapped.nextBoolean();
    }

    @Override // org.apache.commons.math3.random.RandomGenerator
    public synchronized float nextFloat() {
        return this.wrapped.nextFloat();
    }

    @Override // org.apache.commons.math3.random.RandomGenerator
    public synchronized double nextDouble() {
        return this.wrapped.nextDouble();
    }

    @Override // org.apache.commons.math3.random.RandomGenerator
    public synchronized double nextGaussian() {
        return this.wrapped.nextGaussian();
    }
}
