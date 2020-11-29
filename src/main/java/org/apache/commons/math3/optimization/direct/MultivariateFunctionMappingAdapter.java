package org.apache.commons.math3.optimization.direct;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.function.Logit;
import org.apache.commons.math3.analysis.function.Sigmoid;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;

@Deprecated
public class MultivariateFunctionMappingAdapter implements MultivariateFunction {
    private final MultivariateFunction bounded;
    private final Mapper[] mappers;

    /* access modifiers changed from: private */
    public interface Mapper {
        double boundedToUnbounded(double d);

        double unboundedToBounded(double d);
    }

    public MultivariateFunctionMappingAdapter(MultivariateFunction bounded2, double[] lower, double[] upper) {
        MathUtils.checkNotNull(lower);
        MathUtils.checkNotNull(upper);
        if (lower.length != upper.length) {
            throw new DimensionMismatchException(lower.length, upper.length);
        }
        for (int i = 0; i < lower.length; i++) {
            if (upper[i] < lower[i]) {
                throw new NumberIsTooSmallException(Double.valueOf(upper[i]), Double.valueOf(lower[i]), true);
            }
        }
        this.bounded = bounded2;
        this.mappers = new Mapper[lower.length];
        for (int i2 = 0; i2 < this.mappers.length; i2++) {
            if (Double.isInfinite(lower[i2])) {
                if (Double.isInfinite(upper[i2])) {
                    this.mappers[i2] = new NoBoundsMapper();
                } else {
                    this.mappers[i2] = new UpperBoundMapper(upper[i2]);
                }
            } else if (Double.isInfinite(upper[i2])) {
                this.mappers[i2] = new LowerBoundMapper(lower[i2]);
            } else {
                this.mappers[i2] = new LowerUpperBoundMapper(lower[i2], upper[i2]);
            }
        }
    }

    public double[] unboundedToBounded(double[] point) {
        double[] mapped = new double[this.mappers.length];
        for (int i = 0; i < this.mappers.length; i++) {
            mapped[i] = this.mappers[i].unboundedToBounded(point[i]);
        }
        return mapped;
    }

    public double[] boundedToUnbounded(double[] point) {
        double[] mapped = new double[this.mappers.length];
        for (int i = 0; i < this.mappers.length; i++) {
            mapped[i] = this.mappers[i].boundedToUnbounded(point[i]);
        }
        return mapped;
    }

    @Override // org.apache.commons.math3.analysis.MultivariateFunction
    public double value(double[] point) {
        return this.bounded.value(unboundedToBounded(point));
    }

    private static class NoBoundsMapper implements Mapper {
        NoBoundsMapper() {
        }

        @Override // org.apache.commons.math3.optimization.direct.MultivariateFunctionMappingAdapter.Mapper
        public double unboundedToBounded(double y) {
            return y;
        }

        @Override // org.apache.commons.math3.optimization.direct.MultivariateFunctionMappingAdapter.Mapper
        public double boundedToUnbounded(double x) {
            return x;
        }
    }

    private static class LowerBoundMapper implements Mapper {
        private final double lower;

        LowerBoundMapper(double lower2) {
            this.lower = lower2;
        }

        @Override // org.apache.commons.math3.optimization.direct.MultivariateFunctionMappingAdapter.Mapper
        public double unboundedToBounded(double y) {
            return this.lower + FastMath.exp(y);
        }

        @Override // org.apache.commons.math3.optimization.direct.MultivariateFunctionMappingAdapter.Mapper
        public double boundedToUnbounded(double x) {
            return FastMath.log(x - this.lower);
        }
    }

    private static class UpperBoundMapper implements Mapper {
        private final double upper;

        UpperBoundMapper(double upper2) {
            this.upper = upper2;
        }

        @Override // org.apache.commons.math3.optimization.direct.MultivariateFunctionMappingAdapter.Mapper
        public double unboundedToBounded(double y) {
            return this.upper - FastMath.exp(-y);
        }

        @Override // org.apache.commons.math3.optimization.direct.MultivariateFunctionMappingAdapter.Mapper
        public double boundedToUnbounded(double x) {
            return -FastMath.log(this.upper - x);
        }
    }

    private static class LowerUpperBoundMapper implements Mapper {
        private final UnivariateFunction boundingFunction;
        private final UnivariateFunction unboundingFunction;

        LowerUpperBoundMapper(double lower, double upper) {
            this.boundingFunction = new Sigmoid(lower, upper);
            this.unboundingFunction = new Logit(lower, upper);
        }

        @Override // org.apache.commons.math3.optimization.direct.MultivariateFunctionMappingAdapter.Mapper
        public double unboundedToBounded(double y) {
            return this.boundingFunction.value(y);
        }

        @Override // org.apache.commons.math3.optimization.direct.MultivariateFunctionMappingAdapter.Mapper
        public double boundedToUnbounded(double x) {
            return this.unboundingFunction.value(x);
        }
    }
}
