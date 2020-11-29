package org.apache.commons.math3.p000ml.neuralnet;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.function.Constant;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.random.RandomGenerator;

/* renamed from: org.apache.commons.math3.ml.neuralnet.FeatureInitializerFactory */
public class FeatureInitializerFactory {
    private FeatureInitializerFactory() {
    }

    public static FeatureInitializer uniform(RandomGenerator rng, double min, double max) {
        return randomize(new UniformRealDistribution(rng, min, max), function(new Constant(0.0d), 0.0d, 0.0d));
    }

    public static FeatureInitializer uniform(double min, double max) {
        return randomize(new UniformRealDistribution(min, max), function(new Constant(0.0d), 0.0d, 0.0d));
    }

    public static FeatureInitializer function(final UnivariateFunction f, final double init, final double inc) {
        return new FeatureInitializer() {
            /* class org.apache.commons.math3.p000ml.neuralnet.FeatureInitializerFactory.C02811 */
            private double arg = init;

            @Override // org.apache.commons.math3.p000ml.neuralnet.FeatureInitializer
            public double value() {
                double result = f.value(this.arg);
                this.arg += inc;
                return result;
            }
        };
    }

    public static FeatureInitializer randomize(final RealDistribution random, final FeatureInitializer orig) {
        return new FeatureInitializer() {
            /* class org.apache.commons.math3.p000ml.neuralnet.FeatureInitializerFactory.C02822 */

            @Override // org.apache.commons.math3.p000ml.neuralnet.FeatureInitializer
            public double value() {
                return orig.value() + random.sample();
            }
        };
    }
}
