package org.apache.commons.math3.optimization.fitting;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.analysis.DifferentiableMultivariateVectorFunction;
import org.apache.commons.math3.analysis.MultivariateMatrixFunction;
import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.MultivariateDifferentiableVectorFunction;
import org.apache.commons.math3.analysis.integration.BaseAbstractUnivariateIntegrator;
import org.apache.commons.math3.optimization.DifferentiableMultivariateVectorOptimizer;
import org.apache.commons.math3.optimization.MultivariateDifferentiableVectorOptimizer;
import org.apache.commons.math3.optimization.PointVectorValuePair;

@Deprecated
public class CurveFitter<T extends ParametricUnivariateFunction> {
    private final List<WeightedObservedPoint> observations;
    @Deprecated
    private final DifferentiableMultivariateVectorOptimizer oldOptimizer;
    private final MultivariateDifferentiableVectorOptimizer optimizer;

    @Deprecated
    public CurveFitter(DifferentiableMultivariateVectorOptimizer optimizer2) {
        this.oldOptimizer = optimizer2;
        this.optimizer = null;
        this.observations = new ArrayList();
    }

    public CurveFitter(MultivariateDifferentiableVectorOptimizer optimizer2) {
        this.oldOptimizer = null;
        this.optimizer = optimizer2;
        this.observations = new ArrayList();
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
        PointVectorValuePair optimum;
        double[] target = new double[this.observations.size()];
        double[] weights = new double[this.observations.size()];
        int i = 0;
        for (WeightedObservedPoint point : this.observations) {
            target[i] = point.getY();
            weights[i] = point.getWeight();
            i++;
        }
        if (this.optimizer == null) {
            optimum = this.oldOptimizer.optimize(maxEval, new OldTheoreticalValuesFunction(f), target, weights, initialGuess);
        } else {
            optimum = this.optimizer.optimize(maxEval, new TheoreticalValuesFunction(f), target, weights, initialGuess);
        }
        return optimum.getPointRef();
    }

    /* access modifiers changed from: private */
    @Deprecated
    public class OldTheoreticalValuesFunction implements DifferentiableMultivariateVectorFunction {

        /* renamed from: f */
        private final ParametricUnivariateFunction f336f;

        OldTheoreticalValuesFunction(ParametricUnivariateFunction f) {
            this.f336f = f;
        }

        @Override // org.apache.commons.math3.analysis.DifferentiableMultivariateVectorFunction
        public MultivariateMatrixFunction jacobian() {
            return new MultivariateMatrixFunction() {
                /* class org.apache.commons.math3.optimization.fitting.CurveFitter.OldTheoreticalValuesFunction.C03201 */

                @Override // org.apache.commons.math3.analysis.MultivariateMatrixFunction
                public double[][] value(double[] point) {
                    double[][] jacobian = new double[CurveFitter.this.observations.size()][];
                    int i = 0;
                    for (WeightedObservedPoint observed : CurveFitter.this.observations) {
                        jacobian[i] = OldTheoreticalValuesFunction.this.f336f.gradient(observed.getX(), point);
                        i++;
                    }
                    return jacobian;
                }
            };
        }

        @Override // org.apache.commons.math3.analysis.MultivariateVectorFunction
        public double[] value(double[] point) {
            double[] values = new double[CurveFitter.this.observations.size()];
            int i = 0;
            for (WeightedObservedPoint observed : CurveFitter.this.observations) {
                values[i] = this.f336f.value(observed.getX(), point);
                i++;
            }
            return values;
        }
    }

    /* access modifiers changed from: private */
    public class TheoreticalValuesFunction implements MultivariateDifferentiableVectorFunction {

        /* renamed from: f */
        private final ParametricUnivariateFunction f337f;

        TheoreticalValuesFunction(ParametricUnivariateFunction f) {
            this.f337f = f;
        }

        @Override // org.apache.commons.math3.analysis.MultivariateVectorFunction
        public double[] value(double[] point) {
            double[] values = new double[CurveFitter.this.observations.size()];
            int i = 0;
            for (WeightedObservedPoint observed : CurveFitter.this.observations) {
                values[i] = this.f337f.value(observed.getX(), point);
                i++;
            }
            return values;
        }

        @Override // org.apache.commons.math3.analysis.differentiation.MultivariateDifferentiableVectorFunction
        public DerivativeStructure[] value(DerivativeStructure[] point) {
            double[] parameters = new double[point.length];
            for (int k = 0; k < point.length; k++) {
                parameters[k] = point[k].getValue();
            }
            DerivativeStructure[] values = new DerivativeStructure[CurveFitter.this.observations.size()];
            int i = 0;
            for (WeightedObservedPoint observed : CurveFitter.this.observations) {
                DerivativeStructure vi = new DerivativeStructure(point.length, 1, this.f337f.value(observed.getX(), parameters));
                for (int k2 = 0; k2 < point.length; k2++) {
                    vi = vi.add(new DerivativeStructure(point.length, 1, k2, 0.0d));
                }
                values[i] = vi;
                i++;
            }
            return values;
        }
    }
}
