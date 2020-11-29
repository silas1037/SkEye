package org.apache.commons.math3.analysis.integration;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.util.FastMath;

public class TrapezoidIntegrator extends BaseAbstractUnivariateIntegrator {
    public static final int TRAPEZOID_MAX_ITERATIONS_COUNT = 64;

    /* renamed from: s */
    private double f124s;

    public TrapezoidIntegrator(double relativeAccuracy, double absoluteAccuracy, int minimalIterationCount, int maximalIterationCount) throws NotStrictlyPositiveException, NumberIsTooSmallException, NumberIsTooLargeException {
        super(relativeAccuracy, absoluteAccuracy, minimalIterationCount, maximalIterationCount);
        if (maximalIterationCount > 64) {
            throw new NumberIsTooLargeException(Integer.valueOf(maximalIterationCount), 64, false);
        }
    }

    public TrapezoidIntegrator(int minimalIterationCount, int maximalIterationCount) throws NotStrictlyPositiveException, NumberIsTooSmallException, NumberIsTooLargeException {
        super(minimalIterationCount, maximalIterationCount);
        if (maximalIterationCount > 64) {
            throw new NumberIsTooLargeException(Integer.valueOf(maximalIterationCount), 64, false);
        }
    }

    public TrapezoidIntegrator() {
        super(3, 64);
    }

    /* access modifiers changed from: package-private */
    public double stage(BaseAbstractUnivariateIntegrator baseIntegrator, int n) throws TooManyEvaluationsException {
        if (n == 0) {
            double max = baseIntegrator.getMax();
            double min = baseIntegrator.getMin();
            this.f124s = 0.5d * (max - min) * (baseIntegrator.computeObjectiveValue(min) + baseIntegrator.computeObjectiveValue(max));
            return this.f124s;
        }
        long np = 1 << (n - 1);
        double sum = 0.0d;
        double max2 = baseIntegrator.getMax();
        double min2 = baseIntegrator.getMin();
        double spacing = (max2 - min2) / ((double) np);
        double x = min2 + (0.5d * spacing);
        for (long i = 0; i < np; i++) {
            sum += baseIntegrator.computeObjectiveValue(x);
            x += spacing;
        }
        this.f124s = 0.5d * (this.f124s + (sum * spacing));
        return this.f124s;
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.analysis.integration.BaseAbstractUnivariateIntegrator
    public double doIntegrate() throws MathIllegalArgumentException, TooManyEvaluationsException, MaxCountExceededException {
        double t;
        double oldt = stage(this, 0);
        incrementCount();
        while (true) {
            int i = getIterations();
            t = stage(this, i);
            if (i >= getMinimalIterationCount()) {
                double delta = FastMath.abs(t - oldt);
                if (delta <= getRelativeAccuracy() * (FastMath.abs(oldt) + FastMath.abs(t)) * 0.5d || delta <= getAbsoluteAccuracy()) {
                    return t;
                }
            }
            oldt = t;
            incrementCount();
        }
        return t;
    }
}
