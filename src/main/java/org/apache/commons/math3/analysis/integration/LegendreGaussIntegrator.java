package org.apache.commons.math3.analysis.integration;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;

@Deprecated
public class LegendreGaussIntegrator extends BaseAbstractUnivariateIntegrator {
    private static final double[] ABSCISSAS_2 = {-1.0d / FastMath.sqrt(3.0d), 1.0d / FastMath.sqrt(3.0d)};
    private static final double[] ABSCISSAS_3 = {-FastMath.sqrt(0.6d), 0.0d, FastMath.sqrt(0.6d)};
    private static final double[] ABSCISSAS_4 = {-FastMath.sqrt((15.0d + (2.0d * FastMath.sqrt(30.0d))) / 35.0d), -FastMath.sqrt((15.0d - (2.0d * FastMath.sqrt(30.0d))) / 35.0d), FastMath.sqrt((15.0d - (2.0d * FastMath.sqrt(30.0d))) / 35.0d), FastMath.sqrt((15.0d + (2.0d * FastMath.sqrt(30.0d))) / 35.0d)};
    private static final double[] ABSCISSAS_5 = {-FastMath.sqrt((35.0d + (2.0d * FastMath.sqrt(70.0d))) / 63.0d), -FastMath.sqrt((35.0d - (2.0d * FastMath.sqrt(70.0d))) / 63.0d), 0.0d, FastMath.sqrt((35.0d - (2.0d * FastMath.sqrt(70.0d))) / 63.0d), FastMath.sqrt((35.0d + (2.0d * FastMath.sqrt(70.0d))) / 63.0d)};
    private static final double[] WEIGHTS_2 = {1.0d, 1.0d};
    private static final double[] WEIGHTS_3 = {0.5555555555555556d, 0.8888888888888888d, 0.5555555555555556d};
    private static final double[] WEIGHTS_4 = {(90.0d - (5.0d * FastMath.sqrt(30.0d))) / 180.0d, (90.0d + (5.0d * FastMath.sqrt(30.0d))) / 180.0d, (90.0d + (5.0d * FastMath.sqrt(30.0d))) / 180.0d, (90.0d - (5.0d * FastMath.sqrt(30.0d))) / 180.0d};
    private static final double[] WEIGHTS_5 = {(322.0d - (13.0d * FastMath.sqrt(70.0d))) / 900.0d, (322.0d + (13.0d * FastMath.sqrt(70.0d))) / 900.0d, 0.5688888888888889d, (322.0d + (13.0d * FastMath.sqrt(70.0d))) / 900.0d, (322.0d - (13.0d * FastMath.sqrt(70.0d))) / 900.0d};
    private final double[] abscissas;
    private final double[] weights;

    public LegendreGaussIntegrator(int n, double relativeAccuracy, double absoluteAccuracy, int minimalIterationCount, int maximalIterationCount) throws MathIllegalArgumentException, NotStrictlyPositiveException, NumberIsTooSmallException {
        super(relativeAccuracy, absoluteAccuracy, minimalIterationCount, maximalIterationCount);
        switch (n) {
            case 2:
                this.abscissas = ABSCISSAS_2;
                this.weights = WEIGHTS_2;
                return;
            case 3:
                this.abscissas = ABSCISSAS_3;
                this.weights = WEIGHTS_3;
                return;
            case 4:
                this.abscissas = ABSCISSAS_4;
                this.weights = WEIGHTS_4;
                return;
            case 5:
                this.abscissas = ABSCISSAS_5;
                this.weights = WEIGHTS_5;
                return;
            default:
                throw new MathIllegalArgumentException(LocalizedFormats.N_POINTS_GAUSS_LEGENDRE_INTEGRATOR_NOT_SUPPORTED, Integer.valueOf(n), 2, 5);
        }
    }

    public LegendreGaussIntegrator(int n, double relativeAccuracy, double absoluteAccuracy) throws MathIllegalArgumentException {
        this(n, relativeAccuracy, absoluteAccuracy, 3, BaseAbstractUnivariateIntegrator.DEFAULT_MAX_ITERATIONS_COUNT);
    }

    public LegendreGaussIntegrator(int n, int minimalIterationCount, int maximalIterationCount) throws MathIllegalArgumentException {
        this(n, 1.0E-6d, 1.0E-15d, minimalIterationCount, maximalIterationCount);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.analysis.integration.BaseAbstractUnivariateIntegrator
    public double doIntegrate() throws MathIllegalArgumentException, TooManyEvaluationsException, MaxCountExceededException {
        double oldt = stage(1);
        int n = 2;
        while (true) {
            double t = stage(n);
            double delta = FastMath.abs(t - oldt);
            double limit = FastMath.max(getAbsoluteAccuracy(), getRelativeAccuracy() * (FastMath.abs(oldt) + FastMath.abs(t)) * 0.5d);
            if (getIterations() + 1 >= getMinimalIterationCount() && delta <= limit) {
                return t;
            }
            n = FastMath.max((int) (((double) n) * FastMath.min(4.0d, FastMath.pow(delta / limit, 0.5d / ((double) this.abscissas.length)))), n + 1);
            oldt = t;
            incrementCount();
        }
    }

    private double stage(int n) throws TooManyEvaluationsException {
        double step = (getMax() - getMin()) / ((double) n);
        double halfStep = step / 2.0d;
        double midPoint = getMin() + halfStep;
        double sum = 0.0d;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < this.abscissas.length; j++) {
                sum += this.weights[j] * computeObjectiveValue((this.abscissas[j] * halfStep) + midPoint);
            }
            midPoint += step;
        }
        return halfStep * sum;
    }
}
