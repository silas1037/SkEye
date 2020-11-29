package org.apache.commons.math3.fitting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.math3.analysis.function.Gaussian;
import org.apache.commons.math3.analysis.integration.BaseAbstractUnivariateIntegrator;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.ZeroException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.fitting.AbstractCurveFitter;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresBuilder;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math3.linear.DiagonalMatrix;
import org.apache.commons.math3.util.FastMath;

public class GaussianCurveFitter extends AbstractCurveFitter {
    private static final Gaussian.Parametric FUNCTION = new Gaussian.Parametric() {
        /* class org.apache.commons.math3.fitting.GaussianCurveFitter.C02251 */

        @Override // org.apache.commons.math3.analysis.function.Gaussian.Parametric, org.apache.commons.math3.analysis.ParametricUnivariateFunction
        public double value(double x, double... p) {
            try {
                return super.value(x, p);
            } catch (NotStrictlyPositiveException e) {
                return Double.POSITIVE_INFINITY;
            }
        }

        @Override // org.apache.commons.math3.analysis.function.Gaussian.Parametric, org.apache.commons.math3.analysis.ParametricUnivariateFunction
        public double[] gradient(double x, double... p) {
            double[] v = {Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY};
            try {
                return super.gradient(x, p);
            } catch (NotStrictlyPositiveException e) {
                return v;
            }
        }
    };
    private final double[] initialGuess;
    private final int maxIter;

    private GaussianCurveFitter(double[] initialGuess2, int maxIter2) {
        this.initialGuess = initialGuess2;
        this.maxIter = maxIter2;
    }

    public static GaussianCurveFitter create() {
        return new GaussianCurveFitter(null, BaseAbstractUnivariateIntegrator.DEFAULT_MAX_ITERATIONS_COUNT);
    }

    public GaussianCurveFitter withStartPoint(double[] newStart) {
        return new GaussianCurveFitter((double[]) newStart.clone(), this.maxIter);
    }

    public GaussianCurveFitter withMaxIterations(int newMaxIter) {
        return new GaussianCurveFitter(this.initialGuess, newMaxIter);
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
        private final double mean;
        private final double norm;
        private final double sigma;

        public ParameterGuesser(Collection<WeightedObservedPoint> observations) {
            if (observations == null) {
                throw new NullArgumentException(LocalizedFormats.INPUT_ARRAY, new Object[0]);
            } else if (observations.size() < 3) {
                throw new NumberIsTooSmallException(Integer.valueOf(observations.size()), 3, true);
            } else {
                double[] params = basicGuess((WeightedObservedPoint[]) sortObservations(observations).toArray(new WeightedObservedPoint[0]));
                this.norm = params[0];
                this.mean = params[1];
                this.sigma = params[2];
            }
        }

        public double[] guess() {
            return new double[]{this.norm, this.mean, this.sigma};
        }

        private List<WeightedObservedPoint> sortObservations(Collection<WeightedObservedPoint> unsorted) {
            List<WeightedObservedPoint> observations = new ArrayList<>(unsorted);
            Collections.sort(observations, new Comparator<WeightedObservedPoint>() {
                /* class org.apache.commons.math3.fitting.GaussianCurveFitter.ParameterGuesser.C02261 */

                public int compare(WeightedObservedPoint p1, WeightedObservedPoint p2) {
                    if (p1 == null && p2 == null) {
                        return 0;
                    }
                    if (p1 == null) {
                        return -1;
                    }
                    if (p2 == null) {
                        return 1;
                    }
                    int cmpX = Double.compare(p1.getX(), p2.getX());
                    if (cmpX < 0) {
                        return -1;
                    }
                    if (cmpX > 0) {
                        return 1;
                    }
                    int cmpY = Double.compare(p1.getY(), p2.getY());
                    if (cmpY < 0) {
                        return -1;
                    }
                    if (cmpY > 0) {
                        return 1;
                    }
                    int cmpW = Double.compare(p1.getWeight(), p2.getWeight());
                    if (cmpW < 0) {
                        return -1;
                    }
                    return cmpW > 0 ? 1 : 0;
                }
            });
            return observations;
        }

        private double[] basicGuess(WeightedObservedPoint[] points) {
            double fwhmApprox;
            int maxYIdx = findMaxY(points);
            double n = points[maxYIdx].getY();
            double m = points[maxYIdx].getX();
            double halfY = n + ((m - n) / 2.0d);
            try {
                fwhmApprox = interpolateXAtY(points, maxYIdx, 1, halfY) - interpolateXAtY(points, maxYIdx, -1, halfY);
            } catch (OutOfRangeException e) {
                fwhmApprox = points[points.length - 1].getX() - points[0].getX();
            }
            return new double[]{n, m, fwhmApprox / (2.0d * FastMath.sqrt(2.0d * FastMath.log(2.0d)))};
        }

        private int findMaxY(WeightedObservedPoint[] points) {
            int maxYIdx = 0;
            for (int i = 1; i < points.length; i++) {
                if (points[i].getY() > points[maxYIdx].getY()) {
                    maxYIdx = i;
                }
            }
            return maxYIdx;
        }

        private double interpolateXAtY(WeightedObservedPoint[] points, int startIdx, int idxStep, double y) throws OutOfRangeException {
            if (idxStep == 0) {
                throw new ZeroException();
            }
            WeightedObservedPoint[] twoPoints = getInterpolationPointsForY(points, startIdx, idxStep, y);
            WeightedObservedPoint p1 = twoPoints[0];
            WeightedObservedPoint p2 = twoPoints[1];
            if (p1.getY() == y) {
                return p1.getX();
            }
            if (p2.getY() == y) {
                return p2.getX();
            }
            return p1.getX() + (((y - p1.getY()) * (p2.getX() - p1.getX())) / (p2.getY() - p1.getY()));
        }

        private WeightedObservedPoint[] getInterpolationPointsForY(WeightedObservedPoint[] points, int startIdx, int idxStep, double y) throws OutOfRangeException {
            if (idxStep == 0) {
                throw new ZeroException();
            }
            int i = startIdx;
            while (true) {
                if (idxStep >= 0) {
                    if (i + idxStep >= points.length) {
                        break;
                    }
                } else if (i + idxStep < 0) {
                    break;
                }
                WeightedObservedPoint p1 = points[i];
                WeightedObservedPoint p2 = points[i + idxStep];
                if (!isBetween(y, p1.getY(), p2.getY())) {
                    i += idxStep;
                } else if (idxStep < 0) {
                    return new WeightedObservedPoint[]{p2, p1};
                } else {
                    return new WeightedObservedPoint[]{p1, p2};
                }
            }
            throw new OutOfRangeException(Double.valueOf(y), Double.valueOf(Double.NEGATIVE_INFINITY), Double.valueOf(Double.POSITIVE_INFINITY));
        }

        private boolean isBetween(double value, double boundary1, double boundary2) {
            return (value >= boundary1 && value <= boundary2) || (value >= boundary2 && value <= boundary1);
        }
    }
}
