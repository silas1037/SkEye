package org.apache.commons.math3.analysis.interpolation;

import java.util.Arrays;
import org.apache.commons.math3.analysis.BivariateFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.InsufficientDataException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.util.MathArrays;

public class PiecewiseBicubicSplineInterpolatingFunction implements BivariateFunction {
    private static final int MIN_NUM_POINTS = 5;
    private final double[][] fval;
    private final double[] xval;
    private final double[] yval;

    public PiecewiseBicubicSplineInterpolatingFunction(double[] x, double[] y, double[][] f) throws DimensionMismatchException, NullArgumentException, NoDataException, NonMonotonicSequenceException {
        if (x == null || y == null || f == null || f[0] == null) {
            throw new NullArgumentException();
        }
        int xLen = x.length;
        int yLen = y.length;
        if (xLen == 0 || yLen == 0 || f.length == 0 || f[0].length == 0) {
            throw new NoDataException();
        } else if (xLen < 5 || yLen < 5 || f.length < 5 || f[0].length < 5) {
            throw new InsufficientDataException();
        } else if (xLen != f.length) {
            throw new DimensionMismatchException(xLen, f.length);
        } else if (yLen != f[0].length) {
            throw new DimensionMismatchException(yLen, f[0].length);
        } else {
            MathArrays.checkOrder(x);
            MathArrays.checkOrder(y);
            this.xval = (double[]) x.clone();
            this.yval = (double[]) y.clone();
            this.fval = (double[][]) f.clone();
        }
    }

    @Override // org.apache.commons.math3.analysis.BivariateFunction
    public double value(double x, double y) throws OutOfRangeException {
        AkimaSplineInterpolator interpolator = new AkimaSplineInterpolator();
        int i = searchIndex(x, this.xval, 2, 5);
        int j = searchIndex(y, this.yval, 2, 5);
        double[] xArray = new double[5];
        double[] yArray = new double[5];
        double[] zArray = new double[5];
        double[] interpArray = new double[5];
        for (int index = 0; index < 5; index++) {
            xArray[index] = this.xval[i + index];
            yArray[index] = this.yval[j + index];
        }
        for (int zIndex = 0; zIndex < 5; zIndex++) {
            for (int index2 = 0; index2 < 5; index2++) {
                zArray[index2] = this.fval[i + index2][j + zIndex];
            }
            interpArray[zIndex] = interpolator.interpolate(xArray, zArray).value(x);
        }
        return interpolator.interpolate(yArray, interpArray).value(y);
    }

    public boolean isValidPoint(double x, double y) {
        if (x < this.xval[0] || x > this.xval[this.xval.length - 1] || y < this.yval[0] || y > this.yval[this.yval.length - 1]) {
            return false;
        }
        return true;
    }

    private int searchIndex(double c, double[] val, int offset, int count) {
        int r;
        int r2 = Arrays.binarySearch(val, c);
        if (r2 == -1 || r2 == (-val.length) - 1) {
            throw new OutOfRangeException(Double.valueOf(c), Double.valueOf(val[0]), Double.valueOf(val[val.length - 1]));
        }
        if (r2 < 0) {
            r = ((-r2) - offset) - 1;
        } else {
            r = r2 - offset;
        }
        if (r < 0) {
            r = 0;
        }
        if (r + count >= val.length) {
            return val.length - count;
        }
        return r;
    }
}
