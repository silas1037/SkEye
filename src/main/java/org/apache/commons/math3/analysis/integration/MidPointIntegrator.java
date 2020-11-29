package org.apache.commons.math3.analysis.integration;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.util.FastMath;

public class MidPointIntegrator extends BaseAbstractUnivariateIntegrator {
    public static final int MIDPOINT_MAX_ITERATIONS_COUNT = 64;

    public MidPointIntegrator(double relativeAccuracy, double absoluteAccuracy, int minimalIterationCount, int maximalIterationCount) throws NotStrictlyPositiveException, NumberIsTooSmallException, NumberIsTooLargeException {
        super(relativeAccuracy, absoluteAccuracy, minimalIterationCount, maximalIterationCount);
        if (maximalIterationCount > 64) {
            throw new NumberIsTooLargeException(Integer.valueOf(maximalIterationCount), 64, false);
        }
    }

    public MidPointIntegrator(int minimalIterationCount, int maximalIterationCount) throws NotStrictlyPositiveException, NumberIsTooSmallException, NumberIsTooLargeException {
        super(minimalIterationCount, maximalIterationCount);
        if (maximalIterationCount > 64) {
            throw new NumberIsTooLargeException(Integer.valueOf(maximalIterationCount), 64, false);
        }
    }

    public MidPointIntegrator() {
        super(3, 64);
    }

    private double stage(int n, double previousStageResult, double min, double diffMaxMin) throws TooManyEvaluationsException {
        long np = 1 << (n - 1);
        double sum = 0.0d;
        double spacing = diffMaxMin / ((double) np);
        double x = min + (0.5d * spacing);
        for (long i = 0; i < np; i++) {
            sum += computeObjectiveValue(x);
            x += spacing;
        }
        return 0.5d * ((sum * spacing) + previousStageResult);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.analysis.integration.BaseAbstractUnivariateIntegrator
    public double doIntegrate() throws MathIllegalArgumentException, TooManyEvaluationsException, MaxCountExceededException {
        double t;
        double min = getMin();
        double diff = getMax() - min;
        double oldt = diff * computeObjectiveValue(min + (0.5d * diff));
        while (true) {
            incrementCount();
            int i = getIterations();
            t = stage(i, oldt, min, diff);
            if (i >= getMinimalIterationCount()) {
                double delta = FastMath.abs(t - oldt);
                if (delta <= getRelativeAccuracy() * (FastMath.abs(oldt) + FastMath.abs(t)) * 0.5d || delta <= getAbsoluteAccuracy()) {
                    return t;
                }
            }
            oldt = t;
        }
        return t;
    }
}
