package org.apache.commons.math3.optim;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Pair;

public class SimplePointChecker<PAIR extends Pair<double[], ? extends Object>> extends AbstractConvergenceChecker<PAIR> {
    private static final int ITERATION_CHECK_DISABLED = -1;
    private final int maxIterationCount;

    public SimplePointChecker(double relativeThreshold, double absoluteThreshold) {
        super(relativeThreshold, absoluteThreshold);
        this.maxIterationCount = -1;
    }

    public SimplePointChecker(double relativeThreshold, double absoluteThreshold, int maxIter) {
        super(relativeThreshold, absoluteThreshold);
        if (maxIter <= 0) {
            throw new NotStrictlyPositiveException(Integer.valueOf(maxIter));
        }
        this.maxIterationCount = maxIter;
    }

    public boolean converged(int iteration, PAIR previous, PAIR current) {
        if (this.maxIterationCount != -1 && iteration >= this.maxIterationCount) {
            return true;
        }
        double[] p = (double[]) previous.getKey();
        double[] c = (double[]) current.getKey();
        for (int i = 0; i < p.length; i++) {
            double pi = p[i];
            double ci = c[i];
            double difference = FastMath.abs(pi - ci);
            if (difference > getRelativeThreshold() * FastMath.max(FastMath.abs(pi), FastMath.abs(ci)) && difference > getAbsoluteThreshold()) {
                return false;
            }
        }
        return true;
    }
}
