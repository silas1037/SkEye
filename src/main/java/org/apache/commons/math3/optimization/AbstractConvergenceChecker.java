package org.apache.commons.math3.optimization;

import org.apache.commons.math3.util.Precision;

@Deprecated
public abstract class AbstractConvergenceChecker<PAIR> implements ConvergenceChecker<PAIR> {
    @Deprecated
    private static final double DEFAULT_ABSOLUTE_THRESHOLD = (Precision.SAFE_MIN * 100.0d);
    @Deprecated
    private static final double DEFAULT_RELATIVE_THRESHOLD = (Precision.EPSILON * 100.0d);
    private final double absoluteThreshold;
    private final double relativeThreshold;

    @Override // org.apache.commons.math3.optimization.ConvergenceChecker
    public abstract boolean converged(int i, PAIR pair, PAIR pair2);

    @Deprecated
    public AbstractConvergenceChecker() {
        this.relativeThreshold = DEFAULT_RELATIVE_THRESHOLD;
        this.absoluteThreshold = DEFAULT_ABSOLUTE_THRESHOLD;
    }

    public AbstractConvergenceChecker(double relativeThreshold2, double absoluteThreshold2) {
        this.relativeThreshold = relativeThreshold2;
        this.absoluteThreshold = absoluteThreshold2;
    }

    public double getRelativeThreshold() {
        return this.relativeThreshold;
    }

    public double getAbsoluteThreshold() {
        return this.absoluteThreshold;
    }
}
