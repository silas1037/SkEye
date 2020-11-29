package org.apache.commons.math3.analysis.differentiation;

import java.io.Serializable;
import java.lang.reflect.Array;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.UnivariateMatrixFunction;
import org.apache.commons.math3.analysis.UnivariateVectorFunction;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.util.FastMath;

public class FiniteDifferencesDifferentiator implements UnivariateFunctionDifferentiator, UnivariateVectorFunctionDifferentiator, UnivariateMatrixFunctionDifferentiator, Serializable {
    private static final long serialVersionUID = 20120917;
    private final double halfSampleSpan;
    private final int nbPoints;
    private final double stepSize;
    private final double tMax;
    private final double tMin;

    public FiniteDifferencesDifferentiator(int nbPoints2, double stepSize2) throws NotPositiveException, NumberIsTooSmallException {
        this(nbPoints2, stepSize2, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    public FiniteDifferencesDifferentiator(int nbPoints2, double stepSize2, double tLower, double tUpper) throws NotPositiveException, NumberIsTooSmallException, NumberIsTooLargeException {
        if (nbPoints2 <= 1) {
            throw new NumberIsTooSmallException(Double.valueOf(stepSize2), 1, false);
        }
        this.nbPoints = nbPoints2;
        if (stepSize2 <= 0.0d) {
            throw new NotPositiveException(Double.valueOf(stepSize2));
        }
        this.stepSize = stepSize2;
        this.halfSampleSpan = 0.5d * stepSize2 * ((double) (nbPoints2 - 1));
        if (2.0d * this.halfSampleSpan >= tUpper - tLower) {
            throw new NumberIsTooLargeException(Double.valueOf(2.0d * this.halfSampleSpan), Double.valueOf(tUpper - tLower), false);
        }
        double safety = FastMath.ulp(this.halfSampleSpan);
        this.tMin = this.halfSampleSpan + tLower + safety;
        this.tMax = (tUpper - this.halfSampleSpan) - safety;
    }

    public int getNbPoints() {
        return this.nbPoints;
    }

    public double getStepSize() {
        return this.stepSize;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private DerivativeStructure evaluate(DerivativeStructure t, double t0, double[] y) throws NumberIsTooLargeException {
        double[] top = new double[this.nbPoints];
        double[] bottom = new double[this.nbPoints];
        for (int i = 0; i < this.nbPoints; i++) {
            bottom[i] = y[i];
            for (int j = 1; j <= i; j++) {
                bottom[i - j] = (bottom[(i - j) + 1] - bottom[i - j]) / (((double) j) * this.stepSize);
            }
            top[i] = bottom[0];
        }
        int order = t.getOrder();
        int parameters = t.getFreeParameters();
        double[] derivatives = t.getAllDerivatives();
        double dt0 = t.getValue() - t0;
        DerivativeStructure interpolation = new DerivativeStructure(parameters, order, 0.0d);
        DerivativeStructure monomial = null;
        for (int i2 = 0; i2 < this.nbPoints; i2++) {
            if (i2 == 0) {
                monomial = new DerivativeStructure(parameters, order, 1.0d);
            } else {
                derivatives[0] = dt0 - (((double) (i2 - 1)) * this.stepSize);
                monomial = monomial.multiply(new DerivativeStructure(parameters, order, derivatives));
            }
            interpolation = interpolation.add(monomial.multiply(top[i2]));
        }
        return interpolation;
    }

    @Override // org.apache.commons.math3.analysis.differentiation.UnivariateFunctionDifferentiator
    public UnivariateDifferentiableFunction differentiate(final UnivariateFunction function) {
        return new UnivariateDifferentiableFunction() {
            /* class org.apache.commons.math3.analysis.differentiation.FiniteDifferencesDifferentiator.C01931 */

            @Override // org.apache.commons.math3.analysis.UnivariateFunction
            public double value(double x) throws MathIllegalArgumentException {
                return function.value(x);
            }

            @Override // org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction
            public DerivativeStructure value(DerivativeStructure t) throws MathIllegalArgumentException {
                if (t.getOrder() >= FiniteDifferencesDifferentiator.this.nbPoints) {
                    throw new NumberIsTooLargeException(Integer.valueOf(t.getOrder()), Integer.valueOf(FiniteDifferencesDifferentiator.this.nbPoints), false);
                }
                double t0 = FastMath.max(FastMath.min(t.getValue(), FiniteDifferencesDifferentiator.this.tMax), FiniteDifferencesDifferentiator.this.tMin) - FiniteDifferencesDifferentiator.this.halfSampleSpan;
                double[] y = new double[FiniteDifferencesDifferentiator.this.nbPoints];
                for (int i = 0; i < FiniteDifferencesDifferentiator.this.nbPoints; i++) {
                    y[i] = function.value((((double) i) * FiniteDifferencesDifferentiator.this.stepSize) + t0);
                }
                return FiniteDifferencesDifferentiator.this.evaluate(t, t0, y);
            }
        };
    }

    @Override // org.apache.commons.math3.analysis.differentiation.UnivariateVectorFunctionDifferentiator
    public UnivariateDifferentiableVectorFunction differentiate(final UnivariateVectorFunction function) {
        return new UnivariateDifferentiableVectorFunction() {
            /* class org.apache.commons.math3.analysis.differentiation.FiniteDifferencesDifferentiator.C01942 */

            @Override // org.apache.commons.math3.analysis.UnivariateVectorFunction
            public double[] value(double x) throws MathIllegalArgumentException {
                return function.value(x);
            }

            @Override // org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableVectorFunction
            public DerivativeStructure[] value(DerivativeStructure t) throws MathIllegalArgumentException {
                if (t.getOrder() >= FiniteDifferencesDifferentiator.this.nbPoints) {
                    throw new NumberIsTooLargeException(Integer.valueOf(t.getOrder()), Integer.valueOf(FiniteDifferencesDifferentiator.this.nbPoints), false);
                }
                double t0 = FastMath.max(FastMath.min(t.getValue(), FiniteDifferencesDifferentiator.this.tMax), FiniteDifferencesDifferentiator.this.tMin) - FiniteDifferencesDifferentiator.this.halfSampleSpan;
                double[][] y = null;
                for (int i = 0; i < FiniteDifferencesDifferentiator.this.nbPoints; i++) {
                    double[] v = function.value((((double) i) * FiniteDifferencesDifferentiator.this.stepSize) + t0);
                    if (i == 0) {
                        y = (double[][]) Array.newInstance(Double.TYPE, v.length, FiniteDifferencesDifferentiator.this.nbPoints);
                    }
                    for (int j = 0; j < v.length; j++) {
                        y[j][i] = v[j];
                    }
                }
                DerivativeStructure[] value = new DerivativeStructure[y.length];
                for (int j2 = 0; j2 < value.length; j2++) {
                    value[j2] = FiniteDifferencesDifferentiator.this.evaluate(t, t0, y[j2]);
                }
                return value;
            }
        };
    }

    @Override // org.apache.commons.math3.analysis.differentiation.UnivariateMatrixFunctionDifferentiator
    public UnivariateDifferentiableMatrixFunction differentiate(final UnivariateMatrixFunction function) {
        return new UnivariateDifferentiableMatrixFunction() {
            /* class org.apache.commons.math3.analysis.differentiation.FiniteDifferencesDifferentiator.C01953 */

            @Override // org.apache.commons.math3.analysis.UnivariateMatrixFunction
            public double[][] value(double x) throws MathIllegalArgumentException {
                return function.value(x);
            }

            @Override // org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableMatrixFunction
            public DerivativeStructure[][] value(DerivativeStructure t) throws MathIllegalArgumentException {
                if (t.getOrder() >= FiniteDifferencesDifferentiator.this.nbPoints) {
                    throw new NumberIsTooLargeException(Integer.valueOf(t.getOrder()), Integer.valueOf(FiniteDifferencesDifferentiator.this.nbPoints), false);
                }
                double t0 = FastMath.max(FastMath.min(t.getValue(), FiniteDifferencesDifferentiator.this.tMax), FiniteDifferencesDifferentiator.this.tMin) - FiniteDifferencesDifferentiator.this.halfSampleSpan;
                double[][][] y = null;
                for (int i = 0; i < FiniteDifferencesDifferentiator.this.nbPoints; i++) {
                    double[][] v = function.value((((double) i) * FiniteDifferencesDifferentiator.this.stepSize) + t0);
                    if (i == 0) {
                        y = (double[][][]) Array.newInstance(Double.TYPE, v.length, v[0].length, FiniteDifferencesDifferentiator.this.nbPoints);
                    }
                    for (int j = 0; j < v.length; j++) {
                        for (int k = 0; k < v[j].length; k++) {
                            y[j][k][i] = v[j][k];
                        }
                    }
                }
                DerivativeStructure[][] value = (DerivativeStructure[][]) Array.newInstance(DerivativeStructure.class, y.length, y[0].length);
                for (int j2 = 0; j2 < value.length; j2++) {
                    for (int k2 = 0; k2 < y[j2].length; k2++) {
                        value[j2][k2] = FiniteDifferencesDifferentiator.this.evaluate(t, t0, y[j2][k2]);
                    }
                }
                return value;
            }
        };
    }
}
