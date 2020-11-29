package org.apache.commons.math3.analysis.polynomials;

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.MathUtils;

public class PolynomialFunctionNewtonForm implements UnivariateDifferentiableFunction {

    /* renamed from: a */
    private final double[] f137a;

    /* renamed from: c */
    private final double[] f138c;
    private double[] coefficients;
    private boolean coefficientsComputed = false;

    public PolynomialFunctionNewtonForm(double[] a, double[] c) throws NullArgumentException, NoDataException, DimensionMismatchException {
        verifyInputArray(a, c);
        this.f137a = new double[a.length];
        this.f138c = new double[c.length];
        System.arraycopy(a, 0, this.f137a, 0, a.length);
        System.arraycopy(c, 0, this.f138c, 0, c.length);
    }

    @Override // org.apache.commons.math3.analysis.UnivariateFunction
    public double value(double z) {
        return evaluate(this.f137a, this.f138c, z);
    }

    @Override // org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction
    public DerivativeStructure value(DerivativeStructure t) {
        verifyInputArray(this.f137a, this.f138c);
        int n = this.f138c.length;
        DerivativeStructure value = new DerivativeStructure(t.getFreeParameters(), t.getOrder(), this.f137a[n]);
        for (int i = n - 1; i >= 0; i--) {
            value = t.subtract(this.f138c[i]).multiply(value).add(this.f137a[i]);
        }
        return value;
    }

    public int degree() {
        return this.f138c.length;
    }

    public double[] getNewtonCoefficients() {
        double[] out = new double[this.f137a.length];
        System.arraycopy(this.f137a, 0, out, 0, this.f137a.length);
        return out;
    }

    public double[] getCenters() {
        double[] out = new double[this.f138c.length];
        System.arraycopy(this.f138c, 0, out, 0, this.f138c.length);
        return out;
    }

    public double[] getCoefficients() {
        if (!this.coefficientsComputed) {
            computeCoefficients();
        }
        double[] out = new double[this.coefficients.length];
        System.arraycopy(this.coefficients, 0, out, 0, this.coefficients.length);
        return out;
    }

    public static double evaluate(double[] a, double[] c, double z) throws NullArgumentException, DimensionMismatchException, NoDataException {
        verifyInputArray(a, c);
        int n = c.length;
        double value = a[n];
        for (int i = n - 1; i >= 0; i--) {
            value = a[i] + ((z - c[i]) * value);
        }
        return value;
    }

    /* access modifiers changed from: protected */
    public void computeCoefficients() {
        int n = degree();
        this.coefficients = new double[(n + 1)];
        for (int i = 0; i <= n; i++) {
            this.coefficients[i] = 0.0d;
        }
        this.coefficients[0] = this.f137a[n];
        for (int i2 = n - 1; i2 >= 0; i2--) {
            for (int j = n - i2; j > 0; j--) {
                this.coefficients[j] = this.coefficients[j - 1] - (this.f138c[i2] * this.coefficients[j]);
            }
            this.coefficients[0] = this.f137a[i2] - (this.f138c[i2] * this.coefficients[0]);
        }
        this.coefficientsComputed = true;
    }

    protected static void verifyInputArray(double[] a, double[] c) throws NullArgumentException, NoDataException, DimensionMismatchException {
        MathUtils.checkNotNull(a);
        MathUtils.checkNotNull(c);
        if (a.length == 0 || c.length == 0) {
            throw new NoDataException(LocalizedFormats.EMPTY_POLYNOMIALS_COEFFICIENTS_ARRAY);
        } else if (a.length != c.length + 1) {
            throw new DimensionMismatchException(LocalizedFormats.ARRAY_SIZES_SHOULD_HAVE_DIFFERENCE_1, a.length, c.length);
        }
    }
}
