package org.apache.commons.math3.optim.univariate;

import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.optim.OptimizationData;

public class SearchInterval implements OptimizationData {
    private final double lower;
    private final double start;
    private final double upper;

    public SearchInterval(double lo, double hi, double init) {
        if (lo >= hi) {
            throw new NumberIsTooLargeException(Double.valueOf(lo), Double.valueOf(hi), false);
        } else if (init < lo || init > hi) {
            throw new OutOfRangeException(Double.valueOf(init), Double.valueOf(lo), Double.valueOf(hi));
        } else {
            this.lower = lo;
            this.upper = hi;
            this.start = init;
        }
    }

    public SearchInterval(double lo, double hi) {
        this(lo, hi, 0.5d * (lo + hi));
    }

    public double getMin() {
        return this.lower;
    }

    public double getMax() {
        return this.upper;
    }

    public double getStartValue() {
        return this.start;
    }
}
