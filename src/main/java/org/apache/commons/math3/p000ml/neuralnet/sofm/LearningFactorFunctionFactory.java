package org.apache.commons.math3.p000ml.neuralnet.sofm;

import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.p000ml.neuralnet.sofm.util.ExponentialDecayFunction;
import org.apache.commons.math3.p000ml.neuralnet.sofm.util.QuasiSigmoidDecayFunction;

/* renamed from: org.apache.commons.math3.ml.neuralnet.sofm.LearningFactorFunctionFactory */
public class LearningFactorFunctionFactory {
    private LearningFactorFunctionFactory() {
    }

    public static LearningFactorFunction exponentialDecay(final double initValue, final double valueAtNumCall, final long numCall) {
        if (initValue > 0.0d && initValue <= 1.0d) {
            return new LearningFactorFunction() {
                /* class org.apache.commons.math3.p000ml.neuralnet.sofm.LearningFactorFunctionFactory.C02841 */
                private final ExponentialDecayFunction decay = new ExponentialDecayFunction(initValue, valueAtNumCall, numCall);

                @Override // org.apache.commons.math3.p000ml.neuralnet.sofm.LearningFactorFunction
                public double value(long n) {
                    return this.decay.value(n);
                }
            };
        }
        throw new OutOfRangeException(Double.valueOf(initValue), 0, 1);
    }

    public static LearningFactorFunction quasiSigmoidDecay(final double initValue, final double slope, final long numCall) {
        if (initValue > 0.0d && initValue <= 1.0d) {
            return new LearningFactorFunction() {
                /* class org.apache.commons.math3.p000ml.neuralnet.sofm.LearningFactorFunctionFactory.C02852 */
                private final QuasiSigmoidDecayFunction decay = new QuasiSigmoidDecayFunction(initValue, slope, numCall);

                @Override // org.apache.commons.math3.p000ml.neuralnet.sofm.LearningFactorFunction
                public double value(long n) {
                    return this.decay.value(n);
                }
            };
        }
        throw new OutOfRangeException(Double.valueOf(initValue), 0, 1);
    }
}
