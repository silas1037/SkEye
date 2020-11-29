package org.apache.commons.math3.distribution;

import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.OutOfRangeException;

public interface RealDistribution {
    double cumulativeProbability(double d);

    @Deprecated
    double cumulativeProbability(double d, double d2) throws NumberIsTooLargeException;

    double density(double d);

    double getNumericalMean();

    double getNumericalVariance();

    double getSupportLowerBound();

    double getSupportUpperBound();

    double inverseCumulativeProbability(double d) throws OutOfRangeException;

    boolean isSupportConnected();

    @Deprecated
    boolean isSupportLowerBoundInclusive();

    @Deprecated
    boolean isSupportUpperBoundInclusive();

    double probability(double d);

    void reseedRandomGenerator(long j);

    double sample();

    double[] sample(int i);
}
