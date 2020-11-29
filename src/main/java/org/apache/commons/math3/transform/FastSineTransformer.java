package org.apache.commons.math3.transform;

import java.io.Serializable;
import org.apache.commons.math3.analysis.FunctionUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.apache.commons.math3.util.FastMath;

public class FastSineTransformer implements RealTransformer, Serializable {
    static final long serialVersionUID = 20120211;
    private final DstNormalization normalization;

    public FastSineTransformer(DstNormalization normalization2) {
        this.normalization = normalization2;
    }

    @Override // org.apache.commons.math3.transform.RealTransformer
    public double[] transform(double[] f, TransformType type) {
        if (this.normalization == DstNormalization.ORTHOGONAL_DST_I) {
            return TransformUtils.scaleArray(fst(f), FastMath.sqrt(2.0d / ((double) f.length)));
        } else if (type == TransformType.FORWARD) {
            return fst(f);
        } else {
            return TransformUtils.scaleArray(fst(f), 2.0d / ((double) f.length));
        }
    }

    @Override // org.apache.commons.math3.transform.RealTransformer
    public double[] transform(UnivariateFunction f, double min, double max, int n, TransformType type) {
        double[] data = FunctionUtils.sample(f, min, max, n);
        data[0] = 0.0d;
        return transform(data, type);
    }

    /* access modifiers changed from: protected */
    public double[] fst(double[] f) throws MathIllegalArgumentException {
        double[] transformed = new double[f.length];
        if (!ArithmeticUtils.isPowerOfTwo((long) f.length)) {
            throw new MathIllegalArgumentException(LocalizedFormats.NOT_POWER_OF_TWO_CONSIDER_PADDING, Integer.valueOf(f.length));
        } else if (f[0] != 0.0d) {
            throw new MathIllegalArgumentException(LocalizedFormats.FIRST_ELEMENT_NOT_ZERO, Double.valueOf(f[0]));
        } else {
            int n = f.length;
            if (n == 1) {
                transformed[0] = 0.0d;
            } else {
                double[] x = new double[n];
                x[0] = 0.0d;
                x[n >> 1] = 2.0d * f[n >> 1];
                for (int i = 1; i < (n >> 1); i++) {
                    double a = FastMath.sin((((double) i) * 3.141592653589793d) / ((double) n)) * (f[i] + f[n - i]);
                    double b = 0.5d * (f[i] - f[n - i]);
                    x[i] = a + b;
                    x[n - i] = a - b;
                }
                Complex[] y = new FastFourierTransformer(DftNormalization.STANDARD).transform(x, TransformType.FORWARD);
                transformed[0] = 0.0d;
                transformed[1] = 0.5d * y[0].getReal();
                for (int i2 = 1; i2 < (n >> 1); i2++) {
                    transformed[i2 * 2] = -y[i2].getImaginary();
                    transformed[(i2 * 2) + 1] = y[i2].getReal() + transformed[(i2 * 2) - 1];
                }
            }
            return transformed;
        }
    }
}
