package org.apache.commons.math3.analysis.interpolation;

import java.lang.reflect.Array;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.util.MathArrays;

@Deprecated
public class BicubicSplineInterpolator implements BivariateGridInterpolator {
    private final boolean initializeDerivatives;

    public BicubicSplineInterpolator() {
        this(false);
    }

    public BicubicSplineInterpolator(boolean initializeDerivatives2) {
        this.initializeDerivatives = initializeDerivatives2;
    }

    @Override // org.apache.commons.math3.analysis.interpolation.BivariateGridInterpolator
    public BicubicSplineInterpolatingFunction interpolate(double[] xval, double[] yval, double[][] fval) throws NoDataException, DimensionMismatchException, NonMonotonicSequenceException, NumberIsTooSmallException {
        if (xval.length == 0 || yval.length == 0 || fval.length == 0) {
            throw new NoDataException();
        } else if (xval.length != fval.length) {
            throw new DimensionMismatchException(xval.length, fval.length);
        } else {
            MathArrays.checkOrder(xval);
            MathArrays.checkOrder(yval);
            int xLen = xval.length;
            int yLen = yval.length;
            double[][] fX = (double[][]) Array.newInstance(Double.TYPE, yLen, xLen);
            for (int i = 0; i < xLen; i++) {
                if (fval[i].length != yLen) {
                    throw new DimensionMismatchException(fval[i].length, yLen);
                }
                for (int j = 0; j < yLen; j++) {
                    fX[j][i] = fval[i][j];
                }
            }
            SplineInterpolator spInterpolator = new SplineInterpolator();
            PolynomialSplineFunction[] ySplineX = new PolynomialSplineFunction[yLen];
            for (int j2 = 0; j2 < yLen; j2++) {
                ySplineX[j2] = spInterpolator.interpolate(xval, fX[j2]);
            }
            PolynomialSplineFunction[] xSplineY = new PolynomialSplineFunction[xLen];
            for (int i2 = 0; i2 < xLen; i2++) {
                xSplineY[i2] = spInterpolator.interpolate(yval, fval[i2]);
            }
            double[][] dFdX = (double[][]) Array.newInstance(Double.TYPE, xLen, yLen);
            for (int j3 = 0; j3 < yLen; j3++) {
                UnivariateFunction f = ySplineX[j3].derivative();
                for (int i3 = 0; i3 < xLen; i3++) {
                    dFdX[i3][j3] = f.value(xval[i3]);
                }
            }
            double[][] dFdY = (double[][]) Array.newInstance(Double.TYPE, xLen, yLen);
            for (int i4 = 0; i4 < xLen; i4++) {
                UnivariateFunction f2 = xSplineY[i4].derivative();
                for (int j4 = 0; j4 < yLen; j4++) {
                    dFdY[i4][j4] = f2.value(yval[j4]);
                }
            }
            double[][] d2FdXdY = (double[][]) Array.newInstance(Double.TYPE, xLen, yLen);
            for (int i5 = 0; i5 < xLen; i5++) {
                int nI = nextIndex(i5, xLen);
                int pI = previousIndex(i5);
                for (int j5 = 0; j5 < yLen; j5++) {
                    int nJ = nextIndex(j5, yLen);
                    int pJ = previousIndex(j5);
                    d2FdXdY[i5][j5] = (((fval[nI][nJ] - fval[nI][pJ]) - fval[pI][nJ]) + fval[pI][pJ]) / ((xval[nI] - xval[pI]) * (yval[nJ] - yval[pJ]));
                }
            }
            return new BicubicSplineInterpolatingFunction(xval, yval, fval, dFdX, dFdY, d2FdXdY, this.initializeDerivatives);
        }
    }

    private int nextIndex(int i, int max) {
        int index = i + 1;
        return index < max ? index : index - 1;
    }

    private int previousIndex(int i) {
        int index = i - 1;
        if (index >= 0) {
            return index;
        }
        return 0;
    }
}
