package org.apache.commons.math3.fitting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.math3.analysis.function.HarmonicOscillator;
import org.apache.commons.math3.analysis.integration.BaseAbstractUnivariateIntegrator;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.ZeroException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.fitting.AbstractCurveFitter;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresBuilder;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math3.linear.DiagonalMatrix;
import org.apache.commons.math3.util.FastMath;

public class HarmonicCurveFitter extends AbstractCurveFitter {
    private static final HarmonicOscillator.Parametric FUNCTION = new HarmonicOscillator.Parametric();
    private final double[] initialGuess;
    private final int maxIter;

    private HarmonicCurveFitter(double[] initialGuess2, int maxIter2) {
        this.initialGuess = initialGuess2;
        this.maxIter = maxIter2;
    }

    public static HarmonicCurveFitter create() {
        return new HarmonicCurveFitter(null, BaseAbstractUnivariateIntegrator.DEFAULT_MAX_ITERATIONS_COUNT);
    }

    public HarmonicCurveFitter withStartPoint(double[] newStart) {
        return new HarmonicCurveFitter((double[]) newStart.clone(), this.maxIter);
    }

    public HarmonicCurveFitter withMaxIterations(int newMaxIter) {
        return new HarmonicCurveFitter(this.initialGuess, newMaxIter);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.fitting.AbstractCurveFitter
    public LeastSquaresProblem getProblem(Collection<WeightedObservedPoint> observations) {
        int len = observations.size();
        double[] target = new double[len];
        double[] weights = new double[len];
        int i = 0;
        for (WeightedObservedPoint obs : observations) {
            target[i] = obs.getY();
            weights[i] = obs.getWeight();
            i++;
        }
        AbstractCurveFitter.TheoreticalValuesFunction model = new AbstractCurveFitter.TheoreticalValuesFunction(FUNCTION, observations);
        return new LeastSquaresBuilder().maxEvaluations(BaseAbstractUnivariateIntegrator.DEFAULT_MAX_ITERATIONS_COUNT).maxIterations(this.maxIter).start(this.initialGuess != null ? this.initialGuess : new ParameterGuesser(observations).guess()).target(target).weight(new DiagonalMatrix(weights)).model(model.getModelFunction(), model.getModelFunctionJacobian()).build();
    }

    public static class ParameterGuesser {

        /* renamed from: a */
        private final double f175a;
        private final double omega;
        private final double phi;

        public ParameterGuesser(Collection<WeightedObservedPoint> observations) {
            if (observations.size() < 4) {
                throw new NumberIsTooSmallException(LocalizedFormats.INSUFFICIENT_OBSERVED_POINTS_IN_SAMPLE, Integer.valueOf(observations.size()), 4, true);
            }
            WeightedObservedPoint[] sorted = (WeightedObservedPoint[]) sortObservations(observations).toArray(new WeightedObservedPoint[0]);
            double[] aOmega = guessAOmega(sorted);
            this.f175a = aOmega[0];
            this.omega = aOmega[1];
            this.phi = guessPhi(sorted);
        }

        public double[] guess() {
            return new double[]{this.f175a, this.omega, this.phi};
        }

        private List<WeightedObservedPoint> sortObservations(Collection<WeightedObservedPoint> unsorted) {
            List<WeightedObservedPoint> observations = new ArrayList<>(unsorted);
            WeightedObservedPoint curr = observations.get(0);
            int len = observations.size();
            for (int j = 1; j < len; j++) {
                curr = observations.get(j);
                if (curr.getX() < curr.getX()) {
                    int i = j - 1;
                    WeightedObservedPoint mI = observations.get(i);
                    int i2 = i;
                    while (i2 >= 0 && curr.getX() < mI.getX()) {
                        observations.set(i2 + 1, mI);
                        int i3 = i2 - 1;
                        if (i2 != 0) {
                            mI = observations.get(i3);
                            i2 = i3;
                        } else {
                            i2 = i3;
                        }
                    }
                    observations.set(i2 + 1, curr);
                    curr = observations.get(j);
                }
            }
            return observations;
        }

        private double[] guessAOmega(WeightedObservedPoint[] observations) {
            double[] aOmega = new double[2];
            double sx2 = 0.0d;
            double sy2 = 0.0d;
            double sxy = 0.0d;
            double sxz = 0.0d;
            double syz = 0.0d;
            double currentX = observations[0].getX();
            double currentY = observations[0].getY();
            double f2Integral = 0.0d;
            double fPrime2Integral = 0.0d;
            for (int i = 1; i < observations.length; i++) {
                currentX = observations[i].getX();
                currentY = observations[i].getY();
                double dx = currentX - currentX;
                double dy = currentY - currentY;
                double x = currentX - currentX;
                f2Integral += ((((currentY * currentY) + (currentY * currentY)) + (currentY * currentY)) * dx) / 3.0d;
                fPrime2Integral += (dy * dy) / dx;
                sx2 += x * x;
                sy2 += f2Integral * f2Integral;
                sxy += x * f2Integral;
                sxz += x * fPrime2Integral;
                syz += f2Integral * fPrime2Integral;
            }
            double c1 = (sy2 * sxz) - (sxy * syz);
            double c2 = (sxy * sxz) - (sx2 * syz);
            double c3 = (sx2 * sy2) - (sxy * sxy);
            if (c1 / c2 < 0.0d || c2 / c3 < 0.0d) {
                double xRange = observations[observations.length - 1].getX() - observations[0].getX();
                if (xRange == 0.0d) {
                    throw new ZeroException();
                }
                aOmega[1] = 6.283185307179586d / xRange;
                double yMin = Double.POSITIVE_INFINITY;
                double yMax = Double.NEGATIVE_INFINITY;
                for (int i2 = 1; i2 < observations.length; i2++) {
                    double y = observations[i2].getY();
                    if (y < yMin) {
                        yMin = y;
                    }
                    if (y > yMax) {
                        yMax = y;
                    }
                }
                aOmega[0] = 0.5d * (yMax - yMin);
            } else if (c2 == 0.0d) {
                throw new MathIllegalStateException(LocalizedFormats.ZERO_DENOMINATOR, new Object[0]);
            } else {
                aOmega[0] = FastMath.sqrt(c1 / c2);
                aOmega[1] = FastMath.sqrt(c2 / c3);
            }
            return aOmega;
        }

        private double guessPhi(WeightedObservedPoint[] observations) {
            double fcMean = 0.0d;
            double fsMean = 0.0d;
            double currentX = observations[0].getX();
            double currentY = observations[0].getY();
            for (int i = 1; i < observations.length; i++) {
                currentX = observations[i].getX();
                currentY = observations[i].getY();
                double currentYPrime = (currentY - currentY) / (currentX - currentX);
                double omegaX = this.omega * currentX;
                double cosine = FastMath.cos(omegaX);
                double sine = FastMath.sin(omegaX);
                fcMean += ((this.omega * currentY) * cosine) - (currentYPrime * sine);
                fsMean += (this.omega * currentY * sine) + (currentYPrime * cosine);
            }
            return FastMath.atan2(-fsMean, fcMean);
        }
    }
}
