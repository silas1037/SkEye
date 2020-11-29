package org.apache.commons.math3.p000ml.neuralnet.sofm.util;

import org.apache.commons.math3.analysis.function.Logistic;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;

/* renamed from: org.apache.commons.math3.ml.neuralnet.sofm.util.QuasiSigmoidDecayFunction */
public class QuasiSigmoidDecayFunction {
    private final double scale;
    private final Logistic sigmoid;

    public QuasiSigmoidDecayFunction(double initValue, double slope, long numCall) {
        if (initValue <= 0.0d) {
            throw new NotStrictlyPositiveException(Double.valueOf(initValue));
        } else if (slope >= 0.0d) {
            throw new NumberIsTooLargeException(Double.valueOf(slope), 0, false);
        } else if (numCall <= 1) {
            throw new NotStrictlyPositiveException(Long.valueOf(numCall));
        } else {
            this.sigmoid = new Logistic(initValue, (double) numCall, (4.0d * slope) / initValue, 1.0d, 0.0d, 1.0d);
            this.scale = initValue / this.sigmoid.value(0.0d);
        }
    }

    public double value(long numCall) {
        return this.scale * this.sigmoid.value((double) numCall);
    }
}
