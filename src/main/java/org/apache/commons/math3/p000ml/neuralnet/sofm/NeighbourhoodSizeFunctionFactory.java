package org.apache.commons.math3.p000ml.neuralnet.sofm;

import org.apache.commons.math3.p000ml.neuralnet.sofm.util.ExponentialDecayFunction;
import org.apache.commons.math3.p000ml.neuralnet.sofm.util.QuasiSigmoidDecayFunction;
import org.apache.commons.math3.util.FastMath;

/* renamed from: org.apache.commons.math3.ml.neuralnet.sofm.NeighbourhoodSizeFunctionFactory */
public class NeighbourhoodSizeFunctionFactory {
    private NeighbourhoodSizeFunctionFactory() {
    }

    public static NeighbourhoodSizeFunction exponentialDecay(final double initValue, final double valueAtNumCall, final long numCall) {
        return new NeighbourhoodSizeFunction() {
            /* class org.apache.commons.math3.p000ml.neuralnet.sofm.NeighbourhoodSizeFunctionFactory.C02861 */
            private final ExponentialDecayFunction decay = new ExponentialDecayFunction(initValue, valueAtNumCall, numCall);

            @Override // org.apache.commons.math3.p000ml.neuralnet.sofm.NeighbourhoodSizeFunction
            public int value(long n) {
                return (int) FastMath.rint(this.decay.value(n));
            }
        };
    }

    public static NeighbourhoodSizeFunction quasiSigmoidDecay(final double initValue, final double slope, final long numCall) {
        return new NeighbourhoodSizeFunction() {
            /* class org.apache.commons.math3.p000ml.neuralnet.sofm.NeighbourhoodSizeFunctionFactory.C02872 */
            private final QuasiSigmoidDecayFunction decay = new QuasiSigmoidDecayFunction(initValue, slope, numCall);

            @Override // org.apache.commons.math3.p000ml.neuralnet.sofm.NeighbourhoodSizeFunction
            public int value(long n) {
                return (int) FastMath.rint(this.decay.value(n));
            }
        };
    }
}
