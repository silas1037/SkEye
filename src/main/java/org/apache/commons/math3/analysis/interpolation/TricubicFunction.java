package org.apache.commons.math3.analysis.interpolation;

import java.lang.reflect.Array;
import org.apache.commons.math3.analysis.TrivariateFunction;
import org.apache.commons.math3.exception.OutOfRangeException;

/* compiled from: TricubicInterpolatingFunction */
class TricubicFunction implements TrivariateFunction {

    /* renamed from: N */
    private static final short f131N = 4;

    /* renamed from: a */
    private final double[][][] f132a = ((double[][][]) Array.newInstance(Double.TYPE, 4, 4, 4));

    TricubicFunction(double[] aV) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < 4; k++) {
                    this.f132a[i][j][k] = aV[(((k * 4) + j) * 4) + i];
                }
            }
        }
    }

    @Override // org.apache.commons.math3.analysis.TrivariateFunction
    public double value(double x, double y, double z) throws OutOfRangeException {
        if (x < 0.0d || x > 1.0d) {
            throw new OutOfRangeException(Double.valueOf(x), 0, 1);
        } else if (y < 0.0d || y > 1.0d) {
            throw new OutOfRangeException(Double.valueOf(y), 0, 1);
        } else if (z < 0.0d || z > 1.0d) {
            throw new OutOfRangeException(Double.valueOf(z), 0, 1);
        } else {
            double x2 = x * x;
            double[] pX = {1.0d, x, x2, x2 * x};
            double y2 = y * y;
            double[] pY = {1.0d, y, y2, y2 * y};
            double z2 = z * z;
            double[] pZ = {1.0d, z, z2, z2 * z};
            double result = 0.0d;
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    for (int k = 0; k < 4; k++) {
                        result += this.f132a[i][j][k] * pX[i] * pY[j] * pZ[k];
                    }
                }
            }
            return result;
        }
    }
}
