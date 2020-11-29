package org.apache.commons.math3.analysis;

import java.lang.reflect.Array;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.MultivariateDifferentiableFunction;
import org.apache.commons.math3.analysis.differentiation.MultivariateDifferentiableVectorFunction;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.analysis.function.Identity;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class FunctionUtils {
    private FunctionUtils() {
    }

    public static UnivariateFunction compose(final UnivariateFunction... f) {
        return new UnivariateFunction() {
            /* class org.apache.commons.math3.analysis.FunctionUtils.C01661 */

            @Override // org.apache.commons.math3.analysis.UnivariateFunction
            public double value(double x) {
                double r = x;
                for (int i = f.length - 1; i >= 0; i--) {
                    r = f[i].value(r);
                }
                return r;
            }
        };
    }

    public static UnivariateDifferentiableFunction compose(final UnivariateDifferentiableFunction... f) {
        return new UnivariateDifferentiableFunction() {
            /* class org.apache.commons.math3.analysis.FunctionUtils.C01812 */

            @Override // org.apache.commons.math3.analysis.UnivariateFunction
            public double value(double t) {
                double r = t;
                for (int i = f.length - 1; i >= 0; i--) {
                    r = f[i].value(r);
                }
                return r;
            }

            @Override // org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction
            public DerivativeStructure value(DerivativeStructure t) {
                DerivativeStructure r = t;
                for (int i = f.length - 1; i >= 0; i--) {
                    r = f[i].value(r);
                }
                return r;
            }
        };
    }

    @Deprecated
    public static DifferentiableUnivariateFunction compose(final DifferentiableUnivariateFunction... f) {
        return new DifferentiableUnivariateFunction() {
            /* class org.apache.commons.math3.analysis.FunctionUtils.C01823 */

            @Override // org.apache.commons.math3.analysis.UnivariateFunction
            public double value(double x) {
                double r = x;
                for (int i = f.length - 1; i >= 0; i--) {
                    r = f[i].value(r);
                }
                return r;
            }

            @Override // org.apache.commons.math3.analysis.DifferentiableUnivariateFunction
            public UnivariateFunction derivative() {
                return new UnivariateFunction() {
                    /* class org.apache.commons.math3.analysis.FunctionUtils.C01823.C01831 */

                    @Override // org.apache.commons.math3.analysis.UnivariateFunction
                    public double value(double x) {
                        double p = 1.0d;
                        double r = x;
                        for (int i = f.length - 1; i >= 0; i--) {
                            p *= f[i].derivative().value(r);
                            r = f[i].value(r);
                        }
                        return p;
                    }
                };
            }
        };
    }

    public static UnivariateFunction add(final UnivariateFunction... f) {
        return new UnivariateFunction() {
            /* class org.apache.commons.math3.analysis.FunctionUtils.C01844 */

            @Override // org.apache.commons.math3.analysis.UnivariateFunction
            public double value(double x) {
                double r = f[0].value(x);
                for (int i = 1; i < f.length; i++) {
                    r += f[i].value(x);
                }
                return r;
            }
        };
    }

    public static UnivariateDifferentiableFunction add(final UnivariateDifferentiableFunction... f) {
        return new UnivariateDifferentiableFunction() {
            /* class org.apache.commons.math3.analysis.FunctionUtils.C01855 */

            @Override // org.apache.commons.math3.analysis.UnivariateFunction
            public double value(double t) {
                double r = f[0].value(t);
                for (int i = 1; i < f.length; i++) {
                    r += f[i].value(t);
                }
                return r;
            }

            @Override // org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction
            public DerivativeStructure value(DerivativeStructure t) throws DimensionMismatchException {
                DerivativeStructure r = f[0].value(t);
                for (int i = 1; i < f.length; i++) {
                    r = r.add(f[i].value(t));
                }
                return r;
            }
        };
    }

    @Deprecated
    public static DifferentiableUnivariateFunction add(final DifferentiableUnivariateFunction... f) {
        return new DifferentiableUnivariateFunction() {
            /* class org.apache.commons.math3.analysis.FunctionUtils.C01866 */

            @Override // org.apache.commons.math3.analysis.UnivariateFunction
            public double value(double x) {
                double r = f[0].value(x);
                for (int i = 1; i < f.length; i++) {
                    r += f[i].value(x);
                }
                return r;
            }

            @Override // org.apache.commons.math3.analysis.DifferentiableUnivariateFunction
            public UnivariateFunction derivative() {
                return new UnivariateFunction() {
                    /* class org.apache.commons.math3.analysis.FunctionUtils.C01866.C01871 */

                    @Override // org.apache.commons.math3.analysis.UnivariateFunction
                    public double value(double x) {
                        double r = f[0].derivative().value(x);
                        for (int i = 1; i < f.length; i++) {
                            r += f[i].derivative().value(x);
                        }
                        return r;
                    }
                };
            }
        };
    }

    public static UnivariateFunction multiply(final UnivariateFunction... f) {
        return new UnivariateFunction() {
            /* class org.apache.commons.math3.analysis.FunctionUtils.C01887 */

            @Override // org.apache.commons.math3.analysis.UnivariateFunction
            public double value(double x) {
                double r = f[0].value(x);
                for (int i = 1; i < f.length; i++) {
                    r *= f[i].value(x);
                }
                return r;
            }
        };
    }

    public static UnivariateDifferentiableFunction multiply(final UnivariateDifferentiableFunction... f) {
        return new UnivariateDifferentiableFunction() {
            /* class org.apache.commons.math3.analysis.FunctionUtils.C01898 */

            @Override // org.apache.commons.math3.analysis.UnivariateFunction
            public double value(double t) {
                double r = f[0].value(t);
                for (int i = 1; i < f.length; i++) {
                    r *= f[i].value(t);
                }
                return r;
            }

            @Override // org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction
            public DerivativeStructure value(DerivativeStructure t) {
                DerivativeStructure r = f[0].value(t);
                for (int i = 1; i < f.length; i++) {
                    r = r.multiply(f[i].value(t));
                }
                return r;
            }
        };
    }

    @Deprecated
    public static DifferentiableUnivariateFunction multiply(final DifferentiableUnivariateFunction... f) {
        return new DifferentiableUnivariateFunction() {
            /* class org.apache.commons.math3.analysis.FunctionUtils.C01909 */

            @Override // org.apache.commons.math3.analysis.UnivariateFunction
            public double value(double x) {
                double r = f[0].value(x);
                for (int i = 1; i < f.length; i++) {
                    r *= f[i].value(x);
                }
                return r;
            }

            @Override // org.apache.commons.math3.analysis.DifferentiableUnivariateFunction
            public UnivariateFunction derivative() {
                return new UnivariateFunction() {
                    /* class org.apache.commons.math3.analysis.FunctionUtils.C01909.C01911 */

                    @Override // org.apache.commons.math3.analysis.UnivariateFunction
                    public double value(double x) {
                        double sum = 0.0d;
                        for (int i = 0; i < f.length; i++) {
                            double prod = f[i].derivative().value(x);
                            for (int j = 0; j < f.length; j++) {
                                if (i != j) {
                                    prod *= f[j].value(x);
                                }
                            }
                            sum += prod;
                        }
                        return sum;
                    }
                };
            }
        };
    }

    public static UnivariateFunction combine(final BivariateFunction combiner, final UnivariateFunction f, final UnivariateFunction g) {
        return new UnivariateFunction() {
            /* class org.apache.commons.math3.analysis.FunctionUtils.C016710 */

            @Override // org.apache.commons.math3.analysis.UnivariateFunction
            public double value(double x) {
                return combiner.value(f.value(x), g.value(x));
            }
        };
    }

    public static MultivariateFunction collector(final BivariateFunction combiner, final UnivariateFunction f, final double initialValue) {
        return new MultivariateFunction() {
            /* class org.apache.commons.math3.analysis.FunctionUtils.C016811 */

            @Override // org.apache.commons.math3.analysis.MultivariateFunction
            public double value(double[] point) {
                double result = combiner.value(initialValue, f.value(point[0]));
                for (int i = 1; i < point.length; i++) {
                    result = combiner.value(result, f.value(point[i]));
                }
                return result;
            }
        };
    }

    public static MultivariateFunction collector(BivariateFunction combiner, double initialValue) {
        return collector(combiner, new Identity(), initialValue);
    }

    public static UnivariateFunction fix1stArgument(final BivariateFunction f, final double fixed) {
        return new UnivariateFunction() {
            /* class org.apache.commons.math3.analysis.FunctionUtils.C016912 */

            @Override // org.apache.commons.math3.analysis.UnivariateFunction
            public double value(double x) {
                return f.value(fixed, x);
            }
        };
    }

    public static UnivariateFunction fix2ndArgument(final BivariateFunction f, final double fixed) {
        return new UnivariateFunction() {
            /* class org.apache.commons.math3.analysis.FunctionUtils.C017013 */

            @Override // org.apache.commons.math3.analysis.UnivariateFunction
            public double value(double x) {
                return f.value(x, fixed);
            }
        };
    }

    public static double[] sample(UnivariateFunction f, double min, double max, int n) throws NumberIsTooLargeException, NotStrictlyPositiveException {
        if (n <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.NOT_POSITIVE_NUMBER_OF_SAMPLES, Integer.valueOf(n));
        } else if (min >= max) {
            throw new NumberIsTooLargeException(Double.valueOf(min), Double.valueOf(max), false);
        } else {
            double[] s = new double[n];
            double h = (max - min) / ((double) n);
            for (int i = 0; i < n; i++) {
                s[i] = f.value((((double) i) * h) + min);
            }
            return s;
        }
    }

    @Deprecated
    public static DifferentiableUnivariateFunction toDifferentiableUnivariateFunction(final UnivariateDifferentiableFunction f) {
        return new DifferentiableUnivariateFunction() {
            /* class org.apache.commons.math3.analysis.FunctionUtils.C017114 */

            @Override // org.apache.commons.math3.analysis.UnivariateFunction
            public double value(double x) {
                return f.value(x);
            }

            @Override // org.apache.commons.math3.analysis.DifferentiableUnivariateFunction
            public UnivariateFunction derivative() {
                return new UnivariateFunction() {
                    /* class org.apache.commons.math3.analysis.FunctionUtils.C017114.C01721 */

                    @Override // org.apache.commons.math3.analysis.UnivariateFunction
                    public double value(double x) {
                        return f.value(new DerivativeStructure(1, 1, 0, x)).getPartialDerivative(1);
                    }
                };
            }
        };
    }

    @Deprecated
    public static UnivariateDifferentiableFunction toUnivariateDifferential(final DifferentiableUnivariateFunction f) {
        return new UnivariateDifferentiableFunction() {
            /* class org.apache.commons.math3.analysis.FunctionUtils.C017315 */

            @Override // org.apache.commons.math3.analysis.UnivariateFunction
            public double value(double x) {
                return f.value(x);
            }

            @Override // org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction
            public DerivativeStructure value(DerivativeStructure t) throws NumberIsTooLargeException {
                switch (t.getOrder()) {
                    case 0:
                        return new DerivativeStructure(t.getFreeParameters(), 0, f.value(t.getValue()));
                    case 1:
                        int parameters = t.getFreeParameters();
                        double[] derivatives = new double[(parameters + 1)];
                        derivatives[0] = f.value(t.getValue());
                        double fPrime = f.derivative().value(t.getValue());
                        int[] orders = new int[parameters];
                        for (int i = 0; i < parameters; i++) {
                            orders[i] = 1;
                            derivatives[i + 1] = t.getPartialDerivative(orders) * fPrime;
                            orders[i] = 0;
                        }
                        return new DerivativeStructure(parameters, 1, derivatives);
                    default:
                        throw new NumberIsTooLargeException(Integer.valueOf(t.getOrder()), 1, true);
                }
            }
        };
    }

    @Deprecated
    public static DifferentiableMultivariateFunction toDifferentiableMultivariateFunction(final MultivariateDifferentiableFunction f) {
        return new DifferentiableMultivariateFunction() {
            /* class org.apache.commons.math3.analysis.FunctionUtils.C017416 */

            @Override // org.apache.commons.math3.analysis.MultivariateFunction
            public double value(double[] x) {
                return f.value(x);
            }

            @Override // org.apache.commons.math3.analysis.DifferentiableMultivariateFunction
            public MultivariateFunction partialDerivative(final int k) {
                return new MultivariateFunction() {
                    /* class org.apache.commons.math3.analysis.FunctionUtils.C017416.C01751 */

                    @Override // org.apache.commons.math3.analysis.MultivariateFunction
                    public double value(double[] x) {
                        int n = x.length;
                        DerivativeStructure[] dsX = new DerivativeStructure[n];
                        for (int i = 0; i < n; i++) {
                            if (i == k) {
                                dsX[i] = new DerivativeStructure(1, 1, 0, x[i]);
                            } else {
                                dsX[i] = new DerivativeStructure(1, 1, x[i]);
                            }
                        }
                        return f.value(dsX).getPartialDerivative(1);
                    }
                };
            }

            @Override // org.apache.commons.math3.analysis.DifferentiableMultivariateFunction
            public MultivariateVectorFunction gradient() {
                return new MultivariateVectorFunction() {
                    /* class org.apache.commons.math3.analysis.FunctionUtils.C017416.C01762 */

                    @Override // org.apache.commons.math3.analysis.MultivariateVectorFunction
                    public double[] value(double[] x) {
                        int n = x.length;
                        DerivativeStructure[] dsX = new DerivativeStructure[n];
                        for (int i = 0; i < n; i++) {
                            dsX[i] = new DerivativeStructure(n, 1, i, x[i]);
                        }
                        DerivativeStructure y = f.value(dsX);
                        double[] gradient = new double[n];
                        int[] orders = new int[n];
                        for (int i2 = 0; i2 < n; i2++) {
                            orders[i2] = 1;
                            gradient[i2] = y.getPartialDerivative(orders);
                            orders[i2] = 0;
                        }
                        return gradient;
                    }
                };
            }
        };
    }

    @Deprecated
    public static MultivariateDifferentiableFunction toMultivariateDifferentiableFunction(final DifferentiableMultivariateFunction f) {
        return new MultivariateDifferentiableFunction() {
            /* class org.apache.commons.math3.analysis.FunctionUtils.C017717 */

            @Override // org.apache.commons.math3.analysis.MultivariateFunction
            public double value(double[] x) {
                return f.value(x);
            }

            @Override // org.apache.commons.math3.analysis.differentiation.MultivariateDifferentiableFunction
            public DerivativeStructure value(DerivativeStructure[] t) throws DimensionMismatchException, NumberIsTooLargeException {
                int parameters = t[0].getFreeParameters();
                int order = t[0].getOrder();
                int n = t.length;
                if (order > 1) {
                    throw new NumberIsTooLargeException(Integer.valueOf(order), 1, true);
                }
                for (int i = 0; i < n; i++) {
                    if (t[i].getFreeParameters() != parameters) {
                        throw new DimensionMismatchException(t[i].getFreeParameters(), parameters);
                    } else if (t[i].getOrder() != order) {
                        throw new DimensionMismatchException(t[i].getOrder(), order);
                    }
                }
                double[] point = new double[n];
                for (int i2 = 0; i2 < n; i2++) {
                    point[i2] = t[i2].getValue();
                }
                double value = f.value(point);
                double[] gradient = f.gradient().value(point);
                double[] derivatives = new double[(parameters + 1)];
                derivatives[0] = value;
                int[] orders = new int[parameters];
                for (int i3 = 0; i3 < parameters; i3++) {
                    orders[i3] = 1;
                    for (int j = 0; j < n; j++) {
                        int i4 = i3 + 1;
                        derivatives[i4] = derivatives[i4] + (gradient[j] * t[j].getPartialDerivative(orders));
                    }
                    orders[i3] = 0;
                }
                return new DerivativeStructure(parameters, order, derivatives);
            }
        };
    }

    @Deprecated
    public static DifferentiableMultivariateVectorFunction toDifferentiableMultivariateVectorFunction(final MultivariateDifferentiableVectorFunction f) {
        return new DifferentiableMultivariateVectorFunction() {
            /* class org.apache.commons.math3.analysis.FunctionUtils.C017818 */

            @Override // org.apache.commons.math3.analysis.MultivariateVectorFunction
            public double[] value(double[] x) {
                return f.value(x);
            }

            @Override // org.apache.commons.math3.analysis.DifferentiableMultivariateVectorFunction
            public MultivariateMatrixFunction jacobian() {
                return new MultivariateMatrixFunction() {
                    /* class org.apache.commons.math3.analysis.FunctionUtils.C017818.C01791 */

                    @Override // org.apache.commons.math3.analysis.MultivariateMatrixFunction
                    public double[][] value(double[] x) {
                        int n = x.length;
                        DerivativeStructure[] dsX = new DerivativeStructure[n];
                        for (int i = 0; i < n; i++) {
                            dsX[i] = new DerivativeStructure(n, 1, i, x[i]);
                        }
                        DerivativeStructure[] y = f.value(dsX);
                        double[][] jacobian = (double[][]) Array.newInstance(Double.TYPE, y.length, n);
                        int[] orders = new int[n];
                        for (int i2 = 0; i2 < y.length; i2++) {
                            for (int j = 0; j < n; j++) {
                                orders[j] = 1;
                                jacobian[i2][j] = y[i2].getPartialDerivative(orders);
                                orders[j] = 0;
                            }
                        }
                        return jacobian;
                    }
                };
            }
        };
    }

    @Deprecated
    public static MultivariateDifferentiableVectorFunction toMultivariateDifferentiableVectorFunction(final DifferentiableMultivariateVectorFunction f) {
        return new MultivariateDifferentiableVectorFunction() {
            /* class org.apache.commons.math3.analysis.FunctionUtils.C018019 */

            @Override // org.apache.commons.math3.analysis.MultivariateVectorFunction
            public double[] value(double[] x) {
                return f.value(x);
            }

            @Override // org.apache.commons.math3.analysis.differentiation.MultivariateDifferentiableVectorFunction
            public DerivativeStructure[] value(DerivativeStructure[] t) throws DimensionMismatchException, NumberIsTooLargeException {
                int parameters = t[0].getFreeParameters();
                int order = t[0].getOrder();
                int n = t.length;
                if (order > 1) {
                    throw new NumberIsTooLargeException(Integer.valueOf(order), 1, true);
                }
                for (int i = 0; i < n; i++) {
                    if (t[i].getFreeParameters() != parameters) {
                        throw new DimensionMismatchException(t[i].getFreeParameters(), parameters);
                    } else if (t[i].getOrder() != order) {
                        throw new DimensionMismatchException(t[i].getOrder(), order);
                    }
                }
                double[] point = new double[n];
                for (int i2 = 0; i2 < n; i2++) {
                    point[i2] = t[i2].getValue();
                }
                double[] value = f.value(point);
                double[][] jacobian = f.jacobian().value(point);
                DerivativeStructure[] merged = new DerivativeStructure[value.length];
                for (int k = 0; k < merged.length; k++) {
                    double[] derivatives = new double[(parameters + 1)];
                    derivatives[0] = value[k];
                    int[] orders = new int[parameters];
                    for (int i3 = 0; i3 < parameters; i3++) {
                        orders[i3] = 1;
                        for (int j = 0; j < n; j++) {
                            int i4 = i3 + 1;
                            derivatives[i4] = derivatives[i4] + (jacobian[k][j] * t[j].getPartialDerivative(orders));
                        }
                        orders[i3] = 0;
                    }
                    merged[k] = new DerivativeStructure(parameters, order, derivatives);
                }
                return merged;
            }
        };
    }
}
