package org.apache.commons.math3.optim;

public abstract class AbstractConvergenceChecker<PAIR> implements ConvergenceChecker<PAIR> {
    private final double absoluteThreshold;
    private final double relativeThreshold;

    @Override // org.apache.commons.math3.optim.ConvergenceChecker
    public abstract boolean converged(int i, PAIR pair, PAIR pair2);

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
