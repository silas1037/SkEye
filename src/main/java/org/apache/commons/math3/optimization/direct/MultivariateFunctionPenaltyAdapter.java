package org.apache.commons.math3.optimization.direct;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;

@Deprecated
public class MultivariateFunctionPenaltyAdapter implements MultivariateFunction {
    private final MultivariateFunction bounded;
    private final double[] lower;
    private final double offset;
    private final double[] scale;
    private final double[] upper;

    public MultivariateFunctionPenaltyAdapter(MultivariateFunction bounded2, double[] lower2, double[] upper2, double offset2, double[] scale2) {
        MathUtils.checkNotNull(lower2);
        MathUtils.checkNotNull(upper2);
        MathUtils.checkNotNull(scale2);
        if (lower2.length != upper2.length) {
            throw new DimensionMismatchException(lower2.length, upper2.length);
        } else if (lower2.length != scale2.length) {
            throw new DimensionMismatchException(lower2.length, scale2.length);
        } else {
            for (int i = 0; i < lower2.length; i++) {
                if (upper2[i] < lower2[i]) {
                    throw new NumberIsTooSmallException(Double.valueOf(upper2[i]), Double.valueOf(lower2[i]), true);
                }
            }
            this.bounded = bounded2;
            this.lower = (double[]) lower2.clone();
            this.upper = (double[]) upper2.clone();
            this.offset = offset2;
            this.scale = (double[]) scale2.clone();
        }
    }

    @Override // org.apache.commons.math3.analysis.MultivariateFunction
    public double value(double[] point) {
        double overshoot;
        for (int i = 0; i < this.scale.length; i++) {
            if (point[i] < this.lower[i] || point[i] > this.upper[i]) {
                double sum = 0.0d;
                for (int j = i; j < this.scale.length; j++) {
                    if (point[j] < this.lower[j]) {
                        overshoot = this.scale[j] * (this.lower[j] - point[j]);
                    } else if (point[j] > this.upper[j]) {
                        overshoot = this.scale[j] * (point[j] - this.upper[j]);
                    } else {
                        overshoot = 0.0d;
                    }
                    sum += FastMath.sqrt(overshoot);
                }
                return this.offset + sum;
            }
        }
        return this.bounded.value(point);
    }
}
