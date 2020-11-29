package org.apache.commons.math3.stat.interval;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class ConfidenceInterval {
    private double confidenceLevel;
    private double lowerBound;
    private double upperBound;

    public ConfidenceInterval(double lowerBound2, double upperBound2, double confidenceLevel2) {
        checkParameters(lowerBound2, upperBound2, confidenceLevel2);
        this.lowerBound = lowerBound2;
        this.upperBound = upperBound2;
        this.confidenceLevel = confidenceLevel2;
    }

    public double getLowerBound() {
        return this.lowerBound;
    }

    public double getUpperBound() {
        return this.upperBound;
    }

    public double getConfidenceLevel() {
        return this.confidenceLevel;
    }

    public String toString() {
        return "[" + this.lowerBound + ";" + this.upperBound + "] (confidence level:" + this.confidenceLevel + ")";
    }

    private void checkParameters(double lower, double upper, double confidence) {
        if (lower >= upper) {
            throw new MathIllegalArgumentException(LocalizedFormats.LOWER_BOUND_NOT_BELOW_UPPER_BOUND, Double.valueOf(lower), Double.valueOf(upper));
        } else if (confidence <= 0.0d || confidence >= 1.0d) {
            throw new MathIllegalArgumentException(LocalizedFormats.OUT_OF_BOUNDS_CONFIDENCE_LEVEL, Double.valueOf(confidence), 0, 1);
        }
    }
}
