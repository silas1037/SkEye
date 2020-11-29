package org.apache.commons.math3.stat.interval;

import org.apache.commons.math3.distribution.FDistribution;

public class ClopperPearsonInterval implements BinomialConfidenceInterval {
    @Override // org.apache.commons.math3.stat.interval.BinomialConfidenceInterval
    public ConfidenceInterval createInterval(int numberOfTrials, int numberOfSuccesses, double confidenceLevel) {
        IntervalUtils.checkParameters(numberOfTrials, numberOfSuccesses, confidenceLevel);
        double lowerBound = 0.0d;
        double upperBound = 0.0d;
        double alpha = (1.0d - confidenceLevel) / 2.0d;
        double fValueLowerBound = new FDistribution((double) (((numberOfTrials - numberOfSuccesses) + 1) * 2), (double) (numberOfSuccesses * 2)).inverseCumulativeProbability(1.0d - alpha);
        if (numberOfSuccesses > 0) {
            lowerBound = ((double) numberOfSuccesses) / (((double) numberOfSuccesses) + (((double) ((numberOfTrials - numberOfSuccesses) + 1)) * fValueLowerBound));
        }
        double fValueUpperBound = new FDistribution((double) ((numberOfSuccesses + 1) * 2), (double) ((numberOfTrials - numberOfSuccesses) * 2)).inverseCumulativeProbability(1.0d - alpha);
        if (numberOfSuccesses > 0) {
            upperBound = (((double) (numberOfSuccesses + 1)) * fValueUpperBound) / (((double) (numberOfTrials - numberOfSuccesses)) + (((double) (numberOfSuccesses + 1)) * fValueUpperBound));
        }
        return new ConfidenceInterval(lowerBound, upperBound, confidenceLevel);
    }
}
