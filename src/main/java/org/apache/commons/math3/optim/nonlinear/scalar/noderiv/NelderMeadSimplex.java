package org.apache.commons.math3.optim.nonlinear.scalar.noderiv;

import java.util.Comparator;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.PointValuePair;

public class NelderMeadSimplex extends AbstractSimplex {
    private static final double DEFAULT_GAMMA = 0.5d;
    private static final double DEFAULT_KHI = 2.0d;
    private static final double DEFAULT_RHO = 1.0d;
    private static final double DEFAULT_SIGMA = 0.5d;
    private final double gamma;
    private final double khi;
    private final double rho;
    private final double sigma;

    public NelderMeadSimplex(int n) {
        this(n, DEFAULT_RHO);
    }

    public NelderMeadSimplex(int n, double sideLength) {
        this(n, sideLength, DEFAULT_RHO, DEFAULT_KHI, 0.5d, 0.5d);
    }

    public NelderMeadSimplex(int n, double sideLength, double rho2, double khi2, double gamma2, double sigma2) {
        super(n, sideLength);
        this.rho = rho2;
        this.khi = khi2;
        this.gamma = gamma2;
        this.sigma = sigma2;
    }

    public NelderMeadSimplex(int n, double rho2, double khi2, double gamma2, double sigma2) {
        this(n, DEFAULT_RHO, rho2, khi2, gamma2, sigma2);
    }

    public NelderMeadSimplex(double[] steps) {
        this(steps, (double) DEFAULT_RHO, (double) DEFAULT_KHI, 0.5d, 0.5d);
    }

    public NelderMeadSimplex(double[] steps, double rho2, double khi2, double gamma2, double sigma2) {
        super(steps);
        this.rho = rho2;
        this.khi = khi2;
        this.gamma = gamma2;
        this.sigma = sigma2;
    }

    public NelderMeadSimplex(double[][] referenceSimplex) {
        this(referenceSimplex, (double) DEFAULT_RHO, (double) DEFAULT_KHI, 0.5d, 0.5d);
    }

    public NelderMeadSimplex(double[][] referenceSimplex, double rho2, double khi2, double gamma2, double sigma2) {
        super(referenceSimplex);
        this.rho = rho2;
        this.khi = khi2;
        this.gamma = gamma2;
        this.sigma = sigma2;
    }

    @Override // org.apache.commons.math3.optim.nonlinear.scalar.noderiv.AbstractSimplex
    public void iterate(MultivariateFunction evaluationFunction, Comparator<PointValuePair> comparator) {
        int n = getDimension();
        PointValuePair best = getPoint(0);
        PointValuePair secondBest = getPoint(n - 1);
        PointValuePair worst = getPoint(n);
        double[] xWorst = worst.getPointRef();
        double[] centroid = new double[n];
        for (int i = 0; i < n; i++) {
            double[] x = getPoint(i).getPointRef();
            for (int j = 0; j < n; j++) {
                centroid[j] = centroid[j] + x[j];
            }
        }
        double scaling = DEFAULT_RHO / ((double) n);
        for (int j2 = 0; j2 < n; j2++) {
            centroid[j2] = centroid[j2] * scaling;
        }
        double[] xR = new double[n];
        for (int j3 = 0; j3 < n; j3++) {
            xR[j3] = centroid[j3] + (this.rho * (centroid[j3] - xWorst[j3]));
        }
        PointValuePair reflected = new PointValuePair(xR, evaluationFunction.value(xR), false);
        if (comparator.compare(best, reflected) <= 0 && comparator.compare(reflected, secondBest) < 0) {
            replaceWorstPoint(reflected, comparator);
        } else if (comparator.compare(reflected, best) < 0) {
            double[] xE = new double[n];
            for (int j4 = 0; j4 < n; j4++) {
                xE[j4] = centroid[j4] + (this.khi * (xR[j4] - centroid[j4]));
            }
            PointValuePair expanded = new PointValuePair(xE, evaluationFunction.value(xE), false);
            if (comparator.compare(expanded, reflected) < 0) {
                replaceWorstPoint(expanded, comparator);
            } else {
                replaceWorstPoint(reflected, comparator);
            }
        } else {
            if (comparator.compare(reflected, worst) < 0) {
                double[] xC = new double[n];
                for (int j5 = 0; j5 < n; j5++) {
                    xC[j5] = centroid[j5] + (this.gamma * (xR[j5] - centroid[j5]));
                }
                PointValuePair outContracted = new PointValuePair(xC, evaluationFunction.value(xC), false);
                if (comparator.compare(outContracted, reflected) <= 0) {
                    replaceWorstPoint(outContracted, comparator);
                    return;
                }
            } else {
                double[] xC2 = new double[n];
                for (int j6 = 0; j6 < n; j6++) {
                    xC2[j6] = centroid[j6] - (this.gamma * (centroid[j6] - xWorst[j6]));
                }
                PointValuePair inContracted = new PointValuePair(xC2, evaluationFunction.value(xC2), false);
                if (comparator.compare(inContracted, worst) < 0) {
                    replaceWorstPoint(inContracted, comparator);
                    return;
                }
            }
            double[] xSmallest = getPoint(0).getPointRef();
            for (int i2 = 1; i2 <= n; i2++) {
                double[] x2 = getPoint(i2).getPoint();
                for (int j7 = 0; j7 < n; j7++) {
                    x2[j7] = xSmallest[j7] + (this.sigma * (x2[j7] - xSmallest[j7]));
                }
                setPoint(i2, new PointValuePair(x2, Double.NaN, false));
            }
            evaluate(evaluationFunction, comparator);
        }
    }
}
