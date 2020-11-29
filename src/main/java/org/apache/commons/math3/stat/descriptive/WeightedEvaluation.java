package org.apache.commons.math3.stat.descriptive;

import org.apache.commons.math3.exception.MathIllegalArgumentException;

public interface WeightedEvaluation {
    double evaluate(double[] dArr, double[] dArr2) throws MathIllegalArgumentException;

    double evaluate(double[] dArr, double[] dArr2, int i, int i2) throws MathIllegalArgumentException;
}
