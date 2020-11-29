package org.apache.commons.math3.fitting;

import java.util.Collection;
import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.analysis.integration.BaseAbstractUnivariateIntegrator;
import org.apache.commons.math3.fitting.AbstractCurveFitter;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresBuilder;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math3.linear.DiagonalMatrix;

public class SimpleCurveFitter extends AbstractCurveFitter {
    private final ParametricUnivariateFunction function;
    private final double[] initialGuess;
    private final int maxIter;

    private SimpleCurveFitter(ParametricUnivariateFunction function2, double[] initialGuess2, int maxIter2) {
        this.function = function2;
        this.initialGuess = initialGuess2;
        this.maxIter = maxIter2;
    }

    public static SimpleCurveFitter create(ParametricUnivariateFunction f, double[] start) {
        return new SimpleCurveFitter(f, start, BaseAbstractUnivariateIntegrator.DEFAULT_MAX_ITERATIONS_COUNT);
    }

    public SimpleCurveFitter withStartPoint(double[] newStart) {
        return new SimpleCurveFitter(this.function, (double[]) newStart.clone(), this.maxIter);
    }

    public SimpleCurveFitter withMaxIterations(int newMaxIter) {
        return new SimpleCurveFitter(this.function, this.initialGuess, newMaxIter);
    }

    /* access modifiers changed from: protected */
    @Override // org.apache.commons.math3.fitting.AbstractCurveFitter
    public LeastSquaresProblem getProblem(Collection<WeightedObservedPoint> observations) {
        int len = observations.size();
        double[] target = new double[len];
        double[] weights = new double[len];
        int count = 0;
        for (WeightedObservedPoint obs : observations) {
            target[count] = obs.getY();
            weights[count] = obs.getWeight();
            count++;
        }
        AbstractCurveFitter.TheoreticalValuesFunction model = new AbstractCurveFitter.TheoreticalValuesFunction(this.function, observations);
        return new LeastSquaresBuilder().maxEvaluations(BaseAbstractUnivariateIntegrator.DEFAULT_MAX_ITERATIONS_COUNT).maxIterations(this.maxIter).start(this.initialGuess).target(target).weight(new DiagonalMatrix(weights)).model(model.getModelFunction(), model.getModelFunctionJacobian()).build();
    }
}
