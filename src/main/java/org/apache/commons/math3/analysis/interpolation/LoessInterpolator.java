package org.apache.commons.math3.analysis.interpolation;

import java.io.Serializable;
import java.util.Arrays;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.exception.NotFiniteNumberException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.MathUtils;

public class LoessInterpolator implements UnivariateInterpolator, Serializable {
    public static final double DEFAULT_ACCURACY = 1.0E-12d;
    public static final double DEFAULT_BANDWIDTH = 0.3d;
    public static final int DEFAULT_ROBUSTNESS_ITERS = 2;
    private static final long serialVersionUID = 5204927143605193821L;
    private final double accuracy;
    private final double bandwidth;
    private final int robustnessIters;

    public LoessInterpolator() {
        this.bandwidth = 0.3d;
        this.robustnessIters = 2;
        this.accuracy = 1.0E-12d;
    }

    public LoessInterpolator(double bandwidth2, int robustnessIters2) {
        this(bandwidth2, robustnessIters2, 1.0E-12d);
    }

    public LoessInterpolator(double bandwidth2, int robustnessIters2, double accuracy2) throws OutOfRangeException, NotPositiveException {
        if (bandwidth2 < 0.0d || bandwidth2 > 1.0d) {
            throw new OutOfRangeException(LocalizedFormats.BANDWIDTH, Double.valueOf(bandwidth2), 0, 1);
        }
        this.bandwidth = bandwidth2;
        if (robustnessIters2 < 0) {
            throw new NotPositiveException(LocalizedFormats.ROBUSTNESS_ITERATIONS, Integer.valueOf(robustnessIters2));
        }
        this.robustnessIters = robustnessIters2;
        this.accuracy = accuracy2;
    }

    @Override // org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator
    public final PolynomialSplineFunction interpolate(double[] xval, double[] yval) throws NonMonotonicSequenceException, DimensionMismatchException, NoDataException, NotFiniteNumberException, NumberIsTooSmallException {
        return new SplineInterpolator().interpolate(xval, smooth(xval, yval));
    }

    public final double[] smooth(double[] xval, double[] yval, double[] weights) throws NonMonotonicSequenceException, DimensionMismatchException, NoDataException, NotFiniteNumberException, NumberIsTooSmallException {
        int edge;
        double beta;
        if (xval.length != yval.length) {
            throw new DimensionMismatchException(xval.length, yval.length);
        }
        int n = xval.length;
        if (n == 0) {
            throw new NoDataException();
        }
        checkAllFiniteReal(xval);
        checkAllFiniteReal(yval);
        checkAllFiniteReal(weights);
        MathArrays.checkOrder(xval);
        if (n == 1) {
            return new double[]{yval[0]};
        } else if (n == 2) {
            return new double[]{yval[0], yval[1]};
        } else {
            int bandwidthInPoints = (int) (this.bandwidth * ((double) n));
            if (bandwidthInPoints < 2) {
                throw new NumberIsTooSmallException(LocalizedFormats.BANDWIDTH, Integer.valueOf(bandwidthInPoints), 2, true);
            }
            double[] res = new double[n];
            double[] residuals = new double[n];
            double[] sortedResiduals = new double[n];
            double[] robustnessWeights = new double[n];
            Arrays.fill(robustnessWeights, 1.0d);
            for (int iter = 0; iter <= this.robustnessIters; iter++) {
                int[] bandwidthInterval = {0, bandwidthInPoints - 1};
                int i = 0;
                while (i < n) {
                    double x = xval[i];
                    if (i > 0) {
                        updateBandwidthInterval(xval, weights, i, bandwidthInterval);
                    }
                    int ileft = bandwidthInterval[0];
                    int iright = bandwidthInterval[1];
                    if (xval[i] - xval[ileft] > xval[iright] - xval[i]) {
                        edge = ileft;
                    } else {
                        edge = iright;
                    }
                    double sumWeights = 0.0d;
                    double sumX = 0.0d;
                    double sumXSquared = 0.0d;
                    double sumY = 0.0d;
                    double sumXY = 0.0d;
                    double denom = FastMath.abs(1.0d / (xval[edge] - x));
                    int k = ileft;
                    while (k <= iright) {
                        double xk = xval[k];
                        double yk = yval[k];
                        double w = tricube((k < i ? x - xk : xk - x) * denom) * robustnessWeights[k] * weights[k];
                        double xkw = xk * w;
                        sumWeights += w;
                        sumX += xkw;
                        sumXSquared += xk * xkw;
                        sumY += yk * w;
                        sumXY += yk * xkw;
                        k++;
                    }
                    double meanX = sumX / sumWeights;
                    double meanY = sumY / sumWeights;
                    double meanXY = sumXY / sumWeights;
                    double meanXSquared = sumXSquared / sumWeights;
                    if (FastMath.sqrt(FastMath.abs(meanXSquared - (meanX * meanX))) < this.accuracy) {
                        beta = 0.0d;
                    } else {
                        beta = (meanXY - (meanX * meanY)) / (meanXSquared - (meanX * meanX));
                    }
                    res[i] = (beta * x) + (meanY - (beta * meanX));
                    residuals[i] = FastMath.abs(yval[i] - res[i]);
                    i++;
                }
                if (iter == this.robustnessIters) {
                    return res;
                }
                System.arraycopy(residuals, 0, sortedResiduals, 0, n);
                Arrays.sort(sortedResiduals);
                double medianResidual = sortedResiduals[n / 2];
                if (FastMath.abs(medianResidual) < this.accuracy) {
                    return res;
                }
                for (int i2 = 0; i2 < n; i2++) {
                    double arg = residuals[i2] / (6.0d * medianResidual);
                    if (arg >= 1.0d) {
                        robustnessWeights[i2] = 0.0d;
                    } else {
                        double w2 = 1.0d - (arg * arg);
                        robustnessWeights[i2] = w2 * w2;
                    }
                }
            }
            return res;
        }
    }

    public final double[] smooth(double[] xval, double[] yval) throws NonMonotonicSequenceException, DimensionMismatchException, NoDataException, NotFiniteNumberException, NumberIsTooSmallException {
        if (xval.length != yval.length) {
            throw new DimensionMismatchException(xval.length, yval.length);
        }
        double[] unitWeights = new double[xval.length];
        Arrays.fill(unitWeights, 1.0d);
        return smooth(xval, yval, unitWeights);
    }

    private static void updateBandwidthInterval(double[] xval, double[] weights, int i, int[] bandwidthInterval) {
        int left = bandwidthInterval[0];
        int nextRight = nextNonzero(weights, bandwidthInterval[1]);
        if (nextRight < xval.length && xval[nextRight] - xval[i] < xval[i] - xval[left]) {
            bandwidthInterval[0] = nextNonzero(weights, bandwidthInterval[0]);
            bandwidthInterval[1] = nextRight;
        }
    }

    private static int nextNonzero(double[] weights, int i) {
        int j = i + 1;
        while (j < weights.length && weights[j] == 0.0d) {
            j++;
        }
        return j;
    }

    private static double tricube(double x) {
        double absX = FastMath.abs(x);
        if (absX >= 1.0d) {
            return 0.0d;
        }
        double tmp = 1.0d - ((absX * absX) * absX);
        return tmp * tmp * tmp;
    }

    private static void checkAllFiniteReal(double[] values) {
        for (double d : values) {
            MathUtils.checkFinite(d);
        }
    }
}
