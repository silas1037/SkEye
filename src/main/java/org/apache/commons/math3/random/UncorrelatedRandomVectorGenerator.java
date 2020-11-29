package org.apache.commons.math3.random;

import java.util.Arrays;
import org.apache.commons.math3.exception.DimensionMismatchException;

public class UncorrelatedRandomVectorGenerator implements RandomVectorGenerator {
    private final NormalizedRandomGenerator generator;
    private final double[] mean;
    private final double[] standardDeviation;

    public UncorrelatedRandomVectorGenerator(double[] mean2, double[] standardDeviation2, NormalizedRandomGenerator generator2) {
        if (mean2.length != standardDeviation2.length) {
            throw new DimensionMismatchException(mean2.length, standardDeviation2.length);
        }
        this.mean = (double[]) mean2.clone();
        this.standardDeviation = (double[]) standardDeviation2.clone();
        this.generator = generator2;
    }

    public UncorrelatedRandomVectorGenerator(int dimension, NormalizedRandomGenerator generator2) {
        this.mean = new double[dimension];
        this.standardDeviation = new double[dimension];
        Arrays.fill(this.standardDeviation, 1.0d);
        this.generator = generator2;
    }

    @Override // org.apache.commons.math3.random.RandomVectorGenerator
    public double[] nextVector() {
        double[] random = new double[this.mean.length];
        for (int i = 0; i < random.length; i++) {
            random[i] = this.mean[i] + (this.standardDeviation[i] * this.generator.nextNormalizedDouble());
        }
        return random;
    }
}
