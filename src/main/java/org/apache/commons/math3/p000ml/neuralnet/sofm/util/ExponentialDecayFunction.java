package org.apache.commons.math3.p000ml.neuralnet.sofm.util;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.util.FastMath;

/* renamed from: org.apache.commons.math3.ml.neuralnet.sofm.util.ExponentialDecayFunction */
public class ExponentialDecayFunction {

    /* renamed from: a */
    private final double f245a;
    private final double oneOverB;

    public ExponentialDecayFunction(double initValue, double valueAtNumCall, long numCall) {
        if (initValue <= 0.0d) {
            throw new NotStrictlyPositiveException(Double.valueOf(initValue));
        } else if (valueAtNumCall <= 0.0d) {
            throw new NotStrictlyPositiveException(Double.valueOf(valueAtNumCall));
        } else if (valueAtNumCall >= initValue) {
            throw new NumberIsTooLargeException(Double.valueOf(valueAtNumCall), Double.valueOf(initValue), false);
        } else if (numCall <= 0) {
            throw new NotStrictlyPositiveException(Long.valueOf(numCall));
        } else {
            this.f245a = initValue;
            this.oneOverB = (-FastMath.log(valueAtNumCall / initValue)) / ((double) numCall);
        }
    }

    public double value(long numCall) {
        return this.f245a * FastMath.exp(((double) (-numCall)) * this.oneOverB);
    }
}
