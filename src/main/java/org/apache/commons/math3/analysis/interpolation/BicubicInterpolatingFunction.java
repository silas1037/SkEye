package org.apache.commons.math3.analysis.interpolation;

import java.lang.reflect.Array;
import java.util.Arrays;
import org.apache.commons.math3.analysis.BivariateFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.util.MathArrays;

public class BicubicInterpolatingFunction implements BivariateFunction {
    private static final double[][] AINV = {new double[]{1.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d}, new double[]{0.0d, 0.0d, 0.0d, 0.0d, 1.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d}, new double[]{-3.0d, 3.0d, 0.0d, 0.0d, -2.0d, -1.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d}, new double[]{2.0d, -2.0d, 0.0d, 0.0d, 1.0d, 1.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d}, new double[]{0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 1.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d}, new double[]{0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 1.0d, 0.0d, 0.0d, 0.0d}, new double[]{0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, -3.0d, 3.0d, 0.0d, 0.0d, -2.0d, -1.0d, 0.0d, 0.0d}, new double[]{0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 2.0d, -2.0d, 0.0d, 0.0d, 1.0d, 1.0d, 0.0d, 0.0d}, new double[]{-3.0d, 0.0d, 3.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, -2.0d, 0.0d, -1.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d}, new double[]{0.0d, 0.0d, 0.0d, 0.0d, -3.0d, 0.0d, 3.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, -2.0d, 0.0d, -1.0d, 0.0d}, new double[]{9.0d, -9.0d, -9.0d, 9.0d, 6.0d, 3.0d, -6.0d, -3.0d, 6.0d, -6.0d, 3.0d, -3.0d, 4.0d, 2.0d, 2.0d, 1.0d}, new double[]{-6.0d, 6.0d, 6.0d, -6.0d, -3.0d, -3.0d, 3.0d, 3.0d, -4.0d, 4.0d, -2.0d, 2.0d, -2.0d, -2.0d, -1.0d, -1.0d}, new double[]{2.0d, 0.0d, -2.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 1.0d, 0.0d, 1.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d}, new double[]{0.0d, 0.0d, 0.0d, 0.0d, 2.0d, 0.0d, -2.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 1.0d, 0.0d, 1.0d, 0.0d}, new double[]{-6.0d, 6.0d, 6.0d, -6.0d, -4.0d, -2.0d, 4.0d, 2.0d, -3.0d, 3.0d, -3.0d, 3.0d, -2.0d, -1.0d, -2.0d, -1.0d}, new double[]{4.0d, -4.0d, -4.0d, 4.0d, 2.0d, 2.0d, -2.0d, -2.0d, 2.0d, -2.0d, 2.0d, -2.0d, 1.0d, 1.0d, 1.0d, 1.0d}};
    private static final int NUM_COEFF = 16;
    private final BicubicFunction[][] splines;
    private final double[] xval;
    private final double[] yval;

    public BicubicInterpolatingFunction(double[] x, double[] y, double[][] f, double[][] dFdX, double[][] dFdY, double[][] d2FdXdY) throws DimensionMismatchException, NoDataException, NonMonotonicSequenceException {
        int xLen = x.length;
        int yLen = y.length;
        if (xLen == 0 || yLen == 0 || f.length == 0 || f[0].length == 0) {
            throw new NoDataException();
        } else if (xLen != f.length) {
            throw new DimensionMismatchException(xLen, f.length);
        } else if (xLen != dFdX.length) {
            throw new DimensionMismatchException(xLen, dFdX.length);
        } else if (xLen != dFdY.length) {
            throw new DimensionMismatchException(xLen, dFdY.length);
        } else if (xLen != d2FdXdY.length) {
            throw new DimensionMismatchException(xLen, d2FdXdY.length);
        } else {
            MathArrays.checkOrder(x);
            MathArrays.checkOrder(y);
            this.xval = (double[]) x.clone();
            this.yval = (double[]) y.clone();
            int lastI = xLen - 1;
            int lastJ = yLen - 1;
            this.splines = (BicubicFunction[][]) Array.newInstance(BicubicFunction.class, lastI, lastJ);
            for (int i = 0; i < lastI; i++) {
                if (f[i].length != yLen) {
                    throw new DimensionMismatchException(f[i].length, yLen);
                } else if (dFdX[i].length != yLen) {
                    throw new DimensionMismatchException(dFdX[i].length, yLen);
                } else if (dFdY[i].length != yLen) {
                    throw new DimensionMismatchException(dFdY[i].length, yLen);
                } else if (d2FdXdY[i].length != yLen) {
                    throw new DimensionMismatchException(d2FdXdY[i].length, yLen);
                } else {
                    int ip1 = i + 1;
                    double xR = this.xval[ip1] - this.xval[i];
                    for (int j = 0; j < lastJ; j++) {
                        int jp1 = j + 1;
                        double yR = this.yval[jp1] - this.yval[j];
                        double xRyR = xR * yR;
                        this.splines[i][j] = new BicubicFunction(computeSplineCoefficients(new double[]{f[i][j], f[ip1][j], f[i][jp1], f[ip1][jp1], dFdX[i][j] * xR, dFdX[ip1][j] * xR, dFdX[i][jp1] * xR, dFdX[ip1][jp1] * xR, dFdY[i][j] * yR, dFdY[ip1][j] * yR, dFdY[i][jp1] * yR, dFdY[ip1][jp1] * yR, d2FdXdY[i][j] * xRyR, d2FdXdY[ip1][j] * xRyR, d2FdXdY[i][jp1] * xRyR, d2FdXdY[ip1][jp1] * xRyR}));
                    }
                }
            }
        }
    }

    @Override // org.apache.commons.math3.analysis.BivariateFunction
    public double value(double x, double y) throws OutOfRangeException {
        int i = searchIndex(x, this.xval);
        int j = searchIndex(y, this.yval);
        return this.splines[i][j].value((x - this.xval[i]) / (this.xval[i + 1] - this.xval[i]), (y - this.yval[j]) / (this.yval[j + 1] - this.yval[j]));
    }

    public boolean isValidPoint(double x, double y) {
        if (x < this.xval[0] || x > this.xval[this.xval.length - 1] || y < this.yval[0] || y > this.yval[this.yval.length - 1]) {
            return false;
        }
        return true;
    }

    private int searchIndex(double c, double[] val) {
        int r = Arrays.binarySearch(val, c);
        if (r == -1 || r == (-val.length) - 1) {
            throw new OutOfRangeException(Double.valueOf(c), Double.valueOf(val[0]), Double.valueOf(val[val.length - 1]));
        } else if (r < 0) {
            return (-r) - 2;
        } else {
            int last = val.length - 1;
            if (r == last) {
                return last - 1;
            }
            return r;
        }
    }

    private double[] computeSplineCoefficients(double[] beta) {
        double[] a = new double[16];
        for (int i = 0; i < 16; i++) {
            double result = 0.0d;
            double[] row = AINV[i];
            for (int j = 0; j < 16; j++) {
                result += row[j] * beta[j];
            }
            a[i] = result;
        }
        return a;
    }
}
