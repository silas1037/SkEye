package org.apache.commons.math3.analysis.integration;

import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.util.FastMath;

public class SimpsonIntegrator extends BaseAbstractUnivariateIntegrator {
    public static final int SIMPSON_MAX_ITERATIONS_COUNT = 64;

    public SimpsonIntegrator(double relativeAccuracy, double absoluteAccuracy, int minimalIterationCount, int maximalIterationCount) throws NotStrictlyPositiveException, NumberIsTooSmallException, NumberIsTooLargeException {
        super(relativeAccuracy, absoluteAccuracy, minimalIterationCount, maximalIterationCount);
        if (maximalIterationCount > 64) {
            throw new NumberIsTooLargeException(Integer.valueOf(maximalIterationCount), 64, false);
        }
    }

    public SimpsonIntegrator(int minimalIterationCount, int maximalIterationCount) throws NotStrictlyPositiveException, NumberIsTooSmallException, NumberIsTooLargeException {
        super(minimalIterationCount, maximalIterationCount);
        if (maximalIterationCount > 64) {
            throw new NumberIsTooLargeException(Integer.valueOf(maximalIterationCount), 64, false);
        }
    }

    public SimpsonIntegrator() {
        super(3, 64);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.analysis.integration.BaseAbstractUnivariateIntegrator
    public double doIntegrate() throws TooManyEvaluationsException, MaxCountExceededException {
        TrapezoidIntegrator qtrap = new TrapezoidIntegrator();
        if (getMinimalIterationCount() == 1) {
            return ((4.0d * qtrap.stage(this, 1)) - qtrap.stage(this, 0)) / 3.0d;
        }
        double olds = 0.0d;
        double oldt = qtrap.stage(this, 0);
        while (true) {
            double t = qtrap.stage(this, getIterations());
            incrementCount();
            double s = ((4.0d * t) - oldt) / 3.0d;
            if (getIterations() >= getMinimalIterationCount()) {
                double delta = FastMath.abs(s - olds);
                if (delta <= getRelativeAccuracy() * (FastMath.abs(olds) + FastMath.abs(s)) * 0.5d || delta <= getAbsoluteAccuracy()) {
                    return s;
                }
            }
            olds = s;
            oldt = t;
        }
    }
}
