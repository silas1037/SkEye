package org.apache.commons.math3.analysis.interpolation;

import java.lang.reflect.Array;
import java.util.Arrays;
import org.apache.commons.math3.analysis.BivariateFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.util.MathArrays;

@Deprecated
public class BicubicSplineInterpolatingFunction implements BivariateFunction {
    private static final double[][] AINV = {new double[]{1.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d}, new double[]{0.0d, 0.0d, 0.0d, 0.0d, 1.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d}, new double[]{-3.0d, 3.0d, 0.0d, 0.0d, -2.0d, -1.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d}, new double[]{2.0d, -2.0d, 0.0d, 0.0d, 1.0d, 1.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d}, new double[]{0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 1.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d}, new double[]{0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 1.0d, 0.0d, 0.0d, 0.0d}, new double[]{0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, -3.0d, 3.0d, 0.0d, 0.0d, -2.0d, -1.0d, 0.0d, 0.0d}, new double[]{0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 2.0d, -2.0d, 0.0d, 0.0d, 1.0d, 1.0d, 0.0d, 0.0d}, new double[]{-3.0d, 0.0d, 3.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, -2.0d, 0.0d, -1.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d}, new double[]{0.0d, 0.0d, 0.0d, 0.0d, -3.0d, 0.0d, 3.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, -2.0d, 0.0d, -1.0d, 0.0d}, new double[]{9.0d, -9.0d, -9.0d, 9.0d, 6.0d, 3.0d, -6.0d, -3.0d, 6.0d, -6.0d, 3.0d, -3.0d, 4.0d, 2.0d, 2.0d, 1.0d}, new double[]{-6.0d, 6.0d, 6.0d, -6.0d, -3.0d, -3.0d, 3.0d, 3.0d, -4.0d, 4.0d, -2.0d, 2.0d, -2.0d, -2.0d, -1.0d, -1.0d}, new double[]{2.0d, 0.0d, -2.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 1.0d, 0.0d, 1.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d}, new double[]{0.0d, 0.0d, 0.0d, 0.0d, 2.0d, 0.0d, -2.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 1.0d, 0.0d, 1.0d, 0.0d}, new double[]{-6.0d, 6.0d, 6.0d, -6.0d, -4.0d, -2.0d, 4.0d, 2.0d, -3.0d, 3.0d, -3.0d, 3.0d, -2.0d, -1.0d, -2.0d, -1.0d}, new double[]{4.0d, -4.0d, -4.0d, 4.0d, 2.0d, 2.0d, -2.0d, -2.0d, 2.0d, -2.0d, 2.0d, -2.0d, 1.0d, 1.0d, 1.0d, 1.0d}};
    private static final int NUM_COEFF = 16;
    private final BivariateFunction[][][] partialDerivatives;
    private final BicubicSplineFunction[][] splines;
    private final double[] xval;
    private final double[] yval;

    public BicubicSplineInterpolatingFunction(double[] x, double[] y, double[][] f, double[][] dFdX, double[][] dFdY, double[][] d2FdXdY) throws DimensionMismatchException, NoDataException, NonMonotonicSequenceException {
        this(x, y, f, dFdX, dFdY, d2FdXdY, false);
    }

    public BicubicSplineInterpolatingFunction(double[] x, double[] y, double[][] f, double[][] dFdX, double[][] dFdY, double[][] d2FdXdY, boolean initializeDerivatives) throws DimensionMismatchException, NoDataException, NonMonotonicSequenceException {
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
            this.splines = (BicubicSplineFunction[][]) Array.newInstance(BicubicSplineFunction.class, lastI, lastJ);
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
                    for (int j = 0; j < lastJ; j++) {
                        int jp1 = j + 1;
                        this.splines[i][j] = new BicubicSplineFunction(computeSplineCoefficients(new double[]{f[i][j], f[ip1][j], f[i][jp1], f[ip1][jp1], dFdX[i][j], dFdX[ip1][j], dFdX[i][jp1], dFdX[ip1][jp1], dFdY[i][j], dFdY[ip1][j], dFdY[i][jp1], dFdY[ip1][jp1], d2FdXdY[i][j], d2FdXdY[ip1][j], d2FdXdY[i][jp1], d2FdXdY[ip1][jp1]}), initializeDerivatives);
                    }
                }
            }
            if (initializeDerivatives) {
                this.partialDerivatives = (BivariateFunction[][][]) Array.newInstance(BivariateFunction.class, 5, lastI, lastJ);
                for (int i2 = 0; i2 < lastI; i2++) {
                    for (int j2 = 0; j2 < lastJ; j2++) {
                        BicubicSplineFunction bcs = this.splines[i2][j2];
                        this.partialDerivatives[0][i2][j2] = bcs.partialDerivativeX();
                        this.partialDerivatives[1][i2][j2] = bcs.partialDerivativeY();
                        this.partialDerivatives[2][i2][j2] = bcs.partialDerivativeXX();
                        this.partialDerivatives[3][i2][j2] = bcs.partialDerivativeYY();
                        this.partialDerivatives[4][i2][j2] = bcs.partialDerivativeXY();
                    }
                }
                return;
            }
            this.partialDerivatives = null;
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

    public double partialDerivativeX(double x, double y) throws OutOfRangeException {
        return partialDerivative(0, x, y);
    }

    public double partialDerivativeY(double x, double y) throws OutOfRangeException {
        return partialDerivative(1, x, y);
    }

    public double partialDerivativeXX(double x, double y) throws OutOfRangeException {
        return partialDerivative(2, x, y);
    }

    public double partialDerivativeYY(double x, double y) throws OutOfRangeException {
        return partialDerivative(3, x, y);
    }

    public double partialDerivativeXY(double x, double y) throws OutOfRangeException {
        return partialDerivative(4, x, y);
    }

    private double partialDerivative(int which, double x, double y) throws OutOfRangeException {
        int i = searchIndex(x, this.xval);
        int j = searchIndex(y, this.yval);
        return this.partialDerivatives[which][i][j].value((x - this.xval[i]) / (this.xval[i + 1] - this.xval[i]), (y - this.yval[j]) / (this.yval[j + 1] - this.yval[j]));
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
