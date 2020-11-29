package org.apache.commons.math3.fitting;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.analysis.MultivariateMatrixFunction;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.analysis.integration.BaseAbstractUnivariateIntegrator;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.nonlinear.vector.ModelFunction;
import org.apache.commons.math3.optim.nonlinear.vector.ModelFunctionJacobian;
import org.apache.commons.math3.optim.nonlinear.vector.MultivariateVectorOptimizer;
import org.apache.commons.math3.optim.nonlinear.vector.Target;
import org.apache.commons.math3.optim.nonlinear.vector.Weight;

@Deprecated
public class CurveFitter<T extends ParametricUnivariateFunction> {
    private final List<WeightedObservedPoint> observations = new ArrayList();
    private final MultivariateVectorOptimizer optimizer;

    public CurveFitter(MultivariateVectorOptimizer optimizer2) {
        this.optimizer = optimizer2;
    }

    public void addObservedPoint(double x, double y) {
        addObservedPoint(1.0d, x, y);
    }

    public void addObservedPoint(double weight, double x, double y) {
        this.observations.add(new WeightedObservedPoint(weight, x, y));
    }

    public void addObservedPoint(WeightedObservedPoint observed) {
        this.observations.add(observed);
    }

    public WeightedObservedPoint[] getObservations() {
        return (WeightedObservedPoint[]) this.observations.toArray(new WeightedObservedPoint[this.observations.size()]);
    }

    public void clearObservations() {
        this.observations.clear();
    }

    public double[] fit(T f, double[] initialGuess) {
        return fit(BaseAbstractUnivariateIntegrator.DEFAULT_MAX_ITERATIONS_COUNT, f, initialGuess);
    }

    public double[] fit(int maxEval, T f, double[] initialGuess) {
        double[] target = new double[this.observations.size()];
        double[] weights = new double[this.observations.size()];
        int i = 0;
        for (WeightedObservedPoint point : this.observations) {
            target[i] = point.getY();
            weights[i] = point.getWeight();
            i++;
        }
        CurveFitter<T>.TheoreticalValuesFunction model = new TheoreticalValuesFunction(f);
        return this.optimizer.optimize(new MaxEval(maxEval), model.getModelFunction(), model.getModelFunctionJacobian(), new Target(target), new Weight(weights), new InitialGuess(initialGuess)).getPointRef();
    }

    /* access modifiers changed from: private */
    public class TheoreticalValuesFunction {

        /* renamed from: f */
        private final ParametricUnivariateFunction f174f;

        TheoreticalValuesFunction(ParametricUnivariateFunction f) {
            this.f174f = f;
        }

        public ModelFunction getModelFunction() {
            return new ModelFunction(new MultivariateVectorFunction() {
                /* class org.apache.commons.math3.fitting.CurveFitter.TheoreticalValuesFunction.C02231 */

                @Override // org.apache.commons.math3.analysis.MultivariateVectorFunction
                public double[] value(double[] point) {
                    double[] values = new double[CurveFitter.this.observations.size()];
                    int i = 0;
                    for (WeightedObservedPoint observed : CurveFitter.this.observations) {
                        values[i] = TheoreticalValuesFunction.this.f174f.value(observed.getX(), point);
                        i++;
                    }
                    return values;
                }
            });
        }

        public ModelFunctionJacobian getModelFunctionJacobian() {
            return new ModelFunctionJacobian(new MultivariateMatrixFunction() {
                /* class org.apache.commons.math3.fitting.CurveFitter.TheoreticalValuesFunction.C02242 */

                @Override // org.apache.commons.math3.analysis.MultivariateMatrixFunction
                public double[][] value(double[] point) {
                    double[][] jacobian = new double[CurveFitter.this.observations.size()][];
                    int i = 0;
                    for (WeightedObservedPoint observed : CurveFitter.this.observations) {
                        jacobian[i] = TheoreticalValuesFunction.this.f174f.gradient(observed.getX(), point);
                        i++;
                    }
                    return jacobian;
                }
            });
        }
    }
}
