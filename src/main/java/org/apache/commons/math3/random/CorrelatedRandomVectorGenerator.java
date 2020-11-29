package org.apache.commons.math3.random;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RectangularCholeskyDecomposition;

public class CorrelatedRandomVectorGenerator implements RandomVectorGenerator {
    private final NormalizedRandomGenerator generator;
    private final double[] mean;
    private final double[] normalized;
    private final RealMatrix root;

    public CorrelatedRandomVectorGenerator(double[] mean2, RealMatrix covariance, double small, NormalizedRandomGenerator generator2) {
        int order = covariance.getRowDimension();
        if (mean2.length != order) {
            throw new DimensionMismatchException(mean2.length, order);
        }
        this.mean = (double[]) mean2.clone();
        RectangularCholeskyDecomposition decomposition = new RectangularCholeskyDecomposition(covariance, small);
        this.root = decomposition.getRootMatrix();
        this.generator = generator2;
        this.normalized = new double[decomposition.getRank()];
    }

    public CorrelatedRandomVectorGenerator(RealMatrix covariance, double small, NormalizedRandomGenerator generator2) {
        int order = covariance.getRowDimension();
        this.mean = new double[order];
        for (int i = 0; i < order; i++) {
            this.mean[i] = 0.0d;
        }
        RectangularCholeskyDecomposition decomposition = new RectangularCholeskyDecomposition(covariance, small);
        this.root = decomposition.getRootMatrix();
        this.generator = generator2;
        this.normalized = new double[decomposition.getRank()];
    }

    public NormalizedRandomGenerator getGenerator() {
        return this.generator;
    }

    public int getRank() {
        return this.normalized.length;
    }

    public RealMatrix getRootMatrix() {
        return this.root;
    }

    @Override // org.apache.commons.math3.random.RandomVectorGenerator
    public double[] nextVector() {
        for (int i = 0; i < this.normalized.length; i++) {
            this.normalized[i] = this.generator.nextNormalizedDouble();
        }
        double[] correlated = new double[this.mean.length];
        for (int i2 = 0; i2 < correlated.length; i2++) {
            correlated[i2] = this.mean[i2];
            for (int j = 0; j < this.root.getColumnDimension(); j++) {
                correlated[i2] = correlated[i2] + (this.root.getEntry(i2, j) * this.normalized[j]);
            }
        }
        return correlated;
    }
}
