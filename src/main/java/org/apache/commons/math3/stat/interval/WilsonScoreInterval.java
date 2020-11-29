package org.apache.commons.math3.stat.interval;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.util.FastMath;

public class WilsonScoreInterval implements BinomialConfidenceInterval {
    @Override // org.apache.commons.math3.stat.interval.BinomialConfidenceInterval
    public ConfidenceInterval createInterval(int numberOfTrials, int numberOfSuccesses, double confidenceLevel) {
        IntervalUtils.checkParameters(numberOfTrials, numberOfSuccesses, confidenceLevel);
        double z = new NormalDistribution().inverseCumulativeProbability(1.0d - ((1.0d - confidenceLevel) / 2.0d));
        double zSquared = FastMath.pow(z, 2);
        double mean = ((double) numberOfSuccesses) / ((double) numberOfTrials);
        double factor = 1.0d / (1.0d + ((1.0d / ((double) numberOfTrials)) * zSquared));
        double modifiedSuccessRatio = mean + ((1.0d / ((double) (numberOfTrials * 2))) * zSquared);
        double difference = z * FastMath.sqrt(((1.0d / ((double) numberOfTrials)) * mean * (1.0d - mean)) + ((1.0d / (4.0d * FastMath.pow((double) numberOfTrials, 2))) * zSquared));
        return new ConfidenceInterval(factor * (modifiedSuccessRatio - difference), factor * (modifiedSuccessRatio + difference), confidenceLevel);
    }
}
