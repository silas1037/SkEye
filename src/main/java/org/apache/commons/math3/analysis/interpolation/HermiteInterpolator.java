package org.apache.commons.math3.analysis.interpolation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableVectorFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.ZeroException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.CombinatoricsUtils;

public class HermiteInterpolator implements UnivariateDifferentiableVectorFunction {
    private final List<Double> abscissae = new ArrayList();
    private final List<double[]> bottomDiagonal = new ArrayList();
    private final List<double[]> topDiagonal = new ArrayList();

    /* JADX DEBUG: Multi-variable search result rejected for r11v5, resolved type: java.util.List<double[]> */
    /* JADX WARN: Multi-variable type inference failed */
    public void addSamplePoint(double x, double[]... value) throws ZeroException, MathArithmeticException {
        for (int i = 0; i < value.length; i++) {
            double[] y = (double[]) value[i].clone();
            if (i > 1) {
                double inv = 1.0d / ((double) CombinatoricsUtils.factorial(i));
                for (int j = 0; j < y.length; j++) {
                    y[j] = y[j] * inv;
                }
            }
            int n = this.abscissae.size();
            this.bottomDiagonal.add(n - i, y);
            double[] bottom0 = y;
            for (int j2 = i; j2 < n; j2++) {
                double[] bottom1 = this.bottomDiagonal.get(n - (j2 + 1));
                double inv2 = 1.0d / (x - this.abscissae.get(n - (j2 + 1)).doubleValue());
                if (Double.isInfinite(inv2)) {
                    throw new ZeroException(LocalizedFormats.DUPLICATED_ABSCISSA_DIVISION_BY_ZERO, Double.valueOf(x));
                }
                for (int k = 0; k < y.length; k++) {
                    bottom1[k] = (bottom0[k] - bottom1[k]) * inv2;
                }
                bottom0 = bottom1;
            }
            this.topDiagonal.add(bottom0.clone());
            this.abscissae.add(Double.valueOf(x));
        }
    }

    public PolynomialFunction[] getPolynomials() throws NoDataException {
        checkInterpolation();
        PolynomialFunction zero = polynomial(0.0d);
        PolynomialFunction[] polynomials = new PolynomialFunction[this.topDiagonal.get(0).length];
        for (int i = 0; i < polynomials.length; i++) {
            polynomials[i] = zero;
        }
        PolynomialFunction coeff = polynomial(1.0d);
        for (int i2 = 0; i2 < this.topDiagonal.size(); i2++) {
            double[] tdi = this.topDiagonal.get(i2);
            for (int k = 0; k < polynomials.length; k++) {
                polynomials[k] = polynomials[k].add(coeff.multiply(polynomial(tdi[k])));
            }
            coeff = coeff.multiply(polynomial(-this.abscissae.get(i2).doubleValue(), 1.0d));
        }
        return polynomials;
    }

    @Override // org.apache.commons.math3.analysis.UnivariateVectorFunction
    public double[] value(double x) throws NoDataException {
        checkInterpolation();
        double[] value = new double[this.topDiagonal.get(0).length];
        double valueCoeff = 1.0d;
        for (int i = 0; i < this.topDiagonal.size(); i++) {
            double[] dividedDifference = this.topDiagonal.get(i);
            for (int k = 0; k < value.length; k++) {
                value[k] = value[k] + (dividedDifference[k] * valueCoeff);
            }
            valueCoeff *= x - this.abscissae.get(i).doubleValue();
        }
        return value;
    }

    @Override // org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableVectorFunction
    public DerivativeStructure[] value(DerivativeStructure x) throws NoDataException {
        checkInterpolation();
        DerivativeStructure[] value = new DerivativeStructure[this.topDiagonal.get(0).length];
        Arrays.fill(value, x.getField().getZero());
        DerivativeStructure valueCoeff = x.getField().getOne();
        for (int i = 0; i < this.topDiagonal.size(); i++) {
            double[] dividedDifference = this.topDiagonal.get(i);
            for (int k = 0; k < value.length; k++) {
                value[k] = value[k].add(valueCoeff.multiply(dividedDifference[k]));
            }
            valueCoeff = valueCoeff.multiply(x.subtract(this.abscissae.get(i).doubleValue()));
        }
        return value;
    }

    private void checkInterpolation() throws NoDataException {
        if (this.abscissae.isEmpty()) {
            throw new NoDataException(LocalizedFormats.EMPTY_INTERPOLATION_SAMPLE);
        }
    }

    private PolynomialFunction polynomial(double... c) {
        return new PolynomialFunction(c);
    }
}
