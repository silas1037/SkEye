package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.OutOfRangeException;

public interface IntegerDistribution {
    double cumulativeProbability(int i);

    double cumulativeProbability(int i, int i2) throws NumberIsTooLargeException;

    double getNumericalMean();

    double getNumericalVariance();

    int getSupportLowerBound();

    int getSupportUpperBound();

    int inverseCumulativeProbability(double d) throws OutOfRangeException;

    boolean isSupportConnected();

    double probability(int i);

    void reseedRandomGenerator(long j);

    int sample();

    int[] sample(int i);
}
