package org.apache.commons.math3.analysis.integration;

import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.util.FastMath;

public class RombergIntegrator extends BaseAbstractUnivariateIntegrator {
    public static final int ROMBERG_MAX_ITERATIONS_COUNT = 32;

    public RombergIntegrator(double relativeAccuracy, double absoluteAccuracy, int minimalIterationCount, int maximalIterationCount) throws NotStrictlyPositiveException, NumberIsTooSmallException, NumberIsTooLargeException {
        super(relativeAccuracy, absoluteAccuracy, minimalIterationCount, maximalIterationCount);
        if (maximalIterationCount > 32) {
            throw new NumberIsTooLargeException(Integer.valueOf(maximalIterationCount), 32, false);
        }
    }

    public RombergIntegrator(int minimalIterationCount, int maximalIterationCount) throws NotStrictlyPositiveException, NumberIsTooSmallException, NumberIsTooLargeException {
        super(minimalIterationCount, maximalIterationCount);
        if (maximalIterationCount > 32) {
            throw new NumberIsTooLargeException(Integer.valueOf(maximalIterationCount), 32, false);
        }
    }

    public RombergIntegrator() {
        super(3, 32);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.analysis.integration.BaseAbstractUnivariateIntegrator
    public double doIntegrate() throws TooManyEvaluationsException, MaxCountExceededException {
        double s;
        int m = getMaximalIterationCount() + 1;
        double[] previousRow = new double[m];
        double[] currentRow = new double[m];
        TrapezoidIntegrator qtrap = new TrapezoidIntegrator();
        currentRow[0] = qtrap.stage(this, 0);
        incrementCount();
        double olds = currentRow[0];
        while (true) {
            int i = getIterations();
            previousRow = currentRow;
            currentRow = previousRow;
            currentRow[0] = qtrap.stage(this, i);
            incrementCount();
            for (int j = 1; j <= i; j++) {
                double tIJm1 = currentRow[j - 1];
                currentRow[j] = ((tIJm1 - previousRow[j - 1]) / ((double) ((1 << (j * 2)) - 1))) + tIJm1;
            }
            s = currentRow[i];
            if (i >= getMinimalIterationCount()) {
                double delta = FastMath.abs(s - olds);
                if (delta <= getRelativeAccuracy() * (FastMath.abs(olds) + FastMath.abs(s)) * 0.5d || delta <= getAbsoluteAccuracy()) {
                    return s;
                }
            }
            olds = s;
        }
        return s;
    }
}
