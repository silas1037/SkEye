package org.apache.commons.math3.fitting;

import java.util.Collection;
import org.apache.commons.math3.analysis.MultivariateMatrixFunction;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;

public abstract class AbstractCurveFitter {
    /* access modifiers changed from: protected */
    public abstract LeastSquaresProblem getProblem(Collection<WeightedObservedPoint> collection);

    public double[] fit(Collection<WeightedObservedPoint> points) {
        return getOptimizer().optimize(getProblem(points)).getPoint().toArray();
    }

    /* access modifiers changed from: protected */
    public LeastSquaresOptimizer getOptimizer() {
        return new LevenbergMarquardtOptimizer();
    }

    protected static class TheoreticalValuesFunction {

        /* renamed from: f */
        private final ParametricUnivariateFunction f173f;
        private final double[] points;

        public TheoreticalValuesFunction(ParametricUnivariateFunction f, Collection<WeightedObservedPoint> observations) {
            this.f173f = f;
            this.points = new double[observations.size()];
            int i = 0;
            for (WeightedObservedPoint obs : observations) {
                this.points[i] = obs.getX();
                i++;
            }
        }

        public MultivariateVectorFunction getModelFunction() {
            return new MultivariateVectorFunction() {
                /* class org.apache.commons.math3.fitting.AbstractCurveFitter.TheoreticalValuesFunction.C02211 */

                @Override // org.apache.commons.math3.analysis.MultivariateVectorFunction
                public double[] value(double[] p) {
                    int len = TheoreticalValuesFunction.this.points.length;
                    double[] values = new double[len];
                    for (int i = 0; i < len; i++) {
                        values[i] = TheoreticalValuesFunction.this.f173f.value(TheoreticalValuesFunction.this.points[i], p);
                    }
                    return values;
                }
            };
        }

        public MultivariateMatrixFunction getModelFunctionJacobian() {
            return new MultivariateMatrixFunction() {
                /* class org.apache.commons.math3.fitting.AbstractCurveFitter.TheoreticalValuesFunction.C02222 */

                @Override // org.apache.commons.math3.analysis.MultivariateMatrixFunction
                public double[][] value(double[] p) {
                    int len = TheoreticalValuesFunction.this.points.length;
                    double[][] jacobian = new double[len][];
                    for (int i = 0; i < len; i++) {
                        jacobian[i] = TheoreticalValuesFunction.this.f173f.gradient(TheoreticalValuesFunction.this.points[i], p);
                    }
                    return jacobian;
                }
            };
        }
    }
}
