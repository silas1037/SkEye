package org.apache.commons.math3.optimization;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.linear.RealMatrix;

@Deprecated
public class LeastSquaresConverter implements MultivariateFunction {
    private final MultivariateVectorFunction function;
    private final double[] observations;
    private final RealMatrix scale;
    private final double[] weights;

    public LeastSquaresConverter(MultivariateVectorFunction function2, double[] observations2) {
        this.function = function2;
        this.observations = (double[]) observations2.clone();
        this.weights = null;
        this.scale = null;
    }

    public LeastSquaresConverter(MultivariateVectorFunction function2, double[] observations2, double[] weights2) {
        if (observations2.length != weights2.length) {
            throw new DimensionMismatchException(observations2.length, weights2.length);
        }
        this.function = function2;
        this.observations = (double[]) observations2.clone();
        this.weights = (double[]) weights2.clone();
        this.scale = null;
    }

    public LeastSquaresConverter(MultivariateVectorFunction function2, double[] observations2, RealMatrix scale2) {
        if (observations2.length != scale2.getColumnDimension()) {
            throw new DimensionMismatchException(observations2.length, scale2.getColumnDimension());
        }
        this.function = function2;
        this.observations = (double[]) observations2.clone();
        this.weights = null;
        this.scale = scale2.copy();
    }

    @Override // org.apache.commons.math3.analysis.MultivariateFunction
    public double value(double[] point) {
        double[] residuals = this.function.value(point);
        if (residuals.length != this.observations.length) {
            throw new DimensionMismatchException(residuals.length, this.observations.length);
        }
        for (int i = 0; i < residuals.length; i++) {
            residuals[i] = residuals[i] - this.observations[i];
        }
        double sumSquares = 0.0d;
        if (this.weights != null) {
            for (int i2 = 0; i2 < residuals.length; i2++) {
                double ri = residuals[i2];
                sumSquares += this.weights[i2] * ri * ri;
            }
        } else if (this.scale != null) {
            double[] arr$ = this.scale.operate(residuals);
            for (double yi : arr$) {
                sumSquares += yi * yi;
            }
        } else {
            for (double ri2 : residuals) {
                sumSquares += ri2 * ri2;
            }
        }
        return sumSquares;
    }
}
