package org.apache.commons.math3.analysis.interpolation;

import java.lang.reflect.Array;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.util.MathArrays;

@Deprecated
public class TricubicSplineInterpolator implements TrivariateGridInterpolator {
    @Override // org.apache.commons.math3.analysis.interpolation.TrivariateGridInterpolator
    public TricubicSplineInterpolatingFunction interpolate(double[] xval, double[] yval, double[] zval, double[][][] fval) throws NoDataException, NumberIsTooSmallException, DimensionMismatchException, NonMonotonicSequenceException {
        if (xval.length == 0 || yval.length == 0 || zval.length == 0 || fval.length == 0) {
            throw new NoDataException();
        } else if (xval.length != fval.length) {
            throw new DimensionMismatchException(xval.length, fval.length);
        } else {
            MathArrays.checkOrder(xval);
            MathArrays.checkOrder(yval);
            MathArrays.checkOrder(zval);
            int xLen = xval.length;
            int yLen = yval.length;
            int zLen = zval.length;
            double[][][] fvalXY = (double[][][]) Array.newInstance(Double.TYPE, zLen, xLen, yLen);
            double[][][] fvalZX = (double[][][]) Array.newInstance(Double.TYPE, yLen, zLen, xLen);
            for (int i = 0; i < xLen; i++) {
                if (fval[i].length != yLen) {
                    throw new DimensionMismatchException(fval[i].length, yLen);
                }
                for (int j = 0; j < yLen; j++) {
                    if (fval[i][j].length != zLen) {
                        throw new DimensionMismatchException(fval[i][j].length, zLen);
                    }
                    for (int k = 0; k < zLen; k++) {
                        double v = fval[i][j][k];
                        fvalXY[k][i][j] = v;
                        fvalZX[j][k][i] = v;
                    }
                }
            }
            BicubicSplineInterpolator bsi = new BicubicSplineInterpolator(true);
            BicubicSplineInterpolatingFunction[] xSplineYZ = new BicubicSplineInterpolatingFunction[xLen];
            for (int i2 = 0; i2 < xLen; i2++) {
                xSplineYZ[i2] = bsi.interpolate(yval, zval, fval[i2]);
            }
            BicubicSplineInterpolatingFunction[] ySplineZX = new BicubicSplineInterpolatingFunction[yLen];
            for (int j2 = 0; j2 < yLen; j2++) {
                ySplineZX[j2] = bsi.interpolate(zval, xval, fvalZX[j2]);
            }
            BicubicSplineInterpolatingFunction[] zSplineXY = new BicubicSplineInterpolatingFunction[zLen];
            for (int k2 = 0; k2 < zLen; k2++) {
                zSplineXY[k2] = bsi.interpolate(xval, yval, fvalXY[k2]);
            }
            double[][][] dFdX = (double[][][]) Array.newInstance(Double.TYPE, xLen, yLen, zLen);
            double[][][] dFdY = (double[][][]) Array.newInstance(Double.TYPE, xLen, yLen, zLen);
            double[][][] d2FdXdY = (double[][][]) Array.newInstance(Double.TYPE, xLen, yLen, zLen);
            for (int k3 = 0; k3 < zLen; k3++) {
                BicubicSplineInterpolatingFunction f = zSplineXY[k3];
                for (int i3 = 0; i3 < xLen; i3++) {
                    double x = xval[i3];
                    for (int j3 = 0; j3 < yLen; j3++) {
                        double y = yval[j3];
                        dFdX[i3][j3][k3] = f.partialDerivativeX(x, y);
                        dFdY[i3][j3][k3] = f.partialDerivativeY(x, y);
                        d2FdXdY[i3][j3][k3] = f.partialDerivativeXY(x, y);
                    }
                }
            }
            double[][][] dFdZ = (double[][][]) Array.newInstance(Double.TYPE, xLen, yLen, zLen);
            double[][][] d2FdYdZ = (double[][][]) Array.newInstance(Double.TYPE, xLen, yLen, zLen);
            for (int i4 = 0; i4 < xLen; i4++) {
                BicubicSplineInterpolatingFunction f2 = xSplineYZ[i4];
                for (int j4 = 0; j4 < yLen; j4++) {
                    double y2 = yval[j4];
                    for (int k4 = 0; k4 < zLen; k4++) {
                        double z = zval[k4];
                        dFdZ[i4][j4][k4] = f2.partialDerivativeY(y2, z);
                        d2FdYdZ[i4][j4][k4] = f2.partialDerivativeXY(y2, z);
                    }
                }
            }
            double[][][] d2FdZdX = (double[][][]) Array.newInstance(Double.TYPE, xLen, yLen, zLen);
            for (int j5 = 0; j5 < yLen; j5++) {
                BicubicSplineInterpolatingFunction f3 = ySplineZX[j5];
                for (int k5 = 0; k5 < zLen; k5++) {
                    double z2 = zval[k5];
                    for (int i5 = 0; i5 < xLen; i5++) {
                        d2FdZdX[i5][j5][k5] = f3.partialDerivativeXY(z2, xval[i5]);
                    }
                }
            }
            double[][][] d3FdXdYdZ = (double[][][]) Array.newInstance(Double.TYPE, xLen, yLen, zLen);
            for (int i6 = 0; i6 < xLen; i6++) {
                int nI = nextIndex(i6, xLen);
                int pI = previousIndex(i6);
                for (int j6 = 0; j6 < yLen; j6++) {
                    int nJ = nextIndex(j6, yLen);
                    int pJ = previousIndex(j6);
                    for (int k6 = 0; k6 < zLen; k6++) {
                        int nK = nextIndex(k6, zLen);
                        int pK = previousIndex(k6);
                        d3FdXdYdZ[i6][j6][k6] = (((((((fval[nI][nJ][nK] - fval[nI][pJ][nK]) - fval[pI][nJ][nK]) + fval[pI][pJ][nK]) - fval[nI][nJ][pK]) + fval[nI][pJ][pK]) + fval[pI][nJ][pK]) - fval[pI][pJ][pK]) / (((xval[nI] - xval[pI]) * (yval[nJ] - yval[pJ])) * (zval[nK] - zval[pK]));
                    }
                }
            }
            return new TricubicSplineInterpolatingFunction(xval, yval, zval, fval, dFdX, dFdY, dFdZ, d2FdXdY, d2FdZdX, d2FdYdZ, d3FdXdYdZ);
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
