package org.apache.commons.math3.optim.nonlinear.scalar.noderiv;

import java.util.Comparator;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.PointValuePair;

public class MultiDirectionalSimplex extends AbstractSimplex {
    private static final double DEFAULT_GAMMA = 0.5d;
    private static final double DEFAULT_KHI = 2.0d;
    private final double gamma;
    private final double khi;

    public MultiDirectionalSimplex(int n) {
        this(n, 1.0d);
    }

    public MultiDirectionalSimplex(int n, double sideLength) {
        this(n, sideLength, DEFAULT_KHI, DEFAULT_GAMMA);
    }

    public MultiDirectionalSimplex(int n, double khi2, double gamma2) {
        this(n, 1.0d, khi2, gamma2);
    }

    public MultiDirectionalSimplex(int n, double sideLength, double khi2, double gamma2) {
        super(n, sideLength);
        this.khi = khi2;
        this.gamma = gamma2;
    }

    public MultiDirectionalSimplex(double[] steps) {
        this(steps, (double) DEFAULT_KHI, (double) DEFAULT_GAMMA);
    }

    public MultiDirectionalSimplex(double[] steps, double khi2, double gamma2) {
        super(steps);
        this.khi = khi2;
        this.gamma = gamma2;
    }

    public MultiDirectionalSimplex(double[][] referenceSimplex) {
        this(referenceSimplex, (double) DEFAULT_KHI, (double) DEFAULT_GAMMA);
    }

    public MultiDirectionalSimplex(double[][] referenceSimplex, double khi2, double gamma2) {
        super(referenceSimplex);
        this.khi = khi2;
        this.gamma = gamma2;
    }

    @Override // org.apache.commons.math3.optim.nonlinear.scalar.noderiv.AbstractSimplex
    public void iterate(MultivariateFunction evaluationFunction, Comparator<PointValuePair> comparator) {
        PointValuePair[] original = getPoints();
        PointValuePair best = original[0];
        PointValuePair reflected = evaluateNewSimplex(evaluationFunction, original, 1.0d, comparator);
        if (comparator.compare(reflected, best) < 0) {
            PointValuePair[] reflectedSimplex = getPoints();
            if (comparator.compare(reflected, evaluateNewSimplex(evaluationFunction, original, this.khi, comparator)) <= 0) {
                setPoints(reflectedSimplex);
                return;
            }
            return;
        }
        evaluateNewSimplex(evaluationFunction, original, this.gamma, comparator);
    }

    private PointValuePair evaluateNewSimplex(MultivariateFunction evaluationFunction, PointValuePair[] original, double coeff, Comparator<PointValuePair> comparator) {
        double[] xSmallest = original[0].getPointRef();
        setPoint(0, original[0]);
        int dim = getDimension();
        for (int i = 1; i < getSize(); i++) {
            double[] xOriginal = original[i].getPointRef();
            double[] xTransformed = new double[dim];
            for (int j = 0; j < dim; j++) {
                xTransformed[j] = xSmallest[j] + ((xSmallest[j] - xOriginal[j]) * coeff);
            }
            setPoint(i, new PointValuePair(xTransformed, Double.NaN, false));
        }
        evaluate(evaluationFunction, comparator);
        return getPoint(0);
    }
}
